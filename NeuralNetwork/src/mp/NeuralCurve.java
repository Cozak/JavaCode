package mp;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * Curve Drawer
 * @author ZAK
 *
 */
public class NeuralCurve extends ApplicationFrame {

	private String mTitle = null;
	private String xTitle = null;
	private String yTitle = null;
	XYSeries xyser = null;
	private XYSeriesCollection xycollection = null; // contain a serial of lines

	/**
	 * Initialize with title of each axis
	 * 
	 * @param mTitle
	 * @param yTittle
	 * @param xTitle
	 */
	public NeuralCurve(String mTitle, String yTittle, String xTitle,
			String lineTitle) {
		super(mTitle);
		this.mTitle = mTitle;
		this.xTitle = xTitle;
		this.yTitle = yTittle;
		this.xyser = new XYSeries(lineTitle);
		this.xycollection = new XYSeriesCollection();
		this.xycollection.addSeries(xyser);
	}

	/**
	 * show this frame
	 */
	@SuppressWarnings("deprecation")
	public void neuralCurveshow() {
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);
		JFreeChart jfc = ChartFactory.createXYLineChart(this.mTitle,
				this.xTitle, this.yTitle, this.xycollection,
				PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = (XYPlot) jfc.getPlot();
		// XYLineAndShapeRenderer xyline = (XYLineAndShapeRenderer)
		// xyplot.getRenderer();
		// xyline.setShapesVisible(true);
		// xyline.setShapesFilled(true);
		NumberAxis Yaxi = (NumberAxis) xyplot.getRangeAxis();

		Yaxi.setAutoRangeIncludesZero(false);
		Yaxi.setNumberFormatOverride(new DecimalFormat("#0.000"));
		// Yaxi.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		this.setContentPane(new ChartPanel(jfc));
		this.pack();
		RefineryUtilities.positionFrameRandomly(this);
//		RefineryUtilities.centerFrameOnScreen(this); // show this frame at the
														// center of the screen
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * add a set of data to show a new curve
	 * 
	 * @param arr
	 * @param lineName
	 */
	public void addPoint(int nodes, double rate) {
		xyser.add(nodes, rate);
	}

	/**
	 * convert file streams to input-stream and split them into a list of string
	 * 
	 * @param iS
	 * @param Charset
	 * @return array of string
	 */
	public static ArrayList<String> convertStreamToArray(InputStream iS,
			String Charset) {
		try {
			InputStreamReader isR = new InputStreamReader(iS, Charset);
			BufferedReader bR = new BufferedReader(isR);
			ArrayList<String> als = new ArrayList<String>();
			while (bR.ready()) {
				als.add(bR.readLine());
			}
			return als;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return null;
	}

	public static void main(String... args) {
		String dfname = "Result.txt";
		if (args.length > 0 && args[0].matches("[a-zA-Z_].*\\.[a-zA-Z]+")) {
			dfname = args[0];
		}
		File f = new File(dfname);
		ArrayList<String> arr = null;

		try {
			arr = NeuralCurve.convertStreamToArray(new FileInputStream(f),
					"GBK");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("Failed To Open: " + dfname + "\nNeuralCurve.jar [filename]");
			return;
		}

		NeuralCurve trainC = new NeuralCurve("神经网络训练曲线", "拟合率", "单隐层节点数",
				"训练集自测");
		NeuralCurve testC = new NeuralCurve("神经网络训练曲线", "拟合率", "单隐层节点数", "测试集");

		int total = 0;
		for (int i = 0; i < arr.size(); ++i) {
			String[] strs = arr.get(i).split(":");
			if (strs.length != 3) {
				total++;
				continue;
			}
			try {
				int num = Integer.parseInt(strs[0]);
				double rate = Double.parseDouble(strs[2]);
				if (strs[1].equals("TrainSet")) {
					trainC.addPoint(num, rate);
				} else if (strs[1].equals("TestSet")) {
					testC.addPoint(num, rate);
				} else {
					total++;
				}
			} catch (Exception e) {
				continue;
			}
		}
		if (total > arr.size() * 0.5) {
			System.out.println("Case In File :"
					+ "\n[int]-TrainSet-[double]"
					+ "\nor"
					+ "\n[int]-TestSet-[double]");
			return;
		}
		trainC.neuralCurveshow();
		testC.neuralCurveshow();
	}
}
