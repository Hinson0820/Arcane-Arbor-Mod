package net.hinson820.arcanearbor.common.block;

import com.mojang.serialization.MapCodec;
import net.hinson820.arcanearbor.core.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class LuminFruitBlock extends Block implements SimpleWaterloggedBlock {

    public static final MapCodec<LuminFruitBlock> CODEC = simpleCodec(LuminFruitBlock::new);

    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape AABB = Shapes.or(
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 11.0D),
            Block.box(6.0D, 7.0D, 6.0D, 10.0D, 9.0D, 10.0D)
    );
    protected static final VoxelShape HANGING_AABB = Shapes.or(
            Block.box(5.0D, 1.0D, 5.0D, 11.0D, 8.0D, 11.0D),
            Block.box(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D)
    );

    public LuminFruitBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HANGING, Boolean.FALSE)
                .setValue(WATERLOGGED, Boolean.FALSE)
        );
    }

    @Override
    public MapCodec<LuminFruitBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HANGING, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(HANGING) ? HANGING_AABB : AABB;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockState blockstate = this.defaultBlockState().setValue(HANGING, direction == Direction.UP);
                if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return blockstate.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
                }
            }
        }
        BlockState defaultStandingState = this.defaultBlockState().setValue(HANGING, false).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        if (defaultStandingState.canSurvive(context.getLevel(), context.getClickedPos())) {
            return defaultStandingState;
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction direction = getConnectedDirection(state).getOpposite(); // Direction of support
        BlockPos supportPos = pos.relative(direction);
        BlockState supportState = world.getBlockState(supportPos);

        if (state.getValue(HANGING)) {
            return Block.canSupportCenter(world, supportPos, Direction.DOWN) ||
                    supportState.is(BlockInit.LUMINWOOD_LOG.get()) ||
                    supportState.is(BlockInit.STRIPPED_LUMINWOOD_LOG.get()) ||
                    supportState.is(BlockInit.LUMINWOOD_LEAVES.get());
        } else {
            return Block.canSupportCenter(world, supportPos, Direction.UP) ||
                    supportState.is(BlockInit.LUMINWOOD_LOG.get()) ||
                    supportState.is(BlockInit.STRIPPED_LUMINWOOD_LOG.get());
        }
    }

    protected static Direction getConnectedDirection(BlockState state) {
        return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                  LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        if (getConnectedDirection(state).getOpposite() == facing && !state.canSurvive(world, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}


