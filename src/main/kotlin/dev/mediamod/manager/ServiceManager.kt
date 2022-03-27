package dev.mediamod.manager

import dev.mediamod.config.Configuration
import dev.mediamod.data.Track
import dev.mediamod.service.Service
import dev.mediamod.service.impl.browser.BrowserService
import dev.mediamod.service.impl.spotify.SpotifyService
import dev.mediamod.utils.firstNotNullOrNull
import dev.mediamod.utils.pmap
import gg.essential.elementa.state.BasicState
import kotlinx.coroutines.*
import kotlin.concurrent.fixedRateTimer

class ServiceManager {
    private val scope = CoroutineScope(Dispatchers.IO) + Job()
    val currentTrack = BasicState<Track?>(null)
    val services = mutableListOf<Service>()

    fun init() {
        addService(SpotifyService())
        addService(BrowserService())

        fixedRateTimer("MediaMod Track Polling", true, period = 3000L) {
            scope.launch {
                services
                    .sortedByDescending { it.displayName == Configuration.preferredService }
                    .pmap {
                        it.pollTrack()
                    }
                    .firstNotNullOrNull()
                    .let { currentTrack.set(it) }
            }
        }
    }

    private fun addService(service: Service) {
        services.add(service)
        service.init()
    }
}