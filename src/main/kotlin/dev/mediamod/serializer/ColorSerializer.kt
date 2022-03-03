package dev.mediamod.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.Color

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeString(String.format("#%06X", 0xFFFFFF and value.rgb))
    }

    override fun deserialize(decoder: Decoder): Color {
        val value = decoder.decodeString()
        if (!value.startsWith("#"))
            error("You must supply a hexadecimal color! $value is not a valid hexadecimal color.")

        return Color.decode(value)
    }
}