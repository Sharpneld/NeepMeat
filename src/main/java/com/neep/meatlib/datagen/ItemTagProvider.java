package com.neep.meatlib.datagen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.registry.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider
{
    public ItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        Multimap<TagKey<Item>, Item> map = HashMultimap.create();
        ItemRegistry.REGISTERED_ITEMS.stream()
                .filter(item -> item instanceof MeatlibItem)
                .forEach(item -> ((MeatlibItem) item).appendTags(tag -> map.put(tag, item)));

        map.asMap().forEach((tag, items) ->
        {
            var builder = getOrCreateTagBuilder(tag);
            items.forEach(builder::add);
        });
    }
}
