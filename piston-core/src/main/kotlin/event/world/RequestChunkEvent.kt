package dev.sleepyswords.piston.event.world

import dev.sleepyswords.piston.event.Event
import dev.sleepyswords.piston.utility.ChunkVertex
import dev.sleepyswords.piston.world.Chunk

data class RequestChunkEvent(
    val chunkPosition: ChunkVertex,
) : Event

data class ResponseChunkEvent(
    val chunkPosition: ChunkVertex,
    val chunk: Chunk,
) : Event
