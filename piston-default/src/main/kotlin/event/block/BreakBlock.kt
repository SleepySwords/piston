package dev.sleepyswords.piston.event.block

import dev.sleepyswords.piston.block.AirState
import dev.sleepyswords.piston.block.BlockRegistry
import dev.sleepyswords.piston.block.BlockState
import dev.sleepyswords.piston.event.Event
import dev.sleepyswords.piston.utility.BlockVertex
import dev.sleepyswords.piston.world.Chunk

data class StartBreakBlockEvent(val position: BlockVertex): Event

data class BreakBlockEvent(val position: BlockVertex): Event

data class BlockUpdateEvent(val newState: BlockState, val position: BlockVertex): Event, UpdateChunkEvent {
    override fun updateChunk(chunk: Chunk) {
        chunk[position.toChunkOffset()] = newState
    }
}

interface UpdateChunkEvent {
    fun updateChunk(chunk: Chunk)
}
