package com.neep.neepmeat.player.implant;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class PinealEyeImplant implements PlayerImplant
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "pineal_eye");

    public PinealEyeImplant(PlayerEntity player)
    {

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }

    @Override
    public void tick()
    {

    }
}