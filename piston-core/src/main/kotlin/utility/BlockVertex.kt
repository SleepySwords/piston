package dev.sleepyswords.piston.utility

import dev.sleepyswords.piston.world.Chunk

data class BlockVertex(
    var x: Int = 0,
    var y: Short = 0,
    var z: Int = 0,
) {
    fun getIndex(): Int = x + z * 16 + y * 16 * 16

    fun toChunkVertex(): ChunkVertex = ChunkVertex(
        x = x.floorDiv(Chunk.CHUNK_WIDTH),
        z = z.floorDiv(Chunk.CHUNK_LENGTH)
    )
    fun toChunkOffset(): BlockVertex = BlockVertex(
        x = x.mod(Chunk.CHUNK_WIDTH),
        y,
        z = z.mod(Chunk.CHUNK_LENGTH)
    )

    operator fun plus(vertex: BlockVertex): BlockVertex = BlockVertex(
        x = vertex.x + x,
        y = (vertex.y + y).toShort(),
        z = vertex.z + z,
    )
}
