package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseBuildingBlock;
import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.datagen.tag.NMTags;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.DyeItem;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.DyeColor;

import java.util.function.Consumer;

public class RoughConcreteBlock extends BaseBuildingBlock
{
    private final DyeColor col;

    public RoughConcreteBlock(String blockName, boolean makeWall, DyeColor col, Settings settings)
    {
        super(blockName, makeWall, ItemSettings.block().factory(Item::new), settings);
        this.col = col;
    }

    @Override
    public void generateRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        super.generateRecipes(exporter);
        MeatRecipeProvider.offerEightDyeingRecipe(exporter, "_dyeing", this, DyeItem.byColor(col), NMTags.ROUGH_CONCRETE);
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
            consumer.accept(NMTags.ROUGH_CONCRETE);
        }
    }
}
