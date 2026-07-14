package dev.sleepyswords.piston.network.packet.clientbound.play

import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writeBlockVertex
import dev.sleepyswords.piston.network.VarNum.writeVarInt
import dev.sleepyswords.piston.utility.BlockVertex
import kotlinx.io.Sink

class BlockUpdatePacket(val position: BlockVertex, val blockID: Int) : ClientboundPacket {
    override fun encode(out: Sink) {
        out.writeBlockVertex(position)
        out.writeVarInt(blockID)
    }

    override fun toString(): String {
        return "BlockUpdatePacket(position=$position, blockID=$blockID)"
    }

    override val opcode: Int = 0x8
}
