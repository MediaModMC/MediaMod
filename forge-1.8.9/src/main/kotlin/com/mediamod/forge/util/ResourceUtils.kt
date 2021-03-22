package com.mediamod.forge.util

import com.mediamod.core.resource.MediaModResource
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage

fun ResourceLocation.toMediaModResource() = MediaModResource(this.resourceDomain, this.resourcePath)
fun MediaModResource.toResourceLocation() = ResourceLocation(this.namespace, this.path)

val BufferedImage.dynamicTexture
    get() = DynamicTexture(this)