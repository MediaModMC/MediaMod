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

package com.mediamod.gui.panel.impl

import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIImage
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.dsl.*
import com.mediamod.core.MediaModCore
import com.mediamod.gui.component.UIRoundedButton
import com.mediamod.gui.panel.MediaModHomeScreenPanel
import java.awt.Color
import java.net.URL

/**
 * The first panel that the user will see on a MediaMod GUI
 */
class HomePanel : MediaModHomeScreenPanel("Home") {
    init {
        UIText("News", false).constrain {
            x = 20.pixels()
            y = 20.pixels()
            color = titleColour
            textScale = 2.pixels()
        } childOf this
    }

    private val imageHolder by UIContainer().constrain {
        x = 20.pixels()
        y = SiblingConstraint(10f)
        height = 25.percent()
        width = FillConstraint(false)
    } childOf this

    init {
        repeat(3) {
            UIImage.ofURL(URL("https://i.imgur.com/jEIS7Yd.png")).constrain {
                x = CramSiblingConstraint(10f)
                y = CramSiblingConstraint(10f)
                height = basicHeightConstraint { imageHolder.getHeight() / 2 * 1.5f }
                width = 30.percent()
            } childOf imageHolder
        }
    }

    private val updateHeaderContainer by UIContainer().constrain {
        x = 20.pixels()
        y = SiblingConstraint(10f)
        height = (mc.fontRendererObj.FONT_HEIGHT * 2).pixels()
        width = ChildBasedSizeConstraint()
    } childOf this

    init {
        UIText("Updates", false).constrain {
            color = titleColour
            textScale = 2.pixels()
        } childOf updateHeaderContainer

        UIRoundedButton(Color(69, 204, 116), "Update", 50, updateHeaderContainer.getHeight().toInt()) {
            // TODO update mod or something lol
        }.constrain {
            x = SiblingConstraint(10f)
            y = CenterConstraint()
            width = 50.pixels()
            height = basicHeightConstraint { updateHeaderContainer.getHeight() }
        } childOf updateHeaderContainer

        UIText("An update is available! (${MediaModCore.version})", false).constrain {
            x = 20.pixels()
            y = SiblingConstraint(7f)
            textScale = 1.3f.pixels()
            color = Color(142, 142, 142).toConstraint()
        } childOf this
    }
}
