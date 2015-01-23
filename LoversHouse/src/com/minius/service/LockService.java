package com.minius.service; 

import com.minus.lovershouse.singleton.GlobalApplication;

import android.app.Service;  
import android.content.Intent;  
import android.os.CountDownTimer;
import android.os.IBinder;  
import android.os.PowerManager;
import android.os.RemoteCallbackList;  
import android.os.RemoteException;  
import android.util.Log;  

public class LockService extends Service {  

	   private static final String TAG = "LockService";  
	
	   private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();  
	   private MyCount mc; 
	   
	   
	   private IService.Stub mBinder = new IService.Stub() {  
		   
	   
       @Override  
       public void unregisterCallback(ICallback cb){  
           if(cb != null) {  
               mCallbacks.unregister(cb);  
          }  
       }  

       @Override  
       public void registerCallback(ICallback cb){  
           if(cb != null) {  
               mCallbacks.register(cb);  
           }  
       }  
   };  

   @Override  
   public IBinder onBind(Intent intent) {  
      Log.d(TAG, "onBind");  
      return mBinder;  
 }  

   @Override  
   public void onCreate() {  
       
       mc = new MyCount(10000, 1000);  //改为10秒
       mc.start();  
       
       super.onCreate();  
   }  
   


   @Override  
   public void onDestroy() {  
       mCallbacks.kill();  
       super.onDestroy();  
   }  

 	
   private void callBack() {  
       int N = mCallbacks.beginBroadcast();  
       try {  
           for (int i = 0; i < N; i++) {  
               mCallbacks.getBroadcastItem(i).lockOn();  
           }  
       } catch (RemoteException e) {  
           Log.e(TAG, "", e);  
       }  
       mCallbacks.finishBroadcast();  
   }  

   /*定义一个倒计时的内部类*/  
   class MyCount extends CountDownTimer {     
      public MyCount(long millisInFuture, long countDownInterval) {     
          super(millisInFuture, countDownInterval);     
      }     
      @Override     
      public void onFinish() {     
          callBack();      

          
      }     
      @Override     
      public void onTick(long millisUntilFinished) {     
                 
      }    
   }
   
//   private Handler mHandler = new Handler() {  
//
//       @Override  
//       public void handleMessage(Message msg) {  
//           callBack();  
//           super.handleMessage(msg);  
//      }  
//   };  
}  