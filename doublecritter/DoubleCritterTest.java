package doublecritter;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.ArrayList;

import org.junit.Test;

import info.gridworld.actor.Actor;
import info.gridworld.actor.ActorWorld;
import info.gridworld.actor.Rock;
import info.gridworld.grid.Location;

public class DoubleCritterTest {
    //DoubleCritter c = new DoubleCritter("cccccddddd", Color.YELLOW);

    @Test
    /**
     * when the DoubleCritter finds its lover, it will try to move to its lover
     * In this test, there are two adult DoublcCritters with different gender,
     * though they have fallen in love with each other, they must get together
     * so as to do some happy things. After invoking method act several times,
     * they shall meet each other happily 
     */
    public void testSearchTarget() {
        // when the DoubleCritter finds its lover, it will try to move to its lover
        ActorWorld world = new ActorWorld();
        DoubleCritter a = new DoubleCritter("aaaaabbbbb", Color.ORANGE, 1);
        DoubleCritter c = new DoubleCritter("cccccddddd", Color.YELLOW, 0);
        world.add(new Location(7, 7), a);
        world.add(new Location(7, 0), c);
        a.setLoving(true);
        c.setLoving(true);
        a.setLover(c);
        c.setLover(a);
        
        for (int i = 0; i < 4; ++i)
        {
            a.act();
            c.act();
        }
        int dir = a.getLocation().getDirectionToward(c.getLocation());
        assertTrue(a.getLocation().getAdjacentLocation(dir).equals(c.getLocation()));
    }
    
    @Test
    /**
     * Here are two DoubleCritters facing different directions(one is north while the other is south)
     * Since they have gone to together, they will turn till facing each other
     */
    public void testFacing() {
        ActorWorld world = new ActorWorld();
        DoubleCritter a = new DoubleCritter("aaaaabbbbb", Color.ORANGE, 1);
        DoubleCritter c = new DoubleCritter("cccccddddd", Color.YELLOW, 0);
        a.setLoving(true);
        c.setLoving(true);
        a.setNear(true);
        c.setNear(true);
        a.setLover(c);
        c.setLover(a);
        world.add(new Location(7, 3), a);
        world.add(new Location(7, 4), c);
        a.setDirection(Location.NORTH);
        c.setDirection(Location.SOUTH);
        for (int i = 0; i < 2; ++i)
        {
            a.act();
            c.act();
        }
        assertTrue(a.getLocation().getAdjacentLocation(a.getLocation().getDirectionToward(c.getLocation())).equals(c.getLocation()) 
                && c.getLocation().getAdjacentLocation(c.getLocation().getDirectionToward(a.getLocation())).equals(a.getLocation()));
    }
    
    @Test
    /**
     * This couple of lovers have met and faced each other, 
     * then they will dance together by turning around with their color changing
     * In this test, there are three values used to record the difference between
     * two DoublcCritters' color(The difference will gradually lessen)
     */
    public void testDancing() {
        ActorWorld world = new ActorWorld();
        DoubleCritter a = new DoubleCritter("aaaaabbbbb", new Color(10, 20, 36), 1);
        DoubleCritter c = new DoubleCritter("cccccddddd", new Color(173, 230, 215), 0);
        a.setLoving(true);
        c.setLoving(true);
        a.setNear(true);
        c.setNear(true);
        a.setLover(c);
        c.setLover(a);
        world.add(new Location(7, 3), a);
        world.add(new Location(7, 4), c);
        a.setDirection(Location.EAST);
        c.setDirection(Location.WEST);
        
        int r = Math.max(c.getColor().getRed(), a.getColor().getRed()) 
                - Math.min(c.getColor().getRed(), a.getColor().getRed());
        int g = Math.max(c.getColor().getGreen(), a.getColor().getGreen()) 
                - Math.min(c.getColor().getGreen(), a.getColor().getGreen());
        int b = Math.max(c.getColor().getBlue(), a.getColor().getBlue()) 
                - Math.min(c.getColor().getBlue(), a.getColor().getBlue());
        a.act();
        c.act();
        for (int i = 0; i < 7; ++i)
        {
            a.act();
            c.act();
            assertEquals(true, r > Math.max(c.getColor().getRed(), a.getColor().getRed()) 
                    - Math.min(c.getColor().getRed(), a.getColor().getRed()));
            r = Math.max(c.getColor().getRed(), a.getColor().getRed()) 
                    - Math.min(c.getColor().getRed(), a.getColor().getRed());
            assertEquals(true, g > Math.max(c.getColor().getGreen(), a.getColor().getGreen()) 
                    - Math.min(c.getColor().getGreen(), a.getColor().getGreen()));
            g = Math.max(c.getColor().getGreen(), a.getColor().getGreen()) 
                    - Math.min(c.getColor().getGreen(), a.getColor().getGreen());
            assertEquals(true, b > Math.max(c.getColor().getBlue(), a.getColor().getBlue()) 
                    - Math.min(c.getColor().getBlue(), a.getColor().getBlue()));
            b = Math.max(c.getColor().getBlue(), a.getColor().getBlue()) 
                    - Math.min(c.getColor().getBlue(), a.getColor().getBlue());
        }
    }
    
    /**
     * A group of DoubleCritter will gradually enlarge population,
     * so the total number of DoubleCritter is certainly bigger than
     * their initial number
     */
    @Test
    public void testBirthing() {
        ActorWorld world = new ActorWorld();
        DoubleCritter[] arr = new DoubleCritter[10];
        for (int i = 0; i < 10; i += 2)
        {
            arr[i] = new DoubleCritter("0000000000", null, 0);
            world.add(arr[i]);
            arr[i+1] = new DoubleCritter("0000000000", null, 1);
            world.add(arr[i+1]);
        }
        for (int i = 0; i < 100; ++i)
        {
           for (int j = 0; j < arr.length; ++j)
           {
               arr[j].act();
           }
        }
        assertTrue(arr[0].getGrid().getOccupiedLocations().size() > arr.length);
    }
    
    @Test
    /**
     * DoubleCritters have their own rules to mix their DNA
     * (for example, female: abcdefghij, male: 0123456789, child: 02468bdfhj)
     */
    public void testDNA()
    {
        ActorWorld world = new ActorWorld();
        Rock rock = new Rock();
        world.add(new Location(9, 9), rock);
        DoubleCritter a = new DoubleCritter("abcdefghij", new Color(10, 20, 36), 1);
        DoubleCritter c = new DoubleCritter("0123456789", new Color(173, 230, 215), 0);
        world.add(new Location(0, 0), a);
        world.add(new Location(0, 1), c);
        world.add(new Location(2, 0), new Rock());
        world.add(new Location(2, 1), new Rock());
        world.add(new Location(2, 2), new Rock());
        world.add(new Location(1, 2), new Rock());
        world.add(new Location(0, 2), new Rock());
        for (int i = 0; i < 35; ++i)
        {
            a.act();
            c.act();
        }
        a.removeSelfFromGrid();
        c.removeSelfFromGrid();
        ArrayList<Location> locs = rock.getGrid().getOccupiedLocations();
        for (int i = 0; i < locs.size(); ++i)
        {
            Actor tmp = rock.getGrid().get(locs.get(i));
            if (tmp instanceof DoubleCritter)
            {
                assertEquals("02468bdfhj", ((DoubleCritter)tmp).getDNA());
            }
        }
    }
    
    @Test
    /**
     *  A adult male DoubleCritter will eat little DoubleCritters 
     *  which are not its own son(they make a judgment with the DNA of kids)
     */
    public void testEating()
    {
        ActorWorld world = new ActorWorld();
        DoubleCritter bigguy = new DoubleCritter("cccccddddd", new Color(173, 230, 215), 0);
        world.add(bigguy);
        for (int i = 0; i < 30; ++i)
        {
            bigguy.act();
        }
        DoubleCritter[] arr = new DoubleCritter[30];
        for (int i = 0; i < 30; ++i)
        {
            arr[i] = new DoubleCritter("0000000000", null, 0);
            world.add(arr[i]);
        }
        for (int i = 0; i < 30; ++i)
        {
           bigguy.act();
           for (int j = 0; j < arr.length; ++j)
           {
               arr[j].act();
           }
        }
        assertTrue(bigguy.getGrid().getOccupiedLocations().size() < arr.length + 1);
    }
    
    @Test
    /**
     * DubleCritter will turn 45 degrees each time
     */
    public void testTurnAround() {
        // turn right for 45 degrees
        DoubleCritter a = new DoubleCritter("aaaaabbbbb", Color.ORANGE, 7);
        a.setDirection(0);
        DoubleCritter.turnAround(a);
        assertEquals(Location.NORTHEAST, a.getDirection());
    }

}
