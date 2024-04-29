package com.neep.neepmeat.client.model.entity;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.worm.WormEntity;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.Identifier;

public class WormEntityModel extends GeoModel<WormEntity>
{
    @Override
    public Identifier getModelResource(WormEntity object)
    {
        return new Identifier(NeepMeat.NAMESPACE, "geo/god_worm.geo.json");
    }

    @Override
    public Identifier getTextureResource(WormEntity object)
    {
        return new Identifier(NeepMeat.NAMESPACE, "textures/block/duat_stone.png");
    }

    @Override
    public Identifier getAnimationResource(WormEntity animatable)
    {
        return new Identifier(NeepMeat.NAMESPACE, "animations/god_worm.animation.json");
    }
}
