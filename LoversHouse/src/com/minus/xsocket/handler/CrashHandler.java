package com.minus.xsocket.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import com.minius.error.ActErrorReport;
import com.minus.lovershouse.BuildConfig;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.xsocket.asynsocket.AsynSocket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

    public class CrashHandler implements UncaughtExceptionHandler {  
    	 private GlobalApplication softApp;
         private File fileErrorLog;
         public CrashHandler(GlobalApplication app) {
                 softApp = app;
                 fileErrorLog = new File(GlobalApplication.PATH_ERROR_LOG);
         }
      
        @Override  
        public void uncaughtException(Thread thread, Throwable ex) { 
        	// fetch Excpetion Info
            String info = null;
            ByteArrayOutputStream baos = null;
            PrintStream printStream = null;
            try {
                    baos = new ByteArrayOutputStream();
                    printStream = new PrintStream(baos);
                    ex.printStackTrace(printStream);
                    byte[] data = baos.toByteArray();
                    info = new String(data);
                    data = null;
            } catch (Exception e) {
                    e.printStackTrace();
            } finally {
                    try {
                            if (printStream != null) {
                                    printStream.close();
                            }
                            if (baos != null) {
                                    baos.close();
                            }
                    } catch (Exception e) {
                            e.printStackTrace();
                    }
            }
            // print
//            long threadId = thread.getId();
            if (BuildConfig.DEBUG) {
            write2ErrorLog(fileErrorLog, info);
            
            }
          //退出前停心跳
			UserPacketHandler mReq = new UserPacketHandler();
	      	HeartPacketHandler.getInstance().stopHeart();
	      	
			mReq.Logout();
			SelfInfo.getInstance().setDefault();
			SelfInfo.getInstance().setOnline(false);//下线
			GlobalApplication.getInstance().setCommonDefault();
			GlobalApplication.getInstance().setTargetDefault();
			AsynSocket.getInstance().closeSocket();
			GlobalApplication.getInstance().destoryBimap();
//			Database.getInstance(getApplicationContext()).closeDatabase();
			GlobalApplication.getInstance().AppExit();
            // kill App Progress
			System.exit(0);
//            android.os.Process.killProcess(android.os.Process.myPid());
//            Log.d("ANDROID_LAB", "Thread.getName()=" + thread.getName() + " id=" + threadId + " state="
//                            + thread.getState());
//            Log.d("ANDROID_LAB", "Error[" + info + "]");
//            if (threadId != 1) {
//                    // 对于非UI线程可显示出提示界面，如果是UI线程抛的异常则界面卡死直到ANR。
//                    Intent intent = new Intent(softApp, ActErrorReport.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("error", info);
//                    intent.putExtra("by", "uehandler");
//                    softApp.startActivity(intent);
//            } else {
//                    // write 2 /data/data/<app_package>/files/error.log
//                    write2ErrorLog(fileErrorLog, info);
//                    
//                  //退出前停心跳
//    				UserPacketHandler mReq = new UserPacketHandler();
//    		      	HeartPacketHandler.getInstance().stopHeart();
//    		      	
//    				mReq.Logout();
//    				SelfInfo.getInstance().setDefault();
//    				SelfInfo.getInstance().setOnline(false);//下线
//    				GlobalApplication.getInstance().setCommonDefault();
//    				GlobalApplication.getInstance().setTargetDefault();
//    				AsynSocket.getInstance().closeSocket();
//    				GlobalApplication.getInstance().destoryBimap();
////    				Database.getInstance(getApplicationContext()).closeDatabase();
//    				GlobalApplication.getInstance().AppExit();
//                    // kill App Progress
//                    android.os.Process.killProcess(android.os.Process.myPid());
//            }
            // if (!handleException(ex) && mDefaultHandler != null) {  
            // mDefaultHandler.uncaughtException(thread, ex);  
            // } else {  
            // android.os.Process.killProcess(android.os.Process.myPid());  
            // System.exit(10);  
            // }  
//            System.out.println("uncaughtException");  
//      
//            new Thread() {  
//                @Override  
//                public void run() {  
//                    Looper.prepare();  
//                    new AlertDialog.Builder(mContext).setTitle("提示").setCancelable(false)  
//                            .setMessage("程序崩溃了...").setNeutralButton("我知道了", new OnClickListener() {  
//                                @Override  
//                                public void onClick(DialogInterface dialog, int which) {  
//                                    System.exit(0);  
//                                }  
//                            })  
//                            .create().show();  
//                    Looper.loop();  
//                }  
//            }.start();  
        }  
        
        private void write2ErrorLog(File file, String content) {
            FileOutputStream fos = null;
            try {
                    if (file.exists()) {
                            // 清空之前的记录
                            file.delete();
                    } else {
                            file.getParentFile().mkdirs();
                    }
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    fos.write(content.getBytes());
            } catch (Exception e) {
                    e.printStackTrace();
            } finally {
                    try {
                            if (fos != null) {
                                    fos.close();
                            }
                    } catch (Exception e) {
                            e.printStackTrace();
                    }
            }
    }
        
      
//        /** 
//         * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑 
//         * 
//         * @param ex 
//         * @return true:如果处理了该异常信息;否则返回false 
//         */  
//        private boolean handleException(Throwable ex) {  
//            if (ex == null) {  
//                return true;  
//            }  
//             new Handler(Looper.getMainLooper()).post(new Runnable() {  
//             @Override  
//             public void run() {  
//             new AlertDialog.Builder(mContext).setTitle("提示")  
//             .setMessage("程序崩溃了...").setNeutralButton("我知道了", null)  
//             .create().show();  
//             }  
//             });  
//      
//            return true;  
//        }  
    }  
