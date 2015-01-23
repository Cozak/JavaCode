package mp;

public class PackageClassTest {
	public static void changeDouble(Double ddd) {
		ddd = Double.valueOf(100);
	}
	public static void main(String...strings) {
		Double d = new Double(10);
		Double dd = d;
		dd = Double.valueOf(7);
		System.out.println(d);
		PackageClassTest.changeDouble(d);
		System.out.println(d);
		
	}
}
