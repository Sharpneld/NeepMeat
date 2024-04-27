package com.neep.neepmeat.block.entity;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import net.minecraft.block.DoorBlock;

public class BaseDoorBlock extends DoorBlock implements MeatlibBlock
{
    private final String name;

    public BaseDoorBlock(String name, Settings settings, ItemSettings itemSettings)
    {
        super(settings);
        this.name = name;

        BlockRegistry.queue(this);
        ItemRegistry.queue(name, itemSettings.create(this, name, itemSettings));
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }
}
