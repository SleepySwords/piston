package dev.sleepyswords.piston.network.packet.serverbound.play

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket
import kotlinx.io.readDouble
import kotlinx.io.readFloat

data class MovePlayerPosRot(
    val x: Double,
    val feetY: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    val flags: Byte
) : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<MovePlayerPosRot> = { buffer ->
            MovePlayerPosRot(
                x = buffer.readDouble(),
                feetY = buffer.readDouble(),
                z = buffer.readDouble(),
                yaw = buffer.readFloat(),
                pitch = buffer.readFloat(),
                flags = buffer.readByte(),
            )
        }
    }
}
