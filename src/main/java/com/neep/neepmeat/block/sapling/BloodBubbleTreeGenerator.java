package com.neep.neepmeat.block.sapling;

import com.neep.neepmeat.world.NMFeatures;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;


public class BloodBubbleTreeGenerator extends SaplingGenerator
{
    @Override
    protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees)
    {
        return NMFeatures.BLOOD_BUBBLE_TREE;
    }
}
