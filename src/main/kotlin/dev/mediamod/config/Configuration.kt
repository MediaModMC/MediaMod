package dev.mediamod.config

import dev.mediamod.MediaMod
import dev.mediamod.gui.screen.RepositionScreen
import dev.mediamod.gui.screen.ThemeEditorScreen
import gg.essential.universal.UScreen
import gg.essential.vigilance.Vigilant
import java.io.File

@Suppress("ObjectPropertyName")
object Configuration : Vigilant(File("./config/mediamod.toml"), "MediaMod") {
    private var _selectedTheme = 0
        set(value) {
            field = value
            selectedTheme = MediaMod.themeManager.loadedThemes[value].name
        }

    var selectedTheme = "Default"
        set(value) {
            field = value

            val manager = MediaMod.themeManager
            manager.currentTheme = manager.loadedThemes.firstOrNull { it.name == value } ?: manager.loadedThemes.first()
            manager.emitChange()
        }

    var spotifyAccessToken = ""
    var spotifyRefreshToken = ""
    var playerX = 5f
    var playerY = 5f
    var textScrollSpeed = 0.25f

    init {
        category("General") {
            subcategory("Appearance") {
                button(
                    "Reposition Player",
                    "Change the position of the MediaMod Player",
                    "Open"
                ) {
                    UScreen.displayScreen(RepositionScreen())
                }

                selector(
                    ::_selectedTheme,
                    "Theme",
                    "Change the appearance of the MediaMod Player",
                    MediaMod.themeManager.loadedThemes.map { it.name }
                )

                button(
                    "Theme Editor",
                    "Create or edit themes for the MediaMod Player",
                    "Open"
                ) {
                    gui()?.let { UScreen.displayScreen(ThemeEditorScreen(it)) }
                }

                decimalSlider(::playerX, "Player X", hidden = true)
                decimalSlider(::playerY, "Player Y", hidden = true)
                text(::selectedTheme, "Theme Name", hidden = true)
            }

            subcategory("Behaviour") {
                decimalSlider(
                    ::textScrollSpeed,
                    "Text Scroll Speed",
                    "Determines how fast the text will scroll when it exceeds the width of the player",
                    min = 0.05f,
                    max = 0.75f,
                    decimalPlaces = 2
                )
            }
        }

        MediaMod.serviceManager.services
            .filter { it.hasConfiguration }
            .forEach {
                with(it) {
                    category(displayName) { configuration() }
                }
            }

        initialize()

        _selectedTheme = MediaMod.themeManager.loadedThemes
            .indexOfFirst { it.name == selectedTheme }
            .takeIf { it >= 0 } ?: 0
    }
}