package com.neep.neepmeat.fluid.ore_fat;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.neepmeat.fluid.BaseFluid;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class OreFatFluidFactory
{
    public final String namespace;
    public final String baseName;
    public final String flowingName;
    public final String stillName;
    public final String bucketName;

    protected FlowableFluid still;
    protected FlowableFluid flowing;
    protected Block block;
    protected Item bucketItem = Items.AIR;

    protected final boolean isInfinite;
    protected final int tickRate;
    protected final int levelDecrease;

    public OreFatFluidFactory(String namespace, String baseName, boolean isInfinite, int tickRate, int levelDecrease)
    {
        this.namespace = namespace;
        this.baseName = baseName;
        this.flowingName = "flowing_" + baseName;
        this.stillName = baseName;
        this.bucketName = baseName + "_bucket";

        this.isInfinite = isInfinite;
        this.tickRate = tickRate;
        this.levelDecrease = levelDecrease;
    }

    public FlowableFluid registerStill()
    {
        still = Registry.register(Registries.FLUID, new Identifier(namespace, stillName), new Still());
        return still;
    }

    public FlowableFluid registerFlowing()
    {
        flowing = Registry.register(Registries.FLUID, new Identifier(namespace, flowingName), new Flowing());
        return flowing;
    }

    public Block registerBlock()
    {
        block = Registry.register(Registries.BLOCK, new Identifier(namespace, baseName), new FluidBlock(still, MeatlibBlockSettings.copyOf(Blocks.WATER)){});
        return block;
    }

    public abstract class Main extends BaseFluid
    {
        @Override
        public Fluid getFlowing()
        {
            return flowing;
        }

        @Override
        public Fluid getStill()
        {
            return still;
        }

        @Override
        protected boolean isInfinite(World world)
        {
            return isInfinite;
        }

        @Override
        public Item getBucketItem()
        {
            return bucketItem;
        }

        @Override
        protected BlockState toBlockState(FluidState state)
        {
            return block.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
        }

        @Override
        protected int getLevelDecreasePerBlock(WorldView world)
        {
            return levelDecrease;
        }

        @Override
        public int getTickRate(WorldView world)
        {
            return tickRate;
        }
    }

    private class Still extends Main
    {
        @Override
        public int getLevel(FluidState fluidState)
        {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState)
        {
            return true;
        }
    }

    private class Flowing extends Main
    {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder)
        {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState)
        {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState)
        {
            return false;
        }
    }
}
