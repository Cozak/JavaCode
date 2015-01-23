package sparsegrid_llo;

import info.gridworld.grid.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

class OccupantInCol
{
	private Object occupant;
	private int col;
	
	public OccupantInCol()
	{
		this.occupant = null;
		this.col = 0;
	}
	
	public OccupantInCol(Object t, int col)
	{
		this.occupant = t;
		this.col = col;
	}
	
	public Object getObject()
	{
		return this.occupant;
	}
	
	public void setObject(Object t)
	{
		this.occupant = t;
	}
	
	public int getCol()
	{
		return this.col;
	}
	
	public void setCol(int t)
	{
		this.col = t;
	}
}

public class SparseBoundedGrid<E> implements Grid<E>
{
	private ArrayList<LinkedList<OccupantInCol>> occupantArray;
	private int col;
	private int row;
	
	public SparseBoundedGrid()
	{
		this.col = 10;
		this.row = 10;
		initArray();
	}
	
	public SparseBoundedGrid(int row, int col)
	{
		if (row <= 0)
            throw new IllegalArgumentException("rows <= 0");
        if (col <= 0)
            throw new IllegalArgumentException("cols <= 0");
		
		this.col = col;
		this.row = row;
		initArray();
	}
	
	private void initArray()
	{
		occupantArray = new ArrayList<LinkedList<OccupantInCol>>(this.row);
		
		for (int i = 0; i < this.getNumRows(); ++i)
		{
			this.occupantArray.add(new LinkedList<OccupantInCol>());
		}
	}
	
	/**
     * Returns the number of rows in this grid.
     * @return the number of rows, or -1 if this grid is unbounded
     */
    public int getNumRows()
    {
    	return this.row;
    }

    /**
     * Returns the number of columns in this grid.
     * @return the number of columns, or -1 if this grid is unbounded
     */
    public int getNumCols()
    {
    	return this.col;
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
    	if (0 <= loc.getCol() && loc.getCol() < this.getNumCols()
    			&& 0 <= loc.getRow() && loc.getRow() < this.getNumRows())
    	{
    		return true;
    	}
    	return false;
    }
    
    /**
     * 
     * @param head
     * @param tar
     * @return the old object
     */
    
    public Object insertNode(LinkedList<OccupantInCol> head, OccupantInCol tar)
    {
    	if (head.isEmpty())
    	{
    		head.add(tar);
    		return null;
    	}
    	else if (head.getFirst().getCol() == tar.getCol())
    	{
    		Object t = head.getFirst().getObject();
    		head.getFirst().setObject(tar.getObject());
    		return t;
    	}
    	else if (head.getFirst().getCol() > tar.getCol())
    	{
    		head.addFirst(tar);
    		return null;
    	}
    	
    	ListIterator<OccupantInCol> ptr = head.listIterator();
    	while (ptr.hasNext())
    	{
    		OccupantInCol tmp = ptr.next();
    		if (tmp.getCol() == tar.getCol())
    		{
    			Object t = tmp.getObject();
        		tmp.setObject(tar.getObject());
        		return t;
    		}
    		else if (tar.getCol() < tmp.getCol())
    		{
    			ptr.previous();
    			ptr.add(tar);
    			return null;
    		}
    	}
    	
    	head.addLast(tar);
    	return null;
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
    	if (!isValid(loc))
            throw new IllegalArgumentException("Location " + loc
                    + " is not valid");
        if (obj == null)
            throw new NullPointerException("obj == null");
        
       	return (E)this.insertNode(this.occupantArray.get(loc.getRow()), new OccupantInCol(obj, loc.getCol()));
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
    	return (E)this.insertNode(this.occupantArray.get(loc.getRow()), new OccupantInCol(null, loc.getCol()));
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
        
    	LinkedList<OccupantInCol> head = this.occupantArray.get(loc.getRow());
    	if (head.isEmpty())
    	{
    		return null;
    	}
    	if (head.getFirst().getCol() == loc.getCol())
    	{
    		return (E)head.getFirst().getObject();
    	}
    	
    	ListIterator<OccupantInCol> ptr = head.listIterator();
    	while (ptr.hasNext())
    	{
    		OccupantInCol tmp = ptr.next();
    		if (tmp.getCol() == loc.getCol())
    		{
    			return (E)tmp.getObject();
    		}
    	}
    	return null;
    }

    /**
     * Gets the locations in this grid that contain objects.
     * @return an array list of all occupied locations in this grid
     */
    public ArrayList<Location> getOccupiedLocations()
    {
    	ArrayList<Location> arr = new ArrayList<Location>();
    	
    	for (int i = 0; i < this.getNumRows(); ++i)
    	{
    		LinkedList<OccupantInCol> head = this.occupantArray.get(i);
    		
    		if (head.isEmpty())
    		{
    			continue;
    		}
    		else if (head.getFirst().getObject() != null)
    		{
    			arr.add(new Location(i, head.getFirst().getCol()));
    		}
    		
    		ListIterator<OccupantInCol> ptr = head.listIterator();
    		while (ptr.hasNext())
    		{
    			OccupantInCol tmp = ptr.next();
    			if (tmp.getObject() != null)
    			{
    				arr.add(new Location(i, tmp.getCol()));
    			}
    		}
    	}
    	
    	return arr;
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
