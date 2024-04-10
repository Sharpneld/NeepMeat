package com.neep.meatlib.registry;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.item.MeatlibItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemRegistry
{
    public static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();
    public static final List<Item> REGISTERED_ITEMS = new ArrayList<>();

    /**
     * @return An item whose ID matches the block, hopefully the block's corresponding BlockItem.
     */
    @Nullable
    public static Item getMatchingItem(Block block)
    {
        return Registry.ITEM.getOrEmpty(Registry.BLOCK.getId(block)).orElse(null);
    }

    public static Item queue(String namespace, MeatlibItem item)
    {
        MeatLib.assertActive(item);
        if (!(item instanceof Item))
        {
            throw new IllegalArgumentException("tried to queue a non-item for item registration");
        }
        ITEMS.put(new Identifier(namespace, item.getRegistryName()), (Item) item);
        return (Item) item;
    }

    public static <T extends Item & MeatlibItem> T queue(T item)
    {
        MeatLib.assertActive(item);
        ITEMS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, item.getRegistryName()), (Item) item);
        return item;
    }

    public static Item queue(String path, Item item)
    {
        MeatLib.assertActive(item);
        ITEMS.put(new Identifier(MeatLib.CURRENT_NAMESPACE, path), item);
        return item;
    }

    public static void flush()
    {
        for (Iterator<Map.Entry<Identifier, Item>> it = ITEMS.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<Identifier, Item> entry = it.next();

            // TODO: Remove the jank
            Registry.register(Registry.ITEM, entry.getKey(), entry.getValue());

            REGISTERED_ITEMS.add(entry.getValue());

            it.remove();
        }
        ITEMS.clear();
    }
}
