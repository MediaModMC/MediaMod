package dev.mediamod.service

import dev.mediamod.data.Track
import gg.essential.vigilance.Vigilant

abstract class Service {
    abstract val displayName: String
    abstract fun pollTrack(): Track?

    open val hasConfiguration: Boolean = false

    open fun init() {}
    open fun Vigilant.CategoryPropertyBuilder.configuration() {}
}
