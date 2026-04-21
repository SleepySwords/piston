package dev.sleepyswords.piston.network

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readByte
import io.ktor.utils.io.writeByte
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString

object VarNum {
    const val SEGMENT_BITS: Int = 0x7F;
    const val CONTINUE_BIT: Int = 0x80;

    inline fun writeVarInt(value: Int, consumer: (Byte) -> Unit) {
        var v = value
        while (true) {
            if ((v and SEGMENT_BITS.inv()) == 0) {
                consumer(v.toByte());
                return;
            }

            consumer(((v and SEGMENT_BITS) or CONTINUE_BIT).toByte());

            // Note: >>> means that the leftmost bits are filled with zeroes regardless of the sign,
            // rather than being filled with copies of the sign bit to preserve the sign.
            // In languages that don't have a ">>>" operator, This behavior can often be selected by
            // performing the shift on an unsigned type.
            v = v ushr 7;
        }
    }

    fun Sink.writeVarInt(value: Int) {
        writeVarInt(value, ::writeByte)
    }

    suspend fun ByteWriteChannel.writeVarInt(value: Int) {
        writeVarInt(value) {
            writeByte(it)
        }
    }

    inline fun writeVarLong(value: Long, consumer: (Byte) -> Unit) {
        var v = value
        while (true) {
            if ((v and SEGMENT_BITS.toLong().inv()) == 0L) {
                consumer(v.toByte());
                return;
            }

            consumer(((v and SEGMENT_BITS.toLong()) or CONTINUE_BIT.toLong()).toByte());

            // Note: >>> means that the leftmost bits are filled with zeroes regardless of the sign,
            // rather than being filled with copies of the sign bit to preserve the sign.
            // In languages that don't have a ">>>" operator, This behavior can often be selected by
            // performing the shift on an unsigned type.
            v = v ushr 7;
        }
    }

    fun Sink.writeVarLong(value: Long) {
        writeVarLong(value, ::writeByte)
    }

    suspend fun ByteWriteChannel.writeVarLong(value: Long) {
        writeVarLong(value) {
            writeByte(it)
        }
    }

    fun Sink.writeMCString(string: String) {
        writeVarInt(string.length)
        writeString(string)
    }


    inline fun readVarInt(readByte: () -> Byte): Int {
        var value = 0;
        var position = 0;
        var currentByte: Byte;

        while (true) {
            currentByte = readByte();
            value = value or (currentByte.toInt() and SEGMENT_BITS) shl position;

            if ((currentByte.toInt() and CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 32) throw RuntimeException("VarInt is too big");
        }

        return value;
    }

    fun Source.readVarInt(): Int = readVarInt(::readByte)

    fun Source.readMCString(): String {
        val length = readVarInt()
        return readString(length.toLong())
    }

    suspend fun ByteReadChannel.readVarInt(): Int {
        return readVarInt { readByte() }
    }
}
