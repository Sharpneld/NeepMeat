package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseBuildingBlock;
import com.neep.meatlib.datagen.MeatRecipeProvider;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMItems;
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
            super(block, registryName, itemSettings, new MeatlibItemSettings().tags(NMTags.ROUGH_CONCRETE).group(NMItemGroups.BUILDING));
        }
    }
}
