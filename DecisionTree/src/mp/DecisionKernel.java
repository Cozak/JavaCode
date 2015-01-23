package mp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * Main Entrance
 * 
 * @author ZAK
 *
 */
public class DecisionKernel {

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

	/**
	 * Main Entrance
	 * 
	 * @param args
	 */
	public static void main(String... args) {

		boolean isErrorOccur = false;
		ArrayList<String> testfiles = null;
		ArrayList<String> trainfiles = null;
		int numOfItem = -1, itemForComp = -1;
		String sp = "";
		int thresholdA = 100, thresholdB = 600, thPeriod = 40, numofcase = 0;
		// get source input -testfile or -trainfile
		if (args.length < 8) {
			isErrorOccur = true;
		} else {
			testfiles = new ArrayList<String>();
			trainfiles = new ArrayList<String>();
			for (int i = 0; i < args.length;) {
				if (args[i].matches("\\-x")) { // match trainfiles
					while (++i < args.length
							&& args[i].matches("\\w+(\\.\\w+)?")) {
						trainfiles.add(args[i]);
					}
				} else if (args[i].matches("\\-c")) { // match testfiles
					while (++i < args.length
							&& args[i].matches("\\w+(\\.\\w+)?")) {
						testfiles.add(args[i]);
					}
				} else if (args[i].matches("\\-n")) { // match testfiles
					if (++i < args.length && args[i].matches("\\d+")) {
						numOfItem = Integer.parseInt(args[i++]);
					}
				} else if (args[i].matches("\\-m")) { // match testfiles
					if (++i < args.length && args[i].matches("\\d+")) {
						itemForComp = Integer.parseInt(args[i++]);
					}
				} else if (args[i].matches("\\-s")) { // match testfiles
					if (++i < args.length && args[i].matches("[,\\-\\|]")) {
						sp = args[i++];
					}
				} else if (args[i].matches("\\-p")) { // match testfiles
					if (++i < args.length
							&& args[i].matches("\\d+\\-\\d+\\-\\d+")) {
						String[] pars = args[i++].split("-");
						thresholdA = Integer.parseInt(pars[0]);
						thresholdB = Integer.parseInt(pars[1]);
						thPeriod = Integer.parseInt(pars[2]);
					}
				} else {
					isErrorOccur = true; // Illegal Parameters
					break;
				}
			}
			if (numOfItem == -1 || itemForComp == -1 || trainfiles.size() == 0
					|| testfiles.size() == 0 || sp.isEmpty()) {
				isErrorOccur = true;
			}
		}
		if (isErrorOccur) {
			System.out.println("Parameters' Format:" + "\n-x [trainfile][mult]"
					+ "\n-c [testfile][mult]"
					+ "\n-n [number of property][single]"
					+ "\n-m [property to judge][single]"
					+ "\n-s [',', '|', '-']"
					+ "\n-p [LowerBound-UpperBound-Interval] bound:0~1000"
					+ "\nEach item needs at least one parameter");
			return;
		}

		// DecisionTree
		DecisionTree dt = new DecisionTree();
		// Data Set
		ArrayList<String> dataSet = new ArrayList<String>();
		// first, train and build the tree
		// ��ȡѵ�����ļ������ѵ�����ļ���ȡ����ϲ�
		for (String trainfile : trainfiles) {
			File f = new File(trainfile);
			try {
				ArrayList<String> arr = DecisionKernel.convertStreamToArray(
						new FileInputStream(f), "GBK");
				if (arr != null) {
					dataSet.addAll(arr);
				}
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found: " + trainfile);
			}
		}

		/**
		 * read test-files
		 * ��ȡ�����ļ�
		 */
		ArrayList<String> dataTest = new ArrayList<String>();
		for (String testfile : testfiles) {
			File f = new File(testfile);
			try {
				ArrayList<String> brr = DecisionKernel.convertStreamToArray(
						new FileInputStream(f), "GBK");
				if (brr != null) {
					dataTest.addAll(brr);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("File Not Found: " + testfile);
			}

		}
		// build a decision tree with dataSet
		// Ϊ��������ʼ����������
		if (dt.initTrainData(dataSet, itemForComp, numOfItem, sp)) {
			numofcase = dataSet.size();
			dataSet = null; // free
		}
		// read test files and prepare the data set for test
		// ���߻���
		LearningCurve lc = new LearningCurve("Learning Curve", "Matched Rate",
				"Number Of Case For Training");
		// �Ը�������ֵ��Χ����ָ��������ظ�����
		for (int i = thresholdA; i <= thresholdB; i += thPeriod) {
			double bestMatchedRate = 0; // look for the best matched rate
			int bestCases = 0; // number of case when having the best result
			ArrayList<LRPair<Double, Double>> ap = new ArrayList<LRPair<Double, Double>>();
			for (int j = 1; j <= numofcase; ++j) {
				// ��ѵ������ǰj�����ݽ��о��������ɲ�������ֵΪi
				dt.treeTraining(j, i);
				// ���о�����������Ĳ������ݽ��д���
				double r = dt.treeTest(dataTest);
				ap.add(new LRPair<Double, Double>((double) j, r)); // ����ÿ����ֵ�Ľ���ֱ��¼
				// ͳ����ͬ��ֵ�����ƥ���������ѵ��������
				if (r > bestMatchedRate) { // update if better
					bestMatchedRate = r;
					bestCases = j;
				}
			}
			lc.addLine(ap, "Threshold: " + i); // add a learning curve with the
												// certain parameters
			// show the best result for each configuration
			System.out.println("Threshold: " + i + "\n--->BestMatchedRate: "
					+ bestMatchedRate + " in " + bestCases + " cases");
		}

		// show the learning curve
		// dt.treePrintOut();
		// ����ѧϰ����
		lc.LearningCurveshow();
		System.out.println("\n" + dt);
	}
}
