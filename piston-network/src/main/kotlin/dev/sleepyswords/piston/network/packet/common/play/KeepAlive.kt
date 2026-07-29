package dev.sleepyswords.piston.network.dev.sleepyswords.piston.network.packet.common.play

import dev.sleepyswords.piston.network.ClientboundPacket
import kotlinx.io.Sink

class KeepAlivePacket(
    val keepAliveID: Long
) : ClientboundPacket {
    override fun encode(out: Sink) {
        out.writeLong(keepAliveID)
    }

    override val opcode: Int = 0x2B
}
