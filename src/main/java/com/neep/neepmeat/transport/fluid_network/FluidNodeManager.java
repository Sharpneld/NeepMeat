package com.neep.neepmeat.transport.fluid_network;

import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FluidNodeManager
{
    static FluidNodeManager getInstance(World world)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            return FluidNodeManagerImpl.getInstance(serverWorld);
        }


        // Return a dummy instance if used on the client.
        return EMPTY;
    }

    boolean removeNode(NodePos pos);

    List<FluidNode> getNodes(BlockPos pos);

    void entityRemoved(BlockPos pos);

    void entityUnloaded(BlockPos pos);

    @Nullable
    FluidNode get(NodePos nodePos);

    NbtCompound writeNodes(BlockPos pos, NbtCompound nbt);

    // Extracts fluid nodes from nbt and adds them to the world's network
    void readNodes(BlockPos pos, NbtCompound nbt, ServerWorld world);

    boolean updatePosition(World world, NodePos nodePos);

    FluidNodeManager EMPTY = new FluidNodeManager()
    {
        @Override
        public boolean removeNode(NodePos pos)
        {
            return false;
        }

        @Override
        public List<FluidNode> getNodes(BlockPos pos)
        {
            return List.of();
        }

        @Override
        public void entityRemoved(BlockPos pos)
        {

        }

        @Override
        public void entityUnloaded(BlockPos pos)
        {

        }

        @Nullable
        @Override
        public FluidNode get(NodePos nodePos)
        {
            return null;
        }

        @Override
        public NbtCompound writeNodes(BlockPos pos, NbtCompound nbt)
        {
            return nbt;
        }

        @Override
        public void readNodes(BlockPos pos, NbtCompound nbt, ServerWorld world)
        {

        }

        @Override
        public boolean updatePosition(World world, NodePos nodePos)
        {
            return false;
        }
    };
}
