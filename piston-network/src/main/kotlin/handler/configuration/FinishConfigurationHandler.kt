package dev.sleepyswords.piston.network.handler.configuration

import dev.sleepyswords.piston.event.EventBus
import dev.sleepyswords.piston.event.PlayerLoginEvent
import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.GameState
import dev.sleepyswords.piston.network.chunkCache
import dev.sleepyswords.piston.network.packet.clientbound.play.ChunkDataAndUpdateLightPacket
import dev.sleepyswords.piston.network.packet.clientbound.play.GameEvent
import dev.sleepyswords.piston.network.packet.clientbound.play.GameEventPacket
import dev.sleepyswords.piston.network.packet.clientbound.play.GameMode
import dev.sleepyswords.piston.network.packet.clientbound.play.LoginPacket
import dev.sleepyswords.piston.network.packet.clientbound.play.Position
import dev.sleepyswords.piston.network.packet.clientbound.play.Rotation
import dev.sleepyswords.piston.network.packet.clientbound.play.SynchronizePlayerPosition
import dev.sleepyswords.piston.network.packet.clientbound.play.Velocity
import dev.sleepyswords.piston.network.packet.common.configuration.FinishConfigurationPacket
import dev.sleepyswords.piston.utility.ChunkVertex

suspend fun handleFinishConfigurationPacket(
    packet: FinishConfigurationPacket,
    session: GameSession,
    eventBus: EventBus,
) {
    session.gameState = GameState.PLAY

    session.writeServerPacket(
        LoginPacket(
            entityID = 0,
            isHardcore = false,
            dimensionNames = listOf(),
            maxPlayers = 10,
            viewDistance = 10,
            simulationDistance = 10,
            reducedDebugInfo = false,
            enableRespawnScreen = false,
            doLimitedCrafting = false,
            dimensionType = 0,
            dimensionName = "ok",
            hashedSeed = 0,
            gameMode = GameMode.CREATIVE,
            previousGameMode = null,
            isDebug = false,
            isFlat = false,
            deathLocation = null,
            portalCooldown = 0,
            seaLevel = 0,
            enforceSecureChat = false,
        ),
    )

    eventBus.emitServerBound(PlayerLoginEvent(session.username!!, session.uuid!!))

    session.writeServerPacket(
        SynchronizePlayerPosition(
            0,
            Position(0.0, 400.0, 0.0),
            Velocity(),
            Rotation(),
            0,
        ),
    )

    session.writeServerPacket(
        GameEventPacket(
            GameEvent.START_WAIT_FOR_CHUNKS,
            value = 0.0f,
        ),
    )

    // Instead of constantly requesting chunks, we need to perform snapshots and incrementally update them
    for (x in -5 until 5) {
        for (z in -5 until 5) {
            val position = ChunkVertex(x, z)
            // TODO: For now we are constantly awaiting, more likely we need to add a bulk request like before.
            val chunk = chunkCache.getCached(eventBus, position)
            session.writeServerPacket(
                ChunkDataAndUpdateLightPacket(
                    chunkVertex = position,
                    chunk = chunk
                )
            )
        }
    }
}
