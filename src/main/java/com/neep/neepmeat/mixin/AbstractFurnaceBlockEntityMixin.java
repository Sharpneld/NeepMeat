package com.neep.neepmeat.mixin;

import com.neep.neepmeat.machine.Heatable;
import com.neep.neepmeat.machine.HeatableFurnace;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements HeatableFurnace
{
    @Shadow protected abstract boolean isBurning();
    @Shadow int burnTime;

    @Shadow protected DefaultedList<ItemStack> inventory;

    @Shadow @Final private RecipeManager.MatchGetter<Inventory, ? extends AbstractCookingRecipe> matchGetter;
    @Shadow
    int cookTime;
    @Shadow
    int cookTimeTotal;
    @Unique
    protected float heatMultiplier;

    @Override
    public void neepMeat$updateState(World world, BlockPos pos, BlockState oldState)
    {
        if (oldState.get(FurnaceBlock.LIT) != isBurning())
        {
            BlockState newState = oldState.with(FurnaceBlock.LIT, isBurning());
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
        }
    }

    @Override
    public void neepMeat$setBurning()
    {
        this.burnTime = 2;
    }

    @Override
    public void neepMeat$setHeat(float heat)
    {
        this.heatMultiplier = heat;
    }

    @Override
    public float neepMeat$getHeat()
    {
        return heatMultiplier;
    }

    @Override
    public int neepMeat$getCookTime()
    {
        return cookTime;
    }

    @Override
    public void neepMeat$setCookTime(int time)
    {
        cookTime = time;
    }

    @Override
    public int neepMeat$getCookTimeTotal()
    {
        return cookTimeTotal;
    }

    @Override
    public boolean neepMeat$isCooking()
    {
//        ItemStack itemStack = inventory.get(AbstractFurnaceBlockEntity.)
        ItemStack fuelStack = inventory.get(1);
        World world = ((BlockEntity) (Object) this).getWorld();
        AbstractFurnaceBlockEntity furnace = ((AbstractFurnaceBlockEntity) (Object) this);
        if (isBurning() || !fuelStack.isEmpty() && !inventory.get(0).isEmpty())
        {
//            Recipe<?> recipe = world.getRecipeManager().getFirstMatch(recipe, furnace, world).orElse(null);
//            Recipe<?> recipe = getLastRecipe();
            Recipe<?> recipe = matchGetter.getFirstMatch(furnace, world).orElse(null);
            int i = furnace.getMaxCountPerStack();
            return isBurning() && canAcceptRecipeOutput(recipe, inventory, i, world.getRegistryManager());
        }
        return false;
    }

    @Unique
    private static boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count, DynamicRegistryManager dynamicRegistryManager)
    {
        if (slots.get(0).isEmpty() || recipe == null)
        {
            return false;
        }
        ItemStack itemStack = recipe.getOutput(dynamicRegistryManager);
        if (itemStack.isEmpty())
        {
            return false;
        }
        ItemStack itemStack2 = slots.get(2);
        if (itemStack2.isEmpty())
        {
            return true;
        }
        if (!ItemStack.areItemsEqual(itemStack2, itemStack))
        {
            return false;
        }
        if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount())
        {
            return true;
        }
        return itemStack2.getCount() < itemStack.getMaxCount();
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private static void tick(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci)
    {
        // Increment cookTime twice per tick if the heat baseEnergy rises above a certain value.
        float multiplier = blockEntity.neepMeat$getHeat();
        if (blockEntity.neepMeat$isCooking())
        {
            int cookTime = blockEntity.neepMeat$getCookTime();
            int total = blockEntity.neepMeat$getCookTimeTotal();
            // Subtract 1 since the Vanilla method will increment the counter once after this
            int tickIncrement = Heatable.getFurnaceTickIncrement(multiplier) - 1;
            // The comparison in AbstractFurnaceBlockEntity uses ==
            blockEntity.neepMeat$setCookTime(Math.min(total - 1, cookTime + tickIncrement));
        }
    }
}