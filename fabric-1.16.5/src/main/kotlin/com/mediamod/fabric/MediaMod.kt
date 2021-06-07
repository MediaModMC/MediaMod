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

package com.mediamod.fabric

import com.mediamod.core.MediaModCore
import com.mediamod.core.bindings.BindingRegistry
import com.mediamod.fabric.bindings.impl.command.MediaModCommandRegistryProvider
import com.mediamod.fabric.bindings.impl.desktop.DesktopProvider
import com.mediamod.fabric.bindings.impl.minecraft.FontRendererProvider
import com.mediamod.fabric.bindings.impl.minecraft.MinecraftClientProvider
import com.mediamod.fabric.bindings.impl.render.RenderUtilProvider
import com.mediamod.fabric.bindings.impl.texture.TextureManagerProvider
import com.mediamod.fabric.bindings.impl.threading.ThreadingServiceProvider
import com.mediamod.fabric.event.ClientTickEvent
import com.mediamod.fabric.event.RenderTickEvent
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameOverlayRenderer
import net.minecraft.client.gui.screen.ChatScreen

class MediaMod : ModInitializer {
    override fun onInitialize() {
        registerBindings()
        registerEventListeners()

        MediaModCore.initialize()
    }

    /**
     * Sets the instance for all bindings provided by this entry point
     */
    private fun registerBindings() {
        BindingRegistry.threadingService = ThreadingServiceProvider()
        BindingRegistry.renderUtil = RenderUtilProvider()
        BindingRegistry.minecraftClient = MinecraftClientProvider()
        BindingRegistry.fontRenderer = FontRendererProvider()
        BindingRegistry.textureManager = TextureManagerProvider()
        BindingRegistry.commandRegistry = MediaModCommandRegistryProvider()
        BindingRegistry.desktop = DesktopProvider()
    }

    /**
     * Registers event listeners for client tick and render tick
     */
    private fun registerEventListeners() {
        ClientTickEvent.event.register(MediaModCore::onClientTick)

        RenderTickEvent.event.register {
            val screen = MinecraftClient.getInstance().currentScreen
            if (screen is InGameOverlayRenderer || screen is ChatScreen || (screen == null && MinecraftClient.getInstance().world != null))
                MediaModCore.onRender(it)
        }
    }
}
