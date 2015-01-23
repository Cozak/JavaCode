package mp;

import javax.swing.JOptionPane;

/**
 * 
 * @author ZAK	Common Algrothm for 256-bit bmp (black-white)
 *
 */
public class MountClimb implements AlgorithmFS {
	private byte[] imageOrg = null; // 传入的图像原图
	private byte[] image = null; // 可以改变并最终输出的图像
	private int bitCount = 0;
	private int pxWidth = 0;
	private int pxHeight = 0;
	private int colorMXH = 0;
	private int lineBytes = 0;
	private int subSize = 10;
	private int circle = 3;
	private double paraH = 0;
	private double paraP = 0.5;
	private double paraN = 3;
	@Override
	public String algName() { // 算法的唯一ID
		// TODO Auto-generated method stub
		return "Mountain Climb<256>";
	}
	@Override
	/**
	 * 执行函数，输出处理后的byte[]
	 */
	public byte[] executeAFS(byte[] imageS, int bitCount, int pxWidth, int pxHeight, int colorMXH, int lineBytes, int type) {
		if (type != 0) {
			return null;
		}
		// parameters 算法执行参数
		this.subSize = 10;	// 局部大小，每次处理的局部为subSize，正方形区域
		this.circle = 3;	// 总循环次数
		this.paraH = 0;		// 第一号参数
		this.paraP = 0.5;	// 第二号参数
		this.paraN = 3;		// 第三号参数
		while (true) {		// 获取界面输入的参数，处在不匹配输入要重新输入
			String pars = JOptionPane.showInputDialog("<Size, Circle, paraH, paraP, paraN>S C H P N\n"
					+ "(Press Cancel to use default parameters):");
			if (pars != null) {
				if (!pars.matches("\\-?\\d+ \\-?\\d+ \\-?\\d+(\\.\\d+)? \\-?\\d+(\\.\\d+)? \\-?\\d+(\\.\\d+)?")) {
					System.out.println();
					continue;
				}
				String[] parss = pars.split(" ");
				this.subSize = Integer.parseInt(parss[0]);
				if (this.subSize < 3) {
					this.subSize = 3; // set the minimum value
				}
				this.circle = Integer.parseInt(parss[1]);
				if (this.circle < 0) {
					this.circle = 1; // set the minimum value
				}
				this.paraH = Double.parseDouble(parss[2]);
				this.paraP = Double.parseDouble(parss[3]);
				this.paraN = Double.parseDouble(parss[4]);
				break;
			} else {
				break;
			}
		}
		// about the image Image的参数
		this.imageOrg = imageS; // keep it unchanged 保存一份原版Image
		this.image = imageS.clone(); // a copy 使用副本
		this.bitCount = bitCount;
		this.pxWidth = pxWidth;
		this.pxHeight = pxHeight;
		this.colorMXH = colorMXH;
		this.lineBytes = lineBytes;
		
		// clear each sub-area 循环获取整幅Image的每个局部并处理
		for (int cir = 0; cir < this.circle; ++cir) {
			int xle = this.pxWidth, yle = this.pxHeight, xl, yl;
			while (yle > 0) {
				if (yle > this.subSize+3) {
					yl = this.subSize;
				} else {
					yl = yle;
				}
				xle = this.pxWidth;
				while (xle > 0) {
					//System.out.println("x-y : "+yle+"--"+xle);
					if (xle > this.subSize+3) {
						xl = this.subSize;
					} else {
						xl = xle;
					}
					// 狠狠处理这个局部，撸到干净
					smoothSUB((this.pxHeight-yle)*(this.lineBytes)+(this.pxWidth-xle), xl, yl);
					xle -= xl;
				}
				yle -= yl;
			}
		}
		//System.out.println("The End : " + best);
		return this.image;
	}
	/**
	 * 计算pos单点的能量值，注意第二项乘2是考虑到对全局的能量影响
	 * 因为这个点的变化会引起其周围点的第二项能量计算的变化
	 * 若需要计算第二项的真实能量值请免去乘2
	 * @param pos	待计算的点
	 * @return		能量值
	 */
	private double pointEnergyCalculate(int pos) {
		return paraH*(image[colorMXH+pos] == (byte)0xff ? 1 : -1)
				- 2*paraP*calculatePP(pos)
				- paraN*(image[colorMXH+pos] == (byte)0xff ? 1 : -1)*(imageOrg[colorMXH+pos] == (byte)0xff ? 1 : -1);
	}
	
	/**
	 * 完全处理一个局部子图，也就是处理到该局部的能量基本稳定
	 * @param org
	 * @param xl
	 * @param yl
	 */
	private void smoothSUB(int org, int xl, int yl) {
		
		double best = energySUBCalculate(org, xl, yl), res;
		int best_pos,  tpos;
		while(true) {
			res = 9999999;
			best_pos = -1;	// 记录当前带来能量变动最大的点
			for (int i = 0; i < yl; ++i) {
				for (int j = 0; j < xl; ++j) {
					tpos  = colorMXH+org+i*lineBytes+j;
					double tmp1 = pointEnergyCalculate(tpos-colorMXH);
					this.image[tpos] = this.image[tpos] == (byte)0x00 ? (byte)0xff : (byte)0x00; // swap
					double tmp2 = pointEnergyCalculate(tpos-colorMXH);
					if (tmp2 - tmp1 < res) {
						res = tmp2 - tmp1;
						best_pos = tpos;
					}
					this.image[tpos] = this.image[tpos] == (byte)0x00 ? (byte)0xff : (byte)0x00; // back
				}
			}
			if (best_pos != -1) {
				this.image[best_pos] = this.image[best_pos] == (byte)0x00 ? (byte)0xff : (byte)0x00; // swap
				res = energySUBCalculate(org, xl, yl); // 计算局部的完全能量∑
				if (res >= best) { // Local Max 如果当前best_pos指向的那个像素的改动不能使得整个局部能量更小,说明优化到极限
					this.image[best_pos] = this.image[best_pos] == (byte)0x00 ? (byte)0xff : (byte)0x00; // back
					break;
				}
				best = res;
			} else {
				break; // impossible
			}
		}
		// sub-end
	}
	
	/**
	 * 计算局部子图总能量
	 * @param org
	 * @param xl
	 * @param yl
	 * @return
	 */
	private double energySUBCalculate(int org, int xl, int yl) {
		double egyH = 0, egyP = 0, egyN = 0;
			for (int i = 0; i < yl; ++i) {
				for (int j = 0; j < xl; ++j) {
					int tpos = colorMXH+org+i*lineBytes+j;
					// H
					egyH += (image[tpos] == (byte)0xff ? 1 : -1); // 0xff means black-1 默认黑色像素取 1
					// P
					egyP += calculatePP(tpos-colorMXH);	// 计算与邻居的能量差距∑
					// N
					egyN += (image[tpos] == (byte)0xff ? 1 : -1)*(imageOrg[tpos] == (byte)0xff ? 1 : -1);
				}
			}
		return paraH*egyH - paraP*egyP - paraN*egyN;
	}
	
	/**
	 * 计算全局的能量值
	 * @return	the energy of the current statement
	 */
	private double energyALLCalculate() {
		double egyH = 0, egyP = 0, egyN = 0;
		for (int i = 0, j = 0; i < lineBytes*pxHeight; ++i, ++j) {
			if (j == pxWidth) { // at the end of a line
				j = -1;
				i += lineBytes-pxWidth-1;
				continue;
			}
			// H
			egyH += (image[colorMXH+i] == (byte)0xff ? 1 : -1); // 0xff means black-1
			// P
			egyP += calculatePP(i);
			// N
			egyN += (image[colorMXH+i] == (byte)0xff ? 1 : -1)*(imageOrg[colorMXH+i] == (byte)0xff ? 1 : -1);
		}
		return paraH*egyH - paraP*egyP - paraN*egyN;
	}
	/**
	 * 计算pos点能量估值的第二项，涉及其8个邻居
	 * @param pos
	 * @return
	 */
	private int calculatePP(int pos) { // pos from 0 to n
		int egy = 0, poss[] = {pos-lineBytes-1,
								pos-lineBytes,
								pos-lineBytes+1,
								pos-1,
								pos+1,
								pos+lineBytes-1,
								pos+lineBytes,
								pos+lineBytes+1};
		for (int i = 0; i < 8; ++i) {
			if (this.isInside(pos, poss[i])) {
				egy += (image[colorMXH+pos] == (byte)0xff ? 1 : -1)*(image[colorMXH+poss[i]] == (byte)0xff ? 1 : -1);
			}
		}
		return egy;
	}
	/**
	 * To check whether the point tar is pos's valid neighbor
	 * 检查tar这个点是否为pos这个点的合法邻居
	 * @param pos 	from the head of the color matrix to the end 0->n
	 * @param tar 	pos's neighbor
	 * @return
	 */
	private boolean isInside(int pos, int tar) {
		if (pos < pxWidth && (tar == pos-lineBytes-1 || tar == pos-lineBytes || tar == pos-lineBytes+1)) {
			return false;
		}
		if (pos/lineBytes == pxHeight-1 && (tar == pos+lineBytes-1 || tar == pos+lineBytes || tar == pos+lineBytes+1)) {
			return false;
		}
		if (pos % lineBytes == 0 && (tar == pos-lineBytes-1 || tar == pos-1 || tar == pos+lineBytes-1)) {
			return false;
		}
		if ((pos+1+lineBytes-pxWidth) % lineBytes == 0 && (tar == pos+lineBytes-1 || tar == pos+1 || tar == pos+lineBytes+1)) {
			return false;
		}
		return true;
	}
}
