package com.neep.meatlib.datagen;

import com.neep.meatlib.block.BaseBuildingBlock;
import com.neep.meatlib.block.SmoothTileBlock;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.block.MetalScaffoldingBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MeatRecipeProvider extends FabricRecipeProvider
{
    public MeatRecipeProvider(FabricDataGenerator output)
    {
        super(output);
    }

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        BlockRegistry.REGISTERED_BLOCKS.stream()
                .filter(block -> block instanceof BaseBuildingBlock)
                .forEach(block -> ((BaseBuildingBlock) block).generateRecipes(exporter));

        BlockRegistry.REGISTERED_BLOCKS.stream()
                .filter(block -> block instanceof SmoothTileBlock)
                .forEach(block -> ((SmoothTileBlock) block).generateRecipe(exporter));

        BlockRegistry.REGISTERED_BLOCKS.stream()
                .filter(block -> block instanceof MetalScaffoldingBlock)
                .forEach(block -> ((MetalScaffoldingBlock) block).generateRecipe(exporter));

        EXPORTER_CONSUMERS.forEach(consumer -> consumer.accept(exporter));
    }

    public static void offerStairsRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input)
    {
        RecipeProvider.createStairsRecipe(output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerEightDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible dye, ItemConvertible input)
    {
        createEightDyeingRecipe(output, Ingredient.ofItems(dye), Ingredient.ofItems(input))
                .criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input))
                .criterion(RecipeProvider.hasItem(dye), RecipeProvider.conditionsFromItem(dye))
                .offerTo(exporter);
    }

    public static void offerEightDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible dye, TagKey<Item> input)
    {
        createEightDyeingRecipe(output, Ingredient.ofItems(dye), Ingredient.fromTag(input))
                .criterion("has_dye", RecipeProvider.conditionsFromTag(input))
                .criterion(RecipeProvider.hasItem(dye), RecipeProvider.conditionsFromItem(dye))
                .offerTo(exporter);
    }

    public static void offerEightDyeingRecipe(Consumer<RecipeJsonProvider> exporter, String suffix, ItemConvertible output, ItemConvertible dye, TagKey<Item> input)
    {
        Identifier outputId = Registry.ITEM.getId(output.asItem());
        Identifier id = new Identifier(outputId.getNamespace(), outputId.getPath() + suffix);
        createEightDyeingRecipe(output, Ingredient.ofItems(dye), Ingredient.fromTag(input))
                .criterion("has_dye", RecipeProvider.conditionsFromTag(input))
                .criterion(RecipeProvider.hasItem(dye), RecipeProvider.conditionsFromItem(dye))
                .offerTo(exporter, id);
    }

    public static CraftingRecipeJsonBuilder createEightDyeingRecipe(ItemConvertible output, Ingredient dye, Ingredient input)
    {
        return ShapedRecipeJsonBuilder.create(output, 8).input('i', input).input('d', dye).pattern("iii").pattern("idi").pattern("iii");
    }

    // Aargh! Multiple DataGeneratorEntrypoints under the namespace overwrite each other so I am forced to employ this method...
    @FunctionalInterface
    public interface Subsidiary extends Consumer<Consumer<RecipeJsonProvider>> { }

    protected static List<Subsidiary> EXPORTER_CONSUMERS = new ArrayList<>();

    public static void addSubsidiary(Subsidiary consumer)
    {
        EXPORTER_CONSUMERS.add(consumer);
    }
}
