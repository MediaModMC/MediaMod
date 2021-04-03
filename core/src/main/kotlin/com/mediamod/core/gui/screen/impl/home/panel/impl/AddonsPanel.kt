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
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIRoundedRectangle
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.components.UIWrappedText
import club.sk1er.elementa.components.inspector.Inspector
import club.sk1er.elementa.constraints.CramSiblingConstraint
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

        val addonsContainer = UIContainer()
            .constrain {
                x = 20.pixels()
                y = SiblingConstraint(10f)
                width = 100.percent()
                height = 100.percent() - 30.pixels()
            } childOf this

        MediaModAddonRegistry.initialisedAddons.forEach { addon ->
            val metadata = MediaModAddonRegistry.getAddonMetadata(addon.identifier) ?: return@forEach

            repeat(10) {
                AddonComponent(addon.identifier, metadata.displayName)
                    .constrain {
                        x = CramSiblingConstraint(10f)
                        y = CramSiblingConstraint(10f)
                        height = 20.percent()
                        width = 35.percent()
                    } childOf addonsContainer
            }
        }


    }

    override fun afterInitialization() {
        super.afterInitialization()

        Inspector(this) childOf this
    }

    class AddonComponent(identifier: String, name: String, description: String = "An awesome MediaMod addon!") :
        UIComponent() {
        private val backgroundColour = Color(64, 64, 64).brighter()
        private val titleColour = Color(198, 198, 198)

        init {
            val backgroundBlock = UIRoundedRectangle(5f)
                .constrain {
                    width = 100.percent()
                    height = 100.percent()
                    color = backgroundColour.toConstraint()
                } childOf this

            UIText(name, false)
                .constrain {
                    x = 5.pixels()
                    y = 5.pixels()
                    color = titleColour.toConstraint()
                    textScale = 1.25.pixels()
                } childOf backgroundBlock

            UIWrappedText(description, false, trimText = true)
                .constrain {
                    x = 5.pixels()
                    y = SiblingConstraint(5f)
                    width = 100.percent()
                    color = titleColour.darker().toConstraint()
                } childOf backgroundBlock

            UIRoundedButton(Color(69, 130, 204), "Config", 40, 15) {
                MinecraftClient.openConfigScreen(MediaModConfigRegistry.getConfig(identifier))
            }.constrain {
                x = 5.pixels()
                y = SiblingConstraint(10f)
                width = 40.pixels()
                height = 15.pixels()
            } childOf backgroundBlock
        }
    }
}
