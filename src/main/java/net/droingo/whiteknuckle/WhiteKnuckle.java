package net.droingo.whiteknuckle;

import net.droingo.whiteknuckle.block.ModBlocks;
import net.droingo.whiteknuckle.block.entity.ModBlockEntities;
import net.droingo.whiteknuckle.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteKnuckle implements ModInitializer {
	public static final String MOD_ID = "whiteknuckle";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerModBlockEntities();
	}
}