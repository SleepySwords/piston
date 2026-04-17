package dev.sleepyswords.piston.network

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.connection
import kotlinx.coroutines.Dispatchers

class NetworkManager {
    suspend fun test() {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 9002) { }

        val socket = serverSocket.accept()
        socket.connection().input
    }
}