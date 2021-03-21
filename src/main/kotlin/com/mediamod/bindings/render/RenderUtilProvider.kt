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

package com.mediamod.bindings.render

import com.mediamod.core.bindings.render.IRenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * A class which provides commonly used methods for rendering things to the screen
 *
 * @author Conor Byrne (dreamhopping)
 */
class RenderUtilProvider : IRenderUtil {
    private val fontRenderer: FontRenderer = Minecraft.getMinecraft().fontRendererObj

    /**
     * Renders a rectangle to the screen
     * This function supports any number for width and height, converting to double inside the function
     *
     * @param cornerX The x co-ordinate of the top left corner
     * @param cornerY The y co-ordinate of the top left corner
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The desired color for the rectangle
     */
    override fun drawRectangle(cornerX: Number, cornerY: Number, width: Number, height: Number, color: Color) {
        var left = cornerX.toDouble()
        var right = cornerX.toDouble() + width.toDouble()
        var top = cornerY.toDouble()
        var bottom = cornerY.toDouble() + height.toDouble()

        val colorInt = color.rgb
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        // Ensures the right is after left
        if (left < right)
            left = right.also { right = left }

        // Ensures that bottom is below top
        if (top < bottom)
            top = bottom.also { bottom = top }

        // Converting colour to int
        val f3 = (colorInt shr 24 and 255).toFloat() / 255.0f
        val f = (colorInt shr 16 and 255).toFloat() / 255.0f
        val f1 = (colorInt shr 8 and 255).toFloat() / 255.0f
        val f2 = (colorInt and 255).toFloat() / 255.0f

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.color(f, f1, f2, f3)

        with(worldRenderer) {
            begin(7, DefaultVertexFormats.POSITION)
            pos(left, bottom, 0.0).endVertex()
            pos(right, bottom, 0.0).endVertex()
            pos(right, top, 0.0).endVertex()
            pos(left, top, 0.0).endVertex()
        }

        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

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
    override fun drawText(text: String, x: Float, y: Float, color: Color) {
        fontRenderer.drawString(text, x, y, color.rgb, false)
    }

    /**
     * Renders an image to the screen
     *
     * @param imageLocation A resource location for the image
     * @param x The x position of the image
     * @param y The y position of the image
     * @param width The width of the image
     * @param height The height of the image
     */
    fun drawImage(imageLocation: ResourceLocation, x: Int, y: Int, width: Int, height: Int) {
        Minecraft.getMinecraft().textureManager.bindTexture(imageLocation)
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, width, height, width.toFloat(), height.toFloat())
    }

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
    override inline fun drawScissor(x: Int, y: Int, width: Int, height: Int, drawCode: () -> Unit) {
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
