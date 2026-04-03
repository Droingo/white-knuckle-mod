package net.droingo.whiteknuckle.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class RebarBlock extends Block {
    public static final MapCodec<RebarBlock> CODEC = createCodec(RebarBlock::new);

    public static final EnumProperty<Direction> FACING = Properties.FACING;

    public RebarBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<? extends RebarBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> Block.createCuboidShape(7, 7, 8, 9, 9, 16);
            case SOUTH -> Block.createCuboidShape(7, 7, 0, 9, 9, 8);
            case WEST  -> Block.createCuboidShape(8, 7, 7, 16, 9, 9);
            case EAST  -> Block.createCuboidShape(0, 7, 7, 8, 9, 9);
            case UP    -> Block.createCuboidShape(7, 0, 7, 9, 8, 9);
            case DOWN  -> Block.createCuboidShape(7, 8, 7, 9, 16, 9);
        };
    }
}