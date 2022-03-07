package dev.mediamod.modmenu

//#if FABRIC==1
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.mediamod.config.Configuration

class ModMenuImpl : ModMenuApi {
    override fun getModConfigScreenFactory() =
        ConfigScreenFactory { Configuration.gui() }
}
//#endif