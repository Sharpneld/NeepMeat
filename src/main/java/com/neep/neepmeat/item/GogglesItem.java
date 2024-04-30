package com.neep.neepmeat.item;

import com.google.common.collect.Multimap;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.GenericModel;
import com.neep.neepmeat.client.renderer.entity.GogglesArmourRenderer;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.network.SerializableDataTicket;
import mod.azure.azurelib.platform.services.AzureLibNetwork;
import mod.azure.azurelib.renderer.GeoArmorRenderer;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GogglesItem extends ArmorItem implements MeatlibItem, GeoItem
{
    private final AnimatableInstanceCache instanceCache = AzureLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    private final String registryName;
    private final TooltipSupplier tooltip = TooltipSupplier.simple(1);

    public GogglesItem(String name , ArmorMaterial material, Settings settings)
    {
        super(material, Type.HELMET, settings);
        this.registryName = name;
        ItemRegistry.queue(this);
    }

    private PlayState controller(AnimationState<GeoAnimatable> event)
    {
//        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.goggles.idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        this.tooltip.apply(this, tooltip);
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer)
    {
        consumer.accept(new RenderProvider()
        {
            private GeoArmorRenderer<?> renderer;

            @Override
            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original)
            {
                if (this.renderer == null) // Important that we do this. If we just instantiate  it directly in the field it can cause incompatibilities with some mods.
                    this.renderer = new GogglesArmourRenderer(new GenericModel<>(
                            NeepMeat.NAMESPACE,
                            "geo/goggles.geo.json",
                            "textures/entity/armour/goggles.png",
                            "animations/goggles.animation.json"
                    ));

                // This prepares our GeoArmorRenderer for the current render frame.
                // These parameters may be null however, so we don't do anything further with them
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
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
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 20, this::controller));
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return instanceCache;
    }

    @Override
    public double getBoneResetTime() {
        return GeoItem.super.getBoneResetTime();
    }

    @Override
    public boolean shouldPlayAnimsWhileGamePaused() {
        return GeoItem.super.shouldPlayAnimsWhileGamePaused();
    }

    @Override
    public @Nullable ItemGroup meatlib$getItemGroup() {
        return super.meatlib$getItemGroup();
    }

    @Override
    public boolean meatlib$supportsGuideLookup() {
        return super.meatlib$supportsGuideLookup();
    }

    @Override
    public double getTick(Object itemStack) {
        return GeoItem.super.getTick(itemStack);
    }

    @Override
    public boolean isPerspectiveAware() {
        return GeoItem.super.isPerspectiveAware();
    }

    @Override
    public <D> @Nullable D getAnimData(long instanceId, SerializableDataTicket<D> dataTicket) {
        return GeoItem.super.getAnimData(instanceId, dataTicket);
    }

    @Override
    public <D> void setAnimData(Entity relatedEntity, long instanceId, SerializableDataTicket<D> dataTicket, D data) {
        GeoItem.super.setAnimData(relatedEntity, instanceId, dataTicket, data);
    }

    @Override
    public <D> void syncAnimData(long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack) {
        GeoItem.super.syncAnimData(instanceId, dataTicket, data, entityToTrack);
    }

    @Override
    public void triggerAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, String animName) {
        GeoItem.super.triggerAnim(relatedEntity, instanceId, controllerName, animName);
    }

    @Override
    public void triggerAnim(long instanceId, @Nullable String controllerName, String animName, AzureLibNetwork.IPacketCallback packetCallback) {
        GeoItem.super.triggerAnim(instanceId, controllerName, animName, packetCallback);
    }

    @Override
    public @Nullable AnimatableInstanceCache animatableCacheOverride() {
        return GeoItem.super.animatableCacheOverride();
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return super.allowNbtUpdateAnimation(player, hand, oldStack, newStack);
    }

    @Override
    public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        return super.allowContinuingBlockBreaking(player, oldStack, newStack);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        return super.getAttributeModifiers(stack, slot);
    }

    @Override
    public boolean isSuitableFor(ItemStack stack, BlockState state) {
        return super.isSuitableFor(stack, state);
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return super.getRecipeRemainder(stack);
    }

    @Override
    public TypedActionResult<ItemStack> equipAndSwap(Item item, World world, PlayerEntity user, Hand hand) {
        return super.equipAndSwap(item, world, user, hand);
    }

    @Override
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return super.isEnabled(enabledFeatures);
    }
}
