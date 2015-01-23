package com.minus.lovershouse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.minius.common.CommonBitmap;
import com.minius.ui.HeadPhotoHanddler;
import com.minus.cropimage.CropActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.MainActivity.LoginTask;
import com.minus.lovershouse.setting.PrivacyActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.RoundedImageView;
import com.minus.lovershouse.util.SelectPicPopup;
import com.minus.sql_interface.Database;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.HeartPacketHandler;
import com.minus.xsocket.handler.UserPacketHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;


public class RegisterActivity extends Activity implements OnClickListener {
	
	private LinearLayout step1;
	private LinearLayout step2; 


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_register);
	   	if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)// 强制竖屏
		{   
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
		} 
	   	GlobalApplication.getInstance().addActivity(this);
        initView();
        initData();
     // 启动activity时不自动弹出软键盘
   		getWindow().setSoftInputMode(
   				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        PushAgent.getInstance(this).onAppStart();
    }
	
	@Override
	protected void onResume() {
		super.onResume();

		GlobalApplication.getInstance().setRegVisible(true);
		if(receiver == null)
			 receiver = new MyReceiver();
		IntentFilter filter=new IntentFilter();   
		filter.addAction(Protocol.ACTION_USERPACKET);
	    this.registerReceiver(receiver,filter); 
		
    	
		MobclickAgent.onResume(this);
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		GlobalApplication.getInstance().setRegVisible(false);
		MobclickAgent.onPause(this);
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(receiver != null)
		{  
			this.unregisterReceiver(receiver);
			receiver = null;
		}
		destoryBimap();
		//结束Activity&从栈中移除该Activity
		GlobalApplication.getInstance().finishActivity(this);
		super.onDestroy();
		
	}


	private void initView(){
		step1=(LinearLayout) findViewById(R.id.step1);
		stateTv = (TextView) findViewById(R.id.stateTv);
		stateTv.setVisibility(View.INVISIBLE);
		nickNameInd=(ImageView) findViewById(R.id.nicknameInd);
		nickNameInd.setVisibility(View.GONE);
		bigNameEt = (EditText) findViewById(R.id.nicknameEt);
		bigNameEt.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				nickNameInd.setVisibility(View.GONE);
				stateTv.setVisibility(View.INVISIBLE);
			} 
			  
        });
		bigNameEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			  
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(bigNameEt.hasFocus()){
					nickNameInd.setVisibility(View.GONE);
					stateTv.setVisibility(View.INVISIBLE);
				}else{
					if(bigNameEt.getText().length()!=0)
						RegisterActivity.this.checkNickName(bigNameEt.getText().toString());
				}
				
			}
		});
//		nickNameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//			
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				if(actionId == EditorInfo.IME_ACTION_DONE){
//					RegisterActivity.this.checkNickName(nickNameEt.getText().toString().trim());
//				}
//				return false;
//			}
//		});
		emailInd=(ImageView) findViewById(R.id.emailInd);
		emailInd.setVisibility(View.GONE);
		progressInd=(ProgressBar) findViewById(R.id.processInd);
		progressInd.setVisibility(View.GONE);
		emailEt = (EditText) findViewById(R.id.emailEt);
		emailEt.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			} 
			  
        });
		emailEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			  
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(emailEt.hasFocus()){
					if(bigNameEt.getText().equals("")){
						nickNameInd.setVisibility(View.VISIBLE);
						
					}
					if(emailEt.length()!=0){
						emailEt.setText("");
						emailInd.setVisibility(View.GONE);
						stateTv.setVisibility(View.INVISIBLE);
					}
				}else{
					if(emailEt.length()!=0)
						RegisterActivity.this.checkEmail(emailEt.getText().toString());
				}
				
			}
		});
		
		privacySelect=(CheckBox)findViewById(R.id.privacySelect);
		privacySelect.performClick();//initial choosed
		privacyButton=(Button)findViewById(R.id.privacyButton);
		privacyButton.setOnClickListener(this);
		
		btn_regnext_step=(Button)findViewById(R.id.btn_regnext_step);
		btn_regnext_step.setOnClickListener(this);
		tologinButton=(Button)findViewById(R.id.tologinButton);
		tologinButton.setOnClickListener(this);
		
		step2=(LinearLayout)findViewById(R.id.step2);
		stateTv2=(TextView) findViewById(R.id.stateTv2);
		stateTv2.setVisibility(View.INVISIBLE);
		pwdEt = (EditText) findViewById(R.id.passEt);
		pwdEt.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
//				RegisterActivity.this.checkPassword(pwdEt.getText().toString().trim());
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			} 
			  
        });
		pwdEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			  
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(pwdEt.hasFocus()){
//					stateTv2.setVisibility(View.INVISIBLE);
				}else
					RegisterActivity.this.checkPassword(pwdEt.getText().toString().trim());
			}
		});	
		pwdAgainEt = (EditText) findViewById(R.id.passagainEt);
		pwdAgainEt.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
//				RegisterActivity.this.checkConfirmPassword(pwdAgainEt.getText().toString().trim());
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			} 
			  
        });
		pwdAgainEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			  
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(pwdAgainEt.hasFocus()){
//					stateTv2.setVisibility(View.INVISIBLE);
				}else{
					if(pwdAgainEt.length()!=0)
						RegisterActivity.this.checkConfirmPassword(pwdAgainEt.getText().toString().trim());
				}
			}
		});	
		reg_back= (ImageView)findViewById(R.id.reg_back);
		reg_back.setOnClickListener(this);
		reg_back.setVisibility(View.GONE);
		girlImg = (ImageButton) findViewById(R.id.image_girl);
		girlImg.setOnClickListener(this);
		boyImg =(ImageButton) findViewById(R.id.image_boy);
		boyImg.setOnClickListener(this);
		
//		mailState = (ImageView) findViewById(R.id.emailCheckIV);
//		backToLogin = (ImageButton) findViewById(R.id.reg_back);
//		backToLogin.setOnClickListener(this);
		
		nextBtn = (ImageButton) findViewById(R.id.btn_regnext);
		nextBtn.setOnClickListener(this);
		
		headPhoto = (ImageView) findViewById(R.id.image_index);
		headPhoto.setOnClickListener(this);
		
		step1.setVisibility(View.VISIBLE);//初始界面
    	step2.setVisibility(View.GONE);
    	reg_back.setVisibility(View.GONE);
	 }
	 
	 private void initData(){
		 
//		 mailState.setVisibility(View.GONE);
          stateTv.setVisibility(View.INVISIBLE);
          userOK=false;
		    mailCanReg = false;
		    passwordOK =false;
		    headPhotoOk = false;
		    isChooseSex = false;
		    
		 
	 }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.privacyButton:
			if(receiver != null)
			{  
				this.unregisterReceiver(receiver);
				receiver = null;
			}
			startActivity(new Intent(RegisterActivity.this,PrivacyActivity.class));
			break;
		case R.id.btn_regnext_step:
			nextstepPressed=true;
			//二次判断
			if(bigNameEt.getText().length()!=0)
				RegisterActivity.this.checkNickName(bigNameEt.getText().toString());
			else{
				userOK=false;
				stateTv.setText("请填写您的名字");
		    	stateTv.setVisibility(View.VISIBLE);
		    	nickNameInd.setVisibility(View.VISIBLE);
			}
				
			
			if (userOK == false) {
				nextstepPressed=false;
			}else if (privacySelect.isChecked()==false) {
		    	stateTv.setText("请阅读并同意服务协议");
		    	stateTv.setVisibility(View.VISIBLE);
		    	nextstepPressed=false;
		    }else {//if (mailCanReg == false) 
		    	if(emailEt.length()!=0)
					RegisterActivity.this.checkEmail(emailEt.getText().toString());
		    	else{
		    		mailCanReg=false;
		    		nextstepPressed=false;
		    	}
		    		
		    }//else{
		    	
		    	
		    //}
			break;
		case R.id.tologinButton:
			if(receiver != null)
			{  
				this.unregisterReceiver(receiver);
				receiver = null;
			}
			startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
//			finish();
			break;
		case R.id.reg_back:
			step1.setVisibility(View.VISIBLE);
	    	step2.setVisibility(View.GONE);
	    	reg_back.setVisibility(View.GONE);
			break;
		case R.id.image_boy:
			if(!isChooseSex) {
				isChooseSex = true;
				
				boyImg.setImageDrawable(getResources().getDrawable(R.drawable.boy_selected));
				girlImg.setImageDrawable(getResources().getDrawable(R.drawable.girl_normal));
//				nickNameEt.setBackgroundResource(R.drawable._0002_enter_your_name);
//				emailEt.setBackgroundResource(R.drawable._0002_enter_your_name);
//				pwdEt.setBackgroundResource(R.drawable._0002_enter_your_name);
//				pwdAgainEt.setBackgroundResource(R.drawable._0002_enter_your_name);
				   isGirl = false;
			}
			if(isGirl){
				boyImg.setImageDrawable(getResources().getDrawable(R.drawable.boy_selected));
				girlImg.setImageDrawable(getResources().getDrawable(R.drawable.girl_normal));
//				nickNameEt.setBackgroundResource(R.drawable._0002_enter_your_name);
//				emailEt.setBackgroundResource(R.drawable._0002_enter_your_name);
//				pwdEt.setBackgroundResource(R.drawable._0002_enter_your_name);
//				pwdAgainEt.setBackgroundResource(R.drawable._0002_enter_your_name);
				   isGirl = false;
			}
		  
			break;
		case R.id.image_girl:
			if(!isChooseSex) {
				isChooseSex = true;
				boyImg.setImageDrawable(getResources().getDrawable(R.drawable.boy_normal));
				girlImg.setImageDrawable(getResources().getDrawable(R.drawable.girl_selected));
//				nickNameEt.setBackgroundResource(R.drawable._0006_enter_your_name);
//				emailEt.setBackgroundResource(R.drawable._0006_enter_your_name);
//				pwdEt.setBackgroundResource(R.drawable._0006_enter_your_name);
//				pwdAgainEt.setBackgroundResource(R.drawable._0006_enter_your_name);
				   isGirl = true;
			}
			if(! isGirl){
				boyImg.setImageDrawable(getResources().getDrawable(R.drawable.boy_normal));
				girlImg.setImageDrawable(getResources().getDrawable(R.drawable.girl_selected));
//				nickNameEt.setBackgroundResource(R.drawable._0006_enter_your_name);
//				emailEt.setBackgroundResource(R.drawable._0006_enter_your_name);
//				pwdEt.setBackgroundResource(R.drawable._0006_enter_your_name);
//				pwdAgainEt.setBackgroundResource(R.drawable._0006_enter_your_name);
				   isGirl = true;
			}
			break;
		case R.id.btn_rigisterback:
		
			if(receiver != null)
			{  
				this.unregisterReceiver(receiver);
				receiver = null;
			}
			startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
			break;
		
		case R.id.btn_regnext:
			  
			//TODO
			checkPassword(pwdEt.getText().toString().trim());//二次判断
			checkConfirmPassword(pwdAgainEt.getText().toString().trim());
//			if (pwdEt.getText().toString().trim().equals(pwdAgainEt.getText().toString().trim())) {
//		        passwordOK = true;
//		    }
		 
		    
		    if(passwordOK ==false){
//		    	stateTv2.setText("密码不合法");
//		        stateTv2.setVisibility(View.VISIBLE);
		    }
		    else if(!isChooseSex ){
		    	stateTv2.setText("请问您是男生还是女生？");
		        stateTv2.setVisibility(View.VISIBLE);
		    }
//		    else if(headPhotoOk ==false){
//		    	stateTv.setText("请选头像");
//		       stateTv.setVisibility(View.VISIBLE);
//		    
//		    }
		    else{
//		    	if(headPhotoOk ==false){
//		    		 String headphotopath = Protocol.DEFAULT + "";
//		    		  Database.getInstance(this).saveHeadPhotoToTemp(this.emailEt.getText().toString(), headphotopath);
//			    	}
		    	stateTv2.setVisibility(View.INVISIBLE);
	
		        //TODO 裁剪图片 保存图片到sd卡，保存图片地址
		        //NSData *imagedata = UIImageJPEGRepresentation([headImage image],1);
		        //NSLog(@"%d",[imagedata length]);
		        //[[Database getInstance] saveHeadPhotoToTemp:mailInput.text image:imagedata ];
		        Intent regIntent =new Intent(RegisterActivity.this,SetAppearanceActivity.class);
		        String  sex = "b";
		        if(isGirl)
		            sex = "g";
		        else
		            sex = "b";
		        if(receiver != null)
				{  
					this.unregisterReceiver(receiver);
					receiver = null;
				}
		        this.compressAndSavePic();
		        Bundle regBundle = new Bundle();
		        regBundle.putString("sex", sex);
		        regBundle.putString("nickname", bigNameEt.getText().toString().trim());
		        regBundle.putString("account",emailEt.getText().toString().trim().toLowerCase());//服务器上只有小写
		        regBundle.putString("pwd",pwdEt.getText().toString().trim());
		        regBundle.putString("apply","applytoreg");
		        regIntent.putExtras(regBundle);
		        startActivity(regIntent);
		    }
		
			break;
		case R.id.image_index:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(pwdAgainEt.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(pwdEt.getWindowToken(), 0);
			onClickChooseHeadImageBtn();
			break;
		
		default :
			break;
		}
		
		
		
	}
	/*隐藏键盘
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
	
	public  boolean isShouldHideInput(View v, MotionEvent event) {  
        if (v != null && (v instanceof EditText)) {  
            int[] leftTop = { 0, 0 };  
            //获取输入框当前的location位置  
            v.getLocationInWindow(leftTop);  
            int left = leftTop[0];  
            int top = leftTop[1];  
            int bottom = top + v.getHeight();  
            int right = left + v.getWidth();  
            if (event.getX() > left && event.getX() < right  
                    && event.getY() > top && event.getY() < bottom) {  
                // 点击的是输入框区域，保留点击EditText的事件  
                return false;  
            } else {  
                return true;  
            }  
        }  
        return false;  
    } 
	
	// 为弹出窗口实现监听类

	private OnClickListener itemsOnClick = new OnClickListener() {
		public void onClick(View v) {
				mSelectPicPopup.dismiss();
				switch (v.getId()) {
				case R.id.btn_take_photo:
					// create Intent to take a picture and return control to the
					// calling application
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a
																		// file to
																		// save the
																		// image
					
					GlobalApplication.getInstance().setFileUri(fileUri);
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

	//XQDONE 供用户选择相片来源
	public void onClickChooseHeadImageBtn()
	{
		if (mSelectPicPopup == null) {
			mSelectPicPopup = new SelectPicPopup(RegisterActivity.this,
					itemsOnClick);
			mSelectPicPopup.showAtLocation(
					RegisterActivity.this.findViewById(R.id.registerRL),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

		} else if (!(mSelectPicPopup.isShowing())) {
			mSelectPicPopup.showAtLocation(
					RegisterActivity.this.findViewById(R.id.registerRL),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
		} else {
			mSelectPicPopup.dismiss();
		}
	 
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
			if(step2.getVisibility()!=View.VISIBLE){
				
				GlobalApplication.getInstance().finishAllActivity();
			}else{
				step1.setVisibility(View.VISIBLE);
		    	step2.setVisibility(View.GONE);
		    	reg_back.setVisibility(View.GONE);
			}
			
		}
		return true;
//		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == R.layout.activity_register) {
	   	 if(resultCode == 0){
			 //直接返回
	   		 return;
			
		 }
//		 if(resultCode == 1){
//			 //裁剪后的图片暂存在globalapplication
//			 this.headBitmap =  GlobalApplication.getInstance().getHeadPicBm();
//		       headPhoto.setImageBitmap(headBitmap);
//		       headPhotoOk = true;
//			 
//		 }
	    	
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
//	        BitmapFactory.Options options = new BitmapFactory.Options();  
//            options.inSampleSize = 2;  
//            options.outHeight = 90;
//            options.outWidth = 90;
//	       Bitmap tempheadBitmap =BitmapFactory.decodeFile(picturePath, options);
//	       headBitmap = AppManagerUtil.toRoundBitmap(tempheadBitmap);
//	       headBitmap = RoundedImageView.getCroppedBitmap(BitmapFactory.decodeFile(picturePath),90);  
//	        headBitmap =BitmapFactory.decodeFile(picturePath);  
//	        Intent  mIntent = new Intent(RegisterActivity.this,CropActivity.class);
//	        mIntent.putExtra("picPath", picturePath);
//	        mIntent.putExtra("flag", "0");
//	        startActivityForResult(mIntent,R.layout.activity_register);
//	        headPhoto.setImageBitmap(AppManagerUtil.createBitmapBySize(BitmapFactory.decodeFile(picturePath),100,100));
//	       headPhotoOk = true;
	       
//	       tempheadBitmap= null;
//	       try{
//	       tempheadBitmap.recycle();
//	       tempheadBitmap = null;
//	       }catch(Exception e){
//	    	   
//	       }
	    }
	    
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
//	            Toast.makeText(this, "Image saved to:\n" +
//	            		fileUri.getPath(), Toast.LENGTH_LONG).show();
	            HandleHeadPhotoTask t = new HandleHeadPhotoTask();
				t.execute(GlobalApplication.getInstance().getFileUri().getPath());
//	            Intent  mIntent = new Intent(RegisterActivity.this,CropActivity.class);
//		        mIntent.putExtra("picPath", fileUri.getPath());
//		        mIntent.putExtra("flag", "0");
//		        startActivityForResult(mIntent,R.layout.activity_register);
//	 	      headBitmap = RoundedImageView.getCroppedBitmap(BitmapFactory.decodeFile(fileUri.getPath(), options),90);
//		       headPhoto.setImageBitmap(headBitmap);
//		       headPhotoOk = true;
		  
	            
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	}
	
	
	/** 
     * 销毁图片文件 
     */  
    private void destoryBimap() {  
        if (headBitmap != null && !headBitmap.isRecycled()) {  
        	headBitmap.recycle();  
        	headBitmap = null;  
        }  
        

    }  
	
	public boolean checkEmailFormat(String emailAdd){
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(emailAdd);
		return m.matches();
	}
				
	public void checkNickName(String nickName){
		 if(this.hasWhiteSpace(nickName)){
			 stateTv.setText("名字中不能有空格哦");
				stateTv.setVisibility(View.VISIBLE);
				nickNameInd.setVisibility(View.VISIBLE);
				userOK=false;
		 }else if(nickName.trim().length() > 0 && nickName.trim().length() <= 10){
				stateTv.setVisibility(View.INVISIBLE);
				nickNameInd.setVisibility(View.INVISIBLE);
				userOK=true;
		 }else{
				 if(nickName.trim().length() > 10)
					 stateTv.setText("名字最多十个字哦");
				 else
					stateTv.setText("请输入你的名字");
				stateTv.setVisibility(View.VISIBLE);
				nickNameInd.setVisibility(View.VISIBLE);
				userOK=false;
		 }
		
	}
//if has white space
	public boolean hasWhiteSpace(String nickName) {
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(nickName);
		boolean found = matcher.find();
		return found;
	}
	
	public void checkEmail(String email){
		if(checkEmailFormat(email) && email.length() <= 30){
			stateTv.setVisibility(View.INVISIBLE);
			UserPacketHandler mReq = new UserPacketHandler();
			 mReq.CanMailReg(email);	
			 emailInd.setVisibility(View.GONE);
			 progressInd.setVisibility(View.VISIBLE);
		}else if (email.length()  > 30 ){
			stateTv.setText("邮箱过长哦");
			stateTv.setVisibility(View.VISIBLE);
			emailInd.setVisibility(View.GONE);
		}else{
		
			stateTv.setText("请输入格式正确的邮箱哦");
			stateTv.setVisibility(View.VISIBLE);
			emailInd.setVisibility(View.GONE);
		}
	}
	
	public void checkPassword(String pwd)
	{
		if(hasWhiteSpace(pwd)){
			stateTv2.setText("密码中不能有空格哦");
	    	stateTv2.setVisibility(View.VISIBLE);
	    	passwordOK=false;
		}else{
			
		    if (pwd.length() >= 6){
		        stateTv2.setVisibility(View.INVISIBLE);
		    }
		    else{
		    	stateTv2.setText("密码长度至少要6位");
		    	stateTv2.setVisibility(View.VISIBLE);
		    	passwordOK=false;
		    }
		}
	    
	}
	
	public void checkConfirmPassword(String mPwd)
	{
		
	    if (mPwd.equals(pwdEt.getText().toString().trim())&&
	    		mPwd.length() >= 6){
	    	stateTv2.setVisibility(View.INVISIBLE);
	    	passwordOK=true;
	    }
	    else{
	    	if(!mPwd.equals(pwdEt.getText().toString().trim())){
		    	stateTv2.setText("密码前后输入不一样");
		    	stateTv2.setVisibility(View.VISIBLE);
		    	
	    	}else if(mPwd.length() < 6){
	    		stateTv2.setText("密码长度至少要6位");
		    	stateTv2.setVisibility(View.VISIBLE);
	    	}
	    	passwordOK=false;
	    }
	    
	}
	
	public void compressAndSavePic()
	{
		if(headBitmap == null){
			if(isGirl)
				headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable._0002_girl_photo);
			else
				headBitmap = BitmapFactory.decodeResource(getResources(), R.drawable._0003_boy_photo);
		}
		  RegisterActivity.this.headPhoto.setDrawingCacheEnabled(true);
		  Bitmap temp =  ((BitmapDrawable)RegisterActivity.this.headPhoto.getDrawable()).getBitmap();  
//		  GlobalApplication.getInstance().setHeadPicBm(temp);
		  CommonBitmap.getInstance().addCacheBitmap(temp, CommonBitmap.MYHEADPHO);
		  CommonBitmap.getInstance().setMyHeadPhotoInit(true);

	}
	

	public class HandleHeadPhotoTask extends AsyncTask<String, String, Bitmap> {

			@Override
			protected void onPreExecute() {

			
                
				super.onPreExecute();
			}

			@Override
			protected Bitmap doInBackground(String... params) {
				HeadPhotoHanddler mHeadPhotoHandler = new HeadPhotoHanddler();
				Bitmap  endBm = null;
				Bitmap originBm = AppManagerUtil.getDiskBitmap(params[0]);
				  int angle= mHeadPhotoHandler.getExifOrientation(params[0]);
	    		  if(angle!=0){  //如果照片出现了 旋转 那么 就更改旋转度数
	    		                      Matrix matrix = new Matrix();
	    		                      matrix.postRotate(angle);
	    		                      originBm = Bitmap.createBitmap(originBm,
	    		                      0, 0, originBm.getWidth(), originBm.getHeight(), matrix, true);
	    		                  }

				endBm = mHeadPhotoHandler.getHeadPhoto(originBm);
				
			return endBm;
			}

			@Override
			protected void onProgressUpdate(String... values) {

				super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				headBitmap = result;
			  RegisterActivity.this.headPhoto.setImageBitmap(result);
			  headPhotoOk = true;
				super.onPostExecute(result);
			}

	}
	
    public void processMailCanReg(String str){
		 char operatorCode = 0;
			try {
				operatorCode = (char) (str.getBytes("UTF-8"))[3];
			} catch (UnsupportedEncodingException e) {
			
				e.printStackTrace();
			}
			switch(operatorCode){
			case Protocol.MAIL_CAN_REG:
				this.mailCanReg = true;
//				this.mailState.setVisibility(View.VISIBLE);
				stateTv.setVisibility(View.INVISIBLE);
				emailInd.setVisibility(View.GONE);
				 progressInd.setVisibility(View.GONE);
				 if(userOK==false){
					 
				 }else if(privacySelect.isChecked()==false){
					 
				 }else if(nextstepPressed){
					step1.setVisibility(View.GONE);
				    step2.setVisibility(View.VISIBLE);
				    reg_back.setVisibility(View.VISIBLE);
				    nextstepPressed=false;
				    pwdEt.setFocusable(true);//密码框初始得到焦点事件
				    pwdEt.setFocusableInTouchMode(true);
				    pwdEt.requestFocus();
				    InputMethodManager inputManager =

		                    (InputMethodManager)pwdEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

				    inputManager.showSoftInput(pwdEt, 0);//打开键盘
				 }
				break;
			case Protocol.MAIL_CANNOT_REG:
				this.mailCanReg = false;
				nextstepPressed=false;
//				this.mailState.setVisibility(View.GONE);
				stateTv.setText("这个邮箱已经注册过啦");
		    	stateTv.setVisibility(View.VISIBLE);
		    	emailInd.setVisibility(View.VISIBLE);
		    	progressInd.setVisibility(View.GONE);
				break;
			default:
				break;
			}
	 }
	public class MyReceiver extends BroadcastReceiver  
	{  
		@Override  
		public void onReceive(Context context, Intent intent)
		{  
			String action = intent.getAction();   
			
			if(Protocol.ACTION_USERPACKET.equals(action)){
				//读到数据,发送消息,让handler更新界面
				
				String data = intent.getStringExtra(Protocol.EXTRA_DATA);

				Bundle bdata = new Bundle();
				bdata.putString("data", data);
				Message msg = mHandler.obtainMessage();				
				msg.setData(bdata);
				msg.what =Protocol.HANDLE_DATA;
				mHandler.sendMessage(msg);
			   
			}
		
		}	//onReceive
	} 
	
	
	private static class MyHandler extends Handler
	{
		WeakReference<RegisterActivity> mActivity;  
		 MyHandler(RegisterActivity activity) {  
             mActivity = new WeakReference<RegisterActivity>(activity);  
     }  
		@Override
		public void handleMessage(Message msg)
		{
			RegisterActivity theActivity = mActivity.get();  
			String mData = msg.getData().getString("data");
			switch (msg.what)
			{
				case Protocol.HANDLE_DATA:
					Log.v("reg" ,"handle data ");
					if(GlobalApplication.getInstance().isRegVisible())
						theActivity.processMailCanReg(mData);
					break;
			
				default:
					break;
			}
		}
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
	            Log.d("MyCameraApp", "failed to create directory");
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
	
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 200;
	private static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 100;
//	private Uri fileUri;
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	
	private TextView stateTv;
	private ImageView  nickNameInd;
	private EditText  bigNameEt;
	private ImageView  emailInd;
	private ProgressBar progressInd;
	private EditText  emailEt;
	private EditText pwdEt;
	private EditText pwdAgainEt;
	private CheckBox privacySelect;
	private Button privacyButton ;
	private Button btn_regnext_step;
	private boolean nextstepPressed= false;
	private Button tologinButton;
	private boolean isGirl = true;
	private boolean isChooseSex = false;
	private boolean userOK=false;
	public boolean mailCanReg = false;
	private ImageView reg_back;
	private ImageButton girlImg;
	private ImageButton boyImg;
	private TextView stateTv2;
//	private ImageView mailState;
	private  boolean passwordOK = false;
    private boolean  headPhotoOk = false;
    private ImageButton nextBtn;
    private ImageView headPhoto;
	private  Bitmap headBitmap =null;
	
	 private SelectPicPopup mSelectPicPopup = null;
//	 private Button cancelBtn = null;
//	 private Button pickPicBtn = null;
//	 private Button takePicBtn = null;
	
	private MyReceiver receiver = null; 
	private MyHandler mHandler = new MyHandler(this);
	
	
}