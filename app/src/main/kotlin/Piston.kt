package dev.sleepyswords.piston

import dev.sleepyswords.piston.network.NetworkManager
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("Starting Piston server")
    NetworkManager().launchTCPServer("127.0.0.1", 25565)
}
