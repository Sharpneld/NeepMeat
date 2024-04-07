package com.neep.meatlib.datagen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.registry.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider
{
    public ItemTagProvider(FabricDataGenerator dataGenerator)
    {
        super(dataGenerator, null);
    }

    @Override
    protected void generateTags()
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
