@file:UseSerializers(ColorSerializer::class)

package dev.mediamod.theme

import dev.mediamod.serializer.ColorSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.awt.Color
import java.awt.image.BufferedImage

sealed class Theme {
    abstract val name: String
    abstract val colors: Colors

    abstract class InbuiltTheme(override val name: String) : Theme() {
        abstract override val colors: Colors
    }

    @Serializable
    data class LoadedTheme(
        override var name: String,
        override var colors: Colors
    ) : Theme() {
        override fun update(image: BufferedImage) {}
    }

    abstract fun update(image: BufferedImage)
}

@Serializable
data class Colors(
    var background: Color,
    var text: Color,
    @SerialName("progress_bar")
    var progressBar: Color,
    @SerialName("progress_bar_background")
    var progressBarBackground: Color,
    @SerialName("progress_bar_text")
    var progressBarText: Color
)
