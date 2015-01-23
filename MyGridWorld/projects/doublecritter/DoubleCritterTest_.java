package doublecritter;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import info.gridworld.actor.Actor;
import info.gridworld.actor.ActorWorld;
import info.gridworld.actor.Rock;
import info.gridworld.grid.Location;

public class DoubleCritterTest_ {
    ActorWorld world = new ActorWorld();
    
    public DoubleCritter__ a = new DoubleCritter__("aaaaabbbbb", Color.BLUE);
    //public CopyOfDoubleCritter c = new CopyOfDoubleCritter("cccccddddd", Color.YELLOW);
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
        a.ifadult = false;
        a.ifbirthing = false;
        a.iffacing = false;
        a.ifloving = false;
        a.ifnear = false;
        a.ifready = false;
        a.lift = 120;
        a.rate = 8;
        a.lover = null;
        a.lover_DNA = "";
    }

    @Test
    public void testGetActors() {
        world.add(new Location(3, 3), new Rock());
        world.add(new Location(4, 3), new Rock());
        world.add(new Location(3, 4), a);
        // it will detect some rocks nearby
        a.ifloving = false;
        ArrayList<Actor> list = a.getActors();
        assertEquals(false, list.isEmpty());
        // when being in love, the DoubleCritter care nothing and no actor will be returned
        a.ifloving = true;
        list = a.getActors();
        assertEquals(true, list.isEmpty());
    }

    @Test
    public void testProcessActors() {
        world.add(new Location(3, 3), new Rock());
        world.add(new Location(4, 3), new Rock());
        world.add(new Location(3,4), a);
        // it will grow up
        a.ifloving = false;
        a.ifbirthing = false;
        a.ifadult = false;
        a.lift = a.defaultAdultPeriod + 1;
        a.processActors(new ArrayList<Actor>());
        assertEquals(true, a.ifadult);
        // it will die
        a.ifloving = false;
        a.ifbirthing = false;
        a.ifadult = true;
        a.lift = 1;
        a.processActors(new ArrayList<Actor>());
        assertEquals(true, a.getGrid() == null);
        // it is ready for its next courtship
        a.ifloving = false;
        a.ifbirthing = false;
        a.ifadult = true;
        a.ifready = false;
        a.rate = 1;
        a.lift = 70;
        a.processActors(new ArrayList<Actor>());
        assertEquals(8, a.rate);
        assertEquals(true, a.ifready);
        // it can get its lover
        DoubleCritter__ c = new DoubleCritter__("cccccddddd", Color.ORANGE);
        c.ifmale = true;
        c.ifadult = true;
        c.ifready = true;
        c.ifloving = false;
        c.lover = null;
        world.add(new Location(3, 5), c);
        a.lift = 100;
        a.ifmale = false;
        a.ifadult = true;
        a.ifready = true;
        a.lover = null;
        a.ifloving = false;
        world.add(new Location(3,4), a);
        a.processActors(a.getActors());
        assertEquals(true, a.ifloving);
        assertEquals(true, c.ifloving);
        assertEquals(true, a.lover == c);
        assertEquals(true, c.lover == a);
        assertEquals(false, a.ifready);
        assertEquals(false, c.ifready);
        assertEquals(true, a.ifnear);
        assertEquals(true, c.ifnear);
        // a faces to c but c doesn't
        a.iffacing = false;
        c.iffacing = false;
        a.iffocus = false;
        c.iffocus = false;
        a.setDirection(Location.EAST);
        c.setDirection(Location.NORTH);
        a.processActors(a.getActors());
        assertEquals(true, a.iffacing);
        assertEquals(false, c.iffacing);
        // a and c face to face, then they focus on each other
        c.setDirection(Location.WEST);
        c.processActors(c.getActors());
        a.processActors(a.getActors());
        assertEquals(true, c.iffacing);
        assertEquals(true, a.iffocus);
        assertEquals(true, c.iffocus);
    }

    @Test
    public void testGetMoveLocations() {
        ArrayList<Location> locs = a.getMoveLocations();
        assertEquals(true, locs.isEmpty());
        world.add(a);
        locs = a.getMoveLocations();
        assertEquals(false, locs.isEmpty());
    }

    @Test
    public void testSelectMoveLocation() {
        DoubleCritter__ c = new DoubleCritter__("cccccddddd", Color.ORANGE);
        c.ifmale = true;
        c.ifadult = true;
        c.ifready = false;
        c.ifloving = true;
        c.ifnear = false;
        c.lover = a;
        c.rate = 8;
        a.ifmale = true;
        a.ifadult = true;
        a.ifready = false;
        a.ifloving = true;
        a.ifnear = false;
        a.lover = c;
        a.rate = 8;
        world.add(new Location(4, 2), c);
        world.add(new Location(4, 8), a);
        Location loc = a.selectMoveLocation(a.getMoveLocations());
        assertEquals(true, !a.ifnear && !c.ifnear);
        assertEquals(true, loc.equals(new Location(4, 7)) 
                || loc.equals(new Location(3, 7)) 
                || loc.equals(new Location(5, 7)));
    }

    @Test
    public void testMakeMove() {
        // It will move to the certain location
        world.add(new Location(7, 7), a);
        a.makeMove(new Location(7, 6));
        assertEquals(true, a.getLocation().equals(new Location(7, 6)));
        //It is giving birth to its children
        a.ifmale = false;
        a.ifadult = true;
        a.ifloving = false;
        a.iffocus = false;
        a.ifnear = false;
        a.iffacing = false;
        a.ifbirthing = true;
        a.defaultBirthRate = 100;
        a.makeMove(a.getLocation());
        assertEquals(true, a.rate == a.defaultPeriodReady 
                || a.rate == a.defaultBirthRate - a.defaultBirthRateDecrease);
        // They are dancing
        DoubleCritter__ c = new DoubleCritter__("cccccddddd", Color.ORANGE);
        world.add(new Location(7, 7), c);
        a.ifmale = false;
        a.ifadult = true;
        a.ifloving = true;
        a.iffocus = true;
        a.ifnear = true;
        a.iffacing = true;
        a.ifbirthing = false;
        a.ifready = false;
        a.lover = c;
        a.rate = 8;
        c.ifmale = true;
        c.ifadult = true;
        c.ifloving = true;
        c.iffocus = true;
        c.ifnear = true;
        c.iffacing = true;
        c.ifbirthing = false;
        c.ifready = false;
        c.lover = a;
        c.rate = 8;
        a.makeMove(null);
        assertEquals(7, a.rate);
        a.rate = 0;
        c.rate = 0;
        a.makeMove(null);
        c.makeMove(null);
        assertEquals(a.defaultBirthRate, a.rate);
        assertEquals(c.defaultPeriodReady, c.rate);
    }

    @Test
    public void testGetLover() {
        DoubleCritter__ c = new DoubleCritter__("cccccddddd", Color.ORANGE);
        world.add(a);
        world.add(c);
        a.lover = c;
        assertEquals(true, a.getLover() == c);
    }

    @Test
    public void testGetRate() {
        a.rate = 100;
        assertEquals(100, a.getRate());
    }

    @Test
    public void testGetDNA() {
        assertEquals("aaaaabbbbb", a.getDNA());
    }

    @Test
    public void testGetOriginalColor() {
        // Its color is Blue(0, 0, 255)
        int[] arr = a.getOriginalColor();
        assertEquals(0, arr[0]);
        assertEquals(0, arr[1]);
        assertEquals(255, arr[2]);
        // create a new one with color(213, 16, 76)
        DoubleCritter__ c = new DoubleCritter__("cccccddddd", new Color(213, 16, 76));
        arr = c.getOriginalColor();
        assertEquals(213, arr[0]);
        assertEquals(16, arr[1]);
        assertEquals(76, arr[2]);
    }

    @Test
    public void testGetSex() {
        a.ifmale = true;
        assertEquals(true, a.ifmale);
        a.ifmale = false;
        assertEquals(false, a.ifmale);
    }

    @Test
    public void testIsReady() {
        a.ifready = true;
        assertEquals(true, a.ifready);
    }

    @Test
    public void testSetReady() {
        a.setReady(false);
        assertEquals(false, a.ifready);
    }

    @Test
    public void testSetLover() {
        DoubleCritter__ c = new DoubleCritter__("cccccddddd", Color.ORANGE);
        a.setLover(c);
        assertEquals(c, a.lover);
    }

    @Test
    public void testSetLoving() {
        a.setLoving(true);
        assertEquals(true, a.ifloving);
    }

    @Test
    public void testGetLoving() {
        a.ifloving = false;
        assertEquals(false, a.getLoving());
        a.ifloving = true;
        assertEquals(true, a.getLoving());
    }

    @Test
    public void testSetNear() {
        a.setNear(false);
        assertEquals(false, a.ifnear);
        a.setNear(true);
        assertEquals(true, a.ifnear);
    }

    @Test
    public void testIsBirth() {
        a.ifbirthing = false;
        assertEquals(false, a.isBirth());
        a.ifbirthing = true;
        assertEquals(true, a.isBirth());
    }

    @Test
    public void testIsFocus() {
        a.iffocus = false;
        assertEquals(false, a.isFocus());
        a.iffocus = true;
        assertEquals(true, a.isFocus());
    }

    @Test
    public void testGiveBirth() {
        // During the entire period, a female DoubleCritter will give birth to at least one kid
        // All kids, which is born during the same period, will have the same DNA
        world.add(new Location(4, 4), a);
        a.lover_DNA = "0000000000";
        a.DNA = "aaaaaaaaaa";
        a.loverColor = new Color(255, 0, 0);
        a.defaultBirthRate = 100;
        a.defaultBirthRateDecrease = 15;
        for (int i = 0; i < 5; ++i) 
        {
            a.giveBirth();
        }
        ArrayList arr = a.getGrid().getNeighbors(a.getLocation());
        assertEquals(false, arr.isEmpty());
        assertEquals(true, 1 <= arr.size() && arr.size() <= 5);
        assertEquals("00000aaaaa", ((DoubleCritter__)(arr.get(0))).DNA);
    }

    @Test
    public void testMixDNA() {
        // para1 0 2 4 6 8
        // para2 1 3 5 7 9
        //result p1[0] p1[2] p1[4] p1[6] p1[8] p2[1] p2[3] p2[5] p2[7] p2[9]
        assertEquals("11111aaaaa", DoubleCritter__.mixDNA("1111111111", "aaaaaaaaaa"));
        assertEquals("12345abcde", DoubleCritter__.mixDNA("1122334455", "aabbccddee"));
        assertEquals("337jj29ggh", DoubleCritter__.mixDNA("333777jjjy", "22299ggggh"));
    }

    @Test
    public void testLetsDancing() {
        // They will turn around each time and gradually change their color 
        //till they look like each other
        DoubleCritter__ c = new DoubleCritter__("cccccddddd", new Color(15, 20, 37));
        DoubleCritter__ d = new DoubleCritter__("xxxxxyyyyy", new Color(225, 220, 237));
        c.setLover(d);
        int r = Math.max(c.getColor().getRed(), d.getColor().getRed()) 
                - Math.min(c.getColor().getRed(), d.getColor().getRed());
        int g = Math.max(c.getColor().getGreen(), d.getColor().getGreen()) 
                - Math.min(c.getColor().getGreen(), d.getColor().getGreen());
        int b = Math.max(c.getColor().getBlue(), d.getColor().getBlue()) 
                - Math.min(c.getColor().getBlue(), d.getColor().getBlue());
        for (int i = 0; i < 8; ++i)
        {
            c.rate--;
            DoubleCritter__.letsDancing(c);
            assertEquals(true, r > Math.max(c.getColor().getRed(), d.getColor().getRed()) 
                    - Math.min(c.getColor().getRed(), d.getColor().getRed()));
            r = Math.max(c.getColor().getRed(), d.getColor().getRed()) 
                    - Math.min(c.getColor().getRed(), d.getColor().getRed());
            assertEquals(true, g > Math.max(c.getColor().getGreen(), d.getColor().getGreen()) 
                    - Math.min(c.getColor().getGreen(), d.getColor().getGreen()));
            g = Math.max(c.getColor().getGreen(), d.getColor().getGreen()) 
                    - Math.min(c.getColor().getGreen(), d.getColor().getGreen());
            assertEquals(true, b > Math.max(c.getColor().getBlue(), d.getColor().getBlue()) 
                    - Math.min(c.getColor().getBlue(), d.getColor().getBlue()));
            b = Math.max(c.getColor().getBlue(), d.getColor().getBlue()) 
                    - Math.min(c.getColor().getBlue(), d.getColor().getBlue());
        }
    }

    @Test
    public void testResetBirthReady() {
        // reset their statements
        // male takes a rest till its ifready become true
        // female prepares for children's birth
        a.ifmale = true;
        a.resetBirthReady();
        assertEquals(true, a.lover == null);
        assertEquals(true, !a.ifnear && !a.ifloving && !a.iffacing && !a.iffocus);
        DoubleCritter__ c = new DoubleCritter__("cccccddddd", Color.ORANGE);
        a.ifmale = false;
        a.lover = c;
        a.resetBirthReady();
        assertEquals(true, a.ifbirthing);
    }

    @Test
    public void testTurnAround() {
        // turn right for 45 degrees
        a.setDirection(0);
        DoubleCritter__.turnAround(a);
        assertEquals(Location.NORTHEAST, a.getDirection());
    }

}
