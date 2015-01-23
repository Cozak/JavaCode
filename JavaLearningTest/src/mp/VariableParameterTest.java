package mp;

public class VariableParameterTest {
	public static double ADD(double... nums) {
		double res = 0;
		for (double n : nums) {
			res+=n;
		}
		return res;
	}
	
	public static void multiPrint(int a, int b, String... strs) {
		String res = "";
		for (String s : strs) {
			res+=s;
		}
		System.out.println(a+"--"+b+"--"+res);
	}
	
	public static void main(String... args) {
		System.out.println(VariableParameterTest.ADD(12,13,41,13));
		VariableParameterTest.multiPrint(7, 77, "abc", "xyz");
	}
}
