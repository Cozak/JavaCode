package com.minus.lovershouse.setting;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.minus.lovershouse.R;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.singleton.GlobalApplication;

public class HelpActivity extends BroadCast  implements OnClickListener {

	private ImageView back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		//GlobalApplication.getInstance().addActivity(this);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		back = (ImageView) findViewById(R.id.help_back);
		back.setOnClickListener( this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.equals(back)){
			finish();
		}
	}
}
