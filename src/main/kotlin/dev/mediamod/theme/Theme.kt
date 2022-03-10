@file:UseSerializers(ColorSerializer::class)

package dev.mediamod.theme

import dev.mediamod.serializer.ColorSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.awt.Color
import java.awt.image.BufferedImage

@Serializable
open class Theme(
    val name: String,
    var colors: Colors
) {
    @Serializable
    data class Colors(
        val background: Color,
        val text: Color,
        @SerialName("progress_bar")
        val progressBar: Color,
        @SerialName("progress_bar_background")
        val progressBarBackground: Color,
        @SerialName("progress_bar_text")
        val progressBarText: Color
    )

    open fun update(image: BufferedImage) {}
}