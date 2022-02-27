package dev.mediamod.service

import dev.mediamod.data.Track

interface Service {
    fun init()
    fun pollTrack(): Track?
}
