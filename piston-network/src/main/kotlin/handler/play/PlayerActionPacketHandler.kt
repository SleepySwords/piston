package dev.sleepyswords.piston.network.handler.play

import dev.sleepyswords.piston.event.EventBus
import dev.sleepyswords.piston.event.block.BreakBlockEvent
import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.packet.serverbound.play.PlayerActionPacket

suspend fun handlePlayerActionPacket(
    packet: PlayerActionPacket,
    session: GameSession,
    eventBus: EventBus,
) {
    eventBus.emitServerBound(BreakBlockEvent(packet.location))
}
