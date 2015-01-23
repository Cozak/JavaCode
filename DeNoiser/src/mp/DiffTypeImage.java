package mp;

public class DiffTypeImage {
	private int bitCount = 0; // kinds of color
	private int pxWidth = 0; // the number of px in a line
	private int pxHeight = 0; // the number of px in a column
	private int colorMXHead = 0; // point to the head of color matrix
	private int lineBytes = 0; // the actual number of byte to describe a line
	//private byte[] imageD = null;
	
	public DiffTypeImage(byte[] brr) {
		//this.imageD = brr;
		this.bitCount = DiffTypeImage.bitCountCC(brr);
		this.pxWidth = DiffTypeImage.pwWidthCC(brr);
		this.pxHeight = DiffTypeImage.pwHeightCC(brr);
		this.colorMXHead = DiffTypeImage.colorMXHeadCC(brr);
		this.lineBytes = 4*(((this.pxWidth)*(this.bitCount)+31)/32);
	}
	public void selfPrint() {
		System.out.println(this.bitCount+"--"+this.pxWidth+"--"+this.pxHeight+"--"+this.colorMXHead+"--"+this.lineBytes);
	}
	public int getBitCount() {
		return this.bitCount;
	}
	public int getPxWidth() {
		return this.pxWidth;
	}
	public int getPxHeight() {
		return this.pxHeight;
	}
	public int getColorMXH() {
		return this.colorMXHead;
	}
	public int getLineBytes() {
		return this.lineBytes;
	}
	/**
	 * Convert a group of sub-successive bytes into integer 
	 * @param brr
	 * @param frm
	 * @param len 	at most 4 bytes
	 * @return
	 */
	public static int smallRuleHexToDec(byte[] brr, int frm, int len) {
		int res = 0;
		for (int i = 0; i < len; ++i) {
			int tmp = (int)brr[frm+i] & 0xff;
			res += tmp<<(i*8);
		}
		return res;
	}

	/**
	 * (0 is head) No.28-29
	 * @param brr
	 * @return
	 */
	private static int bitCountCC(byte[] brr) {
		return DiffTypeImage.smallRuleHexToDec(brr, 28, 2);
	}
	// No.18-21
	private static int pwWidthCC(byte[] brr) {
		return DiffTypeImage.smallRuleHexToDec(brr, 18, 4);
	}
	// No.22-25
	private static int pwHeightCC(byte[] brr) {
		return DiffTypeImage.smallRuleHexToDec(brr, 22, 4);
	}
	// No.10-13
	private static int colorMXHeadCC(byte[] brr) {
		return DiffTypeImage.smallRuleHexToDec(brr, 10, 4);
	}
	
}
