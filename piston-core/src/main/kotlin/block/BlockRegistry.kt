package dev.sleepyswords.piston.block

import kotlin.reflect.KClass

object BlockRegistry {
    var blockID = 0

    val defaultBlockStates = mutableMapOf<KClass<*>, BlockState>()

    val defaultBlockIDs = mutableListOf<BlockState>()
    val defaultBlockIDLate = mutableMapOf<BlockState, Int>()

    init {
        registerBlock<AirState>(::Air, ::AirState)
        registerBlock<StoneState>(::Stone, ::StoneState)
        blank(7)
        registerBlock<GrassState>(::Grass, ::GrassState)
        blank(3793)
        registerBlock<RedstoneWireState>(::RedstoneWire, ::RedstoneWireState)
        for (i in 0..10000) {
            registerBlock<UnknownState>(::Unknown, ::UnknownState)
        }
    }

    fun blank(skip: Int){
        blockID += skip
    }

    inline fun <reified T> defaultBlockState() = defaultBlockStates[T::class] as T

    inline fun <reified T: BlockState> registerBlock(
        blockConstructor: (baseBlockID: Int, blockStates: List<BlockState>) -> BlockDefinition,
        blockStateConstructor: (def: BlockDefinition, stateID: Int
    ) -> T) {
        val blocks: MutableList<BlockState> = mutableListOf()
        val blockDefinition = blockConstructor(blockID, blocks)

        var currentMultiplier = 1
        for (property in blockDefinition.properties) { currentMultiplier *= property.size }

        for (i in 0 until currentMultiplier) {
            val state = blockStateConstructor(blockDefinition, i)
            blocks.add(state)
            defaultBlockIDs.add(state)
            defaultBlockIDLate[state] = defaultBlockIDs.size - 1
        }

        defaultBlockStates[T::class] = blocks[0]

        blockID += currentMultiplier
    }
}
