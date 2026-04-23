package dev.sleepyswords.piston.network.handler.status

import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.packet.clientbound.status.PongPacket
import dev.sleepyswords.piston.network.packet.serverbound.status.PingPacket
import dev.sleepyswords.piston.network.writeServerPacket

suspend fun handlePingPacket(ping: PingPacket, session: GameSession) {
    session.writeServerPacket(PongPacket(ping.timestamp))
}
