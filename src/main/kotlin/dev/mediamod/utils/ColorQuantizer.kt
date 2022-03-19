package dev.mediamod.utils

import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*

/**
 * A color quantizer
 *
 * Based of leptonica's colorquant.c "Modified median cut color quantization"
 *
 * References:
 * - [colorquant2.c](https://github.com/DanBloomberg/leptonica/blob/master/src/colorquant2.c)
 * - [Color quantization using modified median cut - Dan S. Bloomberg](http://leptonica.org/papers/mediancut.pdf)
 *
 * @author DJtheRedstoner
 */
object ColorQuantizer {

    private const val fractByPopulation = 0.85f
    private const val maxItersAllowed = 5000
    private const val sigbits = 5
    private const val rshift = 8 - sigbits
    private const val mask = 0xff shr rshift

    fun quantize(image: BufferedImage, maxcolors: Int = 16): Array<Color> {
        val histosize = 1 shl (3 * sigbits)
        val histo = IntArray(histosize)

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val rgb = image.getRGB(x, y)
                val rval = rgb shr 16 + rshift and mask
                val gval = rgb shr 8 + rshift and mask
                val bval = rgb shr rshift and mask
                val idx = (rval shl 2 * sigbits) + (gval shl sigbits) + bval
                histo[idx]++
            }
        }

        var count = 0
        var smalln = true
        for (i in 0 until histosize) {
            if (histo[i] > 0) {
                count++
            }
            if (count > maxcolors) {
                smalln = false
                break
            }
        }

        if (smalln) {
            val queue = PriorityQueue<Pair<Color, Int>>(Collections.reverseOrder(Comparator.comparingInt { it.second }))
            for (i in 0 until histosize) {
                if (histo[i] > 0) {
                    val rval = (i shr (2 * sigbits)) shl rshift
                    val gval = ((i shr sigbits) and mask) shl rshift
                    val bval = (i and mask) shl rshift
                    queue.add(Color(rval, gval, bval) to histo[i])
                }
            }
            return queue.map { it.first }.toTypedArray()
        }

        var rmin = 1000000
        var gmin = 1000000
        var bmin = 1000000
        var rmax = 0
        var bmax = 0
        var gmax = 0

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val rgb: Int = image.getRGB(x, y)
                val rval = rgb shr 16 + rshift and mask
                val gval = rgb shr 8 + rshift and mask
                val bval = rgb shr rshift and mask
                if (rval < rmin) rmin = rval
                else if (rval > rmax) rmax = rval
                if (gval < gmin) gmin = gval
                else if (gval > gmax) gmax = gval
                if (bval < bmin) bmin = bval
                else if (bval > bmax) bmax = bval
            }
        }

        val initialVbox = VBox(rmin, rmax, gmin, gmax, bmin, bmax, histo)

        val queueByPop = PriorityQueue(Collections.reverseOrder(Comparator.comparingInt(VBox::count)))
        queueByPop.add(initialVbox)

        var ncolors = 1
        var nitters = 0
        val popcolors = (fractByPopulation * maxcolors).toInt()
        while (true) {
            val vbox = queueByPop.remove()
            if (vbox.count() == 0) {
                queueByPop.add(vbox)
                continue
            }
            val (vbox1, vbox2) = medianCutApply(histo, vbox)
            queueByPop.add(vbox1)
            if (vbox2 != null) {
                queueByPop.add(vbox2)
                ncolors++
            }
            if (ncolors >= popcolors) {
                break
            }
            if (nitters++ > maxItersAllowed) {
//                println("infinite loop; perhaps too few pixels!")
                break
            }
        }

        var maxprod = 0f
        for (vbox in queueByPop) {
            val prod = vbox.count().toFloat() * vbox.volume().toFloat()
            if (prod > maxprod) maxprod = prod
        }
        val norm = if (maxprod == 0f) 1f else 1000000.0f / maxprod

        val queueByVol = PriorityQueue<VBox>(Collections.reverseOrder(Comparator.comparingDouble { (norm * it.count() * it.volume()).toDouble() } ))
        queueByVol.addAll(queueByPop)

        while (true) {
            val vbox = queueByVol.remove()
            if (vbox.count() == 0) {
                queueByVol.add(vbox)
                continue
            }
            val (vbox1, vbox2) = medianCutApply(histo, vbox)
            queueByVol.add(vbox1)
            if (vbox2 != null) {
                queueByVol.add(vbox2)
                ncolors++
            }
            if (ncolors >= maxcolors) {
                break
            }
            if (nitters++ > maxItersAllowed) {
//                println("infinite loop; perhaps too few pixels!")
                break
            }
        }

        queueByPop.clear()
        queueByPop.addAll(queueByVol)

        return queueByPop.map(VBox::avg).map(::Color).toTypedArray()
    }

    private fun medianCutApply(histo: IntArray, vbox: VBox): Pair<VBox, VBox?> {
        val rw = vbox.r2 - vbox.r1 + 1
        val gw = vbox.g2 - vbox.g1 + 1
        val bw = vbox.b2 - vbox.b1 + 1

        if (rw == 1 && gw == 1 && bw == 1) {
            return vbox.copy() to null
        }

        val maxw = maxOf(rw, gw, bw)

        var total = 0
        val partialsum = IntArray(128)
        if (maxw == rw) {
            for (i in vbox.r1..vbox.r2) {
                var sum = 0
                for (j in vbox.g1..vbox.g2) {
                    for (k in vbox.b1..vbox.b2) {
                        val index = (i shl 2 * sigbits) + (j shl sigbits) + k
                        sum += histo[index]
                    }
                }
                total += sum
                partialsum[i] = total
            }
        } else if (maxw == gw) {
            for (i in vbox.g1..vbox.g2) {
                var sum = 0
                for (j in vbox.r1..vbox.r2) {
                    for (k in vbox.b1..vbox.b2) {
                        val index = (i shl sigbits) + (j shl 2 * sigbits) + k
                        sum += histo[index]
                    }
                }
                total += sum
                partialsum[i] = total
            }
        } else {
            for (i in vbox.b1..vbox.b2) {
                var sum = 0
                for (j in vbox.r1..vbox.r2) {
                    for (k in vbox.g1..vbox.g2) {
                        val index = i + (j shl 2 * sigbits) + (k shl sigbits)
                        sum += histo[index]
                    }
                }
                total += sum
                partialsum[i] = total
            }
        }

        var vbox1: VBox? = null
        var vbox2: VBox? = null
        if (maxw == rw) {
            for (i in vbox.r1..vbox.r2) {
                if (partialsum[i] > total / 2) {
                    vbox1 = vbox.copy()
                    vbox2 = vbox.copy()
                    val left = i - vbox.r1
                    val right = vbox.r2 - i
                    if (left <= right) {
                        vbox1.r2 = Math.min(vbox.r2 - 1, i + right / 2)
                    } else {
                        vbox1.r2 = Math.max(vbox.r1, i - 1 - left / 2)
                    }
                    vbox2.r1 = vbox1.r2 + 1
                    break
                }
            }
        } else if (maxw == gw) {
            for (i in vbox.g1..vbox.g2) {
                if (partialsum[i] > total / 2) {
                    vbox1 = vbox.copy()
                    vbox2 = vbox.copy()
                    val left = i - vbox.g1
                    val right = vbox.g2 - i
                    if (left <= right) {
                        vbox1.g2 = Math.min(vbox.g2 - 1, i + right / 2)
                    } else {
                        vbox1.g2 = Math.max(vbox.g1, i - 1 - left / 2)
                    }
                    vbox2.g1 = vbox1.g2 + 1
                    break
                }
            }
        } else {
            for (i in vbox.b1..vbox.b2) {
                if (partialsum[i] > total / 2) {
                    vbox1 = vbox.copy()
                    vbox2 = vbox.copy()
                    val left = i - vbox.b1
                    val right = vbox.b2 - i
                    if (left <= right) {
                        vbox1.b2 = Math.min(vbox.b2 - 1, i + right / 2)
                    } else {
                        vbox1.b2 = Math.max(vbox.b1, i - 1 - left / 2)
                    }
                    vbox2.b1 = vbox1.b2 + 1
                    break
                }
            }
        }

        return vbox1!! to vbox2!!
    }

    class VBox(
        var r1: Int,
        var r2: Int,
        var g1: Int,
        var g2: Int,
        var b1: Int,
        var b2: Int,
        private val histo: IntArray
    ) {
        private val count by lazy {
            var npix = 0
            for (i in r1..r2) {
                for (j in g1..g2) {
                    for (k in b1..b2) {
                        npix += histo[(i shl 2 * sigbits) + (j shl sigbits) + k]
                    }
                }
            }
            npix
        }

        fun count() = count

        fun volume() = (r2 - r1 + 1) * (g2 - g1 + 1) * (b2 - b1 + 1)

        fun avg(): Int {
            var rsum = 0
            var gsum = 0
            var bsum = 0
            var ntot = 0
            val mult = 1 shl (8 - sigbits)

            for (i in r1..r2) {
                for (j in g1..g2) {
                    for (k in b1..b2) {
                        val histoindex = (i shl 2 * sigbits) + (j shl sigbits) + k
                        ntot += histo[histoindex]
                        rsum += (histo[histoindex] * (i + 0.5) * mult).toInt()
                        gsum += (histo[histoindex] * (j + 0.5) * mult).toInt()
                        bsum += (histo[histoindex] * (k + 0.5) * mult).toInt()
                    }
                }
            }

            return if (ntot == 0) {
                (mult * (r1 + r2 + 1) / 2 shl 16) +
                    (mult * (g1 + g2 + 1) shl 8) +
                    mult * (b1 + b2 + 1)
            } else {
                (rsum / ntot shl 16) +
                    (gsum / ntot shl 8) +
                    bsum / ntot
            }
        }

        fun copy() = VBox(r1, r2, g1, g2, b1, b2, histo)
    }

}