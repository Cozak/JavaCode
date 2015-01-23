/**
 * 
 */
package com.minus.lovershouse.util;



import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.minius.ui.CustomDialog.Builder;
import com.minus.actionsystem.MyAnimations;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.singleton.GlobalApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.EditText;
public class AppManagerUtil {
	
	private static String sdState = Environment.getExternalStorageState();

	/**
	 * 判断edittext是否null
	 */
	public static String checkEditText(EditText editText) {
		if (editText != null && editText.getText() != null
				&& !(editText.getText().toString().trim().equals(""))) {
			return editText.getText().toString().trim();
		} else {
			return "";
		}
	}


	   public  static String getDate() {
	        Calendar c = Calendar.getInstance();
	        
	        String year = String.valueOf(c.get(Calendar.YEAR));
	        String month = String.valueOf(c.get(Calendar.MONTH) + 1 );
	        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) );
	        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
	        String mins = String.valueOf(c.get(Calendar.MINUTE));
	        if(hour.length()==1)
	        	hour="0"+hour;
	        if(mins.length()==1)
	        	mins="0"+mins;
	        StringBuffer sbBuffer = new StringBuffer();
	        sbBuffer.append(year + "年" + month + "月" + day + "日" + hour + "时");
	        return sbBuffer.toString();
	    }
	   /**
	    * 日期转换成Java字符串
	    * @param date 
	    * @return str
	    */
	    public static String DateToStr(Date date) {
	      
	       SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	       String str = format.format(date);
	       return str;
	    } 
	    
	    /**
		    * 字符串转换成日期
		    * @param str
		    * @return date
		    */
		    public static Date StrToDate(String str) {
		      
		       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		       Date date = null;
		       try {
		        date = format.parse(str);
		       } catch (ParseException e) {
		        e.printStackTrace();
		       }
		    
		       return date;
		    }

//		    yyyy-MM-dd-HH:mm:ss S
		    public static String getCurTimeInServer_miliSecond() {
		    	if(TimeZone.getDefault().getID().equals("Asia/Shanghai")
		    			|| TimeZone.getDefault().getID().equals("Asia/Taipei")){
		    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss S");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String str = formatter.format(curDate);
					return str;
		    	}else{
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss S Z");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String str = formatter.format(curDate);
				return ConvertToServer(str);
		    	}
			}
		    
//		    yyyy-MM-dd-HH:mm:ss 
		    public  static String getCurDateInServer() {
		    	if(TimeZone.getDefault().getID().equals("Asia/Shanghai")
		    			|| TimeZone.getDefault().getID().equals("Asia/Taipei")){
		    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String str = formatter.format(curDate);
					return str;
		    	}else{
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss Z");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String str = formatter.format(curDate);
				return ConvertToServer(str);
		    	}
			}
			  /**
		     * 将本地区时间转为东八区时区的时间
		     * 本地区时间 格式  ："yyyy-MM-dd-HH:mm:ss z"
		     * 输出东八区时间格式 yyyy-MM-dd-HH:mm:ss 
		     * */
		    public  static String ConvertToServer(String localTime) {
		    	SimpleDateFormat dateFormat = new SimpleDateFormat(
		    			"yyyy-MM-dd-HH:mm:ss Z");
		    	SimpleDateFormat serverFormat = new SimpleDateFormat(
		    			"yyyy-MM-dd-HH:mm:ss");
		    	TimeZone serverZone = TimeZone.getTimeZone("GMT+8");
//		    	dateFormat.setTimeZone(serverZone);
		    	serverFormat.setTimeZone(serverZone);
		    	  Date fromDate = new Date();
		    	  try {
					fromDate =dateFormat.parse(localTime);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	String serverTime = serverFormat.format(fromDate);
		    	
		    	return serverTime;
		    }

		    /**
		     * 将自己设定的特定的时间转为目的时区的时间
		     * 
		     * */
		    public  static String timeConvert(Date srcTime, TimeZone dstZone) {
		    	SimpleDateFormat dateFormat = new SimpleDateFormat(
		    			"yyyy-MM-dd-HH:mm:ss");
		    	dateFormat.setTimeZone(dstZone);
		    	String now = dateFormat.format(srcTime.getTime());
		    	return now;
		    }
		    /**
		     * 东八区时间  "yyyy-MM-dd-HH:mm:ss" 转为 东八区时间  "yyyy-MM-dd-HH:mm:ss z"
		     * 格式
		     * @param from 得到的时间 z为时区
		     * @return to 目标时间
		     */
		    public  static String transFormat(String from){
//		            String to = "";
//		            SimpleDateFormat mSDF = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
//		            SimpleDateFormat initialSDF = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss Z");
//		          
//		            TimeZone serverZone = TimeZone.getTimeZone("GMT+8");
//		            initialSDF.setTimeZone(serverZone); 
//		            mSDF.setTimeZone(serverZone);
//		          
//		            //把字符串转化为Date对象，然后格式化输出这个Date
//		            Date fromDate = new Date();
//		            try {
//		                    fromDate = mSDF.parse(from);
//		                    to = initialSDF.format(fromDate);
//		            } catch (ParseException e1) {
//		                    e1.printStackTrace();
//		            }                                                                                         
		            return from+" +0800";
		    }

		    /**
		     * 转换时间
		     * @param from 东八区的时间 格式"yyyy-MM-dd-HH:mm:ss" 
		     * @return to 本地时间 格式"MM-dd HH:mm " 显示在聊天界面
		     */
		    public static String transformDisplayChat(String serverDate){
		    	
		    	if(TimeZone.getDefault().getID().equals("Asia/Shanghai")
		    			|| TimeZone.getDefault().getID().equals("Asia/Taipei")){
		    		 SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
			         SimpleDateFormat outputSDF = new SimpleDateFormat("MM-dd HH:mm ");
			         Date fromDate = new Date();
                     try {
						fromDate = simple.parse(serverDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			         String toD = outputSDF.format(fromDate);
			         return toD;
		    	}else{
		    	 String from = transFormat(serverDate);
		            String to = "";
		            
		            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss Z");
		            SimpleDateFormat outputSDF = new SimpleDateFormat("MM-dd HH:mm ");
		            //本地时区
		            Calendar nowCal = Calendar.getInstance();        
		            TimeZone localZone = nowCal.getTimeZone();
		            //设定SDF的时区为本地
		            simple.setTimeZone(localZone);
		            
		            //把字符串转化为Date对象，然后格式化输出这个Date
		            Date fromDate = new Date();
		            try {
		                    fromDate = simple.parse(from);
		                    to = outputSDF.format(fromDate);
		            } catch (ParseException e1) {
		                    e1.printStackTrace();
		            }                                                                                         
		            return to;
		    	}
		    }
	  
		    /**
		     * 转换时间 用于日记本显示
		     * @param from 东八区的时间 格式"yyyy-MM-dd-HH:mm:ss" 
		     * @return to 本地时间 格式"yyyy-MM-dd HH:mm " 显示在日记列表
		     */
		    public static String transformDisplayDiary(String serverDate){
		    	if(TimeZone.getDefault().getID().equals("Asia/Shanghai")
		    			|| TimeZone.getDefault().getID().equals("Asia/Taipei")){
		    		 SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
			         SimpleDateFormat outputSDF = new SimpleDateFormat("MM-dd HH:mm ");
			         Date fromDate = new Date();
                     try {
						fromDate = simple.parse(serverDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			         String toD = outputSDF.format(fromDate);
			         return toD;
		    	}else{
		    	 String from = transFormat(serverDate);
		            String to = "";
		            
		            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss Z");
		            SimpleDateFormat outputSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
		            //本地时区
		            Calendar nowCal = Calendar.getInstance();        
		            TimeZone localZone = nowCal.getTimeZone();
		            //设定SDF的时区为本地
		            simple.setTimeZone(localZone);
		            
		            //把字符串转化为Date对象，然后格式化输出这个Date
		            Date fromDate = new Date();
		            try {
		                    fromDate = simple.parse(from);
		                    to = outputSDF.format(fromDate);
		            } catch (ParseException e1) {
		                    e1.printStackTrace();
		            }                                                                                         
		            return to;
		    	}
		    }
		    /**
		     * 转换时间 用于地图显示
		     * @param from 东八区的时间 格式"yyyy-MM-dd-HH:mm:ss" 
		     * @return to 本地时间 格式"yyyy-MM-dd HH:mm " 显示在地图的小气泡
		     */
		    public static String transformDisplayMap(String serverDate){
		    	if(TimeZone.getDefault().getID().equals("Asia/Shanghai")
		    			|| TimeZone.getDefault().getID().equals("Asia/Taipei")){
		    		 SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
			         SimpleDateFormat outputSDF = new SimpleDateFormat("M月d日 H时");
			         Date fromDate = new Date();
                     try {
						fromDate = simple.parse(serverDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			         String toD = outputSDF.format(fromDate);
			         return toD;
		    	}else{
		    	 String from = transFormat(serverDate);
		            String to = "";
		            
		            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss Z");
		            SimpleDateFormat outputSDF = new SimpleDateFormat("M月d日 H时");
		            //本地时区
		            Calendar nowCal = Calendar.getInstance();        
		            TimeZone localZone = nowCal.getTimeZone();
		            //设定SDF的时区为本地
		            simple.setTimeZone(localZone);
		            
		            //把字符串转化为Date对象，然后格式化输出这个Date
		            Date fromDate = new Date();
		            try {
		                    fromDate = simple.parse(from);
		                    to = outputSDF.format(fromDate);
		            } catch (ParseException e1) {
		                    e1.printStackTrace();
		            }                                                                                         
		            return to;
		    	}
		    }
		public static  String getCurDate() {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间 当前时区
			String str = formatter.format(curDate);
			return str;
		}
		
		public static  String getSimpleCurDate() {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间 当前时区
			String str = formatter.format(curDate);
			return str;
		}
		 /**
	     * 将Bitmap转换成指定大xiao
	     * @param bitmap
	     * @param width
	     * @param height
	     * @return
	     */
		 public static Bitmap createBitmapBySize(Bitmap bitmap, int width, int height) {
		        if(bitmap != null){
			 return Bitmap.createScaledBitmap(bitmap, width, height, true);
		        }
		        return null;
		    }
		 
			/**
			 * Resize the bitmap
			 * 
			 * @param bitmap
			 * @param width
			 * @param height
			 * @return
			 */
			public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();
				Matrix matrix = new Matrix();
				float scaleWidth = ((float) width / w);
				float scaleHeight = ((float) height / h);
				matrix.postScale(scaleWidth, scaleHeight);
				Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
				return newbmp;
			}
			
		 public static  Bitmap getimage(String srcPath) {
				BitmapFactory.Options newOpts = new BitmapFactory.Options();
				//开始读入图片，此时把options.inJustDecodeBounds 设回true了
				newOpts.inJustDecodeBounds = true;
				Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
				
				newOpts.inJustDecodeBounds = false;
				int w = newOpts.outWidth;
				int h = newOpts.outHeight;
				GlobalApplication mIns = GlobalApplication.getInstance();
				//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
				float hh =800*2f;//这里设置高度为800f
				float ww =480*2f;//这里设置宽度为480f
				//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
				int be = 1;//be=1表示不缩放
				if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
					be = (int) (newOpts.outWidth / ww);
				} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
					be = (int) (newOpts.outHeight / hh);
				}
				if (be <= 0)
					be = 1;
				newOpts.inSampleSize = be;//设置缩放比例
				//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
				bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
				return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
			}
		 
		public static Bitmap compressImage(Bitmap image) {
			
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 60, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
				while (baos.toByteArray().length / 1024>300) {	//循环判断如果压缩后图片是否大于500kb,大于继续压缩		
					ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
					Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
					baos.reset();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);//这里压缩options%，把压缩后的数据存放到baos中	
				}
				ByteArrayInputStream isBm1 = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
				Bitmap bitmap1 = BitmapFactory.decodeStream(isBm1, null, null);//把ByteArrayInputStream数据生成图片
				return bitmap1;
				
			}
		
		public static boolean isFileExist(String path){

	        File f1 = new File(path );  
	        if(f1.exists() ){
	        	f1 =null;
	          return true;
        }
	        return false;
		}
		
	   /**
		 * 将图片文件保存到SD卡下  
		 * 
		 *
		 */
		public static boolean  writeToSD(String path,Bitmap m,String bitName) {
			try {
			if(!(FileUtil.isSDCardExist())) return false;
				//创建文件对象，用来存储新的图像
		        File f1 = new File(Environment.getExternalStorageDirectory()  
		                + "/LoverHouse" + path );  
		        if(!f1.exists() ){
		              f1.mkdirs();     //如果文件夹不存在 则建立新文件

		        
	        }
		        File f = new File(Environment.getExternalStorageDirectory()  
		                + "/LoverHouse" + path+"/"+bitName + ".png");  
		        if (!deleteFile(f))
		        	return false;
		        try   
		        {  
		            //创建文件  
		            f.createNewFile();  
		        } catch (IOException e)   
		        {  
		            return false; 
		        
		        }  
		      //定义文件输出
		        FileOutputStream fOut = null;  
		        try {  
		            fOut = new FileOutputStream(f);  
		        } catch (FileNotFoundException e) {  
		        
		            e.printStackTrace();  
		        	return false;
		        }  
		        //将bitmap存储
		        m.compress(Bitmap.CompressFormat.PNG, 80, fOut);  
		        try   
		        {  
		            //刷新文件liu
		            fOut.flush();  
		        } catch (IOException e)   
		        {  
		            e.printStackTrace();  
		        }  
		        try   
		        {  
		            //关闭文件liu
		            fOut.close();  
		        } catch (IOException e)   
		        {  
		            e.printStackTrace();  
		        }  
				
		        return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		
		/**
		 * 从SD卡中读取图片文件 这里的完整的路径base+ path + "/"+xx.png
		 * 
		 */
		
		public static  Bitmap getDiskBitmap(String pathString)
		{
			Bitmap bitmap = null;

			try
			{
				File file = new File(pathString);
				if(file.exists())
				{
					bitmap = BitmapFactory.decodeFile(pathString);
				}
			} catch (Exception e)
			{// TODO: handle exception
//				Log.v("result", "exception in get bitmap");
				 return null;
			}
	          return bitmap;
		}
		
//		将SD卡文件删chu
	    public static boolean  deleteFile(File file)
	    {
	     if(sdState.equals(Environment.MEDIA_MOUNTED))
	     {
	      if (file.exists())
	      {
	       if (file.isFile())
	       {
	       return  file.delete();
	       }
	      }else{
	    	  return true; // file not exist
	      }
	     }
	     return false;
	    }
	    
	    /** 
		* 转换图片成圆xing
		* @param bitmap 传入Bitmap对象 
		* @return 
		*/ 
		public static Bitmap toRoundBitmap(Bitmap bitmap) { 
    
		int width = bitmap.getWidth(); 
		int height = bitmap.getHeight(); 
		float roundPx; 
		float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom; 
		if (width <= height) { 
		roundPx = width / 2; 
		top = 0; 
		bottom = width; 
		left = 0; 
		right = width; 
		height = width; 
		dst_left = 0; 
		dst_top = 0; 
		dst_right = width; 
		dst_bottom = width; 
		} else { 
		roundPx = height / 2; 
		float clip = (width - height) / 2; 
		left = clip; 
		right = width - clip; 
		top = 0; 
		bottom = height; 
		width = height; 
		dst_left = 0; 
		dst_top = 0; 
		dst_right = height; 
		dst_bottom = height; 
		} 
		Bitmap output = Bitmap.createBitmap(width, 
		height, Config.ARGB_8888); 
		Canvas canvas = new Canvas(output); 
		final int color =0xffa19774; 
		final Paint paint = new Paint(); 
		final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom); 
		final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom); 
		final RectF rectF = new RectF(dst); 
		paint.setAntiAlias(true); // 设置画笔无锯齿 
		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas  
		paint.setColor(color); 
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); //设置两张图片相交时的模式
		canvas.drawBitmap(bitmap, src, dst, paint); 
		return output; 
		} 

		
		//读SD中的文件
		public static byte[] readFileSdcardFile(String filePath) throws IOException{ 
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    
			 if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) { 
	        // content  
	        FileInputStream fis = new FileInputStream(filePath);  
	    
	        int len = -1;
	        byte[] array = new byte[1024];
	        while( (len = fis.read(array)) != -1){
		                            bos.write(array,0,len); 
		                            }
	                 bos.close();
		            fis.close();
			 }
		       return bos.toByteArray();
		
		}
		
		public static void saveFileToSDCard(String path,String name,  byte[] content) throws Exception  {
			if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//				File f = Environment.getExternalStorageDirectory();//获取SD卡目lu
				//创建文件对象，用来存储新的图像文jian
		        File f1 = new File(Environment.getExternalStorageDirectory()  
		                + "/LoverHouse" + path );  
		        if(!f1.exists() ){
		              f1.mkdirs();     //如果文件夹不存在 则建立新文件

		        
	        }
		        File f = new File(Environment.getExternalStorageDirectory()  
		                + "/LoverHouse" + path+"/"+name);  
		        try   
		        {  
		            //创建文件  
		            f.createNewFile();  
		        } catch (IOException e)   
		        {  
		       
		        }  
				
				
			
				FileOutputStream os = new FileOutputStream(f);
				try {
				    os.write(content);
				    os.close();
				
				} catch (IOException e) {
			
				    e.printStackTrace();
		
				}			 }
		}

/** 
     * 得到amr的时chang
     *  
     * @param file 
     * @return 
     * @throws IOException 
     */  
    public static long getAmrDuration(File file) throws IOException {  
        long duration = -1;  
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };  
        RandomAccessFile randomAccessFile = null;  
        try {  
            randomAccessFile = new RandomAccessFile(file, "rw");  
            long length = file.length();//文件的长du
            int pos = 6;//设置初始位置  
            int frameCount = 0;//初始帧数  
            int packedPos = -1;  
            /////////////////////////////////////////////////////  
            byte[] datas = new byte[1];//初始数据
            while (pos <= length) {  
                randomAccessFile.seek(pos);  
                if (randomAccessFile.read(datas, 0, 1) != 1) {  
                    duration = length > 0 ? ((length - 6) / 650) : 0;  
                    break;  
                }  
                packedPos = (datas[0] >> 3) & 0x0F;  
                pos += packedSize[packedPos] + 1;  
                frameCount++;  
            }  
            /////////////////////////////////////////////////////  
            duration += frameCount * 20;//帧数*20  
        } finally {  
            if (randomAccessFile != null) {  
                randomAccessFile.close();  
            }  
        }  
        return (duration+(long)0.5);  
    }   
    
    public static Builder openAlertDialog(Context mContext, String title, String message, String positiveBtnText, 
            String negativeBtnText, View.OnClickListener positiveBtnListener, 
           View.OnClickListener negativeBtnListener, boolean isCancel)
{

		Builder ibuilder = new com.minius.ui.CustomDialog.Builder(mContext);
		ibuilder.setTitle(title);
		
		ibuilder.setMessage(message);
		ibuilder.setPositiveButton(positiveBtnText,positiveBtnListener);
		ibuilder.setNegativeButton(negativeBtnText, negativeBtnListener);
		ibuilder.create().show();
		return ibuilder;

		
	}
    
    public static void Vibrate(final Activity activity, long milliseconds) {   
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);   
        vib.vibrate(milliseconds);   
 }   
 public static void Vibrate(final Activity activity, long[] pattern,boolean isRepeat) {   
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);   
        vib.vibrate(pattern, isRepeat ? 1 : -1);   
 }   
 
 /** 
  * 设置亮度 
  * 
  * @param activity 
  * @param brightness 
  */ 
 public static void setBrightness(Activity activity, int brightness) { 
     // Settings.System.putInt(activity.getContentResolver(), 
     // Settings.System.SCREEN_BRIGHTNESS_MODE, 
     // Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL); 
     WindowManager.LayoutParams lp = activity.getWindow().getAttributes(); 
     lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f); 
     activity.getWindow().setAttributes(lp); 

 } 
 /** 
  * 停止自动亮度调节 
  * 
  * @param activity 
  */ 
 public static void stopAutoBrightness(Activity activity) { 
     Settings.System.putInt(activity.getContentResolver(), 
             Settings.System.SCREEN_BRIGHTNESS_MODE, 
             Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL); 
 } 
 
 /**  
	2.    * create the bitmap from a byte array  
	3.    * 
	4.    * @param src the bitmap object you want proecss 
	5.    * @param watermark the water mark above the src 
	6.    * @return return a bitmap object ,if paramter's length is 0,return null 
	7.    */  
	 public static  Bitmap mergeBitmap( Bitmap src, Bitmap watermark ,int offsetx, int offsety)   
	   {   
	    
	        if( src == null )   
            {   
	                return null;   
        }   

                 int w = src.getWidth();   
                int h = src.getHeight();   
//                 int ww = watermark.getWidth();   
//	               int wh = watermark.getHeight();   
	             //create the new blank bitmap   
               Bitmap newb = Bitmap.createBitmap( w, h, Config.ARGB_8888 );//创建一个新的和SRC长度宽度一样的位图   
              Canvas cv = new Canvas( newb );   
                //draw src into   
                 cv.drawBitmap( src, 0, 0, null );//在 0，0坐标开始画入src   
                //draw watermark into   
                   cv.drawBitmap( watermark,offsetx,offsety, null );
                //save all clip   
                cv.save( Canvas.ALL_SAVE_FLAG );//保存   
               //store   
                cv.restore();//存储   
              return newb;   
 }  
	 
	 public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } else {
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }
	    }
	    return inSampleSize;
	}
	 
	 public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
		        int reqWidth, int reqHeight) {

		    // First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeResource(res, resId, options);

		    // Calculate inSampleSize
		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    return BitmapFactory.decodeResource(res, resId, options);
		}
	 
	 public static Bitmap readBitmapAutoSize(String filePath, int outWidth, int outHeight) {  
         //outWidth和outHeight是目标图片的最大宽度和高度，用作限制
	FileInputStream fs = null;
	BufferedInputStream bs = null;
	try {
		fs = new FileInputStream(filePath);
		bs = new BufferedInputStream(fs);
		BitmapFactory.Options options = setBitmapOption(filePath, outWidth, outHeight);
		return BitmapFactory.decodeStream(bs, null, options);
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			bs.close();
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	return null;
}

private static BitmapFactory.Options setBitmapOption(String file, int width, int height) {
	BitmapFactory.Options opt = new BitmapFactory.Options();
	opt.inJustDecodeBounds = true;          
         //设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
	BitmapFactory.decodeFile(file, opt);

	int outWidth = opt.outWidth; //获得图片的实际高和宽
	int outHeight = opt.outHeight;
	opt.inDither = false;
	opt.inPreferredConfig = Bitmap.Config.RGB_565;    
         //设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
	opt.inSampleSize = 1;                          
         //设置缩放比,1表示原比例，2表示原来的四分之一....
         //计算缩放比
	if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
		int sampleSize = (outWidth / width + outHeight / height) / 2;
		opt.inSampleSize = sampleSize;
	}

	opt.inJustDecodeBounds = false;//最后把标志复原
	return opt;
}

public static int dip2px(Context context, float dpValue) { 
final float scale = context.getResources().getDisplayMetrics().density ; 
return (int) (dpValue * scale + 0.5f) ;
 }
public void showNotification(Context ctx,String statusTile,
		String title,String content,int id){
	NotificationManager myNotificationManager = (NotificationManager) ctx.getSystemService(
			android.content.Context.NOTIFICATION_SERVICE); 
	//define 
	Notification myNotification = new Notification(
			R.drawable.ic_launcher,statusTile, System.currentTimeMillis());
	myNotification.flags |= Notification.FLAG_ONGOING_EVENT;
	 myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        myNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
	myNotification.defaults = Notification.DEFAULT_LIGHTS;
	myNotification.ledARGB = Color.RED;
	myNotification.ledOnMS = 5000;
	
	//设置通知时间消息

	Intent notificationIntent = new Intent(ctx, this.getClass());
	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	PendingIntent contentIntent = PendingIntent.getActivity
			(ctx.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	myNotification.setLatestEventInfo(ctx,title, content, contentIntent);
	
	myNotificationManager.notify(id,myNotification);

}

public void cancelNotification(int id, Context ctx){
	NotificationManager myNotificationManager = (NotificationManager) ctx.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
	myNotificationManager.cancel(id);
}


}
