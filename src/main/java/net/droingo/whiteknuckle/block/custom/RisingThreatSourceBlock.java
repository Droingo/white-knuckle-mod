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
        int radius = 7;
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

            if (sourceBe.getTickCounter() >= 40) { // THIS CHANGES THE SPEED
                sourceBe.resetTickCounter();



                int radius = 7;

                int currentTopY = p.getY() + 1;
                while (w.getBlockState(new BlockPos(p.getX(), currentTopY, p.getZ())).isOf(net.droingo.whiteknuckle.block.ModBlocks.RISING_THREAT)) {
                    currentTopY++;
                }



                BlockPos center = new BlockPos(p.getX(), currentTopY, p.getZ());

                // 🔊 SOUND AT THE TOP (this is important)
                w.playSound(
                        null,
                        center,
                        SoundEvents.BLOCK_LAVA_EXTINGUISH,
                        SoundCategory.BLOCKS,
                        0.2f,
                        0.8f + w.random.nextFloat() * 0.3f
                );

                w.playSound(
                        null,
                        center,
                        SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP,
                        SoundCategory.BLOCKS,
                        0.6f,
                        1.0f + w.random.nextFloat() * 0.5f
                );


                if (w instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                    serverWorld.spawnParticles(
                            net.minecraft.particle.ParticleTypes.SOUL,
                            center.getX() + 0.5,
                            center.getY() + 1,
                            center.getZ() + 0.5,
                            35,
                            3.5, 0.2, 3.5,
                            0.01
                    );
                }

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

                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + z * z <= radius * radius) {
                            BlockPos placePos = center.add(x, 0, z);

                            if (w.getBlockState(placePos).isAir()) {
                                w.setBlockState(placePos, net.droingo.whiteknuckle.block.ModBlocks.RISING_THREAT.getDefaultState());
                            }
                        }
                    }
                }
            }
        } : null;
    }
}