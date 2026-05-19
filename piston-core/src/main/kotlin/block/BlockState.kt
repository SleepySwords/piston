@file:Suppress("UNCHECKED_CAST")

package dev.sleepyswords.piston.block

interface IBlockState {
    fun <T> get(property: Property<T>): T?

    fun <T> with(property: Property<T>, value: T): BlockState
}

class BlockDefinition(
    val blockID: Int,
    val values: List<Property<*>>,
    val multipliers: IntArray,
    val blocks: List<BlockState>,
)

// In the future, maybe use bit-packing to remove division, or transition tables with an array
abstract class BlockState(
    private val definition: BlockDefinition,
    private val stateID: Int
) : IBlockState {
    override fun <T> get(property: Property<T>): T? {
        return property.get(getPropertyIndex(property))
    }

    fun <T> getPropertyIndex(property: Property<T>): Int {
        return (stateID / definition.multipliers[definition.values.indexOf(property)]) % property.size
    }

    override fun <T> with(property: Property<T>, value: T): BlockState {
        val propertyIndex = definition.values.indexOf(property)
        val delta = (property.index(value) - getPropertyIndex(property)) * definition.multipliers[propertyIndex]
        return definition.blocks[stateID + delta]
    }

    val id = definition.blockID + stateID
}

interface Property<T> {
    fun index(value: T): Int

    fun get(index: Int): T

    val name: String

    val size: Int

    val values: List<T>
}

class BooleanProperty(override val name: String) : Property<Boolean> {
    override fun index(value: Boolean): Int {
        return if (value) 1 else 0
    }

    override fun get(index: Int): Boolean {
        return index == 1
    }

    override val size: Int = 2

    override val values: List<Boolean> = listOf(false, true)
}

interface Snowy<T> : IBlockState {
    fun getSnowy(): Boolean = get(property = SNOWY)!!

    fun withSnowy(value: Boolean): T = with(property = SNOWY, value) as T

    companion object {
        val SNOWY: Property<Boolean> = BooleanProperty("SNOWY")
    }
}

class DirectionProperty(override val name: String) : Property<Direction> {
    override fun index(value: Direction): Int {
        return value.ordinal
    }
    override fun get(index: Int): Direction {
        return Direction.entries[index]
    }

    override val values: List<Direction> = Direction.entries.toList()

    override val size: Int = Direction.entries.size
}

enum class Direction {
    NORTH, SOUTH, EAST, WEST
}

interface DirectionState<T> : IBlockState {
    fun getDirection(): Direction = get(property = DIRECTION)!!

    fun withDirection(value: Direction): T = with(property = DIRECTION, value) as T

    companion object {
        val DIRECTION: Property<Direction> = DirectionProperty("DIRECTION")
    }
}

class GrassState(definition: BlockDefinition, stateID: Int) :
    BlockState(definition, stateID), Snowy<GrassState>, DirectionState<GrassState> {
    companion object Properties {
        val PROPERTIES = listOf(
            Snowy.SNOWY,
            DirectionState.DIRECTION
        )
    }
}

fun main() {
    var grassState = BlockRegistry().defaultBlockState<GrassState>()

    println(grassState.getDirection())
    println(grassState.getSnowy())
    println(grassState.id)

    grassState = grassState.withDirection(Direction.SOUTH).withSnowy(true)

    println(grassState.getDirection())
    println(grassState.getSnowy())
    println(grassState.id)
}
