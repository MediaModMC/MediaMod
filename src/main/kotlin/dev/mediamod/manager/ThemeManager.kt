package dev.mediamod.manager

import dev.mediamod.MediaMod
import dev.mediamod.theme.Theme
import dev.mediamod.theme.impl.DefaultTheme
import dev.mediamod.utils.json
import dev.mediamod.utils.logger
import kotlinx.serialization.decodeFromString
import java.io.File

class ThemeManager {
    private val changeSubscribers = mutableSetOf<Theme.() -> Unit>()
    private val themesDirectory = File(MediaMod.dataDirectory, "themes")

    val loadedThemes = mutableListOf<Theme>(DefaultTheme())

    var currentTheme: Theme = loadedThemes.first()
        set(value) {
            field = value
            emitUpdate()
        }

    fun init() {
        if (!themesDirectory.exists()) {
            themesDirectory.mkdirs()

            // The directory was just created, the chance of there being any themes available is extremely extremely extremely slim
            return
        }

        themesDirectory.walk().forEach {
            if (it.extension != "json")
                return@forEach

            val theme: Theme = json.decodeFromString(it.readText())
            logger.info("Loaded theme: ${theme.name} from ${it.path}")

            loadedThemes.add(theme)
            currentTheme = theme
        }
    }

    fun onChange(callback: Theme.() -> Unit) =
        changeSubscribers.add(callback)

    fun emitUpdate() =
        changeSubscribers.forEach { it.invoke(currentTheme) }
}