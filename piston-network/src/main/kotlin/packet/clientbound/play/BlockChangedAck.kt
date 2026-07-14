package dev.sleepyswords.piston.network.packet.clientbound.play

import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writeVarInt
import kotlinx.io.Sink

class BlockChangedAck(val sequenceID: Int) : ClientboundPacket {
    override fun encode(out: Sink) {
        out.writeVarInt(sequenceID)
    }

    override val opcode: Int = 0x4
}
