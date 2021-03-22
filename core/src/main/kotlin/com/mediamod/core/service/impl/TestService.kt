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

package com.mediamod.core.service.impl

import com.mediamod.core.service.MediaModService
import com.mediamod.core.track.TrackMetadata
import org.apache.logging.log4j.LogManager

/**
 * A test service for MediaMod
 * @author Conor Byrne (dreamhopping)
 */
class TestService : MediaModService("mediamod-test-addon-service") {
    private val logger = LogManager.getLogger("TestService")

    /**
     * Called when your service is being registered
     * You should do any once-off operations in here like configuration file reading, etc.
     * Once this method is complete, your service needs to be ready to use
     */
    override fun initialise() {
        logger.info("My service (${identifier}) has been initialised!")
    }

    /**
     * Called when MediaMod wants to get a [TrackMetadata] instance from you
     * If you do not have one, return null
     */
    override fun fetchTrackMetadata() = TrackMetadata(
        "i wanna slam my head against the wall",
        "glaive",
        1,
        35000,
        "https://i.scdn.co/image/ab67616d0000b273c1c4b6702c901e6efcbe7490"
    )

    /**
     * Called when MediaMod is querying your service to check if it is ready to provide track information
     * You should NOT do any network operations on this call
     *
     * @return true if you are ready to return [TrackMetadata], otherwise false
     */
    override fun hasTrackMetadata() = true
}
