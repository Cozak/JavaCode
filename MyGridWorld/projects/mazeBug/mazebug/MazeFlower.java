package mazeBug.mazebug;

import java.awt.Color;

import info.gridworld.actor.*;

public class MazeFlower extends Flower
{
	public static final Color DEFAULT_COLOR = Color.GREEN;
	public static final Color COVER_COLOR = Color.ORANGE;
	
	public MazeFlower()
	{
		setColor(DEFAULT_COLOR);
	}
	
	public MazeFlower(Color initialColor)
	{
		super(initialColor);
	}
	
	public void act()
	{}
}