package net.droingo.whiteknuckle.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class PitonBlock extends Block {
    public static final MapCodec<PitonBlock> CODEC = createCodec(PitonBlock::new);

    public static final EnumProperty<Direction> FACING = Properties.FACING;
    public static final IntProperty HITS = IntProperty.of("hits", 0, 3);

    public PitonBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(HITS, 0));
    }

    @Override
    public MapCodec<? extends PitonBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HITS);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getSide())
                .with(HITS, 0);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction facing = state.get(FACING);
        BlockPos supportPos = pos.offset(facing.getOpposite());
        return world.getBlockState(supportPos).isSideSolidFullSquare(world, supportPos, facing);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state,
            WorldView world,
            ScheduledTickView tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            Random random
    ) {
        Direction facing = state.get(FACING);
        BlockPos supportPos = pos.offset(facing.getOpposite());

        if (!world.getBlockState(supportPos).isSideSolidFullSquare(world, supportPos, facing)) {
            return Blocks.AIR.getDefaultState();
        }

        return state;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        int hits = state.get(HITS);

        return switch (facing) {
            case NORTH -> switch (hits) {
                case 1 -> Block.createCuboidShape(6.5, 4.0, 13.5, 9.5, 11.5, 16.0);
                case 2 -> Block.createCuboidShape(6.5, 4.0, 14.5, 9.5, 11.5, 16.0);
                case 3 -> Block.createCuboidShape(6.5, 4.0, 15.25, 9.5, 11.5, 16.0);
                default -> Block.createCuboidShape(6.5, 4.0, 12.0, 9.5, 11.5, 16.0);
            };
            case SOUTH -> switch (hits) {
                case 1 -> Block.createCuboidShape(6.5, 4.0, 0.0, 9.5, 11.5, 2.5);
                case 2 -> Block.createCuboidShape(6.5, 4.0, 0.0, 9.5, 11.5, 1.5);
                case 3 -> Block.createCuboidShape(6.5, 4.0, 0.0, 9.5, 11.5, 0.75);
                default -> Block.createCuboidShape(6.5, 4.0, 0.0, 9.5, 11.5, 4.0);
            };
            case WEST -> switch (hits) {
                case 1 -> Block.createCuboidShape(13.5, 4.0, 6.5, 16.0, 11.5, 9.5);
                case 2 -> Block.createCuboidShape(14.5, 4.0, 6.5, 16.0, 11.5, 9.5);
                case 3 -> Block.createCuboidShape(15.25, 4.0, 6.5, 16.0, 11.5, 9.5);
                default -> Block.createCuboidShape(12.0, 4.0, 6.5, 16.0, 11.5, 9.5);
            };
            case EAST -> switch (hits) {
                case 1 -> Block.createCuboidShape(0.0, 4.0, 6.5, 2.5, 11.5, 9.5);
                case 2 -> Block.createCuboidShape(0.0, 4.0, 6.5, 1.5, 11.5, 9.5);
                case 3 -> Block.createCuboidShape(0.0, 4.0, 6.5, 0.75, 11.5, 9.5);
                default -> Block.createCuboidShape(0.0, 4.0, 6.5, 4.0, 11.5, 9.5);
            };
            case UP -> switch (hits) {
                case 1 -> Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 2.5, 9.5);
                case 2 -> Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 1.5, 9.5);
                case 3 -> Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 0.75, 9.5);
                default -> Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 4.0, 9.5);
            };
            case DOWN -> switch (hits) {
                case 1 -> Block.createCuboidShape(6.5, 13.5, 6.5, 9.5, 16.0, 9.5);
                case 2 -> Block.createCuboidShape(6.5, 14.5, 6.5, 9.5, 16.0, 9.5);
                case 3 -> Block.createCuboidShape(6.5, 15.25, 6.5, 9.5, 16.0, 9.5);
                default -> Block.createCuboidShape(6.5, 12.0, 6.5, 9.5, 16.0, 9.5);
            };
        };
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(HITS) >= 3) {
            return VoxelShapes.empty();
        }
        return getOutlineShape(state, world, pos, context);
    }
}