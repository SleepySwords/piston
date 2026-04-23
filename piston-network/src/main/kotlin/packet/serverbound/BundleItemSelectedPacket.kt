package dev.sleepyswords.piston.network.packet.serverbound

import dev.sleepyswords.piston.network.ClientPacketDecoder
import dev.sleepyswords.piston.network.ServerboundPacket
import dev.sleepyswords.piston.network.VarNum.readVarInt

data class BundleItemSelectedPacket(val slotOfBundle: Int, val slotInBundle: Int) : ServerboundPacket {
    companion object {
        val Decoder: ClientPacketDecoder<BundleItemSelectedPacket> = { buffer ->
            val slotOfBundle = buffer.readVarInt()
            val slotInBundle = buffer.readVarInt()

            BundleItemSelectedPacket(slotOfBundle, slotInBundle)
        }
    }
}