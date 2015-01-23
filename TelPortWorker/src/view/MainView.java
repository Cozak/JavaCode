package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;

import server.Kola;

public class MainView extends JFrame{
	private JPanel jP = null;
	private JTextArea jtA = null;
	private JScrollPane jsP = null;
	private JButton butClose = null;
	private JCheckBox jcB = null;
	private LinkedList<String> recTasks = null;
	private boolean isClosed = false;
	//private StringBuffer sB = null;
	
	public MainView() {
		this.recTasks = new LinkedList<String>();
		//this.sB = new StringBuffer("Welcome to Kola");
		this.jP = new JPanel();
		this.jtA = new JTextArea("");
		this.jsP = new JScrollPane(this.jtA);
		this.butClose = new JButton("Close All");
		this.jcB = new JCheckBox("Force");
		this.init();
		
		new Thread() {
			@Override
			public void run() {
				MainView.this.jtA.append("Welcome to Kola-Console"+"\n");
				while (true) {
					synchronized(MainView.this) {
						while (!MainView.this.recTasks.isEmpty()) {
							MainView.this.jtA.append(MainView.this.recTasks.removeFirst()+"\n");	
						}
						if (MainView.this.isClosed) {
							break;
						}
						try {
							MainView.this.wait();
						} catch (InterruptedException e) {
							System.out.println("Console Error");
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
		
		this.butClose.setBounds(20, 220, 90, 20);
		this.jcB.setBounds(110, 220, 90, 20);
		this.butClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent even) {
				// new Thread to close the server
				MainView.this.butClose.setEnabled(false);
				MainView.this.addRecordStr("Console Closing....");
				new Thread() {
					@Override
					public void run() {
						if (MainView.this.jcB.isSelected()) {
							Kola.Kill(false);
						} else {
							Kola.Kill(true);
						}
						MainView.this.isClosed = true;
						MainView.this.addRecordStr("Console Record Server Closed");
						MainView.this.butClose.setEnabled(true);
					}
				}.start();
				
			}
		});
		this.jP.add(butClose);
		this.jP.add(jcB);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(this.jP);
		this.setTitle("Kola-Console\n");
		this.setResizable(false);
		this.setBounds(100, 100, 500, 280);
		this.setVisible(true);
	}
	
	public synchronized void addRecordStr(String msg) {
		this.recTasks.add(msg);
		this.notifyAll();
	}
	
}
