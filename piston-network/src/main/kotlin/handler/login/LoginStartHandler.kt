package dev.sleepyswords.piston.network.handler.login

import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.packet.clientbound.login.LoginSuccessClientboundPacket
import dev.sleepyswords.piston.network.handler.handshake.logger
import dev.sleepyswords.piston.network.packet.common.configuration.FinishConfigurationPacket
import dev.sleepyswords.piston.network.packet.serverbound.login.LoginStartPacket
import dev.sleepyswords.piston.network.writeServerPacket

suspend fun handleLoginStartPacket(packet: LoginStartPacket, session: GameSession) {
    logger.info { "${packet.username} with UUID ${packet.uuid} has been successfully logged in." }

    session.writeServerPacket(LoginSuccessClientboundPacket(packet.username, packet.uuid))
}
