package com.mediamod.fabric.bindings.impl.minecraft

import net.minecraft.client.MinecraftClient as MinecraftClientImpl
import com.mediamod.core.bindings.minecraft.MinecraftClient
import com.mediamod.core.bindings.screen.IWindowScreen
import net.minecraft.client.gui.screen.Screen
import java.io.File

class MinecraftClientProvider : MinecraftClient {
    override val mcDataDir: File = MinecraftClientImpl.getInstance().runDirectory

    override fun openScreen(screen: IWindowScreen) =
        MinecraftClientImpl.getInstance().openScreen(screen as Screen)
}
