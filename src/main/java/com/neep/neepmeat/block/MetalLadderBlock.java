package com.neep.neepmeat.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class MetalLadderBlock extends LadderBlock implements MeatlibBlock
{
    private final String name;

    public static final BooleanProperty TOP = BooleanProperty.of("top");

    public MetalLadderBlock(String name, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.name = name;
        ItemRegistry.queue(name, itemSettings.create(this, name, itemSettings));
        setDefaultState(getStateManager().getDefaultState().with(TOP, false));
    }

    private BlockState getState(WorldAccess world, BlockPos pos, BlockState state)
    {
        Direction facing = state.get(FACING);
        boolean ladderAbove = world.getBlockState(pos.up()).getBlock() instanceof MetalLadderBlock;
        BlockPos offset = pos.offset(facing.getOpposite());
        boolean solidUpFace = world.getBlockState(offset).isSideSolidFullSquare(world, offset, Direction.UP);

        return state.with(TOP, !ladderAbove && solidUpFace);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        World world = ctx.getWorld();

        BlockState superState = super.getPlacementState(ctx);
        if (superState == null)
            return null;

        return getState(world, ctx.getBlockPos(), superState);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        if (direction.getAxis().isVertical())
        {
            return getState(world, pos, state);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(TOP);
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }
}
