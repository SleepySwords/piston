package dev.sleepyswords.piston.network.listener.handshake

import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.GameState
import dev.sleepyswords.piston.network.packet.serverbound.handshake.HandshakePacket

fun handleHandshakePacket(packet: HandshakePacket, session: GameSession) {
    when (packet.intent) {
        HandshakePacket.Intent.STATUS -> {
            session.gameState = GameState.STATUS
        }
        HandshakePacket.Intent.LOGIN, HandshakePacket.Intent.TRANSFER ->  {

        }
    }
}