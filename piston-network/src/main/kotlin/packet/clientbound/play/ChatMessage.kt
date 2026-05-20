package dev.sleepyswords.piston.network.packet.clientbound.play

import dev.sleepyswords.nbt.Nbt
import dev.sleepyswords.nbt.encodeNbtCompoundTag
import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writeBoolean
import kotlinx.io.Sink

class SystemChatMessage(
    val content: Nbt.CompoundTag,
    val overlay: Boolean,
) : ClientboundPacket {
    override fun encode(out: Sink) {
        encodeNbtCompoundTag(out, content)
        out.writeBoolean(overlay)
    }

    override val opcode: Int = 0x77
}
