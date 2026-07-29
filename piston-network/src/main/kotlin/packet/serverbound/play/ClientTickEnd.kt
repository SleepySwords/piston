package dev.sleepyswords.piston.network.packet.serverbound.play

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.VarNum.readPosition
import dev.sleepyswords.piston.network.VarNum.readVarInt
import dev.sleepyswords.piston.utility.BlockVertex

class ClientTickEnd : ServerboundPacket{
    companion object {
        val DECODER: ClientPacketDecoder<ClientTickEnd> = { buffer ->
            ClientTickEnd()
        }
    }
}
