package com.example.myjnitest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class JniTestCe extends ActionBarActivity {
	private Button jbut = null;
	private TextView jtv = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jni_test_ce);
		
		this.jbut = (Button)findViewById(R.id.jni_test_but);
		this.jtv = (TextView)findViewById(R.id.jni_test_tv);
		this.jbut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				JniTestCe.this.jtv.setText(JniTestCe.this.getStr());
			}
			
		});
	}
	
	public native String getStr();
	
	static {
		System.loadLibrary("MyJniTest");
	}
}
