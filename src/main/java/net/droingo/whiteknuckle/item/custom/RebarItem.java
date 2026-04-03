package net.droingo.whiteknuckle.item.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

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
                float throwPitch = 0.9f + world.random.nextFloat() * 0.2f;
                world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_TRIDENT_THROW.value(), SoundCategory.PLAYERS, 0.5f, throwPitch);
                Vec3d hitPos = hitResult.getPos();
                Vec3d trail = hitPos.subtract(start);
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
                world.playSound(null, hitResult.getBlockPos(), SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.BLOCKS, 0.6f, impactPitch);

                world.setBlockState(hitResult.getBlockPos(), Blocks.COBBLESTONE.getDefaultState());

                if (!user.isCreative()) {
                    stack.decrement(1);
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}