package dev.sleepyswords.piston.network.packet.serverbound.play

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket
import kotlinx.io.readDouble

data class MovePlayerPos(
    val x: Double,
    val feetY: Double,
    val z: Double,
    val flags: Byte
) : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<MovePlayerPos> = { buffer ->
            MovePlayerPos(
                x = buffer.readDouble(),
                feetY = buffer.readDouble(),
                z = buffer.readDouble(),
                flags = buffer.readByte(),
            )
        }
    }
}
