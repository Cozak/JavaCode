package com.minus.gallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.sql_interface.Database;
import com.minus.table.GalleryTable;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.AlbumPacketHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class AlbumImageHandler {

	//�ϴ�ͼƬ
		private boolean isUploadingImage= false;
		private int uploadImagePacketIndex = 0;
		private String uploadImageDate =null;
	    private Bitmap uploadImageData = null;
	    private Handler pgHandler = null;
	    private  AlbumImageHandler(){
		  super();
		  init();
	
	}
	    private int  PerImageWidthdp  = 60;
		private int    PerImageHeightdp  = 70;
		private int  PerImageWidthpx  = 60;
	   private int    PerImageHeightpx  = 70;
	
	private void init(){
		 PerImageWidthpx  = AppManagerUtil.dip2px(GlobalApplication.getInstance().getApplicationContext(), PerImageWidthdp);
		 PerImageHeightpx  = AppManagerUtil.dip2px(GlobalApplication.getInstance().getApplicationContext(), PerImageHeightdp);
	}
	/**
	 * ����ģʽ,�߳�
	 * @return
	 */
		
		private static class  AlbumImageHandlerContainer{
			private static AlbumImageHandler  instance = new AlbumImageHandler();
		}
		
		public static AlbumImageHandler  getInstance(){
		    
			  return  AlbumImageHandlerContainer.instance;
		}
		
		//ȡ���ϴ�
		public void onClickedDismissUploadImage()
		{
		    isUploadingImage = false;
		
		    AlbumPacketHandler albumHandler = new AlbumPacketHandler();     //���߷�������ֹ����ͼƬ;
		    albumHandler.sendStopUpload();
		
		    if (uploadImageData != null && !uploadImageData.isRecycled()) {  
		    	uploadImageData.recycle();  
		    	uploadImageData = null;  
	        }  
		
			Message msg = pgHandler.obtainMessage();				
		
						
			msg.what =Protocol.HANDLE_ALBUM_CANCELUPLOADIMG;
			pgHandler.sendMessage(msg);	

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
//	            Log.e("test", "cannot read exif", ex);
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
	

		//���浽���ݿ��в��ҷ���ͼƬ;
	public  GalleryTable saveAndSendImage(String oriPath)
		{
		    //����image�ĳߴ�800 *480
		
		  
		    //��ͼƬ��С���еȱ���ѹ��-- ,300kb
		this.uploadImageData = AppManagerUtil.getimage(oriPath);
		 
		
		int angle= getExifOrientation(oriPath);
		  if(angle!=0){  //�����Ƭ������ ��ת ��ô �͸�����ת����
		                      Matrix matrix = new Matrix();
		                      matrix.postRotate(angle);
		                      this.uploadImageData = Bitmap.createBitmap(this.uploadImageData,
		                      0, 0, this.uploadImageData.getWidth(), this.uploadImageData.getHeight(), matrix, true);
		                  }
		  //ͼƬд���ļ��У����Ұ��ļ�·��������ݿ���.
	
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
	
		String thumbnailDate =formatter1.format(curDate) ;    
        String thumbnailpath = Environment.getExternalStorageDirectory()  
	                + "/LoverHouse" + "/Album"+"/"+thumbnailDate + ".png";
		  
		    
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
			String currDate = formatter.format(curDate);
			
		    String photopath = Environment.getExternalStorageDirectory()  
		                + "/LoverHouse" + "/Album"+"/ori/"+ thumbnailDate + ".png";
		    AppManagerUtil.writeToSD("/Album/ori", uploadImageData, thumbnailDate);
		    
		    
		    //��������ͼ;
			Bitmap thumbnailBm = ThumbnailUtils.extractThumbnail(uploadImageData, PerImageWidthpx, PerImageHeightpx);
		    AppManagerUtil.writeToSD("/Album", thumbnailBm,thumbnailDate);
		
		    //���ݿ��б������ͼ��·��;
		    Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
		    .saveAlbumPicture(currDate,thumbnailpath, photopath);
	
		  
		    //��������ϴ�ͼƬ
		    uploadImagePacketIndex = 0;
		    isUploadingImage = true;
		    uploadImageDate = currDate;
		
		   uploadImageWithAccount(SelfInfo.getInstance().getAccount(),uploadImageDate,uploadImageData,uploadImagePacketIndex);
		
		  GalleryTable mGT = new GalleryTable();
		  mGT.setDeleteStatus(0);
		  mGT.setLastModefyTime(currDate);
		  mGT.setOriPath(photopath);
		  mGT.setPath(thumbnailpath);
		
		    return mGT;
		}

	//packetIndex��ָ��i��������˼;
	public void uploadImageWithAccount(String account ,String date,Bitmap imageData ,int packetIndex)
	{
		byte[] picData = Bitmap2Bytes(imageData);
	    AlbumPacketHandler  albumHandler = new AlbumPacketHandler();
	    //���ͳ�ȥ;
	    int firstPacketPicLen = 8192 - 8 - account.length() - date.length() - (String.format("%d", picData.length)).length() - 4;
	    int appendPacketPicLen = 8192 - 8 - account.length() - date.length() - 3;
	    
	    //ͼƬ��С��һ����С
	    if (packetIndex == 0 && picData.length <= firstPacketPicLen)
	    {
	        albumHandler.UploadFirstPictureData(date, picData, picData.length);
	        return ;
	    }else if (packetIndex == 1 && picData.length <= firstPacketPicLen)
        {
	        
        	//˵����ʱ�����Ѿ���һ�����ݰ� ������
	    	
//            return ;
        }
	    else
	    {
	    	int appendPacketNum = (picData.length - firstPacketPicLen)
					/ appendPacketPicLen + 1;
	    	
	    	
	    	byte[] firstPacket = new byte[firstPacketPicLen];
	    	System.arraycopy(picData, 0, firstPacket, 0, firstPacketPicLen);
		
	        
	        //����һ�½�����
			Bundle bdata = new Bundle();
			bdata.putInt("pg", packetIndex);
			bdata.putInt("pgbase", appendPacketNum + 1);
		
			Message msg = pgHandler.obtainMessage();				
			msg.setData(bdata);
						
			msg.what =Protocol.HANDLE_ALBUM_SETPROGRESSBAR;
			pgHandler.sendMessage(msg);	
	    	//TODO
//	        [self updateProgressWithNumerator:packetIndex Denominator:(appendPacketNum + 1)];
	        
	        //ֻ���͵�һ����
	        if (packetIndex == 0)
	        {
	            albumHandler.UploadFirstPictureData(date,firstPacket, picData.length);
	        
	            return;
	        }
	        else if (packetIndex < appendPacketNum)
	        {
	        	byte[] appendPacket = new byte[appendPacketPicLen];
				System.arraycopy(picData, firstPacketPicLen + (packetIndex-1)
						* appendPacketPicLen, appendPacket, 0,
						appendPacketPicLen);
				albumHandler.UploadAppendPictureData(date, appendPacket);
			
	            return ;
	        }
	        else if (packetIndex == appendPacketNum)
	        {
	        
	        	int lastLen = picData.length - firstPacketPicLen
						- appendPacketPicLen * (appendPacketNum - 1);
				byte[] lastAppendPacket = new byte[lastLen];
				System.arraycopy(picData, firstPacketPicLen + (appendPacketNum - 1)
						* appendPacketPicLen, lastAppendPacket, 0, lastLen);
				albumHandler.UploadAppendPictureData(date, lastAppendPacket);
				
	            return ;
	        }
	    }
	    //���������һ��У����֮�󣬽�����ȡ�������û��޷�ȡ���ϴ�ͼƬ�ˣ�Ĭ�����Ѿ��ɹ��ϴ���ȥ��,�����û�ȡ�������ɹ��ϴ������;
	   albumHandler.UploadPictureFinish(date, picData.length);
	   isUploadingImage =false;
	   //����һ�½�����
	   if (uploadImageData != null && !uploadImageData.isRecycled()) {  
	    	uploadImageData.recycle();  
	    	uploadImageData = null;  
       }  
	//����������Ϣ
		Message msg = pgHandler.obtainMessage();				
		msg.what =Protocol.HANDLE_ALBUM_UPLOADIMGFINISH;
		pgHandler.sendMessage(msg);	
		   // ȡ��������
		Message msg1 = pgHandler.obtainMessage();				
		msg1.what =Protocol.HANDLE_ALBUM_CANCELPG;
		pgHandler.sendMessageDelayed(msg1, 500);
	 
	}
	
	/**
	 * ��BitmapתByte
	 */
	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
		return baos.toByteArray();
	}

//	 ��Ӧ����;
	public void responseForUploadConfirmProgress()
	{
	    uploadImagePacketIndex ++;
	    if (isUploadingImage)
	    {
	       uploadImageWithAccount(SelfInfo.getInstance().getAccount(),uploadImageDate, uploadImageData,uploadImagePacketIndex);
	    }
	}
	
//	����handler
	public void setHandler(Handler mHandler){
		this.pgHandler = mHandler;
	}

	public boolean isUploadingImage() {
		return isUploadingImage;
	}

	public void setUploadingImage(boolean isUploadingImage) {
		this.isUploadingImage = isUploadingImage;
	}
	

}
