package dev.sleepyswords.nbt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ColorSerializer::class)
@Suppress("UNUSED_PARAMETER")
sealed class Color(
    val coloredString: String,
) {
    object Black : Color("black")

    object DarkBlue : Color("dark_blue")

    object DarkGreen : Color("dark_green")

    object DarkAqua : Color("dark_aqua")

    object DarkRed : Color("dark_red")

    object DarkPurple : Color("dark_purple")

    object Gold : Color("gold")

    object Gray : Color("gray")

    object DarkGray : Color("dark_gray")

    object Blue : Color("blue")

    object Green : Color("green")

    object Aqua : Color("aqua")

    object Red : Color("red")

    object LightPurple : Color("light_purple")

    object Yellow : Color("yellow")

    object White : Color("white")

    class Hex(
        hex: String,
    ) : Color(hex)
}

class ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Color,
    ) {
        encoder.encodeString(value.coloredString)
    }

    override fun deserialize(decoder: Decoder): Color {
        TODO("Not yet implemented")
    }
}
