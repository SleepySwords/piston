package dev.sleepyswords.piston

import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.event.world.RequestChunkEvent
import dev.sleepyswords.piston.event.world.ResponseChunkEvent
import dev.sleepyswords.piston.system.System
import dev.sleepyswords.piston.world.World

class ChunkManagementSystem(
    val world: World,
) : System {
    override fun start() {}

    override fun update(eventBuffer: EventBuffer) {
        val requestChunks = eventBuffer.drain<RequestChunkEvent>();

        requestChunks.map { ResponseChunkEvent(
            chunkPosition = it.chunkPosition,
            chunk = world[it.chunkPosition].deepClone())
        }.forEach(eventBuffer::emit)
    }
}
