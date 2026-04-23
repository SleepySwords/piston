package dev.sleepyswords.nbt

import kotlinx.io.Sink
import kotlinx.io.writeDouble
import kotlinx.io.writeFloat
import kotlinx.io.writeString

private fun encodeNbtInternal(sink: Sink, tag: Nbt) {
    when (tag) {
        is Nbt.ByteTag -> sink.writeByte(tag.value)
        is Nbt.ByteArrayTag -> sink.write(tag.value)
        is Nbt.DoubleTag -> sink.writeDouble(tag.value)
        is Nbt.FloatTag -> sink.writeFloat(tag.value)
        is Nbt.IntArrayTag -> {
            sink.writeInt(tag.value.size)
            tag.value.forEach { sink.writeInt(it) }
        }
        is Nbt.IntTag -> sink.writeInt(tag.value)
        is Nbt.ListTag<*> -> {
            sink.writeByte(nbtTagID(tag.tagType))
            sink.writeInt(tag.value.size)
            tag.value.forEach { encodeNbtInternal(sink, it) }
        }
        is Nbt.LongArrayTag -> {
            sink.writeInt(tag.value.size)
            tag.value.forEach { sink.writeLong(it) }
        }
        is Nbt.LongTag -> sink.writeLong(tag.value)
        is Nbt.ShortTag -> sink.writeShort(tag.value)
        is Nbt.StringTag -> {
            sink.writeShort(tag.value.length.toShort())
            sink.writeString(tag.value)
        }
        is Nbt.CompoundTag -> {
            tag.value.forEach { (k, v) ->
                sink.writeByte(nbtTagID(v))
                sink.writeShort(k.length.toShort())
                sink.writeString(k)
                encodeNbtInternal(sink, v)
            }
            sink.writeByte(0)
        }
    }
}

fun nbtTagID(tag: Nbt.TagType<*>): Byte {
    return when (tag) {
        is Nbt.ByteTagType -> 1
        is Nbt.ShortTagType -> 2
        is Nbt.IntTagType -> 3
        is Nbt.LongTagType -> 4
        is Nbt.FloatTagType -> 5
        is Nbt.DoubleTagType -> 6
        is Nbt.ByteArrayTagType -> 7
        is Nbt.StringTagType -> 8
        is Nbt.ListTagType<*> -> 9
        is Nbt.CompoundTagType -> 10
        is Nbt.IntArrayTagType -> 11
        is Nbt.LongArrayTagType -> 12
    }
}

fun nbtTagID(tag: Nbt): Byte {
    return when (tag) {
        is Nbt.ByteTag -> 1
        is Nbt.ShortTag -> 2
        is Nbt.IntTag -> 3
        is Nbt.LongTag -> 4
        is Nbt.FloatTag -> 5
        is Nbt.DoubleTag -> 6
        is Nbt.ByteArrayTag -> 7
        is Nbt.StringTag -> 8
        is Nbt.ListTag<*> -> 9
        is Nbt.CompoundTag -> 10
        is Nbt.IntArrayTag -> 11
        is Nbt.LongArrayTag -> 12
    }
}

fun encodeNameNbtTag(sink: Sink, tag: NamedTag<*>) {
    sink.writeByte(nbtTagID(tag.tag))
    sink.writeShort(tag.name.length.toShort())
    sink.writeString(tag.name)
    encodeNbtInternal(sink, tag.tag)
}

fun encodeNbtCompoundTag(sink: Sink, tag: Nbt.CompoundTag) {
    sink.writeByte(nbtTagID(tag))
    encodeNbtInternal(sink, tag)
}
