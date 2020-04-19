package dev.cbyrne.mediamod.services

import dev.cbyrne.mediamod.services.media.Track

interface IServiceHandler {
    val handlerName: String
    var handlerReady: Boolean
    var estimatedProgress: Int
    var currentTrack: Track

    fun initialize()
}