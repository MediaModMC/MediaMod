package com.mediamod.fabric.bindings.impl.minecraft

import com.mediamod.core.bindings.minecraft.FontRenderer
import net.minecraft.client.MinecraftClient

class FontRendererProvider : FontRenderer {
    override fun getStringWidth(string: String) =
        MinecraftClient.getInstance().textRenderer.getWidth(string)

    override fun trimStringToWidth(string: String, width: Int): String =
        MinecraftClient.getInstance().textRenderer.trimToWidth(string, width)
}
