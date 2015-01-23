package com.minus.weather;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.minius.common.CommonBitmap;
import com.minius.ui.ProgressHUD;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.RegisterActivity;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.setting.ConfigActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.RoundedImageView;
import com.minus.sql_interface.Database;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.LocationHandler;
import com.minus.xsocket.handler.UserPacketHandler;



public class WeatherActivity extends BroadCast  implements OnGeocodeSearchListener,AMapLocationListener, 
                                 Runnable  {

	private LocationManagerProxy mAMapLocManager = null;
//	private TextView myLocation;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler locationHandler = new Handler();
	
	private ImageView backButton = null;
//	private ImageView myHeadphotoframe;
//	private ImageView tarHeadphotoframe;
	private ImageView myHeadphoto = null;
	private ImageView targetHeadphoto = null;
	
	private TextView myTodayCity = null;
	private TextView targetCity = null;
//	private TextView targetNextCity = null;
//	private TextView targetNext2City = null;
	
	private TextView myTodayTemper = null;
	private TextView myNextdayTemper = null;
	private TextView myNext2datTemper = null;		
	private TextView targetTodayTemper = null;
	private TextView targetNextdayTemper = null;
	private TextView targetNext2datTemper = null;
	
	private TextView myTodayDate = null;
	private TextView myNextdayDate = null;
	private TextView myNext2datDate = null;		
	private TextView targetTodayDate = null;
	private TextView targetNextdayDate = null;
	private TextView targetNext2datDate = null;
	
	private ImageView myTodayWeather = null;
	private ImageView myNextdayWeather = null;
	private ImageView myNext2dayWeather = null;
	private ImageView targetTodayWeather = null;
	private ImageView targetNextdayWeather = null;
	private ImageView targetNext2dayWeather = null;
	
	private TextView targetTodayWeek = null;

	private WeatherUtils weatherUtils = null;
//	private Dialog loadingDiary = null;
	
   // myLocation format:latitude,longitude
    private String myLocation = null;
    public  String myAcc = null;
    public  double myLat = 0.0;
    public  double myLng = 0.0;
    private String targetLocation = null;
    private int[] DefHeadimageIds = { R.drawable.girl_photoframe,
    		 R.drawable._0002_girl_photo,  R.drawable.boy_photoframe,
			R.drawable._0003_boy_photo };
    //
    private GeocodeSearch geocoderSearch;
//	private Bitmap myHeadBm;
//	private Bitmap tarHeadBm;
    private ProgressHUD waitingHUD = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);
		
		myAcc = SelfInfo.getInstance().getAccount();
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		
		  if(receiver == null)
		   		 receiver = new MyReceiver();
		   		 IntentFilter filter=new IntentFilter();   
		   			filter.addAction(Protocol.ACTION_LOCATIONPACKET);
		               this.registerReceiver(receiver,filter); 
		  
		               mAMapLocManager = LocationManagerProxy.getInstance(this);
		       		/*
		       		 * mAMapLocManager.setGpsEnable(false);//
		       		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		       		 * API定位采用GPS和网络混合定位方式
		       		 * ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		       		 */
		       		mAMapLocManager.requestLocationUpdates(
		       				LocationProviderProxy.AMapNetwork, 5000, 10, this);
		       		locationHandler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
		       		
	     

		weatherUtils = new WeatherUtils(this);
		initView();
		LoadHeadphoto();		
		
//		loadingDiary = WaitForTheWeatherInfo();
//		loadingDiary.show();
		showProgressHUD("天气加载中");
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		myLocation = null;
		myAcc = null;
		targetLocation = null;
		if(receiver != null)
		{  
			this.unregisterReceiver(receiver);
			receiver = null;
		}
//		if(myHeadBm != null && !myHeadBm.isRecycled()){ 
	        // 回收并且置为null
//			myHeadBm.recycle(); 
//			myHeadBm = null; 
//		} 
//		if(tarHeadBm != null && !tarHeadBm.isRecycled()){ 
	        // 回收并且置为null
//			tarHeadBm.recycle(); 
//			tarHeadBm = null; 
//		}
		super.onDestroy();
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		stopLocation();// 停止定位
	}
	/**
	 * 解析我的天气信息
	 */
	public  void startReadMyWeather(){
		new Thread(){
				public void run(){
					   
					boolean b= false;
//					Log.v("weather", "startReadMyWeather 01");
					b = weatherUtils.GetMyWeather(myLocation);// myTodayCity.getText().toString()
//					Log.v("weather", "startReadMyWeather 02  b "  );
					if(b) mHandler.sendEmptyMessage(Protocol.HANDLE_READ_MYWEATHER_SUCC);
					else{
						mHandler.sendEmptyMessage(Protocol.HANDLE_READ_MYWEATHER_FAIL);
					}
				}
			}.start();
		
	}
	/**
	 * 解析配对方的天气信息
	 */
	public  void startReadTarWeather(){
		new Thread(){
				public void run(){
					boolean b= false;
					b = weatherUtils.GetTarWeather(targetLocation);
					
					if(b) mHandler.sendEmptyMessage(Protocol.HANDLE_READ_TARWEATHER_SUCC);
					else{
						mHandler.sendEmptyMessage(Protocol.HANDLE_READ_TARWEATHER_FAIL);
					}
				}
			}.start();
		
	}

	public void showProgressHUD(String mess) {
		if (waitingHUD == null) {
			waitingHUD = ProgressHUD.show(WeatherActivity.this, mess, true, true,
					null);
		} else {
			if (waitingHUD.isShowing()) {
				waitingHUD.dismiss();
			}

			waitingHUD.setMessage(mess);
			waitingHUD.show();

		}
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
	/**
	 * Get the headphoto
	 */
	public void LoadHeadphoto(){
		//load the headphoto


		if(CommonBitmap.getInstance().getMyHeadBm()!=null)
			myHeadphoto.setImageBitmap(AppManagerUtil.createBitmapBySize(
					CommonBitmap.getInstance().getMyHeadBm(),50,50));//myHeadBm);
		else{
			Resources r = getResources();
			
			if (SelfInfo.getInstance().getSex().equals("b")) {
	
				myHeadphoto.setImageDrawable(r.getDrawable(DefHeadimageIds[3]));
			} else {
				myHeadphoto.setImageDrawable(r.getDrawable(DefHeadimageIds[1]));
			}
			
		}
		
//		tarHeadBm = AppManagerUtil.createBitmapBySize(GlobalApplication.getInstance()
//				.getTarHeadPicBm(),5,5);
//		if(!GlobalApplication.getInstance().getTiTargetHeadPhoPath().equals(String.format("%c", Protocol.DEFAULT)))
//			tarHeadBm =GlobalApplication.getInstance().getTarHeadPicBm();//tarHeadBm);
//		if (tarHeadBm != null) {
//
//			} else {
//				// no myheadpoto use default
//				
//			}
		if(GlobalApplication.getInstance().getTarHeadPicBm()!=null)
			targetHeadphoto.setImageBitmap(AppManagerUtil.createBitmapBySize(
				GlobalApplication.getInstance().getTarHeadPicBm(),50,50));//tarHeadBm
		else{
			Resources r = getResources();
			if (GlobalApplication.getInstance().getTiSex().equals("b")) {
				
				targetHeadphoto.setImageDrawable(r.getDrawable(DefHeadimageIds[3]));
			} else {
				targetHeadphoto.setImageDrawable(r.getDrawable(DefHeadimageIds[1]));
			}
		}
	}
	
	/**
	 * Get the myLocation and targetLocation
	 */
	public void  setMyLocation(String newMyLocation,String newMyCityName){

		
//		String newMyLocation = "22.644,+114.199";
//		String newTargetLocation = "30.276,+120.150";
//		String newMyCityName = "广州";
//		String newTargetCityName = "杭州";
		
		if(!(newMyLocation.equals(myLocation))){
			myLocation = newMyLocation;
			myTodayCity.setText(newMyCityName);
			SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount()
					, Activity.MODE_PRIVATE);
			SharedPreferences.Editor mEditor = mSP.edit();
			mEditor.putString("myTodayCity", newMyCityName);
			mEditor.commit();
//			myNextCity.setText(newMyCityName);
//			myNext2City.setText(newMyCityName);
			}
		}
	/**
	 * 配对方的位置
	 */
public void  setTarLocation(String newTargetLocation){
    if(!(newTargetLocation.equals(targetLocation))){
			targetLocation = newTargetLocation;
}
}
/**
 * 进入程序初始的提示滑动条
 */
	public Dialog WaitForTheWeatherInfo(){
		
		 LayoutInflater inflater = LayoutInflater.from(this);  
	     View v = inflater.inflate(R.layout.weahter_download,null);
	     ProgressBar layout = (ProgressBar) v.findViewById(R.id.weather_download);
		Dialog loadingDialog = new Dialog(this,R.style.MyDialogStyle); 	  
        loadingDialog.setCancelable(true);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT));
        
        return loadingDialog;
	}
	/**
	 * 成功读取我的天气信息，处理
	 */
  public void handleGetMyWeatherSucc(){	
	  
	  SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSP.edit();
		mEditor.putString("myTodayTemper",weatherUtils.weatherTable.getMyTodayTemp() );
		mEditor.putString("myNextdayTemper",weatherUtils.weatherTable.getMyNextdayTemp() );
		mEditor.putString("myNext2datTemper",weatherUtils.weatherTable.getMyNext2dayTemp() );
		
		mEditor.putString("myTodayDate",weatherUtils.weatherTable.getMyTodayDate() );
		mEditor.putString("myNextdayDate",weatherUtils.weatherTable.getMyNextdayDate() );
		mEditor.putString("myNext2datDate",weatherUtils.weatherTable.getMyNext2dayDate() );
		
		mEditor.putString("myTodayWeather",weatherUtils.weatherTable.getMyTodayCode() );
		mEditor.putString("myNextdayWeather",weatherUtils.weatherTable.getMyNextdayCode() );
		mEditor.putString("myNext2dayWeather",weatherUtils.weatherTable.getMyNext2dayCode() );
		
//		  mEditor.putString("TargetTodayCode",weatherUtils.weatherTable.getTargetTodayCode() );
		  mEditor.commit();
		  
		myTodayTemper.setText(weatherUtils.weatherTable.getMyTodayTemp());
		myNextdayTemper.setText(weatherUtils.weatherTable.getMyNextdayTemp());
		myNext2datTemper.setText(weatherUtils.weatherTable.getMyNext2dayTemp());	
		
		myTodayDate.setText(weatherUtils.weatherTable.getMyTodayDate());
	    myNextdayDate.setText(weatherUtils.weatherTable.getMyNextdayDate());
	    myNext2datDate.setText(weatherUtils.weatherTable.getMyNext2dayDate());		
//	    targetTodayWeek.setText(weatherUtils.weatherTable.getMyTodayDay());
	
	    myTodayWeather.setImageDrawable(weatherUtils.ImageViewChange(weatherUtils.weatherTable.getMyTodayCode(), "my")); 
	    myNextdayWeather.setImageDrawable(weatherUtils.ImageViewChange(weatherUtils.weatherTable.getMyNextdayCode(), "my")); 
	    myNext2dayWeather.setImageDrawable(weatherUtils.ImageViewChange(weatherUtils.weatherTable.getMyNext2dayCode(), "my")); 
//	    Toast.makeText(this, "我的天气加载完成！", Toast.LENGTH_SHORT).show();
	 }
  /**
	 * 成功读取配对方的天气信息，处理
	 */
  public void handleGetTarWeatherSucc(){	
//	  Database.getInstance(GlobalApplication.getInstance().getApplicationContext()).updateTargetWeather(
//				SelfInfo.getInstance().getTarget(), weatherUtils.weatherTable.getTargetTodayCode());
	  SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSP.edit();
		mEditor.putString("targetTodayTemper",weatherUtils.weatherTable.getTargetTodayTemp() );
		mEditor.putString("targetNextdayTemper",weatherUtils.weatherTable.getTargetNextdayTemp() );
		mEditor.putString("targetNext2datTemper",weatherUtils.weatherTable.getTargetNext2dayTemp() );
		mEditor.putString("targetTodayWeek",weatherUtils.weatherTable.getTargetTodayDay() );
		
		mEditor.putString("targetTodayDate",weatherUtils.weatherTable.getTargetTodayDate() );
		mEditor.putString("targetNextdayDate",weatherUtils.weatherTable.getTargetNextdayDate() );
		mEditor.putString("targetNext2datDate",weatherUtils.weatherTable.getTargetNext2dayDate() );
		
		
//		if(mSP.getString("targetTodayWeather", "-1").equals("-1")||
//		!mSP.getString("targetTodayWeather", "-1").equals(weatherUtils.weatherTable.getTargetTodayCode()))
//			mEditor.putBoolean("weatherChange", true);
//		else
//			mEditor.putBoolean("weatherChange", false);
		mEditor.putString("targetTodayWeather",weatherUtils.weatherTable.getTargetTodayCode() );
		mEditor.putString("targetNextdayWeather",weatherUtils.weatherTable.getTargetNextdayCode() );
		mEditor.putString("targetNext2datWeather",weatherUtils.weatherTable.getTargetNext2dayCode() );
		
//		  mEditor.putString("TargetTodayCode",weatherUtils.weatherTable.getTargetTodayCode() );
		  mEditor.commit();
		targetTodayTemper.setText(weatherUtils.weatherTable.getTargetTodayTemp());
		targetNextdayTemper.setText(weatherUtils.weatherTable.getTargetNextdayTemp());
		targetNext2datTemper.setText(weatherUtils.weatherTable.getTargetNext2dayTemp());	
		targetTodayWeek.setText(weatherUtils.weatherTable.getTargetTodayDay());
		
	    targetTodayDate.setText(weatherUtils.weatherTable.getTargetTodayDate());
	    targetTodayDate.setVisibility(View.VISIBLE);
	    targetNextdayDate.setText(weatherUtils.weatherTable.getTargetNextdayDate());
	    targetNext2datDate.setText(weatherUtils.weatherTable.getTargetNext2dayDate());
	    
	    targetTodayWeather.setImageDrawable(weatherUtils.ImageViewChange(weatherUtils.weatherTable.getTargetTodayCode(), "target")); 
	    targetNextdayWeather.setImageDrawable(weatherUtils.ImageViewChange(weatherUtils.weatherTable.getTargetNextdayCode(), "target")); 
	    targetNext2dayWeather.setImageDrawable(weatherUtils.ImageViewChange(weatherUtils.weatherTable.getTargetNext2dayCode(), "target"));
//	    Toast.makeText(this, "Ta的天气加载完成！", Toast.LENGTH_SHORT).show();
}
    
  /**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}
    
	/**
	 * 加载控件
	 */
	public void initView(){
		
		backButton = (ImageView)findViewById(R.id.weather_back);
//		myHeadphotoframe=(ImageView)findViewById(R.id.weather_my_headphotoframe);
//		tarHeadphotoframe=(ImageView)findViewById(R.id.weather_target_headphotoframe);
		myHeadphoto = (ImageView)findViewById(R.id.weather_my_headphoto);
		targetHeadphoto = (ImageView)findViewById(R.id.weather_target_headphoto);
		
		
		myTodayCity = (TextView)findViewById(R.id.weather_my_location);
		targetCity = (TextView)findViewById(R.id.weather_target_location);
//		targetNextCity = (TextView)findViewById(R.id.weather_target_locatoin1);
//		targetNext2City = (TextView)findViewById(R.id.weather_target_location2);
		
		myTodayTemper = (TextView)findViewById(R.id.weather_my_today);
		myNextdayTemper = (TextView)findViewById(R.id.weather_my_next1);
		myNext2datTemper = (TextView)findViewById(R.id.weather_my_next2);		
		targetTodayTemper = (TextView)findViewById(R.id.weather_target_today);
		targetNextdayTemper = (TextView)findViewById(R.id.weather_target_next1);
		targetNext2datTemper = (TextView)findViewById(R.id.weather_target_next2);
		
		myTodayDate = (TextView)findViewById(R.id.weather_my_date);
	    myNextdayDate = (TextView)findViewById(R.id.weather_my_date1);
	    myNext2datDate = (TextView)findViewById(R.id.weather_my_date2);		
	    targetTodayDate = (TextView)findViewById(R.id.weather_target_date);
	    targetNextdayDate = (TextView)findViewById(R.id.weather_target_date1);
	    targetNext2datDate = (TextView)findViewById(R.id.weather_target_date2);
	
	    myTodayWeather = (ImageView)findViewById(R.id.weatherphoto_my_today); 
	    myNextdayWeather = (ImageView)findViewById(R.id.weatherphoto_my_next1);
	    myNext2dayWeather = (ImageView)findViewById(R.id.weatherphoto_my_next2);
	    targetTodayWeather = (ImageView)findViewById(R.id.weatherphoto_target_today);
	    targetNextdayWeather = (ImageView)findViewById(R.id.weatherphoto_target_next1);
	    targetNext2dayWeather =  (ImageView)findViewById(R.id.weatherphoto_target_next2);
	    
	    targetTodayWeek = (TextView)findViewById(R.id.weather_target_week);
	    
	    SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount(), Activity.MODE_PRIVATE);
//		mSP.getString("TargetTodayCode","-1");
	    myTodayCity.setText(mSP.getString("myTodayCity"," "));
	    targetCity.setText(mSP.getString("targetCity"," "));
//	    targetNextCity.setText(mSP.getString("targetNextCity"," "));
//	    targetNext2City.setText(mSP.getString("targetNext2City"," "));
	    
	    myTodayTemper.setText(mSP.getString("myTodayTemper"," "));
	    myNextdayTemper.setText(mSP.getString("myNextdayTemper"," "));
	    myNext2datTemper.setText(mSP.getString("myNext2datTemper"," "));
	    
	    myTodayDate.setText(mSP.getString("myTodayDate"," "));
	    myNextdayDate.setText(mSP.getString("myNextdayDate"," "));
	    myNext2datDate.setText(mSP.getString("myNext2datDate"," "));
		
	    myTodayWeather.setImageDrawable(weatherUtils.ImageViewChange(mSP.getString("myTodayWeather","32"), "my"));
	    myNextdayWeather.setImageDrawable(weatherUtils.ImageViewChange(mSP.getString("myNextdayWeather","32"), "my"));
	    myNext2dayWeather.setImageDrawable(weatherUtils.ImageViewChange(mSP.getString("myNext2dayWeather","32"), "my"));
	    
	    targetTodayTemper.setText(mSP.getString("targetTodayTemper"," "));
	    targetNextdayTemper.setText(mSP.getString("targetNextdayTemper"," "));
	    targetNext2datTemper.setText(mSP.getString("targetNext2datTemper"," "));
	    
	    targetTodayDate.setText(mSP.getString("targetTodayDate"," "));//mSP.getString("targetTodayDate"," ")
	    targetTodayDate.setVisibility(View.INVISIBLE);
	    targetNextdayDate.setText(mSP.getString("targetNextdayDate"," "));
	    targetNext2datDate.setText(mSP.getString("targetNext2datDate"," "));
		targetTodayWeek.setText(mSP.getString("targetTodayWeek"," "));
		
	    targetTodayWeather.setImageDrawable(weatherUtils.ImageViewChange(mSP.getString("targetTodayWeather","32"), "target"));
	    targetNextdayWeather.setImageDrawable(weatherUtils.ImageViewChange(mSP.getString("targetNextdayWeather","32"), "target"));
	    targetNext2dayWeather.setImageDrawable(weatherUtils.ImageViewChange(mSP.getString("targetNext2datWeather","32"), "target"));
	    
	    
	    backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	/**
	 * 处理响应数据包
	 * @param str
	 */
	 public void  processResponse(String str)
		{
			 char operatorCode = 0;
				try {
					operatorCode = (char) (str.getBytes("UTF-8"))[3];
				} catch (UnsupportedEncodingException e) {
				
					e.printStackTrace();
				}

//	         Log.v("result", "map operatorCode " +(byte)operatorCode);
				switch(operatorCode){
				case Protocol.GET_USER_NOEXIST_RES: 
//					if(loadingDiary.isShowing())
//						loadingDiary.cancel();
					targetCity.setText("No Match");
					break;
				
				case Protocol.GET_MATCH_USER_LOCATION_NOEXIST_RES:
//					if(loadingDiary.isShowing())
//						loadingDiary.cancel();
					targetCity.setText("Match user has no location");
					new AlertDialog.Builder(WeatherActivity.this).setCancelable(true).setTitle("提示")
					.setMessage("对方暂时还没有上传信息哦")
					.setPositiveButton("我知道了", null)
					.setNegativeButton("取消", null).create().show();
					break;
				case Protocol.GET_LOCATION_SUCC_RES: //返回配对位置
				     String[] tLoc;
                  tLoc = str.substring(Protocol.HEAD_LEN).split(" ");
//                  Log.v("result","target location rece " +tLoc[1] +" " +tLoc[2] );
                  LatLonPoint latLonPoint = new LatLonPoint(Double.valueOf(tLoc[2]),Double.valueOf(tLoc[1]));
                  RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
          				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
          		 geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
                  setTarLocation(tLoc[1]+","+tLoc[2]);
               startReadTarWeather();
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
//				Log.v("result","@@##on receiver " + action);
				if(Protocol.ACTION_LOCATIONPACKET.equals(action)){
					//读到用户数据包 数据,发送消息,让handler更新界面
					
					String data = intent.getStringExtra(Protocol.EXTRA_DATA);
                 
					Bundle bdata = new Bundle();
					bdata.putString("data", data);
					Message msg = mHandler.obtainMessage();				
					msg.setData(bdata);
					

					msg.what =Protocol.HANDLE_RESPON;
					mHandler.sendMessage(msg);
				   
				}
			
			}	//onReceive
		} 
		
		
		private class MyHandler extends Handler
		{
			WeakReference<WeatherActivity> mActivity;  
			 MyHandler(WeatherActivity weatherActivity) {  
	             mActivity = new WeakReference<WeatherActivity>(weatherActivity);  
	     }  
			@Override
			public void handleMessage(Message msg)
			{
				WeatherActivity theActivity = mActivity.get();  
				String mData = msg.getData().getString("data");
				switch (msg.what)
				{
					
					case Protocol.HANDLE_RESPON:
					  theActivity.processResponse(mData);
						break;
					case Protocol.HANDLE_READ_MYWEATHER_FAIL:
//						if(theActivity.loadingDiary.isShowing())
//						theActivity.loadingDiary.cancel();
						Toast.makeText(theActivity, "读取我的天气失败", Toast.LENGTH_SHORT).show();
						break;
					case Protocol.HANDLE_READ_MYWEATHER_SUCC:
						theActivity.handleGetMyWeatherSucc();
					    LocationHandler mReq = new LocationHandler();
					    mReq.UploadPosition(theActivity.myAcc, (float)theActivity.myLat,(float) theActivity.myLng);
						break;
					case Protocol.HANDLE_READ_TARWEATHER_FAIL:
//						if(theActivity.loadingDiary.isShowing())
//							theActivity.loadingDiary.cancel();
						Toast.makeText(theActivity, "读取ta的天气失败", Toast.LENGTH_SHORT).show();
						break;
					case Protocol.HANDLE_READ_TARWEATHER_SUCC:
//						if(theActivity.loadingDiary.isShowing())
//							theActivity.loadingDiary.cancel();
						if (theActivity.waitingHUD != null)
							theActivity.waitingHUD.dismiss();
						theActivity.handleGetTarWeatherSucc();
						break;
					default:
						break;
				}
			}
		}
		
	 private MyReceiver receiver = null; 
	 private MyHandler mHandler = new MyHandler(this);

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			Toast.makeText(this, "12秒内还没有定位成功，停止定位", Toast.LENGTH_SHORT).show();
//			if(loadingDiary.isShowing())
//				loadingDiary.cancel();
			stopLocation();// 销毁掉定位
		}
	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			
			this.aMapLocation = location;// 判断超时机制
			myLat = location.getLatitude();
		    myLng = location.getLongitude();
		    setMyLocation(myLng+","+myLat,location.getCity());
//		    Log.v("weather", "onLocationChanged 001"+location.getCity());
		    this.startReadMyWeather();
		    stopLocation();// 销毁掉定位
		    }
		
	}



	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		String newTargetCityName = "";
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				
				newTargetCityName = result.getRegeocodeAddress().getCity();
//				Log.v("weather", "target city "+newTargetCityName );
			} else {
				newTargetCityName= "no result ";
			}
		} else if (rCode == 27) {
			newTargetCityName= "error_network";
		
		} else if (rCode == 32) {
			newTargetCityName= "error_network";
		} else {
			newTargetCityName= "未知错误";
		}
		targetCity.setText(newTargetCityName);
		//targetNextCity.setText(newTargetCityName);
		//targetNext2City.setText(newTargetCityName);
		SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSP.edit();
		mEditor.putString("targetCity", newTargetCityName);
		//mEditor.putString("targetNextCity", newTargetCityName);
		//mEditor.putString("targetNext2City", newTargetCityName);
		mEditor.commit();
	}
}
