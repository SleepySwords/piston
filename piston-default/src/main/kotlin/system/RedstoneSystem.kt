package dev.sleepyswords.piston.system

import dev.sleepyswords.piston.block.BlockState
import dev.sleepyswords.piston.block.Face
import dev.sleepyswords.piston.block.RedstoneSider
import dev.sleepyswords.piston.block.RedstoneWire
import dev.sleepyswords.piston.block.RedstoneWireState
import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.event.block.BlockUpdateEvent
import dev.sleepyswords.piston.utility.BlockVertex
import dev.sleepyswords.piston.world.World
import kotlin.math.abs
import kotlin.math.sign

class RedstoneSystem(
    val world: World,
) : System {
    private val activePulses = mutableListOf<PlacementPulse>()

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

            val network = collectNetwork(placement)
            if (network.isEmpty()) continue
            processed.addAll(network)

            activePulses.removeAll { pulse -> pulse.order.any { it in network } }

            val seeds = network.intersect(newPlacements)
            val restingPower = network.associateWith { position ->
                (world[position] as RedstoneWireState).getPower()
            }

            activePulses.add(
                PlacementPulse(
                    order = bfsFromSeeds(seeds, network),
                    restingPower = restingPower,
                ),
            )
        }
    }

    private fun tickPulses(eventBuffer: EventBuffer) {
        val iterator = activePulses.iterator()
        while (iterator.hasNext()) {
            val pulse = iterator.next()

            if (pulse.rising) {
                pulse.frontier = minOf(pulse.frontier + 1, pulse.order.lastIndex)
                val waveLength = pulse.frontier + 1
                val stepsRemaining = (pulse.order.size - pulse.frontier).coerceAtLeast(1)

                for (position in pulse.order.take(waveLength)) {
                    val state = world[position] as? RedstoneWireState ?: continue
                    val newPower = stepToward(
                        state.getPower().toInt(),
                        PULSE_PEAK_POWER.toInt(),
                        stepsRemaining,
                    )
                    if (newPower != state.getPower().toInt()) {
                        emitWireUpdate(state.withPower(newPower.toByte()) as RedstoneWireState, position, eventBuffer)
                    }
                }

                val waveLit = pulse.order.take(waveLength).all { position ->
                    (world[position] as? RedstoneWireState)?.getPower() == PULSE_PEAK_POWER
                }
                if (pulse.frontier >= pulse.order.lastIndex && waveLit) {
                    pulse.rising = false
                }
            } else {
                var settled = true
                for (position in pulse.order) {
                    val state = world[position] as? RedstoneWireState ?: continue
                    val target = pulse.restingPower[position]?.toInt() ?: 0
                    val newPower = stepToward(state.getPower().toInt(), target, pulse.order.size)
                    if (newPower != state.getPower().toInt()) {
                        settled = false
                        emitWireUpdate(state.withPower(newPower.toByte()) as RedstoneWireState, position, eventBuffer)
                    } else if (state.getPower().toInt() != target) {
                        settled = false
                    }
                }
                if (settled) {
                    iterator.remove()
                }
            }
        }
    }

    private fun bfsFromSeeds(seeds: Set<BlockVertex>, network: Set<BlockVertex>): List<BlockVertex> {
        val visited = mutableSetOf<BlockVertex>()
        val order = mutableListOf<BlockVertex>()
        val queue = ArrayDeque<BlockVertex>()

        for (seed in seeds) {
            if (seed in network && visited.add(seed)) {
                queue.addLast(seed)
            }
        }

        while (queue.isNotEmpty()) {
            val position = queue.removeFirst()
            order.add(position)

            for (neighbor in connectedWireNeighbors(position)) {
                if (neighbor in network && visited.add(neighbor)) {
                    queue.addLast(neighbor)
                }
            }
        }

        for (position in network) {
            if (position !in visited) {
                order.add(position)
            }
        }

        return order
    }

    private fun collectNetwork(seed: BlockVertex): Set<BlockVertex> {
        val network = mutableSetOf<BlockVertex>()
        val queue = ArrayDeque<BlockVertex>()

        if (world[seed] !is RedstoneWireState) {
            return network
        }

        queue.addLast(seed)

        while (queue.isNotEmpty()) {
            val position = queue.removeFirst()
            if (!network.add(position)) continue

            for (neighbor in connectedWireNeighbors(position)) {
                if (neighbor !in network) {
                    queue.addLast(neighbor)
                }
            }
        }

        return network
    }

    private fun connectedWireNeighbors(position: BlockVertex): List<BlockVertex> {
        val state = world[position] as? RedstoneWireState ?: return emptyList()
        val neighbors = mutableListOf<BlockVertex>()

        for (face in HORIZONTAL_FACES) {
            if (!isConnected(getSide(state, face))) continue

            val neighbor = face.blockOffset(position)
            if (world[neighbor] is RedstoneWireState) {
                neighbors.add(neighbor)
            }
        }

        val above = Face.TOP.blockOffset(position)
        if (world[above] is RedstoneWireState) {
            neighbors.add(above)
        }

        val below = Face.BOTTOM.blockOffset(position)
        if (world[below] is RedstoneWireState) {
            neighbors.add(below)
        }

        return neighbors
    }

    private fun emitWireUpdate(
        state: RedstoneWireState,
        position: BlockVertex,
        eventBuffer: EventBuffer,
    ) {
        val correctedState = getConnectionState(state, position)
        val update = BlockUpdateEvent(correctedState, position)
        update.updateChunk(world[position.toChunkVertex()])
        eventBuffer.emit(update)
    }

    private fun stepToward(current: Int, target: Int, stepsRemaining: Int): Int {
        val diff = target - current
        if (diff == 0) return current

        val step = minOf(
            abs(diff),
            maxOf(POWER_STEP, (abs(diff) + stepsRemaining - 1) / stepsRemaining.coerceAtLeast(1)),
        )
        return (current + sign(diff.toFloat()) * step).toInt().coerceIn(0, 15)
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
        val order: List<BlockVertex>,
        val restingPower: Map<BlockVertex, Byte>,
        var frontier: Int = -1,
        var rising: Boolean = true,
    )

    companion object {
        private const val POWER_STEP = 2
        private const val PULSE_PEAK_POWER: Byte = 15
        private val HORIZONTAL_FACES = listOf(Face.NORTH, Face.SOUTH, Face.EAST, Face.WEST)
    }
}
