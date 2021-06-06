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

package com.mediamod.core.gui.screen.impl.home.component

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CramSiblingConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.StencilEffect
import java.awt.Color
import java.net.URL

class NewsComponent(image: URL, headline: String) : UIComponent() {
    private val backgroundColour = Color(64, 64, 64).brighter()
    private val textColour = Color(142, 142, 142).brighter()

    private val holder by UIRoundedRectangle(5f).constrain {
        x = 0.pixels()
        y = 0.pixels()
        height = 100.percent()
        width = FillConstraint(false)
        color = backgroundColour.toConstraint()
    } childOf this

    init {
        UIImage.ofURL(image).constrain {
            x = 0.pixels()
            y = 0.pixels()
            height = 70.percent()
            width = 100.percent()
        }.effect(StencilEffect()) childOf holder

        UIText(headline, false).constrain {
            x = 5.pixels()
            y = CramSiblingConstraint(5f)
            color = textColour.toConstraint()
        } childOf holder
    }
}
