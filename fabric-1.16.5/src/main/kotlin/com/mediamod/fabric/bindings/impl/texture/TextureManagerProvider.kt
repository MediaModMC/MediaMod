package com.mediamod.fabric.bindings.impl.texture

import com.mediamod.core.bindings.texture.TextureManager
import com.mediamod.core.resource.MediaModResource
import com.mediamod.fabric.util.toDynamicTexture
import com.mediamod.fabric.util.toMediaModResource
import net.minecraft.client.MinecraftClient
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO

class TextureManagerProvider : TextureManager {
    private val cachedImages = mutableMapOf<MediaModResource, BufferedImage>()

    override fun getBufferedImageLocation(name: String, imageStream: InputStream): MediaModResource {
        val resource =
            MinecraftClient.getInstance().textureManager.registerDynamicTexture(name, imageStream.toDynamicTexture())
                .toMediaModResource()

        cachedImages[resource] = ImageIO.read(imageStream)
        return resource
    }

    override fun getBufferedImageForResource(resource: MediaModResource) = cachedImages[resource]
}
