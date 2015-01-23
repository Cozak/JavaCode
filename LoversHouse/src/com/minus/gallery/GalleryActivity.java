package com.minus.gallery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.minius.chat.imagedetail.ImagePagerActivity;
import com.minius.ui.CustomDialog.Builder;
import com.minius.ui.CircleProgress;
import com.minius.ui.ProgressHUD;
import com.minus.lovershouse.ChatActivity;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.bitmap.util.Keys;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.sql_interface.Database;
import com.minus.table.GalleryTable;
import com.minus.weather.WeatherActivity;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.AlbumPacketHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class GalleryActivity extends BroadCast implements OnClickListener {

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	
	private  int MAINIMGHEIGHT = 100;
	private    int MAINIMGWIDTH = 150;

	private Builder mBuilder = null;
	private FrameLayout galleryTop = null;
	private ImageView nopicIV = null;
	private FrameLayout contentView = null;
	private ImageView backBtn = null;
	private ImageView addBtn = null;
	@SuppressWarnings("deprecation")
	private Gallery gallery = null;
	GalleryAdapter galleryAdapter = null;
	private QImageView mainImageView = null;
	private RelativeLayout mainRL = null;
	// private ImagePagerAdapter mImagePagerAdapter = null;
	public Bitmap m = null;
	// private List<GalleryTable> list = new ArrayList<GalleryTable>() ;
	private List<String> refreshTimeList = null;
	private Database db = null;
	private MyReceiver receiver = null;
	private MyHandler mHandler = new MyHandler(this);

	private ProgressHUD showErrorOrSucc = null;
	private AlbumAddPopup mAlbumAddPopup = null;
	private AlbumPgPopup mAlbumPgPopup = null;
	private AlbumOperateImgPopup mOperateImgPopup = null;

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 200;
	private static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 100;
//	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;

	private Timer connectTimer = null;
	private MyGestureListener mMyGestureListener = null;

	private int iniPos = 0;
	
	private boolean isGalleryActivityVisible = true;
	
	 private ProgressHUD mRefreshHUD = null;    
	 private CircleProgress sector = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallary_view);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading40h2)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory( false)
				.cacheOnDisk(false).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		if (receiver == null) {
			receiver = new MyReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Protocol.ACTION_ALBUMPACKET);
			filter.addAction(Protocol.ACTION_ALBUM_UploadImageState_PACKET);
			filter.addAction(Protocol.ACTION_ALBUM_GetImage_PACKET);
			filter.addAction(Protocol.ACTION_ALBUM_LastModifyTime_PACKET);
			filter.addAction(Protocol.ACTION_ALBUM_RemoveOneImage_PACKET);
			filter.addAction(Protocol.ACTION_ALBUM_ReturnConfirm_PACKET);
			filter.addAction(Protocol.ACTION_ALBUM_AlbumHomePageState);
			this.registerReceiver(receiver, filter);
		}
		galleryTop = (FrameLayout) findViewById(R.id.galleryTop);
		galleryTop.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					public boolean onPreDraw() {
						// ���������Ѿ�ȷ����

						int curH = GalleryActivity.this.galleryTop.getHeight();

						int albumH = GlobalApplication.getInstance()
								.getAlbumHeight() - curH;

						GlobalApplication.getInstance().setAlbumHeight(albumH);
						galleryTop.getViewTreeObserver()
								.removeOnPreDrawListener(this);
				
					// ��ȡ��Ļ�Ĵ�С
					DisplayMetrics dMetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
					GalleryActivity.this.MAINIMGHEIGHT = dMetrics.widthPixels;
					if(albumH != 0)
						GalleryActivity.this.MAINIMGHEIGHT = albumH;
					else{
						GalleryActivity.this.MAINIMGHEIGHT= dMetrics.heightPixels -curH;
					}
					GlobalApplication.getInstance().setScreenHeigh(dMetrics.heightPixels);
					GlobalApplication.getInstance().setScreenWidth(dMetrics.widthPixels);
					
						return true;
					}
				});
		contentView = (FrameLayout) findViewById(R.id.galleryfl);
		contentView.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					public boolean onPreDraw() {
						// ���������Ѿ�ȷ����

						int curH = GalleryActivity.this.contentView.getHeight();
						int albumH = GlobalApplication.getInstance()
								.getAlbumHeight() - curH;

						GlobalApplication.getInstance().setAlbumHeight(albumH);

						contentView.getViewTreeObserver()
								.removeOnPreDrawListener(this);

						return true;
					}
				});
		AlbumImageHandler.getInstance().setHandler(mHandler);
		db = Database.getInstance(this);
		initView();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isGalleryActivityVisible = true;
		GlobalApplication.getInstance().setAlbumVisible(true);
		iniPos = getIntent().getIntExtra("pos", 0);
		int who = getIntent().getIntExtra("who", 0);
		if (who == 10) {
			this.galleryAdapter.list.clear();
			this.galleryAdapter.list = db.getAllPicture();
			this.galleryAdapter.notifyDataSetChanged();
			this.gallery.setSelection(iniPos, false);
		}else{

		refreshFromServer();
		}

	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.isGalleryActivityVisible = false;
		GlobalApplication.getInstance().setAlbumVisible(false);
		   if(mAlbumAddPopup != null) {
			   mAlbumAddPopup.dismiss(); 
	         }
		   if(mAlbumPgPopup != null) {
			   mAlbumPgPopup.dismiss(); 
	         }
		   if( mOperateImgPopup != null) {
			   mOperateImgPopup.dismiss(); 
	         }   
		
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //��ȡ back��
    		
    		Intent intent = new Intent();
			intent.setClass(GalleryActivity.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return true;
    	}
    	return super.onKeyDown(keyCode, event);
    
	}

	
	public void showProgressHUD(String mess,int totalSize) {
		if(mRefreshHUD ==null){
			mRefreshHUD= ProgressHUD.show(GalleryActivity.this,mess, false,false,null);
			}else{
				mRefreshHUD.dismiss();
				mRefreshHUD.setMessage(mess);
				mRefreshHUD.show();
			}
		 	sector = (CircleProgress)mRefreshHUD.findViewById(R.id.arc);
//		 	����ģʽΪ���ε�
			sector.setType(CircleProgress.SECTOR);
			sector.setVisibility(View.VISIBLE);
			sector.setTargetPg(totalSize);
			 mRefreshHUD.findViewById(R.id.stateImageView).setVisibility(View.GONE);
			 mRefreshHUD.findViewById(R.id.spinnerImageView).setVisibility(View.GONE);	
		
	}
	
	/**
	 * 
	 * @param pg  0--100
	 */
	public void updatePg(int pg){
		 sector.setmSubCurProgress(pg);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	// ����album�ĳ�ʼ״̬
	public void showInitialAlbum(int selectedImageIndex) {
		List<GalleryTable> list = this.galleryAdapter.list;
		if (list.size() > 0 && selectedImageIndex < list.size()) {

			// ԭͼ��·��;
			String oriImagePath = list.get(selectedImageIndex).getOriPath();

			if (AppManagerUtil.isFileExist(oriImagePath)) {
				dismissNoImageViewIfExist();
				// this.mImagePagerAdapter.notifyDataSetChanged();
				// getImageFromSd(oriImagePath,selectedImageIndex,false);
				// mainImageView.setCurrentItem(selectedImageIndex);

				galleryAdapter.notifyDataSetChanged();
				// this.mImagePagerAdapter.notifyDataSetChanged();
				// gallery.setSelection(selectedImageIndex);
				// galleryAdapter.setSelection(selectedImageIndex);
				gallery.setSelection(selectedImageIndex);
				getImageFromSd(oriImagePath, selectedImageIndex, true);
				// if(gallery.getSelectedItemPosition() != selectedImageIndex){
				// gallery.setSelection(selectedImageIndex);
				// }else{
				// imageLoader.stop();
				// getImageFromSd(oriImagePath,selectedImageIndex,true);
				// }
			} else {
				// ������ݿ���������¼�����Ƕ�������ͼƬΪnil��˵��֮ǰд��ʲô֮����д��󣬾�Ҫ��������¼ɾ����;
				removeImageWithIndex(selectedImageIndex, false);
			}

		} else {
			if (!(AlbumImageHandler.getInstance().isUploadingImage())) {
				showNoImageView();
			} else {
				dismissNoImageViewIfExist();
			}
		}
	}

	// ȡ���ϴ�ͼƬ
	public void cancelUploadImage() {
		removeImageWithIndex(0, false);

	}

	// ����û��Ѿ���¼�Ļ�����ôֱ�Ӻͷ�������ϵ,�����Ҫ����һ����ʱ����ÿ��1����һ�Ρ�

	private void refreshFromServer() {
		if (SelfInfo.getInstance().isOnline()) {
			
			// ��ȡ����޸�ʱ��;
			AlbumPacketHandler albumHandler = new AlbumPacketHandler();
			albumHandler.getAlbumLastModifyTime();
		} else {
			if (this.connectTimer != null) {
				this.connectTimer.cancel();
				this.connectTimer = null;
			}
			this.connectTimer = new Timer();
			TimerTask mTimerTask = new TimerTask() {

				@Override
				public void run() {
					GalleryActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							sendInfoToServer();

						}
					});

				}

			};
			connectTimer.schedule(mTimerTask, 1L, 1000L);

		}

	}

	// ����û��Ѿ���¼�Ļ�����ôֱ�Ӻͷ�������ϵ
	public void sendInfoToServer() {
		if (SelfInfo.getInstance().isOnline()) {
			if (this.connectTimer != null) {
				this.connectTimer.cancel();
				this.connectTimer = null;
			}
//            showProgressHUD("�����������·����������Ϣ");
          
			// ��ȡ����޸�ʱ��;
			AlbumPacketHandler albumHandler = new AlbumPacketHandler();
			albumHandler.updateAlbumLastModifyTime();

		}
	}

	@Override
	protected void onDestroy() {
		if (receiver != null) {
			this.unregisterReceiver(receiver);
			receiver = null;
		}

		if (this.connectTimer != null) {
			this.connectTimer.cancel();
			this.connectTimer = null;
		}
		this.imageLoader.cancelDisplayTask(mainImageView);
		this.imageLoader.clearMemoryCache();
		this.imageLoader.clearDiskCache();
		// Drawable d =mainImageView.getDrawable();
		// if(d!=null&&d instanceof BitmapDrawable)
		// {
		// Bitmap bmp=((BitmapDrawable)d).getBitmap();
		// bmp.recycle();
		// bmp=null;
		// }
		mainImageView = null;
		super.onDestroy();
	}

	public void initView() {
		backBtn = (ImageView) findViewById(R.id.galleryBack);
		backBtn.setOnClickListener(this);
		addBtn = (ImageView) findViewById(R.id.galleryRightBtn);
		addBtn.setOnClickListener(this);
		gallery = (Gallery) findViewById(R.id.gallery);

		gallery.setCallbackDuringFling(false);

		nopicIV = (ImageView) findViewById(R.id.nopicIV);
		mainRL = (RelativeLayout) findViewById(R.id.album_view);
		mainImageView = (QImageView) findViewById(R.id.pager);
		// setLongClickable�Ǳ����
		mainImageView.setLongClickable(true);
		mMyGestureListener = new MyGestureListener(this);
		mainImageView.setOnTouchListener(mMyGestureListener);
		mainImageView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mMyGestureListener.setIsLongClick(true);
				if (galleryAdapter.list == null
						|| galleryAdapter.list.size() == 0 || v == null)
					return false;
				if (mOperateImgPopup == null) {
					mOperateImgPopup = new AlbumOperateImgPopup(
							GalleryActivity.this, operateImgOnClick);
					mOperateImgPopup.showAtLocation(
							GalleryActivity.this.findViewById(R.id.album_view),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��

				} else if (!(mOperateImgPopup.isShowing())) {
					mOperateImgPopup.showAtLocation(
							GalleryActivity.this.findViewById(R.id.album_view),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
				} else {
					mOperateImgPopup.dismiss();
				}

				return true;
			}
		});
		mainRL.setOnTouchListener(mMyGestureListener);
		mainRL.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mMyGestureListener.setIsLongClick(true);
				if (galleryAdapter.list == null
						|| galleryAdapter.list.size() == 0 || v == null)
					return false;
				if (mOperateImgPopup == null) {
					mOperateImgPopup = new AlbumOperateImgPopup(
							GalleryActivity.this, operateImgOnClick);
					mOperateImgPopup.showAtLocation(
							GalleryActivity.this.findViewById(R.id.album_view),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��

				} else if (!(mOperateImgPopup.isShowing())) {
					mOperateImgPopup.showAtLocation(
							GalleryActivity.this.findViewById(R.id.album_view),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
				} else {
					mOperateImgPopup.dismiss();
				}

				return true;
			}
		});
		initGalleryAndMainPager();

	}
	
	
	

	private void imageBrower(int position) {
		
		Intent intent = new Intent(GalleryActivity.this,
				GalleryImagePagerActivity.class);
		// intent.putExtra(GalleryImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		// intent.putExtra(GalleryImagePagerActivity.EXTRA_IMAGE_DATES,
		// createDate);
		intent.putExtra(GalleryImagePagerActivity.EXTRA_IMAGE_INDEX, position);
//		startActivity(intent);

		startActivityForResult(intent,R.layout.image_detail_pager);
	}


	private void initGalleryAndMainPager() {
		// ��ȡ������ݿ�������·��
		List<GalleryTable> list = db.getAllPicture();
		galleryAdapter = new GalleryAdapter(this, list);

		gallery.setAdapter(galleryAdapter);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				((GalleryAdapter) gallery.getAdapter())
				.setMainSelection(position);
				scheduleDismissOnScreenControls();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

		if (list.size() > 0) {
			getImageFromSd(list.get(0).getOriPath(), 0, true);
			gallery.setSelection(0, true);
		}else{
			showNoImageView();
		}
		

	}
	/**
	 * ����֣�gallery ��������ڵ���������ֱ������ȥ�ģ���һ��һ����λ��ȥ�ġ�����
	 * 
	 */
    private void scheduleDismissOnScreenControls() {
		
		mHandler.removeCallbacks(seleImgRunnable);
	
		mHandler.postDelayed(seleImgRunnable, 150);
	
		}
	
	private  Runnable seleImgRunnable  = new Runnable(){

		@Override
		public void run() {
			imageLoader.resume();
			int position = ((GalleryAdapter) gallery.getAdapter())
			.getSelection();
			getImageFromSd(galleryAdapter.list.get(position).getOriPath(),
					position, true);
			
		}
		
	};

	public void getImageFromSd(final String path, final int index,
			boolean isUpdate) {

		if (!(isUpdate)) {
			String temp = "file://" + path;
			
			  ImageSize targetSize = new ImageSize(MAINIMGWIDTH, MAINIMGHEIGHT); // result Bitmap will be fit to this size
			 imageLoader.cancelDisplayTask((ImageView)mainImageView);
		
					if (gallery.getSelectedItemPosition() != index) {
						mainImageView.setImageBitmap(imageLoader.loadImageSync(temp, targetSize, options));
//						gallery.setSelection(index);
					} else {
						mainImageView.setImageBitmap(imageLoader.loadImageSync(temp, targetSize, options));
					}

				
		} else {
			String temp = "file://" + path;
			  ImageSize targetSize = new ImageSize(MAINIMGWIDTH, MAINIMGHEIGHT); // result Bitmap will be fit to this size
			 imageLoader.cancelDisplayTask((ImageView)mainImageView);
			 mainImageView.setImageBitmap(imageLoader.loadImageSync(temp, targetSize, options));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.galleryBack:
			Intent intent = new Intent();
			intent.setClass(GalleryActivity.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);

			break;
		case R.id.galleryRightBtn:

			// no pic dialog before
			int tipNum = 100 - galleryAdapter.list.size();
			String tip = "��ỹ���Դ��" + tipNum + "����Ƭ";
			// ʵ����PopupWindow
			if (mAlbumAddPopup == null) {
				mAlbumAddPopup = new AlbumAddPopup(GalleryActivity.this,
						itemsOnClick, tip);
				mAlbumAddPopup.showAtLocation(
						GalleryActivity.this.findViewById(R.id.album_view),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��

			} else if (!(mAlbumAddPopup.isShowing())) {
				mAlbumAddPopup.setTipText(tip);
				mAlbumAddPopup.showAtLocation(
						GalleryActivity.this.findViewById(R.id.album_view),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
			} else {
				mAlbumAddPopup.dismiss();
			}
			break;

		default:
			break;

		}

	}

	// Ϊaddpic ��������ʵ�ּ�����

	private OnClickListener itemsOnClick = new OnClickListener() {
		public void onClick(View v) {
			mAlbumAddPopup.dismiss();
			switch (v.getId()) {
			case R.id.gallery_makePhoto:
				// create Intent to take a picture and return control to the
				// calling application
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create asd
                 
				GlobalApplication.getInstance().setFileUri(fileUri);
				
				
				// file to
				// save the
				// image
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the
																	// image
																	// file name
               
				// start the image capture Intent
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				break;
			case R.id.gallery_comeFromPhone:
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

	// Ϊoperate img ��������ʵ�ּ�����

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
				mBuilder =AppManagerUtil.openAlertDialog(GalleryActivity.this, "",
						"ȷ��Ҫɾ������Ƭ��", "ɾ��", "ȡ��",
						new View.OnClickListener() {

							@Override
							public void onClick(View v){
								// ɾ����Ƭ
								onClickedDeletePic();
								if(mBuilder != null && mBuilder.getDialog()!= null)
									mBuilder.getDialog().dismiss();

							}
						}, new View.OnClickListener() {
							@Override
							public void onClick(View v){
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

	// ����¼� ��main��������
	private void onClickedDisplayHomePage() {
		int selectedIndex = this.galleryAdapter.getSelection();
		AlbumPacketHandler handler = new AlbumPacketHandler();
		handler.setHomePageWithInitDate(galleryAdapter.list.get(selectedIndex)
				.getLastModefyTime());

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

	// ����Ƭ���浽�ֻ��ı��������;
	private void onClickedSaveToLocalPhotoLibrary() {
		int selectedIndex = this.galleryAdapter.getSelection();
		String imagePath = this.galleryAdapter.list.get(selectedIndex)
				.getOriPath();
		saveToSD(imagePath);
//		try {
//			// д���ļ���;
//			Date dateTemp = AppManagerUtil.StrToDate(galleryAdapter.list.get(
//					selectedIndex).getLastModefyTime());
//			String timeStamp = AppManagerUtil.DateToStr(dateTemp);
//			String url = MediaStore.Images.Media.insertImage(
//					getContentResolver(), imagePath, "minius" + timeStamp,
//					"miniusAlbum");
//			if (url == null || url.equals("")) {
//				// ����ʧ��
//				showErrorOrSucc = null;
//				showErrorOrSucc = ProgressHUD.showSuccOrError(
//						GalleryActivity.this, "����ͼƬʧ��", false);
//				mHandler.sendEmptyMessageDelayed(
//						Protocol.HANDLE_ALBUM_DISMISSDIALOG, 3000);
//			} else {
//				showErrorOrSucc = null;
//				showErrorOrSucc = ProgressHUD.showSuccOrError(
//						GalleryActivity.this, "����ͼƬ�ɹ�", true);
//				mHandler.sendEmptyMessageDelayed(
//						Protocol.HANDLE_ALBUM_DISMISSDIALOG, 3000);
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// ���͹㲥
//		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
////		"/sdcard/image.jpg"
//		String fileName = Environment.getExternalStorageDirectory()+"/image.jpg";
//		Uri uri = Uri.fromFile(new File(fileName));
//		intent.setData(uri);
//		sendBroadcast(intent);

	}

	// ���û����ɾ����ʱ��������޸����ݿ���������¼��־ֵ��
	// ֻ�л�÷�����ȷ����Ϣ֮�������ɾ����¼;

	private void onClickedDeletePic() {
		int removeIndex = GalleryActivity.this.galleryAdapter.getSelection();
		removeImageWithIndex(removeIndex, true);

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
				Log.d("MyCameraApp", "failed to create directory");
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

	// ��Ӧ
	public void reponseForSetHomePageState(String state) {

		if (state.equals("Succ")) {

			showErrorOrSucc = null;
			showErrorOrSucc = ProgressHUD.showSuccOrError(GalleryActivity.this,
					"�����ҳ���óɹ�", true);
			mHandler.sendEmptyMessageDelayed(
					Protocol.HANDLE_ALBUM_DISMISSDIALOG, 3000);
		} else {
			showErrorOrSucc = null;
			showErrorOrSucc = ProgressHUD.showSuccOrError(GalleryActivity.this,
					"�����ҳ����ʧ��", false);
			mHandler.sendEmptyMessageDelayed(
					Protocol.HANDLE_ALBUM_DISMISSDIALOG, 3000);
			// [ProgressHUD showError:@"�����ҳ����ʧ��"];
		}
	}

	public void reponseForRemoveOneImage(String initDate) {
		int listSize = galleryAdapter.list.size();
		for (int index = 0; index < listSize; index++) {
			if (galleryAdapter.list.get(index).getLastModefyTime()
					.equals(initDate)) {
				removeImageWithIndex(index, false);
				break;
			}
		}
	}

	// ����ӷ������õ�������޸�ʱ��;
	private void responseForLastModifyTime(String lastModifyTime) {
		// û������޸�ʱ��˵�����״�ʹ�ã��������ϲ�û�м�¼�С�
		if (lastModifyTime.equals("")) {
			this.showNoImageView();
			return;
		}
		SharedPreferences mSP = this.getSharedPreferences(SelfInfo.getInstance().getAccount(),
				Activity.MODE_PRIVATE);
		String key =Protocol.PREFERENCE_AlbumLastModifyTime;
		String localLastModifyTime = mSP.getString(key, "0000-00-00-00:00:00");

		AlbumPacketHandler albumHandler = new AlbumPacketHandler();
		// �����һ���Ļ��������������Ҫ���е�ʱ���б����бȶ�;
		if (!(lastModifyTime.equals(localLastModifyTime))) {
			
			albumHandler.getAlbumTimeList();
		} else {
			// ���һ���Ļ�����ô�ʹ����ط���
			mHandler.sendEmptyMessage(Protocol.HANDLE_ALBUM_DISMISSWAIT);
			albumHandler.dealWithWaitForRemovingImages();
		}
	}

	private boolean isAlbumContain(String initDate) {
		for (GalleryTable mGT : galleryAdapter.list) {
			if (mGT.getLastModefyTime().equals(initDate)) {
				return true;
			}
		}
		return false;
	}

	private boolean isServerTimeList(String[] timeList, String initDate) {
		for (int index = 0; index < timeList.length; index++) {
			if (timeList[index].equals(initDate)) {
				return true;
			}
		}
		return false;
	}

	// ����������ʱ���б�
	private void responseForTimeList(String str) {
		String mess = str.substring(Protocol.HEAD_LEN);
		String[] timeList = mess.split(" ");

		boolean isNeedToRefresh = false; // �����ɾ�����ݵĻ�������������ɾ������֮���Ҫȥˢ��һ��ҳ��;

		// ˵�����������Ѿ�û��ͼƬ�ˣ�Ҫ��ȫ������ɾ����
		if (timeList.length == 1 && timeList[0].equals("")) {
			for (int index = 0; index < galleryAdapter.list.size(); index++) {
				removeImageWithIndex(index, false);
				isNeedToRefresh = true;
				index--;
			}
			// �������֮�����������ȡһ����д�뱾�ص�����޸�ʱ��;
			AlbumPacketHandler albumHandler = new AlbumPacketHandler();
			albumHandler.updateAlbumLastModifyTime();
			return;
		}

		// �������У�����û�У���Ҫ���������ȡ;
		for (int index = 0; index < timeList.length; index++) {
			if (!(isAlbumContain(timeList[index]))) {
				AlbumPacketHandler albumHandler = new AlbumPacketHandler();
				albumHandler.getImageWithInitDate(timeList[index]);
				if (refreshTimeList == null) {
					refreshTimeList = new ArrayList<String>();
				}
				refreshTimeList.add(timeList[index]);
			}
		}
		if(refreshTimeList != null && refreshTimeList.size() >0){
			 showProgressHUD("�����������·����������Ϣ",refreshTimeList.size());
		}
		// ������û�У������У���Ҫɾ��;
		for (int index = 0; index < this.galleryAdapter.list.size(); index++) {
			GalleryTable dict = galleryAdapter.list.get(index);
			if (!(isServerTimeList(timeList, dict.getLastModefyTime()))) {
				removeImageWithIndex(index, false);
				isNeedToRefresh = true;
				index--;
			}
		}
		if (isNeedToRefresh) {
			galleryAdapter.list = db.getAllPicture();
			galleryAdapter.notifyDataSetChanged();
			if (galleryAdapter.list.size() >= 0) {
				showInitialAlbum(0);
				

			}
			// this.mImagePagerAdapter.notifyDataSetChanged();
			// pager.getAdapter().notifyDataSetChanged();

		}else{
			// �������֮�����������ȡһ����д�뱾�ص�����޸�ʱ��;
			AlbumPacketHandler albumHandler = new AlbumPacketHandler();
			albumHandler.updateAlbumLastModifyTime();
		}
	}

	// ���յ��ӷ�������������ͼƬ;
	private int count = 0;
//	private int updatecount = 0; //���ڼ���ÿ���յ�2��ͼƬ����һ��view
	private void responseForGetImage(String str) {
		if (galleryAdapter.list.size() == 0) {
			dismissNoImageViewIfExist();
		}
		String[] arr = str.split(" ");
		String iniDate = arr[0];
		String thumImgPath = arr[1];
		String oriImgPath = arr[2];

		GalleryTable mGT = new GalleryTable();
		mGT.setDeleteStatus(0);
		mGT.setLastModefyTime(iniDate);
		mGT.setOriPath(oriImgPath);
		mGT.setPath(thumImgPath);
		galleryAdapter.list.add(0, mGT);
		boolean needupdateView = true;

		// ��Ҫ���ж��ǲ����û������������ȡ�ġ�
				if (refreshTimeList != null) {
					if (refreshTimeList.get(0).equals(iniDate)) {
						
						refreshTimeList.remove(0);
						count++;
//						updatecount++;
						this.sector.setMPg(count);
					}
					
					// ˵�����������ȡ�Ķ��Ѿ�ȫ��������,��Ҫ���������ȡһ����д������޸�ʱ��.
					if (refreshTimeList.size() == 0) {
						refreshTimeList = null;
						AlbumPacketHandler handler = new AlbumPacketHandler();
						handler.updateAlbumLastModifyTime();
						mHandler.sendEmptyMessage(Protocol.HANDLE_ALBUM_DISMISSWAIT);

					}else{
						needupdateView = false;
					}
				}
//				if(updatecount >=2){
//					mHandler.sendEmptyMessage(Protocol.HANDLE_ALBUM_DISMISSWAIT);
//					needupdateView = true;
//					updatecount =0;
//				}
				if(needupdateView){
		this.galleryAdapter.notifyDataSetChanged();
		this.gallery.setSelection(0);
		getImageFromSd(galleryAdapter.list.get(0).getOriPath(), 0, true);
				}
		
	}

	// public void refresh

	private void responseForUploadImageState(String uploadImageState) {

		if (uploadImageState.equals("UploadImageFail")) {
			ProgressHUD mP = ProgressHUD.showSuccOrError(
					getApplicationContext(), "ͼƬ�ϴ�ʧ��", false);
			removeImageWithIndex(0, false);
		
			// ����ϴ�ʧ����ô�Ͱѵ�һ��ɾ���ˡ�
		}
	}

	// ɾ��ָ���±��ͼƬ;
	public void removeImageWithIndex(int removeIndex,
			boolean isWaitForServerConfirm) {
		int listSize = this.galleryAdapter.list.size();
		if (removeIndex >= listSize || removeIndex < 0)
			return;
		GalleryTable dict = galleryAdapter.list.get(removeIndex);

		String imagePath = dict.getPath();
		String oriImagePath = dict.getOriPath();

		File f = new File(imagePath);
		File f1 = new File(oriImagePath);

		// ��������ļ������ڵĻ�����ô��Ҫɾ���ɹ����У�������ļ������ڵĻ�����ô�Ϳ���ֱ�Ӱ����ݿ�ɾ�����ɡ�
		// ֻ��ɳ���������ļ����ɹ�ɾ��֮�󣬲Ž�һ��ȥɾ����������
		if (!(AppManagerUtil.deleteFile(f) && AppManagerUtil.deleteFile(f1))) {
			return;
		}

		// ��Ҫ�ȴ�������ȷ��֮���������ɾ���ļ�¼����ֻ�Ǹı����ݿ����¼�ı�־ֵ;����Ļ����ͻ����������ݿ��аѸü�¼ɾ����;
		if (isWaitForServerConfirm) {
			AlbumPacketHandler albumHandler = new AlbumPacketHandler();
			albumHandler.removeImageWithInitDate(dict.getLastModefyTime());
			db.SetStatusForPic(dict.getLastModefyTime(), 1);// //�Ѹ���Ƭ�ĳ�����ɾ����״̬

		} else {
			db.deleteAlbumPicture(dict.getLastModefyTime());
		}

		galleryAdapter.list.remove(removeIndex);

		// ���ɾ��֮��û��ͼƬ��
		if (galleryAdapter.list.size() == 0) {
			showNoImageView();
		} else {
			int nextIndex = removeIndex;

			// �����ɾ�����һ������ô�ͺ���һ��;
			if (removeIndex == galleryAdapter.list.size()) {
				nextIndex--;
			}
			galleryAdapter.notifyDataSetChanged();
			// this.mImagePagerAdapter.notifyDataSetChanged();
			gallery.setSelection(nextIndex);
			galleryAdapter.setMainSelection(nextIndex);
			getImageFromSd(galleryAdapter.list.get(nextIndex).getOriPath(), 0,
					false);
		}
	}

	private void showNoImageView() {
		if (this.nopicIV == null) {
			this.nopicIV = (ImageView) findViewById(R.id.nopicIV);
		}
		this.nopicIV.setVisibility(View.VISIBLE);
		this.contentView.setVisibility(View.GONE);
		this.mainImageView.setVisibility(View.GONE);
	}

	public void dismissNoImageViewIfExist() {
		if (this.nopicIV != null) {
			this.nopicIV.setVisibility(View.GONE);
			this.contentView.setVisibility(View.VISIBLE);
			this.mainImageView.setVisibility(View.VISIBLE);
		}

	}

	// �ϴ�ͼƬ���
	// �ϴ�ͼƬ����ȷ��
	public void responseForUploadConfirmProgress() {
		AlbumImageHandler.getInstance().responseForUploadConfirmProgress();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE
				&& resultCode == RESULT_OK && null != data) {
			// no pic dialog before
			if (mAlbumPgPopup == null) {
				mAlbumPgPopup = new AlbumPgPopup(GalleryActivity.this,
						"Ŭ���ϴ��С�����");
				mAlbumPgPopup.setPg(0);
				mAlbumPgPopup.showAtLocation(
						GalleryActivity.this.findViewById(R.id.album_view),
						Gravity.CENTER, 0, 0); // ����layout��PopupWindow����ʾ��λ��

			} else if (!(mAlbumPgPopup.isShowing())) {
				mAlbumPgPopup.setPg(0);
				mAlbumPgPopup.setTipText("Ŭ���ϴ��С�����");
		
				mAlbumPgPopup.showAtLocation(
						GalleryActivity.this.findViewById(R.id.album_view),
						Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
			} else {
				mAlbumPgPopup.dismiss();
				mAlbumPgPopup.setPg(0);
				mAlbumPgPopup.setTipText("Ŭ���ϴ��С�����");
				mAlbumPgPopup.showAtLocation(
						GalleryActivity.this.findViewById(R.id.album_view),
						Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
			}

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);

			cursor.close();
			TimeConsumingTask t = new TimeConsumingTask();
			t.execute(picturePath);

		}

		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
//				Toast.makeText(this, "Image saved to:\n" + fileUri.getPath(),
//						Toast.LENGTH_LONG).show();
				// no pic dialog before
				if (mAlbumPgPopup == null) {
					mAlbumPgPopup = new AlbumPgPopup(GalleryActivity.this,
							"Ŭ���ϴ��С�����");
					mAlbumPgPopup.setPg(0);
					mAlbumPgPopup.showAtLocation(
							GalleryActivity.this.findViewById(R.id.album_view),
							Gravity.CENTER, 0, 0); // ����layout��PopupWindow����ʾ��λ��

				} else if (!(mAlbumPgPopup.isShowing())) {
					mAlbumPgPopup.setPg(0);
					mAlbumPgPopup.setTipText("Ŭ���ϴ��С�����");
			
					mAlbumPgPopup.showAtLocation(
							GalleryActivity.this.findViewById(R.id.album_view),
							Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
				} else {
					mAlbumPgPopup.dismiss();
					mAlbumPgPopup.setPg(0);
					mAlbumPgPopup.setTipText("Ŭ���ϴ��С�����");
					mAlbumPgPopup.showAtLocation(
							GalleryActivity.this.findViewById(R.id.album_view),
							Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
				}

				String picturePath = GlobalApplication.getInstance().getFileUri().getPath();

				TimeConsumingTask t = new TimeConsumingTask();
				t.execute(picturePath);
				// TODO

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}

		 if (requestCode == R.layout.image_detail_pager){
		 //imagepager ����
		 int pos = data.getIntExtra("pos", 0);
		 this.galleryAdapter.list.clear();
			this.galleryAdapter.list = db.getAllPicture();
			this.galleryAdapter.notifyDataSetChanged();
			this.gallery.setSelection(pos, true);
		
		
		 }
	}

	// ������յ�����޸�ʱ����¼��б�,���߻�ȡ�ɹ�������Ϣ
	public void processStringResponse(String str) {
		char operatorCode = 0;
		try {
			operatorCode = (char) (str.getBytes("UTF-8"))[3];
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		switch (operatorCode) {
		case Protocol.ALBUM_RETURN_TIME_LIST:
			responseForTimeList(str);
			break;

		default:
			break;
		}
	}

	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Protocol.ACTION_ALBUMPACKET.equals(action)) {
				if (intent.getStringExtra(Protocol.EXTRA_DATA) != null) {
					// �����û����ݰ� ����,������Ϣ,��handler���½���
					String data = intent.getStringExtra(Protocol.EXTRA_DATA);
					Bundle bdata = new Bundle();
					bdata.putString("data", data);
					Message msg = mHandler.obtainMessage();
					msg.setData(bdata);

					msg.what = Protocol.HANDLE_RESPON;
					mHandler.sendMessage(msg);
				}
			}

			if (Protocol.ACTION_ALBUM_UploadImageState_PACKET.equals(action)) {
				if (intent.getStringExtra(Protocol.EXTRA_DATA) != null) {
					String uploadImageState = intent
							.getStringExtra(Protocol.EXTRA_DATA);
					responseForUploadImageState(uploadImageState);

				}

			}

			if (Protocol.ACTION_ALBUM_GetImage_PACKET.equals(action)) {
				if (intent.getStringExtra(Protocol.EXTRA_DATA) != null) {
					String imageContent = intent
							.getStringExtra(Protocol.EXTRA_DATA);
					responseForGetImage(imageContent);

				}

			}

			if (Protocol.ACTION_ALBUM_LastModifyTime_PACKET.equals(action)) {
				if (intent.getStringExtra(Protocol.EXTRA_DATA) != null) {
					String lastModifyTime = intent
							.getStringExtra(Protocol.EXTRA_DATA);
					responseForLastModifyTime(lastModifyTime);

				}
			}
			if (Protocol.ACTION_ALBUM_RemoveOneImage_PACKET.equals(action)) {
				if (intent.getStringExtra(Protocol.EXTRA_DATA) != null) {
					String initDate = intent
							.getStringExtra(Protocol.EXTRA_DATA);
					reponseForRemoveOneImage(initDate);

				}
			}
			if(isGalleryActivityVisible){

			if (Protocol.ACTION_ALBUM_ReturnConfirm_PACKET.equals(action)) {

				// �ϴ�����ȷ��
				responseForUploadConfirmProgress();

			}
			if (Protocol.ACTION_ALBUM_AlbumHomePageState.equals(action)) {
				// ������ҳ״̬
				String state = intent.getStringExtra(Protocol.EXTRA_DATA);
				reponseForSetHomePageState(state);
			}
			}

		} // onReceive
	}

	private static class MyHandler extends Handler {
		WeakReference<GalleryActivity> mActivity;

		MyHandler(GalleryActivity galleryActivity) {
			mActivity = new WeakReference<GalleryActivity>(galleryActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			GalleryActivity theActivity = mActivity.get();
			// String mData = msg.getData().getString("data");
			switch (msg.what) {
			case Protocol.HANDLE_RESPON:
				// TODO
				String mData = msg.getData().getString("data");
				theActivity.processStringResponse(mData);
				break;
			case Protocol.HANDLE_ALBUM_CANCELUPLOADIMG:
				theActivity.cancelUploadImage();
				break;
			case Protocol.HANDLE_ALBUM_SETPROGRESSBAR:
				int pgBase = msg.getData().getInt("pgbase");
				int pg = msg.getData().getInt("pg");
				int num = (int) ((pg * 100) / pgBase);
				if (theActivity.mAlbumPgPopup.isShowing()) {
					theActivity.mAlbumPgPopup.setPg(num);
				}
				break;
			case Protocol.HANDLE_ALBUM_UPLOADIMGFINISH:
				// ���½����� ������ʾ
				theActivity.mAlbumPgPopup.setPg(100);
				if (theActivity.mAlbumPgPopup.isShowing()) {
					theActivity.mAlbumPgPopup.setTipText("�ϴ��ɹ���");
				}
				break;
			case Protocol.HANDLE_ALBUM_CANCELPG:
				if (theActivity.mAlbumPgPopup.isShowing()) {
					theActivity.mAlbumPgPopup.dismiss();
				}

				break;
			case Protocol.HANDLE_ALBUM_DISMISSDIALOG:
				if (theActivity.showErrorOrSucc != null) {
					if (theActivity.showErrorOrSucc.isShowing()) {
						theActivity.showErrorOrSucc.dismiss();
						theActivity.showErrorOrSucc = null;
					}
				}

				break;
			case Protocol.HANDLE_ALBUM_DISMISSWAIT:
			if (theActivity.mRefreshHUD != null)
				theActivity.mRefreshHUD.dismiss();
			break;
			case Protocol.HANDLE_ALBUM_SAVETOLOCALSUCC:
				String toastPath = msg.getData().getString("toast");
		    	Toast.makeText(theActivity.getApplicationContext(), "ͼƬ�Ѿ����浽"+toastPath, Toast.LENGTH_LONG).show(); 
		    	break;
			case Protocol.HANDLE_ALBUM_SAVETOLOCALFAIL:
				 Toast.makeText(theActivity.getApplicationContext(), "ͼƬ����ʧ��", Toast.LENGTH_LONG).show();
			    	
				break;
				
			default:
				break;
			}
		}
	}

	private class GalleryAdapter extends BaseAdapter {

		private Context context = null;
		private List<GalleryTable> list = null;
		private int mainSelection = 0;

		public GalleryAdapter(Context context, List<GalleryTable> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;

		}

		@Override
		public int getCount() {
			return this.list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void setMainSelection(int position) {
			// if(selection != position){
			// this.selection = position % this.list.size();
			// getImageFromSd(list.get(selection).getOriPath(),selection,false);
			// }

			this.mainSelection = position % this.list.size();

		}

		public int getSelection() {
			return this.mainSelection;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			String path = "file://"
					+ this.list.get(position % this.list.size()).getPath();
			if (imageView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(
						R.layout.item_gallery_image, parent, false);
			}
			imageLoader.displayImage(path, imageView, options);

			return imageView;

		}

	}

	public class TimeConsumingTask extends
			AsyncTask<String, Integer, GalleryTable> {

		@Override
		protected void onPreExecute() {
			if (mAlbumPgPopup == null) {
				mAlbumPgPopup = new AlbumPgPopup(GalleryActivity.this,
						"Ŭ���ϴ��С�����");
				mAlbumPgPopup.setPg(0);
				mAlbumPgPopup.showAtLocation(
						GalleryActivity.this.findViewById(R.id.album_view),
						Gravity.CENTER, 0, 0); // ����layout��PopupWindow����ʾ��λ��

			} else if (!(mAlbumPgPopup.isShowing())) {
				mAlbumPgPopup.setPg(0);
				mAlbumPgPopup.setTipText("Ŭ���ϴ��С�����");
				mAlbumPgPopup.showAtLocation(
						GalleryActivity.this.findViewById(R.id.album_view),
						Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
			} else {
				mAlbumPgPopup.dismiss();
				mAlbumPgPopup.setPg(0);
				mAlbumPgPopup.setTipText("Ŭ���ϴ��С�����");
				mAlbumPgPopup.showAtLocation(
						GalleryActivity.this.findViewById(R.id.album_view),
						Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0); // ����layout��PopupWindow����ʾ��λ��
			}
			super.onPreExecute();
		}

		@Override
		protected GalleryTable doInBackground(String... params) {
			GalleryTable dict = null;
			try {
				// ���沢�ҷ���ͼƬ
				dict = AlbumImageHandler.getInstance().saveAndSendImage(
						params[0]);
				// publishProgress(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return dict;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(GalleryTable result) {

			if (galleryAdapter.list.size() == 0) {
				dismissNoImageViewIfExist();
			}
			if (result != null) {
				GalleryActivity.this.galleryAdapter.list.add(0, result);
				galleryAdapter.notifyDataSetChanged();
				// this.mImagePagerAdapter.notifyDataSetChanged();
				gallery.setSelection(0);
				galleryAdapter.setMainSelection(0);
				getImageFromSd(result.getOriPath(), 0,
						false);
				// mImagePagerAdapter.notifyDataSetChanged();
				// galleryAdapter.setSelection(0);
//				GalleryActivity.this.gallery.setSelection(0, true);
				
//				��
//				getImageFromSd(result.getOriPath(), 0, true);

			}

			super.onPostExecute(result);
		}

	}

	/**
	 * �̳�GestureListener����дleft��right����
	 */
	private class MyGestureListener extends GestureListener {
		private boolean isLongClick = false;

		public MyGestureListener(Context context) {
			super(context);
		}

		public void setIsLongClick(boolean isLongClick) {
			this.isLongClick = isLongClick;
		}

		@Override
		public boolean left() {
			Log.e("test", "����");
			imageLoader.pause();
			int cur = GalleryActivity.this.gallery.getSelectedItemPosition();
			if (cur < (GalleryActivity.this.galleryAdapter.list.size() - 1)) {
				GalleryActivity.this.gallery.setSelection(cur + 1, true);
				// GalleryActivity.this.galleryAdapter.setSelection(cur+1);
			}

			return super.left();
		}

		@Override
		public boolean right() {
			Log.e("test", "���һ�");
			imageLoader.pause();
			int cur = GalleryActivity.this.gallery.getSelectedItemPosition();
			if (cur > 0) {
				GalleryActivity.this.gallery.setSelection(cur - 1, true);
				// GalleryActivity.this.galleryAdapter.setSelection(cur-1);
			}

			return super.right();
		}

		@Override
		public boolean clickView() {
			if (!(isLongClick)) {
				if (galleryAdapter.list.size() <= 0)
					return false;

				int curPos = gallery.getSelectedItemPosition();
				imageBrower(curPos);
			} else {
				isLongClick = false;
			}

			return super.clickView();
		}
	}

}
