package mp;

import java.io.*;
import java.net.*;

public class UDPServerTest {
	public static void main(String[] args) {
		DatagramSocket dgs = null;
		byte[] rbsend = null;
		try {
			 dgs = new DatagramSocket(4783); // bind on a certain port
			 
			 System.out.println("Serve Launched\n");
		
			 String str = null;
			 while (dgs != null) {
				 rbsend = new byte[128];
			DatagramPacket dgr = new DatagramPacket(rbsend, rbsend.length);
			try {
				dgs.receive(dgr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*if (rbsend == dgr.getData()) {
				System.out.println("Equal!!!\n");
			}*/
			str = new String(dgr.getData()); // rbsend == dgr.getDate();
			System.out.println("Server Recevied : " + str + "\n");
			rbsend = ("Rep : " + str.toUpperCase()).getBytes();
			System.out.println("Message UPPER : " + str.toUpperCase() + "\n");
			DatagramPacket dgp = new DatagramPacket(rbsend, rbsend.length, dgr.getAddress(), dgr.getPort());
			System.out.println("The Length : " + rbsend.length + "\n");
			try {
				dgs.send(dgp);
				System.out.println("Responed\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// str = rbsend.toString();
		}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dgs != null) {
				dgs.close();
			}
		}
	}

}
