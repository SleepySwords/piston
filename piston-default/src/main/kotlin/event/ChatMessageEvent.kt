package dev.sleepyswords.piston.event

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ChatMessageEvent @OptIn(ExperimentalUuidApi::class) constructor(val message: String, val player: Uuid): Event
