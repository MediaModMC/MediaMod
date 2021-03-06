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


package com.mediamod.forge

import com.mediamod.core.MediaModCore
import com.mediamod.core.bindings.BindingRegistry
import com.mediamod.forge.bindings.impl.command.MediaModCommandRegistryProvider
import com.mediamod.forge.bindings.impl.desktop.DesktopProvider
import com.mediamod.forge.bindings.impl.minecraft.FontRendererProvider
import com.mediamod.forge.bindings.impl.minecraft.MinecraftClientProvider
import com.mediamod.forge.bindings.impl.render.RenderUtilProvider
import com.mediamod.forge.bindings.impl.texture.TextureManagerProvider
import com.mediamod.forge.bindings.impl.threading.ThreadingServiceProvider
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * The mod class for MediaMod
 * @author Conor Byrne
 */
@Mod(modid = "mediamod", version = MediaModCore.version)
class MediaMod {
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        registerBindings()

        MinecraftForge.EVENT_BUS.register(this)
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

    @SubscribeEvent
    fun onRender(event: RenderGameOverlayEvent) {
        if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR) MediaModCore.onRender(event.partialTicks)
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) MediaModCore.onClientTick()
    }
}
