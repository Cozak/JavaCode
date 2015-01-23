package pk;

import java.lang.reflect.*;

interface Hello {
	void talk();
};

class HelloWorld implements Hello {
	public void talk() {
		System.out.println("This is my World");
	}
};

class HelloChina implements Hello {
	public void talk() {
		System.out.println("This is my China");
	}
};

class MyHandler implements InvocationHandler {
	private Object proxyed;
	
	public MyHandler(Object tar) {
		this.proxyed = tar;
	}
	
	public Object invoke (Object proxy, Method md, Object[] args)
		throws Throwable {
		Object result;
		System.out.println("Head Shot");
		result = md.invoke(proxyed, args);
		System.out.println("The End");
		
		return result;
	}
};

public class reflect_4 {
	public static void main(String[] args) {
		// HelloWorld's proxy
		HelloWorld hw1 = new HelloWorld();
		MyHandler mh1 = new MyHandler(hw1);
		Hello pro1 = (Hello)Proxy.newProxyInstance(hw1.getClass().getClassLoader(), hw1.getClass().getInterfaces(), mh1);
		pro1.talk();
		
		// HelloChina's proxy
		HelloWorld hw2 = new HelloWorld();
		MyHandler mh2 = new MyHandler(hw2);
		Hello pro2 = (Hello)Proxy.newProxyInstance(hw2.getClass().getClassLoader(), hw2.getClass().getInterfaces(), mh2);
		pro2.talk();
	}
};