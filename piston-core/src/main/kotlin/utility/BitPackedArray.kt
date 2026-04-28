package dev.sleepyswords.utils.utility

sealed interface BitPackedArray {
    val bitsPerEntry: UByte
    val size: Int
    val packed: LongArray

    operator fun get(index: Int): Int

    fun setEntry(index: Int, value: Int): Int

    fun increaseCapacity(bitsPerEntry: UByte)
}

class AlignedBitPackedArray(
    override var bitsPerEntry: UByte,
    override val size: Int,
    override var packed: LongArray = LongArray(requiredLongCount(size, bitsPerEntry.toInt()))
) : BitPackedArray {
    private val maxEntryMask: Long
        get() = (1L shl bitsPerEntry.toInt()) - 1L

    override operator fun get(index: Int): Int {
        validateIndex(index)
        val entriesPerLong = entriesPerLong(bitsPerEntry.toInt())
        val longIndex = index / entriesPerLong
        val bitIndex = (index % entriesPerLong) * bitsPerEntry.toInt()
        return ((packed[longIndex] ushr bitIndex) and maxEntryMask).toInt()
    }

    override fun setEntry(index: Int, value: Int): Int {
        validateIndex(index)
        validateValue(value, bitsPerEntry.toInt())
        val entriesPerLong = entriesPerLong(bitsPerEntry.toInt())
        val longIndex = index / entriesPerLong
        val bitIndex = (index % entriesPerLong) * bitsPerEntry.toInt()
        val clearMask = maxEntryMask shl bitIndex
        val previous = ((packed[longIndex] ushr bitIndex) and maxEntryMask).toInt()
        packed[longIndex] = (packed[longIndex] and clearMask.inv()) or (value.toLong() shl bitIndex)
        return previous
    }

    override fun increaseCapacity(bitsPerEntry: UByte) {
        val currentBits = this.bitsPerEntry.toInt()
        val newBits = bitsPerEntry.toInt()
        require(newBits >= currentBits) {
            "New bit width ($newBits) must be >= current bit width ($currentBits)."
        }
        if (newBits == currentBits) {
            return
        }

        val oldPacked = packed
        val oldEntriesPerLong = entriesPerLong(currentBits)
        val oldMask = (1L shl currentBits) - 1L
        val newPacked = LongArray(requiredLongCount(size, newBits))
        val newEntriesPerLong = entriesPerLong(newBits)
        val newMask = (1L shl newBits) - 1L

        for (index in 0 until size) {
            val oldLongIndex = index / oldEntriesPerLong
            val oldBitIndex = (index % oldEntriesPerLong) * currentBits
            val value = (oldPacked[oldLongIndex] ushr oldBitIndex) and oldMask

            val newLongIndex = index / newEntriesPerLong
            val newBitIndex = (index % newEntriesPerLong) * newBits
            newPacked[newLongIndex] =
                (newPacked[newLongIndex] and (newMask shl newBitIndex).inv()) or (value shl newBitIndex)
        }

        packed = newPacked
        this.bitsPerEntry = bitsPerEntry
    }

    private fun validateIndex(index: Int) {
        require(index in 0 until size) {
            "Entry index $index is out of bounds for size $size."
        }
    }

    private fun validateValue(value: Int, bitsPerEntry: Int) {
        require(value >= 0) {
            "Bit-packed values must be non-negative, got $value."
        }
        require(value <= ((1 shl bitsPerEntry) - 1)) {
            "Value $value does not fit in $bitsPerEntry bits."
        }
    }

    companion object {
        private fun entriesPerLong(bitsPerEntry: Int): Int {
            require(bitsPerEntry in 1..32) {
                "bitsPerEntry must be in range 1..32, got $bitsPerEntry."
            }
            return 64 / bitsPerEntry
        }

        private fun requiredLongCount(size: Int, bitsPerEntry: Int): Int {
            require(size >= 0) {
                "size must be non-negative, got $size."
            }
            if (size == 0) {
                return 0
            }
            val entriesPerLong = entriesPerLong(bitsPerEntry)
            return (size + entriesPerLong - 1) / entriesPerLong
        }
    }
}

class CrossBitPackedArray(
    override var bitsPerEntry: UByte,
    override val size: Int,
    override var packed: LongArray = LongArray(requiredLongCount(size, bitsPerEntry.toInt()))
) : BitPackedArray {
    private val maxEntryMask: Long
        get() = (1L shl bitsPerEntry.toInt()) - 1L

    override operator fun get(index: Int): Int {
        validateIndex(index)
        val entryBits = bitsPerEntry.toInt()
        val bitOffset = index.toLong() * entryBits.toLong()
        val longIndex = (bitOffset / 64L).toInt()
        val startBit = (bitOffset % 64L).toInt()

        if (startBit + entryBits <= 64) {
            return ((packed[longIndex] ushr startBit) and maxEntryMask).toInt()
        }

        val lowerBits = 64 - startBit
        val upperBits = entryBits - lowerBits
        val lowerMask = (1L shl lowerBits) - 1L
        val lowerPart = (packed[longIndex] ushr startBit) and lowerMask
        val upperPart = packed[longIndex + 1] and ((1L shl upperBits) - 1L)
        return (lowerPart or (upperPart shl lowerBits)).toInt()
    }

    override fun setEntry(index: Int, value: Int): Int {
        validateIndex(index)
        validateValue(value, bitsPerEntry.toInt())
        val previous = get(index)

        val entryBits = bitsPerEntry.toInt()
        val valueAsLong = value.toLong() and maxEntryMask
        val bitOffset = index.toLong() * entryBits.toLong()
        val longIndex = (bitOffset / 64L).toInt()
        val startBit = (bitOffset % 64L).toInt()

        if (startBit + entryBits <= 64) {
            val clearMask = maxEntryMask shl startBit
            packed[longIndex] = (packed[longIndex] and clearMask.inv()) or (valueAsLong shl startBit)
            return previous
        }

        val lowerBits = 64 - startBit
        val upperBits = entryBits - lowerBits
        val lowerMask = (1L shl lowerBits) - 1L
        val upperMask = (1L shl upperBits) - 1L

        val lowerValue = valueAsLong and lowerMask
        val upperValue = valueAsLong ushr lowerBits

        packed[longIndex] =
            (packed[longIndex] and (lowerMask shl startBit).inv()) or (lowerValue shl startBit)
        packed[longIndex + 1] = (packed[longIndex + 1] and upperMask.inv()) or (upperValue and upperMask)
        return previous
    }

    override fun increaseCapacity(bitsPerEntry: UByte) {
        val currentBits = this.bitsPerEntry.toInt()
        val newBits = bitsPerEntry.toInt()
        require(newBits >= currentBits) {
            "New bit width ($newBits) must be >= current bit width ($currentBits)."
        }
        if (newBits == currentBits) {
            return
        }

        val oldBits = this.bitsPerEntry
        val oldPacked = packed
        val newPacked = LongArray(requiredLongCount(size, newBits))

        this.bitsPerEntry = bitsPerEntry
        this.packed = newPacked
        for (index in 0 until size) {
            val value = readFromStorage(index, oldBits.toInt(), oldPacked)
            setEntry(index, value)
        }
    }

    private fun readFromStorage(index: Int, bitsPerEntry: Int, storage: LongArray): Int {
        val mask = (1L shl bitsPerEntry) - 1L
        val bitOffset = index.toLong() * bitsPerEntry.toLong()
        val longIndex = (bitOffset / 64L).toInt()
        val startBit = (bitOffset % 64L).toInt()

        if (startBit + bitsPerEntry <= 64) {
            return ((storage[longIndex] ushr startBit) and mask).toInt()
        }

        val lowerBits = 64 - startBit
        val upperBits = bitsPerEntry - lowerBits
        val lowerMask = (1L shl lowerBits) - 1L
        val lowerPart = (storage[longIndex] ushr startBit) and lowerMask
        val upperPart = storage[longIndex + 1] and ((1L shl upperBits) - 1L)
        return (lowerPart or (upperPart shl lowerBits)).toInt()
    }

    private fun validateIndex(index: Int) {
        require(index in 0 until size) {
            "Entry index $index is out of bounds for size $size."
        }
    }

    private fun validateValue(value: Int, bitsPerEntry: Int) {
        require(value >= 0) {
            "Bit-packed values must be non-negative, got $value."
        }
        require(value.toLong() <= ((1L shl bitsPerEntry) - 1L)) {
            "Value $value does not fit in $bitsPerEntry bits."
        }
    }

    companion object {
        private fun requiredLongCount(size: Int, bitsPerEntry: Int): Int {
            require(size >= 0) {
                "size must be non-negative, got $size."
            }
            require(bitsPerEntry in 1..32) {
                "bitsPerEntry must be in range 1..32, got $bitsPerEntry."
            }
            if (size == 0) {
                return 0
            }
            val totalBits = size.toLong() * bitsPerEntry.toLong()
            return ((totalBits + 63L) / 64L).toInt()
        }
    }
}
