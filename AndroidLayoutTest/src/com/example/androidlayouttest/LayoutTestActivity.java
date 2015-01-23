package com.example.androidlayouttest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

public class LayoutTestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table_layout_test); // A test of layout
		
		this.createPOPUP();
	}
	
	private void createPOPUP() {
		View root = this.getLayoutInflater().inflate(R.layout.layout_popup, null);
		final PopupWindow popup = new PopupWindow(root, 200, 300);
		((Button)findViewById(R.id.ok31)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popup.isShowing()) {
					popup.dismiss();
				} else {
					popup.showAsDropDown(v);
				}
				//popup.showAtLocation(findViewById(R.id.ok31), Gravity.CENTER, 20, 20);
			}
		});
		root.findViewById(R.id.popup_radio_but_01).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(LayoutTestActivity.this, "Male", Toast.LENGTH_SHORT).show();
				popup.dismiss();
				
			}
		});
		root.findViewById(R.id.popup_radio_but_02).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(LayoutTestActivity.this, "Female", Toast.LENGTH_SHORT).show();
				popup.dismiss();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
