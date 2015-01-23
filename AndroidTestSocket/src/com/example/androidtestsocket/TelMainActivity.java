package com.example.androidtestsocket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TelMainActivity extends ActionBarActivity {
	private Socket sK = null;
	private TextView tv = null;
	private Button bT = null;
	private String str = "Oh";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tel_main);
		tv = (TextView)findViewById(R.id.socketclient_textview);
		bT = (Button)findViewById(R.id.socketclient_button);
		bT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("------->", "I Click!!!!!!!!");
				new Thread() {
					@Override
					public void run() {
						try {
							sK = new Socket("172.18.32.64", 4783);
							Log.i("----------->","HoHOhOhOhOhO");
							InputStream iS = sK.getInputStream();
							/*try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							//byte[] brr = new byte[iS.available()];
							byte[] brr = new byte[100];
							/*if (iS.available() == 0) {
								Log.i("^^^^^^_____>", "Nothing...........");
							}*/
							iS.read(brr);
							str = new String(brr);
							Handler hd = new Handler(Looper.getMainLooper());
							hd.sendMessage(Message.obtain(hd, new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									tv.setText(str);
								}
								
							}));
							
							
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							Log.i("------->", "SHIT");
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							Log.i("------->", "SLOW");
							e.printStackTrace();
						} finally {
							if (sK != null) {
								try {
									sK.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} finally {
									sK = null;
								}
							}
						}
					}
				}.start();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tel_main, menu);
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
