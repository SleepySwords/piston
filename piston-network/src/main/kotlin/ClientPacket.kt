package dev.sleepyswords.piston.network

import kotlinx.io.Buffer

interface ClientPacket {
    fun encode(out: Buffer)

    val opcode: Int
}