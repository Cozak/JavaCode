package com.minus.table;

import java.sql.Date;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "ALARMCLOCK")
//ALARMCLOCK (account TEXT,initdate TEXT PRIMARY KEY ,color TEXT,content TEXT,forwho TEXT,whattime TEXT)
public class AlarmClockTable {
	@Id(column = "initdate")
	private String initdate;
	private String account;
	private String color;
	private String content;
	private String forwho;
	private String whattime;
	
	public void setAccount(String account){
		this.account = account;
	}
	
	public String getAccount(){
		return this.account;
	}
	
	public void setInitdate(String initdate){
		this.initdate = initdate;
	}
	
	public String getInitdate(){
		return this.initdate;
	}
	
	public void setColor(String color){
		this.color = color;
	}
	
	public String getColor(){
		return this.color; 
	}
	
	public void setContent(String content){
		this.content = content;
	}
	
	public String getContent(){
		return this.content; 
	}
	
	public void setForwho(String forwho){
		this.forwho = forwho;
	}
	
	public String getForwho(){
		return this.forwho; 
	}
	
	public void setWhattime(String whattime){
		this.whattime = whattime;
	}
	
	public String getWhattime(){
		return this.whattime;
	}
} 