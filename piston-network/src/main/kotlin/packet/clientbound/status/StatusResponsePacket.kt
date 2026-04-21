package dev.sleepyswords.piston.network.packet.clientbound.status

import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writeMCString
import kotlinx.io.Sink
import kotlinx.serialization.json.Json

data class StatusResponse(val name: String)

class StatusResponsePacket(val statusResponse: String) : ClientboundPacket {
    override fun encode(out: Sink) {
        out.writeMCString(statusResponse)
    }

    override val opcode: Int = 0x00
}