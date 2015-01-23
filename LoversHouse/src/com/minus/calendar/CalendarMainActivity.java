package com.minus.calendar;

import java.util.ArrayList;
import java.util.List;

import com.minus.lovershouse.R;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.sql_interface.Database;
import com.minus.table.CalendarTable;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.CalendarHandler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CalendarMainActivity extends BroadCast {
	
	public final static int HANDLE_CALENDAR_NEW_MESSAGE = 0x01;
	public final static int HANDLE_CALENDAR_PROMPT = 0x02;
	
	public final static String gTogetherDayId = "0000-00-00-00:00:00";
	public final static String gWifeBirthdayId = "0000-00-00-11:11:11";
	public final static String gHusbandBirthdayId = "0000-00-00-22:22:22";
	public final static String gDefaultMemoDate = "1000-01-01-00:00:00";
	public final static String gMinMemoDate = "1900-01-01-00:00:00";
	
	public static boolean isValidMemoDate(String memoDate) {
		return CommonFunction.calculateDay(gMinMemoDate, memoDate) >= 0;
	}
	
	public static boolean isCalendarMainActivityRunning = false;
	public static boolean isSysCalendarFirstTimeLoad = true;
	
	private ImageView mBackBtn;
	private ImageView mAddBtn;

	private LinearLayout mBody;
	
	private List<CalendarItem> mItems;

	private MyReceiver mReceiver;
	
	private SharedPreferences mPreferences;
	private Database mDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_main);

		isCalendarMainActivityRunning = true;

		if (mReceiver == null)
			mReceiver = new MyReceiver();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Protocol.ACTION_CALENDAR);
		filter.addAction(Protocol.ACTION_CALENDAR_ALL_LOADED);
		this.registerReceiver(mReceiver, filter);

		mPreferences = getSharedPreferences("LoverHouse_Calendar", Activity.MODE_PRIVATE);
        /*SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString("CalendarLastModifyTime", "");
		editor.commit();*/
		mDB = Database.getInstance(this);
		
		initTopView();
		initBodyView();
		
		checkAndSynchronize();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			FrameLayout layout = (FrameLayout) findViewById(R.id.calendar_main_frameLayout);
			if (layout.getChildCount() == 1)
				this.mBackBtn.performClick();
			else {
				layout.removeViewAt(1);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.refreshBodyView();
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCalendarMainActivityRunning = false;
		
		if (mReceiver != null) {
			this.unregisterReceiver(mReceiver);
			mReceiver = null;
		}
		this.mAddBtn = null;
		this.mBackBtn = null;
		this.mBody = null;
		this.mDB = null;
		this.mItems = null;
		this.mPreferences = null;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshBodyView();
	}
	
	protected void removeAllCalendar() {
		List<CalendarTable> items = mDB.getCalendarTables();
		for (int i = 0; i < items.size(); ++i) {
			CalendarHandler.getInstance().removeCalendar(items.get(i).getInitDate());
		}
		mDB.clearAllCalendar();
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString("CalendarLastModifyTime", "");
		editor.commit();
	}
	
	//保证纪念日列表有系统纪念日
	protected void confirmSysCalendar(List<CalendarTable> items) {
		String someDate = "1900-01-01-00:00:00";
		//Log.d("my_calendar", "confirmDatabaseContainSysCalendar() List<CalendarTable>.size()=" + items.size());
		
		CalendarTable togetherDay = null;
		CalendarTable wifeBirthday = null;
		CalendarTable husbandBirthday = null;
		
		for (int i = 0; i < items.size(); ++i) {
			if (items.get(i).getInitDate().equals(gTogetherDayId))
				togetherDay = items.get(i);
			if (items.get(i).getInitDate().equals(gWifeBirthdayId))
				wifeBirthday = items.get(i);
			if (items.get(i).getInitDate().equals(gHusbandBirthdayId))
				husbandBirthday = items.get(i);
		}
		
		if (togetherDay == null)
			items.add(0, new CalendarTable(gTogetherDayId, someDate, gDefaultMemoDate, 
					"" + (char) 0x01, "在一起的日子"));
		if (wifeBirthday == null)
			items.add(1, new CalendarTable(gWifeBirthdayId, someDate, gDefaultMemoDate, 
					"" + (char) 0x04, "老婆的生日"));
		if (husbandBirthday == null)
			items.add(2, new CalendarTable(gHusbandBirthdayId, someDate, gDefaultMemoDate, 
					"" + (char) 0x04, "老公的生日"));
		
		if (isSysCalendarFirstTimeLoad == true 
				&& AsynSocket.getInstance().isConnected() 
				&& SelfInfo.getInstance().isOnline()) {
			isSysCalendarFirstTimeLoad = false;
			if (togetherDay == null) {
				mDB.addCalendar(gTogetherDayId, someDate, 
						gDefaultMemoDate, "" + (char) 0x01, "在一起的日子");
				CalendarHandler.getInstance().addCalendar(gTogetherDayId, someDate, 
						gDefaultMemoDate, "" + (char) 0x01, "在一起的日子");
			}
			if (wifeBirthday == null) {
				mDB.addCalendar(gWifeBirthdayId, someDate, 
						gDefaultMemoDate, "" + (char) 0x04, "老婆的生日");
				CalendarHandler.getInstance().addCalendar(gWifeBirthdayId, someDate, 
						gDefaultMemoDate, "" + (char) 0x04, "老婆的生日");
			}
			if (husbandBirthday == null) {
				mDB.addCalendar(gHusbandBirthdayId, someDate, 
						gDefaultMemoDate, "" + (char) 0x04, "老公的生日");
				CalendarHandler.getInstance().addCalendar(gHusbandBirthdayId, someDate, 
						gDefaultMemoDate, "" + (char) 0x04, "老公的生日");
			}
		}
	}
	
	protected void refreshBodyView() {
		List<CalendarTable> items = mDB.getCalendarTables();
		confirmSysCalendar(items);
		//Log.d("my_calendar", "refreshBodyView() List<CalendarTable>.size()=" + items.size());
		
		mItems.clear();
		mItems.add(new CalendarSysTogether(this, items.get(0)));
		mItems.add(new CalendarSysBirthday(this, items.get(1)));
		mItems.add(new CalendarSysBirthday(this, items.get(2)));
		for (int i = 3; i < items.size(); ++i) {
			CalendarItem item = new CalendarItem(this, items.get(i));
			mItems.add(item);
		}
		
		mBody.removeAllViews();
		for (int i = 0; i < mItems.size(); ++i) {
			mBody.addView(mItems.get(i).getPreview());
		}
	}

	private void initTopView() {
		mBackBtn = (ImageView) findViewById(R.id.calendar_main_backBtn);
		mAddBtn = (ImageView) findViewById(R.id.calendar_main_addBtn);
		
		mBackBtn.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		mAddBtn.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
                intent.setClass(CalendarMainActivity.this, CalendarAddActivity.class);
                startActivity(intent);
			}
		});
	}

	private void initBodyView() {
		mItems = new ArrayList<CalendarItem>();
		mBody = (LinearLayout) this.findViewById(R.id.calendar_main_body);
		//refreshBodyView();
	}
	
	private void checkAndSynchronize() {
		List<CalendarTable> unHandleCalendarTables = mDB.getWaitForHandleCalendar();
		for (int i = 0; i < unHandleCalendarTables.size(); ++i) {
			CalendarTable item = unHandleCalendarTables.get(i);
			if (item.getServerState().equals("" + Protocol.WaitForServerComfirmAdd)) {
				CalendarHandler.getInstance().addCalendar(item.getInitDate(), item.getEditDate(), 
						item.getMemoDate(), item.getPromptPolicy(), item.getTitle());;
			}
			else if (item.getServerState().equals("" + Protocol.WaitForServerComfirmModify)) {
				CalendarHandler.getInstance().modifyCalendar(item.getInitDate(), item.getEditDate(), 
						item.getMemoDate(), item.getPromptPolicy(), item.getTitle());
			}
			else if (item.getServerState().equals("" + Protocol.WaitForServerComfirmRemove)) {
				CalendarHandler.getInstance().removeCalendar(item.getInitDate());
			}
		}
		
		// Send message to server. Synchronize data in the instance of class MyReciever.
		CalendarHandler.getInstance().getLastModifyTimeRead();
	}

	
	//------------------------------------------------------------------------------
	
	private enum BackGroundState {
		DEFAULT, OMIT_LAST_MODIFY_TIME_READ_ONCE
	}
	private class MyReceiver extends BroadcastReceiver {
		private BackGroundState backGroundState = BackGroundState.DEFAULT;
		private String mLastModifyRead;
		
		@Override
		public void onReceive(Context context, Intent intent) {	
			String action = intent.getAction();
//			Log.v("calendar","@@##on receiver " + action);
			
			if(Protocol.ACTION_CALENDAR_ALL_LOADED.equals(action)) {
				refreshBodyView();
				
		        SharedPreferences.Editor editor = mPreferences.edit();
				editor.putString("CalendarLastModifyTime", mLastModifyRead);
				editor.commit();
			}
			
			if (Protocol.ACTION_CALENDAR.equals(action)) {
				char reason = intent.getCharExtra("PROTOCOL_FLAG", '\0');

				if (reason == Protocol.RETURN_CALENDAR_LAST_MODIFY_TIME_WRITE) {
			        SharedPreferences.Editor editor = mPreferences.edit();
					editor.putString("CalendarLastModifyTime", intent.getStringExtra("PROTOCOL_CONTENT"));
					editor.commit();
				}
				else if (reason == Protocol.RETURN_CALENDAR_LAST_MODIFY_TIME_READ) {
					String client = mPreferences.getString("CalendarLastModifyTime", "");
					String server = intent.getStringExtra("PROTOCOL_CONTENT");
					mLastModifyRead = server;
					if (backGroundState == BackGroundState.OMIT_LAST_MODIFY_TIME_READ_ONCE) {
						backGroundState = BackGroundState.DEFAULT;
				        SharedPreferences.Editor editor = mPreferences.edit();
						editor.putString("CalendarLastModifyTime", server);
						editor.commit();
						return;
					}
					if (client == null || client.equals("")) {
						client = "0000-00-00-00:00:00";
					}
					if (CommonFunction.compareTime(client, server) < 0) {
						mDB.clearAllCalendar();
						CalendarHandler.getInstance().loadAllCalendar();
					}
				}
				else if (reason == Protocol.RECEIVE_CALENDAR_REMOVED || 
						reason == Protocol.RECEIVE_CALENDAR_UPDATED || 
						reason == Protocol.RETURN_CALENDAR) {
					refreshBodyView();
					backGroundState = BackGroundState.OMIT_LAST_MODIFY_TIME_READ_ONCE;
					CalendarHandler.getInstance().getLastModifyTimeRead();
				}
				else if (reason == Protocol.ADD_CALENDAR_FAIL) {
					Toast.makeText(getApplicationContext(), "添加纪念日失败", Toast.LENGTH_SHORT).show();
					refreshBodyView();
				}
				else if (reason == Protocol.MODIFY_CALENDAR_FAIL) {
					Toast.makeText(getApplicationContext(), "修改纪念日失败", Toast.LENGTH_SHORT).show();
					refreshBodyView();
				}
				else if (reason == Protocol.REMOVE_CALENDAR_FAIL) {
					Toast.makeText(getApplicationContext(), "删除纪念日失败", Toast.LENGTH_SHORT).show();
					refreshBodyView();
				}
			}
		}
	}
	
}
