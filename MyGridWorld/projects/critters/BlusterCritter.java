package critters;
import info.gridworld.actor.Actor;
import info.gridworld.actor.Critter;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.ArrayList;

public class BlusterCritter extends Critter
{
	private static final int DARKENING_FACTOR = 5; // determine how fast this kind of creature get dark
	private int courage;
	private int oriRed;
	private int oriGreen;
	private int oriBlue;
	
	public BlusterCritter()
	{ // default value of courage is 3
		this.courage = 3;
		initColor();
	}
	public BlusterCritter(int t)
	{
		this.courage = t;
		initColor();
	}
	
	/**
	 * Record its original color
	 */
	public void initColor()
	{
		this.oriRed = this.getColor().getRed();
		this.oriGreen = this.getColor().getGreen();
		this.oriBlue = this.getColor().getBlue();
	}
	/**
     * Gets the actors for processing. Implemented to return the actors that
     * occupy neighboring grid locations within two steps.<br />
     * Postcondition: The state of all actors is unchanged.
     * @return a list of actors that this critter wishes to process.
     */
    public ArrayList<Actor> getActors()
    {
    	ArrayList<Actor> arr = getGrid().getNeighbors(getLocation());
    	
    	Grid<Actor> gr = getGrid();
    	int j = getDirection(), k = getDirection() + Location.HALF_LEFT;
    	if ((j + 360) % 90 == 0) // swap j and k if true
    	{ // make sure that k belongs to {0, 90, 180, 270} while j belongs to {45, 135, 215, 305}
    		int tp = j;
    		j = k;
    		k = tp;
    	}
   
        for (int i = 0; i < 4; ++i) //45, 135, 215, 305
        {
        	int dir = j + i * Location.RIGHT;
        	Location loc = getLocation().getAdjacentLocation(dir);
        	if (!gr.isValid(loc))
        	{
        		continue;
        	}
        	
        	dir += Location.HALF_LEFT;
        	for (int r = 0; r < 3; ++r)
        	{
        		Location iLoc = loc.getAdjacentLocation(dir + r * Location.HALF_RIGHT);
        		if (!gr.isValid(iLoc))
        		{
        			continue;
        		}
        		Actor other = gr.get(iLoc);
        		if (other != null)
        		{
        			arr.add(other);
        		}
        	}
        }
        
        for (int i = 0; i < 4; ++i) //0, 90, 180, 270
        {
        	Location loc = getLocation().getAdjacentLocation(k + i * Location.RIGHT).getAdjacentLocation(k + i * Location.RIGHT);
        	if (!gr.isValid(loc))
        	{
        		continue;
        	}
        	Actor other = gr.get(loc);
        	if (other != null)
        	{
        		arr.add(other);
        	}
        }
        
        return arr;
    }
	
	/**
     * Processes the elements of <code>actors</code>. The BlusterCritter will 
     * count the number of critters nearby. If the number of critters is larger 
     * than courage, the BlusterCritter's color will darken, otherwise it will
     *  become brighter.<br />
     * Postcondition: (1) The state of all actors in the grid other than this
     * critter and the elements of <code>actors</code> is unchanged. (2) The
     * location of this critter is unchanged.
     * @param actors the actors to be processed
     */
    public void processActors(ArrayList<Actor> actors)
    {
    	int tmpC = 0;
    	int par = DARKENING_FACTOR;
        for (Actor a : actors)
        {
            if (a instanceof Critter)
            { // count the number of BlusterCritter nearby
            	tmpC++;
            }
        }
        
        if (this.courage > tmpC)
        { // if the number of BlusterCritter nearby is less than its courage
        	par = -par; // it will get brighter
        }
        Color c = getColor();
    	int red = (int) (c.getRed() - par);
    	int green = (int) (c.getGreen() - par);
    	int blue = (int) (c.getBlue() - par);
    	if (red > this.oriRed || red < 10) {red = c.getRed();}
    	if (green > this.oriGreen || green < 10) {green = c.getGreen();}
    	if (blue > this.oriBlue || blue < 10) {blue = c.getBlue();}
    	
    	
    	setColor(new Color(red, green, blue));
    }
};