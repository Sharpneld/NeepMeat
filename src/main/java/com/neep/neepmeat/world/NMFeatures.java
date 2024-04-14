package com.neep.neepmeat.world;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlocks;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.placementmodifier.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.trunk.BendingTrunkPlacer;

import java.util.List;

public class NMFeatures
{
    public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_ASBESTOS = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(NeepMeat.NAMESPACE, "ore_asbestos"));
    public static final RegistryKey<PlacedFeature> ORE_ASBESTOS_UPPER = RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(NeepMeat.NAMESPACE, "ore_asbestos_upper"));
    public static final RegistryKey<PlacedFeature> ORE_ASBESTOS_LOWER = RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(NeepMeat.NAMESPACE, "ore_asbestos_lower"));

    public static final RegistryEntry<ConfiguredFeature<TreeFeatureConfig, ?>> BLOOD_BUBBLE_TREE = ConfiguredFeatures.register("blood_bubble_tree", Feature.TREE,
            new TreeFeatureConfig.Builder(BlockStateProvider.of(NMBlocks.BLOOD_BUBBLE_LOG),
                    new BendingTrunkPlacer(5, 2, 0, 3, UniformIntProvider.create(0, 1)),
                    new WeightedBlockStateProvider(DataPool.<BlockState>builder()
                            .add(NMBlocks.BLOOD_BUBBLE_LEAVES.getDefaultState(), 3)
                            .add(NMBlocks.BLOOD_BUBBLE_LEAVES_FLOWERING.getDefaultState(), 1)),
                    new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 50),
                    new TwoLayersFeatureSize(1, 0, 1)).dirtProvider(BlockStateProvider.of(Blocks.ROOTED_DIRT)).forceDirt().build());

    public static void init()
    {
        RuleTest ruleTest = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_FEATURE, ORE_ASBESTOS.getValue(), new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(ruleTest, NMBlocks.ASBESTOS.getDefaultState(), 64)));

        placedFeature(ORE_ASBESTOS_UPPER, ORE_ASBESTOS, modifiersWithRarity(6, HeightRangePlacementModifier.uniform(YOffset.fixed(64), YOffset.fixed(128))));
        placedFeature(ORE_ASBESTOS_LOWER, ORE_ASBESTOS, modifiersWithCount(1, HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(60))));

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_ASBESTOS_UPPER);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_ASBESTOS_LOWER);
    }

    private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier)
    {
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
    }

    private static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier)
    {
        return modifiers(CountPlacementModifier.of(count), heightModifier);
    }

    private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier)
    {
        return modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
    }

    private static void placedFeature(RegistryKey<PlacedFeature> registryKey, RegistryKey<ConfiguredFeature<?, ?>> configured, List<PlacementModifier> modifiers)
    {
        BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, registryKey.getValue(), new PlacedFeature(BuiltinRegistries.CONFIGURED_FEATURE.getEntry(configured).orElseThrow(), modifiers));
    }
}
