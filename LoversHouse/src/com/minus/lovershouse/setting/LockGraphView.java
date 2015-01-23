package com.minus.lovershouse.setting;

import com.minus.lovershouse.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LockGraphView extends RelativeLayout {

	LayoutInflater mInflater;
	private Context context;
	private TextView gesturepwd_create_text;
	private LockPatternView gesturepwd_pattern;

	public LockGraphView(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	public LockGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}
	
	/**
	 * 初始化界面
	 */
	private void initView() {
		this.mInflater = LayoutInflater.from(context);
		LayoutInflater.from(context).inflate(R.layout.lock_graph_view,
				this);
		gesturepwd_create_text=(TextView)findViewById(R.id.gesturepwd_create_text);
		gesturepwd_pattern=(LockPatternView)findViewById(R.id.gesturepwd_pattern);
		
	}

	
//	public String getHint() {
//		return gesturepwd_create_text.getText().toString();
//	}

	public void setHint(int hint) {
		this.gesturepwd_create_text.setText(hint);
	}
	
	public void setHint(String hint) {
		this.gesturepwd_create_text.setText(hint);
	}
	
	public void setHint(int hint,Animation shake) {
		this.gesturepwd_create_text.setText(hint);
		if(shake!=null)
			gesturepwd_create_text.startAnimation(shake);
	}
	
	public LockPatternView getpattern() {
		return gesturepwd_pattern;
	}

//	public void setPattern(LockPatternView pattern) {
//		this.gesturepwd_create_text.setText(hint);
//	}
	
	
	
}
