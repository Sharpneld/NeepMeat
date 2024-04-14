package com.neep.meatlib.registry;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.block.BaseColumnBlock;
import com.neep.meatlib.block.BaseLeavesBlock;
import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.ItemConvertible;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlockRegistry
{
    public static final Map<Identifier, Block> BLOCKS = new LinkedHashMap<>(); // Preserve order
    public static final List<Block> REGISTERED_BLOCKS = new ArrayList<>();

    public static <T extends Block & MeatlibBlock> T queue(T block)
    {
        MeatLib.assertActive(block);
        if (block == null)
        {
            throw new IllegalArgumentException("tried to queue something that wasn't a block.");
        }

        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, block.getRegistryName()), block);
        return block;
    }

    public static Block queue(MeatlibBlock block)
    {
        MeatLib.assertActive(block);
        if (!(block instanceof Block))
        {
            throw new IllegalArgumentException("tried to queue something that wasn't a block.");
        }

        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, block.getRegistryName()), (Block) block);
        return (Block) block;
    }

    public static <T extends Block> T queue(T block, String registryName)
    {
        MeatLib.assertActive(block);
        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, registryName), block);
        return block;
    }

    public static <T extends Block> T queueWithItem(T block, String registryName, ItemSettings itemSettings)
    {
        MeatLib.assertActive(block);
        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, registryName), block);
        itemSettings.getFactory().create(block, registryName, itemSettings);
        return block;
    }

    public static <T extends Block> T queueWithItem(T block, String registryName)
    {
        MeatLib.assertActive(block);
        BLOCKS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, registryName), block);
        ItemRegistry.queue(new BaseBlockItem(block, registryName, ItemSettings.block()));
        return block;
    }

    public static void flush()
    {
        for (Map.Entry<Identifier, Block> entry : BLOCKS.entrySet())
        {
            Registry.register(Registry.BLOCK, entry.getKey(), entry.getValue());

            REGISTERED_BLOCKS.add(entry.getValue());
        }
        BLOCKS.clear();
    }

    public static BaseColumnBlock createLogBlock(String name, TooltipSupplier tooltipSupplier)
    {
        return new BaseColumnBlock(name, ItemSettings.block(), MeatlibBlockSettings.create(Material.WOOD, BlockTags.AXE_MINEABLE).strength(2.0f).sounds(BlockSoundGroup.WOOD));
    }

    public static BaseLeavesBlock createLeavesBlock(String name, BlockSoundGroup soundGroup)
    {
        return new BaseLeavesBlock(name, MeatlibBlockSettings.create(Material.LEAVES, FabricMineableTags.SHEARS_MINEABLE)
                .strength(0.2f)
                .ticksRandomly()
                .sounds(soundGroup)
                .nonOpaque()
                .allowsSpawning((p1, p2, p3, p4) -> false)
                .suffocates((p1, p2, p3) -> false).blockVision(((state, world, pos) -> false)))
        {
            @Override
            public ItemConvertible dropsLike()
            {
                return null;
            }
        };
    }
}
