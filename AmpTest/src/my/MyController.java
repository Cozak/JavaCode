package my;

public class MyController implements MyControllerMBean {
	private String name = "This is a fat cat";
	private int age = -1;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getAge() {
		return this.age;
	}
	
	public void start() {
		System.out.println("Controller Start");
	}
	
	public void stop() {
		System.out.println("Controller Stop");
	}
	
	public String showName() {
		return this.name;
	}
	
	public int showAge() {
		return this.age;
	}
}