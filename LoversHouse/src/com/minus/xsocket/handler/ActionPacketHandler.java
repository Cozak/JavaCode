package com.minus.xsocket.handler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.sql_interface.Database;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.util.NetDataTypeTransform;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

public class ActionPacketHandler {

	public String setLength(int len) {
		String mL = String.format("%04d", len);
		return mL;
	}

	// 响应

	public void process(byte[] str) {
		GlobalApplication mIns = GlobalApplication.getInstance();
		String matchname;
		if (!(SelfInfo.getInstance().getSmallName()
				.equals(Protocol.DecoraStyleDefault + ""))) {
			matchname = SelfInfo.getInstance().getSmallName();
		} else {
			matchname = mIns.getTiBigName();
		}
		char type = (char) str[3];
		NetDataTypeTransform mND = new NetDataTypeTransform();
		String alertMsg = null;
		String message = (mND.ByteArraytoString(str, str.length));
		String substr = message.substring(Protocol.HEAD_LEN);
		String[] arr = substr.split(" ");
		
			
			if ( type == Protocol.RECV_SINGLE_CUSTOM_ACTION_RETURN_ALL ) {
		        //获得所有的自定义动作
				Database mDb =  Database.getInstance(mIns.getApplicationContext());
				mDb.initActionTable();
				int len = arr.length ;
		        if ( len >= 2 && len % 2 == 0 ) {
		        	 if(len >= 10)  len = 10;
		            for ( int i = 0; i < len; i+=2 ) {
		            	mDb.addCustomAction(arr[i],arr[i+1], Protocol.SuccessFromServer+"");
		              }
		        }
		   
				Intent intent = new Intent(Protocol.ACTION_ALLSELFCUSTOMACTION);
				intent.putExtra(Protocol.EXTRA_DATA, message);
				GlobalApplication.getInstance().sendBroadcast(intent);
		    }
		    else if ( type == Protocol.RECV_SINGLE_CUSTOM_ACTION_RETURN_TARGET_ALL ) {
//		        LOG(@"RECV TARGET CUSTOM ACTION SUCC" );
		    	int len1 = arr.length ;
		        if (len1 >= 2 && (len1 % 2) == 0 ) {
		        if(len1 >= 10)  len1 = 10;
		        mIns.getTarCustomActionList().clear();
		        HashMap<String,String> tempList = new HashMap<String,String>();
		            for ( int i = 0; i < len1; i = i+2 ) {
		            	
		            	tempList.put(arr[i], arr[i+1]);
		            }
		         mIns.setTarCustomActionList(tempList);
		         tempList = null;
		        }
		  
		    }else	if (type == Protocol.RECV_SINGLE_ACTION_BEGINE) {
		    	// 对方单人动作开始
		    	int actionType = Integer.parseInt(arr[1]);
				switch (actionType) {
				case Protocol.SINGLE_ACTION_EAT:
					alertMsg = matchname + ":开始吃饭了";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setTiStatus(Protocol.SINGLE_ACTION_EAT+"");
					break;
				case Protocol.SINGLE_ACTION_SLEEP:
					alertMsg = matchname + ":开始睡觉了";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setSingle_Action(Protocol.SINGLE_ACTION_SLEEP);
//					mIns.setTiStatus(Protocol.SINGLE_ACTION_SLEEP+"");
					break;
				case Protocol.SINGLE_ACTION_LEARN:
					alertMsg = matchname + ":开始学习了";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setSingle_Action(Protocol.SINGLE_ACTION_LEARN);
//					mIns.setTiStatus(Protocol.SINGLE_ACTION_LEARN+"");
					break;
				case Protocol.SINGLE_ACTION_ANGRY:
					alertMsg = matchname + ":生气了";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setSingle_Action(Protocol.SINGLE_ACTION_ANGRY);
//					mIns.setTiStatus( Protocol.SINGLE_ACTION_ANGRY+"");
					break;
				case Protocol.SINGLE_ACTION_MISS:
					alertMsg = matchname + ":想你了";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setSingle_Action(Protocol.SINGLE_ACTION_MISS);
//					mIns.setTiStatus( Protocol.SINGLE_ACTION_MISS+"");
					break;
				default:
					return;
				}
				Database.getInstance(mIns.getApplicationContext()).updateTargetStatus(SelfInfo.getInstance().getAccount(),
						arr[1]);
				mIns.setTiStatus(arr[1]);
				//TODO 发本地notification
				Intent startServiceIntent2 = new Intent(Protocol.ServiceActionNewMsg); 
				startServiceIntent2.putExtra("content", alertMsg);
				GlobalApplication.getInstance().startService(startServiceIntent2);  
				Intent intent = new Intent(Protocol.ACTION_ACTIONPACKET);
				intent.putExtra(Protocol.EXTRA_DATA, message);
				GlobalApplication.getInstance().sendBroadcast(intent);
			}
			// 单人动作结束
			else if (type == Protocol.RECV_SINGLE_ACTION_END) {
				int actionType = Integer.parseInt(arr[1]);
				switch (actionType) {
				case Protocol.SINGLE_ACTION_EAT:
					alertMsg = matchname + ":吃完饭了";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setSingle_Action(-1);
					break;
				case Protocol.SINGLE_ACTION_SLEEP:
					alertMsg = matchname + ":睡觉结束了";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setSingle_Action(-1);
					break;
				case Protocol.SINGLE_ACTION_LEARN:
					alertMsg = matchname + ":学习结束了";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setSingle_Action(-1);
					break;
				case Protocol.SINGLE_ACTION_ANGRY:
					alertMsg = matchname + ":不生气了";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setSingle_Action(-1);
					break;
				case Protocol.SINGLE_ACTION_MISS:
					alertMsg = matchname + ":不想你了";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setSingle_Action(-1);
					break;
				default:
//					mIns.setSingle_Action(-1);
					return;

				}

				Database.getInstance(mIns.getApplicationContext()).updateTargetStatus(SelfInfo.getInstance().getAccount(),
						Protocol.ActionEnd+"");
				mIns.setTiStatus(Protocol.ActionEnd+"");
				//TODO 发本地notification
				Intent startServiceIntent2 = new Intent(Protocol.ServiceActionNewMsg); 
				startServiceIntent2.putExtra("content", alertMsg);
				GlobalApplication.getInstance().startService(startServiceIntent2); 
				Intent intent = new Intent(Protocol.ACTION_ACTIONPACKET);
				intent.putExtra(Protocol.EXTRA_DATA, message);
				GlobalApplication.getInstance().sendBroadcast(intent);
			}
			else if ( type == Protocol.RECV_SINGLE_CUSTOM_ACTION_BEGIN ) {
		        //对方自定义动作开始
		        int mActionType = Integer.parseInt(arr[1]);
		        mActionType =mActionType+ Protocol.SINGLE_ACTION_CUSTOM1-1;
		        if ( mActionType >= Protocol.SINGLE_ACTION_CUSTOM1 &&
		        		mActionType <=  Protocol.SINGLE_ACTION_CUSTOM5 ) {
		            String status = String.format("%d",mActionType);
		    		Database.getInstance(mIns.getApplicationContext()).updateTargetStatus(SelfInfo.getInstance().getAccount(),
		    				status);
					mIns.setTiStatus(status);
		            alertMsg =matchname+": "+"我开始了 "+arr[2]+"的状态";
		        }
//		        [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationActionInfo object:nil userInfo:@{@"ActionInfo": message}];
//		        [FunctionClass postLocationNotificationWithType:@"Action" AlertBody:alertMsg userInfo:[[NSDictionary alloc]initWithObjectsAndKeys:@"Action", @"type",nil]];
		      //TODO 发本地notification
		        Intent startServiceIntent2 = new Intent(Protocol.ServiceActionNewMsg); 
				startServiceIntent2.putExtra("content", alertMsg);
				GlobalApplication.getInstance().startService(startServiceIntent2); 
//				GlobalApplication.getInstance().startService(startServiceIntent2); 
				Intent intent = new Intent(Protocol.ACTION_ACTIONPACKET);
				intent.putExtra(Protocol.EXTRA_DATA, message);
				GlobalApplication.getInstance().sendBroadcast(intent);
		    }
		    else if ( type == Protocol.RECV_SINGLE_CUSTOM_ACTION_END ) {
		        //对方自定义动作结束
		        int mType =Integer.parseInt(arr[1]);
		        if ( mType >= 1 && mType <= 5 ) {
	                String content = mIns.getTarCustomActionList().get(arr[1]);
//		            NSDictionary *actionDict = [[Database getInstance] getAllActionForUser:[SelfInfo getInstance].account];
		            alertMsg =matchname+ ":"+"我结束了 "+content+"的状态";
		      
		            Intent startServiceIntent2 = new Intent(Protocol.ServiceActionNewMsg); 
					startServiceIntent2.putExtra("content", alertMsg);
					GlobalApplication.getInstance().startService(startServiceIntent2); 
					
		            Database.getInstance(mIns.getApplicationContext()).updateTargetStatus(SelfInfo.getInstance().getAccount(),
							Protocol.ActionEnd+"");
					mIns.setTiStatus(Protocol.ActionEnd+"");
		        }
//		        [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationActionInfo object:nil userInfo:@{@"ActionInfo": message}];
//		        [FunctionClass postLocationNotificationWithType:@"Action" AlertBody:alertMsg userInfo:[[NSDictionary alloc]initWithObjectsAndKeys:@"Action", @"type",nil]];
		      //TODO 发本地notification
				Intent intent = new Intent(Protocol.ACTION_ACTIONPACKET);
				intent.putExtra(Protocol.EXTRA_DATA, message);
				GlobalApplication.getInstance().sendBroadcast(intent);
		    }
			// 双人动作请求
			else if (type == Protocol.RECV_COUPLE_ACTION_REQUEST) {
				int actionType = Integer.parseInt(arr[1]);
				mIns.setCouple_ActionMsg(message);
				switch (actionType) {
				case Protocol.HUG:
					alertMsg = matchname + ":我想抱抱你";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.HUG);
					break;
				case Protocol.KISS:
					alertMsg = matchname + ":亲爱的亲亲我好咩";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.KISS);
					break;
				case Protocol.ABUSE:
					alertMsg = matchname + ":皮痒了吧";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.ABUSE);
					break;
				case Protocol.SEX:
					alertMsg = matchname + ":来啊，快活啊…反正有大把时光";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.SEX);
					break;
				case Protocol.PETTING:
//					alertMsg = matchname + ":摸了下你的头";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.PETTING);
					break;
				case Protocol.PINCHEDFACE:
					alertMsg = matchname + ":捏了下你的脸";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.PINCHEDFACE);
					break;
				default:
//					mIns.setCouple_Action(-1);
					return;
				}
				//TODO 发本地notification
				 Intent startServiceIntent2 = new Intent(Protocol.ServiceActionNewMsg); 
					startServiceIntent2.putExtra("content", alertMsg);
					GlobalApplication.getInstance().startService(startServiceIntent2); 
				Intent intent = new Intent(Protocol.ACTION_ACTIONPACKET);
				intent.putExtra(Protocol.EXTRA_DATA, message);
				GlobalApplication.getInstance().sendBroadcast(intent);

			}
			// 双人动作同意
			else if (type == Protocol.RECV_COUPLE_ACTION_ACCEPT) {
				mIns.setCouple_ActionMsg(message);
				int actionType = Integer.parseInt(arr[1]);
				switch (actionType) {
				case Protocol.HUG:
					alertMsg = matchname + ":好啊，抱抱";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.HUG);
					break;
				case Protocol.KISS:
					alertMsg = matchname + ":好啊，亲亲";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.KISS);
					break;
				case Protocol.SEX:
					alertMsg = matchname + ":春宵一刻值千金！";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.SEX);
					break;
		
				default:
			
					return;
				}
				//TODO 发本地notification
				 Intent startServiceIntent2 = new Intent(Protocol.ServiceActionNewMsg); 
					startServiceIntent2.putExtra("content", alertMsg);
					GlobalApplication.getInstance().startService(startServiceIntent2); 
				Intent intent = new Intent(Protocol.ACTION_ACTIONPACKET);
				intent.putExtra(Protocol.EXTRA_DATA, message);
				GlobalApplication.getInstance().sendBroadcast(intent);
			}
			// 双人动作拒绝
			else if(type == Protocol.RECV_COUPLE_ACTION_REJECT){
				mIns.setCouple_ActionMsg(message);
				int actionType = Integer.parseInt(arr[1]);
				switch (actionType) {
				case Protocol.HUG:
					alertMsg = matchname + ":你的表现不足以打动我";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.HUG);
					break;
				case Protocol.KISS:
					alertMsg = matchname + ":不给亲";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.KISS);
					break;
				case Protocol.SEX:
					alertMsg = matchname + ":色！";
//					SelfInfo.getInstance().setAction(alertMsg);
//					mIns.setCouple_Action(Protocol.SEX);
					break;
			
				default:
					return;
				}
				//TODO 发本地notification
				 Intent startServiceIntent2 = new Intent(Protocol.ServiceActionNewMsg); 
					startServiceIntent2.putExtra("content", alertMsg);
					GlobalApplication.getInstance().startService(startServiceIntent2); 
					
				Intent intent = new Intent(Protocol.ACTION_ACTIONPACKET);
				intent.putExtra(Protocol.EXTRA_DATA, message);
				GlobalApplication.getInstance().sendBroadcast(intent);
				
			}else if ( type == Protocol.RECV_SINGLE_CUSTOM_ACTION_ADD_SUCCESS ) {
		        //TYPEID＋内容
		        if ( arr.length >= 2 ) {
		        	Database.getInstance(mIns.getApplicationContext())
		        	.updateWholeAction(arr[0],arr[1], Protocol.SuccessFromServer+"");
		        	Intent intent = new Intent(Protocol.ACTION_EDITCUSTOMACTION);
		        	intent.putExtra(Protocol.EXTRA_DATA, message);
					GlobalApplication.getInstance().sendBroadcast(intent);
					
		            //自定义动作增加成功
//		            [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationFeedbackSuccMessage object:nil];
//		            [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationButtonBarShouldChange
//		                                                                object:nil
//		                                                              userInfo:userInfo];
		        }
		    }
		    else if ( type == Protocol.RECV_SINGLE_CUSTOM_ACTION_ADD_FAIL ) {
		    	Intent intent = new Intent(Protocol.ACTION_EDITCUSTOMACTION);
	        	intent.putExtra(Protocol.EXTRA_DATA, message);
				GlobalApplication.getInstance().sendBroadcast(intent);
				
		        //自定义动作增加失败
//		        [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationFeedbackFailMessage object:nil];
		    }
		    else if ( type == Protocol.RECV_SINGLE_CUSTOM_ACTION_DELETE_SUCCESS ) {
		        //自定义动作删除成功
		        if (arr.length >= 1 ) {
		        	Database.getInstance(mIns.getApplicationContext())
		        	.updateActionStatus(arr[0], Protocol.ActionRemoved+"");
		        	
		        	Intent intent = new Intent(Protocol.ACTION_EDITCUSTOMACTION);
		        	intent.putExtra(Protocol.EXTRA_DATA, message);
					GlobalApplication.getInstance().sendBroadcast(intent);
//		            [[Database getInstance] deleteActionOfType:arr[0] ForUser:@""];

//		            [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationFeedbackSuccMessage object:nil];
//		            NSDictionary *userInfo = @{@"type":@"delete"};
//		            [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationButtonBarShouldChange
//		                                                                object:nil
//		                                                              userInfo:userInfo];

		        }
		    } else if ( type == Protocol.RECV_SINGLE_CUSTOM_ACTION_DELETE_FAIL ) {
		        //自定义动作删除失败
		    	Intent intent = new Intent(Protocol.ACTION_EDITCUSTOMACTION);
	        	intent.putExtra(Protocol.EXTRA_DATA, message);
				GlobalApplication.getInstance().sendBroadcast(intent);
//		        [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationFeedbackFailMessage object:nil];
		    } else if ( type == Protocol.RECV_SINGLE_CUSTOM_ACTION_UPDATE_SUCCESS ) {
		        //自定义动作修改成功
		        if (arr.length >= 2 ) {
		        	Database.getInstance(mIns.getApplicationContext())
		        	.updateWholeAction(arr[0],arr[1], Protocol.SuccessFromServer+"");
		        	
		        	Intent intent = new Intent(Protocol.ACTION_EDITCUSTOMACTION);
		        	intent.putExtra(Protocol.EXTRA_DATA, message);
					GlobalApplication.getInstance().sendBroadcast(intent);
//		            [[Database getInstance] updateActionOfType:arr[0] ForUser:@"" WithContent:arr[1]];

//		            [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationFeedbackSuccMessage object:nil];
//		            NSDictionary *userInfo = @{@"type":@"update"};
//		            [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationButtonBarShouldChange
//		                                                                object:nil
//		                                                              userInfo:userInfo];
		        }
		    } else if ( type == Protocol.RECV_SINGLE_CUSTOM_ACTION_UPDATE_FAIL ) {
		        //自定义动作修改失败
		    	Intent intent = new Intent(Protocol.ACTION_EDITCUSTOMACTION);
	        	intent.putExtra(Protocol.EXTRA_DATA, message);
				GlobalApplication.getInstance().sendBroadcast(intent);
//		        LOG(@"Update Custom Action Fail!");
//		        [SelfInfo getInstance].hudlabel = @"设置失败";
//		        [[NSNotificationCenter defaultCenter] postNotificationName:USNotificationFeedbackFailMessage object:nil];
		    }
	}

	

	// ----------------------------请求-------------------------------

	/**
	 * 
	 * @param acc
	 * @param time
	 *            yyyy-MM-dd-HH:mm:ss
	 * @param type
	 */
	public boolean SendSingleActionBegin( String time, int type) {
	
		String packet = time + ' ' + type;
		int len = packet.length() + 1;
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_SINGLE_ACTION_BEGINE).append(lenStr)
				.append(packet).append('\0');
		return AsynSocket.getInstance().sendData(mSB.toString());
	}

	public boolean  SendSingleActionEnd(String time, int type) {
	
		String packet =  time + ' ' + type;
		int len = packet.length() + 1;
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_SINGLE_ACTION_END).append(lenStr)
				.append(packet).append('\0');
		return AsynSocket.getInstance().sendData(mSB.toString());
	}

	public boolean SendCoupleActionRequest( String time, int type) {
		
		String packet = time + ' ' + type;
		int len = packet.length() + 1;
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_COUPLE＿ACTION_REQUEST).append(lenStr)
				.append(packet).append('\0');
		return AsynSocket.getInstance().sendData(mSB.toString());
	}

	public boolean  SendCoupleActionReject( String time, int type) {
	
		String packet = time + ' ' + type;
		int len = packet.length() + 1;
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_COUPLE_ACTION_REJECT).append(lenStr)
				.append(packet).append('\0');
		return AsynSocket.getInstance().sendData(mSB.toString());
	}

	public boolean  SendCoupleActionAccept( String time, int type) {
	
		String packet =  time + ' ' + type;
		int len = packet.length() + 1;
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_COUPLE_ACTION_ACCPT).append(lenStr)
				.append(packet).append('\0');
		return AsynSocket.getInstance().sendData(mSB.toString());
	}
//	获得所有动作
	public void RequsetUnacceptAction() {
		
		int len = 1;
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_REQUEST_UNACCEPT_ACTION).append(lenStr)
				.append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}
	
	/*
	 * 获得自己的所有自定义动作
	 */
	public void getAllSelfCustomAction() {
		int len = 1;
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_SINGLE_CUSTOM_ACTION_GETALL).append(lenStr)
				.append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}
	/*
	 * 获得对方的所有自定义动作
	 */
	public void getAllTargetCustomAction() {
		int len = 1;
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_SINGLE_CUSTOM_ACTION_GETTARGET_ALL).append(lenStr)
				.append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());
	}
	
	public void addSelfCustomActionWithType(String  type,String customString) {
		String packet =  type + ' ' +customString;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length+1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_SINGLE_CUSTOM_ACTION_ADD).append(lenStr)
				.append(packet).append('\0');
		AsynSocket.getInstance().sendData(mSB.toString());

	}

	public boolean deleteSelfCustomActionWithType(String type)
	{
		String packet = type;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length+1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_SINGLE_CUSTOM_ACTION_DELETE).append(lenStr)
				.append(packet).append('\0');
		return AsynSocket.getInstance().sendData(mSB.toString());
	}

	public boolean updateSelfCustomActionWithType(String type,String newString) {
		String packet =  type + ' ' +newString;
		int len = 0;
		try {
			len = packet.getBytes("UTF-8").length+1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_SINGLE_CUSTOM_ACTION_UPDATE).append(lenStr)
				.append(packet).append('\0');
		return AsynSocket.getInstance().sendData(mSB.toString());
	}

    public boolean sendSingleCustomActionBeginTime(String time,String type) {
    	String packet = time + ' ' +type;
    	int len = 0;
		try {
			len = packet.getBytes("UTF-8").length+1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_SINGLE_CUSTOM_ACTION_BEGIN).append(lenStr)
				.append(packet).append('\0');
		return AsynSocket.getInstance().sendData(mSB.toString());
	}

    public boolean SendSingleCustomActionEndTime(String time ,int type) {
    	String packet = time + ' ' +type;
    	int len = 0;
		try {
			len = packet.getBytes("UTF-8").length+1;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lenStr = setLength(len);
		StringBuilder mSB = new StringBuilder();
		mSB.append(Protocol.VERSION).append(Protocol.ANDROID)
				.append(Protocol.ACTION_PACKET)
				.append(Protocol.SEND_SINGLE_CUSTOM_ACTION_END).append(lenStr)
				.append(packet).append('\0');
		return AsynSocket.getInstance().sendData(mSB.toString());
	}
}
