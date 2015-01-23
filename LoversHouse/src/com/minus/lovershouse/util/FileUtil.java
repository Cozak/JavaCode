package com.minus.lovershouse.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.os.StatFs;


/**
 * @author Administrator
 *
 */
public class FileUtil{
	
		private static final int ERROR = -1;
		public static int save_dir = 1;

		// �ж��Ƿ��Ѿ���װSD��
		public static boolean isSDCardExist() {
			return android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
		}

		// �ڴ�ʣ��ռ�
		public static long getAvailableInternalMemorySize() {
			File path = Environment.getDataDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		}

		// �ڴ��ܿռ�
		public static long getTotalInternalMemorySize() {
			File path = Environment.getDataDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		}

		// SD��ʣ��ռ�
		public static long getAvailableExternalMemorySize() {
			if (isSDCardExist()) {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				return availableBlocks * blockSize;
			} else {
				return ERROR;
			}
		}

		// SD���ܿռ�
		public static long getTotalExternalMemorySize() {
			if (isSDCardExist()) {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long totalBlocks = stat.getBlockCount();
				return totalBlocks * blockSize;
			} else {
				return ERROR;
			}
		}

		// �ж�SD����external_sd�ļ��е��ܴ�С
		public static long getTotalExternal_SDMemorySize() {
			if (isSDCardExist()) {
				File path = Environment.getExternalStorageDirectory();
				File externalSD = new File(path.getPath() + "/external_sd");
				if (externalSD.exists() && externalSD.isDirectory()) {
					StatFs stat = new StatFs(path.getPath() + "/external_sd");
					long blockSize = stat.getBlockSize();
					long totalBlocks = stat.getBlockCount();
					if (getTotalExternalMemorySize() != -1
						&& getTotalExternalMemorySize() != totalBlocks* blockSize) {
						return totalBlocks * blockSize;
					} else {
						return ERROR;
					}
				} else {
					return ERROR;
				}

			} else {
				return ERROR;
			}
		}

		// �ж�SD����external_sd�ļ��еĿ��ô�С
		public static long getAvailableExternal_SDMemorySize() {
			if (isSDCardExist()) {
				File path = Environment.getExternalStorageDirectory();
				File externalSD = new File(path.getPath() + "/external_sd");
				if (externalSD.exists() && externalSD.isDirectory()) {
					StatFs stat = new StatFs(path.getPath() + "/external_sd");
					long blockSize = stat.getBlockSize();
					long availableBlocks = stat.getAvailableBlocks();
					if (getAvailableExternalMemorySize() != -1
						&& getAvailableExternalMemorySize() != availableBlocks* blockSize) {
						return availableBlocks * blockSize;
					} else {
						return ERROR;
					}

				} else {
					return ERROR;
				}

			} else {
				return ERROR;
			}
		}
	
	  /** 
     * �����ݱ�����SD ���� 
     *  
     * @param fileName �ļ������� 
     * @param content �ļ������� 
     * @return 
     */  
    public static boolean saveContentToSDcard(String fileName, byte[] content) {  
        boolean flag = false;  
        FileOutputStream fileOutputStream = null;  
        // ���SD����·��  
        File file = new File(Environment.getExternalStorageDirectory(), fileName);  
        // �ж�SD���Ƿ������  
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageDirectory())) {  
            try {  
                fileOutputStream = new FileOutputStream(file);  
                fileOutputStream.write(content);  
                flag = true;  
            } catch (FileNotFoundException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } finally {  
                if (fileOutputStream != null) {  
                    try {  
                        fileOutputStream.close();  
                    } catch (IOException e) {  
                        // TODO Auto-generated catch block  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }  
  
        return flag;  
    }  
    
    public String getFileFromSdcard(String fileName) {  
        FileInputStream inputStream = null;  
        // ���ֻ�Ӧ�ÿ����� ByteArrayOutputStream ���ǻ���������ʹ����޹أ����Բ���Ҫ�ر�  
        // Environment.MEDIA_MOUNTED ����ǿɶ�д��״̬������SD���Ǵ��ڵ������  
        ByteArrayOutputStream outputSteam = new ByteArrayOutputStream();  
        File file = new File(Environment.getExternalStorageDirectory(), fileName);  
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {  
            try {  
                inputStream = new FileInputStream(file);  
                int length = 0;  
                byte[] buffer = new byte[1024];  
                while (-1 != (length = (inputStream.read(buffer)))) {  
                    outputSteam.write(buffer, 0, length);  
                }  
  
            } catch (FileNotFoundException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } finally {  
                if (inputStream != null) {  
                    try {  
                        inputStream.close();  
                    } catch (IOException e) {  
                        // TODO Auto-generated catch block  
                        e.printStackTrace();  
                    }  
                }  
            }  
        } else {  
//            Toast.makeText(context, "Please input SD card", Toast.LENGTH_LONG).show();  
//            Log.i(TAG, "No SD card");  
        }  
  
        return new String(outputSteam.toByteArray());  
    }  
    
    
}