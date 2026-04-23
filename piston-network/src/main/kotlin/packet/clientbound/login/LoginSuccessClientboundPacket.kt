package dev.sleepyswords.piston.network.packet.clientbound.login

import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writeMCString
import dev.sleepyswords.piston.network.VarNum.writeUuid
import dev.sleepyswords.piston.network.VarNum.writeVarInt
import kotlinx.io.Sink
import kotlin.uuid.Uuid

data class LoginSuccessClientboundPacket(val username: String, val uuid: Uuid) : ClientboundPacket {
    override fun encode(out: Sink) {
        out.writeUuid(uuid)
        out.writeMCString(username)
        // Some properties?
        out.writeVarInt(0)
    }

    override val opcode: Int = 0x02
}
