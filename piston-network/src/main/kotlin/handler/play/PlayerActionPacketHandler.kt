package dev.sleepyswords.piston.network.handler.play

import dev.sleepyswords.piston.event.EventBus
import dev.sleepyswords.piston.event.block.BreakBlockEvent
import dev.sleepyswords.piston.event.block.StartBreakBlockEvent
import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.packet.clientbound.play.BlockChangedAck
import dev.sleepyswords.piston.network.packet.serverbound.play.PlayerActionPacket

suspend fun handlePlayerActionPacket(
    packet: PlayerActionPacket,
    session: GameSession,
    eventBus: EventBus,
) {
    val event = when (packet.status) {
        PlayerActionPacket.PlayerActionStatus.STARTED_DIGGING -> StartBreakBlockEvent(packet.location)
        PlayerActionPacket.PlayerActionStatus.FINISHED_DIGGING -> BreakBlockEvent(packet.location)
        else -> return
    }
    eventBus.emitServerBound(event)

    // FIXME: This is actually a sync risk, we should generate an event that is sent to the server.
    // If directly after this event a block change is issued, the updated block may change what is displayed on the client
    session.writeServerPacket(BlockChangedAck(packet.sequence))
}
