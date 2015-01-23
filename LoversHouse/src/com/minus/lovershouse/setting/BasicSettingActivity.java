package com.minus.lovershouse.setting;

import com.minus.lovershouse.R;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
//import com.minus.sql_interface.Database;
//import com.minus.table.BasicsettingTable;
//import com.minus.table.UserTable;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class BasicSettingActivity extends BroadCast implements OnClickListener {

	private ImageView back;
	private ToggleButton voice;
	private ToggleButton viberate;
	private ToggleButton num;
	private ToggleButton graph;
	private RelativeLayout modifyPass;
	private boolean needCancel=false;

	SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount()
			, Activity.MODE_PRIVATE);

	SharedPreferences.Editor mEditor = mSP.edit();
	private boolean backTemp=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.basicsetting);
		//GlobalApplication.getInstance().addActivity(this);
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSP=null;
		mEditor=null;
	}

	private void initView() {
		// TODO Auto-generated method stub
		back = (ImageView) findViewById(R.id.basicsetting_back);
		back.setOnClickListener( this);
		
		voice = (ToggleButton) findViewById(R.id.voice);
		viberate = (ToggleButton) findViewById(R.id.viberate);
		num = (ToggleButton) findViewById(R.id.num);
		graph = (ToggleButton) findViewById(R.id.graph);
		modifyPass = (RelativeLayout) findViewById(R.id.modifyPass);
		modifyPass.setOnClickListener(this);
		
//		isprotected=mUt.isProtected();
		voice.setChecked(mSP.getBoolean("isVoice",true));
		viberate.setChecked(mSP.getBoolean("isViberate",true));
		num.setChecked(mSP.getBoolean("isNum",false));
		graph.setChecked(mSP.getBoolean("isGraph",false));
		
		voice.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

				voice.setChecked(isChecked);
				
				mEditor.putBoolean("isVoice", isChecked);
				mEditor.commit();
				

				checkSetting();
			}

		});
		
		viberate.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

				viberate.setChecked(isChecked);
				
				mEditor.putBoolean("isViberate", isChecked);
				mEditor.commit();


				checkSetting();
			}

		});
			
		/*
		 * wholauch
		 * 1后台解锁 6登录解锁 2修改密码 3解锁并取消密码 4图形与数字密码切换 5直接设置
		 */
		num.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			

			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

			if(!backTemp){
				
			
				num.setChecked(isChecked);
				
//			 if(!backnForcheck){
				if(needCancel){
					Intent regIntent =new Intent(BasicSettingActivity.this, PasswordActivity.class);
					regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					Bundle regBundle = new Bundle();
					regBundle.putString("who", "4");//解锁并取消密码，再跳转到图形加锁
					regIntent.putExtras(regBundle);
					startActivityForResult(regIntent,4);
				}else{
					if(graph.isChecked()){
						needCancel=true;
//						num.setChecked(false);
						graph.performClick();
					}else{
//					 if(!backFormodify){//修改密码的非正常回调
						if(isChecked){
//							if(mSP.getString("numPass","").length()==4){//先解锁再设置
//								mEditor.putBoolean("isProtected", true);
//								mEditor.putBoolean("isNum", true);
//								mEditor.commit();

//							}else{//直接设置 4是从另外一个解锁方案跳入
								Intent regIntent =new Intent(BasicSettingActivity.this, PasswordActivity.class);
								regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								Bundle regBundle = new Bundle();
							    regBundle.putString("who", "5");
							    regIntent.putExtras(regBundle);
							    startActivityForResult(regIntent,5);
//							}
						}else{
							
								Intent regIntent =new Intent(BasicSettingActivity.this, PasswordActivity.class);
								regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								Bundle regBundle = new Bundle();
							    regBundle.putString("who", "3");
							    regIntent.putExtras(regBundle);
								startActivityForResult(regIntent,3);
							}
							
//						}else
//							backFormodify=false;
					
					}
				}
//			}else
//				backnForcheck=false;
		}
			}
	});
		
		
		graph.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

			if(!backTemp){
				
			
				graph.setChecked(isChecked);
//			 if(!backgForcheck){
				if(needCancel){
					Intent regIntent =new Intent(BasicSettingActivity.this, UnlockGesturePasswordActivity.class);
					regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					Bundle regBundle = new Bundle();
			        regBundle.putString("who", "4");//解锁并取消密码，再跳转到数字加锁
			        regIntent.putExtras(regBundle);
					startActivityForResult(regIntent,4);
				}else{
					if(num.isChecked()){
						needCancel=true;
	//					graph.setChecked(false);
						num.performClick();
					}else{
//					  if(!backFormodify){//修改密码的非正常回调
						if(isChecked){
//							if(GlobalApplication.getInstance().getLockPatternUtils().savedPatternExists()){
//								mEditor.putBoolean("isProtected", true);
//								mEditor.putBoolean("isGraph", true);
//								mEditor.commit();

//							}else{
								Intent intent = new Intent(BasicSettingActivity.this,//第一次设置，不用解锁
										UnlockGesturePasswordActivity.class);//unlo
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								Bundle regBundle = new Bundle();
						        regBundle.putString("who", "5");
						        intent.putExtras(regBundle);
								startActivityForResult(intent,5);
//							}
							
						}else{
		
							
								Intent regIntent =new Intent(BasicSettingActivity.this, UnlockGesturePasswordActivity.class);
								regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								Bundle regBundle = new Bundle();
						        regBundle.putString("who", "3");//解锁并取消密码，删除密码文件
						        regIntent.putExtras(regBundle);
								startActivityForResult(regIntent,3);
//								GlobalApplication.getInstance().getLockPatternUtils().clearLock();//删除密码文件
							}
//					 }else
//						backFormodify=false;
					}

				}
				
//			 }else
//				 backgForcheck=false;
		}
			}
		});
		backTemp=false;
	}
	
	/*
	 * 判断系统和本应用综合后的设置
	 * 0:不振动不响铃 1:振动但不响铃 2:响铃但不振动 3:既振动也响铃
	 */
	protected void checkSetting() {
		// TODO Auto-generated method stub
		switch(GlobalApplication.getInstance().isSoundViberate()){
		 case 0:
			 mEditor.putInt("voiceOrviberate", 0);
				mEditor.commit();
			 break;
		 case 1:
			 if(viberate.isChecked()){
				 mEditor.putInt("voiceOrviberate", 1);
				mEditor.commit();
			 }
			 else{
				 mEditor.putInt("voiceOrviberate", 0);
					mEditor.commit();
			 }
			 break;
		 case 2:
			 if(voice.isChecked()){
				 mEditor.putInt("voiceOrviberate", 2);
					mEditor.commit();
			 }
			 else{
				 mEditor.putInt("voiceOrviberate", 0);
					mEditor.commit();
			 }
			 break;
		 case 3:
			 if(viberate.isChecked()&&voice.isChecked()){
				 mEditor.putInt("voiceOrviberate", 3);
					mEditor.commit();
			 }
			 else{
				 if(viberate.isChecked()){
					 mEditor.putInt("voiceOrviberate", 1);
						mEditor.commit();
				 }
				 else if(voice.isChecked()){
					 mEditor.putInt("voiceOrviberate", 2);
						mEditor.commit();
				 }
				 else{
					 mEditor.putInt("voiceOrviberate", 0);
						mEditor.commit();
				 }
			 }
				 
			 break;
		 
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.equals(back)){
			finish();
		}else if(v.equals(modifyPass)){
			if(mSP.getBoolean("isProtected",false)){
				
				if(num.isChecked()){
					Intent regIntent =new Intent(BasicSettingActivity.this, PasswordActivity.class);
					regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					Bundle regBundle = new Bundle();
			        regBundle.putString("who", "2");
			        regIntent.putExtras(regBundle);
			        startActivityForResult(regIntent,2);
				}else if(graph.isChecked()){
					Intent regIntent =new Intent(BasicSettingActivity.this, UnlockGesturePasswordActivity.class);
					regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					Bundle regBundle = new Bundle();
			        regBundle.putString("who", "2");//先解锁再设置密码
			        regIntent.putExtras(regBundle);
			        startActivityForResult(regIntent,2);
				}
			}
		
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==4)
			needCancel=false;
		backTemp=true;
//		if(requestCode==4){
//			needCancel=false;
//			if(resultCode==2){
//				if(mSP.getBoolean("isProtected", false)){
//					backnForcheck=true;
//					backgForcheck=true;
//				}else {
//					if(mSP.getString("numPass", "").length()==4){
//						backgForcheck=true;
//					}else if(GlobalApplication.getInstance().getLockPatternUtils().savedPatternExists()){
//						backnForcheck=true;
//					}
//				}
//		
//			}
//		}else if(resultCode==2){
//			
//			if(requestCode!=2||(requestCode==2&&!mSP.getBoolean("isProtected", false)))
//					
//				backFormodify=true;	
//		
//		}

			
		super.onActivityResult(requestCode, resultCode, data);
	}

}

	