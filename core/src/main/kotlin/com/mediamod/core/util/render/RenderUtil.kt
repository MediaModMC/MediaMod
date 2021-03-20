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

package com.mediamod.core.util.render

import java.awt.Color

/**
 * A class which provides commonly used methods for rendering things to the screen
 *
 * @author Conor Byrne (dreamhopping)
 */
abstract class RenderUtil {
    companion object {
        var instance: RenderUtil? = null
            set(value) {
                if (field != null) {
                    field = value
                } else {
                    error("Instance for $this has already been set!")
                }
            }
    }

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
    abstract fun drawRectangle(cornerX: Number, cornerY: Number, width: Number, height: Number, color: Color)

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
    abstract fun drawText(text: String, x: Float, y: Float, color: Color)

    /**
     * Renders an image to the screen
     *
     * @param imageLocation A resource location for the image
     * @param x The x position of the image
     * @param y The y position of the image
     * @param width The width of the image
     * @param height The height of the image
     */
    // abstract fun drawImage(bufferedImage: BufferedImage?, x: Int, y: Int, width: Int, height: Int)

    /**
     * Renders elements to the screen under a scissor
     *
     * @param x The x co-ordinate of the top left corner
     * @param y The y co-ordinate of the top left corner
     * @param width The width of the scissor
     * @param height The height of the scissor
     * @param drawCode The code that will be run under the scissor
     */
    abstract fun drawScissor(x: Int, y: Int, width: Int, height: Int, drawCode: () -> Unit)
}
