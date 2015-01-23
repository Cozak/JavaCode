package critters;
import info.gridworld.actor.*;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.ArrayList;

import jumper.*;

public class KingCrab extends CrabCritter
{
	public KingCrab()
	{ // draw this crab with default color
		setColor(Color.YELLOW);
	}
	
	/**
     * Processes the elements of <code>actors</code>. 
     * All actors will try to keep away from the KingCrab, 
     * and any actor that can not move will be removed.
     * @param actors the actors to be processed
     */
    public void processActors(ArrayList<Actor> actors)
    {
        for (Actor a : actors)
        {// An actor that fail to run away will be removed
            if (!(a instanceof Rock) && (!(a instanceof KingCrab) && !avoid(a)))
            { // KingCrab won't eat rocks, another KingCrab and those actor which is able to run away
                a.removeSelfFromGrid();
            }
        }
    }
    
    /**
     * Different kinds of actor try their own way running away 
     * but these are some limitations which may cause them no way to go
     * @param weaker
     * @return whether this weaker can escape from the KingCrab
     */
    public boolean avoid(Actor weaker)
    {    	
    	if (weaker instanceof Critter)
    	{
    		ArrayList<Location> locs; // record the locations that the weaker can move to
    		if (weaker instanceof CrabCritter)
    		{
    			locs = ((CrabCritter)weaker).getMoveLocations();
    		}
    		else
    		{
    			locs = ((Critter)weaker).getMoveLocations();
    		}
    		
    		for (int i = 0; i < locs.size(); ++i)
    		{
    			if (locs.get(i).equals(this.getLocation().getAdjacentLocation(this.getDirection())) 
        			|| locs.get(i).equals(this.getLocation().getAdjacentLocation(this.getDirection() + Location.HALF_LEFT))
        			|| locs.get(i).equals(this.getLocation().getAdjacentLocation(this.getDirection() + Location.HALF_RIGHT)))
        			{ // those adjacent locations in the upper left, the front and the upper right of the KingCrab will be removed
        					locs.remove(i);
        			}
    		}
    		if (!locs.isEmpty())
    		{ // this weaker can run away
    			weaker.moveTo(locs.get(0));
        		return true;
    		}
    	}
    	else if (weaker instanceof Jumper)
    	{ // if a jumper can move or jump away, return true
    		if (((Jumper)weaker).canMove())
    		{
    			((Jumper) weaker).move();
    			return true;
    		}
    		else if (((Jumper) weaker).canJump())
    		{
    			((Jumper) weaker).jump();
    			return true;
    		}
    	}
    	else if (weaker instanceof Bug)
    	{ // if a bug can move away, return true
    		if (((Bug)weaker).canMove())
    		{
    			((Bug) weaker).move();
    			return true;
    		}
    	}
    	
    	return false;
    }
    
};