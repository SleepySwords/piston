package dev.sleepyswords.piston.network.handler.play

import dev.sleepyswords.piston.event.EventBus
import dev.sleepyswords.piston.event.block.UseItemOnEvent
import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.packet.clientbound.play.BlockChangedAck
import dev.sleepyswords.piston.network.packet.serverbound.play.UseItemOnPacket

suspend fun handleUseItemOnPacket(
    packet: UseItemOnPacket,
    session: GameSession,
    eventBus: EventBus,
) {
    eventBus.emitServerBound(UseItemOnEvent(
        hand = packet.hand,
        position = packet.position,
        face = packet.face,
        cursorPositionX = packet.cursorPositionX,
        cursorPositionY = packet.cursorPositionY,
        cursorPositionZ = packet.cursorPositionZ,
        insideBlock = packet.insideBlock,
        worldBorderHit = packet.worldBorderHit,
        sequence = packet.sequence,
    ))

    // FIXME: This is actually a sync risk, we should generate an event that is sent to the server.
    // If directly after this event a block change is issued, the updated block may change what is displayed on the client
    session.writeServerPacket(BlockChangedAck(packet.sequence))
}
