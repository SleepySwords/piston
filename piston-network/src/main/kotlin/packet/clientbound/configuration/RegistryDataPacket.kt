package dev.sleepyswords.piston.network.packet.clientbound.configuration

import dev.sleepyswords.nbt.Nbt
import dev.sleepyswords.nbt.encodeNbtCompoundTag
import dev.sleepyswords.piston.network.ClientboundPacket
import dev.sleepyswords.piston.network.VarNum.writeBoolean
import dev.sleepyswords.piston.network.VarNum.writeMCString
import dev.sleepyswords.piston.network.VarNum.writeVarInt
import kotlinx.io.Sink

class RegistryDataPacket(val registryID: String, val entriesID: List<Pair<String, Nbt.CompoundTag?>>) : ClientboundPacket {
    override fun encode(out: Sink) {
        out.writeMCString(registryID)
        out.writeVarInt(entriesID.size)
        for ((key, nbt) in entriesID) {
            out.writeMCString(key)
            out.writeBoolean(nbt != null)
            if (nbt != null) {
                encodeNbtCompoundTag(out, nbt)
            }
        }
    }

    override val opcode: Int = 0x07
}