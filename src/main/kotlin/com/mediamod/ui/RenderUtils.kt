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

import net.minecraft.client.gui.Gui
import java.awt.Color

/**
 * A class which provides commonly used methods for rendering things to the screen
 *
 * @author Conor Byrne (dreamhopping)
 */
object RenderUtils {
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
    fun renderRectangle(cornerX: Int, cornerY: Int, width: Int, height: Int, color: Color) =
        Gui.drawRect(cornerX, cornerY, width, height, color.rgb)
}
