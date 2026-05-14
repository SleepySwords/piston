package dev.sleepyswords.piston.network

import dev.sleepyswords.piston.event.EventBus
import dev.sleepyswords.piston.network.handler.configuration.handleClientInformationPacket
import dev.sleepyswords.piston.network.handler.configuration.handleFinishConfigurationPacket
import dev.sleepyswords.piston.network.handler.handshake.handleHandshakePacket
import dev.sleepyswords.piston.network.handler.login.handleLoginStartPacket
import dev.sleepyswords.piston.network.handler.login.handleLoginSuccessPacket
import dev.sleepyswords.piston.network.handler.status.handlePingPacket
import dev.sleepyswords.piston.network.handler.status.handleStatusRequestPacket
import dev.sleepyswords.piston.network.packet.common.configuration.FinishConfigurationPacket
import dev.sleepyswords.piston.network.packet.common.configuration.PluginMessagePacket
import dev.sleepyswords.piston.network.packet.serverbound.BundleItemSelectedPacket
import dev.sleepyswords.piston.network.packet.serverbound.configuration.ClientInformationPacket
import dev.sleepyswords.piston.network.packet.serverbound.handshake.HandshakePacket
import dev.sleepyswords.piston.network.packet.serverbound.login.ConfirmTeleportationPacket
import dev.sleepyswords.piston.network.packet.serverbound.login.LoginStartPacket
import dev.sleepyswords.piston.network.packet.serverbound.login.LoginSuccessServerboundPacket
import dev.sleepyswords.piston.network.packet.serverbound.status.PingPacket
import dev.sleepyswords.piston.network.packet.serverbound.status.StatusRequestPacket
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.io.Source
import kotlin.arrayOfNulls

private val logger = KotlinLogging.logger {}

enum class GameState(
    val id: Int,
) {
    HANDSHAKE(0),
    STATUS(1),
    LOGIN(2),
    CONFIGURATION(3),
    PLAY(4),
}

data class GameSession(
    var gameState: GameState,
    val writeChannel: ByteWriteChannel,
    var username: String? = null,
) {
    suspend fun writeServerPacket(packet: ClientboundPacket) {
        logger.info { "Writing server packet $packet" }
        writeChannel.writeServerPacket(packet)
    }
}

open class ServerboundPacketRegistry {
    private val registry =
        Array(GameState.entries.size) {
            arrayOfNulls<suspend (Source, GameSession, EventBus) -> Unit>(256)
        }

    fun <T : ServerboundPacket> register(
        gameState: GameState,
        opcode: Int,
        decoder: ClientPacketDecoder<T>,
        handler: suspend (T, GameSession, EventBus) -> Unit,
    ) {
        registry[gameState.id][opcode] = { source, session, eventBus ->
            val packet = decoder.decode(source)
            logger.debug { "Received packet $packet" }
            handler(packet, session, eventBus)
        }
    }

    fun <T : ServerboundPacket> register(
        gameState: GameState,
        opcode: Int,
        decoder: ClientPacketDecoder<T>,
        handler: suspend (T, GameSession) -> Unit,
    ) {
        registry[gameState.id][opcode] = { source, session, eventBus ->
            val packet = decoder.decode(source)
            logger.debug { "Received packet $packet" }
            handler(packet, session)
        }
    }

    suspend fun handlePacket(
        gameSession: GameSession,
        eventBus: EventBus,
        opcode: Int,
        source: Source,
    ) {
        val handler = registry[gameSession.gameState.id][opcode]
        if (handler == null) {
//            logger.error { "Received unknown opcode $opcode from ${gameSession.gameState}" }
            return
        }
        handler(source, gameSession, eventBus)
    }
}

fun handlePrintPacket(
    packet: ServerboundPacket,
    gameSession: GameSession,
) {
    logger.debug { "Unhandled packet: $packet" }
}

object ServerboundPacketRegistryCommon : ServerboundPacketRegistry() {
    init {
        register(GameState.HANDSHAKE, 0x00, HandshakePacket.Decoder, ::handleHandshakePacket)

        register(GameState.STATUS, 0x00, StatusRequestPacket.Decoder, ::handleStatusRequestPacket)
        register(GameState.STATUS, 0x01, PingPacket.Decoder, ::handlePingPacket)

        register(GameState.LOGIN, 0x00, LoginStartPacket.Decoder, ::handleLoginStartPacket)
        register(GameState.LOGIN, 0x03, LoginSuccessServerboundPacket.Decoder, ::handleLoginSuccessPacket)

        register(GameState.CONFIGURATION, 0x02, PluginMessagePacket.Decoder, ::handlePrintPacket)
        register(GameState.CONFIGURATION, 0x00, ClientInformationPacket.Decoder, ::handleClientInformationPacket)
        register(GameState.CONFIGURATION, 0x03, FinishConfigurationPacket.Decoder, ::handleFinishConfigurationPacket)

        register(GameState.PLAY, 0x00, ConfirmTeleportationPacket.Decoder, ::handlePrintPacket)
        register(GameState.PLAY, 0x02, BundleItemSelectedPacket.Decoder, ::handlePrintPacket)
    }
}

object ServerboundPacketRegistryV771 : ServerboundPacketRegistry() {
    init {}
}
