package sparsegrid_hm;

import critters.*;
import info.gridworld.actor.*;
import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.Location;

public class SparseBoundedGridRunner
{
    public static void main(String[] args)
    {
        ActorWorld world = new ActorWorld();
        world.addGridClass("sparseGrid_hm.SparseBoundedGrid");
        world.add(new Bug());
        world.add(new Rock());
        world.add(new ChameleonCritter());
        world.show();
    }
}