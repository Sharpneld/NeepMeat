package com.neep.neepmeat.init;

import com.neep.neepmeat.api.NMSoundGroups;
import net.minecraft.block.BlockSetType;
import net.minecraft.sound.SoundEvents;

public class NMBlockSets
{
    public static final BlockSetType RUSTY_METAL = new BlockSetType(
            "rusty_metal",
            true,
            NMSoundGroups.METAL,
            SoundEvents.BLOCK_IRON_DOOR_CLOSE,
            SoundEvents.BLOCK_IRON_DOOR_OPEN,
            SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,
            SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,
            SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF,
            SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF,
            SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON
    );
}
