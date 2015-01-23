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

import info.gridworld.actor.*;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.awt.Color;
/**
 * A <code>BoxBug</code> traces out a square "box" of a given size. <br />
 * The implementation of this class is testable on the AP CS A and AB exams.
 */
public class Jumper extends Actor
{
    /**
     * Constructs a red Jumper.
     */
    public Jumper()
    {
        setColor(Color.RED);
    }

    /**
     * Constructs a Jumper of a given color.
     * @param jumperColor the color for this Jumper
     */
    public Jumper(Color jumperColor)
    {
        setColor(jumperColor);
    }

    /**
     * Jumps if it can jump,
     * Moves if it can move, turns otherwise.
     */
    public void act()
    {
    	if (canJump())
    	{
    		jump();
    	}
    	else if (canMove())
    	{
            move();    		
    	}
        else
        {
            turn();        	
        }
    }

    /**
     * Turns the Jumper 45 degrees to the right without changing its location.
     */
    public void turn()
    {
        setDirection(getDirection() + Location.HALF_RIGHT);
    }

    /**
     * The jumper jumps forward.
     */
    public void jump()
    {
        Grid<Actor> gr = getGrid();
        if (gr == null)
        {
            return;        	
        }
        Location loc = getLocation();
        Location tmpLoc = loc.getAdjacentLocation(getDirection());
        Location next = tmpLoc.getAdjacentLocation(getDirection());
        if (gr.isValid(next))
        {
            moveTo(next);        	
        }
        else
        {
            removeSelfFromGrid();        	
        }

    }
    
    /**
     * Moves the jumper forward.
     */
    public void move()
    {
        Grid<Actor> gr = getGrid();
        if (gr == null)
        {
            return;        	
        }
        Location loc = getLocation();
        Location next = loc.getAdjacentLocation(getDirection());
        if (gr.isValid(next))
        {
            moveTo(next);        	
        }
        else
        {
            removeSelfFromGrid();        	
        }
    }

    /**
     * Tests whether this jumper can move forward into a location that is empty or
     * contains a flower.
     * @return true if this jumper can move.
     */
    public boolean canMove()
    {
        Grid<Actor> gr = getGrid();
        if (gr == null)
        {
            return false;        	
        }
        Location loc = getLocation();
        Location next = loc.getAdjacentLocation(getDirection());
        if (!gr.isValid(next))
        {
            return false;        	
        }
        Actor neighbor = gr.get(next);
        return (neighbor == null) || (neighbor instanceof Flower);
        // ok to move into empty location or onto flower
        // not ok to move onto any other actor
    }
    
    /**
     * Tests whether this jumper can move forward into a location that is empty or
     * contains a flower.
     * @return true if this jumper can move.
     */
    public boolean canJump()
    {
        Grid<Actor> gr = getGrid();
        if (gr == null)
        {
            return false;        	
        }
        Location loc = getLocation();
        Location tmpLoc = loc.getAdjacentLocation(getDirection());
        Location next = tmpLoc.getAdjacentLocation(getDirection());
        if (!gr.isValid(next))
        {
            return false;
        }
        Actor neighbor = gr.get(next);
        return (neighbor == null) || (neighbor instanceof Flower);
        // ok to jump into empty location or onto flower
        // not ok jump move onto any other actor
    }
}
