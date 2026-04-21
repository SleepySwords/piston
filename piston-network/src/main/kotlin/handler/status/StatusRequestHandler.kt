package dev.sleepyswords.piston.network.handler.status

import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.packet.clientbound.status.PlayerSample
import dev.sleepyswords.piston.network.packet.clientbound.status.Players
import dev.sleepyswords.piston.network.packet.clientbound.status.StatusResponse
import dev.sleepyswords.piston.network.packet.clientbound.status.StatusResponsePacket
import dev.sleepyswords.piston.network.packet.clientbound.status.Version
import dev.sleepyswords.piston.network.packet.clientbound.status.text
import dev.sleepyswords.piston.network.packet.serverbound.status.StatusRequestPacket
import dev.sleepyswords.piston.network.writeServerPacket

suspend fun handleStatusRequestPacket(_packet: StatusRequestPacket, session: GameSession) {
    println("Received status request packet.")
    session.writeChannel.writeServerPacket(
        StatusResponsePacket(
            StatusResponse(
                version = Version(name = "1.21.8", protocol = 772),
                players = Players(
                    max = 20,
                    online = 4,
                    sample = listOf(
                        PlayerSample(
                            name = "thinkofdeath",
                            id = "4566e69f-c907-48ee-8d71-d7ba5aa00d20",
                        ),
                    ),
                ),
                description = text("Hello, world!") { bold = true; italic = true },
                favicon = "data:image/png;base64,<data>",
                enforcesSecureChat = false,
            ),
        ),
    )
}