package dev.sleepyswords.piston.event.block

import dev.sleepyswords.piston.block.Face
import dev.sleepyswords.piston.entity.Hand
import dev.sleepyswords.piston.event.Event
import dev.sleepyswords.piston.utility.BlockVertex

// In the future we need the entity id as well
data class UseItemOnEvent(
    val hand: Hand,
    val position: BlockVertex,
    val face: Face,
    val cursorPositionX: Float,
    val cursorPositionY: Float,
    val cursorPositionZ: Float,
    val insideBlock: Boolean,
    val worldBorderHit: Boolean,
    val sequence: Int,
) : Event
