package dev.sleepyswords.piston.network.packet.serverbound.login

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket

class LoginSuccessServerboundPacket : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<LoginSuccessServerboundPacket> = { _ ->
            LoginSuccessServerboundPacket()
        }
    }
}
