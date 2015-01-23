package com.minus.lovershouse;


import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.minius.chat.imagedetail.ImagePagerActivity;
import com.minius.chat.imagedetail.ImagePagerActivity.TimeConsumingTask;
import com.minius.leadpage.GuideActivity;
import com.minius.ui.CustomDialog.Builder;
import com.minius.ui.ProgressHUD;
import com.minus.lovershouse.R;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.setting.PrivacyActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.map.MapActivity;
import com.minus.sql_interface.Database;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.ConnectHandler;
import com.minus.xsocket.handler.HeartPacketHandler;
import com.minus.xsocket.handler.UserPacketHandler;

public class LoginActivity extends BroadCast implements OnClickListener {
	
	private Builder ibuilder;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);             
//	        GlobalApplication.getInstance().setLoginAppear(true);
        initView();
        
        GlobalApplication.getInstance().setLoginVisible(true);
        receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();   
        filter.addAction(Protocol.ACTION_USERPACKET);
        this.registerReceiver(receiver,filter); 
        
        // ����activityʱ���Զ����������
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
//	        if(!(AsynSocket.getInstance().isConnected())){
//	        	ConnectHandler mConn = new ConnectHandler();
//	            mConn.connectToServer();
//			       
//		    }
    }
	 
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	 @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		GlobalApplication.getInstance().setLoginVisible(false);
	}


	@Override
	protected void onDestroy() {
		if(receiver != null) {  
			this.unregisterReceiver(receiver);
			receiver = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
//		return super.onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //��ȡ back��
			if(receiver != null) {  
				this.unregisterReceiver(receiver);
				receiver = null;
			}
			startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right); 
		}
		return true;
	}
	private void initView(){
		SharedPreferences mSP = getSharedPreferences(
				Protocol.PREFERENCE_NAME, Activity.MODE_PRIVATE);
		String lastUser = mSP.getString("LastUser", "");
		
		  mScrollView = (ScrollView) findViewById(R.id.scroll);  
		  login_back=(ImageView)findViewById(R.id.login_back);
		  login_back.setOnClickListener(this);
		  accountEt = (EditText) findViewById(R.id.userEditText);
		  accountEt.setText(lastUser);
		  accountEt.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(accountEt.hasFocus()){
					stateTv.setVisibility(View.INVISIBLE);
				}
					
			}
			   
		   });
		   passEt = (EditText) findViewById(R.id.pwdEditText);
		   passEt.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(passEt.hasFocus()){
						stateTv.setVisibility(View.INVISIBLE);
					}
						
				}
				   
			   });
		stateTv=(TextView)findViewById(R.id.loginstateTv);
		stateTv.setVisibility(View.INVISIBLE);
		   loginBtn = (Button) findViewById(R.id.login);
		   loginBtn.setOnClickListener(this);
//		   registerBtn = (Button) findViewById(R.id.registerButton);
//		   registerBtn.setOnClickListener(this);
		   forgetPwdBtn= (Button) findViewById(R.id.forgetPassButton);
		   forgetPwdBtn.setOnClickListener(this);
		   
		   if(LoginActivity.this.getIntent().getIntExtra("why",0)==1){
			   
			   accountEt.setText(SelfInfo.getInstance().getAccount());

 			   loginFail();
 		   }
	}
	 
	 @Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		  if(intent.getIntExtra("why",0)==1){
			   
			   loginFail();
		   }
	}




	//�����˺Ų�ƥ�䣬��¼ʧ��
	public void loginFail() {
//		showDialog("����������벻��Ŷ");
		stateTv.setText("����������벻��Ŷ");
		stateTv.setVisibility(View.VISIBLE);
       
	 }
	/*
	 //��¼�ɹ���save to the database ʵ����SelfInfo
	public void loginSuccess(){
		String username = accountEt.getText().toString();
        String pwd =passEt.getText().toString();
        SharedPreferences preferences =  getSharedPreferences(Protocol.PREFERENCE_NAME, 	Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
		editor.putString("LastUser",username);
		editor.commit();
		 Database db = Database.getInstance(getApplicationContext());
		 
		 db.addSelfInfo(username,pwd, Protocol.DEFAULT+"");
		 SharedPreferences msP =  getSharedPreferences(SelfInfo.getInstance().getAccount()
				 , 	Activity.MODE_PRIVATE);
	        SharedPreferences.Editor meditor = msP.edit();
	        meditor.putBoolean("isProtected", false);
			meditor.putString("numPass","");
			meditor.commit();//��������
			
		 SelfInfo.getInstance().setHeadpotoPath(db.getHeadPhoto(username));
		 
	    SelfInfo.getInstance().setInfo(username, pwd);
	    
	    if(SelfInfo.getInstance().getHeadpotoPath().equals(Protocol.DEFAULT +"")){
	        UserPacketHandler mReq = new UserPacketHandler();
		    mReq.getMyHeadPhoto();
	    }
	    
	    UserPacketHandler mReq = new UserPacketHandler();
	    mReq.getMatchHeadPhoto();
	    
	    SelfInfo.getInstance().setMainInit(true);
	    
		if(receiver != null)
		{  
			this.unregisterReceiver(receiver);
			receiver = null;
		}
//	    //��½�ɹ���������
//    	HeartPacketHandler.getInstance().setAccount();
     	try {
			HeartPacketHandler.getInstance().startHeart();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     Intent intent = new Intent(LoginActivity.this,MainActivity.class);
         intent.putExtra("who", 1);
         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
         startActivity(intent);

	 }
*/
	/*������ؼ���
	 * (non-Javadoc)
	 * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
	 */
	    public boolean dispatchTouchEvent(MotionEvent ev) {  
	        if (ev.getAction() == MotionEvent.ACTION_UP) {  
	            View v = getCurrentFocus();  
	            if (isShouldHideInput(v, ev)) {  
	      
	                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
	                if (imm != null) {  
	                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0); 
	                }  
	            }  
	             
	        }  
	        return super.dispatchTouchEvent(ev); 

	    }
	    
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.login_back:
			if(receiver != null) {
				this.unregisterReceiver(receiver);
				receiver = null;
			}
			startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right); 
//			finish();
			break;
		case R.id.login:
			if(this.validate()){
  			 //TODO give a login time, or wait too long 

    			//LoginTask t = new LoginTask();
    	    	//t.execute(username,pwd);
				
				if (AsynSocket.getInstance().isConnected()) {
					String username = accountEt.getText().toString();
	                String pwd =passEt.getText().toString();
	            
	                UserPacketHandler mUserPacketHandler = new UserPacketHandler();
	                mUserPacketHandler.Login(username,pwd);
	                SelfInfo.getInstance().setAccount(username);
	                SelfInfo.getInstance().setPwd(pwd);
				}
				else {
					stateTv.setText("��ǰ���粻���ã�������������");
					stateTv.setVisibility(View.VISIBLE);
				}
                /*
                if(receiver != null)
        		{  
        			this.unregisterReceiver(receiver);
        			receiver = null;
        		}
                
        	     Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                 intent.putExtra("who", 1);
                 intent.putExtra("usr", username);
                 intent.putExtra("pwd", pwd);
                 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
                 startActivity(intent);
//                 finish();
                 */
               }
			break;
		case R.id.forgetPassButton:
			String username = accountEt.getText().toString().trim();
			if(!checkEmail(username))
			{
				
			}else{
				UserPacketHandler mReq = new UserPacketHandler();
				 mReq.CanMailReg(username);
			}
			
			//TODO send the email to the server 
			break;
		case R.id.userEditText:
		case R.id.pwdEditText:
		
			break;
		default :
			break;
		}
		
		
		
	}
	
	
	
	// ��֤����
	private boolean validate(){
			String username = accountEt.getText().toString().trim();
			if(username.equals("")){
				stateTv.setText("�����������ʺ�");
				stateTv.setVisibility(View.VISIBLE);
				return false;
			}else if(!checkEmail(username))
				return false;
			String pwd = passEt.getText().toString().trim();
			if(pwd.equals("")){
				stateTv.setText("����������");
				stateTv.setVisibility(View.VISIBLE);
				return false;
			}else if(!checkPassword(pwd))
				return false;
			return true;
	}

	public boolean checkEmail(String email){
			if(checkEmailFormat(email) && email.length() <= 30){
				stateTv.setVisibility(View.INVISIBLE);
				return true;
			}else if (email.length()  > 30 ){
				stateTv.setText("�������Ŷ");
				stateTv.setVisibility(View.VISIBLE);
				return false;
			}else{
			
				stateTv.setText("�������ʽ��ȷ������Ŷ");
				stateTv.setVisibility(View.VISIBLE);
				return false;
			}
	}
		
	public boolean checkEmailFormat(String emailAdd){
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(emailAdd);
		return m.matches();
	}
	
	public boolean checkPassword(String pwd)
	{
		if(hasWhiteSpace(pwd)){
			stateTv.setText("�����в����пո�Ŷ");
	    	stateTv.setVisibility(View.VISIBLE);
	    	return false;
		}else{
			
		    if (pwd.length() >= 6){
		        stateTv.setVisibility(View.INVISIBLE);
		        return true;
		    }
		    else{
		    	stateTv.setText("���볤������Ҫ6λ");
		    	stateTv.setVisibility(View.VISIBLE);
		    	return false;
		    }
		}
	    
	}
		
	public boolean hasWhiteSpace(String nickName) {
			Pattern pattern = Pattern.compile("\\s");
			Matcher matcher = pattern.matcher(nickName);
			boolean found = matcher.find();
			return found;
	}
		

	private View.OnClickListener forgetPassdia = new View.OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.confirm_btn:
					String username = accountEt.getText().toString().trim();
					UserPacketHandler mReq = new UserPacketHandler();
				    mReq.clientForgetpassword(username);
			          if(ibuilder.getDialog()!=null)
							ibuilder.getDialog().dismiss();
					break;
				
				default:
					break;
				}
			}

	};
		
	public void processResponse(String str) {
		char operatorCode = 0;
		try {
			operatorCode = (char) (str.getBytes("UTF-8"))[3];
		} catch (UnsupportedEncodingException e) {
		
			e.printStackTrace();
		}
	
		switch(operatorCode){
		case Protocol.LOGIN_SUCC:
			if (loginPg != null)
				loginPg.dismiss();
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("who", 1);
            intent.putExtra("usr", accountEt.getText().toString());
            intent.putExtra("pwd", passEt.getText().toString());
			startActivity(intent);
			this.finish();
			break;
		case Protocol.LOGIN_FAIL:
			if (loginPg != null)
				loginPg.dismiss();
			this.loginFail();
			break;
		case Protocol.PASSWORD_HAVE_BEEN_SENT: //	�������뵽ָ������
//					this.showDialog("�����Ѿ�����ָ������");
			stateTv.setText("�����Ѿ�����ָ������");
			stateTv.setVisibility(View.VISIBLE);
			break;
		case Protocol.PASSWORD_SENT_FAIL: //	�������뵽ָ������
			stateTv.setText("���뷢��ָ������ʧ��");
			stateTv.setVisibility(View.VISIBLE);
			break;
		case Protocol.MAIL_CAN_REG:
			if(GlobalApplication.getInstance().isLoginVisible()){
				stateTv.setText("��������δע��");
				stateTv.setVisibility(View.VISIBLE);
			}
			break;
		case Protocol.MAIL_CANNOT_REG://���е��ʺ�
			if(GlobalApplication.getInstance().isLoginVisible()){
				String username = accountEt.getText().toString().trim();
				if(checkEmail(username)){
					ibuilder = new com.minius.ui.CustomDialog.Builder(LoginActivity.this);
					ibuilder.setTitle(null);
					ibuilder.setMessage("���ȷ�Ϻ����뽫�ᱻ���ò��ҷ��͵�����ע������");
					ibuilder.setPositiveButton("����", forgetPassdia);
					ibuilder.setNegativeButton("ȡ��", null);
					ibuilder.create().show();
					
				}
			}
			break;
		default:
			break;
		}
	}
		
		 //-------------------------------------------------------------------------------------------------------------
	public class MyReceiver extends BroadcastReceiver  
	{  
				@Override  
				public void onReceive(Context context, Intent intent)
				{  
					String action = intent.getAction();   
	
					if(Protocol.ACTION_USERPACKET.equals(action)){
						//�����û����ݰ� ����,������Ϣ,��handler���½���
						String data = intent.getStringExtra(Protocol.EXTRA_DATA);
	                  
						Bundle bdata = new Bundle();
						bdata.putString("data", data);
						Message msg = mHandler.obtainMessage();				
						msg.setData(bdata);
						msg.what = Protocol.HANDLE_RESPON;
						mHandler.sendMessage(msg);
					   
					}
				
				}	//onReceive
	} 
			
			
	private static class MyHandler extends Handler {
		WeakReference<LoginActivity> mActivity;
		MyHandler(LoginActivity loginActivity) {  
			mActivity = new WeakReference<LoginActivity>(loginActivity);  
		}
		
		@Override
		public void handleMessage(Message msg) {
			LoginActivity theActivity = mActivity.get();  
			String mData = msg.getData().getString("data");
			switch (msg.what) {
              case Protocol.HANDLE_RESPON:
					theActivity.processResponse(mData);
					break;
				default:
					break;
			}
		}
	}
	public class LoginTask extends AsyncTask<String, String, Void>  {	

				@Override
				protected void onPreExecute() {
				
					 	mProgressHUD = ProgressHUD.show(LoginActivity.this, "��½�С�����", true, false,null);
						
			    	super.onPreExecute();
				}
				
				@Override
				protected Void doInBackground(String... params) {
					   UserPacketHandler mUserPacketHandler = new UserPacketHandler();
	                    mUserPacketHandler.
	   				 Login(params[0],params[1]);
					return null;
				}
			
				@Override
				protected void onProgressUpdate(String... values) {

					super.onProgressUpdate(values);
				}
				
				@Override
				protected void onPostExecute(Void result) {
				if(	mProgressHUD != null){
					mProgressHUD.dismiss();
					mProgressHUD = null;   
				}
					super.onPostExecute(result);
				}

				
	}
			
		private ProgressHUD mProgressHUD = null;    	
		private ImageView login_back;
		private EditText accountEt;
		private EditText passEt;
		private TextView stateTv;
		private   Button loginBtn;
//		private Button registerBtn;
		private Button forgetPwdBtn;
		private MyReceiver receiver = null; 
		private MyHandler mHandler = new MyHandler(this);
		private ProgressDialog loginPg;
		private ScrollView mScrollView;  
		private Handler uiHandler = new Handler();  
		
}

