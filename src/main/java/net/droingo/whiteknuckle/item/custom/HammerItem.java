package net.droingo.whiteknuckle.item.custom;

import net.droingo.whiteknuckle.block.custom.PitonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class HammerItem extends Item {
    public HammerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() == null) {
            return ActionResult.PASS;
        }

        return hammerPiton(
                context.getWorld(),
                context.getPlayer(),
                context.getBlockPos(),
                context.getStack()
        );
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        Vec3d start = user.getCameraPosVec(1.0f);
        Vec3d direction = user.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(5.0));

        BlockHitResult hitResult = world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                user
        ));

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return ActionResult.PASS;
        }

        return hammerPiton(world, user, hitResult.getBlockPos(), stack);
    }

    private ActionResult hammerPiton(World world, PlayerEntity player, BlockPos pos, ItemStack stack) {
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof PitonBlock)) {
            return ActionResult.PASS;
        }

        int hits = state.get(PitonBlock.HITS);

        if (!world.isClient()) {
            float pitch = 1.6f + world.random.nextFloat() * 0.3f;
            world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.35f, pitch);
        }

        if (hits >= 3) {
            return ActionResult.SUCCESS;
        }

        int newHits = hits + 1;

        if (!world.isClient()) {
            world.setBlockState(pos, state.with(PitonBlock.HITS, newHits));

            if (newHits == 3) {
                Direction facing = state.get(PitonBlock.FACING);
                BlockPos attachedBlockPos = pos.offset(facing.getOpposite());

                world.setBlockState(attachedBlockPos, Blocks.COBBLESTONE.getDefaultState());

                float finalPitch = 0.9f + world.random.nextFloat() * 0.2f;
                world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.6f, finalPitch);

                if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                    double particleX = attachedBlockPos.getX() + 0.5 + facing.getOffsetX() * 0.5;
                    double particleY = attachedBlockPos.getY() + 0.5 + facing.getOffsetY() * 0.5;
                    double particleZ = attachedBlockPos.getZ() + 0.5 + facing.getOffsetZ() * 0.5;

                    serverWorld.spawnParticles(
                            new net.minecraft.particle.BlockStateParticleEffect(
                                    net.minecraft.particle.ParticleTypes.BLOCK,
                                    Blocks.COBBLESTONE.getDefaultState()
                            ),
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

        return ActionResult.SUCCESS;
    }
}