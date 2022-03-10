package dev.mediamod.theme.impl

import dev.mediamod.theme.Theme
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

class DynamicTheme : Theme(
    name = "Dynamic",
    colors = Colors(
        background = Color.darkGray.darker(),
        text = Color.white,
        progressBar = Color.green,
        progressBarBackground = Color.gray,
        progressBarText = Color.darkGray.darker()
    )
) {
    override fun update(image: BufferedImage) {
        val color = getAverageColor(image)
        colors = Colors(
            background = color,
            text = Color.white,
            progressBar = color.brighter(),
            progressBarBackground = color.darker(),
            // This is *very* temporary
            progressBarText = color.brighter().brighter().brighter()
        )
    }

    private fun getAverageColor(image: BufferedImage): Color {
        var subR: Long = 0
        var subG: Long = 0
        var sumB: Long = 0

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val pixel = Color(image.getRGB(x, y))
                subR += pixel.red.toLong()
                subG += pixel.green.toLong()
                sumB += pixel.blue.toLong()
            }
        }

        val sampled = image.width * image.height
        return Color(
            (subR / sampled).toFloat().roundToInt(),
            (subG / sampled).toFloat().roundToInt(),
            (sumB / sampled).toFloat().roundToInt()
        )
    }
}