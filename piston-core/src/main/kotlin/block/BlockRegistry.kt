package dev.sleepyswords.piston.block

import kotlin.reflect.KClass

object BlockRegistry {
    var blockID = 0

    val defaultBlockStates = mutableMapOf<KClass<*>, BlockState>()

    init {
        registerBlock<AirState>(true, emptyList()) { a, b -> AirState(a, b) }
        registerBlock<StoneState>(false, emptyList()) { a, b -> StoneState(a, b) }
        blank(7)
        registerBlock<GrassState>(false, GrassState.PROPERTIES) { a, b -> GrassState(a, b) }
    }

    fun blank(skip: Int){
        blockID += skip
    }

    inline fun <reified T> defaultBlockState() = defaultBlockStates[T::class] as T

    inline fun <reified T: BlockState> registerBlock(
        isAir: Boolean,
        properties: List<Property<*>>,
        blockConstructor: (def: BlockDefinition, stateID: Int
    ) -> T) {
        val multipliers: MutableList<Int> = mutableListOf()
        var currentMultiplier = 1

        for (property in properties) {
            multipliers.add(currentMultiplier)
            currentMultiplier *= property.size
        }

        val multiplies = multipliers.toIntArray()
        val blocks: MutableList<BlockState> = mutableListOf()
        val blockDefinition = BlockDefinition(blockID, isAir, properties, multiplies, blocks)

        for (i in 0 until currentMultiplier) {
            blocks.add(blockConstructor(blockDefinition, i))
        }

        defaultBlockStates[T::class] = blocks[0]

        blockID += currentMultiplier
    }
}
