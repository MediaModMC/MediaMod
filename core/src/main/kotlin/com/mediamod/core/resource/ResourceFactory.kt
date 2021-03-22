package com.mediamod.core.resource

import com.mediamod.core.bindings.texture.TextureManager
import com.mediamod.core.bindings.threading.ThreadingService
import com.mediamod.core.util.web.WebUtils.inputStreamWithAgent
import java.io.InputStream
import java.net.URL
import java.util.concurrent.CompletableFuture

object ResourceFactory {
    private val images = mutableMapOf<URL, ResourceImageProvider>()
    private val mediaModResource = MediaModResource("mediamod", "mediamod.png")

    fun resourceForUrl(url: URL?): MediaModResource {
        val provider = images[url ?: return mediaModResource]
            ?: ResourceImageProvider(url.file, CompletableFuture.supplyAsync {
                url.inputStreamWithAgent()
            })

        images[url] = provider
        return provider.resource ?: mediaModResource
    }

    class ResourceImageProvider(name: String, imageStream: CompletableFuture<InputStream>) {
        var resource: MediaModResource? = null

        init {
            imageStream.thenAccept {
                ThreadingService.runBlocking { resource = TextureManager.getBufferedImageLocation(name, it) }
            }
        }
    }
}
