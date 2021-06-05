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

package com.mediamod.core.addon

/**
 * The interface which a MediaMod Addon will implement, this will allow MediaMod to recognise it
 * @author Conor Byrne
 */
abstract class MediaModAddon(
    /**
     * A unique identifier for your MediaMod Addon, this can not be the same as any other addon
     *
     * For example: "spotify-addon" or "extension-addon",
     * If a duplicate identifier is found, a warning will be print to the console and the first addon that was loaded will take priority
     */
    val identifier: String,
) {
    /**
     * Called when MediaMod is initialising your addon
     * The addon should be ready for usage when this method is complete
     */
    abstract fun initialise()

    /**
     * Called when MediaMod is unloading your addon
     * The addon should do any configuration saving, etc. in this method
     */
    open fun unload() {}
}
