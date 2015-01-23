package com.minius.service;


import com.minus.lovershouse.ChatActivity;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.WelcomeActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.ControlHandler;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 
 * ���ں�̨socket���ӻ�û��ʱ�����Ƿ����µ���Ϣ ���ﶯ�� �ռ� album�ȣ���post notification 
 * �Լ����������½�ɹ���������Ϣ���ط���
 * 
 * ��Ҫʵ�ֵĹ���
 * 1 �������½�ɹ����ж�����Ϣ�Լ������ط�
 * 
 *
 */
public class MiniusService extends IntentService {  
  
	
	private ControlHandler controlHandler = null;
    public MiniusService() {  
        //����ʵ�ָ���Ĺ��췽��  
        super("com.minius.service.MiniusService");  
    }  
      
    @Override  
    public IBinder onBind(Intent intent) {  
//        System.out.println("onBind");  
        return super.onBind(intent);  
    }  
  
  
    @Override  
    public void onCreate() {  
//        System.out.println("onCreate");  
        super.onCreate();  
    }  
  
    @Override  
    public void onStart(Intent intent, int startId) {  
//        System.out.println("onStart");  
        super.onStart(intent, startId);  
    }  
  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
//        System.out.println("onStartCommand");  
        return super.onStartCommand(intent, flags, startId);  
    }  
  
  
    @Override  
    public void setIntentRedelivery(boolean enabled) {  
        super.setIntentRedelivery(enabled);  
//        System.out.println("setIntentRedelivery");  
    }  
  
    @Override  
    protected void onHandleIntent(Intent intent) {  
        //Intent��startsevice������ 
        String action = intent.getAction();  
        if (action.equals(Protocol.ServiceDidFinishLoginSuccess)) {  
        	dealWithNewNoticeResend();
        }else if (action.equals(Protocol.ServiceDiaryNewMsg)) { 
        	//�ռǱ�����
//        	String statusTile ="�����ռ��и���";
//        	String title = "����";
//        	String content = "�����ռ��и���";
//        	showBackNotification(this.getApplicationContext(),
//        			statusTile,
//        			title, content,R.drawable.missing_item);
        }else if (action.equals(Protocol.ServiceChatNewMsg)) {  
        	//����
        	String statusTile ="���յ�һ���µ�������Ϣ";
        	String title = "����";
        	String content = "���յ�һ���µ�������Ϣ";
        	if(GlobalApplication.getInstance().isAppOnForeground()){
        		if(!(GlobalApplication.getInstance().isChatVisible())){
        		showAutoCancelNotification(this.getApplicationContext(),
            			statusTile,
            			title, content,R.drawable.chat_action_round_man);
        		}
        	}else{
        		showBackNotification(this.getApplicationContext(),
            			statusTile,
            			title, content,R.drawable.chat_action_round_man);
        	}
        	
        	
            
       }else if (action.equals(Protocol.ServiceAlbumNewMsg)) {  
//    	   String statusTile ="��������и���";
//       	String title = "����";
//       	String content = "��������и���";
//       	showBackNotification(this.getApplicationContext(),
//       			statusTile,
//       			title, content,R.drawable.album_backgrund);
      }else if(action.equals(Protocol.ServiceActionNewMsg)){
    	   String statusTile =intent.getStringExtra("content");
         	String title = "����";
         	String content =statusTile;
         	if(GlobalApplication.getInstance().isAppOnForeground()){
         		if(!(GlobalApplication.getInstance().isMainVisible())){
        		showAutoCancelNotification(this.getApplicationContext(),
             			statusTile,
             			title, content,R.drawable.actionboychattoleft);
         		}
        	}else{
        		showBackNotification(this.getApplicationContext(),
             			statusTile,
             			title, content,R.drawable.actionboychattoleft);
        	}
        	
     }
          
       
    }  
    
  //��������Ϣ���Ѻ��ط���
    public void dealWithNewNoticeResend()
    {
    	
        if (controlHandler == null) {
             controlHandler =new ControlHandler();
        }
        controlHandler.dealWithNewNotificationAndWaitForSending();
    }
  
    @Override  
    public void onDestroy() {  
//        System.out.println("onDestroy");  
        super.onDestroy();  
    }  
    
    public void showAutoCancelNotification(Context ctx,String statusTile,
			String title,String content,int id){
    	cancelNotification(id);
		NotificationManager myNotificationManager = (NotificationManager) ctx.getSystemService(
				android.content.Context.NOTIFICATION_SERVICE); 
		//define 
		Notification myNotification = new Notification(
				R.drawable.ic_notification,statusTile, System.currentTimeMillis());
		myNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		 myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
	    myNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
		myNotification.defaults = Notification.DEFAULT_LIGHTS;
		
		myNotification.ledARGB = Color.RED;
		myNotification.ledOnMS = 5000;
		
		SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount()
				, Activity.MODE_PRIVATE);
		boolean isVoice = mSP.getBoolean("isVoice",true);
		boolean isViberate= mSP.getBoolean("isViberate",false);
		/*
		 * �ж�ϵͳ�ͱ�Ӧ���ۺϺ������
		 * 0:���񶯲����� 1:�񶯵������� 2:���嵫���� 3:����Ҳ����
		 */
//		int  set = mSP.getInt("voiceOrviberate", 1);
//		if(set == 1 || set == 3 ){
//			//һ����
//			myNotification.defaults |= Notification.DEFAULT_VIBRATE;
//		
//		}else{
//			//һ������
//			myNotification.defaults |= ~(Notification.DEFAULT_VIBRATE);
//		}
//		if(set == 0|| set == 1 ){
//			//һ��������
//			myNotification.defaults |=  ~(Notification.DEFAULT_SOUND);
//		}else{
//			//һ������
//			myNotification.defaults |= Notification.DEFAULT_SOUND;
//		}
		if(isVoice){
			//һ������
			myNotification.defaults |= Notification.DEFAULT_SOUND;
		}else{
			myNotification.defaults &=  ~(Notification.DEFAULT_SOUND);
		}
		if(isViberate){
			myNotification.defaults |= Notification.DEFAULT_VIBRATE;
		}else{
			myNotification.defaults &= ~(Notification.DEFAULT_VIBRATE);
		}
			
		
		
		//����֪ͨʱ����Ϣ
	
		Intent notificationIntent = new Intent(ctx, ctx.getClass());
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		PendingIntent contentIntent = PendingIntent.getActivity
				(ctx.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		myNotification.setLatestEventInfo(ctx,title, content, contentIntent);
		
		myNotificationManager.notify(id,myNotification);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cancelNotification(id);

	}
    
    public void showBackNotification(Context ctx,String statusTile,
			String title,String content,int id){
    	cancelNotification(id);
		NotificationManager myNotificationManager = (NotificationManager) ctx.getSystemService(
				android.content.Context.NOTIFICATION_SERVICE); 
		//define 
		Notification myNotification = new Notification(
				R.drawable.ic_notification,statusTile, System.currentTimeMillis());
		myNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		 myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
	    myNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
		myNotification.defaults = Notification.DEFAULT_LIGHTS;
		
		myNotification.ledARGB = Color.RED;
		myNotification.ledOnMS = 5000;
		
		SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount()
				, Activity.MODE_PRIVATE);
		boolean isVoice = mSP.getBoolean("isVoice",true);
		boolean isViberate= mSP.getBoolean("isViberate",false);
		/*
		 * �ж�ϵͳ�ͱ�Ӧ���ۺϺ������
		 * 0:���񶯲����� 1:�񶯵������� 2:���嵫���� 3:����Ҳ����
		 */
//		int  set = mSP.getInt("voiceOrviberate", 1);
//		if(set == 1 || set == 3 ){
//			//һ����
//			myNotification.defaults |= Notification.DEFAULT_VIBRATE;
//		
//		}else{
//			//һ������
//			myNotification.defaults |= ~(Notification.DEFAULT_VIBRATE);
//		}
//		if(set == 0|| set == 1 ){
//			//һ��������
//			myNotification.defaults |=  ~(Notification.DEFAULT_SOUND);
//		}else{
//			//һ������
//			myNotification.defaults |= Notification.DEFAULT_SOUND;
//		}
		if(isVoice){
			//һ������
			myNotification.defaults |= Notification.DEFAULT_SOUND;
		}else{
			myNotification.defaults &=  ~(Notification.DEFAULT_SOUND);
		}
		if(isViberate){
			myNotification.defaults |= Notification.DEFAULT_VIBRATE;
		}else{
			myNotification.defaults &= ~(Notification.DEFAULT_VIBRATE);
		}
			
		
		
		//����֪ͨʱ����Ϣ
		Intent notificationIntent = null;
		if(id == R.drawable.chat_action_round_man){
			notificationIntent = new Intent(ctx, ChatActivity.class);
		}else{
		notificationIntent = new Intent(ctx, MainActivity.class);
		}
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		PendingIntent contentIntent = PendingIntent.getActivity
				(ctx.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		myNotification.setLatestEventInfo(ctx,title, content, contentIntent);
		
		myNotificationManager.notify(id,myNotification);

	}
	
	public void cancelNotification(int id){
		NotificationManager myNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		myNotificationManager.cancel(id);
	}
  
}  
	
    