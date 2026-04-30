package dev.sleepyswords.piston.utility

import dev.sleepyswords.piston.Utility

class BitSet(
    private val nBits: Int = 0,
) {
    val backingArray: LongArray = LongArray(Utility.ceilDiv(nBits, Long.SIZE_BITS))

    // TODO: resize automatically
    operator fun set(
        index: Int,
        value: Boolean,
    ) {
        val longIndex = index / 64
        val bitIndex = index % 64
        var l = backingArray[longIndex]

        val mask = (1L shl bitIndex)
        l = l and mask.inv()
        if (value) l = l or mask

        backingArray[longIndex] = l
    }

    fun get(index: Int): Boolean {
        val longIndex = index / 64
        val bitIndex = index % 64
        val l = backingArray[longIndex]

        val value = (l shr bitIndex) and 1
        return value.toInt() != 0
    }
}
