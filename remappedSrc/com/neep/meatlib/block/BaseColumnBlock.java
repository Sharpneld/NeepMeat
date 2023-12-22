package com.neep.meatlib.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.BlockItem;

public class BaseColumnBlock extends PillarBlock implements IMeatBlock
{
    public final BlockItem blockItem;
    private final String regsitryName;

    public BaseColumnBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.blockItem = itemSettings.create(this, registryName, itemSettings);
        this.regsitryName = registryName;
    }

    @Override
    public String getRegistryName()
    {
        return regsitryName;
    }

}
