package com.neep.neepmeat.fluid_util;

import com.neep.neepmeat.block.FluidAcceptor;
import com.neep.neepmeat.block.FluidNodeProvider;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.security.cert.Extension;
import java.util.*;

public class FluidNetwork2
{
    private World world;

    private HashSet<FluidNode> connectedNodes = new HashSet<>();
//    private List<PipeSegment> networkPipes = new ArrayList<>();
    private Map<BlockPos, PipeSegment> networkPipes = new HashMap<>();
    private List<BlockPos> pipeQueue = new ArrayList<>();

    public FluidNetwork2(World world)
    {
        this.world = world;
    }

    public void rebuild(BlockPos startPos, Direction face)
    {
        discoverNodes(startPos, face);
        buildPressures();
//        tick();
    }

    public void tick()
    {
        for (FluidNode node : connectedNodes)
        {
            for (FluidNode targetNode : connectedNodes)
            {
                if (targetNode.equals(node))
                    continue;
                node.transmitFluid(targetNode);
            }
        }
    }

    public void buildPressures()
    {
//        System.out.println("pipes: " + networkPipes.keySet());
        try
        {
            for (FluidNode node : connectedNodes)
            {
                node.setNetwork(this);

//                PipeSegment pos = networkPipes.get(node.getPos().offset(node.getFace()));
//                pos.

//                BlockPos last = null;
//                int distance = 0;
//                for (Iterator<BlockPos> iterator = networkPipes.keySet().iterator(); iterator.hasNext();)
//                {
//                    BlockPos current = iterator.next();
//                    if (last == null)
//                    {
//                        last = current;
//                        networkPipes.get(last).setDistance(distance);
//                        current = iterator.next();
//                    }
//
//                    if (current.isWithinDistance(last, 1.1))
//
//                    last = current;
//                }

                List<BlockPos> nextSet = new ArrayList<>();
                networkPipes.values().forEach((segment) -> segment.setVisited(false));

                pipeQueue.clear();
                pipeQueue.add(node.getPos().offset(node.getFace()));

                for (int i = 0; i < 10; ++i)
                {
//                for (ListIterator<PipeSegment> iterator = networkPipes.listIterator(); iterator.hasNext();)
                    for (ListIterator<BlockPos> iterator = pipeQueue.listIterator(); iterator.hasNext(); )
                    {
                        BlockPos current = iterator.next();
                        networkPipes.get(current).setDistance(i + 1);
                        networkPipes.get(current).setVisited(true);
                        for (Direction direction : networkPipes.get(current).connections)
                        {
                            if (networkPipes.containsKey(current.offset(direction)) && !networkPipes.get(current.offset(direction)).isVisited())
//                            if (networkPipes.containsKey(current.offset(direction)) && !visited.contains(current.offset(direction)))
                            {
                                nextSet.add(current.offset(direction));
                            }
                        }
                        iterator.remove();
                    }
                    pipeQueue.addAll(nextSet);
                    nextSet.clear();
                }

                // TODO: optimise further
                for (FluidNode node1 : connectedNodes)
                {
                    if (node1.equals(node))
                    {
                        continue;
                    }
                    // thing here
//                    System.out.println(node1);
                int distanceToNode = networkPipes.get(node1.getPos().offset(node1.getFace())).getDistance();
//                    int distanceToNode = 1;
                    node.distances.put(node1, distanceToNode);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void discoverNodes(BlockPos startPos, Direction face)
    {
        networkPipes.clear();
        pipeQueue.clear();
        connectedNodes.clear();

        // List of pipes to be searched in next iteration
        List<BlockPos> nextSet = new ArrayList<>();

        networkPipes.put(startPos.offset(face), new PipeSegment(startPos.offset(face), world.getBlockState(startPos.offset(face))));
//            visitedPipes.put(start, 10f);
        pipeQueue.add(startPos.offset(face));

        // Pipe search depth
        for (int i = 0; i < 10; ++i)
        {
            nextSet.clear();
            for (ListIterator<BlockPos> iterator = pipeQueue.listIterator(); iterator.hasNext();)
            {
                BlockPos current = iterator.next();

                for (Direction direction : Direction.values())
                {
                    BlockPos next = current.offset(direction);
                    BlockState state1 = world.getBlockState(current);
                    BlockState state2 = world.getBlockState(next);

                    if (FluidAcceptor.isConnectedIn(state1, direction) && !networkPipes.containsValue(new PipeSegment(next)))
                    {
                        // Check that target is a pipe and not a fluid block entity
                        if (state2.getBlock() instanceof FluidAcceptor
                                && !(state2.getBlock() instanceof FluidNodeProvider))
                        {
                            // Next block is connected in opposite direction
                            if (FluidAcceptor.isConnectedIn(state2, direction.getOpposite()))
                            {
                                nextSet.add(next);
                                networkPipes.put(next, new PipeSegment(next.toImmutable(), state2));
                            }
                        }
                        else if (state2.getBlock() instanceof FluidNodeProvider)
                        {
                            FluidNodeProvider nodeProvider = (FluidNodeProvider) state2.getBlock();
                            if (nodeProvider.connectInDirection(state2, direction.getOpposite()))
                            {
//                                System.out.println("target: " + next.toString());
                                connectedNodes.add(nodeProvider.getNode(world, next, direction.getOpposite()));
                            }
                        }
                    }
                }
                iterator.remove();
            }
            pipeQueue.addAll(nextSet);
        }
//        System.out.println("targets: " + connectedNodes);
    }

}
