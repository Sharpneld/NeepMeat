package com.neep.meatlib.mixin;

import com.neep.meatlib.block.MeatlibBlockExtension;
import com.neep.meatlib.block.MeatlibBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.Function;

@Mixin(Block.class)
public abstract class BlockMixin implements MeatlibBlockExtension
{
    // Oooooh, storing tags in every block may be a waste of space since they are only used in datagen.
    // I'm sure it'll be fine.
    @Unique private Set<TagKey<Block>> tags = Set.of();
    @Unique @Nullable private Function<Block, ItemConvertible> simpleDrop;

    @Inject(method = "<init>", at = @At("TAIL"))
    void onInit(AbstractBlock.Settings settings, CallbackInfo ci)
    {
        if (settings instanceof MeatlibBlockSettings mbs)
        {
            this.tags = mbs.getTags();
            this.simpleDrop = mbs.getSimpleDrop();
        }
    }

    @Override
    public void neepmeat$appendTags(TagConsumer<Block> consumer)
    {
        for (var tag : tags)
        {
            consumer.offer(tag);
        }
    }

    @Override
    public @Nullable ItemConvertible neepmeat$simpleDrop()
    {
        return simpleDrop != null ? simpleDrop.apply((Block) (Object) this) : null;
    }
}
