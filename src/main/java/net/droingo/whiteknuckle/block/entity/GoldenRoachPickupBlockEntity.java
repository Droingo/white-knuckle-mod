package net.droingo.whiteknuckle.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.droingo.whiteknuckle.block.custom.GoldenRoachPickupBlock;

public class GoldenRoachPickupBlockEntity extends BlockEntity {
    private boolean available = true;
    private int respawnTicks = 0;

    public GoldenRoachPickupBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GOLDEN_ROACH_PICKUP_BE, pos, state);
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
        markDirty();
    }

    public int getRespawnTicks() {
        return respawnTicks;
    }

    public void setRespawnTicks(int respawnTicks) {
        this.respawnTicks = respawnTicks;
        markDirty();
    }

    public void decrementRespawnTicks() {
        if (this.respawnTicks > 0) {
            this.respawnTicks--;
            markDirty();
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, GoldenRoachPickupBlockEntity blockEntity) {
        if (world.isClient()) {
            return;
        }

        if (!blockEntity.isAvailable() && blockEntity.getRespawnTicks() > 0) {
            blockEntity.decrementRespawnTicks();

            if (blockEntity.getRespawnTicks() <= 0) {
                blockEntity.setAvailable(true);
                world.setBlockState(pos, state.with(GoldenRoachPickupBlock.AVAILABLE, true));
            }
        }
    }
}