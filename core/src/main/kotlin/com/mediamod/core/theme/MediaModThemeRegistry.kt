package com.mediamod.core.theme

import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import java.io.File

object MediaModThemeRegistry {
    private val gson = Gson()
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
                val theme = gson.fromJson(it.readText(), MediaModTheme::class.java)
                loadedThemes.add(theme)

                logger.info("Loaded theme '${theme.name}' (${theme.id})")
            } catch (e: Exception) {
                logger.warn("$it is not a valid MediaMod theme!")
            }
        }
    }
}
