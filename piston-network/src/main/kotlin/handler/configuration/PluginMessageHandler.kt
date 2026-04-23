package dev.sleepyswords.piston.network.handler.configuration

import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.packet.common.configuration.FinishConfigurationPacket
import dev.sleepyswords.piston.network.packet.common.configuration.PluginMessagePacket

suspend fun handlePluginMessage(packet: PluginMessagePacket, session: GameSession) {
    session.writeServerPacket(FinishConfigurationPacket())
}
