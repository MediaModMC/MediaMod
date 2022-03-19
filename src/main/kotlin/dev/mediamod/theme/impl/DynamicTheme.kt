package dev.mediamod.theme.impl

import dev.mediamod.config.Configuration
import dev.mediamod.theme.Colors
import dev.mediamod.theme.Theme
import dev.mediamod.utils.ColorQuantizer
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.pow

class DynamicTheme : Theme.InbuiltTheme("Dynamic") {
    override var colors = defaultColors
        private set

    override fun update(image: BufferedImage) {
        try {
            val quantizedColors = ColorQuantizer.quantize(image)
            colors = if (Configuration.constrastMode) {
                getContrastColors(quantizedColors)
            } else {
                getColors(quantizedColors)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    private fun getContrastColors(quantizedColors: Array<Color>): Colors {
        val background = quantizedColors[0]
        val backgroundLuminance = background.luminance

        val progressBar = if (quantizedColors.size >= 2) {
            quantizedColors[1].changeUntil(2f, background)
        } else {
            if (backgroundLuminance > 0.5) {
                background.darker()
            } else {
                background.brighter()
            }
        }

        val progressBarBackground = progressBar.changeUntil(1.3f)
        val progressBarText = progressBar.changeUntil(3f)

        return Colors(
            background = background,
            text = Color.WHITE,
            progressBar = progressBar,
            progressBarBackground = progressBarBackground,
            progressBarText = progressBarText
        )
    }

    private fun getColors(quantizedColors: Array<Color>): Colors {
        val progressBar = if (quantizedColors.size >= 2) {
            quantizedColors[1]
        } else {
            quantizedColors[0].brighter()
        }
        return Colors(
            background = quantizedColors[0],
            text = Color.white,
            progressBar = progressBar,
            progressBarBackground = progressBar.darker().darker(),
            progressBarText = progressBar.brighter().brighter()
        )
    }

    private val Color.luminance: Float
        get() = (0.2126f * (red / 255.0).pow(2.2) +
            0.7152f * (green / 255.0).pow(2.2) +
            0.0722f * (blue / 255.0).pow(2.2)).toFloat()

    private fun Color.changeUntil(contrast: Float, against: Color = this): Color {
        return if (against.luminance > 0.5) {
            darkenUntil(contrast, against)
        } else {
            brightenUntil(contrast, against)
        }
    }

    private fun Color.brightenUntil(contrast: Float, against: Color = this): Color {
        var color = this
        while (color.contrast(against) < contrast) {
            val newColor = color.brighter()
            if (newColor == color) return color
            color = newColor
        }
        return color
    }

    private fun Color.darkenUntil(contrast: Float, against: Color = this): Color {
        var color = this
        while (color.contrast(against) < contrast) {
            val newColor = color.darker()
            if (newColor == color) return color
            color = newColor
        }
        return color
    }

    private fun Color.contrast(other: Color): Float {
        var l1 = luminance
        var l2 = other.luminance
        if (l2 > l1) {
            val temp = l1
            l1 = l2
            l2 = temp
        }
        return (l1 + 0.05f) / (l2 + 0.05f)
    }
}