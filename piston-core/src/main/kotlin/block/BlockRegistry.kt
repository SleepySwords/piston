package dev.sleepyswords.piston.block

import kotlin.reflect.KClass

class BlockRegistry {
    var blockID = 0

    val defaultBlockStates = mutableMapOf<KClass<*>, BlockState>()

    init {
        registerBlock<GrassState>(GrassState.PROPERTIES) { a, b -> GrassState(a, b) }
    }

    inline fun <reified T> defaultBlockState() = defaultBlockStates[T::class] as T

    inline fun <reified T> registerBlock(
        properties: List<Property<*>>,
        blockConstructor: (def: BlockDefinition, stateID: Int
    ) -> BlockState) {
        val multipliers: MutableList<Int> = mutableListOf()
        var currentMultiplier = 1

        for (property in properties) {
            multipliers.add(currentMultiplier)
            currentMultiplier *= property.size
        }

        val multiplies = multipliers.toIntArray()
        val blocks: MutableList<BlockState> = mutableListOf()
        val blockDefinition = BlockDefinition(blockID, properties, multiplies, blocks)

        for (i in 0 until currentMultiplier) {
            blocks.add(blockConstructor(blockDefinition, i))
        }

        defaultBlockStates[T::class] = blocks[0]

        blockID += currentMultiplier
    }
}
