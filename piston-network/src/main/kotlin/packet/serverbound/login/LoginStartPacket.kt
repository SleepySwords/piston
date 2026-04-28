package dev.sleepyswords.piston.network.packet.serverbound.login

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.VarNum.readMCString
import dev.sleepyswords.piston.network.VarNum.readUuid
import kotlin.uuid.Uuid

data class LoginStartPacket(
    val username: String,
    val uuid: Uuid,
) : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<LoginStartPacket> = { buffer ->
            val username = buffer.readMCString()
            val uuid = buffer.readUuid()

            LoginStartPacket(username, uuid)
        }
    }
}
