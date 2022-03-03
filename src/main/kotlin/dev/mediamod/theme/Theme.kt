@file:UseSerializers(ColorSerializer::class)

package dev.mediamod.theme

import dev.mediamod.serializer.ColorSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.awt.Color

@Serializable
open class Theme(
    val name: String,
    val colors: Colors
) {
    @Serializable
    data class Colors(
        val background: Color,
        val text: Color,
        @SerialName("progress_bar")
        val progressBar: Color,
        @SerialName("progress_bar_background")
        val progressBarBackground: Color
    )
}