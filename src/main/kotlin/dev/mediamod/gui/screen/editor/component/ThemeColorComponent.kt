package dev.mediamod.gui.screen.editor.component

import dev.mediamod.gui.style.styled
import dev.mediamod.gui.style.stylesheet
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ConstantColorConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.state.BasicState
import gg.essential.vigilance.gui.settings.ColorComponent
import java.awt.Color

class ThemeColorComponent(themeColor: Color, text: String, locked: Boolean) : UIContainer() {
    private val colorState = BasicState(themeColor)
    private var onChange: ((Color) -> Unit)? = null

    private val stylesheet = stylesheet {
        "this" {
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint()
        }

        "preview" {
            color = ConstantColorConstraint().bindColor(colorState)
            y = CenterConstraint()
            width = 8.pixels()
            height = 8.pixels()
        }

        "text" {
            x = SiblingConstraint(5f)
            y = CenterConstraint()
        }

        "colorPicker" {
            x = 0.pixels(true)
            y = CenterConstraint()
        }
    }

    init {
        styled(stylesheet["this"])

        UIBlock()
            .styled(stylesheet["preview"])
            .childOf(this)

        UIText(text, false)
            .styled(stylesheet["text"])
            .childOf(this)

        ColorComponent(themeColor, false)
            .styled(stylesheet["colorPicker"])
            .childOf(this)
            .apply {
                // For some reason, onValueChange isn't an apply
                onValueChange {
                    if (it !is Color) return@onValueChange
                    colorState.set(it)
                    onChange?.invoke(it)
                }

                if (locked) mouseClickListeners.clear()
            }
    }

    fun onChange(block: (Color) -> Unit) = apply { this.onChange = block }
}