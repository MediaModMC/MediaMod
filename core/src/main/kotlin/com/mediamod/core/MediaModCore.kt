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
import com.mediamod.core.bindings.command.MediaModCommandRegistry
import com.mediamod.core.bindings.minecraft.MinecraftClient
import com.mediamod.core.bindings.threading.ThreadingService
import com.mediamod.core.command.impl.MediaModCommand
import com.mediamod.core.render.PlayerRenderer
import com.mediamod.core.schedule.TickSchedulerService
import com.mediamod.core.service.MediaModServiceRegistry
import com.mediamod.core.theme.MediaModThemeRegistry
import com.mediamod.core.track.TrackMetadata
import com.mediamod.core.util.file.createIfNonExisting
import gg.essential.elementa.effects.StencilEffect
import org.apache.logging.log4j.LogManager
import java.io.File

/**
 * The class which handles communication between MediaMod Addons and the mod itself
 *
 * @author Conor Byrne
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
    private val addonDirectory = File(mediamodDirectory, "addons")

    /**
     * The configuration directory for MediaMod Addons
     */
    val addonConfigDirectory = File(addonDirectory, "config")

    /**
     * The themes directory for MediaMod
     * This is where MediaMod themes will be stored
     */
    private val themeDirectory = File(mediamodDirectory, "themes")

    /**
     * An instance of [TrackMetadata], this is the current track information provided by a MediaMod Service
     */
    var currentTrackMetadata: TrackMetadata? = null

    /**
     * Starts MediaMod, called by a mod when the mod loader initialises it
     */
    fun initialize() {
        logger.info("Loading MediaMod v$version!")

        StencilEffect.enableStencil()

        addonDirectory.createIfNonExisting(true)
        themeDirectory.createIfNonExisting(true)
        addonConfigDirectory.createIfNonExisting(true)

        // Load addons
        MediaModAddonRegistry.addAddonSource(addonDirectory)
        MediaModAddonRegistry.discoverAddons()
        MediaModAddonRegistry.initialiseAddons()

        // Load themes
        MediaModThemeRegistry.addDefaultThemes()
        MediaModThemeRegistry.loadThemes(themeDirectory)

        // Register commands
        MediaModCommandRegistry.registerCommand(MediaModCommand())
    }

    /**
     * Fired on each render tick, provided by the mod entry point
     */
    fun onRender(partialTicks: Float) {
        PlayerRenderer.onRenderTick(partialTicks)
    }

    /**
     * Fired on each client tick, provided by the mod entry point
     */
    fun onClientTick() {
        PlayerRenderer.onClientTick()
        TickSchedulerService.onClientTick()
    }

    init {
        ThreadingService.schedule(3000, 0) {
            // Query the current service for some track information, if none is provided, return
            try {
                val trackMetadata =
                    MediaModServiceRegistry.currentService?.fetchTrackMetadata() ?: return@schedule
                if (trackMetadata != currentTrackMetadata) {
                    currentTrackMetadata = trackMetadata
                }
            } catch (t: Throwable) {
                logger.error("An error occurred when fetching TrackMetadata", t)
            }
        }
    }
}
