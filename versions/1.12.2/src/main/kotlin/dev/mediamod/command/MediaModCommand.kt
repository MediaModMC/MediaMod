package dev.mediamod.command

import dev.mediamod.config.Configuration
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.utils.GuiUtil

@Suppress("unused")
class MediaModCommand : Command("mediamod") {
    @DefaultHandler
    fun handle() {
        val gui = Configuration.gui() ?: return
        GuiUtil.open(gui)
    }
}