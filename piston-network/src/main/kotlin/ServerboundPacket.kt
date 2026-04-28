package dev.sleepyswords.piston.network

import dev.sleepyswords.piston.network.VarNum.readVarInt
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readPacket
import kotlinx.io.Source

interface ServerboundPacket

fun interface ClientPacketDecoder<T : ServerboundPacket> {
    fun decode(buffer: Source): T
}

suspend fun ByteReadChannel.readServerPacket(): Pair<Int, Source> {
    val length = readVarInt()
    val b = readPacket(length)
    val opcode = b.readVarInt()

    return Pair(opcode, b)
}
