package dev.sleepyswords.piston.network.packet.serverbound.play

import dev.sleepyswords.piston.block.Face
import dev.sleepyswords.piston.entity.Hand
import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.VarNum.readFace
import dev.sleepyswords.piston.network.VarNum.readHand
import dev.sleepyswords.piston.network.VarNum.readPosition
import dev.sleepyswords.piston.network.VarNum.readVarInt
import dev.sleepyswords.piston.utility.BlockVertex
import kotlinx.io.readFloat

data class UseItemOnPacket(
    val hand: Hand,
    val position: BlockVertex,
    val face: Face,
    val cursorPositionX: Float,
    val cursorPositionY: Float,
    val cursorPositionZ: Float,
    val insideBlock: Boolean,
    val worldBorderHit: Boolean,
    val sequence: Int,
) : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<UseItemOnPacket> = { buffer ->
            val hand = buffer.readHand()
            val position = buffer.readPosition()
            val face = buffer.readFace()
            val cursorPositionX = buffer.readFloat()
            val cursorPositionY = buffer.readFloat()
            val cursorPositionZ = buffer.readFloat()
            val insideBlock = buffer.readByte() != 0.toByte()
            val worldBorderHit = buffer.readByte() != 0.toByte()
            val sequence = buffer.readVarInt()
            UseItemOnPacket(
                hand = hand,
                position = position,
                face = face,
                cursorPositionX = cursorPositionX,
                cursorPositionY = cursorPositionY,
                cursorPositionZ = cursorPositionZ,
                insideBlock = insideBlock,
                worldBorderHit = worldBorderHit,
                sequence = sequence,
            )
        }
    }
}
