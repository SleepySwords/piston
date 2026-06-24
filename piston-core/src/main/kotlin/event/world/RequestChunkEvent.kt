package dev.sleepyswords.piston.event.world

import dev.sleepyswords.piston.event.Event
import dev.sleepyswords.piston.utility.ChunkVertex
import dev.sleepyswords.piston.world.Chunk
import kotlinx.coroutines.CompletableDeferred

data class RequestChunkEvent(
    val chunkPosition: ChunkVertex,
    val completableDeferred: CompletableDeferred<Chunk>,
) : Event
