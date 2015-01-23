package com.minus.weather;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import com.minus.lovershouse.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.widget.Toast;


public class WeatherUtils {
	
	public Context context = null;
	public WeatherTable weatherTable = null;
	
	public WeatherUtils(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		weatherTable = new WeatherTable();
	}
	
	/**
	 * This function is used to get WOEID And Weather
	 * @param MyLocation
	 * @param TargetLocation
	 * @return WeatherTable
	 */
	public boolean GetMyWeather(String MyLocation){

//		String myWOEID = GetTheWOEID(MyLocation);
//		if(myWOEID == null ){
//			return false;
//		}
//		String myWOEID ="2161838";
//		int retryTimes=0;
//		do{
			if(GetWeatherFromBaidu(MyLocation,"my"))
				return true;
//		}
//		while(retryTimes++<3);
		return false;
	}
	
	public boolean GetTarWeather(String TargetLocation){
//		String targetWOEID = GetTheWOEID(TargetLocation);
//		
//		if(targetWOEID == null){
//			return false;
//		}
//		System.out.println("get targetWOEID" + targetWOEID);
		
		if(GetWeatherFromBaidu(TargetLocation,"target")){
			return true;
		}
		return false;
	}
	
	private	boolean isMobile(){  
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	    if(cm!=null){  
	        NetworkInfo  ni = cm.getActiveNetworkInfo();  
	        if(ni !=null && ni.getType() ==  ConnectivityManager.TYPE_MOBILE)
	        {
	         // NETWORK_TYPE_EVDO_A是电信3G
	        //NETWORK_TYPE_EVDO_A是中国电信3G的getNetworkType
	        //NETWORK_TYPE_CDMA电信2G是CDMA
	        //移动2G卡 + CMCC + 2//type = NETWORK_TYPE_EDGE
	        //联通的2G经过测试 China Unicom   1 NETWORK_TYPE_GPRS

		        int netSubtype=ni.getSubtype();
		        if(netSubtype ==TelephonyManager.NETWORK_TYPE_EDGE
		        		||netSubtype == TelephonyManager.NETWORK_TYPE_CDMA
		        		 ||netSubtype == TelephonyManager.NETWORK_TYPE_GPRS
		        				 ){
		        	return true;
		        }
	            
	        	
	        
	        }
	    }
	    return false;
	}  
	/*
	private String GetTheWOEID(String Location){
		
		String Latitude = Location.substring(0,Location.indexOf(","));
		String Longitude = Location.substring(Location.indexOf(",")+1, Location.length()-1);
		String WOEID = null;
		HttpURLConnection MyUrlconnetction=null;
		
		try {
			URL MyUrl = new URL("http://query.yahooapis.com/v1/public/yql?" +
					"q=select%20*%20from%20geo.placefinder%20where%20text=%22" +
					Latitude + "," + Longitude +
					"%22%20and%20gflags=%22R%22");
			
	
			if(isMobile()){
				//当我们使用的是中国移动的手机网络时，下面方法可以直接获取得到10.0.0.172，80端口  
				@SuppressWarnings("deprecation")
				String host=android.net.Proxy.getDefaultHost();//通过andorid.net.Proxy可以获取默认的代理地址  
				@SuppressWarnings("deprecation")
				int port =android.net.Proxy.getDefaultPort();//通过andorid.net.Proxy可以获取默认的代理端口  
				SocketAddress sa=new InetSocketAddress(host,port);  
				//定义代理，此处的Proxy是源自java.net  
				Proxy proxy=new Proxy(java.net.Proxy.Type.HTTP,sa);
				MyUrlconnetction = (HttpURLConnection) MyUrl.openConnection(proxy);
			}else
				MyUrlconnetction = (HttpURLConnection) MyUrl.openConnection();
			
			
			Document MyDoc = StringToElement(MyUrlconnetction.getInputStream());
			WOEID = ParseWOEID(MyDoc);
			
		
			MyUrlconnetction.disconnect();//关闭http连接
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("WOEID FINISH");
		return WOEID;
	}
    */
    /**
     * Get the WOEID
     * @param doc
    
    private String ParseWOEID(Document doc){

    	String WOEID = null;
    	//get the nearest one
       	NodeList Result = doc.getElementsByTagName("Result");
       	Node node = Result.item(0);
       	NodeList nodelist = node.getChildNodes();

       	for(int i=0 ; i<nodelist.getLength();i++){
       		if(nodelist.item(i).getNodeType()==Node.ELEMENT_NODE){
	       		if(nodelist.item(i).getNodeName().equals("woeid"))
	       			WOEID = (String) nodelist.item(i).getFirstChild().getNodeValue();
       		}      			
       	}
       	return WOEID;
    } */
    
	/**
	 * 获得百度天气，完成weatherTable
	 * @throws MalformedURLException 
	 */
	@SuppressWarnings("deprecation")
	private boolean GetWeatherFromBaidu(String targetLocation,String name) {
				
		
		try {
//			System.out.println("TRY TO URL:" + "http://weather.yahooapis.com/forecastrss?w=" + WOEID + "&u=c");
			
//			String url ="http://weather.yahooapis.com/forecastrss?w=" + WOEID + "&u=c";
			String BAIDUAPI_URL = "http://api.map.baidu.com/telematics/v3/weather?location=";// 接口网站前缀
			String OUTPUT_TYPE = "&output=xml";// 输出格式，貌似百度目前只能输出XML格式，json无效
			String APP_KEY = "&ak=T1tbNK2H4K8u8Hqn3PCujtxk";// 开发密钥I4q2hWtn6jU40CNcachl8PP5
			
			String url = BAIDUAPI_URL + targetLocation + OUTPUT_TYPE + APP_KEY;
			System.err.println("链接为：" + url);
			
			String weatherResult = null;// 天气结果
			HttpGet get;// 定义一个Get对象,用于请求对象
			
			DefaultHttpClient client;
			HttpParams httpParams = new BasicHttpParams();
			if(isMobile()){
				String host=android.net.Proxy.getDefaultHost();
				int port = android.net.Proxy.getPort(context);
				if (host != null && port != -1) //这里尝试修改友盟上报告的bug“java.lang.IllegalArgumentException: Host name may not be null”
				{
					HttpHost httpHost = new HttpHost(host, port);   
					//设置代理  
		//			client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,httpHost);
					httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, httpHost);
					HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
					HttpConnectionParams.setSoTimeout(httpParams, 10000);
					
					
					SchemeRegistry schReg = new SchemeRegistry();
					schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
					schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

					// 使用线程安全的连接管理来创建HttpClient
					ClientConnectionManager conMgr = new ThreadSafeClientConnManager(httpParams, schReg);
					client = new DefaultHttpClient(conMgr,httpParams);
				} else
					client = new DefaultHttpClient();
			} else
				client = new DefaultHttpClient();
			

			get = new HttpGet(url); // 将连接的地址通过Get提交
			HttpResponse response = client.execute(get);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				weatherResult = EntityUtils.toString(response.getEntity());
//				return weatherResult;
			} else {
				return false;
			}
			
//			HttpUriRequest req = new HttpGet(url); 
			
//			HttpResponse resp = client.execute(req); 
			
//			String a=EntityUtils.toString(ent,"gb2312");
			
//			if (resp.getStatusLine().getStatusCode() != 200){  
	        
//				return false;

//	        }
//			else{
//				HttpEntity ent = resp.getEntity(); 
//				InputStream stream = ent.getContent(); 
//				DocumentBuilder myDocBuilder = null;
//				try {
//					myDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//				} catch (ParserConfigurationException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} 
//				Document doc = null;
//				try {
//					doc = myDocBuilder.parse(new InputSource(stream));
//				} catch (SAXException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
	
			
	//				HttpEntity entity = response.getEntity();
	//				InputStream content = entity.getContent();
	//				Document doc = StringToElement(content);
					
	//			HttpURLConnection urlconnetction = (HttpURLConnection) url.openConnection();
	//			Document doc = StringToElement(urlconnetction.getInputStream());
//				if(doc == null){
//					System.out.println("GetWeather From Yahoo FAIL!!");
//					return false;
//				}
//				if(!ParseWeather(WOEID,doc,name))
//					return false;
//				stream.close();
	//			urlconnetction.disconnect();
//			}
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return false;
			InputStream input = getStringInputStream(weatherResult);
			ParseBaiDu_Xml xml = new ParseBaiDu_Xml(context,input,name);
			weatherTable=xml.weatherTable;
			try {
				return xml.getWeatherInfos();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
//		System.out.println("FINISH URL:" + WOEID);
			
		return true;
//	
	}
		/**
		 * 将字符串转化为输入流
		 * 
		 * @param s
		 *            传入一个字符串
		 **/
		public static InputStream getStringInputStream(String s) {
			if (s != null && !s.equals("")) {
				try {
					ByteArrayInputStream stringInput = new ByteArrayInputStream(
							s.getBytes());
					return stringInput;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	   /**
     * Parse the downloaded xml and set the weatherview.xml
     * @param WOEID
     
    private boolean ParseWeather(String WOEID,Document doc,String name){
    	
    	//get current weather
    	String celsius="℃";
       	NodeList nodelist = doc.getElementsByTagName("yweather:condition");
       	NamedNodeMap nodemap=null;
       	try{
       		nodemap = nodelist.item(0).getAttributes();
       	}catch(java.lang.NullPointerException e){
       		return false;
       	}
    	
    		
    	if(name.equals("my")){
//    		String Low = (String) nodemap.getNamedItem("low").getNodeValue();
//			String High = (String) nodemap.getNamedItem("high").getNodeValue();
//    		weatherTable.setMyTodayTemp(Low + celsius + "/" + High +celsius);
    	}
    	else if(name.equals("target")){
    		
    		weatherTable.setTargetTodayTemp((String) nodemap.getNamedItem("temp").getNodeValue()+celsius);
    		
    	}
    	
    	nodelist = doc.getElementsByTagName("yweather:forecast");
    	for(int i=0;(i<nodelist.getLength())||(i<3);i++){
    		NamedNodeMap NodeMap = nodelist.item(i).getAttributes();
    		
    		//get today information
    		if(i==0){
    	    	if(name.equals("my")){
    	    		
    	    		
    	    		weatherTable.setMyTodayCode((String) NodeMap.getNamedItem("code").getNodeValue());
    	    		weatherTable.setMyTodayText((String) NodeMap.getNamedItem("text").getNodeValue());
    	    		weatherTable.setMyTodayDate((String) NodeMap.getNamedItem("date").getNodeValue());
    	    		String TodayDate = (String) NodeMap.getNamedItem("date").getNodeValue();
			    	weatherTable.setMyTodayDate(DateChanged(TodayDate,false ));
			    	String Low = (String) NodeMap.getNamedItem("low").getNodeValue();;
					String High = (String) NodeMap.getNamedItem("high").getNodeValue();
					weatherTable.setMyTodayTemp(Low + celsius + "/" + High +celsius);
    	    	}
    	    	else if(name.equals("target")){

    	    		String targetdayDay = (String) NodeMap.getNamedItem("day").getNodeValue();
    	    		weatherTable.setTargetTodayDay(DayChanged(targetdayDay));
    	    		weatherTable.setTargetTodayCode((String) NodeMap.getNamedItem("code").getNodeValue());
    	    		weatherTable.setTargetTodayText((String) NodeMap.getNamedItem("text").getNodeValue());
    	    		weatherTable.setTargetTodayDate((String) NodeMap.getNamedItem("date").getNodeValue());
    	    		String TargetdayDate = (String) NodeMap.getNamedItem("date").getNodeValue();
			    	weatherTable.setTargetTodayDate(DateChanged(TargetdayDate, true));
//			    	weatherTable.setTargetTodayTemp((String) NodeMap.getNamedItem("temp").getNodeValue()+celsius);
    	    	}
    		}
    		
    		//get next day information
    		else if(i==1){
    	    	if(name.equals("my")){
    	    		weatherTable.setMyNextdayCode((String) NodeMap.getNamedItem("code").getNodeValue());
    	    		weatherTable.setMyNextdayText((String) NodeMap.getNamedItem("text").getNodeValue());
    	    		String NextdayDate = (String) NodeMap.getNamedItem("date").getNodeValue();
    	    		weatherTable.setMyNextdayDate(DateChanged(NextdayDate, false));
	    			String Low = (String) NodeMap.getNamedItem("low").getNodeValue();
	    			String High = (String) NodeMap.getNamedItem("high").getNodeValue();
	    			weatherTable.setMyNextdayTemp(Low + celsius + "/" + High +celsius);
    	    	}
    	    	else if(name.equals("target")){
    	    		weatherTable.setTargetNextdayCode((String) NodeMap.getNamedItem("code").getNodeValue());
    	    		weatherTable.setTargetNextdayText((String) NodeMap.getNamedItem("text").getNodeValue());
    	    		String NextdayDate = (String) NodeMap.getNamedItem("date").getNodeValue();
    	    		weatherTable.setTargetNextdayDate(DateChanged(NextdayDate, false));
    	    		String Low = (String) NodeMap.getNamedItem("low").getNodeValue();
	    			String High = (String) NodeMap.getNamedItem("high").getNodeValue();
	    			weatherTable.setTargetNextdayTemp(Low + celsius + "/" + High +celsius);
    	    	}
    		}
    		
    		//get the day after tomorrow information
    		else if(i==2){
    			if(name.equals("my")){
    				
    				weatherTable.setMyNext2dayCode((String) NodeMap.getNamedItem("code").getNodeValue());
    				weatherTable.setMyNext2dayText((String) NodeMap.getNamedItem("text").getNodeValue());
    				String Next2dayDate = (String) NodeMap.getNamedItem("date").getNodeValue();
    				weatherTable.setMyNext2dayDate(DateChanged(Next2dayDate, false));
	    			String High = (String) NodeMap.getNamedItem("high").getNodeValue();
	    			String Low = (String) NodeMap.getNamedItem("low").getNodeValue();
	    			weatherTable.setMyNext2dayTemp(Low + celsius + "/" + High +celsius);
	    			
    			}
    	    	else if(name.equals("target")){
    	    		
    				weatherTable.setTargetNext2dayCode((String) NodeMap.getNamedItem("code").getNodeValue());
    				weatherTable.setTargetNext2dayText((String) NodeMap.getNamedItem("text").getNodeValue());
    				String Next2dayDate = (String) NodeMap.getNamedItem("date").getNodeValue();
    				weatherTable.setTargetNext2dayDate(DateChanged(Next2dayDate, false));
	    			String High = (String) NodeMap.getNamedItem("high").getNodeValue();
	    			String Low = (String) NodeMap.getNamedItem("low").getNodeValue();
	    			weatherTable.setTargetNext2dayTemp(Low + celsius + "/" + High +celsius);
	    			
    	    	}
    		}
    	}
    	return true;
    }*/
	
	/**
	 * InputStream To Document
	 * @param input
	 * @return
	 
    private Document StringToElement(InputStream input) {
        try {
        	if(input == null) return null;
        	System.out.println("input:"+input);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(input);
            doc.normalize();
          
            try
            {
            if (input != null)
            {
            	input.close();
            }
            }
            catch (IOException ex)
            {
            ex.printStackTrace();
            }
            
            return doc;
        } catch (Exception e) {
            return null;
        }
    }*/
    
	
	
	
	
	/**
	 * 
	 * @param str is XXdayCode(the weather code from yahoo) 
	 * @param name choose "target" or "my"
	 * @return
	 */
	public Drawable ImageViewChange(String str,String name){

		Drawable drawable = null;
//		@SuppressWarnings("unused")
		Boolean isDay=true;
//		Date now=new Date();
//		java.text.DateFormat d2=DateFormat.getTimeFormat(context);
//		String nowTime=d2.format(now);
		
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String nowTime = formatter.format(curDate);
		if(nowTime.compareTo("07:00:00")>0&&nowTime.compareTo("19:00:00")<0){
			isDay=true;
		}else
			isDay=false;
		//
		if(str.equals("晴")){
			if(isDay){
				if(name.equals("target"))
					drawable = context.getResources().getDrawable(R.drawable.target_sun);
				else if(name.equals("my"))
					drawable = context.getResources().getDrawable(R.drawable.my_sun);
				else 
					drawable=context.getResources().getDrawable(R.drawable.main_sun);
			}else{
				if(name.equals("target"))
					drawable = context.getResources().getDrawable(R.drawable.target_moon);
				else if(name.equals("my"))
					drawable = context.getResources().getDrawable(R.drawable.my_moon);
				else 
					drawable=context.getResources().getDrawable(R.drawable.main_moon_night);
			}
			
		}
	
		else if(str.equals("云")){
			if(isDay){
				if(name.equals("target"))
					drawable = context.getResources().getDrawable(R.drawable.target_partly_cloudy_at_day);
				else  if(name.equals("my"))
					drawable = context.getResources().getDrawable(R.drawable.my_partly_cloudy_at_day);
				else
					drawable = context.getResources().getDrawable(R.drawable.main_partly_cloudy);
			}else{
				if(name.equals("target"))
					drawable = context.getResources().getDrawable(R.drawable.target_partly_cloudy_at_night);
				else  if(name.equals("my"))
					drawable = context.getResources().getDrawable(R.drawable.my_partly_cloudy_at_night);
				else//night-wind
					drawable = context.getResources().getDrawable(R.drawable.main_partly_cloudy_at_night);
			}
			
		}
		
		else if(str.equals("小雨")||str.equals("阵雨")||str.equals("冻雨")){
			
			if(name.equals("target"))
				drawable = context.getResources().getDrawable(R.drawable.target_drizzle);
			else if(name.equals("my"))
				drawable = context.getResources().getDrawable(R.drawable.my_drizzle);
			else{
				if(isDay)
					drawable = context.getResources().getDrawable(R.drawable.main_drizzle);
				else
					drawable = context.getResources().getDrawable(R.drawable.main_drizzle_night);
			}
		}
		
		else if(str.equals("雨夹雪")||str.equals("中雨")||str.equals("大雨")||str.equals("暴雨")
				||str.equals("大暴雨")||str.equals("特大暴雨")){
			
			if(name.equals("target"))
				drawable = context.getResources().getDrawable(R.drawable.target_heavy_rain);
			else if(name.equals("my"))
				drawable = context.getResources().getDrawable(R.drawable.my_heavy_rain);
			else 
				if(isDay)
					drawable=context.getResources().getDrawable(R.drawable.main_heavy_rain);
				else
					drawable=context.getResources().getDrawable(R.drawable.main_heavy_rain_night);
		}
		
		else if(str.equals("多云")){
			
			if(name.equals("target"))
				drawable = context.getResources().getDrawable(R.drawable.target_cloud);
			else if(name.equals("my"))
				drawable = context.getResources().getDrawable(R.drawable.my_cloud);
			else{
				if(isDay)
					drawable=context.getResources().getDrawable(R.drawable.main_cloud);
				else
					drawable=context.getResources().getDrawable(R.drawable.main_cloud_night);
				}
		}
		
		else if(str.equals("阴")||str.equals("雾")||str.equals("浮尘")||str.equals("霾")){
			
			if(name.equals("target"))
				drawable = context.getResources().getDrawable(R.drawable.target_cloudy);
			else if(name.equals("my"))
				drawable = context.getResources().getDrawable(R.drawable.my_cloudy);
			else{
				if(isDay)
					drawable = context.getResources().getDrawable(R.drawable.main_cloudy);
				else
					drawable = context.getResources().getDrawable(R.drawable.main_cloudy_night);
			}
				
		}
		
		else if(str.equals("雷阵雨")||str.equals("雷阵雨伴有冰雹")){
			
			if(name.equals("target"))
				drawable = context.getResources().getDrawable(R.drawable.target_thunderstorms);
			else if(name.equals("my"))
				drawable = context.getResources().getDrawable(R.drawable.my_thunderstorms);
			else{
				if(isDay)
					drawable=context.getResources().getDrawable(R.drawable.main_thunderstorms);
				else
					drawable=context.getResources().getDrawable(R.drawable.main_thunderstorms_night);
			}
				
		}
		
		//
		else if(str.equals("阵雪")||str.equals("小雪")||str.equals("中雪")||str.equals("大雪")
				||str.equals("暴雪")){
			
			if(name.equals("target"))
				drawable = context.getResources().getDrawable(R.drawable.target_snow);
			else if(name.equals("my"))
				drawable = context.getResources().getDrawable(R.drawable.my_snow);
			else{
				if(isDay)
					drawable = context.getResources().getDrawable(R.drawable.main_snow);
				else
					drawable = context.getResources().getDrawable(R.drawable.main_snow_night);
			}
				
		}
		
		
		
		
		
		
		
		
		else if(str.equals("扬沙")||str.equals("沙尘暴")||str.equals("强沙尘暴")){
			
			if(name.equals("target"))
				drawable = context.getResources().getDrawable(R.drawable.target_gale);
			else if(name.equals("my"))
				drawable = context.getResources().getDrawable(R.drawable.my_gale);
			else{
				if(isDay)
					drawable = context.getResources().getDrawable(R.drawable.main_gale);
				else
					drawable = context.getResources().getDrawable(R.drawable.main_gale_night);
			}
				
		}
		
		
		
		
		
		
		
		
		
		else if(str.equals("大暴雨")||str.equals("特大暴雨")){
			
			if(name.equals("target"))
				drawable = context.getResources().getDrawable(R.drawable.target_typhoon_rain);
			else if(name.equals("my"))
				drawable = context.getResources().getDrawable(R.drawable.my_typhoon_rain);
			else{
				if(isDay)
					drawable=context.getResources().getDrawable(R.drawable.main_typhoon_rain);
				else
					drawable=context.getResources().getDrawable(R.drawable.main_typhoon_rain_night);
			}
		}
		
		else{
			if(isDay){
				if(name.equals("target"))
					drawable = context.getResources().getDrawable(R.drawable.target_sun);
				else if(name.equals("my"))
					drawable = context.getResources().getDrawable(R.drawable.my_sun);
				else 
					drawable=context.getResources().getDrawable(R.drawable.main_sun);
			}else{
				if(name.equals("target"))
					drawable = context.getResources().getDrawable(R.drawable.target_moon);
				else if(name.equals("my"))
					drawable = context.getResources().getDrawable(R.drawable.my_moon);
				else 
					drawable=context.getResources().getDrawable(R.drawable.main_moon_night);
			}
		}
		
		return drawable;
		
	}
	
}
