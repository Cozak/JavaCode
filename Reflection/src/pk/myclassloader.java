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
		String src = ATest.class.getResource("").getFile(); // ��ȡ���ɵ�class�ļ�����java�ļ��ı����ļ����ĸ�Ŀ¼
		System.out.println(src);
		URL url = new URL("file:" + "src"); // ��ȡ·������
		ClassLoader myloader = new URLClassLoader(new URL[]{url}); // ���ɶ�Ӧ��ǰURL·�����ϵ������������������URL�µ����ļ����ࣨ�����У�ATest�౻���أ�
		Class cls = myloader.loadClass("pk.ATest"); // �Ӽ������л�ȡָ�����ࣨATest��������ATest��ġ�Class������
		System.out.println("-----------");
		//ATest t = new ATest(); // ʵ����
		Object t = cls.newInstance(); // ������Ӧ�ġ�Class������ʵ����
	}
};