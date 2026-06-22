package dev.sleepyswords.piston.world

import dev.sleepyswords.piston.utility.ChunkVertex

class World(
    val generator: ChunkGenerator,
    val chunks: MutableMap<ChunkVertex, Chunk> = mutableMapOf(),
) {

    operator fun get(chunkVertex: ChunkVertex): Chunk {
        return chunks.computeIfAbsent(chunkVertex) { _ -> generator.generateChunk(chunkVertex)}
    }
}
