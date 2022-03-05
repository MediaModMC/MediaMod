package dev.mediamod.modmenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.mediamod.config.Configuration

class ModMenuImpl : ModMenuApi {
    override fun getModConfigScreenFactory() =
        ConfigScreenFactory { Configuration.gui() }
}