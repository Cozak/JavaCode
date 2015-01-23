package com.minus.lovershouse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.minius.common.CommonBitmap;
import com.minius.leadpage.OperateGuide;
import com.minius.ui.CustomDialog.Builder;
import com.minius.ui.HeadPhotoHanddler;
import com.minius.ui.ProgressHUD;
import com.minus.lovershouse.R;
import com.minus.actionsystem.ActionBtnOnItemLongClickListener;
import com.minus.actionsystem.InitFigureAppDrawable;
import com.minus.actionsystem.MainActivityItemClickListener;
import com.minus.actionsystem.MenuItemView;
import com.minus.actionsystem.MyAnimations;
import com.minus.calendar.CalendarMainActivity;
import com.minus.calendar.CommonFunction;
import com.minus.diary.DiaryActivity;
import com.minus.gallery.GalleryActivity;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.setting.ConfigActivity;
import com.minus.lovershouse.util.ExitPopup;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.BadgeView;
import com.minus.lovershouse.util.FileUtil;
import com.minus.map.MapActivity;
import com.minus.sql_interface.Database;
import com.minus.table.CalendarTable;
import com.minus.table.CustomActionTable;
import com.minus.table.UserTable;
import com.minus.weather.WeatherActivity;
import com.minus.weather.WeatherUtils;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.ActionPacketHandler;
import com.minus.xsocket.handler.CalendarHandler;
import com.minus.xsocket.handler.ConnectHandler;
import com.minus.xsocket.handler.HeartPacketHandler;
import com.minus.xsocket.handler.LocationHandler;
import com.minus.xsocket.handler.UserPacketHandler;
import com.minus.xsocket.util.NetWorkUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.update.UmengUpdateAgent;

//	主界面 
public class MainActivity extends BroadCast implements OnClickListener,
		MainActivityItemClickListener, ActionBtnOnItemLongClickListener,
		OnLongClickListener, AMapLocationListener {
	protected static final String TAG = "MainActivity";

	protected ImageLoader imageLoader = ImageLoader.getInstance();
    private View curBoyView = null;
    private View curGirlView = null;
	// 菜单按钮长宽
	private int menuButtonHAW = 48;
	
	// new symbol
	private BadgeView albumnewbadge = null;
	private BadgeView diarynewbadge = null;
	private BadgeView chatnewbadge = null;
	private BadgeView CalendarNewbadge = null;

	private WeatherUtils weatherUtils = null;
	private LocationManagerProxy mAMapLocManager = null;

	private FrameLayout mainFL = null;
	private RelativeLayout mainTopRL = null;
	private RelativeLayout mainBottomRL = null;

	private ImageView mainToChatBtn;
	private ImageView mainToWeatherBtn;
	private ImageView mainToConfigBtn;
	private Button mainToDiaryBtn;
	private RelativeLayout mainToAnnLayout;
	private Button maintoMapBtn;
	private ImageView mainToPicBtn;
	private ImageButton lampBtn;

	private BadgeView giudeToMatchbadge;
	private ImageButton guidetomatchBtn;

	private BadgeView boyEndbadge = null;
	private BadgeView girlEndbadge = null;

	private BadgeView boycustomEndbadge = null;
	private BadgeView girlcustomEndbadge = null;
	private MyReceiver receiver = null;
	private MyHandler mHandler = new MyHandler(this);

	private ActionHandler actionHandler = null;

	// action system
	// 自定义动作修改对话框

	private Builder actionDialog = null;

	private FrameLayout girlcustombubbleRL = null;
	private Button girlcustombubble = null;
	private Button girlcustombubbleBtn = null;
	private FrameLayout boycustombubbleRL = null;
	private Button boycustombubble = null;
	private Button boycustombubbleBtn = null;

	// 男生的菜单
	private MenuItemView myPortraitMenu = null;
	// 女生的菜单
	private MenuItemView girlMenu = null;
	// 男女生菜单位置初始化
	private int bactionBtnisSet = 0;
	private int gactionBtnisSet = 0;

	private List<CustomActionTable> myCustomList = new ArrayList<CustomActionTable>();

	// 我的人物菜单的页数位置
	private int myFigureMenupage = 0;

	Handler startTwinkleHandler = null;
	Handler startHandShakeHandler = null;

	private ImageView staticBoyHand = null;
	private ImageView staticBoyEye = null;

	private ImageView staticGirlHand = null;
	private ImageView staticGirlEye = null;

	private ImageButton boyChatbubbleflag = null;
	private ImageButton girlChatbubbleflag = null;

    
	private RelativeLayout boyView = null;
	private ImageView mainboyHBody = null;

	private RelativeLayout girlView = null;
	private ImageView maingirlBody = null;

	private RelativeLayout boySitView = null;
	private View readBoyView = null; // sit view
	private ImageView readBoyBody = null;
	private View eatBoyView = null;
	private ImageView eatBoyBody = null;

	private RelativeLayout girlSitView = null;
	private View readGirlView = null; // sit view
	private ImageView readGirlBody = null;
	private View eatGirlView = null;
	private ImageView eatGirlBody = null;
	private RelativeLayout boyStandView = null;
	private SoftReference<View> angryBoyView = null; // stand view
	private ImageView angryBoyBody = null;
	private SoftReference<View> missBoyView = null;
	private ImageView missBoyBody = null;

	private RelativeLayout girlStandView = null;
	private View angryGirlView = null;
	private ImageView angryGirlBody = null;
	private View missGirlView = null;
	private RelativeLayout sleepRL = null;
	private ImageView missGirlBody = null;
	private ImageView boysleepBubbleView = null;
	private ImageView girlsleepBubbleView = null;
	private String showPortraitSex = "none";
	private Dialog endActionDialog = null;
	private double myLat;
	private double myLng;

	// mian view ui
	private boolean initFinish = false;
	private boolean isMatch = false;
	private String houseStyle = Protocol.pinkHouseStr;

	// 人物动作系统更新策略
	/*
	 * 主界面的单人动作状态分别保存在对应的单例中， 每次重新进入主界面先判断是否有待执行的双人动作， 执行双人动作，后执行单人动作。 单人动作更新策略：
	 * 收到对方或者自己的单人动作就相应更新单例中的status字段， 以及初始化是否 以及处理该动作
	 * 从单例类中获取对应的单人动作状态，包括动作类型和是否已经做过处理 双人动作更新策略：
	 * 当主界面不可见时但程序仍在后台运行时，收到双人动作,推送notification并保存在globalapplication
	 * 的CoupleActionMsg，等再次进入主界面时执行。当程序不运行，收到对方的双人动作请求，服务器暂存,下次登录主动获取。
	 */
	int nowAction = 0; // 当前正在执行的动作
	String nowCustomActionContent = "";
	int matchSingleAction = 0;

	private boolean isMainVisiual = true; // 主界面是否可见
	private String actionMsg = ""; // 当前正在执行的动作信息
	private boolean isRecvAction = false;// 表示当前动作是否执行过
	private String toDoCoupleActionMsg = "";
	// 主界面不可见但并没有被杀死时收到的双人动作信息，只保留最后一条

	private ProgressHUD mProgressHUD = null;
	private ProgressHUD waitingHUD = null;
	private ProgressHUD initMainwaitingHUD = null;

//	private ConnectHandler mCH = ConnectHandler;

	private Builder ibuilder = null;
	private Builder actionBuilder = null;

	private String invitor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);

		if (receiver == null)
			receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		// 网络连接变化
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

		filter.addAction(Protocol.ACTION_DISCONNECTED);//socket duan kai
		filter.addAction(Protocol.ACTION_ONCONNECTED); // socket lian shang 
		filter.addAction(Protocol.ACTION_NONETWORK);  // mei you wangluo 
		filter.addAction(Protocol.ACTION_CONNECTINGTOSERVER);  //zheng zai lianjie 
		filter.addAction(Protocol.ACTION_CONNECTINGTOSERVERFAIL);

		filter.addAction(Protocol.ACTION_USERPACKET);
		filter.addAction(Protocol.ACTION_ACTIONPACKET);
		filter.addAction(Protocol.ACTION_ALLSELFCUSTOMACTION);
		filter.addAction(Protocol.ACTION_EDITCUSTOMACTION);

		filter.addAction(Protocol.ACTION_LOCATIONPACKET);
		filter.addAction(Protocol.ACTION_USERPACKET_COMMON);
		
		filter.addAction(Protocol.ACTION_ModifyLightState);
		filter.addAction(Protocol.ACTION_USERPACKET_ModifyHouseStyle);

		filter.addAction(Protocol.ACTION_CALENDAR);

		filter.addAction(Protocol.NotificationChatNewMsg); // 收到一条更新
		filter.addAction(Protocol.NotificationDiaryNewMsg); // 收到一条更新
		filter.addAction(Protocol.NotificationAlbumNew);

		this.registerReceiver(receiver, filter);

		weatherUtils = new WeatherUtils(this.getApplicationContext());
		initView();
		initActionSystem();
		// 获取屏幕的大小
		DisplayMetrics dMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
		GlobalApplication.getInstance().setScreenHeigh(dMetrics.heightPixels);
		GlobalApplication.getInstance().setScreenWidth(dMetrics.widthPixels);
		GlobalApplication.getInstance().setAlbumHeight(dMetrics.heightPixels);
		
	
		// if (!(Utils.hasPushUserId(getApplicationContext()))) {
		// PushManager.startWork(getApplicationContext(),
		// PushConstants.LOGIN_TYPE_API_KEY,
		// Protocol.api_key);
		// // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
		// // PushManager.enableLbs(getApplicationContext());
		// }
		UmengUpdateAgent.update(this);//自动更新
		
		if (SelfInfo.getInstance().isOnline())
			this.loginSuccess();
		else {
			GlobalApplication.getInstance().setTargetDefault();
			GlobalApplication.getInstance().setCommonDefault();
			WelcomeActivity.login();
			// 启动service 执行重发机制以及检查是否有新的消息
			//Intent startServiceIntent2 = new Intent(
			//		Protocol.ServiceDidFinishLoginSuccess);
			//startService(startServiceIntent2);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isMainVisiual = true;
		GlobalApplication.getInstance().setMainVisible(true);
		initMain();
		resumeUI();
		setTogetherDaysOnAnnLayout();
	}

	// 接收服务器返回消息回调
	public void loginSuccess() {
		if (this.waitingHUD != null) {
			this.waitingHUD.dismiss();
		}
		
		SelfInfo.getInstance().setMainInit(true);
		// // 连接push服务器 在网络层已经请求
		// UserPacketHandler mUp = new UserPacketHandler();
		// mUp.uploadTokenToServer();
		
		//清除通知栏尚未处理的推送通知
		NotificationManager myNotificationManager = (NotificationManager) 
				this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		myNotificationManager.cancelAll();
		
		if (MainActivity.this.getIntent().getIntExtra("who", 0) == 1) {
			String acc = MainActivity.this.getIntent().getStringExtra("usr")
					.toLowerCase();// 一律小写
			String pwd = MainActivity.this.getIntent().getStringExtra("pwd");
			SelfInfo.getInstance().setAccount(acc);
			Database db = Database.getInstance(getApplicationContext());

			db.addSelfInfo(acc, pwd, Protocol.DEFAULT + "");
		}
		
		SharedPreferences mSP = getSharedPreferences(Protocol.PREFERENCE_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSP.edit();
		mEditor.putString("LastUser", SelfInfo.getInstance().getAccount());
		mEditor.commit();

		SharedPreferences mSP1 = GlobalApplication.getInstance().getSharedPreferences(
				SelfInfo.getInstance().getAccount(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor mEditor1 = mSP1.edit();
		mEditor1.putString("Password", SelfInfo.getInstance().getAccount());
		mEditor1.commit();
		
		CommonBitmap.getInstance().setMyHeadPhotoInit(false);

		// if (!(Utils.hasPushUserId(getApplicationContext()))) {
		// PushManager.startWork(getApplicationContext(),
		// PushConstants.LOGIN_TYPE_API_KEY,
		// Protocol.api_key);
		// // Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
		// // PushManager.enableLbs(getApplicationContext());
		// }
		// GlobalApplication mIns = GlobalApplication.getInstance();
		// if (mIns.isLoginAppear()) {
		// return; // login handle
		// }

		// 登陆成功启动心跳
		try {
			HeartPacketHandler.getInstance().startHeart();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ------ Check Whether Exist Unaccept Action Package Or Not
		ActionPacketHandler actionHandler = new ActionPacketHandler();
		actionHandler.RequsetUnacceptAction();
		

		// 启动service 执行重发机制以及检查是否有新的消息
		Intent startServiceIntent2 = new Intent(
				Protocol.ServiceDidFinishLoginSuccess);
		startService(startServiceIntent2);

	}

	/**  
     * 把Bitmap转Byte  
     */    
    public byte[] Bitmap2Bytes(Bitmap bm){    
        ByteArrayOutputStream baos = new ByteArrayOutputStream();    
        bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);    
        return baos.toByteArray();    
    }
    
	public void sendHeadPhoto(){
		
		  int maxBlob = 8100;

		  Bitmap headBm =CommonBitmap.getInstance().getMyHeadBm();
		  if(headBm == null) return ;
		  byte[] headPoto = Bitmap2Bytes(headBm);
		  int numPacket = (headPoto.length -1)/maxBlob + 1;
		    UserPacketHandler req =new UserPacketHandler();
		    String imageLen = String.format("%d",headPoto.length + 1);
		    if(numPacket == 1){
		    	req.UploadFirstHeadPhotoData( imageLen,headPoto);
		    	
		    	req.UploadHeadPhotoDataFinish(headPoto.length);
		    	return;
		    }else{
		    	byte[] first = new byte[maxBlob];
		    	System.arraycopy(headPoto,0, first, 0, maxBlob);
		    	req.UploadFirstHeadPhotoData(imageLen, first);

		    	for(int i = 1; i< numPacket - 1 ; i ++){
		    		byte[] app = new byte[maxBlob];
		    		System.arraycopy(headPoto,i * maxBlob,app, 0, maxBlob);
		    		req.UploadAppendHeadPhotoData(app);

		    	}
		    	int remainLen = headPoto.length - (numPacket - 1) * maxBlob;
		    	byte[] last = new byte[remainLen];
		    	System.arraycopy(headPoto, (numPacket-1)* maxBlob, last, 0, remainLen);
		    	req.UploadAppendHeadPhotoData( last);
		    	req.UploadHeadPhotoDataFinish(headPoto.length);
		    }
		
	  }
	
	private void ReceiveSelfInfo() {
		Database db = Database.getInstance(getApplicationContext());
		SelfInfo mSelfInfo = SelfInfo.getInstance();
		UserPacketHandler mReq = new UserPacketHandler();
		String headPhoto = db.getHeadPhoto(mSelfInfo.getAccount());
		if (headPhoto.equals(Protocol.DEFAULT + "")) {
			mReq.getMyHeadPhoto();
		} else {
			File mFile = new File(headPhoto);
			if(!(mFile.exists())){
				mReq.getMyHeadPhoto();
				mFile = null;
				
			}else{
			SelfInfo.getInstance().setHeadpotoPath(headPhoto);
			
//			HandleHeadPhotoTask t = new HandleHeadPhotoTask();
//			String isBoy = SelfInfo.getInstance().getSex();
//			String isMe = "1";
//			String path = headPhoto;
//			t.execute(path, isBoy, isMe);
			}
		}
		int who = MainActivity.this.getIntent().getIntExtra("who", 0);
		if(who==3)
			sendHeadPhoto();//注册成功登录收到信息后，发送个人photo，因为在setappearance上传失败，原因不明
		
		SharedPreferences mSP1 = getSharedPreferences(SelfInfo.getInstance()
				.getAccount(), Activity.MODE_PRIVATE);
		boolean firstLogin = mSP1.getBoolean("firstLogin", true);
		if (firstLogin) {
			// 获得自己的全部自定义动作。
			ActionPacketHandler mAP = new ActionPacketHandler();
			mAP.getAllSelfCustomAction();

			SharedPreferences.Editor editor1 = mSP1.edit();
			editor1.putBoolean("firstLogin", false);
			editor1.commit();

		} else {
			this.myCustomList = Database.getInstance(getApplicationContext())
					.getAllAction();
			SetActionSystemButton();	
		}
		createSelfFigure(true);
		
		// selfFigureDoAction();
		if (SelfInfo.getInstance().isMatch()) {
			this.isMatch = true;
			mReq.getMatchHeadPhoto();// 获取配对方的头像
			initMainweather();//天气
		} else {
			this.isMatch = false;
		}
		if (this.isMatch) {
			if (giudeToMatchbadge.isShown()) {
				giudeToMatchbadge.toggle();
			}
		}
		if (this.actionHandler != null) {
			this.actionHandler = new ActionHandler(MainActivity.this);
		}
		this.actionHandler.sendEmptyMessageDelayed(0x20, 800);

	}
	
	

	private void ReceiveMatchInfo() {
		isMatch = true;
		if (this.isMatch) {
			if (giudeToMatchbadge.isShown()) {
				giudeToMatchbadge.toggle();
			}
		}
		createMatchFigure(true);

	}

	public void ReceiveCommonInfo() {
		// resetViews();//此处注释，因为common信息是比self先得到，self是个人信息初始化，所以不处理figure信息
		initHouse();
		// 初始化纪念日
		initAnnLayout();
		CommonBitmap.getInstance().setAlubmPhotoInit(false);
		initAlbumButton();
		initLampButton();

	}

	public void loginFail() {
		GlobalApplication mIns = GlobalApplication.getInstance();

		SelfInfo.getInstance().setDefault();
		mIns.setTargetDefault();
		mIns.setCommonDefault();

		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("why", 1);
		startActivity(intent);
		this.finish();
	}

	private void resumeUI() {
		if (SelfInfo.getInstance().isMatch()) {
			isMatch = true;
		
				if (giudeToMatchbadge.isShown()) {
					giudeToMatchbadge.toggle();
				}
			
		} else {
			isMatch = false;
		}
	}

	/**
	 * 处理未完成动作
	 */
	public void handleToActionMsg() {
		if (!(toDoCoupleActionMsg.equals(""))) {
			char packetType = 0;
			try {
				packetType = (char) (actionMsg.getBytes("UTF-8"))[3];
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
			}

			String substr = actionMsg.substring(Protocol.HEAD_LEN);
			String[] arr = substr.split(" ");
			String matchname;
			if (SelfInfo.getInstance().getSmallName().length() >= 2) {

				matchname = SelfInfo.getInstance().getSmallName();
			} else {
				matchname = GlobalApplication.getInstance().getTiBigName();
			}
			if (arr.length >= 2) {
				// ---动作种类
				String actionStr = arr[1];
				String actionDate = arr[0];
				int actionType = Integer.valueOf(actionStr);
				String chatmsg = null;
				// ---动作包类型
				switch (packetType) {

				// ---请求双人动作
				case Protocol.RECV_COUPLE_ACTION_REQUEST:
					switch (actionType) {
					case Protocol.HUG:
						chatmsg = "我想抱抱你。";
						final String chatmsg1 = SelfInfo.getInstance()
								.getNickName() + " 和 " + matchname + "深情地抱在一起。";
						actionBuilder = AppManagerUtil.openAlertDialog(
								MainActivity.this, matchname, chatmsg, "同意",
								"拒绝", new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDateInServer();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										if(actionHandler.SendCoupleActionAccept(
												curDate, Protocol.HUG)){
										MainActivity.this.ActionStart(
												Protocol.HUG, false, false,
												curDate);

										MainActivity.this.addChatMsg(true,
												chatmsg1, curDate);
										}else{
											Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
											.show();
										}

										actionHandler = null;
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();

									}
								}, new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDateInServer();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										if(actionHandler.SendCoupleActionReject(
												curDate, Protocol.HUG)){
										String chatmsg = "你的表现不足以打动我";

										MainActivity.this.addChatMsg(true,
												chatmsg, curDate);
										actionHandler = null;
										}else{
											Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
											.show();
										}
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();
									}
								}, false);
						break;
					case Protocol.KISS:
						chatmsg = "亲爱的，亲亲我好咩。";
						final String chatmsg2 = SelfInfo.getInstance()
								.getNickName()
								+ " 和 "
								+ matchname
								+ "甜蜜地拥吻在一起。";
						actionBuilder =AppManagerUtil.openAlertDialog(MainActivity.this,
								matchname, chatmsg, "同意", "拒绝",
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDateInServer();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										if(actionHandler.SendCoupleActionAccept(
												curDate, Protocol.KISS)){
										MainActivity.this
												.ActionStart(Protocol.KISS,
														false, false, "");

										MainActivity.this.addChatMsg(true,
												chatmsg2, curDate);
										}else{
											Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
											.show();
										}

										actionHandler = null;
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();

									}
								}, new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDateInServer();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										if(actionHandler.SendCoupleActionReject(
												curDate, Protocol.KISS)){
										String chatmsg = "不给亲";
										MainActivity.this.addChatMsg(true,
												chatmsg, curDate);
										}else{
											Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
											.show();
										}
										actionHandler = null;
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();

									}
								}, false);
						break;
					case Protocol.SEX:
						chatmsg = "来啊，快活啊…反正有大把时光。";

						actionBuilder = AppManagerUtil.openAlertDialog(
								MainActivity.this, matchname, chatmsg, "同意",
								"拒绝", new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDateInServer();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										if(actionHandler.SendCoupleActionAccept(
												curDate, Protocol.SEX)){
										String chatmsg = "春宵一刻值千金……";
										MainActivity.this.addChatMsg(true,
												chatmsg, curDate);
										blackScreen();
										long[] pattern = { 100, 400, 100, 400,
												100, 400, 100, 400, 100, 400,
												100, 400, 100, 400, 100, 400,
												100, 400, 100, 400 };
										AppManagerUtil.Vibrate(
												MainActivity.this, pattern,
												false);
										}else{
											Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
											.show();
										}
										actionHandler = null;
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();

									}
								}, new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDateInServer();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										if(actionHandler.SendCoupleActionReject(
												curDate, Protocol.SEX)){
										String chatmsg = "色！";

										MainActivity.this.addChatMsg(true,
												chatmsg, curDate);
										}else{
											Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
											.show();
										}
										actionHandler = null;
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();

									}
								}, false);

						break;

					case Protocol.PETTING:
						MainActivity.this.ActionStart(Protocol.PETTING, false,
								false, actionDate);

						break;
					case Protocol.PINCHEDFACE:
						MainActivity.this.ActionStart(Protocol.PINCHEDFACE,
								false, false, actionDate);

						break;
					case Protocol.ABUSE:
						MainActivity.this.ActionStart(Protocol.ABUSE, false,
								false, actionDate);
						break;
					default:
						break;
					}
					break;
				// ---接受双人动作
				case Protocol.RECV_COUPLE_ACTION_ACCEPT:
					MainActivity.this.ActionStart(actionType, false, false,
							actionDate);
					break;

				default:
					break;
				}
			}
		}
		this.toDoCoupleActionMsg = "";

	}

	@Override
	protected void onPause() {
		super.onPause();
		isMainVisiual = false;
		GlobalApplication.getInstance().setMainVisible(false);
		
		if (waitingHUD != null && waitingHUD.isShowing()) {
			waitingHUD.dismiss();
		}
		if (mProgressHUD != null && mProgressHUD.isShowing()) {
			mProgressHUD.dismiss();
		}
		if (initMainwaitingHUD != null && initMainwaitingHUD.isShowing()) {
			initMainwaitingHUD.dismiss();
		}
		// // 停单人动作
		SelfInfo mSelf = SelfInfo.getInstance();
		// String myStatus = mSelf.getStatus();
		// GlobalApplication mGa = GlobalApplication.getInstance();
		// String tiStatus = mGa.getTiStatus();
		// if (!(myStatus.equals(Protocol.ActionEnd + ""))) {
		// if (mSelf.getSex().equals("b")) {
		// InitSingleActionEnd(Integer.parseInt(myStatus), true);
		// if (isMatch && (!(tiStatus.equals(Protocol.ActionEnd + "")))) {
		// InitSingleActionEnd(Integer.parseInt(myStatus), false);
		// }
		// } else {
		// InitSingleActionEnd(Integer.parseInt(myStatus), false);
		// if (isMatch && (!(tiStatus.equals(Protocol.ActionEnd + "")))) {
		// InitSingleActionEnd(Integer.parseInt(myStatus), true);
		// }
		// }
		// }

		// -------------------------------end

		if (isMatch) {
			stopBoyTwinkleAndShake();
			stopGirlTwinkleAndShake();
		} else {
			if (mSelf.getSex().equals("b")) {
				stopBoyTwinkleAndShake();
			} else {
				stopGirlTwinkleAndShake();
			}
		}
		stopLocation();// 停止定位
		// 及时断开强引用
		mSelf = null;
		// myStatus = null;
		// mGa = null;
		// tiStatus = null;

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (receiver != null) {
			this.unregisterReceiver(receiver);
			receiver = null;
		}
		stopLocation();

		if (this.actionHandler != null) {
			this.actionHandler.removeCallbacks(startGirlTwinkleRunnable);
			this.actionHandler.removeCallbacks(startGirlHandShakeRunnable);
			this.actionHandler.removeCallbacks(startBoyTwinkleRunnable);
			this.actionHandler.removeCallbacks(startBoyHandShakeRunnable);
			this.actionHandler = null;
		}

		// 停单人动作
		SelfInfo mSelf = SelfInfo.getInstance();
		String myStatus = mSelf.getStatus();
		GlobalApplication mGa = GlobalApplication.getInstance();
		String tiStatus = mGa.getTiStatus();

		if (!(myStatus.equals(Protocol.ActionEnd + ""))) {
			if (mSelf.getSex().equals("b")) {
				InitSingleActionEnd(Integer.parseInt(myStatus), true);
				if (isMatch && (!(tiStatus.equals(Protocol.ActionEnd + "")))) {
					InitSingleActionEnd(Integer.parseInt(myStatus), false);
				}

			} else {
				InitSingleActionEnd(Integer.parseInt(myStatus), false);
				if (isMatch && (!(tiStatus.equals(Protocol.ActionEnd + "")))) {
					InitSingleActionEnd(Integer.parseInt(myStatus), true);
				}
			}
		}
		// 及时断开强引用
		mSelf = null;
		myStatus = null;
		
		tiStatus = null;
		if (myCustomList != null) {
			myCustomList.clear();
			myCustomList = null;
		}
		// -------------------------------end
		this.curBoyView = null;
		this.missBoyView = null;
		this.missBoyBody= null;
		InitFigureAppDrawable.getInstance().destoryAll();
		CommonBitmap.getInstance().clearCache();
		if(imageLoader != null){
		imageLoader.clearDiskCache();
		imageLoader.clearMemoryCache();
		imageLoader.pause();
		imageLoader =null;
		}
		
		//退出前停心跳
		UserPacketHandler mReq = new UserPacketHandler();
      	HeartPacketHandler.getInstance().stopHeart();
      	
		mReq.Logout();
		SelfInfo.getInstance().setDefault();
		SelfInfo.getInstance().setOnline(false);//下线
		GlobalApplication.getInstance().setCommonDefault();
		GlobalApplication.getInstance().setTargetDefault();
		
		AsynSocket.getInstance().closeSocket();
		GlobalApplication.getInstance().destoryBimap();
//		Database.getInstance(getApplicationContext()).closeDatabase();

		try {
			//让该接收的数据先储存好
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
		super.onDestroy();
	

	}

	private void initView() {
		mainFL = (FrameLayout) findViewById(R.id.main_topFl);
		mainTopRL = (RelativeLayout) findViewById(R.id.main_top);
		mainBottomRL = (RelativeLayout) findViewById(R.id.main_sex);
		mainToChatBtn = (ImageView) findViewById(R.id.chatBtn);
		mainToChatBtn.setOnClickListener(this);
		mainToWeatherBtn = (ImageView) findViewById(R.id.weatherBtn);
		mainToWeatherBtn.setOnClickListener(this);

		mainToConfigBtn = (ImageView) findViewById(R.id.configBtn);
		mainToConfigBtn.setOnClickListener(this);
		mainToDiaryBtn = (Button) findViewById(R.id.dailyBtn);
		mainToDiaryBtn.setOnClickListener(this);
		mainToAnnLayout = (RelativeLayout) findViewById(R.id.annLayout);
		mainToAnnLayout.setOnClickListener(this);
		maintoMapBtn = (Button) findViewById(R.id.mapBtn);
		maintoMapBtn.setOnClickListener(this);
		mainToPicBtn = (ImageView) findViewById(R.id.pictureBtn);
		mainToPicBtn.setOnClickListener(this);
		lampBtn = (ImageButton) findViewById(R.id.lampBtn);
		lampBtn.setOnClickListener(this);

		staticBoyHand = (ImageView) findViewById(R.id.staticboy_hand);

		staticBoyEye = (ImageView) findViewById(R.id.staticboy_eye);

		staticGirlHand = (ImageView) findViewById(R.id.maingirl_hand);

		staticGirlEye = (ImageView) findViewById(R.id.maingirl_eye);

		// boyChatbubbleflag = (ImageButton)
		// findViewById(R.id.boyChatbubblePos);
		// girlChatbubbleflag = (ImageButton)
		// findViewById(R.id.girlChatbubblePos);
		// 气泡提示
		guidetomatchBtn = (ImageButton) findViewById(R.id.guidetomatchBtn);
		guidetomatchBtn.setOnClickListener(this);
		giudeToMatchbadge = new BadgeView(this, guidetomatchBtn);
		giudeToMatchbadge.setText("邀请请点击菜单按钮(左上角)");
		giudeToMatchbadge.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
		giudeToMatchbadge.setBackgroundResource(R.drawable.chat_box_girl_left);
		giudeToMatchbadge.setTextSize(15);

		girlcustombubbleRL = (FrameLayout) this.findViewById(R.id.girlcustomActionRL);
		girlcustombubbleBtn = (Button) girlcustombubbleRL.findViewById(R.id.girlcustomActionTop);
		girlcustombubble = (Button) girlcustombubbleRL.findViewById(R.id.girlcustomAction);
		boycustombubbleRL = (FrameLayout) this.findViewById(R.id.boycustomActionRL);
		boycustombubbleBtn = (Button) boycustombubbleRL.findViewById(R.id.boycustomActionTop);
		boycustombubble = (Button) this.findViewById(R.id.boycustomAction);

	}
	
// 主界面招手眨眼

	public void stopBoyTwinkleAndShake() {

		if (this.actionHandler != null) {
			this.actionHandler.removeCallbacks(startBoyTwinkleRunnable);
			this.actionHandler.removeCallbacks(startBoyHandShakeRunnable);
		}
	}

	public void stopGirlTwinkleAndShake() {

		if (this.actionHandler != null) {
			this.actionHandler.removeCallbacks(startGirlTwinkleRunnable);
			this.actionHandler.removeCallbacks(startGirlHandShakeRunnable);
		}

	}

	Runnable startBoyTwinkleRunnable = new Runnable() {
		@Override
		public void run() {
			startOneBoyTwinkle();
			// startGirlShake();
			// 生成30--120之间的随机数
			int max = 120;
			int min = 30;
			Random random = new Random();

			int s = random.nextInt(max) % (max - min + 1) + min;
			if(actionHandler != null)
			actionHandler.postDelayed(this, 1000 * s);
		}
	};

	Runnable startBoyHandShakeRunnable = new Runnable() {
		@Override
		public void run() {
			startOneBoyShake();
			// startGirlTwinkle();
			// 生成30--120之间的随机数
			int max = 120;
			int min = 30;
			Random random = new Random();

			int s = random.nextInt(max) % (max - min + 1) + min;
			if(actionHandler != null)
			actionHandler.postDelayed(this, 1000 * s);
		}
	};

	Runnable startGirlTwinkleRunnable = new Runnable() {
		@Override
		public void run() {
			startOneGirlTwinkle();
			// startGirlShake();
			// 生成30--120之间的随机数
			int max = 120;
			int min = 30;
			Random random = new Random();

			int s = random.nextInt(max) % (max - min + 1) + min;
			if(actionHandler != null)
			actionHandler.postDelayed(this, 1000 * s);
		}
	};

	Runnable startGirlHandShakeRunnable = new Runnable() {
		@Override
		public void run() {
			startOneGirlShake();
			// startGirlTwinkle();
			// 生成30--120之间的随机数
			int max = 120;
			int min = 30;
			Random random = new Random();

			int s = random.nextInt(max) % (max - min + 1) + min;
			if(actionHandler != null)
			actionHandler.postDelayed(this, 1000 * s);
		}
	};

	private void startBoyTwinkle() {
		if (this.actionHandler == null) {
			this.actionHandler = new ActionHandler(MainActivity.this);
		} else {
			this.actionHandler.removeCallbacks(startBoyTwinkleRunnable);
		}
		this.actionHandler.postDelayed(startBoyTwinkleRunnable, 1000);
	}

	private void startBoyHandShake() {
		if (this.actionHandler == null) {
			this.actionHandler = new ActionHandler(MainActivity.this);
		} else {
			this.actionHandler.removeCallbacks(startBoyHandShakeRunnable);
		}
		this.actionHandler.postDelayed(startBoyHandShakeRunnable, 1000);
	}

	private void startOneBoyTwinkle() {
		if(actionHandler == null) return;
		if (staticBoyEye == null)
			staticBoyEye = (ImageView) findViewById(R.id.staticboy_eye);
		staticBoyEye.setImageResource(R.anim.staticboy_eye);

		AnimationDrawable twinkle = (AnimationDrawable) staticBoyEye
				.getDrawable();
		twinkle.start();
		actionHandler.sendEmptyMessageDelayed(0x12, 4000);

	}

	private void startOneBoyShake() {
		if(actionHandler == null) return;
		if (staticBoyHand == null)
			staticBoyHand = (ImageView) findViewById(R.id.staticboy_hand);

		// staticBoyBody.setBackgroundResource(R.drawable.boy_portrait_body);
		// staticBoyHand.setVisibility(View.VISIBLE);
		staticBoyHand.setImageResource(R.anim.staticboy_hand);
		AnimationDrawable handShake = (AnimationDrawable) staticBoyHand
				.getDrawable();
		handShake.start();
		actionHandler.sendEmptyMessageDelayed(0x13, 4000);
		// new Timer().schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		// Message msg = new Message();
		// msg.what = 0x13;
		// msg.setTarget(actionHandler);
		// msg.sendToTarget();
		//
		// }
		// }, 4000);
	}

	private void startGirlTwinkle() {
		if (this.actionHandler == null) {
			this.actionHandler = new ActionHandler(MainActivity.this);
		} else {
			this.actionHandler.removeCallbacks(startGirlTwinkleRunnable);
		}
		this.actionHandler.postDelayed(startGirlTwinkleRunnable, 1000);
	}

	private void startGirlHandShake() {
		if (this.actionHandler == null) {
			this.actionHandler = new ActionHandler(MainActivity.this);
		} else {
			this.actionHandler.removeCallbacks(startGirlHandShakeRunnable);
		}
		this.actionHandler.postDelayed(startGirlHandShakeRunnable, 1000);
	}

	private void startOneGirlTwinkle() {
		if (staticGirlEye == null)
			staticGirlEye = (ImageView) findViewById(R.id.maingirl_eye);
		staticGirlEye.setImageResource(R.anim.staticgirl_eye);
		// staticBoyBody.setBackgroundResource(R.drawable.boy_portrait_body);
		AnimationDrawable twinkle = (AnimationDrawable) staticGirlEye
				.getDrawable();
		twinkle.start();
		if (actionHandler != null) {
			actionHandler.sendEmptyMessageDelayed(0x14, 4000);
		}
		// new Timer().schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		// Message msg = new Message();
		// msg.what = 0x14;
		// msg.setTarget(actionHandler);
		// msg.sendToTarget();
		//
		// }
		// }, 4000);
	}

	private void startOneGirlShake() {
		if (staticGirlHand == null)
			staticGirlHand = (ImageView) findViewById(R.id.staticgirl_hand);

		// staticBoyBody.setBackgroundResource(R.drawable.boy_portrait_body);
		// staticGiHand.setVisibility(View.VISIBLE);
		staticGirlHand.setImageResource(R.anim.staticgirl_hand);
		AnimationDrawable handShake = (AnimationDrawable) staticGirlHand
				.getDrawable();
		handShake.start();
		if (actionHandler != null) {
			actionHandler.sendEmptyMessageDelayed(0x15, 4000);
		}
		// new Timer().schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		// Message msg = new Message();
		// msg.what = 0x15;
		// msg.setTarget(actionHandler);
		// msg.sendToTarget();
		//
		// }
		// }, 4000);
	}

	private void showBoyCustomEndButton(View fatherView) {

//		if(boycustombubble.getAnimation()!= null){
//			boycustombubble.getAnimation().cancel();
//		}
		if (boycustomEndbadge == null) {
			boycustomEndbadge = new BadgeView(this, fatherView);

		} else {
			ViewGroup oldParent = (ViewGroup) boycustomEndbadge.getParent();
			if (oldParent != null) {
				oldParent.removeView(boycustomEndbadge);
			}
			boycustomEndbadge = null;
			boycustomEndbadge = new BadgeView(this, fatherView);

		}

		boycustomEndbadge.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
		boycustomEndbadge.setBackgroundResource(R.drawable.boyend);
		boycustomEndbadge.setOnClickListener(this);
		TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
		anim.setInterpolator(new BounceInterpolator());
		anim.setDuration(1000);
		boycustomEndbadge.toggle(anim, null);
	}

	private void showGirlCustomEndButton(View fatherView) {

//		if(girlcustombubble.getAnimation()!= null){
//			girlcustombubble.getAnimation().cancel();
//		}
		if (girlcustomEndbadge == null) {
			girlcustomEndbadge = new BadgeView(this, fatherView);

		} else {
			ViewGroup oldParent = (ViewGroup) girlcustomEndbadge.getParent();
			if (oldParent != null) {
				oldParent.removeView(girlcustomEndbadge);
			}
			girlcustomEndbadge = null;
			girlcustomEndbadge = new BadgeView(this, fatherView);

		}

		girlcustomEndbadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		girlcustomEndbadge.setBackgroundResource(R.drawable.girlend);
		girlcustomEndbadge.setOnClickListener(this);
		TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
		anim.setInterpolator(new BounceInterpolator());
		anim.setDuration(1000);
		girlcustomEndbadge.toggle(anim, null);
	}

	private boolean hideGirlCustomEndButton() {
		if (girlcustomEndbadge == null) {
			return false;
		} else {
			if (girlcustomEndbadge.isShown()) {
				TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
				anim.setInterpolator(new BounceInterpolator());
				anim.setDuration(1000);
				girlcustomEndbadge.toggle(anim, null);
				return true;
			}

		}
		return false;
	}

	private boolean hideBoyCustomEndButton() {
		if (boycustomEndbadge == null) {
			return false;
		} else {
			if (boycustomEndbadge.isShown()) {
				
				TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
				anim.setInterpolator(new BounceInterpolator());
				anim.setDuration(1000);
				boycustomEndbadge.toggle(anim, null);
				return true;
			}

		}

		return false;

	}

	private void showBoyEndButton(View fatherView,int layoutPos) {

		if (boyEndbadge == null) {
			boyEndbadge = new BadgeView(this, fatherView);
			boyEndbadge.setOnClickListener(this);
		} else {
			ViewGroup oldParent = (ViewGroup) boyEndbadge.getParent();
			if (oldParent != null) {
				oldParent.removeView(boyEndbadge);
			}
			boyEndbadge.setTargetView(null);
			boyEndbadge = null;
			boyEndbadge = new BadgeView(this, fatherView);

		}
		// boyEndbadge = new BadgeView(this, fatherView);
		boyEndbadge.setBadgePosition(layoutPos);
		boyEndbadge.setBackgroundResource(R.drawable.boyend);
		boyEndbadge.setOnClickListener(this);
		TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
		anim.setInterpolator(new BounceInterpolator());
		anim.setDuration(1000);
		boyEndbadge.toggle(anim, null);
	}

	private void showGirlEndButton(View fatherView,int layoutPos) {
		if (girlEndbadge == null) {
			girlEndbadge = new BadgeView(this, fatherView);
			girlEndbadge.setOnClickListener(this);
		} else {
			ViewGroup oldParent = (ViewGroup) girlEndbadge.getParent();
			if (oldParent != null) {
				oldParent.removeView(girlEndbadge);
			}
			girlEndbadge = null;
			girlEndbadge = new BadgeView(this, fatherView);

		}

		girlEndbadge.setBadgePosition(layoutPos);
		girlEndbadge.setBackgroundResource(R.drawable.girlend);
		girlEndbadge.setOnClickListener(this);
		TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
		anim.setInterpolator(new BounceInterpolator());
		anim.setDuration(1000);
		girlEndbadge.toggle(anim, null);
	}

	private boolean hideGirlEndButton() {
		if (girlEndbadge == null) {
			return false;
		} else {
			if (girlEndbadge.isShown()) {
				TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
				anim.setInterpolator(new BounceInterpolator());
				anim.setDuration(1000);
				girlEndbadge.toggle(anim, null);
				return true;
			}

		}
		return false;
	}

	private boolean hideBoyEndButton() {
		if (boyEndbadge == null) {
			return false;
		} else {
			if (boyEndbadge.isShown()) {
				TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
				anim.setInterpolator(new BounceInterpolator());
				anim.setDuration(1000);
				boyEndbadge.toggle(anim, null);
				return true;
			}

		}

		return false;

	}

	// 1 init action
	private void initActionSystem() {
		actionHandler = new ActionHandler(MainActivity.this);

		boyView = (RelativeLayout) findViewById(R.id.MyView);
		mainboyHBody = (ImageView) boyView.findViewById(R.id.mainboyImg);

		girlView = (RelativeLayout) findViewById(R.id.GirlView);
		maingirlBody = (ImageView) girlView.findViewById(R.id.maingirlbody);

		myPortraitMenu = (MenuItemView) findViewById(R.id.boyMenuItemView);
		girlMenu = (MenuItemView) findViewById(R.id.girlboyMenuItemView);

		// -----------------------------------------------------------------
		boySitView = (RelativeLayout) findViewById(R.id.boy_sit_view);

		// readBoyView = getLayoutInflater().inflate(
		// R.layout.main_boyread, null);
		// readBoyView.setOnClickListener(MainActivity.this);
		//
		// eatBoyView = getLayoutInflater().inflate(
		// R.layout.main_boyeat, null);
		// eatBoyView.setOnClickListener(MainActivity.this);
		// -----------------------------------------------------------------
		girlSitView = (RelativeLayout) findViewById(R.id.girl_sit_view);

		// readGirlView = getLayoutInflater().inflate(
		// R.layout.main_girlread, null);
		// readGirlView.setOnClickListener(MainActivity.this);
		//
		// eatGirlView = getLayoutInflater().inflate(
		// R.layout.main_girleat, null);
		// eatGirlView.setOnClickListener(MainActivity.this);

		// -----------------------------------------------------------------
		boyStandView = (RelativeLayout) findViewById(R.id.stand_boy_view);
		// angryBoyView = getLayoutInflater().inflate(
		// R.layout.main_angryboy, null);
		// angryBoyView.setOnClickListener(MainActivity.this);
		// missBoyView = getLayoutInflater().inflate(
		// R.layout.main_boymiss, null);
		// missBoyView.setOnClickListener(MainActivity.this);
		// -----------------------------------------------------------------
		girlStandView = (RelativeLayout) findViewById(R.id.stand_girl_view);
		// angryGirlView = getLayoutInflater().inflate(
		// R.layout.main_angrygirl, null);
		// angryGirlView.setOnClickListener(MainActivity.this);
		// missGirlView = getLayoutInflater().inflate(
		// R.layout.main_missinggirl, null);
		// missGirlView.setOnClickListener(MainActivity.this);
		//
        this.sleepRL = (RelativeLayout) findViewById(R.id.singsleep);
		boysleepBubbleView = (ImageView) findViewById(R.id.sleep_boybubble);
//		boysleepBubbleView.setOnClickListener(this);
		girlsleepBubbleView = (ImageView) findViewById(R.id.sleep_girlbubble);
//		girlsleepBubbleView.setOnClickListener(this);
		this.sleepRL.setOnClickListener(null);

		// ---------------------------------------------------------couple

		myPortraitMenu.setPosition();
		girlMenu.setPosition();

		myPortraitMenu.setRadius(150);
		girlMenu.setRadius(150);

		boyView.setOnClickListener(this);
		girlView.setOnClickListener(this);

		this.menuButtonHAW = AppManagerUtil.dip2px(getApplicationContext(),
				(float) 48.0);

		//TODO 如若没有这两句，动作系统初始化不正常
		// 获得自己的全部自定义动作。
		ActionPacketHandler mAP = new ActionPacketHandler();
		mAP.getAllSelfCustomAction();
	}

	/**
	 * 根据性别刷新动作 button
	 */
	private void SetActionSystemButton() {
		myPortraitMenu.removeAllViews();
		girlMenu.removeAllViews();
		 this.gactionBtnisSet=0;
	    this.bactionBtnisSet=0;
		int count = 0;
		if (this.myCustomList != null) {
			count = this.myCustomList.size();
		}

		if (SelfInfo.getInstance().getSex().equals("b")) {
			ImageButton imgBtn1 = new ImageButton(this);
			if (count > 0) {
				imgBtn1.setBackgroundResource(R.drawable.boycustomnext);
			} else {
				imgBtn1.setBackgroundResource(R.drawable.boycustomadd);
			}

			ImageButton imgBtn2 = new ImageButton(this);
			imgBtn2.setBackgroundResource(R.drawable.boymissing);
			ImageButton imgBtn3 = new ImageButton(this);
			imgBtn3.setBackgroundResource(R.drawable.boyangry);
			ImageButton imgBtn4 = new ImageButton(this);
			imgBtn4.setBackgroundResource(R.drawable.boylearning);
			ImageButton imgBtn5 = new ImageButton(this);
			imgBtn5.setBackgroundResource(R.drawable.boysleeping);
			ImageButton imgBtn6 = new ImageButton(this);
			imgBtn6.setBackgroundResource(R.drawable.boyeating);

			myPortraitMenu.addView(imgBtn1);
			myPortraitMenu.addView(imgBtn2);
			myPortraitMenu.addView(imgBtn3);
			myPortraitMenu.addView(imgBtn4);
			myPortraitMenu.addView(imgBtn5);
			myPortraitMenu.addView(imgBtn6);

			ImageButton timgBtn2 = new ImageButton(this);
			timgBtn2.setBackgroundResource(R.drawable.pinchedface_item);
			ImageButton timgBtn3 = new ImageButton(this);
			timgBtn3.setBackgroundResource(R.drawable.petting_item);
			ImageButton timgBtn4 = new ImageButton(this);
			timgBtn4.setBackgroundResource(R.drawable.sex_item);
			ImageButton timgBtn5 = new ImageButton(this);
			timgBtn5.setBackgroundResource(R.drawable.kiss_item);
			ImageButton timgBtn6 = new ImageButton(this);
			timgBtn6.setBackgroundResource(R.drawable.hug_item);

			// girlMenu.addView(timgBtn1);
			girlMenu.addView(timgBtn2);
			girlMenu.addView(timgBtn3);
			girlMenu.addView(timgBtn4);
			girlMenu.addView(timgBtn5);
			girlMenu.addView(timgBtn6);

		} else {
			ImageButton imgBtn1 = new ImageButton(this);
			imgBtn1.setBackgroundResource(R.drawable.boyabuse);
			ImageButton imgBtn2 = new ImageButton(this);
			imgBtn2.setBackgroundResource(R.drawable.boypinchedface);
			ImageButton imgBtn3 = new ImageButton(this);
			imgBtn3.setBackgroundResource(R.drawable.boypetting);
			ImageButton imgBtn4 = new ImageButton(this);
			imgBtn4.setBackgroundResource(R.drawable.boysex);
			ImageButton imgBtn5 = new ImageButton(this);
			imgBtn5.setBackgroundResource(R.drawable.boykiss);
			ImageButton imgBtn6 = new ImageButton(this);
			imgBtn6.setBackgroundResource(R.drawable.boyhug);

			myPortraitMenu.addView(imgBtn1);
			myPortraitMenu.addView(imgBtn2);
			myPortraitMenu.addView(imgBtn3);
			myPortraitMenu.addView(imgBtn4);
			myPortraitMenu.addView(imgBtn5);
			myPortraitMenu.addView(imgBtn6);

			ImageButton timgBtn6 = new ImageButton(this);
			if (count > 0) {
				timgBtn6.setBackgroundResource(R.drawable.girlcustomnext);
			} else {
				timgBtn6.setBackgroundResource(R.drawable.girlcustomadd);
			}

			ImageButton timgBtn1 = new ImageButton(this);
			timgBtn1.setBackgroundResource(R.drawable.missing_item);
			ImageButton timgBtn2 = new ImageButton(this);
			timgBtn2.setBackgroundResource(R.drawable.angry_item);
			ImageButton timgBtn3 = new ImageButton(this);
			timgBtn3.setBackgroundResource(R.drawable.learning_item);
			ImageButton timgBtn4 = new ImageButton(this);
			timgBtn4.setBackgroundResource(R.drawable.sleeping_item);
			ImageButton timgBtn5 = new ImageButton(this);
			timgBtn5.setBackgroundResource(R.drawable.eating_item);

			girlMenu.addView(timgBtn6);
			girlMenu.addView(timgBtn1);
			girlMenu.addView(timgBtn2);
			girlMenu.addView(timgBtn3);
			girlMenu.addView(timgBtn4);
			girlMenu.addView(timgBtn5);
		}
		if (myPortraitMenu.getStatus() == MenuItemView.STATUS_OPEN) {
			MyAnimations
					.startAnimations(MainActivity.this, myPortraitMenu, 300);
		}
		if (girlMenu.getStatus() == MenuItemView.STATUS_OPEN) {
			MyAnimations.startAnimations(MainActivity.this, girlMenu, 300);
		}

	}

	/**
	 * 根据自己一方的第一页菜单
	 */
	private void SetSelfMainActionMenu(boolean isShow) {

		int count = 0;
		if (this.myCustomList == null) {
			return;
		}
		count = this.myCustomList.size();
		if (count <= 0) {
			return;
		}

		if (SelfInfo.getInstance().getSex().equals("b")) {
			
		       this.bactionBtnisSet=0;
			myPortraitMenu.removeAllViews();
			ImageButton imgBtn1 = new ImageButton(this);
			if (count > 0) {
				imgBtn1.setBackgroundResource(R.drawable.boycustomnext);
			} else {
				imgBtn1.setBackgroundResource(R.drawable.boycustomadd);
			}

			ImageButton imgBtn2 = new ImageButton(this);
			imgBtn2.setBackgroundResource(R.drawable.boymissing);
			ImageButton imgBtn3 = new ImageButton(this);
			imgBtn3.setBackgroundResource(R.drawable.boyangry);
			ImageButton imgBtn4 = new ImageButton(this);
			imgBtn4.setBackgroundResource(R.drawable.boylearning);
			ImageButton imgBtn5 = new ImageButton(this);
			imgBtn5.setBackgroundResource(R.drawable.boysleeping);
			ImageButton imgBtn6 = new ImageButton(this);
			imgBtn6.setBackgroundResource(R.drawable.boyeating);

			myPortraitMenu.addView(imgBtn1);
			myPortraitMenu.addView(imgBtn2);
			myPortraitMenu.addView(imgBtn3);
			myPortraitMenu.addView(imgBtn4);
			myPortraitMenu.addView(imgBtn5);
			myPortraitMenu.addView(imgBtn6);

			if (isShow) {
				myPortraitMenu.setMyChange(true);
				myPortraitMenu.requestLayout();
				if (myPortraitMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
					MyAnimations.startAnimations(MainActivity.this,
							myPortraitMenu, 300);
				}
			} else {
				if (myPortraitMenu.getStatus() == MenuItemView.STATUS_OPEN) {
					MyAnimations.startAnimations(MainActivity.this,
							myPortraitMenu, 300);
				}
			}
			this.myFigureMenupage = 0;

		} else {
			 this.gactionBtnisSet=0;
		      
			girlMenu.removeAllViews();
			ImageButton timgBtn6 = new ImageButton(this);
			if (count > 0) {
				timgBtn6.setBackgroundResource(R.drawable.girlcustomnext);
			} else {
				timgBtn6.setBackgroundResource(R.drawable.girlcustomadd);
			}

			ImageButton timgBtn1 = new ImageButton(this);
			timgBtn1.setBackgroundResource(R.drawable.missing_item);
			ImageButton timgBtn2 = new ImageButton(this);
			timgBtn2.setBackgroundResource(R.drawable.angry_item);
			ImageButton timgBtn3 = new ImageButton(this);
			timgBtn3.setBackgroundResource(R.drawable.learning_item);
			ImageButton timgBtn4 = new ImageButton(this);
			timgBtn4.setBackgroundResource(R.drawable.sleeping_item);
			ImageButton timgBtn5 = new ImageButton(this);
			timgBtn5.setBackgroundResource(R.drawable.eating_item);

			girlMenu.addView(timgBtn6);
			girlMenu.addView(timgBtn1);
			girlMenu.addView(timgBtn2);
			girlMenu.addView(timgBtn3);
			girlMenu.addView(timgBtn4);
			girlMenu.addView(timgBtn5);

			if (isShow) {
				girlMenu.setMyChange(true);
				girlMenu.requestLayout();
				if (girlMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
					MyAnimations.startAnimations(MainActivity.this, girlMenu,
							300);
				}
			} else {
				if (girlMenu.getStatus() == MenuItemView.STATUS_OPEN) {
					MyAnimations.startAnimations(MainActivity.this, girlMenu,
							300);
				}
			}
			this.myFigureMenupage = 0;
		}

	}

	/**
	 * 根据customtablelist 刷新自定义动作第二页
	 */
	private void RefreshCustomMenu(String sex) {

		if (sex.equals("b")) {
			
		       this.bactionBtnisSet=0;
			// MyAnimations
			// .startAnimations(MainActivity.this, myPortraitMenu, 300);
			myPortraitMenu.removeAllViews();

			int listSize = myCustomList.size();

			if (listSize < 5) {
				Button imgBtnAdd = new Button(this);

				imgBtnAdd.setBackgroundResource(R.drawable.boycustomadd);
				myPortraitMenu.addView(imgBtnAdd);
			}
			if (listSize > 5)
				listSize = 5;
			if (listSize > 0) {
				CustomActionTable mCA = null;

				for (int i = 0; i < listSize; i++) {
					mCA = myCustomList.get(i);
					Button imgBtn2 = new Button(this);
					imgBtn2.setBackgroundResource(R.drawable.boycustom1);
					imgBtn2.setHeight(MainActivity.this.menuButtonHAW);
					imgBtn2.setWidth(MainActivity.this.menuButtonHAW);
					imgBtn2.setTextSize(9);
					imgBtn2.setTextColor(Color.WHITE);

					imgBtn2.setText(mCA.getContent());
					myPortraitMenu.addView(imgBtn2);
					imgBtn2 = null;

				}

			}

			ImageButton imgBtn1 = new ImageButton(this);
			imgBtn1.setBackgroundResource(R.drawable.boycustomback);
			myPortraitMenu.addView(imgBtn1);
			myPortraitMenu.setMyChange(true);
			myPortraitMenu.requestLayout();
			if (myPortraitMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
				MyAnimations.startAnimations(MainActivity.this, myPortraitMenu,
						300);
			}

			this.myFigureMenupage = 1;

		} else {

			// MyAnimations.startAnimations(MainActivity.this, girlMenu, 300);
			girlMenu.removeAllViews();
			 this.gactionBtnisSet=0;
		   
			int listSize = myCustomList.size();
			if (listSize < 5) {
				Button imgBtnAdd = new Button(this);
				imgBtnAdd.setBackgroundResource(R.drawable.girlcustomadd);
				girlMenu.addView(imgBtnAdd);
			}
			if (listSize > 5)
				listSize = 5;
			if (listSize > 0) {
				CustomActionTable mCA = null;

				for (int i = 0; i < listSize; i++) {
					Button imgBtn2 = new Button(this);
					mCA = myCustomList.get(i);
					imgBtn2.setHeight(MainActivity.this.menuButtonHAW);
					imgBtn2.setWidth(MainActivity.this.menuButtonHAW);
					imgBtn2.setTextSize(9);
					imgBtn2.setTextColor(Color.WHITE);
					imgBtn2.setBackgroundResource(R.drawable.girlcustom1);
					imgBtn2.setText(mCA.getContent());
					girlMenu.addView(imgBtn2);
					imgBtn2 = null;

				}

			}
			ImageButton imgBtn1 = new ImageButton(this);
			imgBtn1.setBackgroundResource(R.drawable.girlcustomback);
			girlMenu.addView(imgBtn1);
			girlMenu.setMyChange(true);
			girlMenu.requestLayout();
			if (girlMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
				MyAnimations.startAnimations(MainActivity.this, girlMenu, 300);
			}
			this.myFigureMenupage = 1;

		}

	}

	/**
	 * 人物自定义动作增删更新策略。 自定义动作类型限制在5个。id 在1-5之间。 增加策略：
	 * 点击添加按钮后，弹出对话框，保存后数据先保存到数据库中，把status的值设为 等待服务器确认。直到服务器返回确认成功信息才更新status值。
	 * 添加自定义动作的typeid值策略 按照id递增顺序查找删除状态的tyid值，选最小的id作为插入typeid 删除策略：
	 * 保存数据库，等待服务器确认，更新status为删除状态
	 * 
	 * 更新策略： 同理。
	 */
	// main界面初始化
	// 从单例类中初始化主界面 前提单例类都初始化好了
	private void initHouse() {

		GlobalApplication mIns = GlobalApplication.getInstance();

		if (mIns.getHouseStyle().equals(Protocol.pinkHouse)) {
			MainActivity.this.houseStyle = Protocol.pinkHouseStr;
		} else if (mIns.getHouseStyle().equals(Protocol.blueHouse)) {
			MainActivity.this.houseStyle = Protocol.blueHouseStr;
		} else if (mIns.getHouseStyle().equals(Protocol.brownHouse)) {
			MainActivity.this.houseStyle = Protocol.brownHouseStr;
		}

		String imageName = "main_background_" + this.houseStyle;
		int backgroundid = MainActivity.this.getResources().getIdentifier(
				imageName, "drawable", "com.minus.lovershouse"); // name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
		this.mainFL.setBackgroundResource(backgroundid);

		mIns = null;

	}

	public void initChatButton() {
		SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(SelfInfo.getInstance().getAccount(),
						Activity.MODE_PRIVATE);
		boolean chatButtonIsNormal = mSP.getBoolean("chatButtonIsNormal", true);
		if (chatButtonIsNormal) {
			MainActivity.this.mainToChatBtn
					.setImageResource(R.drawable.main_chat_selector);
			if (SelfInfo.getInstance().getSex().equals("b")) {
				// boyChatbubbleflag = null;
				// girlChatbubbleflag.setVisibility(View.GONE);
			} else {
				// boyChatbubbleflag.setVisibility(View.GONE);
			}
		} else {
			MainActivity.this.mainToChatBtn
					.setImageResource(R.drawable.newmessage_inform);
			if (SelfInfo.getInstance().getSex().equals("b")) {
				// boyChatbubbleflag = null;
				// girlChatbubbleflag.setVisibility(View.VISIBLE);
			} else {
				// boyChatbubbleflag.setVisibility(View.VISIBLE);
			}
		}
		mSP = null;
	}

	public void initDiaryButton() {
		SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(SelfInfo.getInstance().getAccount(),
						Activity.MODE_PRIVATE);
		boolean diaryButtonIsNormal = mSP.getBoolean("diaryButtonIsNormal",
				true);
		if (diaryButtonIsNormal) {
			this.mainToDiaryBtn
					.setBackgroundResource(R.drawable.maindiarybutton);
		} else {
			this.mainToDiaryBtn
					.setBackgroundResource(R.drawable.newdiary_inform);
		}

	}
	
	public void initAlbumButton() {
		// first pic's date

		Bitmap albumFirstPhoBm = CommonBitmap.getInstance().
				getAlbumBitmap(getApplicationContext(),this.houseStyle);

		if (albumFirstPhoBm != null) {
			MainActivity.this.mainToPicBtn.setImageBitmap(albumFirstPhoBm);

		} else {
			String imageName = this.houseStyle + "album";
			int id = MainActivity.this.getResources().getIdentifier(imageName,
					"drawable", "com.minus.lovershouse"); // name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
			MainActivity.this.mainToPicBtn.setImageResource(id);

		}
	}

//	public void initAlbumButton() {
//		// first pic's date
//		boolean havePic = false;
//		GlobalApplication mIns = GlobalApplication.getInstance();
//		String firstPicInitDate = mIns.getFirstPicture();
//		String filePath = Database.getInstance(getApplicationContext())
//				.getImageFilePathWithInitDate(firstPicInitDate);
//		Bitmap content = null;
//		if(!(FileUtil.isSDCardExist())){
//			String imageName = this.houseStyle + "album";
//			int id = MainActivity.this.getResources().getIdentifier(imageName,
//					"drawable", "com.minus.lovershouse"); // name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
//			MainActivity.this.mainToPicBtn.setImageResource(id);
//			 String hint = "SD卡已经拔出，想你相册，語音，双人动作，头像等功能不可用";
//			Toast.makeText(getApplicationContext(), hint, Toast.LENGTH_SHORT).show();
//			return;
//		}
//		if (!(filePath.equals(""))) {
//
//			content =imageLoader.loadImageSync("file://"+filePath); 
//
//			if (content != null) {
//				havePic = true;
//
//			}
//		}
//
//		if (havePic) {
//			// album frame
//
//			String imageName = "main_album_" + this.houseStyle;
//			int id = MainActivity.this.getResources().getIdentifier(imageName,
//					"drawable", "com.minus.lovershouse"); // name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
//			BitmapFactory.Options opt = new BitmapFactory.Options();
//			opt.inPreferredConfig = Bitmap.Config.RGB_565;// 表示16位位图
//															// 565代表对应三原色占的位数
//			opt.inInputShareable = true;
//			opt.inSampleSize = 1;
//			opt.inPurgeable = true;// 设置图片可以被回收
//			// InputStream is = getResources().openRawResource(id);
//			SoftReference<Bitmap> albumFrame =new SoftReference<Bitmap>(BitmapFactory.decodeResource(getResources(),
//					id, opt));
//
//			int albumHeight = albumFrame.get().getHeight();
//			int albumWidth = albumFrame.get().getWidth();
//			opt = null;
//			
//			SoftReference<Bitmap> resizedPictureBitmap = new SoftReference<Bitmap>(Bitmap.createScaledBitmap(content,
//					albumWidth - 50, albumHeight - 50, true));
//
//			Bitmap m1 = (AppManagerUtil.mergeBitmap(albumFrame.get(),
//					resizedPictureBitmap.get(), 25, 25));
//			Matrix skewMatrix = new Matrix();
//			// 倾斜程度
//			float skew1 = 0f;
//			float skew2 = 0.4f;
//			skewMatrix.setSkew(skew1, skew2);
//
//			Bitmap picBitmap =Bitmap.createBitmap(m1, 0, 0, albumWidth,
//					albumHeight, skewMatrix, true);
//
//			MainActivity.this.mainToPicBtn.setImageBitmap(picBitmap);
////			resizedPictureBitmap.get().recycle();
//			resizedPictureBitmap = null;
////			albumFrame.get().recycle();
//			albumFrame = null;
////			content.recycle();
//			content = null;
//			picBitmap = null;
//
//		} else {
//			String imageName = this.houseStyle + "album";
//			int id = MainActivity.this.getResources().getIdentifier(imageName,
//					"drawable", "com.minus.lovershouse"); // name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
//			MainActivity.this.mainToPicBtn.setImageResource(id);
//
//		}
//	}

	public void initLampButton() {

		if (isMatch) {
			if (GlobalApplication.getInstance().getLightState()
					.equals(Protocol.LightOff + "")) {
				if (houseStyle.equals(Protocol.pinkHouseStr)) {
					MainActivity.this.lampBtn
							.setBackgroundResource(R.drawable.brownlampoff);
				} else if (houseStyle.equals(Protocol.brownHouseStr)) {
					MainActivity.this.lampBtn
							.setBackgroundResource(R.drawable.pinklampoff);
				} else {
					MainActivity.this.lampBtn
							.setBackgroundResource(R.drawable.bluelampoff);
				}
				// String lightOffName = houseStyle + "lampoff";
				// int id = getResources().getIdentifier(lightOffName,
				// "drawable",
				// "com.minus.lovershouse"); //
				// name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
				// MainActivity.this.lampBtn.setBackgroundResource(id);
			} else {
				if (houseStyle.equals(Protocol.pinkHouseStr)) {
					MainActivity.this.lampBtn
							.setBackgroundResource(R.drawable.brownlampon);
				} else if (houseStyle.equals(Protocol.brownHouseStr)) {
					MainActivity.this.lampBtn
							.setBackgroundResource(R.drawable.pinklampon);
				} else {
					MainActivity.this.lampBtn
							.setBackgroundResource(R.drawable.bluelampon);
				}
				// String lightOnName = houseStyle + "lampon";
				// int id = getResources().getIdentifier(lightOnName,
				// "drawable",
				// "com.minus.lovershouse"); //
				// name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
				// MainActivity.this.lampBtn.setBackgroundResource(id);
			}
		} else {
			if (houseStyle.equals(Protocol.pinkHouseStr)) {
				MainActivity.this.lampBtn
						.setBackgroundResource(R.drawable.brownlampoff);
			} else if (houseStyle.equals(Protocol.brownHouseStr)) {
				MainActivity.this.lampBtn
						.setBackgroundResource(R.drawable.pink_lamp_off);
			} else {
				MainActivity.this.lampBtn
						.setBackgroundResource(R.drawable.bluelampoff);
			}
		}
	}
	


	// 初始化人物形象及其状态

	public void initFiguresFromLocal() {
		// create self figure
		SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(Protocol.PREFERENCE_NAME,
						Activity.MODE_PRIVATE);
		String lastUser = mSP.getString("LastUser", "");
		if (!(lastUser.equals(""))) {
			createSelfFigure(false);

			if (SelfInfo.getInstance().isMatch()) {
				isMatch = true;
			} else {
				isMatch = false;
			}

			this.createMatchFigure(false);

		}
	}

	public void initUI() {

		initHouse();
		initChatButton();
		// initWeatherButton();
		initDiaryButton();
		// initCalendarButton();
		initAlbumButton();
		// initMapButton();
		initLampButton();
		initFiguresFromLocal();

	}

	public void resetViews() {
		initHouse();
		initAlbumButton();
		initLampButton();
		// reset self figure view
		createSelfFigure(true);
		// selfFigureDoAction();
		// reset tar
		createMatchFigure(true);
		// matchFigureDoAction();

	}

	// ---------------------------action system --------------------------

	public void createSelfFigure(boolean isNeedInit) {
		// ------ Get Self Information:include gender and match account
		SelfInfo selfinfo = SelfInfo.getInstance();
		boolean isNeedTemp = isNeedInit;
		if (selfinfo.getAppearance() == null) {
			selfinfo.setAppearance(String.format("%c%c%c%c", Protocol.DEFAULT,
					Protocol.DEFAULT, Protocol.DEFAULT, Protocol.DEFAULT));
			isNeedTemp = true;

		}
		if (selfinfo.getSex() == null) {
			selfinfo.setSex("b");
		}

		String status = SelfInfo.getInstance().getStatus();
		String selfApp = SelfInfo.getInstance().getAppearance();
		if (selfinfo.getSex().equals("b")) {
           if(isNeedTemp){
			InitFigureAppDrawable.getInstance().resetBoyAppearance(selfApp);
           }
			WeakReference<LayerDrawable> boyLD =new WeakReference<LayerDrawable>(InitFigureAppDrawable.getInstance().initBoy(
					getApplicationContext()));
			MainActivity.this.mainboyHBody.setImageDrawable(boyLD.get());
			boyLD = null;
			initFigure(selfApp, true, status, true);
		} else {
			if(isNeedTemp){
			InitFigureAppDrawable.getInstance().resetGirlAppearance(selfApp);
			}
			WeakReference<LayerDrawable> girlLD =new WeakReference<LayerDrawable>(InitFigureAppDrawable.getInstance().initGirl(getApplicationContext()));
			
			MainActivity.this.maingirlBody.setImageDrawable(girlLD.get());
			
			girlLD = null;
			initFigure(selfApp, false, status, true);
		}
		// ------ Create Single Figures End
	}

	/**
	 * 
	 * @param appearance
	 *            "x111":预留 , hairStyle , clothesStyle,DecorationStyle
	 */
	public void initFigure(String appearance, boolean isBoy, String status,
			boolean isSelf) {

		int statusType = -1;

		try {
			statusType = Integer.parseInt(status);
		} catch (Exception e) {

			statusType = -1;
		}
		switch (statusType) {
		case Protocol.SINGLE_ACTION_EAT:

		case Protocol.SINGLE_ACTION_SLEEP:

		case Protocol.SINGLE_ACTION_LEARN:

		case Protocol.SINGLE_ACTION_ANGRY:

		case Protocol.SINGLE_ACTION_MISS:

		case Protocol.SINGLE_ACTION_CUSTOM1:

		case Protocol.SINGLE_ACTION_CUSTOM2:

		case Protocol.SINGLE_ACTION_CUSTOM3:

		case Protocol.SINGLE_ACTION_CUSTOM4:

		case Protocol.SINGLE_ACTION_CUSTOM5:
			InitSingleActionStart(statusType, isBoy, isSelf);

			break;
		case Protocol.ActionEnd:
		default:
			// String hairImageName,decorationImageName,clothesImageName;

			if (isBoy) {
				boySitView.removeAllViews();
				boyStandView.removeAllViews();

				this.boyView.setVisibility(View.VISIBLE);
				this.boyView.setOnClickListener(MainActivity.this);
				this.boyView.setClickable(true);
                this.curBoyView = this.boyView;
				startBoyTwinkle();
				startBoyHandShake();

			} else {
				girlSitView.removeAllViews();
				girlStandView.removeAllViews();
				this.girlView.setVisibility(View.VISIBLE);
				 this.curGirlView = this.girlView;
				 this.girlView.setOnClickListener(MainActivity.this);
				 this.girlView.setClickable(true);
				startGirlTwinkle();
				startGirlHandShake();
			}
			break;
		}

	}

	public int getImageid(String imageName) {
		int mImageid = getResources().getIdentifier(imageName, "drawable",
				"com.minus.lovershouse");
		// name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
		return mImageid;
	}

	public void createMatchFigure(boolean isNeedInit) {
		// ------ Get Self Information:include gender and match account
		SelfInfo selfinfo = SelfInfo.getInstance();
		GlobalApplication mIns = GlobalApplication.getInstance();

		// ------ Create Single Figures: match figure
		
		if (selfinfo.isMatch()) {
			this.isMatch = true;
			String status = mIns.getTiStatus();
			String tiApp = mIns.getTiAppearance();
			if (tiApp == null) {
				tiApp = String.format("%c%c%c%c", Protocol.DEFAULT,
						Protocol.DEFAULT, Protocol.DEFAULT, Protocol.DEFAULT);
				mIns.setTiAppearance(tiApp);
			}
			if (selfinfo.getSex() == null) {
				selfinfo.setSex("b");
			}

			if (selfinfo.getSex().equals("b")) {
               if(isNeedInit){
				InitFigureAppDrawable.getInstance().resetGirlAppearance(tiApp);
               }
               
				WeakReference<LayerDrawable> girlLD = new WeakReference<LayerDrawable>(InitFigureAppDrawable.getInstance()
						.initGirl(getApplicationContext()));
				MainActivity.this.maingirlBody.setImageDrawable(girlLD.get());
				
				girlLD = null;

				initFigure(tiApp, false, status, false);
			} else {
			if(isNeedInit){
				InitFigureAppDrawable.getInstance().resetBoyAppearance(tiApp);
			}
			
				WeakReference<LayerDrawable> boyLD = new WeakReference<LayerDrawable>(InitFigureAppDrawable.getInstance()
						.initBoy(getApplicationContext()));

				MainActivity.this.mainboyHBody.setImageDrawable(boyLD.get());
				boyLD = null;
				initFigure(tiApp, true, status, false);

			}
		} else {
			if (selfinfo.getSex().equals("b")) {
				this.girlView.setVisibility(View.GONE);
			} else {
				this.boyView.setVisibility(View.GONE);
			}
		}
		// ------ Create Single Figures End

	}

	private void finishinit() {
		if (SelfInfo.getInstance().isMatch()) {
			this.isMatch = true;

		} else {
			this.isMatch = false;
		}
		this.initFinish = true;
		SelfInfo.getInstance().setMainInit(true);

	}

	private void initMain() {
		// -----------------------------------------------------------------------

		this.initFinish = SelfInfo.getInstance().isMainInit();
		if (this.initFinish) {

			if (SelfInfo.getInstance().isMatch()) {
				this.isMatch = true;
				initMainweather();
				
			} else {
				this.isMatch = false;
			}
			// int who = MainActivity.this.getIntent().getIntExtra("who", 0);
			initHouse();
			
//			this.initAnnLayout();
			initChatButton();
			initDiaryButton();
			initAlbumButton();
			initLampButton();

			createSelfFigure(false);
			createMatchFigure(false);
			handleToActionMsg(); // 处理待处理的双人动作
			if (isRecvAction) {
				handleActionMsg();
			}

		} else {
			SelfInfo.getInstance().setDefault();
			int who = MainActivity.this.getIntent().getIntExtra("who", 0);
			if (SelfInfo.getInstance().isOnline() == false) {
				Log.d("Login", "MainActivity SelfInfo.getInstance().isOnline() == false");
				showInitMainProgressHUD("登陆中。。。");
			}
			//如果登陆超过8秒登陆超时
			this.actionHandler.sendEmptyMessageDelayed(0x21, 8000);
			// waitingHUD = ProgressHUD.show(MainActivity.this, "登陆中。。。", true,
			// false, null);
			if (who == 1||who == 3) {
				this.initFinish = true;
				SelfInfo.getInstance().setAccount(
						MainActivity.this.getIntent().getStringExtra("usr")
								.toLowerCase());
				SelfInfo.getInstance().setPwd(
						MainActivity.this.getIntent().getStringExtra("pwd"));
				LoginTask t = new LoginTask();
				t.execute(MainActivity.this.getIntent().getStringExtra("usr"),
						MainActivity.this.getIntent().getStringExtra("pwd"));
				// from login activity or register if (who == 2) 
			} else {
				// from welcome activity or 
				if(!(SelfInfo.getInstance().isOnline())){
				UserTable selfInfo = Database.getInstance(
						getApplicationContext()).getSelfInfo();
				SelfInfo.getInstance().setInfo(selfInfo.getAccount(),
						selfInfo.getPassword());

				GlobalApplication.getInstance().setTargetDefault();
				GlobalApplication.getInstance().setCommonDefault();
				LoginTask t = new LoginTask();
				t.execute(selfInfo.getAccount(), selfInfo.getPassword());
				}
			}

		}

	}

	// 主界面的天气背景 获取对方的天气
	private void initMainweather() {

		// 初始天气的逻辑
		// String
		// weathercode=Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
		// .getTargetWeather(SelfInfo.getInstance().getTarget());
		SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(SelfInfo.getInstance().getAccount(),
						Activity.MODE_PRIVATE);
		String lastCode = mSP.getString("targetTodayWeather", "-1");

		if (!lastCode.equals("-1")) {
			mainToWeatherBtn.setImageDrawable(weatherUtils.ImageViewChange(
					lastCode, "main"));
			// Boolean weatherChange = mSP.getBoolean("weatherChange", false);
			// if (weatherChange) {
			// setGlassBackground();
			// SharedPreferences.Editor mEditor = mSP.edit();
			// mEditor.putBoolean("weatherChange", false);
			// mEditor.commit();
			// }

		} else {
			mAMapLocManager = LocationManagerProxy.getInstance(this);

			/**
			 * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 5000, 10, this);
			// locationHandler.postDelayed((Runnable) this, 12000);//
			// 设置超过12秒还没有定位到就停止定位
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					stopLocation();
				}
			}, 12000);
		}

	}

	// 开启主界面招手眨眼睛
	private void startTwinkeAndHandShake() {
		if (isMatch) {
			startBoyTwinkle();
			startBoyHandShake();
			
			
			startGirlTwinkle();
			startGirlHandShake();
		} else {
			if (SelfInfo.getInstance().getSex().equals("b")) {
				startBoyTwinkle();
				startBoyHandShake();
			} else {
				startGirlTwinkle();
				startGirlHandShake();
			}

		}

	}

	public class LoginTask extends AsyncTask<String, String, Void> {

		@Override
		protected void onPreExecute() {

			// waitingHUD = ProgressHUD.show(MainActivity.this, "登陆中。。。", true,
			// false, null);

			super.onPreExecute();
		}
		@Override
		
		protected Void doInBackground(String... params) {
			//if (SelfInfo.getInstance().isOnline() == false) {
				UserPacketHandler mUserPacketHandler = new UserPacketHandler();
				mUserPacketHandler.Login(params[0], params[1]);
			//}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {

			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {

			// actionHandler.sendEmptyMessageDelayed(0x20, 15000);

			super.onPostExecute(result);
		}

	}

//	// 将头像与框合并 90.90 并暂时存在全局变量中
//	// path ,isboy,isme
//	public class HandleHeadPhotoTask extends AsyncTask<String, String, Void> {
//
//		@Override
//		protected void onPreExecute() {
//
//			super.onPreExecute();
//		}
//
//		@Override
//		protected Void doInBackground(String... params) {
//			HeadPhotoHanddler mHeadPhotoHandler = new HeadPhotoHanddler();
//			GlobalApplication mIns = GlobalApplication.getInstance();
//			Bitmap endBm = null;
//			if (params[1].equals("b")) {
//				Bitmap frameBm = BitmapFactory.decodeResource(getResources(),
//						R.drawable.boy_photoframe);
//				endBm = (mHeadPhotoHandler.handleHeadPhoto(params[0], frameBm,
//						mIns.getApplicationContext()));
//				frameBm.recycle();
//				frameBm = null;
//			} else {
//				Bitmap frameBm = BitmapFactory.decodeResource(getResources(),
//						R.drawable.girl_photoframe);
//				endBm = (mHeadPhotoHandler.handleHeadPhoto(params[0], frameBm,
//						mIns.getApplicationContext()));
//			}
//			if (params[2].equals("1")) {
//				// Bitmap headPhoto = AppManagerUtil.getDiskBitmap(params[0]);
//				GlobalApplication.getInstance().setHeadPicBm(endBm);
//				// GlobalApplication.getInstance().setHeadPicBm(headPhoto);
//
//			} else {
//				GlobalApplication.getInstance().setTarHeadPicBm(endBm);
//			}
//			return null;
//		}
//
//		@Override
//		protected void onProgressUpdate(String... values) {
//
//			super.onProgressUpdate(values);
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//
//			super.onPostExecute(result);
//		}
//
//	}
	
	

	/**
	 * 主界面的按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		if (this.isMatch) {
			if(v.equals(this.lampBtn)){
				 UserPacketHandler userHandler = new UserPacketHandler();
				 String lightState = GlobalApplication.getInstance().getLightState();
				 if(lightState.equals(Protocol.LightOff+"")){
					 //light the light 
					 if(userHandler.turnOnLight()){
						 GlobalApplication.getInstance().setLightState(Protocol.LightOn+"");
						 Database.getInstance(getApplicationContext()).updateLightState(
								 SelfInfo.getInstance().getAccount(), Protocol.LightOn+"");
						 this.initLampButton();
					 }
				 }else{
					 if(userHandler.turnOffLight()){
						 GlobalApplication.getInstance().setLightState(Protocol.LightOff+"");
						 Database.getInstance(getApplicationContext()).updateLightState(
								 SelfInfo.getInstance().getAccount(), Protocol.LightOff+"");
						 this.initLampButton();
					 }
				 }
			}
			if (v.equals(mainToConfigBtn)) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ConfigActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);

			}

			if (v.equals(mainToChatBtn)) {
				if (this.chatnewbadge != null) {
					if ((chatnewbadge.isShown())) {
						chatnewbadge.toggle();
					}
				}
				// 聊天提醒bubble
				if(boyChatbubbleflag != null){
				boyChatbubbleflag.setVisibility(View.GONE);
				boyChatbubbleflag.setOnClickListener(null);
				}
				// 聊天提醒bubble
				if(girlChatbubbleflag != null){
				girlChatbubbleflag.setVisibility(View.GONE);
				girlChatbubbleflag.setOnClickListener(null);
				}
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ChatActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}

			if (v.equals(mainToDiaryBtn)) {
				if (diarynewbadge != null) {
					if ((diarynewbadge.isShown())) {
						diarynewbadge.toggle();
					}
				}
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DiaryActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
			if (v.equals(this.mainToAnnLayout)) {
				if (CalendarNewbadge != null && (CalendarNewbadge.isShown())) {
					CalendarNewbadge.toggle();
				}
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, CalendarMainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}

			if (v.equals(this.maintoMapBtn)) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MapActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}

			if (v.equals(this.mainToWeatherBtn)) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, WeatherActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				overridePendingTransition(R.anim.push_bottom_in,
						R.anim.push_bottom_out);
			}
     
			if (v.equals(this.mainToPicBtn)) {
				 if(FileUtil.isSDCardExist()){
			    	  //如果sd卡不存在，则提示用户
				if (albumnewbadge != null) {
					if ((albumnewbadge.isShown())) {
						albumnewbadge.toggle();
					}
				}
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, GalleryActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
				startActivity(intent);
				overridePendingTransition(R.anim.push_bottom_in,
						R.anim.push_bottom_out);
				 }else{
			    Toast.makeText(getApplicationContext(), "SD卡已拔出，相册功能暂时不能使用", Toast.LENGTH_SHORT).show();
						
				 }
			}
			
			
			if (v.equals(boycustombubbleBtn)) {
				if (SelfInfo.getInstance().getSex().equals("b")) {
					// self action
					if (!(hideBoyCustomEndButton())) {
					MainActivity.this.showBoyCustomEndButton(girlcustombubbleRL);
					}else{
							//摇摆
						if(boycustombubble.getAnimation() != null){
							boycustombubble.getAnimation().reset();
							boycustombubble.getAnimation().start();
						}else{
								TranslateAnimation translateAnimation = new TranslateAnimation(0f, 20f, 0, 0);
								translateAnimation.setDuration(1000);
								translateAnimation.setRepeatCount(Animation.INFINITE);
								translateAnimation.setRepeatMode(Animation.REVERSE);
								boycustombubble.setAnimation(translateAnimation); //这里iv就是我们要执行动画的item，例如一个imageView
								translateAnimation.start();

						}
					

				}
			}
			}
//				if(v.getId() == R.id.girlcustomActionTop){
//					if (SelfInfo.getInstance().getSex().equals("g")) {
//						// self action
//						if (!(hideGirlCustomEndButton())) {
//							MainActivity.this.showGirlCustomEndButton(girlcustombubbleRL);
//						}else{
//							if(girlcustombubble.getAnimation() != null){
//								girlcustombubble.getAnimation().reset();
//								girlcustombubble.getAnimation().start();
//							}else{
//									TranslateAnimation translateAnimation = new TranslateAnimation(0f, 20f, 0, 0);
//									translateAnimation.setDuration(1000);
//									translateAnimation.setRepeatCount(Animation.INFINITE);
//									translateAnimation.setRepeatMode(Animation.REVERSE);
//									girlcustombubble.setAnimation(translateAnimation); //这里iv就是我们要执行动画的item，例如一个imageView
//									translateAnimation.start();
	//
//							}
//						
//						}
//				
//						}
	//
//					}
				
			if (v.equals(girlcustombubbleBtn)) {
				if (SelfInfo.getInstance().getSex().equals("g")) {
					// self action
					if (!(hideGirlCustomEndButton())) {
						MainActivity.this.showGirlCustomEndButton(girlcustombubbleRL);
					}else{
						if(girlcustombubble.getAnimation() != null){
							girlcustombubble.getAnimation().reset();
							girlcustombubble.getAnimation().start();
						}else{
								TranslateAnimation translateAnimation = new TranslateAnimation(0f, 20f, 0, 0);
								translateAnimation.setDuration(1000);
								translateAnimation.setRepeatCount(Animation.INFINITE);
								translateAnimation.setRepeatMode(Animation.REVERSE);
								girlcustombubble.setAnimation(translateAnimation); 
								translateAnimation.start();

						}
					
					}
			
					}

				}
			
			if (v.equals(boycustomEndbadge)) {
				if (boycustomEndbadge.isShown()) {
					boycustomEndbadge.hide(true);
				}
				String currTime = MainActivity.this.GetCurrentTime();
				ActionPacketHandler mAP = new ActionPacketHandler();
				int actionType = nowAction - Protocol.SINGLE_ACTION_CUSTOM1 + 1;

				String chatmsg = "我结束了'" + nowCustomActionContent + "'状态";

				if (mAP.SendSingleCustomActionEndTime(currTime, actionType)) {
					addChatMsg(true, chatmsg, currTime);
					
					MainActivity.this.CustomActionEnd(true, nowAction, true);

				}

			}

			if (v.equals(girlcustomEndbadge)) {
				if (girlcustomEndbadge.isShown()) {
					girlcustomEndbadge.hide(true);
				}
				String currTime = MainActivity.this.GetCurrentTime();
				ActionPacketHandler mAP = new ActionPacketHandler();
				int actionType = nowAction - Protocol.SINGLE_ACTION_CUSTOM1 + 1;

				String chatmsg = "我结束了'" + nowCustomActionContent + "'状态";

				if (mAP.SendSingleCustomActionEndTime(currTime, actionType)) {
					addChatMsg(true, chatmsg, currTime);
					

					MainActivity.this.CustomActionEnd(true, nowAction, false);

				}

			}

			if (v.equals(boyEndbadge)) {
				
				if (boyEndbadge.isShown()) {
					boyEndbadge.hide(true);
				}
				ActionEnd(nowAction, true, true);
//				LayoutInflater inflater = LayoutInflater.from(this);
//				View view = inflater.inflate(R.layout.cancle_action, null);
//				RelativeLayout layout = (RelativeLayout) view
//						.findViewById(R.id.cancleAction);
//				endActionDialog = new Dialog(this, R.style.MyDialogStyle);
//				endActionDialog.setCancelable(false);
//				endActionDialog.setContentView(layout,
//						new LinearLayout.LayoutParams(
//								LinearLayout.LayoutParams.MATCH_PARENT,
//								LinearLayout.LayoutParams.MATCH_PARENT));
//
//				Button yes = (Button) view.findViewById(R.id.end_action_yes);
//				Button no = (Button) view.findViewById(R.id.end_action_no);
//				yes.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Message msg = new Message();
//						msg.what = 0x10;
//						msg.setTarget(actionHandler);
//						msg.sendToTarget();
//					}
//				});
//
//				no.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Message msg = new Message();
//						msg.what = 0x11;
//						msg.setTarget(actionHandler);
//						msg.sendToTarget();
//					}
//				});
//
//				endActionDialog.show();
			}
			if (v.equals(girlEndbadge)) {
				if (girlEndbadge.isShown()) {
					girlEndbadge.hide(true);
				}
				ActionEnd(nowAction, true, true);
//				LayoutInflater inflater = LayoutInflater.from(this);
//				View view = inflater.inflate(R.layout.cancle_action, null);
//				RelativeLayout layout = (RelativeLayout) view
//						.findViewById(R.id.cancleAction);
//				endActionDialog = new Dialog(this, R.style.MyDialogStyle);
//				endActionDialog.setCancelable(false);
//				endActionDialog.setContentView(layout,
//						new LinearLayout.LayoutParams(
//								LinearLayout.LayoutParams.MATCH_PARENT,
//								LinearLayout.LayoutParams.MATCH_PARENT));
//
//				Button yes = (Button) view.findViewById(R.id.end_action_yes);
//				Button no = (Button) view.findViewById(R.id.end_action_no);
//				yes.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Message msg = new Message();
//						msg.what = 0x10;
//						msg.setTarget(actionHandler);
//						msg.sendToTarget();
//					}
//				});
//
//				no.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Message msg = new Message();
//						msg.what = 0x11;
//						msg.setTarget(actionHandler);
//						msg.sendToTarget();
//					}
//				});
//
//				endActionDialog.show();
			}

			if (v.equals(boyChatbubbleflag)) {
				if (this.chatnewbadge != null) {
					if ((chatnewbadge.isShown())) {
						chatnewbadge.toggle();
					}
				}
				// 聊天提醒bubble
				boyChatbubbleflag.setVisibility(View.GONE);
				boyChatbubbleflag.setOnClickListener(null);
				// 跳到聊天界面
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ChatActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}

			if (v.equals(girlChatbubbleflag)) {
				if (this.chatnewbadge != null) {
					if ((chatnewbadge.isShown())) {
						chatnewbadge.toggle();
					}
				}
				// 聊天提醒bubble
				girlChatbubbleflag.setVisibility(View.GONE);
				girlChatbubbleflag.setOnClickListener(null);
				// 跳到聊天界面
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ChatActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}

			if (v.equals(this.boyView)) {

				if (myPortraitMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
					if (girlMenu.getStatus() == MenuItemView.STATUS_OPEN) {
						MyAnimations.startAnimations(MainActivity.this, girlMenu,
								300);
					}
					showPortraitSex = "b";
				} else {
					showPortraitSex = "none";
				}
				if (bactionBtnisSet == 0) {
					bactionBtnisSet = 1;

					DisplayMetrics dMetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
					// int ScreenWIDTH = dMetrics.widthPixels;
					int[] target = new int[2];
					this.boyView.getLocationOnScreen(target);
					int curX = target[0];
					// int curY = target[1];
					int curH = boyView.getHeight();
					int curY = target[1];
					curH = curY + curH / 2;
					int curW = boyView.getWidth();
					int boypos = (curX + curW / 2);
					myPortraitMenu.setPosition(boypos, curH);
					myPortraitMenu.setRadius(curW);
					myPortraitMenu.setMyChange(true);
					myPortraitMenu.requestLayout();
				}
				MyAnimations
						.startAnimations(MainActivity.this, myPortraitMenu, 300);
			}

			if (angryBoyView != null && angryBoyView.get()!= null &&  v.equals(angryBoyView.get())) {
				if (SelfInfo.getInstance().getSex().equals("b")) {
					// self action
					if (!(hideBoyEndButton())) {
						MainActivity.this.showBoyEndButton(angryBoyView.get(),BadgeView.POSITION_TOP_LEFT);
					}

				} else {
					// tar action
					if (myPortraitMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
						if (girlMenu.getStatus() == MenuItemView.STATUS_OPEN) {
							MyAnimations.startAnimations(MainActivity.this,
									girlMenu, 300);
						}
						showPortraitSex = "b";
					} else {
						showPortraitSex = "none";
					}
					if (bactionBtnisSet == 0) {
						bactionBtnisSet = 1;
						DisplayMetrics dMetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

						int[] target = new int[2];
						this.angryBoyView.get().getLocationOnScreen(target);
						int curX = target[0];
						int curW = angryBoyView.get().getWidth();
						int boypos = (curX + curW / 2);
						int curH = angryBoyView.get().getHeight();
						int curY = target[1];
						curH = curY + curH / 2;
						myPortraitMenu.setPosition(boypos, curH);
						myPortraitMenu.setRadius(curW);
						myPortraitMenu.setMyChange(true);
						myPortraitMenu.requestLayout();
					}
					MyAnimations.startAnimations(MainActivity.this, myPortraitMenu,
							300);
				}
			}
			if (missBoyView != null && missBoyView.get() != null&& v.equals(missBoyView.get())) {
				if (SelfInfo.getInstance().getSex().equals("b")) {
					// self action
					if (!(hideBoyEndButton())) {
						MainActivity.this.showBoyEndButton(missBoyView.get(),BadgeView.POSITION_TOP_LEFT);
					}

				} else {
					// tar action
					if (myPortraitMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
						if (girlMenu.getStatus() == MenuItemView.STATUS_OPEN) {
							MyAnimations.startAnimations(MainActivity.this,
									girlMenu, 300);
						}
						showPortraitSex = "b";
					} else {
						showPortraitSex = "none";
					}
					if (bactionBtnisSet == 0) {
						bactionBtnisSet = 1;

						DisplayMetrics dMetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

						int[] target = new int[2];
						this.missBoyView.get().getLocationOnScreen(target);
						int curX = target[0];
						int curW = missBoyView.get().getWidth();
						int curH = missBoyView.get().getHeight();
						int curY = target[1];
						curH = curY + curH / 2;
						int boypos = (curX + curW / 2);
						myPortraitMenu.setPosition(boypos, curH);
						myPortraitMenu.setRadius(curW);
						myPortraitMenu.setMyChange(true);
						myPortraitMenu.requestLayout();
					}
					MyAnimations.startAnimations(MainActivity.this, myPortraitMenu,
							300);
				}
			}

			if (v.equals(readBoyView)) {
				if (SelfInfo.getInstance().getSex().equals("b")) {
					// self action
					if (!(hideBoyEndButton())) {
						MainActivity.this.showBoyEndButton(readBoyView,BadgeView.POSITION_TOP_LEFT);
					}

				} else {
					// tar action
					if (myPortraitMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
						if (girlMenu.getStatus() == MenuItemView.STATUS_OPEN) {
							MyAnimations.startAnimations(MainActivity.this,
									girlMenu, 300);
						}
						showPortraitSex = "b";
					} else {
						showPortraitSex = "none";
					}
					if (bactionBtnisSet == 0) {
						bactionBtnisSet = 1;

						DisplayMetrics dMetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

						int[] target = new int[2];
						this.readBoyView.getLocationOnScreen(target);
						int curX = target[0];
						int curW = readBoyView.getWidth();
						int boypos = (curX + curW / 2);
						int curH = readBoyView.getHeight();
						int curY = target[1];
						curH = curY + curH / 2;
						myPortraitMenu.setPosition(boypos, curH);
						myPortraitMenu.setRadius(curW);
						myPortraitMenu.setMyChange(true);
						myPortraitMenu.requestLayout();
					}
					MyAnimations.startAnimations(MainActivity.this, myPortraitMenu,
							300);
				}
			}

			if (v.equals(eatBoyView)) {
				if (SelfInfo.getInstance().getSex().equals("b")) {
					// self action
					if (!(hideBoyEndButton())) {
						MainActivity.this.showBoyEndButton(eatBoyView,BadgeView.POSITION_TOP_LEFT);
					}

				} else {
					// tar action
					if (myPortraitMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
						if (girlMenu.getStatus() == MenuItemView.STATUS_OPEN) {
							MyAnimations.startAnimations(MainActivity.this,
									girlMenu, 300);
						}
						showPortraitSex = "b";
					} else {
						showPortraitSex = "none";
					}
					if (bactionBtnisSet == 0) {
						bactionBtnisSet = 1;

						DisplayMetrics dMetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

						int[] target = new int[2];
						this.eatBoyView.getLocationOnScreen(target);
						int curX = target[0];
						int curW = eatBoyView.getWidth();
						int boypos = (curX + curW / 2);
						int curH = eatBoyView.getHeight();
						int curY = target[1];
						curH = curY + curH / 2;
						myPortraitMenu.setPosition(boypos, curH);
						myPortraitMenu.setRadius(curW);
						myPortraitMenu.setMyChange(true);
						myPortraitMenu.requestLayout();
					}
					MyAnimations.startAnimations(MainActivity.this, myPortraitMenu,
							300);
				}
			}
			if(v.equals(this.sleepRL)){
				//当且仅当自己在睡觉才启用该点击监听
				if (SelfInfo.getInstance().getSex().equals("b")) {
					// self action
					
					hideAllMenu();
				
					
					if (!(hideBoyEndButton())) {
						MainActivity.this.showBoyEndButton(this.sleepRL,BadgeView.POSITION_TOP_RIGHT);
					}
				}else{
					if (!(hideGirlEndButton())) {
						MainActivity.this.showGirlEndButton(this.sleepRL,BadgeView.POSITION_TOP_RIGHT);
					}
				}
			}

			

			if (v.equals(angryGirlView)) {
				if (SelfInfo.getInstance().getSex().equals("g")) {
					// self action
					if (!(hideGirlEndButton())) {
						MainActivity.this.showGirlEndButton(angryGirlView,BadgeView.POSITION_TOP_LEFT);
					}
				} else {
					// tar action
					if (girlMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
						if (myPortraitMenu.getStatus() == MenuItemView.STATUS_OPEN) {
							MyAnimations.startAnimations(MainActivity.this,
									myPortraitMenu, 300);
						}
						showPortraitSex = "g";
					} else {
						showPortraitSex = "none";
					}
					if (gactionBtnisSet == 0) {
						gactionBtnisSet = 1;

						DisplayMetrics dMetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
						int[] target = new int[2];
						this.angryGirlView.getLocationOnScreen(target);
						int curX = target[0];
						int curW = angryGirlView.getWidth();
						int boypos = (curX + curW / 2);
						int curH = angryGirlView.getHeight();
						int curY = target[1];
						curH = curY + curH / 2;
						girlMenu.setPosition(boypos, curH);
						girlMenu.setRadius(curW);
						girlMenu.setMyChange(true);
						girlMenu.requestLayout();

					}
					MyAnimations.startAnimations(MainActivity.this, girlMenu, 300);
				}
			}

			if (v.equals(missGirlView)) {
				if (SelfInfo.getInstance().getSex().equals("g")) {
					// self action
					if (!(hideGirlEndButton())) {
						MainActivity.this.showGirlEndButton(missGirlView,BadgeView.POSITION_TOP_LEFT);
					}

				} else {
					// tar action
					// tar action
					if (girlMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
						if (myPortraitMenu.getStatus() == MenuItemView.STATUS_OPEN) {
							MyAnimations.startAnimations(MainActivity.this,
									myPortraitMenu, 300);
						}
						showPortraitSex = "g";
					} else {
						showPortraitSex = "none";
					}
					if (gactionBtnisSet == 0) {
						gactionBtnisSet = 1;

						DisplayMetrics dMetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
						int[] target = new int[2];
						this.missGirlView.getLocationOnScreen(target);
						int curX = target[0];
						int curW = missGirlView.getWidth();
						int boypos = (curX + curW / 2);
						int curH = missGirlView.getHeight();

						int curY = target[1];
						curH = curY + curH / 2;
						girlMenu.setPosition(boypos, curH);
						girlMenu.setRadius(curW);
						girlMenu.setMyChange(true);
						girlMenu.requestLayout();

					}
					MyAnimations.startAnimations(MainActivity.this, girlMenu, 300);
				}
			}

			if (v.equals(readGirlView)) {
				if (SelfInfo.getInstance().getSex().equals("g")) {
					// self action
					if (!(hideGirlEndButton())) {
						MainActivity.this.showGirlEndButton(readGirlView,BadgeView.POSITION_TOP_LEFT);
					}
				} else {
					// tar action
					if (girlMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
						if (myPortraitMenu.getStatus() == MenuItemView.STATUS_OPEN) {
							MyAnimations.startAnimations(MainActivity.this,
									myPortraitMenu, 300);
						}
						showPortraitSex = "g";
					} else {
						showPortraitSex = "none";
					}
					if (gactionBtnisSet == 0) {
						gactionBtnisSet = 1;

						DisplayMetrics dMetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
						int[] target = new int[2];
						this.readGirlView.getLocationOnScreen(target);
						int curX = target[0];
						int curW = readGirlView.getWidth();
						int boypos = (curX + curW / 2);
						int curH = readGirlView.getHeight();
						int curY = target[1];
						curH = curY + curH / 2;
						girlMenu.setPosition(boypos, curH);
						girlMenu.setRadius(curW);
						girlMenu.setMyChange(true);
						girlMenu.requestLayout();

					}
					MyAnimations.startAnimations(MainActivity.this, girlMenu, 300);
				}
			}

			if (v.equals(eatGirlView)) {
				if (SelfInfo.getInstance().getSex().equals("g")) {
					// self action
					if (!(hideGirlEndButton())) {
						MainActivity.this.showGirlEndButton(eatGirlView,BadgeView.POSITION_TOP_LEFT);
					}
				} else {
					// tar action
					if (girlMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
						if (myPortraitMenu.getStatus() == MenuItemView.STATUS_OPEN) {
							MyAnimations.startAnimations(MainActivity.this,
									myPortraitMenu, 300);
						}
						showPortraitSex = "g";
					} else {
						showPortraitSex = "none";
					}
					if (gactionBtnisSet == 0) {
						gactionBtnisSet = 1;

						DisplayMetrics dMetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
						int[] target = new int[2];
						this.eatGirlView.getLocationOnScreen(target);
						int curX = target[0];
						int curW = eatGirlView.getWidth();
						int boypos = (curX + curW / 2);
						int curH = eatGirlView.getHeight();
						int curY = target[1];
						curH = curY + curH / 2;

						girlMenu.setPosition(boypos, curH);
						girlMenu.setRadius(curW);
						girlMenu.setMyChange(true);
						girlMenu.requestLayout();

					}
					MyAnimations.startAnimations(MainActivity.this, girlMenu, 300);
				}
			}

			
			if (v.equals(this.girlView)) {

				if (girlMenu.getStatus() == MenuItemView.STATUS_CLOSE) {
					if (myPortraitMenu.getStatus() == MenuItemView.STATUS_OPEN) {
						MyAnimations.startAnimations(MainActivity.this,
								myPortraitMenu, 300);
					}

					showPortraitSex = "g";
				} else {
					showPortraitSex = "none";
				}
				if (gactionBtnisSet == 0) {
					gactionBtnisSet = 1;

					DisplayMetrics dMetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

					int[] target = new int[2];
					this.girlView.getLocationOnScreen(target);
					int curX = target[0];

					int curW = girlView.getWidth();
					int boypos = (curX + curW / 2);
					int curH = girlView.getHeight();
					int curY = target[1];
					curH = curY + curH / 2;
					girlMenu.setPosition(boypos, curH);
					girlMenu.setRadius(curW);
					girlMenu.setMyChange(true);
					girlMenu.requestLayout();

				}
				MyAnimations.startAnimations(MainActivity.this, girlMenu, 300);
			}
     
		} else {
			// no match
			if (v.equals(mainToConfigBtn)) {
				if (giudeToMatchbadge.isShown()) {
					giudeToMatchbadge.toggle();
				} 
				if(mProgressHUD != null && mProgressHUD.isShowing()){
					mProgressHUD.dismiss();
				}
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ConfigActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}else{
//	
			this.askForMatch();
			}
			
			
			
		}
		
	
	}

	private void hideAllMenu() {
		if (myPortraitMenu.getStatus() == MenuItemView.STATUS_OPEN) {
			MyAnimations.startAnimations(MainActivity.this,
					myPortraitMenu, 300);
		}
		if (girlMenu.getStatus() == MenuItemView.STATUS_OPEN) {
			MyAnimations.startAnimations(MainActivity.this,
					girlMenu, 300);
		}
		
		
	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int i = (int) event.getX();
		int j = (int) event.getY();
		
		
		int[] target = new int[2];
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(girlMenu.getStatus() == MenuItemView.STATUS_OPEN){
				if(this.curGirlView != null){
				this.curGirlView.getLocationOnScreen(target);
				int curX = target[0] ;
				int curY = target[1];
				int curH = this.curGirlView.getHeight();
				int curW = this.curGirlView.getWidth();
				if((i < curX) || (i > curX + curW) || (j < curY) || (j > (curY + curH))){
					MyAnimations.startAnimations(MainActivity.this,
							girlMenu, 300);
					}
				}

			}else if(myPortraitMenu.getStatus() == MenuItemView.STATUS_OPEN){
				if(this.curBoyView != null){
				this.curBoyView.getLocationOnScreen(target);
				int curX = target[0] ;
				int curY = target[1];
				int curH = this.curBoyView.getHeight();
				int curW = this.curBoyView.getWidth();
				if((i < curX) || (i > curX + curW) || (j < curY) || (j > (curY + curH))){
					MyAnimations.startAnimations(MainActivity.this,
							myPortraitMenu, 300);
					}

			}
			}
		}
 	return false;
	}

	@Override
	public void onclick(int item) {
		// 菜单点击
		// single person action
		String mySex = SelfInfo.getInstance().getSex();
		if (showPortraitSex.equals(mySex)) {
			if (this.myFigureMenupage == 0) {
				// 我的人物菜单第一页
				if (item == 0) {
					// 自定义+ or
					if (myCustomList == null || myCustomList.size() == 0) {
						showAddActionDialog();
					} else if (myFigureMenupage == 0) {
						// 跳到第二页
						this.myFigureMenupage = 1;
						RefreshCustomMenu(mySex);

					}

				} else if (item == 1) {
					ActionStart(Protocol.SINGLE_ACTION_MISS, true, true, "");
					// ActionPacketHandler mActionPacket = new
					// ActionPacketHandler();
					// mActionPacket.SendSingleActionBegin(GetCurrentTime(),
					// Protocol.SINGLE_ACTION_MISS);
				} else if (item == 2) {
					ActionStart(Protocol.SINGLE_ACTION_ANGRY, true, true, "");
					// ActionPacketHandler mActionPacket = new
					// ActionPacketHandler();
					// mActionPacket.SendSingleActionBegin(GetCurrentTime(),
					// Protocol.SINGLE_ACTION_ANGRY);
				} else if (item == 3) {
					ActionStart(Protocol.SINGLE_ACTION_LEARN, true, true, "");
					// ActionPacketHandler mActionPacket = new
					// ActionPacketHandler();
					// mActionPacket.SendSingleActionBegin(GetCurrentTime(),
					// Protocol.SINGLE_ACTION_LEARN);
				} else if (item == 4) {
					ActionStart(Protocol.SINGLE_ACTION_SLEEP, true, true, "");
					// ActionPacketHandler mActionPacket = new
					// ActionPacketHandler();
					// mActionPacket.SendSingleActionBegin(GetCurrentTime(),
					// Protocol.SINGLE_ACTION_SLEEP);
				} else if (item == 5) {
					// ActionPacketHandler mActionPacket = new
					// ActionPacketHandler();
					// mActionPacket.SendSingleActionBegin(GetCurrentTime(),
					// Protocol.SINGLE_ACTION_EAT);
					ActionStart(Protocol.SINGLE_ACTION_EAT, true, true, "");

				}
			} else if (this.myFigureMenupage == 1) {
				int mListSize = myCustomList.size();
				// 我的人物菜单第二页
				// 自定义按钮
				if (item == 0) {

					if (mListSize == 5) {
						String typeId = myCustomList.get(item).getTypeID();
						String currTime = GetCurrentTime();
						ActionPacketHandler mAp = new ActionPacketHandler();
						if (mAp.sendSingleCustomActionBeginTime(currTime,
								typeId)) {
							String statusCon = myCustomList.get(item)
									.getContent();
							String chatContent5 = "我目前状态是：" + statusCon;
							int actionType = -1;
							try {
								actionType = Integer.parseInt(typeId);
							} catch (Exception e) {
								actionType = -1;
							}
							actionType += Protocol.SINGLE_ACTION_CUSTOM1 - 1;
							MainActivity.this.addChatMsg(true, chatContent5,
									currTime);
							MainActivity.this.CustomActionStart(true,
									actionType, mySex, statusCon);
						} else {
							Toast.makeText(getApplicationContext(), "发送失败",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						// 第二页添加
						showAddActionDialog();

					}
				} else if (item == 1) {
					// 自定义动作
					if (mListSize == 5) {
						String typeId = myCustomList.get(item).getTypeID();
						String currTime = GetCurrentTime();
						ActionPacketHandler mAp = new ActionPacketHandler();
						if (mAp.sendSingleCustomActionBeginTime(currTime,
								typeId)) {
							String statusCon = myCustomList.get(item)
									.getContent();
							String chatContent5 = "我目前状态是：" + statusCon;
							int actionType = -1;
							try {
								actionType = Integer.parseInt(typeId);
							} catch (Exception e) {
								actionType = -1;
							}
							actionType += Protocol.SINGLE_ACTION_CUSTOM1 - 1;
							MainActivity.this.addChatMsg(true, chatContent5,
									currTime);
							MainActivity.this.CustomActionStart(true,
									actionType, mySex, statusCon);
						} else {
							Toast.makeText(getApplicationContext(), "发送失败",
									Toast.LENGTH_SHORT).show();
						}

					} else {
						String typeId = myCustomList.get(item - 1).getTypeID();
						String currTime = GetCurrentTime();
						ActionPacketHandler mAp = new ActionPacketHandler();
						if (mAp.sendSingleCustomActionBeginTime(currTime,
								typeId)) {
							String statusCon = myCustomList.get(item - 1)
									.getContent();
							String chatContent5 = "我目前状态是：" + statusCon;
							int actionType = -1;
							try {
								actionType = Integer.parseInt(typeId);
							} catch (Exception e) {
								actionType = -1;
							}
							actionType += Protocol.SINGLE_ACTION_CUSTOM1 - 1;
							MainActivity.this.addChatMsg(true, chatContent5,
									currTime);
							MainActivity.this.CustomActionStart(true,
									actionType, mySex, statusCon);
						} else {
							Toast.makeText(getApplicationContext(), "发送失败",
									Toast.LENGTH_SHORT).show();
						}

					}

				} else if (item == 2) {

					if (mListSize == 5) {
						String typeId = myCustomList.get(item).getTypeID();
						String currTime = GetCurrentTime();
						ActionPacketHandler mAp = new ActionPacketHandler();
						if (mAp.sendSingleCustomActionBeginTime(currTime,
								typeId)) {
							String statusCon = myCustomList.get(item)
									.getContent();
							String chatContent5 = "我目前状态是：" + statusCon;
							int actionType = -1;
							try {
								actionType = Integer.parseInt(typeId);
							} catch (Exception e) {
								actionType = -1;
							}
							actionType += Protocol.SINGLE_ACTION_CUSTOM1 - 1;
							MainActivity.this.addChatMsg(true, chatContent5,
									currTime);
							MainActivity.this.CustomActionStart(true,
									actionType, mySex, statusCon);
						} else {
							Toast.makeText(getApplicationContext(), "发送失败",
									Toast.LENGTH_SHORT).show();
						}

					} else if (mListSize >= item) {
						String typeId = myCustomList.get(item - 1).getTypeID();
						String currTime = GetCurrentTime();
						ActionPacketHandler mAp = new ActionPacketHandler();
						if (mAp.sendSingleCustomActionBeginTime(currTime,
								typeId)) {
							String statusCon = myCustomList.get(item - 1)
									.getContent();
							String chatContent5 = "我目前状态是：" + statusCon;
							int actionType = -1;
							try {
								actionType = Integer.parseInt(typeId);
							} catch (Exception e) {
								actionType = -1;
							}
							actionType += Protocol.SINGLE_ACTION_CUSTOM1 - 1;
							MainActivity.this.addChatMsg(true, chatContent5,
									currTime);
							MainActivity.this.CustomActionStart(true,
									actionType, mySex, statusCon);
						} else {
							Toast.makeText(getApplicationContext(), "发送失败",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						// back
						// 跳到第1页
						this.myFigureMenupage = 0;
						SetSelfMainActionMenu(true);
						// Toast.makeText(getApplicationContext(), "back",
						// Toast.LENGTH_LONG).show();
					}
				} else if (item == 3) {

					if (mListSize == 5) {
						String typeId = myCustomList.get(item).getTypeID();
						String currTime = GetCurrentTime();
						ActionPacketHandler mAp = new ActionPacketHandler();
						if (mAp.sendSingleCustomActionBeginTime(currTime,
								typeId)) {
							String statusCon = myCustomList.get(item)
									.getContent();
							String chatContent5 = "我目前状态是：" + statusCon;
							int actionType = -1;
							try {
								actionType = Integer.parseInt(typeId);
							} catch (Exception e) {
								actionType = -1;
							}
							actionType += Protocol.SINGLE_ACTION_CUSTOM1 - 1;
							MainActivity.this.addChatMsg(true, chatContent5,
									currTime);
							MainActivity.this.CustomActionStart(true,
									actionType, mySex, statusCon);
						} else {
							Toast.makeText(getApplicationContext(), "发送失败",
									Toast.LENGTH_SHORT).show();
						}

					} else if (mListSize >= item) {
						String typeId = myCustomList.get(item - 1).getTypeID();
						String currTime = GetCurrentTime();
						ActionPacketHandler mAp = new ActionPacketHandler();
						if (mAp.sendSingleCustomActionBeginTime(currTime,
								typeId)) {
							String statusCon = myCustomList.get(item - 1)
									.getContent();
							String chatContent5 = "我目前状态是：" + statusCon;
							int actionType = -1;
							try {
								actionType = Integer.parseInt(typeId);
							} catch (Exception e) {
								actionType = -1;
							}
							actionType += Protocol.SINGLE_ACTION_CUSTOM1 - 1;
							MainActivity.this.addChatMsg(true, chatContent5,
									currTime);
							MainActivity.this.CustomActionStart(true,
									actionType, mySex, statusCon);
						} else {
							Toast.makeText(getApplicationContext(), "发送失败",
									Toast.LENGTH_SHORT).show();
						}

					} else {
						// back
						// 跳到第1页
						this.myFigureMenupage = 0;
						SetSelfMainActionMenu(true);

					}
				} else if (item == 4) {

					if (mListSize == 5) {
						String typeId = myCustomList.get(item).getTypeID();
						String currTime = GetCurrentTime();
						ActionPacketHandler mAp = new ActionPacketHandler();
						if (mAp.sendSingleCustomActionBeginTime(currTime,
								typeId)) {
							String statusCon = myCustomList.get(item)
									.getContent();
							String chatContent5 = "我目前状态是：" + statusCon;
							int actionType = -1;
							try {
								actionType = Integer.parseInt(typeId);
							} catch (Exception e) {
								actionType = -1;
							}
							actionType += Protocol.SINGLE_ACTION_CUSTOM1 - 1;
							MainActivity.this.addChatMsg(true, chatContent5,
									currTime);
							MainActivity.this.CustomActionStart(true,
									actionType, mySex, statusCon);
						} else {
							Toast.makeText(getApplicationContext(), "发送失败",
									Toast.LENGTH_SHORT).show();
						}

					} else if (mListSize >= item) {
						String typeId = myCustomList.get(item - 1).getTypeID();
						String currTime = GetCurrentTime();
						ActionPacketHandler mAp = new ActionPacketHandler();
						if (mAp.sendSingleCustomActionBeginTime(currTime,
								typeId)) {
							String statusCon = myCustomList.get(item - 1)
									.getContent();
							String chatContent5 = "我目前状态是：" + statusCon;
							int actionType = -1;
							try {
								actionType = Integer.parseInt(typeId);
							} catch (Exception e) {
								actionType = -1;
							}
							actionType += Protocol.SINGLE_ACTION_CUSTOM1 - 1;
							MainActivity.this.addChatMsg(true, chatContent5,
									currTime);
							MainActivity.this.CustomActionStart(true,
									actionType, mySex, statusCon);
						} else {
							Toast.makeText(getApplicationContext(), "发送失败",
									Toast.LENGTH_SHORT).show();
						}

					} else {
						// back
						// 跳到第1页
						this.myFigureMenupage = 0;
						SetSelfMainActionMenu(true);

					}
				} else if (item == 5) {

					// back
					// 跳到第1页
					this.myFigureMenupage = 0;
					SetSelfMainActionMenu(true);

				}

			}
		}
		// couple person action request
		else {
			if (SelfInfo.getInstance().getSex().equals("g")) {
				if (item == 0) {
					// 虐待
					ActionPacketHandler mActionPacket = new ActionPacketHandler();
					String curDate = GetCurrentTime();
					if(mActionPacket.SendCoupleActionRequest(curDate,
							Protocol.ABUSE)){
						String  chatmsg ="皮痒了吧。";

						MainActivity.this.addChatMsg(true, chatmsg, curDate);
					SelfInfo.getInstance().setAction("皮痒了吧。");
					this.ActionStart(Protocol.ABUSE, false, false, "");
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}

				} else if (item == 1) {
					ActionPacketHandler mActionPacket = new ActionPacketHandler();
					String curDate = GetCurrentTime();
					if(mActionPacket.SendCoupleActionRequest(curDate,
							Protocol.PINCHEDFACE)){
						String  chatmsg ="来！让我捏捏";

						MainActivity.this.addChatMsg(true, chatmsg, curDate);
					SelfInfo.getInstance().setAction("来！让我捏捏");
					this.ActionStart(Protocol.PINCHEDFACE, false, true, "");
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}
				} else if (item == 2) {
					ActionPacketHandler mActionPacket = new ActionPacketHandler();
					String curDate = GetCurrentTime();
					if(mActionPacket.SendCoupleActionRequest(curDate,
							Protocol.PETTING)){
					SelfInfo.getInstance().setAction("亲爱的，摸摸头！");
					String  chatmsg = "亲爱的，摸摸头！";

					MainActivity.this.addChatMsg(true, chatmsg, curDate);
					this.ActionStart(Protocol.PETTING, false, true, "");
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}
				} else if (item == 3) {
					String date = AppManagerUtil.getCurDate();
					ActionPacketHandler mActionPacket = new ActionPacketHandler();
					if(mActionPacket.SendCoupleActionRequest(date, Protocol.SEX)){
					SelfInfo.getInstance().setAction("请求已发出，请耐心等待对方同意喔");

					mProgressHUD = ProgressHUD.showSuccOrError(
							MainActivity.this, "请求已发出，请耐心等待对方同意喔", true);

					Message msg = actionHandler.obtainMessage();
					msg.what = 0x09;
					actionHandler.sendMessageDelayed(msg, 3000);
					String chatmsg = "来啊，快活啊…反正有大把时光";
					this.addChatMsg(true, chatmsg, date);
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}
				} else if (item == 4) {
					// 亲亲
					String date = AppManagerUtil.getCurDate();
					ActionPacketHandler mActionPacket = new ActionPacketHandler();
					if(mActionPacket.SendCoupleActionRequest(date, Protocol.KISS)){
					SelfInfo.getInstance().setAction("请求已发出，请耐心等待对方同意喔");

					mProgressHUD = ProgressHUD.showSuccOrError(
							MainActivity.this, "请求已发出，请耐心等待对方同意喔", true);

					Message msg = actionHandler.obtainMessage();
					msg.what = 0x09;
					actionHandler.sendMessageDelayed(msg, 3000);
					String chatmsg = "亲爱的亲亲我好咩！";
					this.addChatMsg(true, chatmsg, date);
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}
				} else if (item == 5) {
					// 亲亲
					String date = AppManagerUtil.getCurDate();
					ActionPacketHandler mActionPacket = new ActionPacketHandler();
					if(mActionPacket.SendCoupleActionRequest(date, Protocol.HUG)){
					SelfInfo.getInstance().setAction("请求已发出，请耐心等待对方同意喔");

					mProgressHUD = ProgressHUD.showSuccOrError(
							MainActivity.this, "请求已发出，请耐心等待对方同意喔", true);

					Message msg = actionHandler.obtainMessage();
					msg.what = 0x09;
					actionHandler.sendMessageDelayed(msg, 3000);
					String chatmsg = "我想抱抱你！";
					this.addChatMsg(true, chatmsg, date);
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}
				}
			} else { // boy's coupleAction
				if (item == 0) {
					String curDate = GetCurrentTime();
					ActionPacketHandler mActionPacket = new ActionPacketHandler();
					
					if(mActionPacket.SendCoupleActionRequest(curDate,
							Protocol.PINCHEDFACE)){
						String  chatmsg ="来！让我捏捏";

						MainActivity.this.addChatMsg(true, chatmsg, curDate);
					SelfInfo.getInstance().setAction("来！让我捏捏");
					this.ActionStart(Protocol.PINCHEDFACE, false, true, "");
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}
				} else if (item == 1) {
					String curDate = GetCurrentTime();
					ActionPacketHandler mActionPacket = new ActionPacketHandler();
					if(mActionPacket.SendCoupleActionRequest(curDate,
							Protocol.PETTING)){
						String  chatmsg ="亲爱的，摸摸头！";

						MainActivity.this.addChatMsg(true, chatmsg, curDate);
					SelfInfo.getInstance().setAction("亲爱的，摸摸头！");
					this.ActionStart(Protocol.PETTING, false, true, "");
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}
				} else if (item == 2) {
					String date = AppManagerUtil.getCurDate();
					ActionPacketHandler mActionPacket = new ActionPacketHandler();
					if(mActionPacket.SendCoupleActionRequest(date, Protocol.SEX)){
					SelfInfo.getInstance().setAction("请求已发出，请耐心等待对方同意喔");

					mProgressHUD = ProgressHUD.showSuccOrError(
							MainActivity.this, "请求已发出，请耐心等待对方同意喔", true);

					Message msg = actionHandler.obtainMessage();
					msg.what = 0x09;
					actionHandler.sendMessageDelayed(msg, 3000);
					String chatmsg = "来啊，快活啊…反正有大把时光";
					this.addChatMsg(true, chatmsg, date);
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}
				} else if (item == 3) {
					// 亲亲
					String date = AppManagerUtil.getCurDate();
					ActionPacketHandler mActionPacket = new ActionPacketHandler();
					if(mActionPacket.SendCoupleActionRequest(date, Protocol.KISS)){
					SelfInfo.getInstance().setAction("请求已发出，请耐心等待对方同意喔");

					mProgressHUD = ProgressHUD.showSuccOrError(
							MainActivity.this, "请求已发出，请耐心等待对方同意喔", true);

					Message msg = actionHandler.obtainMessage();
					msg.what = 0x09;
					actionHandler.sendMessageDelayed(msg, 3000);
					String chatmsg = "亲爱的亲亲我好咩！";
					this.addChatMsg(true, chatmsg, date);
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}
				} else if (item == 4) {
					// 亲亲
					String date = AppManagerUtil.getCurDate();
					ActionPacketHandler mActionPacket = new ActionPacketHandler();
					if(mActionPacket.SendCoupleActionRequest(date, Protocol.HUG)){
						
					
					SelfInfo.getInstance().setAction("请求已发出，请耐心等待对方同意喔");

					mProgressHUD = ProgressHUD.showSuccOrError(
							MainActivity.this, "请求已发出，请耐心等待对方同意喔", true);

					Message msg = actionHandler.obtainMessage();
					msg.what = 0x09;
					actionHandler.sendMessageDelayed(msg, 3000);
					String chatmsg = "我想抱抱你！";
					this.addChatMsg(true, chatmsg, date);
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}
				}
			}
		}
	}

	@Override
	public void onActionBtnLongclick(int item) {
		// 长按actionbutton回调事件
		// 菜单点击
		// single person action
		if (this.myFigureMenupage == 0)
			return;

		String mySex = SelfInfo.getInstance().getSex();
		if (showPortraitSex.equals(mySex)) {
			if (this.myFigureMenupage == 1) {
				int mListSize = myCustomList.size();
				// 我的人物菜单第二页
				if (item == 0) {

					if (mListSize == 5) {
						CustomActionTable mC = myCustomList.get(item);
						// String typeId = myCustomList.get(item).getTypeID();
						//
						// 自定义按钮

						showModifyActionDialog(mC.getContent(), mC.getTypeID());
					} else {
						// 第二页添加
						return;

					}
				} else if (item == 1) {

					if (mListSize == 5) {
						// 自定义按钮
						CustomActionTable mC = myCustomList.get(item);
						showModifyActionDialog(mC.getContent(), mC.getTypeID());
					} else {
						// 自定义按钮
						CustomActionTable mC = myCustomList.get(item - 1);
						showModifyActionDialog(mC.getContent(), mC.getTypeID());

					}

				} else if (item == 2) {

					if (mListSize == 5) {
						CustomActionTable mC = myCustomList.get(item);
						showModifyActionDialog(mC.getContent(), mC.getTypeID());

					} else if (mListSize >= item) {
						CustomActionTable mC = myCustomList.get(item - 1);
						showModifyActionDialog(mC.getContent(), mC.getTypeID());
					} else {

						return;
					}
				} else if (item == 3) {

					if (mListSize == 5) {
						CustomActionTable mC = myCustomList.get(item);
						showModifyActionDialog(mC.getContent(), mC.getTypeID());

					} else if (mListSize >= item) {
						CustomActionTable mC = myCustomList.get(item - 1);
						showModifyActionDialog(mC.getContent(), mC.getTypeID());
					} else {
						// back
						return;
					}
				} else if (item == 4) {

					if (mListSize == 5) {
						CustomActionTable mC = myCustomList.get(item);
						showModifyActionDialog(mC.getContent(), mC.getTypeID());

					} else if (mListSize >= item) {
						CustomActionTable mC = myCustomList.get(item - 1);
						showModifyActionDialog(mC.getContent(), mC.getTypeID());
					} else {
						// back

						return;
					}
				} else if (item == 5) {

					return;

				}

			}
		}

	}

	/**
	 * 请求配对 0x5c协议+服务器类型+用户包+请求配对+包体长度+请求者账号+’\0’ 配对请求
	 * 
	 * @param str
	 */
	private void processMatchRequest(String str) {
		// ReceiveMatchRequest

//		if (GlobalApplication.getInstance().isActivityrun(ConfigActivity.class))
//			return;
		String[] arr;

		arr = str.substring(Protocol.HEAD_LEN).split(" ");
		invitor = arr[0];

		ibuilder = new com.minius.ui.CustomDialog.Builder(MainActivity.this);
		ibuilder.setTitle(null);
		ibuilder.setMessage("你是否愿意和" + invitor + "在一起？");
		ibuilder.setPositiveButton("同意", matchAsk);
		ibuilder.setNegativeButton("拒绝", matchAsk);
		ibuilder.create().show();

	}

	private View.OnClickListener matchAsk = new View.OnClickListener() {
		UserPacketHandler mReq = new UserPacketHandler();

		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.confirm_btn:
				mReq.clientAcceptMatch(invitor);
				if (ibuilder.getDialog() != null)
					ibuilder.getDialog().dismiss();
				break;
			case R.id.cancel_btn:

				mReq.clientRejectMatch(invitor);
				if (ibuilder.getDialog() != null)
					ibuilder.getDialog().dismiss();
				break;
			default:
				break;
			}
		}

	};

	// 初始化纪念日
	private void initAnnLayout() {
		setTogetherDaysOnAnnLayout();
		if (AsynSocket.getInstance().isConnected())
			CalendarHandler.getInstance().getLastModifyTimeRead();
		else {
			calendarPrompt();
		}
	}

	// 设置纪念日在主界面的数字“在一起XX天”
	private void setTogetherDaysOnAnnLayout() { // 0 <= num <= 99999
		CalendarTable calendar = Database.getInstance(this).getCalendarTable(
				CalendarMainActivity.gTogetherDayId);
		if (calendar == null) {
			LayoutInflater inflater = LayoutInflater.from(this);
			RelativeLayout layout = (RelativeLayout) inflater.inflate(
					R.layout.main_calendar_together_day_none, null);
			mainToAnnLayout.removeAllViews();
			mainToAnnLayout.addView(layout);
			return;
		}
		
		int togetherDays = 0;
		if (CalendarMainActivity.isValidMemoDate(calendar.getMemoDate()))
			togetherDays = CommonFunction.calculateDay(calendar.getMemoDate());

		int daySize = 0, days = togetherDays;
		while (days > 0) {
			daySize++;
			days /= 10;
		}
		if (daySize < 1)
			daySize = 1;
		if (daySize > 5)
			daySize = 5;

		LayoutInflater inflater = LayoutInflater.from(this);

		String layoutName = "main_calendar_together_day_" + daySize + "_num";
		int layoutId = MainActivity.this.getResources().getIdentifier(
				layoutName, "layout", "com.minus.lovershouse");
		RelativeLayout layout = (RelativeLayout) inflater.inflate(layoutId,
				null);
		mainToAnnLayout.removeAllViews();
		mainToAnnLayout.addView(layout);

		for (int i = 0; i < daySize; ++i) {
			String numViewName = "main_calendar_together_day_right_" + (i + 1);
			int numViewId = MainActivity.this.getResources().getIdentifier(
					numViewName, "id", "com.minus.lovershouse");
			ImageView numView = (ImageView) layout.findViewById(numViewId);

			int num = togetherDays % 10;
			String numResName = "main_calendar_together_day_num" + num;
			int numResId = MainActivity.this.getResources().getIdentifier(
					numResName, "drawable", "com.minus.lovershouse");
			numView.setBackgroundResource(numResId);
			togetherDays /= 10;
		}
	}

	/*
	 * private void acceptMatch() { // not finish yet this.isMatch = true; if
	 * (giudeToMatchbadge.isShown()) { giudeToMatchbadge.toggle(); } //
	 * 恭喜你与%@配对成功 // String msg =
	 * "恭喜你与"+GlobalApplication.getInstance().getTiAcc()+"配对成功"; //
	 * if(mProgressHUD != null){ // mProgressHUD.dismiss(); // // // } //
	 * mProgressHUD=ProgressHUD.showSuccOrError(MainActivity.this, // msg,
	 * true); }
	 */
	private void acceptMatch() {
		ibuilder = new com.minius.ui.CustomDialog.Builder(MainActivity.this);
		ibuilder.setTitle(null);
		ibuilder.setMessage("恭喜您和"+GlobalApplication.getInstance().getTiAcc()+"喜结良缘！");
		ibuilder.setPositiveButton("开始二人世界～", null);
//		ibuilder.setNegativeButton("取消", null);
		ibuilder.create().show();
	}

	private void refuseMatch() {
		ibuilder = new com.minius.ui.CustomDialog.Builder(MainActivity.this);
		ibuilder.setTitle(null);
		ibuilder.setMessage("对不起，对方拒绝了您的邀请");
		ibuilder.setPositiveButton("再接再厉吧～", null);
//		ibuilder.setNegativeButton("取消", null);
		ibuilder.create().show();
	}
	
	public void modifyHouseStyleSucc() {
		resetViews();

	}

	public void modifyAppearanceSucc() {
		resetViews();
	}

	public void modifyLight() {
		this.initLampButton();

	}

	// -----------------------chat------- new ------
	public void setChatView() {
		// MainActivity.this.mainToChatBtn
		// .setBackgroundResource(R.drawable.newmessage_inform);
		if (!(GlobalApplication.getInstance().isChatVisible())) {
		if (this.chatnewbadge == null) {
			chatnewbadge = new BadgeView(MainActivity.this,
					MainActivity.this.mainToChatBtn);
			chatnewbadge.setText("New");
			chatnewbadge.setBadgePosition(BadgeView.POSITION_TOP_LEFT);

			chatnewbadge.setTextColor(Color.WHITE);
			chatnewbadge.setBadgeBackgroundColor(Color.RED);
			chatnewbadge.setTextSize(8);
		}
		if (!(chatnewbadge.isShown())) {
			chatnewbadge.toggle();
		}
		if (SelfInfo.getInstance().getSex().equals("b")) {
			// // boyChatbubbleflag = null;
			// girlChatbubbleflag.setVisibility(View.VISIBLE);

			addGirlChatBubbleView(GlobalApplication.getInstance().getTiStatus());

		} else {
			addBoyChatBubbleView(GlobalApplication.getInstance().getTiStatus());
		}
		}
	}

	// ------------------------album---------------
	public void setAlbumView() {
		if (!(GlobalApplication.getInstance().isAlbumVisible())) {
			if (this.albumnewbadge == null) {
				albumnewbadge = new BadgeView(MainActivity.this,
						MainActivity.this.mainToPicBtn);
//				albumnewbadge.setText("New");
				albumnewbadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
				albumnewbadge.setBadgeMargin(20);
				albumnewbadge.setBackgroundResource(R.drawable.albumnew);
//				albumnewbadge.setTextColor(Color.WHITE);
//				albumnewbadge.setBadgeBackgroundColor(Color.RED);
//				albumnewbadge.setTextSize(11);
			}
			if (!(albumnewbadge.isShown())) {
				albumnewbadge.toggle();
			}
		}

	}

	// ------------------------diary------------
	public void setDiaryView() {

		// TODO 判断是否处于日记页面
		if (!(GlobalApplication.getInstance().isDiaryVisible())) {
			if (this.diarynewbadge == null) {
				diarynewbadge = new BadgeView(MainActivity.this,
						MainActivity.this.mainToDiaryBtn);
				diarynewbadge.setText("New");
				diarynewbadge.setBadgePosition(BadgeView.POSITION_TOP_LEFT);

				diarynewbadge.setTextColor(Color.WHITE);
				diarynewbadge.setBadgeBackgroundColor(Color.RED);
				diarynewbadge.setTextSize(8);
			}
			if (!(diarynewbadge.isShown())) {
				diarynewbadge.toggle();
			}
			// this.mainToDiaryBtn.setBackgroundResource(R.drawable.newdiary_inform);

		}

	}

	public void processResponse(String str) {
		char operatorCode = 0;
		try {
			operatorCode = (char) (str.getBytes("UTF-8"))[3];
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		switch (operatorCode) {
		case Protocol.LOGIN_SUCC:
			// if (loginPg != null) {
			// loginPg.dismiss();}
			this.loginSuccess();

			finishinit();
			break;
		case Protocol.LOGIN_FAIL:
			// if (loginPg != null) {
			// loginPg.dismiss();}
			this.loginFail();
			break;

		case Protocol.ASK_FOR_MATCH:
			 if(GlobalApplication.getInstance().isMainVisible())
			processMatchRequest(str);
			break;
		 case Protocol.ACCEPT_MATCH:
			if(GlobalApplication.getInstance().isMainVisible())
				 acceptMatch();
			break;

		 case Protocol.REJECT_MATCH:
			 if(GlobalApplication.getInstance().isMainVisible())
				 refuseMatch();
			break;	
		case Protocol.RETURN_SELF_INFO:
			ReceiveSelfInfo();
			break;
		case Protocol.RETURN_COMM_INFO:
			ReceiveCommonInfo();
			break;
		case Protocol.MODIFY_HOUSE_STYLE_SUCC:
			initUI();
			break;
		case Protocol.RETURN_MATCH_INFO:
			ReceiveMatchInfo();
		default:
			break;
		}
	}

	// --Received Action Information
	public void ReceiveActionInfo(String actioninfo) {
		isRecvAction = true;
		actionMsg = actioninfo;

		handleActionMsg();

	}

	// 收到自己的全部自定义动作
	public void processAllSelfCustomAction() {
		this.myCustomList = Database.getInstance(getApplicationContext())
				.getAllAction();
		if (this.myFigureMenupage == 0) {
			// 刷新第1页菜单
			SetSelfMainActionMenu(true);
		} else {
			// 刷新第2页菜单
			RefreshCustomMenu(SelfInfo.getInstance().getSex());
		}
//		dfd
//		// 刷新第一页菜单
//		SetSelfMainActionMenu(false);
		SetActionSystemButton();
//		createSelfFigure(true);
	}

	// 自定义动作增删修改
	public void processEditCustomAction(String serverMsg) {
		char packetType = 0;
		try {
			packetType = (char) (serverMsg.getBytes("UTF-8"))[3];
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		switch (packetType) {
		case Protocol.RECV_SINGLE_CUSTOM_ACTION_ADD_SUCCESS:
			String substr = serverMsg.substring(Protocol.HEAD_LEN);
			String[] arr = substr.split(" ");
			if (arr.length >= 2) {
				CustomActionTable mCA = new CustomActionTable();
				mCA.setContent(arr[1]);
				mCA.setStatus(Protocol.SuccessFromServer + "");
				mCA.setTypeID(arr[0]);
				this.myCustomList.add(mCA);

				if (this.myFigureMenupage == 0) {
					// 刷新第1页菜单
					SetSelfMainActionMenu(true);
				} else {
					// 刷新第2页菜单
					RefreshCustomMenu(SelfInfo.getInstance().getSex());
				}
				mProgressHUD = ProgressHUD.showSuccOrError(MainActivity.this,
						"自定义动作设置成功！", true);

				Message msg = actionHandler.obtainMessage();
				msg.what = 0x09;
				actionHandler.sendMessageDelayed(msg, 3000);
				mCA = null;

			}
			break;
		case Protocol.RECV_SINGLE_CUSTOM_ACTION_ADD_FAIL:
			// 获得自己的全部自定义动作。
						ActionPacketHandler mAP = new ActionPacketHandler();
						mAP.getAllSelfCustomAction();
			mProgressHUD = ProgressHUD.showSuccOrError(MainActivity.this,
					"自定义动作设置失败！", true);
		
			Message msg = actionHandler.obtainMessage();
			msg.what = 0x09;
			actionHandler.sendMessageDelayed(msg, 3000);
			break;
		case Protocol.RECV_SINGLE_CUSTOM_ACTION_DELETE_SUCCESS:
			String deleteTypeId = serverMsg.substring(Protocol.HEAD_LEN);
			int listLen = this.myCustomList.size();
			for (int i = 0; i < listLen; i++) {
				if (myCustomList.get(i).getTypeID().equals(deleteTypeId)) {
					this.myCustomList.remove(i);
					break;
				}

			}

			if (this.myFigureMenupage == 0 || myCustomList.size() == 0) {
				// 刷新第1页菜单
				SetSelfMainActionMenu(true);
			} else if (this.myFigureMenupage == 1) {
				// 刷新第2页菜单
				RefreshCustomMenu(SelfInfo.getInstance().getSex());
			}
			mProgressHUD = ProgressHUD.showSuccOrError(MainActivity.this,
					"自定义动作删除成功！", true);

			Message deleSucc = actionHandler.obtainMessage();
			deleSucc.what = 0x09;
			actionHandler.sendMessageDelayed(deleSucc, 3000);
			break;
		case Protocol.RECV_SINGLE_CUSTOM_ACTION_DELETE_FAIL:
			mProgressHUD = ProgressHUD.showSuccOrError(MainActivity.this,
					"自定义动作删除失败！", true);

			Message deleFail = actionHandler.obtainMessage();
			deleFail.what = 0x09;
			actionHandler.sendMessageDelayed(deleFail, 3000);
			break;
		case Protocol.RECV_SINGLE_CUSTOM_ACTION_UPDATE_SUCCESS:
			String updatestr = serverMsg.substring(Protocol.HEAD_LEN);
			String[] updateArr = updatestr.split(" ");
			if (updateArr.length >= 2) {
				String typeId = updateArr[0];
				String updateContent = updateArr[1];

				int currLen = this.myCustomList.size();
				for (int i = 0; i < currLen; i++) {
					if (myCustomList.get(i).getTypeID().equals(typeId)) {
						myCustomList.get(i).setContent(updateContent);
						break;
					}

				}

				if (this.myFigureMenupage == 0 || myCustomList.size() == 0) {
					// 刷新第1页菜单
					SetSelfMainActionMenu(true);
				} else if (this.myFigureMenupage == 1) {
					// 刷新第2页菜单
					RefreshCustomMenu(SelfInfo.getInstance().getSex());
				}
				mProgressHUD = ProgressHUD.showSuccOrError(MainActivity.this,
						"自定义动作修改除成功！", true);

				Message updateSucc = actionHandler.obtainMessage();
				updateSucc.what = 0x09;
				actionHandler.sendMessageDelayed(updateSucc, 3000);
			}
			break;
		case Protocol.RECV_SINGLE_CUSTOM_ACTION_UPDATE_FAIL:
			mProgressHUD = ProgressHUD.showSuccOrError(MainActivity.this,
					"自定义动作修改失败！", true);

			Message msg2 = actionHandler.obtainMessage();
			msg2.what = 0x09;
			actionHandler.sendMessageDelayed(msg2, 3000);
			break;
		default:
			break;
		}

		// String substr = serverMsg.substring(Protocol.HEAD_LEN);
		// String[] arr = substr.split(" ");

	}

	//
	public void handleActionMsg() {
		this.isRecvAction = true;
		if (actionMsg.equals("")) {
			return;
		}
		char packetType = 0;
		try {
			packetType = (char) (actionMsg.getBytes("UTF-8"))[3];
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		String substr = actionMsg.substring(Protocol.HEAD_LEN);
		String[] arr = substr.split(" ");
		String matchname;
		SelfInfo mSelfInfo = SelfInfo.getInstance();
		if (mSelfInfo.getSmallName().length() >= 2) {

			matchname = mSelfInfo.getSmallName();
		} else {
			matchname = GlobalApplication.getInstance().getTiBigName();
		}

		if (arr.length >= 2) {

			// ---动作种类
			String actionStr = arr[1];
			String actionDate = arr[0];

			int actionType = Integer.valueOf(actionStr);

			// ---双人动作请求和双人动作拒绝弹出框
			// UIAlertView *coupleActionRequest;
			// UIAlertView *coupleActionReject;
			String curDate = AppManagerUtil.getCurDate();
			String chatmsg;
			// ---动作包类型
			switch (packetType) {
			// ---开始对方的单人动作
			case Protocol.RECV_SINGLE_CUSTOM_ACTION_BEGIN:
				// 自定义动作
				actionType += Protocol.SINGLE_ACTION_CUSTOM1 - 1;
				String isBoy = "g";
				if (mSelfInfo.getSex().equals("g")){
					isBoy = "b";
					//关闭之前的菜单栏
					if (this.myPortraitMenu.getStatus() == MenuItemView.STATUS_OPEN) {
						MyAnimations.startAnimations(MainActivity.this,
								myPortraitMenu, 300);
					}
				}else{
					if (girlMenu.getStatus() == MenuItemView.STATUS_OPEN) {
						MyAnimations.startAnimations(MainActivity.this,
								girlMenu, 300);
					}
				}
				String content = "";
				if (arr.length >= 3) {
					content = arr[2];
				}
				// chat
				chatmsg = "我目前状态是：" + content;
				addChatMsg(false, chatmsg, actionDate);
				this.CustomActionStart(false, actionType, isBoy, content);

				break;
			case Protocol.RECV_SINGLE_ACTION_BEGINE:
				// 已经在网络层更新对方的status
				// GlobalApplication.getInstance().setTiStatus(actionStr.trim());
				if (mSelfInfo.getSex().equals("g")){	
					//关闭之前的菜单栏
					if (this.myPortraitMenu.getStatus() == MenuItemView.STATUS_OPEN) {
						MyAnimations.startAnimations(MainActivity.this,
								myPortraitMenu, 300);
					}
				}else{
					if (girlMenu.getStatus() == MenuItemView.STATUS_OPEN) {
						MyAnimations.startAnimations(MainActivity.this,
								girlMenu, 300);
					}
					
				}
				this.ActionStart(actionType, true, false, actionDate);
				break;
			case Protocol.RECV_SINGLE_CUSTOM_ACTION_END:
				// 自定义动作
				String tarContent = GlobalApplication.getInstance()
						.getTarCustomActionList().get(actionStr);
				actionType += Protocol.SINGLE_ACTION_CUSTOM1 - 1;
				boolean isBoy1 = false;
				if (mSelfInfo.getSex().equals("g"))
					isBoy1 = true;
				// chat
				chatmsg = "我结束了'" + tarContent + "'状态";
				addChatMsg(false, chatmsg, actionDate);
				this.CustomActionEnd(false, actionType, isBoy1);
				break;
			// ---结束对方的单人动作
			case Protocol.RECV_SINGLE_ACTION_END:
				// 已经在网络层更新对方的status
				this.ActionEnd(actionType, true, false);

				break;
			// ---请求双人动作
			case Protocol.RECV_COUPLE_ACTION_REQUEST:
				switch (actionType) {
				case Protocol.HUG:
					chatmsg = "我想抱抱你。";
					MainActivity.this.addChatMsg(false, chatmsg, curDate);
					if (this.isMainVisiual) {
						final String chatmsg1 = SelfInfo.getInstance()
								.getNickName() + " 和 " + matchname + "深情地抱在一起。";
						actionBuilder = AppManagerUtil.openAlertDialog(
								MainActivity.this, matchname, chatmsg, "同意",
								"拒绝", new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDate();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										actionHandler.SendCoupleActionAccept(
												curDate, Protocol.HUG);
										MainActivity.this.ActionStart(
												Protocol.HUG, false, false, "");

										MainActivity.this.addChatMsg(true,
												chatmsg1, curDate);

										actionHandler = null;
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();

									}
								}, new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDate();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										actionHandler.SendCoupleActionReject(
												curDate, Protocol.HUG);
										String chatmsg = "你的表现不足以打动我";

										MainActivity.this.addChatMsg(true,
												chatmsg, curDate);
										actionHandler = null;
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();

									}
								}, false);
					} else {
						this.toDoCoupleActionMsg = actionMsg;
					}

					break;
				case Protocol.KISS:
					chatmsg = "亲爱的，亲亲我好咩。";
					MainActivity.this.addChatMsg(false, chatmsg, curDate);
					if (this.isMainVisiual) {
						final String chatmsg2 = SelfInfo.getInstance()
								.getNickName()
								+ " 和 "
								+ matchname
								+ "甜蜜地拥吻在一起。";
						 actionBuilder = AppManagerUtil.openAlertDialog(
								MainActivity.this, matchname, chatmsg, "同意",
								"拒绝", new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDate();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										actionHandler.SendCoupleActionAccept(
												curDate, Protocol.KISS);
										MainActivity.this
												.ActionStart(Protocol.KISS,
														false, false, "");

										MainActivity.this.addChatMsg(true,
												chatmsg2, curDate);

										actionHandler = null;
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();

									}
								}, new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDate();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										actionHandler.SendCoupleActionReject(
												curDate, Protocol.KISS);
										String chatmsg = "不给亲";
										MainActivity.this.addChatMsg(true,
												chatmsg, curDate);
										actionHandler = null;
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();

									}
								}, false);
					} else {
						this.toDoCoupleActionMsg = actionMsg;
					}

					break;
				case Protocol.SEX:
					chatmsg = "来啊，快活啊…反正有大把时光。";
					MainActivity.this.addChatMsg(false, chatmsg, curDate);
					if (!(isMainVisiual)) {
						this.toDoCoupleActionMsg = actionMsg;
					} else {
						
				
						actionBuilder = AppManagerUtil.openAlertDialog(
								MainActivity.this, matchname, chatmsg, "同意",
								"拒绝", new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDate();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										if(actionHandler.SendCoupleActionAccept(
												curDate, Protocol.SEX)){
										String chatmsg = "春宵一刻值千金……";
										MainActivity.this.addChatMsg(true,
												chatmsg, curDate);
										MainActivity.this.ActionStart(Protocol.SEX,
												false, false, "");
//										blackScreen();
//										long[] pattern = { 100, 400, 100, 400,
//												100, 400, 100, 400, 100, 400,
//												100, 400, 100, 400, 100, 400,
//												100, 400, 100, 400 };
//										AppManagerUtil.Vibrate(
//												MainActivity.this, pattern,
//												false);
										actionHandler = null;
										}else{
											Toast.makeText(getApplicationContext(), "发送失败",Toast.LENGTH_SHORT).show();
										}
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();

									}
								}, new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										String curDate = AppManagerUtil
												.getCurDate();
										ActionPacketHandler actionHandler = new ActionPacketHandler();
										actionHandler.SendCoupleActionReject(
												curDate, Protocol.SEX);
										String chatmsg = "色！";

										MainActivity.this.addChatMsg(true,
												chatmsg, curDate);
										actionHandler = null;
										if (actionBuilder.getDialog() != null)
											actionBuilder.getDialog().dismiss();

									}
								}, false);
					}

					break;
				case Protocol.PETTING:
					chatmsg = "摸摸头";

					MainActivity.this.addChatMsg(false, chatmsg, curDate);
					if (!(isMainVisiual)) {
						this.toDoCoupleActionMsg = actionMsg;
					} else {
						MainActivity.this.ActionStart(Protocol.PETTING, false,
								false, "");
					}
					break;
				case Protocol.PINCHEDFACE:
					chatmsg = "来，给大爷捏下脸。";
					MainActivity.this.addChatMsg(false, chatmsg, curDate);
					if (!(isMainVisiual)) {
						this.toDoCoupleActionMsg = actionMsg;
					} else {
						MainActivity.this.ActionStart(Protocol.PINCHEDFACE,
								false, false, "");
					}
					break;
				case Protocol.ABUSE:
					chatmsg = "皮痒了吧。";
					MainActivity.this.addChatMsg(false, chatmsg, curDate);
					if (!(isMainVisiual)) {
						this.toDoCoupleActionMsg = actionMsg;
					} else {
						MainActivity.this.ActionStart(Protocol.ABUSE, false,
								false, "");
					}
					break;
				default:
					break;
				}
				break;
			// ---接受双人动作
			case Protocol.RECV_COUPLE_ACTION_ACCEPT:
				if (!(isMainVisiual)) {
					this.toDoCoupleActionMsg = actionMsg;
				} else {
					MainActivity.this.ActionStart(actionType, false, false, "");
				}
				switch (actionType) {

				case Protocol.HUG:
					chatmsg = "'" + SelfInfo.getInstance().getNickName() + "'"
							+ "和" + "'" + matchname + "'" + "深情地抱在一起。";
					MainActivity.this.addChatMsg(false, chatmsg, curDate);

					// chatmsg = [NSString
					// stringWithFormat:@"'%@'和'%@'深情地抱在一起。",[SelfInfo
					// getInstance].bigname, matchname];
					// [self.selfFigure addChatMsg:NO Content:chatmsg];
					break;
				case Protocol.KISS:
					chatmsg = "'" + SelfInfo.getInstance().getNickName() + "'"
							+ "和" + "'" + matchname + "'" + "甜蜜地拥吻在一起。";
					MainActivity.this.addChatMsg(false, chatmsg, curDate);
					// chatmsg = [NSString
					// stringWithFormat:@"'%@'和'%@'甜蜜地拥吻在一起。",[SelfInfo
					// getInstance].bigname, matchname];
					// [self.selfFigure addChatMsg:NO Content:chatmsg];
					break;
				case Protocol.SEX:
					chatmsg = "春宵一刻值千金……";
					MainActivity.this.addChatMsg(false, chatmsg, curDate);

					break;
				default:
					break;
				}

				break;
			// ---拒绝双人动作
			case Protocol.RECV_COUPLE_ACTION_REJECT:
				switch (actionType) {
				case Protocol.HUG:
					chatmsg = "你的表现不足以打动我";
					actionBuilder = AppManagerUtil.openAlertDialog(
							MainActivity.this, matchname, chatmsg, "好吧，我自己抱自己",
							null, new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									if (actionBuilder.getDialog() != null)
										actionBuilder.getDialog().dismiss();

								}
							}, null, false);

					MainActivity.this.addChatMsg(false, chatmsg, curDate);

					break;
				case Protocol.KISS:
					chatmsg = "不给亲";
					actionBuilder = AppManagerUtil.openAlertDialog(
							MainActivity.this, matchname, chatmsg, "好吧,下次再亲",
							null, new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// dialog.dismiss();
									if (actionBuilder.getDialog() != null)
										actionBuilder.getDialog().dismiss();
								}
							}, null, false);

					MainActivity.this.addChatMsg(false, chatmsg, curDate);
					break;
				case Protocol.SEX:
					chatmsg = "色！";
					actionBuilder = AppManagerUtil.openAlertDialog(
							MainActivity.this, matchname, chatmsg, "好吧", null,
							new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									if (actionBuilder.getDialog() != null)
										actionBuilder.getDialog().dismiss();

								}
							}, null, false);

					MainActivity.this.addChatMsg(false, chatmsg, curDate);

					break;
				default:
					break;
				}

				break;

			default:
				break;
			}

		}
		this.isRecvAction = false;
	}

	final Runnable brightScreen = new Runnable() {
		@Override
		public void run() {
//			 Resources res = getResources();
//		        Drawable drawable = res.getDrawable(R.drawable.transcolor);
//		        MainActivity.this.getWindow().setBackgroundDrawable(drawable);
			mainBottomRL.setBackgroundColor(0x000000);
			curBoyView.setVisibility(View.VISIBLE);
			curGirlView.setVisibility(View.VISIBLE);
			curBoyView.setOnClickListener(MainActivity.this);
			curGirlView.setOnClickListener(MainActivity.this);


		}

	};

	private void blackScreen() {
//
		mainBottomRL.setBackgroundColor(0xaa000000);
//		 Resources res = getResources();
//	        Drawable drawable = res.getDrawable(R.drawable.blackcolor);
//	        this.getWindow().setBackgroundDrawable(drawable);
		actionHandler.postDelayed(brightScreen, 4500);
	}

	// 未配对的情况下弹出提示框
	private void askForMatch() {
		if(mProgressHUD == null){
		mProgressHUD = ProgressHUD.showSuccOrError(MainActivity.this,
				"请先点击菜单-TA,输入对方帐号配对喔", false);
		}else{
		if(mProgressHUD.isShowing()){
			return;
		}
		}
		Message updateSucc = actionHandler.obtainMessage();
		updateSucc.what = 0x09;
		actionHandler.sendMessageDelayed(updateSucc, 3000);
		

		showGiudeToMatch();
	}

	private void showGiudeToMatch() {
		if (giudeToMatchbadge.isShown()) {
			return;
		} else {
			TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
			anim.setInterpolator(new BounceInterpolator());
			anim.setDuration(1000);
			giudeToMatchbadge.toggle(anim, null);
		}
	}

	/**
	 * 解析配对方的天气信息
	 */
	public void startReadTarWeather(final String targetLocation) {
		new Thread() {
			public void run() {
				boolean b = false;
				b = weatherUtils.GetTarWeather(targetLocation);

				if (b)
					mHandler.sendEmptyMessage(Protocol.HANDLE_READ_TARWEATHER_SUCC);
				else {
					mHandler.sendEmptyMessage(Protocol.HANDLE_READ_TARWEATHER_FAIL);
				}
			}
		}.start();

	}

	/**
	 * 处理响应数据包
	 * 
	 * @param str
	 */
	public void processLocation(String str) {
		char operatorCode = 0;
		try {
			operatorCode = (char) (str.getBytes("UTF-8"))[3];
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		Log.v("result", "map operatorCode " + (byte) operatorCode);
		switch (operatorCode) {
		case Protocol.GET_USER_NOEXIST_RES:
			// targetCity.setText("No Match");
			break;

		case Protocol.GET_MATCH_USER_LOCATION_NOEXIST_RES:
			// targetCity.setText("Match user has no location");

			break;
		case Protocol.GET_LOCATION_SUCC_RES: // 返回配对位置
			String[] tLoc;
			tLoc = str.substring(Protocol.HEAD_LEN).split(" ");
			Log.v("result", "target location rece " + tLoc[1] + " " + tLoc[2]);

			startReadTarWeather(tLoc[2] + ",+" + tLoc[1]);
			break;

		default:
			break;

		}
	}

	public void showProgressHUD(String mess) {
		if (waitingHUD == null) {
			//waitingHUD = ProgressHUD.show(MainActivity.this, mess, true, true, null);
			waitingHUD = ProgressHUD.show(MainActivity.this, mess, true, false, null); //对话框不可取消
		} else {
			if (waitingHUD.isShowing()) {
				waitingHUD.dismiss();
			}

			waitingHUD.setMessage(mess);
			waitingHUD.show();

		}
	}

	public void showInitMainProgressHUD(String mess) {
		if (initMainwaitingHUD == null) {
			initMainwaitingHUD = ProgressHUD.show(MainActivity.this, mess, true, false, null); //对话框不可取消
		} else {
			if (initMainwaitingHUD.isShowing()) {
				initMainwaitingHUD.dismiss();
			}

			initMainwaitingHUD.setMessage(mess);
			initMainwaitingHUD.show();
			// initMainwaitingHUD
		}
	}

	// begin for calendar -----------------------------------------------------------------
	private void calendarNew() {
		if (CalendarMainActivity.isCalendarMainActivityRunning)
			return;
		
		if (this. CalendarNewbadge == null) {
			 CalendarNewbadge = new BadgeView(MainActivity.this,
					MainActivity.this.mainToAnnLayout);
			 CalendarNewbadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		
			 CalendarNewbadge.setBackgroundResource(R.drawable.albumnew);
		}
		if (!(CalendarNewbadge.isShown())) {
			CalendarNewbadge.toggle();
		}
	}
	
	private void calendarPrompt() {
		calendarPromptHusbandBirthday();
		calendarPromptWifeBirthday();
		calendarPromptTogetherDay();
		setTogetherDaysOnAnnLayout();
	}

	private static boolean isCalendarAlreadyPromptTogetherDay = false;

	private void calendarPromptTogetherDay() {
		if (isCalendarAlreadyPromptTogetherDay == true)
			return;
		else
			isCalendarAlreadyPromptTogetherDay = true;

		CalendarTable calendar = Database.getInstance(this.getApplicationContext())
				.getCalendarTable(CalendarMainActivity.gTogetherDayId);
		if (calendar == null)
			return;
		String memoDate = calendar.getMemoDate();
		if (CalendarMainActivity.isValidMemoDate(memoDate) == false)
			return;

		String[] str = memoDate.split("-");
		//int year = Integer.valueOf(str[0]);
		int month = Integer.valueOf(str[1]);
		int day = Integer.valueOf(str[2]);

		String[] str2 = AppManagerUtil.getCurDate().split("-");
		//int curYear = Integer.valueOf(str2[0]);
		int curMonth = Integer.valueOf(str2[1]);
		int curDay = Integer.valueOf(str2[2]);

		Builder builder = new com.minius.ui.CustomDialog.Builder(
				MainActivity.this);
		int dayPassed = CommonFunction.calculateDay(memoDate);
		if (dayPassed == 100 || dayPassed == 520 || dayPassed == 1314
				|| (month == curMonth && day == curDay)) {
			builder.setMessage("你们已经一起走过" + dayPassed
					+ "个日日夜夜啦。\n相恋容易，相处不易，且行且珍惜！\n祝愿你们今后也一路顺利~");
			builder.setPositiveButton("好的", null);
			builder.create().show();
		}
	}

	private static boolean isCalendarAlreadyPromptWifeBirthday = false;

	private void calendarPromptWifeBirthday() {
		if (isCalendarAlreadyPromptWifeBirthday == true)
			return;
		else
			isCalendarAlreadyPromptWifeBirthday = true;

		CalendarTable calendar = Database.getInstance(MainActivity.this)
				.getCalendarTable(CalendarMainActivity.gWifeBirthdayId);
		if (calendar == null)
			return;
		String memoDate = calendar.getMemoDate();
		if (CalendarMainActivity.isValidMemoDate(memoDate) == false)
			return;
		if (SelfInfo.getInstance().getSex().equals(SelfInfo.SEX_GIRL))
			return;

		String[] str = memoDate.split("-");
		int year = Integer.valueOf(str[0]);
		int month = Integer.valueOf(str[1]);
		int day = Integer.valueOf(str[2]);

		String[] str2 = AppManagerUtil.getCurDate().split("-");
		int curYear = Integer.valueOf(str2[0]);
		// int curMonth = Integer.valueOf(str2[1]);
		// int curDay = Integer.valueOf(str2[2]);

		String curDate = AppManagerUtil.getSimpleCurDate();
		String nextMemoday = curYear + "-" + month + "-" + day;
		int countdown = CommonFunction.calculateDay(curDate, nextMemoday);
		if (countdown < 0) {
			nextMemoday = (curYear + 1) + "-" + month + "-" + day;
			countdown = CommonFunction.calculateDay(curDate, nextMemoday);
		}

		Builder builder = new com.minius.ui.CustomDialog.Builder(
				MainActivity.this);
		String tiName = GlobalApplication.getInstance().getTiBigName();
		if (countdown == 30 || countdown == 10 || countdown == 3) {
			builder.setMessage("距离 " + tiName + " 生日还有" + countdown
					+ "天\n赶快为她准备一个惊喜吧！");
			builder.setPositiveButton("好的", null);
			builder.create().show();
		}
		if (countdown == 0) {
			// AlertDialog.Builder builder = new
			// AlertDialog.Builder(MainActivity.this);
			builder.setMessage("祝 " + tiName + " " + (curYear - year)
					+ "岁生日快乐！");
			builder.setPositiveButton("好的", null);
			builder.create().show();
		}
	}

	private static boolean isCalendarAlreadyPromptHusbandBirthday = false;

	private void calendarPromptHusbandBirthday() {
		if (isCalendarAlreadyPromptHusbandBirthday == true)
			return;
		else
			isCalendarAlreadyPromptHusbandBirthday = true;

		CalendarTable calendar = Database.getInstance(MainActivity.this)
				.getCalendarTable(CalendarMainActivity.gWifeBirthdayId);
		if (calendar == null)
			return;
		String memoDate = calendar.getMemoDate();
		if (CalendarMainActivity.isValidMemoDate(memoDate) == false)
			return;
		if (SelfInfo.getInstance().getSex().equals(SelfInfo.SEX_BOY))
			return;

		String[] str = memoDate.split("-");
		int year = Integer.valueOf(str[0]);
		int month = Integer.valueOf(str[1]);
		int day = Integer.valueOf(str[2]);

		String[] str2 = AppManagerUtil.getCurDate().split("-");
		int curYear = Integer.valueOf(str2[0]);
		// int curMonth = Integer.valueOf(str2[1]);
		// int curDay = Integer.valueOf(str2[2]);

		String curDate = AppManagerUtil.getSimpleCurDate();
		String nextMemoday = curYear + "-" + month + "-" + day;
		int countdown = CommonFunction.calculateDay(curDate, nextMemoday);
		if (countdown < 0) {
			nextMemoday = (curYear + 1) + "-" + month + "-" + day;
			countdown = CommonFunction.calculateDay(curDate, nextMemoday);
		}

		Builder builder = new com.minius.ui.CustomDialog.Builder(
				MainActivity.this);
		String tiName = GlobalApplication.getInstance().getTiBigName();
		if (countdown == 30 || countdown == 10 || countdown == 3) {
			builder.setMessage("距离 " + tiName + " 生日还有" + countdown
					+ "天\n赶快为他准备一个惊喜吧！");
			builder.setPositiveButton("好的", null);
			builder.create().show();
		}
		if (countdown == 0) {
			builder.setMessage("祝 " + tiName + " " + (curYear - year)
					+ "岁生日快乐！");
			builder.setPositiveButton("好的", null);
			builder.create().show();
		}
	}
	// end for calendar -----------------------------------------------------------------

	// -----------------------------------------------------------------------------------
	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (Protocol.ACTION_CALENDAR.equals(action)) {
				// 检查和更新纪念日“在一起XX天”
				char reason = intent.getCharExtra("PROTOCOL_FLAG", '\0');
				if (reason == Protocol.RETURN_CALENDAR_LAST_MODIFY_TIME_READ) {
					SharedPreferences preferences = getSharedPreferences(
							"LoverHouse_Calendar", Activity.MODE_PRIVATE);
					String client = preferences.getString(
							"CalendarLastModifyTime", "");
					String server = intent.getStringExtra("PROTOCOL_CONTENT");
					if (client == null || client.equals(""))
						client = "0000-00-00-00:00:00";
					if (CommonFunction.compareTime(client, server) < 0) {
						// CalendarMainActivity.gTogetherDayId 放在最后，以判断三个日子更新完毕
						CalendarHandler.getInstance().loadOneCalendar(
								CalendarMainActivity.gHusbandBirthdayId);
						CalendarHandler.getInstance().loadOneCalendar(
								CalendarMainActivity.gWifeBirthdayId);
						CalendarHandler.getInstance().loadOneCalendar(
								CalendarMainActivity.gTogetherDayId);
					} else {
						//calendarPrompt();
						Message msg = mHandler.obtainMessage();
						msg.what = CalendarMainActivity.HANDLE_CALENDAR_PROMPT;
						mHandler.sendMessage(msg);
					}
				}
				if (reason == Protocol.RETURN_CALENDAR) {
					String[] togetherDay = intent.getStringArrayExtra("PROTOCOL_CONTENT");
					if (togetherDay[0].equals(CalendarMainActivity.gTogetherDayId)) {
						// @CalendarMainActivity.gTogetherDayId 放在最后，以判断三个日子更新完毕
						//calendarPrompt();
						Message msg = mHandler.obtainMessage();
						msg.what = CalendarMainActivity.HANDLE_CALENDAR_PROMPT;
						mHandler.sendMessage(msg);
					}
				} // end of if (Protocol.ACTION_CALENDAR.equals(action))
				else if (reason == Protocol.RECEIVE_CALENDAR_REMOVED || 
						reason == Protocol.RECEIVE_CALENDAR_UPDATED || 
						reason == Protocol.RETURN_CALENDAR) {
					Message msg = mHandler.obtainMessage();
					msg.what = CalendarMainActivity.HANDLE_CALENDAR_NEW_MESSAGE;
					mHandler.sendMessage(msg);
				}
			} else if (Protocol.ACTION_USERPACKET.equals(action)) {
				// 读到用户数据包 数据,发送消息,让handler更新界面

				String data = intent.getStringExtra(Protocol.EXTRA_DATA);

				Bundle bdata = new Bundle();
				bdata.putString("data", data);
				Message msg = mHandler.obtainMessage();
				msg.setData(bdata);

				msg.what = Protocol.HANDLE_RESPON;
				mHandler.sendMessage(msg);

			} else if (Protocol.ACTION_ACTIONPACKET.equals(action)) {
				// 读到用户数据包 数据,发送消息,让handler更新界面

				String data = intent.getStringExtra(Protocol.EXTRA_DATA);

				Bundle bdata = new Bundle();
				bdata.putString("data", data);
				Message msg = mHandler.obtainMessage();
				msg.setData(bdata);
				msg.what = Protocol.HANDLE_ACTION_RESPON;
				mHandler.sendMessage(msg);
			} else if (Protocol.ACTION_ALLSELFCUSTOMACTION.equals(action)) {
				// 全部个人自定义动作信息已经获取成功 提醒
				mHandler.sendEmptyMessage(Protocol.HANDLE_ALLSELFCUSTOMACTION);
			} else if (Protocol.ACTION_EDITCUSTOMACTION.equals(action)) {
				// add custom action failed
				String data = intent.getStringExtra(Protocol.EXTRA_DATA);

				Bundle bdata = new Bundle();
				bdata.putString("data", data);
				Message msg = mHandler.obtainMessage();
				msg.setData(bdata);
				msg.what = Protocol.HANDLE_EDITCUSTOMACTION;
				mHandler.sendMessage(msg);

			} else if (Protocol.NotificationChatNewMsg.equals(action)) {
				// 收到一条更新
				mHandler.sendEmptyMessage(Protocol.HANDLE_NewChat);

			} else if (Protocol.NotificationDiaryNewMsg.equals(action)) {
				// 收到一条更新
				mHandler.sendEmptyMessage(Protocol.HANDLE_NewDiary);

			} else if (Protocol.NotificationAlbumNew.equals(action)) {
				// 收到一条更新
				mHandler.sendEmptyMessage(Protocol.HANDLE_NewAlbum);

			}

			else if (Protocol.ACTION_LOCATIONPACKET.equals(action)) {
				// String packageName = MainActivity.this.getPackageName();
				GlobalApplication mIns = GlobalApplication.getInstance();
				if (!mIns.isRunning("com.minus.map")
						&& !mIns.isRunning("com.minus.weather")) {
					String data = intent.getStringExtra(Protocol.EXTRA_DATA);

					Bundle bdata = new Bundle();
					bdata.putString("data", data);
					Message msg = mHandler.obtainMessage();
					msg.setData(bdata);

					msg.what = Protocol.HANDLE_LOCATION_RESPON;
					mHandler.sendMessage(msg);
				}

			} else if (Protocol.ACTION_USERPACKET_COMMON.equals(action)) {
				// initUI();
				
			}else if (Protocol.ACTION_ModifyLightState.equals(action)) {
				mHandler.sendEmptyMessage(Protocol.HANDLE_ModifyLamp);
			}else if(Protocol.ACTION_USERPACKET_ModifyHouseStyle.equals(action)){
				mHandler.sendEmptyMessage(Protocol.HANDLE_ModifyHouseStyle);
			}
			
			else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
					|| action.equals("android.net.wifi.WIFI_STATE_CHANGED")
					|| action.equals(Protocol.ACTION_NONETWORK)) {
				mHandler.sendEmptyMessage(Protocol.HANDLE_NONECTWORK);

			} else if (action.equals(Protocol.ACTION_CONNECTINGTOSERVER)) {
				mHandler.sendEmptyMessage(Protocol.HANDLE_CONNECTING);
			} else if (action.equals(Protocol.ACTION_DISCONNECTED)) {
				mHandler.sendEmptyMessage(Protocol.HANDLE_DISCONNECTED);
			} else if (action.equals(Protocol.ACTION_ONCONNECTED)) {
				mHandler.sendEmptyMessage(Protocol.HANDLE_ONCONNECTED);
			} else if (action.equals(Protocol.ACTION_CONNECTINGTOSERVERFAIL)) {
				mHandler.sendEmptyMessage(Protocol.HANDLE_CONNECTINGTOSERVERFAIL);
			}

		} // onReceive
	}

	private static class MyHandler extends Handler {
		WeakReference<MainActivity> mActivity;

		MyHandler(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MainActivity theActivity = mActivity.get();
			String mData = msg.getData().getString("data");
			switch (msg.what) {

			case CalendarMainActivity.HANDLE_CALENDAR_NEW_MESSAGE:
				theActivity.calendarNew();
				break;
			case CalendarMainActivity.HANDLE_CALENDAR_PROMPT:
				theActivity.calendarPrompt();
				break;
			case Protocol.HANDLE_RESPON:
				theActivity.processResponse(mData);
				break;
			case Protocol.HANDLE_ACTION_RESPON:
				theActivity.ReceiveActionInfo(mData);
				break;
			case Protocol.HANDLE_ALLSELFCUSTOMACTION:
				theActivity.processAllSelfCustomAction();
				break;
			case Protocol.HANDLE_EDITCUSTOMACTION:
				theActivity.processEditCustomAction(mData);
				break;
			case Protocol.HANDLE_LOCATION_RESPON:
				theActivity.processLocation(mData);
				break;
			case Protocol.HANDLE_READ_TARWEATHER_FAIL:

				break;
			case Protocol.HANDLE_READ_TARWEATHER_SUCC:

				theActivity.handleGetTarWeatherSucc();
				break;
			case Protocol.HANDLE_NONECTWORK:
				
				if (!(NetWorkUtil.isNetworkAvailable(theActivity
						.getApplicationContext()))) {
					if(theActivity.isMainVisiual){
						// 无网络连接
						//TODO
						//Toast.makeText(theActivity.getApplicationContext(), "无网络连接，请稍后重试", Toast.LENGTH_SHORT).show();
						theActivity.showProgressHUD("无网络连接，请稍后重试");
					}
				} else {
					if (theActivity.waitingHUD != null) {
						//theActivity.waitingHUD.dismiss();
					}
				}

				break;
//			case Protocol.HANDLE_DISCONNECTED:
////				if(theActivity.isMainVisiual){
////				if (!(SelfInfo.getInstance().isOnline())) {
////					ConnectHandler.getInstance().connectToServer();
////				}
////				}
//				break;
			case Protocol.HANDLE_ONCONNECTED:
				/*
				if (theActivity.waitingHUD != null) {

					if (theActivity.waitingHUD.isShowing()) {
						theActivity.waitingHUD.dismiss();

					}
					theActivity.waitingHUD = null;
				}*/
				
				if (SelfInfo.getInstance().isOnline() == false) {
					GlobalApplication.getInstance().setTargetDefault();
					GlobalApplication.getInstance().setCommonDefault();
					WelcomeActivity.login();
				}

//				if (!(SelfInfo.getInstance().isOnline())) {
//					// 用户不在线，重新登录
//					UserTable selfInfo = Database.getInstance(
//							theActivity.getApplicationContext()).getSelfInfo();
//
//					if (!(selfInfo == null)) {
//						SelfInfo.getInstance().setInfo(selfInfo.getAccount(),
//								selfInfo.getPassword());
//					}
//					GlobalApplication.getInstance().setTargetDefault();
//					GlobalApplication.getInstance().setCommonDefault();
//					UserPacketHandler mUserPacketHandler = new UserPacketHandler();
//					mUserPacketHandler.Login(SelfInfo.getInstance()
//							.getAccount(), SelfInfo.getInstance().getPwd());
//				}
				break;
			case Protocol.HANDLE_CONNECTING:
				if(theActivity.isMainVisiual){
				theActivity.showProgressHUD("连接服务器中");
				}
				break;
			case Protocol.HANDLE_CONNECTINGTOSERVERFAIL:
				if(theActivity.isMainVisiual){
				theActivity.showProgressHUD("哎呀，连接不了服务器了，请稍后重试");
				}
				break;

			case Protocol.HANDLE_NewChat:
				theActivity.setChatView();
				break;
			case Protocol.HANDLE_NewDiary:
				theActivity.setDiaryView();
				break;
			case Protocol.HANDLE_NewAlbum:
				theActivity.setAlbumView();
				break;
			case Protocol.HANDLE_ModifyLamp:
				theActivity.modifyLight();
				break;
			case Protocol.HANDLE_ModifyHouseStyle:
				theActivity.initHouse();
				CommonBitmap.getInstance().setAlubmPhotoInit(false);
				theActivity.initAlbumButton();
				theActivity.initLampButton();
				break;
			default:
				break;
			}
		}
	}

	public static class ActionHandler extends Handler {

		WeakReference<MainActivity> mActivity;

		ActionHandler(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MainActivity theActivity = mActivity.get();
			// String mData = msg.getData().getString("data");
			if (msg.what == 0x08) {
				// blright screen
				theActivity.mainBottomRL.setBackgroundColor(0x000000);
				// AppManagerUtil.setBrightness(theActivity, 255);
			}
			if (msg.what == 0x09) {
				if (theActivity.mProgressHUD != null) {
					theActivity.mProgressHUD.dismiss();
					theActivity.mProgressHUD = null;
				}
			}
			if (msg.what == 0x10) {
				theActivity.ActionEnd(theActivity.nowAction, true, true);
				if (theActivity.endActionDialog.isShowing())
					theActivity.endActionDialog.dismiss();
			} else if (msg.what == 0x11) {
				if (theActivity.endActionDialog.isShowing())
					theActivity.endActionDialog.dismiss();
			} else if (msg.what == 0x12) {
				// 男生眨眼睛停止
				try {
					AnimationDrawable twinkle = (AnimationDrawable) theActivity.staticBoyEye
							.getDrawable();
					twinkle.stop();
				} catch (Exception e) {

				}
				theActivity.staticBoyEye
						.setImageResource(R.drawable.static_boy_eye01);
			} else if (msg.what == 0x13) {
				// 男生招手停止
				if (theActivity.staticBoyHand == null)
					theActivity.staticBoyHand = (ImageView) theActivity
							.findViewById(R.id.staticboy_hand);
				try {
					AnimationDrawable handShake = (AnimationDrawable) theActivity.staticBoyHand
							.getDrawable();
					handShake.stop();
				} catch (Exception e) {

				}

				theActivity.staticBoyHand
						.setImageResource(R.drawable.static_boy_hand03);
			} else if (msg.what == 0x14) {
				// girl眨眼睛停止
				try {
					AnimationDrawable twinkle = (AnimationDrawable) theActivity.staticGirlEye
							.getDrawable();
					twinkle.stop();
				} catch (Exception e) {

				}

				theActivity.staticGirlEye
						.setImageResource(R.drawable.static_girl_eye01);
			} else if (msg.what == 0x15) {
				// girl招手停止
				if (theActivity.staticGirlHand == null)
					theActivity.staticGirlHand = (ImageView) theActivity
							.findViewById(R.id.staticgirl_hand);
				try {
					AnimationDrawable handShake = (AnimationDrawable) theActivity.staticGirlHand
							.getDrawable();
					handShake.stop();
				} catch (Exception e) {

				}

				// staticBoyBody.setBackgroundResource(R.drawable.boy_portrait_body);
				theActivity.staticGirlHand
						.setImageResource(R.drawable.static_girl_hand03);

			} else if (msg.what == 0x16) {
				theActivity.ActionEnd(Protocol.ABUSE, false, false);
			} else if (msg.what == 0x17) {
				theActivity.ActionEnd(Protocol.PINCHEDFACE, false, false);
			} else if (msg.what == 0x18) {

				theActivity.ActionEnd(Protocol.PETTING, false, false);
			} else if (msg.what == 0x19) {
				// kiss
				theActivity.ActionEnd(Protocol.KISS, false, false);
			} else if (msg.what == 0x20) {
				if (theActivity.initMainwaitingHUD != null)
					theActivity.initMainwaitingHUD.dismiss();
				theActivity.showLeadView();
				
			}
			else if (msg.what == 0x21) {
				if (theActivity.initMainwaitingHUD != null && theActivity.initMainwaitingHUD.isShowing()){
					theActivity.initMainwaitingHUD.dismiss();
				//Toast.makeText(theActivity.getApplicationContext(), "登陆超时",Toast.LENGTH_LONG).show();
				 //再重新登陆一次
				if(SelfInfo.getInstance().getAccount().equals("") || SelfInfo.getInstance().getAccount().equals(Protocol.DEFAULT)){
					UserTable selfInfo = Database.getInstance(
							theActivity.getApplicationContext()).getSelfInfo();
					SelfInfo.getInstance().setInfo(selfInfo.getAccount(),
							selfInfo.getPassword());
				}
				
				UserPacketHandler mUserPacketHandler = new UserPacketHandler();
				mUserPacketHandler.Login(SelfInfo.getInstance().getAccount(), SelfInfo.getInstance().getPwd());
//				LoginTask t = new LoginTask();
//				t.execute(selfInfo.getAccount(), selfInfo.getPassword());
	
				}
				
			}

		}
	}
	
	
	private void showLeadView(){

		SharedPreferences mSP = getSharedPreferences(
				Protocol.PREFERENCE_NAME, Activity.MODE_PRIVATE);
	     boolean isNeedGuide = mSP.getBoolean("isNeedGuide", true);
	     if(isNeedGuide && !(this.isMatch) ){
	    	 SharedPreferences.Editor mEditor = mSP.edit();
			 mEditor.putBoolean("isNeedGuide", false);
			 mEditor.commit();
	    		Intent startIntent = new Intent(MainActivity.this,
	    				OperateGuide.class);
			
	    		startActivity(startIntent);
//	    		overridePendingTransition(R.anim.push_bottom_in,
//						R.anim.push_top_out);
	   
	     }
	}

	private String GetCurrentTime() {
		return AppManagerUtil.getCurDate();
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(this);
			mAMapLocManager.destory();
		}
		mAMapLocManager = null;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {

//			this.aMapLocation = location;// 判断超时机制
			myLat = location.getLatitude();
			myLng = location.getLongitude();

			// new thread
			LocationHandler mReq = new LocationHandler();
			mReq.UploadPosition(SelfInfo.getInstance().getAccount(),
					(float) myLat, (float) myLng);

			stopLocation();// 销毁掉定位
		}

	}

	public void handleGetTarWeatherSucc() {
		// TODO Auto-generated method stub
		// Database.getInstance(GlobalApplication.getInstance().getApplicationContext()).updateTargetWeather(
		// SelfInfo.getInstance().getTarget(),
		// weatherUtils.weatherTable.getTargetTodayCode());
		SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(SelfInfo.getInstance().getAccount(),
						Activity.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSP.edit();
		mEditor.putString("targetTodayWeather",
				weatherUtils.weatherTable.getTargetTodayCode());
		// mEditor.putBoolean("weatherChange", true);
		mEditor.commit();
		mainToWeatherBtn.setImageDrawable(weatherUtils.ImageViewChange(
				weatherUtils.weatherTable.getTargetTodayCode(), "main"));
		// setGlassBackground();
	}

	public void InitSingleActionStart(int actionType, boolean isBoy,
			boolean isSelf) {
		if (isSelf) {
			nowAction = actionType;
		}
		switch (actionType) {
		case Protocol.SINGLE_ACTION_MISS:
			if (isBoy) {
				boyView.setClickable(false);
				boyView.setLongClickable(false);
				boyView.setVisibility(View.GONE);
				boyStandView.removeAllViews();
				boyStandView.setVisibility(View.VISIBLE);

				if (missBoyView == null || missBoyView.get() == null) {
					missBoyView= new SoftReference<View>(getLayoutInflater().inflate(
							R.layout.main_boymiss, null));
				
					missBoyBody = (ImageView)missBoyView.get()
							.findViewById(R.id.missing_boy_body);		
				} else {
					ViewGroup oldParent = (ViewGroup) missBoyView.get().getParent();
					if (oldParent != null) {
						oldParent.removeView(missBoyView.get());
					}
				}
				
				// 初始化形象
				WeakReference<LayerDrawable> missLD = new WeakReference<LayerDrawable>(InitFigureAppDrawable.getInstance()
						.initBoyMiss(getApplicationContext()));
				missBoyBody.setImageDrawable(missLD.get());
				missLD = null;
				   this.curBoyView = missBoyView.get();
					missBoyView.get().setOnClickListener(MainActivity.this);
				boyStandView.addView(missBoyView.get());
				final ImageView missBoyHeart = (ImageView) missBoyView.get()
						.findViewById(R.id.missing_heart);
				final ImageView missBoyEye = (ImageView) missBoyView.get().
						findViewById(R.id.boy_miss_eye);
				missBoyHeart.post(new Runnable() {

					@Override
					public void run() {
						AnimationDrawable heart = (AnimationDrawable) missBoyHeart
								.getDrawable();
						if (heart.isRunning()) {
							heart.stop();
						}
						heart.start();
						
						((AnimationDrawable)missBoyEye.getDrawable()).start();
					}

				});

			} else {
				girlView.setClickable(false);
				girlView.setLongClickable(false);
				girlView.setVisibility(View.GONE);

				girlStandView.removeAllViews();
				girlStandView.setVisibility(View.VISIBLE);

				if (missGirlView == null) {
					missGirlView = getLayoutInflater().inflate(
							R.layout.main_missinggirl, null);
					
					missGirlBody = (ImageView) missGirlView
							.findViewById(R.id.missing_girl_body);
					
				} else {
					ViewGroup oldParent = (ViewGroup) missGirlView.getParent();
					if (oldParent != null) {
						oldParent.removeView(missGirlView);
					}
				}
				// 初始化形象
				WeakReference<LayerDrawable> missLD =new WeakReference<LayerDrawable>(InitFigureAppDrawable.getInstance()
						.initGirlMiss(getApplicationContext()));
				missGirlBody.setImageDrawable(missLD.get());

				missLD = null;
				 this.curGirlView = missGirlView;
				girlStandView.addView(missGirlView);
				missGirlView.setOnClickListener(MainActivity.this);
				final ImageView missGirlHeart = (ImageView) missGirlView
						.findViewById(R.id.missing_heart);
				final ImageView missGirlEye = (ImageView) missGirlView
						.findViewById(R.id.missing_girl_eye);
				missGirlHeart.post(new Runnable() {

					@Override
					public void run() {
						AnimationDrawable heart = (AnimationDrawable) missGirlHeart
								.getDrawable();
						heart.stop();
						heart.start();						
						((AnimationDrawable) missGirlEye
						.getDrawable()).start();
					}
				});
			}

			break;
		case Protocol.SINGLE_ACTION_ANGRY:
			if (isBoy) {
				boyStandView.removeAllViews();
				boyView.setClickable(false);
				boyView.setLongClickable(false);
				boyView.setVisibility(View.GONE);

				if (angryBoyView == null) {
					angryBoyView =new SoftReference<View>(getLayoutInflater().inflate(
							R.layout.main_angryboy, null));
					
					angryBoyBody = (ImageView) angryBoyView.get()
							.findViewById(R.id.angry_boy_body);
					
					
				} else {
					ViewGroup oldParent1 = (ViewGroup) angryBoyView.get().getParent();
					if (oldParent1 != null) {
						oldParent1.removeView(angryBoyView.get());
					}
				}
				// 初始化形象
				WeakReference<LayerDrawable> angryBoyLD =new WeakReference<LayerDrawable>(InitFigureAppDrawable
						.getInstance().initBoyAngry(getApplicationContext()));
				angryBoyBody.setImageDrawable(angryBoyLD.get());
				angryBoyLD = null;
				
				angryBoyView.get().setOnClickListener(MainActivity.this);
				this.curBoyView = null;
				 this.curBoyView = angryBoyView.get();
				boyStandView.addView(angryBoyView.get());
				ImageView angryBoyFoot = (ImageView) angryBoyView.get()
						.findViewById(R.id.Angryboy_foot);
				AnimationDrawable bfoot = (AnimationDrawable) angryBoyFoot
						.getDrawable();
				bfoot.start();
			} else {
				girlStandView.removeAllViews();
				girlView.setClickable(false);
				girlView.setLongClickable(false);
				girlView.setVisibility(View.GONE);
				if (angryGirlView == null) {
					angryGirlView = getLayoutInflater().inflate(
							R.layout.main_angrygirl, null);
				
					angryGirlBody = (ImageView) angryGirlView
							.findViewById(R.id.angry_girl_body);
					
					
				} else {
					ViewGroup angryGirloldParent = (ViewGroup) angryGirlView
							.getParent();
					if (angryGirloldParent != null) {
						angryGirloldParent.removeView(angryGirlView);
					}
				}
				// 初始化形象
				WeakReference<LayerDrawable> angryLD = new WeakReference<LayerDrawable>(InitFigureAppDrawable.getInstance()
						.initGirlAngry(getApplicationContext()));
				angryGirlBody.setImageDrawable(angryLD.get());
				angryLD = null;
				
				
				angryGirlView.setOnClickListener(MainActivity.this);
				 this.curGirlView = angryGirlView;
				girlStandView.addView(angryGirlView);
				ImageView angryGirlFoot = (ImageView) angryGirlView
						.findViewById(R.id.Angrygirl_foot);
				ImageView angryGirlFire = (ImageView) angryGirlView
						.findViewById(R.id.Angrygirl_fire);
				AnimationDrawable foot = (AnimationDrawable) angryGirlFoot
						.getDrawable();
				AnimationDrawable fire = (AnimationDrawable) angryGirlFire
						.getDrawable();
				foot.start();
				fire.start();
			}

			break;
		case Protocol.SINGLE_ACTION_EAT:
			if (isBoy) {
				boyView.setClickable(false);
				boyView.setLongClickable(false);
				boyView.setVisibility(View.GONE);
				boySitView.removeAllViews();

				if (eatBoyView == null) {
					eatBoyView = getLayoutInflater().inflate(
							R.layout.main_boyeat, null);
					
					eatBoyBody = (ImageView) eatBoyView
							.findViewById(R.id.EatBoy_body);
				
					
				} else {
					ViewGroup eatBoyoldParent = (ViewGroup) eatBoyView
							.getParent();
					if (eatBoyoldParent != null) {
						eatBoyoldParent.removeView(eatBoyView);
					}
				}
				// 初始化形象
				WeakReference<LayerDrawable> eatBoyLD = new WeakReference<LayerDrawable>(InitFigureAppDrawable
						.getInstance().initBoyEatting(
								getApplicationContext()));
				eatBoyBody.setImageDrawable(eatBoyLD.get());
				eatBoyLD = null;
				
				 this.curBoyView = eatBoyView;
				 eatBoyView.setOnClickListener(MainActivity.this);
				boySitView.addView(eatBoyView);

				ImageView eatBoyEye = (ImageView) eatBoyView
						.findViewById(R.id.EatBoy_eye);
				ImageView eatBoyMouth = (ImageView) eatBoyView
						.findViewById(R.id.EatBoy_mouth);
				ImageView eatBoyHand = (ImageView) eatBoyView
						.findViewById(R.id.EatBoy_leftHand);
				AnimationDrawable eatBoyeye = (AnimationDrawable) eatBoyEye
						.getDrawable();
				AnimationDrawable eatBoymouth = (AnimationDrawable) eatBoyMouth
						.getDrawable();
				AnimationDrawable eatBoyhand = (AnimationDrawable) eatBoyHand
						.getDrawable();
				eatBoyeye.stop();
				eatBoymouth.stop();
				eatBoyhand.stop();
				eatBoyeye.start();
				eatBoymouth.start();
				eatBoyhand.start();
			} else {
				girlView.setClickable(false);
				girlView.setLongClickable(false);
				girlView.setVisibility(View.GONE);

				girlSitView.removeAllViews();

				if (eatGirlView == null) {
					eatGirlView = getLayoutInflater().inflate(
							R.layout.main_girleat, null);
					
					eatGirlBody = (ImageView) eatGirlView
							.findViewById(R.id.EatGirl_body);
					
					

				} else {
					ViewGroup eatGirlViewoldParent = (ViewGroup) eatGirlView
							.getParent();
					if (eatGirlViewoldParent != null) {
						eatGirlViewoldParent.removeView(eatGirlView);
					}
				}
				
				// 初始化形象
				WeakReference<LayerDrawable> eatGirlLD =new WeakReference<LayerDrawable>( InitFigureAppDrawable
						.getInstance().initGirlEatting(
								getApplicationContext()));
				eatGirlBody.setImageDrawable(eatGirlLD.get());
				eatGirlLD = null;
				eatGirlView.setOnClickListener(MainActivity.this);
				 this.curGirlView = eatGirlView;
				girlSitView.addView(eatGirlView);

				ImageView eatGirlEye = (ImageView) eatGirlView
						.findViewById(R.id.EatGirl_eye);
				ImageView eatGirlMouth = (ImageView) eatGirlView
						.findViewById(R.id.EatGirl_mouth);
				ImageView eatGirlHand = (ImageView) eatGirlView
						.findViewById(R.id.EatGirl_leftHand);
				AnimationDrawable eyeAD = (AnimationDrawable) eatGirlEye
						.getDrawable();
				AnimationDrawable mouthAD = (AnimationDrawable) eatGirlMouth
						.getDrawable();
				AnimationDrawable handAD = (AnimationDrawable) eatGirlHand
						.getDrawable();
				eyeAD.stop();
				mouthAD.stop();
				handAD.stop();
				eyeAD.start();
				mouthAD.start();
				handAD.start();
			}

			break;
		case Protocol.SINGLE_ACTION_LEARN:
			if (isBoy) {
				boyView.setClickable(false);
				boyView.setLongClickable(false);
				boyView.setVisibility(View.GONE);
				boySitView.removeAllViews();

				if (readBoyView == null) {
					readBoyView = getLayoutInflater().inflate(
							R.layout.main_boyread, null);
				
					readBoyBody = (ImageView) readBoyView
							.findViewById(R.id.ReadBoy_body);
					
					
				} else {
					ViewGroup readBoyViewoldParent = (ViewGroup) readBoyView
							.getParent();
					if (readBoyViewoldParent != null) {
						readBoyViewoldParent.removeView(readBoyView);
					}
				}
				// 初始化形象
				WeakReference<LayerDrawable> readBoyViewLD = new WeakReference<LayerDrawable>(InitFigureAppDrawable
						.getInstance()
						.initBoyStudy(getApplicationContext()));
				readBoyBody.setImageDrawable(readBoyViewLD.get());
				readBoyViewLD = null;
				
				readBoyView.setOnClickListener(MainActivity.this);
				this.curBoyView  = null;
				 this.curBoyView =readBoyView;
				boySitView.addView(readBoyView);
				ImageView readBoyEye = (ImageView) readBoyView
						.findViewById(R.id.ReadBoy_eye);
				ImageView readBoyBook = (ImageView) readBoyView
						.findViewById(R.id.ReadBoy_Book);
				AnimationDrawable readBoyeyeAD = (AnimationDrawable) readBoyEye
						.getDrawable();
				AnimationDrawable readBoybookAD = (AnimationDrawable) readBoyBook
						.getDrawable();
				readBoyeyeAD.start();
				readBoybookAD.start();
				
				readBoyEye = null;
				readBoyBook = null;
				readBoyeyeAD = null;
				readBoybookAD= null;
			} else {
				girlView.setClickable(false);
				girlView.setLongClickable(false);
				girlView.setVisibility(View.GONE);

				girlSitView.removeAllViews();

				if (readGirlView == null) {
					readGirlView = getLayoutInflater().inflate(
							R.layout.main_girlread, null);
				
					readGirlBody = (ImageView) readGirlView
							.findViewById(R.id.ReadGirl_body);
					
					
				} else {
					ViewGroup readGirlViewoldParent = (ViewGroup) readGirlView
							.getParent();
					if (readGirlViewoldParent != null) {
						readGirlViewoldParent.removeView(readGirlView);
					}
				}
				// 初始化形象
				WeakReference<LayerDrawable> readBm =new WeakReference<LayerDrawable>(InitFigureAppDrawable.getInstance()
						.initGirlStudy(getApplicationContext()));
				readGirlBody.setImageDrawable(readBm.get());
				readBm = null;
				readGirlView.setOnClickListener(MainActivity.this);
				 this.curGirlView = readGirlView;
				girlSitView.addView(readGirlView);
				ImageView readGirlEye = (ImageView) readGirlView
						.findViewById(R.id.ReadGirl_eye);
				ImageView readGirlHand = (ImageView) readGirlView
						.findViewById(R.id.ReadGirl_hand);
				AnimationDrawable eye = (AnimationDrawable) readGirlEye
						.getDrawable();
				AnimationDrawable hand = (AnimationDrawable) readGirlHand
						.getDrawable();
				eye.start();
				hand.start();
			}
			break;

		case Protocol.SINGLE_ACTION_SLEEP:
			if (isBoy) {
				boyView.setClickable(false);
				boyView.setLongClickable(false);
				boyView.setVisibility(View.GONE);
				boysleepBubbleView.setVisibility(View.VISIBLE);
				if(isSelf){
				this.sleepRL.setOnClickListener(MainActivity.this);
				}
				 this.curBoyView =boysleepBubbleView;
				
				AnimationDrawable boysleepBubble = (AnimationDrawable) boysleepBubbleView
						.getDrawable();
				boysleepBubble.start();

			} else {
				girlView.setClickable(false);
				girlView.setLongClickable(false);
				girlView.setVisibility(View.GONE);
				girlsleepBubbleView.setVisibility(View.VISIBLE);
				 this.curGirlView =girlsleepBubbleView;
				if(isSelf){
					this.sleepRL.setOnClickListener(MainActivity.this);
					}
				AnimationDrawable girlsleepBubble = (AnimationDrawable) girlsleepBubbleView
						.getDrawable();
				girlsleepBubble.start();
			}
			break;
		case Protocol.SINGLE_ACTION_CUSTOM1:

		case Protocol.SINGLE_ACTION_CUSTOM2:

		case Protocol.SINGLE_ACTION_CUSTOM3:

		case Protocol.SINGLE_ACTION_CUSTOM4:

		case Protocol.SINGLE_ACTION_CUSTOM5:
			int customTypeId = actionType - Protocol.SINGLE_ACTION_CUSTOM1 + 1;
			String tempCustomActionContent = "";
			if (isSelf) {
				for (CustomActionTable mCA : this.myCustomList) {
					if (mCA.getTypeID().equals(customTypeId + "")) {
						nowCustomActionContent = mCA.getContent();
						tempCustomActionContent = nowCustomActionContent;
						break;
					}
				}
			} else {
				tempCustomActionContent = GlobalApplication.getInstance()
						.getTarCustomActionList().get(customTypeId + "");
			}

			if (isBoy) {
				boyView.setClickable(false);
				boyView.setLongClickable(false);
				boyView.setVisibility(View.GONE);
			
				 
				 if(boycustombubble.getAnimation() != null){
						boycustombubble.getAnimation().reset();
						boycustombubble.getAnimation().start();
					}else{
				//摇摆
					TranslateAnimation translateAnimation = new TranslateAnimation(0f, 20f, 0, 0);
					translateAnimation.setDuration(1000);
					translateAnimation.setRepeatCount(Animation.INFINITE);
					translateAnimation.setRepeatMode(Animation.REVERSE);
					boycustombubble.setAnimation(translateAnimation); //这里iv就是我们要执行动画的item，例如一个imageView
					translateAnimation.start();
					}
					boycustombubbleBtn.setClickable(true);
					boycustombubbleBtn.setOnClickListener(this);
					boycustombubble.setText(tempCustomActionContent);
					boycustombubbleRL.setVisibility(View.VISIBLE);
					 this.curBoyView =boycustombubbleRL;

			} else {
				girlView.setClickable(false);
				girlView.setLongClickable(false);
				girlView.setVisibility(View.GONE);
				
				 if(girlcustombubble.getAnimation() != null){
						girlcustombubble.getAnimation().reset();
						girlcustombubble.getAnimation().start();
					}else{
				//摇摆
					TranslateAnimation translateAnimation = new TranslateAnimation(0f, 20f, 0, 0);
					translateAnimation.setDuration(1000);
					translateAnimation.setRepeatCount(Animation.INFINITE);
					translateAnimation.setRepeatMode(Animation.REVERSE);
					girlcustombubble.setAnimation(translateAnimation); //这里iv就是我们要执行动画的item，例如一个imageView
					translateAnimation.start();
					}
				 girlcustombubbleBtn.setClickable(true);
	             girlcustombubbleBtn.setOnClickListener(MainActivity.this);

					girlcustombubble.setText(tempCustomActionContent);
					girlcustombubbleRL.setVisibility(View.VISIBLE);
					 this.curGirlView =girlcustombubbleRL;
			}
			break;
		default:
			break;
		}
	}

	public void ActionStart(int actionType, boolean isSingleAction,
			boolean isSelf, String date) {
		
		hideAllMenu();
		// 聊天提醒bubble
		if(boyChatbubbleflag != null){
		boyChatbubbleflag.setVisibility(View.GONE);
		boyChatbubbleflag.setOnClickListener(null);
		}
		// 聊天提醒bubble
		if(girlChatbubbleflag != null){
		girlChatbubbleflag.setVisibility(View.GONE);
		girlChatbubbleflag.setOnClickListener(null);
		}
		String curDate = "";
		if (!(isSelf) && date != "") {
			curDate = date;
		} else {
			curDate = AppManagerUtil.getCurDate();
		}

		if (isSingleAction) {
//			if (isSelf) {
//				nowAction = actionType;
//
//				SelfInfo.getInstance().setStatus(nowAction + "");
//
//			}
			switch (actionType) {
			case Protocol.SINGLE_ACTION_CUSTOM1:

				break;
			case Protocol.SINGLE_ACTION_CUSTOM2:

				break;
			case Protocol.SINGLE_ACTION_CUSTOM3:

				break;
			case Protocol.SINGLE_ACTION_CUSTOM4:

				break;
			case Protocol.SINGLE_ACTION_CUSTOM5:

				break;
			case Protocol.SINGLE_ACTION_MISS:
				if (SelfInfo.getInstance().getSex().equals("b")) {
					if (isSelf) {
						ActionPacketHandler actionReq = new ActionPacketHandler();
						if(actionReq.SendSingleActionBegin(curDate, actionType)){
							nowAction = actionType;

							SelfInfo.getInstance().setStatus(nowAction + "");
						// chat
						String chatmsg = "想你了(>﹏<)！";
						addChatMsg(isSelf, chatmsg, curDate);

						boyView.setVisibility(View.GONE);
						boyView.setOnClickListener(null);
						boyView.setClickable(false);

						boyStandView.removeAllViews();
						boyStandView.setVisibility(View.VISIBLE);

						if (missBoyView == null || missBoyView.get() == null) {
							 missBoyView = new SoftReference<View>(getLayoutInflater().inflate(
									R.layout.main_boymiss, null));
						
							missBoyBody = (ImageView) missBoyView.get()
									.findViewById(R.id.missing_boy_body);
						} else {
							ViewGroup oldParent = (ViewGroup) missBoyView.get()
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(missBoyView.get());
							}
						}
						missBoyView.get().setOnClickListener(MainActivity.this);
						// 初始化形象
				         WeakReference<LayerDrawable> missBm = 
						   new WeakReference<LayerDrawable>(InitFigureAppDrawable
									.getInstance().initBoyMiss(
											getApplicationContext()));
						
						missBoyBody.setImageDrawable(missBm.get());
						missBm = null;

						boyStandView.addView(missBoyView.get());
						
						curBoyView = null;
						curBoyView = missBoyView.get();

						//TODO
						final ImageView missBoyHeart = (ImageView) missBoyView.get()
								.findViewById(R.id.missing_heart);
						final ImageView missBoyEye = (ImageView) missBoyView.get()
								.findViewById(R.id.boy_miss_eye);
						missBoyHeart.post(new Runnable() {

							@Override
							public void run() {
								AnimationDrawable heart = (AnimationDrawable) missBoyHeart
										.getDrawable();
								heart.start();
								((AnimationDrawable)missBoyEye.getDrawable()).start();
								
							}

						});
						}else{
							Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
							.show();
						}

					} else {

						// chat
						String chatmsg = "想你了(>﹏<)！";
						addChatMsg(isSelf, chatmsg, curDate);

						girlView.setVisibility(View.GONE);
						girlView.setOnClickListener(null);
						girlView.setClickable(false);

						girlStandView.removeAllViews();
						girlStandView.setVisibility(View.VISIBLE);

						if (missGirlView == null) {
							missGirlView = getLayoutInflater().inflate(
									R.layout.main_missinggirl, null);
						
							missGirlBody = (ImageView) missGirlView
									.findViewById(R.id.missing_girl_body);
						} else {
							ViewGroup oldParent = (ViewGroup) missGirlView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(missGirlView);
							}
						}
						missGirlView.setOnClickListener(MainActivity.this);
						// 初始化形象
						  WeakReference<LayerDrawable> missBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initGirlMiss(
													getApplicationContext()));
						
						missGirlBody.setImageDrawable(missBm.get());
						missBm = null;

						girlStandView.addView(missGirlView);
						curGirlView = missGirlView;

						final ImageView missGirlHeart = (ImageView) missGirlView
								.findViewById(R.id.missing_heart);
						final ImageView missGirlEye = (ImageView) missGirlView
								.findViewById(R.id.missing_girl_eye);
						missGirlHeart.post(new Runnable() {

							@Override
							public void run() {
								AnimationDrawable heart = (AnimationDrawable) missGirlHeart
										.getDrawable();

								heart.start();
								
								((AnimationDrawable)missGirlEye.getDrawable()).start();
								
								
								
							}

						});

					}
				} else {// girl
					if (isSelf) {

						ActionPacketHandler actionReq = new ActionPacketHandler();
						if(actionReq.SendSingleActionBegin(curDate, actionType)){
							
							nowAction = actionType;

							SelfInfo.getInstance().setStatus(nowAction + "");
						// chat
						String chatmsg = "想你了(>﹏<)！";
						addChatMsg(isSelf, chatmsg, curDate);

						girlView.setVisibility(View.GONE);
						girlView.setOnClickListener(null);
						girlView.setClickable(false);

						girlStandView.removeAllViews();
						girlStandView.setVisibility(View.VISIBLE);

						if (missGirlView == null) {
							missGirlView = getLayoutInflater().inflate(
									R.layout.main_missinggirl, null);
							
							missGirlBody = (ImageView) missGirlView
									.findViewById(R.id.missing_girl_body);
						} else {
							ViewGroup oldParent = (ViewGroup) missGirlView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(missGirlView);
							}
						}
						missGirlView.setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> missBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initGirlMiss(
													getApplicationContext()));
						
						missGirlBody.setImageDrawable(missBm.get());
						missBm = null;
						
						curGirlView = missGirlView;
						
						girlStandView.addView(missGirlView);

						final ImageView missGirlHeart = (ImageView) missGirlView
								.findViewById(R.id.missing_heart);
						final ImageView missGirlEye = (ImageView) missGirlView
								.findViewById(R.id.missing_girl_eye);
						missGirlHeart.post(new Runnable() {

							@Override
							public void run() {
								AnimationDrawable heart = (AnimationDrawable) missGirlHeart
										.getDrawable();

								heart.start();
								
								((AnimationDrawable)missGirlEye.getDrawable()).start();
								
								
								
							}

						});
						}else{
							Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
							.show();
						}
					} else {

						// chat
						String chatmsg = "想你了(>﹏<)！";
						addChatMsg(isSelf, chatmsg, curDate);

						boyView.setVisibility(View.GONE);
						boyView.setOnClickListener(null);
						boyView.setClickable(false);

						boyStandView.removeAllViews();
						boyStandView.setVisibility(View.VISIBLE);
						if (missBoyView == null || missBoyView.get() == null) {
							missBoyView = new SoftReference<View>(getLayoutInflater().inflate(
									R.layout.main_boymiss, null));
						
							missBoyBody =(ImageView) missBoyView.get()
									.findViewById(R.id.missing_boy_body);
						} else {
							ViewGroup oldParent = (ViewGroup) missBoyView.get()
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(missBoyView.get());
							}
						}
						missBoyView.get().setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> missBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initBoyMiss(
													getApplicationContext()));
						
						missBoyBody.setImageDrawable(missBm.get());
						missBm = null;
						curBoyView = missBoyView.get();
						boyStandView.addView(missBoyView.get());

						final ImageView missBoyHeart = (ImageView) missBoyView.get()
								.findViewById(R.id.missing_heart);
						final ImageView missBoyEye = (ImageView) missBoyView.get()
								.findViewById(R.id.boy_miss_eye);
						missBoyHeart.post(new Runnable() {

							@Override
							public void run() {
								AnimationDrawable heart = (AnimationDrawable) missBoyHeart
										.getDrawable();

								heart.start();
								
								((AnimationDrawable)missBoyEye.getDrawable()).start();
							}

						});

					}
				}
				break;
			case Protocol.SINGLE_ACTION_ANGRY:

				if ((SelfInfo.getInstance().getSex().equals("b"))) {
					if (isSelf) {

						ActionPacketHandler actionReq = new ActionPacketHandler();
						if(actionReq.SendSingleActionBegin(curDate, actionType)){
							nowAction = actionType;

							SelfInfo.getInstance().setStatus(nowAction + "");
							
						// chat
						String chatmsg = "哼！我生气了！！";
						addChatMsg(isSelf, chatmsg, curDate);

						boyStandView.removeAllViews();

						if (angryBoyView == null || angryBoyView.get() == null) {
							angryBoyView = new SoftReference<View> (getLayoutInflater().inflate(
									R.layout.main_angryboy, null));
						
							angryBoyBody = (ImageView) angryBoyView.get()
									.findViewById(R.id.angry_boy_body);

						} else {
							ViewGroup oldParent = (ViewGroup) angryBoyView.get()
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(angryBoyView.get());
							}
						}
						angryBoyView.get().setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> angryBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initBoyAngry(
													getApplicationContext()));
					
						angryBoyBody.setImageDrawable(angryBm.get());
						angryBm = null;
						curBoyView = null;
						curBoyView = angryBoyView.get();
						boyStandView.addView(angryBoyView.get());

						boyView.setVisibility(View.GONE);
						boyView.setOnClickListener(null);
						boyView.setClickable(false);

						ImageView angryBoyFoot = (ImageView) angryBoyView.get()
								.findViewById(R.id.Angryboy_foot);
						AnimationDrawable foot = (AnimationDrawable) angryBoyFoot
								.getDrawable();

						foot.start();
						angryBoyFoot = null;
						
						}else{
							Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
							.show();
						}

					} else {
						// ---发送相应的聊天信息--
						// chat

						String chatmsg = "哼！我生气了！！";
						addChatMsg(isSelf, chatmsg, curDate);

						girlStandView.removeAllViews();

						if (angryGirlView == null) {
							angryGirlView = getLayoutInflater().inflate(
									R.layout.main_angrygirl, null);
							
							angryGirlBody = (ImageView) angryGirlView
									.findViewById(R.id.angry_girl_body);
						} else {
							ViewGroup oldParent = (ViewGroup) angryGirlView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(angryGirlView);
							}
						}
						angryGirlView.setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> angryBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initGirlAngry(
													getApplicationContext()));
						
						angryGirlBody.setImageDrawable(angryBm.get());
						angryBm = null;

						curGirlView = angryGirlView;
						girlStandView.addView(angryGirlView);

						girlView.setVisibility(View.GONE);
						girlView.setOnClickListener(null);
						girlView.setClickable(false);

						ImageView angryGirlFoot = (ImageView) angryGirlView
								.findViewById(R.id.Angrygirl_foot);
						ImageView angryGirlFire = (ImageView) angryGirlView
								.findViewById(R.id.Angrygirl_fire);
						AnimationDrawable foot = (AnimationDrawable) angryGirlFoot
								.getDrawable();
						AnimationDrawable fire = (AnimationDrawable) angryGirlFire
								.getDrawable();
						foot.start();
						fire.start();
					}
				} else {
					if (isSelf) {
						// 女生自己生气

						ActionPacketHandler actionReq = new ActionPacketHandler();
						if(actionReq.SendSingleActionBegin(curDate, actionType)){
							nowAction = actionType;

							SelfInfo.getInstance().setStatus(nowAction + "");
						// chat
						String chatmsg = "哼！我生气了！！";
						addChatMsg(isSelf, chatmsg, curDate);

						girlStandView.removeAllViews();

						if (angryGirlView == null) {
							angryGirlView = getLayoutInflater().inflate(
									R.layout.main_angrygirl, null);
							
							angryGirlBody = (ImageView) angryGirlView
									.findViewById(R.id.angry_girl_body);
						} else {
							ViewGroup oldParent = (ViewGroup) angryGirlView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(angryGirlView);
							}
						}
						angryGirlView.setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> angryBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initGirlAngry(
													getApplicationContext()));
						angryGirlBody.setImageDrawable(angryBm.get());
						angryBm = null;

						curGirlView = angryGirlView;
						girlStandView.addView(angryGirlView);

						girlView.setVisibility(View.GONE);
						girlView.setOnClickListener(null);
						girlView.setClickable(false);

						ImageView angryGirlFoot = (ImageView) angryGirlView
								.findViewById(R.id.Angrygirl_foot);
						ImageView angryGirlFire = (ImageView) angryGirlView
								.findViewById(R.id.Angrygirl_fire);
						AnimationDrawable foot = (AnimationDrawable) angryGirlFoot
								.getDrawable();
						AnimationDrawable fire = (AnimationDrawable) angryGirlFire
								.getDrawable();
						foot.start();
						fire.start();
						}else{
							Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
							.show();
						}
					} else {
						// ---发送相应的聊天信息--
						// chat

						String chatmsg = "哼！我生气了！！";
						addChatMsg(isSelf, chatmsg, curDate);

						boyStandView.removeAllViews();

						if (angryBoyView == null || angryBoyView.get() == null) {
							angryBoyView =new SoftReference<View>(getLayoutInflater().inflate(
									R.layout.main_angryboy, null));
						
							angryBoyBody = (ImageView) angryBoyView.get()
									.findViewById(R.id.angry_boy_body);

						} else {
							ViewGroup oldParent = (ViewGroup) angryBoyView.get()
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(angryBoyView.get());
							}
						}
						angryBoyView.get().setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> angryBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initBoyAngry(
													getApplicationContext()));
					
						angryBoyBody.setImageDrawable(angryBm.get());
						angryBm = null;
						curBoyView = null;
						curBoyView = angryBoyView.get();
						boyStandView.addView(angryBoyView.get());

						boyView.setVisibility(View.GONE);
						boyView.setOnClickListener(null);
						boyView.setClickable(false);

						ImageView angryBoyFoot = (ImageView) angryBoyView.get()
								.findViewById(R.id.Angryboy_foot);
						AnimationDrawable foot = (AnimationDrawable) angryBoyFoot
								.getDrawable();

						foot.start();
					}
				}
				break;
			case Protocol.SINGLE_ACTION_EAT:
				if (SelfInfo.getInstance().getSex().equals("b")) {
					if (isSelf) {

						ActionPacketHandler actionReq = new ActionPacketHandler();
						if(actionReq.SendSingleActionBegin(curDate, actionType)){
							nowAction = actionType;

							SelfInfo.getInstance().setStatus(nowAction + "");
						// chat
						String chatmsg = "我开始吃饭啦！";
						addChatMsg(isSelf, chatmsg, curDate);

						boyView.setVisibility(View.GONE);
						boyView.setOnClickListener(null);
						boyView.setClickable(false);

						boySitView.removeAllViews();

						if (eatBoyView == null) {
							eatBoyView = getLayoutInflater().inflate(
									R.layout.main_boyeat, null);
						
							eatBoyBody = (ImageView) eatBoyView
									.findViewById(R.id.EatBoy_body);
						} else {
							ViewGroup oldParent = (ViewGroup) eatBoyView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(eatBoyView);
							}
						}
						eatBoyView.setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> eatBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initBoyEatting(
													getApplicationContext()));
					
						eatBoyBody.setImageDrawable(eatBm.get());
						eatBm = null;
						
						curBoyView = eatBoyView;
						boySitView.addView(eatBoyView);

						ImageView eatBoyEye = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_eye);
						ImageView eatBoyMouth = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_mouth);
						ImageView eatBoyHand = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_leftHand);
						AnimationDrawable eye = (AnimationDrawable) eatBoyEye
								.getDrawable();
						AnimationDrawable mouth = (AnimationDrawable) eatBoyMouth
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) eatBoyHand
								.getDrawable();
						eye.start();
						mouth.start();
						hand.start();
						}else{
							Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
							.show();
						}
					} else {
						// chat

						String chatmsg = "我开始吃饭啦！";
						addChatMsg(isSelf, chatmsg, curDate);

						girlView.setVisibility(View.GONE);
						girlView.setOnClickListener(null);
						girlView.setClickable(false);

						girlSitView.removeAllViews();

						if (eatGirlView == null) {
							eatGirlView = getLayoutInflater().inflate(
									R.layout.main_girleat, null);
							
							eatGirlBody = (ImageView) eatGirlView
									.findViewById(R.id.EatGirl_body);
						} else {
							ViewGroup oldParent = (ViewGroup) eatGirlView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(eatGirlView);
							}
						}
						// 初始化形象
						eatGirlView.setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> eatGirlLD = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initGirlEatting(
													getApplicationContext()));
					
						eatGirlBody.setImageDrawable(eatGirlLD.get());

						eatGirlLD = null;
						curGirlView = eatGirlView;
						girlSitView.addView(eatGirlView);

						ImageView eatGirlEye = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_eye);
						ImageView eatGirlMouth = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_mouth);
						ImageView eatGirlHand = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_leftHand);
						AnimationDrawable eye = (AnimationDrawable) eatGirlEye
								.getDrawable();
						AnimationDrawable mouth = (AnimationDrawable) eatGirlMouth
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) eatGirlHand
								.getDrawable();
						eye.start();
						mouth.start();
						hand.start();
					}
				} else {
					if (isSelf) {

						ActionPacketHandler actionReq = new ActionPacketHandler();
						if(actionReq.SendSingleActionBegin(curDate, actionType)){
							nowAction = actionType;

							SelfInfo.getInstance().setStatus(nowAction + "");
						// chat
						String chatmsg = "我开始吃饭啦！";
						addChatMsg(isSelf, chatmsg, curDate);

						girlView.setVisibility(View.GONE);
						girlView.setOnClickListener(null);
						girlView.setClickable(false);

						girlSitView.removeAllViews();

						if (eatGirlView == null) {
							eatGirlView = getLayoutInflater().inflate(
									R.layout.main_girleat, null);
						
							eatGirlBody = (ImageView) eatGirlView
									.findViewById(R.id.EatGirl_body);
						} else {
							ViewGroup oldParent = (ViewGroup) eatGirlView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(eatGirlView);
							}
						}
						eatGirlView.setOnClickListener(MainActivity.this);
						// 初始化形象
						// 初始化形象
						WeakReference<LayerDrawable> eatGirlLD = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initGirlEatting(
													getApplicationContext()));
						
						eatGirlBody.setImageDrawable(eatGirlLD.get());

						eatGirlLD = null;
						curGirlView = eatGirlView;
						girlSitView.addView(eatGirlView);

						ImageView eatGirlEye = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_eye);
						ImageView eatGirlMouth = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_mouth);
						ImageView eatGirlHand = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_leftHand);
						AnimationDrawable eye = (AnimationDrawable) eatGirlEye
								.getDrawable();
						AnimationDrawable mouth = (AnimationDrawable) eatGirlMouth
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) eatGirlHand
								.getDrawable();
						eye.start();
						mouth.start();
						hand.start();
						}else{
							Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
							.show();
						}
					} else {

						// chat
						String chatmsg = "我开始吃饭啦！";
						addChatMsg(isSelf, chatmsg, curDate);

						boyView.setVisibility(View.GONE);
						boyView.setOnClickListener(null);
						boyView.setClickable(false);
						boySitView.removeAllViews();

						if (eatBoyView == null) {
							eatBoyView = getLayoutInflater().inflate(
									R.layout.main_boyeat, null);
						
							eatBoyBody = (ImageView) eatBoyView
									.findViewById(R.id.EatBoy_body);
						} else {
							ViewGroup oldParent = (ViewGroup) eatBoyView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(eatBoyView);
							}
						}
						
						eatBoyView.setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> eatBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initBoyEatting(
													getApplicationContext()));
					
						eatBoyBody.setImageDrawable(eatBm.get());
						eatBm = null;
						curBoyView = eatBoyView;
						boySitView.addView(eatBoyView);
						ImageView eatBoyEye = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_eye);
						ImageView eatBoyMouth = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_mouth);
						ImageView eatBoyHand = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_leftHand);
						AnimationDrawable eye = (AnimationDrawable) eatBoyEye
								.getDrawable();
						AnimationDrawable mouth = (AnimationDrawable) eatBoyMouth
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) eatBoyHand
								.getDrawable();
						eye.start();
						mouth.start();
						hand.start();
					}
				}
				break;
			case Protocol.SINGLE_ACTION_LEARN:
				if (SelfInfo.getInstance().getSex().equals("b")) {
					if (isSelf) {

						ActionPacketHandler actionReq = new ActionPacketHandler();
						if(actionReq.SendSingleActionBegin(curDate, actionType)){
							nowAction = actionType;

							SelfInfo.getInstance().setStatus(nowAction + "");
						// chat
						String chatmsg = "我开始学习啦！";
						addChatMsg(isSelf, chatmsg, curDate);

						boyView.setVisibility(View.GONE);
						boyView.setOnClickListener(null);
						boyView.setClickable(false);

						boySitView.removeAllViews();

						if (readBoyView == null) {
							readBoyView = getLayoutInflater().inflate(
									R.layout.main_boyread, null);
						
							readBoyBody = (ImageView) readBoyView
									.findViewById(R.id.ReadBoy_body);

						} else {
							ViewGroup oldParent = (ViewGroup) readBoyView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(readBoyView);
							}
						}
						readBoyView.setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> readBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initBoyStudy(
													getApplicationContext()));
					
						readBoyBody.setImageDrawable(readBm.get());
						readBm = null;
						curBoyView = readBoyView;
						boySitView.addView(readBoyView);

						ImageView readBoyEye = (ImageView) readBoyView
								.findViewById(R.id.ReadBoy_eye);
						ImageView readBoyBook = (ImageView) readBoyView
								.findViewById(R.id.ReadBoy_Book);
						AnimationDrawable readBoyeyeAD = (AnimationDrawable) readBoyEye
								.getDrawable();
						AnimationDrawable readBoybookAD = (AnimationDrawable) readBoyBook
								.getDrawable();
						readBoyeyeAD.start();
						readBoybookAD.start();
						}else{
							Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
							.show();
						}
					} else {

						// chat
						String chatmsg = "我开始学习啦！";
						addChatMsg(isSelf, chatmsg, curDate);

						girlView.setVisibility(View.GONE);
						girlView.setOnClickListener(null);
						girlView.setClickable(false);

						girlSitView.removeAllViews();

						if (readGirlView == null) {
							readGirlView = getLayoutInflater().inflate(
									R.layout.main_girlread, null);
							
							readGirlBody = (ImageView) readGirlView
									.findViewById(R.id.ReadGirl_body);
						} else {
							ViewGroup oldParent = (ViewGroup) readGirlView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(readGirlView);
							}
						}
						readGirlView.setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> readBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initGirlStudy(
													getApplicationContext()));
						
						readGirlBody.setImageDrawable(readBm.get());
						readBm = null;
						
						
						curGirlView = readGirlView;
						girlSitView.addView(readGirlView);
						ImageView readGirlEye = (ImageView) readGirlView
								.findViewById(R.id.ReadGirl_eye);
						ImageView readGirlHand = (ImageView) readGirlView
								.findViewById(R.id.ReadGirl_hand);
						AnimationDrawable eye = (AnimationDrawable) readGirlEye
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) readGirlHand
								.getDrawable();
						eye.start();
						hand.start();
					}
				} else {
					if (isSelf) {

						ActionPacketHandler actionReq = new ActionPacketHandler();
						if(actionReq.SendSingleActionBegin(curDate, actionType)){
							nowAction = actionType;

							SelfInfo.getInstance().setStatus(nowAction + "");
						// chat
						String chatmsg = "我开始学习啦！";
						addChatMsg(isSelf, chatmsg, curDate);

						girlView.setClickable(false);
						girlView.setLongClickable(false);
						girlView.setVisibility(View.GONE);

						girlSitView.removeAllViews();

						if (readGirlView == null) {
							readGirlView = getLayoutInflater().inflate(
									R.layout.main_girlread, null);
							
							readGirlBody = (ImageView) readGirlView
									.findViewById(R.id.ReadGirl_body);
						} else {
							ViewGroup oldParent = (ViewGroup) readGirlView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(readGirlView);
							}
						}
						readGirlView.setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> readBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initGirlStudy(
													getApplicationContext()));
						
						readGirlBody.setImageDrawable(readBm.get());
						readBm = null;
						
						
						curGirlView = readGirlView;
						girlSitView.addView(readGirlView);
						ImageView readGirlEye = (ImageView) readGirlView
								.findViewById(R.id.ReadGirl_eye);
						ImageView readGirlHand = (ImageView) readGirlView
								.findViewById(R.id.ReadGirl_hand);
						AnimationDrawable eye = (AnimationDrawable) readGirlEye
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) readGirlHand
								.getDrawable();
						eye.start();
						hand.start();
						}else{
							Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
							.show();
						}
					} else {

						// chat
						String chatmsg = "我开始学习啦！";
						addChatMsg(isSelf, chatmsg, curDate);

						boyView.setClickable(false);
						boyView.setLongClickable(false);
						boyView.setVisibility(View.GONE);

						boySitView.removeAllViews();

						if (readBoyView == null) {
							readBoyView = getLayoutInflater().inflate(
									R.layout.main_boyread, null);
						
							readBoyBody = (ImageView) readBoyView
									.findViewById(R.id.ReadBoy_body);

						} else {
							ViewGroup oldParent = (ViewGroup) readBoyView
									.getParent();
							if (oldParent != null) {
								oldParent.removeView(readBoyView);
							}
						}
						readBoyView.setOnClickListener(MainActivity.this);
						// 初始化形象
						WeakReference<LayerDrawable> readBm = 
								   new WeakReference<LayerDrawable>(InitFigureAppDrawable
											.getInstance().initBoyStudy(
													getApplicationContext()));
					
						readBoyBody.setImageDrawable(readBm.get());
						readBm = null;
						
						
						curBoyView = readBoyView;
						boySitView.addView(readBoyView);

						ImageView readBoyEye = (ImageView) readBoyView
								.findViewById(R.id.ReadBoy_eye);
						ImageView readBoyBook = (ImageView) readBoyView
								.findViewById(R.id.ReadBoy_Book);
						AnimationDrawable readBoyeyeAD = (AnimationDrawable) readBoyEye
								.getDrawable();
						AnimationDrawable readBoybookAD = (AnimationDrawable) readBoyBook
								.getDrawable();
						readBoyeyeAD.start();
						readBoybookAD.start();
					}
				}
				break;

			case Protocol.SINGLE_ACTION_SLEEP:
				if (isSelf) {

					ActionPacketHandler actionReq = new ActionPacketHandler();
					if(actionReq.SendSingleActionBegin(curDate, actionType)){
						nowAction = actionType;

						SelfInfo.getInstance().setStatus(nowAction + "");
					// chat
					String chatmsg = "我开始睡觉啦！";
					addChatMsg(isSelf, chatmsg, curDate);
					this.sleepRL.setOnClickListener(MainActivity.this);
					if (SelfInfo.getInstance().getSex().equals("b")) {
						boyView.setClickable(false);
						boyView.setLongClickable(false);
						boyView.setVisibility(View.GONE);
						boysleepBubbleView.setVisibility(View.VISIBLE);
						 this.curBoyView =boysleepBubbleView;
						AnimationDrawable sleepBubble = (AnimationDrawable) boysleepBubbleView
								.getDrawable();
						sleepBubble.start();

					} else {
						girlView.setClickable(false);
						girlView.setLongClickable(false);
						girlView.setVisibility(View.GONE);
						girlsleepBubbleView.setVisibility(View.VISIBLE);
						 this.curGirlView =girlsleepBubbleView;
						this.sleepRL.setOnClickListener(MainActivity.this);
						AnimationDrawable sleepBubble = (AnimationDrawable) girlsleepBubbleView
								.getDrawable();
						sleepBubble.start();
					}
					}else{
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT)
						.show();
					}
				} else {
					
					if (!(SelfInfo.getInstance().getSex().equals("b"))) {
						boyView.setClickable(false);
						boyView.setLongClickable(false);
						boyView.setVisibility(View.GONE);
						boysleepBubbleView.setVisibility(View.VISIBLE);
						 this.curBoyView =boysleepBubbleView;
						AnimationDrawable sleepBubble = (AnimationDrawable) boysleepBubbleView
								.getDrawable();
						sleepBubble.start();

					} else {
						girlView.setClickable(false);
						girlView.setLongClickable(false);
						girlView.setVisibility(View.GONE);
						girlsleepBubbleView.setVisibility(View.VISIBLE);
						this.curGirlView =girlsleepBubbleView;
						AnimationDrawable sleepBubble = (AnimationDrawable) girlsleepBubbleView
								.getDrawable();
						sleepBubble.start();
					}

					// chat
					String chatmsg = "我开始睡觉啦！";
					addChatMsg(isSelf, chatmsg, curDate);

				}

				break;
			default:
				break;
			}
		} else {
			// 双人动作
			switch (actionType) {
			case Protocol.ABUSE:
				this.curBoyView.setVisibility(View.GONE);
				this.curGirlView.setVisibility(View.GONE);
				Intent startIntent = new Intent(MainActivity.this,
						CoupleActionActivity.class);
				startIntent.putExtra("coupleActioinType", Protocol.ABUSE);
				startIntent.putExtra("isBoy", false);
				startIntent.putExtra("isSeflDo", true);
				startActivityForResult(startIntent,R.layout.couple_person_abuse);
//			
				break;
			case Protocol.PINCHEDFACE:
				this.curBoyView.setVisibility(View.GONE);
				this.curGirlView.setVisibility(View.GONE);
				if (isSelf) {
					if (SelfInfo.getInstance().getSex().equals("b")) {
						Intent PINCHEDFACEIntent = new Intent(
								MainActivity.this, CoupleActionActivity.class);
						PINCHEDFACEIntent.putExtra("coupleActioinType",
								Protocol.PINCHEDFACE);
						PINCHEDFACEIntent.putExtra("isBoy", true);
						PINCHEDFACEIntent.putExtra("isSeflDo", isSelf);
						startActivityForResult(PINCHEDFACEIntent,
								R.layout.boypface);

					} else {
						Intent PINCHEDFACEIntent = new Intent(
								MainActivity.this, CoupleActionActivity.class);
						PINCHEDFACEIntent.putExtra("coupleActioinType",
								Protocol.PINCHEDFACE);
						PINCHEDFACEIntent.putExtra("isBoy", false);
						PINCHEDFACEIntent.putExtra("isSeflDo", isSelf);
						startActivityForResult(PINCHEDFACEIntent,
								R.layout.girlpface);

					}
				} else {
					if (!(SelfInfo.getInstance().getSex().equals("b"))) {

						Intent PINCHEDFACEIntent = new Intent(
								MainActivity.this, CoupleActionActivity.class);
						PINCHEDFACEIntent.putExtra("coupleActioinType",
								Protocol.PINCHEDFACE);
						PINCHEDFACEIntent.putExtra("isBoy", true);
						PINCHEDFACEIntent.putExtra("isSeflDo", isSelf);
						startActivityForResult(PINCHEDFACEIntent,
								R.layout.girlpface);
					} else {

						Intent PINCHEDFACEIntent = new Intent(
								MainActivity.this, CoupleActionActivity.class);
						PINCHEDFACEIntent.putExtra("coupleActioinType",
								Protocol.PINCHEDFACE);
						PINCHEDFACEIntent.putExtra("isBoy", false);
						PINCHEDFACEIntent.putExtra("isSeflDo", isSelf);
						startActivityForResult(PINCHEDFACEIntent,
								R.layout.girlpface);
					}
				}
				break;
			case Protocol.PETTING:
				this.curBoyView.setVisibility(View.GONE);
				this.curGirlView.setVisibility(View.GONE);
				if (isSelf) {
					if (SelfInfo.getInstance().getSex().equals("b")) {
						Intent PINCHEDFACEIntent = new Intent(
								MainActivity.this, CoupleActionActivity.class);
						PINCHEDFACEIntent.putExtra("coupleActioinType",
								Protocol.PETTING);
						PINCHEDFACEIntent.putExtra("isBoy", true);
						PINCHEDFACEIntent.putExtra("isSeflDo", true);
						startActivityForResult(PINCHEDFACEIntent,
								R.layout.boypethead);

					} else {

						Intent PINCHEDFACEIntent = new Intent(
								MainActivity.this, CoupleActionActivity.class);
						PINCHEDFACEIntent.putExtra("coupleActioinType",
								Protocol.PETTING);
						PINCHEDFACEIntent.putExtra("isBoy", false);
						PINCHEDFACEIntent.putExtra("isSeflDo", true);
						startActivityForResult(PINCHEDFACEIntent,
								R.layout.girlpetthead);
					}
				} else {
					if (!(SelfInfo.getInstance().getSex().equals("b"))) {

						Intent PINCHEDFACEIntent = new Intent(
								MainActivity.this, CoupleActionActivity.class);
						PINCHEDFACEIntent.putExtra("coupleActioinType",
								Protocol.PETTING);
						PINCHEDFACEIntent.putExtra("isBoy", true);
						PINCHEDFACEIntent.putExtra("isSeflDo", false);
						startActivityForResult(PINCHEDFACEIntent,
								R.layout.girlpetthead);
					} else {

						Intent PINCHEDFACEIntent = new Intent(
								MainActivity.this, CoupleActionActivity.class);
						PINCHEDFACEIntent.putExtra("coupleActioinType",
								Protocol.PETTING);
						PINCHEDFACEIntent.putExtra("isBoy", false);
						PINCHEDFACEIntent.putExtra("isSeflDo", false);
						startActivityForResult(PINCHEDFACEIntent,
								R.layout.boypethead);
					}
				}
				break;
			case Protocol.SEX:
				this.curBoyView.setVisibility(View.GONE);
				this.curGirlView.setVisibility(View.GONE);
				// TODO
				blackScreen();
				long[] pattern = { 100, 400, 100, 400, 100, 400, 100, 400, 100,
						400, 100, 400, 100, 400, 100, 400, 100, 400, 100, 400 };
				AppManagerUtil.Vibrate(MainActivity.this, pattern, false);
				break;

			case Protocol.KISS:
				this.curBoyView.setVisibility(View.GONE);
				this.curGirlView.setVisibility(View.GONE);
				Intent PINCHEDFACEIntent = new Intent(MainActivity.this,
						CoupleActionActivity.class);
				PINCHEDFACEIntent.putExtra("coupleActioinType", Protocol.KISS);
				PINCHEDFACEIntent.putExtra("isBoy", true);
				PINCHEDFACEIntent.putExtra("isSeflDo", false);
				startActivityForResult(PINCHEDFACEIntent, R.layout.boypethead);

				break;
			case Protocol.HUG:
				this.curBoyView.setVisibility(View.GONE);
				this.curGirlView.setVisibility(View.GONE);
				Intent HUGIntent = new Intent(MainActivity.this,
						CoupleActionActivity.class);
				HUGIntent.putExtra("coupleActioinType", Protocol.HUG);
				HUGIntent.putExtra("isBoy", true);
				HUGIntent.putExtra("isSeflDo", false);
				startActivityForResult(HUGIntent, R.layout.boypethead);

				break;

			default:
				break;
			}
		}
	}

	public void CustomActionStart(boolean isSelf, int actionType, String isBoy,
			String content) {
		// 聊天提醒bubble
		if(boyChatbubbleflag != null){
		boyChatbubbleflag.setVisibility(View.GONE);
		boyChatbubbleflag.setOnClickListener(null);
		}
		// 聊天提醒bubble
		if(girlChatbubbleflag != null){
		girlChatbubbleflag.setVisibility(View.GONE);
		girlChatbubbleflag.setOnClickListener(null);
		}
		if (isSelf) {
			nowAction = actionType;
			SelfInfo.getInstance().setStatus(nowAction + "");
			// nowCustomActionTypeId =
			// actionType-Protocol.SINGLE_ACTION_CUSTOM1+1;
			nowCustomActionContent = content;

		}
		if (isBoy.equals("b")) {
			boyView.setClickable(false);
			boyView.setLongClickable(false);
			boyView.setVisibility(View.GONE);
			boycustombubbleBtn.setClickable(true);
			boycustombubbleBtn.setOnClickListener(this);
			boycustombubble.setText(content);
			boycustombubbleRL.setVisibility(View.VISIBLE);
			 this.curBoyView =boycustombubbleRL;
			//摇摆
			TranslateAnimation translateAnimation = new TranslateAnimation(0f, 20f, 0, 0);
			translateAnimation.setDuration(1000);
			translateAnimation.setRepeatCount(Animation.INFINITE);
			translateAnimation.setRepeatMode(Animation.REVERSE);
			boycustombubble.setAnimation(translateAnimation); 
			translateAnimation.start();

		} else {
			girlView.setClickable(false);
			girlView.setLongClickable(false);
			girlView.setVisibility(View.GONE);
			girlcustombubbleBtn.setClickable(true);
			girlcustombubbleBtn.setOnClickListener(this);
			girlcustombubble.setText(content);
			girlcustombubbleRL.setVisibility(View.VISIBLE);
			 this.curGirlView =girlcustombubbleRL;
			//摇摆
			TranslateAnimation translateAnimation = new TranslateAnimation(0f, 20f, 0, 0);
			translateAnimation.setDuration(1000);
			translateAnimation.setRepeatCount(Animation.INFINITE);
			translateAnimation.setRepeatMode(Animation.REVERSE);
			girlcustombubble.setAnimation(translateAnimation); //这里iv就是我们要执行动画的item，例如一个imageView
			translateAnimation.start();
		}

		// switch (actionType) {
		// case Protocol.SINGLE_ACTION_CUSTOM1:
		//
		// break;
		// case Protocol.SINGLE_ACTION_CUSTOM2:
		//
		// break;
		// case Protocol.SINGLE_ACTION_CUSTOM3:
		//
		// break;
		// case Protocol.SINGLE_ACTION_CUSTOM4:
		//
		// break;
		// case Protocol.SINGLE_ACTION_CUSTOM5:
		//
		// break;
		//
		// default:
		// break;
		// }

	}

	public void CustomActionEnd(boolean isSelf, int actionType, boolean isBoy) {

		// ActionPacketHandler mAP = new ActionPacketHandler();
		// mAP.SendSingleActionEnd(curTime, actionType);
		// String chatmsg = "我起床啦！";
		// addChatMsg(isSelf, chatmsg, curTime);
		// 聊天提醒bubble
		if(boyChatbubbleflag != null){
		boyChatbubbleflag.setVisibility(View.GONE);
		boyChatbubbleflag.setOnClickListener(null);
		}
		// 聊天提醒bubble
		if(girlChatbubbleflag != null){
		girlChatbubbleflag.setVisibility(View.GONE);
		girlChatbubbleflag.setOnClickListener(null);
		}
		if (isSelf) {
			SelfInfo.getInstance().setStatus(Protocol.ActionEnd + "");
			this.nowAction = Protocol.ActionEnd;
		}

		if (isBoy) {
			if(boycustombubble.getAnimation()!= null){
				boycustombubble.getAnimation().cancel();
			}
			boycustombubbleBtn.setOnClickListener(null);
			boycustombubbleBtn.setClickable(false);
			boycustombubbleRL.setVisibility(View.INVISIBLE);
			boyView.setVisibility(View.VISIBLE);
			boyView.setClickable(true);
			boyView.setOnClickListener(MainActivity.this);
			startBoyTwinkle();
			startBoyHandShake();
			this.curBoyView = boyView;
			

		} else {
			if(girlcustombubble.getAnimation()!= null){
				girlcustombubble.getAnimation().cancel();
			}
			girlcustombubbleBtn.setOnClickListener(null);
			girlcustombubbleBtn.setClickable(false);
			girlcustombubbleRL.setVisibility(View.INVISIBLE);
//			girlcustombubble.getAnimation().cancel();
			girlView.setVisibility(View.VISIBLE);
			girlView.setClickable(true);
			girlView.setOnClickListener(MainActivity.this);
			startGirlTwinkle();
			startGirlHandShake();
			this.curGirlView = girlView;
			
		}

		// switch (actionType) {
		// case Protocol.SINGLE_ACTION_CUSTOM1:
		//
		// break;
		// case Protocol.SINGLE_ACTION_CUSTOM2:
		//
		// break;
		// case Protocol.SINGLE_ACTION_CUSTOM3:
		//
		// break;
		// case Protocol.SINGLE_ACTION_CUSTOM4:
		//
		// break;
		// case Protocol.SINGLE_ACTION_CUSTOM5:
		//
		// break;
		//
		// default:
		// break;
		// }

	}

	public void InitSingleActionEnd(int actionType, boolean isBoy) {
		switch (actionType) {
		case Protocol.SINGLE_ACTION_MISS:

			if (isBoy) {
				if (missBoyView == null || missBoyView.get() == null) {
					return;
				}

				ImageView missBoyHeart = (ImageView) missBoyView.get()
						.findViewById(R.id.missing_heart);
				ImageView missBoyEye = (ImageView) missBoyView.get()
						.findViewById(R.id.boy_miss_eye);

				AnimationDrawable heart = (AnimationDrawable) missBoyHeart
						.getDrawable();
				heart.stop();
				((AnimationDrawable) missBoyEye
				.getDrawable()).stop();
				
				missBoyEye = null;
				missBoyHeart = null;
				if(missBoyBody != null){
					missBoyBody.setImageDrawable(null);
				}
				missBoyView.get().setOnClickListener(null);
				boyStandView.removeAllViews();

			} else {

				if (missGirlView == null) {
					return;
				}
				ImageView missGirlHeart = (ImageView) missGirlView
						.findViewById(R.id.missing_heart);
				ImageView missGirlEye = (ImageView) missGirlView
						.findViewById(R.id.missing_girl_eye);

				AnimationDrawable heart = (AnimationDrawable) missGirlHeart
						.getDrawable();
				heart.stop();
				((AnimationDrawable) missGirlEye
				.getDrawable()).stop();
				if(missGirlBody != null){
					missGirlBody.setImageDrawable(null);
				}
				missGirlView.setOnClickListener(null);
				missGirlHeart = null;
				missGirlEye = null;
				girlStandView.removeAllViews();
			}
			break;
		case Protocol.SINGLE_ACTION_ANGRY:
			if (isBoy) {
				if (angryBoyView == null || angryBoyView.get() == null) {
					angryBoyView =new SoftReference<View>(getLayoutInflater().inflate(
							R.layout.main_angryboy, null));
					angryBoyView.get().setOnClickListener(MainActivity.this);

				}
				ImageView angryBoyFoot = (ImageView) angryBoyView.get()
						.findViewById(R.id.Angryboy_foot);
				AnimationDrawable foot = (AnimationDrawable) angryBoyFoot
						.getDrawable();
				
				foot.stop();
				
				if(angryBoyBody != null){
					angryBoyBody.setImageDrawable(null);
				}
				angryBoyFoot = null;
//				angryBoyBody = null;
				angryBoyView.get().setOnClickListener(null);
				boyStandView.removeAllViews();

			} else {
				if (angryGirlView == null) {
					angryGirlView = getLayoutInflater().inflate(
							R.layout.main_angrygirl, null);
					angryGirlView.setOnClickListener(MainActivity.this);

				}
				ImageView angryGirlFoot = (ImageView) angryGirlView
						.findViewById(R.id.Angrygirl_foot);
				ImageView angryGirlFire = (ImageView) angryGirlView
						.findViewById(R.id.Angrygirl_fire);
				AnimationDrawable foot = (AnimationDrawable) angryGirlFoot
						.getDrawable();
				AnimationDrawable fire = (AnimationDrawable) angryGirlFire
						.getDrawable();
				foot.stop();
				fire.stop();
				
				if(angryGirlBody != null){
					angryGirlBody.setImageDrawable(null);
				}
//				angryGirlBody = null;
				angryGirlFoot = null;
				angryGirlFire = null;
				angryGirlView.setOnClickListener(null);
				girlStandView.removeAllViews();

			}

			break;
		case Protocol.SINGLE_ACTION_EAT:

			if (isBoy) {
				if (eatBoyView == null) {
					return;
				}
				ImageView eatBoyEye = (ImageView) eatBoyView
						.findViewById(R.id.EatBoy_eye);
				ImageView eatBoyMouth = (ImageView) eatBoyView
						.findViewById(R.id.EatBoy_mouth);
				ImageView eatBoyHand = (ImageView) eatBoyView
						.findViewById(R.id.EatBoy_leftHand);
				AnimationDrawable eye = (AnimationDrawable) eatBoyEye
						.getDrawable();
				AnimationDrawable mouth = (AnimationDrawable) eatBoyMouth
						.getDrawable();
				AnimationDrawable hand = (AnimationDrawable) eatBoyHand
						.getDrawable();
				eye.stop();
				mouth.stop();
				hand.stop();

				if(eatBoyBody != null){
					eatBoyBody.setImageDrawable(null);
				}
				eatBoyView.setOnClickListener(null);
				eatBoyEye = null;
				eatBoyMouth= null;
				eatBoyHand = null;
				boySitView.removeAllViews();

			} else {
				if (eatGirlView == null) {
					return;
				}
				ImageView eatGirlEye = (ImageView) eatGirlView
						.findViewById(R.id.EatGirl_eye);
				ImageView eatGirlMouth = (ImageView) eatGirlView
						.findViewById(R.id.EatGirl_mouth);
				ImageView eatGirlHand = (ImageView) eatGirlView
						.findViewById(R.id.EatGirl_leftHand);
				AnimationDrawable eye = (AnimationDrawable) eatGirlEye
						.getDrawable();
				AnimationDrawable mouth = (AnimationDrawable) eatGirlMouth
						.getDrawable();
				AnimationDrawable hand = (AnimationDrawable) eatGirlHand
						.getDrawable();
				eye.stop();
				mouth.stop();
				hand.stop();

				if(eatGirlBody != null){
					eatGirlBody.setImageDrawable(null);
				}
				eatGirlView.setOnClickListener(null);
				eatGirlEye = null;
				eatGirlMouth= null;
				eatGirlHand = null;
				girlSitView.removeAllViews();
			}

			break;
		case Protocol.SINGLE_ACTION_LEARN:

			if (isBoy) {

				if (readBoyView == null) {
					return;
				}

				ImageView readBoyEye = (ImageView) readBoyView
						.findViewById(R.id.ReadBoy_eye);
				ImageView readBoyBook = (ImageView) readBoyView
						.findViewById(R.id.ReadBoy_Book);
				AnimationDrawable eye = (AnimationDrawable) readBoyEye
						.getDrawable();
				AnimationDrawable book = (AnimationDrawable) readBoyBook
						.getDrawable();
				eye.stop();
				book.stop();
				
				if(readBoyBody != null){
					readBoyBody.setImageDrawable(null);
				}
				readBoyView.setOnClickListener(null);
				readBoyEye = null;
				readBoyBook = null;
				boySitView.removeAllViews();

			} else {
				if (readGirlView == null) {
					return;
				}
				ImageView readGirlEye = (ImageView) readGirlView
						.findViewById(R.id.ReadGirl_eye);
				ImageView readGirlHand = (ImageView) readGirlView
						.findViewById(R.id.ReadGirl_hand);
				AnimationDrawable eye = (AnimationDrawable) readGirlEye
						.getDrawable();
				AnimationDrawable hand = (AnimationDrawable) readGirlHand
						.getDrawable();
				eye.stop();
				hand.stop();

				if(readGirlBody != null){
					readGirlBody.setImageDrawable(null);
				}
				readGirlView.setOnClickListener(null);
				readGirlEye = null;
				readGirlHand = null;
				girlSitView.removeAllViews();

			}

			break;

		case Protocol.SINGLE_ACTION_SLEEP:

			if (isBoy) {
				if (boysleepBubbleView == null)
					return;
				AnimationDrawable sleepBubble = (AnimationDrawable) boysleepBubbleView
						.getDrawable();
				sleepBubble.stop();
				boysleepBubbleView.setVisibility(View.GONE);
				this.sleepRL.setOnClickListener(null);
				MainActivity.this.boyView.setVisibility(View.VISIBLE);
			} else {
				if (girlsleepBubbleView == null)
					return;
				AnimationDrawable sleepBubble = (AnimationDrawable) girlsleepBubbleView
						.getDrawable();
				sleepBubble.stop();
				girlsleepBubbleView.setVisibility(View.GONE);
				MainActivity.this.girlView.setVisibility(View.VISIBLE);
			}

			break;
		case Protocol.SINGLE_ACTION_CUSTOM1:

		case Protocol.SINGLE_ACTION_CUSTOM2:

		case Protocol.SINGLE_ACTION_CUSTOM3:

		case Protocol.SINGLE_ACTION_CUSTOM4:

		case Protocol.SINGLE_ACTION_CUSTOM5:

			if (isBoy) {

				boyView.setVisibility(View.VISIBLE);
				boyView.setClickable(true);
				boyView.setOnClickListener(MainActivity.this);

				if(boycustombubble.getAnimation()!= null){
					boycustombubble.getAnimation().cancel();
				}
				boycustombubbleBtn.setOnClickListener(null);
				boycustombubbleBtn.setClickable(false);
				boycustombubbleRL.setVisibility(View.INVISIBLE);

			} else {
				girlView.setVisibility(View.VISIBLE);
				girlView.setClickable(true);
				girlView.setOnClickListener(MainActivity.this);
				if(girlcustombubble.getAnimation()!= null){
					girlcustombubble.getAnimation().cancel();
				}
				girlcustombubbleBtn.setOnClickListener(null);
				girlcustombubbleBtn.setClickable(false);
				girlcustombubbleRL.setVisibility(View.INVISIBLE);
			}
		default:
			break;
		}
	}

	public void ActionEnd(int actionType, boolean isSingleAction, boolean isSelf) {

		if (isSelf) {

			SelfInfo.getInstance().setStatus(Protocol.ActionEnd + "");

			if (SelfInfo.getInstance().getSex().equals("b")) {

				this.curBoyView = null;
				boyView.setVisibility(View.VISIBLE);
				boyView.setClickable(true);
				boyView.setOnClickListener(MainActivity.this);
				this.curBoyView = boyView;

			} else {
				 this.curGirlView= null;
				girlView.setVisibility(View.VISIBLE);
				girlView.setClickable(true);
				girlView.setOnClickListener(MainActivity.this);
				 this.curGirlView = girlView;

			}
		} else {
			if (!(SelfInfo.getInstance().getSex().equals("b"))) {

				 this.curBoyView = null;
				boyView.setVisibility(View.VISIBLE);
				boyView.setClickable(true);
				boyView.setOnClickListener(MainActivity.this);
				 this.curBoyView = boyView;

			} else {
				 this.curGirlView = null;
				girlView.setVisibility(View.VISIBLE);
				girlView.setClickable(true);
				girlView.setOnClickListener(MainActivity.this);
				 this.curGirlView = girlView;

			}
		}

		String curTime = AppManagerUtil.getCurDate();
		if (isSingleAction) {
			switch (actionType) {
			case Protocol.SINGLE_ACTION_MISS:
				if (isSelf) {

					ActionPacketHandler mAP = new ActionPacketHandler();
					mAP.SendSingleActionEnd(curTime, actionType);

					if (SelfInfo.getInstance().getSex().equals("b")) {

						if (missBoyView == null || missBoyView.get() == null) {
							missBoyView =new SoftReference<View>(getLayoutInflater().inflate(
									R.layout.main_boymiss, null));
							missBoyView.get().setOnClickListener(MainActivity.this);
						}

						ImageView missBoyHeart = (ImageView) missBoyView.get()
								.findViewById(R.id.missing_heart);
						ImageView missBoyEye = (ImageView) missBoyView.get()
								.findViewById(R.id.boy_miss_eye);

						AnimationDrawable heart = (AnimationDrawable) missBoyHeart
								.getDrawable();
						heart.stop();
						((AnimationDrawable) missBoyEye
						.getDrawable()).stop();
						
						if(missBoyBody != null){
							missBoyBody.setImageDrawable(null);
						}
						missBoyView.get().setOnClickListener(null);
						missBoyHeart = null;
						missBoyEye = null;
						boyStandView.removeAllViews();

					} else {

						if (missGirlView == null) {
							missGirlView = getLayoutInflater().inflate(
									R.layout.main_missinggirl, null);
							missGirlView.setOnClickListener(MainActivity.this);
						}
						ImageView missGirlHeart = (ImageView) missGirlView
								.findViewById(R.id.missing_heart);
						ImageView missGirlEye = (ImageView) missGirlView
								.findViewById(R.id.missing_girl_eye);


						AnimationDrawable heart = (AnimationDrawable) missGirlHeart
								.getDrawable();
						heart.stop();
						((AnimationDrawable) missGirlEye
						.getDrawable()).stop();
						
						if(missGirlBody != null){
							missGirlBody.setImageDrawable(null);
						}
						missGirlView.setOnClickListener(null);
						missGirlHeart = null;
						missGirlEye = null;
						girlStandView.removeAllViews();

					}
				} else {
					if (!(SelfInfo.getInstance().getSex().equals("b"))) {
						if (missBoyView == null || missBoyView.get() == null) {
							missBoyView = new SoftReference<View>(getLayoutInflater().inflate(
									R.layout.main_boymiss, null));
							missBoyView.get().setOnClickListener(MainActivity.this);
						}

						ImageView missBoyHeart = (ImageView) missBoyView.get()
								.findViewById(R.id.missing_heart);

						AnimationDrawable heart = (AnimationDrawable) missBoyHeart
								.getDrawable();
						
						heart.stop();
						ImageView missBoyEye = (ImageView) missBoyView.get()
								.findViewById(R.id.boy_miss_eye);

					
						((AnimationDrawable) missBoyEye
						.getDrawable()).stop();
						
						if(missBoyBody != null){
							missBoyBody.setImageDrawable(null);
						}
						missBoyView.get().setOnClickListener(null);
						missBoyHeart = null;
						missBoyEye = null;
						boyStandView.removeAllViews();

					} else {
						if (missGirlView == null) {
							missGirlView = getLayoutInflater().inflate(
									R.layout.main_missinggirl, null);
							missGirlView.setOnClickListener(MainActivity.this);
						}
						ImageView missGirlHeart = (ImageView) missGirlView
								.findViewById(R.id.missing_heart);

						AnimationDrawable heart = (AnimationDrawable) missGirlHeart
								.getDrawable();
						
						heart.stop();
						ImageView missGirlEye = (ImageView) missGirlView
								.findViewById(R.id.missing_girl_eye);
						girlStandView.removeAllViews();
						((AnimationDrawable) missGirlEye
								.getDrawable()).stop();
						
						if(missGirlBody != null){
							missGirlBody.setImageDrawable(null);
						}
						missGirlView.setOnClickListener(null);
						missGirlHeart = null;
						missGirlEye = null;

					}

				}
				break;
			case Protocol.SINGLE_ACTION_ANGRY:
				if (isSelf) {

					ActionPacketHandler mAP = new ActionPacketHandler();
					mAP.SendSingleActionEnd(curTime, actionType);
					String chatmsg = "勉强原谅你！";
					addChatMsg(isSelf, chatmsg, curTime);

					if (SelfInfo.getInstance().getSex().equals("b")) {
						if (angryBoyView == null || angryBoyView.get() == null) {
							angryBoyView =new SoftReference<View>(getLayoutInflater().inflate(
									R.layout.main_angryboy, null));
							angryBoyView.get().setOnClickListener(MainActivity.this);

						}
						ImageView angryBoyFoot = (ImageView) angryBoyView.get()
								.findViewById(R.id.Angryboy_foot);
						AnimationDrawable foot = (AnimationDrawable) angryBoyFoot
								.getDrawable();
						foot.stop();
						if(angryBoyBody != null){
							angryBoyBody.setImageDrawable(null);
						}
						angryBoyView.get().setOnClickListener(null);
						angryBoyFoot = null;
						
						boyStandView.removeAllViews();

					} else {
						if (angryGirlView == null) {
							angryGirlView = getLayoutInflater().inflate(
									R.layout.main_angrygirl, null);
							angryGirlView.setOnClickListener(MainActivity.this);

						}
						ImageView angryGirlFoot = (ImageView) angryGirlView
								.findViewById(R.id.Angrygirl_foot);
						ImageView angryGirlFire = (ImageView) angryGirlView
								.findViewById(R.id.Angrygirl_fire);
						AnimationDrawable foot = (AnimationDrawable) angryGirlFoot
								.getDrawable();
						AnimationDrawable fire = (AnimationDrawable) angryGirlFire
								.getDrawable();
						foot.stop();
						fire.stop();
						
						if(angryGirlBody != null){
							angryGirlBody.setImageDrawable(null);
						}
						angryGirlView.setOnClickListener(null);
						angryGirlFoot = null;
						angryGirlFire = null;
						girlStandView.removeAllViews();

					}
				} else {

					String chatmsg = "勉强原谅你！";
					addChatMsg(isSelf, chatmsg, curTime);

					if (!(SelfInfo.getInstance().getSex().equals("b"))) {
						if (angryBoyView == null || angryBoyView.get() == null) {
							angryBoyView = new SoftReference<View> (getLayoutInflater().inflate(
									R.layout.main_angryboy, null));
							angryBoyView.get().setOnClickListener(MainActivity.this);

						}

						ImageView angryBoyFoot = (ImageView) angryBoyView.get()
								.findViewById(R.id.Angryboy_foot);
						AnimationDrawable foot = (AnimationDrawable) angryBoyFoot
								.getDrawable();
						foot.stop();
						if(angryBoyBody != null){
							angryBoyBody.setImageDrawable(null);
						}
						angryBoyView.get().setOnClickListener(null);
						angryBoyFoot = null;
						boyStandView.removeAllViews();

					} else {
						if (angryGirlView == null) {
							angryGirlView = getLayoutInflater().inflate(
									R.layout.main_angrygirl, null);
							angryGirlView.setOnClickListener(MainActivity.this);

						}
						ImageView angryGirlFoot = (ImageView) angryGirlView
								.findViewById(R.id.Angrygirl_foot);
						ImageView angryGirlFire = (ImageView) angryGirlView
								.findViewById(R.id.Angrygirl_fire);
						AnimationDrawable foot = (AnimationDrawable) angryGirlFoot
								.getDrawable();
						AnimationDrawable fire = (AnimationDrawable) angryGirlFire
								.getDrawable();
						foot.stop();
						fire.stop();
						if(angryGirlBody != null){
							angryGirlBody.setImageDrawable(null);
						}
						angryGirlView.setOnClickListener(null);
						angryGirlFoot = null;
						angryGirlFire = null;
						girlStandView.removeAllViews();
						girlStandView.removeAllViews();

					}
				}
				break;
			case Protocol.SINGLE_ACTION_EAT:

				if (isSelf) {

					ActionPacketHandler mAP = new ActionPacketHandler();
					mAP.SendSingleActionEnd(curTime, actionType);
					String chatmsg = "我吃完饭啦！";
					addChatMsg(isSelf, chatmsg, curTime);

					if (SelfInfo.getInstance().getSex().equals("b")) {
						if (eatBoyView == null) {
							eatBoyView = getLayoutInflater().inflate(
									R.layout.main_boyeat, null);
							eatBoyView.setOnClickListener(MainActivity.this);
						}
						ImageView eatBoyEye = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_eye);
						ImageView eatBoyMouth = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_mouth);
						ImageView eatBoyHand = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_leftHand);
						AnimationDrawable eye = (AnimationDrawable) eatBoyEye
								.getDrawable();
						AnimationDrawable mouth = (AnimationDrawable) eatBoyMouth
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) eatBoyHand
								.getDrawable();
						eye.stop();
						mouth.stop();
						hand.stop();
						if(eatBoyBody != null){
							eatBoyBody.setImageDrawable(null);
						}
						eatBoyView.setOnClickListener(null);
						eatBoyEye = null;
						eatBoyMouth = null;
						eatBoyHand= null;
					
						boySitView.removeAllViews();

					} else {
						if (eatGirlView == null) {
							eatGirlView = getLayoutInflater().inflate(
									R.layout.main_girleat, null);
							eatGirlView.setOnClickListener(MainActivity.this);
						}
						ImageView eatGirlEye = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_eye);
						ImageView eatGirlMouth = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_mouth);
						ImageView eatGirlHand = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_leftHand);
						AnimationDrawable eye = (AnimationDrawable) eatGirlEye
								.getDrawable();
						AnimationDrawable mouth = (AnimationDrawable) eatGirlMouth
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) eatGirlHand
								.getDrawable();
						eye.stop();
						mouth.stop();
						hand.stop();
						if(eatGirlBody != null){
							eatGirlBody.setImageDrawable(null);
						}
						eatGirlView.setOnClickListener(null);
						eatGirlEye = null;
						eatGirlMouth = null;
						eatGirlHand= null;
					
						girlSitView.removeAllViews();

					}
				} else {

					String chatmsg = "我吃完饭啦！";
					addChatMsg(isSelf, chatmsg, curTime);

					if (!(SelfInfo.getInstance().getSex().equals("b"))) {
						if (eatBoyView == null) {
							eatBoyView = getLayoutInflater().inflate(
									R.layout.main_boyeat, null);
							eatBoyView.setOnClickListener(MainActivity.this);
						}
						ImageView eatBoyEye = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_eye);
						ImageView eatBoyMouth = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_mouth);
						ImageView eatBoyHand = (ImageView) eatBoyView
								.findViewById(R.id.EatBoy_leftHand);
						AnimationDrawable eye = (AnimationDrawable) eatBoyEye
								.getDrawable();
						AnimationDrawable mouth = (AnimationDrawable) eatBoyMouth
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) eatBoyHand
								.getDrawable();
						eye.stop();
						mouth.stop();
						hand.stop();
						if(eatBoyBody != null){
							eatBoyBody.setImageDrawable(null);
						}
						eatBoyView.setOnClickListener(null);
						eatBoyEye = null;
						eatBoyMouth = null;
						eatBoyHand= null;
						boySitView.removeAllViews();

					} else {
						if (eatGirlView == null) {
							eatGirlView = getLayoutInflater().inflate(
									R.layout.main_girleat, null);
							eatGirlView.setOnClickListener(MainActivity.this);
						}
						ImageView eatGirlEye = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_eye);
						ImageView eatGirlMouth = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_mouth);
						ImageView eatGirlHand = (ImageView) eatGirlView
								.findViewById(R.id.EatGirl_leftHand);
						AnimationDrawable eye = (AnimationDrawable) eatGirlEye
								.getDrawable();
						AnimationDrawable mouth = (AnimationDrawable) eatGirlMouth
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) eatGirlHand
								.getDrawable();
						eye.stop();
						mouth.stop();
						hand.stop();
						if(eatGirlBody != null){
							eatGirlBody.setImageDrawable(null);
						}
						eatGirlView.setOnClickListener(null);
						eatGirlEye = null;
						eatGirlMouth = null;
						eatGirlHand= null;
						girlSitView.removeAllViews();

					}
				}
				break;
			case Protocol.SINGLE_ACTION_LEARN:
				if (isSelf) {

					ActionPacketHandler mAP = new ActionPacketHandler();
					mAP.SendSingleActionEnd(curTime, actionType);
					String chatmsg = "学习完毕！";
					addChatMsg(isSelf, chatmsg, curTime);

					if (SelfInfo.getInstance().getSex().equals("b")) {

						if (readBoyView == null) {
							readBoyView = getLayoutInflater().inflate(
									R.layout.main_boyread, null);
							readBoyView.setOnClickListener(MainActivity.this);
						}

						ImageView readBoyEye = (ImageView) readBoyView
								.findViewById(R.id.ReadBoy_eye);
						ImageView readBoyBook = (ImageView) readBoyView
								.findViewById(R.id.ReadBoy_Book);
						AnimationDrawable eye = (AnimationDrawable) readBoyEye
								.getDrawable();
						AnimationDrawable book = (AnimationDrawable) readBoyBook
								.getDrawable();
						eye.stop();
						book.stop();
						if(readBoyBody != null){
							readBoyBody.setImageDrawable(null);
						}
						readBoyView.setOnClickListener(null);
						readBoyEye = null;
						readBoyBook= null;
						
						boySitView.removeAllViews();

					} else {
						if (readGirlView == null) {
							readGirlView = getLayoutInflater().inflate(
									R.layout.main_girlread, null);
							readGirlView.setOnClickListener(MainActivity.this);
						}
						ImageView readGirlEye = (ImageView) readGirlView
								.findViewById(R.id.ReadGirl_eye);
						ImageView readGirlHand = (ImageView) readGirlView
								.findViewById(R.id.ReadGirl_hand);
						AnimationDrawable eye = (AnimationDrawable) readGirlEye
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) readGirlHand
								.getDrawable();
						eye.stop();
						hand.stop();

						girlSitView.removeAllViews();

					}
				} else {

					String chatmsg = "学习完毕！";
					addChatMsg(isSelf, chatmsg, curTime);

					if (!(SelfInfo.getInstance().getSex().equals("b"))) {
						if (readBoyView == null) {
							readBoyView = getLayoutInflater().inflate(
									R.layout.main_boyread, null);
							readBoyView.setOnClickListener(MainActivity.this);
						}

						ImageView readBoyEye = (ImageView) readBoyView
								.findViewById(R.id.ReadBoy_eye);
						ImageView readBoyBook = (ImageView) readBoyView
								.findViewById(R.id.ReadBoy_Book);
						AnimationDrawable eye = (AnimationDrawable) readBoyEye
								.getDrawable();
						AnimationDrawable book = (AnimationDrawable) readBoyBook
								.getDrawable();
						eye.stop();
						book.stop();
						if(readBoyBody != null){
							readBoyBody.setImageDrawable(null);
						}
						readBoyView.setOnClickListener(null);
						readBoyEye = null;
						readBoyBook= null;
						boySitView.removeAllViews();

					} else {
						if (readGirlView == null) {
							readGirlView = getLayoutInflater().inflate(
									R.layout.main_girlread, null);
							readGirlView.setOnClickListener(MainActivity.this);
						}
						ImageView readGirlEye = (ImageView) readGirlView
								.findViewById(R.id.ReadGirl_eye);
						ImageView readGirlHand = (ImageView) readGirlView
								.findViewById(R.id.ReadGirl_hand);
						AnimationDrawable eye = (AnimationDrawable) readGirlEye
								.getDrawable();
						AnimationDrawable hand = (AnimationDrawable) readGirlHand
								.getDrawable();
						eye.stop();
						hand.stop();
						if(readGirlBody != null){
							readGirlBody.setImageDrawable(null);
						}
						readGirlView.setOnClickListener(null);
						readGirlEye = null;
						readGirlHand= null;
						girlSitView.removeAllViews();
					}

				}
				break;

			case Protocol.SINGLE_ACTION_SLEEP:
				if (isSelf) {
					
					ActionPacketHandler mAP = new ActionPacketHandler();
					mAP.SendSingleActionEnd(curTime, actionType);
					String chatmsg = "我起床啦！";
					addChatMsg(isSelf, chatmsg, curTime);
					this.sleepRL.setOnClickListener(null);
					if (SelfInfo.getInstance().getSex().equals("b")) {
						AnimationDrawable sleepBubble = (AnimationDrawable) boysleepBubbleView
								.getDrawable();
						sleepBubble.stop();

						boysleepBubbleView.setVisibility(View.GONE);
						MainActivity.this.boyView.setVisibility(View.VISIBLE);

					} else {
						AnimationDrawable sleepBubble = (AnimationDrawable) girlsleepBubbleView
								.getDrawable();
						sleepBubble.stop();

						girlsleepBubbleView.setVisibility(View.GONE);
						MainActivity.this.girlView.setVisibility(View.VISIBLE);

					}

				} else {
					
					if (!(SelfInfo.getInstance().getSex().equals("b"))) {
						AnimationDrawable sleepBubble = (AnimationDrawable) boysleepBubbleView
								.getDrawable();
						sleepBubble.stop();

						boysleepBubbleView.setVisibility(View.GONE);
						MainActivity.this.boyView.setVisibility(View.VISIBLE);

					} else {
						AnimationDrawable sleepBubble = (AnimationDrawable) girlsleepBubbleView
								.getDrawable();
						sleepBubble.stop();

						girlsleepBubbleView.setVisibility(View.GONE);
						MainActivity.this.girlView.setVisibility(View.VISIBLE);

					}

					String chatmsg = "我起床啦！";
					addChatMsg(isSelf, chatmsg, curTime);

				}
				break;
			default:
				break;
			}
		}
	}

	public void addChatMsg(boolean isself, String content, String date) {
		String account = "";

		if (isself) {
			account = SelfInfo.getInstance().getAccount();
			Database.getInstance(getApplicationContext()).addChat(account,
					date, Protocol.Sended + "", content,
					Protocol.VALUE_RIGHT_ACTION, "0");
		} else {
			account = SelfInfo.getInstance().getTarget();
			Database.getInstance(getApplicationContext()).addChat(account,
					date, Protocol.Sended + "", content,
					Protocol.VALUE_LEFT_ACTION, "0");
		}
	}

	private void showAddActionDialog() {
		View textEntryView = getLayoutInflater().inflate(
				R.layout.addactiondialog, null);
		final EditText conET = (EditText) textEntryView
				.findViewById(R.id.messageEt);

		actionDialog = new com.minius.ui.CustomDialog.Builder(MainActivity.this);
		actionDialog.setTitle("添加自定义状态");
		actionDialog.setContentView(textEntryView);
		actionDialog.setMessage(null);
		actionDialog.setPositiveButton("确定", new View.OnClickListener() {
			public void onClick(View v) {
				// 确定添加自定义动作，等待服务器确认后保存数据库
				String content = conET.getText().toString().trim();
				if (content.length() > 0 && content.length() <= 4) {
					Database db = Database.getInstance(getApplicationContext());
					String typeId = db.getAddActionId();
					ActionPacketHandler mAP = new ActionPacketHandler();
					mAP.addSelfCustomActionWithType(typeId, content);

				} else {
					// TODO
				}
				if (actionDialog.getDialog() != null)
					actionDialog.getDialog().dismiss();
			}

		});
		actionDialog.setNegativeButton("取消", new View.OnClickListener() {
			public void onClick(View v) {
				if (actionDialog.getDialog() != null)
					actionDialog.getDialog().dismiss();
			}

		});

		actionDialog.create().show();

		conET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (conET.getText().toString().trim().length() > 4) {
					actionDialog.setTitleHint("自定义状态最多四个字哦");
					actionDialog.showHint(true);
					actionDialog.setPositiveButtonEnable(false);
				} else if (conET.getText().toString().trim().length() <= 0) {
					actionDialog.setTitleHint("自定义状态不能为空哦");
					actionDialog.showHint(true);
					actionDialog.setPositiveButtonEnable(false);
				} else if (isContainSpace(conET.getText().toString().trim())) {

					actionDialog.setTitleHint("自定义状态不能有空格哦");
					actionDialog.showHint(true);
					actionDialog.setPositiveButtonEnable(false);
				} else {
					actionDialog.setTitleHint("真乖^^");
					// actionDialog.showHint(true);

					actionDialog.setPositiveButtonEnable(true);
				}

			}
		});

	}

	public boolean isContainSpace(String text) {
		int len = text.length();
		for (int i = 0; i < len; i++) {
			if (Character.isWhitespace(text.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	private void showModifyActionDialog(final String content,
			final String typeId) {

		View textEntryView = getLayoutInflater().inflate(
				R.layout.addactiondialog, null);
		final EditText conET = (EditText) textEntryView
				.findViewById(R.id.messageEt);
		conET.setText(content.trim());
		actionDialog = new com.minius.ui.CustomDialog.Builder(MainActivity.this);
		actionDialog.setTitle("修改自定义状态");
		actionDialog.setContentView(textEntryView);
		actionDialog.setMessage(null);
		actionDialog.setPositiveButton("确定", new View.OnClickListener() {
			public void onClick(View v) {
				String content = conET.getText().toString().trim();
				if (content.length() <= 4
						&& conET.getText().toString().trim().length() > 0) {

					ActionPacketHandler mAP = new ActionPacketHandler();
					mAP.updateSelfCustomActionWithType(typeId, content);

				} else {
					// TODO
				}
				if (actionDialog.getDialog() != null)
					actionDialog.getDialog().dismiss();
			}

		});
		actionDialog.setNegativeButton("取消", new View.OnClickListener() {
			public void onClick(View v) {
				if (actionDialog.getDialog() != null)
					actionDialog.getDialog().dismiss();
			}

		});
		actionDialog.setNeutralButton("删除", new View.OnClickListener() {
			public void onClick(View v) {
				ActionPacketHandler mAP = new ActionPacketHandler();
				mAP.deleteSelfCustomActionWithType(typeId);
				if (actionDialog.getDialog() != null)
					actionDialog.getDialog().dismiss();
			}

		});
		actionDialog.create().show();

		// final TextView hint
		// =(TextView)textTitle.findViewById(R.id.titlehint);

		conET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (conET.getText().toString().trim().length() > 4) {
					actionDialog.setTitleHint("自定义状态最多四个字哦");
					actionDialog.showHint(true);
					actionDialog.setPositiveButtonEnable(false);
				} else if (conET.getText().toString().trim().length() <= 0) {
					actionDialog.setTitleHint("自定义状态不能为空哦");
					actionDialog.showHint(true);
					actionDialog.setPositiveButtonEnable(false);
				} else if (isContainSpace(conET.getText().toString().trim())) {

					actionDialog.setTitleHint("自定义状态不能有空格哦");
					actionDialog.showHint(true);
					actionDialog.setPositiveButtonEnable(false);
				} else {
					actionDialog.setTitleHint("真乖^^");
					// actionDialog.showHint(false);

					actionDialog.setPositiveButtonEnable(true);
				}

			}
		});

	}

	public void addBoyChatBubbleView(String status) {

		int statusType = -1;

		try {
			statusType = Integer.parseInt(status);
		} catch (Exception e) {

			statusType = -1;
		}
		if (boyChatbubbleflag == null) {
			boyChatbubbleflag = new ImageButton(this);

		} else {
			ViewGroup oldParent = (ViewGroup) boyChatbubbleflag.getParent();
			if (oldParent != null) {
				oldParent.removeView(boyChatbubbleflag);
			}
			boyChatbubbleflag.setVisibility(View.VISIBLE);
		}
		switch (statusType) {
		case Protocol.SINGLE_ACTION_EAT:
		case Protocol.SINGLE_ACTION_LEARN:

			boyChatbubbleflag.setOnClickListener(MainActivity.this);
			boyChatbubbleflag
					.setBackgroundResource(R.drawable.actionboychattoleft);
			RelativeLayout.LayoutParams btParams1 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT); // 设置按钮的宽度和高度
			btParams1.addRule(RelativeLayout.RIGHT_OF, R.id.boy_sit_view);
			btParams1.addRule(RelativeLayout.ALIGN_TOP, R.id.boy_sit_view);
			mainTopRL.addView(boyChatbubbleflag, btParams1);
			break;

		case Protocol.SINGLE_ACTION_SLEEP:
//			boyChatbubbleflag.setOnClickListener(MainActivity.this);
//			boyChatbubbleflag
//					.setBackgroundResource(R.drawable.actionboychattoright);
//			RelativeLayout.LayoutParams btParams2 = new RelativeLayout.LayoutParams(
//					ViewGroup.LayoutParams.WRAP_CONTENT,
//					ViewGroup.LayoutParams.WRAP_CONTENT); // 设置按钮的宽度和高度
//			btParams2.addRule(RelativeLayout.LEFT_OF, R.id.sleep_boybubble);
//			btParams2.addRule(RelativeLayout.ALIGN_TOP, R.id.sleep_boybubble);
//			mainTopRL.addView(boyChatbubbleflag, btParams2);

			break;
		case Protocol.SINGLE_ACTION_ANGRY:
		case Protocol.SINGLE_ACTION_MISS:
			boyChatbubbleflag.setOnClickListener(MainActivity.this);
			boyChatbubbleflag
					.setBackgroundResource(R.drawable.actionboychattoleft);
			RelativeLayout.LayoutParams btParams3 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT); // 设置按钮的宽度和高度
			btParams3.addRule(RelativeLayout.RIGHT_OF, R.id.stand_boy_view);
			btParams3.addRule(RelativeLayout.ALIGN_TOP, R.id.stand_boy_view);
			mainTopRL.addView(boyChatbubbleflag, btParams3);
			break;

		case Protocol.SINGLE_ACTION_CUSTOM1:

		case Protocol.SINGLE_ACTION_CUSTOM2:

		case Protocol.SINGLE_ACTION_CUSTOM3:

		case Protocol.SINGLE_ACTION_CUSTOM4:

		case Protocol.SINGLE_ACTION_CUSTOM5:
			boyChatbubbleflag.setOnClickListener(MainActivity.this);
			boyChatbubbleflag
					.setBackgroundResource(R.drawable.actionboychattoleft);
			RelativeLayout.LayoutParams btParams40 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT); // 设置按钮的宽度和高度
			btParams40.addRule(RelativeLayout.RIGHT_OF, R.id.boycustomActionRL);
			btParams40.addRule(RelativeLayout.ALIGN_TOP, R.id.boycustomActionRL);
			mainTopRL.addView(boyChatbubbleflag, btParams40);
			break;

		default:
			boyChatbubbleflag.setOnClickListener(MainActivity.this);
			boyChatbubbleflag
					.setBackgroundResource(R.drawable.actionboychattoleft);
			RelativeLayout.LayoutParams btParams4 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT); // 设置按钮的宽度和高度
			btParams4.addRule(RelativeLayout.RIGHT_OF, R.id.MyView);
			btParams4.addRule(RelativeLayout.ALIGN_TOP, R.id.MyView);
			mainTopRL.addView(boyChatbubbleflag, btParams4);
			break;
		}

	}

	public void addGirlChatBubbleView(String status) {

		int statusType = -1;

		try {
			statusType = Integer.parseInt(status);
		} catch (Exception e) {

			statusType = -1;
		}
		if (girlChatbubbleflag == null) {
			girlChatbubbleflag = new ImageButton(this);

		} else {
			ViewGroup oldParent = (ViewGroup) girlChatbubbleflag.getParent();
			if (oldParent != null) {
				oldParent.removeView(girlChatbubbleflag);
			}
			girlChatbubbleflag.setVisibility(View.VISIBLE);
		}
		switch (statusType) {
		case Protocol.SINGLE_ACTION_EAT:
		case Protocol.SINGLE_ACTION_LEARN:

			girlChatbubbleflag.setOnClickListener(MainActivity.this);
			girlChatbubbleflag
					.setBackgroundResource(R.drawable.actiongirlchattoright);
			RelativeLayout.LayoutParams btParams5 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT); // 设置按钮的宽度和高度
			btParams5.addRule(RelativeLayout.LEFT_OF, R.id.girl_sit_view);
			btParams5.addRule(RelativeLayout.ALIGN_TOP, R.id.girl_sit_view);
			mainTopRL.addView(girlChatbubbleflag, btParams5);
			break;

		case Protocol.SINGLE_ACTION_SLEEP:
//			girlChatbubbleflag.setOnClickListener(MainActivity.this);
//			girlChatbubbleflag
//					.setBackgroundResource(R.drawable.actiongirlchattoright);
//			RelativeLayout.LayoutParams btParams6 = new RelativeLayout.LayoutParams(
//					ViewGroup.LayoutParams.WRAP_CONTENT,
//					ViewGroup.LayoutParams.WRAP_CONTENT); // 设置按钮的宽度和高度
//			btParams6.addRule(RelativeLayout.LEFT_OF, R.id.sleep_girlbubble);
//			btParams6.addRule(RelativeLayout.ALIGN_TOP, R.id.sleep_girlbubble);
//			mainTopRL.addView(girlChatbubbleflag, btParams6);
			break;
		// boysleepBubbleView

		case Protocol.SINGLE_ACTION_ANGRY:
		case Protocol.SINGLE_ACTION_MISS:
			girlChatbubbleflag.setOnClickListener(MainActivity.this);
			girlChatbubbleflag
					.setBackgroundResource(R.drawable.actiongirlchattoright);
			RelativeLayout.LayoutParams btParams7 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT); // 设置按钮的宽度和高度
			btParams7.addRule(RelativeLayout.LEFT_OF, R.id.stand_girl_view);
			btParams7.addRule(RelativeLayout.ALIGN_TOP, R.id.stand_girl_view);
			mainTopRL.addView(girlChatbubbleflag, btParams7);
			break;
		case Protocol.SINGLE_ACTION_CUSTOM1:

		case Protocol.SINGLE_ACTION_CUSTOM2:

		case Protocol.SINGLE_ACTION_CUSTOM3:

		case Protocol.SINGLE_ACTION_CUSTOM4:

		case Protocol.SINGLE_ACTION_CUSTOM5:
			girlChatbubbleflag.setOnClickListener(MainActivity.this);
			girlChatbubbleflag
					.setBackgroundResource(R.drawable.actiongirlchattoleft);
			RelativeLayout.LayoutParams btParams10 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT); // 设置按钮的宽度和高度
			btParams10.addRule(RelativeLayout.RIGHT_OF, R.id.boycustomActionRL);
			btParams10.addRule(RelativeLayout.ABOVE, R.id.boycustomActionRL);
			mainTopRL.addView(girlChatbubbleflag, btParams10);
			break;
		default:
			girlChatbubbleflag.setOnClickListener(MainActivity.this);
			girlChatbubbleflag
					.setBackgroundResource(R.drawable.actiongirlchattoright);
			RelativeLayout.LayoutParams btParams8 = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT); // 设置按钮的宽度和高度
			btParams8.addRule(RelativeLayout.LEFT_OF, R.id.GirlView);
			btParams8.addRule(RelativeLayout.ALIGN_TOP, R.id.GirlView);
			mainTopRL.addView(girlChatbubbleflag, btParams8);
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		this.curBoyView.setVisibility(View.VISIBLE);
		this.curBoyView.setOnClickListener(MainActivity.this);
		this.curGirlView.setVisibility(View.VISIBLE);
		this.curGirlView.setOnClickListener(MainActivity.this);
	}
	
	//在主界面按返回键表示退出
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		/*//“退出”选项
    		if(mExitPopup == null) {
    			
				mExitPopup = new ExitPopup(MainActivity.this,"mainexit",
						exitOnClick);
				mExitPopup.showAtLocation(
						getWindow().getDecorView(),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

			} else if (!(mExitPopup.isShowing())) {
				mExitPopup.showAtLocation(
						mainFL,
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			} else {
				mExitPopup.dismiss();
			}
			*/
    		
    		//不提供“退出”选项，转到后台运行
    		moveTaskToBack(true);
            return true;
    	}
    	return super.onKeyDown(keyCode, event);
    
	}
	private OnClickListener exitOnClick = new OnClickListener() {
		public void onClick(View v) {
			mExitPopup.dismiss();
			switch (v.getId()) {
			case R.id.exitapp:
				//退出前停心跳
				UserPacketHandler mReq = new UserPacketHandler();
		      	HeartPacketHandler.getInstance().stopHeart();
		      	
				mReq.Logout();
				SelfInfo.getInstance().setDefault();
				SelfInfo.getInstance().setOnline(false);//下线
				GlobalApplication.getInstance().setCommonDefault();
				GlobalApplication.getInstance().setTargetDefault();
				if (receiver != null) {
					MainActivity.this.unregisterReceiver(receiver);
					receiver = null;
				}
				AsynSocket.getInstance().closeSocket();
				GlobalApplication.getInstance().destoryBimap();
//				Database.getInstance(getApplicationContext()).closeDatabase();
				GlobalApplication.getInstance().AppExit();
				try {
					//让该接收的数据先储存好
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				InitFigureAppDrawable.getInstance().destoryAll();
				CommonBitmap.getInstance().clearCache();
				if(imageLoader != null){
				imageLoader.clearDiskCache();
				imageLoader.clearMemoryCache();
				imageLoader.stop();
				imageLoader =null;
				}
			
//				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
				break;
			
			default:
				break;
			}
		}

	};
	private ExitPopup mExitPopup = null;

}
