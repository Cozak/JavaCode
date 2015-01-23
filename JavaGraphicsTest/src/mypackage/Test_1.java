package mypackage;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class Test_1 extends Frame
{
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		
		GradientPaint gp = new GradientPaint(180, 190, Color.yellow,
				220, 210, Color.red, true);
		g2.setPaint(gp);
		g2.setStroke(new BasicStroke(2.0f));
		
		Line2D.Float line = new Line2D.Float(20, 300, 200, 300);
		g2.draw(line);
		
		CubicCurve2D.Float cubic = new CubicCurve2D.Float(70, 100, 120, 50,
				170, 270, 220, 100);
		g2.draw(cubic);
		
		Ellipse2D.Float shape = new Ellipse2D.Float(200, 200, 60, 60);
		g2.fill(shape);
	}
	
	public static void main(String[] args)
	{
		
	}
}