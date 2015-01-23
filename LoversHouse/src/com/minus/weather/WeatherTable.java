package com.minus.weather;

import java.io.Serializable;

import com.minus.table.TargetTable;

public class WeatherTable implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//My Weather
	private String MyTodayCode;
	private String MyTodayText;
	private String MyTodayDate;
	private String TargetTodayDay;
	private String MyTodayTemp;
	
	private String MyNextdayCode;
	private String MyNextdayText;
	private String MyNextdayDate;
	private String MyNextdayTemp;
	
	private String MyNext2dayCode;
	private String MyNext2dayText;
	private String MyNext2dayDate;
	private String MyNext2dayTemp;
	
	//TargetWeather
	private String TargetTodayCode;
	private String TargetTodayText;
	private String TargetTodayDate;
	private String TargetTodayTemp;
	
	private String TargetNextdayCode;
	private String TargetNextdayText;
	private String TargetNextdayDate;
	private String TargetNextdayTemp;
	
	private String TargetNext2dayCode;
	private String TargetNext2dayText;
	private String TargetNext2dayDate;
	private String TargetNext2dayTemp;
	
	public WeatherTable() {
		// TODO Auto-generated constructor stub
		 MyTodayCode = "";
		 MyTodayText = "";
		 MyTodayDate = "";
//		 MyTodayDay = "";
		 MyTodayTemp = "";
		
		 MyNextdayCode = "";
		 MyNextdayText = "";
		 MyNextdayDate = "";
		 MyNextdayTemp = "";
		
		 MyNext2dayCode = "";
		 MyNext2dayText = "";
		 MyNext2dayDate = "";
		 MyNext2dayTemp = "";
		
		//TargetWeather
		 TargetTodayCode = "";
		 TargetTodayText = "";
		 TargetTodayDate = "";
		 TargetTodayDay = "";
		 TargetTodayTemp = "";
		
		 TargetNextdayCode = "";
		 TargetNextdayText = "";
		 TargetNextdayDate = "";
		 TargetNextdayTemp = "";
		
		 TargetNext2dayCode = "";
		 TargetNext2dayText = "";
		 TargetNext2dayDate = "";
		 TargetNext2dayTemp = "";
	}
	
	public String getMyTodayCode() {
		return MyTodayCode;
	}
	public void setMyTodayCode(String myTodayCode) {
		MyTodayCode = myTodayCode;
	}
	public String getMyTodayText() {
		return MyTodayText;
	}
	public void setMyTodayText(String myTodayText) {
		MyTodayText = myTodayText;
	}
	public String getMyTodayDate() {
		return MyTodayDate;
	}
	public void setMyTodayDate(String myTodayDate) {
		MyTodayDate = myTodayDate;
	}
	public String getTargetTodayDay() {
		return TargetTodayDay;
	}
	public void setTargetTodayDay(String targetTodayDay) {
		TargetTodayDay = targetTodayDay;
	}
	public String getMyTodayTemp() {
		return MyTodayTemp;
	}
	public void setMyTodayTemp(String myTodayTemp) {
		MyTodayTemp = myTodayTemp;
	}
	public String getMyNextdayCode() {
		return MyNextdayCode;
	}
	public void setMyNextdayCode(String myNextdayCode) {
		MyNextdayCode = myNextdayCode;
	}
	public String getMyNextdayText() {
		return MyNextdayText;
	}
	public void setMyNextdayText(String myNextdayText) {
		MyNextdayText = myNextdayText;
	}
	public String getMyNextdayDate() {
		return MyNextdayDate;
	}
	public void setMyNextdayDate(String myNextdayDate) {
		MyNextdayDate = myNextdayDate;
	}
	public String getMyNextdayTemp() {
		return MyNextdayTemp;
	}
	public void setMyNextdayTemp(String myNextdayTemp) {
		MyNextdayTemp = myNextdayTemp;
	}
	public String getMyNext2dayCode() {
		return MyNext2dayCode;
	}
	public void setMyNext2dayCode(String myNext2dayCode) {
		MyNext2dayCode = myNext2dayCode;
	}
	public String getMyNext2dayText() {
		return MyNext2dayText;
	}
	public void setMyNext2dayText(String myNext2dayText) {
		MyNext2dayText = myNext2dayText;
	}
	public String getMyNext2dayDate() {
		return MyNext2dayDate;
	}
	public void setMyNext2dayDate(String myNext2dayDate) {
		MyNext2dayDate = myNext2dayDate;
	}
	public String getMyNext2dayTemp() {
		return MyNext2dayTemp;
	}
	public void setMyNext2dayTemp(String myNext2dayTemp) {
		MyNext2dayTemp = myNext2dayTemp;
	}
	public String getTargetTodayCode() {
		return TargetTodayCode;
	}
	public void setTargetTodayCode(String targetTodayCode) {
		TargetTodayCode = targetTodayCode;
	}
	public String getTargetTodayText() {
		return TargetTodayText;
	}
	public void setTargetTodayText(String targetTodayText) {
		TargetTodayText = targetTodayText;
	}
	public String getTargetTodayDate() {
		return TargetTodayDate;
	}
	public void setTargetTodayDate(String targetTodayDate) {
		TargetTodayDate = targetTodayDate;
	}
	public String getTargetTodayTemp() {
		return TargetTodayTemp;
	}
	public void setTargetTodayTemp(String targetTodayTemp) {
		TargetTodayTemp = targetTodayTemp;
	}
	public String getTargetNextdayCode() {
		return TargetNextdayCode;
	}
	public void setTargetNextdayCode(String targetNextdayCode) {
		TargetNextdayCode = targetNextdayCode;
	}
	public String getTargetNextdayText() {
		return TargetNextdayText;
	}
	public void setTargetNextdayText(String targetNextdayText) {
		TargetNextdayText = targetNextdayText;
	}
	public String getTargetNextdayDate() {
		return TargetNextdayDate;
	}
	public void setTargetNextdayDate(String targetNextdayDate) {
		TargetNextdayDate = targetNextdayDate;
	}
	public String getTargetNextdayTemp() {
		return TargetNextdayTemp;
	}
	public void setTargetNextdayTemp(String targetNextdayTemp) {
		TargetNextdayTemp = targetNextdayTemp;
	}
	public String getTargetNext2dayCode() {
		return TargetNext2dayCode;
	}
	public void setTargetNext2dayCode(String targetNext2dayCode) {
		TargetNext2dayCode = targetNext2dayCode;
	}
	public String getTargetNext2dayText() {
		return TargetNext2dayText;
	}
	public void setTargetNext2dayText(String targetNext2dayText) {
		TargetNext2dayText = targetNext2dayText;
	}
	public String getTargetNext2dayDate() {
		return TargetNext2dayDate;
	}
	public void setTargetNext2dayDate(String targetNext2dayDate) {
		TargetNext2dayDate = targetNext2dayDate;
	}
	public String getTargetNext2dayTemp() {
		return TargetNext2dayTemp;
	}
	public void setTargetNext2dayTemp(String targetNext2dayTemp) {
		TargetNext2dayTemp = targetNext2dayTemp;
	}
	
	public void same(WeatherTable weather){
		this.setMyTodayCode(weather.getMyTodayCode());
		this.setMyTodayText(weather.getMyTodayText());
		this.setMyTodayDate(weather.getMyTodayDate());
		this.setTargetTodayDay(weather.getTargetTodayDay());
		this.setMyTodayTemp(weather.getMyTodayTemp());
		
		this.setMyNextdayCode(weather.getMyNextdayCode());
		this.setMyNextdayText(weather.getMyNextdayText());
		this.setMyNextdayDate(weather.getMyNextdayDate());
		this.setMyNextdayTemp(weather.getMyNextdayTemp());
		
		this.setMyNext2dayCode(weather.getMyNext2dayCode());
		this.setMyNext2dayText(weather.getMyNext2dayText());
		this.setMyNext2dayDate(weather.getMyNext2dayDate());
		this.setMyNext2dayTemp(weather.getMyNext2dayTemp());
		
		this.setTargetTodayCode(weather.getTargetTodayCode());
		this.setTargetTodayText(weather.getTargetTodayText());
		this.setTargetTodayDate(weather.getTargetTodayDate());
		this.setTargetTodayTemp(weather.getTargetTodayTemp());
		
		this.setTargetNextdayCode(weather.getTargetNextdayCode());
		this.setTargetNextdayText(weather.getTargetNextdayText());
		this.setTargetNextdayDate(weather.getTargetNextdayDate());
		this.setTargetNextdayTemp(weather.getTargetNextdayTemp());
		
		this.setTargetNext2dayCode(weather.getTargetNext2dayCode());
		this.setTargetNext2dayText(weather.getTargetNext2dayText());
		this.setTargetNext2dayDate(weather.getTargetNext2dayDate());
		this.setTargetNext2dayTemp(weather.getTargetNext2dayTemp());
	}
}
