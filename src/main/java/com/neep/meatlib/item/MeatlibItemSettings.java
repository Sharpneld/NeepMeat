package com.neep.meatlib.item;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.util.Rarity;

import java.util.Set;

// HAHAHHAHAHAHA I'M BRINGING BACK Settings::group! YOU CAN'T STOP ME!
public class MeatlibItemSettings extends FabricItemSettings
{
    public static final Set<TagKey<Item>> DEFAULT_TAGS = Set.of();

    public ItemGroup group;
    public boolean supportsGuideLookup = true; // For minimal extra boilerplate in item declarations, this is the default behaviour.
    private Set<TagKey<Item>> tags = DEFAULT_TAGS;

    public Set<TagKey<Item>> getTags()
    {
        return tags;
    }

    // Mostly taken from FabricItemSettings. Modified to return MeatlibItemSettings.

    public MeatlibItemSettings equipmentSlot(EquipmentSlotProvider equipmentSlotProvider)
    {
        FabricItemInternals.computeExtraData(this).equipmentSlot(equipmentSlotProvider);
        return this;
    }

    public MeatlibItemSettings customDamage(CustomDamageHandler handler)
    {
        FabricItemInternals.computeExtraData(this).customDamage(handler);
        return this;
    }

    // Overrides of vanilla methods

    @Override
    public MeatlibItemSettings food(FoodComponent foodComponent)
    {
        super.food(foodComponent);
        return this;
    }

    @Override
    public MeatlibItemSettings maxCount(int maxCount)
    {
        super.maxCount(maxCount);
        return this;
    }

    @Override
    public MeatlibItemSettings maxDamageIfAbsent(int maxDamage)
    {
        super.maxDamageIfAbsent(maxDamage);
        return this;
    }

    @Override
    public MeatlibItemSettings maxDamage(int maxDamage)
    {
        super.maxDamage(maxDamage);
        return this;
    }

    @Override
    public MeatlibItemSettings recipeRemainder(Item recipeRemainder)
    {
        super.recipeRemainder(recipeRemainder);
        return this;
    }

    @Override
    public MeatlibItemSettings rarity(Rarity rarity)
    {
        super.rarity(rarity);
        return this;
    }

    @Override
    public MeatlibItemSettings fireproof()
    {
        super.fireproof();
        return this;
    }

    @Override
    public MeatlibItemSettings requires(FeatureFlag... features)
    {
        super.requires(features);
        return this;
    }

//    public MeatlibItemSettings tag(TagKey<Item> tag)
//    {
//        this.tags.add(tag);
//        return this;
//    }

    // I would like to use varargs here, but it gives an ugly warning.
//    public MeatlibItemSettings tag(List<TagKey<Item>> tags)
//    {
//        this.tags.addAll(tags);
//        return this;
//    }

    public MeatlibItemSettings group(ItemGroup group)
    {
        this.group = group;
        return this;
    }

    // Could use varargs, but that may be unsafe.
    public MeatlibItemSettings tags()
    {
        this.tags = Set.of();
        return this;
    }

    public MeatlibItemSettings tags(TagKey<Item> tags)
    {
        this.tags = Set.of(tags);
        return this;
    }

    public MeatlibItemSettings tags(Set<TagKey<Item>> tags)
    {
        this.tags = Set.copyOf(tags);
        return this;
    }

    public MeatlibItemSettings noLookup()
    {
        this.supportsGuideLookup = false;
        return this;
    }
}
