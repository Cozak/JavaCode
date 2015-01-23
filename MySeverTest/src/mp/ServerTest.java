package mp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
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
			System.out.println("Client Closed");
		}
		return null;
	}
	
	public static void main (String[] args) {
		Socket sK = null;
		int size = 50;
		
		try {
			ServerSocket serK = new ServerSocket(4783);
			System.out.println("Server Ready\n");
			
			while (true) {
				sK = serK.accept();
			
				System.out.println("A client connected\n");
				BufferedReader iS = new BufferedReader(new InputStreamReader(sK.getInputStream()));
				OutputStream oS = sK.getOutputStream();
				//oS.write("Time to fly\n".getBytes());
				oS.write("1 USR 0;".getBytes());
				oS.flush();
				String msg = inputReader(iS, size, ';');
				int socketSEQ = 100;
				if (msg != null) {
					System.out.println("-->Checking Name and Password: "+msg);
					String[] msgs = msg.split(" ");
					if (msgs.length == 8 && msgs[5].equals("XiaoMi") && msgs[6].equals("123456")) {
						oS.write("2 UES SUC 1;".getBytes());
						oS.flush();
						while (true) {
//							msg = iS.readLine();
							msg = inputReader(iS, size, ';');
							if (msg == null) {
								break;
							}
							System.out.println("------>"+msg);
							msgs = msg.split(" ");
							if (msgs.length < 3) {
								break;
							}
							if (msgs[1].equals("TAO")) {
								oS.write((socketSEQ+" RES TAO x SUC 3;").getBytes());
								oS.flush();
							} else if (msgs[1].equals("LAD")) {
								oS.write((socketSEQ+" RES LAD x SUC 3;").getBytes());
								oS.flush();
							} else if (msgs[1].equals("FLY")) {
								oS.write((socketSEQ+" RES FLY x SUC 3;").getBytes());
								oS.flush();
							} else if (msgs[1].equals("DIR")) {
								oS.write((socketSEQ+" RES DIR x SUC 3;").getBytes());
								oS.flush();
							} else if (msgs[1].equals("HEI")) {
								oS.write((socketSEQ+" RES HEI x SUC 3;").getBytes());
								oS.flush();
							} else if (msgs[1].equals("HOV")) {
								oS.write((socketSEQ+" RES HOV x SUC 3;").getBytes());
								oS.flush();
							} else if (msgs[1].equals("HB")) {
								oS.write((socketSEQ+"HB SUC 1;").getBytes());
								oS.flush();
							} 
						}
					}
				}
				if (sK != null) {
					System.out.println("Socket Closed");
					sK.close();
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("Closed\n");
		}
		
	}
}
