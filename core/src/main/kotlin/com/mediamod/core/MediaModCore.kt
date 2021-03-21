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

import com.mediamod.core.addon.MediaModAddonRegistry
import com.mediamod.core.bindings.minecraft.MinecraftClient
import com.mediamod.core.service.MediaModServiceRegistry
import com.mediamod.core.track.TrackMetadata
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.concurrent.fixedRateTimer

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

    /**
     * A logger instance for this class, used to log any issues that may occur during mod-loading
     */
    private val logger = LogManager.getLogger("MediaMod")

    /**
     * The data directory for MediaMod
     * This contains addons, configuration files & more
     */
    private val mediamodDirectory = File(MinecraftClient.mcDataDir, "mediamod")

    /**
     * The addons directory for MediaMod
     * This is where MediaMod addons will be stored
     */
    private val mediamodAddonDirectory = File(mediamodDirectory, "mediamod")

    /**
     * An instance of [TrackMetadata], this is the current track information provided by a MediaMod Service
     */
    var currentTrackMetadata: TrackMetadata? = null

    /**
     * Starts MediaMod, called by a mod when the mod loader initialises it
     */
    fun initialize() {
        logger.info("Loading MediaMod v$version!")

        // Create the "./mediamod" and "./mediamod/addons" directories if they don't exist
        if (!mediamodAddonDirectory.exists())
            mediamodAddonDirectory.mkdirs()

        try {
            MediaModAddonRegistry.addAddonSource(mediamodAddonDirectory)

            MediaModAddonRegistry.discoverAddons()
            MediaModAddonRegistry.initialiseAddons()
        } catch (t: Throwable) {
            logger.error(t.message)
        }
    }

    init {
        fixedRateTimer("MediaMod: TrackMetadata", false, 0, 3000) {
            // Query the current service for some track information, if none is provided, return
            try {
                val trackMetadata =
                    MediaModServiceRegistry.currentService?.fetchTrackMetadata() ?: return@fixedRateTimer
                if (trackMetadata != currentTrackMetadata) {
                    currentTrackMetadata = trackMetadata
                }
            } catch (t: Throwable) {
                logger.error("An error occurred when fetching TrackMetadata", t)
            }
        }
    }
}
