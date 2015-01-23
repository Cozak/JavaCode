package critters;
import info.gridworld.grid.Location;

import java.util.ArrayList;

public class QuickCrab extends CrabCritter
{
	/**
     * @return a list of empty and valid locations on the left 
     * and the right side within two steps
     */
    public ArrayList<Location> getMoveLocations()
    {
        ArrayList<Location> locs = new ArrayList<Location>();
        int dir = this.getDirection() + Location.LEFT;
        Location lrLoc = getLocation().getAdjacentLocation(dir);
        for (int i = 0; i < 2; ++i)
        { // left side comes first
            if (getGrid().isValid(lrLoc) && getGrid().get(lrLoc) == null)
            {
                Location tmpLoc = lrLoc.getAdjacentLocation(dir);
                if (getGrid().isValid(tmpLoc) &&
                        getGrid().get(tmpLoc) == null)
                { // the QuickCrab will choose the location which is further
                    lrLoc = tmpLoc;
                }
                
                locs.add(lrLoc); // add the chosen location to arraylist
            }
            // right side will be checked next
            dir = this.getDirection() + Location.RIGHT;
            lrLoc = getLocation().getAdjacentLocation(dir);
        }
        return locs;
    }
};