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

	//�������ģ�������Ϣ���Ѻ��ط����ܣ�
	public void dealWithNewNotificationAndWaitForSending()
	{
	    if (SelfInfo.getInstance().isOnline())
	    {
	    	GlobalApplication.getInstance().setControlDefault();
        //���������ȡ��������޸�ʱ��,������ص�����޸�ʱ��ͷ������Ĳ�һ���Ļ���˵���и���;
	        DiaryPacketHandler diaryHandler = new DiaryPacketHandler();
	        diaryHandler.getDiaryReadLastModifyTime();
	        
	        AlbumPacketHandler albumHandler = new  AlbumPacketHandler();
	        albumHandler.getAlbumLastModifyTime();
	        
	        CalendarHandler.getInstance().getLastModifyTimeRead();
	        
	        ChatPacketHandler chatHandler = new ChatPacketHandler();
	        chatHandler.getLastMessageDateWithAccount();
	    }
	}


	//��־
   public void responseForDiaryLastModifyTime(String str)
	{
	    String time =str.substring(Protocol.HEAD_LEN);;
	    //û������޸�ʱ��˵�����״�ʹ�ã��������ϲ�û�м�¼�С�
	    if (time.equals("")) {
	        return;
	    }
		SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(SelfInfo.getInstance().getAccount(),
						Activity.MODE_PRIVATE);
		String localLastModifyTime = mSP.getString(
				Protocol.PREFERENCE_DiaryLastModifyTime, "0000-00-00-00:00:00");
	 
	    //�������޸�ʱ��һ���Ļ�����ô�ʹ����ط�
	    if (localLastModifyTime.equals(time))
	    {
	        DiaryPacketHandler diaryHandler = new DiaryPacketHandler();
	        diaryHandler.dealWithWaitForAddModifyRemoveDiary();
	    }
	    else   //����Ϣ����;
	    {
	    	  Intent intent = new Intent(Protocol.NotificationDiaryNewMsg);    		
			  GlobalApplication.getInstance().sendBroadcast(intent);

	    }

	}


	//���
    public void responseForAlbumLastModifyTime(String str)
	{
	    String lastModifyTime = str.substring(Protocol.HEAD_LEN);
	    
	    //û������޸�ʱ��˵�����״�ʹ�ã��������ϲ�û�м�¼�С�
	    if (lastModifyTime.equals("")) {
	        return;
	    }
	    SharedPreferences mSP = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount(),
				Activity.MODE_PRIVATE);
		String key =Protocol.PREFERENCE_AlbumLastModifyTime;
		String localLastModifyTime = mSP.getString(key, "0000-00-00-00:00:00");
	
	    //���һ���Ļ�����ô�ʹ����ط���
	    if (lastModifyTime.equals(localLastModifyTime))
	    {
	        AlbumPacketHandler albumHandler = new AlbumPacketHandler();
	        albumHandler.dealWithWaitForRemovingImages();
	    }
	    else   //����Ϣ����
	    {
	    	  Intent intent = new Intent(Protocol.NotificationAlbumNew);   		
			  GlobalApplication.getInstance().sendBroadcast(intent);
		  
	    }
//	    [[NSNotificationCenter defaultCenter]removeObserver:self name:USNotificationAlbumLastModifyTime object:nil];
	}


	//������;
    public void responseForMemoDayLastModifyTime()
  {
//	   TODO
	
  }

	//����
  public void receiveLastMsgDate(String str)
	{
	  GlobalApplication mGA = GlobalApplication.getInstance();
	  String lastDateFromServer =str.substring(8);;
		// �������Ϊ�յĻ���˵����������û��������Ϣ.
		if (lastDateFromServer.equals("")) {
			return;
		}
		String lastDateLocal =
				Database.getInstance(mGA.getApplicationContext()).getLastMsgDate();
		if (lastDateLocal.equals("")) {
			lastDateLocal = "0000-00-00-00:00:00";
		} 
//TODO  ʲôʱ�������ݱȽϺ���
		if (!(lastDateFromServer.equals(lastDateLocal))) {
			ChatPacketHandler chatHandler = new ChatPacketHandler();
			chatHandler.getMessageWithAccount(lastDateLocal,
					lastDateFromServer);
			chatHandler = null;
		}
	    Date serverDate = AppManagerUtil.StrToDate(lastDateFromServer);
	    Date localDate = AppManagerUtil.StrToDate(lastDateLocal);
	    
	    int timeInterval = serverDate.compareTo(localDate);
	 
	    //�������ϵ�ʱ��Ƚ���˵��������Ϣ��
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
