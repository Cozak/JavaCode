package com.minus.xsocket.asynsocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import org.xsocket.connection.IHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.MaxConnectionsExceededException;
import org.xsocket.connection.NonBlockingConnectionPool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.xsocket.MyHandler;
import com.minus.xsocket.asynsocket.protocol.Encrypt;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.UserPacketHandler;

public class AsynSocket {
	
	
	private   INonBlockingConnection nbc = null;
	private NonBlockingConnectionPool pool =null;
	private boolean isConnected = false;
    
	private static String defaultHost = "1";
	private MyHandler mHandler = null;
	private static int PORT =9000;
	//private static int PORT =9001;
//	 private static int PORT =80;

	private AsynSocket(){
		pool =  new NonBlockingConnectionPool();
		isConnected = false;
		
	}
	/**
	 * 单例模式,线程
	 * @return
	 */
		
		private static class AsynSocketContainer{
			private static AsynSocket instance = new AsynSocket();
		}
		
		public static AsynSocket getInstance(){
		       
			  return AsynSocketContainer.instance;
		}
		
		public void closeSocket(){
			try{
				 //将信息清除缓存，写入服务器端  
				
		           nbc.flush();  
		         
		           nbc.close();  
		           nbc = null;
			}catch(Exception e){
				
			}
		}
		
		/**
		 * 用连接池处理链接
		 * @param host
		 * @param appHandler
		 */
		public void connection(String host,IHandler appHandler){
			if(this.isConnected) return;
			try {
				mHandler = (MyHandler) appHandler;
				if(nbc != null) nbc.close();
				nbc = pool.getNonBlockingConnection(InetAddress.getByName(host), PORT, appHandler, true, 3000);
				defaultHost = host;
				
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MaxConnectionsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		

		}
		
		public void sendImgData(byte[] mData){
//			if(nbc == null){
//			try {
//				nbc = pool.getNonBlockingConnection(defaultHost, PORT, new MyHandler());
//	
//			} catch (SocketTimeoutException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (MaxConnectionsExceededException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}}
			if(!(this.isConnected)){
				Intent intent = new Intent(Protocol.ACTION_DISCONNECTED);
				GlobalApplication.getInstance().sendBroadcast(intent);
				return ;
			}
		
				try {
					  Encrypt.getInstance().encode(mData, mData.length);
	
					nbc.write(mData);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		
		public boolean  sendData(String mSB){
		
			if(!(this.isConnected)){
				Intent intent = new Intent(Protocol.ACTION_DISCONNECTED);
				GlobalApplication.getInstance().sendBroadcast(intent);
				return false;
			}
//			if(nbc == null){
//				try {
//					nbc = pool.getNonBlockingConnection(defaultHost,PORT,mHandler);
//				} catch (SocketTimeoutException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (MaxConnectionsExceededException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}}
					
					try {
//						 	try {
//				
//					    		byte[] m = mSB.getBytes("UTF-8");
//					    		StringBuilder  test =new StringBuilder("byte : ");
//					    		for(int i = 0;i< m.length;i++){
//					    			test.append(m[i]).append(  "  || ");	
//					    		}
//					    		Log.v("testsocket","nbc send 001 " +  test.toString());
//							} catch (UnsupportedEncodingException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
						
					
			            byte[] temp = mSB.getBytes("UTF-8");
			            Encrypt.getInstance().encode(temp, temp.length);
			       
				    	
//				    		StringBuilder  test1 =new StringBuilder("byte : ");
//				    		for(int i = 0;i< temp.length;i++){
//				    			test1.append(temp[i]).append(  "  || ");
//				    			
//				    		}
//				    		Log.v("testsocket","nbc test1 002  " +  test1.toString());
			         
						nbc.write(temp);
						return true;
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
			     	
						e.printStackTrace();
						return false;
					}

				
		}
		
		
		
		public void setConnected(boolean isConnected) {
			this.isConnected = isConnected;
		}

	//判断是否还连接者
	public boolean isConnected()
		{
		if(nbc != null){
		return this.isConnected;
		}else{
			return false;
		}

		}
}
