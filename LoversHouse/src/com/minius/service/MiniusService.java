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
 * 用于后台socket连接还没断时监听是否有新的消息 人物动作 日记 album等，并post notification 
 * 以及在主界面登陆成功后处理新消息和重发。
 * 
 * 需要实现的功能
 * 1 主界面登陆成功后判断新消息以及处理重发
 * 
 *
 */
public class MiniusService extends IntentService {  
  
	
	private ControlHandler controlHandler = null;
    public MiniusService() {  
        //必须实现父类的构造方法  
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
        //Intent是startsevice发来的 
        String action = intent.getAction();  
        if (action.equals(Protocol.ServiceDidFinishLoginSuccess)) {  
        	dealWithNewNoticeResend();
        }else if (action.equals(Protocol.ServiceDiaryNewMsg)) { 
        	//日记本提醒
//        	String statusTile ="您的日记有更新";
//        	String title = "想你";
//        	String content = "您的日记有更新";
//        	showBackNotification(this.getApplicationContext(),
//        			statusTile,
//        			title, content,R.drawable.missing_item);
        }else if (action.equals(Protocol.ServiceChatNewMsg)) {  
        	//提醒
        	String statusTile ="您收到一条新的聊天信息";
        	String title = "想你";
        	String content = "您收到一条新的聊天信息";
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
//    	   String statusTile ="您的相册有更新";
//       	String title = "想你";
//       	String content = "您的相册有更新";
//       	showBackNotification(this.getApplicationContext(),
//       			statusTile,
//       			title, content,R.drawable.album_backgrund);
      }else if(action.equals(Protocol.ServiceActionNewMsg)){
    	   String statusTile =intent.getStringExtra("content");
         	String title = "想你";
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
    
  //处理新消息提醒和重发。
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
		 * 判断系统和本应用综合后的设置
		 * 0:不振动不响铃 1:振动但不响铃 2:响铃但不振动 3:既振动也响铃
		 */
//		int  set = mSP.getInt("voiceOrviberate", 1);
//		if(set == 1 || set == 3 ){
//			//一定震动
//			myNotification.defaults |= Notification.DEFAULT_VIBRATE;
//		
//		}else{
//			//一定不震动
//			myNotification.defaults |= ~(Notification.DEFAULT_VIBRATE);
//		}
//		if(set == 0|| set == 1 ){
//			//一定不响铃
//			myNotification.defaults |=  ~(Notification.DEFAULT_SOUND);
//		}else{
//			//一定响铃
//			myNotification.defaults |= Notification.DEFAULT_SOUND;
//		}
		if(isVoice){
			//一定响铃
			myNotification.defaults |= Notification.DEFAULT_SOUND;
		}else{
			myNotification.defaults &=  ~(Notification.DEFAULT_SOUND);
		}
		if(isViberate){
			myNotification.defaults |= Notification.DEFAULT_VIBRATE;
		}else{
			myNotification.defaults &= ~(Notification.DEFAULT_VIBRATE);
		}
			
		
		
		//设置通知时间消息
	
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
		 * 判断系统和本应用综合后的设置
		 * 0:不振动不响铃 1:振动但不响铃 2:响铃但不振动 3:既振动也响铃
		 */
//		int  set = mSP.getInt("voiceOrviberate", 1);
//		if(set == 1 || set == 3 ){
//			//一定震动
//			myNotification.defaults |= Notification.DEFAULT_VIBRATE;
//		
//		}else{
//			//一定不震动
//			myNotification.defaults |= ~(Notification.DEFAULT_VIBRATE);
//		}
//		if(set == 0|| set == 1 ){
//			//一定不响铃
//			myNotification.defaults |=  ~(Notification.DEFAULT_SOUND);
//		}else{
//			//一定响铃
//			myNotification.defaults |= Notification.DEFAULT_SOUND;
//		}
		if(isVoice){
			//一定响铃
			myNotification.defaults |= Notification.DEFAULT_SOUND;
		}else{
			myNotification.defaults &=  ~(Notification.DEFAULT_SOUND);
		}
		if(isViberate){
			myNotification.defaults |= Notification.DEFAULT_VIBRATE;
		}else{
			myNotification.defaults &= ~(Notification.DEFAULT_VIBRATE);
		}
			
		
		
		//设置通知时间消息
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
	
    