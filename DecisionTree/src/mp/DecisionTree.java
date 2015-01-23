package mp;

import java.util.*;

/**
 * Decision Tree
 * 
 * @author ZAK
 *
 */
public class DecisionTree {
	private String sp = null; // a symbol used to split each property
	private int numOfItem = -1; // number of property
	private int itemForComp = -1; // property for matching test
	private ArrayList<String[]> itemStates = null;
	private double threshold = 0; // stop growing if rate of entropy gain
									// smaller than threshold
	private int rawNum = 0; // number of case used for training

	private ArrayList<ArrayList<String>> raws = null; // list of original source
	private DTreeNode root = null; // root of the entire tree
	private int timeOfTest = 0; // total tests
	private int testError = 0; // total case error
	private int testPass = 0; // total case passed

	private ArrayList<LRPair<Double, Double>> recordMRate = null; // Record of test <pass, cases>
	
	/**
	 * �����������������ĳ˻�
	 * @param r
	 * @return
	 */
	public static double perLog(double r) {
		if (r < 1E-8) {
			return 0;
		}
		return r * Math.log(r);
	}
	
	/**
	 * ���캯��
	 */
	public DecisionTree() {
		this.recordMRate = new ArrayList<LRPair<Double,Double>>();
	}
	/**
	 * reset original resource
	 * �ͷ�ѵ������Դ
	 */
	public void sourceFree() {
		this.raws = null;
	}

	/**
	 * Set the data for training
	 * ��ȡԭʼѵ�������飬��ÿһ�����ݰ��������иȻ��洢���ں����ľ���������
	 * ͬʱ��Ϊÿһ������ͳ�����п��ܵ�ֵ������һ��String����
	 * @param strs	ԭʼ����
	 * @param compItem	��������Ŀ������
	 * @param itemNum	���ݵ���������Ŀ
	 * @param sp	�����Լ�ķָ���
	 * @return	����ɹ�����true
	 */
	public boolean initTrainData(ArrayList<String> strs, int compItem,
			int itemNum, String sp) {
		if (strs == null || compItem < 0 || itemNum < 1 || compItem >= itemNum) {
			// illegal parameters
			return false;
		}
		this.sp = sp;
		this.raws = new ArrayList<ArrayList<String>>();
		this.itemForComp = compItem;
		this.numOfItem = itemNum;
		this.itemStates = new ArrayList<String[]>();
		ArrayList<HashSet<String>> as = new ArrayList<HashSet<String>>(); // ������ʱͳ�Ƹ������Կ��ܵ�ֵ��Y N ����
		// pre-analyze the input data to initialize itemStates
		for (int i = 0; i < this.numOfItem; ++i) {
			as.add(new HashSet<String>());
		}
		for (int i = 0; i < strs.size(); ++i) { // ����ÿһ��ѵ������
			String[] tmpstrs = strs.get(i).split(this.sp);
			// ���һ�����ݵ�������Ŀ��ָ���Ĳ�һ����ô����
			if (tmpstrs.length != this.numOfItem) { // the number of property is
													// incorrect
				continue;
			}
			// add to raws
			this.raws.add(new ArrayList<String>(Arrays.asList(tmpstrs))); // ѹ���зֺõ�����
			for (int j = 0; j < tmpstrs.length; ++j) {
				// ���������ݵĵ�j�����Լ�������ͳ�ƶ�Ӧ����ֵ��SET���ظ�ֵ�ᱻȥ����
				as.get(j).add(tmpstrs[j]);
			}
		}
		// ��ͳ�Ƶõ��ĸ�����ֵ�ļ���ת��String������д洢���ں�����ѯ
		for (int i = 0; i < as.size(); ++i) {
			this.itemStates.add((String[]) (as.get(i).toArray(new String[0])));
		}
		return true;
	}

	/**
	 * ����������
	 * training with the certain threshold
	 * @param numofcase	���ɾ�����ʵ��ʹ�õ�ѵ������С
	 * @param threshold	���������������
	 * @return
	 */
	public boolean treeTraining(int numofcase, double threshold) {
		this.rawNum = numofcase;
		this.threshold = threshold * (1E-3); // �����Threshold����0.001��Ϊʵ�ʵ�Thresholdֵ
		return this.treeTraining();
	}

	/**
	 * get a set of data to build up the tree make sure that setTrainData has
	 * been invoked	 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean treeTraining() {
		if (this.raws == null) { // raws contains a ArrayList<String>
			return false;
		}
		boolean[] canUse = new boolean[this.numOfItem]; // ��־��������ȷ����ǰ��Щ����δ��ʹ��
		for (int i = 0; i < this.numOfItem; ++i) { // initialize
			canUse[i] = true; // ��ʼ������Ϊÿ�����Ծ�����
		}
		// ������������жϵĽ������
		canUse[this.itemForComp] = false; // avoid the target property
		this.root = this.createSubTree(
				canUse,
				new ArrayList<ArrayList<String>>(this.raws
						.subList(0, this.rawNum > this.raws.size() // ���ָ����ѵ��������Ŀ����ʵ������Ŀ��
								|| this.rawNum < 1 ? this.raws.size() // ��ôȡ����ѵ��������ѵ��
								: this.rawNum)), 
				this.calculateForInfo(this.raws, this.itemForComp)); // ����������Ϊ���ѵ��������Ϣ��
		return true;
	}

	/**
	 * create a subTree
	 * ����һ�������������������ڵ�
	 * @param canUse 	mark which property is not used
	 * @param cases
	 * @param parInfo 	entropy of previous node
	 * @return sub-tree node
	 */
	private DTreeNode createSubTree(boolean[] canUse,
			ArrayList<ArrayList<String>> cases, double parInfo) {
		if (parInfo == 0) { // pure or empty �����������ݼ���Ϣ��Ϊ0��˵�����ݼ��Դ�������Ϊ��
			if (cases.size() > 0) { // pure ���ݼ���Ϊ����˵���Ѵ���
				for (int i = 0; i < this.itemStates.get(this.itemForComp).length; ++i) {
					if (this.itemStates.get(this.itemForComp)[i].equals(cases
							.get(0).get(this.itemForComp))) {
						return new DTreeNode(this.itemForComp, i);
					}
				}
			}
			return null;
		}
		// System.out.println("Tree is growing");
		// choose the max rate, rate = Gains/splitInfo
		ArrayList<ArrayList<ArrayList<String>>> subArr = null; // Ϊ��ǰ���Ե�ÿһ��ֵ�ֱ𱣴���������ݼ�
		double[] subInfo = null;	 // Ϊ��ǰ���Ե�ÿһ��ֵ�������ݼ�����Ϣ��
		double upRate = 0;			// �����Ϣ��������
		int subItem = -1;			// ѡ�����ڵ�ǰ�ڵ��������ԣ��ǵڼ������ԣ�
		for (int i = 0; i < this.numOfItem; ++i) { // for each property ����ÿһ������
			if (!canUse[i]) { // if this property has not been used before ����������ѱ�ʹ�����Թ�
				continue;
			}
			// ��ǰ���Ե�����ֵ��ɵ�String���飨��ʱ���棩
			String[] tmpState = this.itemStates.get(i); // obtain all kinds of
														// state of this
														// property
			// ���ڴ�ŵ�ǰ����ÿһ��ֵ���������ݼ�
			ArrayList<ArrayList<ArrayList<String>>> tmpArr = new ArrayList<ArrayList<ArrayList<String>>>(
					tmpState.length); // tmpState.length equals to the number of
										// properties of the No.i attribute
			for (int j = 0; j < tmpState.length; ++j) {
				tmpArr.add(new ArrayList<ArrayList<String>>());
			}
			// divide cases into several parts based on the certain property
			// �Դ�������ݼ��������ݽ��м�Ⲣ���ݵ�ǰ����ֵ����
			for (int j = 0; j < cases.size(); ++j) { // for each case
				for (int k = 0; k < tmpState.length; ++k) { // search for the
															// state of property
															// matched by this
															// case
					// ȷ��ÿ�����ݵ�ǰ���ԣ���i�����ԣ���ֵ����һ������ֵ
					if (tmpState[k].equals(cases.get(j).get(i))) {
						// add the case of No.k subState of No.i property to
						// temporary-array
						// ��������Ӧ����ֵ����Ͻ�����ݼ�
						tmpArr.get(k).add(cases.get(j));
						break;
					}
				}
			}
			// calculate the gain and info for this property
			// Ϊ��ǰ���Եķ��������������Լ����������ڶԱ���֪��ѽ�������������
			double gain = 0; 		// ����
			double splitInfo = 0;	// �����أ�һ�����Կ��ܵ�ֵԽ�������������Խ��
			double[] tmpInfo = new double[tmpState.length]; // �洢��ǰ���Ե�ÿ������ֵ��Ͻ�����ݼ�����Ϣ��
			for (int j = 0; j < tmpState.length; ++j) {
				// calculating E(m/n)*Info(j)
				tmpInfo[j] = this.calculateForInfo(tmpArr.get(j),
						this.itemForComp);
				gain += tmpInfo[j]
						* ((double) tmpArr.get(j).size() / (double) cases
								.size());
			}
			// real gain
			gain = parInfo - gain; // ���ʵ�ʵ���Ϣ������
			// get splitInfo ���������
			for (int j = 0; j < tmpState.length; ++j) {
				double r = (double) tmpArr.get(j).size()
						/ (double) cases.size();
				splitInfo += DecisionTree.perLog(r);
			}
			// attention: splitInfo is a negative number
			// �������� 1 �ǿ��ǵ������ؿ���Ϊ0
			double tmpRate = gain / (Math.abs(splitInfo) + 1); // splitInfo
																	// may be
																	// zero, add
																	// 1 to
																	// avoid it
			// compare with previous rate ����֪��ѽ���Աȣ����������
			if (upRate < tmpRate) { // update if better
				upRate = tmpRate;
				subInfo = tmpInfo;
				subArr = tmpArr;
				subItem = i;
			}
		}
		// sub-tree developing
		// ���û�п�ѡ���Ի��������Ϣ��������С��ָ����ֵ���򽫵�ǰ���ݼ��Ա����ԣ����絳�ɣ�ռ������ֵ��ΪҶ�ӽڵ�ֵ
		if (subItem == -1 || upRate < this.threshold) { // no unused property or
														// meet the threshold
														// build the leave of
														// tree
			// ���ڷֱ�ͳ�ƶԱ����Ը���ֵ��case��Ŀ
			int[] stateCot = new int[this.itemStates.get(this.itemForComp).length]; // initialize
																					// with
																					// 0
			for (int i = 0; i < cases.size(); ++i) { // find the sub-state which
														// is the most popular
														// one
				for (int j = 0; j < stateCot.length; ++j) {
					if (cases.get(i).get(this.itemForComp)
							.equals(this.itemStates.get(this.itemForComp)[j])) {
						stateCot[j]++;
						break;
					}
				}
			}
			// �ҳ���Ŀ��������ֵ����ȡ������
			int maxpos = 0; // find the most suitable state
			for (int i = 0; i < stateCot.length; ++i) {
				if (stateCot[i] > stateCot[maxpos]) {
					maxpos = i;
				}
			}
			// �������ɵ�Ҷ�ӽڵ�
			return new DTreeNode(this.itemForComp, maxpos);
		} else { // build a sub-tree ��������
			cases = null; // free this array �ͷ�������ϵͳ����
			ArrayList<DTreeNode> trees = new ArrayList<DTreeNode>(); // ���������Ÿ�������ֵ������
			boolean[] subCanUse = canUse.clone(); // ��ȡ��������ֵ���
			subCanUse[subItem] = false; // ����ǰ������Ϊ������
			// �ݹ�����ÿ�����������������ڵ�����
			for (int i = 0; i < subArr.size(); ++i) { // build up and get a
														// array of sub-tree
														// recursively
				trees.add(this.createSubTree(subCanUse, subArr.get(i),
						subInfo[i]));
			}
			// ���ɲ����������ڵ�
			return new DTreeNode(subItem,
					(DTreeNode[]) (trees.toArray(new DTreeNode[0])));
		}
	}

	/**
	 * ���ڼ����������ݼ��ĸ��������µ���Ϣ��
	 * @return entropy of the state of a set of case
	 */
	private double calculateForInfo(ArrayList<ArrayList<String>> cases,
			int tItem) {
		if (cases.size() == 0) { // if empty
			return 0;
		}
		String[] tmpState = this.itemStates.get(tItem);
		int[] tmpStateNum = new int[tmpState.length];
//		for (int i = 0; i < tmpStateNum.length; ++i) { // initialize
//			tmpStateNum[i] = 0;
//		}
		// ͳ�Ƹ�������ֵ��case��Ŀ
		for (int i = 0; i < cases.size(); ++i) {
			for (int j = 0; j < tmpState.length; ++j) {
				if (tmpState[j].equals(cases.get(i).get(tItem))) {
					tmpStateNum[j]++;
					break;
				}
			}
		}
		// ������Ϣ�ز�����
		double res = 0;
		for (int i = 0; i < tmpStateNum.length; ++i) {
			double r = (double) tmpStateNum[i] / (double) cases.size(); // obtain
																		// the
																		// probability
			// System.out.println("r->"+r);
			res += DecisionTree.perLog(r);
			// System.out.println("res-->"+res);
		}
		return Math.abs(res);
	}

	/**
	 * run a set of test case
	 * ������Ĳ������ݼ����о�����
	 * @param tests
	 * @return matched rate
	 */
	public double treeTest(ArrayList<String> tests) {
		if (tests == null) {
			return -1;
		}
		// ͳ�Ʊ��β��Ե�case��Ŀ�Լ���ȷ��
		int cases = 0, pass = 0;
		for (int i = 0; i < tests.size(); ++i) {
			// System.out.println("Test# "+i);
			switch (this.singleTest(tests.get(i).split(this.sp))) {
			case 0: {
				cases++; // how many case
				pass++; // the case matched
				this.timeOfTest++;
				this.testPass++;
				break;
			}
			case 1: {
				cases++;
				this.timeOfTest++;
				break;
			}
			default: {
				this.testError++;
			}
			}
		}
		if (cases != 0) { // record of each test
			this.recordMRate.add(new LRPair<Double, Double>((double)pass, (double)cases));
		}
		return cases != 0 ? (double) pass / (double) cases : -1;
	}

	/**
	 * single case to test
	 * �Ե���case���в���
	 * @param item
	 * @return true if matched
	 */
	private int singleTest(String[] item) {
		if (item.length != this.numOfItem) {
			return 4; // error, the number of property of this case doesn't
						// match
		}
		DTreeNode ptr = this.root;
		// ��ÿ���ڵ�����жϲ����뵽��һ��ֱ��Ҷ�ӽڵ�
		while (ptr.getLevelItem() != this.itemForComp) { // �����ǰ�ڵ㲻��Ҷ�ӽڵ�
			String[] tmpState = this.itemStates.get(ptr.getLevelItem()); // ��ȡ��ǰ�ڵ����Ե�����ֵ����
			int i = 0;
			for (i = 0; i < tmpState.length; ++i) { // ȷ����case�Ķ�Ӧ���Ե�����ֵ����һ��
				if (tmpState[i].equals(item[ptr.getLevelItem()])) {
					ptr = ptr.getSubSon(i);
					break;
				}
			}
			if (i == tmpState.length) { // ��ǰcase������ֵδ����¼�ڰ�
				return 1;
				// return 2; // unknown state of No.i property
			} else if (ptr == null) { // �����ڵ�ȱʧ
				// System.out.println("ERROR: No SubNode");
				// return 3;
				return 1;
			}
		}
		// �ݹ�ִ�У������ȷ���� 0 ���򷵻� 1
		return (this.itemStates.get(ptr.getLevelItem()))[ptr.getLeaveState()]
				.equals(item[this.itemForComp]) ? 0 : 1;
	}

	/**
	 * show a list of record
	 * 
	 * @return null if no test yet
	 */
	public Double getMatchedRate() {
		if (this.timeOfTest == 0) {
			return null;
		}
		return Double.valueOf(this.testPass / this.timeOfTest);
	}

	/**
	 * Print out the entire tree
	 */
	public void treePrintOut() {
		this.nodePrint(this.root, 0);
	}

	/**
	 * print out the entire tree recursively
	 * ��ӡ������
	 * @param header
	 * @param level
	 */
	private void nodePrint(DTreeNode header, int level) {
		if (header == null) {
			return;
		}
		String fg = "";
		System.out.println(fg + ">NO." + header.getLevelItem());
		for (int i = 0; i < level; ++i) {
			fg += "--";
		}
		if (header.getLevelItem() == this.itemForComp) {
			System.out.println(fg
					+ ">"
					+ this.itemStates.get(header.getLevelItem())[header
							.getLeaveState()]);
		} else {
			for (int i = 0; i < this.itemStates.get(header.getLevelItem()).length; ++i) {
				System.out.print(fg + ">"
						+ this.itemStates.get(header.getLevelItem())[i]);
				this.nodePrint(header.getSubSon(i), level + 1);
			}
		}
	}

	@Override
	public String toString() {
		int aveTC = 0, avePS = 0;
		for (int i = 0; i < this.recordMRate.size(); ++i) {
			avePS += this.recordMRate.get(i).getFirst();
			aveTC += this.recordMRate.get(i).getSecond();
		}
		avePS /= this.recordMRate.size() != 0 ? this.recordMRate.size() : 1;
		aveTC /= this.recordMRate.size() != 0 ? this.recordMRate.size() : 1;
		return "There are " + this.numOfItem + " kinds of property"
				+ "\nAverage Test Cases: " + aveTC
				+ "\nAverage Cases Passed: " + avePS
				+ "\nTotal Test Cases: " + this.timeOfTest
				+ "\nTotal Cases Passed: " + this.testPass;
	}

	/**
	 * Tree Node of Decision Tree
	 * �������ڵ���
	 * @author ZAK
	 *
	 */
	private class DTreeNode {
		// �ýڵ������
		private int levelItem = -1; // index of attribute(property)
		// �������һ��Ҷ�ӽڵ㣬��ñ�����Ӧһ����levelItem���Եģ�����ֵ
		private int leaveState = -1; // index of state of the attribute if it's
										// a leave node
		// ��������������������洢��ǰ�ڵ������¼�����
		private DTreeNode[] sons = null; // sub-tree of this node

		/**
		 * used to build up a leave node
		 * ����Ҷ�ӽڵ㹹��
		 * @param subItem
		 * @param ls
		 */
		public DTreeNode(int subItem, int ls) {
			this.levelItem = subItem;
			this.leaveState = ls;
		}

		/**
		 * used to build up a sub-tree
		 * ���������ڵ㹹��
		 * @param subItem
		 * @param dtns
		 */
		public DTreeNode(int subItem, DTreeNode... dtns) {
			this.levelItem = subItem;
			this.sons = dtns;
		}

		/**
		 * get a sub-node based on the index of the property
		 * �������������ֵ�������ض�Ӧ���¼������ڵ�
		 * @param stateIndex
		 * @return
		 */
		public DTreeNode getSubSon(int stateIndex) {
			if (sons == null) {
				return null;
			}
			return this.sons[stateIndex];
		}
		/**
		 * ��ȡ�ڵ����ԣ�������
		 * @return
		 */
		public int getLevelItem() {
			return this.levelItem;
		}
		/**
		 * ��ȡҶ�ӽڵ�����ֵ��������
		 * @return
		 */
		public int getLeaveState() {
			return this.leaveState;
		}
	}
}
