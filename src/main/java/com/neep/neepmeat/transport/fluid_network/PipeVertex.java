package com.neep.neepmeat.transport.fluid_network;

public interface PipeVertex extends PipeFlowComponent
{
    void tick();

    default void preTick() {}

    PipeVertex[] getAdjVertices();

    default PipeVertex getAdjVertex(int dir)
    {
        return getAdjVertices()[dir];
    }

    default void setAdjVertex(int dir, PipeVertex vertex)
    {
        getAdjVertices()[dir] = vertex;
    }

    void setNetwork(PipeNetwork network);

    PipeNetwork getNetwork();

    boolean canSimplify();

    void reset();

    boolean collapseEdges();

    default boolean keepNetworkValid() {return false;}

    float getTotalHead();
    void setHeight(float value);

    void addHead(int h);

    long getPos();
}
