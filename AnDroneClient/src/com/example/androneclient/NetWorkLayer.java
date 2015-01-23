package com.example.androneclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class NetWorkLayer {
	private static NetWorkLayer Noe = null;
	private static Handler netHandler = null;
	private Handler mHandler = null;
	private SocketBT SBT = null;
	private Timer netTimer = null;
	private boolean isWorking = false;
	
	/**
	 * Singleton Mode
	 * @return
	 */
	public static NetWorkLayer getInstance() {
		if (Noe == null) {
			Noe = new NetWorkLayer();
		}
		return Noe;
	}
	/**
	 * Get Task Handler for the Network Tasks
	 * @return
	 * @throws Exception
	 */
	public static Handler getNetHandler() throws Exception {
		if (netHandler == null) {
			if (Noe == null) {
				NetWorkLayer.getInstance();
			} else {
				Log.i("EXEC", "Detect NetWork Handle Error");
				throw new Exception();
			}
		}
		return netHandler;
	}
	
	/**
	 * private constructor for network
	 */
	private NetWorkLayer() {
		this.mHandler = new Handler(Looper.getMainLooper());
		//this.netTimer = new Timer();
		// launch local thread
		new Thread() {
			@Override
			public void run() {
				Looper.myLooper();
				Looper.prepare();
				netHandler = new Handler(Looper.myLooper()) {
					@Override
					public void handleMessage(Message msg) {
						// send task code and execute it
						NetWorkLayer.this.orderSender(msg.what);
						// tell the UI whether the order has sent out
					}
				};
				Looper.myLooper();
				Looper.loop();
			}
		}.start();
	}
	
	public static int LaunchSocket(Context Act, String ip_port) {
		if (NetWorkLayer.Noe.SBT != null && NetWorkLayer.Noe.SBT.ifAlive()) {
			return 0;
		}
		if (Noe.SBT != null) { // be sure the previous socket has been closed
			Noe.SBT.closeSocket();
		}
		// launch a new activity to obtain the socket parameters
		if (NetWorkLayer.Noe == null || NetWorkLayer.netHandler == null) {
			Toast.makeText(Act, "Noe has died", Toast.LENGTH_SHORT).show();
			return 1;
		}
		// decode ip and port
		String[] sip = null; 
		if (!ip_port.matches("\\d{1,3}\\.\\d{1,3}+\\.\\d{1,3}+\\.\\d{1,3}+:\\d{1,5}")) {
			Toast.makeText(Act, "IP Illegal", Toast.LENGTH_SHORT).show();
			return 2; 
		}
		sip = ip_port.split(":");
		NetWorkLayer.Noe.SBT = new NetWorkLayer.SocketBT(sip);
		NetWorkLayer.netHandler.sendMessage(Message.obtain(NetWorkLayer.netHandler, NetWorkLayer.Noe.SBT));
		//if (NetWorkLayer.Noe.ifAlive()) {
		//NetWorkLayer.Noe.mHandler.sendEmptyMessage(1); // notify main thread to get ready
		//NetWorkLayer.Noe.mHandler.post(new KernelActivity.UpdateTask(1));
		Log.i("EXEC","Socket Launching");
		return 0;
		//}
	}
	
	/*public static boolean ifNetOk() {
		if (Noe == null || netHandler == null) {
			// net init failed
			return false;
		}
		// test
		//Noe.mHandler.post(new KernelActivity.UpdateTask(1));
		Noe.ifconnected = true;
		// whether ready to work
		return Noe.ifconnected;
	}*/
	
	public static void NetClose() throws Exception {
		if (Noe != null) {
			// close socket
			Noe.isWorking = false;
			if (Noe.SBT != null) {
				Noe.SBT.closeSocket();
			}
			if (netHandler != null) {
				netHandler.getLooper().quit(); // kill thread
			} else {
				throw new Exception();
			}
			Noe = null;
			netHandler = null;
			System.gc(); // The pass in the pass
		}
	}
	
	private static class SocketBT implements Runnable {
		private String[] sip = null;
		private Socket netSocket = null;
		private BufferedReader buffR = null;
		private OutputStream printW = null;
		private String[] usrInfo = null;
		private int size = 50;
		private int socketSEQ = 100;
		
		public SocketBT(String[] sip) {
			this.sip = sip;
			this.netSocket = new Socket();
		}
		
		/**
		 * get message from server
		 * @return message, if null means closed
		 */
		public String msgReceiver() {
			String str = SocketBT.inputReader(this.buffR, this.size, ';');
			return str;
		}
		
		public void msgSender(String str) {
			try {
				this.printW.write(str.getBytes());
				this.printW.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			SocketAddress saddr;
			try {
				saddr = new InetSocketAddress(InetAddress.getByName(sip[0]), Integer.parseInt(sip[1]));
				Log.i("EXEC", "TCP Building");
				this.netSocket.connect(saddr, 2000);
				Log.i("EXEC", "TCP OK");
				new CircleTask(new Runnable() { // limited task
					@Override
					public void run() {
						// send request of authentication of user
						// init input and output stream
						try {
							Thread.sleep(3000);
							if (!SocketBT.this.ifAlive()) {
								// TCP Failed
								throw new Exception();
							}
							SocketBT.this.buffR = new BufferedReader(new InputStreamReader(SocketBT.this.netSocket.getInputStream()));
							SocketBT.this.printW = SocketBT.this.netSocket.getOutputStream();
							// wait for server's hello
							Log.i("EXEC", "Wait for Server's Hello");
							
							//SocketBT.this.msgReceiver(); // pass the first heart-bit
							//SocketBT.this.msgSender("0 HB 2014 1;");
							
							String hello = SocketBT.this.msgReceiver();
							String[] his = hello.split(" ");
							if (!(his.length == 3 && his[1].equals("USR"))) {
								Log.i("EXEC", "Server's Hello Wrong");
								throw new Exception();
							}							// send the request
							Log.i("EXEC", "Request Sent");
							SocketBT.this.msgSender("1 RES USR 1 MIUI XiaoMi 123456 5;");
							Log.i("EXEC", "Waiting For Reply");
							// wait for the answer
							String usrs = SocketBT.this.msgReceiver();
							Log.i("EXEC", "Reply Received"+usrs);
							SocketBT.this.usrInfo = usrs.split(" "); // type name password
							Log.i("EXEC", "Reply: "+usrs+" OK");
							if (SocketBT.this.usrInfo.length == 4 && SocketBT.this.usrInfo[1].equals("UES") && SocketBT.this.usrInfo[2].equals("SUC")) {
								// success
								Log.i("EXEC", "Success, Calling UI Thread To Update");
								NetWorkLayer.Noe.mHandler.post(new KernelActivity.UpdateTask(1));
								// Launch circle heart-bit
								NetWorkLayer.Noe.isWorking = true;
								Log.i("EXEC", "Ready to launch Heart-Bit");
								NetWorkLayer.Noe.startNetHeartBit();
								Log.i("EXEC", "Ready to launch MsgListener");
								NetWorkLayer.Noe.startMsgListener();
								Log.i("EXEC", "Pre-Launch OK");
								return;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// Error close the socket
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// failed
						Log.i("EXEC", "TCP Failed or Reply illegal");
						SocketBT.this.closeSocket();
						NetWorkLayer.Noe.mHandler.post(new KernelActivity.UpdateTask(0));
					}
				}, 7000, new Runnable() { // task needed after connection failed
					@Override
					public void run() {
						// authentication failed
						// call main thread to update
						Log.i("EXEC", "Time Execeed");
						NetWorkLayer.Noe.mHandler.post(new KernelActivity.UpdateTask(0));
					}
				}).start(); // after 5 seconds means being failed
				//NetWorkLayer.Noe.mHandler.post(new KernelActivity.UpdateTask(1));
				return;
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("EXEC", "TCP Failed");
			NetWorkLayer.Noe.mHandler.post(new KernelActivity.UpdateTask(0));
		}
		
		public boolean ifAlive() {
			if (this.netSocket == null) {
				return false;
			}
			return this.netSocket.isConnected() && !this.netSocket.isClosed();
		}
		
		public void closeSocket() {
			if (this.netSocket != null) {
				try {
					this.netSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.i("EXEC", "Socket Closing");
		}
		
		public static String inputReader(BufferedReader iS, int size, char end) {
			int i = 0;
			char[] arr = new char[size];
			try {
				do {
					iS.read(arr, i, 1);
				} while (arr[i++] != ';');
				return new String(arr, 0, i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private static class CircleTask extends Thread {
		private Timer timer= null;
		private Runnable task = null;
		private Runnable tail = null;
		private long clocks = 0;
		public CircleTask(Runnable task, long clocks, Runnable tail) {
			// TODO Auto-generated constructor stub
			this.timer = new Timer();
			this.clocks = clocks;
			if (clocks < 0) {
				this.clocks = 0;
			}
			this.task = task;
			this.tail = tail;
		}
		@Override
		public void run() {
			final Thread curThread = Thread.currentThread();
			this.timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
//					if (NetWorkLayer.Noe.isWorking) { // 设法将这耦合去掉，弄一个通用终止接口什么的
//						return;
//					}
					Log.i("EXEC", "Interrupting");
					curThread.interrupt();
					// call the main thread to update (Failed)
					if (CircleTask.this.tail != null) {
						Log.i("EXEC", "Ready To Call Tail Run");
						CircleTask.this.tail.run();
					}
				}
			}, this.clocks);
			try {
				if (this.task != null) {
					this.task.run();
				}
				this.timer.cancel(); // Task Done, not interrupt anymore
			} catch (Exception e) {
				Log.i("EXEC", "In Circle Task Killed");
			}
		}
	} 
	
	// send message through socket
	private boolean orderSender(int ord) { // 0~5 0-takeoff 1-land 2-left 3-right 4-up 5-down
		Log.i("EXEC", "Net Looper Get A Message");
		if (!this.SBT.ifAlive()) {
			this.SBT.closeSocket(); // Be sure to close the last socket
			this.mHandler.post(new KernelActivity.UpdateTask(0)); // call main thread to disable the operation
			this.isWorking = false;
			return false;
		}
		Log.i("EXEC", "Ready to execute the Message");
		NetWorkLayer.Noe.SBT.msgSender((String)NetWorkLayer.Noe.EncoderAndDecoder(Integer.valueOf(ord), true));
		return true;
	}
	
	/**
	 * Open heart-bit
	 * Heart-bit will send a special message towards server
	 * if socket has been closed, heart-bit immediately closes
	 */
	private void startNetHeartBit() {
		// new a Timer
		this.netTimer = new Timer();
		// Time Counting
		this.netTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!NetWorkLayer.Noe.isWorking) {
					NetWorkLayer.Noe.netTimer.cancel();
					System.gc(); // clean the rest of Timer
					Log.i("EXEC", "Heart-Bit Closed");
					return; // heart-bit shutdown
				}
				NetWorkLayer.netHandler.sendEmptyMessage(7777); // send heart-bit task
				Log.i("SOCKET", "PingPong");
			}
		}, 1000, 1000); // the heart-bit in each seconds
	}
	
	/**
	 * launch a new thread to listen and accept the message from server
	 */
	private void startMsgListener() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					if (!NetWorkLayer.Noe.isWorking) {
						Log.i("EXEC", "MsgListener Closed");
						return;
					}
					String str = NetWorkLayer.Noe.SBT.msgReceiver();
					if (str == null) { // TCP closed
						NetWorkLayer.Noe.isWorking = false;
						NetWorkLayer.Noe.SBT.closeSocket();
						NetWorkLayer.Noe.mHandler.post(new KernelActivity.UpdateTask(0));
						continue;
					}
					NetWorkLayer.Noe.stateReceiver((Integer)NetWorkLayer.Noe.EncoderAndDecoder(str, false)); 
				}
			}
		}.start();
	}
	
	/**
	 * Get reply and update UI Thread
	 */
	private void stateReceiver(int type) {
		switch(type) {
			case 0: { // lost connection
				this.mHandler.post(new KernelActivity.UpdateTask(0));
				break;
			}
			case 1: { // connected
				this.mHandler.post(new KernelActivity.UpdateTask(1));
				break;
			}
			case 22: { // take off success
				this.mHandler.post(new KernelActivity.UpdateTask(22));
				break;
			}
			case 23: { // landing
				this.mHandler.post(new KernelActivity.UpdateTask(23));
				break;
			}
			case 44: { // moving
				this.mHandler.post(new KernelActivity.UpdateTask(24));
				break;
			}
			case 45: { // hovering
				this.mHandler.post(new KernelActivity.UpdateTask(25));
				break;
			}
			default: /*Unknown Message*/;
		}
	}
	
	// get video
	public void videoReceiver() {
		
	}
	
	/**
	 * true		number->message
	 * false	message->number
	 * @return	object
	 */
	private Object EncoderAndDecoder(Object obj, boolean bol) {
		if (obj == null) {
			return null;
		}
		if (bol) {
			int seq = this.SBT.socketSEQ++;
			switch ((Integer)obj) {
			case 100: {
				Log.i("SOCKET","Sending Take Off");
				return seq+" TAO 0;";
				}
			case 101: {
				Log.i("SOCKET","Sending Land");
				return seq+" LAD 0;";
				}
			case 200: { // try to get control of the flyer
				Log.i("EXEC", "Switch To Controller Mode");
				return seq+" QAR 0";
			}
			case 201: {
				Log.i("EXEC", "Switch To Witcher Mode");
				return null;
			}
			case 50: {
				Log.i("SOCKET","Sending Left");
				return seq+" DIR 0.1 0.4 2;";
				}
			case 51: {
				Log.i("SOCKET","Sending Right");
				return seq+" DIR -0.1 0.4 2;";
				}
			case 52: {
				Log.i("SOCKET","Sending Front");
				return seq+" FLY 0.1 0.5 2;";
				}
			case 53: {
				Log.i("SOCKET","Sending Back");
				return seq+" FLY -0.1 0.5 2;";
				}
			case 54: {
				Log.i("SOCKET","Sending Up");
				return seq+" HEI 0.1 0.5 2;";
				}
			case 55: {
				Log.i("SOCKET","Sending Down");
				return seq+" HEI -0.1 0.5 2;";
				}
			case 77: {
				Log.i("SOCKET", "Sending Hover");
				return seq+" HOV 0;";
				}
			case 7777: {
				Log.i("SOCKET", "Heart-Bit");
				return seq+" HB 2014 1;";
			}
			default:;
			}
		} else {
			String str = (String)obj;
			Log.i("SOCKET", "Receive From Server"+str);
			String[] msgs = str.split(" "); // split with whiteblank
			if (msgs.length == 6 && msgs[2].equals("TAO") && msgs[4].equals("SUC")) {
				return Integer.valueOf(22);
			} else if (msgs.length == 6 && msgs[2].equals("LAD") && msgs[4].equals("SUC")) {
				return Integer.valueOf(23);
			} else if (msgs.length == 6 && msgs[1].equals("RES") && msgs[2].matches("(FLY)|(DIR)|(HEI)") && msgs[4].equals("SUC")) {
				return Integer.valueOf(44);
			} else if (msgs.length == 6 && msgs[1].equals("RES") && msgs[2].equals("HOV") && msgs[4].equals("SUC")) {
				return Integer.valueOf(45);
			}
			return Integer.valueOf(-1); // nuknown order
		}
		return null;
	}
	
}
