package net.droingo.whiteknuckle.item.custom;

import net.droingo.whiteknuckle.block.ModBlocks;
import net.droingo.whiteknuckle.block.custom.PitonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.droingo.whiteknuckle.block.custom.SteamVentBlock;

public class PitonItem extends Item {
    public PitonItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() == null) {
            return ActionResult.PASS;
        }

        return placePiton(
                context.getWorld(),
                context.getPlayer(),
                context.getBlockPos(),
                context.getSide(),
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

        return placePiton(
                world,
                user,
                hitResult.getBlockPos(),
                hitResult.getSide(),
                stack
        );
    }

    private ActionResult placePiton(World world, PlayerEntity player, BlockPos clickedPos, Direction side, ItemStack stack) {
        BlockPos placePos = clickedPos.offset(side);
        BlockState clickedState = world.getBlockState(clickedPos);

        if (clickedState.getBlock() instanceof SteamVentBlock) {
            return ActionResult.PASS;
        }

        if (!world.getBlockState(placePos).isAir()) {
            return ActionResult.PASS;
        }

        BlockState state = ModBlocks.PITON.getDefaultState()
                .with(PitonBlock.FACING, side)
                .with(PitonBlock.HITS, 0);

        if (!state.canPlaceAt(world, placePos)) {
            return ActionResult.PASS;
        }

        if (!world.isClient()) {
            world.setBlockState(placePos, state, Block.NOTIFY_ALL);
            world.playSound(
                    null,
                    placePos,
                    SoundEvents.BLOCK_CHAIN_PLACE,
                    SoundCategory.BLOCKS,
                    0.6f,
                    1.0f
            );

            if (!player.isCreative()) {
                stack.decrement(1);
            }
        }

        return ActionResult.SUCCESS;
    }
}