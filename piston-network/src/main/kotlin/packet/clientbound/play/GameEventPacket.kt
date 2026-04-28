package dev.sleepyswords.piston.network.packet.clientbound.play

import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writeBlockVertex
import dev.sleepyswords.piston.network.VarNum.writeBoolean
import dev.sleepyswords.piston.network.VarNum.writeMCString
import dev.sleepyswords.piston.network.VarNum.writeVarInt
import kotlinx.io.Sink
import kotlinx.io.writeFloat
import kotlinx.io.writeUByte

enum class GameEvent(
    val eventID: UByte,
) {
    START_WAIT_FOR_CHUNKS(13u),
}

class GameEventPacket(
    val gameEvent: GameEvent,
    val value: Float,
) : ClientboundPacket {
    override fun encode(out: Sink) {
        out.writeUByte(gameEvent.eventID)
        out.writeFloat(value)
    }

    override val opcode: Int = 0x26
}
