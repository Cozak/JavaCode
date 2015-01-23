package com.minus.xsocket.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.minus.lovershouse.BuildConfig;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.FileUtil;
import com.minus.sql_interface.Database;
import com.minus.table.ChatTable;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.util.NetDataTypeTransform;

public class ChatPacketHandler {

	static ByteArrayOutputStream picData;
	static ByteArrayOutputStream voiceData;

	public String setLength(int len) {
		String mL = String.format("%04d", len);
		return mL;
	}

	private int getIntFromByte(byte[] str, int len) {
		int total = 0;
		for (int i = 0; i < len; i++) {
			total += (int) (str[i] - '0') * Math.pow(10.0, len - i - 1);
		}
		return total;
	}

	private int getIntFromChar(String str, int len) {
		int total = 0;
		for (int i = 0; i < len; i++) {
			total += (int) (str.charAt(i) - '0') * Math.pow(10.0, len - i - 1);
		}
		return total;
	}

	// 发送文字信息;
	public void sendTextWithAccount(String initDate, String content) {
		String packet = initDate + ' ' + content;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lenStr = setLength(len);

		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE).append(Protocol.CHAT_SEND_TEXT)
				.append(lenStr).append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	// 发送信息已读的状态;
	public void sendMsgReadWithAccount(String beginTime, String endTime) {
		if (BuildConfig.DEBUG)
			Log.d("chatText", "ChatPacketHandler::sendMsgReadWithAccount(0x0c): beginTime = " + beginTime + ", endTime" + endTime);
		String packet = beginTime + ' ' + endTime;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_SEND_MSG_READ).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	// 向服务器发送收到确认已读状态的信息
	public void sendComfirmMsgReadWithAccount() {
		int len = 1;

		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_COMFIRM_READ).append(lenStr).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	// 获取最后一条记录(可能是)的时间
	public void getLastMessageDateWithAccount() {
		int len = 1;

		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_GET_LAST_MESSAGE_TIME).append(lenStr)
				.append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	// 获取EndTimePoint之前的聊天记录，服务器一次只返回15条记录
	public void getMessageRecordWithAccount(String endTimePoint) {

		String packet = endTimePoint;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_GET_MESSAGE_RECORD).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	// 向服务器拉取这个时间段的信息；服务器是把gai时间段的所有未读信息都返回；
	public void getMessageWithAccount(String beginTime, String endTime) {
		if (BuildConfig.DEBUG)
			Log.d("chatText", "ChatPacketHandler::getMessageWithAccount(0x09): beginTime = " + beginTime + ", endTime" + endTime);
		String packet = beginTime + ' ' + endTime;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_GET_MESSAGE).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	// 删除聊天记录
	public void removeMessageWithAccount(String beginTime, String endTime) {
		String packet = beginTime + ' ' + endTime;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_REMOVE_MESSAGE).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	// 把聊天图片转存相册中
	public void savePicToAlbumWithPicDate(String picDate, String newDate) {
		String packet = picDate + " " + newDate;
		int len = packet.length() + 1;
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_SAVE_PIC_TO_ALBUM).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	// 发送语音第一部分
	public void sendVoiceFirstSectionWithAccount(String date, int voiceLen,
			byte[] voiceData) {
		String voiceLength = String.format("%d", voiceLen);
		int len = 0;
		len = date.length() + voiceLength.length() + voiceData.length + 3;
		// 2个空格，一个结束符;
		String lenStr = setLength(len);

		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_VOICE_FIRST_DATA).append(lenStr)
				.append(date).append(' ').append(voiceLength).append(' ');
		byte[] destAray = null;
		byte[] packet = null;
		try {
			packet = mSB.toString().getBytes("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {

			bos.write(packet);
			bos.write(voiceData);
			bos.write('\0');
			bos.flush();
			destAray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
			}
		}
		AsynSocket.getInstance().sendImgData(destAray);

	}

	// 发送语音接下来部分
	public void sendVoiceAppendSectionWithAccount(String date, byte[] voiceData) {
		int len = date.length() + voiceData.length + 2; // 1个空格，一个结束符;
		String lenStr = setLength(len);

		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_VOICE_FOLLOW_DATA).append(lenStr)
				.append(date).append(' ');
		byte[] destAray = null;
		byte[] packet = null;
		try {
			packet = mSB.toString().getBytes("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {

			bos.write(packet);
			bos.write(voiceData);
			bos.write('\0');
			bos.flush();
			destAray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
			}
		}
		AsynSocket.getInstance().sendImgData(destAray);

	}

	// 发送语音最后一部分
	public void sendVoiceFinishSectionWithAccount(String date, int voiceLen) {
		String voiceLength = String.format("%d", voiceLen);
		int len = date.length() + voiceLength.length() + 2; // 1个空格，一个结束符;
		String lenStr = setLength(len);
		String packet = date + ' ' + voiceLength;
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_VOICE_FINISH_DATA).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	// 发送图片第一部分
	public void sendPicFirstSectionWithAccount(String date, int picLen,
			byte[] picData) {

		String picLength = String.format("%d", picLen);
		int len = 0;

		len = date.length() + picLength.length() + picData.length + 3;
		// 2个空格，一个结束符;
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_PICTURE_FIRST_DATA).append(lenStr)
				.append(date).append(' ').append(picLength).append(' ');
		byte[] destAray = null;
		byte[] packet = null;
		try {
			packet = mSB.toString().getBytes("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {

			bos.write(packet);
			bos.write(picData);
			bos.write('\0');
			bos.flush();
			destAray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
			}
		}
		AsynSocket.getInstance().sendImgData(destAray);
	}

	// 发送图片接下来部分
	public void sendPicAppendSectionWithAccount(String date, byte[] picData) {
		int len = 0;

		len = date.length() + picData.length + 2;
		// 1个空格，一个结束符;
		String lenStr = setLength(len);

		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_PICTURE_FOLLOW_DATA).append(lenStr)
				.append(date).append(' ');
		byte[] destAray = null;
		byte[] packet = null;
		try {
			packet = mSB.toString().getBytes("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {

			bos.write(packet);
			bos.write(picData);
			bos.write('\0');
			bos.flush();
			destAray = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
			}
		}
		AsynSocket.getInstance().sendImgData(destAray);

	}

	// 发送图片最后一部分
	public void sendPicFinishSectionWithAccount(String date, int picLen) {
		String picLength = String.format("%d", picLen);
		int len = 0;

		len = date.length() + picLength.length() + 2;
		// 1个空格，一个结束符;
		String lenStr = setLength(len);
		String packet = date + ' ' + picLength;
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.CHAT_PACKAGE)
				.append(Protocol.CHAT_PICTURE_FINISH_DATA).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	public void processChat(byte[] str) {
		char type = (char) str[3];
		// Log.v("result","SOUND_PACKAGE     type");
//		if(Looper.myLooper() != Looper.getMainLooper()){
//			System.out.println("dfdfdf");
//			int i = 0;
//		}
//		
		switch (type) {
		case Protocol.CHAT_SEND_MSG_SUCC:
		case Protocol.CHAT_SEND_MSG_FAIL:

			receiveMsgState(str);
			break;
		case Protocol.CHAT_MSG_READ:
			receiveMsgReadState(str);
			break;

		case Protocol.CHAT_RETURN_LAST_MESSAGE_TIME:
			receiveLastMessageDate(str);
			break;

		case Protocol.CHAT_RETURN_TEXT_MSG:
			receiveTextMsg(str);
			break;

		case Protocol.CHAT_RETURN_VOICE_FIRST_MSG:
		case Protocol.CHAT_RETURN_VOICE_APPEND_MSG:
		case Protocol.CHAT_RETURN_VOICE_FINISH_MSG:
			receiveVoiceMsg(str);
			break;

		case Protocol.CHAT_RETURN_PIC_FIRST_MSG:
		case Protocol.CHAT_RETURN_PIC_APPEND_MSG:
		case Protocol.CHAT_RETURN_PIC_FINISH_MSG:
			receivePictureMsg(str);
			break;
		case Protocol.CHAT_RETURN_MSG_RECORD_FINISH:
			receiveMsgRecordFinish();
			break;

		case Protocol.CHAT_MSG_NOEXIST:
			receiveMsgRecordNoExist();
			break;
		case Protocol.CHAT_SAVE_PIC_TO_ALBUM_SUCC:
		case Protocol.CHAT_SAVE_PIC_TO_ALBUM_FAIL:
			responseForSavePicToAlbumState(str);
			break;
		case Protocol.CHAT_MSG_REMIND:
			receiveMsgRemind(str);
			break;
		default:
			break;
		}
	}

	// 用于当有未读聊天没有拉回来的时候，服务器只是发送一个提醒给客户端，然后客户端再去拉取，而不是直接由服务器转发消息,避免中间消息拉取不回来丢失
	public void receiveMsgRemind(byte[] str) {
		Intent intent = new Intent(Protocol.ACTION_CHATPACKET_MSGREMIND);

		GlobalApplication.getInstance().sendBroadcast(intent);
		
		Intent notifyIntent = new Intent(Protocol.NotificationChatNewMsg);

		GlobalApplication.getInstance().sendBroadcast(notifyIntent);
		// [[NSNotificationCenter
		// defaultCenter]postNotificationName:USNotoficationMsgRemind
		// object:self];
		// [[NSNotificationCenter
		// defaultCenter]postNotificationName:USNotificationChatNewMsg
		// object:nil];
	}

	// 更新信息为已读状态;把之前已发送状态的信息都更新为已读，发送失败和发送中的不会更新.
	// 策略：存着上次已读时间，把这个时间段内的更新为已读。
	public void updateMsgReadStateToCurrentTime(String curTime) {
		String acc = SelfInfo.getInstance().getAccount();
		SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(acc, Activity.MODE_PRIVATE);
		String lastReadTime = mSP.getString(
				Protocol.PREFERENCE_ChatLastReadTime, "0000-00-00-00:00:00");

		Database.getInstance(
				GlobalApplication.getInstance().getApplicationContext())
				.modifyChatStatus(acc, lastReadTime, curTime,
						Protocol.Read + "");
		SharedPreferences.Editor mEditor = mSP.edit();
		mEditor.putString(Protocol.PREFERENCE_ChatLastReadTime, curTime);
		mEditor.commit();
	}

	// 接收从服务器传文字信息状态, 如果信息是已读则直接跳过，为防止重发造成的影响，因为重发会返回发送成功，对方已读就不用重发了
	public void receiveMsgState(byte[] str) {
		char state = (char) str[3];
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String mess = mND.ByteArraytoString(str, str.length);
		String iniDate = mess.substring(Protocol.HEAD_LEN);
		GlobalApplication mIns = GlobalApplication.getInstance();
		mIns.setIniDate(iniDate);

		List<ChatTable> mCt = Database.getInstance(
				GlobalApplication.getInstance().getApplicationContext())
				.getChatWithAccount(SelfInfo.getInstance().getAccount(),
						iniDate);
		if (mCt.size() > 0) {
			if (mCt.get(0).getStatus().equals(Protocol.Read)) {
				return;
			}
		}

		// SharedPreferences mSP =
		// GlobalApplication.getInstance().getSharedPreferences("LoverHouse",
		// Activity.MODE_PRIVATE);

		if (state == Protocol.CHAT_SEND_MSG_SUCC) {
			mIns.setMsgSend(Protocol.Sended + "");
			Database.getInstance(mIns.getApplicationContext())
					.modifyChatStatus(SelfInfo.getInstance().getAccount(),
							iniDate, Protocol.Sended + "");

		}

		if (state == Protocol.CHAT_SEND_MSG_FAIL) {
			mIns.setMsgSend(Protocol.SendFail + "");
			Database.getInstance(mIns.getApplicationContext())
					.modifyChatStatus(SelfInfo.getInstance().getAccount(),
							iniDate, Protocol.SendFail + "");
		}
		// if (state == Protocol.CHAT_MSG_READ) {
		// mIns.setMsgSend(Protocol.Read+"");
		// String lastReadTime =
		// mSP.getString("ChatMyReadTime","0000-00-00-00:00:00");
		// mIns.setMyReadTime(lastReadTime);
		// this.updateMsgReadStateToCurrentTime(iniDate);
		// }
		Intent intent = new Intent(Protocol.ACTION_CHATPACKET_REMIND_DATA);
		intent.putExtra(Protocol.EXTRA_DATA, mess);
		mIns.sendBroadcast(intent);
	}

	// 接收从服务器返回的信息已读时间段状态
	public void receiveMsgReadState(byte[] str) {
		GlobalApplication mIns = GlobalApplication.getInstance();
		SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(SelfInfo.getInstance().getAccount(),
						Activity.MODE_PRIVATE);
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String mess1 = mND.ByteArraytoString(str, str.length);
		String mess = mess1.substring(Protocol.HEAD_LEN);
		String[] timeList = mess.split(" ");
		mIns.setMsgSend(Protocol.Read + "");
		String lastReadTime = mSP.getString(
				Protocol.PREFERENCE_ChatLastReadTime, "0000-00-00-00:00:00");
		mIns.setMyReadTime(lastReadTime);
		// this.updateMsgReadStateToCurrentTime(iniDate);

		if (timeList.length >= 2) {
			updateMsgReadStateToCurrentTime(timeList[1]);
			mIns.setIniDate(timeList[1]);
			sendComfirmMsgReadWithAccount();
			Intent intent = new Intent(Protocol.ACTION_CHATPACKET_REMIND_DATA);
			intent.putExtra(Protocol.EXTRA_DATA, mess);
			mIns.sendBroadcast(intent);

		}
		mIns = null;
	}

	// 接收服务器传回来的最后一条信息的时间;
	public void receiveLastMessageDate(byte[] str) {
		GlobalApplication mGA = GlobalApplication.getInstance();
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String mess = mND.ByteArraytoString(str, str.length);

		Intent intent = new Intent(Protocol.ACTION_CHATPACKET);
		intent.putExtra(Protocol.EXTRA_DATA, mess);
	    mGA.sendBroadcast(intent);
		if(!(mGA.isChatNewInit())){
			//说明是刚登陆成功在拉数据
			mGA.setChatNewInit(true);
			ControlHandler mCH = new ControlHandler();
			mCH.receiveLastMsgDate(mess);
			
			
		}
		// NSMutableDictionary *dic = [[NSMutableDictionary alloc]init];
		//
		// [dic setValue:[[NSString alloc]initWithFormat:@"%s",str + 8]
		// forKey:@"ChatLastMsgTime"];
		// NSLog(@"获取最后一条聊天信息时间为：%s",str+8);
		// [[NSNotificationCenter
		// defaultCenter]postNotificationName:USNotificationChatLastMessageTime
		// object:self userInfo:dic];
	}

	// 从服务器接收文字信息;
	public void receiveTextMsg(byte[] str) {

		NetDataTypeTransform mND = new NetDataTypeTransform();

		String rawMsg = (mND.ByteArraytoString(str, str.length));

		Database db = Database.getInstance(GlobalApplication.getInstance()
				.getApplicationContext());

		// account 0 , String initdate 1 , String status 2 , String message
		// 3, String msgType(Text pic voice) 4 iscoming 5)
		int totalLength = this.getIntFromChar(rawMsg.substring(4), 4); // 文字信息包总长度;
		int index = 8; // 用来偏移下标
		// NSMutableArray *allTextArr = [[NSMutableArray alloc]init];
		List<Object> allTextArr = new ArrayList<Object>();
		while (index < totalLength + 8 - 1) {
			byte[] titleLenByte = new byte[4];
			System.arraycopy(str, index, titleLenByte, 0, 4);
			int perTextMsgLen = this.getIntFromByte(titleLenByte, 4);
			int perFrontIndex = 4;
			int blankNum = 0;
			List<String> textArr = new ArrayList<String>();
			for (int perIndex = 4; perIndex < perTextMsgLen + 4; perIndex++) {
				if (str[index + perIndex] == ' ') {
					// The substring begins at the specified beginIndex and
					// extends to the character at index endIndex - 1
					byte[] temp = new byte[perIndex - perFrontIndex + 1];
					System.arraycopy(str, index + perFrontIndex, temp, 0,
							perIndex - perFrontIndex + 1);
					temp[perIndex - perFrontIndex] = '\0';
					String dest = mND.ByteArraytoString(temp, temp.length);
					textArr.add(dest);
					perFrontIndex = perIndex + 1;
					blankNum++;
					if (blankNum == 3) {
						break;
					}
				}
			}
			byte[] dest1 = new byte[perTextMsgLen - perFrontIndex + 1 + 4];
			System.arraycopy(str, index + perFrontIndex, dest1, 0,
					perTextMsgLen - perFrontIndex + 4);
			dest1[perTextMsgLen - perFrontIndex + 4] = '\0';
			String dest = (mND.ByteArraytoString(dest1, dest1.length));
			textArr.add(dest);
			dest1 = null;
			// account 0 , String initdate 1 , String status 2 , String message
			// 3, String msgType(Text pic voice) 4 )
			int msgType = 0;
			if ((textArr.get(0).equals(SelfInfo.getInstance().getAccount()))) {
				msgType = Protocol.VALUE_RIGHT_TEXT;

			}else {
				msgType = Protocol.VALUE_LEFT_TEXT;
			}
			textArr.add(msgType + "");
			index += (perTextMsgLen + 4);
			if (textArr.size() == 5) {
				// String account,String initdate,String status,String message,
				// int msgtype, String recordTime
				boolean ret = db.addChat(textArr.get(0), textArr.get(1), textArr.get(2),
						textArr.get(3), msgType, "0");
				if (BuildConfig.DEBUG)
					Log.d("chatText", "db.addChat(initDate = " + textArr.get(1) + ", message = " + textArr.get(3) + ") return: " + ret);
				if (ret == true)
					allTextArr.add(textArr);
			}
		}

		Intent intent = new Intent(Protocol.ACTION_CHATPACKET_MSGTEXT_DATA);
		intent.putExtra(Protocol.EXTRA_DATA, (Serializable) allTextArr);
		GlobalApplication.getInstance().sendBroadcast(intent);
		 //如果是已读的信息或者帐号是自己的话，那么说明是在向服务器拉取历史聊天记录,就不用抛出本地通知，否则说明是在接收新的信息，就要本地通知;
		List<String> textArr1 = null;
		if(allTextArr.size() > 0){
			textArr1 = (List<String>) allTextArr.get(0);
		}
		if (!(allTextArr.size() > 0 && (textArr1.get(2).equals(Protocol.Read+"")
		 || textArr1.get(0).equals(SelfInfo.getInstance().getAccount()))))
		 {
			//通知主页面有新消息
			Intent notifyIntent = new Intent(Protocol.NotificationChatNewMsg);
			GlobalApplication.getInstance().sendBroadcast(notifyIntent);
			
			//发送广播给service调用
			//启动service 执行重发机制以及检查是否有新的消息
			Intent startServiceIntent2 = new Intent(Protocol.ServiceChatNewMsg);    
			GlobalApplication.getInstance().startService(startServiceIntent2);  
//			Intent serviceIntent = new Intent(Protocol.ServiceChatNewMsg);
//			serviceIntent.putExtra(Protocol.EXTRA_DATA,"您收到一条新的聊天信息");
//			GlobalApplication.getInstance().sendBroadcast(serviceIntent);
//		 [FunctionClass postLocationNotificationWithType:@"Chat"
//		 AlertBody:@"您收到一条新的聊天信息"];
//		 [[NSNotificationCenter
//		 defaultCenter]postNotificationName:USNotificationChatNewMsg
//		 object:nil];
		 }
	}

	public void receiveVoiceMsg(byte[] str) {
		char type = (char) str[3];
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String rawMsg = (mND.ByteArraytoString(str, str.length));

		int voiceLen = this.getIntFromChar(rawMsg.substring(4), 4);
		int blankNum = 0;
		int index = 8; // 从包内容开始
		int blankTotal;

		// 第一个语音包有状态位，后续包没有;
		if (type == Protocol.CHAT_RETURN_VOICE_FIRST_MSG) {
			blankTotal = 3;
		} else {
			blankTotal = 2;
		}

		while (blankNum < blankTotal) {
			if (rawMsg.charAt(index) == ' ') {
				blankNum++;
			}
			index++;
		}
		voiceLen = voiceLen - index + 8 - 1; // 图片数据的长度,-1是减去结束符;

		String mess = rawMsg.substring(8);
		String[] arr = mess.split(" ");

		if (type == Protocol.CHAT_RETURN_VOICE_FIRST_MSG) {
			if (voiceData != null) {
				try {
					voiceData.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				voiceData = null;
			}
			voiceData = new ByteArrayOutputStream();
			voiceData.write(str, index, voiceLen);

		} else if (type == Protocol.CHAT_RETURN_VOICE_APPEND_MSG) {
			voiceData.write(str, index, voiceLen);
		} else if (type == Protocol.CHAT_RETURN_VOICE_FINISH_MSG) {
			if (voiceData == null)
				return;
			// 写入文件中;
			
			String timeStamp1 = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date());
			String timeStamp = "voicemsg" + timeStamp1;
			String voiceFilePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/LoverHouse"
					+ "/ChatVoice"
					+ "/"
					+ timeStamp + ".amr";
			try {
				AppManagerUtil.saveFileToSDCard("/ChatVoice", timeStamp
						+ ".amr", voiceData.toByteArray());
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			int msgType = 0;
			String acc = SelfInfo.getInstance().getAccount();
			if ((arr[0].equals(acc))) {
				msgType = Protocol.VALUE_RIGHT_AUDIO;

			} else {
				msgType = Protocol.VALUE_LEFT_AUDIO;
			}
			File file = new File(voiceFilePath);
			long len = 0;
			try {
				len = AppManagerUtil.getAmrDuration(file) / 1000;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int recTime = (int) (len + 1.5);
			// String account,String initdate,String status,String message,
			// int msgtype, String recordTime
			Database db = Database.getInstance(GlobalApplication.getInstance()
					.getApplicationContext());
			db.addChat(arr[0], arr[1], arr[2], voiceFilePath, msgType, recTime+"");

//			GlobalApplication.getInstance().sendBroadcast(
//					new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
//							+ Environment.getExternalStorageDirectory())));
			String content = arr[0] + " " + arr[1] + " " + arr[2] + " "
					+ voiceFilePath +" " + recTime;
			Intent intent = new Intent(Protocol.ACTION_CHATPACKET_RECVOICE);
			intent.putExtra(Protocol.EXTRA_DATA, content);
			GlobalApplication.getInstance().sendBroadcast(intent);
			//如果是已读的信息或者自己帐号的话，那么说明是在向服务器拉取历史聊天记录,就不用抛出本地通知，否则说明是在接收新的信息，就要本地通知;
	        if (!(arr[2].equals(Protocol.Read+"") || arr[0].equals(acc)))
	        {
	        	//通知主页面有新消息
				Intent notifyIntent = new Intent(Protocol.NotificationChatNewMsg);
				GlobalApplication.getInstance().sendBroadcast(notifyIntent);
				
				//发送广播给service调用
				Intent startServiceIntent2 = new Intent(Protocol.ServiceChatNewMsg);    
				GlobalApplication.getInstance().startService(startServiceIntent2); 
//				Intent serviceIntent = new Intent(Protocol.ServiceChatNewMsg);
//				serviceIntent.putExtra(Protocol.EXTRA_DATA,"您收到一条新的聊天信息");
//				GlobalApplication.getInstance().sendBroadcast(serviceIntent);
	        }
			try {
				voiceData.close();
			} catch (IOException e) {
			}
			voiceData = null;
		}
	}

	public void receivePictureMsg(byte[] str) {
		char type = (char) str[3];
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String rawMsg = (mND.ByteArraytoString(str, str.length));
		int picLen = getIntFromChar(rawMsg.substring(4), 4);
		int blankNum = 0;
		int index = 8; // 从包内容开始
		int blankTotal;

		// 第一个图片包有状态位，后续包没有;
		if (type == Protocol.CHAT_RETURN_PIC_FIRST_MSG) {
			blankTotal = 3;
		} else {
			blankTotal = 2;
		}

		while (blankNum < blankTotal) {
			if (str[index] == ' ') {
				blankNum++;
			}
			index++;
		}
		picLen = picLen - index + 8 - 1; // 图片数据的长度,-1是减去结束符;

		String mess = rawMsg.substring(8);
		String[] arr = mess.split(" ");

		if (type == Protocol.CHAT_RETURN_PIC_FIRST_MSG) {
			if (picData != null) {
				try {
					picData.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				picData = null;
			}
			picData = new ByteArrayOutputStream();
			picData.write(str, index, picLen);
		} else if (type == Protocol.CHAT_RETURN_PIC_APPEND_MSG) {
			picData.write(str, index, picLen);
		} else if (type == Protocol.CHAT_RETURN_PIC_FINISH_MSG) {

			if (picData == null)
				return;
			// 写入文件中;
			String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date());
			byte[] destAray = picData.toByteArray();
			Bitmap m = BitmapFactory.decodeByteArray(destAray, 0,
					destAray.length);

			String photopath = Environment.getExternalStorageDirectory()
					+ "/LoverHouse" + "/Chatpic" + "/" + timeStamp + ".png";
			AppManagerUtil.writeToSD("/Chatpic", m, timeStamp);

			int msgType = 0;
			String acc = SelfInfo.getInstance().getAccount();
			if ((arr[0].equals(acc))) {
				msgType = Protocol.VALUE_RIGHT_IMAGE;

			} else {
				msgType = Protocol.VALUE_LEFT_IMAGE;
			}
			Database db = Database.getInstance(GlobalApplication.getInstance()
					.getApplicationContext());
			db.addChat(arr[0], arr[1], arr[2], photopath, msgType, "0");

			try {
				picData.close();
			} catch (IOException e) {
			}
			picData = null;

			String content = arr[0] + " " + arr[1] + " " + arr[2] + " "
					+ photopath;
			Intent intent = new Intent(Protocol.ACTION_CHATPACKET_RECPIC);
			intent.putExtra(Protocol.EXTRA_DATA, content);
			GlobalApplication.getInstance().sendBroadcast(intent);

			  //如果是已读的信息或者是自己帐号的话，那么说明是在向服务器拉取历史聊天记录,就不用抛出本地通知，否则说明是在接收新的信息，就要本地通知;
	        if (!(arr[2].equals(Protocol.Read+"") || (arr[0].equals(acc))))
	        {
	        	//通知主页面有新消息
				Intent notifyIntent = new Intent(Protocol.NotificationChatNewMsg);
				GlobalApplication.getInstance().sendBroadcast(notifyIntent);
				
				//发送广播给service调用
//				Intent serviceIntent = new Intent(Protocol.ServiceChatNewMsg);
//				serviceIntent.putExtra(Protocol.EXTRA_DATA,"您收到一条新的聊天信息");
//				GlobalApplication.getInstance().sendBroadcast(serviceIntent);
				
				Intent startServiceIntent2 = new Intent(Protocol.ServiceChatNewMsg);    
				GlobalApplication.getInstance().startService(startServiceIntent2); 
	        }

		}
	}

	// 拉取历史聊天记录结束。
	public void receiveMsgRecordFinish() {
		Intent intent = new Intent(Protocol.ACTION_CHATPACKET_MSGRECFINISH);

		GlobalApplication.getInstance().sendBroadcast(intent);

	}

	// 历史聊天记录不存在 todealwith
	public void receiveMsgRecordNoExist() {
		// 根据帐号组合关键词,如果服务器返回历史聊天记录不存在了，那么以后就不用向服务器拉取了。
		String key = "ChatMsgLib";
		SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(SelfInfo.getInstance().getAccount(),
						Activity.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSP.edit();
		mEditor.putString(key, "ChatMsgLibNoExist");
		mEditor.commit();
		// Intent intent = new Intent(Protocol.ACTION_CHATPACKET_MSGRECNOEXIT);
		//
		// GlobalApplication.getInstance().sendBroadcast(intent);
	}

	// 从服务器接收聊天图片转存到相册的结果;
	public void responseForSavePicToAlbumState(byte[] str) {
		char state = (char) str[3];
		String mess = "";

		if (state == Protocol.CHAT_SAVE_PIC_TO_ALBUM_SUCC) {
			// [dic setValue:@"saveSucc" forKey:@"SaveState"];
			mess = "saveSucc";

		}
		if (state == Protocol.CHAT_SAVE_PIC_TO_ALBUM_FAIL) {
			mess = "saveFail";
			// [dic setValue:@"saveFail" forKey:@"SaveState"];
		}
		Intent intent = new Intent(
				Protocol.ACTION_CHATPACKET_SAVEPICTOALBUMSTATE);
		intent.putExtra(Protocol.EXTRA_DATA, mess);
		GlobalApplication.getInstance().sendBroadcast(intent);
		// [[NSNotificationCenter
		// defaultCenter]postNotificationName:USNotificationChatSavePicToAlbumState
		// object:self userInfo:dic];
	}

	// 处理重发机制;
	// 用于处理重发那些由于网络原因还没有发送成功，还是显示发送中的信息。
	public void dealWithSendingMsg()
	{
	    List<ChatTable> sendingMsgArray = Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
	    		.getSendingChat();
	   
	    for (ChatTable mCt  : sendingMsgArray)
	    {
	        int msgType = mCt.getMsgtype();
	        String msgText = mCt.getMessage();
	        String msgDate = mCt.getInitdate();
	        
	        if (msgType == Protocol.VALUE_RIGHT_IMAGE)
            {
	        	uploadPic(msgText,msgDate);
             
            }
            else if (msgType== Protocol.VALUE_RIGHT_AUDIO)
            {
            	sendRecordData(msgDate, msgText);
            }
            else if (msgType == Protocol.VALUE_RIGHT_TEXT)
            {
            	sendTextWithAccount(msgDate,msgText);
            }
	    }
	}

	private void uploadPic(final String picturePath,final String imageDate) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				SelfInfo  mSelf = SelfInfo.getInstance();
				String email = mSelf.getAccount();
				
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				options.outHeight = 380;
				options.outWidth = 308;
				Bitmap tempBitmap = BitmapFactory.decodeFile(picturePath,
						options);	

				ChatPacketHandler chatHandler = new ChatPacketHandler();

		
				byte[] picData = Bitmap2Bytes(tempBitmap);
				// 发送出去;
				int firstPacketPicLen = 8192 - 8 - email.length()
						- imageDate.length()
						- (String.format("%d", picData.length)).length() - 4;
				int appendPacketPicLen = 8192 - 8 - email.length()
						- imageDate.length() - 3;

				if (picData.length <= firstPacketPicLen) {
					chatHandler.sendPicFirstSectionWithAccount(imageDate,
							picData.length, picData);
				} else {
					int appendPacketNum = (picData.length - firstPacketPicLen)
							/ appendPacketPicLen + 1;
					byte[] firstPacket = new byte[firstPacketPicLen];
					System.arraycopy(picData, 0, firstPacket, 0,
							firstPacketPicLen);
					chatHandler.sendPicFirstSectionWithAccount(imageDate,
							picData.length, firstPacket);

					for (int i = 0; i < appendPacketNum - 1; i++) {
						byte[] appendPacket = new byte[appendPacketPicLen];
						System.arraycopy(picData, firstPacketPicLen + i
								* appendPacketPicLen, appendPacket, 0,
								appendPacketPicLen);

						chatHandler.sendPicAppendSectionWithAccount(
								imageDate, appendPacket);
					}
					int lastLen = picData.length - firstPacketPicLen
							- appendPacketPicLen * (appendPacketNum - 1);
					byte[] lastAppendPacket = new byte[lastLen];
					System.arraycopy(picData, firstPacketPicLen
							+ (appendPacketNum - 1) * appendPacketPicLen,
							lastAppendPacket, 0, lastLen);
					chatHandler.sendPicAppendSectionWithAccount(imageDate,
							lastAppendPacket);
				}
				chatHandler.sendPicFinishSectionWithAccount(imageDate,
						picData.length);
				mSelf = null;
				picData= null;
			

			}

		}).start();
	}
	/**
	 * 把Bitmap转Byte
	 */
	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
		return baos.toByteArray();
	}


	public void sendRecordData(final String currentDate, final String voicePath) {
		
		new Thread( new Runnable(){

			@Override
			public void run() {
				ChatPacketHandler chatHandler = new ChatPacketHandler();
              
				byte[] recordData = null;
			  
				try {
					recordData = AppManagerUtil.readFileSdcardFile(voicePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 //如果用户是在线的话，才发送出去，否则只是写入数据库，等到登录成功之后会重发。
				 if (SelfInfo.getInstance().isOnline()) {
				// 发送语音.
				int firstPacketPicLen = 8192 - 8 -SelfInfo.getInstance().getAccount().length()
						- currentDate.length()
						- String.format("%d", recordData.length).length() - 4;
				int appendPacketPicLen = 8192 - 8 - SelfInfo.getInstance().getAccount().length()
						- currentDate.length() - 3;

				if (recordData.length <= firstPacketPicLen) {
					chatHandler.sendVoiceFirstSectionWithAccount(currentDate,
							recordData.length, recordData);
				} else {
					int appendPacketNum = (recordData.length - firstPacketPicLen)
							/ appendPacketPicLen + 1;

					byte[] firstPacket = new byte[firstPacketPicLen];
					System.arraycopy(recordData, 0, firstPacket, 0, firstPacketPicLen);
					chatHandler.sendVoiceFirstSectionWithAccount( currentDate,
							recordData.length, firstPacket);

					for (int i = 0; i < appendPacketNum - 1; i++) {
						byte[] appendPacket = new byte[appendPacketPicLen];
						System.arraycopy(recordData, firstPacketPicLen + i
								* appendPacketPicLen, appendPacket, 0,
								appendPacketPicLen);

						chatHandler.sendVoiceAppendSectionWithAccount(
								currentDate, appendPacket);
					}
					byte[] lastAppendPacket = new byte[recordData.length
							- firstPacketPicLen - appendPacketPicLen
							* (appendPacketNum - 1)];
					System.arraycopy(recordData, firstPacketPicLen + appendPacketPicLen
							* (appendPacketNum - 1), lastAppendPacket, 0,
							recordData.length - firstPacketPicLen - appendPacketPicLen
									* (appendPacketNum - 1));
					chatHandler.sendVoiceAppendSectionWithAccount( currentDate,
							lastAppendPacket);
				}
				chatHandler.sendVoiceFinishSectionWithAccount(currentDate,
						recordData.length);
				
			}
			}
			
		}).start();
		
	}

}
