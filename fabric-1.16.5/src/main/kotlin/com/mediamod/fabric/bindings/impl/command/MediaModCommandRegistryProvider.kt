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

package com.mediamod.fabric.bindings.impl.command

import com.mediamod.core.bindings.command.MediaModCommandRegistry
import com.mediamod.core.command.ICommand
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource

/**
 * Provides an implementation for [MediaModCommandRegistry]
 * This allows the core submodule to make commands without touching Minecraft classes directly
 *
 * @author Conor Byrne
 */
class MediaModCommandRegistryProvider : MediaModCommandRegistry {
    /**
     * Registers an [ICommand] to the Fabric [ClientCommandManager]
     * A wrapper instance is created to redirect any calls the specified [ICommand]
     *
     * @param command the command to be registered
     */
    override fun registerCommand(command: ICommand) {
        ClientCommandManager.DISPATCHER.register(
            LiteralArgumentBuilder.literal<FabricClientCommandSource>(command.commandName).executes {
                command.execute(it.input.replace("/${command.commandUsage} ", "").split(" "))
                0
            }
        )
    }
}
