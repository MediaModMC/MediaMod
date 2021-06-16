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

import com.google.gson.annotations.SerializedName
import java.awt.Color

data class MediaModTheme(
    val identifier: String,
    val name: String? = "Theme",
    val description: String? = "An awesome theme for MediaMod",
    val author: String? = "Unknown",
    val colors: MediaModThemeColors = MediaModThemeColors()
) {
    data class MediaModThemeColors(
        @SerializedName("playerPrimaryText")
        private val playerPrimaryTextString: String? = "#ffffff",

        @SerializedName("playerSecondaryText")
        private val playerSecondaryTextString: String? = "#b2b2b2",

        @SerializedName("playerProgressBarBackground")
        private val playerProgressBarBackgroundString: String? = "#5b5b5b",

        @SerializedName("playerProgressBarAccent")
        private val playerProgressBarAccentString: String? = "#00ff00",

        @SerializedName("playerBackground")
        private val playerBackgroundString: String? = "#404040",
    ) {
        val playerPrimaryText: Color by lazy {
            Color.decode(playerPrimaryTextString)
        }

        val playerSecondaryText: Color by lazy {
            Color.decode(playerSecondaryTextString)
        }

        val playerProgressBarBackground: Color by lazy {
            Color.decode(playerProgressBarBackgroundString)
        }

        val playerProgressBarAccent: Color by lazy {
            Color.decode(playerProgressBarAccentString)
        }

        val playerBackground: Color by lazy {
            Color.decode(playerBackgroundString)
        }
    }
}
