package com.minus.lovershouse.singleton;

import android.util.Log;

import com.minus.lovershouse.BuildConfig;
import com.minus.xsocket.asynsocket.protocol.Protocol;


public class SelfInfo {
	public static final String SEX_BOY = "b";
	public static final String SEX_GIRL = "g";
	
	private String account;
	private String pwd;
	private String sex;
	private String nickName;  ////bigname是自己给自己起的名字
	private String appearance;  //人物形象
	private String status;  //动作状态
	private String smallName;////smallname对方给自己起的昵称
	private String target;
	private String headpotoPath;
	
	
	private String housestyle;
	private String action; //保存人物动作所产生的聊天语句,格式如：action="对方:好啊，抱抱"
	
	private String hudLabel;
	
	private boolean  isMatch;
	private boolean mainInit;
	private boolean online;
	
	
	private  SelfInfo(){
		  super();
		this.setDefault();
	
	}
	/**
	 * 单例模式,线程
	 * @return
	 */
	private static class  SelfInfoContainer{
		private static SelfInfo instance = new SelfInfo();
	}

	public static SelfInfo getInstance(){
		return  SelfInfoContainer.instance;
	}
	
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
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
	public String getSmallName() {
		return smallName;
	}
	public void setSmallName(String smallName) {
		this.smallName = smallName;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getHeadpotoPath() {
		return headpotoPath;
	}
	public void setHeadpotoPath(String headpotoPath) {
		this.headpotoPath = headpotoPath;
	}
	
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getHudLabel() {
		return hudLabel;
	}
	public void setHudLabel(String hudLabel) {
		this.hudLabel = hudLabel;
	}
	public boolean isMainInit() {
		return mainInit;
	}
	public void setMainInit(boolean mainInit) {
		this.mainInit = mainInit;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		if (BuildConfig.DEBUG) {
			Log.d("Login", "SelfInfo setOnline(" + online + ")");
			//Log.d("Login", "SelfInfo setOnline() " + getTraceInfo());
		}
		this.online = online;
	}
	
	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}

	/**
	 * equ  userinfo
	 * @param account  
	 * @param pwd
	 */
	public void setInfo(String mAccount , String mPwd){
		this.account =mAccount;
		this.pwd = mPwd;
	}
	
	public void setInfo(String acc,String se,String bn,String appear,String st,String sn,String tar){
		this.account = acc;
		this.sex =se;
		this.nickName = bn;
		this.appearance = appear;
		this.status = st;
		this.smallName = sn;
		this.target = tar;
		
	}
	
	public void setDefault(){
		
		this.mainInit = false;
		this.isMatch = false;
		String def = String.format("%c", Protocol.DEFAULT);
		this.sex="b";
		this.nickName = def;
	
		this.appearance =String.format("%c%c%c%c", Protocol.DEFAULT, Protocol.DEFAULT, Protocol.DEFAULT, Protocol.DEFAULT);
		this.status = Protocol.ActionEnd+"";
		this.smallName = def;
		this.target = def;
		this.hudLabel = def;
		this.headpotoPath = def;
		this.housestyle="1";
	}

	public String getHousestyle() {
		return housestyle;
	}

	public void setHousestyle(String housestyle) {
		this.housestyle = housestyle;
	}
	
}