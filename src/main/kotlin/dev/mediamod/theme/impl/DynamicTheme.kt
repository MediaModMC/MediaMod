package dev.mediamod.theme.impl

import dev.mediamod.theme.Colors
import dev.mediamod.theme.Theme
import dev.mediamod.utils.ColorQuantizer
import dev.mediamod.utils.logger
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.pow

class DynamicTheme : Theme.InbuiltTheme("Dynamic") {
    override var colors = classicColors
        private set

    override fun update(image: BufferedImage) {
        try {
            val quantizedColors = ColorQuantizer.quantize(image)

            // Use the most common color as the background
            val background = quantizedColors[0].color

            val progressBar = if (quantizedColors.size >= 2) {
                // Of the rest of the colors with more than 10% of the pixels, find the one with the most contrast
                // against the background
                quantizedColors.subList(1, quantizedColors.size - 1)
                    .filter { it.fraction > 0.10 }
                    .maxByOrNull { it.color.contrast(background) }
                    ?.color
                    // If none of remaining colors have more than 10% of the pixels, use the 2nd most common color
                    ?: quantizedColors[1].color
            } else {
                // Very rare case if image is all one color
                background.changeBy(0.2f)
            }

            // Create lighter/darker variants of the progress bar color for background and text
            // The change parameter can be tuned to get different results
            val progressBarBackground = progressBar.changeBy(0.15f)
            val progressBarText = progressBar.changeBy(0.35f)

            // Use black text if the background is bright
            val text = if (background.luminance > 0.5) {
                Color.BLACK
            } else {
                Color.WHITE
            }

            colors = Colors(
                background = background,
                text = text,
                progressBar = progressBar,
                progressBarBackground = progressBarBackground,
                progressBarText = progressBarText
            )
        } catch (e: Exception) {
            logger.error("An error occurred when doing color quantization: ", e)
            return
        }
    }

    private val Color.luminance: Float
        get() = (0.2126f * (red / 255.0).pow(2.2) +
            0.7152f * (green / 255.0).pow(2.2) +
            0.0722f * (blue / 255.0).pow(2.2)).toFloat()

    private fun Color.contrast(other: Color): Float {
        var l1 = luminance
        var l2 = other.luminance
        if (l2 > l1) {
            val old = l1
            l1 = l2
            l2 = old
        }
        return (l1 + 0.05f) / (l2 + 0.05f)
    }

    private fun Color.changeBy(change: Float): Color {
        val hsl = toHSL()
        if (hsl[2] > 0.5) {
            hsl[2] -= change
        } else {
            hsl[2] += change
        }
        return toRGB(hsl)
    }

    // https://en.wikipedia.org/wiki/HSL_and_HSV#From_RGB
    private fun Color.toHSL(): FloatArray {
        val r = red / 255f
        val g = green / 255f
        val b = blue / 255f
        val v = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val l = (v + min) / 2f
        val c = v - min

        var h: Float
        if (c == 0f) {
            h = 0f
        } else if (v == r) {
            h = (g - b) / c
            if (h < 0) h += 6f
        } else if (v == g) {
            h = 2 + (b - r) / c
        } else {
            h = 4 + (r - g) / c
        }
        h /= 6f

        val s = if (l == 0f || l == 1f) {
            0f
        } else {
            (v - l) / minOf(l, 1 - l)
        }

        val hsl = FloatArray(3)
        hsl[0] = h
        hsl[1] = s
        hsl[2] = l
        return hsl
    }

    // https://en.wikipedia.org/wiki/HSL_and_HSV#HSL_to_RGB
    private fun toRGB(hsl: FloatArray): Color {
        val h = hsl[0] * 6f
        val c = (1 - abs(2 * hsl[2] - 1)) * hsl[1]
        val x = c * (1 - abs(h % 2 - 1))
        var r = 0f
        var g = 0f
        var b = 0f

        if (h < 1) {
            r = c
            g = x
        } else if (h < 2) {
            r = x
            g = c
        } else if (h < 3) {
            g = c
            b = x
        } else if (h < 4) {
            g = x
            b = c
        } else if (h < 5) {
            r = x
            b = c
        } else {
            r = c
            b = x
        }

        val m = hsl[2] - c / 2
        return Color(r + m, g + m, b + m)
    }
}