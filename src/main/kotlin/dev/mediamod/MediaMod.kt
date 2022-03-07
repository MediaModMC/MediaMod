package dev.mediamod

import dev.mediamod.manager.APIManager
import dev.mediamod.manager.RenderManager
import dev.mediamod.manager.ServiceManager
import dev.mediamod.manager.ThemeManager
import dev.mediamod.utils.logger
import java.io.File

//#if FABRIC==0
//$$ import net.minecraftforge.fml.common.Mod
//#endif
//#if MC<=11202
//$$ import net.minecraftforge.fml.common.event.FMLInitializationEvent
//$$ import gg.essential.api.EssentialAPI
//$$ import dev.mediamod.command.MediaModCommand
//#endif

//#if FABRIC==0 && MC<=11202
//$$ @Mod(modid = "mediamod", modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter")
//#endif
object MediaMod {
    val dataDirectory = File("./mediamod")

    val apiManager = APIManager()
    val serviceManager = ServiceManager()
    val renderManager = RenderManager()
    val themeManager = ThemeManager()

    //#if MC<=11202
    //$$ @Mod.EventHandler
    //$$ fun init(event: FMLInitializationEvent) {
    //#else
    fun init() {
    //#endif
        logger.info("MediaMod has started!")

        themeManager.init()
        serviceManager.init()

        //#if MC<=11202
        //$$ EssentialAPI.getCommandRegistry().registerCommand(MediaModCommand())
        //#endif
    }
}