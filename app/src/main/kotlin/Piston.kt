package dev.sleepyswords.piston

import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.network.TCPSystem
import dev.sleepyswords.piston.system.ChunkManagementSystem
import dev.sleepyswords.piston.system.MOTDSystem
import dev.sleepyswords.piston.system.RedstonePlacementSystem
import dev.sleepyswords.piston.system.System
import dev.sleepyswords.piston.world.NoiseGenerator3D
import dev.sleepyswords.piston.world.World
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

private val logger = KotlinLogging.logger {}

fun main() =
    runBlocking {
        logger.info { "Starting Piston server" }

        val systems = mutableListOf<System>()

        val world = World(
            generator = NoiseGenerator3D(),
        )

        systems.add(TCPSystem())
        systems.add(MOTDSystem())
        systems.add(ChunkManagementSystem(world))
        systems.add(RedstonePlacementSystem(world))

        val eventBuffer = EventBuffer()

        for (system in systems) {
            system.start()
        }

        val clock = TimeSource.Monotonic
        var ticks = 0
        var currentTime = clock.markNow()
        while (true) {
            for (system in systems) {
                system.update(eventBuffer)
            }

            val postEvents = eventBuffer.drainAll()

            for (system in systems) {
                system.postUpdate(postEvents)
            }

            delay(10.milliseconds)

            ticks += 1
            if (currentTime.elapsedNow() >= 1.seconds) {
                logger.info {
                    "TPS: ${(ticks * 1.0f / currentTime.elapsedNow().inWholeNanoseconds) * (1.seconds / 1.nanoseconds)}"
                }
                currentTime = clock.markNow()
                ticks = 0
            }
        }
    }
