package com.minus.xsocket.handler;

import java.io.UnsupportedEncodingException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.minus.calendar.CalendarMainActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.sql_interface.Database;
import com.minus.table.CalendarTable;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.util.NetDataTypeTransform;


public class CalendarHandler {

	private CalendarHandler() {}
	public static CalendarHandler handler = new CalendarHandler();
	public static CalendarHandler getInstance() {
		return handler;
	}
	
	public String setLength(int len) {
	    String  mL = String.format("%04d", len);
//	    Log.v("comm ml", mL);
	    return mL;
	}
	
	private int getIntFromByte(byte[] str ,int len) {
		int total = 0;
		for (int i = 0; i < len; i++) {
			total += (int)(str[i] - '0') * Math.pow(10.0, len - i - 1);
		}
		return total;
	}
	
	//part1 process response 
	public void process(byte [] str) {
		char type = (char) str[3];
	    Log.d("calendar","pocess type="+ type);
	    switch (type) {
	    case Protocol.RETURN_CALENDAR:
	    	processOneCalendar(str);
	    	break;
	    
	    case Protocol.ADD_CALENDAR_SUCC:
	    case Protocol.ADD_CALENDAR_FAIL:
	    	processAddCalendar(str);
	    	break;

	    case Protocol.MODIFY_CALENDAR_SUCC:
	    case Protocol.MODIFY_CALENDAR_FAIL:
	    	processModifyCalendar(str);
	    	break;

	    case Protocol.REMOVE_CALENDAR_SUCC:
	    case Protocol.REMOVE_CALENDAR_FAIL:
	    	processRemoveCalendar(str);
	    	break;

	    case Protocol.RETURN_CALENDAR_TIME_LIST:
	    	processCalendarTimeList(str);
	    	break;

	    case Protocol.RETURN_CALENDAR_LAST_MODIFY_TIME_READ:
	    	processLastModifyTimeRead(str);
	    	break;
	    	
	    case Protocol.RETURN_CALENDAR_LAST_MODIFY_TIME_WRITE:
	    	processLastModifyTimeWrite(str);
	    	break;
	    	
	    case Protocol.RECEIVE_CALENDAR_REMOVED:
	    	processReceiveCalendarRemoved(str);
	    	break;
	    	
	    case Protocol.RECEIVE_CALENDAR_UPDATED:
	    	processOneCalendar(str);
	    	break;
	    	
	    default:
	    	break;
	    }
	}

	/*
	private void processReceiveCalendarUpdated(byte[] str) {
		NetDataTypeTransform mND = new NetDataTypeTransform();
	    String mess1 = mND.ByteArraytoString(str, str.length);
	    String mess = mess1.substring(Protocol.HEAD_LEN);
	    
	    String[] list = mess.split(" ");
	    String initDate = list[0];
	    String editDate = list[1];
	    String memoDate = list[2];
	    String promptPolicy = list[4];
	    String title = list[7];
	    
		Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
		.addCalendar(initDate, editDate, memoDate, promptPolicy, title);
		Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
		.modifyCalendarServerState(initDate, "" + Protocol.SuccessFromServer);
		
		Intent intent = new Intent(Protocol.ACTION_CALENDAR);
		intent.putExtra("PROTOCOL_FLAG", (char) str[3]);
		intent.putExtra("PROTOCOL_CONTENT", list);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}
	*/
	
	private void processReceiveCalendarRemoved(byte[] str) {
		NetDataTypeTransform mND = new NetDataTypeTransform();
	    String mess1 = mND.ByteArraytoString(str, str.length);
	    String mess = mess1.substring(Protocol.HEAD_LEN);
	    
	    String[] list = mess.split(" ");
	    String initDate = list[0];

    	Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
    	.modifyCalendarServerState(initDate, "" + Protocol.WaitForServerComfirmRemove);
    	Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
    	.removeCalendar(initDate);

		Intent intent = new Intent(Protocol.ACTION_CALENDAR);
		intent.putExtra("PROTOCOL_FLAG", (char) str[3]);
		intent.putExtra("PROTOCOL_CONTENT", list);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}

	private void processLastModifyTimeRead(byte[] str) {
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String mess1 = mND.ByteArraytoString(str, str.length);
		String mess = mess1.substring(Protocol.HEAD_LEN);
		
		Intent intent = new Intent(Protocol.ACTION_CALENDAR);
		intent.putExtra("PROTOCOL_FLAG", Protocol.RETURN_CALENDAR_LAST_MODIFY_TIME_READ);
		intent.putExtra("PROTOCOL_CONTENT", mess);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}
	
	private void processLastModifyTimeWrite(byte[] str) {
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String mess1 = mND.ByteArraytoString(str, str.length);
		String mess = mess1.substring(Protocol.HEAD_LEN);
		
		Intent intent = new Intent(Protocol.ACTION_CALENDAR);
		intent.putExtra("PROTOCOL_FLAG", Protocol.RETURN_CALENDAR_LAST_MODIFY_TIME_WRITE);
		intent.putExtra("PROTOCOL_CONTENT", mess);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}
	
	//part2,process response 广播到到对应的界面
	private void processToAll( byte[] str) {
		NetDataTypeTransform mND = new  NetDataTypeTransform();
		String mess = mND.ByteArraytoString(str, str.length);
		Intent intent = new Intent(Protocol.ACTION_CALENDAR);
		intent.putExtra(Protocol.EXTRA_DATA, mess);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}

	private void processOneCalendar(byte[] str) {
		NetDataTypeTransform mND = new NetDataTypeTransform();
	    String mess1 = mND.ByteArraytoString(str, str.length);
	    String mess = mess1.substring(Protocol.HEAD_LEN);
	    
	    String[] list = mess.split(" ");
	    String initDate = list[0];
	    String editDate = list[1];
	    String memoDate = list[2];
	    String promptPolicy = list[4];
	    String title = list[7];
	    
		Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
		.addCalendar(initDate, editDate, memoDate, promptPolicy, title);
		Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
		.modifyCalendarServerState(initDate, "" + Protocol.SuccessFromServer);
		
		Intent intent = new Intent(Protocol.ACTION_CALENDAR);
		intent.putExtra("PROTOCOL_FLAG", (char) str[3]);
		intent.putExtra("PROTOCOL_CONTENT", list);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}
	
	private void processAddCalendar(byte[] str) {
	    NetDataTypeTransform mND = new  NetDataTypeTransform();
	    String mess1 = mND.ByteArraytoString(str, str.length);
	    String mess = mess1.substring(Protocol.HEAD_LEN);
	
	    String[] list = mess.split(" ");
	    String initDate = list[0];
	    
	    char type = (char) str[3];
	    if (type == Protocol.ADD_CALENDAR_SUCC) {
	    	Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
	    	.modifyCalendarServerState(initDate, "" + Protocol.SuccessFromServer);
			this.getLastModifyTimeWrite(); //更改完毕，和服务器同步最后更改时间。
	    }
	    else if (type == Protocol.ADD_CALENDAR_FAIL) {
			Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
			.removeCalendar(initDate);
	    }
	    
		Intent intent = new Intent(Protocol.ACTION_CALENDAR);
		intent.putExtra("PROTOCOL_FLAG", type);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}
	
	private void processModifyCalendar(byte[] str) {
	    NetDataTypeTransform mND = new  NetDataTypeTransform();
	    String mess1 = mND.ByteArraytoString(str, str.length);
	    String mess = mess1.substring(Protocol.HEAD_LEN);
	    
	    String[] list = mess.split(" ");
	    String initDate = list[0];
	    
	    char type = (char) str[3];
	    if (type == Protocol.MODIFY_CALENDAR_SUCC) {
	    	Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
	    	.modifyCalendarServerState(initDate, "" + Protocol.SuccessFromServer);
			this.getLastModifyTimeWrite(); //更改完毕，和服务器同步最后更改时间。
	    }
	    else if (type == Protocol.MODIFY_CALENDAR_FAIL) {
	    	this.loadOneCalendar(initDate);
		}

		Intent intent = new Intent(Protocol.ACTION_CALENDAR);
		intent.putExtra("PROTOCOL_FLAG", type);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}
	
	private void processRemoveCalendar(byte[] str) {
	    NetDataTypeTransform mND = new  NetDataTypeTransform();
	    String mess1 = mND.ByteArraytoString(str, str.length);
	    String mess = mess1.substring(Protocol.HEAD_LEN);
	
	    String[] list = mess.split(" ");
	    String initDate = list[0];
	    
	    char type = (char)str[3];
	    if (type == Protocol.REMOVE_CALENDAR_SUCC) {
	    	Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
	    	.removeCalendar(initDate);
			this.getLastModifyTimeWrite(); //更改完毕，和服务器同步最后更改时间。
	    }
	    if (type == Protocol.REMOVE_CALENDAR_FAIL) {
	    	Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
	    	.modifyCalendarServerState(initDate, "" + Protocol.SuccessFromServer);
	    }
	    
		Intent intent = new Intent(Protocol.ACTION_CALENDAR);
		intent.putExtra("PROTOCOL_FLAG", type);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}
	
	private static boolean TIME_LIST_LOADING;
	private void processCalendarTimeList(byte[] str) {
		if (TIME_LIST_LOADING == false) {
			//偶数次获取日历列表，表示日历加载完毕，发送事件
			Intent intent = new Intent(Protocol.ACTION_CALENDAR_ALL_LOADED);
			GlobalApplication.getInstance().sendBroadcast(intent);
			return;
		}
		TIME_LIST_LOADING = false;
		
		NetDataTypeTransform mND = new NetDataTypeTransform();
	    String mess1 = mND.ByteArraytoString(str, str.length);
	    String mess =mess1.substring(Protocol.HEAD_LEN);
//	    Log.d("calendar", "CalendarHandler::processCalendarTimeList() messageContent=" + mess);
	    
	    String[] list = mess.split(" ");
	    for (int i = 0; i < list.length; i += 2) {
	    	loadOneCalendar(list[i]);
	    }
	    
	    //再获取一次日历列表，表示日历加载完毕
	    this.requestCalendarTimeList();
	}
	
	
	///-----------------------------------------华丽丽的分割线   请求
	public void getLastModifyTimeRead() {
	    String packet = "";
	    int len = 0;
			try {
				len = packet.getBytes("UTF-8").length+1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			 String lenStr = setLength(len);
			 
			 StringBuilder mSB = new StringBuilder();
			    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.CALENDAR_PACKAGE)
			    .append(Protocol.GET_READ_LAST_MODIFY_TIME).append(lenStr).append(packet).append('\0');
			    AsynSocket.getInstance().sendData(mSB.toString());
	}

	public void getLastModifyTimeWrite() {
	    String packet = "";
	    int len = 0;
			try {
				len = packet.getBytes("UTF-8").length+1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			 String lenStr = setLength(len);
			 
			 StringBuilder mSB = new StringBuilder();
			    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.CALENDAR_PACKAGE)
			    .append(Protocol.GET_WRITE_LAST_MODIFY_TIME).append(lenStr).append(packet).append('\0');
			    AsynSocket.getInstance().sendData(mSB.toString());

	}
	
	public void addCalendar(String initDate, String editDate, 
			String memoDate ,String promptPolicy, String title) {
		//包体长度+创建时间+空格+修改日期+空格+纪念日期+空格+日历类型+空格+提醒类型+空格
		//+公历或者农历+空格+是否置顶+空格+标题+’\0’
	    String type = "" + (char) 0x03;
	    if (initDate.equals(CalendarMainActivity.gTogetherDayId))
	    	type = "" + (char) 0x01;
	    else if (initDate.equals(CalendarMainActivity.gWifeBirthdayId) || 
	    		initDate.equals(CalendarMainActivity.gHusbandBirthdayId))
	    	type = "" + (char) 0x02;
	    String gregorian_calendar = String.valueOf(0x01); //公历
	    String top = String.valueOf(0x01);
		String packet = initDate+' '+editDate+' '+memoDate+' '+type+' '+promptPolicy+' '
				+gregorian_calendar+' '+top+' '+title;
		
	    int len = 0;
		try {
			len = packet.getBytes("UTF-8").length+1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.CALENDAR_PACKAGE)
		.append(Protocol.ADD_CALENDAR).append(lenStr).append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}
	
	public void modifyCalendar(String initDate, String editDate, 
			String memoDate, String promptPolicy, String title) {
		//包体长度+创建时间+空格+修改日期+空格+纪念日期+空格+日历类型+空格+提醒类型+空格
		//+公历或者农历+空格+是否置顶+空格+标题+’\0’
	    String type = "" + (char) 0x03;
	    if (initDate.equals(CalendarMainActivity.gTogetherDayId))
	    	type = "" + (char) 0x01;
	    else if (initDate.equals(CalendarMainActivity.gWifeBirthdayId) || 
	    		initDate.equals(CalendarMainActivity.gHusbandBirthdayId))
	    	type = "" + (char) 0x02;
	    String gregorian_calendar = String.valueOf(0x01); //公历
	    String top = String.valueOf(0x01);
		String packet = initDate+' '+editDate+' '+memoDate+' '+type+' '+promptPolicy+' '
				+gregorian_calendar+' '+top+' '+title;
		
	    int len = 0;
	  	try {
	  		len = packet.getBytes("UTF-8").length+1;
	  	} catch (UnsupportedEncodingException e) {
	  		e.printStackTrace();
	  	}
	    String lenStr = setLength(len);
	    StringBuilder mSB = new StringBuilder();
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.CALENDAR_PACKAGE)
	    .append(Protocol.MODIFY_CALENDAR).append(lenStr).append(packet).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());
	}
	
	public void removeCalendar(String initDate) {
	    String packet = initDate;
	    int len = 0;
	  	try {
	  		len = packet.getBytes("UTF-8").length+1;
	  	} catch (UnsupportedEncodingException e) {
	  		e.printStackTrace();
	  	}
	    String lenStr = setLength(len);
	    StringBuilder mSB = new StringBuilder();
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.CALENDAR_PACKAGE)
	    .append(Protocol.REMOVE_CALENDAR).append(lenStr).append(packet).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());
	}
	
	public void loadAllCalendar() {
		TIME_LIST_LOADING = true;
		requestCalendarTimeList();
	}
	
	private void requestCalendarTimeList() {
	    String packet = new String();
	    int len = 0;
	  	try {
	  		len = packet.getBytes("UTF-8").length+1;
	  	} catch (UnsupportedEncodingException e) {
	  		e.printStackTrace();
	  	}
	    String lenStr = setLength(len);
	    StringBuilder mSB = new StringBuilder();
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.CALENDAR_PACKAGE)
	    .append(Protocol.GET_CALENDAR_TIME_LIST).append(lenStr).append(packet).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());
	}
	
	public void loadOneCalendar(String initDate) {
		if (initDate.length() == 0) {
//			Log.d("calendar", "CalendarHandler::loadOneCalendar() initDate.length() == 0");
			return;
		}
	    String packet = initDate;
	    int len = 0;
	  	try {
	  		len = packet.getBytes("UTF-8").length+1;
	  	} catch (UnsupportedEncodingException e) {
	  		e.printStackTrace();
	  	}
	    String lenStr = setLength(len);
	    StringBuilder mSB = new StringBuilder();
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.CALENDAR_PACKAGE)
	    .append(Protocol.GET_CALENDAR).append(lenStr).append(packet).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());
	}

}
