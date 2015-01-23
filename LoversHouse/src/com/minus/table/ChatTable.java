package com.minus.table;

import java.sql.Date;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "CHAT")
public class ChatTable {
	@Id(column = "initdate")
	private String initdate;
	private String account;
	private String status;  //sending read 
	private String message;
	private int msgtype; // text pic voice action   对方的1--4  自己的 5--8
	private String recordTime;

	
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
	/**
	 * send or read 
	 * @param status
	 */
	public void setStatus(String status){
		this.status = status;
	}
	/**
	 * send or read 
	 * @param status
	 */
	public String getStatus(){
		return this.status;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	/**
	 * text pic voice action
	 * @return
	 */
	public int getMsgtype() {
		return msgtype;
	}
	
	/**
	 * text pic voice action
	 * @return
	 */
	public void setMsgtype(int msgtype) {
		this.msgtype = msgtype;
	}
	
	
	
	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	public void same(ChatTable chat){
		this.account = chat.getAccount();
		this.initdate = chat.getInitdate();
		this.message = chat.getMessage();
		this.status = chat.getStatus();
		this.msgtype = chat.getMsgtype();
		this.recordTime = chat.getRecordTime();
	}
}
