package pk;

import java.net.MalformedURLException; 
import java.net.URL; 
import java.net.URLClassLoader; 

class ATest {
	static {
		System.out.println("Yes, I am here");
	}
};

public class myclassloader {
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
		String src = ATest.class.getResource("").getFile(); // 获取生成的class文件（该java文件的编译文件）的根目录
		System.out.println(src);
		URL url = new URL("file:" + "src"); // 获取路径对象
		ClassLoader myloader = new URLClassLoader(new URL[]{url}); // 生成对应当前URL路径集合的类加载器，加载所有URL下的类文件的类（此例中，ATest类被加载）
		Class cls = myloader.loadClass("pk.ATest"); // 从加载器中获取指定的类（ATest）并生成ATest类的“Class”对象
		System.out.println("-----------");
		//ATest t = new ATest(); // 实例化
		Object t = cls.newInstance(); // 借助相应的“Class”进行实例化
	}
};