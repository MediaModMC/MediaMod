package dev.mediamod.gui.screen.editor

import dev.mediamod.MediaMod
import dev.mediamod.gui.ColorPalette
import dev.mediamod.gui.component.UIButton
import dev.mediamod.gui.component.UIDialog
import dev.mediamod.gui.screen.editor.component.CreateThemeListItem
import dev.mediamod.gui.screen.editor.component.ThemeEditorContainer
import dev.mediamod.gui.screen.editor.component.ThemeListItem
import dev.mediamod.theme.Theme
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import java.awt.Color

class ThemeEditorScreen : WindowScreen(
    version = ElementaVersion.V1,
    restoreCurrentGuiOnClose = true,
    newGuiScale = 5
) {
    private val manager = MediaMod.themeManager

    private val mainContainer by UIBlock(ColorPalette.background)
        .constrain {
            width = 100.percent()
            height = 100.percent()
        } childOf window

    private val dialogContainer by UIDialog("Create a new theme")
        .content {
            UIText("Hello world")
                .constrain {
                    color = Color.white.darker().constraint
                }
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

    private val themeEditor by ThemeEditorContainer() childOf rightContainer

    private val welcomeText by UIWrappedText(
        text = "Choose a theme on the left side of your screen to edit it!",
        centered = true
    )
        .constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = 90.percent()
        } childOf rightContainer

    init {
        UIText("Theme Editor")
            .constrain {
                x = 15.pixels()
                y = 15.pixels()
                textScale = 1.5f.pixels()
            } childOf leftContainer

        UIText("Themes")
            .constrain {
                x = 15.pixels()
                y = SiblingConstraint(20f)
            } childOf leftContainer

        manager.loadedThemes.forEachIndexed { index, theme ->
            ThemeListItem(theme)
                .constrain {
                    x = 15.pixels()
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

                    // Just in case for some weird reason it says selected
                    editTheme(this)
                } childOf leftContainer
        }

        CreateThemeListItem()
            .constrain {
                x = 15.pixels()
                y = SiblingConstraint(5f)
                width = 100.percent()
                height = ChildBasedMaxSizeConstraint()
            }
            .onClick {
                leftContainer.children
                    .filterIsInstance<ThemeListItem>()
                    .forEach {
                        it.unselect()
                    }

                createTheme()
            } childOf leftContainer

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
        welcomeText.hide()
    }

    private fun createTheme() {
        dialogContainer.unhide()
    }
}