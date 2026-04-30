package dev.sleepyswords.piston.utility

data class BlockVertex(
    var x: Int = 0,
    var y: Short = 0,
    var z: Int = 0,
) {
    fun getIndex(): Int = x + z * 16 + y * 16 * 16
}
