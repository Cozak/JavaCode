package mp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

class A {
	@Override
	public String toString() {
		return "What a ass";
	}
}

public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		//A a = new A();
		//System.out.println(a);
		String sb = "";
		sb += 'c';
		
		int arr[] = new int[100];
		System.out.println(arr.length);
		String opf = "output.txt";
		PrintStream out = new PrintStream(new FileOutputStream(opf));
		System.setOut(out);
		System.out.println("978-0-571-08989-5".matches("(978|979)-[0-9]{1,5}-[0-9]{1,7}-[0-9]{1,6}-[0-9]"));
	}
}
