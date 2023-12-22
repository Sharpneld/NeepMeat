package com.neep.neepmeat.blockentity.pipe;

import com.neep.neepmeat.block.pipe.IItemPipe;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.item_transfer.TubeUtils;
import com.neep.neepmeat.util.ItemInPipe;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SuppressWarnings("UnstableApiUsage")
public class PneumaticPipeBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    protected List<ItemInPipe> items = new ArrayList<>();

    public PneumaticPipeBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.PNEUMATIC_PIPE, pos, state);
    }

    public PneumaticPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public List<ItemInPipe> getItems()
    {
        return items;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);

        NbtList itemList = new NbtList();
        for (ItemInPipe offset : items)
        {
            NbtCompound nbt1 = new NbtCompound();
            nbt1 = offset.toNbt(nbt1);
            itemList.add(nbt1);
        }

        tag.put("items", itemList);

        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);

        NbtList itemList = (NbtList) tag.get("items");
        int size = itemList != null ? itemList.size() : 0;
        items.clear();
        for (int i = 0; i < size; ++i)
        {
            items.add(ItemInPipe.fromNbt(itemList.getCompound(i)));
        }
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        NbtList itemList = (NbtList) tag.get("items");
//        System.out.println(PipeOffset.fromNbt((NbtCompound) itemList.get(0)).getItemStack());
        int size = itemList != null ? itemList.size() : 0;
        items.clear();

        for (int i = 0; i < size; ++i)
        {
            items.add(ItemInPipe.fromNbt(itemList.getCompound(i)));
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        NbtList itemList = new NbtList();
        for (ItemInPipe offset : items)
        {
            NbtCompound nbt1 = new NbtCompound();
            nbt1 = offset.toNbt(nbt1);
            itemList.add(nbt1);
        }

        tag.put("items", itemList);
        return tag;
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, PneumaticPipeBlockEntity be)
    {
        if (be.items.isEmpty())
            return;

        Iterator<ItemInPipe> it = be.items.listIterator();
        while (it.hasNext())
        {
            ItemInPipe item = it.next();
            item.tick();
            if (item.progress >= 1)
            {
                long transferred = TubeUtils.tryTransfer(item, blockPos, blockState, item.out, world);
                if (transferred == -1)
                {
                    it.remove();
                }
                else
                {
                    TubeUtils.bounce(item, world, blockState);
                }
            }
        }
        be.sync();
    }

    public long insert(ItemInPipe item, World world, BlockState state, BlockPos pos, Direction in)
    {
        Direction out;
        List<Direction> connections = ((IItemPipe) state.getBlock()).getConnections(state, direction -> direction != in);

        Random rand = world.getRandom();
        if (!connections.isEmpty())
        {
            out = connections.get(rand.nextInt(connections.size()));
        }
        else
        {
            out = in;
        }

        item.reset(in, out, world.getTime());
        this.items.add(item);
        return item.getItemStack().getCount();
    }

    public void dropItems()
    {
        for (ItemInPipe item : items)
        {
            Entity itemEntity = new ItemEntity(getWorld(), getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, item.getItemStack());
            getWorld().spawnEntity(itemEntity);
        }
        items.clear();
    }
}
