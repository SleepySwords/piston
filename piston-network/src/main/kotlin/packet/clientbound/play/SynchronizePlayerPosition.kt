package dev.sleepyswords.piston.network.packet.clientbound.play

import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writePosition
import dev.sleepyswords.piston.network.VarNum.writeRotation
import dev.sleepyswords.piston.network.VarNum.writeVarInt
import dev.sleepyswords.piston.network.VarNum.writeVelocity
import kotlinx.io.Sink

data class Position(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
)

data class Velocity(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
)

data class Rotation(
    var yaw: Float = 0.0f,
    var pitch: Float = 0.0f,
)

data class Transform(
    var position: Position = Position(),
    var rotation: Rotation = Rotation(),
)

class SynchronizePlayerPosition(
    val teleportID: Int,
    val position: Position,
    val velocity: Velocity,
    val rotation: Rotation,
    val teleportFlags: Int,
) : ClientboundPacket {
    override fun encode(out: Sink) {
        out.writeVarInt(teleportID)
        out.writePosition(position)
        out.writeVelocity(velocity)
        out.writeRotation(rotation)
        out.writeInt(teleportFlags)
    }

    override val opcode: Int = 0x46
}
