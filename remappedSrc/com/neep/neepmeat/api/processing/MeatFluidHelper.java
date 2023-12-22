package com.neep.neepmeat.api.processing;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.FoodComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;

public class MeatFluidHelper
{
    protected static final String KEY_ROOT = NeepMeat.NAMESPACE + ":food";
    protected static final String KEY_HUNGER = "hunger";
    protected static final String KEY_SATURATION = "saturation";
    public static final int MAX_HUNGER = 10;

    public static float getHunger(FluidVariant variant)
    {
        NbtCompound root = getRoot(variant);
        if (root != null)
        {
            return MathHelper.clamp(root.getFloat(KEY_HUNGER), 0, MAX_HUNGER);
        }
        return 0;
    }

    public static float getHunger(NbtCompound nbt)
    {
        if (nbt != null)
        {
            NbtCompound root = getOrCreateRoot(nbt);
            return root.getFloat(KEY_HUNGER);
        }
        return 0;
    }

    public static void setHunger(NbtCompound nbt, float hunger)
    {
        if (nbt != null)
        {
            NbtCompound root = getOrCreateRoot(nbt);
            root.putFloat(KEY_HUNGER, hunger);
        }
    }

    public static void setSaturation(NbtCompound nbt, float saturation)
    {
        if (nbt != null)
        {
            NbtCompound root = getOrCreateRoot(nbt);
            root.putFloat(KEY_SATURATION, saturation);
        }
    }

    public static float getSaturation(FluidVariant variant)
    {
        NbtCompound root = getRoot(variant);
        if (root != null)
        {
            return  root.getFloat(KEY_SATURATION);
        }
        return 0;
    }

    public static float getSaturation(NbtCompound nbt)
    {
        if (nbt != null)
        {
            NbtCompound root = getOrCreateRoot(nbt);
            return root.getFloat(KEY_SATURATION);
        }
        return 0;
    }

    protected static NbtCompound getRoot(FluidVariant variant)
    {
        if (variant.hasNbt()) return variant.getNbt().getCompound(KEY_ROOT);
        return null;
    }

    protected static NbtCompound getOrCreateRoot(NbtCompound nbt)
    {
        if (nbt.contains(KEY_ROOT, NbtCompound.COMPOUND_TYPE)) return nbt.getCompound(KEY_ROOT);
        else
        {
            NbtCompound newRoot = new NbtCompound();
            nbt.put(KEY_ROOT, newRoot);
            return newRoot;
        }
    }

    public static int getColour(FluidVariant variant)
    {
        float hunger = getHunger(variant);
        int r = (int) (255 * hunger / MAX_HUNGER);
        return r << 16;
    }

    public static FluidVariant getVariant(FoodComponent food)
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt(KEY_HUNGER, food.getHunger());
        nbt.putFloat(KEY_SATURATION, food.getSaturationModifier());
        NbtCompound root = new NbtCompound();
        root.put(KEY_ROOT, nbt);
        return FluidVariant.of(NMFluids.STILL_C_MEAT, root);
    }

    private static float floor(float f, int n)
    {
        return (float) (Math.floor(f * Math.pow(10f, n)) / (float) Math.pow(10, n));
    }

    /** Copies the root tag from the old tag to the new one without touching unrelated entries.
     * Intended for transferring meat information from a FluidVariant to an ItemVariant.
     *
     * Hunger and saturation are rounded down to the nearest 1 and 0.1 respectively.
     */
    public static NbtCompound copyRootRounded(NbtCompound nbt, NbtCompound oldNbt)
    {
        if (oldNbt != null)
        {
            NbtCompound root = oldNbt.getCompound(KEY_ROOT).copy();
            root.putFloat(KEY_HUNGER, floor(root.getFloat(KEY_HUNGER), 0));
            root.putFloat(KEY_SATURATION, floor(root.getFloat(KEY_SATURATION), 1));
            nbt.put(KEY_ROOT, root);
        }
        return nbt;
    }
}
