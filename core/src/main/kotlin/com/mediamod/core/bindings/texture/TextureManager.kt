package com.mediamod.core.bindings.texture

import com.mediamod.core.bindings.BindingRegistry
import com.mediamod.core.resource.MediaModResource
import java.io.InputStream

interface TextureManager {
    fun getBufferedImageLocation(name: String, imageStream: InputStream): MediaModResource?

    companion object : TextureManager by BindingRegistry.textureManager
}