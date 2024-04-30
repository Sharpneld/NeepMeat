package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class IntegratorOverlayRenderLayer extends GeoRenderLayer<IntegratorBlockEntity>
{
    private static final Identifier LAYER = new Identifier(NeepMeat.NAMESPACE, "textures/entity/integrator_basic_overlay.png");

    public IntegratorOverlayRenderLayer(GeoRenderer<IntegratorBlockEntity> entityRendererIn)
    {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrices, IntegratorBlockEntity be, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay)
    {
        RenderLayer armorRenderType = RenderLayer.getArmorCutoutNoCull(LAYER);

        getRenderer().reRender(getDefaultBakedModel(be), matrices, bufferSource, be, armorRenderType,
                bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
                1, 1, 1, 1);
    }
}