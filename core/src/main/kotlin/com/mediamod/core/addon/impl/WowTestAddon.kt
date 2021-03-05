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

package com.mediamod.core.addon.impl

import com.mediamod.core.addon.MediaModAddon
import org.apache.logging.log4j.LogManager

class WowTestAddon : MediaModAddon {
    /**
     * A unique identifier for your MediaMod Addon, this can not be the same as any other addon
     *
     * For example: "spotify-addon" or "extension-addon",
     * If a duplicate identifier is found, a warning will be print to the console and the first addon that was loaded will take priority
     */
    override val identifier = "wow-test-addon"

    /**
     * The display name for your addon, this will be shown to the user in various MediaMod menus
     *
     * For example: "Spotify" or "Browser Extension", it is not required to be unique to your addon
     * To avoid confusion, please do not make it the same as any other addon
     */
    override val name: String = "WowTestAddon"

    /**
     * A logger instance for this class, used for logging debug information
     */
    private val logger = LogManager.getLogger("WowTestAddon")

    /**
     * Called when MediaMod is loading your addon, this will occur around the same time as the forge initialisation event
     * This is also called if your addon has been reloaded by the user
     *
     * @return true if the addon has been loaded successfully, otherwise false
     */
    override fun register(): Boolean {
        logger.info("Hello, I have been registered!")
        return true
    }

    /**
     * Called when MediaMod is unloading your addon, this will occur around the same time as Minecraft closing
     * This is also called if your addon has been reloaded by the user
     *
     * @return true if the addon has been unloaded successfully, otherwise false
     */
    override fun unregister(): Boolean {
        logger.info("Goodbye, I have been unregistered!")
        return true
    }
}