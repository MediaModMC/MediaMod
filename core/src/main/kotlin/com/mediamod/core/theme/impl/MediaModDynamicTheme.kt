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

package com.mediamod.core.theme.impl

import com.mediamod.core.MediaModCore
import com.mediamod.core.bindings.texture.TextureManager
import com.mediamod.core.resource.ResourceFactory
import com.mediamod.core.theme.MediaModTheme
import com.mediamod.core.util.color.encode
import java.awt.Color
import java.awt.image.BufferedImage
import java.net.URL

class MediaModDynamicTheme : MediaModTheme(
    "mediamod-dynamic",
    "MediaMod Dynamic",
    "Adjusts the colors of the player depending on the artwork",
    "MediaMod"
) {
    private val averageColorCache = mutableMapOf<BufferedImage, Color>()
    override var colors: MediaModThemeColors = generateColors()

    override fun updateTheme() {
        colors = generateColors()
    }

    private fun generateColors(): MediaModThemeColors {
        var colors = MediaModThemeColors()
        val data = MediaModCore.currentTrackMetadata ?: return colors

        val image = TextureManager.getBufferedImageForResource(ResourceFactory.resourceForUrl(URL(data.albumArtUrl)))
            ?: return colors

        val averageColor = averageColor(image)
        if (averageColor == Color.GRAY) return colors

        colors = MediaModThemeColors(
            Color.WHITE.encode(),
            Color.WHITE.darker().encode(),
            averageColor.darker().encode(),
            averageColor.brighter().encode(),
            averageColor.encode()
        )

        return colors
    }

    private fun averageColor(image: BufferedImage): Color {
        averageColorCache[image]?.let {
            return it
        }

        val color = arrayOf<Color>(Color.gray)
        var countR: Long = 0
        var countG: Long = 0
        var countB: Long = 0

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val pixel = Color(image.getRGB(x, y))
                countR += pixel.red
                countG += pixel.green
                countB += pixel.blue
            }
        }

        val area = image.width * image.height
        color[0] = Color(countR.toInt() / area, countG.toInt() / area, countB.toInt() / area)
        averageColorCache[image] = color[0]

        return color[0]
    }
}