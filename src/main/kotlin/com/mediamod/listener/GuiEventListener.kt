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

import com.mediamod.ui.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

/**
 * Listens to all events related to GUIs like [RenderGameOverlayEvent]
 *
 * @author Conor Byrne (dreamhopping)
 */
object GuiEventListener {
    private val fontRenderer = Minecraft.getMinecraft().fontRendererObj

    private const val textProgressIncrement = 0.005
    private var textProgressPercent = 0.00

    @SubscribeEvent
    fun onRenderTick(event: RenderGameOverlayEvent) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR)
            return

        renderBackground()
        renderText(event.partialTicks)
    }

    private fun renderBackground() {
        RenderUtils.drawRectangle(5, 5, 150, 50, Color.DARK_GRAY)
    }

    private fun renderText(partialTicks: Float) {
        val trackName = "welcome to the end (feat. Sewerperson)"
        val artistName = "Heylog and Sewerperson"

        if (fontRenderer.getStringWidth(artistName) > 90) {
            drawMarqueeText(artistName, 50, 20, Color.WHITE.darker(), partialTicks)
        } else {
            RenderUtils.drawText(artistName, 50f, 20f, Color.WHITE)
        }

        if (fontRenderer.getStringWidth(trackName) > 90) {
            drawMarqueeText(trackName, 50, 10, Color.WHITE, partialTicks)
        } else {
            RenderUtils.drawText(trackName, 50f, 10f, Color.WHITE)
        }
    }

    /**
     * Renders text to the screen that rotates on the x axis around the scissored area, similar to a HTML marquee
     */
    private fun drawMarqueeText(text: String, x: Int, y: Int, color: Color, partialTicks: Float) {
        val textString = "$text     $text"
        val textWidth = fontRenderer.getStringWidth(textString)

        val textProgressPartialTicks =
            min((textProgressPercent + partialTicks * textProgressIncrement), 1.0)

        RenderUtils.drawScissor(x, y, 90, 15) {
            RenderUtils.drawText(
                textString,
                ((x - (textProgressPartialTicks * max(0, textWidth - 90)))).toFloat(),
                y.toFloat(),
                color
            )
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END)
            return

        textProgressPercent += textProgressIncrement
        if (textProgressPercent > 1.0) {
            textProgressPercent = 0.0
        }
    }
}
