package com.minus.table;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import java.sql.Blob;

@Table(name = "TARGET")
//TARGET (account TEXT PRIMARY KEY, headphoto BLOB)
public class TargetTable {
	@Id(column = "account")
	private String account;
	private String sex;
	private String bigName;
	private String appearance;
	private String status;
	private String smallname ;
	private String target;
	private String headphoto;
	
	public void setAccount(String account){
		this.account = account;
	} 
	
	public String getAccount(){
		return this.account;
	}
	
	
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBigName() {
		return bigName;
	}

	public void setBigName(String bigName) {
		this.bigName = bigName;
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

	public void setHeadphoto(String headphoto){
		this.headphoto = headphoto;
	}
	
	public String getHeadphoto(){
		return this.headphoto;
	}
	
	public void same(TargetTable target){
		this.setAccount(target.getAccount());
		this.setHeadphoto(target.getHeadphoto());
	}
}
