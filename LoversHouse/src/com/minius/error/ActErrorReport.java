package com.minius.error;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.minius.leadpage.OperateGuide;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.setting.ConfigActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.ExitPopup;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.handler.HeartPacketHandler;
import com.minus.xsocket.handler.UserPacketHandler;

public class ActErrorReport extends BroadCast {
    private String info;
    /** 标识来处。 */
//    private String by;
    private Button btnCancel;
    private BtnListener btnListener;
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.errorreport);
       
//            by = getIntent().getStringExtra("by");
            info = getIntent().getStringExtra("error");
//            TextView txtHint = (TextView) findViewById(R.id.txtErrorHint);
//            txtHint.setText(getErrorHint(by));
            TextView editError = (TextView) findViewById(R.id.editErrorContent);
            editError.setText(info);
            btnListener = new BtnListener();

            btnCancel = (Button) findViewById(R.id.btnCANCEL);
            btnCancel.setOnClickListener(btnListener);
    }
    private String getErrorHint(String by) {
            String hint = "";
            String append = "";
            if ("uehandler".equals(by)) {
                    append = " when the app running";
            } else if ("error.log".equals(by)) {
                    append = " when last time the app running";
            }
     
            return hint;
    }
    public void onStart() {
            super.onStart();
          
    }
    class BtnListener implements Button.OnClickListener {
            @Override
            public void onClick(View v) {
                    if (v == btnCancel) {
                    	//退出前停心跳
        				UserPacketHandler mReq = new UserPacketHandler();
        		      	HeartPacketHandler.getInstance().stopHeart();
        		      	
        				mReq.Logout();
        				SelfInfo.getInstance().setDefault();
        				SelfInfo.getInstance().setOnline(false);//下线
        				GlobalApplication.getInstance().setCommonDefault();
        				GlobalApplication.getInstance().setTargetDefault();
        				
        				AsynSocket.getInstance().closeSocket();
        				GlobalApplication.getInstance().destoryBimap();
//        				Database.getInstance(getApplicationContext()).closeDatabase();
        				GlobalApplication.getInstance().AppExit();
        			
        				System.exit(0);
                          
                    }
            }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		//退出前停心跳
			UserPacketHandler mReq = new UserPacketHandler();
	      	HeartPacketHandler.getInstance().stopHeart();
	      	
			mReq.Logout();
			SelfInfo.getInstance().setDefault();
			SelfInfo.getInstance().setOnline(false);//下线
			GlobalApplication.getInstance().setCommonDefault();
			GlobalApplication.getInstance().setTargetDefault();
			
			AsynSocket.getInstance().closeSocket();
			GlobalApplication.getInstance().destoryBimap();
//			Database.getInstance(getApplicationContext()).closeDatabase();
			GlobalApplication.getInstance().AppExit();
		
			System.exit(0);
    	}
    	return true;
	}
  
}