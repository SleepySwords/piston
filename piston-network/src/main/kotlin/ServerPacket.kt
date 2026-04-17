package dev.sleepyswords.piston.network

import io.ktor.utils.io.*
import kotlinx.io.Buffer

interface ServerPacket {
    fun encode(out: Buffer)

    val opcode: Int
}

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

fun Buffer.writeVarInt(value: Int) {
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

fun Buffer.writeVarLong(value: Long) {
    writeVarLong(value, ::writeByte)
}

suspend fun ByteWriteChannel.writeVarLong(value: Long) {
    writeVarLong(value) {
        writeByte(it)
    }
}

suspend fun ByteWriteChannel.writeServerPacket(packet: ServerPacket) {
    // Maybe use a pool later on
    val buf = Buffer()

    buf.writeVarInt(packet.opcode)
    packet.encode(buf)

    writeVarInt(buf.size.toInt())
    writePacket(buf)
}