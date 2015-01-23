package com.minus.lovershouse;

import com.minius.leadpage.GuideActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.baseActivity.BroadCast;
//import com.minus.lovershouse.setting.ConfigActivity;
import com.minus.lovershouse.setting.PasswordActivity;
import com.minus.lovershouse.setting.UnlockGesturePasswordActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.xsocket.asynsocket.AsynSocket;
//import com.minus.sql_interface.Database;
//import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.ConnectHandler;
import com.minus.xsocket.handler.UserPacketHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
//import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
//import com.umeng.message.UmengRegistrar;
import com.umeng.update.UmengUpdateAgent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.annotation.SuppressLint;
import android.app.Activity;
//import android.content.ClipboardManager;
//import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
//import android.text.TextUtils;
import android.view.Menu;


public class WelcomeActivity extends BroadCast {
	
	public static final int MSG_TIMEOUT = 0x01;
	public static final int MSG_TRYLOGIN = 0x02;
	
	private Handler welcomeHandler = null;

	private PushAgent mPushAgent;
 
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        MobclickAgent.updateOnlineConfig(this);
        //UmengUpdateAgent.update(this);//自动更新
        FeedbackAgent agent = new FeedbackAgent(GlobalApplication.getInstance());
        agent.sync();
        
        mPushAgent = PushAgent.getInstance(GlobalApplication.getInstance());
		mPushAgent.enable();
//		String deviceToken = PushAgent.getInstance(GlobalApplication.getInstance()).getRegistrationId();
//		testOut.setText(deviceToken);
//		String a = getDeviceInfo(getApplicationContext());

        SelfInfo.getInstance().setDefault();
        ConnectHandler.getInstance().connectToServer();
		
		SharedPreferences mSP = getSharedPreferences(Protocol.PREFERENCE_NAME, Activity.MODE_PRIVATE);
		boolean isFristRun = mSP.getBoolean("IsFirstRun", true);
		String lastUser = mSP.getString("LastUser", "");
		
		SharedPreferences mSP1 = getSharedPreferences(lastUser, Activity.MODE_PRIVATE);
		String password = mSP.getString("Password", "");
		
		if (isFristRun) { //安装后首次打开程序，转到引导页
			Intent intent = new Intent();
			intent.setClass(WelcomeActivity.this, GuideActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			
			WelcomeActivity.this.finish();
		} else if (mSP1.getBoolean("isProtected",false)) {
			if(mSP1.getBoolean("isNum",true)){
				Intent regIntent =new Intent(WelcomeActivity.this, PasswordActivity.class);
				Bundle regBundle = new Bundle();
		        regBundle.putString("who", "6");
		        regIntent.putExtras(regBundle);
		        regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
				startActivity(regIntent);
				WelcomeActivity.this.finish();
			}else{
				Intent regIntent =new Intent(WelcomeActivity.this, UnlockGesturePasswordActivity.class);
				regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				Bundle regBundle = new Bundle();
		        regBundle.putString("who", "6");
		        regIntent.putExtras(regBundle);
				startActivity(regIntent);
				WelcomeActivity.this.finish();
			}
		} else {
			if (lastUser.equals("") || password.equals("")) {//信息不全，上次关闭程序时账户已退出登陆,应该转到登陆界面
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				
				WelcomeActivity.this.finish();
			}
			else { //上次关闭程序时账户未退出登陆,直接转到主界面
				/*/等待后台登陆，超时（3秒）跳转到主界面
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				*/
				
				this.welcomeHandler = new MyHandler();
				Message msg;
				
				msg = welcomeHandler.obtainMessage();
				msg.what = MSG_TRYLOGIN;
				welcomeHandler.sendMessageDelayed(msg, 1000);
				
				msg = welcomeHandler.obtainMessage();
				msg.what = MSG_TRYLOGIN;
				welcomeHandler.sendMessageDelayed(msg, 2000);
				
				msg = welcomeHandler.obtainMessage();
				msg.what = MSG_TIMEOUT;
				welcomeHandler.sendMessageDelayed(msg, 3000); //等待后台登陆，超时（3秒）跳转到主界面
			}
		}

    }
    
/*获取测试设置函数
public static String getDeviceInfo(Context context) {
    try{
      org.json.JSONObject json = new org.json.JSONObject();
      android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
          .getSystemService(Context.TELEPHONY_SERVICE);
  
      String device_id = tm.getDeviceId();
      
      android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
          
      String mac = wifi.getConnectionInfo().getMacAddress();
      json.put("mac", mac);
      
      if( TextUtils.isEmpty(device_id) ){
        device_id = mac;
      }
      
      if( TextUtils.isEmpty(device_id) ){
        device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
      }
      
      json.put("device_id", device_id);
      
      return json.toString();
    }catch(Exception e){
      e.printStackTrace();
    }
  return null;
}*/

    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (this.welcomeHandler != null)
			this.welcomeHandler = null;
	}


	private  class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
	
			switch (msg.what) {  
		     case MSG_TIMEOUT:
		    	 login();
		    	 
		    	 //上次关闭程序时账户未退出登陆,直接转到主界面
		    	 Intent intent = new Intent();
                 intent.setClass(WelcomeActivity.this,MainActivity.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                 //intent.putExtra("who", 2);
                 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
		    	 startActivity(intent);
		    	 
		    	 WelcomeActivity.this.finish();
		    	 /*
		    	 SharedPreferences mSP = getApplicationContext().getSharedPreferences(
		    			 Protocol.PREFERENCE_NAME, Activity.MODE_PRIVATE);
		    	boolean isFirstRun = mSP.getBoolean("IsFirstRun", true);
		    	if(isFirstRun){
		    		 Intent intent = new Intent();
	                  intent.setClass(WelcomeActivity.this,GuideActivity.class);
	                  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
	                  startActivity(intent);
	                  WelcomeActivity.this.finish();
		    	}else{
		     	String dbTitle = mSP.getString("LastUser", "");
		    	  if(dbTitle.equals("")){
		    		  Intent intent = new Intent();
	                  intent.setClass(WelcomeActivity.this,RegisterActivity.class);
	                  intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
	                  startActivity(intent);
	                  WelcomeActivity.this.finish();
		    	  }else{
		    		  SharedPreferences mSP1  = GlobalApplication.getInstance().getSharedPreferences(dbTitle,Activity.MODE_PRIVATE);
		    		  if (mSP1.getBoolean("isProtected",false)){
		    				if(mSP1.getBoolean("isNum",true)){
		    					Intent regIntent =new Intent(WelcomeActivity.this, PasswordActivity.class);
		    					Bundle regBundle = new Bundle();
		    			        regBundle.putString("who", "6");
		    			        regIntent.putExtras(regBundle);
		    			        regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
		    					startActivity(regIntent);
		    					WelcomeActivity.this.finish();
		    				}else{
		    					Intent regIntent =new Intent(WelcomeActivity.this, UnlockGesturePasswordActivity.class);
		    					regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		    					Bundle regBundle = new Bundle();
		    			        regBundle.putString("who", "6");
		    			        regIntent.putExtras(regBundle);
		    					startActivity(regIntent);
		    					WelcomeActivity.this.finish();
		    				}
		    				
		    			}else{
		    	                  Intent intent = new Intent();
		    	                  intent.setClass(WelcomeActivity.this,MainActivity.class);
		    	                  intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		    	                  intent.putExtra("who", 2);
//		    	                  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
		    	                  startActivity(intent);
		    	                  WelcomeActivity.this.finish();
		    			}
		    	  }
		    	}
		    	  */
	                break;
	                
		     case MSG_TRYLOGIN:
		    	 login();
		    	 break;
		    	 
		     default:
		    	 break;
			}
		}
	}

	public static void login() {
		SharedPreferences mSP = GlobalApplication.getInstance().getSharedPreferences(
				Protocol.PREFERENCE_NAME, Activity.MODE_PRIVATE);
		boolean isFristRun = mSP.getBoolean("IsFirstRun", true);
		String lastUser = mSP.getString("LastUser", "");

		SharedPreferences mSP1 = GlobalApplication.getInstance().getSharedPreferences(
				lastUser, Activity.MODE_PRIVATE);
		String password = mSP.getString("Password", "");
		
		if (AsynSocket.getInstance().isConnected() 
				&& SelfInfo.getInstance().isOnline() == false
				&& isFristRun == false 
				&& lastUser.equals("") == false 
				&& password.equals("") == false) {
			// try login after connected //上次关闭程序时账户未退出登陆,直接转到主界面
			SelfInfo.getInstance().setDefault();
			SelfInfo.getInstance().setAccount(lastUser);
			SelfInfo.getInstance().setPwd(password);
			UserPacketHandler mUserPacketHandler = new UserPacketHandler();
			mUserPacketHandler.Login(lastUser, password);
			if (BuildConfig.DEBUG)
				Log.d("Login", "WelcomeActivity static login()");
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_welcome, menu);
        return true;
    }
}
