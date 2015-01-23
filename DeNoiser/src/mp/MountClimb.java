package mp;

import javax.swing.JOptionPane;

/**
 * 
 * @author ZAK	Common Algrothm for 256-bit bmp (black-white)
 *
 */
public class MountClimb implements AlgorithmFS {
	private byte[] imageOrg = null; // �����ͼ��ԭͼ
	private byte[] image = null; // ���Ըı䲢���������ͼ��
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
	public String algName() { // �㷨��ΨһID
		// TODO Auto-generated method stub
		return "Mountain Climb<256>";
	}
	@Override
	/**
	 * ִ�к��������������byte[]
	 */
	public byte[] executeAFS(byte[] imageS, int bitCount, int pxWidth, int pxHeight, int colorMXH, int lineBytes, int type) {
		if (type != 0) {
			return null;
		}
		// parameters �㷨ִ�в���
		this.subSize = 10;	// �ֲ���С��ÿ�δ���ľֲ�ΪsubSize������������
		this.circle = 3;	// ��ѭ������
		this.paraH = 0;		// ��һ�Ų���
		this.paraP = 0.5;	// �ڶ��Ų���
		this.paraN = 3;		// �����Ų���
		while (true) {		// ��ȡ��������Ĳ��������ڲ�ƥ������Ҫ��������
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
		// about the image Image�Ĳ���
		this.imageOrg = imageS; // keep it unchanged ����һ��ԭ��Image
		this.image = imageS.clone(); // a copy ʹ�ø���
		this.bitCount = bitCount;
		this.pxWidth = pxWidth;
		this.pxHeight = pxHeight;
		this.colorMXH = colorMXH;
		this.lineBytes = lineBytes;
		
		// clear each sub-area ѭ����ȡ����Image��ÿ���ֲ�������
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
					// �ݺݴ�������ֲ���ߣ���ɾ�
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
	 * ����pos���������ֵ��ע��ڶ����2�ǿ��ǵ���ȫ�ֵ�����Ӱ��
	 * ��Ϊ�����ı仯����������Χ��ĵڶ�����������ı仯
	 * ����Ҫ����ڶ������ʵ����ֵ����ȥ��2
	 * @param pos	������ĵ�
	 * @return		����ֵ
	 */
	private double pointEnergyCalculate(int pos) {
		return paraH*(image[colorMXH+pos] == (byte)0xff ? 1 : -1)
				- 2*paraP*calculatePP(pos)
				- paraN*(image[colorMXH+pos] == (byte)0xff ? 1 : -1)*(imageOrg[colorMXH+pos] == (byte)0xff ? 1 : -1);
	}
	
	/**
	 * ��ȫ����һ���ֲ���ͼ��Ҳ���Ǵ����þֲ������������ȶ�
	 * @param org
	 * @param xl
	 * @param yl
	 */
	private void smoothSUB(int org, int xl, int yl) {
		
		double best = energySUBCalculate(org, xl, yl), res;
		int best_pos,  tpos;
		while(true) {
			res = 9999999;
			best_pos = -1;	// ��¼��ǰ���������䶯���ĵ�
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
				res = energySUBCalculate(org, xl, yl); // ����ֲ�����ȫ������
				if (res >= best) { // Local Max �����ǰbest_posָ����Ǹ����صĸĶ�����ʹ�������ֲ�������С,˵���Ż�������
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
	 * ����ֲ���ͼ������
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
					egyH += (image[tpos] == (byte)0xff ? 1 : -1); // 0xff means black-1 Ĭ�Ϻ�ɫ����ȡ 1
					// P
					egyP += calculatePP(tpos-colorMXH);	// �������ھӵ���������
					// N
					egyN += (image[tpos] == (byte)0xff ? 1 : -1)*(imageOrg[tpos] == (byte)0xff ? 1 : -1);
				}
			}
		return paraH*egyH - paraP*egyP - paraN*egyN;
	}
	
	/**
	 * ����ȫ�ֵ�����ֵ
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
	 * ����pos��������ֵ�ĵڶ���漰��8���ھ�
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
	 * ���tar������Ƿ�Ϊpos�����ĺϷ��ھ�
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
