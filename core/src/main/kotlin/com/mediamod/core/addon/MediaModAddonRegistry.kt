/*
 *     MediaMod is a mod for Minecraft which displays information about your current track in-game
 *     Copyright (C) 2021 Conor Byrne
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.mediamod.core.addon

import com.mediamod.core.MediaModCore
import com.mediamod.core.addon.exception.AddonRegisterException
import com.mediamod.core.addon.exception.AddonRegistryException
import com.mediamod.core.addon.exception.AddonUnregisterException
import com.mediamod.core.addon.json.MediaModAddonJson
import com.mediamod.core.addon.json.MediaModAddonJsonEntry
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager

/**
 * The registry class handling the loading, unloading and management of all instances of a [MediaModAddon]
 *
 * @author Conor Byrne (dreamhopping)
 */
object MediaModAddonRegistry {
    /**
     * A mutable list of all loaded addons, these are instances of [MediaModAddon]
     * This list is modified in [register] and [unregister]
     */
    private val loadedAddons = mutableListOf<MediaModAddon>()

    /**
     * A mutable map of all discovered addons, these addons are not registered yet and their classes have not been loaded by us
     * This map is modified in [discoverAddons]
     *
     * The key for this map is the addon's identifier and the value is the addon's json entry
     */
    private val discoveredAddons = mutableMapOf<String, MediaModAddonJsonEntry>()

    /**
     * A logger for this class, used to print out errors with addon loading
     */
    private val logger = LogManager.getLogger("MediaMod.AddonRegistry")

    /**
     * A kotlinx-serialization json parser, used when parsing the mediamod-addon.json files
     */
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Registers a MediaMod Addon
     *
     * When registering, [MediaModAddon.register] is called
     * The addon instance will be appended to [loadedAddons] if registration is successful
     *
     * @see [registerAddons]
     * @param addonId The addon's identifier parsed from the mediamod-addon.json file
     * @param addonClass The addon's class, the class name was parsed from the mediamod-addon.json file
     */
    private fun register(addonId: String, addonClass: Class<*>) {
        // Create an instance of the addonClass
        val addonInstance = addonClass.newInstance()

        // Verify that the addon class instance is a subclass of MediaModAddon
        if (addonInstance is MediaModAddon) {
            // Register the addon
            if (addonInstance.register()) {
                // The addon has successfully registered, add it to the list
                loadedAddons.add(addonInstance)
            } else {
                // The addon failed to register, an exception may have been printed to stacktrace
                throw AddonRegisterException(addonId, "The addon failed to register!")
            }
        } else {
            // The addon class does not implement MediaModAddon
            throw AddonRegisterException(addonId, "The addon's class is not a subclass of MediaModAddon!")
        }
    }

    /**
     * Unregisters a MediaMod Addon
     *
     * When unregistering, [MediaModAddon.unregister] is called
     * The addon instance will be removed from [loadedAddons] regardless of the return value of [MediaModAddon.unregister]
     */
    private fun unregister(addonId: String) {
        val addon = loadedAddons.firstOrNull { it.identifier == addonId }
            ?: throw AddonUnregisterException(addonId, "An addon with that identifier could not be found")

        // Unregister and remove the addon from the list
        addon.unregister()
        loadedAddons.remove(addon)
        logger.info("Successfully unregistered ${addon.name} (${addon.identifier})!")
    }

    /**
     * Discovers all MediaMod addons on the classpath
     *
     * An addon must contain the mediamod-addon.json file to be recognised by this method
     * To see the format of this file, check out the example addon in this subproject
     *
     * When an addon is discovered, it is added to the [discoveredAddons] map
     */
    fun discoverAddons() {
        // Get all mediamod-addon.json files and parse the json from each resource
        val addonJsonList = this.javaClass.classLoader.getResources("mediamod-addon.json").toList()
        addonJsonList.forEach { file ->
            try {
                val addonJson: MediaModAddonJson = json.decodeFromString(file.readText())

                // Loop through all addons in the json and add them to the discovered list
                addonJson.addons.forEach { addon ->
                    discoveredAddons[addon.key] = addon.value
                }
            } catch (e: SerializationException) {
                // Ignore any exceptions thrown by this class, unless we are in a development environment
                if (MediaModCore.isDevelopment) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Registers all discovered MediaMod Addons
     *
     * [discoveredAddons] must be run before this method, otherwise no addons will be loaded as they have not been found yet
     * If an addon fails to load, a warning is printed to the console and a warning is shown in the MediaMod GUI
     */
    fun registerAddons() {
        logger.info("Registering addons")

        // Loop through all discovered addons and attempt to load their classes
        discoveredAddons.forEach { entry ->
            val addonId = entry.key
            val addonEntry = entry.value

            // Verify that the API version matches
            if (addonEntry.apiVersion != MediaModCore.apiVersion)
                throw AddonRegisterException(
                    addonId,
                    "The addon's API version (${addonEntry.apiVersion}) does not match MediaMod's (${MediaModCore.apiVersion})"
                )

            // Try to get the addon's class from the addonClass field
            val addonClass = try {
                Class.forName(addonEntry.addonClass)
            } catch (t: Throwable) {
                throw AddonRegisterException(addonId, "The addon class could not be found!")
            }

            // Register the addon
            register(addonId, addonClass)
        }

        logger.info("Registered ${loadedAddons.size} addon${if (loadedAddons.size == 1) "" else "s"}!")
    }

    /**
     * Discovers all MediaMod addons on the classpath and registers them
     */
    fun loadAddons() {
        try {
            // Discover all addons
            logger.info("Starting addon discovery")
            discoverAddons()

            // Check if there is any discovered addons before continuing
            if (discoveredAddons.isEmpty())
                logger.warn("No addons were found during discovery")

            // Register all discovered addons
            logger.info("Discovered ${discoveredAddons.size} addon${if (discoveredAddons.size == 1) "" else "s"}!")
            registerAddons()
        } catch (e: AddonRegistryException) {
            logger.warn(e.message)
        }
    }
}
