package dev.sleepyswords.piston.network

import dev.sleepyswords.nbt.compoundTag
import dev.sleepyswords.nbt.stringTag
import dev.sleepyswords.piston.event.Event
import dev.sleepyswords.piston.event.EventBus
import dev.sleepyswords.piston.network.packet.clientbound.play.SystemChatMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.io.EOFException
import kotlin.uuid.Uuid

private val logger = KotlinLogging.logger {}
data class ChatMessageEvent(val message: String, val player: Uuid): Event

class NetworkManager {
    suspend fun launchTCPServer(
        hostname: String,
        port: Int,
        eventBus: EventBus,
    ) {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val server = aSocket(selectorManager).tcp().bind(hostname, port) { }

        val sessions = mutableListOf<GameSession>()
        val sessionMutex = Mutex()

        logger.info { "Server listening on $hostname:$port" }

        coroutineScope {
            this.launch {
                select {
                    eventBus.clientBoundEvents.onReceive { event ->
                        if (event is ChatMessageEvent) {
                            sessionMutex.withLock {
                                val session = sessions.find { it.uuid == event.player }
                                if (session != null) {
                                    session.writeServerPacket(SystemChatMessage(
                                        content = compoundTag {
                                            "text" - stringTag(event.message)
                                        },
                                        overlay = false
                                    ))
                                }
                            }
                        }
                    }
                }
            }
            while (true) {
                val socket = server.accept()

                this.launch {
                    val writeChannel = socket.openWriteChannel(autoFlush = true)
                    val readChannel = socket.openReadChannel()

                    val session = GameSession(gameState = GameState.HANDSHAKE, writeChannel = writeChannel)

                    sessionMutex.withLock {
                        sessions.add(session)
                    }
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

                        sessionMutex.withLock {
                            sessions.remove(session)
                        }
                    }
                }
            }
        }
    }
}
