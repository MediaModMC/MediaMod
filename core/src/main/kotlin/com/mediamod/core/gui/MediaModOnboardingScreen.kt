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


package com.mediamod.core.gui

import club.sk1er.elementa.components.*
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.dsl.*
import club.sk1er.mods.core.universal.UDesktop
import com.mediamod.core.bindings.minecraft.MinecraftClient
import com.mediamod.core.gui.component.UIRoundedButton
import com.mediamod.core.gui.screen.IWindowScreen
import java.awt.Color
import java.net.URI

class MediaModOnboardingScreen : IWindowScreen() {
    private var previousGuiScale: Int = 0
    private val blockColour = Color(64, 64, 64)

    private val descriptionColour = Color(142, 142, 142)
    private val descriptionText = """
MediaMod has changed a lot since 1.0, with many new features!

Some of these features include
- New addons system
- Improved reliability and performance
- Better player customisation
- and more...

Even more services are supported with MediaMod 2.0, to check out all the services you can install, check out the "Discover" pane in the GUI!
Ready to check it out? 

Click "OK" to never see this GUI again."""

    init {
        val leftBlock = UIBlock(blockColour)
            .constrain {
                x = 0.pixels()
                y = 0.pixels()
                width = 50.percent()
                height = 100.percent()
            } childOf this

        val textContainer = UIContainer().constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = 75.percent()
        } childOf leftBlock

        UIText("Welcome to MediaMod 2.0")
            .constrain {
                x = 10.pixels()
                y = 10.pixels()
                textScale = 1.5.pixels()
            } childOf textContainer

        UIWrappedText(
            descriptionText,
            false,
            trimText = true
        ).constrain {
            x = 10.pixels()
            y = SiblingConstraint() + 10.pixels()
            width = 90.percent()
            color = descriptionColour.toConstraint()
        } childOf textContainer

        val bottomContainer = UIContainer()
            .constrain {
                y = SiblingConstraint()
                width = 100.percent()
                height = 25.percent()
            } childOf leftBlock

        val buttonContainer = UIContainer()
            .constrain {
                x = CenterConstraint()
                y = 50.percent()
                width = 100.percent()
                height = 50.percent()
            } childOf bottomContainer

        UIRoundedButton(Color(69, 204, 116), "OK", 50, 20) {
            MinecraftClient.openScreen(null)
        }.constrain {
            x = 10.pixels()
            width = 50.pixels()
            height = 20.pixels()
        } childOf buttonContainer

        UIRoundedButton(Color(92, 160, 236), "Website", 50, 20) {
            UDesktop.browse(URI("https://mediamodmc.github.io"))
        }.constrain {
            x = SiblingConstraint(5f)
            width = 50.pixels()
            height = 20.pixels()
        } childOf buttonContainer

        UIRoundedButton(Color(126, 92, 236), "Discord", 50, 20) {
            UDesktop.browse(URI("https://inv.wtf/mediamod"))
        }.constrain {
            x = SiblingConstraint(5f)
            width = 50.pixels()
            height = 20.pixels()
        } childOf buttonContainer

        val rightBlock = UIBlock(blockColour.darker())
            .constrain {
                x = 50.percent()
                y = 0.pixels()
                width = 50.percent()
                height = 100.percent()
            } childOf this

        UIImage.ofResource("/assets/mediamod/screenshot.png")
            .constrain {
                x = 5.percent()
                y = 25.percent() - 5.pixels()
                width = 90.percent()
                height = 50.percent()
            } childOf rightBlock

        UIText("This is a placeholder image n stuff", false)
            .constrain {
                x = 5.percent()
                y = SiblingConstraint() + 5.pixels()
                color = descriptionColour.toConstraint()
            } childOf rightBlock
    }
}
