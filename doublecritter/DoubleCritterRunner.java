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
 * @author Chris Nevison
 * @author Barbara Cloud Wells
 * @author Cay Horstmann
 */
package doublecritter;

import jumper.Jumper;
import info.gridworld.actor.ActorWorld;
import info.gridworld.actor.Bug;
import info.gridworld.actor.Rock;
import info.gridworld.grid.Location;

/**
 * This class runs a world that contains DoubleCritters. <br />
 * This class is not tested on the AP CS A and AB exams.
 * It's suggest that if you want to see in detail, 
 * you shall  not set the value of "number" too large
 */
public class DoubleCritterRunner
{
    public static void main(String[] args)
    { // the number of DoubleCritter 
        int number = 26;
        ActorWorld world = new ActorWorld();
        for (int i = 0; i < 10; ++i)
        {
            world.add(new Location(3, i), new Rock());
        }
        for (int i = 4; i < 10; ++i)
        {
            world.add(new Location(i, 4), new Rock());
        }
        world.add(new Jumper());
        world.add(new Bug());
        
        DoubleCritter[] arr = new DoubleCritter[number];
        for (int i = 0; i < number; ++i)
        {
        	String str = "";
        	for (int j = 0; j < 10; ++j)
        	{
        		char c = (char)('a' + i);
        		str += c;
        	}
        	arr[i] = new DoubleCritter(str, null, 7);
            world.add(arr[i]);
            System.out.println("DNA=" + arr[i].getDNA() + "\nColor=" 
            + arr[i].getColor().getRed() + "-" + arr[i].getColor().getGreen() 
            + "-" + arr[i].getColor().getBlue());
        }
        world.show();
    }
}