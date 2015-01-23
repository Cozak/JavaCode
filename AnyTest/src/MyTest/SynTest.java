package MyTest;

class ManyMethod {
	public ManyMethod() {
		
	}
	
	public synchronized void stringShowA(String str) {
		System.out.println("This is method A --> " + str);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Method A Over --> " + str);
	}
	
	public synchronized void stringShowB(String str) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("This is method B --> " + str);
	}
}

class MyTask extends Thread {
	private String name = null;
	private ManyMethod mM = null;
	private int option = -1;
	public MyTask(String name, ManyMethod mm, int opt) {
		this.name = name;
		this.mM = mm;
		this.option = opt;
	}
	
	@Override
	public void run() {
		if (this.mM == null) {
			System.out.println("No Object");
			return;
		}
		
		switch (this.option) {
		case 0 : {
			this.mM.stringShowA(this.name);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.mM.stringShowB(this.name);
		}
		break;
		case 1 : {
			this.mM.stringShowB(this.name);
		}
		break;
		case 2 : {
			
		}
		break;
		default:;
		}
	}
}

public class SynTest {
	
	public static void main(String[] args) {
		ManyMethod tar = new ManyMethod();
		
		new MyTask("Task_1", tar, 0).start();
		new MyTask("Task_2", tar, 1).start();
	} 
	
}
