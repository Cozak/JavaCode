package mp;

import javax.swing.JOptionPane;

public interface AlgorithmFS {
	/**
	 * Each algorithm must have a unique name
	 * @return
	 */
	public String algName();
	/**
	 * 
	 * @param image 	byte[] of the image
	 * @param bitCount 		the number of bit of a pixel
	 * @param pxWidth		the number of pixel in a line
	 * @param pxHeight		the number of pixel in a column
	 * @param colorMXH		the index of color matrix's head
	 * @param lineBytes		the number of byte in a line (include bytes for padding)
	 * @param type			0:black-white 1:black-gray-white 2:colorful
	 * @return		byte[] after execution (if failed, return null)
	 */
	public byte[] executeAFS(byte[] image, int bitCount, int pxWidth, int pxHeight, int colorMXH, int lineBytes, int type);
}


/**
 * 
 * @author ZAK	Common Algrothm for 256-bit bmp (black-white)
 *
 */
class DefaultDeNoise implements AlgorithmFS {
	private byte[] imageOrg = null;
	private byte[] image = null;
	private int bitCount = 0;
	private int pxWidth = 0;
	private int pxHeight = 0;
	private int colorMXH = 0;
	private int lineBytes = 0;
	private double paraH = -1.5;
	private double paraP = 1;
	private double paraN = 2.4;
	@Override
	public String algName() {
		// TODO Auto-generated method stub
		return "Default Clear<256, Non-search>";
	}
	@Override
	public byte[] executeAFS(byte[] imageS, int bitCount, int pxWidth, int pxHeight, int colorMXH, int lineBytes, int type) {
		if (type != 0) {
			return null;
		}
		// parameters
		//this.paraH = 0.3; // biasing the background color (black)
		//this.paraP = 0.8; // surrounding pixels
		//this.paraN = 0.3; // based on the original pixel
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
		//System.out.println("The begin : " + best);
		while(true) {
			for (int i = 0; i < pxHeight; ++i) {
				for (int j = 0; j < pxWidth; ++j) {
					this.image[colorMXH+i*lineBytes+j] = this.image[colorMXH+i*lineBytes+j] == (byte)0x00 ? (byte)0xff : (byte)0x00; // swap
					double tmp = energyCalculate();
					if (tmp < res) {
						res = tmp;
					} else {
						this.image[colorMXH+i*lineBytes+j] = this.image[colorMXH+i*lineBytes+j] == (byte)0x00 ? (byte)0xff : (byte)0x00; // back
					}
				}
			}
			if (Math.abs(best-res) > 1e-6) {
				best = res;
			} else {
				break;
			}
			//break;
			//System.out.println("After one pass : " + res);
			
		}
		//System.out.println("The End : " + best);
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

