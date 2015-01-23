package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import view.ClientView;

public class AntTree {
	public static int buffersize = 2048;
	
	//private String goal_address = null;
	//private int goal_port = -1;
	private ClientView cV = null;
	private Socket sK = null;
	private PrintWriter pW = null;
	private BufferedReader bR = null;
	//private boolean isClosed = false;
	
	public AntTree() {
		this.cV = new ClientView() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public void recordOutPush(String msg) {
				synchronized(AntTree.this) {
					if (AntTree.this.pW != null) {
						AntTree.this.pW.println(msg);
						AntTree.this.cV.addRecordStr("Localhost: "+msg);
					}
				}
			}
			@Override
			public boolean setAddressPort(String address, String port) {
				/*if (AntTree.this.sK != null) {
					System.out.println("Warning!!! Socket seems to be alive before connection");
					return false;
				}*/
				if (!AntTree.isCorrectAddress(address) || !AntTree.isCorrectPort(port)) {
					return false;
				}
				synchronized(AntTree.this) {
					AntTree.this.sK = new Socket();
					try {
						AntTree.this.sK.setReuseAddress(true);
						AntTree.this.sK.setSendBufferSize(buffersize);
						AntTree.this.sK.connect(new InetSocketAddress(
							InetAddress.getByName(address), Integer.parseInt(port)), 5000);
					} catch (SocketTimeoutException t) {
						System.out.println("Connection Time Out");
						AntTree.this.sK = null;
						return false;
					} catch (Exception e) {
						System.out.println("New Connection Failed");
						AntTree.this.sK = null;
						return false;
					}
				
					try {
						AntTree.this.pW = new PrintWriter(AntTree.this.sK.getOutputStream(), true);
						AntTree.this.bR = new BufferedReader(new InputStreamReader(AntTree.this.sK.getInputStream()));
					} catch (IOException e) {
						System.out.println("Error: PrintWriter-BufferedReader");
						return false;
					}
					
				}
				return true;
			}
			@Override
			public void closeCurrentSocket() {
				if (AntTree.this.sK == null) {
					return;
				}
				synchronized(AntTree.this) {
					try {
						AntTree.this.sK.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("Error: Socket Close Failed");
					}
					AntTree.this.sK = null;
					AntTree.this.pW = null;
					AntTree.this.bR = null;
				}
			}
		};
	}
	
	public void clientRun() {
		String str = null;
		while (true) {
			str = null;
			synchronized(this) {
				if (!this.cV.isVisible()) {
					System.out.println("Client: Time to say bye");
					break;
				}
				if (this.sK == null) {
					continue;
				}
			}
			try {
				if ((str = this.bR.readLine()) != null) { // when connected, main thread will stay here
					this.cV.addRecordStr(str);
				} else { // Server suddenly closed!!!!!!!!
					System.out.println("Server Disconnect");
					this.cV.addRecordStr("Server Disconnect");
					this.cV.reSetClient();
					//Thread.sleep(5000);
				}
			} catch (Exception e) { // Client cut down the connection[Link break/Window close]
				// TODO Auto-generated catch block
				System.out.println("Suddenly Socket Closed");
			}
		}
	}
	
	public static boolean isCorrectAddress(String address) {
		// (\d+\.){3}\d+
		return address.matches("(\\d+\\.){3}\\d+");
	}
	public static boolean isCorrectPort(String port) {
		// \d+
		return port.matches("\\d+");
	}
	
	public static void main(String[] args) {
		new AntTree().clientRun();
	}
}
