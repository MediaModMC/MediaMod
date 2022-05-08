package dev.mediamod.gui.screen.editor

import dev.mediamod.MediaMod
import dev.mediamod.gui.ColorPalette
import dev.mediamod.gui.component.UIButton
import dev.mediamod.gui.screen.editor.component.CustomThemeListItem
import dev.mediamod.gui.screen.editor.component.ThemeEditorContainer
import dev.mediamod.gui.screen.editor.component.ThemeListItem
import dev.mediamod.gui.style.styled
import dev.mediamod.gui.style.stylesheet
import dev.mediamod.theme.Theme
import dev.mediamod.theme.impl.classicColors
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UDesktop
import java.awt.Color
import java.net.URL

class ThemeEditorScreen : WindowScreen(
    version = ElementaVersion.V1,
    restoreCurrentGuiOnClose = true,
    newGuiScale = 5
) {
    private val stylesheet = stylesheet {
        "mainContainer" {
            width = 100.percent()
            height = 100.percent()
        }

        "leftContainer" {
            width = 35.percent()
            height = 100.percent()
        }

        "rightContainer" {
            x = SiblingConstraint()
            width = FillConstraint()
            height = 100.percent()
        }

        "themesList" {
            width = 100.percent()
            height = 100.percent()
        }

        "themesListContainer" {
            x = 15.pixels()
            y = SiblingConstraint(10f)
            width = 100.percent() - 15.pixels()
            height = FillConstraint() - 65.pixels()
        }

        "scrollBar" {
            x = 2.5.pixels(true)
            y = 5.pixels()
            width = 3.pixels()
            height = 90.percent()
        }

        "themesListItem" {
            y = SiblingConstraint(5f)
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint()
        }

        "welcomeText" {
            x = CenterConstraint()
            y = CenterConstraint()
            width = 90.percent()
        }

        "title" {
            x = 15.pixels()
            y = 15.pixels()
            textScale = 1.5f.pixels()
        }

        "subtitle" {
            x = 15.pixels()
            y = SiblingConstraint(20f)
        }

        "closeButton" {
            x = CenterConstraint()
            y = 15.pixels(true)
            width = 80.percent()
            height = 25.pixels()
            color = ColorPalette.secondaryBackground.brighter().constraint
        }
    }

    private val mainContainer by UIBlock(ColorPalette.background)
        .styled(stylesheet["mainContainer"])
        .childOf(window)

    private val leftContainer by UIBlock(ColorPalette.secondaryBackground)
        .styled(stylesheet["leftContainer"])
        .childOf(mainContainer)

    private val rightContainer by UIContainer()
        .styled(stylesheet["rightContainer"])
        .childOf(mainContainer)

    private val themesListContainer by UIContainer() styled stylesheet["themesListContainer"]
    private val themesList = ScrollComponent()
        .styled(stylesheet["themesList"])
        .childOf(themesListContainer)
    private val themesListScrollBar by UIBlock(ColorPalette.secondaryBackground.brighter().brighter())
        .styled(stylesheet["scrollBar"])
        .childOf(themesListContainer)

    private val themeEditor by ThemeEditorContainer() childOf rightContainer

    private val welcomeText by UIWrappedText(
        text = "Choose a theme on the left side of your screen to edit it!",
        centered = true
    )
        .styled(stylesheet["welcomeText"])
        .childOf(rightContainer)

    init {
        themesList.setVerticalScrollBarComponent(themesListScrollBar, hideWhenUseless = true)
        MediaMod.themeManager.onLoadedThemesUpdate(::addLoadedThemes)

        UIText("Theme Editor")
            .styled(stylesheet["title"])
            .childOf(leftContainer)

        UIText("Themes")
            .styled(stylesheet["subtitle"])
            .childOf(leftContainer)

        addLoadedThemes(MediaMod.themeManager.loadedThemes)
        themesListContainer childOf leftContainer

        UIButton(text = "Close", textColor = Color.white)
            .styled(stylesheet["closeButton"])
            .onClick {
                restorePreviousScreen()
            } childOf leftContainer
    }

    override fun onScreenClose() {
        super.onScreenClose()
        saveTheme(themeEditor.theme.get())
    }

    private fun editTheme(theme: Theme?) {
        saveTheme(themeEditor.theme.get())
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
        val theme = Theme.LoadedTheme("My Theme$suffix", classicColors.copy())

        MediaMod.themeManager.addTheme(theme)
        editTheme(theme)
        select(theme)
    }

    private fun addLoadedThemes(themes: List<Theme>) {
        themesList.clearChildren()

        themes.forEach { theme ->
            ThemeListItem(theme)
                .styled(stylesheet["themesListItem"])
                .onClick {
                    select(null)
                    editTheme(this)
                } childOf themesList
        }

        CustomThemeListItem("+ Create new theme...")
            .styled(stylesheet["themesListItem"])
            .onClick {
                select(null)
                createTheme()
            } childOf themesList

        CustomThemeListItem("↓ Import theme...")
            .styled(stylesheet["themesListItem"])
            .onClick {
                select(null)
                importTheme()
            } childOf themesList
    }

    private fun select(theme: Theme?) {
        if (theme == null) themeEditor.theme.set(null)

        themesList.allChildren
            .filterIsInstance<ThemeListItem>()
            .forEach {
                when (theme) {
                    it.theme -> it.select()
                    else -> it.unselect()
                }
            }
    }

    private fun saveTheme(theme: Theme?) {
        theme?.let {
            if (it !is Theme.LoadedTheme) return@let
            MediaMod.themeManager.saveTheme(it)
        }
    }

    private fun importTheme() {
        fun error(message: String) = MediaMod.notificationManager.showNotification("Import theme", message)

        val contents = UDesktop.getClipboardString()
        val url = runCatching { URL(contents) }.getOrNull() ?: return error("Invalid theme URL!")
        // TODO: Read contents of URL
    }
}