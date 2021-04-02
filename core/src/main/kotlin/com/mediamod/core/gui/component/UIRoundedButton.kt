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


package com.mediamod.core.gui.component

import club.sk1er.elementa.UIComponent
import club.sk1er.elementa.components.UIRoundedRectangle
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import java.awt.Color

class UIRoundedButton(buttonColor: Color, text: String, buttonWidth: Int, buttonHeight: Int, onClick: () -> Unit) :
    UIComponent() {
    init {
        val rectangle = UIRoundedRectangle(5F)
            .constrain {
                width = buttonWidth.pixels()
                height = buttonHeight.pixels()
                color = buttonColor.toConstraint()
            }
            .onMouseClick {
                onClick()
            } childOf this

        val textElement = UIText(text, false)
            .constrain {
                x = CenterConstraint()
                y = CenterConstraint()
            } childOf rectangle

        rectangle.onMouseEnter {
            animate {
                setColorAnimation(
                    Animations.IN_OUT_SIN,
                    0.25f,
                    buttonColor.darker().toConstraint()
                )
            }

            textElement.animate {
                setColorAnimation(
                    Animations.IN_OUT_SIN,
                    0.25f,
                    Color.WHITE.darker().toConstraint()
                )
            }
        }

        rectangle.onMouseLeave {
            animate { setColorAnimation(Animations.IN_OUT_SIN, 0.25f, buttonColor.toConstraint()) }
            textElement.animate { setColorAnimation(Animations.IN_OUT_SIN, 0.25f, Color.WHITE.toConstraint()) }
        }
    }
}