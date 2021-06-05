/*
 *     MediaMod is a mod for Minecraft which displays information about your current track in-game
 *     Copyright (C) 2021 Conor Byrne
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.mediamod.forge.bindings.impl.command

import com.mediamod.core.bindings.command.MediaModCommandRegistry
import com.mediamod.core.command.ICommand
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraftforge.client.ClientCommandHandler

/**
 * Provides an implementation for [MediaModCommandRegistry]
 * This allows the core submodule to make commands without touching Minecraft classes directly
 *
 * @author Conor Byrne
 */
class MediaModCommandRegistryProvider : MediaModCommandRegistry {
    /**
     * Registers an [ICommand] to the Forge [ClientCommandHandler]
     * A wrapper instance is created to redirect any [CommandBase] calls the specified [ICommand]
     *
     * @param command the command to be registered
     */
    override fun registerCommand(command: ICommand) {
        ClientCommandHandler.instance.registerCommand(object : CommandBase() {
            override fun getCommandName() = command.commandName
            override fun getCommandUsage(sender: ICommandSender) = command.commandUsage
            override fun getCommandAliases() = listOf(command.commandUsage)
            override fun canCommandSenderUseCommand(sender: ICommandSender) = true
            override fun getRequiredPermissionLevel() = -1

            override fun processCommand(sender: ICommandSender, args: Array<out String>) =
                command.execute(args.toList())
        })
    }
}
