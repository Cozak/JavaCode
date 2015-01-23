package com.minius.leadpage;

import com.minus.lovershouse.R;
import com.minus.lovershouse.RegisterActivity;
import com.minus.xsocket.asynsocket.protocol.Protocol;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class TranAnimation extends Activity {
	
	private ImageView mLeft;
//	private ImageView mRight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leadpageanim);
        
        mLeft = (ImageView)findViewById(R.id.imageLeft);
//        mRight = (ImageView)findViewById(R.id.imageRight);
        
        AnimationSet anim = new AnimationSet(true);
		TranslateAnimation mytranslateanim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-1f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
		mytranslateanim.setDuration(1500);
		anim.setStartOffset(500);
		anim.addAnimation(mytranslateanim);
		anim.setFillAfter(true);
		mLeft.startAnimation(anim);
		
//		AnimationSet anim1 = new AnimationSet(true);
//		TranslateAnimation mytranslateanim1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+1f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
//		mytranslateanim1.setDuration(1500);
//		anim1.addAnimation(mytranslateanim1);
//		anim1.setStartOffset(500);
//		anim1.setFillAfter(true);
//		mRight.startAnimation(anim1);
		
		 SharedPreferences mSP = getApplicationContext()
					.getSharedPreferences(Protocol.PREFERENCE_NAME,
							Activity.MODE_PRIVATE);
		 SharedPreferences.Editor mEditor = mSP.edit();
		 mEditor.putBoolean("IsFirstRun", false);
		 mEditor.commit();
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				Intent intent = new Intent (TranAnimation.this,RegisterActivity.class);			
				startActivity(intent);			
				TranAnimation.this.finish();
			}
		}, 2000);
    }
}
