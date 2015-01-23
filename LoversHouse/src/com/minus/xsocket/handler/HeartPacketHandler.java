package com.minus.xsocket.handler;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.sql_interface.Database;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
public class HeartPacketHandler{
	
	private   HeartPacketHandler(){
		  super();
		 
}
	/**
	 * ����ģʽ,�߳�
	 * @return
	 */
		
		private static class  HeartPacketHandlerContainer{
			private static HeartPacketHandler  instance = new HeartPacketHandler();
		}
		
		public static HeartPacketHandler getInstance(){
		    
			  return  HeartPacketHandlerContainer.instance;
		}
		
//public void setAccount(String acc){
//	  this.accout = acc;
//		 
//	  Log.v("heart","user account  " + this.accout);
//}
public String setLength(int len){
    String  mL = String.format("%04d", len);
	 
    return mL;
}

//part1 process response 
 public void process(byte [] str){
//	 Log.v("heart","return heart packet " +str[3]);
    char type = (char) str[3];
    switch (type) {
    case Protocol.RETURN_HEARTMSG:
    	processHeartPacketFromServer();
    	break;
   
        default:
            break;
    }


}

//����ӷ���������������������
public void processHeartPacketFromServer()
{
   //����һ����ʱ������ʱ����Զ��ط�������;
//   ("�յ�����������ȷ�ϰ�");
   
   this.timeoutCount = 0;
   if(this.timerForTimeout != null){
		this.timerForTimeout.cancel();
		this.timerForTimeout = null;
	}
}

public void startHeart() throws UnsupportedEncodingException
{
//   NSLog(@"������������");
   this.timeoutCount = 0;
   sendHeartPacketToServer();
   //����40�붨ʱ�������ơ�
	if(this.timerForHeart != null){
		this.timerForHeart.cancel();
		this.timerForHeart = null;
	}
	this.timerForHeart = new Timer();
	TimerTask mTimerTask = new TimerTask(){

		@Override
		public void run() {
//			Log.v("heart","timerForHeart send heart packet");
			try {
				sendHeartPacketToServer();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	};
	 timerForHeart.schedule(mTimerTask, 40000L, 40000L);
 
}

public void stopHeart()
{
//   "�ر���������");
   this.timeoutCount = 0;
   if(this.timerForHeart != null){
		this.timerForHeart.cancel();
		this.timerForHeart = null;
	}
   if(this.timerForTimeout != null){
		this.timerForTimeout.cancel();
		this.timerForTimeout = null;
	}
}


///-----------------------------------------�������ķָ���   ����

//������������������
public void sendHeartPacketToServer() throws UnsupportedEncodingException
{
	
	
	    int len =1;
	    String  lenStr = setLength(len);
	    StringBuilder mSB = new StringBuilder();
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
	    .append(Protocol.HEART_PACKAGE).append(Protocol.HEART_MESSAGE)
	    .append(lenStr).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());
 
  //ÿ�η���һ����������������֮�������һ��5�볬ʱ��ʱ��;
		if(this.timerForTimeout != null){
			this.timerForTimeout.cancel();
			this.timerForTimeout = null;
		}
		this.timerForTimeout = new Timer();
		TimerTask mTimerTask = new TimerTask(){

			@Override
			public void run() {
//				Log.v("heart","time out !!!! "+ HeartPacketHandler.this.timeoutCount);
				if(HeartPacketHandler.this.timerForTimeout != null){
					HeartPacketHandler.this.timerForTimeout.cancel();
					HeartPacketHandler.this.timerForTimeout = null;
				}
				    
				    if (HeartPacketHandler.this.timeoutCount <= 3)
				    {
				    	HeartPacketHandler.this.timeoutCount ++;
				     
				    }
				    else
				    {
//				       "3����������ʱ�ˣ�����ȷ������");
				    	
				        if (!(AsynSocket.getInstance().isConnected()))
				        {
				        	ConnectHandler.getInstance().connectToServer();
				          
				        }
				        else
				        {
//				            "�ͻ����������ʧȥ������ϵ");
//				        	stopHeart();
				        }
				    }
			}
			
		};
		 timerForTimeout.schedule(mTimerTask, 5000L, 15000L);
  
}

//������������������
public void sendHeartPacketToServer1() throws UnsupportedEncodingException
{
	
	  
	    int len = 1;
	    String  lenStr = setLength(len);
	    StringBuilder mSB = new StringBuilder();
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
	    .append(Protocol.HEART_PACKAGE).append(Protocol.HEART_MESSAGE)
	    .append(lenStr).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());

}



 private int  timeoutCount = 0;
 private Timer timerForTimeout = null;
 private Timer timerForHeart = null;

}
