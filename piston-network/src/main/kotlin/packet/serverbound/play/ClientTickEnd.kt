package dev.sleepyswords.piston.network.packet.serverbound.play

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket

class ClientTickEnd : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<ClientTickEnd> = { buffer ->
            ClientTickEnd()
        }
    }
}
