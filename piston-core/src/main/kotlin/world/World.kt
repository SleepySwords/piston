package dev.sleepyswords.piston.world

import dev.sleepyswords.piston.block.BlockState
import dev.sleepyswords.piston.utility.BlockVertex
import dev.sleepyswords.piston.utility.ChunkVertex

class World(
    val generator: ChunkGenerator,
    val chunks: MutableMap<ChunkVertex, Chunk> = mutableMapOf(),
) {

    operator fun get(chunkVertex: ChunkVertex): Chunk {
        return chunks.computeIfAbsent(chunkVertex) { _ -> generator.generateChunk(chunkVertex)}
    }

    operator fun get(blockVertex: BlockVertex): BlockState {
        return get(blockVertex.toChunkVertex())[blockVertex.toChunkOffset()]
    }

    operator fun set(blockVertex: BlockVertex, blockState: BlockState) {
        get(blockVertex.toChunkVertex())[blockVertex.toChunkOffset()] = blockState
    }
}
