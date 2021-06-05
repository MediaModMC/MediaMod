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

package com.mediamod.core.service

import com.mediamod.core.track.TrackMetadata

/**
 * The class which all MediaMod Services should extend
 * @see MediaModServiceRegistry
 * @author Conor Byrne
 */
abstract class MediaModService(val identifier: String) {
    /**
     * Called when your service is being registered
     * You should do any once-off operations in here like configuration file reading, etc.
     * Once this method is complete, your service needs to be ready to use
     */
    abstract fun initialise()

    /**
     * Called when MediaMod wants to get a [TrackMetadata] instance from you
     * This is called every 3 seconds to avoid rate limits if you are using an API
     * You can do network operations on this method
     *
     * @return null if there is no TrackMetadata available
     */
    abstract fun fetchTrackMetadata(): TrackMetadata?

    /**
     * Called when MediaMod is querying your service to check if it is ready to provide track information
     * You should NOT do any network operations on this call
     *
     * @return true if you are ready to return [TrackMetadata], otherwise false
     */
    abstract fun hasTrackMetadata(): Boolean
}
