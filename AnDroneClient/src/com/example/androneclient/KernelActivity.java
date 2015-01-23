package com.example.androneclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class KernelActivity extends Activity {
	private static KernelActivity THIS = null;
	private Button but_Mode = null;
	private Button but_Switcher = null;
	private Button but_left = null;
	private Button but_right = null;
	private Button but_front = null;
	private Button but_back = null;
	private Button but_up = null;
	private Button but_down = null;
	private Button but_hover = null;
	private Handler TaskHandler = null;
	
	private OnTouchListener kernelListener = null;
	// Statement Feedback
	private TextView text_State = null;

	private View popupR = null;
//	private PopupWindow popup = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kernel);
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		ModeFragment fg = new ModeFragment();
//		getFragmentManager()
//				.beginTransaction().replace(R.id.main_layout, fg)
//				.commit();
	}

	private void init() throws Exception {
		KernelActivity.THIS = this;
		// launch the task rounder
		// this.TaskHandler = KernelTask::getInstance();
		// this.TaskHandler = NetWorkLayer.getNetHandler();
		NetWorkLayer.getInstance(); // launch Net
		this.kernelListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				try {
					return KernelActivity.this
							.commandSender(KernelActivity.THIS.convertIdToOrd(v
									.getId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}
		};
		
		// Popwindow Selection
		this.popupR = this.getLayoutInflater().inflate(R.layout.activity_mode_selection, null);
		final PopupWindow popup = new PopupWindow(this.popupR, 300, 300);
		this.popupR.findViewById(R.id.radio_but_conrtoller).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// send request and get the controller-Authority
				KernelActivity.this.TaskHandler.sendEmptyMessage(200);
				popup.dismiss();
			}
		});
		this.popupR.findViewById(R.id.radio_but_watcher).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// send mode change but just abort
				popup.dismiss();
			}
		});
		
		// Mode Selection and Connection
		this.but_Mode = (Button) findViewById(R.id.but_mode);
		this.but_Mode.setOnClickListener(new OnClickListener() { // switch the
																	// mode
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// launch a fragment to select mode
						if (!popup.isShowing()) {
							 popup.showAsDropDown(v);
							//popup.showAtLocation(KernelActivity.this.but_Mode, Gravity.CENTER, 0, 0);
						} else {
							popup.dismiss(); // cancel
						}
					}
				});

		// Switcher
		this.but_Switcher = (Button) findViewById(R.id.but_Switcher);
		this.but_Switcher.setText(R.string.Str_Connect);
		this.but_Switcher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (((Button) v).getText().equals(
						KernelActivity.this.getResources().getString(
								R.string.Str_takoff))) {
					// send take off task
					KernelActivity.this.TaskHandler.sendEmptyMessage(100);
				} else if (((Button) v).getText().equals(
						KernelActivity.this.getResources().getString(
								R.string.Str_land))) {
					// send land task
					KernelActivity.this.TaskHandler.sendEmptyMessage(101);
				} else if (((Button) v).getText().equals(
						KernelActivity.this.getResources().getString(
								R.string.Str_Connect))) {
					// connect
					Intent intent = new Intent(KernelActivity.this,
							SocketBulidUp.class);
					startActivity(intent);
					/*
					 * if (NetWorkLayer.ifNetOk()) { // test
					 * KernelActivity.this.changeBasedOnNetState(true); } else {
					 * KernelActivity.this.changeBasedOnNetState(false); }
					 */
				} else if (((Button) v).getText().equals(
						KernelActivity.this.getResources().getString(
								R.string.Str_watchModelOn))) {
					// open video and map
					// change the statement
					KernelActivity.this.openVedio();
					KernelActivity.this.openMap();
				} else if (((Button) v).getText().equals(
						KernelActivity.this.getResources().getString(
								R.string.Str_watchModelOff))) {
					// stop video and map
					// change the statement
					KernelActivity.this.stopVedio();
					KernelActivity.this.closeMap();
				}

			}
		});
		this.but_left = (Button) findViewById(R.id.but_left);
		this.but_left.setOnTouchListener(kernelListener);
		this.but_right = (Button) findViewById(R.id.but_right);
		this.but_right.setOnTouchListener(kernelListener);
		this.but_front = (Button) findViewById(R.id.but_front);
		this.but_front.setOnTouchListener(kernelListener);
		this.but_back = (Button) findViewById(R.id.but_back);
		this.but_back.setOnTouchListener(kernelListener);
		this.but_up = (Button) findViewById(R.id.but_up);
		this.but_up.setOnTouchListener(kernelListener);
		this.but_down = (Button) findViewById(R.id.but_down);
		this.but_down.setOnTouchListener(kernelListener);
		this.but_hover = (Button) findViewById(R.id.but_emergency_stop);
		this.but_hover.setOnTouchListener(kernelListener);
		// disable all button except but_switcher
		this.DisableButCL();
		this.but_Mode.setEnabled(false);
		// Statement Dialog
		this.text_State = (TextView) findViewById(R.id.State_feedback);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kernel, menu);
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

	@Override
	protected void onDestroy() {
		if (this.TaskHandler != null) {
			this.TaskHandler = null;
			try {
				NetWorkLayer.NetClose();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// KernelTaskCenter.KernelClose(); // close the kernel center
		}
		super.onDestroy();
	}

	/**
	 * Vedio Control
	 */
	private void openVedio() {

	}

	private void stopVedio() {

	}

	/**
	 * Map Control
	 */
	private void openMap() {

	}

	private void closeMap() {

	}

	private void EnableButCL() {
		// this.but_Switcher.setText(R.string.Str_land);
		this.but_left.setEnabled(true);
		this.but_right.setEnabled(true);
		this.but_front.setEnabled(true);
		this.but_back.setEnabled(true);
		this.but_up.setEnabled(true);
		this.but_down.setEnabled(true);
		this.but_hover.setEnabled(true);
	}

	private void DisableButCL() {
		// this.but_Switcher.setText(R.string.Str_takoff);
		this.but_left.setEnabled(false);
		this.but_right.setEnabled(false);
		this.but_front.setEnabled(false);
		this.but_back.setEnabled(false);
		this.but_up.setEnabled(false);
		this.but_down.setEnabled(false);
		this.but_hover.setEnabled(false);
	}

	/**
	 * Change the statement of control table
	 * 
	 * @param command
	 */
	private void changeBasedOnNetState(int command) {

		if (command == 0) { // lost connection
			this.but_Mode.setEnabled(false); // no mode-selection before
												// connection
			this.DisableButCL(); // disable all control-button
			this.but_Switcher.setEnabled(true);
			this.but_Switcher.setText(R.string.Str_Connect);
			Toast.makeText(this, "Disconnect!", Toast.LENGTH_SHORT).show();
		} else if (command == 1) { // connection established
			try {
				this.TaskHandler = NetWorkLayer.getNetHandler();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.but_Mode.setEnabled(true); // enable the mode-selection
			this.but_Switcher.setText(R.string.tip_choose_mode);
			Toast.makeText(this, "Server Connected!", Toast.LENGTH_SHORT)
					.show();
		} else if (command == 2) { // switch to controller mode
			this.EnableButCL();
			this.but_Switcher.setEnabled(true);
			this.but_Switcher.setText(R.string.Str_takoff);
		} else if (command == 3) { // switch to watch mode
			this.DisableButCL();
			this.but_Switcher.setEnabled(true);
			this.but_Switcher.setText(R.string.Str_watchModelOn);
		}
	}

	private void landSuccess() {
		this.DisableButCL();
		this.but_Switcher.setText(R.string.Str_takoff);
	}

	/**
	 * Each button related to a unique ord
	 * 
	 * @param ID
	 * @return
	 */
	private int convertIdToOrd(int ID) {
		switch (ID) {
		case R.id.but_left: {
			return 50;
		}
		case R.id.but_right: {
			return 51;
		}
		case R.id.but_front: {
			return 52;
		}
		case R.id.but_back: {
			return 53;
		}
		case R.id.but_up: {
			return 54;
		}
		case R.id.but_down: {
			return 55;
		}
		case R.id.but_emergency_stop: {
			return 77;
		}
		}
		return -1;
	}

	private boolean commandSender(int ord) throws Exception {
		switch (ord) {
		case 50: {
			Log.i("EXEC", "but_left pressing");
			break;
		}
		case 51: {
			Log.i("EXEC", "but_right pressing");
			break;
		}
		case 52: {
			Log.i("EXEC", "but_front pressing");
			break;
		}
		case 53: {
			Log.i("EXEC", "but_back pressing");
			break;
		}
		case 54: {
			Log.i("EXEC", "but_up pressing");
			break;
		}
		case 55: {
			Log.i("EXEC", "but_down pressing");
			break;
		}
		case 77: {
			Log.i("EXEC", "but_hover pressing");
			break;
		}
		default: {
			Toast.makeText(KernelActivity.this, R.string.Str_Orderfailure,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		}
		if (this.TaskHandler == null) {
			Toast.makeText(KernelActivity.this, "NULL NetWork LOOPER",
					Toast.LENGTH_SHORT).show();
			this.TaskHandler = NetWorkLayer.getNetHandler();
			Toast.makeText(KernelActivity.this, "Rebuild NetWork LOOPER",
					Toast.LENGTH_SHORT).show();
		} else {
			this.TaskHandler.sendEmptyMessage(ord);
			Log.i("EXEC", "Press Order Sent");
		}
		return true;
	}

	/**
	 * Update the Main Thread UI
	 * 
	 * @author ZAK
	 *
	 */
	public static class UpdateTask implements Runnable {
		private int taskOrd = -1;

		public UpdateTask(int ord) {
			// TODO Auto-generated constructor stub
			this.taskOrd = ord;
		}

		@Override
		public void run() {
			Log.i("EXEC", "Get Update Mission");
			switch (this.taskOrd) {
			case 0: { // lost connection
				KernelActivity.THIS.changeBasedOnNetState(0);
				Log.i("EXEC", "Lost Connection");
				break;
			}
			case 1: { // connected
				KernelActivity.THIS.changeBasedOnNetState(1);
				Log.i("EXEC", "Connection Built UP");
				break;
			}
			case 2: { // switch to controller mode
				KernelActivity.THIS.changeBasedOnNetState(2);
				Log.i("EXEC", "Switch To Controller Mode");
				break;
			}
			case 3: { // switch to watch model
				KernelActivity.THIS.changeBasedOnNetState(3);
				Log.i("EXEC", "Switch To Watcher Mode");
				break;
			}
			case 22: {
				// Flyer has taken off
				KernelActivity.THIS.EnableButCL();
				Log.i("EXEC", "Take Off Success");
				break;
			}
			case 23: { // Flyer landed
				KernelActivity.THIS.landSuccess();
				;
				Log.i("EXCE", "Landed");
				break;
			}
			case 44: { // report statement
				KernelActivity.THIS.text_State.setText("Moving");
				Log.i("EXEC", "Moving");
				break;
			}
			case 45: { // report position
				KernelActivity.THIS.text_State.setText("Hovering");
				Log.i("EXEC", "Hovering");
				break;
			}
			default: {
				// test
				// Toast.makeText(KernelActivity.THIS, "Send Succeed",
				// Toast.LENGTH_SHORT).show();
			}
			}
		}
	}
}
