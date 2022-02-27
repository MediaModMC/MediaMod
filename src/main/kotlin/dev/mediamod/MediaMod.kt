package dev.mediamod

import dev.mediamod.utils.logger
import net.fabricmc.api.ModInitializer

@Suppress("unused")
class MediaMod : ModInitializer {
    override fun onInitialize() {
        logger.info("MediaMod has started!")
    }
}