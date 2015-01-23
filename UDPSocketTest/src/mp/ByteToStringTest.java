package mp;

public class ByteToStringTest {
	public static void main(String[] args) {
		StringBuffer str = new StringBuffer("abcdfg");
		System.out.println("Original --> " + str + "\n");
		str.replace(4, 4, "\0");
		byte[] brr = str.toString().getBytes();
		String stt = new String(brr);
		System.out.println("String --> " + stt + "\n");
		System.out.println("StringBuffer --> : " + str + "\n");
	} 

}
