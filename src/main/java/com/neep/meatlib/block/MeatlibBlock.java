package com.neep.meatlib.block;

import com.neep.meatlib.item.ItemSettings;
import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface MeatlibBlock extends ItemConvertible
{
    String getRegistryName();

    default boolean autoGenDrop()
    {
        return true;
    }

    default ItemConvertible dropsLike()
    {
        return this;
    }

    @Nullable
    default LootTable.Builder genLoot(BlockLootTableGenerator generator)
    {
        return null;
    }

    @FunctionalInterface
    interface ItemFactory
    {
        BlockItem create(Block block, String name, ItemSettings settings);
    }
}
