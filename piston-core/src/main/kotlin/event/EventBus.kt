package dev.sleepyswords.piston.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel

class EventBus {
    private val clientBoundEvents = Channel<Event>(
        capacity = 1024,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    private val serverBoundEvents = Channel<Event>(
        capacity = 1024,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    fun emitServerBound(event: Event) {
        serverBoundEvents.trySend(event)
    }

    fun pollServerBound(): Event? = serverBoundEvents.tryReceive().getOrNull()

    fun emitClientBound(event: Event) {
        clientBoundEvents.trySend(event)
    }

    fun pollClientBound(): Event? = clientBoundEvents.tryReceive().getOrNull()
}
