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

package com.mediamod.core.service

/**
 * The class which handles the registration and management of MediaMod Services
 * A service must extend the [MediaModService] class
 *
 * @author Conor Byrne (dreamhopping)
 */
object MediaModServiceRegistry {
    /**
     * The key is the addon's identifier and the value is a list of services that this addon has registered
     * @see registerService
     */
    private val services = mutableMapOf<String, MutableList<MediaModService>>()

    /**
     * Registers a service into the [MediaModServiceRegistry]
     *
     * @param addonId The identifier of the addon that owns this service
     * @param service The service that should be registered
     */
    fun registerService(addonId: String, service: MediaModService) {
        service.initialise()

        val existingServices = services[addonId] ?: mutableListOf()
        existingServices.add(service)

        services[addonId] = existingServices
    }
}