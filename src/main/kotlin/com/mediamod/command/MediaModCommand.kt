package com.mediamod.command

import com.mediamod.gui.MediaModHomeGui
import com.mediamod.util.TickScheduler
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

/**
 * The command for MediaMod to open the GUI
 */
class MediaModCommand : CommandBase() {
    override fun getCommandName() = "mediamod"
    override fun getCommandUsage(sender: ICommandSender?) = "/mediamod"
    override fun getRequiredPermissionLevel() = 0

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        TickScheduler.schedule(1) {
            Minecraft.getMinecraft().displayGuiScreen(
                MediaModHomeGui()
            )
        }
    }
}
