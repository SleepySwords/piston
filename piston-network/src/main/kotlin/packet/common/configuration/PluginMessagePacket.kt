package dev.sleepyswords.piston.network.packet.common.configuration

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.VarNum.readMCString
import dev.sleepyswords.piston.network.VarNum.writeMCString
import kotlinx.io.Sink

data class PluginMessagePacket(val channel: String) : ClientboundPacket, ServerboundPacket{
    override fun encode(out: Sink) {
        // Some data as well
        out.writeMCString(channel)
    }

    override val opcode: Int = 0x01

    companion object {
        val Decoder = ClientPacketDecoder { buffer ->
            PluginMessagePacket(buffer.readMCString())
        }
    }
}