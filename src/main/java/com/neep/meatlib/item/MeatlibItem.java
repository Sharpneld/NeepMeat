package com.neep.meatlib.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Consumer;

public interface MeatlibItem
{
    // 1.19.2 backport insanity. Ignore.
    @Nullable default ItemGroup getGroupOverride()
    {
        return MeatItemGroups.getGroup((Item) this);
    }

    String getRegistryName();

    // 1.19.2 backport insanity. Ignore.
    default Item group(ItemGroup group)
    {
        MeatItemGroups.queueItem(group, (Item) this);
        return (Item) this;
    }

    default void appendTags(Consumer<TagKey<Item>> consumer)
    {
    }
}
