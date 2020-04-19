package dev.cbyrne.mediamod.commands

import dev.cbyrne.mediamod.MediaMod
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

object MediaModCommand: CommandBase() {
    override fun getCommandName() = "mediamod"
    override fun getCommandUsage(sender: ICommandSender?) = "/mediamod"
    override fun canCommandSenderUseCommand(sender: ICommandSender?) = true
    override fun getRequiredPermissionLevel() = -1
    override fun getCommandAliases() = mutableListOf("media", "mm")
    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) = MediaMod.logger.info("Command Run")
}