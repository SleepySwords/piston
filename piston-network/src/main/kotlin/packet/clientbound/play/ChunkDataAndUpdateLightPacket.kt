package dev.sleepyswords.piston.network.packet.clientbound.play

import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writeVarInt
import dev.sleepyswords.utils.utility.BitSet
import dev.sleepyswords.utils.utility.ChunkVertex
import dev.sleepyswords.utils.world.Chunk
import dev.sleepyswords.utils.world.PaletteStrategy
import io.ktor.utils.io.core.writePacket
import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.writeUByte

fun encodeBlockData(
    out: Sink,
    chunk: Chunk,
) {
    chunk.chunkSections.forEach { section ->
        out.writeShort(section.blockCount)

        out.writeUByte(section.blockPalette.bitsPerEntry)
        when (val strategy = section.blockPalette.paletteStrategy) {
            is PaletteStrategy.SingleValued -> {
                out.writeVarInt(strategy.value.getPhysicalBlock())
            }

            is PaletteStrategy.Direct -> {
                TODO("Actually write")
            }

            is PaletteStrategy.Indirect -> {
                out.writeVarInt(strategy.palette.size)
                strategy.palette.forEach { block -> out.writeVarInt(block.getPhysicalBlock()) }
                strategy.blocks.packed.forEach(out::writeLong)
            }
        }

        // Biome data
        // TODO: properly write this...
        out.writeUByte(0u)
        out.writeVarInt(0)
    }
}

object Light {
    @OptIn(ExperimentalUnsignedTypes::class)
    val lightBuffer = LongArray(256) { _ -> 0L.inv() }

    val skyLight = BitSet(26)
    val blockLight = BitSet(26)

    val emptySkyLight = BitSet(26)
    val emptyBlockLight = BitSet(26)

    init {
        for (i in 0 until 26) {
            skyLight[i] = true
            blockLight[i] = true
        }
    }
}

// Palette will be blocks which are then mapped to their immutable counterpart.
class ChunkDataAndUpdateLightPacket(
    val chunkVertex: ChunkVertex,
    val chunk: Chunk,
) : ClientboundPacket {
    override fun encode(out: Sink) {
        out.writeInt(chunkVertex.x)
        out.writeInt(chunkVertex.z)

        out.writeVarInt(0)
        // Encode the chunks
        val buf = Buffer()
        encodeBlockData(buf, chunk)
        out.writeVarInt(buf.size.toInt())
        out.writePacket(buf)

        out.writeVarInt(0)

        out.writeVarInt(Light.skyLight.backingArray.size)
        Light.skyLight.backingArray.forEach(out::writeLong)

        out.writeVarInt(Light.blockLight.backingArray.size)
        Light.blockLight.backingArray.forEach(out::writeLong)

        out.writeVarInt(Light.emptySkyLight.backingArray.size)
        Light.emptySkyLight.backingArray.forEach(out::writeLong)

        out.writeVarInt(Light.emptyBlockLight.backingArray.size)
        Light.emptyBlockLight.backingArray.forEach(out::writeLong)

        for (i in 0 until 2) {
            out.writeVarInt(26)
            for (i in 0 until 26) {
                out.writeVarInt(2048)
                Light.lightBuffer.forEach(out::writeLong)
            }
        }
    }

    override val opcode: Int = 0x2C
}
