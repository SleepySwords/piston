package dev.sleepyswords.nbt

import kotlinx.serialization.Serializable

@DslMarker
annotation class TextComponentDsl


fun text(content: String, block: TextComponent.() -> Unit = {}): TextComponent {
    return TextComponent(content).apply(block)
}

fun test() {
    text("ok") {
        bold = true
    }
}

@Serializable
@TextComponentDsl
data class TextComponent(
    var text: String? = null,
    var bold: Boolean? = null,
    var italic: Boolean? = null,
    var underlined: Boolean? = null,
    var obfuscate: Boolean? = null,
    var obfuscated: Boolean? = null,
    var strikethrough: Boolean? = null,
    var color: Color? = null,
) {
}