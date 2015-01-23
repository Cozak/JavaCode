package mp;

import java.lang.reflect.Array;

public class ArrayCopyTest {
	
	/**
	 * This is a common method for array's copy
	 * @param arr
	 * @return a new array of object
	 */
	public static Object goodArrayCopy(Object arr) {
		Class<? extends Object> cl = arr.getClass();
		if (!cl.isArray()) {
			System.out.println("SHIT");
			return null;
		}
		Class<?> part = cl.getComponentType();
		int len = Array.getLength(arr);
		Object brr = Array.newInstance(part, (int) (len+len*0.1));
		System.arraycopy(arr, 0, brr, 0, len);
		return brr;
	}
	
	public static void main(String[] args) {
		char[] old = new char[10];
		for (int i = 0; i < 10; ++i) {
			old[i] = (char)('a'+i);
		}
		// copy
		char[] fresh = new char[20];
		System.arraycopy(old, 0, fresh, 0, 7);
		
		System.out.println(String.copyValueOf(fresh).toString());
		System.out.println("-------\n-------");
		System.out.println(String.copyValueOf((char[])goodArrayCopy(old)).toString());
		
		Object[] objs = new Object[5];
//		((String)objs).toString();
	}
}
