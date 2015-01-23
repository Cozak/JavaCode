package mp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

class MainPanel extends JPanel {
	private BufferedImage BI = null;
	
	public MainPanel() {
		//.....
	}
	
	public void setBI(BufferedImage bi) {
		this.BI = bi;
	}
	
	public BufferedImage getBI() {
		return BI;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (this.BI == null) {
			//System.out.println("Nothing");
			return;
		}
		g.drawImage(this.BI, (int)this.getWidth()/2 - (int)this.BI.getWidth()/2,
				(int)this.getHeight()/2 - (int)this.BI.getHeight()/2, Color.WHITE, this);
	}
}

public class MainFace extends JFrame{
	//private JLabel label = null;
	private JFileChooser filechooser = null;
	private static int DEFAULT_WIDTH = 300;
	private static int DEFAULT_HEIGHT = 300;
	private JMenuBar menuBarS = null;
	private JMenu menuP = null;
	private JMenu menuS = null;
	private JMenu menuV = null;
	private JMenu menuALG = null;
	private JMenu menuNAD = null;
	private JMenuItem[] AFS = null;
	private MainPanel MP = null;
	private String title= "De-Noiser";
	
	private SubFace[] SFS = null;
	private final int SFSM = 10;
	
	public MainFace() {
		this.init(); // init
		this.eventAFLS(); // set event listener
		this.showOut();
	}
	
	private void showOut() {
		this.setVisible(true);
	}
	
	private void eventAFLS() {
		
		// filter
		MainFace.this.filechooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "*.bmp";
			}
			
			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				if (f != null) {
					if (f.isDirectory() || f.getName().endsWith(".bmp")) {
						return true;
					}
				}
				return false;
			}
		});
		
		// init clear noise item
		// how many kinds of noise-adders and algorithms can use
		String[] strs =  (String[])MainFace.this.opExec(null, -1); // init
		if (strs == null || strs.length <= 1) {
			return;
		}
		int i,j;
		for (i = 0, j = 0; i < strs.length; ++i) {
			if (strs[i].equals("")) {
				j = i;
				continue;
			}
			JMenuItem jmi = new JMenuItem(strs[i]);
			if (j == 0) { // adding de-noise algorithms
				this.menuALG.add(jmi);
				jmi.addActionListener(new ActionListener() {
				
					@Override
					public void actionPerformed(ActionEvent e) { // process the image with our algorithms
						// TODO Auto-generated method stub
						if (e.getSource() instanceof JMenuItem) {
							MainFace.this.setTitle(title+"....Please Wait....");
							BufferedImage bi = (BufferedImage)MainFace.this.opExec(
									((JMenuItem)e.getSource()).getText(), 7);
							if (bi != null) {
								MainFace.this.MP.setBI(bi);
								MainFace.this.repaint(1000);// *******************************************************
							} else {
								JOptionPane.showMessageDialog(MainFace.this, "Unknown Error During Execution!!!");
							}
							MainFace.this.setTitle(title);
						}
					}
				});
			} else { // adding noise-add method
				this.menuNAD.add(jmi);
				jmi.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						// add a dialog to input the number
						String str = "-1";
						while (str != null && !str.matches("\\d{1,2}") && !str.equals("")) {
							str = JOptionPane.showInputDialog("Between 1~99");
						}
						if (str == null || str.equals("")) {
							return;
						}
						BufferedImage bi = (BufferedImage)MainFace.this.opExec(((JMenuItem)e.getSource()).getText()+"|"+str, 6);
						if (bi != null) {
							MainFace.this.MP.setBI(bi);
							MainFace.this.repaint(1000);// *******************************************************
						} else {
							JOptionPane.showMessageDialog(MainFace.this, "Noise Adding Failed!!!");
						}
					}
				});
			}
		}
		
	}
	
	private void init() {
		this.setTitle(title);
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)screenSize.getWidth()/2 - (int)this.getWidth()/2,
				(int)screenSize.getHeight()/2 - (int)this.getHeight()/2); // set at the center of the screen
		
		this.MP = new MainPanel();
		this.add(this.MP);
		
		//this.label = new JLabel();
		//this.add(this.label);
		this.filechooser = new JFileChooser();
		this.filechooser.setCurrentDirectory(new File("."));
		
		this.menuBarS = new JMenuBar();
		this.setJMenuBar(this.menuBarS); // add MenuBar
		
		// FILE PART
		this.menuS = new JMenu("FILE");
		this.menuBarS.add(this.menuS);
		
		JMenuItem clearItem = new JMenuItem("Clear");
		clearItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				MainFace.this.MP.setBI((BufferedImage)MainFace.this.opExec(null, -2)); // invoke kernel to clean the record
				MainFace.this.repaint(1000);// *******************************************************
			}
		});
		this.menuS.add(clearItem);
		
		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) { // open file
				// TODO Auto-generated method stub
				int res = MainFace.this.filechooser.showOpenDialog(null);
				if (res != JFileChooser.APPROVE_OPTION) {
					return;
				}
				String name = MainFace.this.filechooser.getSelectedFile().getPath();
				BufferedImage tmp = (BufferedImage)MainFace.this.opExec(name, 0);
				if (tmp == null) {
					JOptionPane.showMessageDialog(MainFace.this, "Fail to open");
					return;
				}
				MainFace.this.MP.setBI(tmp);
				MainFace.this.repaint(1000);// *******************************************************
			}
		});
		this.menuS.add(openItem);
		
		
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) { // save file
				// TODO Auto-generated method stub
				if (MainFace.this.MP.getBI() == null) {
					JOptionPane.showMessageDialog(MainFace.this, "NULL FILE!!!");
					return;
				}
				int res = MainFace.this.filechooser.showSaveDialog(null);
				if (res != JFileChooser.APPROVE_OPTION) {
					return;
				}
				if (MainFace.this.filechooser.getSelectedFile().exists()) {
					if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(null, 
							MainFace.this.filechooser.getSelectedFile()+" Existed, Cover it or not?",
							"FILE EXISTED!!!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
						return;
					}
				}
				String name = MainFace.this.filechooser.getSelectedFile().getPath();
				String tmp = (String)MainFace.this.opExec(name, 1);
				if (tmp == null) {
					JOptionPane.showMessageDialog(MainFace.this, "Failed to save");
				}
			}
		});
		this.menuS.add(saveItem);
		
		
		JMenuItem exitItem = new JMenuItem("Exit");
		this.menuS.add(exitItem);
		exitItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0); // EXIT
				//MainFace.this.dispose();
			}
		});
		
		// OP PART
		this.menuP = new JMenu("OPERATION");
		this.menuBarS.add(this.menuP);
		
		JMenuItem goHead = new JMenuItem("Front");
		goHead.addActionListener(new ActionListener() { // go head
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				BufferedImage bi = (BufferedImage)MainFace.this.opExec(null, 2);
				if (bi == null) { // the same
					JOptionPane.showMessageDialog(MainFace.this, "The Latest One");
				} else {
					MainFace.this.MP.setBI(bi);
					MainFace.this.repaint(1000);// *******************************************************
				}
			}
		});
		this.menuP.add(goHead);
		
		
		JMenuItem recoverIM = new JMenuItem("Back");
		recoverIM.addActionListener(new ActionListener() { // back to the last image
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				BufferedImage bi = (BufferedImage)MainFace.this.opExec(null, 3);
				if (bi == null) { // the same
					JOptionPane.showMessageDialog(MainFace.this, "The Oldest One");
				} else {
					MainFace.this.MP.setBI(bi);
					MainFace.this.repaint(1000);// *******************************************************
				}
			}
		});
		this.menuP.add(recoverIM);
		
		
		this.menuNAD = new JMenu("Add Noise");
		this.menuP.add(this.menuNAD);
		
		
		this.menuALG = new JMenu("Noise Clear");
		this.menuP.add(this.menuALG);
		
		// add View
		this.menuV = new JMenu("View");
		this.menuBarS.add(this.menuV);
		JMenuItem subViewItem = new JMenuItem("SubFace");
		this.menuV.add(subViewItem);
		subViewItem.addActionListener(new ActionListener() { // create a subFace
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				if (MainFace.this.MP.getBI() != null) {
					for (int i = 0; i < MainFace.this.SFSM; ++i) {
						if (MainFace.this.SFS[i] == null) {
							MainFace.this.SFS[i] = new SubFace(MainFace.this, MainFace.this.MP.getBI(), i);
							return;
						}
					}
				}
				JOptionPane.showMessageDialog(MainFace.this, "Null File or Too Many Sub-Windows");
			}
		});
		
		this.SFS = new SubFace[this.SFSM];
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // just like System.exit(0)
	}
	
	/**
	 * Override Please
	 * str may be filename 
	 * task_ord
	 * init_-1
	 * open_0
	 * save_1
	 * front_2
	 * back_3
	 * addNoise_6
	 * denoise_7..with algorithm name
	 * compare two image file_17
	 * @return
	 */
	public Object opExec(Object sth, int ord) {
		// all of below are for tests only
		if (ord == -1) {
			String[] strs = new String[3];
			strs[0] = "Comment";
			strs[1] = "Graphy Cuts";
			strs[2] = "Grate Cuts";
			return strs;
		}
		
		if (ord == 7) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private void subClose(int id) {
		this.SFS[id] = null;
	}
	
	private static class SubFace extends JFrame {
		
		private int selfID = -1;
		private MainFace master = null;
		private static int DEFAULT_WIDTH = 300;
		private static int DEFAULT_HEIGHT = 250;
		private JMenuBar menuBarS = null;
		private JMenuItem cmpItem = null;
		private MainPanel MP = null;
		private String title= "De-Noiser-Cmp";
		
		public SubFace(MainFace mst, BufferedImage bi, int id) {
			this.selfID = id;
			this.init();
			this.master = mst;
			this.MP.setBI(bi);
			this.showOut();
		}
		private void showOut() {
			this.setVisible(true);
		}
		private void init() {
			this.setTitle(title);
			this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((int)screenSize.getWidth()/2 - (int)this.getWidth()/2,
					(int)screenSize.getHeight()/2 - (int)this.getHeight()/2); // set at the center of the screen
			
			
			this.MP = new MainPanel();
			this.add(this.MP);
			
			this.menuBarS = new JMenuBar();
			this.setJMenuBar(this.menuBarS); // add MenuBar
			
			this.cmpItem = new JMenuItem("Compare");
			this.menuBarS.add(this.cmpItem);
			this.cmpItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					Double rate = (Double)SubFace.this.master.opExec(SubFace.this.MP.getBI(), 17);
					if (rate == null) {
						JOptionPane.showMessageDialog(SubFace.this, "Null File or Type can't match");
						return;
					}
					JOptionPane.showMessageDialog(SubFace.this, "Recover Rate: "+rate.toString()+"%");
				}
			});
			
			//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
		protected void processWindowEvent(WindowEvent e) {
			if (e.getID() == WindowEvent.WINDOW_CLOSING) {
				this.master.subClose(this.selfID);
				this.dispose();
			}
			super.processWindowEvent(e);
		}
		
		private void selfClose() {
			this.master.subClose(this.selfID);
			this.dispose();
		}
	}
}


