package net.droingo.whiteknuckle.item;

import net.droingo.whiteknuckle.WhiteKnuckle;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item GOLDEN_ROACH = registerItem(
            new Item(new Item.Settings().registryKey(
                    RegistryKey.of(RegistryKeys.ITEM, Identifier.of(WhiteKnuckle.MOD_ID, "golden_roach"))
            ))
    );


    private static Item registerItem(Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(WhiteKnuckle.MOD_ID, "golden_roach"), item);
    }

    public static void registerModItems() {
        WhiteKnuckle.LOGGER.info("Registering Mod Items for " + WhiteKnuckle.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(GOLDEN_ROACH);
        });
    }
}
