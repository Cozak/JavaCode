package com.minus.weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;

/***
 * @author 
 * @category 此类是基于XMLParser的解析，用于解析从百度获取的天气XML信息,不可用于解析其他数据
 * ***/
public class ParseBaiDu_Xml {
	
	public Context context = null;
	private InputStream input = null;
//	private WeatherInfo weatherInfo = null;
	private int dateFlag = 0;// 判断是第几个date元素
	private int weatherFlag = 0;
	private int tempFlag = 0;
	private String nodeName = null;// 当前元素节点名称
	public WeatherTable weatherTable;
	private String name;

	/**
	 * @param input
	 *            通过构造方法将输入流传入
	 * */
	public ParseBaiDu_Xml(Context context,InputStream input,String name) {
		
		this.context = context;
		this.input = input;
		weatherTable = new WeatherTable();
		this.name=name;
	}
	/**
	 * Change day to Chinese
	 * @param str
	 * @return
	 */
	public String DayChanged(String str){
		
		if(str.equals("周日")) str = "星期日";
		else if(str.equals("周一"))	str = "星期一";
		else if(str.equals("周二"))  str = "星期二";
		else if(str.equals("周三"))  str = "星期三";
		else if(str.equals("周四"))  str = "星期四";
		else if(str.equals("周五"))  str = "星期五";
		else if(str.equals("周六"))  str = "星期六";
		else str = "Error!"; 
		
		return str;
	}
	/**
	 * 
	 * @param str means the String to change
	 * @param b if b=true ,return year+month+day ,else return year+month 
	 * @return
	 */
	public String DateChanged(String str, boolean b){
		String[] date = str.split("-");
		String year = date[0];
		String month = date[1];
		String day= date[2];	
		String newdate = null;

		if(month.equals("01")) month = "1";
		else if(month.equals("02")) month = "2";
		else if(month.equals("03")) month = "3";
		else if(month.equals("04")) month = "4";
		else if(month.equals("05")) month = "5";
		else if(month.equals("06")) month = "6";
		else if(month.equals("07")) month = "7";
		else if(month.equals("08")) month = "8";
		else if(month.equals("09")) month = "9";
		else if(month.equals("10")) month = "10";
		else if(month.equals("11")) month = "11";
		else if(month.equals("12")) month = "12";
		else month = "0" ; 
		
		if(day.equals("01")) day = "1";
		else if(day.equals("02")) day = "2";
		else if(day.equals("03")) day = "3";
		else if(day.equals("04")) day = "4";
		else if(day.equals("05")) day = "5";
		else if(day.equals("06")) day = "6";
		else if(day.equals("07")) day = "7";
		else if(day.equals("08")) day = "8";
		else if(day.equals("09")) day = "9";
		
		
		if(b) newdate = year + "." + month + "." + day;
		else newdate = month + "." + day;
		return newdate;
	}
	/**
	 * @return 一个包含当前日期与未来三天天气信息的List对象
	 * @throws XmlPullParserException
	 * @throws IOException
	 * */
	@SuppressLint("SimpleDateFormat")
	public boolean getWeatherInfos() throws XmlPullParserException,
			IOException {
//		List<WeatherInfo> allInfos = null;// 保存解析得来的所有数据
		String celsius="℃";
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser xmlPullParser = factory.newPullParser();// 创建XmlPullParser
		xmlPullParser.setInput(input, "UTF-8");// 设置输入流与编码格式
		// ....操作开始
		int eventType = xmlPullParser.getEventType();// 取得事件的操作码
		while (eventType != XmlPullParser.END_DOCUMENT) {// 如果文档没有结束
			nodeName = xmlPullParser.getName();// 取得当前读取的节点名称
			switch (eventType) {
			// ...文档开始
			case XmlPullParser.START_DOCUMENT:
				System.out.println("文档开始");
//				allInfos = new ArrayList<WeatherInfo>();
				break;
			// ...开始读取标记
			case XmlPullParser.START_TAG:
				if ("date".equals(nodeName)) {
					if (dateFlag == 0) {// 表示第一个date节点，无视 之~~~
						String temp = xmlPullParser.nextText();
						String tempNext=null;
						String tempNext2day=null;
						//转换到.
						String tempDate=DateChanged(temp, true);
						Calendar c = Calendar.getInstance();
						String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
						if(hour.length()==1)
				        	hour="0"+hour;
						weatherTable.setTargetTodayDate(
								tempDate.substring(tempDate.indexOf('.')+1)+" "+hour+"时");
						
						
						try {
							Date date;
							date = (new SimpleDateFormat("yyyy-MM-dd")).parse(temp);
							SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount()
										, Activity.MODE_PRIVATE);
								SharedPreferences.Editor mEditor = mSP.edit();
							if(name.equals("my")){

								Date date2;
								String temp2;
								
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								if(!mSP.getString("baseDate","").equals("")){
									temp2 = mSP.getString("baseDate","");
									date2 = sdf.parse(temp2);//上次定位的时间
									Calendar cal = Calendar.getInstance();
									cal.setTime(date2);
									cal.add(Calendar.DATE, 1);
									date2=sdf.parse((new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime()));
									temp2=sdf.format(date2);//+1
									if(temp.equals(temp2)){
										mEditor.putInt("dateInterval", 1);
										mEditor.commit();
									}else{
										cal.setTime(date2);
										cal.add(Calendar.DATE, 1);
										date2=sdf.parse((new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime()));
										temp2=sdf.format(date2);//+1
										if(temp.equals(temp2)){
											mEditor.putInt("dateInterval", 2);
											mEditor.commit();
										}else{
											mEditor.putInt("dateInterval", 0);
											mEditor.commit();
										}
									
										
									}
								}
							}
							mEditor.putString("baseDate",temp);//基准时间
							mEditor.commit();
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							cal.add(Calendar.DATE, 1);
//							System.out.println((new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime()));
							tempNext = (new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime());
							cal.add(Calendar.DATE, 1);
							tempNext2day = (new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime());
							
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(name.equals("my")){
							weatherTable.setMyTodayDate(DateChanged(temp, false));
							weatherTable.setMyNextdayDate(DateChanged(tempNext, false));
							weatherTable.setMyNext2dayDate(DateChanged(tempNext2day, false));
						}else{
							
//							weatherTable.setTargetTodayDate(DateChanged(temp, true));
							weatherTable.setTargetNextdayDate(DateChanged(tempNext, false));
							weatherTable.setTargetNext2dayDate(DateChanged(tempNext2day, false));
							
						}
							
						
						dateFlag++;
					} else {
//						weatherInfo = new WeatherInfo();
						String temp = xmlPullParser.nextText();
						String[] sArray=temp.split(" ");
//						weatherInfo.setDate(temp);
//						if(name.equals("my")){
							if(dateFlag==1){
								
								
								if(name.equals("my"))
//									weatherTable.setMyTodayDate(sArray[0]);
									;
								else{
									
									weatherTable.setTargetTodayDay(DayChanged(sArray[0]));//
//									weatherTable.setTargetTodayDate(temp);
									if(sArray.length==3){//凌晨的时间 该值中没有第三项：温度 用原值
										String[] s2Array=sArray[2].split("：");//中文的冒号 居中 高度 图片
										weatherTable.setTargetTodayTemp(s2Array[1].substring(0,s2Array[1].length()-1));
									}
								}
									
//							}else if(dateFlag==2){
//		    	    			if(name.equals("my"))
//		    	    				weatherTable.setMyNextdayDate(DateChanged(temp, false));
//								else
//									weatherTable.setTargetNextdayDate(DateChanged(temp, false));
//		    	    		}else if(dateFlag==3){
//		    	    			if(name.equals("my"))
//		    	    				weatherTable.setMyNext2dayDate(DateChanged(temp, false));
//								else
//									weatherTable.setTargetNext2dayDate(DateChanged(temp, false));
//		    	    		}
						}
						dateFlag++;
					}
				}else if ("weather".equals(nodeName)) {
					// ...天气
					String temp=null;
					Boolean isDay=true;
					Date now=new Date();
					java.text.DateFormat d2=DateFormat.getTimeFormat(context);
					String nowTime=d2.format(now);
					if(nowTime.compareTo("07:00:00")>0&&nowTime.compareTo("19:00:00")<0){
						isDay=true;
					}else
						isDay=false;
					String[]  sArray= xmlPullParser.nextText().split("转");
					if(sArray.length!=1){
						if(isDay)
							temp=sArray[0];
						else
							temp=sArray[1];
					}else
						temp=sArray[0];
					
//					weatherInfo.setWeather(temp);
					if(weatherFlag==0){
						if(name.equals("my"))
							weatherTable.setMyTodayCode(temp);//需要变化
						else
							weatherTable.setTargetTodayCode(temp);
					}else if(weatherFlag==1){
    	    			if(name.equals("my"))
    	    				weatherTable.setMyNextdayCode(temp);
						else
							weatherTable.setTargetNextdayCode(temp);
    	    		}else if(weatherFlag==2){
    	    			if(name.equals("my"))
    	    				weatherTable.setMyNext2dayCode(temp);
						else
							weatherTable.setTargetNext2dayCode(temp);
    	    		}
					weatherFlag++;
				}else if ("temperature".equals(nodeName)) {
					String temp = xmlPullParser.nextText();
					String[] sArray=temp.split("~");
					String High = sArray[0].trim();
					String Low=null;
					if(sArray.length!=1)
						Low = sArray[1].trim();
					
					if(tempFlag==0){
						if(name.equals("my")){
							if(sArray.length==1){
//								weatherTable.setMyTodayTemp( High);
								SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount()
										, Activity.MODE_PRIVATE);
								if(mSP.getString("myTodayTemper", " ").equals(" "))
									weatherTable.setMyTodayTemp( High);
								else{
									int dateInt=mSP.getInt("dateInterval", 0);
									if(dateInt==1){
										weatherTable.setMyTodayTemp(mSP.getString("myNextdayTemper", " "));
									}else if(dateInt==2){
										weatherTable.setMyTodayTemp(mSP.getString("myNext2datTemper", " "));
									}else
										weatherTable.setMyTodayTemp( High);
								}
							}else
								weatherTable.setMyTodayTemp(Low  + " / " + High+ celsius);
						}
							
						else{
							if(weatherTable.getTargetTodayTemp().equals("")&&sArray.length==1)//凌晨没有实时的温度，改用温度栏的
								weatherTable.setTargetTodayTemp(High);
							else {//取原来数据的缓存
								SharedPreferences mSP  = GlobalApplication.getInstance().getSharedPreferences(SelfInfo.getInstance().getAccount()
										, Activity.MODE_PRIVATE);
								if(!mSP.getString("targetTodayTemper"," ").equals(" "))
									weatherTable.setTargetTodayTemp(mSP.getString("targetTodayTemper"," "));
							}
						}
					}
						
					else if(tempFlag==1){
						if(name.equals("my"))
							weatherTable.setMyNextdayTemp(Low + " / " + High +celsius);
						else
							weatherTable.setTargetNextdayTemp(Low + " / " + High +celsius);
						
						
					}else if(tempFlag==2){
						if(name.equals("my"))
							weatherTable.setMyNext2dayTemp(Low + " / " + High +celsius);
						else
							weatherTable.setTargetNext2dayTemp(Low + " / " + High +celsius);
					}
						
						
					tempFlag++;
				}
//				} else if ("wind".equals(nodeName)) {
//					// ...风力
//					String temp = xmlPullParser.nextText();
//					weatherInfo.setWind(temp);
//				} else if ("dayPictureUrl".equals(nodeName)) {
//					// ...白天的图片
//					String temp = xmlPullParser.nextText();
//					weatherInfo.setDayPictureUrl(temp);
//				} else if ("nightPictureUrl".equals(nodeName)) {
//					// ...晚上的图片
//					String temp = xmlPullParser.nextText();
//					weatherInfo.setNightPictureUrl(temp);
//				} else if ("temperature".equals(nodeName)) {
//					// ...气温
//					String temp = xmlPullParser.nextText();
//					weatherInfo.setTemperature(temp);
//					allInfos.add(weatherInfo);
//				}
//				 }
				break;
			// ...结束标记
			case XmlPullParser.END_TAG:
				nodeName = xmlPullParser.getName();
				if ("weather_data".equals(nodeName)) {
//					weatherInfo = null;// 清空对象
				}
				break;
			default:
				break;
			}
			eventType = xmlPullParser.next();// 将操作事件指到下一个标记
		}
		return true;
	}
}
