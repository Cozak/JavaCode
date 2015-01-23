package my;

public interface MyControllerMBean {
	public void setName(String name);
	public String getName();
	public void setAge(int age);
	public int getAge();
	public void start();
	public void stop();
	public String showName();
	public int showAge();
}
