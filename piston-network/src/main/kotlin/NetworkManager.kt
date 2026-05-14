package dev.sleepyswords.piston.network

import dev.sleepyswords.piston.event.EventBus
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.EOFException

private val logger = KotlinLogging.logger {}

class NetworkManager {
    suspend fun launchTCPServer(
        hostname: String,
        port: Int,
        eventBus: EventBus,
    ) {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val server = aSocket(selectorManager).tcp().bind(hostname, port) { }

        logger.info { "Server listening on $hostname:$port" }

        while (true) {
            val socket = server.accept()
            val writeChannel = socket.openWriteChannel(autoFlush = true)
            val readChannel = socket.openReadChannel()

            val session = GameSession(gameState = GameState.HANDSHAKE, writeChannel = writeChannel)

            try {
                while (!readChannel.isClosedForRead) {
                    val (opcode, packet) = readChannel.readServerPacket()
                    ServerboundPacketRegistryCommon.handlePacket(session, eventBus, opcode, packet)
                }
            } catch (_: EOFException) {
            } finally {
                withContext(Dispatchers.IO) {
                    socket.close()
                }
            }
        }
    }
}
