package dev.sleepyswords.piston.event

import kotlin.reflect.KClass

// Benchmark against filterIsInstance
class EventBuffer {
    val events = mutableMapOf<KClass<out Event>, MutableList<Event>>()

    fun <T : Event> emit(event: T) {
        val list = events.getOrPut(event::class) { mutableListOf() }
        list.add(event)
    }

    inline fun <reified T : Event> drain(): List<T> {
        val list = events.remove(T::class) ?: emptyList()

        @Suppress("UNCHECKED_CAST")
        return list as List<T>
    }

    fun drainAll(): List<Event> {
        val clearedEvents = events.values.flatten().toCollection(mutableListOf())
        events.clear()
        return clearedEvents
    }
}
