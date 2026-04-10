package net.droingo.whiteknuckle.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class RisingThreatSourceBlockEntity extends BlockEntity {
    private int tickCounter = 0;
    private int currentLayer = 1;

    public RisingThreatSourceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RISING_THREAT_SOURCE_BE, pos, state);
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public void incrementTickCounter() {
        this.tickCounter++;
    }

    public void resetTickCounter() {
        this.tickCounter = 0;
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public void advanceLayer() {
        this.currentLayer++;
        markDirty();
    }
}