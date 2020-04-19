package dev.cbyrne.mediamod

import dev.cbyrne.mediamod.commands.MediaModCommand
import dev.cbyrne.mediamod.keybinds.KeybindManager
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(modid = MediaMod.modid, modLanguageAdapter = "dev.cbyrne.mediamod.language.KotlinLanguageAdapter")
object MediaMod {
    @Suppress("MemberVisibilityCanBePrivate")
    const val modid = "mediamod"
    val logger: Logger = LogManager.getLogger("MediaMod")

    @Mod.EventHandler
    fun onPreInit(e: FMLPreInitializationEvent) {
        // Register our keybinds
        KeybindManager.register()
    }

    @Mod.EventHandler
    fun onInit(e: FMLInitializationEvent) {
        logger.info("MediaMod initialising...")

        // Subscribe to events
        MinecraftForge.EVENT_BUS.register(this)

        // Register the MediaMod command
        ClientCommandHandler.instance.registerCommand(MediaModCommand())

        // Check if our data directory exists
        dataDirectory.takeIf { !it.exists() }?.let {
            logger.info("Creating data directory")
            dataDirectory.takeIf { it.mkdir() }?.let {
                logger.info("Created data directory")
            } ?: logger.error("Failed to create data directory")
        } ?: logger.info("Data directory already exists!")

        // todo: load config
        // todo: load service handlers
        // todo: init service handlers
        logger.info("MediaMod initialized!")
    }
}