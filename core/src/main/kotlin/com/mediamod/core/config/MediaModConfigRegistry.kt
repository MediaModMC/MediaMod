package com.mediamod.core.config

import club.sk1er.vigilance.Vigilant

/**
 * Handles the saving and loading of a MediaMod configuration via the Vigilance library
 * A configuration is provided by the [Vigilant] class
 */
object MediaModConfigRegistry {
    private val registeredConfigurations = mutableMapOf<String, Vigilant>()

    fun registerConfig(addonIdentifier: String, vigilantInstance: Vigilant) {
        vigilantInstance.initialize()
        registeredConfigurations[addonIdentifier] = vigilantInstance
    }

    fun getConfig(addonIdentifier: String) = registeredConfigurations[addonIdentifier]
}
