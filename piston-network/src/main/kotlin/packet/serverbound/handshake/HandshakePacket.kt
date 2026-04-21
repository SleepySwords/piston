package dev.sleepyswords.piston.network.packet.serverbound.handshake

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.VarNum.readMCString
import dev.sleepyswords.piston.network.VarNum.readVarInt
import kotlinx.io.readString
import kotlinx.io.readUShort

data class HandshakePacket(
    val protocolVersion: Int,
    val serverAddress: String,
    val serverPort: UShort,
    val intent: Intent,
) : ServerboundPacket {
    enum class Intent(val id: Int) {
        STATUS(1), LOGIN(2), TRANSFER(3)
    }

    companion object {
        val Decoder: ClientPacketDecoder<HandshakePacket> = { buffer ->
            val protocolVersion = buffer.readVarInt()
            val serverAddress = buffer.readMCString()
            val serverPort = buffer.readUShort()
            val intentID = buffer.readVarInt()
            val intent = Intent.entries[intentID - 1]

            HandshakePacket(protocolVersion, serverAddress, serverPort, intent)
        }
    }
}