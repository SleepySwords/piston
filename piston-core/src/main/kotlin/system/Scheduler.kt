package dev.sleepyswords.piston.system

import dev.sleepyswords.piston.event.EventBuffer
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

val logger = KotlinLogging.logger {}

class Scheduler {
    val systems = mutableListOf<System>()
    val eventBuffer = EventBuffer()

    fun register(system: System) {
        systems.add(system)
    }

    private fun buildDependencyGraph(): List<System> {
        val graph = mutableMapOf<System, MutableSet<System>>()
        val inDegree = mutableMapOf<System, Int>()

        for (system in systems) {
            graph[system] = mutableSetOf()
            inDegree[system] = 0
        }

        for (before in systems) {
            for (after in systems) {
                if (before != after && before.runBefore.intersect(after.runAfter).isNotEmpty()) {
                    graph[before]!!.add(after)
                    inDegree[after] = inDegree[after]!! + 1
                }
            }
        }

        val queue = ArrayDeque<System>()
        val topologicalSort = mutableListOf<System>()
        systems.filter { inDegree[it] == 0 }
            .forEach(queue::add)

        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            topologicalSort.add(next)

            for (neighbour in graph[next]!!) {
                inDegree[neighbour] = inDegree[neighbour]!! - 1
                if (inDegree[neighbour] == 0) {
                    queue.add(neighbour)
                }
            }
        }

        if (topologicalSort.size != systems.size) {
            logger.error { "Found dependency cycle" }
        }

        return topologicalSort
    }

    suspend fun start() {
        val order = buildDependencyGraph()
        logger.info {
            "Successfully initialised system using the order " +
                    order.joinToString(", ", "[", "]") { it::class.simpleName.toString() }
        }
        for (system in order) {
            system.start()
        }

        while (true) {
            tick()
        }
    }

    suspend fun tick() {
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
}
