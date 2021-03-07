package com.mediamod.ui

import com.mediamod.core.web.WebUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO

/**
 * Fetches a [BufferedImage] and converts it into a [DynamicTexture] for rendering
 */
object ImageUtils {
    private val images = mutableMapOf<String, ResourceLocation?>()

    /**
     * Retrieves an image from a [URL], parses it to a [BufferedImage], then saves it as a [ResourceLocation]
     *
     * @param url The url to fetch an image from
     * @return a [ResourceLocation] if successful, otherwise null
     */
    fun getResourceForURL(url: URL?): ResourceLocation? {
        return try {
            if (url == null) return null

            val existingLocation = images["$url"]
            if (existingLocation != null) return existingLocation

            val bufferedImage = WebUtils.get(url).use { ImageIO.read(it) }
            val resourceLocation =
                Minecraft.getMinecraft().textureManager.getDynamicTextureLocation("$url", DynamicTexture(bufferedImage))

            images["$url"] = resourceLocation
            return resourceLocation
        } catch (t: Throwable) {
            t.printStackTrace()

            images["$url"] = null
            null
        }
    }

    /**
     * Parses the string to a URL, then passes it to [getResourceForURL]
     *
     * @param url The string of a url to fetch an image from
     * @return a [ResourceLocation] if successful, otherwise null
     */
    fun getResourceForURL(url: String?) =
        try {
            getResourceForURL(URL(url))
        } catch (t: Throwable) {
            if (url != null)
                images[url] = null

            null
        }
}
