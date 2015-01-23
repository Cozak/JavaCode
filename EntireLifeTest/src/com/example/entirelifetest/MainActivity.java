package com.example.entirelifetest;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("onCreate","Yohoo");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.i("onStart","Yohoo");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.i("onStop","Yohoo");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("onResume","Yohoo");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i("onRestart","Yohoo");
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.i("onRestoreInstanceState","Yohoo");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i("onPause","Yohoo");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("onDestroy","Yohoo");
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i("onSaveInstanceState","Yohoo");
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