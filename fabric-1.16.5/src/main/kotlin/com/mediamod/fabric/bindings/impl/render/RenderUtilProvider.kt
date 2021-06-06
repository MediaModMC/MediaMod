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

package com.mediamod.fabric.bindings.impl.render

import com.mediamod.core.bindings.render.RenderUtil
import com.mediamod.core.resource.MediaModResource
import com.mediamod.fabric.util.toIdentifier
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.opengl.GL11
import java.awt.Color

class RenderUtilProvider : RenderUtil {
    override fun drawRectangle(cornerX: Number, cornerY: Number, width: Number, height: Number, color: Color) {
        var left = cornerX.toDouble()
        var right = cornerX.toDouble() + width.toDouble()
        var top = cornerY.toDouble()
        var bottom = cornerY.toDouble() + height.toDouble()
        val colorInt = color.rgb
        val matrix = MatrixStack().peek().model

        // Ensures the right is after left
        if (left < right)
            left = right.also { right = left }

        // Ensures that bottom is below top
        if (top < bottom)
            top = bottom.also { bottom = top }

        // Converting a color to rgba
        val alpha = (colorInt shr 24 and 255).toFloat() / 255.0f
        val red = (colorInt shr 16 and 255).toFloat() / 255.0f
        val green = (colorInt shr 8 and 255).toFloat() / 255.0f
        val blue = (colorInt and 255).toFloat() / 255.0f

        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()

        with(Tessellator.getInstance().buffer) {
            begin(7, VertexFormats.POSITION_COLOR)
            vertex(matrix, left.toFloat(), bottom.toFloat(), 0.0f).color(red, green, blue, alpha).next()
            vertex(matrix, right.toFloat(), bottom.toFloat(), 0.0f).color(red, green, blue, alpha).next()
            vertex(matrix, right.toFloat(), top.toFloat(), 0.0f).color(red, green, blue, alpha).next()
            vertex(matrix, left.toFloat(), top.toFloat(), 0.0f).color(red, green, blue, alpha).next()
            end()

            BufferRenderer.draw(this)
        }

        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    override fun drawText(text: String, x: Float, y: Float, color: Color) {
        MinecraftClient.getInstance().textRenderer.draw(MatrixStack(), text, x, y, color.rgb)
    }

    override fun drawImage(resource: MediaModResource, x: Int, y: Int, width: Int, height: Int) {
        MinecraftClient.getInstance().textureManager.bindTexture(resource.toIdentifier())
        DrawableHelper.drawTexture(MatrixStack(), x, y, 999, 0f, 0f, width, height, width, height)
    }

    override fun drawScissor(x: Int, y: Int, width: Int, height: Int, drawCode: () -> Unit) {
        val scaleFactor = MinecraftClient.getInstance().window.scaleFactor
        val windowScaledHeight = MinecraftClient.getInstance().window.scaledHeight

        val scaledX = (x * scaleFactor).toInt()
        val scaledY = ((windowScaledHeight * scaleFactor) - ((y + height) * scaleFactor)).toInt()
        val scaledWidth = (width * scaleFactor).toInt()
        val scaledHeight = (height * scaleFactor).toInt()

        // Apply scissor and render elements
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glScissor(scaledX, scaledY, scaledWidth, scaledHeight)

        drawCode()

        GL11.glDisable(GL11.GL_SCISSOR_TEST)
    }
}
