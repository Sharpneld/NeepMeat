package com.neep.meatlib.block;

import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.datagen.tag.NMTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.DyeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SmoothTileBlock
{
    public static List<PaintedBlock> COLOURED_BLOCKS = new ArrayList<>();

    public BaseBlockItem blockItem;

//    public static List<Integer> COLOURS = List.of(
//            0x1D1D21, 0xB02E26, 0x5E7C16, 0x835432,
//            0x3C44AA, 0x8932B8, 0x169C9C, 0x9D9D97,
//            0x474F52, 0xF38BAA, 0x80C71F, 0xFED83D,
//            0x3AB3DA, 0xC74EBD, 0xF9801D, 0xF9FFFE
//    );

//    public static List<String> COLOURS = List.of(


    public SmoothTileBlock(String registryName, AbstractBlock.Settings settings)
    {
        this(registryName, PaintedBlock::new, settings);
    }

    public SmoothTileBlock(String registryName, Constructor constructor, AbstractBlock.Settings settings)
    {
        for (DyeColor col : DyeColor.values())
        {
            PaintedBlock block = BlockRegistry.queue(constructor.create(registryName + "_" + col.getName(), col, settings));
            COLOURED_BLOCKS.add(block);
        }
    }

    @FunctionalInterface
    public interface Constructor
    {
        PaintedBlock create(String registryName, DyeColor col, AbstractBlock.Settings settings);
    }

    public static class PaintedBlock extends Block implements MeatlibBlock
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

        protected BlockItem makeItem()
        {
            return new Item(this, registryName, ItemSettings.block());
        }

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

        public void generateRecipe(Consumer<RecipeJsonProvider> exporter)
        {
            MeatRecipeProvider.offerEightDyeingRecipe(exporter, "_dyeing", this, DyeItem.byColor(getCol()), NMTags.SMOOTH_TILE);
            MeatRecipeProvider.offerEightDyeingRecipe(exporter, this, DyeItem.byColor(getCol()), Blocks.SMOOTH_STONE);
        }
    }

    private static class Item extends BaseBlockItem
    {
        public Item(Block block, String registryName, ItemSettings itemSettings)
        {
            super(block, registryName, itemSettings);
        }

        @Override
        public void appendTags(Consumer<TagKey<net.minecraft.item.Item>> consumer)
        {
            consumer.accept(NMTags.SMOOTH_TILE);
        }
    }
}