package com.minus.lovershouse.baseActivity;



import java.io.UnsupportedEncodingException;

import com.minius.service.ICallback;
import com.minius.service.IService;
import com.minius.service.LockService;
import com.minus.lovershouse.R;
import com.minus.lovershouse.setting.PasswordActivity;
import com.minus.lovershouse.setting.UnlockGesturePasswordActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.ConnectHandler;
import com.minus.xsocket.handler.HeartPacketHandler;
import com.minus.xsocket.handler.UserPacketHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class BroadCast extends Activity{
	
	

	public boolean toBack=false;

	public boolean toBacklock=false;
	private IService mService;
	private PowerManager.WakeLock mWakeLock=null;
	private static final String TAG = "BroadCast"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		GlobalApplication.getInstance().addActivity(this);
		PushAgent.getInstance(GlobalApplication.getInstance()).onAppStart();
	}
	
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		int base = Menu.FIRST;
//		
//		MenuItem item4 = menu.add(base,base+3,base+3,"�˳�");
//		item4.setIcon(R.drawable.menu_unfold_ori);
//		return super.onCreateOptionsMenu(menu);
//}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
/**		final Intent Notificationintent = new Intent();
		 Notificationintent.setAction("com.hkw.myservice.NotificationService");
		this.stopService( Notificationintent);*/
		if(item.getItemId() == Menu.FIRST +3){
			GlobalApplication.getInstance().AppExit();
		}
		
//			Intent intent = new Intent();
//			intent.setAction("ExitApp");
//			this.sendBroadcast(intent);
//			super.finish();
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		String temp=null;
	//	AppManager.context = this;
		super.onResume();
		MobclickAgent.onResume(this);
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)// ǿ������
		{   
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
		} 
		
//		IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_OFF);    
        
//        registerReceiver(mBatInfoReceiver, filter);
			 
		if(toBack){//ÿһ�ν����̨��ָ�
				
			enterForeGround();				
			if(mService!=null){  //���½���ǰ̨������ʱservice���
		            try {  
		                mService.unregisterCallback(mCallback);  
		                unbindService(mConnection); 
		                mService=null;
		             } catch (RemoteException e) {  
		             }  
		    }
				
			if(SelfInfo.getInstance().isMainInit()&&toBacklock){//���������������Һ�̨���г���ʮ��
					
					SharedPreferences mSP1 = getSharedPreferences(Protocol.PREFERENCE_NAME,
  							Activity.MODE_PRIVATE);
					temp=mSP1.getString("LastUser","");
					SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(temp
							, Activity.MODE_PRIVATE);
					
					if (mSP.getBoolean("isProtected",false)){
						if(mSP.getBoolean("isNum",true)){
							Intent regIntent =new Intent(this, PasswordActivity.class);
							Bundle regBundle = new Bundle();
					        regBundle.putString("who", "1");
					        regIntent.putExtras(regBundle);
							startActivity(regIntent);
						}else{
							Intent regIntent =new Intent(this, UnlockGesturePasswordActivity.class);
							Bundle regBundle = new Bundle();
					        regBundle.putString("who", "1");
					        regIntent.putExtras(regBundle);
							startActivity(regIntent);
						}
						
					}
					
					toBacklock=false;
			}
			toBack=false;
		}
		
		
}

//	BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {    
//        @Override    
//        public void onReceive(final Context context, final Intent intent) {  
//              
//
//            String action = intent.getAction();    
//
//              
//           if(Intent.ACTION_SCREEN_ON.equals(action))  
//           {    
//                 
//           }  
//             
//           else if(Intent.ACTION_SCREEN_OFF.equals(action))  
//           {    
//                handleBacklock();
//           }    
//             
//        }    
//    };
    
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onStop() {
		
		PowerManager pm = (PowerManager) GlobalApplication.getInstance().getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();//���Ϊtrue�����ʾ��Ļ�������ˣ�������Ļ�������ˡ�

		if (!isScreenOn||!GlobalApplication.getInstance().isAppOnForeground()) {  
            //app �����̨  
			handleBacklock();
		}
		super.onStop();
		
		
	}
	
	@Override
	protected void onDestroy() {
	
		//����Activity&��ջ���Ƴ���Activity
		GlobalApplication.getInstance().finishActivity(this);
//		if (mBatInfoReceiver != null) {
//			this.unregisterReceiver(mBatInfoReceiver);
//			mBatInfoReceiver = null;
//		}
		 if(mService!=null){  
             try {  
                 mService.unregisterCallback(mCallback);  
                 unbindService(mConnection); //destroy��ʱ��Ҫ����unbindService 
                 
                 mService=null;
              } catch (RemoteException e) {  
                  //Log.e(TAG, "", e);  
              }  
          }  
       
		super.onDestroy();
//		System.exit(0); 
		// TODO Auto-generated method stub
	}

	/** 
     * service�Ļص����� 
     */  
    private ICallback.Stub mCallback = new ICallback.Stub() {  

        @Override  
        public void lockOn() {  
        	toBacklock=true;//��Ҫ����������
        	releaseWakeLock();
        }  
    };  

    /** 
     * ע��connection 
     */  
    private ServiceConnection mConnection = new ServiceConnection() {  

        @Override  
        public void onServiceDisconnected(ComponentName name) {  
//            mService = null;  
        }  

        @Override  
       public void onServiceConnected(ComponentName name, IBinder service) {  
            mService = IService.Stub.asInterface(service);  
            try {  
                mService.registerCallback(mCallback);  
            } catch (RemoteException e) {  
            }  
        }  
    };  
    

    //�����豸��Դ��
    	private void acquireWakeLock()
    	{
    		if (null == mWakeLock)
    		{
    			PowerManager pm = (PowerManager)GlobalApplication.getInstance().getSystemService(
    					GlobalApplication.getInstance().POWER_SERVICE);
    			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, TAG);
    			if (null != mWakeLock)
    			{
    				mWakeLock.acquire();
    			}
    		}
    	}
    	
    	//�ͷ��豸��Դ��
    	private void releaseWakeLock()
    	{
    		if (null != mWakeLock)
    		{
    			mWakeLock.release();
    			mWakeLock = null;
    		}
    	}
    	


    
    public void enterForeGround(){
    	 
    	SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(Protocol.PREFERENCE_NAME,
						Activity.MODE_PRIVATE);
		String lastUser = mSP.getString("LastUser", "");
        if (lastUser.equals("")){
        	//���û��lastuser��¼��ʱ�򣬾Ͳ���ִ�����������;    
            return;
        }
        
      //�ж��Ƿ�ͷ�����ʧȥ��ϵ�����û�еĻ�����ôֱ�ӷ�һ����������ȥ����;
        if(!(AsynSocket.getInstance().isConnected())){
        	ConnectHandler.getInstance().connectToServer();
		}else{
			 if(SelfInfo.getInstance().isOnline()){
				 try {
					HeartPacketHandler.getInstance().startHeart();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}	    		
		     }else{
		    	 UserPacketHandler mUP = new UserPacketHandler();
		    	 mUP.Login(SelfInfo.getInstance().getAccount(), SelfInfo.getInstance().getPwd());
		    	 
		     }
		}
  

    }
    
    public  boolean isShouldHideInput(View v, MotionEvent event) {  
        if (v != null && (v instanceof EditText)) {  
            int[] leftTop = { 0, 0 };  
            //��ȡ�����ǰ��locationλ��  
            v.getLocationInWindow(leftTop);  
            int left = leftTop[0];  
            int top = leftTop[1];  
            int bottom = top + v.getHeight();  
            int right = left + v.getWidth();  
            if (event.getX() > left && event.getX() < right  
                    && event.getY() > top && event.getY() < bottom) {  
                // ���������������򣬱������EditText���¼�  
                return false;  
            }else{  
                return true;  
            }  
        }  
        return false;  
    } 
    

/*
 * ����app�����̨������ĻϨ�����������ж�
 */
	private void handleBacklock(){
		toBack=true;//��¼��ǰ�Ѿ������̨  
		acquireWakeLock();
		//�����ֹ̨ͣ����
		  //ֹͣ����
	    HeartPacketHandler.getInstance().stopHeart();
		
		
		String temp=null;

		SharedPreferences mSP1 = getSharedPreferences(Protocol.PREFERENCE_NAME,
					Activity.MODE_PRIVATE);
		temp=mSP1.getString("LastUser","");

		SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(temp
					, Activity.MODE_PRIVATE);
			
		if (mSP.getBoolean("isProtected",false)){
			Intent i = new Intent(this, LockService.class);  
            bindService(i, mConnection, Context.BIND_AUTO_CREATE);
		}
	}
	
	
}