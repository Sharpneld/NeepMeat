package com.neep.meatlib.datagen.loot;

import com.neep.meatlib.block.IMeatBlock;
import com.neep.meatlib.registry.BlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

import java.util.Map;

public class BlockLootTableProvider extends FabricBlockLootTableProvider
{
    public BlockLootTableProvider(FabricDataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    @Override
    protected void generateBlockLootTables()
    {
        for (Map.Entry<Identifier, Block> entry : BlockRegistry.BLOCKS.entrySet())
        {
            if (entry.getValue() instanceof IMeatBlock meatBlock && meatBlock.dropsSelf())
            {
                this.addDrop(entry.getValue());
            }
        }
    }
}