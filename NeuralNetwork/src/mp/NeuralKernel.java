package mp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Neural Kernel
 * @author ZAK
 *
 */
public class NeuralKernel {

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

		File ftra = new File("digitstra.txt");
		ArrayList<String> arr = null;
		File ftest = new File("digitstest.txt");
		ArrayList<String> brr = null;
		// NeuralNet nn = null;
		// Gain Function
		IntfActFunc func = new IntfActFunc() {

			@Override
			public Double IAFExecutor(Double inDate) {
				// TODO Auto-generated method stub
				double b = Math.pow(Math.E, -inDate);
				return 1 / (1 + b);
			}

			@Override
			public Double IAFDiffer(Double inDate) {
				// TODO Auto-generated method stub
				return Math.pow(Math.E, -inDate)
						* Math.pow(this.IAFExecutor(inDate), 2);
			}
		};
		try {
			arr = NeuralKernel.convertStreamToArray(new FileInputStream(ftra),
					"GBK");
			brr = NeuralKernel.convertStreamToArray(new FileInputStream(ftest),
					"GBK");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// threads 多线程运行多组测试
		int loops = 3;
		NeuralKernel.LoopThread th1 = new LoopThread(arr, brr, func, 5, 24, loops);
		NeuralKernel.LoopThread th2 = new LoopThread(arr, brr, func, 24, 40, loops);
		NeuralKernel.LoopThread th3 = new LoopThread(arr, brr, func, 40, 51, loops);
		NeuralKernel.LoopThread th4 = new LoopThread(arr, brr, func, 51, 58, loops);
		NeuralKernel.LoopThread th5 = new LoopThread(arr, brr, func, 58, 61, loops);
		th1.setPriority(Thread.MAX_PRIORITY);
		th2.setPriority(Thread.MAX_PRIORITY);
		th3.setPriority(Thread.MAX_PRIORITY);
		th4.setPriority(Thread.MAX_PRIORITY);
		th5.setPriority(Thread.MAX_PRIORITY);
		th1.start();
		th2.start();
		th3.start();
		th4.start();
		th5.start();
	}

	public static class LoopThread extends Thread {
		private ArrayList<String> trainset = null;
		private ArrayList<String> testset = null;
		private IntfActFunc func = null;
		private int up = -1;
		private int lw = -1;
		private int avgLoop = 5;

		public LoopThread(ArrayList<String> arr, ArrayList<String> brr,
				IntfActFunc func, int lw, int up, int avgloop) {
			this.trainset = arr;
			this.testset = brr;
			this.func = func;
			this.up = up;
			this.lw = lw;
			this.avgLoop = avgloop;
		}

		@Override
		public void run() {
			int[] nodeLy = new int[] { 64, 1, 10 }; // the head must be 64

			double avgTra = 0, avgTest = 0;

			for (int i = lw; i < up; ++i) {
				nodeLy[1] = i;
				ArrayList<ArrayList<IntfActFunc>> funs = new ArrayList<ArrayList<IntfActFunc>>();
				// while
				// the end is 1
				// 新建神经网络框架
				NeuralNet nn = new NeuralNet(nodeLy);
				// 预备各层神经元的激励函数
				for (int ii = 1; ii < nodeLy.length; ++ii) {
					funs.add(new ArrayList<IntfActFunc>());
					for (int j = 0; j < nodeLy[ii]; ++j) {
						funs.get(funs.size() - 1).add(this.func);
					}
				}
				// 设置每个神经元的激励函数
				nn.initNodeGains(funs);
				// 设置训练集，指明训练集的输入维度
				nn.initTrainSet(64, this.trainset, ",");
				avgTra = 0;
				avgTest = 0;

				// 对于同一份参数重复运行，取得多次测试结果
				for (int ll = 0; ll < avgLoop; ++ll) {
					nn.setUrAndLt(2, 0.8, 20);
					nn.initLinkWeight(-0.6, 0.6);
					nn.trainNet();
					avgTra += nn.testNet(this.trainset);
					avgTest += nn.testNet(this.testset);
				}
				// 获取多次测试平均正确率
				avgTra /= avgLoop;
				avgTest /= avgLoop;
				// 输出测试结果
				System.out.println(nodeLy[1] + ":TrainSet:" + avgTra);
				System.out.println(nodeLy[1] + ":TestSet:" + avgTest);
			}
		}
	}
}
