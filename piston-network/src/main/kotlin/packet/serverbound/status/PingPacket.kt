package dev.sleepyswords.piston.network.packet.serverbound.status

import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.ClientPacketDecoder

class PingPacket(val timestamp: Long) : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<PingPacket> = { source ->
            PingPacket(source.readLong())
        }
    }
}

