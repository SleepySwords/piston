package dev.sleepyswords.piston.network

import dev.sleepyswords.piston.network.packet.clientbound.play.Position
import dev.sleepyswords.piston.network.packet.clientbound.play.Rotation
import dev.sleepyswords.piston.network.packet.clientbound.play.Velocity
import dev.sleepyswords.piston.utility.BlockVertex
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readByte
import io.ktor.utils.io.writeByte
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeDouble
import kotlinx.io.writeFloat
import kotlinx.io.writeString
import kotlin.uuid.Uuid

object VarNum {
    const val SEGMENT_BITS: Int = 0x7F
    const val CONTINUE_BIT: Int = 0x80

    inline fun writeVarInt(
        value: Int,
        consumer: (Byte) -> Unit,
    ) {
        var v = value
        while (true) {
            if ((v and SEGMENT_BITS.inv()) == 0) {
                consumer(v.toByte())
                return
            }

            consumer(((v and SEGMENT_BITS) or CONTINUE_BIT).toByte())

            // Note: >>> means that the leftmost bits are filled with zeroes regardless of the sign,
            // rather than being filled with copies of the sign bit to preserve the sign.
            // In languages that don't have a ">>>" operator, This behavior can often be selected by
            // performing the shift on an unsigned type.
            v = v ushr 7
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

    inline fun writeVarLong(
        value: Long,
        consumer: (Byte) -> Unit,
    ) {
        var v = value
        while (true) {
            if ((v and SEGMENT_BITS.toLong().inv()) == 0L) {
                consumer(v.toByte())
                return
            }

            consumer(((v and SEGMENT_BITS.toLong()) or CONTINUE_BIT.toLong()).toByte())

            // Note: >>> means that the leftmost bits are filled with zeroes regardless of the sign,
            // rather than being filled with copies of the sign bit to preserve the sign.
            // In languages that don't have a ">>>" operator, This behavior can often be selected by
            // performing the shift on an unsigned type.
            v = v ushr 7
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

    fun Sink.writeUuid(uuid: Uuid) {
        uuid.toLongs { mostSignificantBits, leastSignificantBits ->
            writeLong(mostSignificantBits)
            writeLong(leastSignificantBits)
        }
    }

    fun Sink.writeBoolean(value: Boolean) {
        writeByte(if (value) 1 else 0)
    }

    fun Sink.writeBlockVertex(position: BlockVertex) {
        var internal = position.x.toLong()
        internal = internal shl 26
        internal = internal or (position.z.toLong() and ((1L shl 26) - 1))
        internal = internal shl 12
        internal = internal or (position.y.toLong() and ((1L shl 12) - 1))

        writeLong(internal)
    }

    fun Sink.writePosition(position: Position) {
        writeDouble(position.x)
        writeDouble(position.y)
        writeDouble(position.z)
    }

    fun Sink.writeVelocity(velocity: Velocity) {
        writeDouble(velocity.x)
        writeDouble(velocity.y)
        writeDouble(velocity.z)
    }

    fun Sink.writeRotation(rotation: Rotation) {
        writeFloat(rotation.yaw)
        writeFloat(rotation.pitch)
    }

    inline fun readVarInt(readByte: () -> Byte): Int {
        var value = 0
        var position = 0
        var currentByte: Byte

        while (true) {
            currentByte = readByte()
            value = value or ((currentByte.toInt() and SEGMENT_BITS) shl position)

            if ((currentByte.toInt() and CONTINUE_BIT) == 0) break

            position += 7

            if (position >= 32) throw RuntimeException("VarInt is too big")
        }

        return value
    }

    fun Source.readVarInt(): Int = readVarInt(::readByte)

    fun Source.readPosition(): BlockVertex {
        val position = readLong()
        val x = position shr 38
        val y = position shl 52 shr 52
        val z = position shl 26 shr 38
        return BlockVertex(x.toInt(), y.toShort(), z.toInt())
    }

    fun Source.readMCString(): String {
        val length = readVarInt()
        return readString(length.toLong())
    }

    fun Source.readUuid(): Uuid {
        val mostSignificantBits = readLong()
        val leastSignificantBits = readLong()

        return Uuid.fromLongs(mostSignificantBits, leastSignificantBits)
    }

    suspend fun ByteReadChannel.readVarInt(): Int = readVarInt { readByte() }
}
