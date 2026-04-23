package dev.sleepyswords.piston.network.packet.serverbound.login

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.VarNum.readVarInt

data class ConfirmTeleportationPacket(val teleportId: Int) : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<ConfirmTeleportationPacket> = { buffer ->
            ConfirmTeleportationPacket(buffer.readVarInt())
        }
    }
}

