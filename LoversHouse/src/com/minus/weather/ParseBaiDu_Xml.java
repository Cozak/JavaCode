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
 * @category �����ǻ���XMLParser�Ľ��������ڽ����ӰٶȻ�ȡ������XML��Ϣ,�������ڽ�����������
 * ***/
public class ParseBaiDu_Xml {
	
	public Context context = null;
	private InputStream input = null;
//	private WeatherInfo weatherInfo = null;
	private int dateFlag = 0;// �ж��ǵڼ���dateԪ��
	private int weatherFlag = 0;
	private int tempFlag = 0;
	private String nodeName = null;// ��ǰԪ�ؽڵ�����
	public WeatherTable weatherTable;
	private String name;

	/**
	 * @param input
	 *            ͨ�����췽��������������
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
		
		if(str.equals("����")) str = "������";
		else if(str.equals("��һ"))	str = "����һ";
		else if(str.equals("�ܶ�"))  str = "���ڶ�";
		else if(str.equals("����"))  str = "������";
		else if(str.equals("����"))  str = "������";
		else if(str.equals("����"))  str = "������";
		else if(str.equals("����"))  str = "������";
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
	 * @return һ��������ǰ������δ������������Ϣ��List����
	 * @throws XmlPullParserException
	 * @throws IOException
	 * */
	@SuppressLint("SimpleDateFormat")
	public boolean getWeatherInfos() throws XmlPullParserException,
			IOException {
//		List<WeatherInfo> allInfos = null;// ���������������������
		String celsius="��";
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser xmlPullParser = factory.newPullParser();// ����XmlPullParser
		xmlPullParser.setInput(input, "UTF-8");// ����������������ʽ
		// ....������ʼ
		int eventType = xmlPullParser.getEventType();// ȡ���¼��Ĳ�����
		while (eventType != XmlPullParser.END_DOCUMENT) {// ����ĵ�û�н���
			nodeName = xmlPullParser.getName();// ȡ�õ�ǰ��ȡ�Ľڵ�����
			switch (eventType) {
			// ...�ĵ���ʼ
			case XmlPullParser.START_DOCUMENT:
				System.out.println("�ĵ���ʼ");
//				allInfos = new ArrayList<WeatherInfo>();
				break;
			// ...��ʼ��ȡ���
			case XmlPullParser.START_TAG:
				if ("date".equals(nodeName)) {
					if (dateFlag == 0) {// ��ʾ��һ��date�ڵ㣬���� ֮~~~
						String temp = xmlPullParser.nextText();
						String tempNext=null;
						String tempNext2day=null;
						//ת����.
						String tempDate=DateChanged(temp, true);
						Calendar c = Calendar.getInstance();
						String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
						if(hour.length()==1)
				        	hour="0"+hour;
						weatherTable.setTargetTodayDate(
								tempDate.substring(tempDate.indexOf('.')+1)+" "+hour+"ʱ");
						
						
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
									date2 = sdf.parse(temp2);//�ϴζ�λ��ʱ��
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
							mEditor.putString("baseDate",temp);//��׼ʱ��
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
									if(sArray.length==3){//�賿��ʱ�� ��ֵ��û�е�����¶� ��ԭֵ
										String[] s2Array=sArray[2].split("��");//���ĵ�ð�� ���� �߶� ͼƬ
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
					// ...����
					String temp=null;
					Boolean isDay=true;
					Date now=new Date();
					java.text.DateFormat d2=DateFormat.getTimeFormat(context);
					String nowTime=d2.format(now);
					if(nowTime.compareTo("07:00:00")>0&&nowTime.compareTo("19:00:00")<0){
						isDay=true;
					}else
						isDay=false;
					String[]  sArray= xmlPullParser.nextText().split("ת");
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
							weatherTable.setMyTodayCode(temp);//��Ҫ�仯
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
							if(weatherTable.getTargetTodayTemp().equals("")&&sArray.length==1)//�賿û��ʵʱ���¶ȣ������¶�����
								weatherTable.setTargetTodayTemp(High);
							else {//ȡԭ�����ݵĻ���
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
//					// ...����
//					String temp = xmlPullParser.nextText();
//					weatherInfo.setWind(temp);
//				} else if ("dayPictureUrl".equals(nodeName)) {
//					// ...�����ͼƬ
//					String temp = xmlPullParser.nextText();
//					weatherInfo.setDayPictureUrl(temp);
//				} else if ("nightPictureUrl".equals(nodeName)) {
//					// ...���ϵ�ͼƬ
//					String temp = xmlPullParser.nextText();
//					weatherInfo.setNightPictureUrl(temp);
//				} else if ("temperature".equals(nodeName)) {
//					// ...����
//					String temp = xmlPullParser.nextText();
//					weatherInfo.setTemperature(temp);
//					allInfos.add(weatherInfo);
//				}
//				 }
				break;
			// ...�������
			case XmlPullParser.END_TAG:
				nodeName = xmlPullParser.getName();
				if ("weather_data".equals(nodeName)) {
//					weatherInfo = null;// ��ն���
				}
				break;
			default:
				break;
			}
			eventType = xmlPullParser.next();// �������¼�ָ����һ�����
		}
		return true;
	}
}
