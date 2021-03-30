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
import com.mediamod.core.addon.impl.config.TestAddonConfig
import com.mediamod.core.config.MediaModConfigRegistry
import com.mediamod.core.service.MediaModServiceRegistry
import com.mediamod.core.service.impl.TestService
import org.apache.logging.log4j.LogManager

/**
 * A test addon for MediaMod which registers a service ([TestService])
 * @author Conor Byrne (dreamhopping)
 */
class TestAddon : MediaModAddon("mediamod-test-addon") {
    private val logger = LogManager.getLogger("TestAddon")

    /**
     * Called when MediaMod is initialising your addon
     * The addon should be ready for usage when this method is complete
     */
    override fun initialise() {
        logger.info("Registering my service")
        MediaModServiceRegistry.registerService(identifier, TestService())

        logger.info("Registering my config")
        MediaModConfigRegistry.registerConfig(identifier, TestAddonConfig)
    }

    /**
     * Called when MediaMod is unloading your addon
     * The addon should do any configuration saving, etc. in this method
     */
    override fun unload() {
        logger.info("Unloaded")
    }
}
