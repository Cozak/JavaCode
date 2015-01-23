package mp;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

class BallMember {
	private String ballName = null;
	private JPanel world = null;
	private Dimension ballSize = null;
	private double xp = -1, yp = -1;
	private double xr = -1, yr = -1;
	private final double maxRange = 120;
	
	/**
	 * Get its world and ID
	 * @param wd
	 * @param num
	 * @throws Exception 
	 */
	public BallMember(JPanel wd, int num, Dimension dm) {
		if (wd == null) {
			System.out.println("World is null");
			System.exit(1); // illegal
		}
		this.ballName = "Ball_No."+num;
		this.world = wd;
		this.ballSize = dm;
		Dimension dw = this.world.getSize();
		this.xp = Math.random()*(dw.getWidth() - this.ballSize.getWidth());
		this.yp = Math.random()*(dw.getHeight() - this.ballSize.getHeight());
		this.xr = Math.random()*this.maxRange - this.maxRange/2;
		this.yr = Math.random()*this.maxRange - this.maxRange/2;
	}
	public String getBallName() {
		return this.ballName;
	}
	public void ballDelete() {
		this.world = null;
	}
	public double getXRate() {
		return this.xr;
	}
	public double getYRate() {
		return this.yr;
	}
	/**
	 * Tell the ball how to act and change its statement
	 */
	public double act() {
		this.xp += this.xr/(Math.abs(this.xr) > Math.abs(this.yr)?Math.abs(this.xr):Math.abs(this.yr));
		this.yp += this.yr/(Math.abs(this.xr) > Math.abs(this.yr)?Math.abs(this.xr):Math.abs(this.yr));
		Dimension dm = this.world.getSize();
		// if crash bounce back
		if (this.xp < 0) {
			this.xp *= -1;
			this.xr *= -1;
		} else if (this.xp > dm.getWidth()-this.ballSize.getWidth()) {
			this.xp -= 2*(this.xp - (dm.getWidth()-this.ballSize.getWidth()));
			this.xr *= -1;
		}
		if (this.yp < 0) {
			this.yp *= -1;
			this.yr *= -1;
		} else if (this.yp > dm.getHeight()-this.ballSize.getHeight()) {
			this.yp -= 2*(this.yp - (dm.getHeight()-this.ballSize.getHeight()));
			this.yr *= -1;
		}
		return PhysicsTheorm.delayBasedOnSpeed2D(Math.abs(this.xr), Math.abs(this.yr));
	}
	
	/**
	 * @return the shape of the ball
	 */
	public Ellipse2D getShape() {
		return new Ellipse2D.Double(this.xp, this.yp, this.ballSize.getWidth(), this.ballSize.getHeight());
	}
	/**
	 * Whether this ball is still alive
	 * @return
	 */
	public boolean isAlive() {
		return this.world != null;
	}
}

class BallFace extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final double JFHeight = 400;
	private final double JFWidth = 400;
	private final String ballTitle = "Ball Flying";
	private JPanel ballTable = null;
	private ArrayList<BallMember> balls = null;
	private final int maxBalls = 20;
	private int ballNum = 100;
	private boolean timerGain = true;
	
	public BallFace() {
		this.init();
//		this.eventInit();
		this.timeEngine();
	}
	private void timeEngine() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					synchronized (BallFace.this) {
						if (!BallFace.this.timerGain) {
							break;
						}
					}
					BallFace.this.ballTable.repaint(500);
				}
			}
		}.start();
	}
	private void init() {
		// set title, size and location
		this.setTitle(this.ballTitle);
		this.setSize((int)JFWidth, (int)JFHeight);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)screenSize.getWidth()/2 - (int)this.JFWidth/2,
				(int)screenSize.getHeight()/2 - (int)this.JFHeight/2);
		// add menuBar
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		JMenu menus = new JMenu("OP");
		JMenu menuc = new JMenu("Conp");
		menuBar.add(menus);
		menuBar.add(menuc);
		// add menuItem
		JMenuItem butAdd = new JMenuItem("Add");
		menus.add(butAdd);
		butAdd.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
					BallFace.this.addBall();
			}
		});
		JButton keyAdd = new JButton();
		this.add(keyAdd);
		keyAdd.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				System.out.println(e.getKeyChar());
				if (e.getKeyChar() == KeyEvent.VK_1) {
					BallFace.this.addBall();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		JMenuItem butController = new JMenuItem("Controller");
		menuc.add(butController);
		JMenuItem butTimer = new JMenuItem("Time-Switcher");
		menuc.add(butTimer);
		butTimer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				synchronized (BallFace.this) {
					if (BallFace.this.timerGain) {
						BallFace.this.timerGain = false;
					} else {
						BallFace.this.timerGain = true;
						BallFace.this.timeEngine();
					}
				}
			}
		});
		JMenuItem butClear = new JMenuItem("Clear");
		menus.add(butClear);
		butClear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (BallFace.this.balls != null) {
					while (BallFace.this.balls.size() > 0) {
						BallFace.this.ballClear(0);
					}
					BallFace.this.ballTable.repaint(100);
				}
			}
		});
		
		// array of balls
		this.balls = new ArrayList<BallMember>(this.maxBalls);
		// create table to paint balls
		this.ballTable = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for (BallMember bm : BallFace.this.balls) {
					Graphics2D gd = (Graphics2D)g;
					gd.fill(bm.getShape());
				}
			}

		};
		this.add(this.ballTable);
		
		// set default site
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
//	private void eventInit() {
//		
//	}
	
	private void addBall() {
		this.balls.add(new BallMember(this.ballTable, this.ballNum++, new Dimension(10, 10)));
		// launch a unique thread for this ball
		new ObjectThread(this.balls.get(this.balls.size()-1)).start();
	}
	
	private class ObjectThread extends Thread {
		private BallMember mball = null;
		
		public ObjectThread(BallMember bm) {
			this.mball = bm;
		}
		@Override
		public void run() {
			try {
				while (this.mball != null && this.mball.isAlive()) {
					// let the ball fly
//					BallFace.this.ballTable.repaint(1000);
					ObjectThread.sleep((long)this.mball.act());
				}
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				System.out.println("Interrupt Error");
			}
		}
	}
	
	private void ballClear(int ord) {
		if (this.balls == null || this.balls.size() <= ord) {
			return;
		}
		this.balls.get(ord).ballDelete();
		this.balls.remove(ord);
	}
	
}

class PhysicsTheorm {
	public static double delayBasedOnSpeed2D(double spx, double spy) {
		double res = 300 / Math.sqrt(Math.pow(spx, 2)+Math.pow(spy, 2));
		return res > 0 ? res : 1;
	}
}

public class BallJumper {
	public static void main(String... args) {
		new BallFace();
	}
}
