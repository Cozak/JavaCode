package com.minus.table;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "COMMON")
public class CommonTable {
	@Id(column = "account")
	private String account;
	private String motion;
	private String friends;
	private String witness;
	private String houseStyle;
	private String lightState;
	private String firstPicture;
	
	
//	"CREATE TABLE IF NOT EXISTS COMMON (account TEXT PRIMARY KEY,
//	motion TEXT, friends TEXT, witness TEXT, houseStyle TEXT, lightState TEXT, firstPicture TEXT)";
	public void setAccount(String account){
		this.account = account;
	}
	
	public String getAccount(){
		return this.account;
	}
	
	public String getMotion() {
		return motion;
	}

	public void setMotion(String motion) {
		this.motion = motion;
	}

	public String getFriends() {
		return friends;
	}

	public void setFriends(String friends) {
		this.friends = friends;
	}

	public String getWitness() {
		return witness;
	}

	public void setWitness(String witness) {
		this.witness = witness;
	}

	public String getHouseStyle() {
		return houseStyle;
	}

	public void setHouseStyle(String houseStyle) {
		this.houseStyle = houseStyle;
	}

	public String getLightState() {
		return lightState;
	}

	public void setLightState(String lightState) {
		this.lightState = lightState;
	}

	public String getFirstPicture() {
		return firstPicture;
	}

	public void setFirstPicture(String firstPicture) {
		this.firstPicture = firstPicture;
	}

	
	public void same(CommonTable item){
		this.account = item.getAccount();
	    this.motion = item.getMotion();
		this.firstPicture = item.getFirstPicture();
		this.friends = item.getFriends();
		this.houseStyle = item.getHouseStyle();
		this.lightState = item.getLightState();
		this.witness = item.getWitness();
	
	}

}
