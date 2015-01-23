/* 
 * AP(r) Computer Science GridWorld Case Study:
 * Copyright(c) 2005-2006 Cay S. Horstmann (http://horstmann.com)
 *
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * @author Cay Horstmann
 * @author Chris Nevison
 * @author Barbara Cloud Wells
 */
package jumper;
import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.Location;
import info.gridworld.actor.Bug;
import info.gridworld.actor.Rock;

import java.awt.Color;

/**
 * This class runs a world that contains box bugs. <br />
 * This class is not tested on the AP CS A and AB exams.
 */
public class JumperRunner
{
    public static void main(String[] args)
    {
        ActorWorld world = new ActorWorld();
        Jumper xox = new Jumper();
        world.add(new Location(9, 4), xox);
        Jumper alice = new Jumper();
        world.add(new Location(9, 2), alice);
        Jumper bob = new Jumper();
        bob.setDirection(180);
        bob.setColor(Color.BLUE);
        world.add(new Location(3, 2), bob);
        world.add(new Location(7, 4), new Rock());
        world.add(new Location(5, 4), new Rock());
        world.add(new Location(3, 4), new Rock());
        world.add(new Location(2, 4), new Rock());
        world.show();
    }
}