package dev.mediamod.data

import java.net.URL

data class Track(
    val name: String,
    val artist: String,
    val artwork: URL,
    val elapsed: Long,
    val duration: Long,
    val paused: Boolean
)