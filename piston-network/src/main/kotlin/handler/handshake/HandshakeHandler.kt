package dev.sleepyswords.piston.network.handler.handshake

import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.GameState
import dev.sleepyswords.piston.network.packet.serverbound.handshake.HandshakePacket
import io.github.oshai.kotlinlogging.KotlinLogging

val logger = KotlinLogging.logger {}

fun handleHandshakePacket(
    packet: HandshakePacket,
    session: GameSession,
) {
    logger.debug { "Attempted handshake by player with protocol version ${packet.protocolVersion}, intent ${packet.intent}" }
    when (packet.intent) {
        HandshakePacket.Intent.STATUS -> {
            session.gameState = GameState.STATUS
        }

        HandshakePacket.Intent.LOGIN, HandshakePacket.Intent.TRANSFER -> {
            session.gameState = GameState.LOGIN
        }
    }
}
