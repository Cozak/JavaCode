package com.minus.xsocket.handler;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.util.Log;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.xsocket.MyHandler;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.util.NetWorkUtil;

public class ConnectHandler {
	

	private String IP = "115.28.19.187";
	//private String IP = "192.168.1.103";
	private boolean  isConnecting = false;
//	private  String IP="115.28.19.187";

//	private  String IP="192.168.1.106";
	//#define IP @"192.168.1.107"
    private int connectCount  = 0;

//	private int  nextConnectServerTimeInterval  = 15;
	
	private Timer   timerForTimeout = null;

	private   ConnectHandler(){
		  super();
		  isConnecting = false;
		  connectCount  = 0;
	}
	/**
	 * ����ģʽ,�߳�
	 * @return
	 */
	
	private static class  ConnectHandlerContainer{
		private static ConnectHandler instance = new ConnectHandler();
	}
	
	public static ConnectHandler getInstance(){
	    
		  return  ConnectHandlerContainer.instance;
	}
	
	public void connectToServer(){  
		if(!(this.isConnecting)){
			if(NetWorkUtil.isNetworkAvailable(GlobalApplication.getInstance().getApplicationContext()))
			{
		        AsynSocket.getInstance().connection(IP, new MyHandler());
		        this.isConnecting = true;
		        ConnectHandler.this.connectCount = 0;
			}
		
			if(this.timerForTimeout != null){
				this.timerForTimeout.cancel();
				this.timerForTimeout = null;
			}
			this.timerForTimeout = new Timer();
			TimerTask mTimerTask = new TimerTask(){

				@Override
				public void run() {
					didConnectToServer();
				}
				
			};
			timerForTimeout.schedule(mTimerTask, 4000L, 5000L);
		}
	}
	
	private void didConnectToServer() {
	    if (ConnectHandler.this.connectCount  <= 5) {
	    	if(AsynSocket.getInstance().isConnected()){
	    		ConnectHandler.this.connectCount = 0;
	    		this.isConnecting = false;
	    		/*
	    		if(ConnectHandler.this.timerForTimeout != null){
					ConnectHandler.this.timerForTimeout.cancel();
					ConnectHandler.this.timerForTimeout = null;
				}*/
	    	}else{
	    		if(NetWorkUtil.isNetworkAvailable(GlobalApplication.getInstance().getApplicationContext())){
	    			 AsynSocket.getInstance().connection(IP, new MyHandler());
	    			 if(ConnectHandler.this.connectCount>0){
//	    				 "���ӷ�������"
	    				 Intent intent = new Intent(Protocol.ACTION_CONNECTINGTOSERVER);
	    				 GlobalApplication.getInstance().sendBroadcast(intent);
	    			 }
	    			 ConnectHandler.this.connectCount++;
	    		}else{
//	    			"������"
	    			 Intent intent = new Intent(Protocol.ACTION_NONETWORK);
					 GlobalApplication.getInstance().sendBroadcast(intent);
					 ConnectHandler.this.connectCount = 0;
					 if(ConnectHandler.this.timerForTimeout != null){
							ConnectHandler.this.timerForTimeout.cancel();
							ConnectHandler.this.timerForTimeout = null;
						}
	    		}
	    	}
	    }else{
		   	 Intent intent = new Intent(Protocol.ACTION_CONNECTINGTOSERVERFAIL);
			 GlobalApplication.getInstance().sendBroadcast(intent);
			 ConnectHandler.this.connectCount = 0;
			 this.isConnecting = false;
			 /*
			 if(ConnectHandler.this.timerForTimeout != null) {
				ConnectHandler.this.timerForTimeout.cancel();
				ConnectHandler.this.timerForTimeout = null;
			}*/
	    }
	}


	 
}
