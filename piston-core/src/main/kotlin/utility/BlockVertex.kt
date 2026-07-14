package dev.sleepyswords.piston.utility

import dev.sleepyswords.piston.world.Chunk

data class BlockVertex(
    var x: Int = 0,
    var y: Short = 0,
    var z: Int = 0,
) {
    fun getIndex(): Int = x + z * 16 + y * 16 * 16

    fun toChunkVertex(): ChunkVertex = ChunkVertex(x.floorDiv(Chunk.CHUNK_WIDTH), z.floorDiv(Chunk.CHUNK_LENGTH))
    fun toChunkOffset(): BlockVertex = BlockVertex(x.mod(Chunk.CHUNK_WIDTH), y, z.mod(Chunk.CHUNK_LENGTH))
}
