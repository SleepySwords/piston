package dev.sleepyswords.piston.event.block

import dev.sleepyswords.piston.event.Event
import dev.sleepyswords.piston.utility.BlockVertex

data class BreakBlockEvent(val position: BlockVertex): Event
