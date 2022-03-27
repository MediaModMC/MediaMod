package dev.mediamod.config

import dev.mediamod.MediaMod
import dev.mediamod.gui.screen.RepositionScreen
import dev.mediamod.gui.screen.editor.ThemeEditorScreen
import gg.essential.universal.UScreen
import gg.essential.vigilance.Vigilant
import java.io.File
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

@Suppress("ObjectPropertyName")
object Configuration : Vigilant(File("./config/mediamod.toml"), "MediaMod") {
    private val updateListeners = mutableMapOf<KProperty<*>, MutableList<(Any.() -> Unit)>>()

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

    var playerFirstFormatString by observable("[track]")
    var playerSecondFormatString by observable("by [artist]")

    var playerX = 5f
    var playerY = 5f

    var textScrollSpeed = 0.25f

    private var _preferredService = 0
        set(value) {
            field = value
            preferredService = MediaMod.serviceManager.services[value].displayName
        }

    var preferredService = ""

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
                    UScreen.displayScreen(ThemeEditorScreen())
                }

                decimalSlider(::playerX, "Player X", hidden = true)
                decimalSlider(::playerY, "Player Y", hidden = true)
                text(::selectedTheme, "Theme Name", hidden = true)
            }

            subcategory("Behaviour") {
                text(
                    ::playerFirstFormatString,
                    "Track name format",
                    "Customise the way the track name appears on the MediaMod Player. You can use [track] for the track name, and [artist] for the artist name.",
                    "[track]"
                )

                text(
                    ::playerSecondFormatString,
                    "Artist name format",
                    "Customise the way the artist name appears on the MediaMod Player. You can use [track] for the track name, and [artist] for the artist name.",
                    "by [artist]"
                )

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

        category("Services") {
            subcategory("Behaviour") {
                selector(
                    ::_preferredService,
                    "Preferred Service",
                    "Choose the service which will take priority over others. If that service isn't playing anything, it will fallback to the others.",
                    MediaMod.serviceManager.services.map { it.displayName }
                )

                text(::preferredService, "Preferred Service Name", hidden = true)
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

        _preferredService = MediaMod.serviceManager.services
            .indexOfFirst { it.displayName == preferredService }
            .takeIf { it >= 0 } ?: 0
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> observable(default: T) = Delegates.observable(default) { property, _, newValue ->
        updateListeners[property]?.forEach { it.invoke(newValue as Any) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> listen(property: KProperty<T>, block: T.() -> Unit) {
        updateListeners
            .computeIfAbsent(property) { mutableListOf() }
            .add(block as Any.() -> Unit)
    }
}