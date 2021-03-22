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

package com.mediamod.core.bindings.render

import com.mediamod.core.bindings.BindingRegistry
import com.mediamod.core.resource.MediaModResource
import com.mediamod.core.resource.ResourceFactory
import java.awt.Color
import java.net.URL

interface RenderUtil {
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
    fun drawRectangle(cornerX: Number, cornerY: Number, width: Number, height: Number, color: Color)

    /**
     * Renders text to the screen*
     * It also accepts floats as an input by using the old drawString method
     *
     * @param text The text to render
     * @param x The x co-ordinate of the top left corner
     * @param y The y co-ordinate of the top left corner
     * @param color The color of the text
     */
    fun drawText(text: String, x: Float, y: Float, color: Color)

    /**
     * Renders an image to the screen
     *
     * @param url A URL for the image
     * @param x The x position of the image
     * @param y The y position of the image
     * @param width The width of the image
     * @param height The height of the image
     */
    fun drawImage(url: URL?, x: Int, y: Int, width: Int, height: Int) {
        val resource = ResourceFactory.resourceForUrl(url)
        drawImage(resource, x, y, width, height)
    }

    /**
     * Renders an image to the screen
     *
     * @param resource A resource location for the image
     * @param x The x position of the image
     * @param y The y position of the image
     * @param width The width of the image
     * @param height The height of the image
     */
    fun drawImage(resource: MediaModResource, x: Int, y: Int, width: Int, height: Int)

    /**
     * Renders elements to the screen under a scissor
     *
     * @param x The x co-ordinate of the top left corner
     * @param y The y co-ordinate of the top left corner
     * @param width The width of the scissor
     * @param height The height of the scissor
     * @param drawCode The code that will be run under the scissor
     */
    fun drawScissor(x: Int, y: Int, width: Int, height: Int, drawCode: () -> Unit)

    companion object : RenderUtil by BindingRegistry.renderUtil
}