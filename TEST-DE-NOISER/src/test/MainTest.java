package test;

import mp.AlgorithmFS;
import mp.MainKernel;
import mp.NoiseAdder;

public class MainTest {
	public static void main(String[] args) {
		MainKernel mk = new MainKernel();
		mk.algAdd(new AlgorithmFS() {
			
			@Override
			public byte[] executeAFS(byte[] arg0, int arg1, int arg2, int arg3,
					int arg4, int arg5, int arg6) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String algName() {
				// TODO Auto-generated method stub
				return "A-Star";
			}
		});
		mk.methodAdd(new NoiseAdder() {
			
			@Override
			public String noiseName() {
				// TODO Auto-generated method stub
				return "A-Noise";
			}
			
			@Override
			public byte[] executeNAD(int arg0, byte[] arg1, int arg2, int arg3,
					int arg4, int arg5, int arg6, int arg7) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		mk.startToRun();
	}
}
