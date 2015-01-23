package com.minus.xsocket.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Xml.Encoding;

public class NetDataTypeTransform {
	public static final String coding="UTF-8"; //ȫ�ֶ��壬����Ӧϵͳ��������  
    public NetDataTypeTransform(){  
          
    }  
    /** 
     * ��intתΪ���ֽ���ǰ�����ֽ��ں��byte���� 
     */  
    public byte[] IntToByteArray(int n) {  
        byte[] b = new byte[4];  
        b[0] = (byte) (n & 0xff);  
        b[1] = (byte) (n >> 8 & 0xff);  
        b[2] = (byte) (n >> 16 & 0xff);  
        b[3] = (byte) (n >> 24 & 0xff);  
        return b;  
    }  
    /** 
     * byte����ת��Ϊint 
     * �����ֽ���ǰ�����ֽ��ں��byte���� תΪint
     */  
    public int ByteArrayToInt(byte[] bArr) {  
         if(bArr.length!=4){  
             return -1;  
         }  
         return (int) ((((bArr[3] & 0xff) << 24)    
                    | ((bArr[2] & 0xff) << 16)    
                    | ((bArr[1] & 0xff) << 8) | ((bArr[0] & 0xff) << 0)));   
    }  
    /** 
     * ��byte����ת����String,ת��ʱ��utf-8���뷽ʽ 
     */  
    public String ByteArraytoString(byte[] valArr,int maxLen) {  
        String result=null;  
        int index = 0;  
        while(index < valArr.length && index < maxLen) {  
            if(valArr[index] == 0) {  
                break;  
            }  
            index++;  
        }  
        byte[] temp = new byte[index];  
        System.arraycopy(valArr, 0, temp, 0, index);  
        try {
            result= new String(temp,"UTF-8");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        return result;  
    }  
    /** 
     * ��Stringת��Ϊbyte,Ϊ��֧�����ģ�ת��ʱ��utf-8���뷽ʽ 
     */  
    public byte[] StringToByteArray(String str){  
        byte[] temp = null;  
        try {  
            temp = str.getBytes("UTF-8");  
        } catch (UnsupportedEncodingException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return temp;  
    }  
    
   
}
