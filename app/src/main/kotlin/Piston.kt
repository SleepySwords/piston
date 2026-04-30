package dev.sleepyswords.piston

import dev.sleepyswords.piston.network.NetworkManager
import dev.sleepyswords.piston.network.handler.configuration.PreGeneratedChunks
import dev.sleepyswords.piston.network.packet.clientbound.play.Light
import kotlinx.coroutines.runBlocking

fun main() =
    runBlocking {
        println("Starting Piston server")
        NetworkManager().launchTCPServer("127.0.0.1", 25565)
    }
