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
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11
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
    private var textProgressPercent = 0.00

    // Constants for GLScissor box
    private const val boxX = 50
    private const val boxY = 5
    private const val boxWidth = 90
    private const val boxHeight = 20

    // save me
    private const val INCREMEMENBRSHIEJBSDBHCXBDFES = 0.005

    @SubscribeEvent
    fun onRenderTick(event: RenderGameOverlayEvent) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR)
            return

        renderBackground()
        renderText(event.partialTicks)
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END)
            return

        textProgressPercent += INCREMEMENBRSHIEJBSDBHCXBDFES
        if (textProgressPercent > 1.0) {
            textProgressPercent = 0.0
        }
    }

    private fun renderBackground() {
        RenderUtils.drawRectangle(5, 5, 150, 50, Color.DARK_GRAY)
    }

    private fun renderText(partialTicks: Float) {
        val trackName = "welcome to the end (feat. Sewerperson)"

        if (fontRenderer.getStringWidth(trackName) > 90) {
            val textString = "$trackName     $trackName"
            val textProgressPartialTicks =
                min((textProgressPercent + partialTicks * INCREMEMENBRSHIEJBSDBHCXBDFES), 1.0)

            // Setup GL Scissoring
            GL11.glEnable(GL11.GL_SCISSOR_TEST)

            // Scissor box calculation
            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            val scaleFactor = scaledResolution.scaleFactor
            val x = boxX * scaleFactor
            val y = (scaledResolution.scaledHeight * scaleFactor) - ((boxY + boxHeight) * scaleFactor)
            val width = boxWidth * scaleFactor
            val height = boxHeight * scaleFactor
            val textWidth = fontRenderer.getStringWidth(textString)

            // Apply scissor and render text
            GL11.glScissor(x, y, width, height)

            RenderUtils.drawText(
                textString,
                (50 - (textProgressPartialTicks * max(0, textWidth - boxWidth))).toFloat(),
                10f,
                Color.WHITE
            )

            GL11.glDisable(GL11.GL_SCISSOR_TEST)
        } else {
            RenderUtils.drawText(trackName, 50f, 10f, Color.WHITE)
        }
    }
}
