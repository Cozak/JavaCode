package mypackage;

public class TestArray {
	
	public static void play(Integer it) {
		it = 7;
		System.out.println("Inside"+it);
	}
	
	public static void main (String[] args) throws ClassNotFoundException {
		Integer[] ins = new Integer[10];
		System.out.println("Before"+ins[7]);
		play(ins[7]);
		System.out.println("After"+ins[7]);
		System.out.println(Class.forName("TestArray").getResource("/").getPath());
	}
}