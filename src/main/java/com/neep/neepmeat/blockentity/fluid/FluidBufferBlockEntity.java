package com.neep.neepmeat.blockentity.fluid;

import com.neep.neepmeat.fluid_transfer.FluidBuffer;
import com.neep.neepmeat.fluid_transfer.storage.WritableFluidBuffer;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FluidBufferBlockEntity extends BlockEntity implements FluidBuffer.FluidBufferProvider, BlockEntityClientSerializable
{
    protected final WritableFluidBuffer buffer;

    public FluidBufferBlockEntity(BlockEntityType type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.buffer = new WritableFluidBuffer(this, (FluidConstants.BUCKET / 8));
    }

    public FluidBufferBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.FLUID_BUFFER, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        buffer.writeNbt(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNbt(tag);
    }

    @Override
    @Nullable
    public WritableFluidBuffer getBuffer(Direction direction)
    {
        return buffer;
    }

    public boolean onUse(PlayerEntity player, Hand hand)
    {
        if (buffer.handleInteract(world, player, hand))
        {
            return true;
        }
        else if (!world.isClient())
        {
            TankBlockEntity.showContents((ServerPlayerEntity) player, world, pos, buffer);
            return true;
        }
        return true;
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        buffer.readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        return buffer.writeNbt(tag);
    }

    @Override
    public void sync()
    {
        World world = this.getWorld();
        if (world != null && !world.isClient)
        {
            BlockEntityClientSerializable.super.sync();
        }
    }
}
