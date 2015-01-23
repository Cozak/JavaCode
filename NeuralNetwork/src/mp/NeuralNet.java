package mp;

import java.util.ArrayList;

/**
 * Gain Function Interface
 * @author ZAK
 *
 */
interface IntfActFunc {
	/**
	 * calculate the active value for the certain node 神经元激励函数接口方法
	 * 
	 * @param inDate
	 * @return
	 */
	public Double IAFExecutor(Double inDate);

	/**
	 * this function is the differential style of the IAFExecutor 神经元激励函数导数方法
	 * 
	 * @param inDate
	 * @return
	 */
	public Double IAFDiffer(Double inDate);
}

/**
 * Neural Net 
 * @author ZAK
 *
 */
public class NeuralNet {

	private int NumOfLayer = -1; // how many level in the net
	private ArrayList<Integer> NumOfNodeInLayer = null; // the number of node in
														// each layer
	private double[][] NodeValue = null; // In-date for each node in the net
	private ArrayList<ArrayList<IntfActFunc>> NodeGains = null; // active
																// function of
																// each node
	private ArrayList<double[]> WeightOfLink = null; // weight of each
														// link

	private int NumOfProperty = -1; // NumOfProperty is the index of the result
									// for this item
	private String splitMark = null;
	private ArrayList<double[]> trainSet = null;

	private double[] caseres = null;

	private double rate = 1; // the rate of training 学习速率
	private double limit = 1e-4; // stop training if meet it 最小均方差限定值
	private int loopTime = 100; // 同一组训练样本的循环次数

	/**
	 * structure of the entire neural net 根据一个int数组确定网络层数以及每层的节点数
	 * 
	 * @param nodesinlayers
	 */
	public NeuralNet(int... nodesinlayers) {
		this.NumOfLayer = nodesinlayers.length;
		this.NumOfNodeInLayer = new ArrayList<Integer>();
		this.NodeValue = new double[nodesinlayers.length][];
		for (int i = 0; i < nodesinlayers.length; ++i) {
			this.NumOfNodeInLayer.add(nodesinlayers[i]);
			this.NodeValue[i] = new double[nodesinlayers[i]];
		}
	}

	/**
	 * pre-process the train-set 初始化训练集
	 * 
	 * @param numofpro
	 * @param res
	 * @return
	 */
	public boolean initTrainSet(int numofpro, ArrayList<String> res, String sp) {
		if (res == null || sp == null) {
			return false;
		}
		try {
			this.NumOfProperty = numofpro;
			this.splitMark = sp;
			this.trainSet = new ArrayList<double[]>();
			for (int i = 0; i < res.size(); ++i) {
				String[] tmpstrs = res.get(i).split(this.splitMark);

				// 剔除不合规格的样本
				if (tmpstrs.length != this.NumOfProperty + 1) { // each case
																// with its real
																// result at the
																// end
					continue;
				}
				double[] tmpDoubles = new double[tmpstrs.length];
				for (int j = 0; j < tmpstrs.length; ++j) {
					tmpDoubles[j] = Double.parseDouble(tmpstrs[j]);
				}
				this.trainSet.add(tmpDoubles);
			}
		} catch (Exception e) {
			return false; // unknown error occur
		}

		return true;
	}

	/**
	 * There should be a link between each pair of node in two different layers
	 * You should know the number of link among these layers 初始化各层之间的连接权值
	 * 
	 * @param a
	 *            list of weights of each layers
	 * @return
	 */
	public boolean initLinkWeight(ArrayList<String> resw) {
		if (resw == null || this.NumOfNodeInLayer == null) {
			return false;
		}
		try {
			// 为每一个连接设置初始权值
			ArrayList<double[]> tmpArr = new ArrayList<double[]>();
			for (int i = 0; i < this.NumOfLayer - 1; ++i) {
				String[] tmpStrs = resw.get(i).split(this.splitMark);
				tmpArr.add(new double[this.NumOfNodeInLayer.get(i)
						* this.NumOfNodeInLayer.get(i + 1)]);
				for (int j = 0; j < tmpArr.get(i).length; ++j) {
					if (tmpStrs.length > j) {
						tmpArr.get(i)[j] = Double.parseDouble(tmpStrs[j]);
					} else { // weight = 0 as default
						tmpArr.get(i)[j] = 0; // 默认权值为0
					}
				}
			}
			this.WeightOfLink = tmpArr; // update
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * randomly initialize the weight of each link 初始化各连接权值，根据一对上下限随机设置初始权值
	 * 
	 * @param low
	 * @param up
	 */
	public void initLinkWeight(double low, double up) {
		// int loop = 0;
		ArrayList<double[]> tmpArr = new ArrayList<double[]>();
		for (int i = 0; i < this.NumOfLayer - 1; ++i) {
			tmpArr.add(new double[this.NumOfNodeInLayer.get(i)
					* this.NumOfNodeInLayer.get(i + 1)]);
			for (int j = 0; j < tmpArr.get(i).length; ++j) {
				tmpArr.get(i)[j] = Math.random() * (up - low) + low;
				// loop++;
			}
		}
		this.WeightOfLink = tmpArr; // update success
	}

	/**
	 * randomly initialize the weight of each link 使用默认的镜像值随机设置初始权值
	 */
	public void initLinkWeight() {
		// int loop = 0;
		ArrayList<double[]> tmpArr = new ArrayList<double[]>();
		for (int i = 0; i < this.NumOfLayer - 1; ++i) {
			tmpArr.add(new double[this.NumOfNodeInLayer.get(i)
					* this.NumOfNodeInLayer.get(i + 1)]);
			for (int j = 0; j < tmpArr.get(i).length; ++j) {
				tmpArr.get(i)[j] = Math.random() > 0.5 ? 0.5 : -0.5;
			}
		}
		this.WeightOfLink = tmpArr; // update success
	}

	/**
	 * except the input layer, each node has a gain function 初始化各个神经元的激励函数以及相应导数
	 * 
	 * @param nodegains
	 * @return
	 */
	public boolean initNodeGains(ArrayList<ArrayList<IntfActFunc>> nodegains) {
		if (nodegains == null || this.NumOfNodeInLayer == null) {
			return false;
		}
		for (int i = 1; i < this.NumOfLayer; ++i) { // layer 0 is input layer,
													// so it need no gain
													// function
			if (nodegains.get(i - 1).size() < this.NumOfNodeInLayer.get(i)) {
				return false;
			}
		}
		this.NodeGains = nodegains;

		// insert a list of gain function for the input layer
		// 归一化方法，将输入的各组参数归一至0~1的范围
		IntfActFunc fc = new IntfActFunc() {

			@Override
			public Double IAFExecutor(Double inDate) {
				// TODO Auto-generated method stub
				return inDate / 20;
			}

			@Override
			public Double IAFDiffer(Double inDate) {
				// TODO Auto-generated method stub
				return new Double(0);
			}

		};
		// 插入输入层的归一化方法
		this.NodeGains.add(0, new ArrayList<IntfActFunc>());
		for (int i = 0; i < this.NumOfNodeInLayer.get(0); ++i) {
			this.NodeGains.get(0).add(fc);
		}
		return true;
	}

	/**
	 * test with a list of cases 神经元网络测试运行
	 * 
	 * @param tests
	 * @return the matched rate
	 */
	public double testNet(ArrayList<String> tests) {
		if (tests.size() == 0) {
			return -1;
		}
		int cot = 0;
		for (int i = 0; i < tests.size(); ++i) {

			// 剔除不符合规范的测试用例
			String[] strs = tests.get(i).split(this.splitMark);
			if (strs.length != this.NumOfProperty + 1) {
				continue;
			}
			double[] vals = new double[this.NumOfProperty + 1];
			try {
				for (int j = 0; j < strs.length; ++j) {
					vals[j] = Double.parseDouble(strs[j]);
				}
				// 判断输出结果是否正确并记录
				if (this.caseRunner(vals) && this.compResult(vals)) {
					cot++;
				}
			} catch (Exception e) {
				// go on
			}
		}
		// 返回本次测试拟合率
		return (double) cot / (double) tests.size();
	}

	/**
	 * launch the process of NN train make sure that all methods used to
	 * initialize the system have been executed 启动网络训练
	 */
	public void trainNet() {
		double maxF = 1e+8, oldF;
		while (true) {
			// 若loopTime参数大于等于0，说明当前训练只是依据次数
			// 否则根据最小均方差是否达到限定值limit决定是否停止
			if (this.loopTime == 0 || (this.loopTime < 0 && maxF < this.limit)) {
				break;
			}
			if (this.loopTime > 0) {
				this.loopTime--;
			}
			oldF = maxF;
			maxF = 0;
			for (int i = 0; i < this.trainSet.size(); ++i) {
				// ....push into input layer
				double[] acase = this.trainSet.get(i);
				if (this.caseRunner(acase)) {
					maxF += Math.pow(this.errorRes(acase), 2);
					this.caseUpdate(this.formatRealResult(acase));
				}
			}
			maxF /= this.trainSet.size();
			// 如果均方差出现反弹，则按照预设比例缩小学习参数，放缓学习速率
			if (maxF > oldF) {
				this.rate *= 0.9; // self-adjust
			}
		}
	}

	/**
	 * run a case in this net make sure the length of the case is longer than
	 * the number of input-node 输入一个例子并运行
	 * 
	 * @param acase
	 * @return true when the final result has been set into the output node
	 */
	private boolean caseRunner(double[] acase) {
		// store the input casse into the input node
		if (acase.length <= this.NumOfNodeInLayer.get(0)) { // don't forget the
															// real result
			return false; // failed to run this case
		}
		this.NodeValue[0] = acase.clone(); // 输入层获取归一化处理后的输入参数
		for (int i = 1; i < this.NumOfLayer; ++i) { // 对于每一层
			for (int j = 0; j < this.NumOfNodeInLayer.get(i); ++j) { // 对于每层的每个节点
				double tmp = 0;
				for (int k = 0; k < this.NumOfNodeInLayer.get(i - 1); ++k) { // 计算上一层输出到该节点的加权和
					// 将每个连接着当前节点的链接求取加权和
					tmp += this.NodeGains.get(i - 1).get(k)
							.IAFExecutor(this.NodeValue[i - 1][k])
							* this.WeightOfLink.get(i - 1)[this.NumOfNodeInLayer
									.get(i) * k + j]; // weight*value
				}
				// 将加权和存储于当前节点
				// 之所以不是存储加权和的激励函数结果，是因为便于之后求取该节点的激励函数导数结果
				this.NodeValue[i][j] = tmp;
			}
		}
		// case result
		// 将本次训练结果保存于特定数组，便于接下来方向更新或者结果输出操作
		this.caseres = new double[this.NumOfNodeInLayer
				.get(this.NumOfLayer - 1)];
		for (int i = 0; i < this.NumOfNodeInLayer.get(this.NumOfLayer - 1); ++i) {
			// 将存储的加权和输入激励函数求得结果
			this.caseres[i] = this.NodeGains.get(this.NumOfLayer - 1).get(i)
					.IAFExecutor(this.NodeValue[this.NumOfLayer - 1][i]);
		}

		return true;
	}

	/**
	 * calculate and store the final result into the space of output node
	 * 比较网络输出结果以及真实结果，在输出序列中找到最大值的索引，该索引就是输出结果
	 * @return true if matched
	 */
	private boolean compResult(double[] acase) {
		int index = -1;
		double res = (-1) * (1e+8);
		for (int i = 0; i < this.NumOfNodeInLayer.get(this.NumOfLayer - 1); ++i) {
			if (this.caseres[i] > res) {
				res = this.caseres[i];
				index = i;
			}
		}
		return Math.abs(index - acase[acase.length - 1]) < 1e-6 ? true : false;
	}

	/**
	 * set the rate of learning and the limitation
	 * @param rt 学习速率
	 * @param lt 限定值
	 * @param loop 循环次数
	 */
	public void setUrAndLt(double rt, double lt, int loop) {
		if (rt < 0 || (loop < 0 && lt <= 0)) {
			return;
		}
		this.rate = rt;
		this.limit = lt;
		this.loopTime = loop;
	}
	
	private double hitV = 0.85; // 目标特征的归一化值
	private double nohitV = 0.05; // 错误特征的归一化值
	
	/**
	 * convert the representation of the real result
	 * 将真实结果归一化，用于反向更新
	 * 归一为10个节点组成的数组，索引为实际正确值的单元置为 hitV，其余为 nohitV
	 * @param acase
	 * @return
	 */
	private double[] formatRealResult(double[] acase) {
		double[] res = new double[this.NumOfNodeInLayer
				.get(this.NumOfLayer - 1)];
		for (int i = 0; i < res.length; ++i) {
			res[i] = this.nohitV; // not hit
		}
		int index = (int) acase[acase.length - 1];
		if (index > 10 || index < 0) {
			return null;
		}
		res[index] = this.hitV; // hit
		// res[index] = (double) index / 10.0; // good job, full point
		return res;
	}

	/**
	 * 计算当前运行输出值与真实值的累积差值
	 * @param acase
	 * @return
	 */
	private double errorRes(double[] acase) {
		double err = 0;
		int hit = (int) acase[this.NumOfNodeInLayer.get(this.NumOfLayer - 1)];
		for (int i = 0; i < this.NumOfNodeInLayer.get(this.NumOfLayer - 1); ++i) {
			err += Math.abs(this.caseres[i] - (hit == i ? this.hitV : this.nohitV));
		}
		return err;
	}

	/**
	 * update the weight of each link
	 * 反向更新方法，根据每一次的样本运行结果进行反向链接权值更新
	 * @param realres
	 *            should come from the output of formatRealResult
	 */
	private double caseUpdate(double[] realres) {
		if (realres == null) {
			return 0;
		}
		double maxF = 0;
		IntfActFunc func;
		
		// prepare the diff in output node
		// 根据(y-a)*g'(in) 计算输出层的误差值并存储在每个输出层节点
		for (int i = 0; i < this.NumOfNodeInLayer.get(this.NumOfLayer - 1); ++i) {
			func = this.NodeGains.get(this.NumOfLayer - 1).get(i);
			this.NodeValue[this.NumOfLayer - 1][i] = (realres[i] - this.caseres[i])
					* func.IAFDiffer(this.NodeValue[this.NumOfLayer - 1][i]);
		}
		
		// update links in each layer and store the diff into each node
		// 从输出层向上逐层传递误差值并更新链接权值
		for (int i = this.NumOfLayer - 2; i >= 0; --i) { // 对于每一层
			for (int j = 0; j < this.NumOfNodeInLayer.get(i); ++j) { // 对于每层的每个节点
				func = this.NodeGains.get(i).get(j); // 获取当前节点的激励函数以及导数
				double resdiff = 0;
				for (int k = 0; k < this.NumOfNodeInLayer.get(i + 1); ++k) { // 累加计算下层各节点与当前节点连接加权误差和的
					
					resdiff += this.NodeValue[i + 1][k]
							* this.WeightOfLink.get(i)[this.NumOfNodeInLayer
									.get(i + 1) * j + k];
					// update the weight of link
					// 一根链接参与计算之后则进行更新  
					// 更新量df为学习率*当前节点激励函数输出值*下层节点误差值
					double df = this.rate
							* func.IAFExecutor(this.NodeValue[i][j])
							* this.NodeValue[i + 1][k];
					if (Math.abs(df) > maxF) { // 统计最大更新量
						maxF = Math.abs(df);
					}
					
					// 更新当前链接权值
					this.WeightOfLink.get(i)[this.NumOfNodeInLayer.get(i + 1)
							* j + k] += df;
				}
				
				resdiff *= func.IAFDiffer(this.NodeValue[i][j]); // update value
																// ready
				// 当前节点存储误差值，完成误差传递
				this.NodeValue[i][j] = resdiff;
			}
		}
		// 返回本次更新最大权值更新差量
		return maxF;
	}
}
