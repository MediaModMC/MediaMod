package dev.cbyrne.mediamod

import dev.cbyrne.mediamod.commands.MediaModCommand
import dev.cbyrne.mediamod.keybinds.KeybindManager
import dev.cbyrne.mediamod.services.ServiceManager
import dev.cbyrne.mediamod.services.impl.SpotifyService
import dev.cbyrne.mediamod.utils.PlayerMessager
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
        MinecraftForge.EVENT_BUS.register(PlayerMessager)

        // Register the MediaMod command
        ClientCommandHandler.instance.registerCommand(MediaModCommand)

        // Create data directory
        dataDirectory.takeIf { it.mkdir() }?.let {
            logger.info("Created data directory")
        } ?: logger.error("Unable to create data directory, does it already exist?")

        // todo: load config

        ServiceManager.services.add(SpotifyService())
        ServiceManager.initializeServices()

        logger.info("MediaMod initialized!")
    }
}