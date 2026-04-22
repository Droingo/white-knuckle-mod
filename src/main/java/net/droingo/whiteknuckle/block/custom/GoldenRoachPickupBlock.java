package net.droingo.whiteknuckle.block.custom;

import com.mojang.serialization.MapCodec;
import net.droingo.whiteknuckle.block.entity.GoldenRoachPickupBlockEntity;
import net.droingo.whiteknuckle.block.entity.ModBlockEntities;
import net.droingo.whiteknuckle.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class GoldenRoachPickupBlock extends BlockWithEntity {
    public static final MapCodec<GoldenRoachPickupBlock> CODEC = createCodec(GoldenRoachPickupBlock::new);

    public static final BooleanProperty AVAILABLE = Properties.ENABLED;
    public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape ROACH_SHAPE = Block.createCuboidShape(5, 0, 5, 11, 4, 11);

    public GoldenRoachPickupBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(AVAILABLE, true)
                .with(FACE, BlockFace.FLOOR)
                .with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AVAILABLE, FACE, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction side = ctx.getSide();
        BlockFace face;
        Direction facing;

        if (side == Direction.UP) {
            face = BlockFace.FLOOR;
            facing = ctx.getHorizontalPlayerFacing().getOpposite();
        } else if (side == Direction.DOWN) {
            face = BlockFace.CEILING;
            facing = ctx.getHorizontalPlayerFacing().getOpposite();
        } else {
            face = BlockFace.WALL;
            facing = side;
        }

        return this.getDefaultState()
                .with(AVAILABLE, true)
                .with(FACE, face)
                .with(FACING, facing);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos supportPos;
        Direction supportFace;

        switch (state.get(FACE)) {
            case FLOOR -> {
                supportPos = pos.down();
                supportFace = Direction.UP;
            }
            case CEILING -> {
                supportPos = pos.up();
                supportFace = Direction.DOWN;
            }
            case WALL -> {
                supportPos = pos.offset(state.get(FACING).getOpposite());
                supportFace = state.get(FACING);
            }
            default -> {
                return false;
            }
        }

        return world.getBlockState(supportPos).isSideSolidFullSquare(world, supportPos, supportFace);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(AVAILABLE) ? ROACH_SHAPE : VoxelShapes.empty();
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GoldenRoachPickupBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.GOLDEN_ROACH_PICKUP_BE, GoldenRoachPickupBlockEntity::tick);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (world.getBlockEntity(pos) instanceof GoldenRoachPickupBlockEntity roachBe) {
            if (!roachBe.isAvailable()) {
                return ActionResult.CONSUME;
            }

            player.giveItemStack(new ItemStack(ModItems.GOLDEN_ROACH));
            roachBe.setAvailable(false);
            roachBe.setRespawnTicks(6000);

            BlockState newState = state.with(AVAILABLE, false);
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}