package com.minius.baidupush;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.minus.lovershouse.ChatActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.WelcomeActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.xsocket.handler.UserPacketHandler;


	/**
	 * Push��Ϣ����receiver�����д����Ҫ�Ļص������� һ����˵�� onBind�Ǳ���ģ���������startWork����ֵ��
	 * onMessage��������͸����Ϣ�� onSetTags��onDelTags��onListTags��tag��ز����Ļص���
	 * onNotificationClicked��֪ͨ�����ʱ�ص��� onUnbind��stopWork�ӿڵķ���ֵ�ص�
	 * 
	 * ����ֵ�е�errorCode���������£� 
	 *  0 - Success
	 *  10001 - Network Problem
	 *  30600 - Internal Server Error
	 *  30601 - Method Not Allowed 
	 *  30602 - Request Params Not Valid
	 *  30603 - Authentication Failed 
	 *  30604 - Quota Use Up Payment Required 
	 *  30605 - Data Required Not Found 
	 *  30606 - Request Time Expires Timeout 
	 *  30607 - Channel Token Timeout 
	 *  30608 - Bind Relation Not Found 
	 *  30609 - Bind Number Too Many
	 * 
	 * �����������Ϸ��ش���ʱ��������Ͳ����������⣬����ͬһ����ķ���ֵrequestId��errorCode��ϵ����׷�����⡣
	 * 
	 */
	public class BaiduPushReceiver extends FrontiaPushMessageReceiver {
	    /** TAG to Log */
	    public static final String TAG = BaiduPushReceiver.class
	            .getSimpleName();

	    /**
	     * ����PushManager.startWork��sdk����push
	     * server�������������������첽�ġ�������Ľ��ͨ��onBind���ء� �������Ҫ�õ������ͣ���Ҫ�������ȡ��channel
	     * id��user id�ϴ���Ӧ��server�У��ٵ���server�ӿ���channel id��user id�������ֻ������û����͡�
	     * 
	     * @param context
	     *            BroadcastReceiver��ִ��Context
	     * @param errorCode
	     *            �󶨽ӿڷ���ֵ��0 - �ɹ�
	     * @param appid
	     *            Ӧ��id��errorCode��0ʱΪnull
	     * @param userId
	     *            Ӧ��user id��errorCode��0ʱΪnull
	     * @param channelId
	     *            Ӧ��channel id��errorCode��0ʱΪnull
	     * @param requestId
	     *            �����˷��������id����׷������ʱ���ã�
	     * @return none
	     */
	    @Override
	    public void onBind(Context context, int errorCode, String appid,
	            String userId, String channelId, String requestId) {
//	        String responseString = "onBind errorCode=" + errorCode + " appid="
//	                + appid + " userId=" + userId + " channelId=" + channelId
//	                + " requestId=" + requestId;


	        // �󶨳ɹ��������Ѱ�flag��������Ч�ļ��ٲ���Ҫ�İ�����
	        if (errorCode == 0) {
	            Utils.setPushUserId(context, userId);
	            
	            UserPacketHandler mUp= new UserPacketHandler();
	            mUp.uploadTokenToServer();
	            
	        }
//	        // Demo���½���չʾ���룬Ӧ��������������Լ��Ĵ����߼�
//	        updateContent(context, responseString);
	    }

	    /**
	     * ����͸����Ϣ�ĺ�����
	     * 
	     * @param context
	     *            ������
	     * @param message
	     *            ���͵���Ϣ
	     * @param customContentString
	     *            �Զ�������,Ϊ�ջ���json�ַ���
	     */
	    @Override
	    public void onMessage(Context context, String message,
	            String customContentString) {
	        String messageString = "͸����Ϣ message=\"" + message
	                + "\" customContentString=" + customContentString;
//	        Log.d(TAG, messageString);
	        String title = "����";
	        if(message.equals("���յ��µĶ���")){
	        	 showBackNotification(context,title,
	 	        		title,message,1);
	        }else {
//	        	������Ϣ
	        	 showBackNotification(context,title,
		 	        		title,message,2);
	        }
	       

	        // �Զ������ݻ�ȡ��ʽ��mykey��myvalue��Ӧ͸����Ϣ����ʱ�Զ������������õļ���ֵ
	        if (!TextUtils.isEmpty(customContentString)) {
	            JSONObject customJson = null;
	            try {
	                customJson = new JSONObject(customContentString);
	                String myvalue = null;
	                if (customJson.isNull("mykey")) {
	                    myvalue = customJson.getString("mykey");
	                }
	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }

	        // Demo���½���չʾ���룬Ӧ��������������Լ��Ĵ����߼�
	        updateContent(context, messageString);
	    }

	    /**
	     * ����֪ͨ����ĺ�����ע������֪ͨ���û����ǰ��Ӧ���޷�ͨ���ӿڻ�ȡ֪ͨ�����ݡ�
	     * 
	     * @param context
	     *            ������
	     * @param title
	     *            ���͵�֪ͨ�ı���
	     * @param description
	     *            ���͵�֪ͨ������
	     * @param customContentString
	     *            �Զ������ݣ�Ϊ�ջ���json�ַ���
	     */
	    @Override
	    public void onNotificationClicked(Context context, String title,
	            String description, String customContentString) {
//	        String notifyString = "֪ͨ��� title=\"" + title + "\" description=\""
//	                + description + "\" customContent=" + customContentString;
//	        Log.d(TAG, notifyString);
	        Intent intent = new Intent();
	        intent.setClass(context.getApplicationContext(), WelcomeActivity.class);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
	        		Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	        context.getApplicationContext().startActivity(intent);
	        // �Զ������ݻ�ȡ��ʽ��mykey��myvalue��Ӧ֪ͨ����ʱ�Զ������������õļ���ֵ
	        if (!TextUtils.isEmpty(customContentString)) {
	            JSONObject customJson = null;
	            try {
	                customJson = new JSONObject(customContentString);
	                String myvalue = null;
	                if (customJson.isNull("mykey")) {
	                    myvalue = customJson.getString("mykey");
	                }
	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }

//	        // Demo���½���չʾ���룬Ӧ��������������Լ��Ĵ����߼�
//	        updateContent(context, notifyString);
	    }

	    /**
	     * setTags() �Ļص�������
	     * 
	     * @param context
	     *            ������
	     * @param errorCode
	     *            �����롣0��ʾĳЩtag�Ѿ����óɹ�����0��ʾ����tag�����þ�ʧ�ܡ�
	     * @param successTags
	     *            ���óɹ���tag
	     * @param failTags
	     *            ����ʧ�ܵ�tag
	     * @param requestId
	     *            ������������͵������id
	     */
	    @Override
	    public void onSetTags(Context context, int errorCode,
	            List<String> sucessTags, List<String> failTags, String requestId) {
	        String responseString = "onSetTags errorCode=" + errorCode
	                + " sucessTags=" + sucessTags + " failTags=" + failTags
	                + " requestId=" + requestId;
//	        Log.d(TAG, responseString);

	        // Demo���½���չʾ���룬Ӧ��������������Լ��Ĵ����߼�
	        updateContent(context, responseString);
	    }

	    /**
	     * delTags() �Ļص�������
	     * 
	     * @param context
	     *            ������
	     * @param errorCode
	     *            �����롣0��ʾĳЩtag�Ѿ�ɾ���ɹ�����0��ʾ����tag��ɾ��ʧ�ܡ�
	     * @param successTags
	     *            �ɹ�ɾ����tag
	     * @param failTags
	     *            ɾ��ʧ�ܵ�tag
	     * @param requestId
	     *            ������������͵������id
	     */
	    @Override
	    public void onDelTags(Context context, int errorCode,
	            List<String> sucessTags, List<String> failTags, String requestId) {
	        String responseString = "onDelTags errorCode=" + errorCode
	                + " sucessTags=" + sucessTags + " failTags=" + failTags
	                + " requestId=" + requestId;
//	        Log.d(TAG, responseString);

	        // Demo���½���չʾ���룬Ӧ��������������Լ��Ĵ����߼�
	        updateContent(context, responseString);
	    }

	    /**
	     * listTags() �Ļص�������
	     * 
	     * @param context
	     *            ������
	     * @param errorCode
	     *            �����롣0��ʾ�о�tag�ɹ�����0��ʾʧ�ܡ�
	     * @param tags
	     *            ��ǰӦ�����õ�����tag��
	     * @param requestId
	     *            ������������͵������id
	     */
	    @Override
	    public void onListTags(Context context, int errorCode, List<String> tags,
	            String requestId) {
	        String responseString = "onListTags errorCode=" + errorCode + " tags="
	                + tags;
//	        Log.d(TAG, responseString);

	        // Demo���½���չʾ���룬Ӧ��������������Լ��Ĵ����߼�
	        updateContent(context, responseString);
	    }

	    /**
	     * PushManager.stopWork() �Ļص�������
	     * 
	     * @param context
	     *            ������
	     * @param errorCode
	     *            �����롣0��ʾ�������ͽ�󶨳ɹ�����0��ʾʧ�ܡ�
	     * @param requestId
	     *            ������������͵������id
	     */
	    @Override
	    public void onUnbind(Context context, int errorCode, String requestId) {
	        String responseString = "onUnbind errorCode=" + errorCode
	                + " requestId = " + requestId;
//	        Log.d(TAG, responseString);

	        // ��󶨳ɹ�������δ��flag��
	        if (errorCode == 0) {
	            Utils.setPushUserId(context, "");
	        }
	        // Demo���½���չʾ���룬Ӧ��������������Լ��Ĵ����߼�
	        updateContent(context, responseString);
	    }

	    private void updateContent(Context context, String content) {
//	        Log.d(TAG, "updateContent");
	        String logText = "" + Utils.logStringCache;

	        if (!logText.equals("")) {
	            logText += "\n";
	        }

	        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm-ss");
	        logText += sDateFormat.format(new Date()) + ": ";
	        logText += content;

	        Utils.logStringCache = logText;

	     
	    }
	    
	    public void showAutoCancelNotification(Context ctx,String statusTile,
				String title,String content,int id){
	    	cancelNotification(id,ctx);
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
//			int  set = mSP.getInt("voiceOrviberate", 1);
//			if(set == 1 || set == 3 ){
//				//һ����
//				myNotification.defaults |= Notification.DEFAULT_VIBRATE;
//			
//			}else{
//				//һ������
//				myNotification.defaults |= ~(Notification.DEFAULT_VIBRATE);
//			}
//			if(set == 0|| set == 1 ){
//				//һ��������
//				myNotification.defaults |=  ~(Notification.DEFAULT_SOUND);
//			}else{
//				//һ������
//				myNotification.defaults |= Notification.DEFAULT_SOUND;
//			}
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
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT| 
					Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
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
			
			cancelNotification(id,ctx);

		}
	    
	    public void showBackNotification(Context ctx,String statusTile,
				String title,String content,int id){
	    	cancelNotification(id,ctx);
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
			int  set = mSP.getInt("voiceOrviberate", 1);
			if(set == 1 || set == 3 ){
				//һ����
				myNotification.defaults |= Notification.DEFAULT_VIBRATE;
			
			}else{
				//һ������
				myNotification.defaults |= ~(Notification.DEFAULT_VIBRATE);
			}
			if(set == 0|| set == 1 ){
				//һ��������
				myNotification.defaults |=  ~(Notification.DEFAULT_SOUND);
			}else{
				//һ������
				myNotification.defaults |= Notification.DEFAULT_SOUND;
			}
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
		
			Intent notificationIntent = new Intent(ctx, WelcomeActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT| 
					Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			PendingIntent contentIntent = PendingIntent.getActivity
					(ctx.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			myNotification.setLatestEventInfo(ctx,title, content, contentIntent);
			
			myNotificationManager.notify(id,myNotification);

		}
		
		public void cancelNotification(int id,Context ctx ){
			NotificationManager myNotificationManager = (NotificationManager) ctx.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			myNotificationManager.cancel(id);
		}
}