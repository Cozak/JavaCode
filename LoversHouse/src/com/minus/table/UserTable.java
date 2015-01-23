package com.minus.table;

import java.sql.Blob;

import com.minus.xsocket.asynsocket.protocol.Protocol;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "USER")
//account TEXT PRIMARY KEY, password TEXT, sex TEXT, bigname TEXT, appearance TEXT, 
//status TEXT, smallname TEXT, target TEXT, headphoto BLOB)"
public class UserTable {
	@Id(column = "account")
	private String account;
	private String password;
	private String sex;
	private String bigname;
	private String appearance;
	private String status;
	private String smallname;
	private String target;
	private String headphoto;
	
	
	public void setAccount(String account){
		this.account = account;
	}
	
	public String getAccount(){
		return this.account;
	}
	
	public void setPassword(String password){
		this.password = password;		
	}
	
	public String getPassword(){
		return this.password;
	}
	
	
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBigname() {
		return bigname;
	}

	public void setBigname(String bigname) {
		this.bigname = bigname;
	}

	public String getAppearance() {
		return appearance;
	}

	public void setAppearance(String appearance) {
		this.appearance = appearance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSmallname() {
		return smallname;
	}

	public void setSmallname(String smallname) {
		this.smallname = smallname;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setHeadphoto(String  b){
		this.headphoto = b;
	}
	
	public String getHeadphoto(){
		return this.headphoto;
	}
	
	public void same(UserTable user){
		this.account = user.getAccount();
		this.password = user.getPassword();
		this.headphoto = user.getHeadphoto();
		this.appearance = user.getAppearance();
		this.bigname = user.getBigname();
		if(user.getSex().equals(Protocol.DEFAULT + "")){
			this.sex = "b";
		}else{
			this.sex = "g";
		}
	
		this.smallname = user.getSmallname();
		this.status  = user.getStatus();
		this.target = user.getTarget();
	}
}

 