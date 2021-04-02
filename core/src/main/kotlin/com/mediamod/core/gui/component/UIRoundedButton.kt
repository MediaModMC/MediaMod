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