package com.neep.neepmeat.machine.charnel_compactor;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.machine.integrator.Integrator;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CharnelCompactorBlock extends BaseBlock implements DataCable
{
    public static final IntProperty LEVEL = Properties.LEVEL_8;

    public CharnelCompactorBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(LEVEL, 0));
    }

    public static float getIncreaseChance(Item item)
    {
        return item.getDefaultStack().isIn(NMTags.CHARNEL_COMPACTOR) ? 1 : 0;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        int i = state.get(LEVEL);
        ItemStack itemStack = player.getStackInHand(hand);
        float chance = getIncreaseChance(itemStack.getItem());
        Integrator integrator = Integrator.findIntegrator(world, pos, 10);
        if (i < 8 && chance > 0)
        {
            if (integrator != null && integrator.canEnlighten())
            {
                if (i < 7 && !world.isClient)
                {
                    CharnelCompactorStorage.addLevel(new CharnelCompactorStorage.WorldLocation(world, pos));

                    player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                    if (!player.getAbilities().creativeMode)
                    {
                        itemStack.decrement(1);
                    }
                }
                return ActionResult.success(world.isClient);
            }
            else if (integrator == null)
            {
                player.sendMessage(Text.translatable("message." + NeepMeat.NAMESPACE + ".compactor.not_found"), true);
            }
            else if (!integrator.canEnlighten())
            {
                player.sendMessage(Text.translatable("message." + NeepMeat.NAMESPACE + ".compactor.immature"), true);
            }
        }

        if (i == 8)
        {
            CharnelCompactorStorage.extractOutput(new CharnelCompactorStorage.WorldLocation(world, pos), true);
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        if (state.get(LEVEL) == 7)
        {
            world.scheduleBlockTick(pos, state.getBlock(), 20);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (state.get(LEVEL) == 7)
        {
            world.setBlockState(pos, state.cycle(LEVEL), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(LEVEL);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos)
    {
        return state.get(LEVEL);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance)
    {
        if (entity instanceof ItemEntity item)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ItemStack stack = item.getStack();
                long inserted = CharnelCompactorStorage.getStorage(world, pos, Direction.UP).insert(ItemVariant.of(stack), stack.getCount(), transaction);
                stack.decrement((int) inserted);
                transaction.commit();
            }
        }
    }
}
