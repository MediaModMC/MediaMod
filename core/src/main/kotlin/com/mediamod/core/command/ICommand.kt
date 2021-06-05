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

package com.mediamod.core.command

/**
 * A class that all commands should implement
 * This will allow you to register commands without having a version specific implementation for it
 *
 * @author Conor Byrne
 */
interface ICommand {
    /**
     * The name of your command, this will be what the player types prefixed with "/" in chat to invoke it
     */
    val commandName: String

    /**
     * If your command execution fails, this message will be printed along with "Invalid syntax: "
     * This tells the user how the command should be used, for example: "/mycommand <gui/refresh>"
     */
    val commandUsage: String

    /**
     * Called when a player has executed your command
     *
     * @param args The list of arguments the player has supplied, this can be empty
     * @return if your command was successful or not, if false, [commandUsage] will be shown to the user
     */
    fun execute(args: List<String>)
}
