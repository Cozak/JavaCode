package com.minus.lovershouse.enity;


public class ChatMsgEnity {

    private String author; //nickname

    private String initdate;  //date
    
    private String status; //read sending 
    
    private String message; // text; pic path; voice path
    
    private int  msgType;
    
    private String recordTime;


 

	public String getAuthor() {
		return author;
	}




	public void setAuthor(String author) {
		this.author = author;
	}




	public String getInitdate() {
		return initdate;
	}




	public void setInitdate(String initdate) {
		this.initdate = initdate;
	}




	public String getStatus() {
		return status;
	}




	public void setStatus(String status) {
		this.status = status;
	}




	public String getMessage() {
		return message;
	}




	public void setMessage(String message) {
		this.message = message;
	}




	public int getMsgType() {
		return msgType;
	}




	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}




	public String getRecordTime() {
		return recordTime;
	}




	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}




	public ChatMsgEnity() {
		super();
    }

	
    

	public ChatMsgEnity(String author, String initDate, String status, String message,int msgType,String recordLen) {
        super();
       this.author = author;
       this.status = status;
       this.message = message;
       this.msgType = msgType;
       this.recordTime = recordLen;
    }

}

