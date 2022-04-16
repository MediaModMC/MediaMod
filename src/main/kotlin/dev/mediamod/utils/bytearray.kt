package dev.mediamod.utils

internal fun ByteArray.hex() = joinToString("") { "%02x".format(it) }
