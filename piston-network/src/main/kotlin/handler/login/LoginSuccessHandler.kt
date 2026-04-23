package dev.sleepyswords.piston.network.handler.login

import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.GameState
import dev.sleepyswords.piston.network.packet.serverbound.login.LoginSuccessServerboundPacket

fun handleLoginSuccessPacket(packet: LoginSuccessServerboundPacket, session: GameSession) {
    session.gameState = GameState.CONFIGURATION
}
