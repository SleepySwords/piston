package dev.sleepyswords.piston.network.packet.serverbound.status

import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.ClientPacketDecoder

class StatusRequestPacket : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<StatusRequestPacket> = { _ ->
            StatusRequestPacket()
        }
    }
}

