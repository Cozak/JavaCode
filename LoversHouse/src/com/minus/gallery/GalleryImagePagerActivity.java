package com.minus.gallery;

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
import com.minius.ui.CustomDialog.Builder;
import com.minius.ui.HackyViewPager;
import com.minius.ui.ProgressHUD;
import com.minus.lovershouse.ChatActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.util.ExitPopup;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.ChatPicMenuPopup;
import com.minus.lovershouse.util.SelectPicPopup;
import com.minus.sql_interface.Database;
import com.minus.table.GalleryTable;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.AlbumPacketHandler;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GalleryImagePagerActivity extends FragmentActivity implements
		OnCancelListener {
//	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	public static final String EXTRA_IMAGE_DATES = "image_dates";
	// 主要用于把聊天记录中相片导入到相册中时候，制作缩略图用到。
	private int PerImageWidth = 60;
	private int PerImageHeight = 70;

	private AlbumOperateImgPopup mOperateImgPopup = null;
	private MyHandler mHandler = new MyHandler(this);

	private HackyViewPager mPager;
	private int pagerPosition;
	private TextView indicator;
	private ImageView backBtn = null;
	private ImageView menuBtn = null;
	
	private Builder mBuilder = null;
	
	private Database mDB = null;

//	public String[] urlsList;
//	public String[] dateList;
//	private List<GalleryTable> dataList = new ArrayList<GalleryTable>() ;
	ImagePagerAdapter mAdapter= null;
	private MyReceiver receiver = null;
	private ProgressHUD showErrorOrSucc = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);
		
		if(receiver == null){
			receiver = new MyReceiver();
	   		IntentFilter filter=new IntentFilter();   
	   		filter.addAction(Protocol.ACTION_ALBUM_RemoveOneImage_PACKET);
	 
	   		filter.addAction(Protocol.ACTION_ALBUM_AlbumHomePageState);
	        this.registerReceiver(receiver,filter);
		}

		backBtn = (ImageView) findViewById(R.id.imgdt_btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int cur = GalleryImagePagerActivity.this.mPager
						.getCurrentItem();
				Intent bakcIntent = new Intent(GalleryImagePagerActivity.this,
						GalleryActivity.class);
				
				bakcIntent.putExtra("pos", cur);
				bakcIntent.putExtra("who", 10);
				setResult(0,bakcIntent);
//				startActivity(bakcIntent);
				GalleryImagePagerActivity.this.finish();
			}
		});
		menuBtn = (ImageView) findViewById(R.id.imgdt_btn_menu);

		menuBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 实例化SelectPicPopupWindow
				if (mOperateImgPopup == null) {
					mOperateImgPopup = new AlbumOperateImgPopup(GalleryImagePagerActivity.this,
							operateImgOnClick);
					mOperateImgPopup.showAtLocation(
							GalleryImagePagerActivity.this.findViewById(R.id.gallerydetailFl),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

				} else if (!(mOperateImgPopup.isShowing())) {
					mOperateImgPopup.showAtLocation(
							GalleryImagePagerActivity.this.findViewById(R.id.gallerydetailFl),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				} else {
					mOperateImgPopup.dismiss();
				}

			}
		});

		

		mPager = (HackyViewPager) findViewById(R.id.pager);
		
		mDB =  Database.getInstance(getApplicationContext());
	
		PushAgent.getInstance(this).onAppStart();
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		int cur = GalleryImagePagerActivity.this.mPager
					.getCurrentItem();
			Intent bakcIntent = new Intent(GalleryImagePagerActivity.this,
					GalleryActivity.class);
			
			bakcIntent.putExtra("pos", cur);
			bakcIntent.putExtra("who", 10);
			setResult(0,bakcIntent);
//			startActivity(bakcIntent);
			GalleryImagePagerActivity.this.finish();
    		
    	}
    	return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		GlobalApplication.getInstance().setAlbumVisible(true);
		MobclickAgent.onResume(this);
		
		List<GalleryTable> dataList =mDB
				.getAllPicture();
	       mAdapter = new ImagePagerAdapter(
				getSupportFragmentManager(), dataList);
		mPager.setAdapter(mAdapter);
		
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
		
		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		mPager.setCurrentItem(pagerPosition);
		
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
		GlobalApplication.getInstance().setAlbumVisible(false);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (receiver != null) {
			this.unregisterReceiver(receiver);
			receiver = null;
		}

	
		indicator = null;
		mPager = null;
		backBtn = null;
		menuBtn = null;

	}
	
	// 响应
		public void reponseForSetHomePageState(String state) {

			if (state.equals("Succ")) {

				showErrorOrSucc = null;
				showErrorOrSucc = ProgressHUD.showSuccOrError(GalleryImagePagerActivity.this,
						"相册首页设置成功", true);
				mHandler.sendEmptyMessageDelayed(
						Protocol.HANDLE_ALBUM_DISMISSDIALOG, 3000);
			} else {
				showErrorOrSucc = null;
				showErrorOrSucc = ProgressHUD.showSuccOrError(GalleryImagePagerActivity.this,
						"相册首页设置失败", false);
				mHandler.sendEmptyMessageDelayed(
						Protocol.HANDLE_ALBUM_DISMISSDIALOG, 3000);
				// [ProgressHUD showError:@"相册首页设置失败"];
			}
		}

		public void reponseForRemoveOneImage(String initDate) {
			int listSize = mAdapter.fileList.size();
			for (int index = 0; index < listSize; index++) {
				if (mAdapter.fileList.get(index).getLastModefyTime()
						.equals(initDate)) {
					removeImageWithIndex(index, false);
					break;
				}
			}
		}

	@Override
	public void onSaveInstanceState(Bundle outState) {
//		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public List<GalleryTable> fileList;

		public ImagePagerAdapter(FragmentManager fm, List<GalleryTable>  fileList) {
			super(fm);
			this.fileList = fileList;
		}
		
		//解决更新数据源无效情况 
//		http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view
		@Override
		public int getItemPosition(Object object) {
		    return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList.get(position).getOriPath();
			return GalleryImageDetailFragment.newInstance(url);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			System.out.println("position Destory" + position);
			super.destroyItem(container, position, object);
		}

	}

	

	
	private OnClickListener operateImgOnClick = new OnClickListener() {
		public void onClick(View v) {
			mOperateImgPopup.dismiss();
			switch (v.getId()) {
			case R.id.galleryShowInMain:
				onClickedDisplayHomePage();
				break;
			case R.id.gallerySaveImage:
				onClickedSaveToLocalPhotoLibrary();
				break;
			case R.id.galleryDeleteImage:
				mBuilder = AppManagerUtil.openAlertDialog(GalleryImagePagerActivity.this, "",
						"确定要删除该相片吗？", "删除", "取消",
						new View.OnClickListener() {

					@Override
					public void onClick(View v){
								// 删除照片
								onClickedDeletePic();
								if(mBuilder != null && mBuilder.getDialog()!= null)
									mBuilder.getDialog().dismiss();

							}
						}, new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if(mBuilder != null && mBuilder.getDialog()!= null)
									mBuilder.getDialog().dismiss();

							}
						}, true);

				break;
			default:
				break;
			}
		}

	};
	
//	点击事件 在main界面设置
		private void onClickedDisplayHomePage()
		{
		    int selectedIndex =this.mPager.getCurrentItem();
		    AlbumPacketHandler handler = new AlbumPacketHandler();
		    handler.setHomePageWithInitDate(mAdapter.fileList.
		    		get(selectedIndex).getLastModefyTime());	   
		}
	// 把相片保存到手机的本地相册中;
	private void onClickedSaveToLocalPhotoLibrary() {
		int selectedIndex = mPager.getCurrentItem();
		String imagePath =mAdapter.fileList.get(selectedIndex).getOriPath() ;
		saveToSD(imagePath);
//		try {
//			// 写入文件中;
//			String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
//					.format(new Date());
//			String url = MediaStore.Images.Media.insertImage(
//					getContentResolver(), imagePath, "minius" + timeStamp,
//					"miniusAlbum");
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// 发送广播
//		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//		Uri uri = Uri.fromFile(new File("/sdcard/image.jpg"));
//		intent.setData(uri);
//		sendBroadcast(intent);

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
				Message msg = mHandler.obtainMessage();
				msg.setData(bdata);
				msg.what = Protocol.HANDLE_ALBUM_SAVETOLOCALSUCC;
				mHandler.sendMessage(msg);
				}else{
				mHandler.sendEmptyMessage(
						Protocol.HANDLE_ALBUM_SAVETOLOCALFAIL);
				}
			}
			
		}).start();
		
	}
	//当用户点击删除的时候仅仅是修改数据库中这条记录标志值，
//	只有获得服务器确认信息之后才真正删除记录;
	
	private void onClickedDeletePic()
	{
		 int removeIndex =mPager.getCurrentItem();
          removeImageWithIndex(removeIndex,true);
	}
	
	//删除指定下标的图片;
	public void removeImageWithIndex(int removeIndex, boolean isWaitForServerConfirm)
	{
			List<GalleryTable> list = mAdapter.fileList;
			if(removeIndex >= list.size() || removeIndex <0) return;
		     GalleryTable dict =list.get(removeIndex);
		    
		    String imagePath = dict.getPath();
		    String oriImagePath = dict.getOriPath();

	         File f=new File(imagePath);
	         File f1 = new File(oriImagePath);
	         
		    //如果两个文件都存在的话，那么就要删除成功才行，如果有文件不存在的话，那么就可以直接把数据库删除即可。
	         //只有沙盒中两个文件都成功删除之后，才进一步去删除服务器的
		  if(!(AppManagerUtil.deleteFile(f) && AppManagerUtil.deleteFile(f1))){
			  return;
		  }
		    
		    //需要等待服务器确认之后才能真正删除的记录，就只是改变数据库里记录的标志值;否则的话，就会立即从数据库中把该记录删除掉;
		    if (isWaitForServerConfirm )
		    {
		        AlbumPacketHandler albumHandler =new AlbumPacketHandler();
		        albumHandler.removeImageWithInitDate(dict.getLastModefyTime());
		        mDB.SetStatusForPic(dict.getLastModefyTime(), 1);////把gai相片改成正在删除的状态
		 
		    }
		    else
		    {
		      mDB.deleteAlbumPicture(dict.getLastModefyTime());
		      }
		    
		    mAdapter.fileList.remove(removeIndex);
	          
		    
		 
		    //如果删除之后没有图片了
		    if (mAdapter.fileList.size() == 0)
		    {
		      //返回主界面d
		    	int cur = GalleryImagePagerActivity.this.mPager
						.getCurrentItem();
				Intent bakcIntent = new Intent(GalleryImagePagerActivity.this,
						GalleryActivity.class);
				
				bakcIntent.putExtra("pos", cur);
				bakcIntent.putExtra("who", 10);
				setResult(0,bakcIntent);
				GalleryImagePagerActivity.this.finish();
		    
		    }
		    else
		    {
		        int nextIndex = removeIndex;
		        
		        //如果是删除最后一个，那么就后退一个;
		        if (removeIndex == mAdapter.fileList.size())
		        {
		            nextIndex --;
		        }
//		        galleryAdapter.notifyDataSetChanged();
//		        this.mImagePagerAdapter.notifyDataSetChanged();
		        mPager.setCurrentItem(nextIndex);
		        mAdapter.notifyDataSetChanged();
		    }
		}
	

	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
		  
			if(Protocol.ACTION_ALBUMPACKET.equals(action)){
				if(intent.getStringExtra(Protocol.EXTRA_DATA) != null){
					//读到用户数据包 数据,发送消息,让handler更新界面			
					String data = intent.getStringExtra(Protocol.EXTRA_DATA);
					Bundle bdata = new Bundle();
					bdata.putString("data", data);
					Message msg = mHandler.obtainMessage();				
					msg.setData(bdata);
								
					msg.what =Protocol.HANDLE_RESPON;
					mHandler.sendMessage(msg);	
				}
			}
			
			
			
//			if(Protocol.ACTION_ALBUM_GetImage_PACKET.equals(action)){
//				if(intent.getStringExtra(Protocol.EXTRA_DATA) != null){		
//					String imageContent = intent.getStringExtra(Protocol.EXTRA_DATA);
//					responseForGetImage(imageContent);
//					
//				}
//				
//				
//			}
			
//			if(Protocol.ACTION_ALBUM_LastModifyTime_PACKET.equals(action)){
//				if(intent.getStringExtra(Protocol.EXTRA_DATA) != null){		
//					String lastModifyTime = intent.getStringExtra(Protocol.EXTRA_DATA);
//					responseForLastModifyTime(lastModifyTime);
//					
//				}
//           }
			if(Protocol.ACTION_ALBUM_RemoveOneImage_PACKET.equals(action)){
				if(intent.getStringExtra(Protocol.EXTRA_DATA) != null){		
					String initDate = intent.getStringExtra(Protocol.EXTRA_DATA);
					reponseForRemoveOneImage(initDate);
					
				}
            }
			
			
		if(Protocol.ACTION_ALBUM_AlbumHomePageState.equals(action)){
			//设置主页状态
			String state= intent.getStringExtra(Protocol.EXTRA_DATA);
			reponseForSetHomePageState(state);
		}
		
		} // onReceive
	}

	private static class MyHandler extends Handler {
		WeakReference<GalleryImagePagerActivity> mActivity;

		MyHandler(GalleryImagePagerActivity activity) {
			mActivity = new WeakReference<GalleryImagePagerActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			GalleryImagePagerActivity theActivity = mActivity.get();

			switch (msg.what) {
			case Protocol.HANDLE_ALBUM_DISMISSDIALOG:
				if (theActivity.showErrorOrSucc != null) {
					if (theActivity.showErrorOrSucc.isShowing()) {
						theActivity.showErrorOrSucc.dismiss();
						theActivity.showErrorOrSucc = null;
					}
				}
				break;
			case Protocol.HANDLE_ALBUM_SAVETOLOCALSUCC:
				String toastPath = msg.getData().getString("toast");
		    	Toast.makeText(theActivity.getApplicationContext(), "图片已经保存到"+toastPath, Toast.LENGTH_LONG).show(); 
		    	break;
			case Protocol.HANDLE_ALBUM_SAVETOLOCALFAIL:
				 Toast.makeText(theActivity.getApplicationContext(), "图片保存失败", Toast.LENGTH_LONG).show();
			    	
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}

	
}