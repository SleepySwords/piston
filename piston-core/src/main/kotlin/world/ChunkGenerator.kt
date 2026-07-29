package dev.sleepyswords.piston.world

import dev.sleepyswords.piston.block.Air
import dev.sleepyswords.piston.block.BlockState
import dev.sleepyswords.piston.block.Stone
import dev.sleepyswords.piston.noise.Noise
import dev.sleepyswords.piston.utility.BlockVertex
import dev.sleepyswords.piston.utility.ChunkVertex

interface ChunkGenerator {
    fun generateBlock(blockVertex: BlockVertex): BlockState

    fun generateChunk(chunkVertex: ChunkVertex): Chunk {
        val chunk = Chunk()

        for (x in 0 until 16) {
            for (z in 0 until 16) {
                for (y in Chunk.MIN_HEIGHT until Chunk.CHUNK_HEIGHT + Chunk.MIN_HEIGHT) {
                    val vertex = BlockVertex(x + chunkVertex.x * Chunk.CHUNK_WIDTH, y.toShort(), z + chunkVertex.z * Chunk.CHUNK_LENGTH)
                    chunk[x, y.toShort(), z] = generateBlock(vertex)
                }
            }
        }

        return chunk
    }
}

class TestChunkGenerator : ChunkGenerator {
    override fun generateBlock(blockVertex: BlockVertex): BlockState {
        if (blockVertex.x == 10 && blockVertex.z == 10) {
            Stone.DEFAULT_STATE
        }
        return if (blockVertex.y <= 10 && blockVertex.x != 10 && blockVertex.z != 10) {
            Stone.DEFAULT_STATE
        } else {
            Air.DEFAULT_STATE
        }
    }
}

const val X_PERIOD = 64.0
const val Y_PERIOD = 64.0
const val Z_PERIOD = 64.0

class NoiseGenerator : ChunkGenerator {
    override fun generateBlock(blockVertex: BlockVertex
    ): BlockState {
        val noise: Double =
            Noise.octavePerlin(
                x = blockVertex.x / X_PERIOD,
                y = 0.0,
                z = blockVertex.z / Z_PERIOD,
                octaves = 6,
                persistence = 0.2
            )

        return if (blockVertex.y < (noise * 64 + 90).toInt()) {
            Stone.DEFAULT_STATE
        } else {
            Air.DEFAULT_STATE
        }
    }
}

class NoiseGenerator3D : ChunkGenerator {
    override fun generateBlock(blockVertex: BlockVertex): BlockState {
        val noise: Double =
            Noise.octavePerlin(
                x = blockVertex.x / X_PERIOD,
                y = blockVertex.y / Y_PERIOD,
                z = blockVertex.z / Z_PERIOD,
                octaves = 6,
                persistence = 0.2
            )

        return if (0.0 < noise) {
            Stone.DEFAULT_STATE
        } else {
            Air.DEFAULT_STATE
        }
    }
}

fun main() {
    val generator = TestChunkGenerator()
    val chunk = generator.generateChunk(ChunkVertex(0, 0))
    println(chunk[0, 0, 0])
}
