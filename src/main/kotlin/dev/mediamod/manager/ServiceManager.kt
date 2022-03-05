package dev.mediamod.manager

import dev.mediamod.data.Track
import dev.mediamod.service.Service
import dev.mediamod.service.impl.TestService
import net.minecraft.client.MinecraftClient
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread

class ServiceManager {
    private val listeners = mutableSetOf<Track.() -> Unit>()
    val services = mutableSetOf<Service>()

    fun init() {
        addService(TestService())

        thread(true, name = "MediaMod Track Polling") {
            fixedRateTimer("MediaMod Track Polling", true, period = 3000L) {
                services
                    .firstNotNullOfOrNull { it.pollTrack() }
                    ?.let {
                        emit(it)
                    }
            }
        }
    }

    fun onTrack(callback: Track.() -> Unit) =
        listeners.add(callback)

    private fun emit(track: Track) =
        listeners.forEach {
            MinecraftClient.getInstance().execute { it(track) }
        }

    private fun addService(service: Service) {
        services.add(service)
        service.init()
    }
}