package dev.sleepyswords.piston.block

interface Block {
    fun getPhysicalBlock(): Int

    fun isAirBlock(): Boolean
}

class Air(blockID: Int, blocks: List<BlockState>) : BlockDefinition(blockID, blocks) {
    override val properties: List<Property<*>> = emptyList()
    override val isAir: Boolean = true
}

class AirState(blockDefinition: BlockDefinition, stateID: Int): BlockState(blockDefinition, stateID)

class Stone(blockID: Int, blocks: List<BlockState>) : BlockDefinition(blockID, blocks) {
    override val properties: List<Property<*>> = emptyList()
    override val isAir: Boolean = false
}

class StoneState(blockDefinition: BlockDefinition, stateID: Int): BlockState(blockDefinition, stateID) {
    override fun getPhysicalBlockState(): BlockState {
        return BlockRegistry.defaultBlockState<GrassState>()
    }
}


class Grass(blockID: Int, blocks: List<BlockState>) : BlockDefinition(blockID, blocks) {
    override val properties: List<Property<*>> = GrassState.PROPERTIES
    override val isAir: Boolean = false
}
