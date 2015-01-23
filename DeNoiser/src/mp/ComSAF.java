package mp;

import javax.swing.JOptionPane;

/**
 * 
 * @author ZAK	Common Algrothm for 256-bit bmp (black-white)
 *
 */
public class ComSAF implements AlgorithmFS {
	private byte[] imageOrg = null;
	private byte[] image = null;
	private int bitCount = 0;
	private int pxWidth = 0;
	private int pxHeight = 0;
	private int colorMXH = 0;
	private int lineBytes = 0;
	private double paraH = 0;
	private double paraP = 0.5;
	private double paraN = 3;
	@Override
	public String algName() {
		// TODO Auto-generated method stub
		return "Common De-Noise Search(useless)";
	}
	@Override
	public byte[] executeAFS(byte[] imageS, int bitCount, int pxWidth, int pxHeight, int colorMXH, int lineBytes, int type) {
		if (type != 0) {
			return null;
		}
		// parameters
		this.paraH = 0;
		this.paraP = 0.5;
		this.paraN = 3;
		while (true) {
			String pars = JOptionPane.showInputDialog("H P N(Press Cancel to use default parameters):");
			if (pars != null) {
				if (!pars.matches("\\-?\\d+(\\.\\d+)? \\-?\\d+(\\.\\d+)? \\-?\\d+(\\.\\d+)?")) {
					System.out.println();
					continue;
				}
				String[] parss = pars.split(" ");
				this.paraH = Integer.parseInt(parss[0]);
				this.paraP = Integer.parseInt(parss[1]);
				this.paraN = Integer.parseInt(parss[2]);
				break;
			} else {
				break;
			}
		}
		// about the image
		this.imageOrg = imageS; // keep it unchanged
		this.image = imageS.clone(); // a copy
		this.bitCount = bitCount;
		this.pxWidth = pxWidth;
		this.pxHeight = pxHeight;
		this.colorMXH = colorMXH;
		this.lineBytes = lineBytes;
		// doing..........................
		double best = energyCalculate(), res = best;
		int best_pos,  tpos;
		System.out.println("The begin : " + best);
		while(true) {
			best_pos = -1;
			for (int i = 0; i < pxHeight; ++i) {
				for (int j = 0; j < pxWidth; ++j) {
					tpos  = colorMXH+i*lineBytes+j;
					this.image[tpos] = this.image[tpos] == (byte)0x00 ? (byte)0xff : (byte)0x00; // swap
					double tmp = energyCalculate();
					if (tmp < res) {
						res = tmp;
						best_pos = tpos;
					}
					this.image[tpos] = this.image[tpos] == (byte)0x00 ? (byte)0xff : (byte)0x00; // back
				}
			}
			if (Math.abs(best-res) > 1e-6 && best_pos != -1) {
				best = res;
				this.image[best_pos] = this.image[best_pos] == (byte)0x00 ? (byte)0xff : (byte)0x00;
			} else {
				break;
			}
			//break;
			//System.out.println("After one pass : " + res);
			
		}
		System.out.println("The End : " + best);
		return this.image;
	}
	
	/**
	 * 
	 * @return	the energy of the current statement
	 */
	private double energyCalculate() {
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
	/*private int calculateHH(int paraH) {
		int egy = 0;
		
		return egy;
	}*/
	/**
	 * Calculate eight points nearby
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
	/*private int calculateNN(int paraN) {
		int egy = 0;
		return egy;
	}*/
}
