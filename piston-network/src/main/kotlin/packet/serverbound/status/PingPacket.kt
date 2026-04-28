package dev.sleepyswords.piston.network.packet.serverbound.status

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket

class PingPacket(
    val timestamp: Long,
) : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<PingPacket> = { source ->
            PingPacket(source.readLong())
        }
    }
}
