package com.minus.lovershouse;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.minius.common.CommonBitmap;
import com.minius.ui.HeadPhotoHanddler;
import com.minus.actionsystem.InitFigureAppDrawable;
import com.minus.actionsystem.InitFigureAppearance;
import com.minus.lovershouse.R;
import com.minus.lovershouse.adapter.SetAppearanceAdapter;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.BadgeView;
import com.minus.sql_interface.Database;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.HeartPacketHandler;
import com.minus.xsocket.handler.UserPacketHandler;


public class SetAppearanceActivity extends BroadCast implements OnClickListener {
	
	private boolean isFirstInit = true;
		 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_setappearance);
	        if(receiver == null)
	   		 receiver = new MyReceiver();
	   		 IntentFilter filter=new IntentFilter();   
	   			filter.addAction(Protocol.ACTION_USERPACKET);
	               this.registerReceiver(receiver,filter); 
	        initView();
	        initData();
		
		
	    }
		 
	 @Override
		protected void onDestroy() {
			if(receiver != null)
			{  
				this.unregisterReceiver(receiver);
				receiver = null;
			}
			super.onDestroy();
		}

	private void initView(){
		this.topRL = (RelativeLayout) findViewById(R.id.mSetAppRL);
		this.boyRL = (RelativeLayout) findViewById(R.id.setappBoyView);
		this.girlRL = (RelativeLayout) findViewById(R.id.setappGirlView);
		
		   setHairView=(ViewGroup) getLayoutInflater().inflate(R.layout.sethair,null);
		   setBodyView = (ViewGroup) getLayoutInflater().inflate(R.layout.setbody,null);
		   setDecoView = (ViewGroup) getLayoutInflater().inflate(R.layout.setdeco,null);
		   
		  hairGridView = (GridView) setHairView.findViewById(R.id.hairgridview);
		  clothesGridView = (GridView) setBodyView.findViewById(R.id.clothesgridview);
		  decGridView = (GridView) setDecoView.findViewById(R.id.decgridview);

		    this.girlCloth = (ImageView) girlRL.findViewById(R.id.girlbodyAIV);
		    this.girlHair =(ImageView) girlRL.findViewById(R.id.girlhairAIV);
		    this.girlDec =(ImageView) girlRL.findViewById(R.id.girldecoAIV);
		    
		    this.boyCloth = (ImageView) boyRL.findViewById(R.id.boybodyAIV);
		    this.boyHair = (ImageView) boyRL.findViewById(R.id.boyhairAIV);
		    this.boyDec = (ImageView) boyRL.findViewById(R.id.boydecoAIV);
		    
		    
		    
		    this.backtoregisterBtn = (ImageView) findViewById(R.id.btn_setappback);
		    this.backtoregisterBtn.setOnClickListener(this);
		    
		    this.finishBtn = (ImageView) findViewById(R.id.btn_setfinish);
		    this.finishBtn.setOnClickListener(this);
		    
		    
		    DisplayMetrics dMetrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
	         ScreenWIDTH = dMetrics.widthPixels;
			
			appearanceList = (ListView)findViewById(R.id.decorateLv);	
			 appearanceList.getLayoutParams().width=(2*ScreenWIDTH)/16 ;
			this.appearanceListAdapter = new SetAppearanceAdapter(this);
			appearanceList.setAdapter(appearanceListAdapter);
			////////////////////////////////////////////////////////////////////////
			appearanceList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View v, int pos,
						long id) {
				
					switch(pos){
					
					case 0 :
						initSub(v);
						if(setHairView.isShown()){
							
						}else{
							setHairView.setVisibility(View.VISIBLE);
							
							setBodyView.setVisibility(View.GONE);
							setDecoView.setVisibility(View.GONE);
						}

						break;
					case 1 :
						initSub(v);
                       if(setBodyView.isShown()){
							
						}else{
							setHairView.setVisibility(View.GONE);
							
							setBodyView.setVisibility(View.VISIBLE);
							setDecoView.setVisibility(View.GONE);
						}


						break;
					case 2 :
						initSub(v);
						 if(setDecoView.isShown()){
								
							}else{
								setHairView.setVisibility(View.GONE);
								setDecoView.setVisibility(View.VISIBLE);
								setBodyView.setVisibility(View.GONE);
							}

						break;

						default:
							break;
						
					}
				}
				
			});
		
	 }
	
	private void initSub(View v){
		if(this.isFirstInit){
			this.isFirstInit= false;
			setHairView(v);
			setClothesView(v);
			setDecView(v);
			setHairView.setVisibility(View.GONE);
			setBodyView.setVisibility(View.GONE);
			setDecoView.setVisibility(View.GONE);
			
			
		}
		
	}
	/*
	  * 设置的子菜单
	  */
	 private void setHairView(View v){
		 //仿照IOS端，右边的弹出框比左边菜单稍小一点
		 RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams  
		            ( (13*ScreenWIDTH)/16,v.getHeight()*3-5);    		 
			    	 lp1.setMargins(v.getWidth(),0 ,0,0);
			    	 topRL.addView(setHairView, lp1);   

	 }
	 
	 /*
	  * 设置的子菜单
	  */
	 private void setClothesView(View v){
		 RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams  
		            ( (13*ScreenWIDTH)/16,v.getHeight()*3-5);    

			    	 lp1.setMargins(v.getWidth(),0 ,0,0);

			    	 topRL.addView(setBodyView, lp1);   

	 }
	 /*
	  * 设置的子菜单
	  */
	 private void setDecView(View v){
		 RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams  
		            ( (13*ScreenWIDTH)/16,v.getHeight()*3-5);    

			    	 lp1.setMargins(v.getWidth(),0 ,0,0);

			    	 topRL.addView(setDecoView, lp1);   

	 }
	 
	 
	 
	 private void initData(){
	
		 Bundle setAppBundle = this.getIntent().getExtras();
		 sex = setAppBundle.getString("sex");
		 nickName = setAppBundle.getString("nickname");
		 email = setAppBundle.getString("account");	
		 pwd = setAppBundle.getString("pwd");
		 apply = setAppBundle.getString("apply");
//		 if(this.apply.equals("applytochange"))
//			 backtoregisterBtn.setVisibility(View.GONE);
		 if(sex.equals("b")){
		 this.bg = 'b';
		 this.boyRL.setVisibility(View.VISIBLE);
		 this.girlRL.setVisibility(View.GONE);
		 this.hairGridViewAdapter = new ImageAdapter(boyHairImageIds,1);
		 this.hairGridView.setAdapter(hairGridViewAdapter);
		 this.clothesGridViewAdapter = new ImageAdapter(boyClothesImageIds,2);
		 this.clothesGridView.setAdapter(clothesGridViewAdapter);
		 this.decGridViewAdapter = new ImageAdapter(boyDecImageIds,3);
		 this.decGridView.setAdapter(decGridViewAdapter);
		 
		 this.hairGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					appear[1] = (char) (position +1);
					String hairName ="boy_hair_to_right"+(int) (position +1);
						int hairid = getResources().getIdentifier(hairName, "drawable",
								"com.minus.lovershouse"); 
					
							SetAppearanceActivity.this.boyHair.setBackgroundResource(hairid);
							ImageView m = (ImageView)view.findViewById(R.id.imageSelect);
							showHairSelectButton(m);
						
				}
			});
		 
		 this.clothesGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
					appear[2] = (char) (position+1);
					String clothesName="boy_clothes_to_right"+(int) (position +1);
					int clothesId = getResources().getIdentifier(clothesName, "drawable",
							"com.minus.lovershouse");
				
						SetAppearanceActivity.this.boyCloth.setBackgroundResource(clothesId);
						ImageView m = (ImageView)view.findViewById(R.id.imageSelect);
						showClothesSelectButton(m);
				}
			});
		 
		 this.decGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					appear[3] = (char) (position+1);
					String decName="boy_decoration_to_right"+(int) (position +1);
					int decId = getResources().getIdentifier(decName, "drawable",
							"com.minus.lovershouse");
				
						SetAppearanceActivity.this.boyDec.setBackgroundResource(decId);
						ImageView m = (ImageView)view.findViewById(R.id.imageSelect);
						showDecSelectButton(m);
//					
				}
			});
		 
//		 int padding_in_dp = 66;  // 6 dps
//		    int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
//		 RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
//		 lp.setMargins(5, padding_in_px, 0, 0);  
	
		

		 }else{
			 this.bg = 'g';
			 this.boyRL.setVisibility(View.GONE);
			 this.girlRL.setVisibility(View.VISIBLE);
			 this.hairGridViewAdapter = new ImageAdapter(girlHairImageIds,1);
			 this.hairGridView.setAdapter(hairGridViewAdapter);
			 this.clothesGridViewAdapter = new ImageAdapter(girlClothesImageIds,2);
			 this.clothesGridView.setAdapter(clothesGridViewAdapter);
			 this.decGridViewAdapter = new ImageAdapter(girlDecImageIds,3);
			 this.decGridView.setAdapter(decGridViewAdapter);
			 
			 this.hairGridView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
						appear[1] = (char)( position+1);
						String hairName ="girl_hair_to_left"+(int) (position +1);
							int hairid = getResources().getIdentifier(hairName, "drawable",
									"com.minus.lovershouse"); 
					
								SetAppearanceActivity.this.girlHair.setBackgroundResource(hairid);
								ImageView m = (ImageView)view.findViewById(R.id.imageSelect);
								showHairSelectButton(m);
							
					}
				});
			 
			 this.clothesGridView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if(position > 9){
							position = position + 4;
						}
						
						
						appear[2] = (char) (position +1);
						String clothesName="girl_clothes_to_left"+(int) (position +1);
						int clothesId = getResources().getIdentifier(clothesName, "drawable",
								"com.minus.lovershouse");
				
							SetAppearanceActivity.this.girlCloth.setBackgroundResource(clothesId);
							ImageView m = (ImageView)view.findViewById(R.id.imageSelect);
							showClothesSelectButton(m);
//						
					}
				});
			 
			 this.decGridView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						appear[3] = (char) (position +1);
						String decName="girl_decoration_to_left"+(int) (position +1);
						int decId = getResources().getIdentifier(decName, "drawable",
								"com.minus.lovershouse");
					
							SetAppearanceActivity.this.girlDec.setBackgroundResource(decId);
							ImageView m = (ImageView)view.findViewById(R.id.imageSelect);
							showDecSelectButton(m);
					
					}
				});

			
		
		 }
		//注册时使用默认形象，重设形象时用现有形象
		    if (this.apply.equals("applytoreg")) {
		    	 this.appear[0] = Protocol.DEFAULT;
				 this.appear[1] = Protocol.DEFAULT;
				 this.appear[2] = Protocol.DEFAULT;
				 this.appear[3] = Protocol.DEFAULT;
		    } else {
		        for (int i = 0; i < 4; i++) {
		        	appear[i] =  (SelfInfo.getInstance().getAppearance().charAt(i));
		        }
		    }
//		 init figure 
		    
		   
			if(this.bg == 'b'){
				 String hairName ="boy_hair_to_right"+(byte)appear[1];
					int hairid = getResources().getIdentifier(hairName, "drawable",
							"com.minus.lovershouse"); // name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
					String clothesName="boy_clothes_to_right"+(byte)appear[2];
					int clothesId = getResources().getIdentifier(clothesName, "drawable",
							"com.minus.lovershouse");
					String decName="boy_decoration_to_right"+(byte)appear[3];
					int decId = getResources().getIdentifier(decName, "drawable",
							"com.minus.lovershouse");
				this.boyCloth.setBackgroundResource(clothesId);
				this.boyDec.setBackgroundResource(decId);
				this.boyHair.setBackgroundResource(hairid);
			}else{
				 String hairName ="girl_hair_to_left"+(byte)appear[1];
					int hairid = getResources().getIdentifier(hairName, "drawable",
							"com.minus.lovershouse"); // name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
					String clothesName="girl_clothes_to_left"+(byte)appear[2];
					int clothesId = getResources().getIdentifier(clothesName, "drawable",
							"com.minus.lovershouse");
					String decName="girl_decoration_to_left"+(byte)appear[3];
					int decId = getResources().getIdentifier(decName, "drawable",
							"com.minus.lovershouse");
				this.girlCloth.setBackgroundResource(clothesId);
				this.girlDec.setBackgroundResource(decId);
				this.girlHair.setBackgroundResource(hairid);
			}
		 
		 
	 }
	 
	@Override
	public void onClick(View v) {
		switch(v.getId()){
	case R.id.btn_setappback:
			//TODO 善后
			if(receiver != null)
			{  
				this.unregisterReceiver(receiver);
				receiver = null;
			}
//			if(this.apply.equals("applytoreg"))
//				startActivity(new Intent(SetAppearanceActivity.this,RegisterActivity.class));
//			else
			finish();
			break;
		case R.id.btn_setfinish:
			onClickFinishBtn();
			break;
		default:
			break;
		}
	 
	 
	}
	
	private void onClickFinishBtn()
	{
	    if (savingPg != null) {
	    	savingPg.dismiss();
	    	  }
		if(this.apply.equals("applytoreg")){
			savingPg = ProgressDialog.show(this,"处理中 ","请稍后");
			savingPg.setCancelable(true); 
			savingPg.show();	
		
			String app = String.format("%c%c%c%c",appear[0],appear[1],appear[2],appear[3]);
			GlobalApplication.getInstance().setModifyAppearance(app);
			UserPacketHandler mReq = new UserPacketHandler();
			mReq.register(email, pwd, sex,nickName,app);
		} else if(this.apply.equals("applytochange"))
	    {
		
			String app = String.format("%c%c%c%c",appear[0],appear[1],appear[2],appear[3]);
			GlobalApplication.getInstance().setModifyAppearance(app);
			UserPacketHandler mReq = new UserPacketHandler();
			mReq.ModifyFigure(app); 
			}
	    
	}
	  @SuppressWarnings("deprecation")
	private void showPopupWindow(View parent,PopupWindow pop,int pos) { 
	    	
	    	
	    	pop.setOutsideTouchable(true);
	    	pop.setBackgroundDrawable(new BitmapDrawable());
	    	 int[] location = new int[2];  
	    	 parent.getLocationOnScreen(location);  
	           
	    	 pop.showAtLocation( parent, Gravity.NO_GRAVITY, location[0]+parent.getWidth(), 
	    			 location[1]-pos*parent.getHeight());  
	    	
	        pop.update();  
	    	}
	  /*
	  public void sendHeadPhoto(){
		
		  int maxBlob = 8100;

		  Bitmap headBm =CommonBitmap.getInstance().getMyHeadBm();
		  if(headBm == null) return ;
		  byte[] headPoto = Bitmap2Bytes(headBm);
		  int numPacket = (headPoto.length -1)/maxBlob + 1;
		    UserPacketHandler req =new UserPacketHandler();
		    String imageLen = String.format("%d",headPoto.length + 1);
		    if(numPacket == 1){
		    	req.UploadFirstHeadPhotoData( imageLen,headPoto);
		    	
		    	req.UploadHeadPhotoDataFinish(headPoto.length);
		    	return;
		    }else{
		    	byte[] first = new byte[maxBlob];
		    	System.arraycopy(headPoto,0, first, 0, maxBlob);
		    	req.UploadFirstHeadPhotoData(imageLen, first);

		    	for(int i = 1; i< numPacket - 1 ; i ++){
		    		byte[] app = new byte[maxBlob];
		    		System.arraycopy(headPoto,i * maxBlob,app, 0, maxBlob);
		    		req.UploadAppendHeadPhotoData(app);

		    	}
		    	int remainLen = headPoto.length - (numPacket - 1) * maxBlob;
		    	byte[] last = new byte[remainLen];
		    	System.arraycopy(headPoto, (numPacket-1)* maxBlob, last, 0, remainLen);
		    	req.UploadAppendHeadPhotoData( last);
		    	req.UploadHeadPhotoDataFinish(headPoto.length);
		    }
		
	  }*/
	  
	  /**  
	     * 把Bitmap转Byte  
	     */   /* 
	    public byte[] Bitmap2Bytes(Bitmap bm){    
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();    
	        bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);    
	        return baos.toByteArray();    
	    }
	    */
	    /**  
	     * @param 将字节数组转换为ImageView可调用的Bitmap对象  
	     * @param bytes  
	     * @param opts  
	     * @return Bitmap  
	     */    
	    public static Bitmap getPicFromBytes(byte[] bytes,    
	            BitmapFactory.Options opts) {    
	        if (bytes != null)    
	            if (opts != null)    
	                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,    
	                        opts);    
	            else    
	                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);    
	        return null;    
	    }    
	    
	  //XQDONE 注册成功之后存储下信息
	    public void RegisterSuccess() {
	    	
	    	Database.getInstance(
					GlobalApplication.getInstance().getApplicationContext())
					.initDatabase(email);
	    	  SharedPreferences mSp = this.getSharedPreferences(Protocol.PREFERENCE_NAME,Activity.MODE_PRIVATE);
	    	  SharedPreferences.Editor mEditor = mSp.edit();
	    	  mEditor.putString("LastUser", email);
	    	  mEditor.commit();
	           
	            //将头像传到远程服务器
		        //this.sendHeadPhoto();
	        
	        //  then 将账户信息保存至数据库, for now ,the headpic is still temp 
	    	 String path = "/HeadPhoto";
	         String bitName =email;
	         String delepath = Environment.getExternalStorageDirectory()  
	                 + "/LoverHouse"+path+"/"+bitName + ".png";
//	  	    String headPhotoPath = Database.getInstance(getApplicationContext()).readHeadPhotoFromTemp(email);
//		    Bitmap headBm = AppManagerUtil.getDiskBitmap(headPhotoPath);
//          String newPath =  "/HeadPhoto";
//	        String newHeadPicPath = Environment.getExternalStorageDirectory()  
//	                 + "/LoverHouse"+newPath+"/"+bitName + ".png";
//	        AppManagerUtil.writeToSD(newHeadPicPath, headBm, bitName);
	         
//	        AppManagerUtil.deleteFile(new File(delepath));
	         
	         
	        SelfInfo.getInstance().setAccount(email);
	        Database.getInstance(getApplicationContext()).addSelfInfo(email, pwd, delepath);
	        startSaveToSd();
	      //登录交由main处理
//	        UserPacketHandler mUserPacketHandler = new UserPacketHandler();
//	            mUserPacketHandler.
//				 Login(email, pwd);
	    }
	  public void startSaveToSd(){
		  new Thread(){
			  public void run(){
				  AppManagerUtil.writeToSD("/HeadPhoto", CommonBitmap.getInstance().getMyHeadBm(), email);
					Message msg = mHandler.obtainMessage();
					msg.what = DIALOGFINISH;
					mHandler.sendMessage(msg);
			  }
		  }.start();
	  }
	 
	  
	  public void jumpToMain(){
		    Intent intent = new Intent(SetAppearanceActivity.this,MainActivity.class);
	           intent.putExtra("who", 3);
	           intent.putExtra("usr", email);
               intent.putExtra("pwd", pwd);
               intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
	           startActivity(intent);
	           SetAppearanceActivity.this.finish();
	  }
	  
	  private void showHairSelectButton(View fatherView) {
			if (hairSelectedbadge == null) {
				hairSelectedbadge = new BadgeView(this, fatherView);
				
			} else {
				
				ViewGroup oldParent = (ViewGroup) hairSelectedbadge.getParent();
				if (oldParent != null) {
					oldParent.removeView(hairSelectedbadge);
				}
				hairSelectedbadge = null;
				hairSelectedbadge = new BadgeView(this, fatherView);

			}
			
			hairSelectedbadge.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
			hairSelectedbadge.setBackgroundResource(R.drawable.signup_2_decoration_tick);
		
			TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
			anim.setInterpolator(new BounceInterpolator());
			anim.setDuration(500);
			hairSelectedbadge.toggle(anim, null);
		}
	  
	  private void showClothesSelectButton(View fatherView) {
			if (clothesSelectedbadge == null) {
				clothesSelectedbadge = new BadgeView(this, fatherView);
				
			} else {
				
				ViewGroup oldParent = (ViewGroup) clothesSelectedbadge.getParent();
				if (oldParent != null) {
					oldParent.removeView(clothesSelectedbadge);
				}
				clothesSelectedbadge = null;
				clothesSelectedbadge = new BadgeView(this, fatherView);

			}
			
			clothesSelectedbadge.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
			clothesSelectedbadge.setBackgroundResource(R.drawable.signup_2_decoration_tick);
		
			TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
			anim.setInterpolator(new BounceInterpolator());
			anim.setDuration(500);
			clothesSelectedbadge.toggle(anim, null);
		}
	  
	  private void showDecSelectButton(View fatherView) {
			if (decSelectedbadge == null) {
				decSelectedbadge = new BadgeView(this, fatherView);
				
			} else {
				
				ViewGroup oldParent = (ViewGroup) decSelectedbadge.getParent();
				if (oldParent != null) {
					oldParent.removeView(decSelectedbadge);
				}
				decSelectedbadge = null;
				decSelectedbadge = new BadgeView(this, fatherView);

			}
			
			decSelectedbadge.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
			decSelectedbadge.setBackgroundResource(R.drawable.signup_2_decoration_tick);
		
			TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
			anim.setInterpolator(new BounceInterpolator());
			anim.setDuration(500);
			decSelectedbadge.toggle(anim, null);
		}
	  public void processReg(String mData){
		  char operatorCode = 0;
			try {
				operatorCode = (char) (mData.getBytes("UTF-8"))[3];
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
//			  Log.v("setapp","processReg");
		
			switch(operatorCode){
			case Protocol.REGISTER_SUCC:
				RegisterSuccess();
				break;
			case Protocol.REGISTER_FAIL:
				 if (savingPg != null) {
				savingPg.dismiss();
				    	  }
				break;
			case Protocol.MODIFY_HEAD_PHOTO_SUCC:
				
			case Protocol.MODIFY_HEAD_PHOTO_FAIL:
				
				break;
			case Protocol.MODIFY_FIGURE_SUCC:
				String app = String.format("%c%c%c%c",appear[0],appear[1],appear[2],appear[3]);
				if(this.bg == 'b'){
					InitFigureAppDrawable.getInstance().resetBoyAppearance(app);
				}else{
					InitFigureAppDrawable.getInstance().resetGirlAppearance(app);
				}
				jumpToMain();
				break;
			case Protocol.MODIFY_FIGURE_FAIL:
				jumpToMain();
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
			WeakReference<SetAppearanceActivity> mActivity;  
			 MyHandler(SetAppearanceActivity activity) {  
	             mActivity = new WeakReference<SetAppearanceActivity>(activity);  
	     }  
			@Override
			public void handleMessage(Message msg)
			{
				SetAppearanceActivity theActivity = mActivity.get();  
				String mData = msg.getData().getString("data");
				switch (msg.what)
				{
					case Protocol.HANDLE_DATA:
//						Log.v("result" ,"handle data ");
					    theActivity.processReg(mData);
						break;
					case DIALOGFINISH:
						 if (theActivity.savingPg != null) {
							 theActivity.savingPg.dismiss();
						    	  }
						 theActivity.jumpToMain();
						 break;
					default:
						break;
				}
			}
		}
		
		static class ViewHolder {
			ImageView imageView;
			ImageView index ;

		}
		public class ImageAdapter extends BaseAdapter {
			
			private int[] imageSource = null;
			int type =0;
			public ImageAdapter(int[] imgSource, int type){
				super();
				this.imageSource = imgSource;
				this.type = type;
			}
			@Override
			public int getCount() {
				return imageSource.length;
			}

			@Override
			public Object getItem(int position) {
				return imageSource[position];
			}

			@Override
			public long getItemId(int position) {
				return position;
			}
			private boolean isInit = false;

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final ViewHolder holder;
				View view = convertView;
				if (view == null) {
					view = getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
					holder = new ViewHolder();
			
					holder.imageView = (ImageView) view.findViewById(R.id.image);
					holder.index = (ImageView) view.findViewById( R.id.imageSelect);
					
					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
				}

				holder.imageView.setImageResource(imageSource[position]);
				if(parent.getChildCount() == position){
					if(!(isInit)){
				if(type == 1){
					if(position == ((int)appear[1] -1)){
						showHairSelectButton(holder.index);
					}
				}else if(type ==2){
					if(position == ((int)appear[2] -1)){
						showClothesSelectButton(holder.index);
					}
				}else if(type == 3){
					if(position == ((int)appear[3] -1)){
						showDecSelectButton(holder.index);
					}
				}
				isInit =true;
					}
				}
				return view;
			}
		}
	  private ListView appearanceList;
	  private SetAppearanceAdapter appearanceListAdapter;
	  private int ScreenWIDTH ;
	  private PopupWindow popupWindow;
	  private ViewGroup setHairView;
	  private ViewGroup setBodyView;
	  private ViewGroup setDecoView;
	  private ImageView backtoregisterBtn;
	  private ImageView finishBtn;

	 

	  
	  
	  
	  
	  private ProgressDialog savingPg;
	  private char[] appear = new char[4];
	  private char bg;     // b : boy  g :girl
	  private char fhcd; //face hair clothes decoration 1 2 3 4
	  private char num;
	  private String headPhotoPath = "";
	  private String sex = "",nickName = "",email = "",pwd= "";
	  private String apply = "";
	  private  Bitmap headBitmap =null;
		
		private MyReceiver receiver = null; 
		private MyHandler mHandler = new MyHandler(this);
		
		private static final  int DIALOGFINISH = 100;
		
		
		private int[] boyHairImageIds = { R.drawable.b21,
				R.drawable.b22, R.drawable.b23,
				R.drawable.b24, R.drawable.b25,
				R.drawable.b26};
		private int[] girlHairImageIds = { R.drawable.g21,
				R.drawable.g22, R.drawable.g23,
				R.drawable.g24, R.drawable.g25,
				R.drawable.g26};
		private int[] boyClothesImageIds = { R.drawable.b31,
				R.drawable.b32, R.drawable.b33,
				R.drawable.b34, R.drawable.b35,
				R.drawable.b36, R.drawable.b37,
				R.drawable.b38,R.drawable.b39 };
		private int[] girlClothesImageIds = { R.drawable.g31,
				R.drawable.g32, R.drawable.g33,
				R.drawable.g34, R.drawable.g35,
				R.drawable.g36, R.drawable.g37,
				R.drawable.g38,R.drawable.g39,
				R.drawable.g310,
				R.drawable.g315,R.drawable.g316, R.drawable.g317 };
//		11 -- 14 没 R.drawable.g311,R.drawable.g312,R.drawable.g313,R.drawable.g314,
		private int[] boyDecImageIds = { R.drawable.b41,
				R.drawable.b42, R.drawable.b43,
				R.drawable.b44, R.drawable.b45,
				R.drawable.b46};
		private int[] girlDecImageIds = { R.drawable.g41,
				R.drawable.g42, R.drawable.g43,
				R.drawable.g44, R.drawable.g45,
				R.drawable.g46, R.drawable.g47,
				R.drawable.g48,R.drawable.g49,
				R.drawable.g410,R.drawable.g411,
				R.drawable.g412  };
		private GridView hairGridView = null;
		private ImageAdapter hairGridViewAdapter = null;
		private GridView clothesGridView = null;
		private ImageAdapter clothesGridViewAdapter = null;
		private GridView decGridView = null;
		private ImageAdapter decGridViewAdapter = null;
		
		private RelativeLayout topRL = null;
		private RelativeLayout  boyRL = null;
		private RelativeLayout girlRL = null;
		  private ImageView  boyCloth = null;
		  private ImageView boyHair = null;
		  private ImageView boyDec = null;

		  private ImageView  girlCloth = null;
		  private ImageView girlHair = null;
		  private ImageView girlDec = null;
		  
		  private BadgeView hairSelectedbadge = null;
		  private BadgeView clothesSelectedbadge = null;
		  private BadgeView decSelectedbadge = null;
	
}

