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
    private val loadedThemesUpdateSubscribers = mutableSetOf<(List<Theme>) -> Unit>()
    private val changeSubscribers = mutableSetOf<Theme.() -> Unit>()
    private val updateSubscribers = mutableSetOf<Theme.() -> Unit>()
    private val themesDirectory = File(MediaMod.dataDirectory, "themes")

    val loadedThemes = mutableListOf<Theme>(DefaultTheme(), DynamicTheme())
    var currentTheme: Theme = loadedThemes.first()
        set(value) {
            field = value
            emitChange()
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

            val theme: Theme.LoadedTheme = json.decodeFromString(it.readText())
            logger.info("Loaded theme: ${theme.name} from ${it.path}")

            loadedThemes.add(theme)
        }
    }

    fun addTheme(theme: Theme) {
        loadedThemes.add(theme)
        emitLoadedThemesUpdate()
    }

    fun onLoadedThemesUpdate(callback: (List<Theme>) -> Unit) =
        loadedThemesUpdateSubscribers.add(callback)

    fun emitLoadedThemesUpdate() =
        loadedThemesUpdateSubscribers.forEach { it.invoke(loadedThemes) }

    fun onChange(callback: Theme.() -> Unit) =
        changeSubscribers.add(callback)

    fun emitChange() =
        changeSubscribers.forEach { it.invoke(currentTheme) }

    fun onUpdate(callback: Theme.() -> Unit) =
        updateSubscribers.add(callback)

    fun emitUpdate() =
        updateSubscribers.forEach { it.invoke(currentTheme) }

    fun updateTheme(image: BufferedImage) {
        currentTheme.update(image)
        emitUpdate()
    }
}