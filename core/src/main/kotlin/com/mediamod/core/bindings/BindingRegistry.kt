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

package com.mediamod.core.bindings

import com.mediamod.core.bindings.command.MediaModCommandRegistry
import com.mediamod.core.bindings.desktop.Desktop
import com.mediamod.core.bindings.minecraft.FontRenderer
import com.mediamod.core.bindings.minecraft.MinecraftClient
import com.mediamod.core.bindings.render.RenderUtil
import com.mediamod.core.bindings.texture.TextureManager
import com.mediamod.core.bindings.threading.ThreadingService

object BindingRegistry {
    lateinit var threadingService: ThreadingService

    lateinit var minecraftClient: MinecraftClient
    lateinit var fontRenderer: FontRenderer

    lateinit var textureManager: TextureManager
    lateinit var renderUtil: RenderUtil

    lateinit var commandRegistry: MediaModCommandRegistry
    lateinit var desktop: Desktop
}