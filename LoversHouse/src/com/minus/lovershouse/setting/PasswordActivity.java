package com.minus.lovershouse.setting;

//import java.math.RoundingMode;

import com.minius.common.CommonBitmap;
import com.minius.ui.HeadPhotoHanddler;
import com.minius.ui.CustomDialog.Builder;
import com.minus.lovershouse.setting.CommonPasswordView;
import com.minus.lovershouse.setting.CommonPasswordView.OnClickPhoneNumberListener;
//import com.minus.lovershouse.setting.UnlockGesturePasswordActivity.HandleHeadPhotoTask;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.RegisterActivity;
//import com.minus.lovershouse.WelcomeActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.map.MapActivity;
//import com.minus.lovershouse.util.RoundedImageView;
import com.minus.sql_interface.Database;
//import com.minus.table.BasicsettingTable;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.UserPacketHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
//import android.widget.Toast;


public class PasswordActivity extends Activity implements OnClickListener {

	private FrameLayout numpass_top;
	private ImageView numpass_back;
	private ImageView unlockimage;
	private TextView unlockaccount;
	private ImageButton forgetpass;
//	private RelativeLayout relativeLayout;
	private com.minus.lovershouse.setting.CommonPasswordView commonPasswordView;
	private com.minus.lovershouse.setting.CommonPasswordView commonPasswordView2;
	private String firstPass=null;
	private String secondPass=null;
	private int timesFlag=1;
	private Animation mShakeAnim;
	SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount()
			, Activity.MODE_PRIVATE);

	SharedPreferences.Editor mEditor = mSP.edit();
	String whoLauch=null;
	private Bitmap endBm = null;
	private Handler bmHandler = null;
	private ViewFlipper flipper;
	private String lastUser=null;
	private Builder ibuilder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.password);
		//GlobalApplication.getInstance().addActivity(this);
		initView();
		PushAgent.getInstance(this).onAppStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)// 强制竖屏
		{   
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
		}
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		lastUser=null;
		firstPass=null;
		secondPass=null;
		whoLauch=null;
		mSP=null;
		mEditor=null;
		endBm=null;
		commonPasswordView=null;
		commonPasswordView2=null;
	}
	private void initView() {
		// TODO Auto-generated method stub
		numpass_top = (FrameLayout) findViewById(R.id.numpass_top);
		numpass_back = (ImageView) findViewById(R.id.numpass_back);
		numpass_back.setOnClickListener(this);
		unlockimage = (ImageView) findViewById(R.id.unlockimage);
		unlockaccount = (TextView) findViewById(R.id.unlockaccount);
		SharedPreferences mSP1 = GlobalApplication.getInstance()
				.getSharedPreferences(Protocol.PREFERENCE_NAME,
						Activity.MODE_PRIVATE);
		lastUser = mSP1.getString("LastUser", "");
		String isBoy = mSP1.getString("LastUsersex", "b");
		unlockaccount.setText(lastUser);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		forgetpass = (ImageButton) findViewById(R.id.forgetpass);
		forgetpass.setOnClickListener(this);
//		relativeLayout = (RelativeLayout) findViewById(R.id.passlayout);
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		commonPasswordView = new CommonPasswordView(PasswordActivity.this);
//		commonPasswordView.setHint("请输入密码",null);
		commonPasswordView2 = new CommonPasswordView(PasswordActivity.this);
		Bundle unlockBundle = PasswordActivity.this.getIntent().getExtras();
		whoLauch = unlockBundle.getString("who");
		 if(whoLauch.equals("1")||whoLauch.equals("6")){
			 getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			 commonPasswordView.setHint("请输入密码",null);
			 numpass_top.setVisibility(View.GONE);
			 unlockimage.setVisibility(View.VISIBLE);
				
				boolean initFinish = mSP1.getBoolean("isMainInitFinish", false);
			 if(!initFinish){
//					SharedPreferences mSP1 = GlobalApplication.getInstance()
//							.getSharedPreferences(Protocol.PREFERENCE_NAME,
//									Activity.MODE_PRIVATE);
//					lastUser = mSP1.getString("LastUser", "");
//					String isBoy = mSP1.getString("LastUsersex", "b");
					SelfInfo.getInstance().setAccount(lastUser);
					SelfInfo.getInstance().setSex(isBoy);
					
					SelfInfo.getInstance().setHeadpotoPath(
							Database.getInstance(getApplicationContext()).getHeadPhoto(lastUser));
				}
			 
//			 if(CommonBitmap.getInstance().getMyHeadBm()!=null)
				unlockimage.setImageBitmap(AppManagerUtil.createBitmapBySize(
							CommonBitmap.getInstance().getMyHeadBm(),100,100));
			 /*else{//直接从welcome进入主页面，有锁的情况下，显示图片
					

					HandleHeadPhotoTask t = new HandleHeadPhotoTask();
					String isBoy = mSP1.getString("LastUsersex", "b");
					String isMe = "1";
					String path = Environment.getExternalStorageDirectory()  
				                + "/LoverHouse"+"/HeadPhoto"+"/"+lastUser + ".png";

					t.execute(path, isBoy, isMe);

					
			 }*/
			 unlockaccount.setVisibility(View.VISIBLE);
//			 forgetpass.setVisibility(View.VISIBLE);
			
		 }else{
			 numpass_top.setVisibility(View.VISIBLE);
			 unlockimage.setVisibility(View.GONE);
			 unlockaccount.setVisibility(View.GONE);
			 
			 if(whoLauch.equals("2")){
				 commonPasswordView.setHint("请输入原密码",null);
			 
			 }else if(whoLauch.equals("3")){
				 commonPasswordView.setHint("请输入密码",null);
			 }else if(whoLauch.equals("4")){
				 commonPasswordView.setHint("请输入原密码",null);
			 }else if(whoLauch.equals("5")){
				 commonPasswordView.setHint("请设置密码",null);
				 forgetpass.setVisibility(View.GONE);
			 }
		}
		commonPasswordView.setOnClickPhoneNumberListener(new OnClickPhoneNumberListener() {
			
			@Override
			public void OnClick() {

				 Intent intent =null;
				 if(whoLauch.equals("1")){

					 if(commonPasswordView.getPassword().equals(
							 mSP.getString("numPass", ""))){

						 commonPasswordView.setHint("解锁成功",null);
						 finish();
					 }else{

						 commonPasswordView.setHint("您输入的密码错误",mShakeAnim);
						 commonPasswordView.cleanAllPassword();
					 }
					 
					
				 }else if(whoLauch.equals("2")){

					 switch(timesFlag){
					 	case 1:
					 		
							 if(commonPasswordView.getPassword().equals(mSP.getString("numPass", ""))){

								 commonPasswordView.setHint("",null);
						 		 commonPasswordView2.setHint("请输入新的密码",null);
						 		 commonPasswordView.cleanAllPassword();
						 		 timesFlag++;
						 		 moveNext();
						 		 
							 }else{
								 commonPasswordView.cleanAllPassword();
								 commonPasswordView.setHint("密码不一致",null);
							 }
					 		break;
//					 	case 2:
//					 		commonPasswordView.setHint("请再次输入密码",null);
//					 		firstPass=commonPasswordView.getPassword();
//						 	commonPasswordView.cleanAllPassword();
//						 	timesFlag++;	
//					 		break;
					 	case 3:
	 		
					 		secondPass=commonPasswordView.getPassword();
					 		if(firstPass.equals(secondPass)){

								commonPasswordView.setHint("密码修改成功",null);
								mEditor.putString("numPass", firstPass);
								mEditor.commit();
					 			finish();
					 		}else{
					 			commonPasswordView.setHint("",null);
					 			commonPasswordView.cleanAllPassword();
					 			commonPasswordView2.setHint("密码不一致",null);
					 			timesFlag--;
					 			movePrevious();
					 		}
					 		break;
					 	default:
							 break;
					 }
					 
					 
		
				 }else if(whoLauch.equals("3")){
					 if(commonPasswordView.getPassword().equals(
							mSP.getString("numPass", ""))){
						 	mEditor.putBoolean("isProtected", false);
							mEditor.putBoolean("isNum", false);
							mEditor.putString("numPass", "");
							mEditor.commit(); commonPasswordView.setHint("解锁成功",null);
							finish();
					 }else{
						 commonPasswordView.cleanAllPassword();
						 commonPasswordView.setHint("密码不一致",null);
				 		
					 }
					 
					 
				 }else if(whoLauch.equals("4")){

					 if(commonPasswordView.getPassword().equals(
							 mSP.getString("numPass", ""))){
						 	commonPasswordView.setHint("解锁成功",null);

							intent = new Intent(PasswordActivity.this,//第一次设置，不用解锁
									UnlockGesturePasswordActivity.class);//
							intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							Bundle regBundle = new Bundle();
					        regBundle.putString("who", "5");//跳转到图形解锁
					        intent.putExtras(regBundle);
							startActivityForResult(intent, 5);
						 
							overridePendingTransition(R.anim.in_from_right_slow,
									R.anim.out_to_left_slow);
					 }else{
						 commonPasswordView.cleanAllPassword();
				 		 commonPasswordView.setHint("密码不一致",null);
					 }
					
				 }else if(whoLauch.equals("5")){
					 switch(timesFlag){
					 	case 1:
					 		
					 		firstPass=commonPasswordView.getPassword();
					 		commonPasswordView.setHint("",null);
					 		commonPasswordView2.setHint("请再次输入密码",null);
					 		commonPasswordView.cleanAllPassword();
					 		timesFlag++;
					 		moveNext();
					 		break;
					 	
//					 	case 2:
//					 		secondPass=commonPasswordView.getPassword();
//					 		if(firstPass.equals(secondPass)){
//					 			mEditor.putBoolean("isProtected", true);
//								mEditor.putBoolean("isNum", true);
//								mEditor.putBoolean("isGraph", false);
//								mEditor.putString("numPass", firstPass);
//								mEditor.commit();
//					 			commonPasswordView.setHint("密码设置成功",null);
//					 			finish();
//					 		}else{
//					 			commonPasswordView.cleanAllPassword();
//					 			commonPasswordView.setHint("密码不一致",null);
//					 		}
//					 		break;
//					 	default:
//							 break;
					 }
				 }else if(whoLauch.equals("6")){
					 SharedPreferences sP  = GlobalApplication.getInstance().getSharedPreferences(
							 lastUser, Activity.MODE_PRIVATE);
						 if(commonPasswordView.getPassword().equals(
								 sP.getString("numPass", ""))){
							 commonPasswordView.setHint("解锁成功",null);
							 intent=new Intent();
	    	                 intent.setClass(PasswordActivity.this,MainActivity.class);
	    	                 intent.putExtra("who", 2);
	    	                 intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
	    	                 startActivity(intent);
	    	                 overridePendingTransition(R.anim.in_from_right_slow,
										R.anim.out_to_left_slow);
	    	                 finish();
						 
						 }else{
							 commonPasswordView.setHint("您输入的密码错误",mShakeAnim);
							 commonPasswordView.cleanAllPassword();
						 }
					 
					 
				 }
				
				
			}
		});
		
		commonPasswordView2.setOnClickPhoneNumberListener(new OnClickPhoneNumberListener() {
			
			@Override
			public void OnClick() {

				 Intent intent =null;
				 if(whoLauch.equals("2")){

					 switch(timesFlag){
					 	
					 	case 2:
					 		commonPasswordView.setHint("请再次输入密码",null);
					 		commonPasswordView2.setHint("",null);
					 		firstPass=commonPasswordView2.getPassword();
						 	commonPasswordView2.cleanAllPassword();
						 	timesFlag++;	
						 	moveNext();
					 		break;
					 	
					 	default:
							 break;
					 }
					 
				 }else if(whoLauch.equals("5")){
					 switch(timesFlag){
					 	
					 	case 2:
					 		secondPass=commonPasswordView2.getPassword();
					 		if(firstPass.equals(secondPass)){
					 			mEditor.putBoolean("isProtected", true);
								mEditor.putBoolean("isNum", true);
								mEditor.putBoolean("isGraph", false);
								mEditor.putString("numPass", firstPass);
								mEditor.commit();
					 			commonPasswordView2.setHint("密码设置成功",null);
					 			finish();
					 		}else{
					 			commonPasswordView2.cleanAllPassword();
					 			commonPasswordView.setHint("密码不一致",null);
					 			commonPasswordView2.setHint("",null);
					 			timesFlag--;
					 			movePrevious();
					 		}
					 		break;
					 	default:
							 break;
					 }
				 }
				
				
			}
		});

		flipper.addView(commonPasswordView);
	}//end of initview



    private void movePrevious() {
    	if (flipper.getChildCount() > 1) {
        	flipper.removeViewAt(0);
        }
    	if(timesFlag==1)
        	flipper.addView(commonPasswordView, 1);
        else if(timesFlag==2)
        	flipper.addView(commonPasswordView2, 1);
        flipper.setInAnimation(PasswordActivity.this, R.anim.in_from_left_slow);
        flipper.setOutAnimation(PasswordActivity.this, R.anim.out_to_right_slow);
        flipper.showPrevious();
    }
    
	private void moveNext() {
        if (flipper.getChildCount() > 1) {
        	flipper.removeViewAt(0);
        }
        if(timesFlag==2)
        	flipper.addView(commonPasswordView2, 1);
        else if(timesFlag==3)
        	flipper.addView(commonPasswordView, 1);
        flipper.setInAnimation(PasswordActivity.this, R.anim.in_from_right_slow);
        flipper.setOutAnimation(PasswordActivity.this, R.anim.out_to_left_slow);
        flipper.showNext();
    }
	/*
	// 将头像与框合并 90.90 并暂时存在全局变量中
			// path ,isboy,isme
			public class HandleHeadPhotoTask extends AsyncTask<String, String, Void> {

				@Override
				protected void onPreExecute() {

					super.onPreExecute();
				}

				@Override
				protected Void doInBackground(String... params) {
					HeadPhotoHanddler mHeadPhotoHandler = new HeadPhotoHanddler();
					GlobalApplication mIns = GlobalApplication.getInstance();
					if (params[1].equals("b")) {
						Bitmap frameBm = BitmapFactory.decodeResource(getResources(),
								R.drawable.boy_photoframe);
						endBm = (mHeadPhotoHandler.handleHeadPhoto(params[0], frameBm,
								mIns.getApplicationContext()));
					} else {
						Bitmap frameBm = BitmapFactory.decodeResource(getResources(),
								R.drawable.girl_photoframe);
						endBm = (mHeadPhotoHandler.handleHeadPhoto(params[0], frameBm,
								mIns.getApplicationContext()));
					}
					
					return null;
				}

				@Override
				protected void onProgressUpdate(String... values) {

					
					
					super.onProgressUpdate(values);
				}

				@Override
				protected void onPostExecute(Void result) {


					if(bmHandler == null){
						bmHandler = new MyHandler();
						
					}
					bmHandler.sendEmptyMessage(1);
					super.onPostExecute(result);
				}

			}
			@SuppressLint("HandlerLeak")
			private  class MyHandler extends Handler {
				

				@Override
				public void handleMessage(Message msg) {
					
					unlockimage.setImageBitmap(endBm);
				}
			}*/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.equals(numpass_back)){
			if(!whoLauch.equals("1")&&!whoLauch.equals("6")){
				setResult(2);
				finish();
			}
		}
		if(v.equals(forgetpass)){
			ibuilder = new com.minius.ui.CustomDialog.Builder(PasswordActivity.this);
			ibuilder.setTitle(null);
			ibuilder.setMessage("是否确认重新登录？");
			ibuilder.setPositiveButton("确定", forgetDialog);
			ibuilder.setNegativeButton("取消", null);
			ibuilder.create().show();
			
		}
	}
	
	private View.OnClickListener forgetDialog = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.confirm_btn:
				SharedPreferences mSP1 = GlobalApplication.getInstance()
				.getSharedPreferences(Protocol.PREFERENCE_NAME,
						Activity.MODE_PRIVATE);
		boolean initFinish = mSP1.getBoolean("isMainInitFinish", false);
				if(initFinish){
  		        	UserPacketHandler mReq = new UserPacketHandler();
  					mReq.Logout();
		        	}
				
					SelfInfo.getInstance().setDefault();
					GlobalApplication.getInstance().setCommonDefault();
					GlobalApplication.getInstance().setTargetDefault();
					SelfInfo.getInstance().setOnline(false);//下线
					
					
  				mEditor.putBoolean("isProtected", false);
  				mEditor.putBoolean("isNum", false);
  				mEditor.putString("numPass", "");
  				mEditor.commit();
  				
  				
				SharedPreferences.Editor mEditor1 = mSP1.edit();
				mEditor1.putString("LastUser", "");
				mEditor1.putBoolean("isMainInitFinish", false);
				mEditor1.commit();			
					
				Intent intent = new Intent();
				intent.setClass(PasswordActivity.this,RegisterActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				   
				GlobalApplication.getInstance().finishOtherActivity();
				finish();
  				
				break;
			
			default:
				break;
			}
		}

	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==5&&resultCode==2){
			setResult(2);
			
		}
		finish();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(!whoLauch.equals("1")){
			setResult(2);
			finish();
		}
		
		return true;
	}

}
