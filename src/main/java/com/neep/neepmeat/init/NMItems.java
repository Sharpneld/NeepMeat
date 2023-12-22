package com.neep.neepmeat.init;

import com.neep.meatlib.item.BaseCraftingItem;
import com.neep.meatlib.item.TieredCraftingItemFactory;
import com.neep.meatweapons.MeatWeapons;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.item.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

@SuppressWarnings("unused")
public class NMItems
{
    public static TieredCraftingItemFactory ROUGH = new TieredCraftingItemFactory(new String[]{"rough", "pristine"});
    public static TieredCraftingItemFactory CRUDE = new TieredCraftingItemFactory(new String[]{"crude", "adv"});

    public static Item TANK_MINECART = new TankMinecartItem("tank_minecart", new FabricItemSettings().maxCount(1).group(NMItemGroups.GENERAL));

    public static Item SACRIFICIAL_DAGGER = new DaggerItem("sacrificial_dagger", new FabricItemSettings().group(MeatWeapons.WEAPONS));
    public static Item CHEESE_CLEAVER = new CheeseCleaverItem("cheese_cleaver", new FabricItemSettings().group(MeatWeapons.WEAPONS));
    public static Item SLASHER = new SlasherItem("slasher", new FabricItemSettings().group(MeatWeapons.WEAPONS));

    public static Item ANIMAL_HEART = new BaseCraftingItem("animal_heart", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item REANIMATED_HEART = new BaseCraftingItem("reanimated_heart", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item COPPER_COIL = new BaseCraftingItem("copper_coil", 0, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item BIOELECTRIC_ORGAN = new BaseCraftingItem("bioelectric_organ", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item STATOR = new BaseCraftingItem("stator", 0, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item FLYWHEEL = new BaseCraftingItem("flywheel", 0, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item REFRACTORY_BRICKS = new BaseCraftingItem("refractory_brick", 0, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item WHISPER_BRASS = new BaseCraftingItem("whisper_brass", 0, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item MEAT_STEEL = new BaseCraftingItem("meat_steel", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item BLOOD_BUBBLE = new BaseCraftingItem("blood_bubble", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item MEAT_STEEL_COMPONENT = new BaseCraftingItem("meat_steel_component", 0, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item CONTROL_UNIT = new BaseCraftingItem("control_unit", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

    // MEAT
    public static Item RAW_MEAT_BRICK = new BaseCraftingItem("raw_meat_brick", 0, new FabricItemSettings().group(NMItemGroups.INGREDIENTS).food(NMFoodComponents.MEAT_BRICK));
    public static Item COOKED_MEAT_BRICK = new BaseCraftingItem("cooked_meat_brick", 0, new FabricItemSettings().group(NMItemGroups.INGREDIENTS).food(NMFoodComponents.COOKED_MEAT_BRICK));

    // Made from machine fluid
    public static Item MACHINE_FLUID = new BaseCraftingItem("machine_fluid", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

    // Organism Parts
    public static Item DIGESTIVE_SYSTEM = new BaseCraftingItem("digestive_system", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
//    public static Item INTERFACE_PORTS = new BaseCraftingItem("interface_array", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item INTERNAL_COMPONENTS = new BaseCraftingItem("internal_components", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
//    public static Item GANGLIAL_CENTRE = new BaseCraftingItem("ganglial_cluster", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

    public static Item ROUGH_BRAIN = new BaseCraftingItem("brain_rough", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item ENLIGHTENED_BRAIN = new BaseCraftingItem("enlightened_brain", 2, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

    public static Item CRUDE_INTEGRATION_CHARGE = new BaseCraftingItem("integration_charge_crude", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item ADV_INTEGRATION_CHARGE = new BaseCraftingItem("integration_charge_adv", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

    public static Item WHISPER_WHEAT = new BaseCraftingItem("whisper_wheat", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));
    public static Item WHISPER_FLOUR = new BaseCraftingItem("whisper_flour", 1, new FabricItemSettings().group(NMItemGroups.INGREDIENTS));

//    public static Item MEAT_STEEL_BOOTS = new MeatSteelArmourItem("meat_steel_boots", ArmorMaterials.DIAMOND, EquipmentSlot.FEET, new FabricItemSettings().group(NMItemGroups.GENERAL));
//    public static Item MEAT_STEEL_LEGS = new MeatSteelArmourItem("meat_steel_legs", ArmorMaterials.DIAMOND, EquipmentSlot.LEGS, new FabricItemSettings().group(NMItemGroups.GENERAL));
//    public static Item MEAT_STEEL_CHESTPLATE = new MeatSteelArmourItem("meat_steel_chestplate", ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new FabricItemSettings().group(NMItemGroups.GENERAL));
}
