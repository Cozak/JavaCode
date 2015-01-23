package com.minius.ui;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.minus.lovershouse.R;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.FileUtil;
import com.minus.lovershouse.util.RoundedImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.util.Log;

public class HeadPhotoHanddler {

	  public float dip2px(Context context, float dpValue) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return  (dpValue * scale + 0.5f);  
	    }  
	  /**
	     * 得到 图片旋转 的角度 适配三星手机调用照片 自动旋转一定角度
	     * @param filepath
	     * @return
	     */
	    public int getExifOrientation(String filepath) {
	        int degree = 0;
	        ExifInterface exif = null;
	        try {
	            exif = new ExifInterface(filepath);
	        } catch (IOException ex) {
	            Log.e("test", "cannot read exif", ex);
	        }
	        if (exif != null) {
	            int orientation = exif.getAttributeInt(
	                    ExifInterface.TAG_ORIENTATION, -1);
	            if (orientation != -1) {
	                switch (orientation) {
	                    case ExifInterface.ORIENTATION_ROTATE_90:
	                        degree = 90;
	                        break;
	                    case ExifInterface.ORIENTATION_ROTATE_180:
	                        degree = 180;
	                        break;
	                    case ExifInterface.ORIENTATION_ROTATE_270:
	                        degree = 270;
	                        break;
	                }
	            }
	        }
	        return degree;
	    }
	

      /**
       * 将头像缩放，并与框重合
       * 如果路径不可用或者sd卡不可用，返回默认头像
       * @param imagePath
       * @param isBoy
       * @return
       */
      public Bitmap handleHeadPhoto(String imagePath,Bitmap frameBm,Context ctx){
    	  
    	  Bitmap headPhoto = null;
    	  if(FileUtil.isSDCardExist()){
    		  headPhoto= AppManagerUtil.getDiskBitmap(imagePath);
    		  int angle= getExifOrientation(imagePath);
    		  if(angle!=0){  //如果照片出现了 旋转 那么 就更改旋转度数
    		                      Matrix matrix = new Matrix();
    		                      matrix.postRotate(angle);
    		                      headPhoto = Bitmap.createBitmap(headPhoto,
    		                      0, 0, headPhoto.getWidth(), headPhoto.getHeight(), matrix, true);
    		                  }
    	  }
    	  
    	  if(headPhoto == null){
    		  headPhoto = BitmapFactory.decodeResource(ctx.getResources(),
						R.drawable.photo);
    	  }
    	 
    		int offset = (int)dip2px(ctx, (float)8.0) ;
    		Bitmap resizedPictureBitmap = Bitmap.createScaledBitmap(headPhoto,frameBm.getWidth()-offset,frameBm.getHeight()-offset, true);
//    		Bitmap resizedPictureBitmap = Bitmap.createScaledBitmap(headPhoto,frameBm.getWidth(),frameBm.getHeight(), true);
//    				Bitmap.createScaledBitmap(testPicture, 90,90, true);
    		Bitmap roundBitmap =AppManagerUtil.toRoundBitmap(resizedPictureBitmap);
    		return (AppManagerUtil.mergeBitmap(frameBm,roundBitmap,offset/2,offset/2));   
      }
      /**
       * 获得一个头像 90*90 圆形的
       * @param origin
       * @return
       */
      public Bitmap getHeadPhoto(Bitmap origin){
    	  Bitmap temp = ThumbnailUtils.extractThumbnail(origin,90, 90);
    	  Bitmap roundBitmap =AppManagerUtil.toRoundBitmap(temp);
//    	  Bitmap roundBitmap = RoundedImageView.getCroppedBitmap(temp,45);
          return   roundBitmap;
      }
      
    //compress image
	  public  Bitmap compressImage(Bitmap image) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			
			if( baos.toByteArray().length / 1024>100) {	//判断如果压缩后图片是否大于100kb,大于继续压缩		

				ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
				Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
				baos.reset();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);//这里压缩options%，把压缩后的数据存放到baos中
			
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
			return bitmap;
		}
}
