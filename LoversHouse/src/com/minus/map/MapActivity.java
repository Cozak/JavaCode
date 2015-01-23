package com.minus.map;


import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.minius.ui.CustomDialog.Builder;
import com.minus.lovershouse.R;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.RoundedImageView;
import com.minus.xsocket.asynsocket.AsynSocket;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.HeartPacketHandler;
import com.minus.xsocket.handler.LocationHandler;
import com.minus.xsocket.handler.UserPacketHandler;

public class MapActivity extends BroadCast implements OnClickListener,OnMarkerClickListener,
OnInfoWindowClickListener, OnMarkerDragListener, OnMapLoadedListener,InfoWindowAdapter ,LocationSource,
AMapLocationListener {
	
	private ImageView backImgBtn;
	private ImageView refreshImagBtn;
	

//	private LayerDrawable tarHeadLayerDrawable;
//	private LayerDrawable myHeadLayerDrawable;

	private int[] DefHeadimageIds = { R.drawable.girl_photoframe,
   		 R.drawable._0002_girl_photo,  R.drawable.boy_photoframe,
			R.drawable._0003_boy_photo };

	private int[] MapBackground = {
			R.drawable.map_background,R.drawable.map_boy_background};
	
	private AMap aMap;
	private MapView mapView;
	private Marker marker2;// 有跳动效果的marker对象

	private String curTime;
	private double targetLat=36.06;
	private double targetLog =103.834;
	private  float myLat = (float) 22.644;
	private float myLog = (float) 114.199;
//	Drawable mylayers ;//new Drawable;
//	Drawable tarlayers;//new Drawable;
	private Bitmap myHeadBm=null;
	private Bitmap tarHeadBm=null;
	
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private ImageView imgSource1;
	private RoundedImageView imgSource2;
	private RelativeLayout contentLayout;
	private RelativeLayout contentLayout_2;
	private ImageView imgSource2_1;
	private RoundedImageView imgSource2_2;
	private Builder ibuilder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
//		LocationHandler mLoc= new LocationHandler();
//		mLoc.UploadPosition("aaa@qq.com",11.2f ,12.3f);
		mapView = (MapView) findViewById(R.id.map);
		contentLayout = (RelativeLayout) findViewById(R.id.content);
		imgSource1 = (ImageView) findViewById(R.id.imgSource1);
		imgSource2 = (RoundedImageView) findViewById(R.id.imgSource2);
		contentLayout_2 = (RelativeLayout) findViewById(R.id.content_2);
		imgSource2_1 = (ImageView) findViewById(R.id.imgSource2_1);
		imgSource2_2 = (RoundedImageView) findViewById(R.id.imgSource2_2);
		mapView.onCreate(savedInstanceState); // 此方法必须重写
		  if(receiver == null)
		   		 receiver = new MyReceiver();
		   		 IntentFilter filter=new IntentFilter();   
		   			filter.addAction(Protocol.ACTION_LOCATIONPACKET);
		               this.registerReceiver(receiver,filter); 
		               
		initMyheadPic();
		initTarheadPic();
		init();
	}
//	   private void destoryBimap() {  
//	        if (myHead== null && !myHead.isRecycled()) {  
//	        	myHead.recycle();  
//	        	myHead = null;  }
//	        }  
	   
	private void initMyheadPic() {
		
		if(SelfInfo.getInstance().getSex().equals("b")){
			
			imgSource1.setBackgroundResource(MapBackground[1]);
			      
		}else{
			imgSource1.setBackgroundResource(MapBackground[0]);
		}
		

		Bitmap myHeadBmtemp = 
				AppManagerUtil.createBitmapBySize(AppManagerUtil.getDiskBitmap(Environment.getExternalStorageDirectory()  
                + "/LoverHouse"+"/HeadPhoto"+"/"+SelfInfo.getInstance().getAccount() + ".png"),55,55);
		
//		myHeadBm =GlobalApplication.getInstance().getHeadPicBm();
		Resources r = getResources();
		
		if (myHeadBmtemp != null) {

//			if (SelfInfo.getInstance().getSex().equals("b")) //{
//				mylayers = r.getDrawable(DefHeadimageIds[2]);
//			} else {
//				mylayers = r.getDrawable(DefHeadimageIds[0]);
//			}
//			mylayers = new BitmapDrawable(r, myHeadBm);
		} else {
			// no myheadpoto use default
			if (SelfInfo.getInstance().getSex().equals("b")) {
//				mylayers[0] = r.getDrawable(DefHeadimageIds[2]);
//				mylayers = r.getDrawable(DefHeadimageIds[3]);
				myHeadBmtemp=drawableToBitmap(r.getDrawable(DefHeadimageIds[3]));
			} else {
//				mylayers[0] = r.getDrawable(DefHeadimageIds[0]);
//				mylayers = r.getDrawable(DefHeadimageIds[1]);
				myHeadBmtemp=drawableToBitmap(r.getDrawable(DefHeadimageIds[1]));
			}
		}

		imgSource2.setImageBitmap(myHeadBmtemp);
		
		contentLayout.setDrawingCacheEnabled(true);
		contentLayout.measure(
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		contentLayout.layout(0, 0, contentLayout.getMeasuredWidth(),
				contentLayout.getMeasuredHeight());

		contentLayout.buildDrawingCache();
		
		myHeadBm= AppManagerUtil.createBitmapBySize(contentLayout.getDrawingCache(),50,80);
		imgSource2.setImageBitmap(null);
//		contentLayout=null;
		myHeadBmtemp.recycle();
		myHeadBmtemp=null;
//		if (tarHeadBm != null) {
//
////			if (SelfInfo.getInstance().getSex().equals("b")) //{
////				tarlayers[1] = r.getDrawable(DefHeadimageIds[2]);
////			} else {
////				tarlayers[1] = r.getDrawable(DefHeadimageIds[0]);
////			}
////			tarlayers = new BitmapDrawable(r, tarHeadBm);
//		} else {
//			// no tarheadpoto use default
//			if (SelfInfo.getInstance().getSex().equals("b")) {
////				tarlayers = r.getDrawable(DefHeadimageIds[2]);
////				tarlayers = r.getDrawable(DefHeadimageIds[3]);
//				tarHeadBm=drawableToBitmap(r.getDrawable(DefHeadimageIds[3]));
//			} else {
////				tarlayers[0] = r.getDrawable(DefHeadimageIds[0]);
////				tarlayers = r.getDrawable(DefHeadimageIds[1]);
//				tarHeadBm=drawableToBitmap(r.getDrawable(DefHeadimageIds[1]));
//			}
//		}

//		myHeadLayerDrawable = new LayerDrawable(mylayers);
//		tarHeadLayerDrawable = new LayerDrawable(tarlayers);
	}
private void initTarheadPic() {
		
	if(GlobalApplication.getInstance().getTiSex().equals("b")){
		
		imgSource2_1.setBackgroundResource(MapBackground[1]);
		      
	}else{
		imgSource2_1.setBackgroundResource(MapBackground[0]);
	}
//		tarHeadBm = AppManagerUtil.createBitmapBySize(GlobalApplication.getInstance()
//				.getTarHeadPicBm(),5,5);
	Bitmap tarHeadBmtemp=null;
	tarHeadBmtemp=AppManagerUtil.createBitmapBySize(
			AppManagerUtil.getDiskBitmap(Environment.getExternalStorageDirectory()  
            + "/LoverHouse"+"/HeadPhoto"+"/"+GlobalApplication.getInstance().getTiAcc() + ".png"),55,55);
//	if(!GlobalApplication.getInstance().getTiTargetHeadPhoPath().equals(String.format("%c", Protocol.DEFAULT)))
//			tarHeadBmtemp =AppManagerUtil.createBitmapBySize(
//					GlobalApplication.getInstance().getTarHeadPicBm(),55,55);
		Resources r = getResources();
		
		if (tarHeadBmtemp != null) {

		} else {
			// no tarheadpoto use default
			if (GlobalApplication.getInstance().getTiSex().equals("b")) {
				tarHeadBmtemp=drawableToBitmap(r.getDrawable(DefHeadimageIds[3]));
			} else {
				tarHeadBmtemp=drawableToBitmap(r.getDrawable(DefHeadimageIds[1]));
			}
		}
		imgSource2_2.setImageBitmap(tarHeadBmtemp);
		
		contentLayout_2.setDrawingCacheEnabled(true);
		contentLayout_2.measure(
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		contentLayout_2.layout(0, 0, contentLayout.getMeasuredWidth(),
				contentLayout_2.getMeasuredHeight());

		contentLayout_2.buildDrawingCache();
		tarHeadBm= AppManagerUtil.createBitmapBySize(contentLayout_2.getDrawingCache(),50,80);
		imgSource2_2.setImageBitmap(null);
//		contentLayout=null;
		tarHeadBmtemp.recycle();
		tarHeadBmtemp=null;
	}
	/**
	 * 初始化AMap对象
	 */
	private void init() {
	    this.backImgBtn = (ImageView) this.findViewById(R.id.map_back);
	    this.backImgBtn.setOnClickListener(this);
	    this.refreshImagBtn = (ImageView) this.findViewById(R.id.map_refresh);
	    this.refreshImagBtn.setOnClickListener(this);
		
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		LocationManager locationManager = (LocationManager)getBaseContext().  
		getSystemService(Context.LOCATION_SERVICE);  
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			/*
			new AlertDialog.Builder(this).setCancelable(true).setMessage("未打开GPS，是否设置？")
  			.setPositiveButton("设置", new DialogInterface.OnClickListener()
  		      {
  		        public void onClick(DialogInterface paramDialogInterface, int paramInt)
  		        {
  		        	Intent intent = new Intent();  
  		          intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
  		          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
  		          try   
  		          {  
  		              getBaseContext().startActivity(intent);  
  		                        
  		                
  		          } catch(ActivityNotFoundException ex)   
  		          {  
  		                
  		              // The Android SDK doc says that the location settings activity  
  		              // may not be found. In that case show the general settings.  
  		                
  		              // General settings activity  
  		              intent.setAction(Settings.ACTION_SETTINGS);  
  		              try {  
  		                     getBaseContext().startActivity(intent);  
  		              } catch (Exception e) {  
  		              }  
  		          }  
  		        	
  		        }
  		      }).setNegativeButton("取消", null).create().show();
  		      */
			ibuilder = new com.minius.ui.CustomDialog.Builder(MapActivity.this);
			ibuilder.setTitle(null);
			ibuilder.setMessage("未打开GPS，是否设置？");
			ibuilder.setPositiveButton("设置", gpsHelp);
			ibuilder.setNegativeButton("取消", null);
			ibuilder.create().show();
		}
			
	}
	private View.OnClickListener gpsHelp = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.confirm_btn:
				Intent intent = new Intent();  
		          intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
		          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		          try   
		          {  
		              getBaseContext().startActivity(intent);  
		                        
		                
		          } catch(ActivityNotFoundException ex)   
		          {  
		                
		              // The Android SDK doc says that the location settings activity  
		              // may not be found. In that case show the general settings.  
		                
		              // General settings activity  
		              intent.setAction(Settings.ACTION_SETTINGS);  
		              try {  
		                     getBaseContext().startActivity(intent);  
		              } catch (Exception e) {  
		              }  
		          }  
		          if(ibuilder.getDialog()!=null)
						ibuilder.getDialog().dismiss();
				break;
			
			default:
				break;
			}
		}

	};
	
	
	public  Bitmap drawableToBitmap(Drawable drawable) {       

        Bitmap bitmap = Bitmap.createBitmap(

                                        drawable.getIntrinsicWidth(),

                                        drawable.getIntrinsicHeight(),

                                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                                                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

}

	private void setUpMap() {
		aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
		aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
	
		
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		try{
			myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(myHeadBm));//myHeadLayerDrawable
			} catch (OutOfMemoryError e) {
				if (SelfInfo.getInstance().getSex().equals("b")) {

					myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(DefHeadimageIds[3]));
				} else {
					myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(DefHeadimageIds[1]));
				}
			}
//		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(myHeadBm));// 设置小蓝点的图标myHeadLayerDrawable

		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		// myLocationStyle.radiusFillColor(color)//设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(0.1f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setMyLocationRotateAngle(180);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		mListener = null;
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(receiver != null)
		{  
			this.unregisterReceiver(receiver);
			receiver = null;
		}
		if(myHeadBm != null && !myHeadBm.isRecycled()){ 
	        // 回收并且置为null
//			myHeadBm.recycle(); 
			myHeadBm = null; 
		} 
		if(tarHeadBm != null && !tarHeadBm.isRecycled()){ 
	        // 回收并且置为null
//			tarHeadBm.recycle(); 
			tarHeadBm = null; 
		} 
		if(mapView!=null)
			mapView.onDestroy();
//		System.gc();
		
	}

	
	public void drawMarkers(double tLat,double tLog,double mLat, double mLog) {
	
		initTarheadPic();
		StringBuilder  msg= new StringBuilder();
		LatLng targetlatlng = new LatLng(tLat,tLog);
		LatLng mLatLng = new LatLng(mLat,mLog);
		float distance = AMapUtils.calculateLineDistance(targetlatlng, mLatLng);
//		String curTime = AppManagerUtil.getDate();
		if(distance > 500){
			if(distance>1000){
				DecimalFormat df = new DecimalFormat("#.00");
				msg.append("我们相距").append(df.format(distance/1000)).append("公里");
			}else
				msg.append("我们相距").append(distance).append("米");
		}else{
			 msg.append("我们靠得很近，能听见彼此的心跳.");
		}

//		 marker2 = aMap.addMarker(new MarkerOptions()
//				.position(targetlatlng)
//				.title(curTime).snippet(msg.toString())
//				.icon(BitmapDescriptorFactory
//						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//				.perspective(true).draggable(true));
//		 BitmapDescriptorFactory.fromBitmap(drawableToBitmap(myHeadLayerDrawable))
//		tarBitmap=drawableToBitmap(tarlayers);
		try{
		 marker2 = aMap.addMarker(new MarkerOptions()
			.position(targetlatlng)
			.title(curTime).snippet(msg.toString())
			.icon(BitmapDescriptorFactory.fromBitmap(tarHeadBm))//tarHeadLayerDrawable
			.perspective(true).draggable(true));
		} catch (OutOfMemoryError e) {
			 marker2 = aMap.addMarker(new MarkerOptions()
				.position(targetlatlng)
				.title(curTime).snippet(msg.toString())
//				.icon(BitmapDescriptorFactory.fromBitmap(tarHeadBm))//tarHeadLayerDrawable
				.perspective(true).draggable(true));
			 if (GlobalApplication.getInstance().getTiSex().equals("b")) {
				 marker2.setIcon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(getResources().getDrawable(DefHeadimageIds[3]))));
				} else {
					marker2.setIcon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(getResources().getDrawable(DefHeadimageIds[1]))));
				}
		}
		//marker2.setRotateAngle(90);// 设置marker旋转90度
		marker2.showInfoWindow();// 设置默认显示一个infowinfow
//		aMap.addPolyline((new PolylineOptions())
//				.add(new LatLng(targetlatlng.latitude,targetlatlng.longitude)
//					,new LatLng(myLat ,myLog))
//				.geodesic(true).color(Color.RED));
		LatLngBounds bounds = new LatLngBounds.Builder()
		.include(mLatLng)
		.include(targetlatlng).build();
		
//			CameraUpdateFactory.zoomBy((float)100);
		
			
		if(distance>500)
			aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
		else
			aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
//		float scale = aMap.getScalePerPixel(); 
//		if(scale<10)
//			aMap.moveCamera(CameraUpdateFactory.zoomTo(10));
		}



	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(final Marker marker) {
		if (marker.equals(marker2)) {
			if (aMap != null) {
				jumpPoint(marker);
			}
		}
	
		return false;
	}

	/**
	 * marker点击时跳动一下
	 */
	public void jumpPoint(final Marker marker) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = aMap.getProjection();
		LatLng targetlatlng = new LatLng(targetLat,targetLog);
		Point startPoint = proj.toScreenLocation(targetlatlng);
		startPoint.offset(0, -100);
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 1500;

		final Interpolator interpolator = new BounceInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * targetLog + (1 - t)
						* startLatLng.longitude;
				double lat = t *targetLat + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});
	}

	/**
	 * 监听点击infowindow窗口事件回调
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
//		Toast.makeText(getApplicationContext(), "你点击了infoWindow窗口", Toast.LENGTH_LONG).show();
		
	}

	/**
	 * 监听拖动marker时事件回调
	 */
	@Override
	public void onMarkerDrag(Marker marker) {

	}

	/**
	 * 监听拖动marker结束事件回调
	 */
	@Override
	public void onMarkerDragEnd(Marker marker) {

	}

	/**
	 * 监听开始拖动marker事件回调
	 */
	@Override
	public void onMarkerDragStart(Marker marker) {

	}

	/**
	 * 监听amap地图加载成功事件回调
	 */
	@Override
	public void onMapLoaded() {
		// 设置所有maker显示在当前可视区域地图中
//		LatLngBounds bounds = new LatLngBounds.Builder()
//				.include(Constants.XIAN).include(Constants.CHENGDU)
//				.include(targetlatlng).build();
//		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
	}

	/**
	 * 监听自定义infowindow窗口的infocontents事件回调
	 */
	@Override
	public View getInfoContents(Marker marker) {
		
		View infoContent = getLayoutInflater().inflate(
				R.layout.custom_info_contents, null);
		render(marker, infoContent);
		return infoContent;
	}

	/**
	 * 监听自定义infowindow窗口的infowindow事件回调
	 */
	@Override
	public View getInfoWindow(Marker marker) {
	
		View infoWindow = getLayoutInflater().inflate(
				R.layout.custom_info_window, null);

		render(marker, infoWindow);
		return infoWindow;
	}

	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {
		String title = marker.getTitle();
		TextView titleUi = ((TextView) view.findViewById(R.id.title));
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
					titleText.length(), 0);
			titleUi.setTextSize(15);
			titleUi.setText(titleText);

		} else {
			titleUi.setText("");
		}
		String snippet = marker.getSnippet();
		TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
		if (snippet != null) {
			SpannableString snippetText = new SpannableString(snippet);
			
			snippetText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
					snippetText.length(), 0);
			snippetUi.setTextSize(10);
			snippetUi.setText(snippetText);
		} else {
			snippetUi.setText("");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.map_refresh:
				aMap.clear();//bitmap也可能被recycle了
				if(myHeadBm.isRecycled()){
					myHeadBm = null; 
					initMyheadPic();
				}
				if(tarHeadBm!=null&&tarHeadBm.isRecycled()){
					tarHeadBm = null; 
					initTarheadPic();
				}
//				MyLocationStyle myLocationStyle = new MyLocationStyle();
//				try{
//				myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(myHeadBm));//myHeadLayerDrawable
//				} catch (OutOfMemoryError e) {
//					if (SelfInfo.getInstance().getSex().equals("b")) {
//
//						myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(DefHeadimageIds[3]));
//					} else {
//						myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(DefHeadimageIds[1]));
//					}
//				}
//				myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
//				myLocationStyle.strokeWidth(0.1f);// 设置圆形的边框粗细
//				
//				aMap.setMyLocationStyle(myLocationStyle);
//			aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
//			aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
			setUpMap();
//			LocationHandler mLoc= new LocationHandler();
//			String acc = SelfInfo.getInstance().getAccount();
			
//			 Log.v("location","myacc locat succ  01"+ acc + " " + myLat +", "+ myLog);
//			  drawMarkers( targetLat,targetLog,myLat, myLog);
//			mLoc.UploadPosition(acc,myLat ,myLog);//上传自己的位置信息
			break;
		case R.id.map_back:
//			 Intent intent = new Intent();
//             intent.setClass(this,MainActivity.class);
//             startActivity(intent);
//             overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
			aMap.clear();
//			if(myHeadBm != null && !myHeadBm.isRecycled()){ 
//		        // 回收并且置为null
//				myHeadBm.recycle(); 
//				myHeadBm = null; 
//			} 
//			if(tarHeadBm != null && !tarHeadBm.isRecycled()){ 
//		        // 回收并且置为null
//				tarHeadBm.recycle(); 
//				tarHeadBm = null; 
//			} 
			finish();
			break;

			
		default:
			break;
		}
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
	
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			float bearing = aMap.getCameraPosition().bearing;
			aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
			
			myLat = (float) aLocation.getLatitude();
			myLog = (float) aLocation.getLongitude();
			
			
			//TODO  float double differ with c++
			LocationHandler mLoc= new LocationHandler();
			String acc = SelfInfo.getInstance().getAccount();
			
//			 Log.v("location","myacc locat succ  01"+ acc + " " + myLat +", "+ myLog);
//			  drawMarkers( targetLat,targetLog,myLat, myLog);
			mLoc.UploadPosition(acc,myLat ,myLog);//上传自己的位置信息
			deactivate();
			
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 10000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}
	
	 public void  processResponse(String str)
		{
			 char operatorCode = 0;
				try {
					operatorCode = (char) (str.getBytes("UTF-8"))[3];
				} catch (UnsupportedEncodingException e) {
				
					e.printStackTrace();
				}
//				 Log.v("location","processResponse " +(byte)operatorCode +" = 43 no match user");
	    
				switch(operatorCode){
				case Protocol.GET_USER_NOEXIST_RES: 
					break;
				
				case Protocol.GET_MATCH_USER_LOCATION_NOEXIST_RES:
//					 hercity.text=@"No Match";
//			            hertodaydate.text=@"";
//			            hertodaytempature.text=@"";
//			            hertomorrowdate.text=@"";
//			            hertomorrowtempature.text=@"";
//			            herthirddate.text=@"";
//			            herthirdtempature.text=@"";
					break;
				case Protocol.GET_LOCATION_SUCC_RES: //返回配对位置
				     String[] tLoc;
                     tLoc = str.substring(Protocol.HEAD_LEN).split(" ");
//                     Log.v("location","target location rece " +tLoc[0] +" " +tLoc[1] );
//                     try{
                     curTime = AppManagerUtil.transformDisplayMap(tLoc[0]);
                     targetLog = Double.valueOf(tLoc[1]);
                     targetLat=Double.valueOf(tLoc[2]);
                     drawMarkers( targetLat,targetLog,myLat, myLog);
//                     }catch(Exception e){
//                    	 
//                     }

					break;
			      default:
			    	  break;
		 
		            
		    }
		}

		 
		 //-------------------------------------------------------------------------------------------------------------
		 public class MyReceiver extends BroadcastReceiver  
			{  
				@Override  
				public void onReceive(Context context, Intent intent)
				{  
					String action = intent.getAction();   
//					Log.v("result","@@##on receiver " + action);
					if(Protocol.ACTION_LOCATIONPACKET.equals(action)){
						//读到用户数据包 数据,发送消息,让handler更新界面
						
						String data = intent.getStringExtra(Protocol.EXTRA_DATA);
	                 
						Bundle bdata = new Bundle();
						bdata.putString("data", data);
						Message msg = mHandler.obtainMessage();				
						msg.setData(bdata);
						
//						byte packetType = 0;
//						try {
//							packetType = (data.getBytes("UTF-8"))[2];
//						} catch (UnsupportedEncodingException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						switch(packetType){
//						case Protocol.USER_PACKAGE:
//							Log.v("result","USER_PACKAGE in main ");
//							msg.what =Protocol.HANDLE_REQ;
//							mHandler.sendMessage(msg);
//						break;
//						
//						default:
//							msg.what =Protocol.HANDLE_RESPON;
//							mHandler.sendMessage(msg);
//							break;
//						}
						msg.what =Protocol.HANDLE_RESPON;
						mHandler.sendMessage(msg);
					   
					}
				
				}	//onReceive
			} 
			
			
			private static class MyHandler extends Handler
			{
				WeakReference<MapActivity> mActivity;  
				 MyHandler(MapActivity mapActivity) {  
		             mActivity = new WeakReference<MapActivity>(mapActivity);  
		     }  
				@Override
				public void handleMessage(Message msg)
				{
					MapActivity theActivity = mActivity.get();  
					String mData = msg.getData().getString("data");
					switch (msg.what)
					{
						
						case Protocol.HANDLE_RESPON:
							//TODO
							theActivity.processResponse(mData);
							break;
						default:
							break;
					}
				}
			}
			
		 private MyReceiver receiver = null; 
		 private MyHandler mHandler = new MyHandler(this);
}
