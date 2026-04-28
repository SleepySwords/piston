package dev.sleepyswords.piston.network

import dev.sleepyswords.piston.network.VarNum.writeVarInt
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writePacket
import kotlinx.io.Buffer
import kotlinx.io.Sink

// FIXME: Maybe make this a separate deserialiser like kotlinx serialisation
interface ClientboundPacket {
    fun encode(out: Sink)

    val opcode: Int
}

suspend fun ByteWriteChannel.writeServerPacket(packet: ClientboundPacket) {
    // Maybe use a pool later on
    val buf = Buffer()

    buf.writeVarInt(packet.opcode)
    packet.encode(buf)

    writeVarInt(buf.size.toInt())
    writePacket(buf)
}
