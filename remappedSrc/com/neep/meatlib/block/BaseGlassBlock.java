package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.item.BlockItem;

public class BaseGlassBlock extends AbstractGlassBlock implements IMeatBlock
{
    public BlockItem blockItem;
    private String registryName;

    public BaseGlassBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.create(this, registryName, itemSettings);
        this.registryName = registryName;
    }

    public BlockItem getBlockItem()
    {
        return blockItem;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}