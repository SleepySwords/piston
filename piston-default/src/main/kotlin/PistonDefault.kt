package dev.sleepyswords.piston

object PistonDefault {
    val PISTON_DEFAULT_NAMESPACE = Namespace("piston_default")

    val GAME_TICK_START = PISTON_DEFAULT_NAMESPACE.createResource("tick_start")
    val GAME_TICK_END = PISTON_DEFAULT_NAMESPACE.createResource("tick_end")
}
