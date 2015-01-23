package mp;
/**
 * Bit Count Sample Here
 * @author ZAK
 *
 */
public class BitMain {
	public static void main(String[] args) {
		int a = -10;
		System.out.println((a>>>1 | 1<<31) + 5);
	}
}
