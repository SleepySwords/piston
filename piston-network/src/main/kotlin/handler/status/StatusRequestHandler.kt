package dev.sleepyswords.piston.network.handler.status

import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.packet.clientbound.status.StatusResponsePacket
import dev.sleepyswords.piston.network.packet.serverbound.status.StatusRequestPacket
import dev.sleepyswords.piston.network.writeServerPacket

suspend fun handleStatusRequestPacket(_packet: StatusRequestPacket, session: GameSession) {
    println("Received status request packet.")
    session.writeChannel.writeServerPacket(StatusResponsePacket("""
       {
            "version": {
                "name": "1.21.8",
                "protocol": 772
            },
            "players": {
                "max": 20,
                "online": 1,
                "sample": [
                    {
                        "name": "thinkofdeath",
                        "id": "4566e69f-c907-48ee-8d71-d7ba5aa00d20"
                    }
                ]
            },
            "description": {
                "text": "Hello, world!"
            },
            "favicon": "data:image/png;base64,<data>",
            "enforcesSecureChat": false
        }
         
    """.trimIndent()))
}