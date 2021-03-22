package com.mediamod.forge.bindings.impl.texture

import com.mediamod.core.bindings.texture.TextureManager
import com.mediamod.core.resource.MediaModResource
import com.mediamod.forge.util.dynamicTexture
import com.mediamod.forge.util.toMediaModResource
import net.minecraft.client.Minecraft
import java.io.InputStream
import javax.imageio.ImageIO

class TextureManagerProvider : TextureManager {
    private val textureManager = Minecraft.getMinecraft().textureManager

    override fun getBufferedImageLocation(name: String, imageStream: InputStream): MediaModResource? {
        return try {
            val image = ImageIO.read(imageStream)
            textureManager.getDynamicTextureLocation(name, image.dynamicTexture).toMediaModResource()
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }
}
