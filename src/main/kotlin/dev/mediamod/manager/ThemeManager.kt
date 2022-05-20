package dev.mediamod.manager

import dev.mediamod.MediaMod
import dev.mediamod.theme.Theme
import dev.mediamod.theme.impl.ClassicTheme
import dev.mediamod.theme.impl.DynamicTheme
import dev.mediamod.utils.json
import dev.mediamod.utils.logger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.awt.image.BufferedImage
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.writeText

class ThemeManager {
    private val loadedThemesUpdateSubscribers = mutableSetOf<(List<Theme>) -> Unit>()
    private val changeSubscribers = mutableSetOf<Theme.() -> Unit>()
    private val updateSubscribers = mutableSetOf<Theme.() -> Unit>()
    private val themesDirectory = File(MediaMod.dataDirectory, "themes")
    private val themeLocations = mutableMapOf<String, String>()

    val loadedThemes = mutableListOf<Theme>(DynamicTheme(), ClassicTheme())
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
            if (themeLocations[theme.name] != null) {
                logger.warn("Found theme with the same name as another from ${it.path}! Refusing to load it.")
                return
            }

            logger.info("Loaded theme: ${theme.name} from ${it.path}")

            themeLocations[theme.name] = it.name
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

    fun saveTheme(theme: Theme.LoadedTheme) {
        val fileName = themeLocations[theme.name] ?: return
        val path = Path(themesDirectory.absolutePath) / fileName
        path.writeText(json.encodeToString(theme))
    }

    fun importTheme(theme: Theme.LoadedTheme) {
        val fileName = "${theme.name.trim().lowercase().replace("[\\\\/:*?\"<>|]", "_")}.json"
        themeLocations[theme.name] = fileName

        saveTheme(theme)
        addTheme(theme)
    }
}