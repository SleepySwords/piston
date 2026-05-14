package dev.sleepyswords.piston.event

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel

class EventBus {
    private val events = Channel<Event>(
        capacity = 1024,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    fun emit(event: Event) {
        events.trySend(event)
    }

    fun poll(): Event? = events.tryReceive().getOrNull()
}
