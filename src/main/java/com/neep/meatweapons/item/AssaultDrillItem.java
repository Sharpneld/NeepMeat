package com.neep.meatweapons.item;

import com.neep.meatlib.item.IMeatItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.entity.BulletDamageSource;
import com.neep.neepmeat.init.NMParticles;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.manager.SingletonAnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class AssaultDrillItem extends Item implements IMeatItem, IAnimatable, ISyncable
{
    public AnimationFactory factory = new SingletonAnimationFactory(this);
    public Item ammunition;
    public boolean hasLore;
    protected String registryName;

    public final String controllerName = "controller";

    public AssaultDrillItem(String registryName, FabricItemSettings settings)
    {
        super(settings.maxCount(1));
        this.registryName = registryName;

        GeckoLibNetwork.registerSyncable(this);
        ItemRegistry.queueItem(this);
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

    @Override
    public void registerControllers(AnimationData animationData)
    {
        animationData.addAnimationController(new AnimationController(this, controllerName, 1, this::predicate));
    }

    public AnimationFactory getFactory()
    {
        return this.factory;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);

        if (!world.isClient())
        {
            final int id = GeckoLibUtil.guaranteeIDForStack(user.getStackInHand(hand), (ServerWorld) world);
            GeckoLibNetwork.syncAnimation(user, this, id, 0);
        }

        itemStack.getOrCreateNbt().putBoolean("using", true);

        return TypedActionResult.fail(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        double radius = 1.3;
        double distance = 1.2;
        Vec3d tip = user.getEyePos().add(user.getRotationVec(1).normalize().multiply(distance));
        Box box = Box.of(tip, radius, radius, radius);
        if (world instanceof ServerWorld serverWorld)
        {
            world.getOtherEntities(user, box, e -> true).forEach(entity ->
            {
//                entity.getBoundingBox().raycast(user.getEyePos(), user.getEyePos()
//                        .add(user.getRotationVec(1f).normalize().multiply(range))).ifPresent(v ->
                {
                    if (entity instanceof LivingEntity && entity.isAlive() && user instanceof PlayerEntity player)
                    {
                        entity.damage(BulletDamageSource.create(player, 0.04f), 1);
                        entity.timeUntilRegen = 1;

                        serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.NETHER_WART_BLOCK.getDefaultState()), tip.x, tip.y, tip.z, 3, 0.02, 0.02, 0.02, 0.2);
                    }
                }
//                );
            });
        }

        super.usageTick(world, user, stack, remainingUseTicks);
    }

    public static boolean using(ItemStack stack)
    {
        if (stack.getItem() instanceof AssaultDrillItem)
        {
            return stack.getOrCreateNbt().getBoolean("using");
        }
        return false;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
    {
        stack.getOrCreateNbt().putBoolean("using", false);
    }


    @Nullable
    @Override
    public SoundEvent getEquipSound()
    {
        return super.getEquipSound();
    }

    @Override
    public void onAnimationSync(int id, int state)
    {
        if (state == 0)
        {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("animation.assault_drill.spin"));
        }
    }

    protected <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event)
    {
        return PlayState.CONTINUE;
    }
}