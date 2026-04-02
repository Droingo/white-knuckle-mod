package net.droingo.whiteknuckle.item.custom;

import net.droingo.whiteknuckle.block.custom.PitonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class HammerItem extends Item {
    public HammerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof PitonBlock)) {
            return ActionResult.PASS;
        }

        int hits = state.get(PitonBlock.HITS);
        if (hits >= 3) {
            return ActionResult.SUCCESS;
        }

        int newHits = hits + 1;

        if (!world.isClient()) {
            float pitch = 1.6f + world.random.nextFloat() * 0.3f;
            world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.35f, pitch);
            world.setBlockState(pos, state.with(PitonBlock.HITS, newHits));

            if (newHits == 3) {
                Direction facing = state.get(PitonBlock.FACING);
                BlockPos attachedBlockPos = pos.offset(facing.getOpposite());

                world.setBlockState(attachedBlockPos, Blocks.COBBLESTONE.getDefaultState());

                float finalPitch = 0.9f + world.random.nextFloat() * 0.2f;
                world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.6f, finalPitch);

                if (world instanceof ServerWorld serverWorld) {
                    double particleX = attachedBlockPos.getX() + 0.5 + facing.getOffsetX() * 0.5;
                    double particleY = attachedBlockPos.getY() + 0.5 + facing.getOffsetY() * 0.5;
                    double particleZ = attachedBlockPos.getZ() + 0.5 + facing.getOffsetZ() * 0.5;

                    serverWorld.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.COBBLESTONE.getDefaultState()),
                            particleX,
                            particleY,
                            particleZ,
                            12,
                            0.12, 0.12, 0.12,
                            0.03
                    );
                }
            }
        }
    return ActionResult.SUCCESS;}
}