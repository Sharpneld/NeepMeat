package com.neep.neepmeat.client;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.block.BasePaintedBlock;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.client.model.SwordModel;
import com.neep.neepmeat.client.renderer.*;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.init.BlockInitialiser;
import com.neep.neepmeat.init.FluidInitialiser;
import com.neep.neepmeat.init.ItemInit;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ExtraModelProvider;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

import java.util.ArrayList;
import java.util.List;

public class NeepMeatClient
{
    public static final EntityModelLayer MODEL_GLASS_TANK_LAYER = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "glass_tank"), "main");

    public static List<BasePaintedBlock.PaintedBlock> COLOURED_BLOCKS = new ArrayList<>();

    public static void registerRenderers()
    {

        // Custom baked models
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new NeepMeatModelProvider());
        ModelLoadingRegistry.INSTANCE.registerModelProvider(NMExtraModels.EXTRA_MODELS);

        // BlockEntity renderers
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.GLASS_TANK_BLOCK_ENTITY, GlassTankRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_GLASS_TANK_LAYER, GlassTankModel::getTexturedModelData);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.ITEM_BUFFER_BLOCK_ENTITY, ItemBufferRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.TROMMEL_BLOCK_ENTITY, TrommelRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.INTEGRATOR, IntegratorEggRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityInitialiser.BIG_LEVER, BigLeverRenderer::new);

        GeoItemRenderer.registerItemRenderer(ItemInit.SLASHER, new SwordRenderer<>(new SwordModel<>(
                NeepMeat.NAMESPACE,
                "geo/slasher.geo.json",
                "textures/item/slasher.png",
                "animations/slasher.animation.json"
        )));

        GeoItemRenderer.registerItemRenderer(ItemInit.CHEESE_CLEAVER, new SwordRenderer<>(new SwordModel<>(
                NeepMeat.NAMESPACE,
                "geo/cheese_cleaver.geo.json",
                "textures/item/cheese_cleaver.png",
                "animations/cheese_cleaver.animation.json"
        )));

        // Fluid textures
        FluidRenderHandlerRegistry.INSTANCE.register(FluidInitialiser.STILL_BLOOD, FluidInitialiser.FLOWING_BLOOD, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_still"),
                0x440d0e
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(FluidInitialiser.STILL_ENRICHED_BLOOD, FluidInitialiser.FLOWING_ENRICHED_BLOOD, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_still"),
                0xbb1d1d
        ));

        for (BasePaintedBlock.PaintedBlock block : COLOURED_BLOCKS)
        {
            ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> block.getColour(), block);
            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> block.getColour(), block.asItem());
        }

//        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 0x3495eb, BlockInitialiser.GREY_SMOOTH_TILE);
//        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x3495eb, BlockInitialiser.GREY_SMOOTH_TILE.asItem());

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidInitialiser.STILL_BLOOD, FluidInitialiser.FLOWING_BLOOD);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidInitialiser.STILL_ENRICHED_BLOOD, FluidInitialiser.FLOWING_ENRICHED_BLOOD);

        //if you want to use custom textures they needs to be registered.
        //In this example this is unnecessary because the vanilla water textures are already registered.
        //To register your custom textures use this method.
        //ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
        //    registry.register(new Identifier("modid:block/custom_fluid_still"));
        //    registry.register(new Identifier("modid:block/custom_fluid_flowing"));
        //});

        // Block cutouts
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.GLASS_TANK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.MESH_PANE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.PUMP);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.RUSTED_BARS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.PNEUMATIC_TUBE);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.SCAFFOLD_PLATFORM);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.SCAFFOLD_PLATFORM.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.SCAFFOLD_PLATFORM.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.BLUE_SCAFFOLD);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.BLUE_SCAFFOLD.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.BLUE_SCAFFOLD.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.YELLOW_SCAFFOLD);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.YELLOW_SCAFFOLD.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) BlockInitialiser.YELLOW_SCAFFOLD.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.TANK_WALL);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.SCAFFOLD_TRAPDOOR);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), BlockInitialiser.SCAFFOLD_TRAPDOOR);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.FLUID_DRAIN);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockInitialiser.SLOPE_TEST);

    }
}
