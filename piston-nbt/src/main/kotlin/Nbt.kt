package dev.sleepyswords.nbt

data class NamedTag<T: Nbt>(val name: String, val tag: T)

sealed class Nbt {

    sealed interface TagType<T: Nbt>

    object ByteTagType : TagType<ByteTag> {}
    data class ByteTag(val value: Byte) : Nbt()

    object ShortTagType : TagType<ShortTag>
    data class ShortTag(val value: Short) : Nbt()

    object IntTagType : TagType<IntTag>
    data class IntTag(val value: Int) : Nbt()

    object LongTagType : TagType<LongTag>
    data class LongTag(val value: Long) : Nbt()

    object FloatTagType : TagType<FloatTag>
    data class FloatTag(val value: Float) : Nbt()

    object DoubleTagType : TagType<DoubleTag>
    data class DoubleTag(val value: Double) : Nbt()

    object ByteArrayTagType : TagType<ByteArrayTag>
    class ByteArrayTag(val value: ByteArray) : Nbt()

    object StringTagType : TagType<StringTag>
    data class StringTag(val value: String) : Nbt()

    class ListTagType<T: Nbt> : TagType<ListTag<T>>
    class ListTag<T: Nbt>(val tagType: TagType<T>, val value: List<T>) : Nbt()

    object CompoundTagType : TagType<CompoundTag>
    data class CompoundTag(val value: Map<String, Nbt>) : Nbt()

    object IntArrayTagType : TagType<IntArrayTag>
    class IntArrayTag(val value: IntArray) : Nbt()

    object LongArrayTagType : TagType<LongArrayTag>
    class LongArrayTag(val value: LongArray) : Nbt()
}

fun <T: Nbt> namedTag(name: String, tag: T): NamedTag<T> {
    return NamedTag(name, tag)
}

fun <T: Nbt> listTag(tagType: Nbt.TagType<T>, receiver: MutableList<T>.() -> Unit): Nbt.ListTag<T> {
    val list = mutableListOf<T>()
    receiver(list)
    return Nbt.ListTag(tagType, list)
}

fun byteTag(value: Byte) : Nbt.ByteTag = Nbt.ByteTag(value)

fun shortTag(value: Short) : Nbt.ShortTag = Nbt.ShortTag(value)

fun intTag(value: Int) : Nbt.IntTag = Nbt.IntTag(value)

fun longTag(value: Long) : Nbt.LongTag = Nbt.LongTag(value)

fun floatTag(value: Float) : Nbt.FloatTag = Nbt.FloatTag(value)

fun doubleTag(value: Double) : Nbt.DoubleTag = Nbt.DoubleTag(value)

fun byteArrayTag(value: ByteArray) : Nbt.ByteArrayTag = Nbt.ByteArrayTag(value)

fun stringTag(value: String) : Nbt.StringTag = Nbt.StringTag(value)

fun intArrayTag(value: IntArray) : Nbt.IntArrayTag = Nbt.IntArrayTag(value)

fun longArrayTag(value: LongArray) : Nbt.LongArrayTag = Nbt.LongArrayTag(value)

class CompoundTagBuilder(val tags: MutableMap<String, Nbt> = mutableMapOf<String, Nbt>()) {
    operator fun String.minus(nbt: Nbt) {
        tags[this] = nbt
    }
    fun put(key: String, nbt: Nbt) {
        tags[key] = nbt
    }
}

fun compoundTag(receiver: CompoundTagBuilder.() -> Unit): Nbt.CompoundTag {
    val builder = CompoundTagBuilder()
    receiver(builder)

    return Nbt.CompoundTag(builder.tags)
}
