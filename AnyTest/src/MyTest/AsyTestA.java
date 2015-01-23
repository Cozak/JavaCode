package MyTest;

public class AsyTestA {
	static class SoManyMethod {
		private Integer mylocak = new Integer(7);
		
		public void method_0(String name) {
			synchronized(this.mylocak) {
				System.out.println("This is method_0 --> "+name);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Method_0 Over--> "+name);
			}
		}
		
		public void method_1(String name) {
			synchronized(this) {
				System.out.println("This is method_1 --> "+name);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Method_1 Over--> "+name);
			}
		}
		
		public synchronized void method_2(String name) {
			System.out.println("This is method_2 --> "+name);
		}
		
		public void method_3(String name) {
			System.out.println("This is method_3 --> "+name);
		}
	}
	
	static class MyTask extends Thread {
		private String name = null;
		private SoManyMethod mth = null;
		private int option = -1;
		public MyTask(String na, SoManyMethod mh, int op) {
			this.name = na;
			this.mth = mh;
			this.option = op;
		}
		
		@Override
		public void run() {
			switch (this.option) {
			case 0 : {
				this.mth.method_0(this.name);
			} break;
			case 1 : {
				this.mth.method_1(this.name);
			} break;
			case 2 : {
				this.mth.method_2(this.name);
			} break;
			case 3 : {
				this.mth.method_3(this.name);
			} break;
			case 4 : {
				this.mth.method_0(this.name);
				this.mth.method_2(this.name);
			} break;
			default : {
				System.out.println("No such method!!!");
			}
			}
		}
	}
	
	public static void main(String[] args) {
		SoManyMethod sm = new SoManyMethod();
		// Test 1
		/*new MyTask("Alice", sm, 0).start();
		new MyTask("Bob", sm, 2).start();*/
		
		// Test 2
		//new MyTask("Alice", sm, 1).start();
		//new MyTask("Bob", sm, 2).start();
		
		// Test 3
		new MyTask("Alice", sm, 4).start();
		new MyTask("Bob", sm, 1).start();
	}
}