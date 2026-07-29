package dev.sleepyswords.piston.block

import dev.sleepyswords.piston.utility.BlockVertex

enum class Face {
    BOTTOM,
    TOP,
    NORTH,
    SOUTH,
    WEST,
    EAST, ;

    fun blockOffset(vertex: BlockVertex): BlockVertex {
        when (this) {
            BOTTOM -> return BlockVertex(vertex.x, (vertex.y - 1).toShort(), vertex.z)
            TOP -> return BlockVertex(vertex.x, (vertex.y + 1).toShort(), vertex.z)
            NORTH -> return BlockVertex(vertex.x, vertex.y, vertex.z - 1)
            SOUTH -> return BlockVertex(vertex.x, vertex.y, vertex.z + 1)
            WEST -> return BlockVertex(vertex.x - 1, vertex.y, vertex.z)
            EAST -> return BlockVertex(vertex.x + 1, vertex.y, vertex.z)
        }
    }
}
