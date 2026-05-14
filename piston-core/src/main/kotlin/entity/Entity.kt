package dev.sleepyswords.piston.entity

import dev.sleepyswords.piston.Component
import kotlin.reflect.KClass

class Entity(val entityID: Int) {
    val components = mutableMapOf<KClass<out Component>, Component>()

    fun <T: Component> attachComponent(component: T): Boolean {
        if (components.containsKey(component::class)) {
            return false
        }
        components[component::class] = component
        return true
    }

    inline fun <reified T: Component> getComponent(): T? {
        return components[T::class] as? T
    }
}
