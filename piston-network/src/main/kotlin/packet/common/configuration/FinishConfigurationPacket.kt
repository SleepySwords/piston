package dev.sleepyswords.piston.network.packet.common.configuration

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.VarNum.writeMCString
import kotlinx.io.Sink

class FinishConfigurationPacket : ClientboundPacket, ServerboundPacket{
    override fun encode(out: Sink) {}
    override val opcode: Int = 0x03
    companion object {
        val Decoder = ClientPacketDecoder { buffer ->
            FinishConfigurationPacket()
        }
    }
}
