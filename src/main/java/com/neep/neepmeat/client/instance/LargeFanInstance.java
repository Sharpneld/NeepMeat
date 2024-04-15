package com.neep.neepmeat.client.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.neepmeat.block.LargeFanBlock;
import com.neep.neepmeat.block.entity.LargeFanBlockEntity;
import com.neep.neepmeat.client.NMExtraModels;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class LargeFanInstance extends BlockEntityInstance<LargeFanBlockEntity> implements DynamicInstance
{
    private final ModelData vanes;

    public LargeFanInstance(MaterialManager materialManager, LargeFanBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        this.vanes = materialManager.defaultCutout().material(Materials.TRANSFORMED).getModel(NMExtraModels.LARGE_FAN_VANES).createInstance();
    }

    @Override
    protected void remove()
    {
        vanes.delete();
    }

    @Override
    public void beginFrame()
    {
        Direction.Axis axis = blockEntity.getCachedState().get(LargeFanBlock.AXIS);

        float rotation = MathHelper.wrapDegrees(blockEntity.rotationOffset + AnimationTickHolder.getRenderTime());

        vanes.loadIdentity()
                .translate(getInstancePosition())
                .centre();
        rotateAxis(vanes, axis);
        vanes.rotateY(rotation);
        vanes.unCentre();

    }

    private void rotateAxis(ModelData model, Direction.Axis axis)
    {
        switch (axis)
        {
            case X -> model.rotate(Direction.SOUTH, MathHelper.HALF_PI);
            case Z -> model.rotate(Direction.EAST, MathHelper.HALF_PI);
            case Y -> {}
        }
    }

    @Override
    public void updateLight()
    {
        super.updateLight();
        relight(getWorldPosition(), vanes);
    }
}
