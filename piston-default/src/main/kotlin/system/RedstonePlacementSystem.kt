package dev.sleepyswords.piston.system

import dev.sleepyswords.piston.Utility
import dev.sleepyswords.piston.block.BlockState
import dev.sleepyswords.piston.block.Face
import dev.sleepyswords.piston.block.RedstoneSider
import dev.sleepyswords.piston.block.RedstoneWire
import dev.sleepyswords.piston.block.RedstoneWireState
import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.event.block.BlockUpdateEvent
import dev.sleepyswords.piston.utility.BlockVertex
import dev.sleepyswords.piston.world.World
import jdk.jshell.execution.Util

class RedstonePlacementSystem(
    val world: World,
) : System {
    private val activePulses = mutableListOf<PlacementPulse>()
    private val enqueuedPulses = mutableListOf<PlacementPulse>()

    override fun start() {}

    override fun update(eventBuffer: EventBuffer) {
        val blockUpdates = eventBuffer.drain<BlockUpdateEvent>()
        blockUpdates.forEach(eventBuffer::emit)

        val newPlacements = blockUpdates
            .filter(::isNewWirePlacement)
            .map { it.position }
            .toSet()
        val wirePositions = blockUpdates.flatMap { affectedWirePositions(it.position) }.toSet()

        val corrections = wirePositions.mapNotNull { position ->
            val currentState = world[position] as? RedstoneWireState ?: return@mapNotNull null
            val correctedState = getConnectionState(currentState, position)
            if (correctedState == currentState) null
            else BlockUpdateEvent(correctedState, position)
        }

        corrections.forEach { it.updateChunk(world[it.position.toChunkVertex()]) }
        corrections.forEach(eventBuffer::emit)

        if (newPlacements.isNotEmpty()) {
            startPlacementPulses(newPlacements)
        }

        tickPulses(eventBuffer)
    }

    private fun isNewWirePlacement(event: BlockUpdateEvent): Boolean {
        val state = event.newState as? RedstoneWireState ?: return false
        return state.getPower() == 0.toByte() && isDot(state)
    }

    private fun startPlacementPulses(newPlacements: Set<BlockVertex>) {
        val processed = mutableSetOf<BlockVertex>()

        for (placement in newPlacements) {
            if (placement in processed) continue
            if (world[placement] !is RedstoneWireState) continue

            bfsEnqueue(placement)
        }
    }

    private fun tickPulses(eventBuffer: EventBuffer) {
        val enqueuedIterator = enqueuedPulses.iterator()
        while (enqueuedIterator.hasNext()) {
            val pulse = enqueuedIterator.next()
            if (pulse.offset == 0) {
                activePulses.add(PlacementPulse(pulse.block, 30))
                enqueuedIterator.remove()
            }
            pulse.offset -= 1
        }
        val activeIterator = activePulses.iterator()
        while (activeIterator.hasNext()) {
            val pulse = activeIterator.next()
            pulse.offset -= 1;
            val block = world[pulse.block]
            if (block is RedstoneWireState && pulse.offset >= 0) {
                eventBuffer.emit(
                    event = BlockUpdateEvent(
                        newState = block.withPower(
                            value = (pulse.offset.div(2)).toByte()
                        ),
                        position = pulse.block
                    )
                )
            } else {
                activeIterator.remove()
            }
        }
    }

    private fun bfsEnqueue(placement: BlockVertex) {
        val visited = mutableSetOf<BlockVertex>()
        val queue = ArrayDeque<PlacementPulse>()
        queue.add(PlacementPulse(placement, 0))
        while (queue.isNotEmpty()) {
            val popped = queue.removeLast()
            if (visited.contains(popped.block)) {
                continue
            }
            enqueuedPulses.add(popped)
            visited.add(popped.block)
            for (neighbor in affectedWirePositions(popped.block)) {
                if (world[neighbor] is RedstoneWireState){
                    queue.addFirst(PlacementPulse(neighbor, popped.offset + 5))
                }
            }
        }
    }

    private fun affectedWirePositions(position: BlockVertex): List<BlockVertex> {
        val positions = mutableListOf(position)

        for (face in HORIZONTAL_FACES) {
            val neighbor = face.blockOffset(position)
            positions.add(neighbor)

            val neighborState = world[neighbor]
            if (isRedstoneConductor(neighborState)) {
                positions.add(Face.TOP.blockOffset(neighbor))
            } else {
                positions.add(Face.BOTTOM.blockOffset(neighbor))
            }
        }

        return positions.filter { world[it] is RedstoneWireState }
    }

    private fun getConnectionState(state: RedstoneWireState, position: BlockVertex): RedstoneWireState {
        val dot = isDot(state)
        var result = getMissingConnections(
            RedstoneWire.DEFAULT_STATE
                .withWest(RedstoneSider.NONE)
                .withEast(RedstoneSider.NONE)
                .withSouth(RedstoneSider.NONE)
                .withNorth(RedstoneSider.NONE)
                .withPower(state.getPower())
                as RedstoneWireState,
            position,
        )

        if (dot && isDot(result)) {
            return result
        }

        val northConnected = isConnected(result.getNorth())
        val southConnected = isConnected(result.getSouth())
        val eastConnected = isConnected(result.getEast())
        val westConnected = isConnected(result.getWest())

        val nsOnly = !northConnected && !southConnected
        val ewOnly = !eastConnected && !westConnected

        if (!westConnected && nsOnly) {
            result = result.withWest(RedstoneSider.SIDE)
        }
        if (!eastConnected && nsOnly) {
            result = result.withEast(RedstoneSider.SIDE)
        }
        if (!northConnected && ewOnly) {
            result = result.withNorth(RedstoneSider.SIDE)
        }
        if (!southConnected && ewOnly) {
            result = result.withSouth(RedstoneSider.SIDE)
        }

        return result
    }

    private fun getMissingConnections(state: RedstoneWireState, position: BlockVertex): RedstoneWireState {
        val above = Face.TOP.blockOffset(position)
        val canConnectUp = !isRedstoneConductor(world[above])
        var result = state

        for (face in HORIZONTAL_FACES) {
            if (!isConnected(getSide(result, face))) {
                val connection = getConnectingSide(position, face, canConnectUp)
                result = withSide(result, face, connection)
            }
        }

        return result
    }

    private fun getConnectingSide(
        position: BlockVertex,
        face: Face,
        canConnectUp: Boolean,
    ): RedstoneSider {
        val neighbor = face.blockOffset(position)
        val neighborState = world[neighbor]

        if (canConnectUp) {
            val aboveNeighbor = Face.TOP.blockOffset(neighbor)
            val aboveNeighborState = world[aboveNeighbor]
            if (canSurviveOn(neighborState) && shouldConnectTo(aboveNeighborState)) {
                if (isFaceSturdy(neighborState, face.opposite())) {
                    return RedstoneSider.UP
                }
                return RedstoneSider.SIDE
            }
        }

        val belowNeighbor = Face.BOTTOM.blockOffset(neighbor)
        return if (
            !shouldConnectTo(neighborState, face) &&
            (isRedstoneConductor(neighborState) || !shouldConnectTo(world[belowNeighbor]))
        ) {
            RedstoneSider.NONE
        } else {
            RedstoneSider.SIDE
        }
    }

    private fun shouldConnectTo(state: BlockState, direction: Face? = null): Boolean {
        return state is RedstoneWireState
    }

    private fun isRedstoneConductor(state: BlockState): Boolean {
        if (state.definition.isAir) return false
        if (state is RedstoneWireState) return false
        return true
    }

    private fun canSurviveOn(state: BlockState): Boolean {
        if (state.definition.isAir) return false
        if (state is RedstoneWireState) return false
        return true
    }

    private fun isFaceSturdy(state: BlockState, face: Face): Boolean {
        if (state.definition.isAir) return false
        if (state is RedstoneWireState) return false
        return true
    }

    private fun isDot(state: RedstoneWireState): Boolean {
        return !isConnected(state.getNorth()) &&
            !isConnected(state.getSouth()) &&
            !isConnected(state.getEast()) &&
            !isConnected(state.getWest())
    }

    private fun isConnected(side: RedstoneSider): Boolean = side != RedstoneSider.NONE

    private fun getSide(state: RedstoneWireState, face: Face): RedstoneSider = when (face) {
        Face.NORTH -> state.getNorth()
        Face.SOUTH -> state.getSouth()
        Face.EAST -> state.getEast()
        Face.WEST -> state.getWest()
        else -> error("Not a horizontal face: $face")
    }

    private fun withSide(state: RedstoneWireState, face: Face, side: RedstoneSider): RedstoneWireState = when (face) {
        Face.NORTH -> state.withNorth(side)
        Face.SOUTH -> state.withSouth(side)
        Face.EAST -> state.withEast(side)
        Face.WEST -> state.withWest(side)
        else -> error("Not a horizontal face: $face")
    }

    private fun Face.opposite(): Face = when (this) {
        Face.NORTH -> Face.SOUTH
        Face.SOUTH -> Face.NORTH
        Face.EAST -> Face.WEST
        Face.WEST -> Face.EAST
        Face.TOP -> Face.BOTTOM
        Face.BOTTOM -> Face.TOP
    }

    private data class PlacementPulse(
        val block: BlockVertex,
        var offset: Int,
    )

    companion object {
        private const val POWER_STEP = 2
        private const val PULSE_PEAK_POWER: Byte = 15
        private val HORIZONTAL_FACES = listOf(Face.NORTH, Face.SOUTH, Face.EAST, Face.WEST)
    }
}
