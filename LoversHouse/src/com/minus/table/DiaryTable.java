package com.minus.table;

import java.sql.Date;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
//作者账号 初始日期(key) 是否最新 标题 内容 编辑日期
@Table(name = "DIARY")
public class DiaryTable {
	private int id;
	private String initdate;
	private String author;
	private int isnew;   // 1 : true 0 :false
	private String title;
	private String content;
	private String editdate;
	private String serverState;
	
	
	 public int getId() {
	        return id;
	    }
	    public void setId(int id) {
	        this.id = id;
	    }
	public void setServerState(String serverState){
		this.serverState = serverState;
	}
	
	public String getServerState(){
		return this.serverState;
	}
	public void setAuthor(String author){
		this.author = author;
	}
	
	public String getAuthor(){
		return this.author;
	}
	
	public void setInitdate(String initdate){
		this.initdate = initdate;
	}
	
	public String getInitdate(){
		return this.initdate;
	}
	
	public void setIsnew(int isnew){
		this.isnew = isnew;
	}
	
	public int getIsnew(){
		return this.isnew;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setContent(String content){
		this.content = content;
	}
	
	public String getContent(){
		return this.content; 
	}
	
	public void setEditdate(String editdate){
		this.editdate = editdate;
	}
	
	public String getEditdate(){
		return this.editdate;
	}
	
	public void same(DiaryTable diary){
		this.author = diary.getAuthor();
		this.content = diary.getContent();
		this.editdate = diary.getEditdate();
		this.initdate = diary.getInitdate();
		this.isnew = diary.getIsnew();
		this.title = diary.getTitle();
		this.serverState = diary.getServerState();
	}
}

