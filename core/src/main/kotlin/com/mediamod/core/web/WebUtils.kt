package com.mediamod.core.web

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