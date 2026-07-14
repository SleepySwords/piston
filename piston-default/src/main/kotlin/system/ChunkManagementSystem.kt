package dev.sleepyswords.piston.system

import dev.sleepyswords.piston.block.AirState
import dev.sleepyswords.piston.block.BlockRegistry
import dev.sleepyswords.piston.block.GrassState
import dev.sleepyswords.piston.block.Stone
import dev.sleepyswords.piston.block.StoneState
import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.event.block.BlockUpdateEvent
import dev.sleepyswords.piston.event.block.BreakBlockEvent
import dev.sleepyswords.piston.event.block.StartBreakBlockEvent
import dev.sleepyswords.piston.event.world.RequestChunkEvent
import dev.sleepyswords.piston.world.World


class ChunkManagementSystem(
    val world: World,
) : System {
    override fun start() {}

    override fun update(eventBuffer: EventBuffer) {
        val blockPositions = eventBuffer.drain<StartBreakBlockEvent>()
        blockPositions.forEach { event ->
            println(event)
            world[event.position] = BlockRegistry.defaultBlockState<AirState>()
        }

        blockPositions.map {
            BlockUpdateEvent(
            BlockRegistry.defaultBlockState<GrassState>(),
            it.position)
        }.forEach(eventBuffer::emit)

        val requestChunks = eventBuffer.drain<RequestChunkEvent>();

        // FIXME: we only want block data, deepClone also does not seem like a good idea...

        requestChunks.forEach {
            it.completableDeferred.complete(world[it.chunkPosition].deepClone())
        }
    }
}
