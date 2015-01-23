package mp;

import java.util.ArrayList;

/**
 * Gain Function Interface
 * @author ZAK
 *
 */
interface IntfActFunc {
	/**
	 * calculate the active value for the certain node ��Ԫ���������ӿڷ���
	 * 
	 * @param inDate
	 * @return
	 */
	public Double IAFExecutor(Double inDate);

	/**
	 * this function is the differential style of the IAFExecutor ��Ԫ����������������
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

	private double rate = 1; // the rate of training ѧϰ����
	private double limit = 1e-4; // stop training if meet it ��С�������޶�ֵ
	private int loopTime = 100; // ͬһ��ѵ��������ѭ������

	/**
	 * structure of the entire neural net ����һ��int����ȷ����������Լ�ÿ��Ľڵ���
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
	 * pre-process the train-set ��ʼ��ѵ����
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

				// �޳����Ϲ�������
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
	 * You should know the number of link among these layers ��ʼ������֮�������Ȩֵ
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
			// Ϊÿһ���������ó�ʼȨֵ
			ArrayList<double[]> tmpArr = new ArrayList<double[]>();
			for (int i = 0; i < this.NumOfLayer - 1; ++i) {
				String[] tmpStrs = resw.get(i).split(this.splitMark);
				tmpArr.add(new double[this.NumOfNodeInLayer.get(i)
						* this.NumOfNodeInLayer.get(i + 1)]);
				for (int j = 0; j < tmpArr.get(i).length; ++j) {
					if (tmpStrs.length > j) {
						tmpArr.get(i)[j] = Double.parseDouble(tmpStrs[j]);
					} else { // weight = 0 as default
						tmpArr.get(i)[j] = 0; // Ĭ��ȨֵΪ0
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
	 * randomly initialize the weight of each link ��ʼ��������Ȩֵ������һ��������������ó�ʼȨֵ
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
	 * randomly initialize the weight of each link ʹ��Ĭ�ϵľ���ֵ������ó�ʼȨֵ
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
	 * except the input layer, each node has a gain function ��ʼ��������Ԫ�ļ��������Լ���Ӧ����
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
		// ��һ��������������ĸ��������һ��0~1�ķ�Χ
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
		// ���������Ĺ�һ������
		this.NodeGains.add(0, new ArrayList<IntfActFunc>());
		for (int i = 0; i < this.NumOfNodeInLayer.get(0); ++i) {
			this.NodeGains.get(0).add(fc);
		}
		return true;
	}

	/**
	 * test with a list of cases ��Ԫ�����������
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

			// �޳������Ϲ淶�Ĳ�������
			String[] strs = tests.get(i).split(this.splitMark);
			if (strs.length != this.NumOfProperty + 1) {
				continue;
			}
			double[] vals = new double[this.NumOfProperty + 1];
			try {
				for (int j = 0; j < strs.length; ++j) {
					vals[j] = Double.parseDouble(strs[j]);
				}
				// �ж��������Ƿ���ȷ����¼
				if (this.caseRunner(vals) && this.compResult(vals)) {
					cot++;
				}
			} catch (Exception e) {
				// go on
			}
		}
		// ���ر��β��������
		return (double) cot / (double) tests.size();
	}

	/**
	 * launch the process of NN train make sure that all methods used to
	 * initialize the system have been executed ��������ѵ��
	 */
	public void trainNet() {
		double maxF = 1e+8, oldF;
		while (true) {
			// ��loopTime�������ڵ���0��˵����ǰѵ��ֻ�����ݴ���
			// ���������С�������Ƿ�ﵽ�޶�ֵlimit�����Ƿ�ֹͣ
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
			// �����������ַ���������Ԥ�������Сѧϰ�������Ż�ѧϰ����
			if (maxF > oldF) {
				this.rate *= 0.9; // self-adjust
			}
		}
	}

	/**
	 * run a case in this net make sure the length of the case is longer than
	 * the number of input-node ����һ�����Ӳ�����
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
		this.NodeValue[0] = acase.clone(); // ������ȡ��һ���������������
		for (int i = 1; i < this.NumOfLayer; ++i) { // ����ÿһ��
			for (int j = 0; j < this.NumOfNodeInLayer.get(i); ++j) { // ����ÿ���ÿ���ڵ�
				double tmp = 0;
				for (int k = 0; k < this.NumOfNodeInLayer.get(i - 1); ++k) { // ������һ��������ýڵ�ļ�Ȩ��
					// ��ÿ�������ŵ�ǰ�ڵ��������ȡ��Ȩ��
					tmp += this.NodeGains.get(i - 1).get(k)
							.IAFExecutor(this.NodeValue[i - 1][k])
							* this.WeightOfLink.get(i - 1)[this.NumOfNodeInLayer
									.get(i) * k + j]; // weight*value
				}
				// ����Ȩ�ʹ洢�ڵ�ǰ�ڵ�
				// ֮���Բ��Ǵ洢��Ȩ�͵ļ����������������Ϊ����֮����ȡ�ýڵ�ļ��������������
				this.NodeValue[i][j] = tmp;
			}
		}
		// case result
		// ������ѵ������������ض����飬���ڽ�����������»��߽���������
		this.caseres = new double[this.NumOfNodeInLayer
				.get(this.NumOfLayer - 1)];
		for (int i = 0; i < this.NumOfNodeInLayer.get(this.NumOfLayer - 1); ++i) {
			// ���洢�ļ�Ȩ�����뼤��������ý��
			this.caseres[i] = this.NodeGains.get(this.NumOfLayer - 1).get(i)
					.IAFExecutor(this.NodeValue[this.NumOfLayer - 1][i]);
		}

		return true;
	}

	/**
	 * calculate and store the final result into the space of output node
	 * �Ƚ������������Լ���ʵ�����������������ҵ����ֵ������������������������
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
	 * @param rt ѧϰ����
	 * @param lt �޶�ֵ
	 * @param loop ѭ������
	 */
	public void setUrAndLt(double rt, double lt, int loop) {
		if (rt < 0 || (loop < 0 && lt <= 0)) {
			return;
		}
		this.rate = rt;
		this.limit = lt;
		this.loopTime = loop;
	}
	
	private double hitV = 0.85; // Ŀ�������Ĺ�һ��ֵ
	private double nohitV = 0.05; // ���������Ĺ�һ��ֵ
	
	/**
	 * convert the representation of the real result
	 * ����ʵ�����һ�������ڷ������
	 * ��һΪ10���ڵ���ɵ����飬����Ϊʵ����ȷֵ�ĵ�Ԫ��Ϊ hitV������Ϊ nohitV
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
	 * ���㵱ǰ�������ֵ����ʵֵ���ۻ���ֵ
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
	 * ������·���������ÿһ�ε��������н�����з�������Ȩֵ����
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
		// ����(y-a)*g'(in) �������������ֵ���洢��ÿ�������ڵ�
		for (int i = 0; i < this.NumOfNodeInLayer.get(this.NumOfLayer - 1); ++i) {
			func = this.NodeGains.get(this.NumOfLayer - 1).get(i);
			this.NodeValue[this.NumOfLayer - 1][i] = (realres[i] - this.caseres[i])
					* func.IAFDiffer(this.NodeValue[this.NumOfLayer - 1][i]);
		}
		
		// update links in each layer and store the diff into each node
		// �������������㴫�����ֵ����������Ȩֵ
		for (int i = this.NumOfLayer - 2; i >= 0; --i) { // ����ÿһ��
			for (int j = 0; j < this.NumOfNodeInLayer.get(i); ++j) { // ����ÿ���ÿ���ڵ�
				func = this.NodeGains.get(i).get(j); // ��ȡ��ǰ�ڵ�ļ��������Լ�����
				double resdiff = 0;
				for (int k = 0; k < this.NumOfNodeInLayer.get(i + 1); ++k) { // �ۼӼ����²���ڵ��뵱ǰ�ڵ����Ӽ�Ȩ���͵�
					
					resdiff += this.NodeValue[i + 1][k]
							* this.WeightOfLink.get(i)[this.NumOfNodeInLayer
									.get(i + 1) * j + k];
					// update the weight of link
					// һ�����Ӳ������֮������и���  
					// ������dfΪѧϰ��*��ǰ�ڵ㼤���������ֵ*�²�ڵ����ֵ
					double df = this.rate
							* func.IAFExecutor(this.NodeValue[i][j])
							* this.NodeValue[i + 1][k];
					if (Math.abs(df) > maxF) { // ͳ����������
						maxF = Math.abs(df);
					}
					
					// ���µ�ǰ����Ȩֵ
					this.WeightOfLink.get(i)[this.NumOfNodeInLayer.get(i + 1)
							* j + k] += df;
				}
				
				resdiff *= func.IAFDiffer(this.NodeValue[i][j]); // update value
																// ready
				// ��ǰ�ڵ�洢���ֵ���������
				this.NodeValue[i][j] = resdiff;
			}
		}
		// ���ر��θ������Ȩֵ���²���
		return maxF;
	}
}
