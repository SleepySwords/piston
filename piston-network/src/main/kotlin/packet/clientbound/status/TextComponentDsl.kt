package dev.sleepyswords.piston.network.packet.clientbound.status

import dev.sleepyswords.piston.network.packet.clientbound.status.TextComponent.ArrayComponent
import dev.sleepyswords.piston.network.packet.clientbound.status.TextComponent.ObjectComponent
import dev.sleepyswords.piston.network.packet.clientbound.status.TextComponent.StringComponent

@DslMarker
annotation class TextComponentDsl

fun text(content: String, block: (TextComponentBuilder.() -> Unit)? = null): TextComponent {
    if (block == null) {
        return ObjectComponent(text = content)
    }
    return TextComponentBuilder().apply {
        text = content
        block()
    }.build()
}

fun textList(vararg components: TextComponent): TextComponent = ArrayComponent(components.toList())

fun textString(content: String): TextComponent = StringComponent(content)

@TextComponentDsl
class TextComponentBuilder {
    var type: String? = null
    var text: String? = null
    var translate: String? = null
    var score: ScoreComponent? = null
    var selector: String? = null
    var keybind: String? = null
    var nbt: String? = null

    var color: String? = null
    var font: String? = null
    var bold: Boolean? = null
    var italic: Boolean? = null
    var underlined: Boolean? = null
    var strikethrough: Boolean? = null
    var obfuscated: Boolean? = null
    var shadowColor: ShadowColor? = null

    var insertion: String? = null
    var clickEvent: ClickEvent? = null
    var hoverEvent: HoverEvent? = null

    private val extra: MutableList<TextComponent> = mutableListOf()

    fun extra(component: TextComponent) {
        extra += component
    }

    fun extra(content: String, block: (TextComponentBuilder.() -> Unit)? = null) {
        extra += text(content, block)
    }

    fun build(): TextComponent = ObjectComponent(
        type = type,
        text = text,
        translate = translate,
        score = score,
        selector = selector,
        keybind = keybind,
        nbt = nbt,
        extra = extra.ifEmpty { null },
        color = color,
        font = font,
        bold = bold,
        italic = italic,
        underlined = underlined,
        strikethrough = strikethrough,
        obfuscated = obfuscated,
        shadowColor = shadowColor,
        insertion = insertion,
        clickEvent = clickEvent,
        hoverEvent = hoverEvent,
    )
}
