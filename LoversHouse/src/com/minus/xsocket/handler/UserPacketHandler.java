package com.minus.xsocket.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.minius.common.CommonBitmap;
import com.minius.ui.HeadPhotoHanddler;
import com.minus.lovershouse.BuildConfig;
import com.minus.lovershouse.R;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.sql_interface.Database;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.util.NetDataTypeTransform;
import com.umeng.fb.model.Reply.STATUS;

public class UserPacketHandler {

	static ByteArrayOutputStream picData;
	static ByteArrayOutputStream tarPicData;

	public String setLength(int len) {
		String mL = String.format("%04d", len);
		return mL;
	}

	// part1 process response
	public void process(byte[] str) {
//		Log.v("test", str[3] + "");
//		if(Thread.currentThread() == Looper.getMainLooper().getThread()){
//			System.out.println("dfdfdf");
//			int i = 0;
//		}
//		if(Looper.myLooper() != Looper.getMainLooper()){
//			System.out.println("dfdfdf");
//			int i = 0;
//		}
		
		char type = (char) str[3];
		switch (type) {

		case Protocol.MAIL_CAN_REG:
		case Protocol.MAIL_CANNOT_REG:
			processCanMailReg(str);
			break;
		case Protocol.REGISTER_SUCC:
		case Protocol.REGISTER_FAIL:

			processRegister(str);
			break;
		case Protocol.LOGIN_SUCC:
			if (BuildConfig.DEBUG)
				Log.d("Login", "UserPacketHandler Protocol.LOGIN_SUCC");
			GlobalApplication mGA = GlobalApplication.getInstance();
			SharedPreferences mSP = mGA.getApplicationContext().getSharedPreferences(
					Protocol.PREFERENCE_NAME, Activity.MODE_PRIVATE);
			String dbTitle = mSP.getString("LastUser", "");
			SelfInfo mSelf = SelfInfo.getInstance();
			if (dbTitle.equals("")) {
				// 说明是从登陆页面登陆的
				// 需要修改db的名字
				Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
						.initDatabase(mSelf.getAccount());
				SharedPreferences.Editor mEditor = mSP.edit();
				mEditor.putString("LastUser", mSelf.getAccount());
				mEditor.putString("Password", mSelf.getPwd());
				mEditor.commit();
			}
			
			mSelf.setOnline(true);
			
			uploadTokenToServer();
			processLogin(str);
			break;
		case Protocol.LOGIN_FAIL:
			Log.d("Login", "UserPacketHandler Protocol.LOGIN_FAIL");
			SelfInfo.getInstance().setOnline(false);
			processLogin(str);
			break;
		case Protocol.MODIFY_SEX_SUCC:
		case Protocol.MODIFY_SEX_FAIL:
			processModifySex(str);
			break;
		case Protocol.MODIFY_PASSWORD_SUCC:
		case Protocol.MODIFY_PASSWORD_FAIL:
			processModifyPassword(str);
			break;
		case Protocol.MODIFY_BIG_NAME_SUCC:
		case Protocol.MODIFY_BIG_NAME_FAIL:
			processModifyBigName(str);
			break;
		case Protocol.MODIFY_FIGURE_SUCC:
		case Protocol.MODIFY_FIGURE_FAIL:
			processModifyFigure(str);
			break;
		case Protocol.MODIFY_STATUS_SUCC:
		case Protocol.MODIFY_STATUS_FAIL:
			processModifyStatus(str);
			break;
		case Protocol.MODIFY_SMALL_NAME_SUCC:
		case Protocol.MODIFY_SMALL_NAME_FAIL:
			processModifySmallName(str);
			break;
		case Protocol.MODIFY_HEAD_PHOTO_SUCC:
		case Protocol.MODIFY_HEAD_PHOTO_FAIL:
			processModifyHeadPhoto(str);
			break;
		case Protocol.MATCH_REQ_TRANSMIT_FAIL:
		case Protocol.MATCH_REQ_TRANSMIT_SUCC:
			processTransmitReq(str);
			break;
		case Protocol.ACCEPT_MATCH:
		case Protocol.REJECT_MATCH:
			processMatchAccOrReject(str);
			break;
		case Protocol.RETURN_SELF_INFO:
			processSelfInfo(str);
			break;
		case Protocol.RETURN_MATCH_INFO:
			processMatchInfo(str);
			break;
		// TODO
		case Protocol.RETURN_COMM_INFO:
			processCommonInfo(str);
			break;
		case Protocol.RETURN_FIRST_HEAD_PHOTO_DATA:
			receiveSelfHeadPhoto(str);
			break;
		case Protocol.RETURN_FOLLOW_HEAD_PHOTO_DATA:
			// processSelfHeadPhotoAppendData(str);
			receiveSelfHeadPhoto(str);
			break;
		case Protocol.RETURN_HEAD_PHOTO_DATA_FINISH:
			// processSelfHeadPhotoFinish(str);
			receiveSelfHeadPhoto(str);
			break;
		case Protocol.RETURN_MATCH_FIRST_HEAD_PHOTO_DATA:

		case Protocol.RETURN_MATCH_FOLLOW_HEAD_PHOTO_DATA:

		case Protocol.RETURN_MATCH_HEAD_PHOTO_DATA_FINISH:
			receiveMatchHeadPhoto(str);
			break;
		case Protocol.ADD_FRIEND_SUCC:
		case Protocol.ADD_FRIEND_FAIL:
			processAddFriend(str);
			break;

		case Protocol.RETURN_ALBUM_HOME_PAGE_FAIL:
		case Protocol.RETURN_ALBUM_HOME_PAGE_SUCC:
			processAlbumHomePageState(str);
			break;
		case Protocol.MODIFY_ALBUM_HOME_PAGE:
			processChangeAlbumHomePage(str);
			  break;
		 case Protocol.RETURN_TURNONLIGHT:
	     case Protocol.RETURN_TURNOFFLIGHT:
	            processLightState(str);
	            break;
		case Protocol.MODIFY_HOUSE_STYLE_SUCC:
		case Protocol.MODIFY_HOUSE_STYLE_FAIL:
			processModifyHouseStyle(str);
			break;
		case Protocol.MODIFY_HOUSE_STYLE_BACK:
			processModifyHouseBack(str);
			break;
		case Protocol.ASK_FOR_MATCH: // 服务器请求配对
			this.processRequestMatch(str);
			break;

		case Protocol.PASSWORD_HAVE_BEEN_SENT:

		case Protocol.PASSWORD_SENT_FAIL:
			this.processPasswordsentback(str);
			break;
		default:
			break;
		}

	}

	// part2,process response 广播到到对应的界面

	public void processRegister(byte[] str) {
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String mess = mND.ByteArraytoString(str, str.length);
//		Log.v("result", " 发送 广播 " + mess);
		Intent intent = new Intent(Protocol.ACTION_USERPACKET);
		intent.putExtra(Protocol.EXTRA_DATA, mess);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}

	public void processLogin(byte[] str) {

		NetDataTypeTransform mND = new NetDataTypeTransform();
		String mess = mND.ByteArraytoString(str, str.length);
//		Log.v("result", " 发送 广播 " + mess);
		Intent intent = new Intent(Protocol.ACTION_USERPACKET);
		intent.putExtra(Protocol.EXTRA_DATA, mess);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}

	public void processPasswordsentback(byte[] str) {
		// char type = (char)str[3];

		processLogin(str);
	}

	public void processModifySex(byte[] str) {
		char type = (char) str[3];
		if (type == Protocol.MODIFY_SEX_SUCC) {
			SelfInfo.getInstance().setSex(
					GlobalApplication.getInstance().getModifySex());
			SelfInfo.getInstance().setHudLabel("Modify Sex Succ");

		} else {
			SelfInfo.getInstance().setHudLabel("Modify Sex Fai");

		}

		processLogin(str);
	}

	public void processModifyPassword(byte[] str) {
		char type = (char) str[3];
		if (type == Protocol.MODIFY_PASSWORD_SUCC) {
			SelfInfo.getInstance().setPwd(
					GlobalApplication.getInstance().getModifyPwd());
			SelfInfo.getInstance().setHudLabel("Modify Password Succ");

		} else {
			SelfInfo.getInstance().setHudLabel("Modify Password fail");
		}
		processLogin(str);
	}

	public void processModifyBigName(byte[] str) {
		char type = (char) str[3];
		if (type == Protocol.MODIFY_BIG_NAME_SUCC) {
			SelfInfo.getInstance().setNickName(
					GlobalApplication.getInstance().getModifyBigname());
			SelfInfo.getInstance().setHudLabel("Modify BigName Succ");

		} else {
			SelfInfo.getInstance().setHudLabel("Modify BigName  Fail");

		}
		processLogin(str);

	}

	public void processModifyFigure(byte[] str) {
		char type = (char) str[3];
		if (type == Protocol.MODIFY_FIGURE_SUCC) {

			SelfInfo.getInstance().setAppearance(
					GlobalApplication.getInstance().getModifyAppearance());
			Database.getInstance(
					GlobalApplication.getInstance().getApplicationContext())
					.updateSelfAppearance(SelfInfo.getInstance().getAccount(),
							SelfInfo.getInstance().getAppearance());

		} else {

		}
		processLogin(str);
	}

	public void processModifyStatus(byte[] str) {

		char type = (char) str[3];
		if (type == Protocol.MODIFY_STATUS_SUCC) {
			SelfInfo.getInstance().setStatus(
					GlobalApplication.getInstance().getModifyStatus());
			SelfInfo.getInstance().setHudLabel("Modify SmallName Succ");
		} else {
			SelfInfo.getInstance().setHudLabel("Modify SmallName Fail");

		}
		processLogin(str);
	}

	public void processModifySmallName(byte[] str) {
		char type = (char) str[3];
		if (type == Protocol.MODIFY_SMALL_NAME_SUCC) {
			SelfInfo.getInstance().setSmallName(
					GlobalApplication.getInstance().getModifySmallMame());
			SelfInfo.getInstance().setHudLabel("Modify Statue Succ");
		} else {
			SelfInfo.getInstance().setHudLabel("Modify Statue Fail");

		}
		processLogin(str);
	}

	public void processModifyHeadPhoto(byte[] str) {
		char type = (char) str[3];
		if (type == Protocol.MODIFY_HEAD_PHOTO_SUCC) {

			SelfInfo.getInstance().setHudLabel("Modify HeadPhoto Succ");

		} else {
			SelfInfo.getInstance().setHudLabel("Modify HeadPhoto  Fail");

		}
		processLogin(str);

	}

	public void processTransmitReq(byte[] str) {
		char type = (char) str[3];
		if (type == Protocol.MATCH_REQ_TRANSMIT_SUCC) {

			SelfInfo.getInstance().setHudLabel("Match req transmit Succ");
		} else {
			SelfInfo.getInstance().setHudLabel("Match req transmit Fail");

		}
		processLogin(str);

	}

	// 协议+服务器类型+用户包+接受配对+包体长度+接受账户+’\0’
	public void processMatchAccOrReject(byte[] str) {
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String mess = mND.ByteArraytoString(str, str.length);
		if (str[3] == Protocol.ACCEPT_MATCH) {
			String acceptor = mess.substring(Protocol.HEAD_LEN);
			SelfInfo.getInstance().setTarget(acceptor);
			GlobalApplication.getInstance().setTiAcc(acceptor);
			this.getMatchInfo();
			SelfInfo.getInstance().setHudLabel("accept");
			SelfInfo.getInstance().setMatch(true);
		} else {
			SelfInfo.getInstance().setHudLabel("reject");
			SelfInfo.getInstance().setMatch(false);
		}

//		Log.v("result", " 发送 广播 " + mess);
		Intent intent = new Intent(Protocol.ACTION_USERPACKET);
		intent.putExtra(Protocol.EXTRA_DATA, mess);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}

	// 协议+服务器类型+用户包+返回配对信息+包体长度+配对的账号+空格+
	// 性别+空格+大名+空格+形象+空格+状态+空格+对方小名+空格+配对账号+’\0’
	public void processSelfInfo(byte[] str) {
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String messtemp = mND.ByteArraytoString(str, str.length);

		String mess = messtemp.substring(Protocol.HEAD_LEN);

		String[] arr = mess.split(" ");

		String acc = arr[0];
		String se = arr[1];
		String bn = arr[2];
		String appear = arr[3];
		String st = arr[4];

		String sn = arr[5];
		String tar = arr[6];
		if (!(st.equals(Protocol.ActionEnd + ""))) {
			if (st.charAt(0) > ('0' + 40)) {
				// 服务器的自定义status是从'1'+40开始的
				char statusChar = (char) (st.charAt(0) - 40);
				int statusInt = statusChar - '0';
				if (statusInt > 0 && statusInt <= 5) {
					statusInt = statusInt + Protocol.SINGLE_ACTION_CUSTOM1 - 1;
					st = statusInt + "";

				}
			}
		}

		Database.getInstance(
				GlobalApplication.getInstance().getApplicationContext())
				.updateSelfInfo(acc, se, bn, appear, st, sn, tar);

		if (tar.length() == 1 && (byte) tar.charAt(0) == 0x01) {
			SelfInfo.getInstance().setMatch(false);
		} else {
			SelfInfo.getInstance().setMatch(true);
		}
		if (se.charAt(0) == 0x01) {
			se = "b";
		} else if (se.charAt(0) == 0x02) {
			se = "g";
		}
		SelfInfo.getInstance().setInfo(acc, se, bn, appear, st, sn, tar);

		SharedPreferences mSP = GlobalApplication.getInstance().getApplicationContext()
				.getSharedPreferences(Protocol.PREFERENCE_NAME,
						Activity.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSP.edit();
		mEditor.putString("LastUsersex", se);//用于登录时密码锁的头像边框
		mEditor.commit();
		
		Intent intent = new Intent(Protocol.ACTION_USERPACKET);
		intent.putExtra(Protocol.EXTRA_DATA, messtemp);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}

	public void processMatchInfo(byte[] str) {
		// 获得所有的对方自定义动作
		ActionPacketHandler mAP = new ActionPacketHandler();
		mAP.getAllTargetCustomAction();

		NetDataTypeTransform mND = new NetDataTypeTransform();
		String messtemp = mND.ByteArraytoString(str, str.length);
		String mess = messtemp.substring(Protocol.HEAD_LEN);
		String[] arr = mess.split(" ");
		String acc = arr[0];
		String se = arr[1];
		String bn = arr[2];
		String appear = arr[3];
		String st = arr[4];
		String sn = arr[5];
		String tar = arr[6];

		if (!(st.equals(Protocol.ActionEnd + ""))) {
			if (st.charAt(0) > ('0' + 40)) {
				// 服务器的自定义status是从'1'+40开始的
				char statusChar = (char) (st.charAt(0) - 40);
				int statusInt = statusChar - '0';
				if (statusInt > 0 && statusInt <= 5) {
					statusInt = statusInt + Protocol.SINGLE_ACTION_CUSTOM1 - 1;
					st = statusInt + "";

				}
			}
		}

		Database.getInstance(
				GlobalApplication.getInstance().getApplicationContext())
				.addTargetInfo(acc, se, bn, appear, st, sn, tar);

		SelfInfo.getInstance().setMatch(true);

		if (se.charAt(0) == 0x01) {
			se = "b";
		} else if (se.charAt(0) == 0x02) {
			se = "g";
		}
	
		GlobalApplication.getInstance().setTarInfo(acc, se, bn, appear, st, sn,
				tar);
		String matchPic = Database.getInstance(
				GlobalApplication.getInstance().getApplicationContext())
				.getTargetHeadPhoto(tar);
		GlobalApplication.getInstance()
				.setTiTargetHeadPhoPath(
						Database.getInstance(
								GlobalApplication.getInstance()
										.getApplicationContext())
								.getTargetHeadPhoto(
										SelfInfo.getInstance().getAccount()));
		String mPath = GlobalApplication.getInstance().getTiTargetHeadPhoPath();
				
		if (mPath.equals(Protocol.DEFAULT + "") || AppManagerUtil.isFileExist(mPath)) {
			this.getMatchHeadPhoto();
		} else {
			Bitmap m = AppManagerUtil.getDiskBitmap(matchPic);
			GlobalApplication.getInstance().setTarHeadPicBm(m);
			GlobalApplication.getInstance().setTiTargetHeadPhoPath(matchPic);
		}

		Intent intent = new Intent(Protocol.ACTION_USERPACKET);
		intent.putExtra(Protocol.EXTRA_DATA, messtemp);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}

	public void processCommonInfo(byte[] str) {
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String messtemp = mND.ByteArraytoString(str, str.length);
		String mess = messtemp.substring(Protocol.HEAD_LEN);
		String[] arr = mess.split(" ");
		GlobalApplication mIns = GlobalApplication.getInstance();
		String mo = Protocol.DEFAULT + "";
		String fri = Protocol.DEFAULT + "";
		String wit = Protocol.DEFAULT + "";
		String hs = Protocol.DEFAULT + "";
		String lightState = Protocol.DEFAULT + "";
		String firstPicture = Protocol.DEFAULT + "";
		if (arr.length < 5) {
			mIns.setCommonDefault();
		} else {
			mo = arr[0];
			fri = arr[1];
			wit = arr[2];
			hs = arr[3];
			lightState = arr[4];
			firstPicture = arr[5];

			mIns.setCommon(mo, fri, wit, hs, lightState, firstPicture);

		}
		Database.getInstance(mIns.getApplicationContext()).addCommonInfo(
				SelfInfo.getInstance().getAccount(), mo, fri, wit, hs,
				lightState, firstPicture);
		Intent intent = new Intent(Protocol.ACTION_USERPACKET);
		intent.putExtra(Protocol.EXTRA_DATA, messtemp);
		GlobalApplication.getInstance().sendBroadcast(intent);
	}

	public void processAddFriend(byte[] str) {
		// NSString *mess = [[NSString alloc] initWithFormat:@"%s",str ];
		// [[Publisher getInstance] publishAll:mess];
		// TODO
	}
//对方修改房屋风格返回
	public void processModifyHouseBack(byte[] str) {
		
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String messtemp = mND.ByteArraytoString(str, str.length);
//		Log.v("test", "返回CommonInfo 001 " + messtemp);
		String mess = messtemp.substring(Protocol.HEAD_LEN);
		String[] arr = mess.split(" ");
		// byte a=(byte)arr[0].charAt(0);
		// String hs=a+"";
		// byte a=(byte)(hs.charAt(0)-'0');
		GlobalApplication.getInstance().setHouseStyle(arr[0]);
		Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
		.updateHouseStyle(SelfInfo.getInstance().getAccount(),arr[0]);
	
		Intent intent = new Intent(Protocol.ACTION_USERPACKET_ModifyHouseStyle);
		GlobalApplication.getInstance().sendBroadcast(intent);
     
	}

	public void processModifyHouseStyle(byte[] str) {
		// TODO
		char type = (char) str[3];
		if (type == Protocol.MODIFY_HOUSE_STYLE_SUCC) {
			GlobalApplication.getInstance().setHouseStyle(
					SelfInfo.getInstance().getHousestyle());
			CommonBitmap.getInstance().setAlubmPhotoInit(false);

		} else {
//			SelfInfo.getInstance().setHudLabel("Modify HouseStyle Fail");

		}
		processLogin(str);
		// TODO broadcast
	}

	public void processCanMailReg(byte[] str) {
		this.processLogin(str);
	}

	

	private int getIntFromByte(byte[] str, int len) {
		int total = 0;
		for (int i = 0; i < len; i++) {
			total += (int) (str[i] - '0') * Math.pow(10.0, len - i - 1);
		}
		return total;
	}

	// 接收到图片
	public void receiveMatchHeadPhoto(byte[] str) {
		char type = (char) str[3];
		byte[] picLenByte = new byte[4];
		System.arraycopy(str, 4, picLenByte, 0, 4);
		int picLen = this.getIntFromByte(picLenByte, 4);

		picLen = picLen - 1; // 1个结束符;
		int beginIndex = 8; // 图片开始的下标;

		if (type == Protocol.RETURN_MATCH_FIRST_HEAD_PHOTO_DATA) {

			if (tarPicData != null) {
				try {
					tarPicData.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tarPicData = null;
			}
			tarPicData = new ByteArrayOutputStream();
			tarPicData.write(str, beginIndex, picLen);
		} else if (type == Protocol.RETURN_MATCH_FOLLOW_HEAD_PHOTO_DATA) {
			tarPicData.write(str, beginIndex, picLen);
		} else if (type == Protocol.RETURN_MATCH_HEAD_PHOTO_DATA_FINISH) {
			NetDataTypeTransform mND = new NetDataTypeTransform();
			String mess1 = mND.ByteArraytoString(str, str.length);
			String imageLenStr = mess1.substring(Protocol.HEAD_LEN);

			byte[] lenBytes = null;
			try {
				lenBytes = imageLenStr.getBytes("utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int imageLen = this.getIntFromByte(lenBytes, lenBytes.length);

			// 如果接收到的图片长度和服务器发过来的长度不一样的话那么就要重新获取;
			if (imageLen != tarPicData.size()) {
				if (tarPicData != null) {
					try {
						tarPicData.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tarPicData = null;
				}
				return;
			}

			if (tarPicData == null)
				return;
			// 写入文件中;

			byte[] destAray = tarPicData.toByteArray();
			Bitmap m = BitmapFactory.decodeByteArray(destAray, 0,
					destAray.length);
			String tarAcc = SelfInfo.getInstance().getTarget().trim();
			String headphotopath = Environment.getExternalStorageDirectory()
					+ "/LoverHouse" + "/HeadPhoto" + "/" + tarAcc + ".png";
			if(AppManagerUtil.writeToSD("/HeadPhoto", m, tarAcc)){
//			GlobalApplication.getInstance().setTarHeadPicBm(m);
//			GlobalApplication.getInstance().setTiTargetHeadPhoPath(
//					headphotopath);
			Database.getInstance(
					GlobalApplication.getInstance().getApplicationContext())
					.updateTargetHeadPhoto(tarAcc, headphotopath);
			}

			HeadPhotoHanddler mHeadPhotoHandler = new HeadPhotoHanddler();
			Bitmap endBm = null;
			if (GlobalApplication.getInstance().getTiSex().equals("b")) {
				Bitmap frameBm = BitmapFactory.decodeResource(GlobalApplication
						.getInstance().getApplicationContext().getResources(),
						R.drawable.boy_photoframe);
				endBm = (mHeadPhotoHandler.handleHeadPhoto(headphotopath,
						frameBm, GlobalApplication.getInstance()
								.getApplicationContext()));
			} else {
				Bitmap frameBm = BitmapFactory.decodeResource(GlobalApplication
						.getInstance().getApplicationContext().getResources(),
						R.drawable.girl_photoframe);
				endBm = (mHeadPhotoHandler.handleHeadPhoto(headphotopath,
						frameBm, GlobalApplication.getInstance()
								.getApplicationContext()));
			}

			GlobalApplication.getInstance().setTarHeadPicBm(endBm);

			if (tarPicData != null) {
				try {
					tarPicData.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				tarPicData = null;
			}
		}
	}

	// 接收到图片
	public void receiveSelfHeadPhoto(byte[] str) {
		char type = (char) str[3];
		byte[] picLenByte = new byte[4];
		System.arraycopy(str, 4, picLenByte, 0, 4);
		int picLen = this.getIntFromByte(picLenByte, 4);

		picLen = picLen - 1; // 1个结束符;
		int beginIndex = 8; // 图片开始的下标;

		if (type == Protocol.RETURN_FIRST_HEAD_PHOTO_DATA) {
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
			picData.write(str, beginIndex, picLen);
		} else if (type == Protocol.RETURN_FOLLOW_HEAD_PHOTO_DATA) {
			picData.write(str, beginIndex, picLen);
		} else if (type == Protocol.RETURN_HEAD_PHOTO_DATA_FINISH) {
			NetDataTypeTransform mND = new NetDataTypeTransform();
			String mess1 = mND.ByteArraytoString(str, str.length);
			String imageLenStr = mess1.substring(Protocol.HEAD_LEN);

			byte[] lenBytes = null;
			try {
				lenBytes = imageLenStr.getBytes("utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int imageLen = this.getIntFromByte(lenBytes, lenBytes.length);

			// 如果接收到的图片长度和服务器发过来的长度不一样的话那么就要重新获取;
			if (imageLen != picData.size()) {
				if (picData != null) {
					try {
						picData.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					picData = null;
				}

				return;
			}

			if (picData == null)
				return;
			// 写入文件中;

			byte[] destAray = picData.toByteArray();
			Bitmap m = BitmapFactory.decodeByteArray(destAray, 0,
					destAray.length);

			String selfAcc = SelfInfo.getInstance().getAccount().trim();
			AppManagerUtil.writeToSD("/HeadPhoto", m, selfAcc);

			String headphotopath = Environment.getExternalStorageDirectory()
					+ "/LoverHouse" + "/HeadPhoto" + "/" + selfAcc + ".png";
			SelfInfo.getInstance().setHeadpotoPath(headphotopath);
			Database.getInstance(
					GlobalApplication.getInstance().getApplicationContext())
					.appendSelfHeadPhoto(selfAcc, headphotopath);

			HeadPhotoHanddler mHeadPhotoHandler = new HeadPhotoHanddler();
			Bitmap endBm = null;
			if (SelfInfo.getInstance().getSex().equals("b")) {
				Bitmap frameBm = BitmapFactory.decodeResource(GlobalApplication
						.getInstance().getApplicationContext().getResources(),
						R.drawable.boy_photoframe);
				endBm = (mHeadPhotoHandler.handleHeadPhoto(headphotopath,
						frameBm, GlobalApplication.getInstance()
								.getApplicationContext()));
			} else {
				Bitmap frameBm = BitmapFactory.decodeResource(GlobalApplication
						.getInstance().getApplicationContext().getResources(),
						R.drawable.girl_photoframe);
				endBm = (mHeadPhotoHandler.handleHeadPhoto(headphotopath,
						frameBm, GlobalApplication.getInstance()
								.getApplicationContext()));
			}
			
			CommonBitmap.getInstance().addCacheBitmap(endBm, CommonBitmap.MYHEADPHO);
			CommonBitmap.getInstance().setMyHeadPhotoInit(true);

			if (picData != null) {
				try {
					picData.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				picData = null;
			}

		}
	}

	// step2 server 请求包 direct transmit
	public void processRequestMatch(byte[] str) {
		this.processLogin(str);

	}

	public void processAlbumHomePageState(byte[] str) {
		char type = (char) str[3];
		String state = "Fail";
		if (type == Protocol.RETURN_ALBUM_HOME_PAGE_SUCC) {
			CommonBitmap.getInstance().setAlubmPhotoInit(false);
			NetDataTypeTransform mND = new NetDataTypeTransform();
			String messtemp = mND.ByteArraytoString(str, str.length);
			String firstPicture = messtemp.substring(Protocol.HEAD_LEN);
			GlobalApplication mIns = GlobalApplication.getInstance();
			mIns.setFirstPicture(firstPicture);
			Database.getInstance(mIns.getApplicationContext())
					.updateFirstPicture(SelfInfo.getInstance().getAccount(),
							firstPicture);
			state = "Succ";
			// LOG(@"set album home page succ");
		} else if (type == Protocol.RETURN_ALBUM_HOME_PAGE_FAIL) {

		}
		Intent intent = new Intent(Protocol.ACTION_ALBUM_AlbumHomePageState);
		intent.putExtra(Protocol.EXTRA_DATA, state);
		GlobalApplication.getInstance().sendBroadcast(intent);

	}

	// 对方改变主页
	public void processChangeAlbumHomePage(byte[] str) {
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String messtemp = mND.ByteArraytoString(str, str.length);
		String firstPicture = messtemp.substring(Protocol.HEAD_LEN);
		GlobalApplication mIns = GlobalApplication.getInstance();
		mIns.setFirstPicture(firstPicture);
		Database.getInstance(mIns.getApplicationContext()).updateFirstPicture(
				SelfInfo.getInstance().getAccount(), firstPicture);
		CommonBitmap.getInstance().setAlubmPhotoInit(false);

	}
	
   public void processLightState(byte[] str)
	{
	    char type = (char)str[3];
	   String lightState = "";
	    if (type == Protocol.RETURN_TURNOFFLIGHT) {
//	      "Turn Off Light";
	    	lightState = String.format("%c",Protocol.LightOff);
	    	GlobalApplication.getInstance().setLightState(lightState);
	    }
	    else if (type == Protocol.RETURN_TURNONLIGHT){
//	      "Turn On Light";
	    	lightState = String.format("%c",Protocol.LightOn);
	    	GlobalApplication.getInstance().setLightState(lightState);
	       
	    }
	    Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
	    .updateLightState(SelfInfo.getInstance().getAccount(), lightState);
	
	    Intent intent = new Intent(Protocol.ACTION_ModifyLightState);
		
		GlobalApplication.getInstance().sendBroadcast(intent);
//	    [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationModifyLightState object:nil];
	}

	// /-----------------------------------------华丽丽的分割线 请求

	public void uploadTokenToServer() {
		GlobalApplication mGA = GlobalApplication.getInstance();
		SharedPreferences mSP = mGA
				.getSharedPreferences(Protocol.PREFERENCE_NAME,
						Activity.MODE_PRIVATE);
		String key = SelfInfo.getInstance().getAccount() + "PushUserId";
		String pushUserId = mSP.getString(key, "");

		if (pushUserId.equals("")) {
			PushManager.startWork(mGA
					.getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
					Protocol.api_key);
			return;
		}else if(!(PushManager.isPushEnabled(mGA.getApplicationContext()))){
			//推送接口被禁止
			PushManager.startWork(mGA
					.getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
					Protocol.api_key);
			return;
		}

		int len = 0;
		try {
			len = pushUserId.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.UPLOAD_TOKEN)
				.append(lenStr).append(pushUserId).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}
	
	public void uploadLogoutTokenToServer() {
		String pushUserId = "12";

		int len = 0;
		try {
			len = pushUserId.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.UPLOAD_TOKEN)
				.append(lenStr).append(pushUserId).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}


	/**
	 * handle login
	 * 
	 * @param acc
	 * @param pass
	 */
	public void Login(String acc, String pass) {

		String packet = acc + ' ' + pass;// [[NSString alloc]
											// initWithFormat:@"%@ %@",acc,pass];
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.LOGIN)
				.append(lenStr).append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	// 登出
	public void Logout() {

		int len = 1; // 结束符

		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.LOGOUT)
				.append(lenStr).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	/**
	 * handle can mail reg
	 * 
	 * @param mail
	 */
	public void CanMailReg(String mail) {
		String packet = mail;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = setLength(len);

		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.CAN_MAIL_REG)
				.append(lenStr).append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	// 上传第一个头像数据
	public void UploadFirstHeadPhotoData(String length, byte[] image) {
		int len = image.length + length.length() + 2; // 一个空格，一个结束符
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE)
				.append(Protocol.UPLOAD_FIRST_HEAD_PHOTO_DATA).append(lenStr)
				.append(length).append(' ');

		byte[] destAray = null;
		byte[] packet = null;
		try {
			packet = mSB.toString().getBytes("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {

			bos.write(packet);
			bos.write(image);
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

	/**
	 * upload append photo
	 * 
	 * @param acc
	 * @param image
	 */
	public void UploadAppendHeadPhotoData(byte[] image) {
		int len = image.length + 1; // 一个结束符
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE)
				.append(Protocol.UPLOAD_FOLLOW_HEAD_PHOTO_DATA).append(lenStr);
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
			bos.write(image);
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

	/**
	 * 上传头像结束
	 */
	public void UploadHeadPhotoDataFinish(int length) {

		String packet = length + "";
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1; // 1个结束符
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE)
				.append(Protocol.UPLOAD_HEAD_PHOTO_DATA_FINISH).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	// 添加配对
	public void AddMatch(String accept) {
		String packet = accept;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.ADD_MATCH)
				.append(lenStr).append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	/**
	 * register
	 * 
	 * @param acc
	 * @param pass
	 * @param sex
	 * @param bigname
	 * @param figure
	 */
	public void register(String acc, String pass, String sex, String bigname,
			String figure) {
		char mSex = 0x01;
		if (!(sex.equals("b"))) {
			mSex = 0x02;
		}
		String packet = acc + ' ' + pass + ' ' + mSex + ' ' + bigname + ' '
				+ figure;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = setLength(len);

		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.REGISTER)
				.append(lenStr).append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	// 修改密码
	public void ModifyPassword(String pass) {
		String packet = pass;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.MODIFY_PWD)
				.append(lenStr).append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	public void ModifySex(String sex) {
		String packet = sex;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.MODIFY_SEX)
				.append(lenStr).append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	public void ModifyBigName(String bigname) {
		String packet = bigname;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.MODIFY_BIG_NAME)
				.append(lenStr).append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	public void ModifyFigure(String figure) {
		// String packet = figure;
		int len = 0;
		try {
			len = figure.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = this.setLength(len);
		// int packet =(figure.charAt(0)-'0');
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.MODIFY_FIGURE)
				.append(lenStr).append(figure);
		// for(int i=0;i<figure.length();i++){
		// switch (packet) {
		// case 1:
		// mSB.append(Protocol.FIGURE_1);
		// break;
		//
		// case 2:
		// mSB.append(Protocol.FIGURE_2);
		// break;
		//
		// case 3:
		// mSB.append(Protocol.FIGURE_3);
		// break;
		//
		// case 4:
		// mSB.append(Protocol.FIGURE_4);
		// break;
		//
		// case 5:
		// mSB.append(Protocol.FIGURE_5);
		// break;
		//
		// case 6:
		// mSB.append(Protocol.FIGURE_6);
		// break;
		// default:
		// break;
		//
		//
		// }
		// }
		mSB.append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	public void ModifyStatus(String status) {
		String packet = status;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.MODIFY_STATUS)
				.append(lenStr).append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	// 修改对方小名
	public void ModifySmallName(String smallname) {
		String packet = smallname;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE)
				.append(Protocol.MODIFY_SMALL_NAME).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	// 修改小屋风格
	public void ModifyHouseStyle(String housestyle) {

		int len = 0;
		try {
			len = housestyle.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = this.setLength(len);

		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE)
				.append(Protocol.MODIFY_HOUSE_STYLE).append(lenStr)
				.append(housestyle).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	// 获取配对信息
	public void getMatchInfo() {

		int len = 1;
		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.GET_MATCH_INFO)
				.append(lenStr).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	public void getMatchHeadPhoto() {

		int len = 1;

		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE)
				.append(Protocol.GET_MATCH_HEADPHOTO).append(lenStr)
				.append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	public void getMyHeadPhoto() {

		int len = 1;

		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE)
				.append(Protocol.GET_SELF_HEADPHOTO).append(lenStr)
				.append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}

	// 接受配对
	public void clientAcceptMatch(String inviter) {
		String packet = inviter;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE)
				.append(Protocol.REG_ACCEPT_MATCH).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
		SelfInfo.getInstance().setMatch(true);
		GlobalApplication.getInstance().setTiAcc(inviter);
		SelfInfo.getInstance().setTarget(inviter);

		// 主动请求配对信息
		getMatchInfo();

	}

	// 拒绝配对
	public void clientRejectMatch(String inviter) {
		String packet = inviter;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE)
				.append(Protocol.REG_REFUSE_MATCH).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
		SelfInfo.getInstance().setMatch(false);
	}

	// 忘记密码
	public void clientForgetpassword(String account) {
		String packet = account;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE).append(Protocol.FORGET_PASSWORD)
				.append(lenStr).append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
		SelfInfo.getInstance().setMatch(false);
	}
	
	public boolean turnOnLight()
	{
		int len = 1;

	String lenStr = this.setLength(len);
	StringBuilder mSB = new StringBuilder();
	mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
			.append(Protocol.USER_PACKAGE)
			.append(Protocol.TURNONLIGHT).append(lenStr)
			.append('\0');
	 return AsynSocket.getInstance().sendData(mSB.toString());
	   
	}
	public boolean turnOffLight()
	{
		int len = 1;

		String lenStr = this.setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.USER_PACKAGE)
				.append(Protocol.TURNOFFLIGHT).append(lenStr)
				.append('\0');
		 return AsynSocket.getInstance().sendData(mSB.toString());
	}

}