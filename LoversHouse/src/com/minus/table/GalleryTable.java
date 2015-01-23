package com.minus.table;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "GalleryTable")
public class GalleryTable {
	
	@Id(column = "lastModefyTime")
	String lastModefyTime = null; //YYYY-MM-DD-hh:mm:ss
	String path = null;
	String oriPath = null;
	int deleteStatus = 0;   //0 means normal,1 means need to delete
	
	public String getLastModefyTime() {
		return lastModefyTime;
	}
	public void setLastModefyTime(String lastModefyTime) {
		this.lastModefyTime = lastModefyTime;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getOriPath() {
		return oriPath;
	}
	public void setOriPath(String oriPath) {
		this.oriPath = oriPath;
	}
	public int getDeleteStatus() {
		return deleteStatus;
	}
	public void setDeleteStatus(int deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	
}
