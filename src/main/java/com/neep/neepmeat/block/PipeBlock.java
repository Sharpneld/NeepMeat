package com.neep.neepmeat.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PipeBlock extends BaseBlock implements FluidAcceptor
{
//    public static final EnumProperty<PipeConnection> NORTH_CONNECTION = PipeProperties.NORTH_CONNECTION;
//    public static final EnumProperty<PipeConnection> EAST_CONNECTION = PipeProperties.EAST_CONNECTION;
//    public static final EnumProperty<PipeConnection> SOUTH_CONNECTION = PipeProperties.SOUTH_CONNECTION;
//    public static final EnumProperty<PipeConnection> WEST_CONNECTION = PipeProperties.WEST_CONNECTION;
//    public static final EnumProperty<PipeConnection> UP_CONNECTION = PipeProperties.UP_CONNECTION;
//    public static final EnumProperty<PipeConnection> DOWN_CONNECTION = PipeProperties.DOWN_CONNECTION;
    public static final BooleanProperty NORTH_CONNECTION = Properties.NORTH;
    public static final BooleanProperty EAST_CONNECTION = Properties.EAST;
    public static final BooleanProperty SOUTH_CONNECTION = Properties.SOUTH;
    public static final BooleanProperty WEST_CONNECTION = Properties.WEST;
    public static final BooleanProperty UP_CONNECTION = Properties.UP;
    public static final BooleanProperty DOWN_CONNECTION = Properties.DOWN;

    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

    public static final Map<Direction, BooleanProperty> DIR_TO_CONNECTION = (new ImmutableMap.Builder<Direction, BooleanProperty>()
            .put(Direction.NORTH, NORTH_CONNECTION)
            .put(Direction.EAST, EAST_CONNECTION)
            .put(Direction.SOUTH, SOUTH_CONNECTION)
            .put(Direction.WEST, WEST_CONNECTION)
            .put(Direction.DOWN, DOWN_CONNECTION)
            .put(Direction.UP, UP_CONNECTION)
    ).build();

    public static final Map<Direction, VoxelShape> DIR_SHAPES = (new ImmutableMap.Builder<Direction, VoxelShape>()
            .put(Direction.NORTH, Block.createCuboidShape(4, 4, 0, 12, 12, 5))
            .put(Direction.EAST, Block.createCuboidShape(11, 4, 4, 16, 12, 12))
            .put(Direction.SOUTH, Block.createCuboidShape(4, 4, 11, 12, 12, 16))
            .put(Direction.WEST, Block.createCuboidShape(0, 4, 4, 5, 12, 12))
            .put(Direction.UP, Block.createCuboidShape(4, 11, 4, 12, 16, 12))
            .put(Direction.DOWN, Block.createCuboidShape(4, 0, 4, 12, 5, 12))
    ).build();

    public PipeBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
        this.setDefaultState(this.stateManager.getDefaultState()
//                .with(NORTH_CONNECTION, PipeConnection.NONE)
//                .with(EAST_CONNECTION, PipeConnection.NONE)
//                .with(SOUTH_CONNECTION, PipeConnection.NONE)
//                .with(WEST_CONNECTION, PipeConnection.NONE)
//                .with(UP_CONNECTION, PipeConnection.NONE)
//                .with(DOWN_CONNECTION, PipeConnection.NONE));
                .with(NORTH_CONNECTION, false)
                .with(EAST_CONNECTION, false)
                .with(SOUTH_CONNECTION, false)
                .with(WEST_CONNECTION, false)
                .with(UP_CONNECTION, false)
                .with(DOWN_CONNECTION, false));

        for (BlockState state : this.getStateManager().getStates())
        {
            SHAPES.put(state, getShapeForState(state));
        }

    }

    public VoxelShape getShapeForState(BlockState state)
    {
        VoxelShape shape = Block.createCuboidShape(4, 4, 4, 12, 12, 12);
        for (Direction direction : Direction.values())
        {
            if (state.get(DIR_TO_CONNECTION.get(direction)))
            {
                shape = VoxelShapes.union(shape, DIR_SHAPES.get(direction));
            }
        }
        return shape;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPES.get(state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context)
    {
//        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1.0f, 1f);
        return SHAPES.get(state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getPlacementState(ctx.getWorld(), this.getDefaultState(), ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos)
    {
        boolean bl7;
        boolean bl = isNotConnected(state);
        state = this.getConnectedState(world, this.getDefaultState(), pos);
        if (bl && isNotConnected(state))
        {
            return state;
        }
        boolean north = state.get(NORTH_CONNECTION);
        boolean south = state.get(SOUTH_CONNECTION);
        boolean east = state.get(EAST_CONNECTION);
        boolean west = state.get(WEST_CONNECTION);
        boolean up = state.get(UP_CONNECTION);
        boolean down = state.get(DOWN_CONNECTION);
        boolean nNS = !north && !south;
        boolean nEW = !east && !west;
        boolean nUD = !up && !down;
        if (!west && nNS & nUD)
        {
            state = state.with(WEST_CONNECTION, true);
        }
        if (!east && nNS && nUD)
        {
            state = state.with(EAST_CONNECTION, true);
        }
        if (!north && nEW && nUD)
        {
            state = state.with(NORTH_CONNECTION, true);
        }
        if (!south && nEW && nUD)
        {
            state = state.with(SOUTH_CONNECTION, true);
        }
//        System.out.println("up: " + up + ", nNS: " + nNS + ", nEW: " + nEW + ", nUD: " + nUD);
        if (!up && nNS && nEW)
        {
            state = state.with(UP_CONNECTION, true);
        }
        if (!down && nNS && nEW)
        {
            state = state.with(DOWN_CONNECTION, true);
        }
        return state;
    }

    @Override
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth)
    {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.Type.HORIZONTAL)
        {
            BlockState blockPos;
            boolean connection = state.get(DIR_TO_CONNECTION.get(direction));
            if (!connection || world.getBlockState(mutable.set(pos, direction)).isOf(this)) continue;
            mutable.move(Direction.DOWN);

            mutable.set(pos, direction).move(Direction.UP);
            blockPos = world.getBlockState(mutable);
            if (blockPos.isOf(Blocks.OBSERVER)) continue;
            BlockPos blockPos1 = mutable.offset(direction.getOpposite());
            BlockState blockState3 = blockPos.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos1), world, mutable, blockPos1);
            RedstoneWireBlock.replace(blockPos, blockState3, world, mutable, flags, maxUpdateDepth);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        BlockState targetState = world.getBlockState(pos.offset(direction));
        boolean connection = canConnectTo(targetState, direction.getOpposite());

        if (connection == state.get(DIR_TO_CONNECTION.get(direction)) && !isFullyConnected(state))
        {
            return state.with(DIR_TO_CONNECTION.get(direction), connection);
        }
        return this.getPlacementState(world, this.getDefaultState().with(DIR_TO_CONNECTION.get(direction), connection), pos);
    }

    // TODO: Add other things
    public boolean canConnectTo(BlockState state, Direction direction)
    {
        if (state.getBlock() instanceof FluidAcceptor)
        {
            return ((FluidAcceptor) state.getBlock()).connectInDirection(state, direction);
        }
        return false;
    }

    private static boolean isNotConnected(BlockState state)
    {
        return !state.get(NORTH_CONNECTION)
                && !state.get(SOUTH_CONNECTION)
                && !state.get(EAST_CONNECTION)
                && !state.get(WEST_CONNECTION)
                && !state.get(UP_CONNECTION)
                && !state.get(DOWN_CONNECTION)
                ;
    }

    private static boolean isFullyConnected(BlockState state)
    {
        return state.get(NORTH_CONNECTION)
                && state.get(SOUTH_CONNECTION)
                && state.get(EAST_CONNECTION)
                && state.get(WEST_CONNECTION)
                && state.get(UP_CONNECTION)
                && state.get(DOWN_CONNECTION)
                ;
    }

    private BlockState getConnectedState(BlockView world, BlockState state, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            boolean property = state.get(DIR_TO_CONNECTION.get(direction));
            if (property) continue;
            BlockPos adjPos = pos.offset(direction);
            BlockState adjState = world.getBlockState(adjPos);
            state = state.with(DIR_TO_CONNECTION.get(direction), canConnectTo(adjState, direction.getOpposite()));
        }
        return state;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld)
        {
            return ActionResult.PASS;
        }
        if (!world.isClient)
        {
            Vec3d hitPos = hit.getPos();
            Direction direction = hit.getSide();
            boolean connected = state.get(DIR_TO_CONNECTION.get(direction));
//            if (!connected)
//            {
////                System.out.println(hitPos.subtract(hit.getBlockPos()));
//            }
//            else
//            {
//
//            }
//            System.out.println(connected);
//            world.setBlockState(pos, state.with(DIR_TO_CONNECTION.get(direction), false), Block.NOTIFY_ALL);
////            world.setBlockState(pos, Blocks.DIRT.getDefaultState());

//            world.setBlockState(pos, state.with(DIR_TO_CONNECTION.get(direction), false), Block.NOTIFY_ALL);
            return ActionResult.PASS;
//        }
        }
        return ActionResult.PASS;
    }

    public static Pair<List<BlockPos>, List<BlockPos>> findNodes(BlockPos pos, Direction fromDirection, List<BlockPos> visited, World world, int level)
    {
        System.out.println(pos.toString());
        List<BlockPos> list = new ArrayList<>();
        visited.add(pos);
        if (level > 0)
        {
            for (Direction direction : Direction.values())
            {
                BlockPos pos2 = pos.offset(direction);
                BlockState state = world.getBlockState(pos);
                BlockState state2 = world.getBlockState(pos2);

                if (FluidAcceptor.isConnectedIn(state, direction))
                {
                    if (state2.getBlock() instanceof FluidAcceptor
                            && !(state2.getBlock() instanceof FluidNodeProvider)
                            && !visited.contains(pos2)
                            && direction != fromDirection)
                    {
                        // Next block is connected in opposite direction
                        if (FluidAcceptor.isConnectedIn(state2, direction.getOpposite()))
                        {
                            // Continue searching and add nodes.
                            Pair<List<BlockPos>, List<BlockPos>> pair = findNodes(pos2, direction.getOpposite(), visited, world, level - 1);
                            list.addAll(pair.getLeft());
                            visited.addAll(pair.getRight());
                        }
                    }
                    else if (state2.getBlock() instanceof FluidNodeProvider && direction != fromDirection)
                    {
                        if (((FluidNodeProvider) state2.getBlock()).connectInDirection(state2, direction.getOpposite()))
                        {
//                            System.out.println(pos2.toString());
                            list.add(pos2);
                        }
                    }
                }
            }
        }
        return new Pair<>(list, visited);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH_CONNECTION, EAST_CONNECTION, SOUTH_CONNECTION, WEST_CONNECTION, UP_CONNECTION, DOWN_CONNECTION);
    }
}
