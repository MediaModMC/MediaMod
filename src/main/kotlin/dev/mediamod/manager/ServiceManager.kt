package dev.mediamod.manager

import dev.mediamod.data.Track
import dev.mediamod.service.Service
import dev.mediamod.service.impl.spotify.SpotifyService
import gg.essential.elementa.state.BasicState
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread

class ServiceManager {
    val currentTrack = BasicState<Track?>(null)
    val services = mutableSetOf<Service>()

    fun init() {
        addService(SpotifyService())

        thread(true, name = "MediaMod Track Polling") {
            fixedRateTimer("MediaMod Track Polling", true, period = 3000L) {
                services.firstNotNullOfOrNull {
                    runCatching { it.pollTrack() }.getOrNull()
                }?.let { currentTrack.set(it) }
            }
        }
    }

    private fun addService(service: Service) {
        services.add(service)
        service.init()
    }
}