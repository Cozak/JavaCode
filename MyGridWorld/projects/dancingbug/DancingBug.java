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

import info.gridworld.actor.Bug;

/**
 * A <code>BoxBug</code> traces out a square "box" of a given size. <br />
 * The implementation of this class is testable on the AP CS A and AB exams.
 */
public class DancingBug extends Bug
{
    private int[] dArray;
    private int aptr;
    private int scount;
    private boolean isTurn;
    /**
     * Constructs a box bug that traces a square of a given side length
     * @param length the side length
     */
    public DancingBug(int[] aArray)
    { // initializing
    	this.dArray = aArray.clone();
        this.aptr = 0;
        this.scount = 0;
        this.isTurn = false;
    }

    /**
     * Moves to the next location of the square.
     */
    public void act()
    {
    	if (this.isTurn)
    	{ // the bug is turning
    		if (this.scount >= this.dArray[this.aptr])
    		{	// The bug has turned for certain times, it's time to move forwards
    			this.isTurn = false;
    			this.scount = 0;
    			this.aptr++;
    			if (this.aptr >= this.dArray.length)
    			{ // start again with the initial array value
    				this.aptr = 0;
    			}
    		}
    		else
    		{ // take a turn and count 
    			this.scount++;
    			turn();
    		}
    	}
    	else if (canMove())
    	{
    		move();
    		this.isTurn = true;
    	}
    	else
    	{
    		turn();
    	}
    }
}
