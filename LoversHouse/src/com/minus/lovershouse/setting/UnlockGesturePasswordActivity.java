package com.minus.lovershouse.setting;

import java.util.ArrayList;
import java.util.List;

//import net.tsz.afinal.exception.DbException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
//import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
//import android.os.Handler;
//import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
//import android.widget.RelativeLayout;
import android.widget.TextView;
//import android.widget.Toast;

import com.minius.common.CommonBitmap;
import com.minius.ui.HeadPhotoHanddler;
import com.minius.ui.CustomDialog.Builder;
//import com.minus.lovershouse.MainActivity.HandleHeadPhotoTask;
import com.minus.lovershouse.setting.LockPatternUtils;
import com.minus.lovershouse.setting.LockPatternView;
import com.minus.lovershouse.setting.LockPatternView.Cell;
import com.minus.lovershouse.setting.LockPatternView.DisplayMode;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
//import com.minus.lovershouse.util.RoundedImageView;

import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.RegisterActivity;
//import com.minus.lovershouse.WelcomeActivity;
import com.minus.sql_interface.Database;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.UserPacketHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
public class UnlockGesturePasswordActivity extends Activity {
	
	private static final int ID_EMPTY_MESSAGE = -1;
	private static final String KEY_UI_STAGE = "uiStage";
	private static final String KEY_PATTERN_CHOICE = "chosenPattern";
	private FrameLayout gpass_top;
	private ImageView gpass_back;
	private ImageView gunlockimage;
	private LockGraphView mLockGraphView=null;
	private LockGraphView mLockGraphView2=null;
	private ViewFlipper flipper;
	private CountDownTimer mCountdownTimer = null;
	private TextView gunlockaccount;
//	private TextView mHeadTextView;
//	private TextView mBlankView;
	private Animation mShakeAnim;
	private Bitmap endBm = null;
	private Handler bmHandler = null;

	protected List<LockPatternView.Cell> mChosenPattern = null;
	private Stage mUiStage = Stage.Introduction;

	private boolean stage=false;
	
	private String lastUser=null;
	
	SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount()
			, Activity.MODE_PRIVATE);

	SharedPreferences.Editor mEditor = mSP.edit();
	private ImageButton gesturepwd_unlock_forget;
	private Builder ibuilder;
	
	private int timesFlag=1;//当前的view
	/**
	 * Keep track internally of where the user is in choosing a pattern.
	 */
	protected enum Stage {

		Introduction(R.string.lockpattern_recording_intro_header,
				ID_EMPTY_MESSAGE, true), 
				ChoiceTooShort(
				R.string.lockpattern_recording_incorrect_too_short,
				ID_EMPTY_MESSAGE, true), FirstChoiceValid(
				R.string.lockpattern_pattern_entered_header,
				ID_EMPTY_MESSAGE, false), NeedToConfirm(
				R.string.lockpattern_need_to_confirm
				, ID_EMPTY_MESSAGE, true), ConfirmWrong(
				R.string.lockpattern_need_to_unlock_wrong,
				ID_EMPTY_MESSAGE, true), ChoiceConfirmed(
				R.string.lockpattern_pattern_confirmed_header,
				ID_EMPTY_MESSAGE, false), Unlock(
				R.string.lockpattern_pattern_unclockhint,
				ID_EMPTY_MESSAGE, true), UnlockWrong(
				R.string.lockpattern_pattern_unclockwronghint,
				ID_EMPTY_MESSAGE, true);

		/**
		 * @param headerMessage
		 *            The message displayed at the top.
		 * @param leftMode
		 *            The mode of the left button.
		 * @param rightMode
		 *            The mode of the right button.
		 * @param footerMessage
		 *            The footer message.
		 * @param patternEnabled
		 *            Whether the pattern widget is enabled.
		 */
		Stage(int headerMessage,  int footerMessage,
				boolean patternEnabled) {
			this.headerMessage = headerMessage;
			this.footerMessage = footerMessage;
			this.patternEnabled = patternEnabled;
		}

		final int headerMessage;
		final int footerMessage;
		final boolean patternEnabled;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.gesturepassword_unlock);
		
		gpass_top = (FrameLayout) findViewById(R.id.gpass_top);
		gpass_back = (ImageView) findViewById(R.id.gpass_back);
		gpass_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v.equals(gpass_back)){
					if(!whoLauch.equals("1")&&!whoLauch.equals("6")){
						setResult(2);
						finish();
					}
				}
			}
		});
//		mLockPatternView = (LockPatternView) this
//				.findViewById(R.id.gesturepwd_unlock_lockview);
		gunlockimage = (ImageView) findViewById(R.id.gunlockimage);
		
//		if(CommonBitmap.getInstance().getMyHeadBm()!=null)
		SharedPreferences mSP1 = GlobalApplication.getInstance()
				.getSharedPreferences(Protocol.PREFERENCE_NAME,
						Activity.MODE_PRIVATE);
		boolean initFinish = mSP1.getBoolean("isMainInitFinish", false);
		if(!initFinish){
		
			lastUser = mSP1.getString("LastUser", "");
			String isBoy = mSP1.getString("LastUsersex", "b");
			SelfInfo.getInstance().setAccount(lastUser);
			SelfInfo.getInstance().setSex(isBoy);
			
			SelfInfo.getInstance().setHeadpotoPath(
					Database.getInstance(getApplicationContext()).getHeadPhoto(lastUser));
		}
			
		gunlockimage.setImageBitmap(AppManagerUtil.createBitmapBySize(
					CommonBitmap.getInstance().getMyHeadBm(),100,100));
			/*
		else{//直接从welcome进入主页面，有锁的情况下，显示图片
			SharedPreferences mSP1 = GlobalApplication.getInstance()
					.getSharedPreferences(Protocol.PREFERENCE_NAME,
							Activity.MODE_PRIVATE);
			lastUser = mSP1.getString("LastUser", "");
			

			HandleHeadPhotoTask t = new HandleHeadPhotoTask();
			String isBoy = mSP1.getString("LastUsersex", "b");
			String isMe = "1";
			String path = Environment.getExternalStorageDirectory()  
		                + "/LoverHouse"+"/HeadPhoto"+"/"+lastUser + ".png";

			t.execute(path, isBoy, isMe);

			
		}*/
		
//		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
//		mLockPatternView.setTactileFeedbackEnabled(true);
		gunlockaccount = (TextView) findViewById(R.id.gunlockaccount);
		if(SelfInfo.getInstance().getAccount()!=null)
			gunlockaccount.setText(SelfInfo.getInstance().getAccount());
		else{
			UserTable selfInfo = Database.getInstance(this).getSelfInfo();
			gunlockaccount.setText(selfInfo.getAccount());
		}
//		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
//		mHeadTextView.setText("请输入密码");
		
//		mBlankView = (TextView) findViewById(R.id.gesturepwd_unlock_blank);//分隔符 无实际意义
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		mLockGraphView = new LockGraphView(UnlockGesturePasswordActivity.this);
		mLockGraphView2 = new LockGraphView(UnlockGesturePasswordActivity.this);
		flipper.addView(mLockGraphView);
//		mHeaderText = (TextView) findViewById(R.id.gesturepwd_create_text);
		mLockGraphView.getpattern().setOnPatternListener(mChooseNewLockPatternListener);
		mLockGraphView2.getpattern().setOnPatternListener(mChooseNewLockPatternListener2);
		mLockGraphView.getpattern().setTactileFeedbackEnabled(true);
		mLockGraphView2.getpattern().setTactileFeedbackEnabled(true);
		
		gesturepwd_unlock_forget=(ImageButton) findViewById(R.id.forgetpass);
		gesturepwd_unlock_forget.setOnClickListener(new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ibuilder = new com.minius.ui.CustomDialog.Builder(UnlockGesturePasswordActivity.this);
				ibuilder.setTitle(null);
				ibuilder.setMessage("是否确认重新登录？");
				ibuilder.setPositiveButton("确定", forgetDialog);
				ibuilder.setNegativeButton("取消", null);
				ibuilder.create().show();
				
			}
		});
		Bundle unlockBundle = UnlockGesturePasswordActivity.this.getIntent().getExtras();
		whoLauch = unlockBundle.getString("who");
		if(whoLauch.equals("1")||whoLauch.equals("6")){
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			gpass_top.setVisibility(View.GONE);
			gunlockimage.setVisibility(View.VISIBLE);
			gunlockaccount.setVisibility(View.VISIBLE);
//			mBlankView.setVisibility(View.GONE);
			updateStage(Stage.Unlock,false);
		}else{
			gpass_top.setVisibility(View.VISIBLE);
			gunlockimage.setVisibility(View.GONE);
			gunlockaccount.setVisibility(View.INVISIBLE);
//			mBlankView.setVisibility(View.INVISIBLE);
			updateStage(Stage.Introduction,false);
			if(whoLauch.equals("5")){
				gesturepwd_unlock_forget.setVisibility(View.GONE);
			}
		}
		
//		if (savedInstanceState == null) {
//			updateStage(Stage.Introduction);
//		} else {
//			// restore from previous state
//			final String patternString = savedInstanceState
//					.getString(KEY_PATTERN_CHOICE);
//			if (patternString != null) {
//				mChosenPattern = LockPatternUtils
//						.stringToPattern(patternString);
//			}
//			updateStage(Stage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
//		}
		
		PushAgent.getInstance(this).onAppStart();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_UI_STAGE, mUiStage.ordinal());
		if (mChosenPattern != null) {
			outState.putString(KEY_PATTERN_CHOICE,
					LockPatternUtils.patternToString(mChosenPattern));
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
	  				mEditor.putBoolean("isGraph", false);
	  				mEditor.commit();
  				    
					
					SharedPreferences.Editor mEditor1 = mSP1.edit();
					mEditor1.putString("LastUser", "");
					mEditor1.putBoolean("isMainInitFinish", false);
					mEditor1.commit();
					
					
					Intent intent = new Intent();
				    intent.setClass(UnlockGesturePasswordActivity.this,RegisterActivity.class);
				    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				    startActivity(intent);
				    
				    if(GlobalApplication.getInstance().getLockPatternUtils().savedPatternExists())
				    	GlobalApplication.getInstance().getLockPatternUtils().clearLock();//删除密码文件
				    GlobalApplication.getInstance().finishOtherActivity();
				    finish();
				    break;
			default:
				    break;
			}
		}
	};
	
	@Override
	protected void onResume() {
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
		super.onDestroy();
		endBm=null;
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
		mLockGraphView=null;
		mLockGraphView2=null;
		
	}
	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockGraphView.getpattern().clearPattern();
		}
	};

	private Runnable mClearPatternRunnable2 = new Runnable() {
		public void run() {
			mLockGraphView2.getpattern().clearPattern();
		}
	};
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
			

			public void handleMessage(Message msg) {
				
				gunlockimage.setImageBitmap(endBm);
			}
		}*/
		
	protected String whoLauch=null;

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockGraphView.getpattern().removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockGraphView.getpattern().removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			
//				mLockGraphView
//						.setDisplayMode(LockPatternView.DisplayMode.Correct);

//				mHeadTextView.setText("解锁成功");
				
//			if (GlobalApplication.getInstance().getLockPatternUtils().checkPattern(pattern)) {	
			//Intent intent =null;
			
			if(whoLauch.equals("1")){

				if (GlobalApplication.getInstance().getLockPatternUtils().checkPattern(pattern)) 
					finish();
				else {
//					mLockGraphView.
//							.setDisplayMode(LockPatternView.DisplayMode.Wrong);
					//if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {

						//mLockGraphView.setHint("您输入的密码错误",mShakeAnim);//，还可以再输入" + retry + "次"
						updateStage(Stage.UnlockWrong,false);

						mLockGraphView.postDelayed(mClearPatternRunnable, 1000);
					//}
				}
			}else if(whoLauch.equals("2")){

//				intent = new Intent(UnlockGesturePasswordActivity.this,
//							CreateGesturePasswordActivity.class);
//
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				startActivityForResult(intent,2);// 打开新的Activity
//				overridePendingTransition(R.anim.in_from_right_slow,
//							R.anim.out_to_left_slow);
				
				switch(timesFlag){
			 	case 1:
			 		
			 		if (GlobalApplication.getInstance().getLockPatternUtils().checkPattern(pattern)) {
			 			
			 			GlobalApplication.getInstance().getLockPatternUtils().clearLock();//删除密码文件

			 			mLockGraphView.setHint("");
			 			//mLockGraphView2.setHint("请输入新的密码",null);
//				 		commonPasswordView.cleanAllPassword();
			 			updateStage(Stage.Introduction,false);
				 		timesFlag++;
				 		moveNext();
				 		stage=true;
				 		gesturepwd_unlock_forget.setVisibility(View.GONE);
					 }else{
//						 commonPasswordView.cleanAllPassword();
						 //mLockGraphView.setHint("密码不一致");
						 updateStage(Stage.UnlockWrong,false);
						 mLockGraphView.postDelayed(mClearPatternRunnable, 1000);
					 }
			 		break;

			 	case 3:
		
			 		if (mChosenPattern.equals(pattern)) {
			 			
			 			mLockGraphView.setHint("密码修改成功");
			 			GlobalApplication.getInstance().getLockPatternUtils().saveLockPattern(pattern);
			 			finish();
			 		}else{
			 			mLockGraphView.setHint("");
			 			//mLockGraphView2.setHint("密码不一致",null);
			 			updateStage(Stage.ConfirmWrong,true);
			 			timesFlag--;
			 			stage=true;
			 			movePrevious();
			 			mLockGraphView.postDelayed(mClearPatternRunnable, 1000);
			 		}
//			 		secondPass=commonPasswordView.getPassword();
//			 		if(firstPass.equals(secondPass)){
//
//						commonPasswordView.setHint("密码修改成功",null);
//						mEditor.putString("numPass", firstPass);
//						mEditor.commit();
//			 			finish();
//			 		}else{
//			 			commonPasswordView.setHint("",null);
//			 			commonPasswordView.cleanAllPassword();
//			 			commonPasswordView2.setHint("密码不一致",null);
//			 			timesFlag--;
//			 			movePrevious();
//			 		}
			 		break;
				 default:
					break;
				 }
			}else if(whoLauch.equals("3")){

				if (GlobalApplication.getInstance().getLockPatternUtils().checkPattern(pattern)) {
					mEditor.putBoolean("isProtected", false);
					mEditor.putBoolean("isGraph", false);
						
					mEditor.commit();
					finish();
				 	GlobalApplication.getInstance().getLockPatternUtils().clearLock();//删除密码文件
				}else{
					 //mLockGraphView.setHint("密码不一致",null);
					updateStage(Stage.UnlockWrong,false);
					mLockGraphView.postDelayed(mClearPatternRunnable, 1000);
				}
			}else if(whoLauch.equals("4")){

				if (GlobalApplication.getInstance().getLockPatternUtils().checkPattern(pattern)) {
					Intent regIntent =new Intent(UnlockGesturePasswordActivity.this, PasswordActivity.class);
					regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					Bundle regBundle = new Bundle();
					regBundle.putString("who", "5");
					regIntent.putExtras(regBundle);
					startActivityForResult(regIntent,5);
					overridePendingTransition(R.anim.in_from_right_slow,
							R.anim.out_to_left_slow);
				}else{
					 //mLockGraphView.setHint("密码不一致",null);
					updateStage(Stage.UnlockWrong,false);
					mLockGraphView.postDelayed(mClearPatternRunnable, 1000);
				}
			}else if(whoLauch.equals("5")){

//				if (mUiStage == Stage.Introduction
//						|| mUiStage == Stage.ChoiceTooShort|| mUiStage == Stage.ConfirmWrong) {
					if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
						updateStage(Stage.ChoiceTooShort,false);
						mLockGraphView.postDelayed(mClearPatternRunnable, 1000);
					} else {
						mChosenPattern = new ArrayList<LockPatternView.Cell>(
								pattern);
						mLockGraphView.setHint("");
						updateStage(Stage.FirstChoiceValid,false);
						timesFlag++;
						moveNext();
						stage=true;
						
					}
//				} else {
//					throw new IllegalStateException("Unexpected stage " + mUiStage
//							+ " when " + "entering the pattern.");
//				}
		
			}else if(whoLauch.equals("6")){

					if (GlobalApplication.getInstance().getLockPatternUtils().checkPattern(pattern)){ 
						 Intent regIntent = new Intent();
						 regIntent.setClass(UnlockGesturePasswordActivity.this,MainActivity.class);
						 
						 regIntent.putExtra("who", 2);
						 regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
	   	                 startActivity(regIntent);
	   	                 overridePendingTransition(R.anim.in_from_right_slow,
									R.anim.out_to_left_slow);
   	                  	 finish();
						 
					}else{
//						 commonPasswordView.cleanAllPassword();
						 //mLockGraphView.setHint("密码不一致",null);
						 updateStage(Stage.UnlockWrong,false);
						 mLockGraphView.postDelayed(mClearPatternRunnable, 1000);
					 }
				
			} 

			
			
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
			mLockGraphView.setHint(R.string.lockpattern_recording_inprogress);
		}
	};
	

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener2 = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockGraphView2.getpattern().removeCallbacks(mClearPatternRunnable2);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockGraphView2.getpattern().removeCallbacks(mClearPatternRunnable2);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			// Log.i("way", "result = " + pattern.toString());
			
			if(whoLauch.equals("2")){

				 switch(timesFlag){
				 	
				 	case 2:
//				 		if (mUiStage == Stage.Introduction ||
//						 	mUiStage == Stage.ChoiceTooShort|| mUiStage == Stage.ConfirmWrong) {
							if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
								updateStage(Stage.ChoiceTooShort,false);
								mLockGraphView2.postDelayed(mClearPatternRunnable2, 1000);
							} else {
								if(mChosenPattern!=null)
									mChosenPattern=null;
								mChosenPattern = new ArrayList<LockPatternView.Cell>(
										pattern);
								mLockGraphView2.setHint("");
								updateStage(Stage.FirstChoiceValid,false);
								timesFlag++;
								moveNext();
								stage=false;
							}
//						} else {
//							throw new IllegalStateException("Unexpected stage " + mUiStage
//									+ " when " + "entering the pattern.");
//						}
				 		break;
				 	
				 	default:
						 break;
				 }
				 
			 }else if(whoLauch.equals("5")){
//				 if (mUiStage == Stage.NeedToConfirm
//							|| mUiStage == Stage.ChoiceTooShort) {
						if (mChosenPattern == null)
							throw new IllegalStateException(
									"null chosen pattern in stage 'need to confirm");
						if(mChosenPattern.equals(pattern)) {
							updateStage(Stage.ChoiceConfirmed,false);
							mLockGraphView2.setHint("密码设置成功");
							saveChosenPatternAndFinish();
						} else {
							//mLockGraphView.setHint("与上次输入不一致");
							mLockGraphView2.setHint("");
							updateStage(Stage.ConfirmWrong,true);
							timesFlag--;
							movePrevious();
							stage=false;
							mLockGraphView.postDelayed(mClearPatternRunnable, 1000);
						}
//					} else {
//						throw new IllegalStateException("Unexpected stage " + mUiStage
//								+ " when " + "entering the pattern.");
//					}
				 
			 }
			//mLockGraphView2.postDelayed(mClearPatternRunnable2, 1000);
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
			mLockGraphView2.setHint(R.string.lockpattern_recording_inprogress);
		}
	};
	private void movePrevious() {
    	if (flipper.getChildCount() > 1) {
        	flipper.removeViewAt(0);
        }
    	if(timesFlag==1)
        	flipper.addView(mLockGraphView, 1);
        else if(timesFlag==2)
        	flipper.addView(mLockGraphView2, 1);
        flipper.setInAnimation(UnlockGesturePasswordActivity.this, R.anim.in_from_left_slow);
        flipper.setOutAnimation(UnlockGesturePasswordActivity.this, R.anim.out_to_right_slow);
        flipper.showPrevious();
    }
    
	private void moveNext() {
        if (flipper.getChildCount() > 1) {
        	flipper.removeViewAt(0);
        }
        if(timesFlag==2)
        	flipper.addView(mLockGraphView2, 1);
        else if(timesFlag==3)
        	flipper.addView(mLockGraphView, 1);
        flipper.setInAnimation(UnlockGesturePasswordActivity.this, R.anim.in_from_right_slow);
        flipper.setOutAnimation(UnlockGesturePasswordActivity.this, R.anim.out_to_left_slow);
        flipper.showNext();
    }
	
	private void updateStage(Stage stage,Boolean needJump) {
		mUiStage = stage;
		
		// same for whether the patten is enabled
//		if (stage.patternEnabled) {
//			mLockPatternView.enableInput();
//		} else {
//			mLockPatternView.disableInput();
//		}

		if(this.stage)
			mLockGraphView2.getpattern().setDisplayMode(DisplayMode.Correct);
		else
			mLockGraphView.getpattern().setDisplayMode(DisplayMode.Correct);
		
		switch (mUiStage) {
		case Introduction:
			if(this.stage){
				mLockGraphView2.getpattern().clearPattern();	
				mLockGraphView2.setHint(stage.headerMessage);
			}else{
				mLockGraphView.getpattern().clearPattern();	
				mLockGraphView.setHint(stage.headerMessage);
			}
			
			break;
		case ChoiceTooShort:
			if(this.stage){
				mLockGraphView2.getpattern().setDisplayMode(DisplayMode.Wrong);
				//postClearPatternRunnable2();
				mLockGraphView2.setHint(getResources().getString(stage.headerMessage,
						LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
			}else{
				mLockGraphView.getpattern().setDisplayMode(DisplayMode.Wrong);
				//postClearPatternRunnable();
				mLockGraphView.setHint(getResources().getString(stage.headerMessage,
						LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
			}
			
			break;
		case FirstChoiceValid:
			updateStage(Stage.NeedToConfirm,false);
			
			break;
		case NeedToConfirm:
			if(this.stage){
				mLockGraphView2.getpattern().clearPattern();
				mLockGraphView.setHint(stage.headerMessage);
			}else{
				mLockGraphView.getpattern().clearPattern();
				mLockGraphView2.setHint(stage.headerMessage);
			}
			break;
		case ConfirmWrong:
			if(this.stage){
				mLockGraphView2.getpattern().clearPattern();
				if(needJump)
					mLockGraphView.setHint(stage.headerMessage);
				else
					mLockGraphView2.setHint(stage.headerMessage);
			}else{
				mLockGraphView.getpattern().clearPattern();
				if(needJump)
					mLockGraphView2.setHint(stage.headerMessage);
				else
					mLockGraphView.setHint(stage.headerMessage);
				
				
			}
			break;
		case ChoiceConfirmed:
			break;
		case Unlock:
			mLockGraphView.setHint(stage.headerMessage);
			break;
		case UnlockWrong:
			mLockGraphView.setHint(stage.headerMessage,mShakeAnim);
			break;
		}

	}
/*
	// clear the wrong pattern unless they have started a new one
	// already
	private void postClearPatternRunnable() {
		mLockGraphView.getpattern().removeCallbacks(mClearPatternRunnable);
		mLockGraphView.getpattern().postDelayed(mClearPatternRunnable, 2000);
	}
	private void postClearPatternRunnable2() {
		mLockGraphView2.getpattern().removeCallbacks(mClearPatternRunnable2);
		mLockGraphView2.getpattern().postDelayed(mClearPatternRunnable2, 2000);
	}*/


	private void saveChosenPatternAndFinish() {
		GlobalApplication.getInstance().getLockPatternUtils().saveLockPattern(mChosenPattern);
		SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount()
					, Activity.MODE_PRIVATE);

			SharedPreferences.Editor mEditor = mSP.edit();
			mEditor.putBoolean("isProtected", true);
			mEditor.putBoolean("isGraph", true);
			mEditor.putBoolean("isNum", false);
			
			mEditor.commit();
		finish();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==2)//2 5
			setResult(2);
		else if(whoLauch.equals("4")){
			setResult(1);
		}
		finish();
			
	};
	
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			if(!whoLauch.equals("1")&&!whoLauch.equals("6")){
				setResult(2);
				finish();
			}
		}
			return true;
	};
}
