package mp;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class Box {
	private int HL = -1;
	private int WL = -1;
	
	public Box(int a, int b) {
		this.HL = a;
		this.WL = b;
	}
	
	public int ADD() {
		return this.HL + this.WL;
	}
	
	public static int ADD(int a, int b) {
		return a+b;
	}
	
	protected int SUB() {
		return this.HL-this.WL;
	}
	
	private int MUL() {
		return this.HL*this.WL;
	}
}


public class ReflectTest {
	/**
	 * This is a Class Analyzer
	 * @param obj
	 */
	public static void ClassAnalyzer(Object obj) {
		
		Class cl = obj.getClass();
		// constructor
		String cstr = "";
		Constructor[] cons = cl.getDeclaredConstructors();
		for (Constructor cs : cons) {
			cstr+=cs.getName();
			String ms = Modifier.toString(cs.getModifiers());
			if (ms.length() > 0) {
				cstr = ms+" "+cstr;
			}
			cstr+="(";
			Class[] pars = cs.getParameterTypes();
			for (Class ps : pars) {
				cstr+=ps.getName()+", ";
			}
			cstr = cstr.substring(0, cstr.length()-(pars.length == 0 ? 0 : 2))+");\n";
		}
		// method
		String mstr = "";
		Method[] mths = cl.getDeclaredMethods();
		for (Method ms: mths) {
			String ss = Modifier.toString(ms.getModifiers());
			if (ss.length() > 0) {
				mstr += ss+" ";
			}
			mstr+=ms.getReturnType().getName();
			mstr+=" "+ms.getName()+"(";
			Class[] pars = ms.getParameterTypes();
			for (Class ps : pars) {
				mstr+=ps.getName()+", ";
			}
			mstr = mstr.substring(0, mstr.length()-(pars.length == 0 ? 0 : 2))+");\n";
		}
		// field
		String fstr = "";
		Field[] fds = cl.getDeclaredFields();
		for (Field fd : fds) {
			String ms = Modifier.toString(fd.getModifiers());
			if (ms.length() > 0) {
				fstr+=ms+" ";
			}
			fstr+=fd.getType().getName()+" "+fd.getName()+";\n";
		}
		System.out.println(cstr+mstr+fstr);
	}
	
	public static void main(String[] args) {
		Box t = new Box(100, 80);
		ClassAnalyzer(t);
	}
}
