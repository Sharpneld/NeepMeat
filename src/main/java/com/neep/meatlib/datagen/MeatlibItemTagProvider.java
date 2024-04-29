package com.neep.meatlib.datagen;

import com.neep.meatlib.block.MeatlibBlockExtension;
import com.neep.meatlib.registry.ItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class MeatlibItemTagProvider extends FabricTagProvider.ItemTagProvider
{
    public MeatlibItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
//        Multimap<TagKey<Item>, Item> map = HashMultimap.create();
//        ItemRegistry.REGISTERED_ITEMS.stream()
//                .filter(item -> item instanceof MeatlibItem)
//                .forEach(item -> ((MeatlibItem) item).appendTags(tag -> map.put(tag, item)));

        for (Item entry : ItemRegistry.REGISTERED_ITEMS)
        {
            MeatlibBlockExtension.TagConsumer<Item> consumer = t -> getOrCreateTagBuilder(t).add(entry);
            entry.meatlib$appendTags(consumer);
        }

//        map.asMap().forEach((tag, items) ->
//        {
//            var builder = getOrCreateTagBuilder(tag);
//            items.forEach(builder::add);
//        });
    }
}
