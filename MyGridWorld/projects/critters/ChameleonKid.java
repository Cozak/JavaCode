package critters;
import info.gridworld.actor.Actor;
import info.gridworld.actor.Critter;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.ArrayList;

public class ChameleonKid extends ChameleonCritter
{
	private static final double DARKENING_FACTOR = 0.05;
	
    /**
     * Gets the actors for processing. Implemented to return the actors that
     * occupy neighboring grid locations. Override this method in subclasses to
     * look elsewhere for actors to process.<br />
     * Postcondition: The state of all actors is unchanged.
     * @return a list of actors that this critter wishes to process.
     */
    public ArrayList<Actor> getActors()
    {
    	ArrayList<Actor> tmp = new ArrayList<Actor>();
    	Location loc = getLocation();
    	Location hLoc = loc.getAdjacentLocation(getDirection() + Location.AHEAD);
    	if (getGrid().isValid(hLoc))
    	{
    		Actor former = getGrid().get(hLoc);
            if (former != null)
            {
            	tmp.add(former);
           	}
    	}
    	Location lLoc = loc.getAdjacentLocation(getDirection() + Location.HALF_CIRCLE);
    	if (getGrid().isValid(lLoc))
    	{
    		Actor later = getGrid().get(lLoc);
            if (later != null)
            {
            	tmp.add(later);
           	}
    	}
        return tmp;
    }
	
    /**
     * Randomly selects a neighbor in front of or behind the ChameleonKid and changes this chameleonKid's color to be the
     * same as that neighbor's. If there are no neighbors, its color will darken.
     */
    public void processActors(ArrayList<Actor> actors)
    {
        int n = actors.size();
        if (n == 0)
        {
        	Color c = getColor();
        	int red = (int) (c.getRed() * (1 - DARKENING_FACTOR));
        	int green = (int) (c.getGreen() * (1 - DARKENING_FACTOR));
        	int blue = (int) (c.getBlue() * (1 - DARKENING_FACTOR));

        	setColor(new Color(red, green, blue));
            return;
        }
        int r = (int) (Math.random() * n);

        Actor other = actors.get(r);
        setColor(other.getColor());
    }
};