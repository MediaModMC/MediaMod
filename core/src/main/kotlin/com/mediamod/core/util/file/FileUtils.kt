package com.mediamod.core.util.file

import java.io.File

fun File.createIfNonExisting(isDir: Boolean) {
    if (!this.exists())
        if (isDir) this.mkdirs() else this.createNewFile()
}