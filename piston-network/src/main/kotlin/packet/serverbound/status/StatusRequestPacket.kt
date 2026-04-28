package dev.sleepyswords.piston.network.packet.serverbound.status

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket

class StatusRequestPacket : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<StatusRequestPacket> = { _ ->
            StatusRequestPacket()
        }
    }
}
