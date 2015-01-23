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
			// sd ��������
			// ���յ� ACTION_MEDIA_EJECT �㲥֮��sd �����ǿ��Զ�д�ģ�
			// ֱ�����յ� ACTION_MEDIA_REMOVED��ACTION_MEDIA_UNMOUNTED�ȹ㲥֮��sd
			// ���Ų����Զ�д��
			String hint = "SD���Ѿ��γ���������ᣬ�Z����˫�˶�����ͷ��ȹ��ܲ�����";
			Toast.makeText(context, hint, Toast.LENGTH_LONG).show();
		} else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			// sd ������
		}

		if (action.equals(Protocol.ACTION_DISCONNECTED)) {
			// �������ǰ̨������
			if (GlobalApplication.getInstance().isAppOnForeground()) {
				if (!(SelfInfo.getInstance().isOnline())) {
					ConnectHandler.getInstance().connectToServer();
				}
			}
		}
		
		if (action.equals(Protocol.ACTION_ONCONNECTED)) {
			// ������ӳɹ����ж��Ƿ��Ѿ���¼
			if (GlobalApplication.getInstance().isAppOnForeground()) {
				if (!(SelfInfo.getInstance().isOnline())) {
					// �û������ߣ����µ�¼
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