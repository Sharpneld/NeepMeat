package com.neep.neepmeat.mixin;

import com.neep.neepmeat.item.MeatSteelHoeItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class EnchantmentTargetMixins
{
    @Mixin(targets = "net/minecraft/enchantment/EnchantmentTarget$11")
    public static class Weapon
    {
        @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
        void onIsAcceptableItem(Item item, CallbackInfoReturnable<Boolean> cir)
        {
            if (item instanceof MeatSteelHoeItem)
            {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Mixin(targets = "net/minecraft/enchantment/EnchantmentTarget$12")
    public static class Digger
    {
        @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
        void onIsAcceptableItem(Item item, CallbackInfoReturnable<Boolean> cir)
        {
            if (item instanceof MeatSteelHoeItem)
            {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
