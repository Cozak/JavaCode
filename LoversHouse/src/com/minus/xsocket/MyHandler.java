package com.minus.xsocket;

import java.io.IOException;  
import java.nio.BufferUnderflowException;  
import java.nio.channels.ClosedChannelException;  
  
import org.xsocket.MaxReadSizeExceededException;  
import org.xsocket.connection.IConnectHandler;  
import org.xsocket.connection.IConnectionTimeoutHandler;
import org.xsocket.connection.IDataHandler;  
import org.xsocket.connection.IDisconnectHandler;  
import org.xsocket.connection.IIdleTimeoutHandler;
import org.xsocket.connection.INonBlockingConnection;  

import com.minus.lovershouse.BuildConfig;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Encrypt;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.HeartPacketHandler;

import android.content.Intent;
import android.util.Log;
public class MyHandler implements IDataHandler ,IConnectHandler ,IDisconnectHandler,IIdleTimeoutHandler ,IConnectionTimeoutHandler {  
  
    /** 
     * 连接时的操作 
     */  
    @Override  
    public boolean onConnect(INonBlockingConnection nbc) throws IOException,  
            BufferUnderflowException, MaxReadSizeExceededException {  
//        String  remoteName=nbc.getgetRemoteAddress().getHostName();  
		if (BuildConfig.DEBUG)
			Log.d("Login", "MyHandler onConnect");
    	boolean isOpen = nbc.isOpen();
        if(isOpen){
        	AsynSocket.getInstance().setConnected(true);
         	Intent intent = new Intent(Protocol.ACTION_ONCONNECTED);
    		GlobalApplication.getInstance().sendBroadcast(intent);
        }else{
        	AsynSocket.getInstance().setConnected(false);
        }

    	
       return false;  
    }  

    /** 
     * 连接断开时的操作 
     */  
    @Override  
    public boolean onDisconnect(INonBlockingConnection nbc) throws IOException {  
        // TODO Auto-generated method stub  
		if (BuildConfig.DEBUG)
			Log.d("Login", "MyHandler onDisconnect");
    	AsynSocket.getInstance().setConnected(false);
    	 SelfInfo.getInstance().setOnline(false);
    	 HeartPacketHandler.getInstance().stopHeart();
    	  	Intent intent = new Intent(Protocol.ACTION_DISCONNECTED);
    		GlobalApplication.getInstance().sendBroadcast(intent);
    		
       return false;  
    }  
    /** 
     *  
     * 接收到数据时候的处理 
     */  
    @Override  
    public boolean onData(INonBlockingConnection nbc) throws IOException,  
            BufferUnderflowException, ClosedChannelException,  
            MaxReadSizeExceededException {  

    	  nbc.setEncoding("UTF-8");
    	  byte[] headData = nbc.readBytesByLength(Protocol.HEAD_LEN);
          Encrypt.getInstance().decode(headData, headData.length);
    	  if(headData.length == Protocol.HEAD_LEN){
    	
    		  byte[] buffer = new byte[Protocol.HEAD_LEN];
    		  
    		  System.arraycopy(headData, 0, buffer, 0, Protocol.HEAD_LEN);
    		
    	        int len = 1000 * (buffer[4] - '0') + 100 * (buffer[5] - '0') + 10 * (buffer[6] - '0') + (buffer[7] - '0');
//    	        Log.v("test", "len : "+ len );
//    	        Log.v("location"," "+buffer[0]+" | "+buffer[1]+" | "+buffer[2]+" | "+(byte)buffer[3]+" | "+(byte)buffer[4]+" | "+buffer[5]+" | "+buffer[6]+" | "+buffer[7] );
//    	        Log.v("testsocket"," head : "+buffer[0]+" | "+buffer[1]+" | "+buffer[2]+" | "+(byte)buffer[3]+" | "+(byte)buffer[4]+" | "+buffer[5]+" | "+buffer[6]+" | "+buffer[7] );
    	        nbc.setHandler(new ContentHandler(this, len, buffer));
    	    }

//         Log.v("result","rex data ");
         return true;  
    }  
    
    /**  
     * 请求处理超时的处理事件  
     */  
    @Override  
    public boolean onIdleTimeout(INonBlockingConnection connection) throws IOException {   
        // TODO Auto-generated method stub   
    	
        return false;   
    }   
    /**  
     * 连接超时处理事件  
     */  
    @Override  
    public boolean onConnectionTimeout(INonBlockingConnection connection) throws IOException {   
        // TODO Auto-generated method stub   
    	AsynSocket.getInstance().setConnected(false);
        return false;   
    }   
    
    
  
}  
 
 
 

  
