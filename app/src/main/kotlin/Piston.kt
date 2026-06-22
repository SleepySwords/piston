package dev.sleepyswords.piston

import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.system.System
import dev.sleepyswords.piston.world.NoiseGenerator3D
import dev.sleepyswords.piston.world.World
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

fun main() =
    runBlocking {
        println("Starting Piston server")

        val systems = mutableListOf<System>()

        val world = World(
            generator = NoiseGenerator3D(),
        )

        systems.add(TCPSystem())
        systems.add(MOTDSystem())
        systems.add(ChunkManagementSystem(world))

        val eventBuffer = EventBuffer()

        for (system in systems) {
            system.start()
        }

        while (true) {
            for (system in systems) {
                system.update(eventBuffer)
            }

            val postEvents = eventBuffer.drainAll()

            for (system in systems) {
                system.postUpdate(postEvents)
            }

            delay(100.milliseconds)
        }
    }
