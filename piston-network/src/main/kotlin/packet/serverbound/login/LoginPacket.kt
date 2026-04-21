package dev.sleepyswords.piston.network.packet.serverbound.login

import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.ClientPacketDecoder
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}
class LoginPacket(val num: Int) : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<LoginPacket> = {
                buffer -> LoginPacket(buffer.readByte().toInt())
        }
    }
}

