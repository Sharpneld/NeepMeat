package com.neep.meatlib.datagen.loot;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.block.MeatlibBlockExtension;
import com.neep.meatlib.registry.BlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootTable;

public class BlockLootTableProvider extends FabricBlockLootTableProvider
{
    public BlockLootTableProvider(FabricDataGenerator output)
    {
        super(output);
    }

    @Override
    protected void generateBlockLootTables()
    {
        for (Block entry : BlockRegistry.REGISTERED_BLOCKS)
        {
            if (entry instanceof MeatlibBlock meatBlock)
            {
                // True by default for MeatlibBlock
                if (meatBlock.autoGenDrop())
                {
                    // True by default for MeatlibBlock
                    LootTable.Builder builder = meatBlock.genLoot(this);
                    if (builder != null)
                    {
                        addDrop(entry, builder);
                    }
                    else
                    {
                        // Legacy behaviour
                        ItemConvertible like = meatBlock.dropsLike();
                        if (like != null)
                        {
                            this.addDrop(entry, like);
                        }
                    }
                }
            }
            else if (entry instanceof MeatlibBlockExtension extension)
            {
                // The default behaviour injected into Item delegates to MeatlibItemSettings if applicable.
                ItemConvertible drop = extension.neepmeat$simpleDrop();
                if (drop != null)
                {
                    this.addDrop(entry, drop);
                }
            }
        }
    }
}
