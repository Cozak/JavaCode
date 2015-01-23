package doublecritter;

import info.gridworld.actor.Actor;
import info.gridworld.actor.Critter;
import info.gridworld.actor.Rock;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.ArrayList;

public class DoubleCritter__ extends Critter
{
	public int lift = 120;
	public int defaultAdultPeriod = 100;
	public String DNA = "";
	public String lover_DNA = "";
	// a DoubleCritter needs time to grow up
	public boolean ifadult = false;
	public boolean ifready = false;
	public boolean ifmale;
	public boolean ifloving = false;
	public boolean ifnear = false;
	public boolean iffacing = false;
	public boolean iffocus = false;
	// ifbirthing is true limited to female
	public boolean ifbirthing = false; 
	public DoubleCritter__ lover = null;
	public int rate = 8;
	public int defaultPeriodReady = 10;
	public int defaultBirthRate = 80;
	public int defaultBirthRateDecrease = 15;
	public int[] currentColor = {0, 0, 0};
	public Color loverColor = null;
	
	public DoubleCritter__(String dna, Color col)
	{ // DNA must be setup when creating a new DoubleCritter
		double r = Math.random();
        if (r < 0.5) { // male grows faster and their oestrous cycle is shorter
            this.ifmale = true;
            this.defaultAdultPeriod = 100;
            this.defaultPeriodReady = 15;
        } else { // female needs more time to grow up
            this.ifmale = false;
            this.defaultAdultPeriod = 85;
            this.defaultPeriodReady = 20;
        }
        
        if (col == null)
        { // set color
        	this.setColor(new Color((int) (Math.random() * 256 * 256 * 256)));
        }
        else
        {
            this.setColor(col);
        }
        this.currentColor[0] = this.getColor().getRed();
        this.currentColor[1] = this.getColor().getGreen();
        this.currentColor[2] = this.getColor().getBlue();
        this.DNA = dna;
	}
	
	public DoubleCritter__ getLover()
	{
	    return this.lover;
	}
	
	public int getRate()
	{
	    return this.rate;
	}
	
	public String getDNA()
	{ // return this DoubleCritter's DNA
		return this.DNA;
	}
	
	public int[] getOriginalColor()
	{ // return this DoubleCritter's original color
		return this.currentColor;
	}
	
	public boolean getSex()
	{ // return its gender
		return this.ifmale;
	}
	
	public boolean isReady()
	{ // return true if this DC gets ready to copulation
		return this.ifready;
	}
	
	public void setReady(boolean ifr)
	{ // set ifready
		this.ifready = ifr;
	}
	
	public void setLover(DoubleCritter__ lov)
	{ // set its belove
		this.lover = lov;
	}
	
	public void setLoving(boolean iflov)
	{
		this.ifloving = iflov;
	}
	
	public boolean getLoving()
	{
		return this.ifloving;
	}
	
	public void setNear(boolean ifn)
	{
		this.ifnear = ifn;
	}
	
	public boolean isBirth()
	{
		return this.ifbirthing;
	}
	
	public boolean isFocus()
	{
		return this.iffocus;
	}
	
	/**
	 * A female gives birth to a child each time 
	 * and the child is added to the grid world at empty adjacent location ramdonly
	 */
	public void giveBirth()
	{
		ArrayList<Location> locs = this.getGrid().getEmptyAdjacentLocations(this.getLocation());
		
		if (locs.isEmpty())
		{
			return;
		}
		// adding a child
		double r = Math.random();
		boolean bol = false;
		if (r < 0.5) 
		{
		    bol = true;
		}
		DoubleCritter__ child = new DoubleCritter__(
				DoubleCritter__.mixDNA(this.lover_DNA, this.getDNA()),
				DoubleCritter__.mixColor(this.getColor(), this.loverColor, bol)
				);
		child.putSelfInGrid(this.getGrid(), locs.get((int)(r*locs.size())));
	}
	
	/**
	 * @param a DNA from male
	 * @param b DNA from female
	 * @return the result of mixing this couple's DNA by rules
	 */
	public static String mixDNA(String a, String b)
	{
		String str = "";
		if (!a.isEmpty()) 
		{
			for (int i = 0; i < 5; ++i)
			{
				str += a.charAt(i*2);
			}
		}
		if (!b.isEmpty()) 
		{
			for (int i = 0; i < 5; ++i)
			{
				str += b.charAt(1 + i*2);
			}
		}
		return str;
	}
	
	/**
	 * @param a color from male
	 * @param b color from female
	 * @param bol is true if the child is male
	 * @return result of mixing couple's color by rules
	 */
	public static Color mixColor(Color a, Color b, boolean bol)
	{
		int r;
		int g;
		int bl;
		if (bol)
		{
			r = (4*a.getRed()+b.getRed())/5;
			g = (4*a.getGreen()+b.getGreen())/5;
			bl = (4*a.getBlue()+b.getBlue())/5;
		}
		else
		{
			r = (2*b.getRed()+3*a.getRed())/5;
			g = (2*b.getGreen()+3*a.getGreen())/5;
			bl = (2*b.getBlue()+3*a.getBlue())/5;
		}
		double ran = Math.random();
		if (ran < 0.02)
		{ // child's color may change with small probability
			r = 10;
		}
		else if (ran < 0.04)
		{
			g = 10;
		}
		else if (ran < 0.06)
		{
			bl = 10;
		}
		else if (ran < 0.08)
		{
			r = 245;
		}
		else if (ran < 0.1)
		{
			g = 245;
		}
		else if (ran < 0.12)
		{
			bl = 245;
		}
		
		return new Color(r, g, bl);
		
	}
	
	/**
	 * @param a the DoubleCritter is dancing with its lover
	 * they will change their color gradually and turn around at the same time
	 */
	public static void letsDancing(DoubleCritter__ a)
	{
		DoubleCritter__.turnAround(a);
		
		int[] arrL = a.getLover().getOriginalColor();
		int[] arrO = a.getOriginalColor();
		int r = arrO[0] + ((arrL[0] - arrO[0])/15)*(8-a.getRate());
		int g = arrO[1] + ((arrL[1] - arrO[1])/15)*(8-a.getRate());
		int b = arrO[2] + ((arrL[2] - arrO[2])/15)*(8-a.getRate());
		if (r < 0) 
		{
		    r = 0;
		} 
		else if (r > 255) 
		{
		    r = 255;
		}
		if (g < 0) 
		{
		    g = 0;
		} 
		else if (g > 255) 
		{
		    g = 255;
		}
		if (b < 0) 
		{
		    b = 0;
		} 
		else if (b > 255)
		{
		    b = 255;
		}
		a.setColor(new Color(r, g, b));
	}
	
	/**
	 *detect its neighbor and find its food or potential lover
	 */
	public ArrayList<Actor> getActors()
    {
		if (this.ifloving)
		{
			return new ArrayList<Actor>();
		}
        return super.getActors();    
    }
	
	/**
	 * When the DoubleCritter is not in love, it just find and eat foods;
	 * After finding out its lover, it will change its statement according to
	 * its lover's behaviors
	 */
	public void processActors(ArrayList<Actor> actors)
    {
        if (!this.ifloving && !this.ifbirthing)
        { // lift is running away
        	this.lift--;
    		if (!this.ifadult && this.lift <= this.defaultAdultPeriod)
    		{ // has grown up
    			this.ifadult = true;
    			this.ifready = true;
    		}
    		if (this.lift == 0)
    		{ // time to die
    			this.removeSelfFromGrid();
    			return;
    		}
    		
    		if (this.ifadult && !this.isReady())
    		{ // wait for the next courtship
    			this.rate--;
    			if (this.rate == 0)
    			{
    				this.setReady(true);
    				this.rate = 8;
    			}
    		}
        	
        	// try to find a lover
        	for (Actor a : actors)
            {
        		if ((a instanceof DoubleCritter__)) {
        			if (this.ifready && ((DoubleCritter__)a).getSex() != this.getSex() && ((DoubleCritter__)a).isReady())
        			{ // has found out its lover and then change their own statements
        				this.setReady(false);
            			this.setLover((DoubleCritter__)a);
            			this.setLoving(true);
            			((DoubleCritter__)a).setReady(false);
            			((DoubleCritter__)a).setLover(this);
            			((DoubleCritter__)a).setLoving(true);
            			break;
        			}
        			if (this.ifmale && this.ifadult && !((DoubleCritter__)a).ifadult && ((DoubleCritter__)a).ifmale)
        			{ // not its son
        				if (((DoubleCritter__)a).getDNA().indexOf(DoubleCritter__.mixDNA(this.DNA, "")) == -1) 
        				{ // eating other male DoubleCritter(not adult)
        					a.removeSelfFromGrid();
        				}
        			}
        		} else if (!(a instanceof Rock) && !(a instanceof Critter)) 
        		{ // eating
                    a.removeSelfFromGrid(); 
                }
            }
        }
        
        if (this.ifloving)
        { // fall in love
        	if (!this.ifnear)
        	{
        		if (this.getLocation().getAdjacentLocation(
        				this.getLocation().getDirectionToward(
        						this.lover.getLocation())).equals(
        								this.lover.getLocation()))
        		{
        			this.setNear(true);
        			this.lover.setNear(true);
        		}
        	}
        	else
        	{
        		if (!this.iffacing && this.getLocation().getAdjacentLocation(
        		        this.getDirection()).equals(this.lover.getLocation()))
        		{
        			this.iffacing = true;
        		}
        		
        		if (this.iffacing && this.getDirection() != this.lover.getDirection() 
        		        && (this.getDirection() - this.lover.getDirection()) %  Location.HALF_CIRCLE == 0)
        		{
        			this.iffocus = true;
        		}
        	}
        }
    }
	
	/**
	 * return an arraylist containing locations it can move to
	 */
	public ArrayList<Location> getMoveLocations()
	{
		if (this.getGrid() == null)
		{
			return new ArrayList<Location>();
		}
		
		return super.getMoveLocations();
	}
	
	/**
	 * Choose a suited location 
	 * according to its statement(be in love or just finding food)
	 */
	public Location selectMoveLocation(ArrayList<Location> locs)
    {
        if (this.getGrid() == null)
        {
            return null;
        }
        
        int n = locs.size();
        
        if (this.ifloving)
        {
            if (this.ifnear)
            {
                if (!this.iffacing)
                {
                    n = 0;
                }
            }
            else
            {
                ArrayList<Location> cot = new ArrayList<Location>();
                for (int i = 0; i < locs.size(); ++i)
                {
                    int diff = this.getLocation().getDirectionToward(locs.get(i)) 
                            - this.getLocation().getDirectionToward(this.lover.getLocation());
                    if ((Location.HALF_LEFT <= diff && diff <= Location.HALF_RIGHT) 
                            || (315 <= diff && diff <= 360) || (-360 <= diff && diff <= -315))
                    {
                        cot.add(locs.get(i));
                    }
                }
                n = cot.size();
                locs = cot;
            }
        }
        if (n == 0)
        {
            return getLocation();
        }
        int r = (int) (Math.random() * n);
        return locs.get(r);
    }
	
	/**
	 * When this DoubleCritter is finding food, it just move to location(loc);
	 * When it is facing its lover, they will dance together
	 * When its ifbirthing is true, it will stay and give birth to its children
	 */
	public void makeMove(Location loc)
    {
		if (this.getGrid() == null)
		{
			return;
		}
		
		if (this.isBirth())
		{
			double r = Math.random();
        	if ((int)(r*100) < this.rate)
        	{
        		this.giveBirth();
        		this.rate -= this.defaultBirthRateDecrease;
        	}
        	else
        	{
        		this.ifbirthing = false;
        		this.rate = this.defaultPeriodReady;
        	}
			
			return;
		}
		else if (this.isFocus())
		{
			if (this.lover.isFocus())
			{
				if (this.rate != 0)
				{
					DoubleCritter__.letsDancing(this);
					this.rate--;
				}
				else
				{
					this.resetBirthReady();
				}
			}
			else if (!this.lover.getLoving())
			{
				this.resetBirthReady();
			}
			return;
		}
		else if (this.iffacing)
		{
			return;
		}
		
        if (loc == null) {
            removeSelfFromGrid();
        } else if (this.getLocation().equals(loc)) {
        	DoubleCritter__.turnAround(this);
        } 
        else {
            moveTo(loc);
        }
    }
	
	/**
	 * a male DoubleCritter resets its statements and waits for its next courtship
	 * a female DoubleCritter gets ready to give birth to its children
	 */
	public void resetBirthReady()
	{
		this.rate = this.defaultPeriodReady;
		if (!this.ifmale)
		{
			this.ifbirthing = true;
			this.rate = defaultBirthRate;
			
			this.lover_DNA = this.lover.getDNA();
			this.loverColor = this.lover.getColor();
		}
		this.setLover(null);
		this.setNear(false);
		this.setLoving(false);
		this.setColor(new Color(this.currentColor[0], this.currentColor[1], this.currentColor[2]));
		this.iffacing = false;
		this.iffocus = false;
		
	}
	
	/**
	 * @param a this actor will turn right 45 degrees
	 */
	public static void turnAround(Actor a)
	{
		a.setDirection(a.getDirection() + Location.HALF_RIGHT);
	}
	
	/**
	 * return this actor's statements
	 */
	public String toString()
    {
        return getClass().getName() + "[location=" + this.getLocation() + ",direction="
                + this.getDirection() + ",color=" + this.getColor() + ",DNA=" + this.getDNA() + "]";
    }
}