package mypackage;

import java.awt.*;
import java.awt.event.*;

public class Test_0 extends Frame {
	public static void main(String[] args) {
		new Test_0().run();
	}
	
	private void run() {
		configureFrame();
		createButton();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		setVisible(true);
	}
	
	/**
	 * create the main window
	 */
	private void configureFrame() {
		setTitle("Hello AWT");
		setLayout(new FlowLayout());
		setSize(new Dimension(200, 200));
		setLocation(0, 0);
	}
	
	/**
	 * create the button
	 */
	private void createButton() {
		Button button = new Button("Open");
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createDialog();
			}
		});
		
		add(button, BorderLayout.NORTH);
	}
	
	/**
	 * create the dialog
	 */
	private void createDialog() {
		final Dialog dialog = new Dialog(Test_0.this, "Dialog", true);
		dialog.setSize(new Dimension(267, 117));
		
		Toolkit toolkit = dialog.getToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		
		int x = Test_0.this.getX() 
				+ (Test_0.this.getWidth() - dialog.getWidth())/2;
		
		if (x < 0) {
			x = 0;
		}
		if (x + dialog.getWidth() > screenSize.width) {
			x = screenSize.width - dialog.getWidth();
		}
	}
}

