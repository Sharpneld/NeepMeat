package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.item.BlockItem;

public class BaseTrapdoorBlock extends TrapdoorBlock implements IMeatBlock
{
    public BaseBlockItem blockItem;
    private final String registryName;

    public BaseTrapdoorBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(settings, BlockSetType.IRON);
        this.blockItem = new BaseBlockItem(this, itemName, itemSettings);
        this.registryName = itemName;
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
