package net.droingo.whiteknuckle.block;

import net.droingo.whiteknuckle.WhiteKnuckle;
import net.droingo.whiteknuckle.block.custom.PitonBlock;
import net.droingo.whiteknuckle.block.custom.RebarBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block PITON = registerBlockWithItem("piton",
            new PitonBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS)
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(WhiteKnuckle.MOD_ID, "piton")))
                    .nonOpaque()));

    public static final Block REBAR = registerBlockWithoutItem("rebar",
            new RebarBlock(AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(WhiteKnuckle.MOD_ID, "rebar")))
                    .noCollision()
                    .nonOpaque()
                    .strength(0.5f)
            ));

    private static Block registerBlockWithItem(String name, Block block) {
        Identifier id = Identifier.of(WhiteKnuckle.MOD_ID, name);

        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block,
                new Item.Settings().registryKey(
                        RegistryKey.of(RegistryKeys.ITEM, id)
                )));

        return block;
    }

    private static Block registerBlockWithoutItem(String name, Block block) {
        Identifier id = Identifier.of(WhiteKnuckle.MOD_ID, name);
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void registerModBlocks() {
        WhiteKnuckle.LOGGER.info("Registering Mod Blocks for " + WhiteKnuckle.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(PITON);
        });
    }
}