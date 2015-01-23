package mypackage;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JPanel;

public class Chessboard extends JPanel
{
	//default length and the number of grid
	public static final int _gridLen = 22, _gridNum = 19;
	
	private Vector chessman;
	private int alreadyNum;
	private int curentTurn;
	private int gridNum, gridLen;
	private int chessmanLength;
	private Chesspoint[][] map;
	private Image offscreen;
	private Graphics offg;
	private int size;
	private int top = 13, left = 13;
	private Point mouseLoc;
	private ControlPanel controlPanel;
	
	public int getWidth() {
		return size + controlPanel.getWidth() + 35;
	}
	public int getHeight() {
		return size;
	}
	
	public Chessboard() {
		gridNum = this._gridNum;
		gridLen = this._gridLen;
		chessmanLength = gridLen * 9 / 10;
		size = 2 * left + gridNum * gridLen;
		addMouseListener(new PutChess());
		addMouseMotionListener(new MML());
		setLayout(new BorderLayout());
		controlPanel = new ControlPanel();
		setSize(getWidth(), size);
		add(controlPanel, "West");
		startGame();
	}
	
	public void addNotify() {
		super.addNotify();
		offscreen = createImage(size, size);
		offg = offscreen.getGraphics();
	}
	
	public void paint(Graphics g) {
		offg.setColor(new Color(180, 150, 100));
		offg.fillRect(0, 0, size, size);
		
		offg.setColor(Color.BLACK);
		for (int i = 0; i < gridNum; ++i) {
			int x1
		}
	}
}