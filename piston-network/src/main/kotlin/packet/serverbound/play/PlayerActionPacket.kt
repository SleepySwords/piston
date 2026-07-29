package dev.sleepyswords.piston.network.packet.serverbound.play

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.VarNum.readPosition
import dev.sleepyswords.piston.network.VarNum.readVarInt
import dev.sleepyswords.piston.utility.BlockVertex

data class PlayerActionPacket(
    val status: PlayerActionStatus,
    val location: BlockVertex,
    val face: Byte,
    val sequence: Int,
) : ServerboundPacket{
    enum class PlayerActionStatus {
        STARTED_DIGGING,
       	CANCELLED_DIGGING,
       	FINISHED_DIGGING,
       	DROP_ITEM_STACK,
       	DROP_ITEM,
       	SHOOT_ARROW,
       	SWAP_ITEM_IN_HAND,
    }

    companion object {
        val Decoder: ClientPacketDecoder<PlayerActionPacket> = { buffer ->
            PlayerActionPacket(
                status = PlayerActionStatus.entries[buffer.readVarInt()],
                location = buffer.readPosition(),
                face = buffer.readByte(),
                sequence = buffer.readVarInt(),
            )
        }
    }
}
