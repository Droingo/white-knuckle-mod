package net.droingo.whiteknuckle.item;

import net.droingo.whiteknuckle.WhiteKnuckle;
import net.droingo.whiteknuckle.item.custom.PitonItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.droingo.whiteknuckle.item.custom.RebarItem;
import net.droingo.whiteknuckle.item.custom.HammerItem;


public class ModItems {
    public static final Item GOLDEN_ROACH = registerItem("golden_roach",
            new Item(new Item.Settings().registryKey(
                    RegistryKey.of(RegistryKeys.ITEM, Identifier.of(WhiteKnuckle.MOD_ID, "golden_roach"))
            ))
    );

    public static final Item HAMMER = registerItem("hammer",
            new HammerItem(new Item.Settings().registryKey(
                    RegistryKey.of(RegistryKeys.ITEM, Identifier.of(WhiteKnuckle.MOD_ID, "hammer"))
            ))
    );

    public static final Item REBAR = registerItem("rebar",
            new RebarItem(new Item.Settings().registryKey(
                    RegistryKey.of(RegistryKeys.ITEM, Identifier.of(WhiteKnuckle.MOD_ID, "rebar"))
            ))
    );

    public static final Item PITON = registerItem("piton",
            new PitonItem(new Item.Settings().registryKey(
                    RegistryKey.of(RegistryKeys.ITEM, Identifier.of(WhiteKnuckle.MOD_ID, "piton"))
            ))
    );

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(WhiteKnuckle.MOD_ID, name), item);
    }

    public static void registerModItems() {
        WhiteKnuckle.LOGGER.info("Registering Mod Items for " + WhiteKnuckle.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(GOLDEN_ROACH);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(HAMMER);
            entries.add(REBAR);
            entries.add(PITON);
        });
    }
}