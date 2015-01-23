package com.example.androneclient;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class KernelTaskCenter {
	private static KernelTaskCenter One = null;
	private static Thread selfThread = null;
	private static Handler selfHandler = null;
	public static KernelTaskCenter getInstance() {
		if (One == null) {
			One = new KernelTaskCenter();
		}
		return One;
	}
	
	public static Handler getHandlerInstance() {
		if (selfHandler == null) {
			if (One != null) {
				Log.i("Error", "Kernel has launched but not the selfhandler");
			}
			KernelTaskCenter.getInstance();
		}
		return selfHandler;
	}
	
	private KernelTaskCenter() {
		selfThread = new Thread() {
			public Looper Lp = null;
			@Override
			public void run() {
				Lp = Looper.myLooper();
				Lp.prepare();
				selfHandler = new Handler(Lp);
				Lp.loop();
				Log.i("KernelTask", "Looper Over");
			}
		};
		selfThread.start();
	}
	
	public static boolean KernelClose() {
		if (One != null) {
			selfHandler.getLooper().quit(); // kill the kernel looper
			One = null; // reinit
			selfHandler = null;
			selfThread = null;
		}
		return true;
	}
}
