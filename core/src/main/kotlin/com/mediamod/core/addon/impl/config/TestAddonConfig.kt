package com.mediamod.core.addon.impl.config

import club.sk1er.vigilance.Vigilant
import club.sk1er.vigilance.data.Property
import club.sk1er.vigilance.data.PropertyType
import com.mediamod.core.MediaModCore
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
