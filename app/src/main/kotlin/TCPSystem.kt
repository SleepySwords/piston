package dev.sleepyswords.piston

import dev.sleepyswords.piston.event.Event
import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.event.EventBus
import dev.sleepyswords.piston.network.NetworkManager
import dev.sleepyswords.piston.system.System
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TCPSystem: System {
    val eventBus = EventBus()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null


    override fun start() {
        job = scope.launch(Dispatchers.IO) {
            NetworkManager().launchTCPServer("127.0.0.1", 25565, eventBus)
        }
    }

    override fun update(eventBuffer: EventBuffer) {
        while (true) {
            val event = eventBus.poll() ?: break
            eventBuffer.emit(event)
        }
    }

    override fun postUpdate(events: List<Event>) {

    }
}
