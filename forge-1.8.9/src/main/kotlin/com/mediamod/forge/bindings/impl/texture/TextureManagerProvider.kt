package com.mediamod.forge.bindings.impl.texture

import com.mediamod.core.bindings.texture.TextureManager
import com.mediamod.core.resource.MediaModResource
import com.mediamod.forge.util.dynamicTexture
import com.mediamod.forge.util.toMediaModResource
import net.minecraft.client.Minecraft
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO

class TextureManagerProvider : TextureManager {
    private val textureManager = Minecraft.getMinecraft().textureManager
    private val cachedImages = mutableMapOf<MediaModResource, BufferedImage>()

    override fun getBufferedImageLocation(name: String, imageStream: InputStream): MediaModResource? {
        return try {
            val image = ImageIO.read(imageStream)
            val resource = textureManager.getDynamicTextureLocation(name, image.dynamicTexture).toMediaModResource()

            cachedImages[resource] = image
            resource
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }

    override fun getBufferedImageForResource(resource: MediaModResource) = cachedImages[resource]
}
