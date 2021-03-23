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

package com.mediamod.core.render

import com.mediamod.core.MediaModCore
import com.mediamod.core.bindings.render.RenderUtil
import java.awt.Color
import java.net.URL

object PlayerRenderer {
    private val titleTextRenderer = MarqueeingTextRenderer(50, 10, 90, 20)
    private val artistTextRenderer = MarqueeingTextRenderer(50, 20, 90, 20, textColor = Color.WHITE.darker())
    private val progressBarRenderer = ProgressBarRenderer(50, 35, 90, 10)

    fun onRenderTick(partialTicks: Float) {
        if (MediaModCore.currentTrackMetadata == null)
            return

        renderBackground()
        renderText(partialTicks)
        renderAlbumArt()
        renderProgressBar()
    }

    fun onClientTick() {
        titleTextRenderer.onTick()
        artistTextRenderer.onTick()
    }

    private fun renderBackground() {
        RenderUtil.drawRectangle(5, 5, 145, 45, Color.DARK_GRAY)
    }

    private fun renderAlbumArt() {
        RenderUtil.drawImage(URL(MediaModCore.currentTrackMetadata?.albumArtUrl), 10, 10, 35, 35)
    }

    private fun renderProgressBar() {
        // We should only render the progress bar if we have the progress and duration of the track
        progressBarRenderer.setProgress(MediaModCore.currentTrackMetadata?.progress ?: return)
        progressBarRenderer.duration = MediaModCore.currentTrackMetadata?.duration ?: return

        progressBarRenderer.render()
    }

    private fun renderText(partialTicks: Float) {
        titleTextRenderer.text = MediaModCore.currentTrackMetadata?.name ?: "Unknown Track"
        artistTextRenderer.text = MediaModCore.currentTrackMetadata?.artist ?: "Unknown Artist"

        titleTextRenderer.render(partialTicks)
        artistTextRenderer.render(partialTicks)
    }
}
