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
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.system.measureTimeMillis

/**
 * The mod class for MediaMod
 *
 * @author Conor Byrne (dreamhopping)
 */
@Mod(modid = "mediamod", version = MediaModCore.version)
class MediaMod {
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

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        logger.info("Loading MediaMod v${MediaModCore.version}!")

        // Create the "./mediamod/addons" Directory if it doesn't exist already
        if (!mediamodAddonDirectory.exists())
            mediamodAddonDirectory.mkdirs()

        // Add ${dir}/mediamod/addons as an addon source
        MediaModAddonRegistry.addAddonSource(mediamodAddonDirectory)

        // Discover and register all MediaMod addons
        val timeToLoadAddons = measureTimeMillis(MediaModAddonRegistry::loadAddons)
        logger.info("Took ${timeToLoadAddons}ms to load addons")
    }
}
