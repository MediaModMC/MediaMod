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

import com.google.gson.Gson
import com.mediamod.core.MediaModCore
import com.mediamod.core.addon.exception.AddonUnregisterException
import com.mediamod.core.addon.json.MediaModAddonJson
import com.mediamod.core.addon.json.MediaModAddonJsonEntry
import org.apache.logging.log4j.LogManager
import java.io.File
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader
import kotlin.system.measureTimeMillis

/**
 * The registry class handling the loading, unloading and management of all instances of a [MediaModAddon]
 *
 * @author Conor Byrne (dreamhopping)
 */
object MediaModAddonRegistry {
    /**
     * The [Method] instance for the [URLClassLoader.addURL] method
     * Allows us to access it at any time, this is done on initialisation to make addon loading seem faster when done
     */
    private val addUrlMethod: Method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
        .apply { isAccessible = true }

    /**
     * A mutable list of all initialised addons, these are instances of [MediaModAddon]
     */
    private val initialisedAddons = mutableListOf<MediaModAddon>()

    /**
     * A mutable map of all discovered addons, these addons are not registered yet and their classes have not been loaded
     * The key for this map is the addon's identifier and the value is the addon's json entry
     */
    private val discoveredAddons = mutableMapOf<String, MediaModAddonJsonEntry>()

    /**
     * A list of [File]s which are "external" sources which MediaMod will load addons from
     * This has to be a directory, otherwise it will be ignored
     */
    private val externalAddonSources = mutableListOf<File>()

    /**
     * A logger for this class, used to print out errors with addon loading
     */
    private val logger = LogManager.getLogger("MediaMod: Addon Registry")

    /**
     * A gson json parser, used when parsing the mediamod-addon.json files
     */
    private val gson = Gson()

    /**
     * Initialises a MediaMod Addon
     *
     * When initialising, [MediaModAddon.initialise] is called
     * The addon instance will be appended to [initialisedAddons] if registration is successful
     *
     * @param addonId The addon's identifier parsed from the mediamod-addon.json file
     * @param addonClass The addon's class, the class name was parsed from the mediamod-addon.json file
     */
    private fun initialiseAddon(addonId: String, addonClass: String?) {
        // Try to get the addon's class from the addonClass field
        val addonClazz = try {
            this.javaClass.classLoader.loadClass(addonClass)
        } catch (t: Throwable) {
            return logger.error("Failed to register addon $addonId: The addon class could not be found!")
        }

        val addonInstance = addonClazz.newInstance()
        if (addonInstance !is MediaModAddon) {
            return logger.error("Failed to register addon $addonId: The addon's class is not a subclass of MediaModAddon!")
        }

        addonInstance.initialise()
        initialisedAddons.add(addonInstance)
    }

    /**
     * Unloads a MediaMod Addon
     *
     * When unloaded, [MediaModAddon.unload] is called.
     * Once it is finished, the addon instance will be removed from [initialisedAddons]
     */
    private fun unload(addonId: String) {
        val addon = initialisedAddons.firstOrNull { it.identifier == addonId }
            ?: throw AddonUnregisterException(addonId, "An addon with that identifier could not be found")

        // Unregister and remove the addon from the list
        addon.unload()
        initialisedAddons.remove(addon)

        logger.info("Successfully unloaded ${addon.identifier}!")
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
        val time = measureTimeMillis {
            // Iterate over all "external" addon sources, get all jar or zip files and add them to the classloader
            externalAddonSources.forEach { source ->
                source.listFiles()
                    .forEach {
                        if (it.name.endsWith("jar") || it.name.endsWith("zip"))
                            addUrlMethod.invoke(this.javaClass.classLoader, it.toURI().toURL())
                    }
            }

            // Find all mediamod-addon.json files and iterate over them
            this.javaClass.classLoader.getResources("mediamod-addon.json").iterator().forEach { file ->
                try {
                    // Loop through all addons in the json and add them to the discovered list
                    val addonJson = gson.fromJson(file.readText(), MediaModAddonJson::class.java)
                    addonJson.addons.forEach { addon ->
                        discoveredAddons[addon.key] = addon.value
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        logger.info("Discovered ${discoveredAddons.size} addon${if (discoveredAddons.size == 1) "" else "s"} in ${time}ms!")
    }

    /**
     * Registers all discovered MediaMod Addons
     *
     * [discoveredAddons] must be run before this method, otherwise no addons will be loaded as they have not been found yet
     * If an addon fails to load, a warning is printed to the console and a warning is shown in the MediaMod GUI
     */
    fun initialiseAddons() {
        // Loop through all discovered addons and attempt to load their classes
        val time = measureTimeMillis {
            discoveredAddons.forEach { (addonId, addonEntry) ->
                // Verify that this addon identifier hasn't been loaded already
                val existingAddon = initialisedAddons.firstOrNull { it.identifier == addonId }
                if (existingAddon != null)
                    return@forEach logger.error("Failed to register addon $addonId: Another addon already exists with this identifier! (class: ${existingAddon.javaClass.name}) Please contact the developer for more information")

                // Verify that the API version matches
                if (addonEntry.apiVersion != MediaModCore.apiVersion)
                    return@forEach logger.error("Failed to register addon $addonId: The addon's API version (${addonEntry.apiVersion}) does not match MediaMod's (${MediaModCore.apiVersion})! Please contact the developer for more information")

                // Register the addon
                initialiseAddon(addonId, addonEntry.addonClass)
            }
        }

        logger.info("Initialised ${initialisedAddons.size} addon${if (initialisedAddons.size == 1) "" else "s"} in ${time}ms!")
    }

    /**
     * Adds an external addon source to the list ([externalAddonSources])
     * A check is performed to see if the addon source is a directory, if this passes, it will be added
     * Otherwise, a warning will be logged
     */
    fun addAddonSource(source: File) {
        // Verify the source is a directory
        if (!source.isDirectory) return logger.warn("Attempt to add non-directory addon source! (${source})")

        // Add the source to the addon list
        externalAddonSources.add(source)
    }
}
