package dev.sleepyswords.utils.world

interface Block {
    fun getPhysicalBlock(): Int

    fun isAirBlock(): Boolean}

object Air : Block {
    override fun getPhysicalBlock(): Int {
        return 0
    }

    override fun isAirBlock(): Boolean {
        return true
    }
}

object Stone : Block {
    override fun getPhysicalBlock(): Int {
        return 1
    }

    override fun isAirBlock(): Boolean {
        return false
    }
}
