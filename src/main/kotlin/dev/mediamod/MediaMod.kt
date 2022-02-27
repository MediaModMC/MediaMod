package dev.mediamod

import dev.mediamod.manager.ServiceManager
import dev.mediamod.utils.logger
import net.fabricmc.api.ModInitializer

object MediaMod : ModInitializer {
    val serviceManager = ServiceManager()

    override fun onInitialize() {
        logger.info("MediaMod has started!")

        serviceManager.init()
    }
}