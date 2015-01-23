package com.minus.table;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "TEMP")
//TEMP (account TEXT PRIMARY KEY, headphoto BLOB)
public class Temp {
	@Id(column = "account")
	private String account;
	private String  headphoto;
	
	public void setAccount(String account){
		this.account = account;
	}
	
	public String getAccount(){
		return this.account;
	}
	
	
	public void setHeadphoto(String  headphoto){
		this.headphoto = headphoto;
	}
	
	public String  getHeadphoto(){
		return this.headphoto;
	}
	
	public void same(Temp temp){
		this.account = temp.getAccount();
		this.headphoto = temp.getHeadphoto();
	}
}
