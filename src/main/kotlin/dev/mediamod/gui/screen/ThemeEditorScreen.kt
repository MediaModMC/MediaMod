package dev.mediamod.gui.screen

import dev.mediamod.gui.ColorPalette
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UScreen
import java.awt.Color

class ThemeEditorScreen(private val parentScreen: UScreen) :
    WindowScreen(ElementaVersion.V1, restoreCurrentGuiOnClose = true) {
    private val mainContainer by UIBlock(ColorPalette.background)
        .constrain {
            width = 100.percent()
            height = 100.percent()
        } childOf window

    private val leftContainer by UIBlock(ColorPalette.secondaryBackground)
        .constrain {
            width = 35.percent()
            height = 100.percent()
        } childOf mainContainer

    private val rightContainer by UIContainer()
        .constrain {
            x = SiblingConstraint()
            width = FillConstraint()
            height = 100.percent()
        } childOf mainContainer

    init {
        UIText("Theme Editor")
            .constrain {
                x = CenterConstraint()
                y = 15.pixels()
                textScale = 1.25f.pixels()
            } childOf leftContainer

        val buttonContainer = UIRoundedRectangle(5f)
            .onMouseClick {
                displayScreen(parentScreen)
            }
            .constrain {
                x = CenterConstraint()
                y = 15.pixels(true)
                width = 80.percent()
                height = 25.pixels()
                color = ColorPalette.secondaryBackground.brighter().constraint
            } childOf leftContainer

        UIText("Close")
            .constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                color = Color.white.constraint
            } childOf buttonContainer

        UIText("TODO") childOf rightContainer
    }
}