package com.mediamod.fabric.bindings.impl.minecraft

import net.minecraft.client.MinecraftClient as MinecraftClientImpl
import com.mediamod.core.bindings.minecraft.MinecraftClient
import java.io.File

class MinecraftClientProvider : MinecraftClient {
    override val mcDataDir: File = MinecraftClientImpl.getInstance().runDirectory
}
