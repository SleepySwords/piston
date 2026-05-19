package dev.sleepyswords.piston

import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.network.handler.configuration.PlayerLoginEvent
import dev.sleepyswords.piston.system.System
import java.util.UUID

data class ChatMessageEvent(val message: String, val player: UUID)

class MOTDSystem: System {
    override fun start() {
        TODO("Not yet implemented")
    }

    override fun update(eventBuffer: EventBuffer) {
        val loginEvents = eventBuffer.drain<PlayerLoginEvent>()

        for (loginEvent in loginEvents) {
//            eventBuffer.emit(ChatMessageEvent("Hello!", loginEvent.uuid))
        }

        loginEvents.forEach(eventBuffer::emit)
    }
}
