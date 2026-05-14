package dev.sleepyswords.piston.system

// Figure out schedule, probably using a DAG and topological sort.
class SystemHandler {
    val systems: MutableSet<System> = mutableSetOf()

    fun registerSystem(system: System) {
        systems.add(system)
    }

    fun update() {
        for (system in systems) {
//            system.update()
        }
    }
}
