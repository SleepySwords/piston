package dev.sleepyswords.piston.event

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class PlayerLoginEvent @OptIn(ExperimentalUuidApi::class) constructor(val playerName: String, val uuid: Uuid): Event
