package com.minus.table;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import java.sql.Blob;

@Table(name = "BASICSETTING")
//TARGET (account TEXT PRIMARY KEY, headphoto BLOB)
public class BasicsettingTable {
	@Id(column = "account")
	private String account;
	private boolean voice;
	private boolean viberate;
	private int voiceOrviberate;//0无振动无铃声 1有振动无铃声 2有铃声无振动 3有振动有铃声
	private boolean isProtected;
	private boolean num;
	private boolean graph;
	private String numPass;
	
	public void setAccount(String account){
		this.account = account;
	} 
	
	public String getAccount(){
		return this.account;
	}

	public boolean isVoice() {
		return voice;
	}

	public void setVoice(boolean voice) {
		this.voice = voice;
	}

	public boolean isViberate() {
		return viberate;
	}

	public int getVoiceOrviberate() {
		return voiceOrviberate;
	}

	public void setVoiceOrviberate(int voiceOrviberate) {
		this.voiceOrviberate = voiceOrviberate;
	}

	public void setViberate(boolean viberate) {
		this.viberate = viberate;
	}



	public boolean isProtected() {
		return isProtected;
	}

	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}


	public boolean isNum() {
		return num;
	}

	public void setNum(boolean num) {
		this.num = num;
	}

	public boolean isGraph() {
		return graph;
	}

	public void setGraph(boolean graph) {
		this.graph = graph;
	}

	public String getNumPass() {
		return numPass;
	}

	public void setNumPass(String numPass) {
		this.numPass = numPass;
	}
	
	
	
	
//	public void same(TargetTable target){
//		
//	}
}
