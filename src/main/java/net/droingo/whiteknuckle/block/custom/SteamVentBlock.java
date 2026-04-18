package net.droingo.whiteknuckle.block.custom;

import com.mojang.serialization.MapCodec;
import net.droingo.whiteknuckle.block.entity.ModBlockEntities;
import net.droingo.whiteknuckle.block.entity.SteamVentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import static net.droingo.whiteknuckle.block.custom.SteamVentBlock.ACTIVE;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SteamVentBlock extends BlockWithEntity {
    public static final MapCodec<SteamVentBlock> CODEC = createCodec(SteamVentBlock::new);

    public static final EnumProperty<Direction> FACING = Properties.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    private static final VoxelShape NORTH_SHAPE = net.minecraft.block.Block.createCuboidShape(5, 5, 14, 11, 11, 16);
    private static final VoxelShape SOUTH_SHAPE = net.minecraft.block.Block.createCuboidShape(5, 5, 0, 11, 11, 2);
    private static final VoxelShape WEST_SHAPE  = net.minecraft.block.Block.createCuboidShape(14, 5, 5, 16, 11, 11);
    private static final VoxelShape EAST_SHAPE  = net.minecraft.block.Block.createCuboidShape(0, 5, 5, 2, 11, 11);
    private static final VoxelShape UP_SHAPE    = net.minecraft.block.Block.createCuboidShape(5, 0, 5, 11, 2, 11);
    private static final VoxelShape DOWN_SHAPE  = net.minecraft.block.Block.createCuboidShape(5, 14, 5, 11, 16, 11);

    public SteamVentBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(ACTIVE, false));
    }

    @Override
    protected MapCodec<? extends SteamVentBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getSide())
                .with(ACTIVE, false);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SteamVentBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            case UP -> UP_SHAPE;
            case DOWN -> DOWN_SHAPE;
        };
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.STEAM_VENT_BE ? (w, p, s, be) -> {
            if (be instanceof SteamVentBlockEntity ventBe) {
                ventBe.incrementTickCounter();

                if (ventBe.getTickCounter() >= 80) { // 4 seconds
                    ventBe.resetTickCounter();

                    boolean isActive = s.get(ACTIVE);
                    w.setBlockState(p, s.with(ACTIVE, !isActive), 3);
                    s = w.getBlockState(p);
                }

                if (s.get(ACTIVE) && w instanceof ServerWorld serverWorld && ventBe.getTickCounter() % 5 == 0) {
                    Direction facing = s.get(FACING);

                    if (ventBe.getTickCounter() % 10 == 0) {
                        float steamPitch = 1.3f + w.random.nextFloat() * 0.3f;
                        w.playSound(
                                null,
                                p,
                                SoundEvents.BLOCK_LAVA_EXTINGUISH,
                                SoundCategory.BLOCKS,
                                0.25f,
                                steamPitch
                        );
                    }

                    double x = p.getX() + 0.5 + facing.getOffsetX() * 0.6;
                    double y = p.getY() + 0.5 + facing.getOffsetY() * 0.6;
                    double z = p.getZ() + 0.5 + facing.getOffsetZ() * 0.6;

                    double velX = facing.getOffsetX() * 0.22;
                    double velY = facing.getOffsetY() * 0.22;
                    double velZ = facing.getOffsetZ() * 0.22;

                    serverWorld.spawnParticles(
                            net.minecraft.particle.ParticleTypes.CLOUD,
                            x, y, z,
                            6,
                            0.08, 0.08, 0.08,
                            0.01
                    );

                    serverWorld.spawnParticles(
                            net.minecraft.particle.ParticleTypes.CLOUD,
                            x, y, z,
                            0,
                            velX, velY, velZ,
                            1.0
                    );

                    Box pushBox = new Box(
                            p.getX() + 0.5,
                            p.getY() + 0.5,
                            p.getZ() + 0.5,
                            p.getX() + 0.5,
                            p.getY() + 0.5,
                            p.getZ() + 0.5
                    ).stretch(
                            facing.getOffsetX() * 6.0,
                            facing.getOffsetY() * 6.0,
                            facing.getOffsetZ() * 6.0
                    ).expand(0.75, 0.75, 0.75);

                    for (PlayerEntity player : w.getEntitiesByClass(PlayerEntity.class, pushBox, entity -> true)) {
                        double pushStrength = 0.42;

                        double pushX = facing.getOffsetX() * pushStrength;
                        double pushY = facing.getOffsetY() * pushStrength;
                        double pushZ = facing.getOffsetZ() * pushStrength;

                        if (facing == Direction.UP) {
                            pushY = 0.65;
                        }

                        player.addVelocity(pushX, pushY, pushZ);
                        player.velocityModified = true;

                        player.damage(serverWorld, w.getDamageSources().generic(), 1.0f);
                    }
                }
            }
        } : null;
    }
}