package com.minius.leadpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.minus.lovershouse.R;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.setting.ConfigActivity;

public class ConfigOperateGuide extends Activity {
	
private ImageButton guideBtn = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configwriteta);

        guideBtn = (ImageButton)findViewById(R.id.showBtn);
        guideBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ConfigOperateGuide.this, ConfigActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  

				intent.putExtra("isNeedShowGuide", false);
				startActivity(intent);
				
				ConfigOperateGuide.this.finish();				
			}
		});


    }
    
    
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent();
		intent.setClass(ConfigOperateGuide.this, ConfigActivity.class);
		 intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
//		 onNewIntent(). 
		intent.putExtra("isNeedShowGuide", false);
		startActivity(intent);
		
		ConfigOperateGuide.this.finish();
		return super.onTouchEvent(event);
	}

	  @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //»ñÈ¡ back¼ü
	    		
	    		Intent intent = new Intent();
	    		intent.setClass(ConfigOperateGuide.this, ConfigActivity.class);
	    		 intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
//	    		 onNewIntent(). 
	    		intent.putExtra("isNeedShowGuide", false);
	    		startActivity(intent);
	    		
	    		ConfigOperateGuide.this.finish();
	    	}
	    	return true;
		}
	    

	@Override
	protected void onResume() {
		super.onResume();

	}
}

	