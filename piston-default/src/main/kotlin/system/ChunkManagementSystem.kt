package dev.sleepyswords.piston.system

import dev.sleepyswords.piston.block.Air
import dev.sleepyswords.piston.block.BlockRegistry
import dev.sleepyswords.piston.block.Grass
import dev.sleepyswords.piston.block.RedstoneSider
import dev.sleepyswords.piston.block.RedstoneWire
import dev.sleepyswords.piston.event.BroadcastEvent
import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.event.block.BlockUpdateEvent
import dev.sleepyswords.piston.event.block.StartBreakBlockEvent
import dev.sleepyswords.piston.event.block.UseItemOnEvent
import dev.sleepyswords.piston.event.world.RequestChunkEvent
import dev.sleepyswords.piston.utility.BlockVertex
import dev.sleepyswords.piston.world.World


class ChunkManagementSystem(
    val world: World,
) : System {
    override fun start() {}

    override fun update(eventBuffer: EventBuffer) {
        val placeEvents = eventBuffer.drain<UseItemOnEvent>()
        val (updateEvents, chatMessages) = placeEvents.map { event ->
            val placeBlockLocation = event.position + BlockVertex(0, 1, 0)
            val blockUpdate = BlockUpdateEvent(
                BlockRegistry.defaultBlockIDs[BlockRegistry.defaultBlockIDLate[world[placeBlockLocation]]!! + 1],
                placeBlockLocation,
            )

            val chatMessage = BroadcastEvent("Block ID: ${world[placeBlockLocation].id + 1}")

            Pair(blockUpdate, chatMessage)
        }.unzip()

        updateEvents.forEach{ it.updateChunk(world[it.position.toChunkVertex()])}
        updateEvents.forEach(eventBuffer::emit)
        chatMessages.forEach(eventBuffer::emit)

        val breakEvents = eventBuffer.drain<StartBreakBlockEvent>()
        val updates = breakEvents.map {
            BlockUpdateEvent(
                RedstoneWire.DEFAULT_STATE
                    .withEast(RedstoneSider.NONE)
                    .withSouth(RedstoneSider.SIDE)
                    .withNorth(RedstoneSider.NONE)
                    .withWest(RedstoneSider.UP)
                    .withPower(5),
            it.position)
        }
        updates.forEach{ it.updateChunk(world[it.position.toChunkVertex()])}
        updates.forEach(eventBuffer::emit)

        val requestChunks = eventBuffer.drain<RequestChunkEvent>();

        // FIXME: we only want block data, deepClone also does not seem like a good idea...

        requestChunks.forEach {
            it.completableDeferred.complete(world[it.chunkPosition].deepClone())
        }
    }
}
