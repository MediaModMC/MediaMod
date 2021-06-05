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

package com.mediamod.core.addon.impl.config

import com.mediamod.core.MediaModCore
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.io.File

/**
 * An example configuration class
 * All configuration classes use Sk1erLLC's Vigilance library
 * @see Vigilant
 */
object TestAddonConfig : Vigilant(File(MediaModCore.addonConfigDirectory, "mediamod-test-addon.toml")) {
    @Property(
        type = PropertyType.SWITCH, name = "Example switch",
        description = "This is a switch for the example addon!",
        category = "General", subcategory = "Subcategory"
    )
    var exampleBoolean = true
}
