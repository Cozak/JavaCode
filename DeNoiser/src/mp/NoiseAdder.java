package mp;

public interface NoiseAdder {
	/**
	 * Each Noise-Adder must have a unique name
	 * @return
	 */
	public String noiseName();
	/**
	 * @param perc 		scale
	 * @param image 	byte[] of the image
	 * @param bitCount 		the number of bit of a pixel
	 * @param pxWidth		the number of pixel in a line
	 * @param pxHeight		the number of pixel in a column
	 * @param colorMXH		the index of color matrix's head
	 * @param lineBytes		the number of byte in a line (include bytes for padding)
	 * @param type			0:black-white 1:black-gray-white 2:colorful
	 * @return		byte[] after execution (if failed, return null)
	 */
	public byte[] executeNAD(int perc, byte[] image, int bitCount, int pxWidth, int pxHeight, int colorMXH, int lineBytes, int type);
}

class DefaultAddNoise implements NoiseAdder {
	public String noiseName() {
		return "Default Noise<256, black-white>";
	}
	/**
	 * Warning!!! This version support bitCount = 8 only!!!
	 * @return
	 */
	public byte[] executeNAD(int perc, byte[] image, int bitCount, int pxWidth, int pxHeight, int colorMXH, int lineBytes, int type) {
		for (int i = colorMXH, j = 0, diff = lineBytes - pxWidth - 1;
				i < image.length; ++i, ++j) {
			if (j >= pxWidth) {
				j = -1;
				i += diff;
				continue;
			}
			double r = Math.random();
			if (r*100 < perc) {
				//System.out.println("Luck");
				this.swapCC(image, i);
			}
		}
		return image;
	}
	private void swapCC(byte[] brr, int p) {
		int num = ((int)brr[p]) & 0xff;
		brr[p] &= 0x00;
		if (num != 255) {
			brr[p] |= 0xff;
		}
	}
}
