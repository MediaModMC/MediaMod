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


package com.mediamod

import com.mediamod.bindings.minecraft.MinecraftClientProvider
import com.mediamod.bindings.render.RenderUtilProvider
import com.mediamod.bindings.threading.MultithreadingUtilProvider
import com.mediamod.bindings.threading.TickSchedulerUtilProvider
import com.mediamod.core.MediaModCore
import com.mediamod.core.bindings.minecraft.IMinecraftClient
import com.mediamod.core.bindings.render.IRenderUtil
import com.mediamod.core.bindings.threading.IMultithreadingUtil
import com.mediamod.core.bindings.threading.ITickSchedulerUtil
import com.mediamod.listener.GuiEventListener
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.EventBus

/**
 * The mod class for MediaMod
 * @author Conor Byrne (dreamhopping)
 */
@Mod(
    modid = "mediamod",
    version = MediaModCore.version,
    modLanguageAdapter = "com.mediamod.launch.KotlinLanguageAdapter"
)
object MediaMod {
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        registerBindings()
        registerEventListeners()

        MediaModCore.initialize()
    }

    /**
     * Sets the instance for all bindings provided by this entry point
     */
    private fun registerBindings() {
        IMultithreadingUtil.instance = MultithreadingUtilProvider()
        ITickSchedulerUtil.instance = TickSchedulerUtilProvider()
        IRenderUtil.instance = RenderUtilProvider()
        IMinecraftClient.instance = MinecraftClientProvider()
    }

    /**
     * Registers all event listeners to [MinecraftForge.EVENT_BUS] via [EventBus.register]
     * Current event listeners: [GuiEventListener]
     */
    private fun registerEventListeners() {
        MinecraftForge.EVENT_BUS.register(GuiEventListener)
    }
}
