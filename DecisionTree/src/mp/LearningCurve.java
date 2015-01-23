package mp;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * draw curves with sets of data
 * 
 * @author ZAK
 *
 */
public class LearningCurve extends ApplicationFrame {
	private String mTitle = null;
	private String xTitle = null;
	private String yTitle = null;
	private XYSeriesCollection xycollection = null; // contain a serial of lines

	/**
	 * Initialize with title of each axis
	 * @param mTitle
	 * @param yTittle
	 * @param xTitle
	 */
	public LearningCurve(String mTitle, String yTittle, String xTitle) {
		super(mTitle);
		this.mTitle = mTitle;
		this.xTitle = xTitle;
		this.yTitle = yTittle;
		this.xycollection = new XYSeriesCollection();
	}

	/**
	 * show this frame
	 */
	@SuppressWarnings("deprecation")
	public void LearningCurveshow() {
		JFreeChart jfc = ChartFactory.createXYLineChart(this.mTitle,
				this.xTitle, this.yTitle, this.xycollection,
				PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = (XYPlot)jfc.getPlot();
//		XYLineAndShapeRenderer xyline = (XYLineAndShapeRenderer) xyplot.getRenderer();
//		xyline.setShapesVisible(true);
//		xyline.setShapesFilled(true);
		NumberAxis Yaxi = (NumberAxis) xyplot.getRangeAxis();
	
		Yaxi.setAutoRangeIncludesZero(false);
		Yaxi.setNumberFormatOverride(new DecimalFormat("#0.000"));
//		Yaxi.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		this.setContentPane(new ChartPanel(jfc));
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this); // show this frame at the
														// center of the screen
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * add a set of data to show a new curve
	 * @param arr
	 * @param lineName
	 */
	public void addLine(ArrayList<LRPair<Double, Double>> arr, String lineName) {
		XYSeries xyser = new XYSeries(lineName);
		for (int i = 0; i < arr.size(); ++i) {
			xyser.add(arr.get(i).getFirst(), arr.get(i).getSecond());
		}
		this.xycollection.addSeries(xyser);
	}

}
