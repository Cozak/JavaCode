package com.minus.lovershouse.singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.baidu.frontia.FrontiaApplication;
import com.minus.table.GalleryTable;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.CrashHandler;
import com.minus.xsocket.handler.HeartPacketHandler;
import com.minus.xsocket.handler.UserPacketHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.message.PushAgent;
import com.minus.lovershouse.BuildConfig;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.RegisterActivity;
import com.minus.lovershouse.bitmap.util.Utils;
import com.minus.lovershouse.setting.LockPatternUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class GlobalApplication extends FrontiaApplication {

	private static Stack<Activity> activityStack;
	private static GlobalApplication singleton;
	public static final String PATH_ERROR_LOG = Environment.getExternalStorageDirectory()  
            + "/LoverHouse/minius.log";
	
	private LockPatternUtils mLockPatternUtils;


	

	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
		// PushAgent mPushAgent = PushAgent.getInstance(this);
		// mPushAgent.setDebugMode(true);
		initImageLoader(getApplicationContext());
		mLockPatternUtils = new LockPatternUtils(this);
//		if (BuildConfig.DEBUG) {
//			Utils.enableStrictMode();
//		}
		// ȫ���쳣����
		CrashHandler mCH = new  CrashHandler(this);
		Thread.setDefaultUncaughtExceptionHandler(mCH);
	}

	public LockPatternUtils getLockPatternUtils() {
		return mLockPatternUtils;
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		// ImageLoaderConfiguration config =
		// ImageLoaderConfiguration.createDefault(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
//				.writeDebugLogs() // Remove for release app
				.build();

		// new ImageLoaderConfiguration.Builder(context)
		// .threadPriority(Thread.NORM_PRIORITY - 2)
		// .denyCacheImageMultipleSizesInMemory()
		// .tasksProcessingOrder(QueueProcessingType.LIFO)
		// .build();
		ImageLoader.getInstance().init(config);
	}

	
//	��ϵͳ�ڴ治�㣬���к�̨�������ȼ�Ϊbackground�Ľ��̣�����ָ��̨���еĽ��̣�
//	����ɱ��ʱ��ϵͳ�����OnLowMemory��
	@Override
	public void onLowMemory() {
		
		super.onLowMemory();
		if(!(isAppOnForeground())){
			//�������ں�̨�����յ�low�Ļص�ʱ�ų�����
			//�˳�ǰͣ����
			UserPacketHandler mReq = new UserPacketHandler();
	      	HeartPacketHandler.getInstance().stopHeart();
	      	
			mReq.Logout();
			SelfInfo.getInstance().setDefault();
			SelfInfo.getInstance().setOnline(false);//����
			setCommonDefault();
			setTargetDefault();
			
			AsynSocket.getInstance().closeSocket();
			this.destoryBimap();
			 AppExit();
			try {
				//�øý��յ������ȴ����
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
		}
	}
//	����Android 4.0֮���ṩ��API��ϵͳ����ݲ�ͬ���ڴ�״̬���ص���
	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		super.onTrimMemory(level);
	}
	
	public GlobalApplication() {
		super();
		this.setCommonDefault();
		this.setTargetDefault();
		this.setControlDefault();
		

	}

	// Returns the application instance
	public static GlobalApplication getInstance() {
		if (singleton == null) {
			singleton = new GlobalApplication();
		}
		return singleton;
	}

	/**
	 * add Activity ���Activity��ջ
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}

		activityStack.add(activity);

	}

	public boolean isActivityEmpty() {

		return activityStack.isEmpty();
	}

	/**
	 * get current Activity ��ȡ��ǰActivity��ջ�����һ��ѹ��ģ�
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();

		return activity;
	}

	
	/**
	 * get current Activity java name ��ȡ��ǰActivity��java��
	 */
	// public String currentActivityname() {
	// Activity activity = activityStack.lastElement();
	// String temp=activity.toString();
	//
	// return temp.substring(temp.lastIndexOf(".")+1, temp.indexOf("@"));
	// }
	/**
	 * ������ǰActivity��ջ�����һ��ѹ��ģ�
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * ����ָ����Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * ��ǰactivityջ�Ƿ���ָ����activity
	 */
	public boolean isActivityrun(Class<?> cls) {
		Activity temp = null;
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				temp = activity;
			}
		}
		if (temp != null)
			return true;
		else
			return false;
	}

	/**
	 * ����ָ��������Activity
	 */
	public void finishActivity(Class<?> cls) {
		Activity temp = null;
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				temp = activity;
			}
		}
		if (temp != null)
			finishActivity(temp);
	}

	/**
	 * ������register������Activity�������˳���¼
	 */
	public void finishOtherActivity() {
		Activity temp = null;
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)){
				if (RegisterActivity.class.equals(activityStack.get(i).getClass())) 
						temp = activityStack.get(i);
				else
					activityStack.get(i).finish();
			}
				
		}
		
		activityStack.clear();
		if(temp!=null)
			activityStack.add(temp);
			
	}
	
	/**
	 * ��������Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * �˳�Ӧ�ó���
	 */
	public void AppExit() {
		try {
			finishAllActivity();
		} catch (Exception e) {

		}
	}

	/*
	 * �жϵ�ǰactivity�Ƿ����������е�
	 */
	public boolean isRunning(String packageName) {
		// List<RunningTaskInfo> appTask = activityManager.getRunningTasks(1);

		if (activityStack != null && activityStack.size() > 0) {
			for (int i = 0; i < activityStack.size(); i++) {
				if (activityStack.get(i).toString().contains(packageName))
					return true;

			}
		}
		return false;

	}

	/**
	 * �����Ƿ���ǰ̨����
	 * 
	 * @return
	 */
	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device

		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}
	
//	public boolean isBackgroundRunning() {
//		ActivityManager activityManager = (ActivityManager) getApplicationContext()
//				.getSystemService(Context.ACTIVITY_SERVICE);
//		String processName = getApplicationContext().getPackageName();
//
//		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//
//		if (activityManager == null) return true;
//		// get running application processes
//		List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
//		for (ActivityManager.RunningAppProcessInfo process : processList) {
//		if (process.processName.startsWith(processName)) {
//		boolean isForeground =( process.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && process.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE);
//		boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
//		if (isLockedState) return true;
//		if(isForeground) return false;
//		
//		}
//		}
//		return true;
//		}

	// ��������ģʽ

	@SuppressWarnings("deprecation")
	public int isSoundViberate() {
		AudioManager volMgr = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		switch (volMgr.getRingerMode()) {// ��ȡϵͳ���õ�����ģʽ
		case AudioManager.RINGER_MODE_SILENT:// ����ģʽ��ֵΪ0����ʱ���𶯣�������
			return 0;
		case AudioManager.RINGER_MODE_VIBRATE:// ��ģʽ��ֵΪ1����ʱ���𶯣�������
			return 1;
		case AudioManager.RINGER_MODE_NORMAL:// ����ģʽ��ֵΪ2�������������1_���嵫���𶯣�2_����+��

			if (volMgr.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER) == AudioManager.VIBRATE_SETTING_OFF) {
				// ����
				return 2;
			} else if (volMgr
					.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER) == AudioManager.VIBRATE_SETTING_ONLY_SILENT) {
				// ֻ�ھ���ʱ��
				return 2;
			} else {
				// ��
				return 3;
			}
		default:
			return 3;
		}
	}

	public boolean isRegVisible() {
		return isRegVisible;
	}

	public void setRegVisible(boolean isRegVisible) {
		this.isRegVisible = isRegVisible;
	}
	
	public boolean isLoginVisible() {
		return isLoginVisible;
	}

	public void setLoginVisible(boolean isLoginVisible) {
		this.isLoginVisible = isLoginVisible;
	}
	
	public boolean isMainVisible() {
		return isMainVisible;
	}

	public void setMainVisible(boolean isMainVisible) {
		this.isMainVisible = isMainVisible;
	}

	public boolean isDiaryVisible() {
		return isDiaryVisible;
	}

	public void setDiaryVisible(boolean isDiaryVisible) {
		this.isDiaryVisible = isDiaryVisible;
	}

	public boolean isChatVisible() {
		return isChatVisible;
	}

	public void setChatVisible(boolean isChatVisible) {
		this.isChatVisible = isChatVisible;
	}

	public boolean isAlbumVisible() {
		return isAlbumVisible;
	}

	public void setAlbumVisible(boolean isAlbumVisible) {
		this.isAlbumVisible = isAlbumVisible;
	}

	// common info method
	public void setCommon(String mo, String fri, String wit, String hs,
			String ls, String fp) {
		this.motion = mo;
		this.friends = fri;
		this.witness = wit;
		this.houseStyle = hs;
		this.lightState = ls;
		this.firstPicture = fp;
	}

	public void setCommonDefault() {
		String def = String.format("%c", Protocol.DEFAULT);

		motion = def;
		witness = def;
		friends = def;
		houseStyle = Protocol.blueHouse;
		this.lightState = def;
		this.firstPicture = def;
	}

	// target info method
	public void setTarInfo(String acc, String se, String bn, String appear,
			String st, String sn, String tar) {

		this.tiAcc = acc;
		this.tiSex = se;
		this.tiBigName = bn;
		this.tiAppearance = appear;
		this.tiStatus = st;
		this.tiSmallName = sn;
		this.tiTarget = tar;
	}

	public void setTargetDefault() {

		String def = String.format("%c", Protocol.DEFAULT);
		this.tiAcc = def;
		this.tiSex = "g";
		this.tiBigName = def;
		this.tiAppearance = String.format("%c%c%c%c", Protocol.DEFAULT,
				Protocol.DEFAULT, Protocol.DEFAULT, Protocol.DEFAULT);
		this.tiStatus = Protocol.ActionEnd + "";
		this.tiSmallName = def;
		this.tiTarget = def;
		this.tiTargetHeadPhoPath = def;

	}

	// public String getTempAccount() {
	// return tempAccount;
	// }
	// public void setTempAccount(String tempAccount) {
	// this.tempAccount = tempAccount;
	// }
	// public String getTempPwd() {
	// return tempPwd;
	// }
	// public void setTempPwd(String tempPwd) {
	// this.tempPwd = tempPwd;
	// }

	// public boolean isLoginAppear() {
	// return isLoginAppear;
	// }

	// public void setLoginAppear(boolean isLoginAppear) {
	// this.isLoginAppear = isLoginAppear;
	// }

	// ----------------------------------------------------------------------------------------------

	public String getMotion() {
		return motion;
	}

	public void setMotion(String motion) {
		this.motion = motion;
	}

	public String getFriends() {
		return friends;
	}

	public void setFriends(String friends) {
		this.friends = friends;
	}

	public String getWitness() {
		return witness;
	}

	public void setWitness(String witness) {
		this.witness = witness;
	}

	public String getHouseStyle() {
		return houseStyle;
	}

	public void setHouseStyle(String houseStyle) {
		this.houseStyle = houseStyle;
	}

	public String getLightState() {
		return lightState;
	}

	public void setLightState(String lightState) {
		this.lightState = lightState;
	}

	public String getFirstPicture() {
		return firstPicture;
	}

	public void setFirstPicture(String firstPicture) {
		this.firstPicture = firstPicture;
	}

	public String getModifyPwd() {
		return modifyPwd;
	}

	public void setModifyPwd(String modifyPwd) {
		this.modifyPwd = modifyPwd;
	}

	public String getModifySex() {
		return modifySex;
	}

	public void setModifySex(String modifySex) {
		this.modifySex = modifySex;
	}

	public String getModifyBigname() {
		return modifyBigname;
	}

	public void setModifyBigname(String modifyBigname) {
		this.modifyBigname = modifyBigname;
	}

	public String getModifyAppearance() {
		return modifyAppearance;
	}

	public void setModifyAppearance(String modifyAppearance) {
		this.modifyAppearance = modifyAppearance;
	}

	public String getModifyStatus() {
		return modifyStatus;
	}

	public void setModifyStatus(String modifyStatus) {
		this.modifyStatus = modifyStatus;
	}

	public String getModifySmallMame() {
		return modifySmallMame;
	}

	public void setModifySmallMame(String modifySmallMame) {
		this.modifySmallMame = modifySmallMame;
	}

	public String getTiAcc() {
		return tiAcc;
	}

	public void setTiAcc(String tiAcc) {
		this.tiAcc = tiAcc;
	}

	public String getTiSex() {
		return tiSex;
	}

	public void setTiSex(String tiSex) {
		this.tiSex = tiSex;
	}

	public String getTiBigName() {
		return tiBigName;
	}

	public void setTiBigName(String tiBigName) {
		this.tiBigName = tiBigName;
	}

	public String getTiAppearance() {
		return tiAppearance;
	}

	public void setTiAppearance(String tiAppearance) {
		this.tiAppearance = tiAppearance;
	}

	public String getTiStatus() {
		return tiStatus;
	}

	public void setTiStatus(String tiStatus) {
		this.tiStatus = tiStatus;
	}

	public String getTiSmallName() {
		return tiSmallName;
	}

	public void setTiSmallName(String tiSmallName) {
		this.tiSmallName = tiSmallName;
	}

	public String getTiTarget() {
		return tiTarget;
	}

	public void setTiTarget(String tiTarget) {
		this.tiTarget = tiTarget;
	}

	public String getTiTargetHeadPhoPath() {
		return tiTargetHeadPhoPath;
	}

	public void setTiTargetHeadPhoPath(String tiTargetHeadPhoPath) {
		this.tiTargetHeadPhoPath = tiTargetHeadPhoPath;
	}

	public HashMap<String, String> getTarCustomActionList() {
		return tarCustomActionList;
	}

	public void setTarCustomActionList(
			HashMap<String, String> tarCustomActionList) {
		this.tarCustomActionList = tarCustomActionList;
	}

	// public int getUploadDiaryLastModifyTimeState() {
	// return UploadDiaryLastModifyTimeState;
	// }
	// public void setUploadDiaryLastModifyTimeState(int
	// uploadDiaryLastModifyTimeState) {
	// UploadDiaryLastModifyTimeState = uploadDiaryLastModifyTimeState;
	// }

	// public String getDiaryIniDate() {
	// return diaryIniDate;
	// }
	//
	//
	//
	//
	//
	// public void setDiaryIniDate(String diaryIniDate) {
	// this.diaryIniDate = diaryIniDate;
	// }
	//
	//
	//
	//
	//
	// public String getDiaryTitle() {
	// return diaryTitle;
	// }
	//
	//
	//
	//
	//
	// public void setDiaryTitle(String diaryTitle) {
	// this.diaryTitle = diaryTitle;
	// }
	//
	//
	//
	//
	//
	// public String getDiaryArticle() {
	// return diaryArticle;
	// }
	//
	//
	//
	//
	//
	// public void setDiaryArticle(String diaryArticle) {
	// this.diaryArticle = diaryArticle;
	// }
	//

	// public LayerDrawable getDiaryHeadLayerDrawable() {
	// return diaryHeadLayerDrawable;
	// }
	//
	//
	//
	//
	//
	// public void setDiaryHeadLayerDrawable(LayerDrawable
	// diaryHeadLayerDrawable) {
	// this.diaryHeadLayerDrawable = diaryHeadLayerDrawable;
	// }

//	public Bitmap getHeadPicBm() {
//		return headPicBm;
//	}
//
//	public void setHeadPicBm(Bitmap headPicBm) {
//		this.headPicBm = headPicBm;
//	}

	public Bitmap getTarHeadPicBm() {
		return tarHeadPicBm;
	}

	public void setTarHeadPicBm(Bitmap tarHeadPicBm) {
		this.tarHeadPicBm = tarHeadPicBm;
	}

	

	public void destoryBimap() {

		if (tarHeadPicBm != null && !tarHeadPicBm.isRecycled()) {
			tarHeadPicBm.recycle();
			tarHeadPicBm = null;
		}
	
	}

	public String getMyReadTime() {
		return MyReadTime;
	}

	public void setMyReadTime(String myReadTime) {
		MyReadTime = myReadTime;
	}

	public String getMsgSend() {
		return MsgSend;
	}

	public void setMsgSend(String msgSend) {
		MsgSend = msgSend;
	}

	public String getIniDate() {
		return IniDate;
	}

	public void setIniDate(String iniDate) {
		IniDate = iniDate;
	}

	public boolean isChatNewInit() {
		return isChatNewInit;
	}

	public void setChatNewInit(boolean isChatNewInit) {
		this.isChatNewInit = isChatNewInit;
	}

	public boolean isDiaryNewInit() {
		return isDiaryNewInit;
	}

	public void setDiaryNewInit(boolean isDiaryNewInit) {
		this.isDiaryNewInit = isDiaryNewInit;
	}

	public boolean isAlbumNewInit() {
		return isAlbumNewInit;
	}

	public void setAlbumNewInit(boolean isAlbumNewInit) {
		this.isAlbumNewInit = isAlbumNewInit;
	}

	public void setControlDefault() {
		this.isAlbumNewInit = false;
		this.isDiaryNewInit = false;
		this.isChatNewInit = false;

	}

	public int getUploadPictureLastModifyTimeState() {
		return UploadPictureLastModifyTimeState;
	}

	public void setUploadPictureLastModifyTimeState(
			int uploadPictureLastModifyTimeState) {
		UploadPictureLastModifyTimeState = uploadPictureLastModifyTimeState;
	}

	public int getUploadPicture() {
		return UploadPicture;
	}

	public void setUploadPicture(int uploadPicture) {
		UploadPicture = uploadPicture;
	}

	public int getPicTempCount() {
		return picTempCount;
	}

	public void setPicTempCount(int picTempCount) {
		this.picTempCount = picTempCount;
	}

	public String getAlbumLastModefyTime() {
		return albumLastModefyTime;
	}

	public void setAlbumLastModefyTime(String albumLastModefyTime) {
		this.albumLastModefyTime = albumLastModefyTime;
	}

	public String getCouple_ActionMsg() {
		return couple_ActionMsg;
	}

	public void setCouple_ActionMsg(String couple_ActionMsg) {
		this.couple_ActionMsg = couple_ActionMsg;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeigh() {
		return screenHeigh;
	}

	public void setScreenHeigh(int screenHeigh) {
		this.screenHeigh = screenHeigh;
	}

	public int getAlbumHeight() {
		return albumHeight;
	}

	public void setAlbumHeight(int albumHeight) {
		this.albumHeight = albumHeight;
	}

	public int getInitPos() {
		return initPos;
	}

	public void setInitPos(int initPos) {
		this.initPos = initPos;
	}

	public List<GalleryTable> getmGT() {
		return mGT;
	}

	public void setmGT(List<GalleryTable> mGT) {
		this.mGT = mGT;
	}
	
	

	public boolean isSelectPic() {
		return isSelectPic;
	}

	public void setSelectPic(boolean isSelectPic) {
		this.isSelectPic = isSelectPic;
	}



	public Uri getFileUri() {
		return fileUri;
	}

	public void setFileUri(Uri fileUri) {
		this.fileUri = fileUri;
	}



	private boolean isRegVisible = false;
	private boolean isLoginVisible = false;
	private boolean isMainVisible = false;
	private boolean isDiaryVisible = false;
	private boolean isChatVisible = false;
	private boolean isAlbumVisible = false;

	// common
	String motion;
	String friends;
	String witness;
	String houseStyle=Protocol.pinkHouseStr;
	String lightState;
	String firstPicture;

	// modify temp
	String modifyPwd;
	String modifySex;
	String modifyBigname;
	String modifyAppearance;
	String modifyStatus;
	String modifySmallMame;

	// target info
	String tiAcc;
	String tiSex;
	String tiBigName;
	String tiAppearance; // �Է���������
	String tiStatus; // �Է���Զ���
	String tiSmallName;
	String tiTarget;
	String tiTargetHeadPhoPath;

	// target custom action
	private HashMap<String, String> tarCustomActionList = new HashMap<String, String>();
	// handle server state diary
	// 0 is fail ,while 1 is succ
	// private int UploadDiaryLastModifyTimeState = 0;
	// edit diary only me can edit or new
	// private String diaryAcc = "";
	// private String diaryAuthor ="";
	// private String diaryIniDate = "";
	// private String diaryTitle ="";
	// private String diaryArticle = "";
	// private LayerDrawable diaryHeadLayerDrawable;
	// chat state
	private String MyReadTime = null;
	private String MsgSend = null;
	private String IniDate = null;

	// �ոյ�½����������Ϣ�����Լ��ط�
	private boolean isChatNewInit = false;
	private boolean isDiaryNewInit = false;
	private boolean isAlbumNewInit = false;
	// ����ͨ�õ�ͷ�� �Լ��� �ͶԷ��� �Լ���ҳ���
//	private Bitmap headPicBm = null;
	private Bitmap tarHeadPicBm = null;


	private int UploadPictureLastModifyTimeState = 0;
	private int UploadPicture = 0;
	private int picTempCount = 0;
	private String albumLastModefyTime = null;

	private String couple_ActionMsg = null;

	// �ֻ��豸����Ϣ
	private int screenWidth = 0;
	private int screenHeigh = 0;
	// Album
	private int albumHeight = 0; // ���ͼƬʵ�ʸ߶�
	private int initPos = 0; // ���ĳ�ʼ��Ƭλ��
	private List<GalleryTable> mGT = null; // ��������Դ����detail activity ���á�
	
	//chat 
	private boolean isSelectPic = false;
	
//	������url
	private Uri fileUri = null;

}
