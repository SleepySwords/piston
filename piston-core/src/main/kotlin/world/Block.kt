package dev.sleepyswords.utils.world

interface Block {
    fun getPhysicalBlock(): Int

    fun isAirBlock(): Boolean
}

object Air : Block {
    override fun getPhysicalBlock(): Int = 0

    override fun isAirBlock(): Boolean = true
}

object Stone : Block {
    override fun getPhysicalBlock(): Int = 1

    override fun isAirBlock(): Boolean = false
}

object Grass : Block {
    override fun getPhysicalBlock(): Int = 2

    override fun isAirBlock(): Boolean = false
}
