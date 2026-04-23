package dev.sleepyswords.piston.network.packet.serverbound.configuration

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.VarNum.readMCString

data class ClientInformationPacket(
    val locale: String,
    val viewDistance: Byte,
) : ServerboundPacket{
    companion object {
        val Decoder = ClientPacketDecoder { buffer ->
            ClientInformationPacket(buffer.readMCString(), buffer.readByte())
        }
    }
}