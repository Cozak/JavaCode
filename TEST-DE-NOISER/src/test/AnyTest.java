package test;

public class AnyTest {
	public static void main(String[] args) {
		byte by = (byte)0xff;
		if (by == (byte)0xff) {
			System.out.println(by);
		}
		System.out.println(0xff);
		if (0xff != (byte)0xff) {
			System.out.println("Oh..");
		}
	} 
}
