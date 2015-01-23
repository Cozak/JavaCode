package com.minus.lovershouse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.minus.diary.DiaryActivity;
import com.minus.gallery.GalleryActivity;
import com.minus.lovershouse.BuildConfig;
import com.minus.lovershouse.R;
import com.minus.lovershouse.ChatListFragment.ChatMsgViewAdapter;
import com.minus.lovershouse.ChatListFragment.OnSetListViewListener;
import com.minus.lovershouse.MainActivity.MyReceiver;
import com.minus.lovershouse.bitmap.util.Keys;
import com.minus.lovershouse.bitmap.util.Utils;
import com.minus.lovershouse.enity.ChatMsgEnity;
import com.minus.lovershouse.face.FaceRelativeLayout;
import com.minus.lovershouse.util.ExitPopup;
import com.minus.lovershouse.setting.PasswordActivity;
import com.minus.lovershouse.setting.UnlockGesturePasswordActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.AudioRecorder;
import com.minus.lovershouse.util.FileUtil;
import com.minus.lovershouse.util.RTPullListView;
import com.minus.lovershouse.util.SelectPicPopup;
import com.minus.lovershouse.util.RTPullListView.OnRefreshListener;
import com.minus.sql_interface.Database;
import com.minus.table.ChatTable;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.ChatPacketHandler;
import com.minus.xsocket.handler.ConnectHandler;
import com.minus.xsocket.handler.HeartPacketHandler;
import com.minus.xsocket.handler.UserPacketHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

@SuppressLint("NewApi")
public class ChatActivity extends FragmentActivity implements OnClickListener,
		OnSetListViewListener {
	
	private boolean  isChatActivityVisible = true;
	
//	public boolean isSelectPic = false;
	private boolean isAppBackground = false; //�����Ƿ��ж�������
	
	private ImageView faceKeyBoardSendBtn  = null;
	//emotionSend"android:stackFromBottom="true"
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		if (BuildConfig.DEBUG) {
//			Utils.enableStrictMode();
//		}
		this.setContentView(R.layout.activity_chat);
		GlobalApplication.getInstance().addActivity(this);
		
		if (receiver == null)
			receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Protocol.ACTION_USERPACKET);
		filter.addAction(Protocol.ACTION_CHATPACKET_MSGTEXT_DATA);
		filter.addAction(Protocol.ACTION_CHATPACKET_RECPIC);
		filter.addAction(Protocol.ACTION_CHATPACKET_REMIND_DATA);
		filter.addAction(Protocol.ACTION_CHATPACKET_RECVOICE);
		filter.addAction(Protocol.ACTION_CHATPACKET);
		filter.addAction(Protocol.ACTION_CHATPACKET_MSGRECFINISH);
		filter.addAction(Protocol.ACTION_CHATPACKET_MSGREMIND);
		
		this.registerReceiver(receiver, filter);
		// ����activityʱ���Զ����������
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		
		if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
			final FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.add(R.id.chatlistRl, new ChatListFragment(), TAG);
			ft.commit();
		}
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onCreate in activity  2 ");
		}
		initView();

	}

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		GlobalApplication.getInstance().setChatVisible(false);
		this.isChatActivityVisible = false;
		MobclickAgent.onPause(this);
	}
	
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (!GlobalApplication.getInstance().isAppOnForeground()) {  
            //app �����̨  
			isAppBackground =true;//��¼��ǰ�Ѿ������̨  
		}
	}
	
	public void enterForeGround(){
   	 
    	SharedPreferences mSP = GlobalApplication.getInstance()
				.getSharedPreferences(Protocol.PREFERENCE_NAME,
						Activity.MODE_PRIVATE);
		String lastUser = mSP.getString("LastUser", "");
        if (lastUser.equals("")){
        	//���lastuser��¼��ʱ�򣬾Ͳ���ִ�����������;    
            return;
        }
        
      //�ж��Ƿ�ͷ�����ʧȥ��ϵ�����û�еĻ�����ôֱ�ӷ�һ����������ȥ����;
        if(!(AsynSocket.getInstance().isConnected())){
        	ConnectHandler.getInstance().connectToServer();
		}else{
			 if(SelfInfo.getInstance().isOnline()){
				 try {
					HeartPacketHandler.getInstance().startHeart();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}	    		
		     }else{
		    	 UserPacketHandler mUP = new UserPacketHandler();
		    	 mUP.Login(SelfInfo.getInstance().getAccount(), SelfInfo.getInstance().getPwd());
		    	 
		     }
		}
  

    }
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //��ȡ back��
    		
    		Intent intent = new Intent();
			intent.setClass(ChatActivity.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return true;
    	}
    	return super.onKeyDown(keyCode, event);
    	
	}


	@Override
	protected void onDestroy() {
		if (receiver != null) {
			this.unregisterReceiver(receiver);
			receiver = null;
		}
		//����Activity&��ջ���Ƴ���Activity
				GlobalApplication.getInstance().finishActivity(this);

				if (this.connectTimer != null) {
					this.connectTimer.cancel();
					this.connectTimer = null;
				}

		super.onDestroy();
	}

	@Override
	protected void onResume() {
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)// ǿ������
		{   
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
		} 
		
		this.isChatActivityVisible  = true;
		GlobalApplication.getInstance().setChatVisible(true);
		initData();
		refreshFromServer();
		if(!(GlobalApplication.getInstance().isSelectPic())){
		if(isAppBackground){//�����̨��ָ�
			enterForeGround();
			
			if(SelfInfo.getInstance().isMainInit()){
				String temp = "";
				if(SelfInfo.getInstance().getAccount()!=null)
					temp=SelfInfo.getInstance().getAccount();
				else{
					UserTable selfInfo = Database.getInstance(this).getSelfInfo();
					if(selfInfo!=null)
						temp=selfInfo.getAccount();
				}

				SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(temp
						, Activity.MODE_PRIVATE);
				
				if (mSP.getBoolean("isProtected",false)){
					if(mSP.getBoolean("isNum",true)){
						Intent regIntent =new Intent(this, PasswordActivity.class);
						Bundle regBundle = new Bundle();
				        regBundle.putString("who", "1");
				        regIntent.putExtras(regBundle);
						startActivity(regIntent);
					}else{
						Intent regIntent =new Intent(this, UnlockGesturePasswordActivity.class);
						Bundle regBundle = new Bundle();
				        regBundle.putString("who", "1");
				        regIntent.putExtras(regBundle);
						startActivity(regIntent);
					}
					
				}
			}
		}
		}
	
		isAppBackground=false;
		
		super.onResume();
		MobclickAgent.onResume(this);
		
	}



	private void initView() {
		topRl = (FrameLayout) findViewById(R.id.chat_top);
		bottomRl = (RelativeLayout) findViewById(R.id.rl_bottom);
		mFRl = (FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout);
		faceKeyBoardSendBtn = (ImageView) mFRl.findViewById(R.id.emotionSend);
		faceKeyBoardSendBtn.setOnClickListener(this);
		mVoiceLL = (RelativeLayout) findViewById(R.id.voice_input);
		backTomianImgBtn = (ImageView) findViewById(R.id.btn_back);
		backTomianImgBtn.setOnClickListener(this);
		mBtnSend = (ImageView) findViewById(R.id.iv_send);
		mBtnSend.setOnClickListener(this);
		picBtn = (ImageView) findViewById(R.id.iv_pic);
		picBtn.setOnClickListener(this);
		contentEt = (EditText) findViewById(R.id.et_sendmessage);
		contentEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				if (contentEt.getText().toString().length() >= 0
						&& !(ChatActivity.this.isSend)) {
					ChatActivity.this.isSend = true;
//					left_btn_select
					
					mBtnSend.setImageResource(R.drawable.chatsend);
//					mBtnSend.setText("����");
				}
				if (contentEt.getText().toString().length() == 0) {
					mBtnSend.setImageResource(R.drawable.chat_voice_button);
//					mBtnSend.setText("");
					ChatActivity.this.isSend = false;
				}

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});

		record = (ImageView) this.findViewById(R.id.btn_sendvoice);
		keyboardBtn = (ImageView) this.findViewById(R.id.btn_keyboard);
		keyboardBtn.setOnClickListener(this);

		// ¼��
		record.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (BuildConfig.DEBUG)
						Log.d("chatVoice", "ChatActivity::initView(): begin: record.setOnTouchListener MotionEvent.ACTION_DOWN");
					 if(FileUtil.isSDCardExist()){
				    	  //���sd�������ڣ�����ʾ�û�
						 if (RECODE_STATE != RECORD_ING) {
								// TODO if the voice file is too much .delete some
								// scanOldFile();
//								record.setText("�ɿ�����¼��");
								String timeStamp = new SimpleDateFormat(
										"yyyyMMddHHmmss").format(new Date());
								mr = new AudioRecorder("voicemsg" + timeStamp);
								RECODE_STATE = RECORD_ING;
								showVoiceDialog();
								try {
									if (BuildConfig.DEBUG)
										 Log.d("chatVoice", "ChatActivity::initView(): end: record.setOnTouchListener MotionEvent.ACTION_DOWN, before mr.start()");
									mr.start();
									if (BuildConfig.DEBUG)
										 Log.d("chatVoice", "ChatActivity::initView(): end: record.setOnTouchListener MotionEvent.ACTION_DOWN, after mr.start()");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									//¼���쳣���п�����û��¼��Ȩ��
									recodeTime = -1;
									mr = null;
								}
								recordThread();
							}
					 }else{
						 Toast.makeText(getApplicationContext(), "SD���Ѱγ�������������ʱ����ʹ��", Toast.LENGTH_SHORT).show();	
					 }
					 
					 if (BuildConfig.DEBUG)
						 Log.d("chatVoice", "ChatActivity::initView(): end: record.setOnTouchListener MotionEvent.ACTION_DOWN");
					break;
				case MotionEvent.ACTION_UP:
					if (BuildConfig.DEBUG)
						Log.d("chatVoice", "ChatActivity::initView(): begin: record.setOnTouchListener MotionEvent.ACTION_UP");
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE = RECODE_ED;
						if (recordingDialog.isShowing()) {
							recordingDialog.dismiss();
						}
						try {
							if (mr != null)
								mr.stop();
							voiceValue = 0.0;
						} catch (IOException e) {
							e.printStackTrace();
						}

						//if (recodeTime < 0)
							//;// do nothing. //¼���쳣���п�����û��¼��Ȩ��
						if (recodeTime < MIX_TIME) {
							// ¼��ʱ��̫�̣�ɾ��֮
							if (mr != null) {
								deletevoiceFile(mr.getAudioPath());
								showWarnToast("ʱ��̫��   ¼��ʧ��");
							}
							else
								showWarnToast("����¼��Ȩ��");
//							record.setText("��ס��ʼ¼��");
							RECODE_STATE = RECORD_NO;
						} else {
							// ��ʱ0.5���ӣ������û����ֻ�û��˵�ꣻ
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
//							record.setText("��ס��ʼ¼��");
							sendVoice();
						}
					}

					if (BuildConfig.DEBUG)
						Log.d("chatVoice", "ChatActivity::initView(): begin: record.setOnTouchListener MotionEvent.ACTION_UP");
					break;
				}
				return true;
			}
		});

	}

	// ɾ�������ļ�
	void deletevoiceFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	// ¼����ʱ�߳�
	void recordThread() {
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	// ¼���߳�
	private Runnable ImgThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (RECODE_STATE == RECORD_ING) {
				if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
					imgHandle.sendEmptyMessage(0);
				} else {
					try {
						Thread.sleep(200);
						recodeTime += 0.2;
						if (RECODE_STATE == RECORD_ING) {
							if (mr != null)
								voiceValue = mr.getAmplitude();
							imgHandle.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					// ¼������15���Զ�ֹͣ
					if (RECODE_STATE == RECORD_ING) {
						RECODE_STATE = RECODE_ED;
						if (recordingDialog.isShowing()) {
							recordingDialog.dismiss();
						}
						try {
							mr.stop();
							voiceValue = 0.0;
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						if (recodeTime < 0.6) {
							// ¼��ʱ��̫�̣�ɾ��֮
							if (mr != null) {
								deletevoiceFile(mr.getAudioPath());
								showWarnToast("ʱ��̫��   ¼��ʧ��");
							}
							else
								showWarnToast("����¼��Ȩ��");
//							record.setText("��ס��ʼ¼��");
							RECODE_STATE = RECORD_NO;
						} else {
//							record.setText("��ס��ʼ¼��");
							sendVoice();
						}
					}
					break;
				case 1:
					setDialogImage();
					break;
				default:
					break;
				}

			}
		};
	};

	// ¼��DialogͼƬ��������С�л�
	void setDialogImage() {
		if (voiceValue < 200.0) {
			dialog_img.setImageResource(R.drawable.record_animate_01);
		} else if (voiceValue > 200.0 && voiceValue < 400) {
			dialog_img.setImageResource(R.drawable.record_animate_02);
		} else if (voiceValue > 400.0 && voiceValue < 800) {
			dialog_img.setImageResource(R.drawable.record_animate_03);
		} else if (voiceValue > 800.0 && voiceValue < 1600) {
			dialog_img.setImageResource(R.drawable.record_animate_04);
		} else if (voiceValue > 1600.0 && voiceValue < 3200) {
			dialog_img.setImageResource(R.drawable.record_animate_05);
		} else if (voiceValue > 3200.0 && voiceValue < 5000) {
			dialog_img.setImageResource(R.drawable.record_animate_06);
		} else if (voiceValue > 5000.0 && voiceValue < 7000) {
			dialog_img.setImageResource(R.drawable.record_animate_07);
		} else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_08);
		} else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_09);
		} else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_10);
		} else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_11);
		} else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_12);
		} else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_13);
		} else if (voiceValue > 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_14);
		}
	}

	// ¼��ʱ��ʾDialog
	void showVoiceDialog() {
		recordingDialog = new Dialog(ChatActivity.this, R.style.DialogStyle);
		recordingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		recordingDialog.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		recordingDialog.setContentView(R.layout.recdg);
		dialog_img = (ImageView) recordingDialog.findViewById(R.id.dialog_img);
		recordingDialog.show();
	}

	// ¼���쳣ʱToast��ʾ
	// ¼��ʱ��̫��ʱToast��ʾ
	void showWarnToast(String warnMessage) {
		Toast toast = new Toast(ChatActivity.this);
		LinearLayout linearLayout = new LinearLayout(ChatActivity.this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 20, 20, 20);

		ImageView imageView = new ImageView(ChatActivity.this);
		imageView.setImageResource(R.drawable.voice_to_short); // ͼ��

		TextView mTv = new TextView(ChatActivity.this);
		//mTv.setText("ʱ��̫��   ¼��ʧ��");
		mTv.setText(warnMessage);
		mTv.setTextSize(14);
		mTv.setTextColor(Color.WHITE);// ������ɫ
		// ��ImageView��ToastView�ϲ���Layout��
		linearLayout.addView(imageView);
		linearLayout.addView(mTv);
		linearLayout.setGravity(Gravity.CENTER);// ���ݾ���
		linearLayout.setBackgroundResource(R.drawable.record_bg);// �����Զ���toast�ı���

		toast.setView(linearLayout);
		toast.setGravity(Gravity.CENTER, 0, 0);// ���λ��Ϊ�м� 100Ϊ������100dp
		toast.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			Intent intent = new Intent();
			intent.setClass(ChatActivity.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		case R.id.emotionSend:
			if ((!(ChatActivity.this.isSend))
					&& !(ChatActivity.this.isVoiceMode)) {
				mFRl.setVisibility(View.GONE);
				mVoiceLL.setVisibility(View.VISIBLE);
				ChatActivity.this.isVoiceMode = true;
			} else {
				ChatActivity.this.isSend = false;
				sendText();
			}
			break;
		case R.id.iv_send:

			if ((!(ChatActivity.this.isSend))
					&& !(ChatActivity.this.isVoiceMode)) {
				mFRl.setVisibility(View.GONE);
				mVoiceLL.setVisibility(View.VISIBLE);
				ChatActivity.this.isVoiceMode = true;
			} else {
				ChatActivity.this.isSend = false;
				sendText();
			}

			break;
		case R.id.iv_pic:
			 if(FileUtil.isSDCardExist()){
		    	  //���sd�������ڣ�����ʾ�û�
				
				// no pic dialog before
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(contentEt.getWindowToken(), 0);
					mFRl.hideFaceView();

					// ʵ����SelectPicPopupWindow
					if (mSelectPicPopup == null) {
						mSelectPicPopup = new SelectPicPopup(ChatActivity.this,
								itemsOnClick);
						mSelectPicPopup.setTitle("����ͼƬ");
						mSelectPicPopup.showAtLocation(
								ChatActivity.this.findViewById(R.id.chat),
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��

					} else if (!(mSelectPicPopup.isShowing())) {
						mSelectPicPopup.showAtLocation(
								ChatActivity.this.findViewById(R.id.chat),
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
					} else {
						mSelectPicPopup.dismiss();
					}
			 }else{
		    Toast.makeText(getApplicationContext(), "SD���Ѱγ�����ͼƬ������ʱ����ʹ��", Toast.LENGTH_SHORT).show();
					
			 }

			
			break;
		case R.id.btn_keyboard:
			mFRl.setVisibility(View.VISIBLE);
			mVoiceLL.setVisibility(View.GONE);
			ChatActivity.this.isVoiceMode = false;
			break;

		default:
			break;

		}

	}

	// Ϊ��������ʵ�ּ�����

	private OnClickListener itemsOnClick = new OnClickListener() {
		public void onClick(View v) {
			mSelectPicPopup.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				// create Intent to take a picture and return control to the
				// calling applicatio
				GlobalApplication.getInstance().setSelectPic(true);
				Bundle bundle1 = new Bundle(); 
				bundle1.putInt("isSelectPic",1 );
				
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a
																	// file to
																	// save the
				GlobalApplication.getInstance().setFileUri(fileUri);													// image
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the
																	// image
																	// file name

				// start the image capture Intent
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				break;
			case R.id.btn_pick_photo:
				GlobalApplication.getInstance().setSelectPic(true);
				Intent imgPickItent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(imgPickItent,
						PICK_IMAGE_ACTIVITY_REQUEST_CODE);
				break;
			default:
				break;
			}
		}

	};

	public void initData() {
		this.email = SelfInfo.getInstance().getAccount();

	}

	private void sendText() {

//		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);  
//        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);  
//        r.play();   
		String contString = contentEt.getText().toString();
       if(contString.trim().equals("")) {
    	   Toast.makeText(getApplicationContext(), "���������ܷ��Ϳ���ϢŶ", Toast.LENGTH_LONG).show();
    	   return;
       }

		if (contString.length() > 0) {
			String date = getDate();
			ChatTable entity = new ChatTable();
			entity.setInitdate(date);
            entity.setMsgtype(Protocol.VALUE_RIGHT_TEXT);
			entity.setStatus(Protocol.Sending + "");
			entity.setMessage(contString);

			if (sendTextMessageToServer(contString, date)) {
				mDataArrays.add(entity);
				// consider the efficient ,not update every item ,listview contain
				// an novisible item
				mAdapter.updateView(mListView.getCount() - 1);
				// mAdapter.notifyDataSetChanged();

				contentEt.setText("");

//				mListView.setSelection(mListView.getCount() - 1);
				mListView.setSelection(mListView.getBottom());
				// mListView.setSelection(0);
				//
			}
			else {
				if (BuildConfig.DEBUG)
					Log.d("chatText", "ChatActivity::sendText() sendTextMessageToServer return false");
			}
		}
		isFaceShow = false;
	}

	private void sendVoice() {

		if (recodeTime != 0.0 && mr != null) {
			String currentDate = getDate();
			String voicePath = mr.getAudioPath();
			String recordTime = ((int) (recodeTime + 0.5))+ "";
			ChatTable entity = new ChatTable();
			entity.setInitdate(currentDate);
			entity.setMsgtype(Protocol.VALUE_RIGHT_AUDIO);
			entity.setStatus(Protocol.Sending + "");
			entity.setMessage(voicePath);
			entity.setRecordTime(recordTime);

			mDataArrays.add(entity);
			// consider the efficient ,not update every item ,listview contain
			// an novisible item
			mAdapter.updateView(mListView.getCount() - 1);
			// mAdapter.notifyDataSetChanged();
			mListView.setSelection(mListView.getBottom());
//			mListView.setSelection(mListView.getCount() - 1);
			sendRecordData(currentDate, voicePath,recordTime);

		}
	}
/**
 * ����ת��Ϊ�������ĵĵ�ǰʱ��
 * @return
 */
	private String getDate() {
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
//		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
//		String str = formatter.format(curDate);
		return AppManagerUtil.getCurDateInServer();
	}

	//�յ�����Ϣ����ʱ�򣬾�Ҫ�����������ȡ���һ����Ϣʱ�䣬
	public void responseMsgRemind()
	{
		ChatPacketHandler chatHandler = new ChatPacketHandler();
	    chatHandler.getLastMessageDateWithAccount();
	}

	// �ӷ��������յ����һ����Ϣ��ʱ�䣬����ͱ��صĲ�һ�������������ȡ������
	public void receiveLastMsgDate(String str) {
		String lastDateFromServer = str.substring(8);
		// �������Ϊ�յĻ���˵����������û��������Ϣ.
		if (lastDateFromServer.equals("")) {
			return;
		}
		String lastDateLocal = Database.getInstance(getApplicationContext()).getLastMsgDate();
		if (lastDateLocal.equals("")) {
			lastDateLocal = "0000-00-00-00:00:00";
		} 
		  Date serverDate = AppManagerUtil.StrToDate(lastDateFromServer);
		    Date localDate = AppManagerUtil.StrToDate(lastDateLocal);
		    
		    int timeInterval = serverDate.compareTo(localDate);
		    //�������ϵ�ʱ��Ƚ���˵��������Ϣ��
		if (timeInterval >0) {
			ChatPacketHandler chatHandler = new ChatPacketHandler();
			chatHandler.getMessageWithAccount(lastDateLocal,
					lastDateFromServer);
			chatHandler = null;
		}else{
			ChatPacketHandler chatHandler = new ChatPacketHandler();
			chatHandler.dealWithSendingMsg();
			chatHandler = null;
		}
		
	}

	// �����Ѷ��Է���Ϣ״̬��������
	private void sendMsgReadToServer() {
		//����������������Ļ�����ô�Ͳ��ܷ����Ѷ���ȥ��
	    if (!(this.isChatActivityVisible)) {
	        return ;
	    }
		int index = mDataArrays.size() - 1;
		// ������һ�����Լ�������Ϣ������û����Ϣ��ʱ�򣬾Ͳ��÷����Ѷ�״̬;
		if (index < 0 || (this.mDataArrays.get(index).getMsgtype() >3)) {
			return;
		}
		
		SharedPreferences mSP = this.getSharedPreferences(email,
				Activity.MODE_PRIVATE);
		String tarReadTime = mSP
				.getString("TarReadTime", "0000-00-00-00:00:00");
		String lastTarMsgTime = Database.getInstance(getApplicationContext()).getLastMsgDate();
		if (lastTarMsgTime.equals("")) {
	        lastTarMsgTime = "0000-00-00-00:00:00";
	    }
		if (!(tarReadTime.equals(lastTarMsgTime))) {
			ChatPacketHandler mReq = new ChatPacketHandler();
			mReq.sendMsgReadWithAccount(
					tarReadTime,	lastTarMsgTime);
			SharedPreferences.Editor mEditor = mSP.edit();
			mEditor.putString("TarReadTime", lastTarMsgTime);
			mEditor.commit();
		}

	}

	private void receiveMsgState() {
		if (this.mDataArrays.size() == 0) {
			return;
		}
		GlobalApplication mIns = GlobalApplication.getInstance();
		String msgState = mIns.getMsgSend();
		String date = mIns.getIniDate(); 

		int index = 0;

		// �����Ѷ�״̬���������ϴ��Ѷ�ʱ���ʱ�򣬾�Ҫ�����ڼ��ȫ��������Ϊ�Ѷ�״̬��
		// ���˷���ʧ�ܵġ�
		if (msgState.equals(Protocol.Read + "")) {
			String lastReadTime = mIns.getMyReadTime();//�ϴ��Ѷ�ʱ��
			for (index = this.mDataArrays.size() - 1; index >= 0; index--) {
				String msgDate = this.mDataArrays.get(index).getInitdate();
				if (msgDate.compareToIgnoreCase(lastReadTime) < 0) {
//					����Ի��б�����һ����¼���ϴ��Ѷ�ʱ��֮ǰ��˵���Ѿ����¹����Ͳ�����
					break;
				}
				if ((this.mDataArrays.get(index).getMsgtype() > 3)
						&& (msgDate.compareToIgnoreCase(date) <= 0)
						&& !(this.mDataArrays.get(index).getStatus()
								.equals(Protocol.SendFail + ""))) {
					this.mDataArrays.get(index).setStatus(msgState);
					this.mAdapter.updateView(index);
				}
			}
		} else {
			for (index = this.mDataArrays.size() - 1; index >= 0; index--) {
				String msgDate = this.mDataArrays.get(index).getInitdate();
				if ((this.mDataArrays.get(index).getMsgtype() >3)
						&& (msgDate.compareToIgnoreCase(date) == 0)) {
					this.mDataArrays.get(index).setStatus(msgState);
					this.mAdapter.updateView(index);
					break;
				}
			}

		}
	}
	@SuppressWarnings("unchecked")
	private void receiveTextMsg(Intent intent) {

		List<Object> serializableExtra = (List<Object>) intent
				.getSerializableExtra(Protocol.EXTRA_DATA);
		List<Object> allTextArr = serializableExtra;
		// account 0 , String initdate 1 , String status 2 , String message 3, String msgType 4 )
		 //������Ѷ�����Ϣ�Ļ�����ô�����û�������������ȡ��ʷ�����¼��
	    if (allTextArr.size() > 0 )
	    {
	    	List<String> mItem = (List<String>) allTextArr.get(0);
	    	if(mItem.get(2).equals(Protocol.Read+"") || mItem.get(0).equals(email)){
	    	     for (int index = 0; index <allTextArr.size(); index ++)
	 	        {
	    	    	 ChatTable entity = new ChatTable();

	    				List<String> msgRecItem = (List<String>) allTextArr.get(index);
	    				entity.setInitdate(msgRecItem.get(1));
	    				int msgType = Integer.parseInt(msgRecItem.get(4));
	    			
	    			    entity.setMsgtype(msgType);
	    				
	    				String s = msgRecItem.get(3);	
	    				entity.setMessage(s);
	    				entity.setStatus(msgRecItem.get(2));// sending
	    				msgRecords.add(0, entity);
	 	        }
	    	     return;
	    	}
        }
	    
	    boolean detectMsgRepeatition = false;
	    boolean getHistoryFromNetwork = false;
		for (int index = 0; index < allTextArr.size(); index++) {
			ChatTable entity = new ChatTable();
			// account 0 , String initdate 1 , String status 2 , String message
			// 3, String msgType(Text pic voice) 4)
			
			List<String> textItem = (List<String>) allTextArr.get(index);
			entity.setInitdate(textItem.get(1));
			int msgType = Integer.parseInt(textItem.get(4));
			
		    entity.setMsgtype(msgType);	
			String s = textItem.get(3);
        	entity.setMessage(s);
        	entity.setStatus(textItem.get(2));// sending
        	
        	ChatTable lastMsg = mDataArrays.get(mDataArrays.size() - 1);
        	if (lastMsg.getInitdate().equals(entity.getInitdate()) == false 
        			|| lastMsg.getMessage().equals(entity.getMessage()) == false) {
        		mDataArrays.add(entity);
        		if (compareTime(entity.getInitdate(), lastMsg.getInitdate()) > 0)
        			getHistoryFromNetwork = true;
        	}
        	else {
        		detectMsgRepeatition = true;
        		if (BuildConfig.DEBUG)
        			Log.d("chatText", "ChatActivity.receiveTextMsg(): detected message repeatition");
        	}
//			mAdapter.updateView(mListView.getCount() - 1);
        	
			if (BuildConfig.DEBUG)
				Log.d("chatText", "ChatActivity.receiveTextMsg(initDate = " + textItem.get(1) + ", message = " + textItem.get(3) + ")");
		}
		
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mListView.getCount() - 1);
		this.sendMsgReadToServer();
	}

	public static int compareTime(String left, String right) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		try {
			Date dLeft = sdf.parse(left);
			Date dRight = sdf.parse(right);
			return dLeft.compareTo(dRight);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (BuildConfig.DEBUG)
			Log.e("ChatActivity.compareTime", "date format error!!!");
		return 0;
	}
	
	private void receiveVoiceMsg(String str) {
		String[] arr = str.split(" ");
		String acc = arr[0];
		String date = arr[1];
		String status = arr[2];
		String voicePath = arr[3];
		String recTime =arr[4];
//		File file = new File(voicePath);
//		long len = 0;
//		try {
//			len = AppManagerUtil.getAmrDuration(file) / 1000;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		recTime = (int) (len + 1.5);
		
		
		// need to refresh //selection: ָ����ѯ����
		// String selection = MediaStore.Audio.Media.DATA + " =?";
		// //�趨��ѯĿ¼
		// String totalpath =voicePath;
		// String[] selectionArgs = {totalpath};
		// String[] mCursorCols = new String[] { MediaStore.Audio.Media.DATA,
		//
		// MediaStore.Audio.Media.DURATION };
		//
		//
		//
		// Cursor mCursor = getContentResolver().query(
		// MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mCursorCols,
		//
		// selection, selectionArgs, null);
		// if(mCursor.getCount() >0){
		// mCursor.moveToPosition(0);
		// int durationIndex =
		// mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
		// recTime =mCursor.getInt(durationIndex) / 1000;
		// }
		//

		// ��ʾ�����������
		ChatTable entity = new ChatTable();
		entity.setInitdate(date);
		entity.setAccount(acc);
		if(acc.equals(SelfInfo.getInstance().getAccount())){
			entity.setMsgtype(Protocol.VALUE_RIGHT_AUDIO);
		}else{
			entity.setMsgtype(Protocol.VALUE_LEFT_AUDIO);
		}
		entity.setStatus(status);
		entity.setMessage(voicePath);
		entity.setRecordTime(recTime);
		//������Ѷ��ģ���������ȡ�����¼;
	    if (status.equals(Protocol.Read+"") || acc.equals(email))
	    {
	        msgRecords.add(entity);
	    }else{
		mDataArrays.add(entity);
//		mAdapter.updateView(mListView.getCount() - 1);
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mListView.getCount() - 1);
		this.sendMsgReadToServer();
	    }

	}

	private void receivePictureMsg(String str) {
		String[] arr = str.split(" ");
		String acc = arr[0];
		String date = arr[1];
		String status = arr[2];
		String picPath = arr[3];
		// ��ʾ�����������
		ChatTable entity = new ChatTable();
		entity.setInitdate(date);
		entity.setAccount(acc);
		if(acc.equals(SelfInfo.getInstance().getAccount())){
			entity.setMsgtype(Protocol.VALUE_RIGHT_IMAGE);
		}else{
			entity.setMsgtype(Protocol.VALUE_LEFT_IMAGE);
		}
		entity.setStatus(status);
		entity.setMessage(picPath);

		//������Ѷ��ģ���������ȡ�����¼;
	    if (status.equals(Protocol.Read+"") || acc.equals(email))
	    {
	      msgRecords.add(entity);
	    }
	    else{
		mDataArrays.add(entity);
//		mAdapter.updateView(mListView.getCount() - 1);
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mListView.getCount() - 1);
		this.sendMsgReadToServer();
	    }
	}

	// ����û��Ѿ���¼�Ļ�����ôֱ�Ӻͷ�������ϵ,�����Ҫ����һ����ʱ����ÿ��1����һ�Ρ�
	public void refreshFromServer() {
		if (SelfInfo.getInstance().isOnline()) {
			sendMsgReadToServer(); // �����Ѷ�״̬��ȥ;
			ChatPacketHandler chatHandler = new ChatPacketHandler();
			chatHandler.getLastMessageDateWithAccount();
		} else {
			if (this.connectTimer != null) {
				this.connectTimer.cancel();
				this.connectTimer = null;
			}
			this.connectTimer = new Timer();
			TimerTask mTimerTask = new TimerTask() {

				@Override
				public void run() {
					ChatActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							sendInfoToServer();

						}
					});

				}

			};
			connectTimer.schedule(mTimerTask, 1L, 1000L);
		}
	}

	//����û��Ѿ���¼�Ļ�����ôֱ�Ӻͷ�������ϵ
	public  void sendInfoToServer()
	{
		if (SelfInfo.getInstance().isOnline()) {
			if (this.connectTimer != null) {
				this.connectTimer.cancel();
				this.connectTimer = null;
			}

			sendMsgReadToServer(); // �����Ѷ�״̬��ȥ;
			ChatPacketHandler chatHandler = new ChatPacketHandler();
			chatHandler.getLastMessageDateWithAccount();
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int j = (int) event.getY();
		int[] topTarget = new int[2];
		int[] bottomTarget = new int[2];
		ChatActivity.this.topRl.getLocationOnScreen(topTarget);
		ChatActivity.this.bottomRl.getLocationOnScreen(bottomTarget);

		int curY = topTarget[1];
		int curH = ChatActivity.this.topRl.getHeight();

		int curY1 = bottomTarget[1];

		if (j > (curY + curH) && j < (curY1)) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(contentEt.getWindowToken(), 0);
			ChatActivity.this.mFRl.hideFaceView();
		}

		return false;
	}

	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE
				&& resultCode == RESULT_OK && null != data) {
			GlobalApplication.getInstance().setSelectPic(true);
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
           String currentDate = getDate();
    	 
    				ChatTable entity = new ChatTable();
    	    		entity.setInitdate(currentDate);
    	    		entity.setAccount(SelfInfo.getInstance().getAccount());
    	    		entity.setMsgtype(Protocol.VALUE_RIGHT_IMAGE);
    	    		entity.setStatus(Protocol.Sending + "");
    	    		entity.setMessage(picturePath);
    	    		entity.setRecordTime("0");
    	    		mDataArrays.add(entity);
    	    		mAdapter.notifyDataSetChanged();
    	    		mListView.setSelection(mListView.getBottom());
    	    		LoadPicTask(entity);

		}

		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			GlobalApplication.getInstance().setSelectPic(true);
			if (resultCode == RESULT_OK) {
//				  //HTC
//                if (data.getData() != null) {
//                        //���ݷ��ص�URI��ȡ��Ӧ��SQLite��Ϣ
//                        Cursor cursor = this.getContentResolver().query(data.getData(), null,
//                                        null, null, null);
//                        if (cursor.moveToFirst()) {
//                                filePath = cursor.getString(cursor.getColumnIndex("_data"));// ��ȡ����·��
//                        }
//                        cursor.close();
//                }else{//����  С��(С���ֻ������Զ��洢DCIM...  ����ø��ְ��ֺ�...)
//                        object = (Bitmap) (data.getExtras() == null ? null : data.getExtras().get("data"));
//                }
				// Image captured and saved to fileUri specified in the Intent
//				Toast.makeText(this, "Image saved to:\n" + fileUri.getPath(),
//						Toast.LENGTH_LONG).show();
				 String currentDate = getDate();
		    	 
 				ChatTable entity = new ChatTable();
 	    		entity.setInitdate(currentDate);
 	    		entity.setAccount(SelfInfo.getInstance().getAccount());
 	    		entity.setMsgtype(Protocol.VALUE_RIGHT_IMAGE);
 	    		entity.setStatus(Protocol.Sending + "");
 	    		entity.setMessage(GlobalApplication.getInstance().getFileUri().getPath());
 	    		entity.setRecordTime("0");
 	    		mDataArrays.add(entity);
// 	    		mAdapter.updateView(mListView.getCount() - 1);
 	    		mAdapter.notifyDataSetChanged();
 	    		mListView.setSelection(mListView.getBottom());
 	    		LoadPicTask(entity);
				
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}
	}

	// ������Ϣ
	public boolean sendTextMessageToServer(String message, String date) {
		String email = SelfInfo.getInstance().getAccount();
		try {
//			Log.v("testchin", "chatactivity send 001 ");
			byte[] m = message.getBytes("UTF-8");
			StringBuilder test = new StringBuilder("byte : ");
			for (int i = 0; i < m.length; i++) {
				test.append(m[i]).append("  || ");

			}
//			Log.v("testchin", "chatactivity send  " + test.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean ret = Database.getInstance(getApplicationContext()).addChat(email, date,
				Protocol.Sending + "", message, Protocol.VALUE_RIGHT_TEXT, "0");
		if (ret) {
			ChatPacketHandler chatHandler = new ChatPacketHandler();
			chatHandler.sendTextWithAccount(date, message);
		}
		
		return ret;
	}



	// socket����¼����Ϣ
	public void sendRecordData(final String currentDate, final String voicePath,final String recordTime) {
		// �ȱ���������ݿ�.
		Database db = Database.getInstance(this);
		db.addChat(email, currentDate, Protocol.Sending + "", voicePath,
				Protocol.VALUE_RIGHT_AUDIO, recordTime);
		new Thread( new Runnable(){

			@Override
			public void run() {
				ChatPacketHandler chatHandler = new ChatPacketHandler();

				byte[] recordData = null;
				try {
					recordData = AppManagerUtil.readFileSdcardFile(voicePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 //����û������ߵĻ����ŷ��ͳ�ȥ������ֻ��д�����ݿ⣬�ȵ���¼�ɹ�֮����ط���
				 if (SelfInfo.getInstance().isOnline()) {
				// ��������.
				int firstPacketPicLen = 8192 - 8 - email.length()
						- currentDate.length()
						- String.format("%d", recordData.length).length() - 4;
				int appendPacketPicLen = 8192 - 8 - email.length()
						- currentDate.length() - 3;

				if (recordData.length <= firstPacketPicLen) {
					chatHandler.sendVoiceFirstSectionWithAccount(currentDate,
							recordData.length, recordData);
				} else {
					int appendPacketNum = (recordData.length - firstPacketPicLen)
							/ appendPacketPicLen + 1;

					byte[] firstPacket = new byte[firstPacketPicLen];
					System.arraycopy(recordData, 0, firstPacket, 0, firstPacketPicLen);
					chatHandler.sendVoiceFirstSectionWithAccount( currentDate,
							recordData.length, firstPacket);

					for (int i = 0; i < appendPacketNum - 1; i++) {
						byte[] appendPacket = new byte[appendPacketPicLen];
						System.arraycopy(recordData, firstPacketPicLen + i
								* appendPacketPicLen, appendPacket, 0,
								appendPacketPicLen);

						chatHandler.sendVoiceAppendSectionWithAccount(
								currentDate, appendPacket);
					}
					byte[] lastAppendPacket = new byte[recordData.length
							- firstPacketPicLen - appendPacketPicLen
							* (appendPacketNum - 1)];
					System.arraycopy(recordData, firstPacketPicLen + appendPacketPicLen
							* (appendPacketNum - 1), lastAppendPacket, 0,
							recordData.length - firstPacketPicLen - appendPacketPicLen
									* (appendPacketNum - 1));
					chatHandler.sendVoiceAppendSectionWithAccount( currentDate,
							lastAppendPacket);
				}
				chatHandler.sendVoiceFinishSectionWithAccount(currentDate,
						recordData.length);
				
			}
			}
			
		}).start();
		
	}

	public void startSaveToSd(final String path, final Bitmap pic,
			final String bitname) {
		new Thread() {
			public void run() {
				AppManagerUtil.writeToSD(path, pic, bitname);
			}
		}.start();
	}

	// compress image
	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG,80, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��

		if (baos.toByteArray().length / 1024 > 300) { // �ж����ѹ����ͼƬ�Ƿ����300kb,���ڼ���ѹ��

			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
			baos.reset();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��

		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
		return bitmap;
	}
	  /**
     * �õ� ͼƬ��ת �ĽǶ� ���������ֻ�������Ƭ �Զ���תһ���Ƕ�
     * @param filepath
     * @return
     */
    public int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
//            Log.e("test", "cannot read exif", ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }
	  private void LoadPicTask (final ChatTable tempCT){  
	     
	    new Thread(new Runnable(){

			@Override
			public void run() {
				 
	        	    BitmapFactory.Options options = new BitmapFactory.Options();
	        	   options.inSampleSize = 2;
	        	   options.outHeight = 380;
	        	   options.outWidth = 308;
	        	   Bitmap tempBitmap = 
	        		BitmapFactory.decodeFile(tempCT.getMessage(), options);
	        	      
	         
	            	   Bitmap temp = compressImage(tempBitmap);
	            	   int angle= getExifOrientation(tempCT.getMessage());
	         		  if(angle!=0){  //�����Ƭ������ ��ת ��ô �͸�����ת����
	         		                      Matrix matrix = new Matrix();
	         		                      matrix.postRotate(angle);
	         		                      temp = Bitmap.createBitmap(temp,
	         		                      0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
	         		                  }
	            	   
	            		String currentDate = tempCT.getInitdate();
	    				String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
	    						.format(new Date());
	    				String email = SelfInfo.getInstance().getAccount();
	    				String path = "/ChatPic/" + email;
	    				String bitName = timeStamp;
	    				String totalpath = Environment.getExternalStorageDirectory()
	    						+ "/LoverHouse" + path + "/" + bitName + ".png";
	    				AppManagerUtil.writeToSD(path, temp, bitName);
	    				
	    				
	    				ChatPacketHandler chatHandler = new ChatPacketHandler();

	    				Database.getInstance(getApplicationContext()).addChat(email,
	    						currentDate, Protocol.Sending + "", totalpath,
	    						Protocol.VALUE_RIGHT_IMAGE, "0");

	    				 //����û������ߵĻ����ŷ��ͳ�ȥ������ֻ��д�����ݿ⣬�ȵ���¼�ɹ�֮����ط���
	    			    if (SelfInfo.getInstance().isOnline()) {
	    			        
	    			   
	    				byte[] picData = Bitmap2Bytes(temp);
	    				// ���ͳ�ȥ;
	    				int firstPacketPicLen = 8192 - 8 - email.length()
	    						- currentDate.length()
	    						- (String.format("%d", picData.length)).length() - 4;
	    				int appendPacketPicLen = 8192 - 8 - email.length()
	    						- currentDate.length() - 3;

	    				if (picData.length <= firstPacketPicLen) {
	    					chatHandler.sendPicFirstSectionWithAccount(currentDate,
	    							picData.length, picData);
	    				} else {
	    					int appendPacketNum = (picData.length - firstPacketPicLen)
	    							/ appendPacketPicLen + 1;
	    					byte[] firstPacket = new byte[firstPacketPicLen];
	    					System.arraycopy(picData, 0, firstPacket, 0, firstPacketPicLen);
	    					chatHandler.sendPicFirstSectionWithAccount( currentDate,
	    							picData.length, firstPacket);

	    					for (int i = 0; i < appendPacketNum - 1; i++) {
	    						byte[] appendPacket = new byte[appendPacketPicLen];
	    						System.arraycopy(picData, firstPacketPicLen + i
	    								* appendPacketPicLen, appendPacket, 0,
	    								appendPacketPicLen);

	    						chatHandler.sendPicAppendSectionWithAccount( currentDate,
	    								appendPacket);
	    					}
	    					int lastLen = picData.length - firstPacketPicLen
	    							- appendPacketPicLen * (appendPacketNum - 1);
	    					byte[] lastAppendPacket = new byte[lastLen];
	    					System.arraycopy(picData, firstPacketPicLen + (appendPacketNum - 1)
	    							* appendPacketPicLen, lastAppendPacket, 0, lastLen);
	    					chatHandler.sendPicAppendSectionWithAccount( currentDate,
	    							lastAppendPacket);
	    				}
	    				chatHandler.sendPicFinishSectionWithAccount(currentDate,
	    						picData.length);
	    			    }
				
			}
	    	
	    }).start();
	   
	    }  
	

	/**
	 * ��BitmapתByte
	 */
	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
		return baos.toByteArray();
	}

	/**
	 * ��Bitmapת����ָ����С
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap createBitmapBySize(Bitmap bitmap, int width, int height) {
		return Bitmap.createScaledBitmap(bitmap, width, height, true);
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
//				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".png");
		} else {
			return null;
		}

		return mediaFile;
	}
	public void updateNetworkRecords()
	{
	
	    if (msgRecords.size() > 0)
	    {
	    	for(int i = msgRecords.size() -1 ; i >= 0; i--){
	    		ChatTable mC =  msgRecords.get(i);
	    		  this.mDataArrays.add(0, mC);
	    	}
	    }
		myRefreshHandler.sendEmptyMessageDelayed(LOAD_NEW_INFO,200);
	}

	@Override
	public void setListView(RTPullListView mListView,
			ChatMsgViewAdapter mAdapter) {
		this.mListView = mListView;
		this.mAdapter = mAdapter;
		this.mDataArrays = ChatListFragment.getmDataArrays();

		mListView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
		// ����ˢ�¼�����
		mListView.setOnRefreshListener(new OnRefreshListener() {

			@SuppressLint("NewApi")
			@Override
			public void onRefresh() {
				new Thread(new Runnable() {

					@Override
					public void run() {
						/* �������������Ϣ
						if (true) {
							ChatTable chatTable = new ChatTable();
							chatTable.same(mDataArrays.get(0));
							chatTable.setMessage(chatTable.getMessage() + "-1-");
							mDataArrays.add(0, chatTable);
				        	myRefreshHandler.sendEmptyMessageDelayed(LOAD_NEW_INFO, 1500);
							return;
						}*/
						if (BuildConfig.DEBUG)
							Log.d("ChatActivity", "setonRefreshListener()run()-begin");
						
						int msgIndex = mDataArrays.size();
						List<ChatTable> preChatList = Database.getInstance(
								getApplicationContext()).getLastChat(msgIndex,
								15);
						int len = preChatList.size();
						if (BuildConfig.DEBUG)
							Log.d("ChatActivity", "setonRefreshListener()run()-middle: len=" + len);
						if(len > 0){
							if (BuildConfig.DEBUG)
								Log.d("ChatActivity", "setonRefreshListener()-begin: len > 0");
							for (int i = len-1; i >=0 ; i--) {
								ChatTable entity = preChatList.get(i);
								mDataArrays.add(0,entity);
							}
							myRefreshHandler.sendEmptyMessageDelayed(LOAD_NEW_INFO, 1500);
							//myRefreshHandler.sendEmptyMessage(LOAD_NEW_INFO);
							if (BuildConfig.DEBUG)
								Log.d("ChatActivity", "setonRefreshListener()-end: len > 0");
						}else{
							if (BuildConfig.DEBUG)
								Log.d("ChatActivity", "setonRefreshListener()-begin: len > 0 else");
							 //����û�еĻ�����Ҫ��������ȡ��
							 String key ="ChatMsgLib";
							 SharedPreferences mSP = ChatActivity.this.getSharedPreferences(email,
										Activity.MODE_PRIVATE);
						        if (mSP.getString(key, "").equals("ChatMsgLibNoExist"))
						        {
	//					        	myRefreshHandler.sendEmptyMessage(LOAD_NEW_INFO);
						        	myRefreshHandler.sendEmptyMessageDelayed(LOAD_NEW_INFO, 1500);
	//					            [self performSelector:@selector(doneLoadingTableViewData) withObject:nil afterDelay:0.02];
						            return;    //˵��֮ǰ�Ѿ��ӷ�����ȷ�Ϲ����������¼���Ѿ���ȡ�����ˡ�
						        }
						        
						        ChatPacketHandler chatHandler =new ChatPacketHandler();
						        if (mDataArrays.size() > 0) {
						            chatHandler.getMessageRecordWithAccount(mDataArrays.get(0).getInitdate());
						        }
						        else{
						           
						            String currentDate = getDate();
						            chatHandler.getMessageRecordWithAccount(currentDate);
						        }
						     //��ʱ10��Ļ���ֹͣ����
						        myRefreshHandler.sendEmptyMessageDelayed(LOAD_NEW_INFO,10000);
	//						
								if (BuildConfig.DEBUG)
									Log.d("ChatActivity", "setonRefreshListener()-end: len > 0 else");
						}
						if (BuildConfig.DEBUG)
							Log.d("ChatActivity", "setonRefreshListener()run()-end");
					}
				}).start();
			}
		});

		mListView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//if (BuildConfig.DEBUG)
					//Log.d("ChatActivity", "setOnTouchListener()run()-begin");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(contentEt.getWindowToken(), 0);
				mFRl.hideFaceView();
				
				//if (BuildConfig.DEBUG)
					//Log.d("ChatActivity", "setOnTouchListener()run()-end");
				return false;
			}
		});

		// mListView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		//
		//
		// ChatMsgEnity entity =
		// ChatListFragment.getmDataArrays().get(position-1);
		// if(BuildConfig.DEBUG){
		// Log.v("we","001  " + position);
		// Log.v("we","002  " + entity.getOtherMsgType());
		// }
		// if(entity.getOtherMsgType().equals(Protocol.MSG_PICTURE+"")){
		// Log.v("we","003  " + position);
		// Intent intent= new Intent();
		// intent.setClass( ChatActivity.this, ChatPicDetailActivity.class);
		// intent.putExtra(Keys.PATH, entity.getText());
		// startActivity(intent);
		// }
		// }
		// });
	}

	private void simulateTouch() {
		/*
		// Obtain MotionEvent object
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis() + 100;
		float x = 200f;
		float y = 200f;
		// List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
		int metaState = 0;
		MotionEvent motionEvent = MotionEvent.obtain(
		    downTime, 
		    eventTime, 
		    MotionEvent.ACTION_DOWN, 
		    x, 
		    y, 
		    metaState
		);
		// Dispatch touch event to view
		mListView.dispatchTouchEvent(motionEvent);
		*/
		long curTime = SystemClock.uptimeMillis();
		MotionEvent motionEvent_down = MotionEvent.obtain(curTime + 0, curTime + 10, MotionEvent.ACTION_DOWN, 200, 200, 0);
		MotionEvent motionEvent_move = MotionEvent.obtain(curTime + 20, curTime + 30, MotionEvent.ACTION_MOVE, 200, 300, 0);
		MotionEvent motionEvent_up = MotionEvent.obtain(curTime + 40, curTime + 50, MotionEvent.ACTION_UP, 200, 300, 0);
		mListView.dispatchTouchEvent(motionEvent_down);
		mListView.dispatchTouchEvent(motionEvent_move);
		mListView.dispatchTouchEvent(motionEvent_up);
	}
	
	// �������
	private Handler myRefreshHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_NEW_INFO:
				if (BuildConfig.DEBUG)
					Log.d("ChatActivity", "Handler-handleMessage(): begin");
				mListView.onRefreshComplete();
				
				mAdapter.notifyDataSetChanged();
				//mListView.setSelection(mListView.getCount() - 1);
				mListView.setSelection(0);
				if (BuildConfig.DEBUG)
					Log.d("ChatActivity", "Handler-handleMessage(): mListView.getCount() = " + mListView.getCount());
				if (BuildConfig.DEBUG)
					Log.d("ChatActivity", "Handler-handleMessage(): end");
				
				simulateTouch();
				break;
			default:
				break;
			}
		}

	};

	// -------------------------------------------------------------------------------------------------------------
	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (Protocol.ACTION_CHATPACKET.equals(action)) {
				// �����û����ݰ� ����,������Ϣ,��handler���½���

				String data = intent.getStringExtra(Protocol.EXTRA_DATA);

				Bundle bdata = new Bundle();
				bdata.putString("data", data);
				Message msg = mHandler.obtainMessage();
				msg.setData(bdata);

				msg.what = Protocol.HANDLE_RESPON;
				mHandler.sendMessage(msg);

			}

			if (Protocol.ACTION_CHATPACKET_REMIND_DATA.equals(action)) {
				// return txt pic voice msg status like sending send etc
				receiveMsgState();

			}

			if (Protocol.ACTION_CHATPACKET_MSGTEXT_DATA.equals(action)) {
				// return text msg
				receiveTextMsg(intent);
			}

			if (Protocol.ACTION_CHATPACKET_RECPIC.equals(action)) {
				// �����û����ݰ� ����,������Ϣ,��handler���½���

				String data = intent.getStringExtra(Protocol.EXTRA_DATA);
				receivePictureMsg(data);

			}

			if (Protocol.ACTION_CHATPACKET_RECVOICE.equals(action)) {
				// �����û����ݰ� ����,������Ϣ,��handler���½���

				String data = intent.getStringExtra(Protocol.EXTRA_DATA);
				receiveVoiceMsg(data);

			}
			if (Protocol.ACTION_CHATPACKET_MSGRECFINISH.equals(action)) {
				// �����û����ݰ� ����,������Ϣ,��handler���½���

				updateNetworkRecords();
			}
			if (Protocol.ACTION_CHATPACKET_MSGREMIND.equals(action)) {
				// �û���
				responseMsgRemind();
			
			}
			
		} // onReceive
	}

	private static class MyHandler extends Handler {
		WeakReference<ChatActivity> mActivity;

		MyHandler(ChatActivity activity) {
			mActivity = new WeakReference<ChatActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ChatActivity theActivity = mActivity.get();
			String mData = msg.getData().getString("data");
			switch (msg.what) {

			case Protocol.HANDLE_RESPON:
				theActivity.processResponse(mData);
				break;
			default:
				break;
			}
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
		case Protocol.CHAT_RETURN_LAST_MESSAGE_TIME:
			receiveLastMsgDate(str);
			break;
		case Protocol.LOGIN_FAIL:

			break;

		case Protocol.ASK_FOR_MATCH:

			break;
		case Protocol.ACCEPT_MATCH:

			break;
		default:
			break;
		}
	}

	private String email = "";
	private ImageView backTomianImgBtn;
	private ImageView mBtnSend;
	private ImageView picBtn;
	private SelectPicPopup mSelectPicPopup = null;

	private EditText contentEt;
	private RTPullListView mListView;
	private List<ChatTable> mDataArrays = new ArrayList<ChatTable>();
	private List<ChatTable> msgRecords = new ArrayList<ChatTable>();
	private ChatMsgViewAdapter mAdapter;
	private Boolean isVoiceMode = false;
	private Boolean isSend = false;

	Context context = this;
	FrameLayout topRl;
	RelativeLayout bottomRl;
	FaceRelativeLayout mFRl;
	RelativeLayout mVoiceLL;
	public boolean isFaceShow = false;

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 200;
	private static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 100;
//	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String TAG = "ChatActivity";

	private ImageView record;
	private ImageView keyboardBtn;
	private Dialog recordingDialog;
	private AudioRecorder mr = null;
	private Thread recordThread;

	private static int MAX_TIME = 15; // �¼��ʱ�䣬��λ�룬0Ϊ��ʱ������
	private static double MIX_TIME = 0.6; // ���¼��ʱ�䣬��λ�룬0Ϊ��ʱ�����ƣ�������Ϊ1

	private static int RECORD_NO = 0; // ����¼��
	private static int RECORD_ING = 1; // ����¼��
	private static int RECODE_ED = 2; // ���¼��

	private static int RECODE_STATE = 0; // ¼����״̬

	private static double recodeTime = 0.0f; // ¼����ʱ��
	private static double voiceValue = 0.0; // ��˷��ȡ������ֵ

	private ImageView dialog_img;

	private static final int LOAD_NEW_INFO = 5;

	private MyReceiver receiver = null;
	private MyHandler mHandler = new MyHandler(this);
	
	private Timer connectTimer = null;

}
