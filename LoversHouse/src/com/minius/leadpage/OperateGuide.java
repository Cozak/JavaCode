package com.minius.leadpage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.minus.actionsystem.InitFigureAppDrawable;
import com.minus.lovershouse.CoupleActionActivity;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.CoupleActionActivity.GetImageTask;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.setting.ConfigActivity;
import com.minus.lovershouse.util.ExitPopup;
import com.minus.xsocket.asynsocket.protocol.Protocol;

public class OperateGuide extends Activity {
	
private ImageButton guideBtn = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainopenhelp);
        guideBtn = (ImageButton) findViewById(R.id.showBtn);
        guideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(OperateGuide.this, ConfigActivity.class);
				intent.putExtra("isNeedShowGuide", true);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
				OperateGuide.this.finish();

				
			}
		});
       
    }
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //»ñÈ¡ back¼ü
    		
    		Intent intent = new Intent();
			intent.setClass(OperateGuide.this, ConfigActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtra("isNeedShowGuide", true);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_left,
					R.anim.out_to_right);
			OperateGuide.this.finish();
    	}
    	return true;
	}
    
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Intent intent = new Intent();
		intent.setClass(OperateGuide.this, ConfigActivity.class);
		intent.putExtra("isNeedShowGuide", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_left,
				R.anim.out_to_right);
		OperateGuide.this.finish();
		return super.onTouchEvent(event);
	}


	@Override
	protected void onResume() {
		super.onResume();

	}
}

	