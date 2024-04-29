package com.neep.neepmeat.item;

import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.registry.ItemRegistry;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.renderer.GeoArmorRenderer;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MeatSteelArmourItem extends ArmorItem implements MeatlibItem, GeoItem
{
    private AnimatableInstanceCache instanceCache = AzureLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    protected final String registryName;

    public MeatSteelArmourItem(String name , ArmorMaterial material, ArmorItem.Type type, Settings settings)
    {
        super(material, type, settings);
        this.registryName = name;
        ItemRegistry.queue(this);
    }

    private PlayState predicate(AnimationState<GeoAnimatable> event)
    {
        event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.meat_steel_armour.idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer)
    {
        consumer.accept(new RenderProvider()
        {
            private GeoArmorRenderer<?> renderer;

//            @Override
//            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
//                if(this.renderer == null) // Important that we do this. If we just instantiate  it directly in the field it can cause incompatibilities with some mods.
//                    this.renderer = new MeatSteelArmourRenderer();
//
//                // This prepares our GeoArmorRenderer for the current render frame.
//                // These parameters may be null however, so we don't do anything further with them
//                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
//
//                return this.renderer;
//            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider()
    {
        return renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 20, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return instanceCache;
    }
}
