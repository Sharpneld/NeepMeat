package com.neep.meatlib.datagen;

import com.neep.meatlib.block.BaseWallBlock;
import com.neep.meatlib.block.MeatlibBlockExtension;
import com.neep.meatlib.registry.BlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    public BlockTagProvider(FabricDataGenerator output)
    {
        super(output);
    }

    @Override
    protected void generateTags()
    {
        for (Block entry : BlockRegistry.REGISTERED_BLOCKS)
        {
            MeatlibBlockExtension.TagConsumer<Block> consumer = t -> getOrCreateTagBuilder(t).add(entry);
            entry.neepmeat$appendTags(consumer);

            // JAAAAAAAAAAANK
            if (entry instanceof BaseWallBlock wall)
            {
                this.getOrCreateTagBuilder(wall.getWallTag()).add(entry);
            }
        }
    }
}
