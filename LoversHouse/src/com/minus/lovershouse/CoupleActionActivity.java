package com.minus.lovershouse;

import java.lang.ref.WeakReference;
import java.util.Timer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.minus.actionsystem.InitFigureAppDrawable;
import com.minus.lovershouse.MainActivity.LoginTask;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.setting.PasswordActivity;
import com.minus.lovershouse.setting.UnlockGesturePasswordActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.sql_interface.Database;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.UserPacketHandler;


public class CoupleActionActivity extends BroadCast {
	
	private int coupelActionType = -1;
	private boolean isBoy = false;
	private boolean isSelfDo = false;
	private ImageView m = null;
	private ImageView hugGirlHair = null;
	private WeakReference<View> contentView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        coupelActionType = this.getIntent().getIntExtra("coupleActioinType",-1);
        isBoy = this.getIntent().getBooleanExtra("isBoy", false);  //发出双人动作人的性别
        isSelfDo = this.getIntent().getBooleanExtra("isSeflDo", false);
        switch(coupelActionType){
        case Protocol.ABUSE:
        	//男生不能虐待
            contentView = new WeakReference<View>(InitFigureAppDrawable.getInstance().getAbuseVG(getLayoutInflater()));
        	
        	 setContentView(contentView.get());
        	  m = (ImageView) contentView.get().findViewById(R.id.boy_abuse_body);
        	break;
        case Protocol.PINCHEDFACE:
        	if(isBoy){   	
        		contentView = new WeakReference<View>(InitFigureAppDrawable.getInstance().getGirlpFaceVG(getLayoutInflater()));
            	
        		setContentView(contentView.get());
        		 m = (ImageView) contentView.get().findViewById(R.id.girl_pinchedface_main);
        	}else{		
               contentView = new WeakReference<View>(InitFigureAppDrawable.getInstance().getBoypFaceVG(getLayoutInflater())); 	
        	   setContentView(contentView.get());
        
        	 m = (ImageView) contentView.get().findViewById(R.id.boy_pinchedface_main);
        	}
        	break;
        case Protocol.PETTING:
        	if(isBoy){
       contentView = new WeakReference<View>(InitFigureAppDrawable.getInstance().getGirlpHeadVG(getLayoutInflater()));
            	
        		setContentView(contentView.get());
        		
        		 m = (ImageView) contentView.get().findViewById(R.id.girl_pinchedface_main);

        	}else{	
         contentView = new WeakReference<View>(InitFigureAppDrawable.getInstance().getBoypHeadVG(getLayoutInflater()));
            	
        		setContentView(contentView.get());
        	
        	 m = (ImageView) contentView.get().findViewById(R.id.boy_pinchedface_main);
        	}
        	break;
        case Protocol.KISS:
        	contentView = new WeakReference<View>(InitFigureAppDrawable.getInstance().getKissVG(getLayoutInflater()));
        	
    		setContentView(contentView.get());
        	
        	  m = (ImageView) contentView.get().findViewById(R.id.kiss_main);

        	break;
        case Protocol.HUG:
             contentView = new WeakReference<View>(InitFigureAppDrawable.getInstance().getHugVG(getLayoutInflater()));
        	
    		setContentView(contentView.get());
        	 
        	  m = (ImageView) contentView.get().findViewById(R.id.hug_main);
        	  hugGirlHair = (ImageView) contentView.get().findViewById(R.id.girl_hug_hair);

        	break;
        default:
        	this.finish();
        	break;
        	
        
        }
     
        if(this.mHandler == null){
			this.mHandler = new MyHandler();	
		}
        
    }
    
    @Override
    public void onBackPressed() {
    	// do nothing, prevent user from discontinuing coupleAction which is a bug.
    	// 返回键空操作，因为用户中止双人动作会导致bug.
    	if (imageTask != null)
    		imageTask.cancel(false);
    	
    	this.mHandler.sendEmptyMessage(1); //mHandler will call stopCoupleAction() and Activity.finish().
    }
    
    //在coupleAction执行过程中，如果摁下HOME键，GetImageTask会执行两次导致bug（更加细致的原因尚未查找），此处防止GetImageTask执行两次
    private boolean haveExec_GetImageTask = false;
    GetImageTask imageTask = null;
    @Override
	protected void onResume() {
		if (BuildConfig.DEBUG) Log.d("CoupleActionActivity", "Entering onResume");
		
		super.onResume();
		
		if (haveExec_GetImageTask == false)
		{
			haveExec_GetImageTask = true;
			imageTask = new GetImageTask();
			if(isBoy){
				imageTask.execute(coupelActionType,1);
			}else{
				imageTask.execute(coupelActionType,0);
			}
		}
		
		if (BuildConfig.DEBUG) Log.d("CoupleActionActivity", "Leaving onResume");
//		handleCoupleAction(isBoy,isSelfDo,coupelActionType);
	}

	/**
	 * 
	 * @param isBoy  发动作的人是否是男的
	 * @param isSelfDo 是否是由y用户自己发的双人动作
	 */
	private void handleCoupleAction(boolean isBoy,boolean isSelfDo,int actionType) {
		switch(actionType){
		 case Protocol.ABUSE:
				final ImageView handView = (ImageView) contentView.get().findViewById(R.id.girl_abuse_hand);
				handView.setVisibility(View.VISIBLE);
				handView.post(new Runnable(){

					@Override
					public void run() {
						   AnimationDrawable hand = (AnimationDrawable) handView
									.getDrawable();
						     hand.start();
						
					}
					
				});
			
      	break;
      case Protocol.PINCHEDFACE:
    	  if(isBoy){
    		
    
			final ImageView pinhandView = (ImageView)  contentView.get().findViewById(R.id.girl_pinchedface_hand);
		
			pinhandView.setVisibility(View.VISIBLE);
			final ImageView pinEyeView = (ImageView)  contentView.get().findViewById(R.id.girl_pinchedface_eye);
			
			pinEyeView.setVisibility(View.VISIBLE);
			pinhandView.post(new Runnable(){

				@Override
				public void run() {
					AnimationDrawable pinhand = (AnimationDrawable) pinhandView
							.getDrawable();
					pinhand.start();
					
					AnimationDrawable pinEye = (AnimationDrawable) pinEyeView
							.getDrawable();
					pinEye.start();
					
				}
				
			});
		

		
		
    	  }else{
    	
		final ImageView boypinhandView = (ImageView)  contentView.get().findViewById(R.id.boy_pinchedface_hand);
		boypinhandView.setVisibility(View.VISIBLE);
		final ImageView boypinEyeView = (ImageView)  contentView.get().findViewById(R.id.boy_pinchedface_eye);
		boypinEyeView.setVisibility(View.VISIBLE);
		boypinhandView.post(new Runnable(){

			@Override
			public void run() {
				AnimationDrawable pinhand = (AnimationDrawable) boypinhandView
						.getDrawable();
				pinhand.start();
			
				AnimationDrawable pinEye = (AnimationDrawable) boypinEyeView
						.getDrawable();
				pinEye.start();
				
			}
			
		});
	
		
    	  }
    	  
    	  
    	  break;
      case Protocol.PETTING:
    	  if(isBoy){
    		
				final ImageView pinhandView = (ImageView)  
						contentView.get().findViewById(R.id.girl_petted_hand);
				pinhandView.setVisibility(View.VISIBLE);
				final ImageView pinEyeView = (ImageView) contentView.get().findViewById(R.id.girl_pinchedface_eye);
				pinEyeView.setVisibility(View.VISIBLE);
				
				pinEyeView.post(new Runnable(){

					@Override
					public void run() {
						AnimationDrawable pinhand = (AnimationDrawable) pinhandView
								.getDrawable();
						pinhand.start();

					
						AnimationDrawable pinEye = (AnimationDrawable) pinEyeView
								.getDrawable();
						pinEye.start();
					}
					
				});
				
			
      	  }else{
     
			final ImageView pinhandView = (ImageView)  contentView.get().findViewById(R.id.boy_petted_hand);
			pinhandView.setVisibility(View.VISIBLE);
			final ImageView pinEyeView = (ImageView) contentView.get().findViewById(R.id.boy_pinchedface_eye);
			pinEyeView.setVisibility(View.VISIBLE);
			pinEyeView.post(new Runnable(){

				@Override
				public void run() {
					AnimationDrawable pinhand = (AnimationDrawable) pinhandView
							.getDrawable();
					pinhand.start();
				
					AnimationDrawable pinEye = (AnimationDrawable) pinEyeView
							.getDrawable();
					pinEye.start();
				}
				
			});
			
		
		
      	  }
     
      	break;
      case Protocol.KISS:
    	
    		final ImageView butterflyImg= (ImageView)  contentView.get().findViewById(R.id.Butterfly);
    		
    		butterflyImg.post(new Runnable(){

				@Override
				public void run() {
					AnimationDrawable butterfly = (AnimationDrawable) butterflyImg
							.getDrawable();
					butterfly.start();
				}
				
			});
		
			 
      	break;
      case Protocol.HUG:
    	 final  ImageView girlHugEyeImg= (ImageView)  contentView.get().findViewById(R.id.girl_hug_eye);
    	final ImageView boyHugEyeImg= (ImageView)  contentView.get().findViewById(R.id.boy_hug_eye);
    	
    	girlHugEyeImg.setVisibility(View.VISIBLE);
    	boyHugEyeImg.setVisibility(View.VISIBLE);
    	boyHugEyeImg.post(new Runnable(){

			@Override
			public void run() {
				AnimationDrawable girlHugEye = (AnimationDrawable) girlHugEyeImg
		  				.getDrawable();
		  		girlHugEye.start();
		
				AnimationDrawable boyHugEye = (AnimationDrawable) boyHugEyeImg
						.getDrawable();
				boyHugEye.start();
			}
			
		});
  		
		
      	break;
      default:
      	break;
		}
		
		mHandler.sendEmptyMessageDelayed(1, 5000); //mHandler will call stopCoupleAction() and Activity.finish().
	}
	private void stopCoupleAction(boolean isBoy,boolean isSelfDo,int actionType){
		if(contentView.get() == null ) return;
		
		switch(actionType){
		
		 case Protocol.ABUSE:
				ImageView handView = (ImageView) contentView.get().findViewById(R.id.girl_abuse_hand);
		        AnimationDrawable hand = (AnimationDrawable) handView
				.getDrawable();
	        	hand.stop();
	        	handView = null;
	        	
	      
      	break;
      case Protocol.PINCHEDFACE:
    	  if(isBoy){
    		
//    		initPFaceOrPHead( true);
			ImageView pinhandView = (ImageView)  contentView.get().findViewById(R.id.girl_pinchedface_hand);
//			pinhandView.setVisibility(View.VISIBLE);
			AnimationDrawable pinhand = (AnimationDrawable) pinhandView
					.getDrawable();
			pinhand.stop();

			ImageView pinEyeView = (ImageView)  findViewById(R.id.girl_pinchedface_eye);
			AnimationDrawable pinEye = (AnimationDrawable) pinEyeView
					.getDrawable();
			pinEye.stop();
			
			pinhandView = null;
			pinhand = null;
			pinEyeView = null;
			pinEye = null;
			
    	  }else{
    		
		ImageView pinhandView = (ImageView) contentView.get().findViewById(R.id.boy_pinchedface_hand);
//		pinhandView.setVisibility(View.VISIBLE);
		AnimationDrawable pinhand = (AnimationDrawable) pinhandView
				.getDrawable();
		pinhand.stop();
		ImageView pinEyeView = (ImageView)  contentView.get().findViewById(R.id.boy_pinchedface_eye);
		AnimationDrawable pinEye = (AnimationDrawable) pinEyeView
				.getDrawable();
		pinEye.stop();
		
		pinhandView = null;
		pinhand = null;
		pinEyeView =null;
		pinEye = null;
    	  }
    	  
    	  
    	  break;
      case Protocol.PETTING:
    	  if(isBoy){
    	
				ImageView pinhandView = (ImageView)  
						contentView.get().findViewById(R.id.girl_petted_hand);
//				pinhandView.setVisibility(View.VISIBLE);
				AnimationDrawable pinhand = (AnimationDrawable) pinhandView
						.getDrawable();
				pinhand.stop();

				ImageView pinEyeView = (ImageView) contentView.get().findViewById(R.id.girl_pinchedface_eye);
				AnimationDrawable pinEye = (AnimationDrawable) pinEyeView
						.getDrawable();
				pinEye.stop();
				
				pinhandView = null;
				pinhand = null;
				pinEyeView = null;
				pinEye = null;
				
				
      	  }else{
      
			ImageView pinhandView = (ImageView)  contentView.get().findViewById(R.id.boy_petted_hand);
//			pinhandView.setVisibility(View.VISIBLE);
			AnimationDrawable pinhand = (AnimationDrawable) pinhandView
					.getDrawable();
			pinhand.stop();
			ImageView pinEyeView = (ImageView) contentView.get().findViewById(R.id.boy_pinchedface_eye);
			AnimationDrawable pinEye = (AnimationDrawable) pinEyeView
					.getDrawable();
			pinEye.stop();
			
			pinhandView = null;
			pinhand = null;
			pinEyeView= null;
			pinEye = null;
      	  }
     
      	break;
      case Protocol.KISS:
    	  ImageView butterflyImg= (ImageView)  contentView.get().findViewById(R.id.Butterfly);
			AnimationDrawable butterfly = (AnimationDrawable) butterflyImg
					.getDrawable();
			butterfly.stop();
			butterflyImg =  null;
      	break;
      case Protocol.HUG:
    	  ImageView girlHugEyeImg= (ImageView)  contentView.get().findViewById(R.id.girl_hug_eye);
    		AnimationDrawable girlHugEye = (AnimationDrawable) girlHugEyeImg
    				.getDrawable();
    		girlHugEye.stop();
    		girlHugEyeImg = null;
    		girlHugEye = null;
  		ImageView boyHugEyeImg= (ImageView)  contentView.get().findViewById(R.id.boy_hug_eye);
  		AnimationDrawable boyHugEye = (AnimationDrawable) boyHugEyeImg
  				.getDrawable();
  		boyHugEye.stop();
  		boyHugEyeImg = null;
  		boyHugEye = null;
      	break;
      default:
      	break;
		}
		
		
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	     
		
		contentView =  null;
		mHandler = null;
		   if(m instanceof ImageView)
           {
               Drawable d=((ImageView)m).getDrawable();
               if(d!=null&&d instanceof BitmapDrawable)
               {                        
            	  
                   Bitmap bmp=((BitmapDrawable)d).getBitmap();
                   bmp.recycle();
                   bmp=null;
               }
               ((ImageView)m).setImageBitmap(null);
               if(d!=null){
                   d.setCallback(null);
               }
           }
		   
		 

	}
	

	private  class MyHandler extends Handler {

		private boolean hasExec_stopCoupleAction = false; //只调用一次stopCoupleAction
		@Override
		public void handleMessage(Message msg) {
	
			switch (msg.what) {  
		     case 1:
		    	 if (hasExec_stopCoupleAction == false) {
		    		 hasExec_stopCoupleAction = true;
			    	 stopCoupleAction(isBoy,isSelfDo,coupelActionType);
			    	 ViewGroup oldParent = (ViewGroup) CoupleActionActivity.this.contentView.get().getParent();
			    	 if (oldParent != null)
			    		 oldParent.removeView(CoupleActionActivity.this.contentView.get());
			    	 CoupleActionActivity.this.finish();
		    	 }
	          default:
	                break;
			}
		}
	}
	private Handler mHandler = null;

	
	 public class GetImageTask extends AsyncTask<Integer, String,Bitmap> {

			@Override
			protected void onPreExecute() {

				super.onPreExecute();
			}

			@Override
			protected Bitmap doInBackground(Integer... params) {
				Bitmap temp = null;
				switch(params[0]){
				 case Protocol.ABUSE:
					 temp = 
			            InitFigureAppDrawable.getInstance().getCoupleAbuseID(getApplicationContext());
		      	break;
		      case Protocol.PINCHEDFACE:
		      case Protocol.PETTING:
		    	 
		    	  temp = 
		            InitFigureAppDrawable.getInstance().getPFaceOrPHeadID(getApplicationContext()
		            		,params[1]);
		      	break;
		      case Protocol.KISS:
		    	  
		    	  temp = 
		            InitFigureAppDrawable.getInstance().getKissID(getApplicationContext());
		    	  break;
		    	 
		      case Protocol.HUG:
		    	 temp = 
		            InitFigureAppDrawable.getInstance().getHugID(getApplicationContext());
		      	break;
		      default:
		      	break;
				}
				return temp;
			}

			@Override
			protected void onProgressUpdate(String... values) {
                
				super.onProgressUpdate(values);
			}

			@Override
			protected void onCancelled() {
				
			}
			
			@Override
			protected void onPostExecute(Bitmap result) {
				if(coupelActionType == Protocol.HUG){
					int hugGirlHairId =  InitFigureAppDrawable.getInstance().getHugGirlHair();
					hugGirlHair.setVisibility(View.VISIBLE);
					hugGirlHair.setImageResource(hugGirlHairId);
				}
				m.setImageBitmap(result);
				handleCoupleAction(isBoy,isSelfDo,coupelActionType);
				super.onPostExecute(result);
			}

		}
	
}