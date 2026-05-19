package dev.sleepyswords.piston.block

import kotlin.reflect.KClass

object BlockRegistry {
    var blockID = 0

    val defaultBlockStates = mutableMapOf<KClass<*>, BlockState>()

    init {
        registerBlock<AirState>(::Air, ::AirState)
        registerBlock<StoneState>(::Stone, ::StoneState)
        blank(7)
        registerBlock<GrassState>(::Grass, ::GrassState)
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
            blocks.add(blockStateConstructor(blockDefinition, i))
        }

        defaultBlockStates[T::class] = blocks[0]

        blockID += currentMultiplier
    }
}
