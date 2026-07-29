package dev.sleepyswords.piston.block

class Air(blockID: Int, blocks: List<BlockState>) : BlockDefinition(blockID, blocks) {
    override val properties: List<Property<*>> = emptyList()
    override val isAir: Boolean = true

    companion object {
        val DEFAULT_STATE by lazy {
            BlockRegistry.defaultBlockState<AirState>()
        }
    }
}

class AirState(blockDefinition: BlockDefinition, stateID: Int): BlockState(blockDefinition, stateID)

class Unknown(blockID: Int, blocks: List<BlockState>) : BlockDefinition(blockID, blocks) {
    override val properties: List<Property<*>> = emptyList()
    override val isAir: Boolean = true

    companion object {
        val DEFAULT_STATE by lazy {
            BlockRegistry.defaultBlockState<AirState>()
        }
    }
}

class UnknownState(blockDefinition: BlockDefinition, stateID: Int): BlockState(blockDefinition, stateID)


class Stone(blockID: Int, blocks: List<BlockState>) : BlockDefinition(blockID, blocks) {
    override val properties: List<Property<*>> = emptyList()
    override val isAir: Boolean = false

    companion object {
        val DEFAULT_STATE by lazy {
            BlockRegistry.defaultBlockState<StoneState>()
        }
    }
}

class StoneState(blockDefinition: BlockDefinition, stateID: Int): BlockState(blockDefinition, stateID)

class Grass(blockID: Int, blocks: List<BlockState>) : BlockDefinition(blockID, blocks) {
    override val properties: List<Property<*>> = GrassState.PROPERTIES
    override val isAir: Boolean = false

    companion object {
        val DEFAULT_STATE by lazy {
            BlockRegistry.defaultBlockState<GrassState>()
        }
    }
}

class RedstoneWire(blockID: Int, blocks: List<BlockState>) : BlockDefinition(blockID, blocks) {
    override val properties: List<Property<*>> = RedstoneWireState.PROPERTIES
    override val isAir: Boolean = false

    companion object {
        val DEFAULT_STATE by lazy {
            BlockRegistry.defaultBlockState<RedstoneWireState>()
        }
    }
}

class RedstoneWireState(blockDefinition: BlockDefinition, stateID: Int):
    BlockState(blockDefinition, stateID),
    RedstoneSide<RedstoneWireState>,
    RedstonePower<RedstoneWireState>
{
    companion object Properties {
        val PROPERTIES = listOf(
            RedstoneSide.WEST_DIRECTION,
            RedstoneSide.SOUTH_DIRECTION,
            RedstonePower.POWER,
            RedstoneSide.NORTH_DIRECTION,
            RedstoneSide.EAST_DIRECTION,
        )
    }
}

