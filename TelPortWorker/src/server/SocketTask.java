package server;

import intface.RoundTaskInF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketTask implements Runnable, RoundTaskInF {
	private Kola kola = null;
	private Socket sK = null;
	private InputStream ipS = null;
	private OutputStream opS = null;
	private boolean isInterupt = false;
	
	public SocketTask(Socket sk) {
		this.sK = sk;
		try {
			this.kola = Kola.createKola();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void init() {
		try {
			this.ipS = this.sK.getInputStream();
			this.opS = this.sK.getOutputStream();
		} catch (IOException e) {
			System.out.println("Unknown Socket Task Problem");
		}
	}
	
	@Override
	public void run() {
		this.init();
		System.out.println("This new Socket's port : "+this.sK.getLocalPort());
		if (this.ipS == null || this.opS == null) {
			return;
		}
		
		PrintWriter pw = new PrintWriter(this.opS, true);
		BufferedReader br = new BufferedReader(new InputStreamReader(this.ipS));
		
		//System.out.println("Port : "+this.sK.getPort()+" connected");
		this.kola.callRecordBuffChange("Address:Port "
				+this.sK.getInetAddress().toString()+":"+this.sK.getPort()+" connected");
		
		String msg = null;
		try {
			
			while ((msg = br.readLine()) != null && !this.isCircleInterupt()) {
				//System.out.println(this.sK.getPort()+" ------> "+msg);
				this.kola.callRecordBuffChange(
						this.sK.getInetAddress().toString()+":"+this.sK.getPort()+" ------> "+msg);
				
				pw.println("Responed: "+msg);
			}
		} catch (IOException e) {
			//System.out.println("Chat Failed");
			this.kola.callRecordBuffChange("Chat Failed");
		} finally {
			if (this.sK != null) {
				try {
					//System.out.println("Port : "+this.sK.getPort()+" close");
					this.kola.callRecordBuffChange("Address:Port "
					+this.sK.getInetAddress().toString()+":"+this.sK.getPort()+" close");
					this.sK.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public boolean isCircleInterupt() {
		// TODO Auto-generated method stub
		return this.isInterupt;
	}

	@Override
	public void setCircleInterupt(boolean bol) {
		// TODO Auto-generated method stub
		this.isInterupt = bol;
		try {
			this.sK.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
