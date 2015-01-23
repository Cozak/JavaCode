package critters;
import info.gridworld.actor.*;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.ArrayList;

import jumper.*;

public class KingCrab_SP extends CrabCritter
{
	public KingCrab_SP()
	{
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
            if ((a instanceof Rock) || (a instanceof Flower) || (!(a instanceof KingCrab_SP) && !avoid(a))) 
                a.removeSelfFromGrid();
        }
    }
    
    public boolean avoid(Actor weaker)
    {
    	//Rock and Flower have no leg or any part like that
    	if ((weaker instanceof Rock) || (weaker instanceof Flower))
    	{
    		return false;
    	}
    	
    	int pb = 0;
		if (getLocation().getDirectionToward(weaker.getLocation()) ==  (getDirection() + 315) % 360)
		{
			pb = -45;
		}
		else if (getLocation().getDirectionToward(weaker.getLocation()) ==  (getDirection() + 45) % 360)
		{
			pb = 45;
		}
    	
    	if (weaker instanceof Bug)
    	{	
    		ArrayList<Location> locs = getLocationsToEscape(weaker.getLocation(), getDirection(), pb, 0, true);
    		if (locs.size() == 0)
    		{
    			return false;
    		}
    		weaker.setDirection(getLocation().getDirectionToward(weaker.getLocation()));
    		weaker.moveTo(locs.get(0));
    	}
    	else if (weaker instanceof Jumper)
    	{
    		ArrayList<Location> locs = getLocationsToEscape(weaker.getLocation(), getDirection(), pb, 1, true);
    		if (locs.size() == 0)
    		{
    			return false;
    		}
    		weaker.setDirection(getLocation().getDirectionToward(weaker.getLocation()));
    		weaker.moveTo(locs.get(0));
    	}
    	else if (weaker instanceof CrabCritter)
    	{
    		ArrayList<Location> locs = getLocationsToEscape(weaker.getLocation(), getDirection(), pb, 2, false);
    		if (locs.size() == 0)
    		{
    			return false;
    		}
    		int diff = weaker.getLocation().getDirectionToward(locs.get(0)) - weaker.getDirection();
    		if ((-180 <= diff && diff <= 0) || ( 180 <= diff && diff <= 360))
    		{
    			diff = weaker.getLocation().getDirectionToward(locs.get(0)) + Location.RIGHT;
    		}
    		else
    		{
    			diff = weaker.getLocation().getDirectionToward(locs.get(0)) + Location.LEFT;
    		}
    		weaker.setDirection(diff);
    		weaker.moveTo(locs.get(0));
    	}
    	else if (weaker instanceof Critter)
    	{
    		ArrayList<Location> locs = getLocationsToEscape(weaker.getLocation(), getDirection(), pb, 0, true);
    		if (locs.size() == 0)
    		{
    			return false;
    		}
    		weaker.setDirection(getLocation().getDirectionToward(weaker.getLocation()));
    		weaker.moveTo(locs.get(0));
    	}
    	
    	return true;
    }
    
    public ArrayList<Location> getLocationsToEscape(Location base, int dir, int pd, int mode, boolean canFlower)
    {
    	ArrayList<Location> arr = new ArrayList<Location>();
    	int cot = pd == 0 ? 3 : 4 ;
    	dir = dir >= 0 ? dir : dir + 360;
    	
    	if (mode == 0 || mode == 1)
    	{
    		if (cot == 3) {dir -= 45; pd = 45;}
    		for (int i = 0; i < cot; ++i)
    		{
    			Location tmp_loc = base.getAdjacentLocation(dir + i * pd);
    			if (!getGrid().isValid(tmp_loc))
    			{
    				continue;
    			}
    			Actor other = getGrid().get(tmp_loc);
    			if (other == null || (canFlower && other instanceof Flower))
    			{
    				arr.add(tmp_loc);
    			}
    		}
    		
    		if (mode == 1)
    		{
    			for (int i = 0; i < cot; ++i)
        		{
        			Location tmp_loc = base.getAdjacentLocation(dir + i * pd).getAdjacentLocation(dir + i * pd);
        			if (!getGrid().isValid(tmp_loc))
        			{
        				continue;
        			}
        			Actor other = getGrid().get(tmp_loc);
        			if (other == null || (canFlower && other instanceof Flower))
        			{
        				arr.add(tmp_loc);
        			}
        		}
    		}
    	}
    	else if (mode == 2 || mode == 3)
    	{
    		if (cot == 3) {cot = 1;}
    		for (int i = 0; i < cot/2; ++i)
    		{
    			Location tmp_loc = base.getAdjacentLocation(dir + i * 2 * pd);
    			if (!getGrid().isValid(tmp_loc))
    			{
    				continue;
    			}
    			Actor other = getGrid().get(tmp_loc);
    			if (other == null || (canFlower && other instanceof Flower))
    			{
    				arr.add(tmp_loc);
    			}
    		}
    		
    		/*if (mode == 3)
    		{
    			for (int i = 0; i < cot/2; ++i)
        		{
        			Location tmp_loc = base.getAdjacentLocation(dir + i * 2 * pd).getAdjacentLocation(dir + i * 2 * pd);
        			Actor other = getGrid().get(tmp_loc);
        			if (other == null || (canFlower && other instanceof Flower))
        			{
        				arr.add(tmp_loc);
        			}
        		}
    		}*/
    	}
    		
    	return arr;
    }
};