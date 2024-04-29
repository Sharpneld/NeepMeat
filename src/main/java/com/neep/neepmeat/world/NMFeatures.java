package com.neep.neepmeat.world;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlocks;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.placementmodifier.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.trunk.BendingTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NMFeatures extends FabricDynamicRegistryProvider
{
    public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_ASBESTOS = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(NeepMeat.NAMESPACE, "ore_asbestos"));
    public static final RegistryKey<PlacedFeature> ORE_ASBESTOS_UPPER = RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(NeepMeat.NAMESPACE, "ore_asbestos_upper"));
    public static final RegistryKey<PlacedFeature> ORE_ASBESTOS_LOWER = RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(NeepMeat.NAMESPACE, "ore_asbestos_lower"));

    public NMFeatures(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    public static void init()
    {
//        MeatLibDataGen.register(NMFeatures::new);

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

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries)
    {
//        final RegistryWrapper.Impl<Biome> biomeRegistry = registries.getWrapperOrThrow(RegistryKeys.BIOME);
//        entries.add(ExampleBiomes.MODDED_BIOME_KEY, biomeRegistry.getOrThrow(ExampleBiomes.MODDED_BIOME_KEY).value());

        var thing = new TreeFeatureConfig.Builder(
                BlockStateProvider.of(NMBlocks.BLOOD_BUBBLE_LOG),
//                new StraightTrunkPlacer(5, 2, 0),
                new BendingTrunkPlacer(5, 2, 0, 3, UniformIntProvider.create(1, 2)),
                new WeightedBlockStateProvider(DataPool.<BlockState>builder()
                        .add(NMBlocks.BLOOD_BUBBLE_LEAVES.getDefaultState(), 3)
                        .add(NMBlocks.BLOOD_BUBBLE_LEAVES_FLOWERING.getDefaultState(), 1)),
                new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 50),
                new TwoLayersFeatureSize(1, 0, 1)).dirtProvider(BlockStateProvider.of(Blocks.ROOTED_DIRT)).forceDirt().build();
        entries.add(RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(NeepMeat.NAMESPACE, "blood_bubble_tree")), new ConfiguredFeature<>(Feature.TREE, thing));

        RuleTest ruleTest = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        entries.add(ORE_ASBESTOS, new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(ruleTest, NMBlocks.ASBESTOS.getDefaultState(), 64)));
        placedFeature(ORE_ASBESTOS_UPPER, ORE_ASBESTOS, entries, modifiersWithRarity(4, HeightRangePlacementModifier.uniform(YOffset.fixed(64), YOffset.fixed(128))));
        placedFeature(ORE_ASBESTOS_LOWER, ORE_ASBESTOS, entries, modifiersWithCount(1, HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(60))));
    }

    private static void placedFeature(RegistryKey<PlacedFeature> registryKey, RegistryKey<ConfiguredFeature<?, ?>> configured, Entries entries, List<PlacementModifier> modifiers)
    {
        entries.add(registryKey, new PlacedFeature(entries.ref(configured), modifiers));
    }

    @Override
    public String getName()
    {
        return NeepMeat.NAMESPACE + " features";
    }
}
