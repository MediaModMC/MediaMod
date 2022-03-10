package dev.mediamod.theme.impl

import dev.mediamod.theme.Theme
import java.awt.Color

class DefaultTheme : Theme(
    name = "Default",
    colors = Colors(
        background = Color.darkGray.darker(),
        text = Color.white,
        progressBar = Color.green,
        progressBarBackground = Color.gray,
        progressBarText = Color.darkGray.darker()
    )
)