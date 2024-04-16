package com.neep.meatlib.datagen;

import com.neep.meatlib.block.BaseWallBlock;
import com.neep.meatlib.block.MeatlibBlockExtension;
import com.neep.meatlib.registry.BlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    public BlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupCompletableFuture)
    {
        super(output, lookupCompletableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        for (Block entry : BlockRegistry.REGISTERED_BLOCKS)
        {
//            if (entry instanceof MeatlibBlock meatBlock)
//            {
//                this.getOrCreateTagBuilder(meatBlock.getPreferredTool()).add(entry);
//            }

            MeatlibBlockExtension.TagConsumer<Block> consumer = t -> getOrCreateTagBuilder(t).add(entry);
            entry.neepmeat$appendTags(consumer);

            if (entry instanceof BaseWallBlock wall)
            {
                this.getOrCreateTagBuilder(wall.getWallTag()).add(entry);
            }
        }
    }
}
