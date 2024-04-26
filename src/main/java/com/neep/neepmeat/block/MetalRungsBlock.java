package com.neep.neepmeat.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class MetalRungsBlock extends LadderBlock implements MeatlibBlock
{
    private final String name;

    public MetalRungsBlock(String name, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.name = name;
        ItemRegistry.queue(name, itemSettings.create(this, name, itemSettings));
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }
}
