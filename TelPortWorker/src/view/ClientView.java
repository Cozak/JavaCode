package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.*;

public class ClientView extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jP = null;
	private JTextArea jtA = null;
	private JTextArea jtaC = null;
	private JScrollPane jsP = null;
	private JScrollPane jspC = null;
	private JButton butSend = null;
	private LinkedList<String> recTasks = null;
	private boolean isClosed = false;
	private JTextArea aR = null;
	private JTextArea pT = null;
	private JButton linkBreak = null;
	//private StringBuffer sB = null;
	
	public ClientView() {
		this.recTasks = new LinkedList<String>();
		//this.sB = new StringBuffer("Welcome to Kola");
		this.jP = new JPanel();
		this.jtA = new JTextArea("");
		this.jtaC = new JTextArea("");
		this.jsP = new JScrollPane(this.jtA);
		this.jspC = new JScrollPane(this.jtaC);
		this.butSend = new JButton("Send");
		this.aR = new JTextArea("Address");
		this.pT = new JTextArea("Port");
		this.linkBreak = new JButton("Link");
		
		this.init();
		
		new Thread() {
			@Override
			public void run() {
				ClientView.this.jtA.append("Welcome to AntTree-ChatBox"+"\n");
				while (true) {
					synchronized(ClientView.this) {
						while (!ClientView.this.recTasks.isEmpty()) {
							ClientView.this.jtA.append(ClientView.this.recTasks.removeFirst()+"\n");	
						}
						if (ClientView.this.isClosed) {
							break;
						}
						try {
							ClientView.this.wait();
						} catch (InterruptedException e) {
							System.out.println("ChatBox Error");
						}
					}
				}
			}
		}.start();
	}
	
	private void init() {
		this.jP.setLayout(null);
		
		this.jsP.setBounds(20, 20, 450, 200);
		this.jP.add(jsP);
		this.jtA.setLineWrap(true);
		this.jtA.setEditable(false);
		
		this.jspC.setBounds(20, 230, 450, 30);
		this.jP.add(jspC);
		this.jtaC.setLineWrap(true);
		
		this.butSend.setBounds(380, 270, 90, 20);
		this.butSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent even) {
				// TODO Auto-generated method stub
				String str = ClientView.this.jtaC.getText();
				ClientView.this.recordOutPush(str);
				ClientView.this.jtaC.setText("");
			}
		});
		this.butSend.setEnabled(false);
		this.jP.add(butSend);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ClientView.this.closeCurrentSocket();
				synchronized(ClientView.this) {
					ClientView.this.isClosed = true;
					ClientView.this.addRecordStr("See You Next Time"); // according inner method of windows
				}														// this word can't be seen in the box
				//System.exit(0);
				ClientView.this.setVisible(false);
			}
		});
		
		this.aR.setBounds(20, 330, 100, 20);
		this.jP.add(this.aR);
		this.pT.setBounds(130, 330, 40, 20);
		this.jP.add(this.pT);
		this.linkBreak.setBounds(20, 360, 150, 30);
		this.jP.add(this.linkBreak);
		this.linkBreak.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (ClientView.this.linkBreak.getText().equals("Link")) {
					
					if (ClientView.this.setAddressPort(
							ClientView.this.aR.getText(), ClientView.this.pT.getText())) {
						ClientView.this.butSend.setEnabled(true);
						ClientView.this.aR.setEditable(false);
						ClientView.this.pT.setEditable(false);
						ClientView.this.linkBreak.setText("Break");
						ClientView.this.addRecordStr("Connection Succeed");
					} else {
						ClientView.this.addRecordStr(
								ClientView.this.aR.getText()+":"
						+ClientView.this.pT.getText()+"---inValid");
					}
				} else {
					
					ClientView.this.butSend.setEnabled(false);
					ClientView.this.aR.setEditable(true);
					ClientView.this.pT.setEditable(true);
					ClientView.this.linkBreak.setText("Link");
					ClientView.this.closeCurrentSocket();
					ClientView.this.addRecordStr("Current connection off");
				}
			}
			
		});
		
		this.add(this.jP);
		this.setTitle("AntTree-ChatBox\n");
		this.setResizable(false);
		this.setBounds(100, 100, 500, 450);
		this.setVisible(true);
	}
	
	public synchronized void addRecordStr(String msg) {
		this.recTasks.add(msg);
		this.notifyAll();
	}
	
	public boolean reConnectTry() {
		return ClientView.this.setAddressPort(
				ClientView.this.aR.getText(), ClientView.this.pT.getText());
	}
	
	public void reSetClient() {
		ClientView.this.butSend.setEnabled(false);
		ClientView.this.aR.setEditable(true);
		ClientView.this.pT.setEditable(true);
		ClientView.this.linkBreak.setText("Link");
		ClientView.this.closeCurrentSocket();
		ClientView.this.addRecordStr("Current connection off[Reset]");
	}
	
	/*
	 * should be override
	 */
	public void recordOutPush(String msg) {
		//.......
	}
	
	/*
	 * should be override
	 */
	public boolean setAddressPort(String address, String port) {
		//.......
		return false;
	}
	
	/*
	 * should be override
	 */
	public void closeCurrentSocket() {
		//........
		System.out.println("Current Socket has been closed");
	}
	
	/*public static void main(String[] args) {
		new ClientView();
	}*/
}
