package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import view.MainView;

public class Kola {
	private static int port = 4783;
	private static int linkMax = 10;
	private static Kola kola = null;
	private static int perThread = 1;
	//private static int kolaCount = 0;
	
	private AsyThreadPool TP = null;
	private int recvSize = 2048; // 2k
	private ServerSocket serverSK = null;
	//private LinkedList<SocketTask> sers = new LinkedList<SocketTask>();
	private StringBuffer strBuff = new StringBuffer();
	private MainView mV = null;
	
	
	private Kola() throws IOException {
		this.TP = new AsyThreadPool(Runtime.getRuntime()
				.availableProcessors() * Kola.perThread);
		//this.TP = new AsyThreadPool(2);
		
		this.serverSK = new ServerSocket();
		this.serverSK.setReuseAddress(true);
		this.serverSK.setReceiveBufferSize(this.recvSize);
		this.serverSK.bind(new InetSocketAddress(Kola.port), Kola.linkMax);
		
		// create a window for administrator
		this.mV = new MainView();
	}
	
	public void callRecordBuffChange(String msg) {
		synchronized(this.strBuff) {
			this.strBuff.replace(0, this.strBuff.length(), msg);
			//System.out.println("RECORD UP");
			Kola.this.mV.addRecordStr(Kola.this.strBuff.toString());
			//System.out.println("RECORD DOWN");
		}
	}
	
	public synchronized static Kola createKola() throws IOException {
		if (Kola.kola == null) {
			Kola.kola = new Kola();
			System.out.println("Kola jumps up");
		}
		//Kola.kolaCount++;
		return Kola.kola;
	}
	
	public synchronized void killKola(boolean bol) throws IOException {
		try {
			if (bol) {
				this.TP.join();
			} else {
				this.TP.close();
			}
		} finally {
			if (this.serverSK != null) {
				this.serverSK.close();
			}
		}
	}
	
	public static void Kill(boolean bol) {
		try {
			if (Kola.kola == null) {
				return;
			}
			/*if (--Kola.kolaCount > 0) {
				System.out.println("A Kola's body here");
				return;
			}*/
			Kola.kola.killKola(bol);
			System.out.println("Kola all over");
		} catch (IOException e) {
			System.out.println("Kola Error End");
		}
		
	}
	
	public static void main(String[] args) {
		Kola kola = null;
		try {
			kola = Kola.createKola();
			// service begin
		    kola.server();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("One Tree one Kola");
		}
		
	}
	
	public void server() {
		Socket whorse = null;
		while (true) {
			try {
				whorse = this.serverSK.accept();
			} catch (IOException e) {
				System.out.println("Whorse is shocked");
				break;
			}
			if (whorse == null) {
				continue;
			}
			
			SocketTask tmp = new SocketTask(whorse);
			//this.sers.add(tmp);
			this.TP.addTask(tmp);
			// new SocketTask(); 
			whorse = null;
		}
	}
}
