package dev.mediamod.config

import dev.mediamod.MediaMod
import dev.mediamod.screen.RepositionScreen
import gg.essential.universal.UScreen
import gg.essential.vigilance.Vigilant
import java.io.File

@Suppress("ObjectPropertyName")
object Configuration : Vigilant(File("./config/mediamod.toml")) {
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

                decimalSlider(::playerX, "Player X", hidden = true)
                decimalSlider(::playerY, "Player Y", hidden = true)
                text(::selectedTheme, "Theme Name", hidden = true)
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