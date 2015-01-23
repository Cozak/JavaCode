package com.minius.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.sql_interface.Database;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.ConnectHandler;
import com.minus.xsocket.handler.UserPacketHandler;
import com.minus.xsocket.util.NetWorkUtil;

public class ConnectBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();

		GlobalApplication mGa = GlobalApplication.getInstance();
		if(mGa == null ) return;
		if (action.equals(Protocol.ACTION_DISCONNECTED)) {
			// 如果程序前台才重连
			if (mGa.isAppOnForeground()) {
				if (!(SelfInfo.getInstance().isOnline())) {
					ConnectHandler.getInstance().connectToServer();
				}
			}
		}
		
		if (action.equals(Protocol.ACTION_ONCONNECTED)) {
			// 如果连接成功就判断是否已经登录
			if ((mGa.isAppOnForeground())) {
				if (SelfInfo.getInstance().isMainInit() && (!(SelfInfo.getInstance().isOnline()))) {
					// 用户不在线，重新登录
					UserTable selfInfo = Database.getInstance(
							mGa.getApplicationContext()).getSelfInfo();

					if (!(selfInfo == null)) {
						SelfInfo.getInstance().setInfo(selfInfo.getAccount(),
								selfInfo.getPassword());
					}
					mGa.setTargetDefault();
					mGa.setCommonDefault();
					UserPacketHandler mUserPacketHandler = new UserPacketHandler();
					mUserPacketHandler.Login(SelfInfo.getInstance()
							.getAccount(), SelfInfo.getInstance().getPwd());
				}
			}
		}
		 if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
					|| action.equals("android.net.wifi.WIFI_STATE_CHANGED")
					|| action.equals(Protocol.ACTION_NONETWORK)){
		if ((NetWorkUtil.isNetworkAvailable(mGa
				.getApplicationContext()))) {
			if (mGa.isAppOnForeground()) {
			if (!(AsynSocket.getInstance().isConnected())) {
				
					ConnectHandler.getInstance().connectToServer();
				
			}
			}
		}
		
		}

	}

}