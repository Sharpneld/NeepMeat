package com.neep.neepmeat.item;

import com.google.common.collect.Multimap;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.GenericModel;
import com.neep.neepmeat.client.renderer.SwordRenderer;
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
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SlasherItem extends AnimatedSword implements GeoItem
{
    public static String CONTROLLER_NAME = "controller";
    private final AnimatableInstanceCache instanceCache = AzureLibUtil.createInstanceCache(this);
    private final Supplier<Object> rendererProvider = GeoItem.makeRenderer(this);

    public SlasherItem(String registryName, Settings settings)
    {
        super(registryName, ToolMaterials.DIAMOND, 0, -1.2f, settings);
    }

    private PlayState predicate(AnimationState<GeoAnimatable> event)
    {
        return PlayState.CONTINUE;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        super.postHit(stack, target, attacker);
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            long id = GeoItem.getOrAssignId(user.getStackInHand(hand), serverWorld);
//            triggerAnim(user, id, );

//            if (user.isSprinting())
//            {
//                GeckoLibNetwork.syncAnimation(user, this, id, ANIM_STAB);
//            }
//            else
//            {
//                GeckoLibNetwork.syncAnimation(user, this, id, ANIM_SWING);
//            }
        }
        return super.use(world, user, hand);
    }

//    @Override
//    public void onAnimationSync(int id, int state)
//    {
//        final AnimationController controller = GeckoLibUtil.getControllerForID(this.factory, id, CONTROLLER_NAME);
//
//        controller.transitionLengthTicks = 1;
//        switch (state)
//        {
//            case ANIM_SWING:
//                controller.markNeedsReload();
//                controller.setAnimation(new AnimationBuilder().addAnimation("animation.slasher.swing"));
//                break;
//            case ANIM_STAB:
//                controller.markNeedsReload();
//                controller.setAnimation(new AnimationBuilder().addAnimation("animation.slasher.stab"));
//                break;
//            default:
//
//
//        }
//    }

    @Override
    public AnimationQueue getQueue()
    {
        return null;
    }

    @Override
    public void syncNearby(String name) {
        super.syncNearby(name);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer)
    {
        consumer.accept(new RenderProvider()
        {
            private SwordRenderer<SlasherItem> renderer;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer()
            {
                if (this.renderer == null)
                    this.renderer = new SwordRenderer<>(
                            new GenericModel<>(
                                    NeepMeat.NAMESPACE,
                                    "geo/slasher.geo.json",
                                    "textures/item/slasher.png",
                                    "animations/slasher.animation.json"

                            )
                    );

                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider()
    {
        return rendererProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<GeoAnimatable>(this, CONTROLLER_NAME, 20, this::predicate));
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return instanceCache;
    }

    @Override
    public double getBoneResetTime() {
        return super.getBoneResetTime();
    }

    @Override
    public boolean shouldPlayAnimsWhileGamePaused() {
        return super.shouldPlayAnimsWhileGamePaused();
    }

    @Override
    public void appendTags(Consumer<TagKey<Item>> consumer) {
        super.appendTags(consumer);
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
    public boolean onSwing(ItemStack stack, PlayerEntity player) {
        return super.onSwing(stack, player);
    }

    @Override
    public double getTick(Object itemStack) {
        return super.getTick(itemStack);
    }

    @Override
    public boolean isPerspectiveAware() {
        return super.isPerspectiveAware();
    }

    @Override
    public <D> @Nullable D getAnimData(long instanceId, SerializableDataTicket<D> dataTicket) {
        return super.getAnimData(instanceId, dataTicket);
    }

    @Override
    public <D> void setAnimData(Entity relatedEntity, long instanceId, SerializableDataTicket<D> dataTicket, D data) {
        super.setAnimData(relatedEntity, instanceId, dataTicket, data);
    }

    @Override
    public <D> void syncAnimData(long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack) {
        super.syncAnimData(instanceId, dataTicket, data, entityToTrack);
    }

    @Override
    public void triggerAnim(Entity relatedEntity, long instanceId, @Nullable String controllerName, String animName) {
        super.triggerAnim(relatedEntity, instanceId, controllerName, animName);
    }

    @Override
    public void triggerAnim(long instanceId, @Nullable String controllerName, String animName, AzureLibNetwork.IPacketCallback packetCallback) {
        super.triggerAnim(instanceId, controllerName, animName, packetCallback);
    }

    @Override
    public @Nullable AnimatableInstanceCache animatableCacheOverride() {
        return super.animatableCacheOverride();
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
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return super.isEnabled(enabledFeatures);
    }
}
