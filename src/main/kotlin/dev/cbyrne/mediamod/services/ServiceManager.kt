package dev.cbyrne.mediamod.services

import dev.cbyrne.mediamod.MediaMod

object ServiceManager {
    val services: MutableList<IServiceHandler> = mutableListOf()

    fun initializeServices() {
        MediaMod.logger.info("Initializing Services...")

        services.takeIf { it.isNotEmpty() }?.let { it ->
            it.forEach {
                MediaMod.logger.info("Initializing ${it.handlerName}...")
                it.initialize()
                MediaMod.logger.info("Initialized ${it.handlerName}!")
            }
        } ?: MediaMod.logger.warn("No services found!")
    }
}