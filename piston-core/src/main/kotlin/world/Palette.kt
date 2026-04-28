package dev.sleepyswords.utils.world

import dev.sleepyswords.utils.utility.AlignedBitPackedArray
import dev.sleepyswords.utils.utility.BitPackedArray

sealed class PaletteStrategy<T> {
    abstract val bitsPerEntry: UByte

    class SingleValued<T>(val value: T) : PaletteStrategy<T>() {
        override val bitsPerEntry: UByte = 0u
    }

    class Indirect<T>(
        var blocks: BitPackedArray = AlignedBitPackedArray(4u, 16*16*16),
        val palette: MutableList<T>
    ) : PaletteStrategy<T>() {
        operator fun get(entry: Int) : T {
            return palette[blocks[entry]]
        }

        fun increaseCapacity(newBitsPerBlock: UByte) {
            blocks.increaseCapacity(newBitsPerBlock)
        }

        override val bitsPerEntry: UByte
            get() = blocks.bitsPerEntry
    }

    class Direct<T>(val blocks: MutableList<T>) : PaletteStrategy<T>() {
        override val bitsPerEntry: UByte = 15u
    }
}

class Palette<T>(var paletteStrategy: PaletteStrategy<T>) {
    constructor(defaultValue: T): this(PaletteStrategy.SingleValued(defaultValue))

    val bitsPerEntry: UByte
        get() = paletteStrategy.bitsPerEntry

    operator fun get(entry: Int): T {
        return when (val currentStrategy = paletteStrategy) {
            is PaletteStrategy.SingleValued -> currentStrategy.value
            is PaletteStrategy.Indirect -> currentStrategy.palette[currentStrategy.blocks[entry]]
            is PaletteStrategy.Direct -> currentStrategy.blocks[entry]
        }
    }

    operator fun set(entry: Int, block: T) {
        // Not thread safe
        when (val currentStrategy = paletteStrategy) {
            is PaletteStrategy.Direct -> {
                currentStrategy.blocks[entry] = block
            }
            is PaletteStrategy.Indirect -> {
                val index = if (currentStrategy.palette.indexOf(block) != -1) {
                    currentStrategy.palette.indexOf(block)
                } else {
                    if (currentStrategy.palette.size >= (1 shl currentStrategy.bitsPerEntry.toInt())) {
                        // We must promote to a direct palette
                        if (currentStrategy.bitsPerEntry == 14.toUByte()) {
                            val blocks = MutableList(
                                Chunk.CHUNK_WIDTH * Chunk.CHUNK_LENGTH * Chunk.CHUNK_SECTION_HEIGHT) {
                                i -> currentStrategy[i]
                            }
                            val directStrategy = PaletteStrategy.Direct(blocks = blocks)
                            directStrategy.blocks[entry] = block
                            paletteStrategy = directStrategy
                        } else {
                            currentStrategy.increaseCapacity((currentStrategy.bitsPerEntry + 1u).toUByte())
                        }
                    }
                    currentStrategy.palette.add(block)
                    currentStrategy.palette.size - 1
                }
                currentStrategy.blocks.setEntry(entry, index)
            }
            is PaletteStrategy.SingleValued -> {
                if (currentStrategy.value != block) {
                    val strategy = PaletteStrategy.Indirect(
                        palette = mutableListOf(
                            currentStrategy.value, block
                        )
                    )
                    strategy.blocks.setEntry(entry, 1)
                    paletteStrategy = strategy
                }
            }
        }
    }
}