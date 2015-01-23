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

		// 判断是否已经安装SD卡
		public static boolean isSDCardExist() {
			return android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
		}

		// 内存剩余空间
		public static long getAvailableInternalMemorySize() {
			File path = Environment.getDataDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		}

		// 内存总空间
		public static long getTotalInternalMemorySize() {
			File path = Environment.getDataDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		}

		// SD卡剩余空间
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

		// SD卡总空间
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

		// 判断SD卡下external_sd文件夹的总大小
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

		// 判断SD卡下external_sd文件夹的可用大小
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
     * 把内容保存在SD 卡上 
     *  
     * @param fileName 文件的名称 
     * @param content 文件的内容 
     * @return 
     */  
    public static boolean saveContentToSDcard(String fileName, byte[] content) {  
        boolean flag = false;  
        FileOutputStream fileOutputStream = null;  
        // 获得SD卡的路径  
        File file = new File(Environment.getExternalStorageDirectory(), fileName);  
        // 判断SD卡是否可以用  
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
        // 在手机应用开发中 ByteArrayOutputStream 流是缓冲的流，和磁盘无关，可以不需要关闭  
        // Environment.MEDIA_MOUNTED 如果是可读写的状态，并且SD卡是存在的情况下  
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