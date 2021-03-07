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

package com.mediamod.ui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * A class which provides commonly used methods for rendering things to the screen
 *
 * @author Conor Byrne (dreamhopping)
 */
object RenderUtils {
    private val fontRenderer: FontRenderer = Minecraft.getMinecraft().fontRendererObj

    /**
     * Renders a rectangle to the screen
     * Calls [Gui.drawRect], this function is a wrapper to make the parameters easier to understand
     *
     * @param cornerX The x co-ordinate of the top left corner
     * @param cornerY The y co-ordinate of the top left corner
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The desired color for the rectangle
     */
    fun drawRectangle(cornerX: Int, cornerY: Int, width: Int, height: Int, color: Color) =
        Gui.drawRect(cornerX, cornerY, width, height, color.rgb)

    /**
     * Renders text to the screen
     * Calls [FontRenderer.drawString], this function is a wrapper to make the parameters easier to understand
     *
     * It also accepts floats as an input by using the old drawString method
     *
     * @param text The text to render
     * @param x The x co-ordinate of the top left corner
     * @param y The y co-ordinate of the top left corner
     * @param color The color of the text
     */
    fun drawText(text: String, x: Float, y: Float, color: Color) =
        fontRenderer.drawString(text, x, y, color.rgb, false)

    /**
     * Renders elements to the screen under a scissor
     * Scales the inputs to [ScaledResolution]
     *
     * @param x The x co-ordinate of the top left corner
     * @param y The y co-ordinate of the top left corner
     * @param width The width of the scissor
     * @param height The height of the scissor
     * @param drawCode The code that will be run under the scissor
     */
    inline fun drawScissor(x: Int, y: Int, width: Int, height: Int, drawCode: () -> Unit) {
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
        val scaleFactor = scaledResolution.scaleFactor

        // Scale inputs
        val scaledX = x * scaleFactor
        val scaledY = (scaledResolution.scaledHeight * scaleFactor) - ((y + height) * scaleFactor)
        val scaledWidth = width * scaleFactor
        val scaledHeight = height * scaleFactor

        // Apply scissor and render elements
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glScissor(scaledX, scaledY, scaledWidth, scaledHeight)

        drawCode()

        GL11.glDisable(GL11.GL_SCISSOR_TEST)
    }
}
