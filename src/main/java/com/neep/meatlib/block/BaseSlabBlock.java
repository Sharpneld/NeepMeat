package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.loot.LootTable;

public class BaseSlabBlock extends SlabBlock implements MeatlibBlock
{
    protected String registryName;
    protected BlockItem blockItem;

    public BaseSlabBlock(BlockState baseBlockState, String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.blockItem = itemSettings.create(this, registryName, itemSettings);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public LootTable.Builder genLoot(BlockLootTableGenerator generator)
    {
        return BlockLootTableGenerator.slabDrops(this);
    }
}
