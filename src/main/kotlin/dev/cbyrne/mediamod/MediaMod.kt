package dev.cbyrne.mediamod

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(modid = MediaMod.modid, modLanguageAdapter = "dev.cbyrne.mediamod.language.KotlinLanguageAdapter")
object MediaMod {
    @Suppress("MemberVisibilityCanBePrivate") // it actually can't
    const val modid = "mediamod"

    @Mod.EventHandler
    fun onInit(e: FMLInitializationEvent) {
        println("<:BRUHSOUNDEFFECT2:684665400856018944>")
    }
}