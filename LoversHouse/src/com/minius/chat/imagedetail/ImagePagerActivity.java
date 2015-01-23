package com.minius.chat.imagedetail;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.minius.ui.CircleProgress;
import com.minius.ui.HackyViewPager;
import com.minius.ui.ProgressHUD;
import com.minus.gallery.GalleryActivity;
import com.minus.lovershouse.ChatActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.ChatPicMenuPopup;
import com.minus.lovershouse.util.SelectPicPopup;
import com.minus.sql_interface.Database;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.ChatPacketHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImagePagerActivity extends FragmentActivity implements OnCancelListener{
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	//主要用于把聊天记录中相片导入到相册中时候，制作缩略图用到。
	private int  PerImageWidth  = 60;
	private int    PerImageHeight  = 70;
	
  
	private ProgressHUD mProgressHUD = null;    
	private ProgressHUD showErrorOrSucc = null;
	  private CircleProgress sector;
	  private int progress = 0;
	  private ImageView inditor;
	 
	private Handler  updateServerTimeHandler = null;
	private boolean isRunning = true;

	private HackyViewPager mPager;
	private int pagerPosition;
	private TextView indicator;
	private ImageView backBtn = null;
	private ImageView menuBtn = null;
	
	public String[] urlsList;
	public String[] allList ;
	private String imageDateToAlbum = null;
	
	private MyReceiver receiver = null;

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);

		
		backBtn = (ImageView) findViewById(R.id.imgdt_btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
				ImagePagerActivity.this.finish();
			}
		});
		menuBtn = (ImageView) findViewById(R.id.imgdt_btn_menu);
		
		menuBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 实例化SelectPicPopupWindow
				if (mChatPicMenuPopup == null) {
					mChatPicMenuPopup = new ChatPicMenuPopup(ImagePagerActivity.this,
							itemsOnClick);
					mChatPicMenuPopup.showAtLocation(
							ImagePagerActivity.this.findViewById(R.id.pager),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

				} else if (!(mChatPicMenuPopup.isShowing())) {
					mChatPicMenuPopup.showAtLocation(
							ImagePagerActivity.this.findViewById(R.id.pager),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				} else {
					mChatPicMenuPopup.dismiss();
				}
			}
		});
		
		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		allList= getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
		urlsList = new String[allList.length/2];
		
//		List<String> tempList = new ArrayList<String>();
        for(int i = 0; i<(allList.length/2);i++){
        	urlsList[i] = allList[i *2];
        }
		mPager = (HackyViewPager) findViewById(R.id.pager);
		ImagePagerAdapter mAdapter = new ImagePagerAdapter(
				getSupportFragmentManager(), urlsList);
		mPager.setAdapter(mAdapter);
//		boolean pauseOnScroll = false; // or true
//		boolean pauseOnFling = true; // or false
//		PauseOnDragListener listener = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);
//		mPager.setOnDragListener(listener);
		mPager.setOffscreenPageLimit(3);
		indicator = (TextView) findViewById(R.id.indicator);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
				.getAdapter().getCount());
		indicator.setText(text);
		// 更新下标
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				CharSequence text = getString(R.string.viewpager_indicator,
						arg0 + 1, mPager.getAdapter().getCount());
				indicator.setText(text);
			}

		});
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		mPager.setCurrentItem(pagerPosition);
		PushAgent.getInstance(this).onAppStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)// 强制竖屏
		{   
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
		} 
		
		if(updateServerTimeHandler == null)
		updateServerTimeHandler = new MyHandler(ImagePagerActivity.this);
		
		MobclickAgent.onResume(this);
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)// 强制竖屏
		{   
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
		} 
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(receiver != null)
		{  
			this.unregisterReceiver(receiver);
			receiver = null;
		}
		
		mProgressHUD = null;
		indicator = null;
	     mPager = null;
       backBtn = null;
       menuBtn = null;
		
		 urlsList = null;
	     allList  = null;
	     imageDateToAlbum = null;
	}



	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public String[] fileList;

		public ImagePagerAdapter(FragmentManager fm, String[] fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.length;
		}

	
		@Override
		public Fragment getItem(int position) {
			String url = fileList[position];
			return ImageDetailFragment.newInstance(url);
		}

	}
	
	// 为弹出窗口实现监听类

		private OnClickListener itemsOnClick = new OnClickListener() {
			public void onClick(View v) {
				mChatPicMenuPopup.dismiss();
				switch (v.getId()) {
				case R.id.btn_save_album:
					onClickedSaveToMiniusAlbum();
					break;
				case R.id.btn_save_phone:
					onClickedSaveToLocalPhotoLibrary();
					break;
				default:
					break;
				}
			}

		};
		
		
		private  void onClickedSaveToMiniusAlbum(){
			
			if(receiver == null){
				receiver = new MyReceiver();
		   		IntentFilter filter=new IntentFilter();   
		   	
		   		filter.addAction(Protocol.ACTION_CHATPACKET_SAVEPICTOALBUMSTATE);
		        this.registerReceiver(receiver,filter);
			}
			//使用当前操作时间而不是对话发送照片时间
			imageDateToAlbum = AppManagerUtil.getCurDate();
			
	        int selectedIndex = mPager.getCurrentItem();
	
	    	String imageChatDate =this.allList[selectedIndex*2+1];

			ChatPacketHandler chatHandler = new ChatPacketHandler();
			chatHandler.savePicToAlbumWithPicDate(imageChatDate, imageDateToAlbum);
			showProgressDialog();
			//TODO
			//TODO
			//TODO   
			//TODO
			//TODO
			//TODO   
//	        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(responseForSavePicToAlbum:) name:USNotificationChatSavePicToAlbumState object:nil];
		}
		
		//把相片保存到手机的本地相册中;
		private void onClickedSaveToLocalPhotoLibrary()
			{
			    int selectedIndex = mPager.getCurrentItem();
			    String imagePath = urlsList[selectedIndex];
			    saveToSD(imagePath);
//			    try {
//			    	//写入文件中;
//			    	String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
//					.format(new Date());
//				    String url = MediaStore.Images.Media.insertImage(getContentResolver(), imagePath, "minius"+timeStamp, "miniusAlbum");
//				    if (url == null || url.equals("")) {
//						// 设置失败
//						showErrorOrSucc = null;
//						showErrorOrSucc = ProgressHUD.showSuccOrError(
//								ImagePagerActivity.this, "保存图片失败", false);
//				
//						updateServerTimeHandler.sendEmptyMessageDelayed(
//								3, 2000);
//					} else {
//						showErrorOrSucc = null;
//						showErrorOrSucc = ProgressHUD.showSuccOrError(
//								ImagePagerActivity.this, "保存图片成功", true);
//						updateServerTimeHandler.sendEmptyMessageDelayed(
//								3, 2000);
//					}
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			 // 发送广播
//				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
////				"/sdcard/image.jpg"
//				String fileName = Environment.getExternalStorageDirectory()+"/image.jpg";
//				Uri uri = Uri.fromFile(new File(fileName));
//				intent.setData(uri);
//				sendBroadcast(intent);
			    
			  
			}
		
	private void saveToSD(final String imagePath){
		new Thread( new Runnable(){

			@Override
			public void run() {
				  BitmapFactory.Options options = new BitmapFactory.Options();
	        	   options.inSampleSize = 2;
	        	   options.outHeight = 380;
	        	   options.outWidth = 308;
	        	   Bitmap tempBitmap = 
	        		BitmapFactory.decodeFile(imagePath, options);
	        	  String path = "/miniusAlbum";
	        	  String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date());
		
			String bitName = timeStamp;
				String totalpath = Environment.getExternalStorageDirectory()
						+ "/LoverHouse" + path + "/" + bitName + ".png";
				if(AppManagerUtil.writeToSD(path, tempBitmap, bitName)){
				Bundle bdata = new Bundle();
				bdata.putString("toast", totalpath);
				Message msg = updateServerTimeHandler.obtainMessage();
				msg.setData(bdata);
				msg.what = 4;
				updateServerTimeHandler.sendMessage(msg);
				}else{
				updateServerTimeHandler.sendEmptyMessage(
						5);
				}
			}
			
		}).start();
		
	}
		
	 Runnable updateConnectServerRunnable=new Runnable() {
	     @Override
	     public void run() {
	    	 if(isRunning){
	    	 progress +=20;
	    	 if(progress >120){
	    		 progress = 0;
	    		 isRunning =false;
     		Message msg1 =updateServerTimeHandler.obtainMessage();
     		msg1.what = 2;
     		updateServerTimeHandler.sendMessageDelayed(msg1, 500);
     		
	    	 }else{
	    	 Message msg =updateServerTimeHandler.obtainMessage();
	 		msg.what = 1;
	 		updateServerTimeHandler.sendMessage(msg);
	    	 updateServerTimeHandler.postDelayed(this, 1000);
	    	 }
	    	 
	    	 }
	     }
	 };

private void showProgressDialog()
	{
	if(mProgressHUD ==null){
 	mProgressHUD = ProgressHUD.show(ImagePagerActivity.this,"正在联系服务器...", false,false,this);
	}else{
		mProgressHUD.setMessage("正在联系服务器...");
		mProgressHUD.show();
	}
 	sector = (CircleProgress)mProgressHUD.findViewById(R.id.arc);
// 	/设置模式为扇形的
	sector.setType(CircleProgress.SECTOR);
	sector.setVisibility(View.VISIBLE);
	inditor = (ImageView) mProgressHUD.findViewById(R.id.stateImageView);
	mProgressHUD.findViewById(R.id.spinnerImageView).setVisibility(View.GONE);
//	inditor.setBackgroundResource(R.drawable.errorblack2);
	inditor.setVisibility(View.GONE);
	 
	if(updateServerTimeHandler == null){
		progress = 0;
		updateServerTimeHandler = new MyHandler(ImagePagerActivity.this);
	}else{
		updateServerTimeHandler.removeCallbacks(updateConnectServerRunnable);  
	}
	isRunning = true;
	updateServerTimeHandler.postDelayed(updateConnectServerRunnable, 2000);
	}
		

public class TimeConsumingTask extends AsyncTask<String, String, Void>  {	

	@Override
	protected void onPreExecute() {
		if(mProgressHUD ==null){
		 	mProgressHUD = ProgressHUD.show(ImagePagerActivity.this,"正在转存", false,false,null);
			}
    	super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(String... params) {
		try {
			saveToAlbum(Integer.parseInt(params[0]) ,params[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//把聊天中的相片保存到想你相册中。
		private void saveToAlbum(int imageIndex ,String newImageDate)
			{
			String  oriPath = urlsList[imageIndex];
		    //对图片大小进行等比例压缩-- ,100kb
				Bitmap uploadImageData = AppManagerUtil.getimage(oriPath);
				//图片写入文件中，并且把文件路径存进数据库中.
			
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
				Date curDate = AppManagerUtil.StrToDate(newImageDate);
			
				String thumbnailDate =formatter1.format(curDate) ;    
		        String thumbnailpath = Environment.getExternalStorageDirectory()  
			                + "/LoverHouse" + "/Album"+"/"+thumbnailDate + ".png";
				  
				    
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
					String currDate = formatter.format(curDate);
					
				    String photopath = Environment.getExternalStorageDirectory()  
				                + "/LoverHouse" + "/Album"+"/ori/"+ thumbnailDate + ".png";
				    AppManagerUtil.writeToSD("/Album/ori", uploadImageData, thumbnailDate);
				    
				    //制作缩略图;
					Bitmap thumbnailBm = ThumbnailUtils.extractThumbnail(uploadImageData, PerImageWidth, PerImageHeight);
				    AppManagerUtil.writeToSD("/Album", thumbnailBm,thumbnailDate);
				
				    //数据库中保存的是图的路径;
				    Database.getInstance(GlobalApplication.getInstance().getApplicationContext())
				    .saveAlbumPicture(currDate,thumbnailpath, photopath);
			}
		
	
	@Override
	protected void onProgressUpdate(String... values) {

		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(Void result) {
		mProgressHUD.setMessage("转存成功");
		inditor.setBackgroundResource(R.drawable.successblack2);
		inditor.setVisibility(View.VISIBLE);
		sector.setVisibility(View.GONE);
		Message msg =updateServerTimeHandler.obtainMessage();
		msg.what = 2;
		updateServerTimeHandler.sendMessageDelayed(msg, 1000);
//		mProgressHUD.dismiss();
		super.onPostExecute(result);
	}

	
}

@Override
public void onCancel(DialogInterface dialog) {
	
}		
	public void responseForSavePicToAlbum(String state)
	{
		 isRunning =false;
		if(updateServerTimeHandler != null){
			progress = 0;
			updateServerTimeHandler.removeCallbacks(updateConnectServerRunnable);  
		
		}
	
		if(receiver != null)
		{  
			this.unregisterReceiver(receiver);
			receiver = null;
		}
		
	    if (state.equals("saveSucc"))
		    {
		    int pos = mPager.getCurrentItem();

			TimeConsumingTask t = new TimeConsumingTask();
	    	t.execute(pos+"",imageDateToAlbum);
		
		    }
		    else{
		    	mProgressHUD.setMessage("转存失败");
        		sector.setVisibility(View.GONE);
        		inditor.setBackgroundResource(R.drawable.errorblack2);
        		inditor.setVisibility(View.VISIBLE);
        		Message msg =updateServerTimeHandler.obtainMessage();
        		msg.what = 2;
        		updateServerTimeHandler.sendMessageDelayed(msg, 1000);

		    }
		}

		private class MyReceiver extends BroadcastReceiver  {  
			@Override  
			public void onReceive(Context context, Intent intent){  
				String action = intent.getAction();   	
				if(Protocol.ACTION_CHATPACKET_SAVEPICTOALBUMSTATE.equals(action)){
					String state = intent.getStringExtra(Protocol.EXTRA_DATA);
					responseForSavePicToAlbum(state);
				}
			
			
			}	//onReceive
		} 
		private static class MyHandler extends Handler {
			WeakReference<ImagePagerActivity> mActivity;

			MyHandler(ImagePagerActivity activity) {
				mActivity = new WeakReference<ImagePagerActivity>(activity);
			}

			@Override
			public void handleMessage(Message msg) {
				ImagePagerActivity theActivity = mActivity.get();
		
				switch (msg.what) {  
			     case 1:
		            	if(theActivity.progress <= 100)
		            theActivity.sector.setmSubCurProgress(theActivity.progress);
		            	if(theActivity.progress == 120){
		            		 if(theActivity.receiver != null)
		              		{  
		            			 theActivity.unregisterReceiver(theActivity.receiver);
		            			 theActivity.receiver = null;
		              		}
		            	 theActivity.mProgressHUD.setMessage("操作超时");
		            	 theActivity.sector.setVisibility(View.GONE);
		            	 theActivity.inditor.setBackgroundResource(R.drawable.errorblack2);
		            	 theActivity.inditor.setVisibility(View.VISIBLE);
		            	}
		                break;
			     case 2:
			    	 theActivity.mProgressHUD.dismiss();
			    	 break;
			     case 3:
			    	 if(theActivity.showErrorOrSucc != null)
			    	 theActivity.showErrorOrSucc.dismiss();
			    	 break;
			     case 4:
			    	String toastPath = msg.getData().getString("toast");
			    	Toast.makeText(theActivity.getApplicationContext(), "图片已经保存到"+toastPath, Toast.LENGTH_LONG).show();
			    	 break;
			     case 5:
			    	 Toast.makeText(theActivity.getApplicationContext(), "图片保存失败", Toast.LENGTH_LONG).show();
			    	 break;
		            default:
		                break;
				}
			}
		}
	private ChatPicMenuPopup mChatPicMenuPopup = null;
}