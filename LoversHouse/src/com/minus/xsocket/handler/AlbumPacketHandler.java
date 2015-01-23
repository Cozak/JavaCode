package com.minus.xsocket.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.sql_interface.Database;
import com.minus.table.GalleryTable;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.util.NetDataTypeTransform;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;

public class AlbumPacketHandler {

	static ByteArrayOutputStream  picData;
	public String setLength(int len){
	    String  mL = String.format("%04d", len);
//	    Log.v("comm ml", mL);
	    return mL;
	}
	  private int getIntFromByte(byte[] str ,int len)
	  {
	      int total = 0;
	      for (int i = 0; i < len; i++)
	      {
	          total += (int)(str[i] - '0') * Math.pow(10.0, len - i - 1);
	      }
	      return total;
	  }
	  
	//根据用户创建日期返回该image;
	  public  void getImageWithInitDate(String initDate)
	  {
	      String packet =  initDate;
	      int len = 0;
			try {
				len = packet.getBytes("UTF-8").length+1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		     String lenStr = this.setLength(len);
		     StringBuilder mSB = new StringBuilder();
			    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.PICTURE_PACKAGE)
			    .append(Protocol.ALBUM_GET_IMAGE).append(lenStr).append(packet).append('\0');
			    AsynSocket.getInstance().sendData(mSB.toString());
			
	  }

	  //删除相片
	  public void removeImageWithInitDate(String initDate)
	  {
		  String packet =  initDate;
	      int len = 0;
			try {
				len = packet.getBytes("UTF-8").length+1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		     String lenStr = this.setLength(len);
		     StringBuilder mSB = new StringBuilder();
			    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.PICTURE_PACKAGE)
			    .append(Protocol.ALBUM_REMOVE_IMAGE).append(lenStr).append(packet).append('\0');
			    AsynSocket.getInstance().sendData(mSB.toString());
	      
	   
	  }


	  //获取只读最后修改时间   0x03
	  public void getAlbumLastModifyTime()
	  {
	      int len = 1;
	      String lenStr = this.setLength(len);
		     StringBuilder mSB = new StringBuilder();
			    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.PICTURE_PACKAGE)
			    .append(Protocol.ALBUM_GET_LAST_MODIFY_TIME).append(lenStr).append('\0');
			    AsynSocket.getInstance().sendData(mSB.toString());

	  }
	  
	//获取可改最后修改时间   0x04
	  public void updateAlbumLastModifyTime()
	  {
	      int len = 1;
	      String lenStr = this.setLength(len);
		     StringBuilder mSB = new StringBuilder();
			    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.PICTURE_PACKAGE)
			    .append(Protocol.ALBUM_GET_SERVER_LAST_MODIFY_TIME).append(lenStr).append('\0');
			    AsynSocket.getInstance().sendData(mSB.toString());

	  }

	  //获取所有图片的时间列表
	  public void getAlbumTimeList()
	  {
	      int len = 1;
	      String lenStr = this.setLength(len);
		     StringBuilder mSB = new StringBuilder();
			    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.PICTURE_PACKAGE)
			    .append(Protocol.ALBUM_GET_TIME_LIST).append(lenStr).append('\0');
			    AsynSocket.getInstance().sendData(mSB.toString());
	  }
	  
		//设置相册主页显示
	  public void setHomePageWithInitDate(String initDate)
	  {
	      String packet = initDate;
	      int len = initDate.length() + 1;
	      String lenStr = setLength(len);
	      StringBuilder mSB = new StringBuilder();
		    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.USER_PACKAGE)
		    .append(Protocol.ALBUM_HOMEPAGE).append(lenStr).append(packet).append('\0');
		    AsynSocket.getInstance().sendData(mSB.toString());
	  }


	  //上传图片第一部分
	  
	  public void UploadFirstPictureData(String CreateTime,byte[] image,int imageLength){
			String packetTemp =  CreateTime + ' ' + imageLength + ' ';
			int len = packetTemp.length() + image.length + 1;
			String lenStr = setLength(len);
			StringBuilder mSB = new StringBuilder();
			mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.PICTURE_PACKAGE)
			.append(Protocol.ALBUM_UPLOAD_FIRST_DATA).append(lenStr).append(packetTemp);
			
			
			byte[] packet = null;
			byte[] destArray = null;
			
			try {
				packet = mSB.toString().getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				baos.write(packet);
				baos.write(image);
				baos.write('\0');
				destArray = baos.toByteArray();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(destArray == null){
				return ;
			}
			AsynSocket.getInstance().sendImgData(destArray);
		}

	  //上传图片接下来部分
	  public void UploadAppendPictureData(String createTime,byte[] image){
			
			int len =createTime.length() + 1 + image.length + 1;
			String lenStr = setLength(len);
			StringBuilder mSB = new StringBuilder();
			mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.PICTURE_PACKAGE)
			.append(Protocol.ALBUM_UPLOAD_FOLLOW_DATA).append(lenStr)
			.append(createTime).append(' ');
			byte[] packet = null;
			byte[] destArray = null;
			try {
				packet = mSB.toString().getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				baos.write(packet);
				baos.write(image);
				baos.write('\0');
				destArray = baos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(destArray == null){
			
				return ;
			}
			AsynSocket.getInstance().sendImgData(destArray);
		}

	  //上传图片最后一部分
		public void UploadPictureFinish(String CreateTime,int PciLength){
			String packet = CreateTime + ' ' + PciLength;
			int len = packet.length() + 1;
			String lenStr = setLength(len);
			StringBuilder mSB = new StringBuilder();
			mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.PICTURE_PACKAGE)
			.append(Protocol.ALBUM_UPLOAD_FINISH_DATA).append(lenStr).append(packet).append('\0');
			AsynSocket.getInstance().sendData(mSB.toString());
		}
	  //发送信息给服务器中止上传图片;
	  public void sendStopUpload()
	  {
	      int len = 1;
	  	String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.PICTURE_PACKAGE)
		.append(Protocol.ALBUM_STOP_UPLOAD).append(lenStr).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	  }

	  //处理删除之前没有成功删除的;
	  public void dealWithWaitForRemovingImages()
	  {
	      //进来首先把之前删除失败的更新给服务器;
	      //处理那些用户之前删除的，但是还没有成功在服务器成功删除掉的图片;
	      List<GalleryTable> removingImages =Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
	    		  .FinePicForDelete();
	      for (GalleryTable mGt : removingImages)
	      {
	        removeImageWithInitDate(mGt.getLastModefyTime());
	      }
	  }
	  

	  
	  
	  //响应

	// 接收到图片
	public void receiveImage(byte[] str)
	{
	    char type = (char)str[3];
	    byte[] picLenByte = new byte[4];
	    System.arraycopy(str,4, picLenByte, 0, 4);
	    int picLen =this.getIntFromByte(picLenByte, 4);
	  
	    picLen = picLen - 19 - 1 - 1;   //19是日期的长度，1个空格，1个结束符;
	    int beginIndex = 8 + 19 + 1;    //图片开始的下标;
	    
	    if (type == Protocol.ALBUM_RETURN_FIRST_DATA)
	    {
	    	if( picData != null) {
	    		 try {
	    			 picData.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
	    		 picData = null;
	    	 }
	         picData = new ByteArrayOutputStream();     
	         picData.write(str, beginIndex, picLen);
//	        ImageData = [[NSMutableData alloc] initWithBytes:str+beginIndex length:picLen];
	    }
	    else if(type ==Protocol. ALBUM_RETURN_FOLLOW_DATA)
	    {
	    	   picData.write(str, beginIndex, picLen);
	    }
	    else if(type == Protocol.ALBUM_RETURN_FINISH_DATA)
	    {
	    	 NetDataTypeTransform mND = new  NetDataTypeTransform();
			 String mess1 = mND.ByteArraytoString(str, str.length);
	         String mess =mess1.substring(Protocol.HEAD_LEN);
	         String[] arr = mess.split(" ");
	         String  imageDateStr = arr[0];
	         String  imageLenStr = arr[1];
	         byte[] lenBytes = null;
			try {
				lenBytes = imageLenStr.getBytes("utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	         int imageLen = this.getIntFromByte(lenBytes, lenBytes.length);
	    
	        //如果接收到的图片长度和服务器发过来的长度不一样的话那么就要重新获取;
	        if (imageLen !=picData.size()) {
	        	if( picData != null) {
		    		 try {
		    			 picData.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}   
		    		 picData = null;
		    	 }
	           getImageWithInitDate(imageDateStr);
	            return ;
	        }
	        
	    	 if(picData == null) return;
	         //写入文件中;
	        	 Date dateTemp = AppManagerUtil.StrToDate(imageDateStr);
	    		String timeStamp = AppManagerUtil.DateToStr(dateTemp);
	    		byte[] destAray = picData.toByteArray();
	    	    Bitmap m =  BitmapFactory.decodeByteArray(destAray, 0, destAray.length);
	    	   
	    	   String photopath = Environment.getExternalStorageDirectory()  
		                + "/LoverHouse" + "/Album"+"/ori/"+ timeStamp + ".png";
			   AppManagerUtil.writeToSD("/Album/ori", m, timeStamp);
	    	   //生成缩略图
	        Bitmap thumbnailBm =ThumbnailUtils.extractThumbnail(m, 60, 60);
	        
	        String thumbnailpath = Environment.getExternalStorageDirectory()  
	                + "/LoverHouse" + "/Album"+"/"+ timeStamp + ".png";
		    AppManagerUtil.writeToSD("/Album", thumbnailBm, timeStamp);
//	        BOOL isWriteOriSucc = NO;
//	        BOOL isWriteThuSucc = NO;
	        //接收到的图片写入原图文件路径中，同时制作一个缩略图;
//	        NSString *imageFilePath = [picDir stringByAppendingPathComponent:dateStr];
//	        NSString *oriImageFilePath = [imageFilePath stringByAppendingString:@"ori.jpg"];  //原图的路径
//	        isWriteOriSucc = [ImageData writeToFile:oriImageFilePath atomically:YES];
	        
//	        //制作缩略图;
//	        NSString *thuImageFilePath = [imageFilePath stringByAppendingString:@".jpg"];
//	        UIImage *oriImage = [UIImage imageWithData:ImageData];
//	        CGSize thumbnailSize = oriImage.size;
//	        thumbnailSize.height = PerImageHeight * 2;
//	        thumbnailSize.width = PerImageWidth * 2;
//	        UIImage *thumbnailImage = [FunctionClass thumbnailWithImage:oriImage size:thumbnailSize];
//	        NSData *thumbnailData = UIImageJPEGRepresentation(thumbnailImage,1);
//	        isWriteThuSucc = [thumbnailData writeToFile:thuImageFilePath atomically:YES];
	        
	        //如果写入不成功的话，那么就要向服务器重新啦一起;
//	        if (!(isWriteThuSucc && isWriteOriSucc)) {
//	            ImageData = nil;
//	            [self getImageWithInitDate:imageDateStr];
//	            return ;
//	        }
	        //数据库中保存的是缩略图和原图的路径;
		   
		   Database db =Database.getInstance(GlobalApplication.getInstance().getApplicationContext());
		      db.saveAlbumPicture(imageDateStr, thumbnailpath,photopath);
//	        [[Database getInstance]addImageWithInitDate:imageDateStr ImagePath:thuImageFilePath];
//	        NSDictionary *newImage = [[NSDictionary alloc]initWithObjectsAndKeys:
//	                              imageDateStr, @"initDate", thuImageFilePath, @"imagePath", nil];
//	        NSDictionary *dict = [[NSDictionary alloc]initWithObjectsAndKeys:newImage, @"newImage", nil];
//	        [[NSNotificationCenter defaultCenter]postNotificationName:USNotificationAlbumGetImage object:self userInfo:dict];
//	        NSLog(@"成功接收到时间为：%@ 的图片",imageDateStr);
//	        ImageData = nil;
//	        
//	        [FunctionClass postLocationNotificationWithType:@"Album" AlertBody:@"您的相册有更新"];
		    
		      if( picData != null) {
		    		 try {
		    			 picData.close();
					} catch (IOException e) {
						e.printStackTrace();
					}   
		    		 picData = null;
		    	 }
	    
		      String content = imageDateStr + " "+thumbnailpath +" "+photopath;
		    	Intent intent = new Intent(Protocol.ACTION_ALBUM_GetImage_PACKET);
				intent.putExtra(Protocol.EXTRA_DATA,content);
				GlobalApplication.getInstance().sendBroadcast(intent);
				
				Intent notifyIntent = new Intent(Protocol.NotificationAlbumNew);
				GlobalApplication.getInstance().sendBroadcast(notifyIntent);
	    }
	}


	public void processAlbumUploadImage(byte[] str)
	{
	    char type = (char)str[3];
	    String content = "";
	    if (type == Protocol.ALBUM_UPLOAD_IMAGE_SUCC)
	    {
//	        NSLog(@"Upload photo success");
//	        [dict setValue:@"UploadImageSuccess" forKey:@"UploadImageState"];
	    	content = "UploadImageSuccess";
	    }
	    else if (type == Protocol.ALBUM_UPLOAD_IMAGE_FAIL)
	    {
//	        NSLog(@"Upload photo fail");
//	        [dict setValue:@"UploadImageFail" forKey:@"UploadImageState"];
	    	content = "UploadImageFail";
	    }
	    
	 
	    	Intent intent = new Intent(Protocol.ACTION_ALBUM_UploadImageState_PACKET);
			intent.putExtra(Protocol.EXTRA_DATA,content);
			GlobalApplication.getInstance().sendBroadcast(intent);
//	    [[NSNotificationCenter defaultCenter]postNotificationName:USNotificationAlbumUploadImageState object:self userInfo:dict];
	}

	//删除成功确认信息中包含删除的那张图片的时间;
	public void processRemoveImage(byte[] str)
	{
	    char type = (char)str[3];
	    NetDataTypeTransform mND = new  NetDataTypeTransform();
		 String mess = mND.ByteArraytoString(str, str.length);
	    String initDate =mess.substring(8);
	    if(type == Protocol.ALBUM_REMOVE_IMAGE_SUCC)
	    {
//	        NSLog(@"Remove image success");
	    	Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
	    	.deleteAlbumPicture(initDate);
//	        [[Database getInstance]removeImageWithInitDate:initDate];
	    }
	    else if(type == Protocol.ALBUM_REMOVE_IMAGE_FAIL)
	    {
//	        NSLog(@"Remove image fail");
	    }
	}

	public void processAlbumLastModifyTime(byte[] str)
	{
	    char type = (char)str[3];
	    GlobalApplication mGA =  GlobalApplication.getInstance();
	    NetDataTypeTransform mND = new  NetDataTypeTransform();
		 String mess = mND.ByteArraytoString(str, str.length);
//	    NSMutableDictionary *dict = [[NSMutableDictionary alloc]init];
	    String lastModifyTime = mess.substring(8);
	    if (type == Protocol.ALBUM_RETURN_READ_LAST_MODIFY_TIME)
	    {
	    	//返回只读的日期，用于判断
	    	Intent intent = new Intent(Protocol.ACTION_ALBUM_LastModifyTime_PACKET);
			intent.putExtra(Protocol.EXTRA_DATA,lastModifyTime);
			mGA.sendBroadcast(intent);
			
			if(!(mGA.isAlbumNewInit())){
				//说明是刚登陆成功在拉数据
				mGA.setAlbumNewInit(true);
				ControlHandler mCH = new ControlHandler();
				mCH.responseForAlbumLastModifyTime(mess);
	
			}
	    }
	    else
	    {
	    	SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount(), Activity.MODE_PRIVATE);
		      SharedPreferences.Editor mEditor = mSP.edit();
		      String key =  Protocol.PREFERENCE_AlbumLastModifyTime;
			  mEditor.putString(key, lastModifyTime);
			  mEditor.commit();
	    }
	}


  public void processAlbumTimeList(byte[] str)
	{
	  NetDataTypeTransform mND = new  NetDataTypeTransform();
     String mess = mND.ByteArraytoString(str, str.length);
     Intent intent = new Intent(Protocol.ACTION_ALBUMPACKET);
		intent.putExtra(Protocol.EXTRA_DATA,mess);
		GlobalApplication.getInstance().sendBroadcast(intent);

	}

 public void processReturnConfirm(byte[] str)
	{
          Intent intent = new Intent(Protocol.ACTION_ALBUM_ReturnConfirm_PACKET);

			GlobalApplication.getInstance().sendBroadcast(intent);

	}

	public void processAlbumRemoveOneImage(byte[] str)
	{
		  
		     NetDataTypeTransform mND = new  NetDataTypeTransform();
		     String mess = mND.ByteArraytoString(str, str.length);
		     String imageDate = mess.substring(Protocol.HEAD_LEN);
		     Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
		     .deleteAlbumPicture(imageDate);
		     
		     Intent intent = new Intent(Protocol.ACTION_ALBUM_RemoveOneImage_PACKET);
			intent.putExtra(Protocol.EXTRA_DATA,imageDate);
			GlobalApplication.getInstance().sendBroadcast(intent);
	}
	public void processAlbum(byte[] str)
	{
	    char type = (char)str[3];
	    switch (type)
	    {
	        case Protocol.ALBUM_RETURN_READ_LAST_MODIFY_TIME:
	        case Protocol.ALBUM_RETURN_WRITE_LAST_MODIFY_TIME:
	           processAlbumLastModifyTime(str);
	            break;
	            
	        case Protocol.ALBUM_RETURN_TIME_LIST:
	            processAlbumTimeList(str);
	            break;
	            
	        case Protocol.ALBUM_RETURN_FIRST_DATA:
	        case Protocol.ALBUM_RETURN_FOLLOW_DATA:
	        case Protocol.ALBUM_RETURN_FINISH_DATA:
	            receiveImage(str);
	            break;
	            
	        case Protocol.ALBUM_UPLOAD_IMAGE_SUCC:
	        case Protocol.ALBUM_UPLOAD_IMAGE_FAIL:
	          processAlbumUploadImage(str);
	            break;
	            
	        case Protocol.ALBUM_REMOVE_IMAGE_SUCC:
	        case Protocol.ALBUM_REMOVE_IMAGE_FAIL:
	           processRemoveImage(str);
	            break;
	            
	        case Protocol.AlBUM_RETURN_CONFIRM:
	           processReturnConfirm(str);
	            break;
	            
	        case Protocol.ALBUM_RETURN_REMOVE_IMAGE:
	           processAlbumRemoveOneImage(str);
	            break;
	    }
	}

}
