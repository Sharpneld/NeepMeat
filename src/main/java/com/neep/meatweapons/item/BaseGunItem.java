package com.neep.meatweapons.item;

import com.neep.meatlib.item.IMeatItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.Util;
import com.neep.meatweapons.entity.BulletDamageSource;
import com.neep.meatweapons.entity.ExplodingShellEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.manager.SingletonAnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Predicate;

public abstract class BaseGunItem extends Item implements IMeatItem, IGunItem, IAnimatable, ISyncable
{
    public AnimationFactory factory = new SingletonAnimationFactory(this);
    Map<GunSounds, SoundEvent> sounds = new EnumMap<GunSounds, SoundEvent>(GunSounds.class);
    public Item ammunition;
    public boolean hasLore;
    public final int maxShots;
    public final int cooldown;
    protected final Random random = new Random(0);
    protected String registryName;

    public BaseGunItem(String registryName, Item ammunition, int maxShots, int cooldown, boolean hasLore, FabricItemSettings settings)
    {
        super(settings.group(MeatWeapons.WEAPONS).maxCount(1).maxDamage(maxShots).maxDamageIfAbsent(maxShots));
        this.registryName = registryName;

        this.ammunition = ammunition;
        this.maxShots = maxShots;
        this.hasLore = hasLore;
        this.cooldown = cooldown;
        GeckoLibNetwork.registerSyncable(this);
        ItemRegistry.queueItem(this);
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
            tooltip.add(new TranslatableText("item." + MeatWeapons.NAMESPACE + "." + registryName + ".lore"));
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

    public AnimationFactory getFactory()
    {
        return this.factory;
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
        ammoStack = ammoStack == null ? IGunItem.removeStack(this.ammunition, user) : ammoStack;
        if (ammoStack != null)
        {
            stack.setDamage(0);
            ammoStack.decrement(1);

            if (!user.world.isClient)
            {
                // Do not sync reload animation with other players; it looks silly.
                final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerWorld) user.world);
                GeckoLibNetwork.syncAnimation(user, this, id, ANIM_RELOAD);

                playSound(user.world, user, GunSounds.RELOAD);
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
            target.get().damage(BulletDamageSource.create(player, 0.1f), 2);
            entity.timeUntilRegen = 0;
        }

        playSound(world, player, GunSounds.FIRE_PRIMARY);
        if (!player.isCreative())
        {
            stack.setDamage(stack.getDamage() + 1);
        }

        syncAnimation(world, player, stack, ANIM_FIRE, true);
    }

    protected void fireShell(World world, PlayerEntity player, ItemStack stack)
    {
        double yaw = Math.toRadians(player.getHeadYaw());
        double pitch = Math.toRadians(player.getPitch(0.1f));

        double mult = 1; // Multiplier for bullet speed.
        double vx = mult * -Math.sin(yaw) * Math.cos(pitch) + player.getVelocity().getX();
        double vy = mult * -Math.sin(pitch) + player.getVelocity().getY();
        double vz = mult * Math.cos(yaw) * Math.cos(pitch) + player.getVelocity().getZ();

        Vec3d pos = new Vec3d(player.getX(), player.getY() + 1.4, player.getZ());
        if (!player.isSneaking())
        {
            Vec3d transform = getMuzzleOffset(player, stack).rotateY((float) -yaw);
            pos = pos.add(transform);
        }

        ExplodingShellEntity shell = new ExplodingShellEntity(world, 1, pos.x, pos.y, pos.z, vx, vy, vz);
        shell.setOwner(player);
        world.spawnEntity(shell);

        playSound(world, player, GunSounds.FIRE_SECONDARY);

        stack.setDamage(stack.getDamage() + 1);

        syncAnimation(world, player, stack, ANIM_FIRE, true);
    }

    public static Optional<Entity> hitScan(@NotNull PlayerEntity caster, Vec3d start, Vec3d end, double distance, IGunItem gunItem)
    {
        World world = caster.world;
        if (!world.isClient)
        {
            // Find where the ray hits a block
            RaycastContext ctx = new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, caster);
            BlockHitResult blockResult = world.raycast(ctx);

            Predicate<Entity> entityFilter = entity -> !entity.isSpectator() && entity.collides();

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

    public void syncAnimation(World world, PlayerEntity player, ItemStack stack, int animation, boolean broadcast)
    {
        final int id = GeckoLibUtil.guaranteeIDForStack(stack, (ServerWorld) world);
        if (broadcast)
        {
            GeckoLibNetwork.syncAnimation(player, this, id, animation);
            for (PlayerEntity otherPlayer : PlayerLookup.tracking(player))
            {
                GeckoLibNetwork.syncAnimation(otherPlayer, this, id, animation);
            }
        }
    }

    public void playSound(World world, PlayerEntity player, GunSounds sound)
    {
        if (sounds.containsKey(sound))
        {
            world.playSound(
                    null,
                    player.getBlockPos(),
                    sounds.get(sound),
                    SoundCategory.PLAYERS,
                    1f,
                    1f
            );
        }
    }

    protected <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event)
    {
        return PlayState.CONTINUE;
    }
}
