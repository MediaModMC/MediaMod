package com.mediamod.fabric.bindings.impl.texture

import com.mediamod.core.bindings.texture.TextureManager
import com.mediamod.fabric.util.toDynamicTexture
import com.mediamod.fabric.util.toMediaModResource
import net.minecraft.client.MinecraftClient
import java.io.InputStream

class TextureManagerProvider : TextureManager {
    override fun getBufferedImageLocation(name: String, imageStream: InputStream) =
        MinecraftClient.getInstance().textureManager.registerDynamicTexture(name, imageStream.toDynamicTexture())
            .toMediaModResource()
}