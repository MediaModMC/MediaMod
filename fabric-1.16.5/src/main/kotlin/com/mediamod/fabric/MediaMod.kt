package com.mediamod.fabric

import com.mediamod.core.MediaModCore
import com.mediamod.core.bindings.BindingRegistry
import com.mediamod.fabric.bindings.impl.minecraft.FontRendererProvider
import com.mediamod.fabric.bindings.impl.minecraft.MinecraftClientProvider
import com.mediamod.fabric.bindings.impl.render.RenderUtilProvider
import com.mediamod.fabric.bindings.impl.schedule.TickSchedulerServiceProvider
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
        BindingRegistry.tickSchedulerService = TickSchedulerServiceProvider()
        BindingRegistry.renderUtil = RenderUtilProvider()

        BindingRegistry.minecraftClient = MinecraftClientProvider()
        BindingRegistry.fontRenderer = FontRendererProvider()
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
