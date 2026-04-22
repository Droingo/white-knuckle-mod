package net.droingo.whiteknuckle.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class RisingThreatSourceBlockEntity extends BlockEntity {
    private int tickCounter = 0;
    private int currentLayer = 1;
    private int pausedTicks = 0;
    private boolean playerTooClose = false;
    private boolean pauseUsed = false;

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
    public int getPausedTicks() {
        return pausedTicks;
    }

    public void incrementPausedTicks() {
        this.pausedTicks++;
        markDirty();
    }

    public void resetPausedTicks() {
        this.pausedTicks = 0;
        markDirty();
    }

    public boolean isPlayerTooClose() {
        return playerTooClose;
    }

    public void setPlayerTooClose(boolean playerTooClose) {
        this.playerTooClose = playerTooClose;
        markDirty();
    }
    public boolean isPauseUsed() {
        return pauseUsed;
    }

    private boolean linkedPause = false;

    public void setPauseUsed(boolean pauseUsed) {
        this.pauseUsed = pauseUsed;
        markDirty();
    }

    public boolean isLinkedPause() {
        return linkedPause;
    }

    public void setLinkedPause(boolean linkedPause) {
        this.linkedPause = linkedPause;
        markDirty();
    }
}