package com.neep.neepmeat.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LargeFanBlockEntity extends BlockEntity
{
    public int rotationOffset = -1;

    public LargeFanBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
        if (rotationOffset == -1)
        {
            rotationOffset = (int) (world.getTime() % 100);
            markDirty();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("rotation_offset", rotationOffset);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.rotationOffset = nbt.getInt("rotation_offset");
    }
}
