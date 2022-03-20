package dev.mediamod.service.impl.browser

import dev.mediamod.data.Track
import dev.mediamod.data.api.browser.ExtensionTrackInfo
import dev.mediamod.service.Service
import dev.mediamod.service.impl.browser.connection.ExtensionConnectionManager
import java.net.URL

class BrowserService : Service() {
    private val extensionManager = ExtensionConnectionManager()
    private var track: ExtensionTrackInfo? = null

    override val displayName = "Browser"

    override fun init() {
        extensionManager.init()
        extensionManager.onTrack { track = it }
    }

    override fun pollTrack() = track?.let {
        Track(it.title, it.artist, URL(it.albumArt), it.timestamps.elapsed, it.timestamps.duration, false)
    }
}