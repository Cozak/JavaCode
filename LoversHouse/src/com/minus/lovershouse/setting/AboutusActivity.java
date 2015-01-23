package com.minus.lovershouse.setting;

import com.minus.lovershouse.setting.PrivacyActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.R.id;
import com.minus.lovershouse.R.layout;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.singleton.GlobalApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AboutusActivity extends BroadCast implements OnClickListener {

	private ImageView back;
	private ImageView policy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.aboutus);
		//GlobalApplication.getInstance().addActivity(this);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		back = (ImageView) findViewById(R.id.about_back);
		back.setOnClickListener( this);
		policy= (ImageView) findViewById(R.id.about_policy);
		policy.setOnClickListener( this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.equals(back)){
			finish();
		}else if(v.equals(policy)){
			Intent intent = new Intent();
		    intent.setClass(AboutusActivity.this,PrivacyActivity.class);
		    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		    startActivity(intent);
		}
	}
	
	

}
