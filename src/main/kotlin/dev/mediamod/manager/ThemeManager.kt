package dev.mediamod.manager

import dev.mediamod.MediaMod
import dev.mediamod.theme.Theme
import dev.mediamod.theme.impl.DefaultTheme
import dev.mediamod.theme.impl.DynamicTheme
import dev.mediamod.utils.json
import dev.mediamod.utils.logger
import kotlinx.serialization.decodeFromString
import java.awt.image.BufferedImage
import java.io.File

class ThemeManager {
    private val changeSubscribers = mutableSetOf<Theme.() -> Unit>()
    private val themesDirectory = File(MediaMod.dataDirectory, "themes")

    val loadedThemes = mutableListOf(DefaultTheme(), DynamicTheme())

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
        }
    }

    fun onChange(callback: Theme.() -> Unit) =
        changeSubscribers.add(callback)

    // TODO: When the theme changes, we need some way to get the current image so we can update the theme
    fun emitUpdate() =
        changeSubscribers.forEach { it.invoke(currentTheme) }

    fun updateTheme(image: BufferedImage) {
        currentTheme.update(image)
        emitUpdate()
    }
}