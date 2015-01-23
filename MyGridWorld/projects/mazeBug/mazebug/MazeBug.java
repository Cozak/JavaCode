package mazeBug.mazebug;

import info.gridworld.actor.Actor;
import info.gridworld.actor.Bug;
import info.gridworld.actor.Rock;
import info.gridworld.actor.Flower;
import info.gridworld.grid.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;
import java.util.Set;

import javax.swing.JOptionPane;

/**
 * A <code>MazeBug</code> can find its way in a maze. <br />
 * The implementation of this class is testable on the AP CS A and AB exams.
 */
public class MazeBug extends Bug {
	public Location next;
	public Location last;
	public boolean isEnd = false;
	public boolean isTurn = false;
	public boolean isFound = false;
	public boolean isBack = false;
	public Stack<ArrayList<Location>> crossLocation = new Stack<ArrayList<Location>>();
	public Set<Location> visitedLocation = new HashSet<Location>();
	public Integer stepCount = 0;
	boolean hasShown = false;//final message has been shown
	
	private int freq[] = {1, 1, 1, 1};

	/**
	 * Constructs a box bug that traces a square of a given side length
	 * 
	 * @param length
	 *            the side length
	 */
	public MazeBug() {
		setColor(Color.GREEN);
		next = last = new Location(0, 0);
		//last = this.getLocation();
		//next = this.getLocation();
		//System.out.println("Creation\n");
	}

	/**
	 * Moves to the next location of the square.
	 */
	public void act() {
		//boolean willMove = canMove();
		if (isEnd == true)
		{
		//to show step count when reach the goal		
			if (hasShown == false) 
			{
				String msg = stepCount.toString() + " steps";
				JOptionPane.showMessageDialog(null, msg);
				hasShown = true;
			}
		} 
		else 
		{
			if (!isTurn)
			{
				Think();
				this.isTurn = true;
			}
			if (this.canMove()) 
			{
				move();
				//increase step count when move 
				stepCount++;
				
				if (isFound)
				{
					isEnd = true;
				}
				this.isTurn = false;
			}
			else
			{
				turn();
			}
		}
		
	}

	/**
	 * Find all positions that can be move to.
	 * 
	 * @param loc
	 *            the location to detect.
	 * @return List of positions.
	 */
	public ArrayList<Location> getValid(Location loc) {
		Grid<Actor> gr = getGrid();
		if (gr == null)
			return null;
		ArrayList<Location> valid = new ArrayList<Location>();
		
		int dir = Location.NORTH;
		for (int i = 0; i < 4; ++i)
		{
			Location tmp = loc.getAdjacentLocation(dir);
			if (gr.isValid(tmp) && gr.get(tmp) == null)
			{
				valid.add(tmp);
			}
			else
			{
				valid.add(null);
			}
			
			dir += Location.RIGHT;
		}
		
		return valid;
	}

	/**
	 * Tests whether this bug can move forward into a location that is empty or
	 * contains a flower.
	 * 
	 * @return true if this bug can move.
	 */
	public boolean canMove() 
	{
		return this.getLocation().getAdjacentLocation(this.getDirection()).equals(this.next);
	}
	/**
	 * Moves the bug forward, putting a flower into the location it previously
	 * occupied.
	 */
	public void move() {
		Grid<Actor> gr = getGrid();
		if (gr == null)
			return;
		Location loc = getLocation();
		if (gr.isValid(next)) {
			setDirection(getLocation().getDirectionToward(next));
			moveTo(next);
		} else
			removeSelfFromGrid();
		
		if (!this.isBack)
		{
			MazeFlower flower = new MazeFlower(MazeFlower.DEFAULT_COLOR);
			flower.putSelfInGrid(gr, loc);
		}
		else
		{
			MazeFlower flower = new MazeFlower(MazeFlower.COVER_COLOR);
			flower.putSelfInGrid(gr, loc);
		}
		
		
		this.changeFreq(!this.isBack);
	}
	
	public void Think()
	{
		if (!this.visitedLocation.contains(this.getLocation()))
		{
			this.visitedLocation.add(this.getLocation());
		}
		
		if (Detect())
		{
			//System.out.println("Target founded\n");
			return;
		}
		
		if (!isBack)
		{
			ArrayList<Location> arr = new ArrayList<Location>();
			arr.add(this.getLocation());
			arr.add(this.last);
			this.crossLocation.push(arr);
		}
		
		this.next = this.ToSelect(this.getValid(this.getLocation()));
		if (this.next == null && !this.crossLocation.empty())
		{
			ArrayList<Location> lastLoc = this.crossLocation.pop();
			this.next = lastLoc.get(1);
			this.isBack = true;
		}
		else
		{
			this.last = this.getLocation();
			this.isBack = false;
		}
		return;
	}
	
	public Location ToSelect(ArrayList<Location> targets)
	{
		Random t = new Random();
		int total = 0;
		int fqs[] = {0, 0, 0, 0};
		for (int i = 0; i < targets.size(); ++i)
		{
			if (targets.get(i) == null || this.visitedLocation.contains(targets.get(i)))
			{}
			else
			{
				fqs[i] = this.freq[i]*10;
			}
			total += fqs[i];
		}
		
		if (total > 0)
		{
			int res = t.nextInt(total);
			total = 0;
			for (int i = 0; i < targets.size(); ++i)
			{
				total += fqs[i]*10;
				if (res < total)
				{
					return targets.get(i);
				}
			}
		}
		return null;
	}
	
	public boolean Detect()
	{
		int dir = Location.NORTH;
		for (int i = 0; i < 4; ++i)
		{
			Location tmp = this.getLocation().getAdjacentLocation(dir);
			if (this.getGrid().isValid(tmp) && this.getGrid().get(tmp) instanceof Rock)
			{
				Actor tar = this.getGrid().get(tmp);
				if (tar.getColor().equals(Color.RED))
				{
					this.isFound = true;
					this.next = tmp;
					return true;
				}
			}
			dir += Location.RIGHT;
		}
		
		return false;
	}
	
	public void changeFreq(boolean up)
	{
		if(up)
		{
			switch (this.getDirection())
			{
			case Location.NORTH : this.freq[0]++; break;
			case Location.EAST : this.freq[1]++; break;
			case Location.SOUTH : this.freq[2]++; break;
			case Location.WEST : this.freq[3]++; break;
			}
		}
		else
		{
			switch (this.getDirection())
			{
			case Location.SOUTH : this.freq[0]--; break;
			case Location.WEST : this.freq[1]--; break;
			case Location.NORTH : this.freq[2]--; break;
			case Location.EAST : this.freq[3]--; break;
			}
		}
		
		System.out.println(this.freq[0] + "--" + this.freq[1] + "--" + this.freq[2] + "--" + this.freq[3]);
	}
}
