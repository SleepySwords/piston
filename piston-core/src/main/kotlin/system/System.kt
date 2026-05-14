package dev.sleepyswords.piston.system

import dev.sleepyswords.piston.event.Event
import dev.sleepyswords.piston.event.EventBuffer

interface System {
    fun start()

    fun update(eventBuffer: EventBuffer)

    fun postUpdate(events: List<Event>) {}
}
