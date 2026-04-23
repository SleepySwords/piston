package dev.sleepyswords.piston.network.packet.clientbound.play

import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writeBoolean
import dev.sleepyswords.piston.network.VarNum.writeMCString
import dev.sleepyswords.piston.network.VarNum.writeBlockVertex
import dev.sleepyswords.piston.network.VarNum.writeVarInt
import kotlinx.io.Sink

enum class GameMode(val gameModeID: Byte) {
    SURVIVAL(0), CREATIVE(1), ADVENTURE(2), SPECTATOR(3)
}

data class BlockVertex(
    var x: Int = 0,
    var y: Int = 0,
    var z: Short = 0
)

class LoginPacket(
    val entityID: Int,
    val isHardcore: Boolean,
    val dimensionNames: List<String>,
    val maxPlayers: Int,
    val viewDistance: Int,
    val simulationDistance: Int,
    val reducedDebugInfo: Boolean,
    val enableRespawnScreen: Boolean,
    val doLimitedCrafting: Boolean,
    val dimensionType: Int,
    val dimensionName: String,
    val hashedSeed: Long,
    val gameMode: GameMode,
    val previousGameMode: GameMode?,
    val isDebug: Boolean,
    val isFlat: Boolean,
    val deathLocation: Pair<String, BlockVertex>?,
    val portalCooldown: Int,
    val seaLevel: Int,
    val enforceSecureChat: Boolean,
) : ClientboundPacket {
    override fun encode(out: Sink) {
        out.writeInt(entityID)
        out.writeBoolean(isHardcore)
        out.writeVarInt(dimensionNames.size)
        for (dimensionName in dimensionNames) {
            out.writeMCString(dimensionName)
        }
        out.writeVarInt(maxPlayers)
        out.writeVarInt(viewDistance)
        out.writeVarInt(simulationDistance)
        out.writeBoolean(reducedDebugInfo)
        out.writeBoolean(enableRespawnScreen)
        out.writeBoolean(doLimitedCrafting)
        out.writeVarInt(dimensionType)
        out.writeMCString(dimensionName)
        out.writeLong(hashedSeed)
        out.writeByte(gameMode.gameModeID)
        out.writeByte(previousGameMode?.gameModeID ?: -1)
        out.writeBoolean(isDebug)
        out.writeBoolean(isFlat)
        out.writeBoolean(deathLocation != null)
        if (deathLocation != null) {
            val (deathDimension, deathPositon) = deathLocation
            out.writeMCString(deathDimension)
            out.writeBlockVertex(deathPositon)
        }
        out.writeVarInt(portalCooldown)
        out.writeVarInt(seaLevel)
        out.writeBoolean(enforceSecureChat)
    }

    override val opcode: Int = 0x30
}