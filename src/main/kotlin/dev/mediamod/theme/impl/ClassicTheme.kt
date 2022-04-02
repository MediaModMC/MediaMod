package dev.mediamod.theme.impl

import dev.mediamod.theme.Colors
import dev.mediamod.theme.Theme
import java.awt.Color
import java.awt.image.BufferedImage

internal val classicColors = Colors(
    background = Color.darkGray.darker(),
    text = Color.white,
    progressBar = Color.green,
    progressBarBackground = Color.gray,
    progressBarText = Color.darkGray.darker()
)

class ClassicTheme : Theme.InbuiltTheme("Classic") {
    override val colors = classicColors
    override fun update(image: BufferedImage) {}
}