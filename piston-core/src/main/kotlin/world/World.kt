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
        val (x,y,z) = blockVertex
        return get(blockVertex.toChunkVertex())[x.mod(Chunk.CHUNK_WIDTH), y, z.mod(Chunk.CHUNK_LENGTH)]
    }

    operator fun set(blockVertex: BlockVertex, blockState: BlockState) {
        val (x,y,z) = blockVertex
        get(blockVertex.toChunkVertex())[x.mod(Chunk.CHUNK_WIDTH), y, z.mod(Chunk.CHUNK_LENGTH)] = blockState
    }
}
