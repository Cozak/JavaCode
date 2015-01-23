package com.minus.table;

import com.minus.xsocket.asynsocket.protocol.Protocol;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "CALENDAR")
//CALENDAR (title TEXT, initDate TEXT PRIMARY KEY, editDate TEXT)
public class CalendarTable {
	@Id(column = "initDate")
	private String initDate;
	private String editDate;
	private String memoDate;
	private String promptPolicy;
	private String title;
	private String serverState;

	public CalendarTable() {
		this.initDate = null;
		this.editDate = null;
		this.memoDate = null;
		this.promptPolicy = null;
		this.title = null;
		this.serverState = null;
	}
	
	public CalendarTable(String initDate, String editDate, String memoDate, 
			String promptPolicy, String title) {
		this.initDate = initDate;
		this.editDate = editDate;
		this.memoDate = memoDate;
		this.promptPolicy = promptPolicy;
		this.title = title;
		this.serverState = "" + Protocol.WaitForServerComfirmAdd;
	}
	
	public void setInitDate(String initDate){
		this.initDate = initDate;
	}
	
	public String getInitDate(){
		return this.initDate;
	}

	public void setEditDate(String editDate){
		this.editDate = editDate;
	}
	
	public String getEditDate(){
		return this.editDate;
	}

	public void setMemoDate(String memoDate){
		this.memoDate = memoDate;
	}
	
	public String getMemoDate(){
		return this.memoDate;
	}
	
	public void setPromptPolicy(String promptPolicy){
		this.promptPolicy = promptPolicy;
	}
	
	public String getPromptPolicy(){
		return this.promptPolicy;
	}

	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getServerState() {
		return serverState;
	}

	public void setServerState(String serverState) {
		this.serverState = serverState;
	}

}
