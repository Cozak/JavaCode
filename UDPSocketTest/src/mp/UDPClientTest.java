package mp;

import java.io.*;
import java.net.*;

public class UDPClientTest {
	public static void main(String[] args) {
		InetAddress SIP = null;
		try {
			SIP = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket dgs = null;
		byte[] rbsend = new byte[128];
		try {
			 dgs = new DatagramSocket(); // select a valid port for the client randomly
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String str = null;
		while (dgs != null) {
			try {
				str = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Sed : " + str + "\n");
			rbsend = str.getBytes();
			System.out.println("The Length of Text in Client : " + rbsend.length + "\n");
			DatagramPacket dgp = new DatagramPacket(rbsend, rbsend.length, SIP, 4783);
			try {
				dgs.send(dgp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (str.equals("bye")) {
				break;
			}
			rbsend = new byte[128];
			DatagramPacket dgr = new DatagramPacket(rbsend, rbsend.length);
			try {
				dgs.receive(dgr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// str = rbsend.toString();
			str = new String(dgr.getData());
			System.out.println(str + "\n");
		}
		
		if (dgs != null) {
			dgs.close();
			System.out.println("Client Closed");
		}
	}

}
