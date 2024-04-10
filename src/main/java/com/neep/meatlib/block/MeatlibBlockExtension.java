package com.neep.meatlib.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

public interface MeatlibBlockExtension
{
    default void neepmeat$appendTags(TagConsumer<Block> consumer) {}

    @Nullable
    default ItemConvertible neepmeat$simpleDrop()
    {
        return null;
    }

    interface TagConsumer<T>
    {
        void offer(TagKey<T> tag);
    }
}
