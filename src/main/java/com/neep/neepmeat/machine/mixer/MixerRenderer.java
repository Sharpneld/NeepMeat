package com.neep.neepmeat.machine.mixer;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.client.renderer.MultiFluidRenderer;
import com.neep.neepmeat.fluid_transfer.storage.WritableSingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@SuppressWarnings("UnstableApiUsage")
public class MixerRenderer implements BlockEntityRenderer<MixerBlockEntity>
{
    public MixerRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(MixerBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        matrices.translate(0, 1 - 5f / 16f, 0);
        float progress = 0;
        float nextOutput = 0;
        if (be.getCurrentRecipe() != null)
        {
            progress = (be.getWorld().getTime() + tickDelta - be.processStart) / (float) be.processLength;
            nextOutput = progress * be.getCurrentRecipe().fluidOutput.amount();
        }

        WritableSingleFluidStorage storage = (WritableSingleFluidStorage) be.storage.getFluidOutput();
        storage.renderLevel = MathHelper.lerp(0.1f, storage.renderLevel,(storage.getAmount() + nextOutput) / (float) storage.getCapacity());
        float outputEnd = storage.renderLevel;
        FluidVariant output = storage.getResource();

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(0.9f, 1, 0.9f);
        matrices.translate(-0.5, -0.5, -0.5);
        MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, output, 0, outputEnd, outputEnd, 1);

        if (be.getCurrentRecipe() != null)
        {
            FluidVariant var1 = (FluidVariant) be.getCurrentRecipe().fluidInput1.resource();
            FluidVariant var2 = (FluidVariant) be.getCurrentRecipe().fluidInput2.resource();
            FluidVariant var3 = (FluidVariant) be.getCurrentRecipe().fluidOutput.resource();

            float scale = (-Math.abs(2 * progress - 1) + 1) * 0.1f;

            MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, var1, outputEnd, outputEnd + scale, outputEnd + scale, 1);
            MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, var2, outputEnd + scale, outputEnd + scale * 2, outputEnd + scale * 2, 1);

            if (progress > 0.5f)
            {
                float offset = 0.5f - scale;
//                MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, var3, outputEnd + scale * 2, outputEnd + 0.5f, outputEnd + 0.5f, 1);
            }
        }
        matrices.pop();
        matrices.pop();

        matrices.translate(0.5, 1.5, 0.5);
        float rotatingAngle = MathHelper.wrapDegrees((be.getWorld().getTime() + tickDelta) * 100f);
        be.bladeAngle = MathHelper.lerpAngleDegrees(0.1f, be.bladeAngle, be.currentRecipe == null ? 0 : rotatingAngle);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(be.bladeAngle));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.MIXER_AGITATOR_BLADES, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
    }
}
