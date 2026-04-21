package dev.sleepyswords.piston.network

import dev.sleepyswords.piston.network.handler.status.handleStatusRequestPacket
import dev.sleepyswords.piston.network.listener.handshake.handleHandshakePacket
import dev.sleepyswords.piston.network.packet.serverbound.handshake.HandshakePacket
import dev.sleepyswords.piston.network.packet.serverbound.login.LoginPacket
import dev.sleepyswords.piston.network.packet.serverbound.status.StatusRequestPacket
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.io.Source
import kotlin.arrayOfNulls

private val logger = KotlinLogging.logger {}

enum class GameState(val id: Int) {
    HANDSHAKE(0), STATUS(1), LOGIN(2), PLAY(3)
}

data class GameSession(var gameState: GameState, val writeChannel: ByteWriteChannel)

open class ServerboundPacketRegistry {
    private val registry = Array(GameState.entries.size) {
        arrayOfNulls<suspend (Source, GameSession) -> Unit>(256)
    }

    fun <T: ServerboundPacket> register(
        gameState: GameState,
        opcode: Int,
        decoder: ClientPacketDecoder<T>,
        handler: suspend (T, GameSession) -> Unit
    ) {
        registry[gameState.id][opcode] = { source, session -> handler(decoder.decode(source), session) }
    }

    suspend fun handlePacket(gameSession: GameSession, opcode: Int, source: Source) {
        val handler = registry[gameSession.gameState.id][opcode]
        if (handler == null) {
            logger.error { "Received unknown opcode $opcode from ${gameSession.gameState}" }
            return
        }
        handler(source, gameSession)
    }
}

object ServerboundPacketRegistryCommon : ServerboundPacketRegistry() {
    init {
        register(GameState.HANDSHAKE, 0x00, HandshakePacket.Decoder, ::handleHandshakePacket)

        register(GameState.STATUS, 0x00, StatusRequestPacket.Decoder, ::handleStatusRequestPacket)
    }
}

object ServerboundPacketRegistryV771 : ServerboundPacketRegistry() {
    init {
        register(GameState.HANDSHAKE, 0x00, LoginPacket.Decoder) { packet,_ -> println(packet.num) }

        register(GameState.STATUS, 0x00, LoginPacket.Decoder) { packet,_ -> println(packet.num) }

        register(GameState.LOGIN, 0x00, LoginPacket.Decoder) { packet,_ -> println(packet.num) }
    }
}