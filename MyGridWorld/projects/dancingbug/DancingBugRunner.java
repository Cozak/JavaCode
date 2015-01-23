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

package dancingbug;

import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.Location;

import java.awt.Color;

/**
 * This class runs a world that contains box bugs. <br />
 * This class is not tested on the AP CS A and AB exams.
 */
public class DancingBugRunner
{
    public static void main(String[] args)
    { //  Here are 3 array containing data for tests
    	int[] arr1 = {2, 4, 7, 8, 1, 3, 4, 9, 3, 5};
    	int[] arr2 = {9, 5, 3, 4, 7, 8, 2, 5, 8, 7};
    	int[] arr4 = {0,1,2,3,4,5,6,7,8,9};
    	
        ActorWorld world = new ActorWorld();
        DancingBug alice = new DancingBug(arr1);
        alice.setColor(Color.ORANGE);
        //DancingBug bob = new DancingBug(arr2);
        world.add(new Location(4, 4), alice);
        //world.add(new Location(6, 6), bob);
        //DancingBug xox = new DancingBug(arr4);
        //world.add(new Location(5, 5), xox);
        //xox.setColor(Color.BLUE);
        world.show();
    }
}