package com.neep.meatlib.block;

import com.google.common.collect.Lists;
import com.neep.meatlib.registry.BlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.DyeColor;

import java.util.ArrayList;
import java.util.List;

public class PaintedBlockManager<T extends PaintedBlockManager.PaintedBlock>
{
    public static final List<PaintedBlock> COLOURED_BLOCKS = new ArrayList<>();
    public final List<T> entries = Lists.newArrayList();

    public PaintedBlockManager(String registryName, Constructor<T> constructor, AbstractBlock.Settings settings)
    {
        for (DyeColor col : DyeColor.values())
        {
            T block = BlockRegistry.queue(constructor.create(registryName + "_" + col.getName(), col, settings));
            COLOURED_BLOCKS.add(block);
            entries.add(block);
        }
    }

    @FunctionalInterface
    public interface Constructor<T extends PaintedBlock>
    {
        T create(String registryName, DyeColor col, AbstractBlock.Settings settings);
    }

    public abstract static class PaintedBlock extends Block implements MeatlibBlock
    {
        protected final String registryName;
        public final BlockItem blockItem;
        public final DyeColor col;

        public PaintedBlock(String registryName, DyeColor col, Settings settings)
        {
            super(settings);
            this.registryName = registryName;
            this.blockItem = makeItem();
            this.col = col;
        }

        protected abstract BlockItem makeItem();

        @Override
        public String getRegistryName()
        {
            return registryName;
        }

        public DyeColor getCol()
        {
            return this.col;
        }

        public int getRawCol()
        {
            return col.getFireworkColor();
        }
    }
}
