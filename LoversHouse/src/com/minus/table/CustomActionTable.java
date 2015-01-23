package com.minus.table;

import com.minus.xsocket.asynsocket.protocol.Protocol;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "CUSTOMACTION")
public class CustomActionTable {
	@Id(column = "typeID")

	private String typeID;
	private String content;
	private String status;
	
	
	public String getTypeID() {
		return typeID;
	}

	public void setTypeID(String typeID) {
		this.typeID = typeID;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
