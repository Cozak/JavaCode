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
import com.minus.xsocket.handler.ActionPacketHandler;
import com.minus.xsocket.handler.AlbumPacketHandler;
import com.minus.xsocket.handler.CalendarHandler;
import com.minus.xsocket.handler.ChatPacketHandler;
import com.minus.xsocket.handler.DiaryPacketHandler;
import com.minus.xsocket.handler.HeartPacketHandler;
import com.minus.xsocket.handler.LocationHandler;
import com.minus.xsocket.handler.UserPacketHandler;

import android.content.Intent;
import android.util.Log;

//	分模块处理
public class ContentHandler implements IDataHandler ,IConnectHandler ,IDisconnectHandler,IIdleTimeoutHandler ,IConnectionTimeoutHandler {
	   private int remaining = 0;
	   private byte[] buffer;
	   private MyHandler mdl = null;
	   public ContentHandler(MyHandler mdl, int dataLength, byte[] buffer2) {  
	      this.mdl = mdl;
	      remaining = dataLength;
	      this.buffer = buffer2;
	      //...
	   }

	@Override
	public boolean onData(INonBlockingConnection nbc) throws IOException,
			BufferUnderflowException, ClosedChannelException,
			MaxReadSizeExceededException {
		  nbc.setEncoding("UTF-8");
            int available = nbc.available();
        
	      int lengthToRead = remaining;
//	      Log.v("testsocket","lengthToRead  " + lengthToRead + " available  "+ available);
//	      if (available < remaining) {
//	         lengthToRead = available;
//	      
//	      }
	 
	      byte[] buffers =nbc.readBytesByLength(lengthToRead);
	      Encrypt.getInstance().decode(buffers, buffers.length);
	      remaining -= lengthToRead;
	      // processing the data
	      byte[] allBuff = new byte[lengthToRead + this.buffer.length];
	      System.arraycopy(this.buffer,0, allBuff,0, this.buffer.length);
	      System.arraycopy(buffers, 0, allBuff, buffer.length ,buffers.length);
//	      Log.v("testsocket","read data buffer length  004 " + buffers.length +"   remaining " + remaining);
//	      Log.v("test","read data buffer length  004 " + allBuff.length +"   remaining " + remaining);
	      byte[] ptr =allBuff;
	        
	        char type =(char) ptr[2];
	  
	        switch (type) {
	            case Protocol.USER_PACKAGE:
//	            	Log.v("result","user  packetage ");
	            	this.processUserPacket(ptr);

	                break;
	            case Protocol.HEART_PACKAGE:

	            	processHeartPacket(ptr);
	                break;
	            case Protocol.DIARY_PACKAGE:
//	            	Log.v("result","DIARY_PACKAGE");
//	            	Log.v("diary","DIARY_PACKAGE");
	            	processDiaryPacket(ptr);
                          break;
	            case Protocol.CALENDAR_PACKAGE:
//	            	Log.v("result","CALENDAR_PACKAGE");
	            	processCalendarPacket(ptr);
                           break;
	            case Protocol.PICTURE_PACKAGE:
	            	processAlbumPacket(ptr);
//	            	Log.v("result","PICTURE_PACKAGE");
	            	break;
	            case Protocol.LOCATION_PACKAGE:
	            	processLocationPacket(ptr);
//	            	Log.v("result","LOCATION_PACKAGE");
	            	break;
	            case Protocol.CHAT_PACKAGE:
//	            	Log.v("result","SOUND_PACKAGE");
	            	processChatPacket(ptr);
	            	break;
	            case Protocol.ACTION_PACKET:
	            	processActionPacket(ptr);
//	            	Log.v("result","ACTION_PACKAGE");
	            	break;
	            default:
	                break;
	      
	        }
	      if (remaining == 0) {  
	         nbc.setAttachment(mdl);
	         nbc.setHandler(mdl);
	      }
	      return true;  
	}
	

	
	public void processUserPacket( byte[] string){
		
		UserPacketHandler mUserPacket = new UserPacketHandler();
		mUserPacket.process(string);
	  
	}
	
	public void processDiaryPacket(byte[] string){
		DiaryPacketHandler mDia = new DiaryPacketHandler();
		mDia.process(string);
		
	}
	
	public void processLocationPacket(byte[] string){
		LocationHandler mLP = new LocationHandler();
		mLP.process(string);
	}
	
	public void processHeartPacket(byte[] string){
		HeartPacketHandler.getInstance().process(string);
	}
   
	
public void processChatPacket( byte[] string){
		
		ChatPacketHandler mChatPacket = new ChatPacketHandler();
		mChatPacket.processChat(string);
	  
	}
public void processActionPacket(byte[] string){
	ActionPacketHandler mActionPacket = new ActionPacketHandler();
	mActionPacket.process(string);
}

public void processAlbumPacket(byte[] string){
	AlbumPacketHandler mAlbumPacket = new AlbumPacketHandler();
	mAlbumPacket.processAlbum(string);
}

public void processCalendarPacket(byte[] string){
	CalendarHandler.getInstance().process(string);
}

	
	@Override
	public boolean onDisconnect(INonBlockingConnection arg0) throws IOException {
		if (BuildConfig.DEBUG)
			Log.d("Login", "ContentHandler onDisconnect");
		AsynSocket.getInstance().setConnected(false);
		SelfInfo.getInstance().setOnline(false);
		HeartPacketHandler.getInstance().stopHeart();
   	  	Intent intent = new Intent(Protocol.ACTION_DISCONNECTED);
   		GlobalApplication.getInstance().sendBroadcast(intent);
		return false;
	}

	@Override
	public boolean onConnect(INonBlockingConnection nbc) throws IOException,
			BufferUnderflowException, MaxReadSizeExceededException {
		// TODO Auto-generated method stub
		if (BuildConfig.DEBUG)
			Log.d("Login", "ContentHandler onConnect");
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

	@Override
	public boolean onConnectionTimeout(INonBlockingConnection arg0)
			throws IOException {
		AsynSocket.getInstance().setConnected(false);
		return false;
	}

	@Override
	public boolean onIdleTimeout(INonBlockingConnection arg0)
			throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

}

