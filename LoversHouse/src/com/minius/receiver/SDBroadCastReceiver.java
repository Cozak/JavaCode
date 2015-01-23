package com.minius.receiver;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.sql_interface.Database;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.ConnectHandler;
import com.minus.xsocket.handler.UserPacketHandler;
import com.minus.xsocket.util.NetWorkUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class SDBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
			// sd 卡不可用
			// 接收到 ACTION_MEDIA_EJECT 广播之后，sd 卡还是可以读写的，
			// 直到接收到 ACTION_MEDIA_REMOVED、ACTION_MEDIA_UNMOUNTED等广播之后，sd
			// 卡才不可以读写。
			String hint = "SD卡已经拔出，想你相册，語音，双人动作，头像等功能不可用";
			Toast.makeText(context, hint, Toast.LENGTH_LONG).show();
		} else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			// sd 卡可用
		}

		if (action.equals(Protocol.ACTION_DISCONNECTED)) {
			// 如果程序前台才重连
			if (GlobalApplication.getInstance().isAppOnForeground()) {
				if (!(SelfInfo.getInstance().isOnline())) {
					ConnectHandler.getInstance().connectToServer();
				}
			}
		}
		
		if (action.equals(Protocol.ACTION_ONCONNECTED)) {
			// 如果连接成功就判断是否已经登录
			if (GlobalApplication.getInstance().isAppOnForeground()) {
				if (!(SelfInfo.getInstance().isOnline())) {
					// 用户不在线，重新登录
					UserTable selfInfo = Database.getInstance(
							GlobalApplication.getInstance().getApplicationContext()).getSelfInfo();

					if (!(selfInfo == null)) {
						SelfInfo.getInstance().setInfo(selfInfo.getAccount(),
								selfInfo.getPassword());
					}
					GlobalApplication.getInstance().setTargetDefault();
					GlobalApplication.getInstance().setCommonDefault();
					UserPacketHandler mUserPacketHandler = new UserPacketHandler();
					mUserPacketHandler.Login(SelfInfo.getInstance()
							.getAccount(), SelfInfo.getInstance().getPwd());
				}
			}
		}
		 if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
					|| action.equals("android.net.wifi.WIFI_STATE_CHANGED")
					|| action.equals(Protocol.ACTION_NONETWORK)){
		if ((NetWorkUtil.isNetworkAvailable(GlobalApplication.getInstance()
				.getApplicationContext()))) {
		
			if (!(AsynSocket.getInstance().isConnected())) {
				
					ConnectHandler.getInstance().connectToServer();
				
			}
		}
		
		}

	}

}