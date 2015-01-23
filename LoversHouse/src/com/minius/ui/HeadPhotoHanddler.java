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
	     * �õ� ͼƬ��ת �ĽǶ� ���������ֻ�������Ƭ �Զ���תһ���Ƕ�
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
       * ��ͷ�����ţ�������غ�
       * ���·�������û���sd�������ã�����Ĭ��ͷ��
       * @param imagePath
       * @param isBoy
       * @return
       */
      public Bitmap handleHeadPhoto(String imagePath,Bitmap frameBm,Context ctx){
    	  
    	  Bitmap headPhoto = null;
    	  if(FileUtil.isSDCardExist()){
    		  headPhoto= AppManagerUtil.getDiskBitmap(imagePath);
    		  int angle= getExifOrientation(imagePath);
    		  if(angle!=0){  //�����Ƭ������ ��ת ��ô �͸�����ת����
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
       * ���һ��ͷ�� 90*90 Բ�ε�
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
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
			
			if( baos.toByteArray().length / 1024>100) {	//�ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��		

				ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
				Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ
				baos.reset();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��
			
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ
			return bitmap;
		}
}
