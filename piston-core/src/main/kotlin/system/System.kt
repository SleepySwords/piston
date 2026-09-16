package dev.sleepyswords.piston.system

import dev.sleepyswords.piston.event.EventBuffer

interface System {
    fun start() {}

    fun update(eventBuffer: EventBuffer) {}

    val runBefore: Set<Phase>
        get() = setOf()

    val runAfter: Set<Phase>
        get() = setOf()

    val runIn: Set<Phase>
        get() = setOf()
}
