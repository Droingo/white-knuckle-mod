package net.droingo.whiteknuckle.block.entity;

import net.droingo.whiteknuckle.WhiteKnuckle;
import net.droingo.whiteknuckle.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<RisingThreatSourceBlockEntity> RISING_THREAT_SOURCE_BE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(WhiteKnuckle.MOD_ID, "rising_threat_source"),
                    FabricBlockEntityTypeBuilder.create(
                            RisingThreatSourceBlockEntity::new,
                            ModBlocks.RISING_THREAT_SOURCE
                    ).build()
            );

    public static void registerModBlockEntities() {
        WhiteKnuckle.LOGGER.info("Registering Mod Block Entities for " + WhiteKnuckle.MOD_ID);
    }
}