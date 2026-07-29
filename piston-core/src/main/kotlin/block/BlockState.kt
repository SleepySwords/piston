@file:Suppress("UNCHECKED_CAST")

package dev.sleepyswords.piston.block

interface IBlockState {
    fun <T> get(property: Property<T>): T?

    fun <T> with(property: Property<T>, value: T): BlockState
}

// This will become the actual block...
abstract class BlockDefinition(
    val baseBlockID: Int,
    val blockStates: List<BlockState>,
) {
    abstract val properties: List<Property<*>>
    abstract val isAir: Boolean

    val multipliers by lazy {
        val multipliers: MutableList<Int> = mutableListOf()
        var currentMultiplier = 1

        for (property in properties) {
            multipliers.add(currentMultiplier)
            currentMultiplier *= property.size
        }

        multipliers.toIntArray()
    }
}

// In the future, maybe use bit-packing to remove division, or transition tables with an array
abstract class BlockState(
    val definition: BlockDefinition,
    private val stateID: Int
) : IBlockState {
    override fun <T> get(property: Property<T>): T? {
        return property.get(getPropertyIndex(property))
    }

    fun <T> getPropertyIndex(property: Property<T>): Int {
        return (stateID / definition.multipliers[definition.properties.indexOf(property)]) % property.size
    }

    override fun <T> with(property: Property<T>, value: T): BlockState {
        val propertyIndex = definition.properties.indexOf(property)
        val delta = (property.index(value) - getPropertyIndex(property)) * definition.multipliers[propertyIndex]
        return definition.blockStates[stateID + delta]
    }

    val id = definition.baseBlockID + stateID

    open fun getPhysicalBlockState(): BlockState = this
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

class RedstoneSiderProperty(override val name: String) : Property<RedstoneSider> {
    override fun index(value: RedstoneSider): Int {
        return value.ordinal
    }
    override fun get(index: Int): RedstoneSider {
        return RedstoneSider.entries[index]
    }

    override val values: List<RedstoneSider> = RedstoneSider.entries.toList()

    override val size: Int = RedstoneSider.entries.size
}


enum class RedstoneSider {
    UP, SIDE, NONE
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

interface RedstoneSide<T> : IBlockState {
    fun getNorth(): RedstoneSider = get(property = NORTH_DIRECTION)!!

    fun withNorth(value: RedstoneSider): T = with(property = NORTH_DIRECTION, value) as T

    fun getSouth(): RedstoneSider = get(property = SOUTH_DIRECTION)!!

    fun withSouth(value: RedstoneSider): T = with(property = SOUTH_DIRECTION, value) as T

    fun getEast(): RedstoneSider = get(property = EAST_DIRECTION)!!

    fun withEast(value: RedstoneSider): T = with(property = EAST_DIRECTION, value) as T

    fun getWest(): RedstoneSider = get(property = WEST_DIRECTION)!!

    fun withWest(value: RedstoneSider): T = with(property = WEST_DIRECTION, value) as T

    companion object {
        val NORTH_DIRECTION: Property<RedstoneSider> = RedstoneSiderProperty("NORTH")
        val SOUTH_DIRECTION: Property<RedstoneSider> = RedstoneSiderProperty("SOUTH")
        val EAST_DIRECTION: Property<RedstoneSider> = RedstoneSiderProperty("EAST")
        val WEST_DIRECTION: Property<RedstoneSider> = RedstoneSiderProperty("WEST")
    }
}

interface RedstonePower<T> : IBlockState {
    fun getPower(): Byte = get(POWER)!!

    fun withPower(value: Byte) = with(POWER, value)

    companion object {
        val POWER: Property<Byte> = RedstonePowerProperty("REDSTONE")
    }
}

class RedstonePowerProperty(override val name: String) : Property<Byte> {
    override fun index(value: Byte): Int {
        return value.toInt()
    }
    override fun get(index: Int): Byte {
        return index.toByte()
    }

    override val values: List<Byte> = (0 until 16).map(Int::toByte)
    override val size: Int = values.size
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
    var grassState = Grass.DEFAULT_STATE

    println(grassState.getDirection())
    println(grassState.getSnowy())
    println(grassState.id)

    grassState = grassState.withDirection(Direction.SOUTH).withSnowy(true)

    println(grassState.getDirection())
    println(grassState.getSnowy())
    println(grassState.id)
}
