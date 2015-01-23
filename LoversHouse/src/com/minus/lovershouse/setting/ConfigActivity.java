package com.minus.lovershouse.setting;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
//import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.android.pushservice.PushManager;
import com.minius.baidupush.Utils;
import com.minius.common.CommonBitmap;
import com.minius.leadpage.ConfigOperateGuide;
//import com.minius.leadpage.OperateGuide;
import com.minius.ui.CustomDialog.Builder;
import com.minius.ui.HeadPhotoHanddler;
import com.minius.ui.ProgressHUD;
//import com.minus.cropimage.CropActivity;
//import com.minus.lovershouse.LoginActivity;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.RegisterActivity;
import com.minus.lovershouse.SetAppearanceActivity;
//import com.minus.lovershouse.WelcomeActivity;
//import com.minus.lovershouse.RegisterActivity.HandleHeadPhotoTask;
import com.minus.lovershouse.adapter.MenuListAdapter;
import com.minus.lovershouse.baseActivity.BroadCast;
//import com.minus.lovershouse.bitmap.util.BoxblurBitmap;
import com.minus.lovershouse.setting.AboutusActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.ExitPopup;
import com.minus.lovershouse.util.SelectPicPopup;
//import com.minus.map.MapActivity;
import com.minus.sql_interface.Database;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.HeartPacketHandler;
import com.minus.xsocket.handler.UserPacketHandler;
import com.umeng.fb.FeedbackAgent;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
//import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ExifInterface;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
import android.net.Uri;
//import android.opengl.Visibility;
import android.os.AsyncTask;
//import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
//import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
//import android.view.ViewGroup.LayoutParams;
//import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
import android.widget.AdapterView;
//import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


//http://www.open-open.com/lib/view/open1382412545855.html

public class ConfigActivity extends BroadCast implements OnClickListener {
	 
//	  private PopupWindow guidePopupWindow = null;
//	  private ViewGroup configHelpView = null;
	  
	  private ListView menuList;
	  private MenuListAdapter menuListAdapter;
	  private ScrollView scrollpop;
	  private RelativeLayout childpop;
//	  private ImageButton backTomainBtn;
	  private int ScreenWIDTH ;
	  private RelativeLayout myaccountView;
	  private ViewGroup targetpopview;
	  
	  private FrameLayout bgFl ;
	  private ImageView bg;
	  private RelativeLayout mRl ;
	  // myaccount pop up view
	  private LinearLayout myaccountLL;
	  private LinearLayout mypassLL;
	  private TextView myaccountTv;
	  private EditText  myausernameEt;
	  private ImageButton myausernameImgBtn;
	  private ImageButton mytransImgBtn;
	  private TextView passTitle;
	  private ImageButton myaeditpassImgBtn;
	  private ImageButton backTomyaccountImgBtn;
	  
	  //tar pop  view 
	  private TextView tarAccTv;
	  private EditText  tarAccEt;
	  private RelativeLayout targetaccbtn;
	  private ImageButton tarAccImgBtn;
	  private ImageButton acctransImgBtn;
	  private TextView tarBigNameTv;
	  private EditText  tarBigNameEt;
	  private ImageButton tarBigNameImgBtn;
	  private ImageButton tartransImgBtn;
//	  private View testline;
	  private ImageButton beTogetherImgBtn;

//	  private TextView statusTv;
	  private SelectPicPopup mSelectPicPopup = null;
	  private ExitPopup mExitPopup = null;
	 
	  private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 200;
	  private static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 100;
//	  private Uri fileUri;
//	  private Bitmap headBitmap=null;
	  public static final int MEDIA_TYPE_IMAGE = 1;
      private 	int lastMotionX;
      private Boolean ucChangeO=false;
      private Boolean ucChangeN=false;
      private MyReceiver receiver = null; 
 	  private MyHandler mHandler = new MyHandler(this);
 	  private Handler scrollHandler = new Handler();
 	 
	  private boolean isfirstInitmy = true;
	  private boolean isfirstInittar = true;
	  private boolean isfirstInithouse = true;
	  private boolean isfirstInitsoft = true;
	  private TextView myAccTv;
	  private ViewGroup mysoftwareView;
	  private ViewGroup smallhouseView;
	  private String houseStyle;
	  private ImageView cosmetics;
	  private ImageView sunny;
	  private ImageView coffee;
	  private TextView basicSetting;
	  private TextView about;
	  private TextView help;
	  private TextView feedback;
	  private TextView exit;
	  private EditText oldpassEt;
	  private EditText newpassET;
	  private EditText newpassagainET;
	  private ImageButton oldpassImgBtn;
	  private ImageButton newpassImgBtn;
	  private ImageButton newpassagainImgBtn;
	  private TextView resultTv;
	  private TextView daban;
	  private ImageButton enterBtn;
	  private RelativeLayout passResult;
	  private ImageButton dabanImgBtn;
	  private TextView rank;
	  private LinearLayout texts;
	  private RelativeLayout usernameLayout;
	  private TextView hintinvite;
	  private ProgressHUD acceptMatchHUD = null;
	  private Handler matchHandler = null;
	
//	  public static final  int HANDLEBACKGROUD = 0x50;
	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_config);
	        if(receiver == null)
		   		 receiver = new MyReceiver();
		   		 IntentFilter filter=new IntentFilter();   
		   			filter.addAction(Protocol.ACTION_USERPACKET);
		               this.registerReceiver(receiver,filter); 
	        initView();
	        setGlassBackground();
	        setVolumeControlStream(AudioManager.STREAM_MUSIC);  
//	        Resources res=getResources();
//			 Bitmap bm=BitmapFactory.decodeResource(res, R.drawable._0002_background);
//			 dealWithbackgroundUI(bm);
//	        if(GlobalApplication.getInstance().getBlurBm()!=null)
//	        	dealWithbackgroundUI(GlobalApplication.getInstance().getBlurBm());
	    }
	 
	 
	 
	 


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
	
		super.onResume();
		
		boolean isNeedShowGuide = this.getIntent().getBooleanExtra("isNeedShowGuide", false);
		
		if(isNeedShowGuide){
			Intent startIntent = new Intent(ConfigActivity.this,
					ConfigOperateGuide.class);
//			startIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			this.getIntent().putExtra("isNeedShowGuide", false);
			startActivity(startIntent);
//			overridePendingTransition(R.anim.push_bottom_in,
//					R.anim.push_top_out);
		
		}
	}
	public View getTaView(int position) {
		// 得到第一个可显示控件的位置，
	int visiblePosition = menuList.getFirstVisiblePosition();
    // 只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
    if (position - visiblePosition >= 0) {
		// 得到要更新的item的view , it has an invisible view
	  View convertView =menuList.getChildAt(position
								- visiblePosition );
		return convertView;
    }
    return null;
	}
	  @SuppressWarnings("deprecation")
	private void showPopupWindow(PopupWindow pop) { 
	    	
//	    	View parent =getTaView(2);
//	    	pop.setOutsideTouchable(true);
//	    	pop.setBackgroundDrawable(new BitmapDrawable());
//	    	 int[] location = new int[2];  
//	    	 parent.getLocationOnScreen(location);  
//	           
//	    	 pop.showAtLocation( parent, Gravity.NO_GRAVITY, location[0]+parent.getWidth(), 
//	    			 location[1]-2*parent.getHeight());  
//	    	
//	        pop.update();  
	    	}
	//设置毛玻璃效果 

public void setGlassBackground(){
		
	GlobalApplication mIns = GlobalApplication.getInstance();
        if(mIns == null || mIns.getHouseStyle() == null)  {
        	this.bg.setBackgroundResource(R.drawable.config_background_blue);
        }
	if (mIns.getHouseStyle().equals(Protocol.pinkHouse)) {
		ConfigActivity.this.houseStyle = Protocol.pinkHouseStr;
	} else if (mIns.getHouseStyle().equals(Protocol.blueHouse)) {
		ConfigActivity.this.houseStyle = Protocol.blueHouseStr;
	} else if (mIns.getHouseStyle().equals(Protocol.brownHouse)) {
		ConfigActivity.this.houseStyle = Protocol.brownHouseStr;
	}

	String imageName = "config_background_" + this.houseStyle;
	int backgroundid = ConfigActivity.this.getResources().getIdentifier(
			imageName, "drawable", "com.minus.lovershouse"); // name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
	
	this.bg.setBackgroundResource(backgroundid);

	mIns = null;
	
}
/*
@TargetApi(16)
public void dealWithbackgroundUI(Bitmap mBp){
	 FrameLayout m = (FrameLayout) findViewById(R.id.bgFl);
	 ImageView bg = (ImageView) findViewById(R.id.bg);
	 Drawable drawable =new BitmapDrawable(mBp);
	 
	 if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
	 m.setBackground(drawable);
	 else{
		 m.setBackgroundDrawable(drawable);
	 }
}*/


	@Override
	protected void onDestroy() {
		houseStyle=null;
		 if(receiver != null)
			{  
				this.unregisterReceiver(receiver);
				receiver = null;
			}
		super.onDestroy();
	}


	@SuppressLint("NewApi")
	private void initView(){
//		 this.isfirstInit = true;
			DisplayMetrics dMetrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
	         ScreenWIDTH = dMetrics.widthPixels;
	         
			 bgFl = (FrameLayout) findViewById(R.id.bgFl);
			 bg = (ImageView) findViewById(R.id.bg);
	          mRl = (RelativeLayout) findViewById(R.id.mRl);
//		      backTomainBtn= (ImageButton) findViewById(R.id.backBtn);
//		      backTomainBtn.setOnClickListener(this);
		      //my
		      myaccountView=(RelativeLayout) findViewById(R.id.myaccount);
     		  myaccountLL = (LinearLayout) myaccountView.findViewById(R.id.myaccountLL);
			  mypassLL = (LinearLayout) myaccountView.findViewById(R.id.mypassLL);
			  myaccountTv = (TextView) myaccountView.findViewById(R.id.accountText);
			  myaccountTv.setText(SelfInfo.getInstance().getAccount());
			  myAccTv = (TextView) myaccountView.findViewById(R.id.useraccountText);
			  if(//SelfInfo.getInstance().getNickName()!=null||
					  SelfInfo.getInstance().getNickName()!=String.format("%c", Protocol.DEFAULT))
				  myAccTv.setText(SelfInfo.getInstance().getNickName());
			  myAccTv.setOnClickListener(this);
			  myausernameEt = (EditText) myaccountView.findViewById(R.id.usernameET);
			  myausernameImgBtn = (ImageButton) myaccountView.findViewById(R.id.usernameImgBtn );
			  mytransImgBtn = (ImageButton) myaccountView.findViewById(R.id.transImgBtn );
			  mytransImgBtn.setOnClickListener(ConfigActivity.this);
			  mytransImgBtn.setTag("0");
			  passTitle =(TextView) myaccountView.findViewById(R.id.passTitle);
			  passTitle.setOnClickListener(ConfigActivity.this);
			  myaeditpassImgBtn = (ImageButton) myaccountView.findViewById(R.id.editPassImgBtn );
			  myaeditpassImgBtn .setOnClickListener(ConfigActivity.this);
			  backTomyaccountImgBtn = (ImageButton) myaccountView.findViewById(R.id.passreturnImgBtn );
			  backTomyaccountImgBtn.setOnClickListener(ConfigActivity.this);
			  enterBtn=(ImageButton) myaccountView.findViewById(R.id.passenterImgBtn );
			  enterBtn.setOnClickListener(ConfigActivity.this);
			  texts=(LinearLayout) myaccountView.findViewById(R.id.texts);
			  
			  oldpassEt=(EditText)myaccountView.findViewById(R.id.oldpassEt);
			  oldpassImgBtn = (ImageButton) myaccountView.findViewById(R.id.oldpassImgBtn );
			  oldpassEt.setOnFocusChangeListener(new OnFocusChangeListener() {
				  
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(oldpassEt.hasFocus()==false&&oldpassEt.length()!=0){
						if(oldpassEt.getText().toString().trim().
							equals(SelfInfo.getInstance().getPwd())){
							oldpassImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0000_right));
							oldpassImgBtn.setVisibility(View.VISIBLE);
							ucChangeO=true;
							if(ucChangeN==true){
								backTomyaccountImgBtn.setVisibility(View.GONE);
								enterBtn.setVisibility(View.VISIBLE);
//								ucChangeN=false;
//								ucChangeO=false;
							}
								
							
						}else{
							oldpassImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0001_wrong));
							oldpassImgBtn.setVisibility(View.VISIBLE);
							ucChangeO=false;
							backTomyaccountImgBtn.setVisibility(View.VISIBLE);
							enterBtn.setVisibility(View.GONE);
						}
					}
					
				}
			});
			  oldpassEt.addTextChangedListener(new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
						if(oldpassEt.length()!=0){
							if(oldpassEt.getText().toString().trim().
								equals(SelfInfo.getInstance().getPwd())){
								oldpassImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0000_right));
								oldpassImgBtn.setVisibility(View.VISIBLE);
								ucChangeO=true;
								if(ucChangeN==true){
									backTomyaccountImgBtn.setVisibility(View.GONE);
									enterBtn.setVisibility(View.VISIBLE);
//									ucChangeN=false;
//									ucChangeO=false;
								}
									
								
							}else{
								oldpassImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0001_wrong));
								oldpassImgBtn.setVisibility(View.INVISIBLE);
								ucChangeO=false;
								backTomyaccountImgBtn.setVisibility(View.VISIBLE);
								enterBtn.setVisibility(View.GONE);
							}
						}
						
					}
				});
			  newpassET=(EditText)myaccountView.findViewById(R.id.newpassET);
			  newpassImgBtn = (ImageButton) myaccountView.findViewById(R.id.newpassImgBtn );
			  newpassET.setOnFocusChangeListener(new OnFocusChangeListener() {
				  
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if(newpassET.hasFocus()==false)
							
							if(newpassET.length()!=0){
								if(hasWhiteSpace(newpassET.getText().toString())){
									Toast toast = Toast.makeText(getApplicationContext(),
				            			     "密码中不能有空格哦", Toast.LENGTH_LONG);
				            			   toast.setGravity(Gravity.CENTER, 0, 0);
				            			   toast.show();
									newpassImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0001_wrong));
									newpassImgBtn.setVisibility(View.VISIBLE);
								}else{
									if(newpassET.length()>=6){
									newpassImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0000_right));
									newpassImgBtn.setVisibility(View.VISIBLE);
									}else{
									Toast toast = Toast.makeText(getApplicationContext(),
				            			     "密码最少要有六位哦", Toast.LENGTH_LONG);
				            			   toast.setGravity(Gravity.CENTER, 0, 0);
				            			   toast.show();
									newpassImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0001_wrong));
									newpassImgBtn.setVisibility(View.VISIBLE);
									}
								}
							}
						
						}
				});
			  newpassET.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					if(newpassagainET.length()!=0){
						if(hasWhiteSpace(newpassET.getText().toString())){
							if(newpassET.length()==newpassagainET.length()){
								Toast toast = Toast.makeText(getApplicationContext(),
		            			     "密码中不能有空格哦", Toast.LENGTH_LONG);
		            			   toast.setGravity(Gravity.CENTER, 0, 0);
		            			   toast.show();
							}
							newpassImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0001_wrong));
							newpassImgBtn.setVisibility(View.VISIBLE);
							ucChangeN=false;
							backTomyaccountImgBtn.setVisibility(View.VISIBLE);
							enterBtn.setVisibility(View.GONE);
						}else{
							if(newpassET.getText().toString().
									equals(newpassagainET.getText().toString())){
								newpassagainImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0000_right));
								newpassagainImgBtn.setVisibility(View.VISIBLE);
								//change
								ucChangeN=true;
								if(ucChangeO==true){
									backTomyaccountImgBtn.setVisibility(View.GONE);
									enterBtn.setVisibility(View.VISIBLE);
	//								ucChangeO=false;
	//								ucChangeN=false;
								}
							}else{
								if(newpassET.length()==newpassagainET.length()){
								Toast toast = Toast.makeText(getApplicationContext(),
			            			     "您前后输入的密码不一致", Toast.LENGTH_LONG);
			            			   toast.setGravity(Gravity.CENTER, 0, 0);
			            			   toast.show();
								}
								newpassagainImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0001_wrong));
								newpassagainImgBtn.setVisibility(View.VISIBLE);
								ucChangeN=false;
								backTomyaccountImgBtn.setVisibility(View.VISIBLE);
								enterBtn.setVisibility(View.GONE);
							}
							
							
						}
					}
				}
			});
			  newpassagainET=(EditText)myaccountView.findViewById(R.id.newpassagainET);
			  newpassagainImgBtn = (ImageButton) myaccountView.findViewById(R.id.newpassagainImgBtn );
			  newpassagainET.setOnFocusChangeListener(new OnFocusChangeListener() {
				  
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if(newpassagainET.hasFocus()==false)
							if(newpassagainET.length()!=0){
								if(hasWhiteSpace(newpassagainET.getText().toString())){
									Toast toast = Toast.makeText(getApplicationContext(),
				            			     "密码中不能有空格哦", Toast.LENGTH_LONG);
									toast.setGravity(Gravity.CENTER, 0, 0);
				            		toast.show();
				            		newpassagainImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0001_wrong));
				            		newpassagainImgBtn.setVisibility(View.VISIBLE);
				            		ucChangeN=false;
									backTomyaccountImgBtn.setVisibility(View.VISIBLE);
									enterBtn.setVisibility(View.GONE);
								}else{
									if(newpassET.getText().toString().
										equals(newpassagainET.getText().toString())){
										newpassagainImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0000_right));
										newpassagainImgBtn.setVisibility(View.VISIBLE);
										//change
										
										ucChangeN=true;
										if(ucChangeO==true){
											backTomyaccountImgBtn.setVisibility(View.GONE);
											enterBtn.setVisibility(View.VISIBLE);
	//										ucChangeO=false;
	//										ucChangeN=false;
										}
									
									}else{
										
										Toast toast = Toast.makeText(getApplicationContext(),
					            			     "您前后输入的密码不一致", Toast.LENGTH_LONG);
					            			   toast.setGravity(Gravity.CENTER, 0, 0);
					            			   toast.show();
									newpassagainImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0001_wrong));
									newpassagainImgBtn.setVisibility(View.VISIBLE);
									ucChangeN=false;
									backTomyaccountImgBtn.setVisibility(View.VISIBLE);
									enterBtn.setVisibility(View.GONE);
									}
								}
							}
						
					}
				});
			  newpassagainET.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					if(newpassagainET.length()!=0//&&newpassagainET.length()==newpassET.length()
							){
						if(hasWhiteSpace(newpassagainET.getText().toString())){
							if(newpassET.length()==newpassagainET.length()){
								Toast toast = Toast.makeText(getApplicationContext(),
			            			     "密码中不能有空格哦", Toast.LENGTH_LONG);
								toast.setGravity(Gravity.CENTER, 0, 0);
			            		toast.show();
							}
		            		newpassagainImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0001_wrong));
		            		newpassagainImgBtn.setVisibility(View.VISIBLE);
		            		ucChangeN=false;
							backTomyaccountImgBtn.setVisibility(View.VISIBLE);
							enterBtn.setVisibility(View.GONE);
						}else{
							if(newpassET.getText().toString().
								equals(newpassagainET.getText().toString())){
								newpassagainImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0000_right));
								newpassagainImgBtn.setVisibility(View.VISIBLE);
								//if(oldpassImgBtn.)
								//change
								ucChangeN=true;
								if(ucChangeO==true){
									backTomyaccountImgBtn.setVisibility(View.GONE);
									enterBtn.setVisibility(View.VISIBLE);
	//								ucChangeO=false;
	//								ucChangeN=false;
								}
							}else{
								if(newpassET.length()==newpassagainET.length()){
									Toast toast = Toast.makeText(getApplicationContext(),
			            			     "您前后输入的密码不一致", Toast.LENGTH_LONG);
			            			   toast.setGravity(Gravity.CENTER, 0, 0);
			            			   toast.show();
								}
								newpassagainImgBtn.setImageDrawable(getResources().getDrawable(R.drawable._0001_wrong));
								newpassagainImgBtn.setVisibility(View.VISIBLE);
								ucChangeN=false;
								backTomyaccountImgBtn.setVisibility(View.VISIBLE);
								enterBtn.setVisibility(View.GONE);
							}
						}
					}
				}
			});
			  passResult=(RelativeLayout)myaccountView.findViewById(R.id.passresult);
			  resultTv=(TextView)myaccountView.findViewById(R.id.resultTv);
			  daban=(TextView)myaccountView.findViewById(R.id.daban);
			  daban.setOnClickListener(ConfigActivity.this);
			  dabanImgBtn= (ImageButton) myaccountView.findViewById(R.id.dabanImgBtn );
			  dabanImgBtn.setOnClickListener(ConfigActivity.this);
			  //tar
			  targetpopview=(ViewGroup) findViewById(R.id.taraccount);
			  tarAccTv = (TextView) targetpopview.findViewById(R.id.targetaccountText);
			  if(SelfInfo.getInstance().isMatch())
					  tarAccTv.setText(SelfInfo.getInstance().getTarget());
			  else
				  tarAccTv.setOnClickListener(this);
			  tarAccEt = (EditText) targetpopview.findViewById(R.id.targetaccET);
			  tarAccEt.setOnFocusChangeListener(new OnFocusChangeListener() {
				  
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if(tarAccEt.hasFocus()){
							scrollHandler.postDelayed(new Runnable() {  
	                  
				   	            @Override  
				   	            public void run() {  
				   	                //将ScrollView滚动到底  
				   	            	scrollpop.fullScroll(ScrollView.FOCUS_DOWN);//防止遮挡  
				   	            }  
				   	        }, 200);
							
						}
					}
			  });
			  
			  targetaccbtn = (RelativeLayout) targetpopview.findViewById(R.id.targetaccbtn);
			  tarAccImgBtn = (ImageButton) targetpopview.findViewById(R.id.targetaccImgBtn);
			  acctransImgBtn= (ImageButton) targetpopview.findViewById(R.id.acctransImgBtn);
			  acctransImgBtn .setOnClickListener(ConfigActivity.this);
			  
			  usernameLayout= (RelativeLayout) targetpopview.findViewById(R.id.usernameLayout);
			  tarBigNameTv = (TextView) targetpopview.findViewById(R.id.targetusernameText);
			  if(//SelfInfo.getInstance().getSmallName()!=null||
					  SelfInfo.getInstance().getSmallName()!=String.format("%c", Protocol.DEFAULT))
				  tarBigNameTv.setText(SelfInfo.getInstance().getSmallName());
			  tarBigNameTv.setOnClickListener(this);
			  tarBigNameEt =(EditText) targetpopview.findViewById(R.id.targetusernameET);
			  tarBigNameImgBtn = (ImageButton) targetpopview.findViewById(R.id.targetusernameImgBtn);
			  tartransImgBtn = (ImageButton) targetpopview.findViewById(R.id.targettransImgBtn);
			  tartransImgBtn .setOnClickListener(ConfigActivity.this);
//			  testline = (View) targetpopview.findViewById(R.id.testline);
			  hintinvite = (TextView)  targetpopview.findViewById(R.id.hintinvite);
			  beTogetherImgBtn= (ImageButton) targetpopview.findViewById(R.id.tarbetogImgBtn);
			  beTogetherImgBtn.setOnClickListener(ConfigActivity.this);
			  
//			  statusTv = (TextView) targetpopview.findViewById(R.id.watingResponse);
			  //smallhouse
			  smallhouseView=(ViewGroup) findViewById(R.id.smallhouse);
			  cosmetics=(ImageView)smallhouseView.findViewById(R.id.cosmetics);
			  cosmetics.setOnClickListener(ConfigActivity.this);
			  sunny=(ImageView)smallhouseView.findViewById(R.id.sunny);
			  sunny.setOnClickListener(ConfigActivity.this);
			  coffee=(ImageView)smallhouseView.findViewById(R.id.coffee);
			  coffee.setOnClickListener(ConfigActivity.this);
//			  initHousestyle();
			//software
			  mysoftwareView=(ViewGroup) findViewById(R.id.software);
			  basicSetting=(TextView)mysoftwareView.findViewById(R.id.basic_setting);
			  basicSetting.setOnClickListener(ConfigActivity.this);
			  about=(TextView)mysoftwareView.findViewById(R.id.about);
			  about.setOnClickListener(ConfigActivity.this);
			  help=(TextView)mysoftwareView.findViewById(R.id.help);
			  help.setOnClickListener(ConfigActivity.this);
			  feedback=(TextView)mysoftwareView.findViewById(R.id.feedback);
			  feedback.setOnClickListener(ConfigActivity.this);
			  rank=(TextView)mysoftwareView.findViewById(R.id.rank);
			  rank.setOnClickListener(ConfigActivity.this);
			  exit=(TextView)mysoftwareView.findViewById(R.id.exit);
			  exit.setOnClickListener(ConfigActivity.this);
			  
			  if(SelfInfo.getInstance().isMatch()){
				  targetaccbtn.setVisibility(View.GONE);
				  tarAccImgBtn.setVisibility(View.GONE);
				  acctransImgBtn.setVisibility(View.GONE);
//				  testline.setVisibility(View.GONE);
				  beTogetherImgBtn.setVisibility(View.GONE);
				  usernameLayout.setVisibility(View.VISIBLE);
				  hintinvite.setVisibility(View.GONE);
			  }else{
				  usernameLayout.setVisibility(View.GONE);
				  hintinvite.setVisibility(View.VISIBLE);
			  }
//		   	goodfriendpopview=(ViewGroup) getLayoutInflater().inflate(R.layout.goodfriendpopview,null);
		    
			
			menuList = (ListView)findViewById(R.id.menuList);	
			 menuList.getLayoutParams().width=(4*ScreenWIDTH)/16 ;
			TextView textView = new TextView(this);
			textView.setHeight(dip2px(ConfigActivity.this,50));//适配
			textView.setText("菜单 ");
			textView.setTextSize(25);
			textView.setTextColor(Color.parseColor("#ffffff"));//pink 
			textView.setBackgroundColor(Color.parseColor("#f96282"));//#ffc0cb
			textView.setGravity(Gravity.CENTER);
			menuList.addHeaderView(textView);
			
			this.menuListAdapter = new MenuListAdapter(this);
			menuList.setAdapter(menuListAdapter);
			////////////////////////////////////////////////////////////////////////
		
			//为设置界面设置监听器
			menuList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View v, int pos,
						long id) {
				if(pos != 0&&pos != 1)
					 ConfigActivity.this.initSubView(v,pos);
				
					switch(pos){
					case 1:
						
						onClickChooseHeadImageBtn();
						break;
					case 2 :
						if(ConfigActivity.this.myaccountView.isShown()){
							
						}else{
							ConfigActivity.this.myaccountView.setVisibility(View.VISIBLE);
							ConfigActivity.this.targetpopview.setVisibility(View.GONE);
//							ConfigActivity.this.goodfriendpopview.setVisibility(View.GONE);
							ConfigActivity.this.smallhouseView.setVisibility(View.GONE);
							ConfigActivity.this.mysoftwareView.setVisibility(View.GONE);
						}
										
						break;
					case 3 :
                       if(ConfigActivity.this.targetpopview.isShown()){
							
						}else{
							ConfigActivity.this.myaccountView.setVisibility(View.GONE);
							ConfigActivity.this.targetpopview.setVisibility(View.VISIBLE);
//							ConfigActivity.this.goodfriendpopview.setVisibility(View.GONE);
							ConfigActivity.this.smallhouseView.setVisibility(View.GONE);
							ConfigActivity.this.mysoftwareView.setVisibility(View.GONE);
						}
					
						break;
					case 4 :
						  /*if(ConfigActivity.this.goodfriendpopview.isShown()){
								
							}else{
								ConfigActivity.this.myaccountView.setVisibility(View.GONE);
								ConfigActivity.this.targetpopview.setVisibility(View.GONE);
								ConfigActivity.this.goodfriendpopview.setVisibility(View.VISIBLE);
							}*/
						ConfigActivity.this.myaccountView.setVisibility(View.GONE);
						ConfigActivity.this.targetpopview.setVisibility(View.GONE);
//						ConfigActivity.this.goodfriendpopview.setVisibility(View.GONE);
						ConfigActivity.this.smallhouseView.setVisibility(View.VISIBLE);
						ConfigActivity.this.mysoftwareView.setVisibility(View.GONE);
						initHousestyle();
						break;	
						
					case 5 :
						  
						ConfigActivity.this.myaccountView.setVisibility(View.GONE);
						ConfigActivity.this.targetpopview.setVisibility(View.GONE);
//						ConfigActivity.this.goodfriendpopview.setVisibility(View.GONE);
						ConfigActivity.this.smallhouseView.setVisibility(View.GONE);
						ConfigActivity.this.mysoftwareView.setVisibility(View.VISIBLE);
						break;
						
						default:
							break;
						
					}
				}
				
			});
			scrollpop = (ScrollView) findViewById(R.id.scrollpop);
			scrollpop.setOnTouchListener(new OnTouchListener() {
				
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					int i = (int) event.getX();
					int j = (int) event.getY();
					int x = 10000;
					  if(event.getAction() == MotionEvent.ACTION_DOWN){
							/*		手指按下时的x坐标*/
							lastMotionX = (int)event.getRawX();
//							Log.v("demo","down 001 " + lastMotionX);
						}
					
					int[] target = new int[2];
					if(event.getAction() == MotionEvent.ACTION_UP){
						View v2 = ConfigActivity.this.getCurrentFocus();
						//文本框有焦点 点击周围的屏幕
//						if(myausernameEt.getVisibility()==View.VISIBLE){
//								mytransImgBtn.performClick();//是否一并处理
//						}else if(tarAccEt.getVisibility()==View.VISIBLE){
//								acctransImgBtn.performClick();
//						}else if(tarBigNameEt.getVisibility()==View.VISIBLE){
//								tartransImgBtn.performClick();
						if (v2 != null && (v2 instanceof EditText)) {
							v2.clearFocus();
						}else{//没有文本框得到焦点时，点击取消子菜单
							if(!ConfigActivity.this.myaccountView.isShown()&&!ConfigActivity.this.targetpopview.isShown()&&
									!ConfigActivity.this.smallhouseView.isShown()&&!ConfigActivity.this.mysoftwareView.isShown()){
				                
									 x= (int)event.getRawX();
//										Log.v("demo","up  " +x);
				//						  if(x <  lastMotionX - 15  ){
									    	                    
				//					      }
									if( x > (4*ScreenWIDTH)/16){
										
										Intent intent = new Intent();
									    intent.setClass(ConfigActivity.this,MainActivity.class);
									    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
									    startActivity(intent);
							           
							           //android.R.anim.,android.R.anim.slide_out_right
//							           ConfigActivity.this.finish();
							           overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
									}
								
						
							}else{
								if(ConfigActivity.this.myaccountView.isShown()){
									ConfigActivity.this.myaccountView.getLocationOnScreen(target);
									int curX = target[0] ;
									int curY = target[1];
									int curH = ConfigActivity.this.myaccountView.getHeight();
									int curW = ConfigActivity.this.myaccountView.getWidth();
									if((i < curX) || (i > curX + curW) || (j < curY) || (j > (curY + curH))){
										ConfigActivity.this.myaccountView.setVisibility(View.GONE);
										}
								
								}else if(ConfigActivity.this.targetpopview.isShown()){
									ConfigActivity.this.targetpopview.getLocationOnScreen(target);
									int curX = target[0] ;
									int curY = target[1];
									int curH = ConfigActivity.this.targetpopview.getHeight();
									int curW = ConfigActivity.this.targetpopview.getWidth();
									if((i < curX) || (i > curX + curW) || (j < curY) || (j > (curY + curH))){
										ConfigActivity.this.targetpopview.setVisibility(View.GONE);
										}
					//			}else if(this.goodfriendpopview.isShown()){
					//				this.goodfriendpopview.getLocationOnScreen(target);
					//				int curX = target[0] ;
					//				int curY = target[1];
					//				int curH = this.goodfriendpopview.getHeight();
					//				int curW = this.goodfriendpopview.getWidth();
					//				if((i < curX) || (i > curX + curW) || (j < curY) || (j > (curY + curH))){
					//					this.goodfriendpopview.setVisibility(View.GONE);
					//					}
								}else if(ConfigActivity.this.smallhouseView.isShown()){
									ConfigActivity.this.smallhouseView.getLocationOnScreen(target);
									int curX = target[0] ;
									int curY = target[1];
									int curH = ConfigActivity.this.smallhouseView.getHeight();
									int curW = ConfigActivity.this.smallhouseView.getWidth();
									if((i < curX) || (i > curX + curW) || (j < curY) || (j > (curY + curH))){
										ConfigActivity.this.smallhouseView.setVisibility(View.GONE);
										}
								}else if(ConfigActivity.this.mysoftwareView.isShown()){
									ConfigActivity.this.mysoftwareView.getLocationOnScreen(target);
									int curX = target[0] ;
									int curY = target[1];
									int curH = ConfigActivity.this.mysoftwareView.getHeight();
									int curW = ConfigActivity.this.mysoftwareView.getWidth();
									if((i < curX) || (i > curX + curW) || (j < curY) || (j > (curY + curH))){
										ConfigActivity.this.mysoftwareView.setVisibility(View.GONE);
										}
								}
							}
						}
					}
			 	return true;
				}
			});
			childpop = (RelativeLayout) findViewById(R.id.childpop);
			// 启动activity时不自动弹出软键盘
       		getWindow().setSoftInputMode(
       				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	 }

	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }
    
	 /*
	  * 设置的子菜单
	  */
	private void initSubView(View v,int pos){
		 if(this.isfirstInitmy&&pos==2){
			 	int[] location = new int[2];
			    v.getLocationOnScreen(location);
			    int y = location[1];
			 // me 
//			        	RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams  
//			            ( (3*ScreenWIDTH)/4, ViewGroup.LayoutParams.WRAP_CONTENT);    
//						lp1.setMargins(v.getWidth(),y-myaccountView.getHeight()/2, 0, 0);
//						
//						mRl.addView( myaccountView, lp1 ); 

			    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)myaccountView.getLayoutParams(); 
				    layoutParams.width = (3*ScreenWIDTH)/4; 
				    layoutParams.setMargins(0,y-myaccountView.getHeight()/2+12, 0, 0);//+是微调
				    myaccountView.setLayoutParams(layoutParams);
//			    myaccountView.layout(v.getWidth(), y-myaccountView.getHeight()/2,0 , 0);
//			    myaccountView.setX(v.getWidth()); //2.3.4(10)不支持set(11)
//			    myaccountView.setY(y-myaccountView.getHeight()/2);
			    this.isfirstInitmy = false;
			    
		 }else if(this.isfirstInittar&&pos==3){
	         //ta
			 	
//						RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams  
//					            ( (13*ScreenWIDTH)/16, ViewGroup.LayoutParams.WRAP_CONTENT); 
//	                 if(SelfInfo.getInstance().isMatch())
//						lp2.setMargins(v.getWidth(),(int)(v.getHeight() *3.3) ,0,0);
//	                 else
//	                	 lp2.setMargins(v.getWidth(),(int)(v.getHeight() *3) ,0,0);
//	                 
//	                     mRl.addView(targetpopview, lp2 );    
			 	int[] location = new int[2];
			    v.getLocationOnScreen(location);
			    int y = location[1];
			    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)targetpopview.getLayoutParams(); 
			    layoutParams.width = (3*ScreenWIDTH)/4; 
			    layoutParams.setMargins(0,y-targetpopview.getHeight()/2+12, 0, 0);
			    targetpopview.setLayoutParams(layoutParams);
			    this.isfirstInittar = false;
			    
		 }else if(this.isfirstInithouse&&pos==4){
	         /*goodfriend
	                 
						RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams  
					            ( (13*ScreenWIDTH)/16, ViewGroup.LayoutParams.WRAP_CONTENT);   
						lp3.setMargins(v.getWidth(),(int)(v.getHeight() *(4-0.5)) ,0,0);
		               mRl.addView(goodfriendpopview, lp3 );    
		       		ConfigActivity.this.myaccountView.setVisibility(View.GONE);
					ConfigActivity.this.targetpopview.setVisibility(View.GONE);
					ConfigActivity.this.goodfriendpopview.setVisibility(View.GONE);*/
	                   //smallhouse
		                 
//							RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams  
//						            ( (13*ScreenWIDTH)/16, ViewGroup.LayoutParams.WRAP_CONTENT);    
//							lp3.setMargins(v.getWidth(),(int)(v.getHeight() *2.8) ,0,0);
//
//		                     mRl.addView(smallhouseView, lp3 );  
			 	int[] location = new int[2];
			    v.getLocationOnScreen(location);
			    int y = location[1];
			    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)smallhouseView.getLayoutParams(); 
			    layoutParams.width = (3*ScreenWIDTH)/4; 
			    layoutParams.setMargins(0,y-smallhouseView.getHeight()/2+12, 0, 0);
			    smallhouseView.setLayoutParams(layoutParams);
			    this.isfirstInithouse = false;
			    
		 }else if(this.isfirstInitsoft&&pos==5){
		                   //software
			                 
//								RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams  
//							            ( (13*ScreenWIDTH)/16, ViewGroup.LayoutParams.WRAP_CONTENT);    
//								lp4.setMargins(v.getWidth(),(int)(v.getHeight() *4.0) ,0,0);
//
//			                     mRl.addView(mysoftwareView, lp4 );
			 	int[] location = new int[2];
			    v.getLocationOnScreen(location);
			    int y = location[1];
			    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)mysoftwareView.getLayoutParams(); 
			    layoutParams.width = (3*ScreenWIDTH)/4; 
			    layoutParams.setMargins(0,y-mysoftwareView.getHeight()/2+12, 0, 0);
			    mysoftwareView.setLayoutParams(layoutParams);
			    this.isfirstInitsoft = false;
			 
		 }else{
			 return;
		 }
	 }
	 
//if has white space
	public boolean hasWhiteSpace(String nickName) {
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(nickName);
		boolean found = matcher.find();
		return found;
	}
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
//	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".png");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	private OnClickListener itemsOnClick = new OnClickListener() {
		public void onClick(View v) {
			mSelectPicPopup.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				// create Intent to take a picture and return control to the
				// calling application
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				GlobalApplication.getInstance().setFileUri(fileUri); // create a
																	// file to
																	// save the
																	// image
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the
																	// image
																	// file name
    
				// start the image capture Intent
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				break;
			case R.id.btn_pick_photo:
				Intent imgPickItent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(imgPickItent,
						PICK_IMAGE_ACTIVITY_REQUEST_CODE);
				break;
			default:
				break;
			}
		}

	};

	private OnClickListener exitOnClick = new OnClickListener() {
		public void onClick(View v) {
			mExitPopup.dismiss();
			switch (v.getId()) {
			case R.id.exitapp:
				
				// 解除百度云推送绑定
				PushManager.stopWork(getApplicationContext());
				GlobalApplication mGA = GlobalApplication.getInstance();
				//将pushid的值设为空 上传一个非法的pushid token 给服务器
				Utils.setPushUserId(mGA.getApplicationContext(), "");
				UserPacketHandler mReq = new UserPacketHandler();
				mReq.uploadLogoutTokenToServer();
				
				//退出前停心跳
			  	HeartPacketHandler.getInstance().stopHeart();
			  	
			  	
				
				mReq.Logout();
//				Database.getInstance(ConfigActivity.this).emptyUserTable(
//						SelfInfo.getInstance().getAccount());
				SelfInfo.getInstance().setDefault();
				SelfInfo.getInstance().setOnline(false);//下线
				
				mGA.setCommonDefault();
				mGA.setTargetDefault();
			
//				mGA.finishActivity(MainActivity.class);
				
			    SharedPreferences mSP  =mGA.getSharedPreferences(SelfInfo.getInstance().getAccount()
						, Activity.MODE_PRIVATE);
	
				SharedPreferences.Editor mEditor = mSP.edit();
				mEditor.putBoolean("firstLogin", true);
			    mEditor.putBoolean("isProtected", false);
			    mEditor.putBoolean("isGraph", false);
			    mEditor.putBoolean("isNum", false);
			    mEditor.putString("numPass", "");
			    mEditor.commit();
			    if(mGA.getLockPatternUtils().savedPatternExists())
			    	mGA.getLockPatternUtils().clearLock();//删除密码文件
	         
		     	SharedPreferences mSP1 = getSharedPreferences(Protocol.PREFERENCE_NAME,
						Activity.MODE_PRIVATE);
				SharedPreferences.Editor mEditor1 = mSP1.edit();
				mEditor1.putString("LastUser", SelfInfo.getInstance().getAccount());
				mEditor1.putString("Password", "");
//				mEditor1.putBoolean("isMainInitFinish", false);
				mEditor1.commit();
				
	//			Database.getInstance(getApplicationContext()).initDatabase("");
	
	//	         ConfigActivity.this.finish();
				Intent intent = new Intent();
			    intent.setClass(ConfigActivity.this,RegisterActivity.class);
			    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
			    startActivity(intent);
				mGA.finishOtherActivity();
				ConfigActivity.this.finish();
				mGA=null;
				break;
			
			default:
				break;
			}
		}

	};
	private Builder ibuilder;
	private String invitor;

	//XQDONE 供用户选择相片来源
		public void onClickChooseHeadImageBtn()
		{
			if (mSelectPicPopup == null) {
				mSelectPicPopup = new SelectPicPopup(ConfigActivity.this,
						itemsOnClick);
				mSelectPicPopup.showAtLocation(
						ConfigActivity.this.findViewById(R.id.configRL),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

			} else if (!(mSelectPicPopup.isShowing())) {
				mSelectPicPopup.showAtLocation(
						ConfigActivity.this.findViewById(R.id.configRL),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			} else {
				mSelectPicPopup.dismiss();
			}
		 
		}
		//退出登录选择
		public void onClickExitBtn()
		{
			if (mExitPopup == null) {
				mExitPopup = new ExitPopup(ConfigActivity.this,"",
						exitOnClick);
				mExitPopup.showAtLocation(
						ConfigActivity.this.findViewById(R.id.configRL),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

			} else if (!(mExitPopup.isShowing())) {
				mExitPopup.showAtLocation(
						ConfigActivity.this.findViewById(R.id.configRL),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			} else {
				mExitPopup.dismiss();
			}
		 
		}
		/**
		*initial house style
		*/
		private void initHousestyle(){


			if(GlobalApplication.getInstance().getHouseStyle().equals(Protocol.blueHouse)) {

					cosmetics.setBackgroundResource(R.drawable.setting_house_pink);
					sunny.setBackgroundResource(R.drawable.setting_house_blue_selected);
					coffee.setBackgroundResource(R.drawable.setting_house_coffee);
			}else if(GlobalApplication.getInstance().getHouseStyle().equals(Protocol.brownHouse)){
					cosmetics.setBackgroundResource(R.drawable.setting_house_pink);
					sunny.setBackgroundResource(R.drawable.setting_house_blue);
					coffee.setBackgroundResource(R.drawable.setting_house_coffee_selected);
			}else{
					cosmetics.setBackgroundResource(R.drawable.setting_house_pink_selected);//getResources().getDrawable(R.drawable.setting_house_pink_selected));
					sunny.setBackgroundResource(R.drawable.setting_house_blue);
					coffee.setBackgroundResource(R.drawable.setting_house_coffee);
			}

			
		}
		
		/*点击隐藏键盘
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

//	   if(v.equals(backTomainBtn)){
//		   Intent intent = new Intent();
//		    intent.setClass(ConfigActivity.this,MainActivity.class);
//           startActivity(intent);
//           overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);      
//                     
//	   }
	   
	   if(v.equals(mytransImgBtn)||v.equals(myAccTv)){
		 // right button
		   if(mytransImgBtn.getTag().equals("0")){
			   // to edit
//			   tarAccImgBtn.setBackground(this.getResources().getDrawable(R.drawable.setting_ta_done));
			   myausernameImgBtn.setBackgroundResource(R.drawable.setting_ta_done);
			   mytransImgBtn.setTag("1");
			   
			   myausernameEt.setText(myAccTv.getText().toString());
			   myAccTv.setVisibility(View.GONE);
			   myausernameEt.setVisibility(View.VISIBLE);
			   myausernameEt.setFocusable(true);
			   myausernameEt.setFocusableInTouchMode(true);
			   myausernameEt.requestFocus();

			   InputMethodManager inputManager =

		                    (InputMethodManager)myausernameEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		        inputManager.showSoftInput(myausernameEt, 0);//打开键盘
             }else if (mytransImgBtn.getTag().equals("1")){
            	 if(myausernameEt.length()!=0){
	            	 if(hasWhiteSpace(myausernameEt.getText().toString())){
	            		 myausernameEt.setText("");
	            		 Toast toast = Toast.makeText(getApplicationContext(),
	            			     "用户名不能包含有空格哦", Toast.LENGTH_LONG);
	            			   toast.setGravity(Gravity.CENTER, 0, 0);
	            			   toast.show();
	            	 }else{
		            	 myausernameImgBtn.setBackgroundResource(R.drawable.setting_ta_write);
		            	 mytransImgBtn.setTag("0");
					   
		            	 InputMethodManager imm = (InputMethodManager)myausernameEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		            	 imm.hideSoftInputFromWindow(myausernameEt.getWindowToken(), 0);//隐藏键盘
		            	 
		            	 GlobalApplication.getInstance().setModifyBigname(myausernameEt.getText().toString());
			  			   UserPacketHandler mReq = new UserPacketHandler();
			  			   mReq.ModifyBigName(myausernameEt.getText().toString());
		            	 myAccTv.setVisibility(View.VISIBLE);
					   myausernameEt.setVisibility(View.GONE);
	            	 }
            	 }
		   }
	   
	   }
	   
	   if(v.equals(myaeditpassImgBtn)||v.equals(passTitle)){
			 // TODO edit password 
		   myaccountLL.setVisibility(View.GONE);
		   mypassLL.setVisibility(View.VISIBLE);
		   texts.setVisibility(View.VISIBLE);
			
		   }
	   
	   if(v.equals(backTomyaccountImgBtn)){
		   ucChangeN=false;
		   ucChangeO=false;
		   oldpassEt.setText("");
		   newpassagainET.setText("");
		   newpassET.setText("");
		   oldpassImgBtn.setVisibility(View.GONE);
		   newpassImgBtn.setVisibility(View.GONE);
		   newpassagainImgBtn.setVisibility(View.GONE);
		   passResult.setVisibility(View.GONE);
		   myaccountLL.setVisibility(View.VISIBLE);
		   mypassLL.setVisibility(View.GONE);
	   }
	   if(v.equals(enterBtn)){
		   		GlobalApplication.getInstance().setModifyPwd(newpassagainET.getText().toString());
		   		UserPacketHandler mReq = new UserPacketHandler();
		   		mReq.ModifyPassword(newpassagainET.getText().toString());
			   oldpassEt.setText("");
			   newpassagainET.setText("");
			   newpassET.setText("");
			   oldpassImgBtn.setVisibility(View.GONE);
			   newpassImgBtn.setVisibility(View.GONE);
			   newpassagainImgBtn.setVisibility(View.GONE);
	   }
	   if(v.equals(dabanImgBtn)||v.equals(daban)){
		   Intent regIntent =new Intent(ConfigActivity.this,SetAppearanceActivity.class);
		   regIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
//	        if(receiver != null)
//			{  
//				this.unregisterReceiver(receiver);
//				receiver = null;
//			}
	        Bundle regBundle = new Bundle();
	        regBundle.putString("sex", SelfInfo.getInstance().getSex());
	        regBundle.putString("nickname", SelfInfo.getInstance().getNickName());
	        regBundle.putString("account",SelfInfo.getInstance().getAccount());
	        regBundle.putString("pwd",SelfInfo.getInstance().getPwd());
	        regBundle.putString("apply","applytochange");
	        regIntent.putExtras(regBundle);
	        startActivity(regIntent);
//	        this.finish();
	   }
	   //tar
	  
	   if(v.equals(acctransImgBtn)||v.equals(tarAccTv)){
		   if(acctransImgBtn.getTag().equals("0")){
			   // to edit
			   
//			   tarAccImgBtn.setBackground(this.getResources().getDrawable(R.drawable.setting_ta_done));
			   tarAccImgBtn.setBackgroundResource(R.drawable.setting_ta_done);
			   acctransImgBtn.setTag("1");
			   
			   if(!tarAccTv.getText().toString().equals("暂无"))
				   tarAccEt.setText(tarAccTv.getText().toString());
			   else
				   tarAccEt.setText("");
			   hintinvite.setVisibility(View.VISIBLE);
			   hintinvite.setText("请输入对方帐号");
			   tarAccTv.setVisibility(View.GONE);
			   tarAccEt.setVisibility(View.VISIBLE);
			   tarAccEt.setFocusable(true);
			   tarAccEt.setFocusableInTouchMode(true);
			   tarAccEt.requestFocus();

			   InputMethodManager inputManager =

		                    (InputMethodManager)tarAccEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		        inputManager.showSoftInput(tarAccEt, 0);
		        scrollHandler.postDelayed(new Runnable() {  
	                  
	   	            @Override  
	   	            public void run() {  
	   	                //将ScrollView滚动到底  
	   	            	scrollpop.fullScroll(ScrollView.FOCUS_DOWN);//防止遮挡  
	   	            }  
	   	        }, 200);
             }else if (acctransImgBtn.getTag().equals("1")){
            	 
				   tarAccImgBtn.setBackgroundResource(R.drawable.setting_ta_write);
				   acctransImgBtn.setTag("0");
				   
				   tarAccTv.setText(tarAccEt.getText().toString().trim());
				   //Glo.setNickName(tarAccEt.getText().toString());
				   
				   tarAccTv.setVisibility(View.VISIBLE);
				   tarAccEt.setVisibility(View.GONE);
				   InputMethodManager imm = (InputMethodManager)tarAccEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	            	 imm.hideSoftInputFromWindow(tarAccEt.getWindowToken(), 0);
			
		   }
	   }
      if(v.equals(tartransImgBtn)||v.equals(tarBigNameTv)){
    	  if(tartransImgBtn.getTag().equals("0")){
			   // to edit
    		  
    		  
//			   tarBigNameImgBtn.setBackground(this.getResources().getDrawable(R.drawable.setting_ta_done));
			   tarBigNameImgBtn.setBackgroundResource(R.drawable.setting_ta_done);
			   tartransImgBtn.setTag("1");
			   
			   tarBigNameEt.setText(tarBigNameTv.getText().toString());
			   tarBigNameTv.setVisibility(View.GONE);
			   tarBigNameEt.setVisibility(View.VISIBLE);
			   tarBigNameEt.setFocusable(true);
			   tarBigNameEt.setFocusableInTouchMode(true);
			   tarBigNameEt.requestFocus();

			   InputMethodManager inputManager =

		                    (InputMethodManager)tarBigNameEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		        inputManager.showSoftInput(tarBigNameEt, 0);
		        
		        scrollHandler.postDelayed(new Runnable() {  
	                  
    	            @Override  
    	            public void run() {  
    	                //将ScrollView滚动到底  
    	            	scrollpop.fullScroll(ScrollView.FOCUS_DOWN);//防止遮挡  
    	            }  
    	        }, 200);
            }else if (tartransImgBtn.getTag().equals("1")){
            	if(tarBigNameEt.length()!=0){
	            	if(hasWhiteSpace(tarBigNameEt.getText().toString())){
	            		tarBigNameEt.setText("");
	           		 Toast toast = Toast.makeText(getApplicationContext(),
	           			     "昵称不能包含有空格哦", Toast.LENGTH_LONG);
	           			   toast.setGravity(Gravity.CENTER, 0, 0);
	           			   toast.show();
	           	 	}else{
	           	 		tarBigNameImgBtn.setBackgroundResource(R.drawable.setting_ta_write);
	           	 	tartransImgBtn.setTag("0");
				   
				   
				   GlobalApplication.getInstance().setModifySmallMame(tarBigNameEt.getText().toString());
				   UserPacketHandler mReq = new UserPacketHandler();
				   mReq.ModifySmallName(tarBigNameEt.getText().toString().trim());
				   tarBigNameTv.setVisibility(View.VISIBLE);
				   tarBigNameEt.setVisibility(View.GONE);
				   InputMethodManager imm = (InputMethodManager)tarBigNameEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	            	 imm.hideSoftInputFromWindow(tarBigNameEt.getWindowToken(), 0);
	           	 }
            	}
		   }
	   }
      
      if(v.equals(beTogetherImgBtn)){
    	  if(beTogetherImgBtn.getTag().equals("0")){
    		  tarAccTv.setText(tarAccEt.getText().toString());
    		  tarAccTv.setVisibility(View.VISIBLE);
			   tarAccEt.setVisibility(View.GONE);
			   InputMethodManager imm = (InputMethodManager)tarAccEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
          	 imm.hideSoftInputFromWindow(tarAccEt.getWindowToken(), 0);
			   tarAccImgBtn.setBackgroundResource(R.drawable.setting_ta_write);
			   acctransImgBtn.setTag("0");
		   if(tarAccTv.getText().toString().length() > 40){
			   hintinvite.setText("账号最长为40位");
			   hintinvite.setVisibility(View.VISIBLE);
			   
			   if(acctransImgBtn.getTag().equals("0")){
				   // to edit
//				   tarAccImgBtn.setBackground(this.getResources().getDrawable(R.drawable.setting_ta_done));
				   tarAccImgBtn.setBackgroundResource(R.drawable.setting_ta_done);
				   acctransImgBtn.setTag("1");
//				   tarAccTv.setText("1@qq.com");
//				   tarAccEt.setText("1@qq.com");
				   tarAccTv.setVisibility(View.GONE);
				   tarAccEt.setVisibility(View.VISIBLE);
//				   tarAccEt.setFocusable(true);
				   
				   }
		   }else if(tarAccTv.getText().toString().length()==0||
				   tarAccTv.getText().toString().equals("暂无")){
			   beTogetherImgBtn.setTag("0");
			   tarAccTv.setText("");
			   hintinvite.setText("请输入对方帐号");
			   hintinvite.setVisibility(View.VISIBLE);
		   }else{
//			   tarAccTv.setText(tarAccEt.getText().toString().trim());
			  UserPacketHandler mReq = new UserPacketHandler();
			  mReq.AddMatch(tarAccEt.getText().toString().trim());
			  
			  beTogetherImgBtn.setTag("1");
			  beTogetherImgBtn.setBackgroundResource(R.drawable.setting_ta_cancel);
			  hintinvite.setText("等待对方回复。。。");
			  hintinvite.setVisibility(View.VISIBLE);
			   
		   }
	   }else{
		   //TODO cancel 
		   beTogetherImgBtn.setTag("0");
		   beTogetherImgBtn.setBackgroundResource(R.drawable.setting_ta_apply);
		   hintinvite.setVisibility(View.INVISIBLE);
	   }
    	  
      }
		//smallhouse
      if(v.equals(cosmetics)){
    	  SelfInfo.getInstance().setHousestyle(Protocol.pinkHouse);
    	  UserPacketHandler mReq = new UserPacketHandler();
    	  String housestyle = String.format("%c",(char)1);
			mReq.ModifyHouseStyle(housestyle);
      }
      if(v.equals(sunny)){
    	  SelfInfo.getInstance().setHousestyle(Protocol.blueHouse);
    	  UserPacketHandler mReq = new UserPacketHandler();
    	  String housestyle = String.format("%c",(char)2);
			mReq.ModifyHouseStyle(housestyle);
      }

      if(v.equals(coffee)){
    	  SelfInfo.getInstance().setHousestyle(Protocol.brownHouse);
    	  UserPacketHandler mReq = new UserPacketHandler();
    	  String housestyle = String.format("%c",(char)3);
			mReq.ModifyHouseStyle(housestyle);
      }
      //software
      if(v.equals(basicSetting)){
    	  Intent intent = new Intent();
		    intent.setClass(ConfigActivity.this,BasicSettingActivity.class);
		    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
         startActivity(intent);
      }
      if(v.equals(about)){
    	  Intent intent = new Intent();
		    intent.setClass(ConfigActivity.this,AboutusActivity.class);
		    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
         startActivity(intent);
      }
      if(v.equals(help)){
    	  Intent intent = new Intent();
		    intent.setClass(ConfigActivity.this,HelpActivity.class);
		    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
       startActivity(intent);
      }

      if(v.equals(feedback)){
	  
    	  FeedbackAgent agent = new FeedbackAgent(ConfigActivity.this);
//    	  agent.sync();
    	    agent.startFeedbackActivity();
      }
      if(v.equals(rank)){
    	  	Intent intent = new Intent();
    	  	intent.setClass(ConfigActivity.this,RankActivity.class);
		    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		    startActivity(intent);
       
      }
      if(v.equals(exit)){
    	  onClickExitBtn();
      }
	}
	
	private  void TrainsmitFail(){
		hintinvite.setText("用户名不存在或TA已经配对了哦～");
		hintinvite.setVisibility(View.VISIBLE);
	}
	
	private  void TrainsmitSucc(){
		hintinvite.setText("配对请求转发成功");
		hintinvite.setVisibility(View.VISIBLE);
	}
	
	private void acceptMatch(){
		             SelfInfo.getInstance().setMatch(true);
		             tarAccTv.setClickable(false);
					tarAccImgBtn.setVisibility(View.GONE);
					acctransImgBtn.setVisibility(View.GONE);
//					testline.setVisibility(View.GONE);
					hintinvite.setVisibility(View.GONE);
					beTogetherImgBtn.setVisibility(View.GONE);
					tarAccTv.setText(SelfInfo.getInstance().getTarget());//配对的帐号
//					  myDialog = new AlertDialog.Builder(ConfigActivity.this).create();  
//					   myDialog.show();  
//					  myDialog.getWindow().setContentView(R.layout.matchdialog);  
//					  myDialog.getWindow().setGravity(Gravity.CENTER);
//		              comfirmBtn = (ImageButton) myDialog.getWindow()  
//		                      .findViewById(R.id.matchfinishbtn);  
//		             
//		              whoTv = (TextView) myDialog.getWindow()  
//		                      .findViewById(R.id.matchContentTv);  
					usernameLayout.setVisibility(View.VISIBLE);
					tarBigNameTv.setVisibility(View.VISIBLE);
					tarBigNameEt.setVisibility(View.GONE);
					InputMethodManager imm = (InputMethodManager)tarBigNameEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	            	 imm.hideSoftInputFromWindow(tarBigNameEt.getWindowToken(), 0);
		              //TODO
		              String matchContent = "恭喜您和"+GlobalApplication.getInstance().getTiAcc()+"喜结良缘！";
//		              whoTv.setText(matchContent);
//		              acceptMatchHUD = ProgressHUD.showSuccOrError(ConfigActivity.this,matchContent, true);
//		              if(this.matchHandler == null){
//		  				this.matchHandler = new MymatchHandler();
//		  				
//		  			}
//		  			Message msg =matchHandler.obtainMessage();
//		  			matchHandler.sendMessageDelayed(msg, 3000);
		  			
//		              comfirmBtn.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							 myDialog.dismiss();
//							//TODO 引导页
//						}
//					});
		            ibuilder = new com.minius.ui.CustomDialog.Builder(ConfigActivity.this);
		      		ibuilder.setTitle(null);
		      		ibuilder.setMessage(matchContent);
		      		ibuilder.setPositiveButton("开始二人世界～", null);
//		      		ibuilder.setNegativeButton("取消", null);
		      		ibuilder.create().show();     
		            
			 
	}
	
		 private void refuseMatch()
		 {
//			View toastRoot = getLayoutInflater().inflate(R.layout.customtoast, null);
//			TextView message = (TextView) toastRoot.findViewById(R.id.contentTv);
//			message.setText("对不起，对方拒绝了您的邀请");
//			message.setTextSize((float) 20.0);
//
//			Toast toastStart = new Toast(this);
//			toastStart.setGravity(Gravity.CENTER, 0, 0);
//			toastStart.setDuration(Toast.LENGTH_SHORT);
//			toastStart.setView(toastRoot);
//			toastStart.show();
			
//			acceptMatchHUD = ProgressHUD.showSuccOrError(ConfigActivity.this,"对不起，对方拒绝了您的邀请", false);
//			
//			if(this.matchHandler == null){
//				this.matchHandler = new MymatchHandler();
//				
//			}
//			Message msg =matchHandler.obtainMessage();
//			matchHandler.sendMessageDelayed(msg, 3000);
			 ibuilder = new com.minius.ui.CustomDialog.Builder(ConfigActivity.this);
				ibuilder.setTitle(null);
				ibuilder.setMessage("对不起，对方拒绝了您的邀请");
				ibuilder.setPositiveButton("再接再厉～", null);
//				ibuilder.setNegativeButton("取消", null);
				ibuilder.create().show();
//			tarAccEt.setText(tarAccTv.getText().toString().trim());
			hintinvite.setText("请向您的另一半发出邀请");
			hintinvite.setVisibility(View.VISIBLE);
			beTogetherImgBtn.setBackgroundResource(R.drawable.setting_ta_apply);
			beTogetherImgBtn.setTag("0");
			
		 }
		 
		 private  class MymatchHandler extends Handler {
				

				@Override
				public void handleMessage(Message msg) {
			
					if (acceptMatchHUD != null)
						acceptMatchHUD.dismiss();
					
				
			}
		 }
		 
		 /**
		  * 请求配对               0x5c协议+服务器类型+用户包+请求配对+包体长度+请求者账号+’\0’
		  * 配对请求
		  * @param str
		  */
		 private void processMatchRequest(String str){
			//ReceiveMatchRequest
			     String[] arr;
			
				  arr = str.substring(Protocol.HEAD_LEN).split(" ");
				  invitor = arr[0];
				  
//				  askMachDialog  = new AlertDialog.Builder(ConfigActivity.this).create();  
//				  askMachDialog.getWindow().setGravity(Gravity.CENTER); 
//				  askMachDialog.show();  
//				  askMachDialog.getWindow().setContentView(R.layout.customd;  
			
				  ibuilder = new com.minius.ui.CustomDialog.Builder(ConfigActivity.this);
					ibuilder.setTitle(null);
					ibuilder.setMessage("你是否愿意和"+invitor+"在一起？");
					ibuilder.setPositiveButton("同意", matchAsk);
					ibuilder.setNegativeButton("拒绝", matchAsk);
					ibuilder.create().show();
				/*
	              yesBtn = (ImageButton) askMachDialog.getWindow()  
	                      .findViewById(R.id.yesbtn);  
	              noBtn =  (ImageButton) askMachDialog.getWindow()  
	                      .findViewById(R.id.nobtn);  
	              whoTv = (TextView) askMachDialog.getWindow()  
	                      .findViewById(R.id.whoTv);  
	              //TODO
	              whoTv.setText(invitor);
	              
	              
	              yesBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
					UserPacketHandler mReq = new UserPacketHandler();
					mReq.clientAcceptMatch( invitor);
					askMachDialog.dismiss();
					}
				});
	              
	              noBtn.setOnClickListener(new OnClickListener() {
	  				
	  				@Override
	  				public void onClick(View v) {
	  					UserPacketHandler mReq = new UserPacketHandler();
						mReq.clientRejectMatch(invitor);
	  					askMachDialog.dismiss();
	  					
	  					
	  				}
	  			});*/
	              }
		 private View.OnClickListener matchAsk = new View.OnClickListener() {
			 UserPacketHandler mReq = new UserPacketHandler();
				public void onClick(View v) {
					switch (v.getId()) {
					
					case R.id.confirm_btn:
//						UserPacketHandler mReq = new UserPacketHandler();
						mReq.clientAcceptMatch( invitor);
//						askMachDialog.dismiss();
						if(ibuilder.getDialog()!=null)
							ibuilder.getDialog().dismiss();
						break;
					case R.id.cancel_btn:
						
						mReq.clientRejectMatch(invitor);
						if(ibuilder.getDialog()!=null)
							ibuilder.getDialog().dismiss();
//	  					askMachDialog.dismiss();
	  				break;
					default:
						break;
					}
				}

			};
	
	//XQDONE 未配对的情况下弹出提示框
		 /*
		 private void askForMatch()
		 {
			 View toastRoot = getLayoutInflater().inflate(R.layout.toast, null);
			 TextView message = (TextView) toastRoot.findViewById(R.id.message);
			 message.setText("请先向您的另一半发出邀请哦～");
			 message.setTextSize((float) 20.0);

				Toast toastStart = new Toast(this);
				toastStart.setGravity(Gravity.CENTER, 0, 0);
				toastStart.setDuration(Toast.LENGTH_SHORT);
				toastStart.setView(toastRoot);
				toastStart.show();
				
		 }*/
	
	 public void processResponse(String str){
		 char operatorCode = 0;
			try {
				operatorCode = (char) (str.getBytes("UTF-8"))[3];
			} catch (UnsupportedEncodingException e) {
			
				e.printStackTrace();
			}
			switch(operatorCode){
			case Protocol.MODIFY_HEAD_PHOTO_SUCC:
					Database db = Database.getInstance(GlobalApplication.getInstance().getApplicationContext());
					UserTable mUt = db.getSelfInfo();
					// path ,isboy,isme
//					HandleHeadPhoto2Task t = new HandleHeadPhoto2Task();
//					String isBoy = "1";
//					String isMe = "1";
				
				
					String path = mUt.getHeadphoto();
				
					Bitmap temp=null;
					if(path.equals(Protocol.DEFAULT+ "")){
						path=Environment.getExternalStorageDirectory()  
		                 + "/LoverHouse"+"/HeadPhoto"+"/"+mUt.getAccount() + ".png";
						db.addSelfInfo(SelfInfo.getInstance().getAccount(), 
								SelfInfo.getInstance().getPwd(), path);
						
					}
					temp= AppManagerUtil.getDiskBitmap(path);
					CommonBitmap.getInstance().addCacheBitmap(temp, CommonBitmap.MYHEADPHO);
					CommonBitmap.getInstance().setMyHeadPhotoInit(true);
//					t.execute(path, isBoy,isMe);
					menuListAdapter.notifyDataSetChanged();
				break;
			case Protocol.MODIFY_HEAD_PHOTO_FAIL:
				break;
			case Protocol.MATCH_REQ_TRANSMIT_FAIL: //	配对 请求转发成功or fail
				TrainsmitFail();
				break;
			case Protocol.MATCH_REQ_TRANSMIT_SUCC:
				TrainsmitSucc();
				break;
			case Protocol.ACCEPT_MATCH:
				if(!GlobalApplication.getInstance().isMainVisible())
					acceptMatch();
				break;
			case Protocol.REJECT_MATCH:
				if(!GlobalApplication.getInstance().isMainVisible())
					refuseMatch();
				break;	
			case Protocol.ASK_FOR_MATCH:
				if(!GlobalApplication.getInstance().isMainVisible())
				processMatchRequest(str);
				break;
			case Protocol.MODIFY_BIG_NAME_SUCC:	
			
			case Protocol.MODIFY_BIG_NAME_FAIL:	
				myAccTv.setText(SelfInfo.getInstance().getNickName());
				break;	
			case Protocol.MODIFY_SMALL_NAME_SUCC:
				
			case Protocol.MODIFY_SMALL_NAME_FAIL:
				tarBigNameTv.setText(SelfInfo.getInstance().getSmallName());
				break;
			case Protocol.MODIFY_PASSWORD_SUCC:
				ucChangeN=false;
				ucChangeO=false;
				texts.setVisibility(View.GONE);
				
				passResult.setVisibility(View.VISIBLE);
				resultTv.setText("修改成功");
				backTomyaccountImgBtn.setVisibility(View.VISIBLE);
				enterBtn.setVisibility(View.GONE);
				break;
			case Protocol.MODIFY_PASSWORD_FAIL:
				ucChangeN=false;
				ucChangeO=false;
				texts.setVisibility(View.GONE);
				
				passResult.setVisibility(View.VISIBLE);
				resultTv.setText("修改失败");
				backTomyaccountImgBtn.setVisibility(View.VISIBLE);
				enterBtn.setVisibility(View.GONE);
				break;
				
			case Protocol.MODIFY_HOUSE_STYLE_SUCC:
				this.finish();
				break;
			case Protocol.MODIFY_HOUSE_STYLE_FAIL:
//				ConfigActivity.this.smallhouseView.setVisibility(View.GONE);
//				this.finish();
				break;
			default:
				break;
			}
	 }
	 

//	  @SuppressWarnings("deprecation")
//	private void showPopupWindow(View parent,final PopupWindow pop) { 
//	    	
//		    if (pop.isShowing())
//                pop.dismiss();
////	    	pop.setOutsideTouchable(true);
//		    pop.setFocusable(true);
//	    	pop.setBackgroundDrawable(new BitmapDrawable());
//	    	 int[] location = new int[2];  
//	    	 parent.getLocationOnScreen(location);  
//	    
//	    	 pop.setTouchInterceptor(new OnTouchListener() {
//
//					@Override
//					public boolean onTouch(View v, MotionEvent event) {
//						  if (event.getY()<0 || event.getX() < 0){  //这里处理，当点击gridview以外区域的时候，菜单关闭
//						      Log.d("demo", "popupWindow@@@"
//			    	            );
//						 	 pop.setFocusable(false);
//		    	                if (pop.isShowing())
//		    	                    pop.dismiss();
//		    	            }else{
//		    	            	 pop.setFocusable(true);
//		    	            }
//						  pop.update();  
//		    	            Log.d("demo", "popupWindow::onTouch >>> view: "
//		    	                    + v + ", event: " + event);
//		    	            return false;
//					}
//	    	        });
//	    	 pop.showAtLocation( parent, Gravity.NO_GRAVITY, location[0]+parent.getWidth(), location[1]);  
//	    	
//	        pop.update();  
//	    	}
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			
			if(!ConfigActivity.this.myaccountView.isShown()&&!ConfigActivity.this.targetpopview.isShown()&&
					!ConfigActivity.this.smallhouseView.isShown()&&!ConfigActivity.this.mysoftwareView.isShown()){
                
				if(event.getAction() == MotionEvent.ACTION_UP){
					 	int x= (int)event.getRawX();

					 	if( x > (4*ScreenWIDTH)/16){
							
							Intent intent = new Intent();
						    intent.setClass(ConfigActivity.this,MainActivity.class);
						    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
						    startActivity(intent);
			           
				           //android.R.anim.,android.R.anim.slide_out_right
	//			           ConfigActivity.this.finish();
				           overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
					 	}
				}
		
			}
			return true;
		}
		 /**  
	     * 把Bitmap转Byte  
	     */    
	    public byte[] Bitmap2Bytes(Bitmap bm){    
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();    
	        bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);    
	        return baos.toByteArray();    
	    }
		public void sendHeadPhoto(){
			  int maxBlob = 8100;
//			  HeartPacketHandler mheart =  HeartPacketHandler.getInstance();
//			  String headPhotoPath = Database.getInstance(getApplicationContext()).readHeadPhotoFromTemp(email);
//			  Log.v("setapp", "head path in send  " + headPhotoPath);
			
			  Bitmap headBm =CommonBitmap.getInstance().getMyHeadBm();
		
			  byte[] headPoto = Bitmap2Bytes(headBm);
//			  Log.v("setapp", "send head photo 002" + headPoto.length);
			  int numPacket = (headPoto.length -1)/maxBlob + 1;
//			  Log.v("setapp", "send head photo numPacket" + numPacket );
			    UserPacketHandler req =new UserPacketHandler();
			    String imageLen = String.format("%d",headPoto.length + 1);
			    if(numPacket == 1){
			    	req.UploadFirstHeadPhotoData( imageLen,headPoto);
			    	
			    	req.UploadHeadPhotoDataFinish(headPoto.length);
//			    	mheart.sendHeartPacketToServer1();
			    	return;
			    }else{
			    	byte[] first = new byte[maxBlob];
			    	System.arraycopy(headPoto,0, first, 0, maxBlob);
			    	req.UploadFirstHeadPhotoData(imageLen, first);

			    	for(int i = 1; i< numPacket - 1 ; i ++){
			    		byte[] app = new byte[maxBlob];
			    		System.arraycopy(headPoto,i * maxBlob,app, 0, maxBlob);
			    		req.UploadAppendHeadPhotoData( app);

			    	}
			    	int remainLen = headPoto.length - (numPacket - 1) * maxBlob;
			    	byte[] last = new byte[remainLen];
			    	System.arraycopy(headPoto, (numPacket-1)* maxBlob, last, 0, remainLen);
			    	req.UploadAppendHeadPhotoData( last);
			    	req.UploadHeadPhotoDataFinish(headPoto.length);
//			    	mheart.sendHeartPacketToServer1();
			    	
			    }
//			    Log.v("setapp", "end   numPacket" + numPacket  );
			
		  }
		
		public void startSaveToSd(){
			  new Thread(){
				  public void run(){
					  
					  AppManagerUtil.writeToSD("/HeadPhoto", CommonBitmap.getInstance().getMyHeadBm(), 
							  SelfInfo.getInstance().getAccount());

				  }
			  }.start();
		  }
		
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    super.onActivityResult(requestCode, resultCode, data);
		    if (requestCode == R.layout.activity_config) {
		   	 if(resultCode == 0){
				 //直接返回
		   		 return;
				
			 }
//			 if(resultCode == 1){
				 //裁剪后的图片暂存在globalapplication
//				 this.headBitmap =  GlobalApplication.getInstance().getHeadPicBm();
//			       headPhoto.setImageBitmap(headBitmap);
//				 startSaveToSd();
//				 sendHeadPhoto();
				 
//			 }
		    	
		    }
		    if (requestCode ==PICK_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
		        Uri selectedImage = data.getData();
		        String[] filePathColumn = { MediaStore.Images.Media.DATA };

		        Cursor cursor = getContentResolver().query(selectedImage,
		                filePathColumn, null, null, null);
		        cursor.moveToFirst();

		        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		        String picturePath = cursor.getString(columnIndex);
		        cursor.close();

		        HandleHeadPhotoTask t = new HandleHeadPhotoTask();
				t.execute(picturePath);
//		        Intent  mIntent = new Intent(ConfigActivity.this,CropActivity.class);
//		        mIntent.putExtra("picPath", picturePath);
//		        mIntent.putExtra("flag", "1");
//		        startActivityForResult(mIntent,R.layout.activity_config);

		    }
		    
		    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
		        if (resultCode == RESULT_OK) {
		            // Image captured and saved to fileUri specified in the Intent
//		            Toast.makeText(this, "Image saved to:\n" +
//		            		fileUri.getPath(), Toast.LENGTH_LONG).show();
//		         
//		            Intent  mIntent = new Intent(ConfigActivity.this,CropActivity.class);
//			        mIntent.putExtra("picPath", fileUri.getPath());
//			        mIntent.putExtra("flag", "1");
//			        startActivityForResult(mIntent,R.layout.activity_config);

		        	HandleHeadPhotoTask t = new HandleHeadPhotoTask();
					t.execute(GlobalApplication.getInstance().getFileUri().getPath());
		            
		        } else if (resultCode == RESULT_CANCELED) {
		            // User cancelled the image capture
		        } else {
		            // Image capture failed, advise user
		        }
		    }
		}
		
		
		
		public class HandleHeadPhotoTask extends AsyncTask<String, String, Bitmap> {

			@Override
			protected void onPreExecute() {

			
                
				super.onPreExecute();
			}

			@Override
			protected Bitmap doInBackground(String... params) {
				if(params[0] == null ||params[0].equals("")) return null;
				HeadPhotoHanddler mHeadPhotoHandler = new HeadPhotoHanddler();
				GlobalApplication mIns = GlobalApplication.getInstance();
	 			
				Bitmap  endBm = null;
			
				
		 			if (SelfInfo.getInstance().getSex().equals("b")) {
		 				Bitmap frameBm = BitmapFactory.decodeResource(mIns.getApplicationContext().getResources(),
		 						R.drawable.boy_photoframe);
		 				endBm = (mHeadPhotoHandler.handleHeadPhoto(params[0], frameBm,
		 						mIns.getApplicationContext()));
		 				frameBm = null;
		 			} else {
		 				Bitmap frameBm = BitmapFactory.decodeResource(mIns.getApplicationContext().getResources(),
		 						R.drawable.girl_photoframe);
		 				endBm = (mHeadPhotoHandler.handleHeadPhoto(params[0], frameBm,
		 						mIns.getApplicationContext()));
		 			
		 				frameBm = null;
		 			}
		 			
			return endBm;
			}

			@Override
			protected void onProgressUpdate(String... values) {

				super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				CommonBitmap.getInstance().addCacheBitmap(result, CommonBitmap.MYHEADPHO);
				CommonBitmap.getInstance().setMyHeadPhotoInit(true);
				startSaveToSd();
				 sendHeadPhoto();
				super.onPostExecute(result);
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
						//读到用户数据包 数据,发送消息,让handler更新界面
						
						String data = intent.getStringExtra(Protocol.EXTRA_DATA);
	                  
						Bundle bdata = new Bundle();
						bdata.putString("data", data);
						Message msg = mHandler.obtainMessage();				
						msg.setData(bdata);
                        
						msg.what =Protocol.HANDLE_RESPON;
						mHandler.sendMessage(msg);
					   
					}
				
				}	//onReceive
			} 
			
			@TargetApi(16)
			private static class MyHandler extends Handler
			{
				WeakReference<ConfigActivity> mActivity;  
				 MyHandler(ConfigActivity activity) {  
		             mActivity = new WeakReference<ConfigActivity>(activity);  
		     }  
				@Override
				public void handleMessage(Message msg)
				{
					ConfigActivity theActivity = mActivity.get();  
					
					switch (msg.what)
					{
						
						case Protocol.HANDLE_RESPON:
							String mData = msg.getData().getString("data");
							theActivity.processResponse(mData);
							break;
//						case HANDLEBACKGROUD:
//							theActivity.dealWithbackgroundUI((Bitmap)msg.obj);
//							
//							break;
					
						default:
							break;
					}
				}
			}
	  
	  
}

