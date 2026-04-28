package dev.sleepyswords.utils.world

import dev.sleepyswords.utils.utility.BlockVertex
import dev.sleepyswords.utils.utility.ChunkVertex

interface ChunkGenerator {
    fun generateBlock(blockVertex: BlockVertex): Block

    fun generateChunk(chunkVertex: ChunkVertex): Chunk {
        val chunk = Chunk()

        for (x in 0 until 16) {
            for (z in 0 until 16) {
                for (y in Chunk.MIN_HEIGHT until Chunk.CHUNK_HEIGHT + Chunk.MIN_HEIGHT) {
                    val vertex = BlockVertex(x + chunkVertex.x * Chunk.CHUNK_WIDTH, y.toShort(), z * Chunk.CHUNK_LENGTH)
                    chunk[x, y.toShort(), z] = generateBlock(vertex)
                }
            }
        }

        return chunk
    }
}

class TestChunkGenerator : ChunkGenerator {
    override fun generateBlock(blockVertex: BlockVertex): Block {
        if (blockVertex.x == 10 && blockVertex.z == 10) {
            return Air
        }
        return if (blockVertex.y <= 10 && blockVertex.x != 10 && blockVertex.z != 10) {
            Stone
        } else {
            Air
        }
    }
}

fun main() {
    val generator = TestChunkGenerator()
    val chunk = generator.generateChunk(ChunkVertex(0, 0))
    println(chunk[0, 0, 0])
}
