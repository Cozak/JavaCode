package MyTest;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.ListIterator;

public class Test
{
	public static void main(String[] args)
	{
		LinkedList<Integer> lis = new LinkedList<Integer>();
		
		for (int i = 0; i < 10; ++i)
		{
			lis.add(new Integer(i));
		}
		
		ListIterator<Integer> ptr = lis.listIterator();
		
		while (ptr.hasNext())
		{
			if ((Integer)ptr.next() == 7)
			{
				ptr.add(new Integer(99));
			}
		}
		
		for (ptr = lis.listIterator(); ptr.hasNext();)
		{
			System.out.println(ptr.next() + "\n");
		}
	}
}
