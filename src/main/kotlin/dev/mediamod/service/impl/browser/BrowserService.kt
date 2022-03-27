package dev.mediamod.service.impl.browser

import dev.mediamod.data.Track
import dev.mediamod.service.Service
import dev.mediamod.service.impl.browser.connection.ExtensionConnectionManager
import java.net.URL

class BrowserService : Service() {
    private val extensionManager = ExtensionConnectionManager()
    override val displayName = "Browser"

    override fun init() = extensionManager.init()
    override suspend fun pollTrack() = extensionManager.requestTrack()?.let {
        Track(it.title, it.artist, URL(it.albumArt), it.timestamps.elapsed, it.timestamps.duration, it.paused)
    }
}