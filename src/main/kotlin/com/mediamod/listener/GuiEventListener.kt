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

package com.mediamod.listener

import com.mediamod.MediaMod
import com.mediamod.ui.ImageUtils
import com.mediamod.ui.RenderUtils
import com.mediamod.ui.render.MarqueeingTextRenderer
import com.mediamod.ui.render.ProgressBarRenderer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

/**
 * Listens to all events related to GUIs like [RenderGameOverlayEvent]
 *
 * @author Conor Byrne (dreamhopping)
 */
object GuiEventListener {
    private val mediamodIconLocation = ResourceLocation("mediamod", "mediamod.png")

    private val titleTextRenderer = MarqueeingTextRenderer(50, 10, 90, 20)
    private val artistTextRenderer = MarqueeingTextRenderer(50, 20, 90, 20, textColor = Color.WHITE.darker())
    private val progressBarRenderer = ProgressBarRenderer(50, 35, 90, 10)

    @SubscribeEvent
    fun onRenderTick(event: RenderGameOverlayEvent) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR)
            return

        renderBackground()
        renderText(event.partialTicks)
        renderAlbumArt()
        renderProgressBar()
    }

    private fun renderBackground() {
        RenderUtils.drawRectangle(5, 5, 145, 45, Color.DARK_GRAY)
    }

    private fun renderAlbumArt() {
        val imageLocation =
            ImageUtils.getResourceForURL(MediaMod.currentTrackMetadata?.albumArtUrl) ?: mediamodIconLocation

        RenderUtils.drawImage(imageLocation, 10, 10, 35, 35)
    }

    private fun renderProgressBar() {
        // We should only render the progress bar if we have the progress and duration of the track
        progressBarRenderer.setProgress(MediaMod.currentTrackMetadata?.progress ?: return)
        progressBarRenderer.duration = MediaMod.currentTrackMetadata?.duration ?: return

        progressBarRenderer.render()
    }

    private fun renderText(partialTicks: Float) {
        titleTextRenderer.text = MediaMod.currentTrackMetadata?.name ?: "Unknown Track"
        artistTextRenderer.text = MediaMod.currentTrackMetadata?.artist ?: "Unknown Artist"

        titleTextRenderer.render(partialTicks)
        artistTextRenderer.render(partialTicks)
    }
}
