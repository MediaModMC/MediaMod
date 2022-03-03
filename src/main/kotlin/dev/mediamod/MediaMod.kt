package dev.mediamod

import dev.mediamod.manager.RenderManager
import dev.mediamod.manager.ServiceManager
import dev.mediamod.manager.ThemeManager
import dev.mediamod.utils.logger
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import java.io.File

object MediaMod : ModInitializer {
    val dataDirectory = File(MinecraftClient.getInstance().runDirectory, "mediamod")

    val serviceManager = ServiceManager()
    val renderManager = RenderManager()
    val themeManager = ThemeManager()

    override fun onInitialize() {
        logger.info("MediaMod has started!")

        renderManager.init()
        serviceManager.init()
        themeManager.init()
    }
}