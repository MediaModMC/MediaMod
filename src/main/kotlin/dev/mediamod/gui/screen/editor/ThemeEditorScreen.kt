package dev.mediamod.gui.screen.editor

import dev.mediamod.MediaMod
import dev.mediamod.gui.ColorPalette
import dev.mediamod.gui.component.UIButton
import dev.mediamod.gui.screen.editor.component.CreateThemeListItem
import dev.mediamod.gui.screen.editor.component.ThemeEditorContainer
import dev.mediamod.gui.screen.editor.component.ThemeListItem
import dev.mediamod.theme.Theme
import dev.mediamod.theme.impl.defaultColors
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
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

    private val themesList = ScrollComponent()
        .constrain {
            x = 15.pixels()
            y = SiblingConstraint(10f)
            width = 100.percent()
            height = FillConstraint()
        }

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
        MediaMod.themeManager.onLoadedThemesUpdate(::addLoadedThemes)

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

        addLoadedThemes(MediaMod.themeManager.loadedThemes)
        themesList childOf leftContainer

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

    private fun editTheme(theme: Theme?) {
        themeEditor.theme.set(theme)

        if (theme == null) {
            welcomeText.unhide()
        } else {
            welcomeText.hide()
        }
    }

    private fun createTheme() {
        val count = MediaMod.themeManager.loadedThemes.filter { it.name.lowercase().startsWith("my theme") }.size
        val suffix = if (count == 0) "" else " (${count + 1})"
        val theme = Theme.LoadedTheme("My Theme$suffix", defaultColors)

        MediaMod.themeManager.addTheme(theme)
        editTheme(theme)
        select(theme)
    }

    private fun addLoadedThemes(themes: List<Theme>) {
        themesList.clearChildren()

        themes.forEach { theme ->
            ThemeListItem(theme)
                .constrain {
                    y = SiblingConstraint(5f)
                    width = 100.percent()
                    height = ChildBasedMaxSizeConstraint()
                }
                .onClick {
                    select(null)
                    editTheme(this)
                } childOf themesList
        }

        CreateThemeListItem()
            .constrain {
                y = SiblingConstraint(5f)
                width = 100.percent()
                height = ChildBasedMaxSizeConstraint()
            }
            .onClick {
                select(null)
                createTheme()
            } childOf themesList
    }

    private fun select(theme: Theme?) {
        themesList.allChildren
            .filterIsInstance<ThemeListItem>()
            .forEach {
                when (theme) {
                    it.theme -> it.select()
                    else -> it.unselect()
                }
            }
    }
}