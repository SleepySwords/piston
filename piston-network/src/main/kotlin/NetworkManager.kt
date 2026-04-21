package dev.sleepyswords.piston.network

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.EOFException

private val logger = KotlinLogging.logger {}

class NetworkManager {
    fun launchTCPServer(hostname: String, port: Int) {
        runBlocking {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val server = aSocket(selectorManager).tcp().bind(hostname, port) { }

            logger.debug { "Server listening on ${hostname}:${port}" }

            while (true) {
                val socket = server.accept()
                launch {
                    val writeChannel = socket.openWriteChannel(autoFlush = true)
                    val readChannel = socket.openReadChannel()

                    val session = GameSession(GameState.HANDSHAKE, writeChannel)

                    try {
                        while (!readChannel.isClosedForRead) {
                            val (opcode, packet) = readChannel.readServerPacket()
                            ServerboundPacketRegistryCommon.handlePacket(session, opcode, packet)
                        }
                    } catch (_: EOFException) {

                    } finally {
                        socket.close()
                    }
                }
            }
        }
    }
}