package dev.sleepyswords.piston.block

interface Block {
    fun getPhysicalBlock(): Int

    fun isAirBlock(): Boolean
}

object Air : Block {
    override fun getPhysicalBlock(): Int = 0

    override fun isAirBlock(): Boolean = true
}

class AirState(blockDefinition: BlockDefinition, stateID: Int): BlockState(blockDefinition, stateID)

object Stone : Block {
    override fun getPhysicalBlock(): Int = 1

    override fun isAirBlock(): Boolean = false
}

class StoneState(blockDefinition: BlockDefinition, stateID: Int): BlockState(blockDefinition, stateID) {
    override fun getPhysicalBlockState(): BlockState {
        return BlockRegistry.defaultBlockState<GrassState>()
    }
}

object Grass : Block {
    override fun getPhysicalBlock(): Int = 2

    override fun isAirBlock(): Boolean = false
}
