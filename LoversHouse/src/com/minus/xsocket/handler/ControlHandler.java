package com.minus.xsocket.handler;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.sql_interface.Database;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.util.NetDataTypeTransform;

public class ControlHandler {
	public String setLength(int len){
	    String  mL = String.format("%04d", len);
	 
	    return mL;
	}

	//处理各个模块的新消息提醒和重发功能；
	public void dealWithNewNotificationAndWaitForSending()
	{
	    if (SelfInfo.getInstance().isOnline())
	    {
	    	GlobalApplication.getInstance().setControlDefault();
        //向服务器获取各个最后修改时间,如果本地的最后修改时间和服务器的不一样的话，说明有更新;
	        DiaryPacketHandler diaryHandler = new DiaryPacketHandler();
	        diaryHandler.getDiaryReadLastModifyTime();
	        
	        AlbumPacketHandler albumHandler = new  AlbumPacketHandler();
	        albumHandler.getAlbumLastModifyTime();
	        
	        CalendarHandler.getInstance().getLastModifyTimeRead();
	        
	        ChatPacketHandler chatHandler = new ChatPacketHandler();
	        chatHandler.getLastMessageDateWithAccount();
	    }
	}


	//日志
   public void responseForDiaryLastModifyTime(String str)
	{
	    String time =str.substring(Protocol.HEAD_LEN);;
	    //没有最后修改时间说明是首次使用，服务器上并没有记录中。
	    if (time.equals("")) {
	        return;
	    }
		SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(SelfInfo.getInstance().getAccount(),
						Activity.MODE_PRIVATE);
		String localLastModifyTime = mSP.getString(
				Protocol.PREFERENCE_DiaryLastModifyTime, "0000-00-00-00:00:00");
	 
	    //如果最后修改时间一样的话，那么就处理重发
	    if (localLastModifyTime.equals(time))
	    {
	        DiaryPacketHandler diaryHandler = new DiaryPacketHandler();
	        diaryHandler.dealWithWaitForAddModifyRemoveDiary();
	    }
	    else   //新消息提醒;
	    {
	    	  Intent intent = new Intent(Protocol.NotificationDiaryNewMsg);    		
			  GlobalApplication.getInstance().sendBroadcast(intent);

	    }

	}


	//相册
    public void responseForAlbumLastModifyTime(String str)
	{
	    String lastModifyTime = str.substring(Protocol.HEAD_LEN);
	    
	    //没有最后修改时间说明是首次使用，服务器上并没有记录中。
	    if (lastModifyTime.equals("")) {
	        return;
	    }
	    SharedPreferences mSP = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount(),
				Activity.MODE_PRIVATE);
		String key =Protocol.PREFERENCE_AlbumLastModifyTime;
		String localLastModifyTime = mSP.getString(key, "0000-00-00-00:00:00");
	
	    //如果一样的话，那么就处理重发。
	    if (lastModifyTime.equals(localLastModifyTime))
	    {
	        AlbumPacketHandler albumHandler = new AlbumPacketHandler();
	        albumHandler.dealWithWaitForRemovingImages();
	    }
	    else   //新消息提醒
	    {
	    	  Intent intent = new Intent(Protocol.NotificationAlbumNew);   		
			  GlobalApplication.getInstance().sendBroadcast(intent);
		  
	    }
//	    [[NSNotificationCenter defaultCenter]removeObserver:self name:USNotificationAlbumLastModifyTime object:nil];
	}


	//纪念日;
    public void responseForMemoDayLastModifyTime()
  {
//	   TODO
	
  }

	//聊天
  public void receiveLastMsgDate(String str)
	{
	  GlobalApplication mGA = GlobalApplication.getInstance();
	  String lastDateFromServer =str.substring(8);;
		// 如果返回为空的话，说明服务器上没有聊天信息.
		if (lastDateFromServer.equals("")) {
			return;
		}
		String lastDateLocal =
				Database.getInstance(mGA.getApplicationContext()).getLastMsgDate();
		if (lastDateLocal.equals("")) {
			lastDateLocal = "0000-00-00-00:00:00";
		} 
//TODO  什么时候拉数据比较合理
		if (!(lastDateFromServer.equals(lastDateLocal))) {
			ChatPacketHandler chatHandler = new ChatPacketHandler();
			chatHandler.getMessageWithAccount(lastDateLocal,
					lastDateFromServer);
			chatHandler = null;
		}
	    Date serverDate = AppManagerUtil.StrToDate(lastDateFromServer);
	    Date localDate = AppManagerUtil.StrToDate(lastDateLocal);
	    
	    int timeInterval = serverDate.compareTo(localDate);
	 
	    //服务器上的时间比较晚，说明有新消息；
	    if (timeInterval> 0) {
	    	 Intent intent = new Intent(Protocol.NotificationChatNewMsg);		
			  GlobalApplication.getInstance().sendBroadcast(intent);
//	         [[NSNotificationCenter defaultCenter]postNotificationName:USNotificationChatNewMsg object:nil];
	    } else{
	    	ChatPacketHandler chatHandler = new ChatPacketHandler();
			chatHandler.dealWithSendingMsg();
			chatHandler = null;
	      
	    }
	}

}
