package dev.sleepyswords.utils

import kotlin.time.Instant
import kotlin.time.Clock
import kotlinx.serialization.Serializable
import kotlinx.coroutines.*
import kotlin.time.ExperimentalTime

object Utility {
    fun ceilDiv(a: Int, b: Int): Int = (a + b - 1) / b
}

@Serializable
class Printer(val message: String) {
    @OptIn(ExperimentalTime::class)
    fun printMessage() = runBlocking {
        val now: Instant = Clock.System.now()
        launch {
            delay(1000L)
            println(now.toString())
        }
        println(message)
    }
}