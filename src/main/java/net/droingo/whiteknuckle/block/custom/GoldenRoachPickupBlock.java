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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class GoldenRoachPickupBlock extends BlockWithEntity {
    public static final MapCodec<GoldenRoachPickupBlock> CODEC = createCodec(GoldenRoachPickupBlock::new);

    public static final BooleanProperty AVAILABLE = Properties.ENABLED;
    public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape FLOOR_SHAPE = Block.createCuboidShape(5, 0, 5, 11, 2, 11);
    private static final VoxelShape CEILING_SHAPE = Block.createCuboidShape(5, 14, 5, 11, 16, 11);
    private static final VoxelShape WALL_NORTH_SHAPE = Block.createCuboidShape(5, 5, 14, 11, 11, 16);
    private static final VoxelShape WALL_SOUTH_SHAPE = Block.createCuboidShape(5, 5, 0, 11, 11, 2);
    private static final VoxelShape WALL_EAST_SHAPE = Block.createCuboidShape(0, 5, 5, 2, 11, 11);
    private static final VoxelShape WALL_WEST_SHAPE = Block.createCuboidShape(14, 5, 5, 16, 11, 11);

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
        Direction facing = Direction.Type.HORIZONTAL.random(ctx.getWorld().random);

        if (side == Direction.UP) {
            face = BlockFace.FLOOR;
        } else if (side == Direction.DOWN) {
            face = BlockFace.CEILING;
        } else {
            face = BlockFace.WALL;
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
        if (!state.get(AVAILABLE)) {
            return VoxelShapes.empty();
        }

        return switch (state.get(FACE)) {
            case FLOOR -> FLOOR_SHAPE;
            case CEILING -> CEILING_SHAPE;
            case WALL -> {
                Direction facing = state.get(FACING);
                yield switch (facing) {
                    case NORTH -> WALL_NORTH_SHAPE;
                    case SOUTH -> WALL_SOUTH_SHAPE;
                    case EAST -> WALL_EAST_SHAPE;
                    case WEST -> WALL_WEST_SHAPE;
                    default -> VoxelShapes.empty();
                };
            }
        };
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
            world.playSound(
                    null,
                    pos,
                    SoundEvents.ENTITY_ITEM_PICKUP,
                    SoundCategory.BLOCKS,
                    0.5f,
                    1.4f
            );
            roachBe.setAvailable(false);
            roachBe.setRespawnTicks(6000);

            BlockState newState = state.with(AVAILABLE, false);
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}