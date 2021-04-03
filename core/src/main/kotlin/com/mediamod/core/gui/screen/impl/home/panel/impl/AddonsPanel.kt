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

package com.mediamod.core.gui.screen.impl.home.panel.impl

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.CramSiblingConstraint
import club.sk1er.elementa.constraints.FillConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.dsl.*
import com.mediamod.core.addon.MediaModAddonRegistry
import com.mediamod.core.bindings.minecraft.MinecraftClient
import com.mediamod.core.config.MediaModConfigRegistry
import com.mediamod.core.gui.component.UIRoundedButton
import com.mediamod.core.gui.screen.impl.home.panel.MediaModHomeScreenPanel
import java.awt.Color

/**
 * The panel that displays information about the currently installed addons
 * Here is where an addon's [Vigilant] gui can be opened by the user
 *
 * @author Conor Byrne (dreamhopping) & Nora
 */
class AddonsPanel : MediaModHomeScreenPanel("Addons") {
    init {
        UIText("Addons", false)
            .constrain {
                x = 20.pixels()
                y = 20.pixels()
                color = titleColour
                textScale = 2.pixels()
            } childOf this

        val addonsContainer = ScrollComponent("You have no addons installed! Go to the \"Discover\" tab to find some!")
            .constrain {
                x = 20.pixels()
                y = SiblingConstraint(10f)
                width = 100.percent()
                height = FillConstraint() - 35.pixels()
            } childOf this

        MediaModAddonRegistry.initialisedAddons.forEach { addon ->
            val metadata = MediaModAddonRegistry.getAddonMetadata(addon.identifier) ?: return@forEach

            repeat(10) {
                AddonComponent(addon.identifier, metadata.name, metadata.description ?: "An awesome MediaMod Addon!")
                    .constrain {
                        x = CramSiblingConstraint(20f)
                        y = CramSiblingConstraint(10f)
                        height = 25.percent()
                        width = 35.percent()
                    } childOf addonsContainer
            }
        }
    }

    class AddonComponent(
        identifier: String,
        name: String,
        description: String
    ) : UIComponent() {
        private val backgroundColour = Color(64, 64, 64).brighter()
        private val titleColour = Color(198, 198, 198)

        init {
            val backgroundBlock = UIRoundedRectangle(5f)
                .constrain {
                    width = 100.percent()
                    height = 100.percent()
                    color = backgroundColour.toConstraint()
                } childOf this

            val textContainer = UIContainer()
                .constrain {
                    width = 100.percent()
                    height = 65.percent()
                } childOf backgroundBlock

            val buttonContainer = UIContainer()
                .constrain {
                    y = SiblingConstraint(5f)
                    width = 100.percent()
                    height = 35.percent() - 5.pixels()
                } childOf backgroundBlock

            UIText(name, false)
                .constrain {
                    x = 5.pixels()
                    y = 5.pixels()
                    color = titleColour.toConstraint()
                    textScale = 1.25.pixels()
                } childOf textContainer

            UIWrappedText(description, false, trimText = true)
                .constrain {
                    x = 5.pixels()
                    y = SiblingConstraint(5f)
                    width = 100.percent() - 10.pixels()
                    height = FillConstraint() - 5.pixels()
                    color = titleColour.darker().toConstraint()
                } childOf textContainer

            UIRoundedButton(Color(69, 130, 204), "Settings", 50, 15) {
                MinecraftClient.openConfigScreen(MediaModConfigRegistry.getConfig(identifier))
            }.constrain {
                x = 5.pixels()
                width = 45.percent()
                height = 15.pixels()
            } childOf buttonContainer
        }
    }
}
