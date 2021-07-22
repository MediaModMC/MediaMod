/*
 *     MediaMod is a mod for Minecraft which displays information about your current track in-game
 *     Copyright (C) 2021 Conor Byrne
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.mediamod.core.theme

import com.google.gson.Gson
import com.mediamod.core.config.impl.MediaModConfig
import com.mediamod.core.theme.impl.MediaModDefaultTheme
import com.mediamod.core.theme.impl.MediaModDynamicTheme
import org.apache.logging.log4j.LogManager
import java.io.File

object MediaModThemeRegistry {
    private val gson = Gson()
    private val logger = LogManager.getLogger("MediaMod: Theme Registry")

    val loadedThemes = mutableListOf<MediaModTheme>()
    val selectedTheme: MediaModTheme
        get() =
            loadedThemes.firstOrNull { it.identifier == MediaModConfig.selectedTheme }
                ?: loadedThemes.first { it.identifier == "mediamod-classic" }

    fun addDefaultThemes() {
        loadedThemes.add(MediaModDefaultTheme())
        loadedThemes.add(MediaModDynamicTheme())
    }

    fun loadThemes(mediamodThemeDirectory: File) {
        val files = mediamodThemeDirectory.listFiles() ?: return
        files.filter { it.extension == "json" }.forEach {
            try {
                val theme = gson.fromJson(it.readText(), MediaModTheme::class.java)
                loadedThemes.add(theme)

                logger.info("Loaded theme '${theme.name}' (${theme.identifier})")
            } catch (e: Exception) {
                logger.warn("$it is not a valid MediaMod theme!")
            }
        }
    }
}
