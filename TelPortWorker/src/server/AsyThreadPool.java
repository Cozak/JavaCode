package server;

import intface.RoundTaskInF;

import java.util.LinkedList;

public class AsyThreadPool extends ThreadGroup{
	private boolean isClosed;
	private static int threadPoolID = 0;
	private LinkedList<Runnable> taskList = new LinkedList<Runnable>();
	/**
	 * Init
	 * @param poolsize
	 */
	public AsyThreadPool(int poolsize) {
		super("ThreadPoolID-"+(threadPoolID++));
		AsyThreadPool.this.setDaemon(true);
		for (int i = 0; i < poolsize; ++i) {
			new WorkThread(i).start();
		}
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Add new tasks to tasklist
	 * @param nt
	 */
	public synchronized void addTask(Runnable nt) {
		if (AsyThreadPool.this.isClosed) {
			System.out.println("Pool has been closed!!! New Task denied");
			throw new IllegalStateException();
		}
		if (nt != null) {
			AsyThreadPool.this.taskList.add(nt);
			notify();
		}
	}
	
	protected synchronized Runnable getTask() throws InterruptedException {
		while (AsyThreadPool.this.taskList.isEmpty()) {
			if (AsyThreadPool.this.isClosed) {
				return null;
			}
			wait();
		}
		return AsyThreadPool.this.taskList.removeFirst();
	}
	
	public synchronized void close() {
		if (AsyThreadPool.this.isClosed) {
			return;
		}
		AsyThreadPool.this.isClosed = true;
		AsyThreadPool.this.taskList.clear();
		
		// notify each thread to call its circle-task down now
		WorkThread[] alives = new WorkThread[AsyThreadPool.this.activeCount()];
		int aliveNum = AsyThreadPool.this.enumerate(alives);
		
		for (int i = 0; i < aliveNum; ++i) {
			alives[i].callCircleTaskDown();
		}
		
		AsyThreadPool.this.interrupt();
	}
	
	public void join() {
		synchronized(this) {
			if (AsyThreadPool.this.isClosed) {
				return;
			}
			AsyThreadPool.this.isClosed = true;
			AsyThreadPool.this.notifyAll();
		}
		
		Thread[] alives = new Thread[AsyThreadPool.this.activeCount()];
		int aliveNum = AsyThreadPool.this.enumerate(alives);
		
		for (int i = 0; i < aliveNum; ++i) {
			try {
				alives[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
	
	public class WorkThread extends Thread {
		private Runnable ts = null;
		public WorkThread(int i) {
			super(AsyThreadPool.this, "WorkThread-"+i);
		}
		public void callCircleTaskDown() {
			if (ts != null && ts instanceof RoundTaskInF) {
				((RoundTaskInF)ts).setCircleInterupt(true);
			}
		}
		@Override
		public void run() {
			while (true) {
				// Runnable ts = null;
				ts = null;
				try {
					ts = AsyThreadPool.this.getTask();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
				if (ts == null) {
					break;
				}
				try {
					ts.run();
				} catch (Throwable e) {
					System.out.println("Warning!!! Unexcepted Error occuried!!!\nIn WorkThread : "+this.getName());
				}
			}
			
			System.out.println("Thread End"+" : "+""+this.getName());
		}
	}
}
