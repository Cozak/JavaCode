package sparsegrid_sgn;

import info.gridworld.grid.*;

import java.util.ArrayList;

class SparseGridNode
{
	private Object occupant;
	private int col;
	private SparseGridNode next;
	
	public SparseGridNode()
	{
		this.occupant = null;
		this.col = 0;
		this.next = null;
	}
	
	public SparseGridNode(Object t, int col, SparseGridNode next)
	{
		this.occupant = t;
		this.col = col;
		this.next = next;
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
	
	public SparseGridNode getNext()
	{
		return this.next;
	}
	
	public void setNext(SparseGridNode t)
	{
		this.next = t;
	}
}

public class SparseBoundedGrid<E> implements Grid<E>
{
	private ArrayList<SparseGridNode> occupantArray;
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
		occupantArray = new ArrayList<SparseGridNode>(this.row);
		
		for (int i = 0; i < this.getNumRows(); ++i)
		{
			this.occupantArray.add(new SparseGridNode(null, 0, null));
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
    
    public Object insertNode(SparseGridNode head, SparseGridNode tar)
    {
    	SparseGridNode tmp = head;
    	
    	if (tmp.getCol() == tar.getCol())
    	{
    		Object t = tmp.getObject();
    		tmp.setObject(tar.getObject());
    		return t;
    	}
    	
    	while (tmp.getNext() != null && tmp.getNext().getCol() < tar.getCol())
    	{
    		tmp = tmp.getNext();
    	}
    	
    	if (tmp.getNext() == null)
    	{
    		tar.setNext(null);
    		tmp.setNext(tar);
    	}
    	else if (tmp.getNext().getCol() == tar.getCol())
    	{
    		Object t = tmp.getNext().getObject();
    		tmp.getNext().setObject(tar.getObject());
    		return t;
    	}
    	else
    	{
    		tar.setNext(tmp.getNext());
    		tmp.setNext(tar);
    	}
    	
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
        
       	return (E)this.insertNode(this.occupantArray.get(loc.getRow()), new SparseGridNode(obj, loc.getCol(), null));
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
    	return (E)this.insertNode(this.occupantArray.get(loc.getRow()), new SparseGridNode(null, loc.getCol(), null));
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
        
    	SparseGridNode tmp = this.occupantArray.get(loc.getRow());
    	do
    	{
    		if (tmp.getCol() == loc.getCol())
    		{
    			return (E)tmp.getObject();
    		}
    		else if (tmp.getCol() > loc.getCol())
    		{
    			break;
    		}
    		
    		tmp = tmp.getNext();
    	}while (tmp != null);
    	
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
    		SparseGridNode ptr = this.occupantArray.get(i);
    		while (ptr != null)
    		{
    			if (ptr.getObject() != null)
    			{
    				arr.add(new Location(i, ptr.getCol()));
    			}
    			
    			ptr = ptr.getNext();
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
