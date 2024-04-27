package com.neep.meatlib.item;

import com.neep.meatlib.block.MeatlibBlockExtension;
import net.minecraft.item.Item;

public interface MeatlibItemExtension
{
    default boolean meatlib$supportsGuideLookup()
    {
        return false;
    }

    default void meatlib$appendTags(MeatlibBlockExtension.TagConsumer<Item> consumer) {}
}
