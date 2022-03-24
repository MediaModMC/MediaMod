package dev.mediamod.service.impl.browser

import dev.mediamod.data.Track
import dev.mediamod.service.Service
import dev.mediamod.service.impl.browser.connection.ExtensionConnectionManager
import kotlinx.coroutines.runBlocking
import java.net.URL

class BrowserService : Service() {
    private val extensionManager = ExtensionConnectionManager()
    override val displayName = "Browser"

    override fun init() {
        extensionManager.init()
    }

    override fun pollTrack(): Track? =
        runBlocking {
            val track = extensionManager.requestTrack() ?: return@runBlocking null
            Track(
                track.title,
                track.artist,
                URL(track.albumArt),
                track.timestamps.elapsed,
                track.timestamps.duration,
                track.paused
            )
        }
}