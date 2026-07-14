package dev.sleepyswords.piston.system

import dev.sleepyswords.piston.event.ChatMessageEvent
import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.event.PlayerLoginEvent
import kotlin.uuid.ExperimentalUuidApi

class MOTDSystem: System {
    override fun start() { }

    @OptIn(ExperimentalUuidApi::class)
    override fun update(eventBuffer: EventBuffer) {
        val loginEvents = eventBuffer.drain<PlayerLoginEvent>()

        for (loginEvent in loginEvents) {
            eventBuffer.emit(ChatMessageEvent("Hello!", loginEvent.uuid))
        }

        loginEvents.forEach(eventBuffer::emit)
    }
}
