package com.minus.xsocket.handler;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.sql_interface.Database;
import com.minus.table.DiaryTable;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.util.NetDataTypeTransform;

public class DiaryPacketHandler {
	
	public String setLength(int len){
	    String  mL = String.format("%04d", len);
//	    Log.v("comm ml", mL);
	    return mL;
	}
	  private int getIntFromByte(byte[] str ,int len)
	  {
	      int total = 0;
	      for (int i = 0; i < len; i++)
	      {
	          total += (int)(str[i] - '0') * Math.pow(10.0, len - i - 1);
	      }
	      return total;
	  }

	//part1 process response 
	 public void process(byte [] str){
	    char type = (char) str[3];
	    switch (type) {
	    case Protocol.ADD_DIARY_SUCC:
	    case Protocol.ADD_DIARY_FAIL:
	    	processAddDiary(str);
	    	break;
	    case Protocol.MODIFY_DIARY_SUCC:
	    case Protocol.MODIFY_DIARY_FAIL:
	    	processModifyDiary(str);
	    	break;
	    case Protocol.REMOVE_DIARY_SUCC:
	    case Protocol.REMOVE_DIARY_FAIL:
	    	 processRemoveDiary(str);
	        break;
	    case Protocol.RETURN_DIARY_READ_LAST_MODIFY_TIME:
        case Protocol.RETURN_DIARY_WRITE_LAST_MODIFY_TIME:
	 
	    	processDiaryReturnLastModifyTime(str);
	    	break;
	    case Protocol.RETURN_DIARY_TIME_LIST:
           processDiaryTimeList(str);
            break;
            
        case Protocol.RETURN_ONE_DIARY:
            processOneDiary(str);
            break;
        
        case Protocol.RETURN_MODIFY_DIARY:
           processOneModifyDiary(str);
            break;
        
        case Protocol.RETURN_REMOVE_DIARY:
            processOneRemoveDiary(str);
            break;
	     
	        default:
	            break;
	    }

	   
	}


	//part2,process response 广播到到对应的界面
	 
	public void processToAll( byte[] str)
	{
		 NetDataTypeTransform mND = new  NetDataTypeTransform();
		    String mess = mND.ByteArraytoString(str, str.length);
			Intent intent = new Intent(Protocol.ACTION_DIARYPACKET);
			intent.putExtra(Protocol.EXTRA_DATA,mess);
			GlobalApplication.getInstance().sendBroadcast(intent);

	}
	
	public void processAddDiary(byte[] str)
	{
	    char type = (char)str[3];
	    String state;
	    NetDataTypeTransform mND = new  NetDataTypeTransform();
	    String mess = mND.ByteArraytoString(str, str.length);
	    if (type == Protocol.ADD_DIARY_SUCC) {
	        state = Protocol.SuccessFromServer+"";
	    }
	    else{
	        state = Protocol.FailFromServer+"";

	    }
	    String iniDate = mess.substring(Protocol.HEAD_LEN);
//	    Log.v("diary","Add Diary inidate " +iniDate );
	    //写入数据库;
	    Database.
	    getInstance(GlobalApplication.getInstance().getApplicationContext()).
	    updateStateFromServer(SelfInfo.getInstance().getAccount(), iniDate, state);
	    Intent intent = new Intent(Protocol.ACTION_DIARYPACKET);
		intent.putExtra(Protocol.EXTRA_DATA,mess);
		GlobalApplication.getInstance().sendBroadcast(intent);
//	    [dict setValue:iniDate forKey:@"IniDate"];
//	    [dict setValue:state forKey:@"AddDiaryState"];
//	    [[NSNotificationCenter defaultCenter]postNotificationName:USNotificationDiaryAdd object:nil userInfo:dict];
	    
	}
public void processModifyDiary(byte[] str)
	{
	    char type = (char)str[3];
	    NetDataTypeTransform mND = new  NetDataTypeTransform();
	    String mess = mND.ByteArraytoString(str, str.length);
	    String state;
	    if (type == Protocol.MODIFY_DIARY_SUCC)
	    {
	        state =Protocol.SuccessFromServer+"" ;
//	        NSLog(@"Modify Diary Succ");
	    }
	    else
	    {
	        state =Protocol.FailFromServer +"";
//	        NSLog(@"Modify Diary Fail");
	    }
	    String iniDate =mess.substring(Protocol.HEAD_LEN);
	    Database.
	    getInstance(GlobalApplication.getInstance().getApplicationContext()).
	    updateStateFromServer(SelfInfo.getInstance().getAccount(), iniDate, state);
 
//	    [dict setValue:iniDate forKey:@"IniDate"];
//	    [dict setValue:state forKey:@"ModifyDiaryState"];
//	    [[NSNotificationCenter defaultCenter]postNotificationName:USNotificationDiaryModify object:nil userInfo:dict];
		Intent intent = new Intent(Protocol.ACTION_DIARYPACKET);
		intent.putExtra(Protocol.EXTRA_DATA,mess);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}
	public void processRemoveDiary(byte[] str)
	{
	    char type = (char)str[3];
	
	    if (type == Protocol.REMOVE_DIARY_SUCC)
	    {
	        NetDataTypeTransform mND = new  NetDataTypeTransform();
		    String mess = mND.ByteArraytoString(str, str.length);
		    String iniDate =mess.substring(Protocol.HEAD_LEN);
		    Database.
		    getInstance(GlobalApplication.getInstance().getApplicationContext()).removeDiary(SelfInfo.getInstance().getAccount(), iniDate);
			Intent intent = new Intent(Protocol.ACTION_DIARYPACKET);
			intent.putExtra(Protocol.EXTRA_DATA,mess);
			GlobalApplication.getInstance().sendBroadcast(intent);
//	        "Remove Diary Succ");
	    }
	    else
	    {
//	      "Remove Diary Fail");
	    }
	}
	
	/**
	 * return lastModifytime ,Get diary last modify time from server
	 * @param str
	 */
	public void processDiaryReturnLastModifyTime(byte[] str)
	{
		 char type = (char)str[3];
		 NetDataTypeTransform mND = new  NetDataTypeTransform();
		 String mess = mND.ByteArraytoString(str, str.length);
        
         GlobalApplication mGA = GlobalApplication.getInstance();
         
		    if (type == Protocol.RETURN_DIARY_READ_LAST_MODIFY_TIME)
		    {

		    	Intent intent = new Intent(Protocol.ACTION_DIARYPACKET_READLASTMODIFYTIME);
				intent.putExtra(Protocol.EXTRA_DATA,mess);
		 		mGA.sendBroadcast(intent);
		 		if(!(mGA.isDiaryNewInit())){
					//说明是刚登陆成功在拉数据
					mGA.setDiaryNewInit(true);
					ControlHandler mCH = new ControlHandler();
					mCH.responseForDiaryLastModifyTime(mess);
		
				}
		    }
		    else
		    {
		    	//Get diary write last modify time from server
		    	 String lastModifyTime =mess.substring(Protocol.HEAD_LEN);
		    	SharedPreferences mSP = mGA
						.getSharedPreferences(SelfInfo.getInstance().getAccount(),
								Activity.MODE_PRIVATE);
		    	 SharedPreferences.Editor mEditor = mSP.edit();
				  mEditor.putString(Protocol.PREFERENCE_DiaryLastModifyTime, 
						  lastModifyTime);
				  mEditor.commit();


		    }
	

				
	 }
	
	public void processDiaryTimeList(byte[] str)
	{
		 processToAll(str);
	
	}
/**
 * handler one diary from server
 * @param str
 */
	public void processOneDiary( byte[] str)
	{  
		NetDataTypeTransform mND = new  NetDataTypeTransform();
	     String mess1 = mND.ByteArraytoString(str, str.length);
	    String mess =mess1.substring(Protocol.HEAD_LEN);
	
	    String[] diaryRecord = mess.split(" ");
	    
	    GlobalApplication mGA = GlobalApplication.getInstance();
	    
	    Database mDB= Database.getInstance(mGA.getApplicationContext());
	    
	    String account = diaryRecord[0];
	    String iniDate = diaryRecord[1];
	    String editDate = diaryRecord[2];
	    String newSymbol = diaryRecord[3];
	    
	    //account is email ,did not contain chinese
	    int beginIndex = account.length() + iniDate.length() + editDate.length() + newSymbol.length() + 8 + 4;
	    
	    byte[] titleLenByte = new byte[4];
	    System.arraycopy(str,beginIndex, titleLenByte, 0, 4);
	    int titleLen = getIntFromByte(titleLenByte,4);
	    byte[] destTitle = new byte[titleLen + 1] ;
	    System.arraycopy(str, beginIndex+4, destTitle, 0, titleLen);
	    destTitle[titleLen] = '\0';
	    String title =mND.ByteArraytoString(destTitle, destTitle.length);
	    
	    beginIndex += (titleLen + 4);
	    byte[] contentLenByte = new byte[4];
	    System.arraycopy(str,beginIndex, contentLenByte, 0, 4);
	    int contentLen =getIntFromByte(contentLenByte,4);
	    byte[] destContent  =new byte[contentLen + 1];
	    System.arraycopy(str, beginIndex+4,destContent, 0, contentLen);
	    destContent[contentLen] = '\0';
	    String  content =mND.ByteArraytoString(destContent, destContent.length); 
	    String state = Protocol.SuccessFromServer + "";
	    int isNew = 0;
	    if(newSymbol.equals("1")){
	    	isNew = 1;
	    }
	   
	     List<String> textArr = new ArrayList<String>();
	     textArr.add(account);
	     textArr.add(iniDate);
	     textArr.add(editDate);
	     textArr.add(title);
	     textArr.add(content);
	     textArr.add(newSymbol);
	     textArr.add(state);
	   //如果数据库有和这条日志的账户和创建时间一样的，说明对方是更新改日志;
	     if (mDB.isExistTheDiaryItemWithAccount(account, iniDate)){
	    	 
	    	 mDB.modifyDiary(account, iniDate, title, content, editDate, state, isNew);
	    	 Intent intent = new Intent(Protocol.ACTION_DIARYPACKET_REFRESH);
				intent.putExtra(Protocol.EXTRA_DATA,(Serializable)textArr);
				mGA.sendBroadcast(intent);
	        
	     }
	     else{
	    	 mDB.addDiary(account,iniDate, title, content, editDate, state,isNew);
	       
	    	 Intent intent = new Intent(Protocol.ACTION_DIARYPACKET_ONERECORD);
				intent.putExtra(Protocol.EXTRA_DATA,(Serializable)textArr);
				mGA.sendBroadcast(intent);
	     }
	     
	     Intent intent = new Intent(Protocol.NotificationDiaryNewMsg);
	
			mGA.sendBroadcast(intent);
	     

	}

	//从服务器上接收到一条修改日志(对方的)
	public  void processOneModifyDiary(byte[] str)
	{
		NetDataTypeTransform mND = new  NetDataTypeTransform();
	     String mess1 = mND.ByteArraytoString(str, str.length);
	    String mess =mess1.substring(Protocol.HEAD_LEN);
	    GlobalApplication mGA = GlobalApplication.getInstance();

	    String[] diaryRecord = mess.split(" ");
	    
	    String account = diaryRecord[0];
	    String iniDate = diaryRecord[1];
	    String editDate = diaryRecord[2];
	    String newSymbol = diaryRecord[3];
	    
//	    "从服务器更新一条对方修改的日志为%@",mess);
	    //account is email ,did not contain chinese
	    int beginIndex = account.length() + iniDate.length() + editDate.length() + newSymbol.length() + 8 + 4;
	    
	    byte[] titleLenByte = new byte[4];
	    System.arraycopy(str,beginIndex, titleLenByte, 0, 4);
	    int titleLen = getIntFromByte(titleLenByte,4);
	    byte[] destTitle = new byte[titleLen + 1] ;
	    System.arraycopy(str, beginIndex+4, destTitle, 0, titleLen);
	    destTitle[titleLen] = '\0';
	    String title =mND.ByteArraytoString(destTitle, destTitle.length);
	    
	    beginIndex += (titleLen + 4);
	    byte[] contentLenByte = new byte[4];
	    System.arraycopy(str,beginIndex, contentLenByte, 0, 4);
	    int contentLen =getIntFromByte(contentLenByte,4);
	    byte[] destContent  =new byte[contentLen + 1];
	    System.arraycopy(str, beginIndex+4,destContent, 0, contentLen);
	    destContent[contentLen] = '\0';
	    String  content =mND.ByteArraytoString(destContent, destContent.length); 
	    String state = Protocol.SuccessFromServer + "";
	    int isNew = 0;
	    if(newSymbol.equals("1")){
	    	isNew = 1;
	    }
	    List<String> textArr = new ArrayList<String>();
	     textArr.add(account);
	     textArr.add(iniDate);
	     textArr.add(editDate);
	     textArr.add(title);
	     textArr.add(content);
	     textArr.add(newSymbol);
	     textArr.add(state);
	    Database.getInstance(mGA.getApplicationContext())
	    .modifyDiary(account,iniDate, title, content, editDate, state,isNew);
	    
	     Intent intent = new Intent(Protocol.ACTION_DIARYPACKET_REFRESH);
	     intent.putExtra(Protocol.EXTRA_DATA,(Serializable)textArr);
	     mGA.sendBroadcast(intent);//对应界面从数据库中更新
	
	    //通知主界面有新日志
	     Intent notifyIntent = new Intent(Protocol.NotificationDiaryNewMsg);
		 mGA.sendBroadcast(notifyIntent);

	}

	//从服务器上接收到删除日志的包
	public void processOneRemoveDiary(byte[] str)
	{
		NetDataTypeTransform mND = new  NetDataTypeTransform();
	     String mess1 = mND.ByteArraytoString(str, str.length);
	    String mess =mess1.substring(Protocol.HEAD_LEN);
	    GlobalApplication mGA = GlobalApplication.getInstance();
//	    "从服务器获取一条对方删除日志为：%@",mess);
	    String[] diaryRecord = mess.split(" ");
	    
	    String account = diaryRecord[0];
	    String iniDate = diaryRecord[1];
	    
	    Database.getInstance(mGA.getApplicationContext())
	    .removeDiary(account, iniDate);
	    Intent intent = new Intent(Protocol.ACTION_DIARYPACKET_REFRESH);
		
	 	mGA.sendBroadcast(intent);//对应界面从数据库中更新
	 	  //通知主界面有新日志
	     Intent notifyIntent = new Intent(Protocol.NotificationDiaryNewMsg);
		 mGA.sendBroadcast(notifyIntent);
	}
	
	
	
	///-----------------------------------------华丽丽的分割线   请求
//	添加日记
public void AddDiary(String initdate,String title,String content ,String editdate)
{
	int titleLen = 0;
	int contentLen = 0;
	try {
		titleLen =title.getBytes("UTF-8").length;
		contentLen =content.getBytes("UTF-8").length;
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	String titleLenStr = this.setLength(titleLen);
    String contentLenStr = this.setLength(contentLen);
	String packet =initdate+' ' +editdate+' '+titleLenStr+title+contentLenStr +content;
	  int len = 0;
		try {
			len = packet.getBytes("UTF-8").length+1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
     String lenStr = setLength(len);
   
    StringBuilder mSB = new StringBuilder();
    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.DIARY_PACKAGE)
    .append(Protocol.ADD_DIARY).append(lenStr).append(packet).append('\0');
    AsynSocket.getInstance().sendData(mSB.toString());
	}

public void ModifyDiary(String initdate,String title ,String content ,String editdate)
{
	int titleLen = 0;
	int contentLen = 0;
	try {
		titleLen =title.getBytes("UTF-8").length;
		contentLen =content.getBytes("UTF-8").length;
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	String titleLenStr = this.setLength(titleLen);
    String contentLenStr = this.setLength(contentLen);
	String packet = initdate+' ' +editdate+' '+titleLenStr+title+contentLenStr +content;
	  int len = 0;
		try {
			len = packet.getBytes("UTF-8").length+1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
   String lenStr = setLength(len);

   StringBuilder mSB = new StringBuilder();
   mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.DIARY_PACKAGE)
   .append(Protocol.MODIFY_DIARY).append(lenStr).append(packet).append('\0');
   AsynSocket.getInstance().sendData(mSB.toString());
	    
   
}
//删除日记   only me 
	public void RemoveDiary(String initdate){
		String packet = initdate;
		  int len = 0;
			try {
				len = packet.getBytes("UTF-8").length+1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	    String lenStr = setLength(len);
	 
	    StringBuilder mSB = new StringBuilder();
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.DIARY_PACKAGE)
	    .append(Protocol.REMOVE_DIARY).append(lenStr).append(packet).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());

	}
	//获取只读的最后修改时间
	 public void getDiaryReadLastModifyTime()
		{
		 int len = 1;
         String lenStr = setLength(len);
			 
	     StringBuilder mSB = new StringBuilder();
			    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.DIARY_PACKAGE)
			    .append(Protocol.GET_DIARY_READ_LAST_MODIFY_TIME).append(lenStr).append('\0');
			    AsynSocket.getInstance().sendData(mSB.toString());
		}
	//获取可写的最后修改时间：
	public void GetDiaryLastModifyTime()
	{

		  int len = 1;
		
	    String lenStr = setLength(len);
	 
	    StringBuilder mSB = new StringBuilder();
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.DIARY_PACKAGE)
	    .append(Protocol.GET_DIARY_WRITE_LAST_MODIFY_TIME).append(lenStr).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());
	}

	
	public void GetDiaryTimeList()
	 {
	
		  int len = 1;
	    String lenStr = setLength(len);
	 
	    StringBuilder mSB = new StringBuilder();
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.DIARY_PACKAGE)
	    .append(Protocol.GET_DIARYLTIMELIST).append(lenStr).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());
	    }
	
	//根据用户帐号和日志创建日期返回该条日志;获取日记
	public void GetDiaryWithAccount(String account,String iniDate)
	{
		String packet = account +' '+iniDate;
		  int len = 0;
			try {
				len = packet.getBytes("UTF-8").length+1;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	    String lenStr = setLength(len);
	 
	    StringBuilder mSB = new StringBuilder();
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.DIARY_PACKAGE)
	    .append(Protocol.GET_DIARY).append(lenStr).append(packet).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());
	
	}
	
	//发送新日记已读的标志
	public void RemoveDiaryNewSymbol(String iniDate)
	{
	    String packet =  iniDate;
	    int len = 0;
		try {
			len = packet.getBytes("UTF-8").length+1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    String lenStr = setLength(len);
    StringBuilder mSB = new StringBuilder();
    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.DIARY_PACKAGE)
    .append(Protocol.DIARY_REMOVE_NEW_SYMBOL).append(lenStr).append(packet).append('\0');
    AsynSocket.getInstance().sendData(mSB.toString());
//	   "更改日志的状态为已读");
	}

	//处理那些待上传，待修改，待删除的日志;
  public void dealWithWaitForAddModifyRemoveDiary()
	{
	  Database db = Database.
			  getInstance(GlobalApplication.getInstance().getApplicationContext());
			  
	    List<DiaryTable> diaryWaitForRemove = 
	    		db.getWaitForRemoveDiary();
	    for (DiaryTable mDT : diaryWaitForRemove)
	    {
	    	RemoveDiary(mDT.getInitdate());
	    }

	    List<DiaryTable> diaryWaitForAdd = db.getWaitForAddDiary();
	    for (DiaryTable mDT : diaryWaitForAdd)
	    {
	        this.AddDiary(mDT.getInitdate(), mDT.getTitle(), mDT.getContent(),mDT.getEditdate());
	        
	    }
	    
	    List<DiaryTable> diaryWaitForModify =db.getWaitForModifyDiary();
	    for(DiaryTable mDT : diaryWaitForModify)
	    {
	    	this.ModifyDiary(mDT.getInitdate(),mDT.getTitle(),mDT.getContent(),mDT.getEditdate());
	       
	    }
	}
	
}