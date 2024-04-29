package com.neep.neepmeat.block.entity;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public class BaseDoorBlock extends DoorBlock implements MeatlibBlock
{
    private final String name;

    public BaseDoorBlock(String name, Settings settings, ItemSettings itemSettings, BlockSetType blockSetType)
    {
        super(settings, blockSetType);
        this.name = name;

        BlockRegistry.queue(this);
        ItemRegistry.queue(name, itemSettings.create(this, name, itemSettings));
    }

    @Override
    public @Nullable LootTable.Builder genLoot(BlockLootTableGenerator generator)
    {
        return generator.doorDrops(this);
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }
}
