package net.droingo.whiteknuckle;

import net.droingo.whiteknuckle.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;

public class WhiteKnuckleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(ModBlocks.GOLDEN_ROACH_PICKUP, BlockRenderLayer.CUTOUT);
    }
}