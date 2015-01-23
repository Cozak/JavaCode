package com.minus.lovershouse.bitmap.util;

import java.util.concurrent.ThreadPoolExecutor;

public class MyExecutorService extends ThreadPoolExecutor{

	public MyExecutorService(int corePoolSize) {
		super(corePoolSize, corePoolSize, corePoolSize, null, null);
	}
	
	public void stopWorkThread() {
		
		
	}
	
	public void shutDownWorkThread() {
		
		
		
	}

}
