package dev.mediamod.utils

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.animate

internal fun UIComponent.setColorAnimated(color: ColorConstraint) = apply {
    animate {
        setColorAnimation(Animations.IN_OUT_EXP, 0.5f, color, 0f)
    }
}