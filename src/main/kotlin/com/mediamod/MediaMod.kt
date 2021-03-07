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


package com.mediamod

import com.mediamod.core.MediaModCore
import com.mediamod.core.addon.MediaModAddonRegistry
import com.mediamod.core.service.MediaModServiceRegistry
import com.mediamod.core.track.TrackMetadata
import com.mediamod.listener.GuiEventListener
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.EventBus
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.concurrent.fixedRateTimer

/**
 * The mod class for MediaMod
 * @author Conor Byrne (dreamhopping)
 */
@Mod(
    modid = "mediamod",
    version = MediaModCore.version,
    modLanguageAdapter = "com.mediamod.launch.KotlinLanguageAdapter"
)
object MediaMod {
    /**
     * A logger instance for this class, used to log any issues that may occur during mod-loading
     */
    private val logger = LogManager.getLogger("MediaMod")

    /**
     * The data directory for MediaMod
     * This contains addons, configuration files & more
     */
    private val mediamodDirectory = File(Minecraft.getMinecraft().mcDataDir, "mediamod")

    /**
     * The addons directory for MediaMod
     * This is where MediaMod addons will be stored
     */
    private val mediamodAddonDirectory = File(mediamodDirectory, "addons")

    /**
     * An instance of [TrackMetadata], this is the current track information provided by a [MediaModService]
     */
    var currentTrackMetadata: TrackMetadata? = null

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        logger.info("Loading MediaMod v${MediaModCore.version}!")

        // Create the "./mediamod" and "./mediamod/addons" directories if they don't exist
        if (!mediamodAddonDirectory.exists())
            mediamodAddonDirectory.mkdirs()

        // Add ".mediamod/addons" as an addon source
        MediaModAddonRegistry.addAddonSource(mediamodAddonDirectory)

        // Discover and register all MediaMod addons
        try {
            MediaModAddonRegistry.discoverAddons()
            MediaModAddonRegistry.initialiseAddons()
        } catch (t: Throwable) {
            logger.error(t.message)
        }

        // Register event listeners & commands
        registerEventListeners()
    }

    /**
     * Registers all event listeners to [MinecraftForge.EVENT_BUS] via [EventBus.register]
     * Current event listeners: [GuiEventListener]
     */
    private fun registerEventListeners() {
        MinecraftForge.EVENT_BUS.register(GuiEventListener)
    }

    init {
        fixedRateTimer("MediaMod: TrackMetadata", false, 0, 3000) {
            // Query the current service for some track information, if none is provided, return
            try {
                val trackMetadata = MediaModServiceRegistry.currentService?.fetchTrackMetadata() ?: return@fixedRateTimer
                if (trackMetadata != currentTrackMetadata) {
                    currentTrackMetadata = trackMetadata
                }
            } catch (t: Throwable) {
                logger.error("An error occurred when fetching TrackMetadata", t)
            }
        }
    }
}
