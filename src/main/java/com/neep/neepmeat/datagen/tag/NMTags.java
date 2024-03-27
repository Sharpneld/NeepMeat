package com.neep.neepmeat.datagen.tag;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NMTags
{
    public static final TagKey<Item> CHARNEL_COMPACTOR = registerItem(NeepMeat.NAMESPACE, "charnel_substrate");
    public static final TagKey<Item> BLOOD_BUBBLE_LOGS = registerItem(NeepMeat.NAMESPACE, "blood_bubble_logs");
    public static final TagKey<Item> FLUID_PIPES = registerItem(NeepMeat.NAMESPACE, "fluid_pipes");

    public static final TagKey<Item> RAW_MEAT = TagKey.of(Registry.ITEM_KEY, new Identifier("c", "raw_meat"));
    public static final TagKey<Item> METAL_SCAFFOLDING = TagKey.of(Registry.ITEM_KEY, new Identifier("c", "metal_scaffolding"));

    public static final TagKey<Item> GUIDE_LOOKUP = TagKey.of(Registry.ITEM.getKey(), new Identifier(NeepMeat.NAMESPACE, "guide_lookup"));

    public static final TagKey<EntityType<?>> CLONEABLE = TagKey.of(Registry.ENTITY_TYPE.getKey(), new Identifier(NeepMeat.NAMESPACE, "cloneable"));

    private static TagKey<Item> registerItem(String namespace, String id)
    {
//        return RequiredTagListRegistry.register(new Identifier(namespace, id), "charnel_substrate");
//        return .method_40092(Registry.BLOCK_KEY, new Identifier("fabric", id));
//        return class_6862.method_40092(Registry.BLOCK_KEY, new Identifier("fabric", id));
        return TagKey.of(Registry.ITEM_KEY, new Identifier(namespace, id));
    }

    public static void init()
    {

    }
}
