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


package com.mediamod.core

/**
 * The class which handles communication between MediaMod Addons and the mod itself
 *
 * @author Conor Byrne (dreamhopping)
 */
object MediaModCore {
    /**
     * The current version of MediaMod
     */
    const val version = "2.0.0-dev.1"

    /**
     * The current api version for MediaMod addons, if an addon is on a newer or older API version it will not be loaded
     */
    const val apiVersion = 1

    /**
     * Check if the current instance of Minecraft is a development environment
     * The check is done by checking if the class net.minecraft.client.Minecraft exists
     * In a production environment, this would be obfuscated and would not be found
     *
     * This value is cached after the first call as the environment state can not change without a reboot of the client
     * @return true if we are in a development environment, otherwise false
     */
    val isDevelopment: Boolean by lazy {
        kotlin.runCatching { Class.forName("net.minecraft.client.Minecraft") }.isSuccess
    }
}
