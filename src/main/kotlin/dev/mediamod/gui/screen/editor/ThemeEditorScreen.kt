package dev.mediamod.gui.screen.editor

import dev.mediamod.MediaMod
import dev.mediamod.gui.ColorPalette
import dev.mediamod.gui.component.UIButton
import dev.mediamod.gui.screen.editor.component.ThemeEditorContainer
import dev.mediamod.gui.screen.editor.component.ThemeListItem
import dev.mediamod.theme.Theme
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import java.awt.Color

class ThemeEditorScreen : WindowScreen(
    version = ElementaVersion.V1,
    restoreCurrentGuiOnClose = true
) {
    private val manager = MediaMod.themeManager

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

    private val themeEditor by ThemeEditorContainer()
        .constrain {
            width = FillConstraint()
            height = FillConstraint()
        } childOf rightContainer

    init {
        UIText("Theme Editor")
            .constrain {
                x = CenterConstraint()
                y = 15.pixels()
                textScale = 1.25f.pixels()
            } childOf leftContainer

        UIText("Themes")
            .constrain {
                x = 10.percent()
                y = SiblingConstraint(20f)
            } childOf leftContainer

        manager.loadedThemes.forEachIndexed { index, theme ->
            ThemeListItem(theme)
                .constrain {
                    x = 10.percent()
                    y = SiblingConstraint(if (index == 0) 10f else 5f)
                    width = 100.percent()
                    height = ChildBasedMaxSizeConstraint()
                }
                .onClick {
                    leftContainer.children
                        .filterIsInstance<ThemeListItem>()
                        .forEach {
                            it.unselect()
                        }

                    editTheme(this)
                } childOf leftContainer
        }

        UIButton(text = "Close", textColor = Color.white)
            .constrain {
                x = CenterConstraint()
                y = 15.pixels(true)
                width = 80.percent()
                height = 25.pixels()
                color = ColorPalette.secondaryBackground.brighter().constraint
            }
            .onClick {
                restorePreviousScreen()
            } childOf leftContainer
    }

    private fun editTheme(theme: Theme) {
        themeEditor.theme.set(theme)
    }
}