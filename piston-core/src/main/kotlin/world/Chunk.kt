package dev.sleepyswords.piston.world

import dev.sleepyswords.piston.Utility
import dev.sleepyswords.piston.block.Air
import dev.sleepyswords.piston.block.AirState
import dev.sleepyswords.piston.block.Block
import dev.sleepyswords.piston.block.BlockRegistry
import dev.sleepyswords.piston.block.BlockState
import dev.sleepyswords.piston.utility.BlockVertex

class ChunkSection(
    val blockPalette: Palette<BlockState>,
    var blockCount: Short,
) {
    constructor() : this(Palette(BlockRegistry.defaultBlockState<AirState>()), 0)

    operator fun set(
        x: Int,
        y: Int,
        z: Int,
        block: BlockState,
    ) {
        val old = get(x, y, z)
        if (old.definition.isAir != block.definition.isAir) {
            blockCount = (blockCount + if (block.definition.isAir) -1 else 1).toShort()
        }
        blockPalette[Chunk.getIndex(x, y, z)] = block
    }

    operator fun get(
        x: Int,
        y: Int,
        z: Int,
    ): BlockState = blockPalette[Chunk.getIndex(x, y, z)]
}

class Chunk {
    val chunkSections: List<ChunkSection>
    val heightmap: ShortArray = ShortArray(CHUNK_WIDTH * CHUNK_LENGTH)

    init {
        val noSections = Utility.ceilDiv(CHUNK_HEIGHT, CHUNK_SECTION_HEIGHT)
        chunkSections = (0 until noSections).map { ChunkSection() }.toList()
    }

    operator fun set(
        blockVertex: BlockVertex,
        block: BlockState,
    ) {
        set(blockVertex.x, blockVertex.y, blockVertex.z, block)
    }

    operator fun set(
        x: Int,
        y: Short,
        z: Int,
        block: BlockState,
    ) {
        val chunkIndex = (y - MIN_HEIGHT) / CHUNK_SECTION_HEIGHT
        val chunkOffset = (y - MIN_HEIGHT) % CHUNK_SECTION_HEIGHT
        chunkSections[chunkIndex][x, chunkOffset, z] = block
    }

    operator fun get(blockVertex: BlockVertex): BlockState = get(blockVertex.x, blockVertex.y, blockVertex.z)

    operator fun get(
        x: Int,
        y: Short,
        z: Int,
    ): BlockState {
        val chunkIndex = (y - MIN_HEIGHT) / CHUNK_SECTION_HEIGHT
        val chunkOffset = (y - MIN_HEIGHT) % CHUNK_SECTION_HEIGHT
        return chunkSections[chunkIndex][x, chunkOffset, z]
    }

    companion object {
        const val CHUNK_WIDTH = 16
        const val CHUNK_LENGTH = 16
        const val CHUNK_SECTION_HEIGHT = 16

        // These should not be set in stone and be part of the dimension
        const val CHUNK_HEIGHT = 384
        const val MIN_HEIGHT = -64

        fun getIndex(
            x: Int,
            y: Int,
            z: Int,
        ): Int = x + z * CHUNK_WIDTH + y * CHUNK_WIDTH * CHUNK_LENGTH
    }
}
