package dev.sleepyswords.piston.network.packet.clientbound.status

import dev.sleepyswords.nbt.TextComponent
import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writeMCString
import kotlinx.io.Sink
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class PongPacket(
    private val timestamp: Long,
) : ClientboundPacket {
    override fun encode(out: Sink) {
        out.writeLong(timestamp)
    }

    override val opcode: Int = 0x01
}
