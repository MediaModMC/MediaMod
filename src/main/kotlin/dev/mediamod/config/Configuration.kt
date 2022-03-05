package dev.mediamod.config

import dev.mediamod.MediaMod
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
            manager.emitUpdate()
        }

    init {
        category("Appearance") {
            selector(
                ::_selectedTheme,
                "Theme",
                "Change the appearance of the MediaMod Player",
                MediaMod.themeManager.loadedThemes.map { it.name }
            )

            text(::selectedTheme, "Theme Name", hidden = true)
        }

        initialize()

        _selectedTheme = MediaMod.themeManager.loadedThemes
            .indexOfFirst { it.name == selectedTheme }
            .takeIf { it >= 0 } ?: 0
    }
}