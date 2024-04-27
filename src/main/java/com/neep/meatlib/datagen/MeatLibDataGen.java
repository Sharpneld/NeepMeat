package com.neep.meatlib.datagen;

import com.google.common.collect.Sets;
import com.neep.meatlib.datagen.loot.BlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataProvider;

import java.util.Set;
import java.util.function.Function;

public class MeatLibDataGen implements DataGeneratorEntrypoint
{
    private static final Set<Function<FabricDataGenerator, ? extends DataProvider>> FACTORIES = Sets.newHashSet();
//    private static final Set<Function.RegistryDependentFactory<?>> REGISTRY_DEPENDENT = Sets.newHashSet();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        fabricDataGenerator.addProvider(BlockLootTableProvider::new);
        fabricDataGenerator.addProvider(BlockTagProvider::new);
        fabricDataGenerator.addProvider(MeatRecipeProvider::new);
        fabricDataGenerator.addProvider(MeatlibItemTagProvider::new);

        FACTORIES.forEach(fabricDataGenerator::addProvider);
    }

    public static <T extends DataProvider> void register(Function<FabricDataGenerator, T> factory)
    {
        FACTORIES.add(factory);
    }

//    public static <T extends DataProvider> void register(FabricDataGenerator.Pack.RegistryDependentFactory<T> factory)
//    {
//        REGISTRY_DEPENDENT.add(factory);
//    }
}
