package com.neep.meatlib.mixin;

import com.neep.meatlib.item.MeatlibItemExtension;
import com.neep.meatlib.item.MeatlibItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin implements MeatlibItemExtension
{
    @Unique
    private ItemGroup itemGroup;

    @Unique
    private boolean supportsGuideLookup;

    @Inject(method = "<init>", at = @At("TAIL"))
    void onInit(Item.Settings settings, CallbackInfo ci)
    {
        if (settings instanceof MeatlibItemSettings meatlibItemSettings)
        {
            this.itemGroup = meatlibItemSettings.group;
            this.supportsGuideLookup = meatlibItemSettings.supportsGuideLookup;
        }
    }

//    @Inject(at = @At("TAIL"), method = "appendTooltip*")
//    private void onAppendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci)
//    {
//    }

//    @Override
//    public @Nullable ItemGroup meatlib$getItemGroup()
//    {
//        return itemGroup;
//    }

    @Override
    public boolean meatlib$supportsGuideLookup()
    {
        return supportsGuideLookup;
    }
}
