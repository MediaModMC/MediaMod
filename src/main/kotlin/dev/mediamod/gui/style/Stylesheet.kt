package dev.mediamod.gui.style

import gg.essential.elementa.UIComponent
import gg.essential.elementa.UIConstraints

class Stylesheet {
    private val styles = mutableMapOf<String, UIConstraints.() -> Unit>()

    operator fun String.invoke(configuration: UIConstraints.() -> Unit) = apply { styles[this] = configuration }
    operator fun get(name: String) = styles[name]
}

infix fun <T : UIComponent> T.styled(style: (UIConstraints.() -> Unit)?) = apply {
    style?.invoke(constraints)
}

fun stylesheet(configuration: Stylesheet.() -> Unit) =
    Stylesheet().apply(configuration)