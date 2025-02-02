package com.neep.meatweapons.item;

import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.Util;
import com.neep.meatweapons.client.renderer.BaseGunRenderer;
import com.neep.meatweapons.damage.MWDamageSources;
import com.neep.meatweapons.entity.BulletDamageSource;
import com.neep.neepmeat.api.item.OverrideSwingItem;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class BaseGunItem extends Item implements MeatlibItem, GunItem, GeoItem, OverrideSwingItem
{
    protected final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    protected final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    protected Map<GunSounds, SoundEvent> sounds = new EnumMap<GunSounds, SoundEvent>(GunSounds.class);
    public Item ammunition;
    public boolean hasLore;
    public final int maxShots;
    public final int cooldown;
    protected final Random random = new Random(0);
    protected String registryName;

    protected final String controllerName = "controller";

    public BaseGunItem(String registryName, Item ammunition, int maxShots, int cooldown, boolean hasLore, MeatlibItemSettings settings)
    {
        super(settings.maxCount(1).maxDamage(maxShots).maxDamageIfAbsent(maxShots).group(MeatWeapons.WEAPONS));
//        group(MeatWeapons.WEAPONS);
        this.registryName = registryName;
        this.ammunition = ammunition;
        this.maxShots = maxShots;
        this.hasLore = hasLore;
        this.cooldown = cooldown;
        ItemRegistry.queue(this);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public Random getRandom()
    {
        return random;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        if (hasLore)
        {
            tooltip.add(Text.translatable("item." + MeatWeapons.NAMESPACE + "." + registryName + ".lore"));
        }
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack)
    {
        return false;
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.NONE;
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemStack = user.getStackInHand(hand);
//        user.setCurrentHand(hand);
//        fire(world, user, itemStack);
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference)
    {
        if (cursorStackReference.get().getItem().equals(ammunition) && stack.getDamage() != 0)
        {
            this.reload(player, stack, cursorStackReference.get());
            return true;
        }
        return false;
    }

    @Override
    public int getShots(ItemStack stack, int trigger)
    {
        return trigger == 0 ? this.maxShots - stack.getDamage() : -1;
    }

    // Should only be called on server.
    public void reload(PlayerEntity user, ItemStack stack, @Nullable ItemStack ammoStack)
    {
        user.getItemCooldownManager().set(this, 7);
        ammoStack = ammoStack == null ? GunItem.removeStack(this.ammunition, user) : ammoStack;
        if (ammoStack != null)
        {
            stack.setDamage(0);
            ammoStack.decrement(1);

            if (user.getWorld() instanceof ServerWorld serverWorld)
            {
                // Do not sync reload animation with other players; it looks silly.
                final long id = GeoItem.getOrAssignId(stack, serverWorld);
                triggerAnim(user, id, "Activation", "activate");

                playSound(serverWorld, user, GunSounds.RELOAD);
            }
        }
    }

    protected void fireBeam(World world, PlayerEntity player, ItemStack stack)
    {
        Random random = getRandom();

        double yaw = Math.toRadians(player.getHeadYaw()) + 0.1 * (random.nextFloat() - 0.5);
        double pitch = Math.toRadians(player.getPitch(0.1f)) + 0.1 * (random.nextFloat() - 0.5);

        Vec3d pos = new Vec3d(player.getX(), player.getY() + 1.4, player.getZ());
        Vec3d transform = getMuzzleOffset(player, stack).rotateX((float) -pitch).rotateY((float) -yaw);
        pos = pos.add(transform);

        double d = 0.2;
        Vec3d perturb = new Vec3d(random.nextFloat() - 0.5, random.nextFloat() - 0.5, random.nextFloat() - 0.5)
                .multiply(d);
        Vec3d end = pos.add(player.getRotationVec(1)
                .add(perturb)
                .multiply(40));
        Optional<Entity> target = hitScan(player, pos, end, 40, this);
        if (target.isPresent())
        {
            Entity entity = target.get();
            target.get().damage(BulletDamageSource.create(world, player, 0.1f), 2);
            entity.timeUntilRegen = 0;
        }

        playSound(world, player, GunSounds.FIRE_PRIMARY);

        syncAnimation(world, player, stack, "fire", true);
    }

    protected void fireShell(World world, PlayerEntity player, ItemStack stack, double speed, ProjectileFactory factory)
    {
        double yaw = Math.toRadians(player.getHeadYaw());
        double pitch = Math.toRadians(player.getPitch(0.1f));

        double vx = speed * -Math.sin(yaw) * Math.cos(pitch) + player.getVelocity().getX();
        double vy = speed * -Math.sin(pitch) + player.getVelocity().getY();
        double vz = speed * Math.cos(yaw) * Math.cos(pitch) + player.getVelocity().getZ();

        Vec3d pos = new Vec3d(player.getX(), player.getY() + 1.4, player.getZ());
        if (!player.isSneaking())
        {
            Vec3d transform = getMuzzleOffset(player, stack).rotateY((float) -yaw);
            pos = pos.add(transform);
        }

        PersistentProjectileEntity shell = factory.create(world, pos.x, pos.y, pos.z, vx, vy, vz);
        shell.setOwner(player);
        world.spawnEntity(shell);

        playSound(world, player, GunSounds.FIRE_SECONDARY);

        syncAnimation(world, player, stack, "fire", true);
    }

    public static Optional<Entity> hitScan(@NotNull LivingEntity caster, Vec3d start, Vec3d end, double distance, GunItem gunItem)
    {
        World world = caster.getWorld();
        if (!world.isClient)
        {
            // Find where the ray hits a block
            RaycastContext ctx = new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, caster);
            BlockHitResult blockResult = world.raycast(ctx);

            Predicate<Entity> entityFilter = entity -> !entity.isSpectator() && entity.canHit();

            double minDistance = distance;
            Entity entity = null;
            EntityHitResult entityResult = null;
            for (EntityHitResult result : Util.getRayTargets(caster, start, blockResult.getPos(), entityFilter, 0.1))
            {
                if (result.getPos().distanceTo(start) < minDistance)
                {
                    minDistance = result.getPos().distanceTo(start);
                    entity = result.getEntity();
                    entityResult = result;
                }
            }

            Vec3d hitPos = Objects.requireNonNullElse(entityResult, blockResult).getPos();
            gunItem.syncBeamEffect((ServerWorld) world, start, hitPos, new Vec3d(0, 0, 0), 0.2f, 9, 100);

            return Optional.ofNullable(entity);
        }
        return Optional.empty();
    }

    @Override
    public void syncAnimation(World world, LivingEntity player, ItemStack stack, String animation, boolean broadcast)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            if (player instanceof PlayerEntity playerEntity)
            {
                long id = GeoItem.getOrAssignId(stack, (ServerWorld) world);
                triggerAnim(player, id, controllerName, animation);

                if (broadcast)
                {
                    for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
                    {
                        triggerAnim(otherPlayer, id, controllerName, animation);
                    }
                }
            }
        }
        else
        {
            triggerAnim(player, GeoItem.getId(stack), controllerName, animation);
        }
    }

    @Override
    public void playSound(World world, LivingEntity entity, GunSounds sound)
    {
        if (sounds.containsKey(sound))
        {
            world.playSound(
                    null,
                    entity.getBlockPos(),
                    sounds.get(sound),
                    SoundCategory.PLAYERS,
                    1f,
                    1f
            );
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, controllerName, this::fireController));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return instanceCache;
    }

    @Override
    public Supplier<Object> getRenderProvider()
    {
        return renderProvider;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer)
    {
        consumer.accept(new RenderProvider()
        {
            private BaseGunRenderer<? extends BaseGunItem> renderer;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer()
            {
                if (renderer == null)
                {
                    this.renderer = new BaseGunRenderer<>(createModel());
                }
                return renderer;
            }
        });
    }

    protected abstract GeoModel<? extends BaseGunItem> createModel();

    protected <P extends BaseGunItem> PlayState fireController(AnimationState<P> event)
    {
        return PlayState.CONTINUE;
    }

    public interface ProjectileFactory
    {
        PersistentProjectileEntity create(World world, double x, double y, double z, double vx, double vy, double vz);
    }
}
