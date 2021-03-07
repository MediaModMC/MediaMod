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
import com.mediamod.ui.RenderUtils
import net.minecraft.client.Minecraft
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
    private val fontRenderer = Minecraft.getMinecraft().fontRendererObj
    private val mediamodIconLocation = ResourceLocation("mediamod", "mediamod.png")

    @SubscribeEvent
    fun onRenderTick(event: RenderGameOverlayEvent) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR)
            return

        renderBackground()
        renderText(event.partialTicks)
        renderAlbumArt()
    }

    private fun renderBackground() {
        RenderUtils.drawRectangle(5, 5, 150, 50, Color.DARK_GRAY)
    }

    private fun renderAlbumArt() {
        RenderUtils.drawImage(mediamodIconLocation, 10, 10, 35, 35)
    }

    private fun renderText(partialTicks: Float) {
        val trackName = MediaMod.currentTrackMetadata?.name ?: "Unknown Track"
        val artistName = MediaMod.currentTrackMetadata?.artist ?: "Unknown Artist"

        if (fontRenderer.getStringWidth(artistName) > 90) {
            RenderUtils.drawScrollingText(artistName, 50, 20, Color.WHITE.darker(), 90, 20, partialTicks)
        } else {
            RenderUtils.drawText(artistName, 50f, 20f, Color.WHITE.darker())
        }

        if (fontRenderer.getStringWidth(trackName) > 90) {
            RenderUtils.drawScrollingText(trackName, 50, 10, Color.WHITE, 90, 20, partialTicks)
        } else {
            RenderUtils.drawText(trackName, 50f, 10f, Color.WHITE)
        }
    }
}
