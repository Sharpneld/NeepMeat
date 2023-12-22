package com.neep.neepmeat.init;

import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.item.DaggerItem;
import com.neep.neepmeat.item.base.BaseItem;
import com.neep.neepmeat.item.NMItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class ItemInit
{
    public static Map<String, NMItem> ITEMS = new HashMap<>();

    public static Item SACRIFICIAL_DAGGER = new DaggerItem("sacrificial_dagger", new FabricItemSettings().group(NMItemGroups.GENERAL));
    public static Item ANIMAL_HEART = new BaseItem("animal_heart", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item REANIMATED_HEART = new BaseItem("reanimated_heart", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item FLESH = new BaseItem("flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item REANIMATED_FLESH = new BaseItem("reanimated_flesh", new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

    public static void putItem(String id, NMItem item)
    {
        if (!(item instanceof Item))
        {
            throw new IllegalArgumentException("tried to queue a non-item for item registration");
        }
        ITEMS.put(id, item);
    }

    public static void registerItems()
    {
        for (NMItem item : ITEMS.values())
        {
            Registry.register(Registry.ITEM, new Identifier(NeepMeat.NAMESPACE, item.getRegistryName()), (Item) item);
        }
    }

}
