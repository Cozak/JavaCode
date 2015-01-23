package com.minus.xsocket.asynsocket.protocol;

import java.io.UnsupportedEncodingException;

public class Encrypt {

//    public static void encode(byte[] data ,int len);
//    public static void decode(byte[] data ,int len);
//
//    public  void  SubBytes(byte[] data ,int len);
//    public  void  ShiftRow(byte[] data ,int len);
//		
//    public  void  InvSubBytes(byte[] data ,int len);
//    public  void InvShiftRows(byte[] data ,int len);
//
//    public  void AddRoundKey(byte[] data ,int len);:(unsigned char*) data length:(int) len key:(unsigned char) k;
	private  String keyStr = "dingyi2013";
	private byte[] key = null;
	
    

	private static byte[] sBox =
	{ /*      0     1     2     3     4     5     6     7     8     9     a     b     c     d     e     f */ 
	/*0*/	0x0f, 0x30, 0x02, 0x34, 0x37, (byte)0xf5, 0x06, 0x07, 0x08, 0x39, 0x2e, 0x2a, 0x0c, 0x0d, 0x0e, 0x00, 
	/*1*/(byte)0xf0, 0x14, 0x12, 0x13, 0x11, 0x15, 0x16, 0x17, (byte)0xf6, 0x69, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 
	/*2*/	0x60, 0x31, 0x32, 0x23, 0x24, (byte)0xc5, 0x26, (byte)0xbd, 0x38,(byte) 0x89, 0x0b, 0x2b, 0x2c, (byte)0xfd, 0x0a, 0x3f, 
	/*3*/	0x01, 0x21, 0x22, (byte)0xd3, 0x03, 0x35, 0x4f, 0x04, 0x28, 0x09, 0x3a, 0x5a, (byte)0xcc, 0x7d, 0x3e, 0x2f, 
	/*4*/	(byte)0xb0, 0x41, 0x42, 0x43, (byte)0xe4, 0x45, 0x46, 0x47, 0x48, 0x79, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x36, 
	/*5*/	0x50, (byte)0xf1, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x3b, 0x5b, 0x5c, 0x5d, 0x5e, 0x5f, 
	/*6*/	0x20, 0x61, 0x62, (byte)0xf3, 0x64, 0x65, 0x66, 0x74, 0x68, 0x19, (byte)0x9f, 0x7b, 0x6c, 0x6d, 0x6e, (byte)0x8f, 
	/*7*/	(byte)0xc1, 0x71, (byte)0x92, 0x73, 0x67, 0x75, 0x76, 0x77, 0x78, 0x49, 0x7a, 0x6b, 0x7c, 0x3d, 0x7e, 0x7f, 
	/*8*/	(byte)0x80, (byte)0x81, (byte)0x82, (byte)0x83, (byte)0x84, (byte)0x85, (byte)0x86, (byte)0x87, (byte)0x88, (byte)0x29,(byte) 0x8a,(byte) 0x8b, (byte)0xd9, (byte)0x8d, (byte)0x8e, 0x6f, 
	/*9*/	(byte)0x90, (byte)0x91, 0x72, (byte)0x93, (byte)0x94, (byte)0x95, (byte)0x96, (byte)0x97, (byte)0xc7, (byte)0xc6, (byte)0x9a, (byte)0x9b,(byte)0x9c,(byte) 0x9d, (byte)0x9e,(byte) 0x6a, 
	/*a*/	(byte)0xa0,(byte)0xa1, (byte)0xa2, (byte)0xa3,(byte) 0xa4, (byte)0xa5,(byte) 0xa6, (byte)0xa7, (byte)0xa8, (byte)0xa9,(byte) 0xaf, (byte)0xab, (byte)0xac, (byte)0xad,(byte) 0xae,(byte) 0xaa, 
	/*b*/	0x40, (byte)0xb1, (byte)0xb2, (byte)0xb3,(byte) 0xb4, (byte)0xb5, (byte)0xb6,(byte) 0xb7, (byte)0xb8, (byte)0xb9, (byte)0xba, (byte)0xbb,(byte) 0xbc, (byte)0x27, (byte)0xbe,(byte) 0xbf, 
	/*c*/	(byte)0xc0, (byte)0x70, (byte)0xc2, (byte)0xc3, (byte)0xc4, (byte)0x25,(byte) 0x99, (byte)0x98, (byte)0xc8, (byte)0xda, (byte)0xca, (byte)0xcb, 0x3c, (byte)0xdd, (byte)0xce, (byte)0xcf, 
	/*d*/	(byte)0xd0, (byte)0xd1, (byte)0xd2, 0x33, (byte)0xd4,(byte) 0xd5, (byte)0xd6, (byte)0xd7, (byte)0xd8, (byte)0x8c, (byte)0xc9, (byte)0xdb, (byte)0xdc, (byte)0xcd, (byte)0xde, (byte)0xdf, 
	/*e*/	(byte)0xe0, (byte)0xe1, (byte)0xe2, (byte)0xe3, 0x44,(byte) 0xe5, (byte)0xe6, (byte)0xe7, (byte)0xe8, (byte)0xe9,(byte) 0xea, (byte)0xeb,(byte) 0xec,(byte) 0xed, (byte)0xff,(byte) 0xef, 
	/*f*/	0x10, 0x51,(byte) 0xf2, 0x63, (byte)0xf4, 0x05, 0x18, (byte)0xf7, (byte)0xf8, (byte)0xf9, (byte)0xfa, (byte)0xfb, (byte)0xfc, 0x2d, (byte)0xfe, (byte)0xee
	};
	
	
	private  Encrypt(){
		  super();
	      init();	
	}
	
	private void init(){
		
			try {
				key= keyStr.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	/**
	 * 单例模式,线程
	 * @return
	 */
		
		private static class  EncryptContainer{
			private static Encrypt instance = new Encrypt();
		}
		
		public static Encrypt getInstance(){
		    
			  return  EncryptContainer.instance;
		}
		
	 public  void encode(byte[] data ,int len)
	{
		if(key == null || key.length > 10) return ;
		
		for(int i=0; i< 10; i++)
		{
			SubBytes(data ,len);
	        ShiftRows(data,len);
	         AddRoundKey(data ,len,key[i]);
		}
	}

	 public void  decode(byte[] data ,int len)
	{
		 if(key == null || key.length > 10) return ;
		for(int i=0; i< 10; i++)
		{
	        AddRoundKey(data,len,key[9-i]);
	        InvShiftRows(data,len);
	         InvSubBytes(data ,len);
		}
	}

    public static void AddRoundKey(byte[] data ,int len,byte k)
	{
		for(int i=0; i<len; i++)
			data[i] ^= k;
	}

    public static void SubBytes(byte[] data ,int len)
	{
		for(int i=0; i<len; i++){
			if(data[i] <0)  {
				data[i]  = sBox[data[i] &0xff];
			}else{
				data[i] = sBox[data[i]];
			}
			
		}
	}

    public static void ShiftRows(byte[] data ,int len)
	{
		byte[] t = new byte[4];
		int row = len/4;
		int r,c;

		for(r=1; r<row; r++)
		{
			for(c=0; c<4; c++)
			{
				t[c] = data[r*4+(c+r)%4];
			}
			for(c=0; c<4; c++)
			{
				data[r*4+(c+r)%4] = t[c];
			}
		}
	}

    public static  void InvSubBytes(byte[] data ,int len)
	{
		for(int i=0; i<len; i++){
			data[i] = sBox[(data[i] & 0xff)];
		}
	}

    public static  void InvShiftRows(byte[] data ,int len)
	{
    	byte[] t = new byte[4];
		int row = len/4;
		int r,c;
		for(r=1; r<row; r++)
		{
			for(c=0; c<4; c++)
			{
				t[c] = data[r*4+(c-r+4)%4];
			}
			for(c=0; c<4; c++)
			{
				data[r*4+(c-r+4)%4] = t[c];
			}
		}
}
}
