package dev.sleepyswords.piston.network.packet.clientbound.status

import dev.sleepyswords.nbt.TextComponent
import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writeMCString
import kotlinx.io.Sink
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class StatusResponse(
    val version: Version,
    val players: Players,
    val description: TextComponent,
    val favicon: String,
    val enforcesSecureChat: Boolean,
)

@Serializable
data class Version(
    val name: String,
    val protocol: Int,
)

@Serializable
data class Players(
    val max: Int,
    val online: Int,
    val sample: List<PlayerSample>,
)

@Serializable
data class PlayerSample(
    val name: String,
    val id: String,
)

private val json =
    Json {
        encodeDefaults = false
        explicitNulls = false
    }

class StatusResponsePacket(
    private val statusResponse: StatusResponse,
) : ClientboundPacket {
    override fun encode(out: Sink) {
        println(json.encodeToString(statusResponse))
        out.writeMCString(
            json.encodeToString(statusResponse),
        )
    }

    override val opcode: Int = 0x00
}
