package com.neep.meatweapons.client.model;

import com.neep.meatweapons.item.BaseGunItem;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.Identifier;

public class BaseGunModel<T extends BaseGunItem> extends GeoModel<T>
{
    private final Identifier modelPath;
    private final Identifier texturePath;
    private final Identifier animationPath;

    public BaseGunModel(Identifier modelPath, Identifier texturePath, Identifier animationPath)
    {
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        this.animationPath = animationPath;
    }


    @Override
    public Identifier getModelResource(T object)
    {
        return modelPath;
    }

    @Override
    public Identifier getTextureResource(T object)
    {
        return texturePath;
    }

    @Override
    public Identifier getAnimationResource(T animatable)
    {
        return animationPath;
    }
}
