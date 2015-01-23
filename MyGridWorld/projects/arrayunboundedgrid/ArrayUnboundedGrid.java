package arrayunboundedgrid;

import info.gridworld.grid.*;

import java.util.ArrayList;

public class ArrayUnboundedGrid<E> implements Grid<E>
{
	private Object[][] occupantArray;
	
	public ArrayUnboundedGrid()
	{
		this.occupantArray = new Object[16][16];
	}
	
	/**
     * Returns the number of rows in this grid.
     * @return the number of rows, or -1 if this grid is unbounded
     */
    public int getNumRows()
    {
    	return -1;
    }

    /**
     * Returns the number of columns in this grid.
     * @return the number of columns, or -1 if this grid is unbounded
     */
    public int getNumCols()
    {
    	return -1;
    }

    /**
     * Checks whether a location is valid in this grid. <br />
     * Precondition: <code>loc</code> is not <code>null</code>
     * @param loc the location to check
     * @return <code>true</code> if <code>loc</code> is valid in this grid,
     * <code>false</code> otherwise
     */
    public boolean isValid(Location loc)
    {
    	if (loc == null)
            throw new NullPointerException("loc == null");
        
    	if (loc.getRow() < 0 || loc.getCol() < 0)
    	{
    		return false;
    	}
    	return true;
    }
    
    public void ToExtend(Location loc)
    {
    	Object[][] old = this.occupantArray;
    	
    	int r = this.occupantArray.length;
    	int c = this.occupantArray[0].length;
    	
    	while (r <= loc.getRow() || c <= loc.getCol())
    	{
    		r*=2;
    		c*=2;
    	}
    	
    	this.occupantArray = new Object[r][c];
    	
    	//copy
    	for (int i = 0; i < old.length; ++i)
    	{
    		for (int j = 0; j < old[0].length; ++j)
    		{
    			this.occupantArray[i][j] = old[i][j];
    		}
    	}
    }
    
    /**
     * Puts an object at a given location in this grid. <br />
     * Precondition: (1) <code>loc</code> is valid in this grid (2)
     * <code>obj</code> is not <code>null</code>
     * @param loc the location at which to put the object
     * @param obj the new object to be added
     * @return the object previously at <code>loc</code> (or <code>null</code>
     * if the location was previously unoccupied)
     */
    public E put(Location loc, E obj)
    {
    	if (!this.isValid(loc))
            throw new IllegalArgumentException("Location " + loc
                    + " is not valid");
    		
    	if (obj == null)
            throw new NullPointerException("obj == null");

        // Add the object to the grid.
        E oldOccupant = get(loc);
        if (loc.getRow() >= this.occupantArray.length || loc.getCol() >= this.occupantArray[0].length)
        {
        	this.ToExtend(loc);
        }
        occupantArray[loc.getRow()][loc.getCol()] = obj;
        return oldOccupant;
    }

    /**
     * Removes the object at a given location from this grid. <br />
     * Precondition: <code>loc</code> is valid in this grid
     * @param loc the location of the object that is to be removed
     * @return the object that was removed (or <code>null<code> if the location
     *  is unoccupied)
     */
    public E remove(Location loc)
    {
    	if (!this.isValid(loc))
            throw new IllegalArgumentException("Location " + loc
                    + " is not valid");
        
    	// Remove the object from the grid.
        if (loc.getRow() >= this.occupantArray.length || loc.getCol() >= this.occupantArray[0].length)
        {
        	return null;
        }
        E r = get(loc);
        occupantArray[loc.getRow()][loc.getCol()] = null;
        return r;
    }

    /**
     * Returns the object at a given location in this grid. <br />
     * Precondition: <code>loc</code> is valid in this grid
     * @param loc a location in this grid
     * @return the object at location <code>loc</code> (or <code>null<code> 
     *  if the location is unoccupied)
     */
    public E get(Location loc)
    {
    	if (!this.isValid(loc))
            throw new IllegalArgumentException("Location " + loc
                    + " is not valid");
        
    	if (loc.getRow() < this.occupantArray.length && loc.getCol() < this.occupantArray[0].length)
    	{
    		return (E)this.occupantArray[loc.getRow()][loc.getCol()];
    	}
    	
    	return null;
    }

    /**
     * Gets the locations in this grid that contain objects.
     * @return an array list of all occupied locations in this grid
     */
    public ArrayList<Location> getOccupiedLocations()
    {
    	ArrayList<Location> locs = new ArrayList<Location>();

        // Look at all grid locations.
        for (int r = 0; r < this.occupantArray.length; r++)
        {
            for (int c = 0; c < this.occupantArray[0].length; c++)
            {
                // If there's an object at this location, put it in the array.
                Location loc = new Location(r, c);
                if (get(loc) != null)
                    locs.add(loc);
            }
        }

        return locs;
    }

    /**
     * Gets the valid locations adjacent to a given location in all eight
     * compass directions (north, northeast, east, southeast, south, southwest,
     * west, and northwest). <br />
     * Precondition: <code>loc</code> is valid in this grid
     * @param loc a location in this grid
     * @return an array list of the valid locations adjacent to <code>loc</code>
     * in this grid
     */
    public ArrayList<Location> getValidAdjacentLocations(Location loc)
    {
    	ArrayList<Location> locs = new ArrayList<Location>();

        int d = Location.NORTH;
        for (int i = 0; i < Location.FULL_CIRCLE / Location.HALF_RIGHT; i++)
        {
            Location neighborLoc = loc.getAdjacentLocation(d);
            if (this.isValid(neighborLoc))
            {
                locs.add(neighborLoc);
            }
            d = d + Location.HALF_RIGHT;
        }
        return locs;
    }

    /**
     * Gets the valid empty locations adjacent to a given location in all eight
     * compass directions (north, northeast, east, southeast, south, southwest,
     * west, and northwest). <br />
     * Precondition: <code>loc</code> is valid in this grid
     * @param loc a location in this grid
     * @return an array list of the valid empty locations adjacent to
     * <code>loc</code> in this grid
     */
    public ArrayList<Location> getEmptyAdjacentLocations(Location loc)
    {
    	ArrayList<Location> locs = new ArrayList<Location>();
        for (Location neighborLoc : getValidAdjacentLocations(loc))
        {
            if (get(neighborLoc) == null)
                locs.add(neighborLoc);
        }
        return locs;
    }

    /**
     * Gets the valid occupied locations adjacent to a given location in all
     * eight compass directions (north, northeast, east, southeast, south,
     * southwest, west, and northwest). <br />
     * Precondition: <code>loc</code> is valid in this grid
     * @param loc a location in this grid
     * @return an array list of the valid occupied locations adjacent to
     * <code>loc</code> in this grid
     */
    public ArrayList<Location> getOccupiedAdjacentLocations(Location loc)
    {
    	ArrayList<Location> locs = new ArrayList<Location>();
        for (Location neighborLoc : getValidAdjacentLocations(loc))
        {
            if (get(neighborLoc) != null)
                locs.add(neighborLoc);
        }
        return locs;
    }

    /**
     * Gets the neighboring occupants in all eight compass directions (north,
     * northeast, east, southeast, south, southwest, west, and northwest).
     * <br />
     * Precondition: <code>loc</code> is valid in this grid
     * @param loc a location in this grid
     * @return returns an array list of the objects in the occupied locations
     * adjacent to <code>loc</code> in this grid
     */
    public ArrayList<E> getNeighbors(Location loc)
    {
    	ArrayList<E> neighbors = new ArrayList<E>();
        for (Location neighborLoc : getOccupiedAdjacentLocations(loc))
        {
            neighbors.add(get(neighborLoc));
        }
        return neighbors;
    }
}
