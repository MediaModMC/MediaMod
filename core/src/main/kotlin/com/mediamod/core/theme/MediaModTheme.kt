package com.mediamod.core.theme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class MediaModTheme(
    val id: String,
    val name: String? = "Theme",
    val description: String? = "An awesome theme for MediaMod",
    val author: String? = "Unknown",
    val colors: MediaModThemeColors = MediaModThemeColors()
) {
    @Serializable
    data class MediaModThemeColors(
        @SerialName("playerPrimaryText")
        private val playerPrimaryTextString: String? = "#ffffff",

        @SerialName("playerSecondaryText")
        private val playerSecondaryTextString: String? = "#b2b2b2",

        @SerialName("playerProgressBarBackground")
        private val playerProgressBarBackgroundString: String? = "#5b5b5b",

        @SerialName("playerProgressBarAccent")
        private val playerProgressBarAccentString: String? = "#00ff00",

        @SerialName("playerBackground")
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
