package com.minus.xsocket.handler;

import android.content.Intent;
import android.util.Log;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.util.NetDataTypeTransform;

public class LocationHandler {
	public String setLength(int len){
	    String  mL = String.format("%04d", len);
	 
	    return mL;
	}

	//part1 process response 
	 public void process(byte [] str){
//		 Log.v("location","41receic " +str[3]);
	    char type = (char) str[3];
	    switch (type) {
	    case Protocol.GET_LOCATION_SUCC_RES:
	    case Protocol.GET_USER_NOEXIST_RES:
	    case Protocol.GET_MATCH_USER_LOCATION_NOEXIST_RES:
	    	processLocation(str);
	    	break;
	   
	        default:
	            break;
	    }


	}


	//part2,process response 广播到到对应的界面
	 /**
	  * 响应
	  * @param str
	  */
	public void processLocation(byte [] str)
	{
		   NetDataTypeTransform mND = new  NetDataTypeTransform();
		    String mess = mND.ByteArraytoString(str, str.length);
//		    Log.v("result"," 发送 广播 "+ mess);
			Intent intent = new Intent(Protocol.ACTION_LOCATIONPACKET);
			intent.putExtra(Protocol.EXTRA_DATA,mess);
			GlobalApplication.getInstance().sendBroadcast(intent);
	}


	
	///-----------------------------------------华丽丽的分割线   请求
	
	public void UploadPosition(String acc, float latitude ,float longitude)
	{
//		 Log.v("location","up load pos " + acc);//acc+' '+
	    String packet = AppManagerUtil.getCurDateInServer()
	    		+' '+String.format("%f", longitude)+' '+String.format("%f",latitude);
	    int len =packet.length()+1;
        String lenStr = setLength(len);
        StringBuilder mSB = new StringBuilder();
//	    Log.v("result", "lenStr :"+lenStr);
	    //11120011
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.LOCATION_PACKAGE)
	    .append(Protocol.UPLOAD_LOCATION).append(lenStr).append(packet).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());
	  
	}

	public void UploadPosition(String acc ,String  cityname ,double latitude ,double longitude)
	{
	    String  packet =acc +' '+ cityname + ' '+ String.format("%f",latitude)+' '+String.format("%f", longitude);
	    int len =packet.length()+1;
        String lenStr = setLength(len);
        StringBuilder mSB = new StringBuilder();
//	    Log.v("result", "lenStr :"+lenStr);
	    //11120011
	    mSB.append(Protocol.VERSION).append(Protocol.ANDROID).append(Protocol.LOCATION_PACKAGE)
	    .append(Protocol.REQUEST_LOCATION).append(lenStr).append(packet).append('\0');
	    AsynSocket.getInstance().sendData(mSB.toString());
	}
}
