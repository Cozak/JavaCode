package mp;

class SIA {
	public static void SIprint() {
		System.out.println("This is SIA");
	}
	public void siprint() {
		System.out.println("This is SIA Object");
	}
}

class SIB extends SIA {
	public static void SIprint() {
		System.out.println("This is SIB");
	}
	public void siprint() {
		System.out.println("This is SIB Object");
	}
}

public class StaticInherit {
	public static void main(String...strings) {
		SIA a = new SIA();
		SIB b = new SIB();
		SIA p = a;
		p.siprint();
		p.SIprint();
		p = b;
		p.siprint();
		p.SIprint();
		p = null;
		p.SIprint();
	}
}
