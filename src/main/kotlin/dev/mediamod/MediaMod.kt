package dev.mediamod

import dev.mediamod.manager.*
import dev.mediamod.utils.logger
import java.io.File
import kotlin.concurrent.thread
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction
import kotlin.system.measureTimeMillis

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
    val sessionManager = SessionManager()
    val themeManager = ThemeManager()

    //#if MC<=11202
    //$$ @Mod.EventHandler
    //$$ fun init(event: FMLInitializationEvent) {
    //#else
    fun init() {
        //#endif
        logger.info("MediaMod has started!")

        //#if FABRIC==1
        initializeReflect()
        //#endif

        themeManager.init()
        serviceManager.init()

        //#if MC<=11202
        //$$ EssentialAPI.getCommandRegistry().registerCommand(MediaModCommand())
        //#endif
    }

    private fun initializeReflect() {
        thread(start = true, isDaemon = false, name = "MediaMod kreflect warmup") {
            val time = measureTimeMillis {
                themeManager::onChange.javaMethod
                    ?.kotlinFunction?.parameters
                    ?.filter { it.kind == KParameter.Kind.VALUE }
                    ?: run {
                        logger.warn("Failed to locate method for kreflect warmup!")
                        return@measureTimeMillis
                    }
            }

            logger.info("Took $time ms to warm up kreflect")
        }
    }
}