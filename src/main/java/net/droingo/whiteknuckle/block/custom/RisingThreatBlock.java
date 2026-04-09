package net.droingo.whiteknuckle.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;


public class RisingThreatBlock extends Block {
    public static final MapCodec<RisingThreatBlock> CODEC = createCodec(RisingThreatBlock::new);

    public RisingThreatBlock(Settings settings) {
        super(settings);
    }

    private static final VoxelShape THREAT_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 11, 16);
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return THREAT_SHAPE;
    }

    @Override
    public MapCodec<? extends RisingThreatBlock> getCodec() {
        return CODEC;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        entity.setVelocity(
                entity.getVelocity().x * 0.6,
                Math.max(entity.getVelocity().y, -0.08),
                entity.getVelocity().z * 0.6
        );

        entity.velocityModified = true;

        if (!world.isClient() && world instanceof ServerWorld serverWorld && entity instanceof PlayerEntity player) {
            player.damage(serverWorld, world.getDamageSources().generic(), 3.0f);
        }

        super.onSteppedOn(world, pos, state, entity);
    }
}