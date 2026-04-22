package net.droingo.whiteknuckle.block.custom;

import com.mojang.serialization.MapCodec;
import net.droingo.whiteknuckle.block.ModBlocks;
import net.droingo.whiteknuckle.block.entity.ModBlockEntities;
import net.droingo.whiteknuckle.block.entity.RisingThreatSourceBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.math.Box;

public class RisingThreatSourceBlock extends BlockWithEntity {
    public static final MapCodec<RisingThreatSourceBlock> CODEC = createCodec(RisingThreatSourceBlock::new);

    public RisingThreatSourceBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends RisingThreatSourceBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RisingThreatSourceBlockEntity(pos, state);
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        int radius = 8;
        int maxHeight = world.getTopYInclusive();

        for (int y = pos.getY() + 1; y <= maxHeight; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radius * radius) {
                        BlockPos checkPos = new BlockPos(pos.getX() + x, y, pos.getZ() + z);

                        if (world.getBlockState(checkPos).isOf(ModBlocks.RISING_THREAT)) {
                            world.setBlockState(checkPos, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }
        }

        super.onStateReplaced(state, world, pos, moved);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.RISING_THREAT_SOURCE_BE ? (w, p, s, be) -> {
            if (!(be instanceof RisingThreatSourceBlockEntity sourceBe)) {
                return;
            }

            sourceBe.incrementTickCounter();

            int riseDelay = 80 + w.random.nextInt(5);

            int radius = 8;
            BlockPos center = new BlockPos(p.getX(), p.getY() + sourceBe.getCurrentLayer(), p.getZ());

            boolean playerTooClose = false;

            for (PlayerEntity player : w.getPlayers()) {
                BlockPos playerPos = player.getBlockPos();

                int dx = playerPos.getX() - center.getX();
                int dz = playerPos.getZ() - center.getZ();
                boolean insideCone = (dx * dx + dz * dz) <= (radius * radius);

                int verticalGap = playerPos.getY() - center.getY();

                if (insideCone && verticalGap >= 0 && verticalGap <= 25) {
                    playerTooClose = true;
                    break;
                }
            }

            sourceBe.setPlayerTooClose(playerTooClose);

            boolean linkedPause = false;

            for (int x = p.getX() - 80; x <= p.getX() + 80; x++) {
                for (int z = p.getZ() - 80; z <= p.getZ() + 80; z++) {
                    BlockPos nearbyPos = new BlockPos(x, p.getY(), z);

                    if (nearbyPos.equals(p)) {
                        continue;
                    }

                    if (w.getBlockEntity(nearbyPos) instanceof RisingThreatSourceBlockEntity nearbySourceBe) {
                        if (nearbySourceBe.isPlayerTooClose()) {
                            linkedPause = true;
                            break;
                        }
                    }
                }

                if (linkedPause) {
                    break;
                }
            }

            sourceBe.setLinkedPause(linkedPause);

            boolean shouldPause = playerTooClose || sourceBe.isLinkedPause();

            if (shouldPause && !sourceBe.isPauseUsed()) {
                if (sourceBe.getPausedTicks() < 300) {
                    sourceBe.incrementPausedTicks();
                    sourceBe.resetTickCounter();
                    return;
                } else {
                    sourceBe.setPauseUsed(true);
                }
            }

            if (!shouldPause) {
                if (sourceBe.getPausedTicks() > 0) {
                    sourceBe.resetPausedTicks();
                }
                if (sourceBe.isPauseUsed()) {
                    sourceBe.setPauseUsed(false);
                }
            }

            if (sourceBe.getTickCounter() >= riseDelay) {
                sourceBe.resetTickCounter();
                sourceBe.resetPausedTicks();

                radius = 8;
                center = new BlockPos(p.getX(), p.getY() + sourceBe.getCurrentLayer(), p.getZ());

                // stopper check on the next layer
                boolean touchedStopper = false;

                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + z * z <= radius * radius) {
                            BlockPos checkPos = center.add(x, 0, z);

                            if (w.getBlockState(checkPos).isOf(net.minecraft.block.Blocks.OBSIDIAN)) {
                                touchedStopper = true;
                                break;
                            }
                        }
                    }
                    if (touchedStopper) {
                        break;
                    }
                }

                if (touchedStopper) {
                    return;
                }

                // sound at the active rising layer
                w.playSound(
                        null,
                        center,
                        net.minecraft.sound.SoundEvents.BLOCK_LAVA_EXTINGUISH,
                        net.minecraft.sound.SoundCategory.BLOCKS,
                        0.7f,
                        0.8f + w.random.nextFloat() * 0.3f
                );

                w.playSound(
                        null,
                        center,
                        net.minecraft.sound.SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP,
                        net.minecraft.sound.SoundCategory.BLOCKS,
                        0.4f,
                        1.0f + w.random.nextFloat() * 0.5f
                );

                if (w instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                    serverWorld.spawnParticles(
                            net.minecraft.particle.ParticleTypes.SOUL,
                            center.getX() + 0.5,
                            center.getY() + 0.7,
                            center.getZ() + 0.5,
                            35,
                            3.5, 0.2, 3.5,
                            0.01
                    );
                }

                // place only into air, but ALWAYS advance the layer
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + z * z <= radius * radius) {
                            BlockPos placePos = center.add(x, 0, z);

                            boolean hasItemFrame = !w.getEntitiesByClass(
                                    ItemFrameEntity.class,
                                    new Box(placePos),
                                    entity -> true
                            ).isEmpty();

                            if (w.getBlockState(placePos).isAir() && !hasItemFrame) {
                                w.setBlockState(placePos, ModBlocks.RISING_THREAT.getDefaultState());
                            }
                        }
                    }
                }

                sourceBe.advanceLayer();
            }
        } : null;
    }
}