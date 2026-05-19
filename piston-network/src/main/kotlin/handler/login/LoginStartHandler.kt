package dev.sleepyswords.piston.network.handler.login

import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.packet.clientbound.login.LoginSuccessClientboundPacket
import dev.sleepyswords.piston.network.packet.serverbound.login.LoginStartPacket

suspend fun handleLoginStartPacket(
    packet: LoginStartPacket,
    session: GameSession,
) {
    session.username = packet.username
    session.uuid = packet.uuid
    session.writeServerPacket(LoginSuccessClientboundPacket(packet.username, packet.uuid))
}
