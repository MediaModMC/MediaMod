package com.mediamod.core.bindings.texture

import com.mediamod.core.bindings.BindingRegistry
import com.mediamod.core.resource.MediaModResource
import java.awt.image.BufferedImage
import java.io.InputStream

interface TextureManager {
    fun getBufferedImageLocation(name: String, imageStream: InputStream): MediaModResource?
    fun getBufferedImageForResource(resource: MediaModResource): BufferedImage?

    companion object : TextureManager by BindingRegistry.textureManager
}