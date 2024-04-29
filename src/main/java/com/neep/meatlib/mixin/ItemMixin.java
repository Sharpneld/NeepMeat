package com.neep.meatlib.mixin;

import com.neep.meatlib.block.MeatlibBlockExtension;
import com.neep.meatlib.item.MeatlibItemExtension;
import com.neep.meatlib.item.MeatlibItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Item.class)
public abstract class ItemMixin implements MeatlibItemExtension
{
    @Unique private Set<TagKey<Item>> tags = Set.of();
    @Unique private ItemGroup itemGroup;
    @Unique private boolean supportsGuideLookup;

    @Inject(method = "<init>", at = @At("TAIL"))
    void onInit(Item.Settings settings, CallbackInfo ci)
    {
        if (settings instanceof MeatlibItemSettings mis)
        {
            this.tags = mis.getTags();
            this.itemGroup = mis.group;
            this.supportsGuideLookup = mis.supportsGuideLookup;
        }
    }

    @Override
    public @Nullable ItemGroup meatlib$getItemGroup()
    {
        return itemGroup;
    }

    @Override
    public boolean meatlib$supportsGuideLookup()
    {
        return supportsGuideLookup;
    }

    @Override
    public void meatlib$appendTags(MeatlibBlockExtension.TagConsumer<Item> consumer)
    {
        for (var tag : tags)
        {
            consumer.offer(tag);
        }
    }
}
