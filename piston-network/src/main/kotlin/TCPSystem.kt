package dev.sleepyswords.piston.network

import dev.sleepyswords.piston.PistonDefault
import dev.sleepyswords.piston.event.EventBuffer
import dev.sleepyswords.piston.event.EventBus
import dev.sleepyswords.piston.system.Phase
import dev.sleepyswords.piston.system.Scheduler
import dev.sleepyswords.piston.system.System
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun registerTCP(scheduler: Scheduler) {
    val eventBus = EventBus()
    scheduler.register(system = TCPSystem(eventBus))
    scheduler.register(system = TCPSendSystem(eventBus))
    scheduler.register(system = TCPReceiveSystem(eventBus))
}

class TCPSystem(
    val eventBus: EventBus
): System {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null

    override val runBefore: Set<Phase>
        get() = setOf(PistonDefault.GAME_TICK)

    override fun start() {
        job = scope.launch(Dispatchers.IO) {
            NetworkManager().launchTCPServer("127.0.0.1", 25565, eventBus)
        }
    }
}

class TCPSendSystem(
    val eventBus: EventBus
) : System {
    override val runBefore: Set<Phase>
        get() = setOf(PistonDefault.GAME_TICK)

    override fun update(eventBuffer: EventBuffer) {
        while (true) {
            val event = eventBus.pollServerBound() ?: break
            eventBuffer.emit(event)
        }
    }
}

class TCPReceiveSystem(
    val eventBus: EventBus
) : System {
    override val runAfter: Set<Phase>
        get() = setOf(PistonDefault.GAME_TICK)

    override fun update(eventBuffer: EventBuffer) {
        eventBuffer.drainAll().forEach(eventBus::emitClientBound)
    }
}
