package com.neep.meatlib.item;

import com.neep.meatlib.block.MeatlibBlockExtension;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface MeatlibItemExtension
{
    // Not sure if the namespace prefix is necessary.
    // I've forgotten which document described best practices.
    @Nullable
    default ItemGroup meatlib$getItemGroup()
    {
        return null;
    }

    default boolean meatlib$supportsGuideLookup()
    {
        return false;
    }

    default void meatlib$appendTags(MeatlibBlockExtension.TagConsumer<Item> consumer) {}
}
