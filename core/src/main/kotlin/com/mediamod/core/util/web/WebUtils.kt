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


package com.mediamod.core.util.web

import java.io.InputStream
import java.net.URL

/**
 * A class for making web requests, used for fetching images and other requests
 */
object WebUtils {
    private val userAgent = "MediaMod/${javaClass.`package`.implementationVersion}"

    /**
     * Gets an [InputStream] from a URL using the MediaMod user agent
     * Some websites require the User-Agent header to be set
     */
    fun get(url: URL): InputStream {
        val connection = url.openConnection()
        connection.setRequestProperty("User-Agent", userAgent)

        return connection.getInputStream()
    }
}