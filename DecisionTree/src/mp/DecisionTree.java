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
	 * 计算概率与自身对数的乘积
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
	 * 构造函数
	 */
	public DecisionTree() {
		this.recordMRate = new ArrayList<LRPair<Double,Double>>();
	}
	/**
	 * reset original resource
	 * 释放训练集资源
	 */
	public void sourceFree() {
		this.raws = null;
	}

	/**
	 * Set the data for training
	 * 获取原始训练集数组，将每一条数据按照属性切割，然后存储用于后续的决策树构建
	 * 同时，为每一种属性统计所有可能的值，存入一个String数组
	 * @param strs	原始数据
	 * @param compItem	决策树的目标属性
	 * @param itemNum	数据的总属性数目
	 * @param sp	各属性间的分隔符
	 * @return	如果成功返回true
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
		ArrayList<HashSet<String>> as = new ArrayList<HashSet<String>>(); // 用于临时统计各个属性可能的值（Y N ？）
		// pre-analyze the input data to initialize itemStates
		for (int i = 0; i < this.numOfItem; ++i) {
			as.add(new HashSet<String>());
		}
		for (int i = 0; i < strs.size(); ++i) { // 对于每一条训练数据
			String[] tmpstrs = strs.get(i).split(this.sp);
			// 如果一条数据的属性数目和指定的不一致那么舍弃
			if (tmpstrs.length != this.numOfItem) { // the number of property is
													// incorrect
				continue;
			}
			// add to raws
			this.raws.add(new ArrayList<String>(Arrays.asList(tmpstrs))); // 压入切分好的数据
			for (int j = 0; j < tmpstrs.length; ++j) {
				// 将该条数据的第j项属性加入用于统计对应属性值的SET（重复值会被去掉）
				as.get(j).add(tmpstrs[j]);
			}
		}
		// 将统计得到的各属性值的集合转成String数组进行存储便于后续查询
		for (int i = 0; i < as.size(); ++i) {
			this.itemStates.add((String[]) (as.get(i).toArray(new String[0])));
		}
		return true;
	}

	/**
	 * 决策树生成
	 * training with the certain threshold
	 * @param numofcase	生成决策树实际使用的训练集大小
	 * @param threshold	用于限制树的深度
	 * @return
	 */
	public boolean treeTraining(int numofcase, double threshold) {
		this.rawNum = numofcase;
		this.threshold = threshold * (1E-3); // 输入的Threshold乘以0.001作为实际的Threshold值
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
		boolean[] canUse = new boolean[this.numOfItem]; // 标志数组用于确定当前哪些属性未被使用
		for (int i = 0; i < this.numOfItem; ++i) { // initialize
			canUse[i] = true; // 初始化，认为每个属性均可用
		}
		// 除了用于最后判断的结果属性
		canUse[this.itemForComp] = false; // avoid the target property
		this.root = this.createSubTree(
				canUse,
				new ArrayList<ArrayList<String>>(this.raws
						.subList(0, this.rawNum > this.raws.size() // 如果指定的训练数据数目多余实际总数目，
								|| this.rawNum < 1 ? this.raws.size() // 那么取整个训练集进行训练
								: this.rawNum)), 
				this.calculateForInfo(this.raws, this.itemForComp)); // 第三个参数为这个训练集的信息熵
		return true;
	}

	/**
	 * create a subTree
	 * 构建一颗子树并返回子树根节点
	 * @param canUse 	mark which property is not used
	 * @param cases
	 * @param parInfo 	entropy of previous node
	 * @return sub-tree node
	 */
	private DTreeNode createSubTree(boolean[] canUse,
			ArrayList<ArrayList<String>> cases, double parInfo) {
		if (parInfo == 0) { // pure or empty 如果传入的数据集信息熵为0，说明数据集以纯化或者为空
			if (cases.size() > 0) { // pure 数据集不为空则说明已纯化
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
		ArrayList<ArrayList<ArrayList<String>>> subArr = null; // 为当前属性的每一种值分别保存分类后的数据集
		double[] subInfo = null;	 // 为当前属性的每一种值保存数据集的信息熵
		double upRate = 0;			// 最大信息熵增益率
		int subItem = -1;			// 选作用于当前节点分类的属性（是第几项属性）
		for (int i = 0; i < this.numOfItem; ++i) { // for each property 对于每一项属性
			if (!canUse[i]) { // if this property has not been used before 如果该属性已被使用则略过
				continue;
			}
			// 当前属性的所有值组成的String数组（临时保存）
			String[] tmpState = this.itemStates.get(i); // obtain all kinds of
														// state of this
														// property
			// 用于存放当前属性每一种值分类后的数据集
			ArrayList<ArrayList<ArrayList<String>>> tmpArr = new ArrayList<ArrayList<ArrayList<String>>>(
					tmpState.length); // tmpState.length equals to the number of
										// properties of the No.i attribute
			for (int j = 0; j < tmpState.length; ++j) {
				tmpArr.add(new ArrayList<ArrayList<String>>());
			}
			// divide cases into several parts based on the certain property
			// 对传入的数据集逐条数据进行检测并根据当前属性值分类
			for (int j = 0; j < cases.size(); ++j) { // for each case
				for (int k = 0; k < tmpState.length; ++k) { // search for the
															// state of property
															// matched by this
															// case
					// 确认每条数据当前属性（第i项属性）的值是哪一种属性值
					if (tmpState[k].equals(cases.get(j).get(i))) {
						// add the case of No.k subState of No.i property to
						// temporary-array
						// 分类放入对应属性值所下辖的数据集
						tmpArr.get(k).add(cases.get(j));
						break;
					}
				}
			}
			// calculate the gain and info for this property
			// 为当前属性的分类结果计算增益以及增益率用于对比已知最佳结果，更好则更新
			double gain = 0; 		// 增益
			double splitInfo = 0;	// 分组熵（一个属性可能的值越多则该熵倾向于越大）
			double[] tmpInfo = new double[tmpState.length]; // 存储当前属性的每种属性值下辖的数据集的信息熵
			for (int j = 0; j < tmpState.length; ++j) {
				// calculating E(m/n)*Info(j)
				tmpInfo[j] = this.calculateForInfo(tmpArr.get(j),
						this.itemForComp);
				gain += tmpInfo[j]
						* ((double) tmpArr.get(j).size() / (double) cases
								.size());
			}
			// real gain
			gain = parInfo - gain; // 获得实际的信息熵增益
			// get splitInfo 计算分类熵
			for (int j = 0; j < tmpState.length; ++j) {
				double r = (double) tmpArr.get(j).size()
						/ (double) cases.size();
				splitInfo += DecisionTree.perLog(r);
			}
			// attention: splitInfo is a negative number
			// 额外增加 1 是考虑到分类熵可能为0
			double tmpRate = gain / (Math.abs(splitInfo) + 1); // splitInfo
																	// may be
																	// zero, add
																	// 1 to
																	// avoid it
			// compare with previous rate 与已知最佳结果对比，更好则更新
			if (upRate < tmpRate) { // update if better
				upRate = tmpRate;
				subInfo = tmpInfo;
				subArr = tmpArr;
				subItem = i;
			}
		}
		// sub-tree developing
		// 如果没有可选属性或者最佳信息熵增益率小于指定阈值，则将当前数据集对比属性（比如党派）占多数的值设为叶子节点值
		if (subItem == -1 || upRate < this.threshold) { // no unused property or
														// meet the threshold
														// build the leave of
														// tree
			// 用于分别统计对比属性各种值的case数目
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
			// 找出数目最多的属性值，获取其索引
			int maxpos = 0; // find the most suitable state
			for (int i = 0; i < stateCot.length; ++i) {
				if (stateCot[i] > stateCot[maxpos]) {
					maxpos = i;
				}
			}
			// 返回生成的叶子节点
			return new DTreeNode(this.itemForComp, maxpos);
		} else { // build a sub-tree 生成子树
			cases = null; // free this array 释放引用让系统回收
			ArrayList<DTreeNode> trees = new ArrayList<DTreeNode>(); // 构建数组存放各个属性值的子树
			boolean[] subCanUse = canUse.clone(); // 获取可用属性值深拷贝
			subCanUse[subItem] = false; // 将当前属性设为不可用
			// 递归生成每个子树并加入子树节点数组
			for (int i = 0; i < subArr.size(); ++i) { // build up and get a
														// array of sub-tree
														// recursively
				trees.add(this.createSubTree(subCanUse, subArr.get(i),
						subInfo[i]));
			}
			// 生成并返回子树节点
			return new DTreeNode(subItem,
					(DTreeNode[]) (trees.toArray(new DTreeNode[0])));
		}
	}

	/**
	 * 用于计算输入数据集的给定属性下的信息熵
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
		// 统计各个属性值的case数目
		for (int i = 0; i < cases.size(); ++i) {
			for (int j = 0; j < tmpState.length; ++j) {
				if (tmpState[j].equals(cases.get(i).get(tItem))) {
					tmpStateNum[j]++;
					break;
				}
			}
		}
		// 计算信息熵并返回
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
	 * 对输入的测试数据集运行决策树
	 * @param tests
	 * @return matched rate
	 */
	public double treeTest(ArrayList<String> tests) {
		if (tests == null) {
			return -1;
		}
		// 统计本次测试的case数目以及正确率
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
	 * 对单个case进行测试
	 * @param item
	 * @return true if matched
	 */
	private int singleTest(String[] item) {
		if (item.length != this.numOfItem) {
			return 4; // error, the number of property of this case doesn't
						// match
		}
		DTreeNode ptr = this.root;
		// 在每个节点进行判断并深入到下一层直到叶子节点
		while (ptr.getLevelItem() != this.itemForComp) { // 如果当前节点不是叶子节点
			String[] tmpState = this.itemStates.get(ptr.getLevelItem()); // 获取当前节点属性的属性值数组
			int i = 0;
			for (i = 0; i < tmpState.length; ++i) { // 确定该case的对应属性的属性值是哪一个
				if (tmpState[i].equals(item[ptr.getLevelItem()])) {
					ptr = ptr.getSubSon(i);
					break;
				}
			}
			if (i == tmpState.length) { // 当前case的属性值未曾记录在案
				return 1;
				// return 2; // unknown state of No.i property
			} else if (ptr == null) { // 后续节点缺失
				// System.out.println("ERROR: No SubNode");
				// return 3;
				return 1;
			}
		}
		// 递归执行，如果正确返回 0 否则返回 1
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
	 * 打印决策树
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
	 * 决策树节点类
	 * @author ZAK
	 *
	 */
	private class DTreeNode {
		// 该节点的属性
		private int levelItem = -1; // index of attribute(property)
		// 如果这是一个叶子节点，则该变量对应一个（levelItem属性的）属性值
		private int leaveState = -1; // index of state of the attribute if it's
										// a leave node
		// 如果是子树根，则该数组存储当前节点所有下级子树
		private DTreeNode[] sons = null; // sub-tree of this node

		/**
		 * used to build up a leave node
		 * 用于叶子节点构造
		 * @param subItem
		 * @param ls
		 */
		public DTreeNode(int subItem, int ls) {
			this.levelItem = subItem;
			this.leaveState = ls;
		}

		/**
		 * used to build up a sub-tree
		 * 用于子树节点构造
		 * @param subItem
		 * @param dtns
		 */
		public DTreeNode(int subItem, DTreeNode... dtns) {
			this.levelItem = subItem;
			this.sons = dtns;
		}

		/**
		 * get a sub-node based on the index of the property
		 * 根据输入的属性值索引返回对应的下级子树节点
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
		 * 获取节点属性（索引）
		 * @return
		 */
		public int getLevelItem() {
			return this.levelItem;
		}
		/**
		 * 获取叶子节点属性值（索引）
		 * @return
		 */
		public int getLeaveState() {
			return this.leaveState;
		}
	}
}
