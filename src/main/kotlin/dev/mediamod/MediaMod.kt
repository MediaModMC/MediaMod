package dev.mediamod

import dev.mediamod.manager.RenderManager
import dev.mediamod.manager.ServiceManager
import dev.mediamod.utils.logger
import net.fabricmc.api.ModInitializer

object MediaMod : ModInitializer {
    val serviceManager = ServiceManager()
    val renderManager = RenderManager()

    override fun onInitialize() {
        logger.info("MediaMod has started!")

        renderManager.init()
        serviceManager.init()
    }
}