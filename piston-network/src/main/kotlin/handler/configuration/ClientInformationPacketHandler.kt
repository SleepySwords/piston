package dev.sleepyswords.piston.network.handler.configuration

import dev.sleepyswords.nbt.Nbt
import dev.sleepyswords.nbt.byteTag
import dev.sleepyswords.nbt.compoundTag
import dev.sleepyswords.nbt.floatTag
import dev.sleepyswords.nbt.intTag
import dev.sleepyswords.nbt.listTag
import dev.sleepyswords.nbt.stringTag
import dev.sleepyswords.piston.network.GameSession
import dev.sleepyswords.piston.network.packet.clientbound.configuration.RegistryDataPacket
import dev.sleepyswords.piston.network.packet.common.configuration.FinishConfigurationPacket
import dev.sleepyswords.piston.network.packet.serverbound.configuration.ClientInformationPacket

open class DamageType(
    val exhaustion: Float,
    val message_id: String,
    val scaling: String,
) {
    fun toNbt(): Nbt.CompoundTag =
        compoundTag {
            "exhaustion" - floatTag(exhaustion)
            "message_id" - stringTag(message_id)
            "scaling" - stringTag(scaling)
        }
}

object CactusDamageType : DamageType(0.1f, "cactus", "when_caused_by_living_non_player")

suspend fun handleClientInformationPacket(
    packet: ClientInformationPacket,
    session: GameSession,
) {
    session.writeServerPacket(
        RegistryDataPacket(
            "minecraft:dimension_type",
            listOf(
                Pair(
                    "minecraft:overword",
                    compoundTag {
                        put("ambient_light", Nbt.DoubleTag(0.0))
                        put("bed_works", Nbt.IntTag(1))
                        put("coordinate_scale", Nbt.DoubleTag(1.0))
                        put("effects", Nbt.StringTag("minecraft:overworld"))
                        put("has_ceiling", Nbt.IntTag(0))
                        put("has_raids", Nbt.IntTag(1))
                        put("has_skylight", Nbt.IntTag(1))
                        put("height", Nbt.IntTag(384))
                        put("infiniburn", Nbt.StringTag("#minecraft:infiniburn_overworld"))
                        put("logical_height", Nbt.IntTag(384))
                        put("min_y", Nbt.IntTag(-64))
                        put("monster_spawn_block_light_limit", Nbt.IntTag(0))
                        put(
                            "monster_spawn_light_level",
                            compoundTag {
                                put("max_inclusive", Nbt.IntTag(7))
                                put("min_inclusive", Nbt.IntTag(0))
                                put("type", Nbt.StringTag("minecraft:uniform"))
                            },
                        )
                        put("natural", Nbt.IntTag(1))
                        put("piglin_safe", Nbt.IntTag(0))
                        put("respawn_anchor_works", Nbt.IntTag(0))
                        put("ultrawarm", Nbt.IntTag(0))
                    },
                ),
            ),
        ),
    )
    session.writeServerPacket(
        RegistryDataPacket(
            "minecraft:painting_variant",
            listOf(
                Pair(
                    "minecraft:alban",
                    compoundTag {
                        put("asset_id", Nbt.StringTag("minecraft:alban"))
                        put("height", Nbt.IntTag(1))
                        put("width", Nbt.IntTag(1))
                    },
                ),
            ),
        ),
    )
    session.writeServerPacket(
        RegistryDataPacket(
            "minecraft:worldgen/biome",
            listOf(
                Pair(
                    "minecraft:plains",
                    compoundTag {
                        "carvers" -
                            listTag(Nbt.StringTagType) {
                                add(stringTag("minecraft:cave"))
                                add(stringTag("minecraft:cave_extra_underground"))
                                add(stringTag("minecraft:canyon"))
                            }
                        "downfall" - floatTag(0.4f)
                        "effects" -
                            compoundTag {
                                "fog_color" - intTag(12638463)
                                "mood_sound" -
                                    compoundTag {
                                        "block_search_extent" - intTag(8)
                                        "offset" - floatTag(2.0f)
                                        "sound" - stringTag("minecraft:ambient.cave")
                                        "tick_delay" - intTag(6000)
                                    }
                                "music_volume" - floatTag(1.0f)
                                "sky_color" - intTag(7907327)
                                "water_color" - intTag(4159204)
                                "water_fog_color" - intTag(329011)
                            }
                        "features" -
                            listTag(Nbt.ListTagType()) {
                                add(listTag(Nbt.StringTagType) { })
                                add(
                                    listTag(Nbt.StringTagType) {
                                        add(stringTag("minecraft:lake_lava_undergound"))
                                        add(stringTag("minecraft:lake_lava_surface"))
                                    },
                                )
                                add(listTag(Nbt.StringTagType) { })
                                add(listTag(Nbt.StringTagType) { })
                                add(listTag(Nbt.StringTagType) { })
                                add(listTag(Nbt.StringTagType) { })
                                add(listTag(Nbt.StringTagType) { })
                                add(listTag(Nbt.StringTagType) { })
                                add(listTag(Nbt.StringTagType) { })
                            }
                        "has_precipitation" - byteTag(1)
                        "spawn_costs" - compoundTag {}
                        "spawners" -
                            compoundTag {
                                "ambient" - listTag(Nbt.CompoundTagType) {}
                                "axolotls" - listTag(Nbt.CompoundTagType) {}
                                "creature" - listTag(Nbt.CompoundTagType) {}
                                "misc" - listTag(Nbt.CompoundTagType) {}
                                "underground_water_creature" - listTag(Nbt.CompoundTagType) {}
                                "water_ambient" - listTag(Nbt.CompoundTagType) {}
                                "water_creature" - listTag(Nbt.CompoundTagType) {}
                            }
                        "temperature" - Nbt.FloatTag(0.8f)
                    },
                ),
            ),
        ),
    )
    session.writeServerPacket(
        RegistryDataPacket(
            "minecraft:wolf_variant",
            listOf(
                Pair(
                    "minecraft:pale",
                    compoundTag {
                        "assets" -
                            compoundTag {
                                "angry" - stringTag("minecraft:entity/wolf/wolf_angry")
                                "tame" - stringTag("minecraft:entity/wolf/wolf_tame")
                                "wild" - stringTag("minecraft:entity/wolf/wolf")
                            }
                        "spawn_conditions" -
                            listTag(Nbt.CompoundTagType) {
                                add(
                                    compoundTag {
                                        "priority" - intTag(0)
                                    },
                                )
                            }
                    },
                ),
            ),
        ),
    )
    session.writeServerPacket(
        RegistryDataPacket(
            "minecraft:pig_variant",
            listOf(
                Pair(
                    "minecraft:temperate",
                    compoundTag {
                        "asset_id" - stringTag("minecraft:entity/pig/temperate_pig")
                        "spawn_conditions" -
                            listTag(Nbt.CompoundTagType) {
                                add(
                                    compoundTag {
                                        "priority" - intTag(0)
                                    },
                                )
                            }
                    },
                ),
            ),
        ),
    )
    session.writeServerPacket(
        RegistryDataPacket(
            "minecraft:cat_variant",
            listOf(
                Pair(
                    "minecraft:white",
                    compoundTag {
                        "asset_id" - stringTag("minecraft:entity/cat/white")
                        "spawn_conditions" -
                            listTag(Nbt.CompoundTagType) {
                                add(
                                    compoundTag {
                                        "priority" - intTag(0)
                                    },
                                )
                            }
                    },
                ),
            ),
        ),
    )
    session.writeServerPacket(
        RegistryDataPacket(
            "minecraft:chicken_variant",
            listOf(
                Pair(
                    "minecraft:temperate",
                    compoundTag {
                        "asset_id" - stringTag("minecraft:entity/chicken/temperate_white")
                        "spawn_conditions" -
                            listTag(Nbt.CompoundTagType) {
                                add(
                                    compoundTag {
                                        "priority" - intTag(0)
                                    },
                                )
                            }
                    },
                ),
            ),
        ),
    )
    session.writeServerPacket(
        RegistryDataPacket(
            "minecraft:cow_variant",
            listOf(
                Pair(
                    "minecraft:temperate",
                    compoundTag {
                        "asset_id" - stringTag("minecraft:entity/cow/temperate_cow")
                        "spawn_conditions" -
                            listTag(Nbt.CompoundTagType) {
                                add(
                                    compoundTag {
                                        "priority" - intTag(0)
                                    },
                                )
                            }
                    },
                ),
            ),
        ),
    )
    session.writeServerPacket(
        RegistryDataPacket(
            "minecraft:frog_variant",
            listOf(
                Pair(
                    "minecraft:temperate2",
                    compoundTag {
                        "asset_id" - stringTag("minecraft:entity/frog/temperate_frog")
                        "spawn_conditions" -
                            listTag(Nbt.CompoundTagType) {
                                add(
                                    compoundTag {
                                        "priority" - intTag(0)
                                    },
                                )
                            }
                    },
                ),
            ),
        ),
    )
    session.writeServerPacket(
        RegistryDataPacket(
            "minecraft:wolf_sound_variant",
            listOf(
                Pair(
                    "minecraft:cute",
                    compoundTag {
                        "ambient_sound" - stringTag("minecraft:entity.wolf_cute.ambient")
                        "death_sound" - stringTag("minecraft:entity.wolf_cute.death")
                        "growl_sound" - stringTag("minecraft:entity.wolf_cute.growl")
                        "hurt_sound" - stringTag("minecraft:entity.wolf_cute.hurt")
                        "pant_sound" - stringTag("minecraft:entity.wolf_cute.pant")
                        "whine_sound" - stringTag("minecraft:entity.wolf_cute.whine")
                    },
                ),
            ),
        ),
    )

    val requiredDamageTypes =
        listOf(
            "minecraft:cactus",
            "minecraft:campfire",
            "minecraft:cramming",
            "minecraft:dragon_breath",
            "minecraft:drown",
            "minecraft:dry_out",
            "minecraft:ender_pearl",
            "minecraft:fall",
            "minecraft:fly_into_wall",
            "minecraft:freeze",
            "minecraft:generic",
            "minecraft:generic_kill",
            "minecraft:hot_floor",
            "minecraft:in_fire",
            "minecraft:in_wall",
            "minecraft:lava",
            "minecraft:lightning_bolt",
            "minecraft:magic",
            "minecraft:on_fire",
            "minecraft:out_of_world",
            "minecraft:outside_border",
            "minecraft:stalagmite",
            "minecraft:starve",
            "minecraft:sweet_berry_bush",
            "minecraft:wither",
        )

    for (requiredDamageType in requiredDamageTypes) {
        session.writeServerPacket(
            RegistryDataPacket(
                "minecraft:damage_type",
                listOf(
                    requiredDamageType to CactusDamageType.toNbt(),
                ),
            ),
        )
    }
    session.writeServerPacket(FinishConfigurationPacket())
}
