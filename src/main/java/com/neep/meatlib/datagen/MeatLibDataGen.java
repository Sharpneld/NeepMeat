package com.neep.meatlib.datagen;

import com.google.common.collect.Sets;
import com.neep.meatlib.datagen.loot.BlockLootTableProvider;
import com.neep.neepmeat.world.NMFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class MeatLibDataGen implements DataGeneratorEntrypoint
{
    private static final Set<FabricDataGenerator.Pack.Factory<?>> FACTORIES = Sets.newHashSet();
    private static final Set<FabricDataGenerator.Pack.RegistryDependentFactory<?>> REGISTRY_DEPENDENT = Sets.newHashSet();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        fabricDataGenerator.addProvider(BlockLootTableProvider::new);
        fabricDataGenerator.addProvider(BlockTagProvider::new);
        fabricDataGenerator.addProvider(MeatRecipeProvider::new);
        fabricDataGenerator.addProvider(ItemTagProvider::new);

        FACTORIES.forEach(pack::addProvider);
        REGISTRY_DEPENDENT.forEach(pack::addProvider);
    }

    public static <T extends DataProvider> void register(FabricDataGenerator.Pack.Factory<T> factory)
    {
        FACTORIES.add(factory);
    }

    public static <T extends DataProvider> void register(FabricDataGenerator.Pack.RegistryDependentFactory<T> factory)
    {
        REGISTRY_DEPENDENT.add(factory);
    }
}
