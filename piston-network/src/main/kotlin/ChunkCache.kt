package dev.sleepyswords.piston.network

import dev.sleepyswords.piston.event.EventBus
import dev.sleepyswords.piston.event.block.BlockUpdateEvent
import dev.sleepyswords.piston.event.world.RequestChunkEvent
import dev.sleepyswords.piston.utility.ChunkVertex
import dev.sleepyswords.piston.world.Chunk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ChunkCache {
    // Chunk will need to be replaced with a snapshot for entities and other elements
    val chunks: MutableMap<ChunkVertex, Chunk> = mutableMapOf()
    val mutex = Mutex()

    suspend fun getCached(eventBus: EventBus, chunkVertex: ChunkVertex): Chunk {
        val chunk = mutex.withLock {
            chunks[chunkVertex]
        } ?: run {
            val channel = CompletableDeferred<Chunk>()

            eventBus.emitServerBound(
                RequestChunkEvent(chunkVertex, channel)
            )
            val chunk = channel.await()
            chunks[chunkVertex] = chunk
            chunk
        }

        return chunk
    }

    suspend fun applyUpdate(blockUpdateEvent: BlockUpdateEvent) {
        val newChunk = chunks[blockUpdateEvent.position.toChunkVertex()]!!.deepClone()
        blockUpdateEvent.updateChunk(newChunk)
        mutex.withLock {
            chunks[blockUpdateEvent.position.toChunkVertex()] = newChunk
        }
    }
}
