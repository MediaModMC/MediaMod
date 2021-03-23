package com.mediamod.core.theme

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import java.io.File

object MediaModThemeRegistry {
    private val logger = LogManager.getLogger("MediaMod: Theme Registry")
    private val loadedThemes = mutableListOf<MediaModTheme>()
    private val classicTheme = MediaModTheme(
        "mediamod-classic",
        "MediaMod Classic",
        "The classic theme for MediaMod",
        "MediaMod"
    )

    fun addDefaultThemes() {
        loadedThemes.add(classicTheme)
    }

    fun loadThemes(mediamodThemeDirectory: File) {
        val files = mediamodThemeDirectory.listFiles() ?: return
        files.filter { it.extension == "json" }.forEach {
            try {
                val theme: MediaModTheme = Json.decodeFromString(it.readText())
                loadedThemes.add(theme)

                logger.info("Loaded theme ${theme.name} (${theme.id})")
            } catch (ex: SerializationException) {
                logger.warn("$it is not a valid MediaMod theme!")
            }
        }
    }
}
