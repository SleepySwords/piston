package dev.sleepyswords.piston

import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.network.TCPSystem
import dev.sleepyswords.piston.system.ChunkManagementSystem
import dev.sleepyswords.piston.system.MOTDSystem
import dev.sleepyswords.piston.system.RedstonePlacementSystem
import dev.sleepyswords.piston.system.Scheduler
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

        val scheduler = Scheduler()

        val world = World(
            generator = NoiseGenerator3D(),
        )

        scheduler.register(system = TCPSystem())
        scheduler.register(system = MOTDSystem())
        scheduler.register(system = ChunkManagementSystem(world))
        scheduler.register(system = RedstonePlacementSystem(world))

        scheduler.start()
    }
