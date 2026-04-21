package dev.sleepyswords.piston.network.packet.clientbound.status

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = TextComponentSerializer::class)
sealed interface TextComponent {
    @Serializable
    data class StringComponent(val value: String) : TextComponent

    @Serializable
    data class ArrayComponent(val value: List<TextComponent>) : TextComponent

    @Serializable
    data class ObjectComponent(
        val type: String? = null,
        val text: String? = null,
        val translate: String? = null,
        val score: ScoreComponent? = null,
        val selector: String? = null,
        val keybind: String? = null,
        val nbt: String? = null,
        val extra: List<TextComponent>? = null,
        val color: String? = null,
        val font: String? = null,
        val bold: Boolean? = null,
        val italic: Boolean? = null,
        val underlined: Boolean? = null,
        val strikethrough: Boolean? = null,
        val obfuscated: Boolean? = null,
        @SerialName("shadow_color") val shadowColor: ShadowColor? = null,
        val insertion: String? = null,
        @SerialName("click_event") val clickEvent: ClickEvent? = null,
        @SerialName("hover_event") val hoverEvent: HoverEvent? = null,
    ) : TextComponent
}

@Serializable
data class ScoreComponent(
    val name: String,
    val objective: String,
    val value: String? = null,
)

@Serializable(with = ShadowColorSerializer::class)
sealed interface ShadowColor {
    @Serializable
    data class IntColor(val value: Int) : ShadowColor

    @Serializable
    data class RgbaColor(val value: List<Float>) : ShadowColor
}

@Serializable
data class ClickEvent(
    val action: String,
    val value: String,
)

@Serializable
data class HoverEvent(
    val action: String,
    val value: TextComponent? = null,
    val contents: TextComponent? = null,
)

object TextComponentSerializer : kotlinx.serialization.KSerializer<TextComponent> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TextComponent")

    override fun serialize(encoder: Encoder, value: TextComponent) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("TextComponent can only be serialized to JSON")

        val element: JsonElement = when (value) {
            is TextComponent.StringComponent -> JsonPrimitive(value.value)
            is TextComponent.ArrayComponent -> JsonArray(
                value.value.map { child ->
                    jsonEncoder.json.encodeToJsonElement(TextComponentSerializer, child)
                },
            )
            is TextComponent.ObjectComponent ->
                jsonEncoder.json.encodeToJsonElement(TextComponent.ObjectComponent.serializer(), value)
        }

        jsonEncoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): TextComponent {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("TextComponent can only be deserialized from JSON")
        val element = jsonDecoder.decodeJsonElement()

        return when (element) {
            is JsonPrimitive ->
                TextComponent.StringComponent(element.content)
            is JsonArray ->
                TextComponent.ArrayComponent(
                    element.map { child ->
                        jsonDecoder.json.decodeFromJsonElement(TextComponentSerializer, child)
                    },
                )
            else ->
                jsonDecoder.json.decodeFromJsonElement(TextComponent.ObjectComponent.serializer(), element)
        }
    }
}

object ShadowColorSerializer : kotlinx.serialization.KSerializer<ShadowColor> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ShadowColor")

    override fun serialize(encoder: Encoder, value: ShadowColor) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("ShadowColor can only be serialized to JSON")

        val element = when (value) {
            is ShadowColor.IntColor -> JsonPrimitive(value.value)
            is ShadowColor.RgbaColor -> JsonArray(value.value.map { JsonPrimitive(it) })
        }

        jsonEncoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): ShadowColor {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("ShadowColor can only be deserialized from JSON")
        val element = jsonDecoder.decodeJsonElement()

        return when (element) {
            is JsonPrimitive -> ShadowColor.IntColor(element.int)
            is JsonArray -> ShadowColor.RgbaColor(element.map { it.jsonPrimitive.float })
            else -> throw SerializationException("Invalid shadow_color format: $element")
        }
    }
}
