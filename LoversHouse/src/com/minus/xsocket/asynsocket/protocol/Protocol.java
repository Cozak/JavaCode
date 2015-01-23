package com.minus.xsocket.asynsocket.protocol;

public class Protocol {
	public static final int MAX_PACKET  = 8192;
    public static final int  HEAD_LEN  =  8;
    
    
	/////////////////////协议版本(Protocol)
	public static final char VERSION  = 0x01;

	/////////////////////客户端/服务器类型( Client Type)
	public static final char IPHONE        =  0x01;
	public static final char ANDROID      =  0x02;
	public static final char LAMP            =  0x03;
	public static final char SERVER        =  0x04;

	/////////////////////包的类型( Package Type)
//	用户包 （User）         0x01
//	心跳包 （Heart）        0x02
//	日记包 （Diary）        0x03 
//	日历包 （Calendar）     0x04
//	相册包 （Picture）      0x05
//	位置包 （Location）     0x06
//	信息包 （Sound）       0x07

	public static final char USER_PACKAGE   =0x01;
	public static final char HEART_PACKAGE   =0x02;
	public static final char DIARY_PACKAGE     =0x03;
	public static final char CALENDAR_PACKAGE     = 0x04;
	public static final char PICTURE_PACKAGE     =  0x05	;
	public static final char LOCATION_PACKAGE      =  0x06		;
	public static final char CHAT_PACKAGE       =   0x07;  
	public static final char ACTION_PACKET      = 0x08;

	/////////////////////用户包操作码  0x01


	public static final char REGISTER    =    0x01          ;
	public static final char LOGIN       =   0x02         ;
	public static final char LOGOUT    =    0x03      ;
	public static final char CAN_MAIL_REG  =    0x04        ;
	public static final char UPLOAD_FIRST_HEAD_PHOTO_DATA=0x05;
	public static final char UPLOAD_FOLLOW_HEAD_PHOTO_DATA=0x06;
	public static final char UPLOAD_HEAD_PHOTO_DATA_FINISH=0x07;
	public static final char ADD_MATCH=0x08;
	public static final char ADD_FRIEND	=0x09;
	public static final char MODIFY_PWD   =0x0A;     
	public static final char MODIFY_SEX   =0x0B           ;
	public static final char MODIFY_BIG_NAME  =  0x0C             ;
	public static final char MODIFY_FIGURE     = 0x0D             ;
	public static final char MODIFY_STATUS   = 0x0E            ;
	public static final char MODIFY_SMALL_NAME=0x0F;
	public static final char MODIFY_HOUSE_STYLE	=0x10;
	
	public static final char GET_SELF_INFO	=	0x11;
	public static final char GET_MATCH_INFO	=	0x12;
	public static final char GET_MATCH_HEADPHOTO   =  0x13;
	public static final char GET_COMMON_INFO  =   0x14;
	public static final char GET_SELF_HEADPHOTO   =   0x15;
	public static final char  REG_ACCEPT_MATCH   =   0x16;
	public static final char  REG_REFUSE_MATCH   =   0x17;
	public static final char FORGET_PASSWORD = 0x18;
	public static final char UPLOAD_TOKEN     =    0x19;
	public static final char  FEEDBACK        =  0x1a;
	public static final char TURNONLIGHT    =      0x1b;
	public static final char TURNOFFLIGHT    =     0x1c;
	public static final char ALBUM_HOMEPAGE        =          0x1d;
	
	//------------------------------------------------------------------------------------
	
	public static final char REGISTER_SUCC	=0x41			;
	public static final char REGISTER_FAIL	=	0x42			;
	public static final char LOGIN_SUCC	=	0x43		;
	public static final char LOGIN_FAIL	=	0x44		;
	public static final char MAIL_CAN_REG		=0x45			;
	public static final char MAIL_CANNOT_REG		=0x46			;
	public static final char MATCH_REQ_TRANSMIT_SUCC	=	0x47		;

	public static final char MATCH_REQ_TRANSMIT_FAIL	=0x48		;	
	public static final char ADD_FRIEND_SUCC		=			0x49;
	public static final char ADD_FRIEND_FAIL			=		0x4A;
	public static final char MODIFY_PASSWORD_SUCC=0x4B			;
	public static final char MODIFY_PASSWORD_FAIL	=0x4C			;
	public static final char MODIFY_SEX_SUCC	=0x4D			;
	public static final char MODIFY_SEX_FAIL	=0x4E		;
	public static final char MODIFY_BIG_NAME_SUCC	=0x4F			;
	public static final char MODIFY_BIG_NAME_FAIL	=	0x50			;
	public static final char MODIFY_FIGURE_SUCC	=0x51			;
	public static final char MODIFY_FIGURE_FAIL		=0x52		;

	
	public static final char MODIFY_STATUS_SUCC	=0x53		; //修改人物状态成功
	public static final char MODIFY_STATUS_FAIL	=0x54	;
	public static final char MODIFY_SMALL_NAME_SUCC	=0x55	;
	public static final char MODIFY_SMALL_NAME_FAIL	=0x56			;
	public static final char MODIFY_HOUSE_STYLE_SUCC		=	0x57;
	public static final char MODIFY_HOUSE_STYLE_FAIL		=	0x58;
	public static final char MODIFY_HEAD_PHOTO_SUCC	=0x59		;
	public static final char MODIFY_HEAD_PHOTO_FAIL	=0x5A		;
	public static final char INVITE_FAIL		=0x5B		;
	public static final char ASK_FOR_MATCH	=0x5c		;
	public static final char ACCEPT_MATCH		=0x5d			;
	public static final char REJECT_MATCH		=0x5e			;
	public static final char RETURN_SELF_INFO	=0x5f			;
	public static final char RETURN_MATCH_INFO	=0x60		;	
	public static final char RETURN_COMM_INFO	=0x61		;	
	
	public static final char RETURN_FIRST_HEAD_PHOTO_DATA=0x62	;		
	public static final char RETURN_FOLLOW_HEAD_PHOTO_DATA=0x63		;	
	public static final char RETURN_HEAD_PHOTO_DATA_FINISH	=0x64	;	
	
	public static final char RETURN_MATCH_FIRST_HEAD_PHOTO_DATA=0x65	;		
	public static final char RETURN_MATCH_FOLLOW_HEAD_PHOTO_DATA=0x66		;	
	public static final char RETURN_MATCH_HEAD_PHOTO_DATA_FINISH	=0x67	;	
	public static final char PASSWORD_HAVE_BEEN_SENT	=0x68	;	
	public static final char PASSWORD_SENT_FAIL	=0x69	;	
	public static final char RETURN_ALBUM_HOME_PAGE_SUCC = 0x6a;
	public static final char RETURN_ALBUM_HOME_PAGE_FAIL   =   0x6b;
	public static final char MODIFY_HOUSE_STYLE_BACK	=0x6C	;
	public static final char RETURN_TURNONLIGHT        =   0x6d;
	public static final char  RETURN_TURNOFFLIGHT      =    0x6e;
   public static final char  MODIFY_ALBUM_HOME_PAGE   =    0x6f;
	
	
/////////////////////心跳包(heart Package Operation Code 0X02)
public static final char HEART_MESSAGE	=				0x01;

public static final char RETURN_HEARTMSG =  0x41;

/////////////////////日记操作包 0X03
public static final char GET_DIARY     = 0x01;
public static final char ADD_DIARY	=0x02;
public static final char MODIFY_DIARY	=	0x03;
public static final char REMOVE_DIARY	=0x04;
public static final char GET_DIARY_READ_LAST_MODIFY_TIME	=0x05;  //获取只读的最后修改时间
public static final char GET_DIARY_WRITE_LAST_MODIFY_TIME	=0x06; //获取可写的最后修改时间;
public static final char GET_DIARYLTIMELIST	=0x07; 
public static final char DIARY_REMOVE_NEW_SYMBOL	=0x08;

//-------
public static final char RETURN_ONE_DIARY	=0x41;
public static final char ADD_DIARY_SUCC		=			0x42;
public static final char ADD_DIARY_FAIL			=		0x43;
public static final char MODIFY_DIARY_SUCC		=		0x44;
public static final char MODIFY_DIARY_FAIL		=		0x45;
public static final char REMOVE_DIARY_SUCC	=			0x46;
public static final char REMOVE_DIARY_FAIL		=		0x47;
public static final char RETURN_DIARY_READ_LAST_MODIFY_TIME		=		0x48;
public static final char RETURN_DIARY_WRITE_LAST_MODIFY_TIME		=		0x49;
public static final char RETURN_DIARY_TIME_LIST	=		0x4a;  	
public static final char RETURN_MODIFY_DIARY 		=		0x4b;  
public static final char RETURN_REMOVE_DIARY 		=		0x4c;   
///////////////////////////////////////////////////日历操作包 0x04


public static final char GET_CALENDAR   =   0x01;
public static final char ADD_CALENDAR	=0x02;
public static final char MODIFY_CALENDAR	=0x03;
public static final char REMOVE_CALENDAR	=	0x04;
public static final char GET_READ_LAST_MODIFY_TIME =  0x05;
public static final char GET_WRITE_LAST_MODIFY_TIME   = 0x06;
public static final char GET_CALENDAR_TIME_LIST	= 0x07;

public static final char RETURN_CALENDAR         =        0x41;
public static final char ADD_CALENDAR_SUCC		=		0x42;
public static final char ADD_CALENDAR_FAIL			=	0x43;
public static final char MODIFY_CALENDAR_SUCC		=	0x44;
public static final char MODIFY_CALENDAR_FAIL			= 0x45;
public static final char REMOVE_CALENDAR_SUCC			= 0x46;
public static final char REMOVE_CALENDAR_FAIL		=	0x47;
public static final char RETURN_CALENDAR_LAST_MODIFY_TIME_READ =  0x48;
public static final char RETURN_CALENDAR_LAST_MODIFY_TIME_WRITE  = 0x49;
public static final char RETURN_CALENDAR_TIME_LIST	= 0x4a;
public static final char RECEIVE_CALENDAR_REMOVED	= 0x4b;
public static final char RECEIVE_CALENDAR_UPDATED	= 0x4c;
///////////////////////////////////////////////////////相册操作包 0X05

/////////////////////album操作包/////////////////////////////
//请求
public static final char ALBUM_GET_IMAGE          =       0x01;
public static final char ALBUM_REMOVE_IMAGE     =         0x02;

public static final char ALBUM_GET_LAST_MODIFY_TIME      =0x03;//获取只读最后修改时间
public static final char ALBUM_GET_SERVER_LAST_MODIFY_TIME    =0x04;//获取可写最后修改时间
public static final char ALBUM_GET_TIME_LIST           =  0x05;
public static final char ALBUM_UPLOAD_FIRST_DATA        = 0x06;
public static final char ALBUM_UPLOAD_FOLLOW_DATA       = 0x07;
public static final char ALBUM_UPLOAD_FINISH_DATA     =   0x08;
public static final char ALBUM_STOP_UPLOAD           =    0x09;

//响应
public static final char ALBUM_RETURN_FIRST_DATA        = 0x41;
public static final char ALBUM_RETURN_FOLLOW_DATA     =   0x42;
public static final char ALBUM_RETURN_FINISH_DATA       = 0x43;
public static final char ALBUM_REMOVE_IMAGE_SUCC        = 0x44;
public static final char ALBUM_REMOVE_IMAGE_FAIL        = 0x45;
public static final char ALBUM_RETURN_READ_LAST_MODIFY_TIME    = 0x46;
public static final char ALBUM_RETURN_WRITE_LAST_MODIFY_TIME =  0x47;
public static final char ALBUM_RETURN_TIME_LIST    =      0x48;
public static final char ALBUM_UPLOAD_IMAGE_SUCC    =     0x49;
public static final char ALBUM_UPLOAD_IMAGE_FAIL    =     0x4a;
public static final char AlBUM_RETURN_CONFIRM   =         0x4b;
public static final char ALBUM_RETURN_REMOVE_IMAGE   =    0x4c   ;//从服务器返回删除一张图片


//////////////////////////////////////位置操作包 0x06
public static final char UPLOAD_LOCATION=	0x01;
public static final char REQUEST_LOCATION = 0x02;

public static final char GET_LOCATION_SUCC_RES	=	0x41;
public static final char GET_USER_NOEXIST_RES	=	0x42;
public static final char GET_MATCH_USER_LOCATION_NOEXIST_RES	=	0x43;

/////////////////////////////////////////////////////聊天操作包0x07
//request
public static final char CHAT_SEND_TEXT=	0x01;
public static final char CHAT_VOICE_FIRST_DATA=	0x02;
public static final char CHAT_VOICE_FOLLOW_DATA=	0x03;
public static final char CHAT_VOICE_FINISH_DATA=	0x04;

public static final char CHAT_PICTURE_FIRST_DATA=	0x05;
public static final char CHAT_PICTURE_FOLLOW_DATA=	0x06;
public static final char CHAT_PICTURE_FINISH_DATA=	0x07;
public static final char CHAT_GET_LAST_MESSAGE_TIME=	0x08;
public static final char CHAT_GET_MESSAGE=	0x09;//获取未读的信息 
public static final char CHAT_GET_MESSAGE_RECORD     =    0x0a  ; //用于用户下拉时候向服务器获取历史聊天记录;
public static final char CHAT_REMOVE_MESSAGE           =  0X0b;
public static final char CHAT_SEND_MSG_READ         =     0x0c;
public static final char CHAT_COMFIRM_READ            =   0x0d  ;  //当收到已读状态之后，向服务器发确认包
public static final char CHAT_SAVE_PIC_TO_ALBUM      =    0x0e    ;//把聊天中图片转存到相册中;


//response

public static final char CHAT_SEND_MSG_SUCC         =     0x41;
public static final char CHAT_SEND_MSG_FAIL           =   0x42;
public static final char CHAT_RETURN_LAST_MESSAGE_TIME   = 0x43;
public static final char CHAT_RETURN_TEXT_MSG            =0x44;
public static final char CHAT_RETURN_PIC_FIRST_MSG      = 0x45;
public static final char CHAT_RETURN_PIC_APPEND_MSG    =  0x46;
public static final char CHAT_RETURN_PIC_FINISH_MSG     = 0x47;
public static final char CHAT_RETURN_VOICE_FIRST_MSG    = 0x48;
public static final char CHAT_RETURN_VOICE_APPEND_MSG   = 0x49;
public static final char CHAT_RETURN_VOICE_FINISH_MSG    =0x4a;
public static final char CHAT_RETURN_MSG_RECORD_FINISH   = 0x4b    ;//历史聊天记录返回结束。
public static final char CHAT_REMOVE_MSG_SUCC      =      0x4c;
public static final char CHAT_REMOVE_MSG_FAIL       =     0x4d;
public static final char CHAT_MSG_READ          =         0x4e;
public static final char  CHAT_MSG_NOEXIST          =      0x4f ;   //不存在历史聊天记录
public static final char  CHAT_SAVE_PIC_TO_ALBUM_SUCC  =   0x50;
public static final char CHAT_SAVE_PIC_TO_ALBUM_FAIL  =   0x51;
public static final char CHAT_MSG_REMIND        =  0x52;//用于当有未读聊天没有拉回来的时候，服务器只是发送一个提醒给客户端，然后客
//户端再去拉取，而不是直接由服务器转发消息,避免中间消息拉取不回来丢失问题;


/////////////////////动作包(action Package Operation Code)
public static final char SEND_SINGLE_ACTION_BEGINE = 0x01;
public static final char SEND_SINGLE_ACTION_END = 0x02;
public static final char SEND_COUPLE＿ACTION_REQUEST = 0x03;
public static final char SEND_COUPLE_ACTION_ACCPT = 0x04;
public static final char SEND_COUPLE_ACTION_REJECT = 0x05;
public static final char SEND_REQUEST_UNACCEPT_ACTION  =  0X06;
public static final char SEND_SINGLE_CUSTOM_ACTION_ADD   = 0x07;
public static final char SEND_SINGLE_CUSTOM_ACTION_DELETE =0x08;
public static final char SEND_SINGLE_CUSTOM_ACTION_UPDATE =0x09;
public static final char SEND_SINGLE_CUSTOM_ACTION_GETALL =0x0A;
public static final char SEND_SINGLE_CUSTOM_ACTION_BEGIN =0x0B;
public static final char SEND_SINGLE_CUSTOM_ACTION_END   =0x0C;
public static final char SEND_SINGLE_CUSTOM_ACTION_GETTARGET_ALL= 0x0D;

//响应
public static final char RECV_SINGLE_ACTION_BEGINE   =    0x41;
public static final char RECV_SINGLE_ACTION_END      =    0x42;
public static final char RECV_COUPLE_ACTION_REQUEST  =    0x43;
public static final char RECV_COUPLE_ACTION_ACCEPT   =    0x44;
public static final char RECV_COUPLE_ACTION_REJECT   =    0x45;
public static final char RECV_SINGLE_CUSTOM_ACTION_ADD_SUCCESS  = 0x46;
public static final char RECV_SINGLE_CUSTOM_ACTION_ADD_FAIL   =  0x47;
public static final char RECV_SINGLE_CUSTOM_ACTION_DELETE_SUCCESS=  0x48;
public static final char RECV_SINGLE_CUSTOM_ACTION_DELETE_FAIL   = 0x49;
public static final char RECV_SINGLE_CUSTOM_ACTION_UPDATE_SUCCESS= 0x4A;
public static final char RECV_SINGLE_CUSTOM_ACTION_UPDATE_FAIL  =  0x4B;
public static final char RECV_SINGLE_CUSTOM_ACTION_RETURN_ALL   =  0x4C;
public static final char RECV_SINGLE_CUSTOM_ACTION_BEGIN =0x4D;
public static final char RECV_SINGLE_CUSTOM_ACTION_END =  0x4E;
public static final char RECV_SINGLE_CUSTOM_ACTION_RETURN_TARGET_ALL =0x4F;

//动作定义
public static final int SINGLE_ACTION_EAT   =    1;
public static final int SINGLE_ACTION_SLEEP   =    2;
public static final int SINGLE_ACTION_LEARN   =    3;
public static final int SINGLE_ACTION_ANGRY   =   4;
public static final int SINGLE_ACTION_MISS   =    5;
//自定义动作
public static final int SINGLE_ACTION_CUSTOM1   =    6;
public static final int SINGLE_ACTION_CUSTOM2  =    7;
public static final int SINGLE_ACTION_CUSTOM3   =    8;
public static final int SINGLE_ACTION_CUSTOM4   =   9;
public static final int SINGLE_ACTION_CUSTOM5   =    10;




public static final int  HUG =  1;
public static final int KISS  = 2;
public static final int SEX =  3;
public static final int  PETTING  = 4;
public static final int PINCHEDFACE =  5;
public static final int ABUSE =  6;


//用于人物造型部分
public static final String pinkHouse= String.format("%c", 0x01) ;
public static final String blueHouse=String.format("%c", 0x02) ;
public static final String  brownHouse =String.format("%c",0x03) ;
public static final String pinkHouseStr  ="pink";
public static final String blueHouseStr ="blue";
public static final String brownHouseStr ="brown";
	//finish
	public static final char DEFAULT=0x01;
	public static final char NEW  = 0x01;
	public static final char OLD  = 0x02;
	public static final char Finish=	'\0';

	public static final int MaxPacket =1400;

	public static final char Male= 0x01;
	public static final char Female =0x02;;

	public static final char StatusDefault =0x01;

	public static final char HairStyleDefault =0x01;
	public static final char FaceStyleDefault =0x01;
	public static final char ClothesStyleDefault =0x01;
	public static final char DecoraStyleDefault= 0x01;


	public static final char LightOff =  0x01;
	public static final char LightOn =  0x02;
	public static final char NotExist =0x01;
	
	
	/*---------------------------------diary  and  custom action----------------------------------------------------------------------------------*/
	public static final char WaitForServerComfirmAdd= 'a'  ;  //等待服务器确认添加日志。动作状态;
	public static final char SuccessFromServer = 'b'       ;   //已经得到服务器成功的回复;
	public static final char FailFromServer = 'c'       ;      //已经得到服务器失败的回复;
	public static final char WaitForServerComfirmRemove =  'd' ;//等待服务器确认删除状态;
	public static final char DiaryDraft =  'e'     ;            //作为草稿的日志;
	public static final char WaitForServerComfirmModify =  'f' ;//等待服务器确认修改日志状态;
	public static final char ActionRemoved = 'g';  //自定义动作已经删除
	
	/*-------------------------------custom action -----------------------------------------------------*/

	
	/*------------------------------------------------------华丽丽的分割线---------------------------------------------*/
	
	/**
	 * new  提醒  USNotificationChatNewMsg
	 */
	public static final String NotificationChatNewMsg = "NotificationChatNewMsg";
	public static final String ServiceChatNewMsg = "ServiceChatNewMsg";
	
	public static final String NotificationDiaryNewMsg = "NotificationDiaryNewMsg";
	public static final String ServiceDiaryNewMsg = "ServiceDiaryNewMsg";

	public static final String NotificationAlbumNew = "NotificationAlbumNew";
	public static final String ServiceAlbumNewMsg = "ServiceAlbumNewMsg";
	
	public static final String ServiceDidFinishLoginSuccess = "ServiceMainDidLoginSucc";

	public static final String ServiceActionNewMsg = "ServiceActionNewMsg";
	//----------------------------------------------------------------
	
	public static final String ACTION_SENDALL = "ACTION_SENDALL";
	public static final String ACTION_USERPACKET = "USERPACKET";
	public static final String ACTION_USERPACKET_COMMON = "ReturnCommonInfo";
	public static final String ACTION_USERPACKET_ModifyHouseStyle = "USERPACKETModifyHouseStyle";
	
	public static final String ACTION_LOCATIONPACKET = "LOCATIONPACKET";
	public static final String ACTION_DIARYPACKET = "DIARYPACKET";
	public static final String ACTION_DIARYPACKET_READLASTMODIFYTIME = "DIARY_READ_LAST_MODIFY_TIME";
	public static final String ACTION_DIARYPACKET_ONERECORD= "diaryOneRecord";
	public static final String ACTION_DIARYPACKET_REFRESH= "diaryrefresh";
	
	
	public static final String ACTION_CHATPACKET = "CHATPACKET";
	public static final String ACTION_CHATPACKET_MSGTEXT_DATA = "chatMSGTEXT_DATA";
	public static final String ACTION_CHATPACKET_RECPIC = "chatRECPIC";
	public static final String ACTION_CHATPACKET_RECVOICE = "chatRECVOICE";
	public static final String ACTION_CHATPACKET_REMIND_DATA = "chatREMIND_DATA";
	public static final String ACTION_CHATPACKET_MSGREMIND = "MsgRemind";//提醒用户拉新消息
	public static final String ACTION_CHATPACKET_MSGRECFINISH = "ChatMsgRecFinish";
	public static final String ACTION_CHATPACKET_MSGRECNOEXIT = "ChatMSGRECNOEXIT";
	public static final String ACTION_CHATPACKET_SAVEPICTOALBUMSTATE = "ChatSavePicToAlbumState";
	
	public static final String ACTION_CALENDAR = "CALENDARPACKET";
	public static final String ACTION_CALENDAR_ALL_LOADED = "calendarAllLoaded";
	
	public static final String ACTION_ALBUMPACKET = "ALBUM";
	public static final String ACTION_ALBUM_UploadImageState_PACKET = "AlbumUploadImageState";
	public static final String ACTION_ALBUM_GetImage_PACKET = "AlbumGetImage";
	public static final String ACTION_ALBUM_LastModifyTime_PACKET = "AlbumLastModifyTime";
	public static final String ACTION_ALBUM_RemoveOneImage_PACKET = "AlbumRemoveOneImage";
	public static final String ACTION_ALBUM_ReturnConfirm_PACKET = "AlbumReturnConfirm";
	public static final String ACTION_ALBUM_AlbumHomePageState = "AlbumHomePageState";
	
	public static final String ACTION_ModifyLightState = "ModifyLightState";
	
	
	public static final String ACTION_ACTIONPACKET = "ACTIONPACKET";
	public static final String ACTION_ALLSELFCUSTOMACTION = "selfcustomAction";
	public static final String ACTION_EDITCUSTOMACTION = "ACTION_EDITCUSTOMACTION";
//	public static final String ACTION_ADDCUSTOMACTIONFAIL = "ACTION_ADDCUSTOMACTIONFAIL";
	
	
	public static final String ACTION_DISCONNECTED = "com.loverhouse.minius.Disconnected";
	public static final String ACTION_ONCONNECTED = "com.loverhouse.minius.Onconnected";
	public static final String ACTION_NONETWORK= "com.loverhouse.minius.nonetwork";
	public static final String ACTION_CONNECTINGTOSERVER= "connectingtoserver";
	public static final String ACTION_CONNECTINGTOSERVERFAIL= "connectingtoserverfail";
	
	public static final String EXTRA_DATA = "EXTRA_DATAL";

	
	
	public static final byte HANDLE_DATA = 0x01;
	
	public static final byte HANDLE_REQ = 0X02;
	
	public static final byte HANDLE_RESPON = 0X03;
	
	public static final byte HANDLE_READ_MYWEATHER_SUCC = 0X04;
	
	public static final byte HANDLE_READ_MYWEATHER_FAIL = 0X05;
	
	public static final byte HANDLE_READ_TARWEATHER_SUCC = 0X06;
	
	public static final byte HANDLE_READ_TARWEATHER_FAIL = 0X07;
	
	public static final byte HANDLE_ACTION_RESPON = 0x08;
	public static final byte HANDLE_LOCATION_RESPON = 0x09;
	public static final byte HANDLE_NONECTWORK = 0x10;
	public static final byte HANDLE_DISCONNECTED = 0x11;
	public static final byte HANDLE_CONNECTING = 0x12;
	public static final byte HANDLE_ONCONNECTED = 0x13;
	public static final byte HANDLE_CONNECTINGTOSERVERFAIL = 0x14;
	public static final byte HANDLE_ALLSELFCUSTOMACTION = 0x15;
	public static final byte HANDLE_EDITCUSTOMACTION = 0x16;
	public static final byte HANDLE_NewChat = 0x17;
	public static final byte HANDLE_NewDiary = 0x18;
	public static final byte HANDLE_NewAlbum = 0x19;
	public static final byte HANDLE_ModifyLamp = 0x20;
	public static final byte HANDLE_ModifyHouseStyle = 0x21;

	
	//album handler
	public static final byte HANDLE_ALBUM_SETPROGRESSBAR= 0x10;
	public static final byte HANDLE_ALBUM_CANCELUPLOADIMG= 0x11;
	public static final byte HANDLE_ALBUM_UPLOADIMGFINISH= 0x12;
	public static final byte HANDLE_ALBUM_CANCELPG= 0x13;
	public static final byte HANDLE_ALBUM_DISMISSDIALOG= 0x14;
	public static final byte HANDLE_ALBUM_DISMISSWAIT= 0x15;
	public static final byte HANDLE_ALBUM_SAVETOLOCALSUCC= 0x16;
	public static final byte HANDLE_ALBUM_SAVETOLOCALFAIL= 0x17;

	
	
	//small
	public static final char PINK        =  0x01;
	public static final char BLUE      =  0x02;
	public static final char COFFEE            =  0x03;
	
	//用于聊天信息部分
	public static final char  Sended = '1'  ;      //已发送
	public static final char  Read  =  '2'      ;  //对方已读
	public static final char  Sending ='3'      ;  //正在发送，等待服务器确认发送成功
	public static final char  SendFail = '4'       ;//发送失败
	public static final String  MSG_TEXT ="text"    ;     //数据库中存储聊天信息为文字类型
	public static final String MSG_VOICE ="voice"   ;    //数据库中存储聊天信息为语音类型
	public static final String  MSG_PICTURE= "pic"        ;//数据库中存储聊天信息为图片类型
	public static final String  MSG_ACTION ="action"     ; //数据库中存储聊天信息为人物动作类型
	
// 8种不同的布局
	public static final int VALUE_LEFT_TEXT = 0;
	public static final int VALUE_LEFT_IMAGE = 1;
	public static final int VALUE_LEFT_AUDIO = 2;
	public static final int VALUE_LEFT_ACTION = 3;
	public static final int VALUE_RIGHT_TEXT = 4;
	public static final int VALUE_RIGHT_IMAGE =5;
	public static final int VALUE_RIGHT_AUDIO = 6;
	public static final int VALUE_RIGHT_ACTION = 7;
	
	//sharedpreference 
	public static final String  PREFERENCE_NAME ="Minius"     ; //共用prefrence
	//default db name 
	public static final String  DATABASEDEFAULT_NAME ="Minius"     ; //共用prefrence
	
	//各模块最后修改时间
	public static final String  PREFERENCE_DiaryLastModifyTime ="DiaryLastModifyTime"     ;
	public static final String  PREFERENCE_AlbumLastModifyTime ="AlbumLastModifyTime"     ;
	public static final String  PREFERENCE_ChatLastReadTime ="ChatLastReadTime"     ;
	//action system
	public static final char  ActionEnd =(char)0x7f ;
	
	
	
   public static  final String api_key = "T1tbNK2H4K8u8Hqn3PCujtxk";

}
