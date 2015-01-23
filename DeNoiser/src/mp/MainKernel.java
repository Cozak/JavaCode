package mp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class MainKernel {
	private MainFace MF = null;
	//private DiffTypeImage DTI = null;
	private NoiseAdder[] NAS = null;
	private int NASL = 0;
	private final int NASM = 7;
	private AlgorithmFS[] AFS = null;
	private int AFSL = 0;
	private final int AFSM = 7; // Max Algorithm
	private byte[][] imageS = null; // store a list of images
	private DiffTypeImage[] imageDTI = null;
	private int imageSP = -1;
	private int imageSM = 0;
	private static int historyLength = 10;
	
	public MainKernel() {
		imageS = new byte[historyLength][];
		imageDTI = new DiffTypeImage[historyLength];
		this.initAlg();
	}
	/**
	 * init for algorithms' list
	 */
	private void initAlg() {
		this.AFS = new AlgorithmFS[this.AFSM];
		this.NAS = new NoiseAdder[this.NASM];
		// add algorithms (ComAF as default item)
		this.AFS[this.AFSL++] = new DefaultDeNoise();
		this.AFS[this.AFSL++] = new MountClimb();
		// ... other algorithms
		// add noise method
		this.NAS[this.NASL++] = new DefaultAddNoise();
		// ... other method
	}
	private byte[] algorithmExec(String name) { // name of the algorithm
		if (this.imageSP < 0 || this.imageSP >= this.imageSM) { // current image is null
			return null;
		}
		for (int i = 0; i < this.AFS.length; ++i) {
			if (this.AFS[i] != null && this.AFS[i].algName().equals(name)) { // send a copy
				return this.AFS[i].executeAFS(this.fetchImageFromRecord(this.imageSP).clone(),
						this.imageDTI[this.imageSP].getBitCount(),
						this.imageDTI[this.imageSP].getPxWidth(),
						this.imageDTI[this.imageSP].getPxHeight(),
						this.imageDTI[this.imageSP].getColorMXH(),
						this.imageDTI[this.imageSP].getLineBytes(), 0); // byte[] send towards Execution
			}
		}
		return null;
	}
	private byte[] noiseAddExec(String name, int perc) {
		if (this.imageSP < 0 || this.imageSP >= this.imageSM) { // current image is null
			return null;
		}
		for (int i = 0; i < this.NAS.length; ++i) {
			if (this.NAS[i] != null && this.NAS[i].noiseName().equals(name)) { // send a copy
				return this.NAS[i].executeNAD(perc, this.fetchImageFromRecord(this.imageSP).clone(),
						this.imageDTI[this.imageSP].getBitCount(),
						this.imageDTI[this.imageSP].getPxWidth(),
						this.imageDTI[this.imageSP].getPxHeight(),
						this.imageDTI[this.imageSP].getColorMXH(),
						this.imageDTI[this.imageSP].getLineBytes(), 0); // byte[] send towards Execution
			}
		}
		return null;
	}
	
	public void startToRun() {
		// ........
		// TODO Auto-generated constructor stub
				this.MF = new MainFace() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public Object opExec(Object sth, int ord) {
						// clear record
						if (ord == -2) {
							MainKernel.this.resetRecord();
							return null;
						}
						
						// init noise-adder's and algorithms' list
						if (ord == -1) {
							int i,j;
							String[] algMethes = new String[MainKernel.this.AFSL+MainKernel.this.NASL+1];
							for (i = 0; i < MainKernel.this.AFSL; ++i) {
								algMethes[i] = MainKernel.this.AFS[i].algName();
							}
							algMethes[i++] = "";
							for (j = 0;i < algMethes.length; ++i, ++j) {
								algMethes[i] = MainKernel.this.NAS[j].noiseName();
							}
							return algMethes;
						}
						
						// open file
						if (ord == 0) {
							byte[] tmp = ImageFileToByte((String)sth);
							if (tmp == null) { // fail to open
								return null;
							}
							updateImageRecord(tmp);
							MainKernel.this.imageSP = MainKernel.this.imageSM - 1;
							return MainKernel.ByteToImage(MainKernel.this.fetchImageFromRecord(
									MainKernel.this.imageSP));
						}
						// save file
						if (ord == 1) {
							if (MainKernel.ByteToImageFile((String)sth, MainKernel.this.fetchImageFromRecord(
									MainKernel.this.imageSP))) {
								return "true";
							}
							return null;
						}
						// go front
						if (ord == 2) {
							if (MainKernel.this.imageSP + 1 < MainKernel.this.imageSM) {
								MainKernel.this.imageSP++; // go ahead
								return MainKernel.ByteToImage(MainKernel.this.fetchImageFromRecord(
										MainKernel.this.imageSP));
							}
							
						}
						// go back
						if (ord == 3) {
							if (MainKernel.this.imageSP - 1 >= 0) {
								MainKernel.this.imageSP--; // go back
								return MainKernel.ByteToImage(MainKernel.this.fetchImageFromRecord(
										MainKernel.this.imageSP));
							}
						}
						// add noise
						if (ord == 6) {
							//System.out.println("Here is MainKernel with "+Integer.parseInt(str));
							String[] ts = ((String)sth).split("\\|");
							if (ts.length != 2) {
								return null;
							}
							byte[] tbrr = MainKernel.this.noiseAddExec(ts[0], Integer.parseInt(ts[1]));
							if (tbrr != null) {
								//System.out.println("Here is Ok");
								updateImageRecord(tbrr);
								MainKernel.this.imageSP = MainKernel.this.imageSM - 1;
								return MainKernel.ByteToImage(MainKernel.this.fetchImageFromRecord(
										MainKernel.this.imageSP));
							}
							//System.out.println("Here is Out");
						}
						
						// execute the algorithm
						if (ord == 7) {
							byte[] tbrr = MainKernel.this.algorithmExec((String)sth);
							if (tbrr != null) {
								updateImageRecord(tbrr);
								MainKernel.this.imageSP = MainKernel.this.imageSM - 1;
								return MainKernel.ByteToImage(MainKernel.this.fetchImageFromRecord(
										MainKernel.this.imageSP));
							}
						}
						
						// compare two image
						if (ord == 17) {
							if (fetchImageFromRecord(MainKernel.this.imageSP) == null) { // Null File
								return null;
							}
							return MainKernel.this.compareImage(MainKernel.ImageToByte((BufferedImage)sth));
						}
						
						return null;
					}
				};
	}
	private void resetRecord() {
		this.imageSM = 0;
		this.imageSP = -1;
	}
	/**
	 * Cover the next one in front of the imageSP
	 * @param brr
	 */
	private void updateImageRecord(byte[] brr) {
		if (brr == null) { // history is limited
			return;
		}
		this.imageSM = this.imageSP+1;
		if (this.imageSM >= MainKernel.historyLength) {
			this.imageSM--; // cover the last one
		}
		this.imageS[this.imageSM] = brr;
		this.imageDTI[this.imageSM++] = new DiffTypeImage(brr);
	}
	private byte[] fetchImageFromRecord(int ord) {
		if (ord < 0 || ord >= this.imageSM) {
			return null;
		}
		return this.imageS[ord];
	}
	/**
	 * Image File convert to Bytes
	 * @param filename
	 * @return
	 */
	private static byte[] ImageFileToByte(String filename) {
		try {
			File f = new File(filename);
			if (!f.exists()) { // if file doesn't exist...create!
				//JOptionPane.showMessageDialog(null, "File Not Found");
				return null;
				//f.createNewFile();
			}
			BufferedImage image = ImageIO.read(f);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "BMP", baos);
			return baos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Bytes convert to Image File
	 * @return
	 */
	private static boolean ByteToImageFile(String name, byte[] brr) {
		if (brr == null) {
			return false;
		}
		File f = new File(name);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(f);
			ImageIO.write(MainKernel.ByteToImage(brr), "BMP", fout);
			fout.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (fout != null) {
			try {
				fout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * Image convert to Bytes
	 * @param image
	 * @return
	 */
	private static byte[] ImageToByte(BufferedImage image) {
		if (image == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "BMP", baos);
			return baos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Bytes convert to Image
	 * @param brr
	 * @return
	 */
	private static BufferedImage ByteToImage(byte[] brr) {
		if (brr == null) {
			return null;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(brr);
		try {
			BufferedImage image = ImageIO.read(bais);
			return image;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *  Compare two image
	 * @param image
	 * @return the percentage of similarity
	 */
	private Double compareImage(byte[] image) {
		if (image == null) {
			return null;
		}
		DiffTypeImage tmpDTI = new DiffTypeImage(image);
		if (tmpDTI.getBitCount() == this.imageDTI[this.imageSP].getBitCount() 
				&& tmpDTI.getPxWidth() == this.imageDTI[this.imageSP].getPxWidth() 
				&& tmpDTI.getPxHeight() == this.imageDTI[this.imageSP].getPxHeight()) {
			byte[] host = this.fetchImageFromRecord(this.imageSP);
			int head = tmpDTI.getColorMXH(), xl = tmpDTI.getPxWidth(), yl = tmpDTI.getPxHeight(), lb = tmpDTI.getLineBytes(), diff = 0;
			for (int i = 0; i < yl; ++i) {
				for (int j = 0; j < xl; ++j) {
					int tpos = head+i*lb+j;
					if (image[tpos] != host[tpos]) {
						diff++;
					}
				}
			}
			return new Double(((double)(xl*yl - diff))/((double)(xl*yl))*100);
		}
		return null;
	}
	
	/**
	 * Add new algorithms into AFS
	 * Before invoking startToRun
	 */
	public void algAdd(AlgorithmFS afs) {
		if (afs != null) {
			this.AFS[this.AFSL++] = afs;
		}
	}
	/**
	 * Add new method into NAS
	 * Before invoking startToRun
	 */
	public void methodAdd(NoiseAdder nad) {
		if (nad != null) {
			this.NAS[this.NASL++] = nad;
		}
	}
	
	public static void main(String[] args) {
		// demo
		MainKernel mk = new MainKernel();
		mk.algAdd(null); // new AlgorithmFS()
		mk.methodAdd(null); // new NoiseAdder()
		mk.startToRun(); // start to run
	}
}
