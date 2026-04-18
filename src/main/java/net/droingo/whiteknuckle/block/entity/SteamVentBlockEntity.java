package net.droingo.whiteknuckle.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SteamVentBlockEntity extends BlockEntity {
    private int tickCounter = 0;

    public SteamVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STEAM_VENT_BE, pos, state);
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public void incrementTickCounter() {
        this.tickCounter++;
        markDirty();
    }

    public void resetTickCounter() {
        this.tickCounter = 0;
        markDirty();
    }
}