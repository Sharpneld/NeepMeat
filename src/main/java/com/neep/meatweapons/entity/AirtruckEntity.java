package com.neep.meatweapons.entity;

import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.MeatWeapons;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import dev.onyxstudios.cca.api.v3.component.sync.ComponentPacketWriter;
import dev.onyxstudios.cca.api.v3.component.sync.PlayerSyncPredicate;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.network.SerializableDataTicket;
import mod.azure.azurelib.util.AzureLibUtil;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class AirtruckEntity extends AbstractVehicleEntity implements GeoEntity
{
    private final AnimatableInstanceCache instanceCache = AzureLibUtil.createInstanceCache(this);

    public final float maxSpeed = 0.05f;
    protected final float forwardsAccel = 0.004f;
    public float forwardsVelocity;

    protected boolean accelerating;
    protected boolean braking;
    protected short soundStage = 0;

    public AirtruckEntity(EntityType<? extends AbstractVehicleEntity> type, World world)
    {
        super(type, world);
    }

    public static AirtruckEntity create(World world)
    {
        return new AirtruckEntity(MeatWeapons.AIRTRUCK, world);
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    protected void updateMotion()
    {
        if (!this.hasPassengers())
        {
            return;
        }

        float upVelocity = 0.0f;
        if (this.pressingLeft)
            this.yawVelocity -= 1.0f;
        if (this.pressingRight)
            this.yawVelocity += 1.0f;

        this.setYaw(this.getYaw() + this.yawVelocity);

        if (this.pressingForward && !this.pressingBack)
        {
            this.forwardsVelocity = Math.min(this.forwardsVelocity + forwardsAccel, maxSpeed);
            this.accelerating = true;
        }
        else if (!this.pressingForward && this.pressingBack)
        {
            this.forwardsVelocity = Math.max(this.forwardsVelocity - forwardsAccel, -maxSpeed);
        }
        else
        {
            this.forwardsVelocity *= this.velocityDecay;
        }

        if (this.pressingUp)
        {
            upVelocity += 0.08;
        }
        if (this.pressingDown)
        {
            upVelocity -= 0.08;
        }
        this.setVelocity(this.getVelocity().add(MathHelper.sin(-this.getYaw() * ((float)Math.PI / 180)) * forwardsVelocity,
                upVelocity,
                MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)) * forwardsVelocity));
    }

    public void onSpawnPacket(EntitySpawnS2CPacket packet)
    {
        super.onSpawnPacket(packet);
//        MinecraftClient.getInstance().getSoundManager().play(new AirtruckSoundInstance(this));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> event)
    {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("animation.airtruck.fly"));
        return PlayState.CONTINUE;
    }

    @Override
    public ItemStack asStack()
    {
        return MWItems.AIRTRUCK_ITEM.getDefaultStack();
    }

    @Override
    public SoundEvent getDamageSound()
    {
        return SoundEvents.ENTITY_IRON_GOLEM_DAMAGE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 0, this::predicate));
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return instanceCache;
    }

    @Override
    public double getBoneResetTime() {
        return GeoEntity.super.getBoneResetTime();
    }

    @Override
    public boolean shouldPlayAnimsWhileGamePaused() {
        return GeoEntity.super.shouldPlayAnimsWhileGamePaused();
    }

    @Override
    public <C extends Component> C getComponent(ComponentKey<C> key) {
        return super.getComponent(key);
    }

    @Override
    public void syncComponent(ComponentKey<?> key) {
        super.syncComponent(key);
    }

    @Override
    public void syncComponent(ComponentKey<?> key, ComponentPacketWriter packetWriter) {
        super.syncComponent(key, packetWriter);
    }

    @Override
    public void syncComponent(ComponentKey<?> key, ComponentPacketWriter packetWriter, PlayerSyncPredicate predicate) {
        super.syncComponent(key, packetWriter, predicate);
    }

    @Override
    public ComponentProvider asComponentProvider() {
        return super.asComponentProvider();
    }

    @Override
    public <D> @Nullable D getAnimData(SerializableDataTicket<D> dataTicket) {
        return GeoEntity.super.getAnimData(dataTicket);
    }

    @Override
    public <D> void setAnimData(SerializableDataTicket<D> dataTicket, D data) {
        GeoEntity.super.setAnimData(dataTicket, data);
    }

    @Override
    public void triggerAnim(@Nullable String controllerName, String animName) {
        GeoEntity.super.triggerAnim(controllerName, animName);
    }

    @Override
    public double getTick(Object entity) {
        return GeoEntity.super.getTick(entity);
    }

    @Override
    public @Nullable AnimatableInstanceCache animatableCacheOverride() {
        return GeoEntity.super.animatableCacheOverride();
    }

    @Override
    public <A> @Nullable A getAttached(AttachmentType<A> type) {
        return super.getAttached(type);
    }

    @Override
    public <A> A getAttachedOrThrow(AttachmentType<A> type) {
        return super.getAttachedOrThrow(type);
    }

    @Override
    public <A> A getAttachedOrSet(AttachmentType<A> type, A defaultValue) {
        return super.getAttachedOrSet(type, defaultValue);
    }

    @Override
    public <A> A getAttachedOrCreate(AttachmentType<A> type, Supplier<A> initializer) {
        return super.getAttachedOrCreate(type, initializer);
    }

    @Override
    public <A> A getAttachedOrCreate(AttachmentType<A> type) {
        return super.getAttachedOrCreate(type);
    }

    @Override
    public <A> A getAttachedOrElse(AttachmentType<A> type, @Nullable A defaultValue) {
        return super.getAttachedOrElse(type, defaultValue);
    }

    @Override
    public <A> A getAttachedOrGet(AttachmentType<A> type, Supplier<A> defaultValue) {
        return super.getAttachedOrGet(type, defaultValue);
    }

    @Override
    public <A> @Nullable A setAttached(AttachmentType<A> type, @Nullable A value) {
        return super.setAttached(type, value);
    }

    @Override
    public boolean hasAttached(AttachmentType<?> type) {
        return super.hasAttached(type);
    }

    @Override
    public <A> @Nullable A removeAttached(AttachmentType<A> type) {
        return super.removeAttached(type);
    }

    @Override
    public <A> @Nullable A modifyAttached(AttachmentType<A> type, UnaryOperator<A> modifier) {
        return super.modifyAttached(type, modifier);
    }

    @Override
    public boolean cannotBeSilenced() {
        return super.cannotBeSilenced();
    }
}
