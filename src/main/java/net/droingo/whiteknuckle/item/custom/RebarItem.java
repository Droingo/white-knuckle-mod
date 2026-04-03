package net.droingo.whiteknuckle.item.custom;

import net.droingo.whiteknuckle.block.ModBlocks;
import net.droingo.whiteknuckle.block.custom.RebarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class RebarItem extends Item {
    public RebarItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        Vec3d start = user.getCameraPosVec(1.0f);
        Vec3d direction = user.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(20.0));

        BlockHitResult hitResult = world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                user
        ));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            if (!world.isClient()) {
                if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof net.droingo.whiteknuckle.block.custom.PitonBlock) {
                    return ActionResult.PASS;
                }
                
                float throwPitch = 0.9f + world.random.nextFloat() * 0.2f;
                world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_TRIDENT_THROW.value(), SoundCategory.PLAYERS, 0.5f, throwPitch);

                Vec3d hitPosVec = hitResult.getPos();
                Vec3d trail = hitPosVec.subtract(start);
                int steps = 20;

                if (world instanceof ServerWorld serverWorld) {
                    for (int i = 0; i <= steps; i++) {
                        double progress = (double) i / steps;
                        Vec3d point = start.add(trail.multiply(progress));

                        serverWorld.spawnParticles(
                                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.IRON_BLOCK.getDefaultState()),
                                point.x,
                                point.y,
                                point.z,
                                1,
                                0.0, 0.0, 0.0,
                                0.0
                        );
                    }
                }

                float impactPitch = 0.8f + world.random.nextFloat() * 0.2f;
                Direction face = hitResult.getSide();
                Vec3d impactPos = hitResult.getPos().add(
                        face.getOffsetX() * 0.1,
                        face.getOffsetY() * 0.1,
                        face.getOffsetZ() * 0.1
                );

                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.COBBLESTONE.getDefaultState()),
                            impactPos.x,
                            impactPos.y,
                            impactPos.z,
                            20,
                            0.15, 0.15, 0.15,
                            0.2
                    );
                }

                world.playSound(null, hitResult.getBlockPos(), SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.BLOCKS, 0.6f, impactPitch);

                BlockPos hitPos = hitResult.getBlockPos();

                world.setBlockState(hitPos, Blocks.COBBLESTONE.getDefaultState());

                BlockPos placePos = hitPos.offset(face);

                if (world.getBlockState(placePos).isAir()) {
                    world.setBlockState(placePos,
                            ModBlocks.REBAR.getDefaultState()
                                    .with(RebarBlock.FACING, face)
                    );
                }

                if (!user.isCreative()) {
                    stack.decrement(1);
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}