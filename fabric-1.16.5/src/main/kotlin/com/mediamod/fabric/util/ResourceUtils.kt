package com.mediamod.fabric.util

import com.mediamod.core.resource.MediaModResource
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import java.io.InputStream

fun Identifier.toMediaModResource() = MediaModResource(this.namespace, this.path)
fun MediaModResource.toIdentifier() = Identifier(this.namespace, this.path)

fun InputStream.toDynamicTexture() = NativeImageBackedTexture(NativeImage.read(this))
