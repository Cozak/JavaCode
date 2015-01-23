package com.minus.sql_interface;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.minus.calendar.CalendarMainActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.table.AlarmClockTable;
import com.minus.table.BasicsettingTable;
import com.minus.table.CalendarTable;
import com.minus.table.ChatTable;
import com.minus.table.CommonTable;
import com.minus.table.CustomActionTable;
import com.minus.table.DiaryTable;
import com.minus.table.GalleryTable;
import com.minus.table.TargetTable;
import com.minus.table.Temp;
import com.minus.table.UserTable;
import com.minus.xsocket.asynsocket.protocol.Protocol;

public class Database implements DatabaseDAO {
	
	private FinalDb db;
	private static  Context context;
	private static  int dbVersion = 1;
	private static  boolean  isDebug = false;
	private  String dbName = null;

	/**
	 * 构造函数，构造一个finaldb
	 * @param context
	 */
	private  Database(Context context) {
		
		SharedPreferences mSP = context
				.getSharedPreferences(Protocol.PREFERENCE_NAME,
						Activity.MODE_PRIVATE);
	      String dbTitle = mSP.getString("LastUser", "");
	      if(dbTitle.equals("")) {
//	    	  needInit = true; 
	    	
	      }else{
		this.dbName = dbTitle
				+".db";
		db = FinalDb.create(context, dbName,  isDebug, dbVersion, null,false);
//		initTableInfo()
//		needInit = false;
	      }
//		db = FinalDb.create(context,"minius.db");
//		db.getDatebase()
//		FinalDb.create(context, dbName, isDebug, dbVersion, dbUpdateListener)
		
	}
//	
//   /**
//    * constructor 
//    * @param context
//    * @param dbName
//    */
//	public Database(Context context,String dbName) {
//		
//		db = FinalDb.create(context, dbName,true,dbVersion,null);
//	}
//	
	
	
	
	/**
	 * TODO 
	 */
	public void closeDatabase() {
//		  if(db != null){
//	    	  if(db.getDatebase().isOpen()){
//			      db.getDatebase().close();
//			      }
//	    	  }
	}

	
	public boolean  initDatabase(String dbName){
			
//				
//			SharedPreferences mSP = context
//					.getSharedPreferences(Protocol.PREFERENCE_NAME,
//							Activity.MODE_PRIVATE);
//		      String dbTitle = mSP.getString("LastUser", "");
//		      if(dbTitle.equals("")) {
////		    	  if(db != null){
////		    	  if(db.getDatebase().isOpen()){
////				      db.getDatebase().close();
////				      }
////		    	  }
//		    	  return false; 
//		      }
		   
//		  if(db != null){
//    	  if(db.getDatebase().isOpen()){
//		      db.getDatebase().close();
//		      }
//    	  }
//		  db = null;
		this.dbName = dbName +".db";
		db = FinalDb.create(context, this.dbName, isDebug, dbVersion, null, true);
		
		return true;	
	}

	
//
//	public static boolean isNeedInit() {
//		return needInit;
//	}
//
//	public static void setNeedInit(boolean needInit) {
//		Database.needInit = needInit;
//	}


	public boolean deleteUserTable(){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return false;
		}
		db.delete(UserTable.class);
		return true;
	}
	
	public boolean deleteTargetTable(){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return false;
		}
		db.delete(TargetTable.class);
		return true;
	}
	
	public boolean deleteDiaryTable(){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return false;
		}
		db.delete(DiaryTable.class);
		return true;
	}
	
	public boolean deleteAlarmClockTable(){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return false;
		}
		db.delete(AlarmClockTable.class);
		return true;
	}
	
	public boolean deleteChatTable(){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
			return false;
		}
		db.delete(ChatTable.class);
		return true;
	}
	
	public boolean deleteCalendarTable(){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return false;
		}
		db.delete(CalendarTable.class);
		return true;
	}
	
	public boolean deleteAll(String id){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return false;
		}
		db.deleteById(UserTable.class,id);
		db.deleteById(TargetTable.class,id);
		db.deleteByWhere(DiaryTable.class,"author='" + id + "'");
		db.deleteByWhere(AlarmClockTable.class,"account='" + id + "'");
		db.deleteByWhere(ChatTable.class,"account='" + id + "'");
		db.deleteByWhere(CalendarTable.class,"account='" + id + "'");
		db.deleteByWhere(BasicsettingTable.class,"account='" + id + "'");
		return true;
	}
	
	public Boolean userTableEmpty(){
		if(db == null) return true;
		if(!db.getDatebase().isOpen()){
			return true;
		}
		if(db.findAll(UserTable.class).size()!=0) 
			return false;
		else return true;
	}
	/*
	 * 根据帐号删除对应的用户表数据
	 */
	public Boolean emptyUserTable(String id){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
			return false;
		}
		db.deleteById(UserTable.class,id);
		return true;
	}
	
	//获取自己的信息 邮箱密码
	public UserTable getSelfInfo(){
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return null;
		}		
		if(userTableEmpty()) {
			return null;
		}
		UserTable user = new UserTable();
		List<UserTable> userlist = db.findAll(UserTable.class);
		user.same((UserTable)userlist.get(0));
		return user;
	} 

/**
 * return path where store the img
 * when return -1 means null database , while -2 means null table
 * @param account
 * @return
 */
	public String getHeadPhoto(String account){
		if(db == null) return "-1";
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return "-1";
		}
		UserTable user = new UserTable();
		if(db.findById(account, UserTable.class) == null) {
//			System.out.println(" getHeadPhoto account not exit");
			return "-2";
		}
	
		user.same((UserTable)db.findById(account, UserTable.class));
		return user.getHeadphoto();
	}
		
	//insert基本信息 如果没头像调用 先删除后插入 包括邮箱，密码， 默认头像Q
	public boolean addSelfInfo(String account,String password,String defaultPath){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		UserTable user = new UserTable();
		if(db.findById(account, UserTable.class) != null) {
			db.deleteById(UserTable.class, account);
		}
//		db.deleteAll(UserTable.class);
		
		String def = String.format("%c",Protocol.DEFAULT);
		String figureDef =String.format("%c%c%c%c",Protocol.DEFAULT,Protocol.DEFAULT,Protocol.DEFAULT,Protocol.DEFAULT); 
	    String  statusDef = Protocol.ActionEnd+"";
	    user.setAccount(account);
	    user.setPassword(password);
	    user.setSex(def);
	    user.setBigname(def);
	    user.setAppearance(figureDef);
	    user.setStatus(statusDef);
	    user.setSmallname(def);
	    user.setTarget(def);
		user.setHeadphoto(defaultPath);   
		
		return db.saveBindId(user);
	}
	
public boolean updateSelfInfo(String account , String sex,String bigname,String appearance,String  status 
	,String smallname ,String target)
	{
	if(db == null) return false;
	if(!db.getDatebase().isOpen()){

		return false;
	}
	UserTable user = new UserTable();
	if(db.findById(account, UserTable.class) != null) {
		user.same(db.findById(account, UserTable.class));
		user.setSex(sex);
		user.setBigname(bigname);
		user.setAppearance(appearance);
		user.setStatus( status);
		user.setSmallname(smallname);
		user.setTarget(target);
		db.update(user, "account='"+user.getAccount()+"'");
	}
	else {
		return false;
	}
	
	return true;
	}
//modify password
public boolean updateSelfPassword(String account,String password){
	if(db == null) return false;
	if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		UserTable user = new UserTable();
		if(db.findById(account, UserTable.class) != null) {
			user.same(db.findById(account, UserTable.class));
			user.setPassword(password);
		}
		else {
			return false;
		}
		db.update(user, "account='"+user.getAccount()+"'");
		return true;
	}
public boolean updateSelfSex(String account,String sex)
	{
	if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		UserTable user = new UserTable();
		if(db.findById(account, UserTable.class) != null) {
			user.same(db.findById(account, UserTable.class));
			user.setSex(sex);
		}
		else {
			return false;
		}
		db.update(user, "account='"+user.getAccount()+"'");
		return true;
	  
	}

	//修改大名
public  boolean updateSelfBigname(String account,String bigname){
	if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		UserTable user = new UserTable();
		if(db.findById(account, UserTable.class) != null) {
			user.same(db.findById(account, UserTable.class));
			user.setBigname(bigname);
		}
		else {
			return false;
		}
		db.update(user, "account='"+user.getAccount()+"'");
		return true;
	}

	//修改外形
public boolean updateSelfAppearance(String account ,String appearance){
	if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		UserTable user = new UserTable();
		if(db.findById(account, UserTable.class) != null) {
			user.same(db.findById(account, UserTable.class));
			user.setAppearance(appearance);
		}
		else {
			return false;
		}
		db.update(user, "account='"+user.getAccount()+"'");
		return true;
	  
	}

	//修改状态
public boolean updateSelfStatus(String account ,String status){
	if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		UserTable user = new UserTable();
		if(db.findById(account, UserTable.class) != null) {
			user.same(db.findById(account, UserTable.class));
			user.setStatus(status);
		}
		else {
			return false;
		}
		db.update(user, "account='"+user.getAccount()+"'");
		return true;
	  
	}

	//修改小名
public boolean updateSelfSmallname(String  account,String smallname){
	if(db == null) return false;
	if(!db.getDatebase().isOpen()){

//		System.out.println("DataBase is not open");
		return false;
	}
	UserTable user = new UserTable();
	if(db.findById(account, UserTable.class) != null) {
		user.same(db.findById(account, UserTable.class));
		user.setSmallname(smallname);
	}
	else {
		return false;
	}
	db.update(user, "account='"+user.getAccount()+"'");
	return true;
	 
	}
public boolean updateSelfHeadPhoto(String account,String  headphotopath){
	if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		UserTable user = new UserTable();
		if(db.findById(account, UserTable.class) != null) {
			user.same(db.findById(account, UserTable.class)); 
            user.setHeadphoto( headphotopath);
		}
		else {
//			System.out.println("Not find the user: "+account);
			return false;
		}
		db.update(user, "account='"+user.getAccount()+"'");
//		System.out.println("updates");
		return true;
	}
	/**
	 * TODO  no use  delete it 
	 * @param account
	 * @param headphoto
	 * @return
	 */
	public boolean appendSelfHeadPhoto(String account,String headphotopath){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		UserTable user = new UserTable();
		if(db.findById(account, UserTable.class) != null) {
			user.same(db.findById(account, UserTable.class));
			user.setHeadphoto(headphotopath);
		}
		else {
//			System.out.println("Not find the user: "+account);
			return false;
		}
		db.update(user, "account='"+user.getAccount()+"'");
		return true;
	}
	
	

	//target,
	public TargetTable getTargetInfo(){
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){

			return null;
		}		

		TargetTable targetinfo= new TargetTable();
		List<TargetTable> tarlist = db.findAll(TargetTable.class);
		if(tarlist.size() >0 ) {
			targetinfo = tarlist.get(0);
			if(targetinfo.getSex().equals(Protocol.DEFAULT+"")) {
				targetinfo.setSex("b");
			}else{
				targetinfo.setSex("g");
			}
		}else{
			String noExist = Protocol.DEFAULT +"";
			targetinfo.setAccount(noExist);
			targetinfo.setAppearance(noExist);
			targetinfo.setBigName(noExist);
			targetinfo.setHeadphoto(noExist);
			targetinfo.setSex("b");
			targetinfo.setSmallname(noExist);
			targetinfo.setStatus(noExist);
			targetinfo.setTarget(noExist);
		}


	    return targetinfo;
	}
	public String getTargetHeadPhoto(String account){
		if(db == null) return "";
		if(!db.getDatebase().isOpen()){

			return null;
		}
		TargetTable target = new TargetTable();
		if(db.findById(account, TargetTable.class) != null)
			target.same(db.findById(account, TargetTable.class));
		else {
			return Protocol.DEFAULT+"";
		}
		return target.getHeadphoto();
	}
	
	public boolean addTargetInfo(String account ,String sex,String bigname,String appearance,String status,String smallname ,String target)
	{
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

			return false;
		}
		      db.deleteAll(TargetTable.class);
	           String  def = Protocol.DEFAULT  + "";
	           TargetTable targetInfo = new TargetTable();
              targetInfo.setAccount(account);
              targetInfo.setSex(sex);
              targetInfo.setBigName(bigname);
              targetInfo.setAppearance(appearance);
              targetInfo.setStatus(status);
              targetInfo.setSmallname(smallname);
              targetInfo.setTarget(target);
              targetInfo.setHeadphoto(def);
	   		return db.saveBindId(targetInfo);
	      
	}
	
	public boolean updateTargetBigname(String account,String bigname){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

			return false;
		}
		TargetTable tar = new TargetTable();
		if(db.findById(account, TargetTable.class) != null) {
			tar = db.findById(account, TargetTable.class);
			tar.setBigName(bigname);
		}
		else {
			return false;
		}
		db.update(tar, "account='"+account+"'");
		return true;
	}
	
	public boolean updateTargetAppearance(String account,String appearance){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

			return false;
		}
		TargetTable tar = new TargetTable();
		if(db.findById(account, TargetTable.class) != null) {
			tar = db.findById(account, TargetTable.class);
			tar.setAppearance(appearance);
		}
		else {
			return false;
		}
		db.update(tar, "account='"+account+"'");
		return true;
	  
	}
	
	public boolean updateTargetStatus(String account ,String status){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

			return false;
		}
		TargetTable tar = new TargetTable();
		if(db.findById(account, TargetTable.class) != null) {
			tar = db.findById(account, TargetTable.class);
			tar.setStatus(status);
		}
		else {
			return false;
		}
		db.update(tar, "account='"+account+"'");
		return true;
	    
	}
	public boolean updateTargetSmallname(String account ,String smallname){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

			return false;
		}
		TargetTable tar = new TargetTable();
		if(db.findById(account, TargetTable.class) != null) {
			tar = db.findById(account, TargetTable.class);
			tar.setSmallname(smallname);
		}
		else {
			return false;
		}
		db.update(tar, "account='"+account+"'");
		return true;
	
	}


/**
 *
 * @param account
 * @param headphotopath
 * @return
 */
	public boolean updateTargetHeadPhoto(String account,String headphotopath){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

			return false;
		}
		TargetTable tar = new TargetTable();
		if(db.findById(account, TargetTable.class) != null) {
			tar = db.findById(account, TargetTable.class);
			tar.setHeadphoto(headphotopath);
		}
		else {
			return false;
		}
		db.update(tar, "account='"+account+"'");
		return true;
	}
	
//	/**
////	 * what is append
//	 * @param account
//	 * @param headphoto
//	 * @return
//	 */
//	public boolean appendTargetHeadPhoto(String account,String headphotopath) {
//		if(!db.getDatebase().isOpen()) {
//			System.out.println("DataBase is not open");
//			return false;
//		}
//		TargetTable target = new TargetTable();
//		if(db.findById(account, TargetTable.class) != null) {
//			target.same(db.findById(account, TargetTable.class));
//			target.setHeadphoto(headphotopath);
//		}
//		else {
//			System.out.println("Not find the target: "+account);
//			return false;
//		}
//		db.update(target, "account='"+target.getAccount()+"'");
//		return true;
//	}
	
	/*
	//basicsetting
	@Override
	public BasicsettingTable getBasicsettingInfo() {
		// TODO Auto-generated method stub
		if(!db.getDatebase().isOpen()) {
			return null;
		}		

		BasicsettingTable bsinfo= new BasicsettingTable();
		List<BasicsettingTable> bslist = db.findAll(BasicsettingTable.class);
		if(bslist.size() >0 ) {
			bsinfo = bslist.get(0);
		}else{
			Boolean defaultState=false;
			bsinfo.setAccount(Protocol.DEFAULT+"");
			bsinfo.setVoice(defaultState);
			bsinfo.setViberate(defaultState);
			bsinfo.setProtected(defaultState);
			bsinfo.setNum(defaultState);
			bsinfo.setGraph(defaultState);
			bsinfo.setNumPass("");
		}


	    return bsinfo;
	}

	public boolean addBasicsettingInfo(String account,Boolean defaultState,String numpass) {
		if(!db.getDatebase().isOpen()) {
			System.out.println("DataBase is not open");
			return false;
		}
		BasicsettingTable bs = new BasicsettingTable();

		db.deleteAll(BasicsettingTable.class);
		bs.setAccount(account);
		bs.setVoice(defaultState);
		bs.setViberate(defaultState);
		bs.setProtected(defaultState);
		bs.setNum(defaultState);
		bs.setGraph(defaultState);
		bs.setNumPass(numpass);
		return db.saveBindId(bs);
	}
	
	@Override
	public boolean updateBasicsettingVoice(String account, Boolean voice) {
		// TODO Auto-generated method stub
		if(!db.getDatebase().isOpen()) {
			return false;
		}
		BasicsettingTable bs = new BasicsettingTable();
		if(db.findById(account, BasicsettingTable.class) != null) {
			bs = db.findById(account, BasicsettingTable.class);
			bs.setVoice(voice);
		}
		else {
			return false;
		}
		db.update(bs, "account='"+account+"'");
		return true;
	}

	@Override
	public boolean updateBasicsettingViberate(String account, Boolean viberate) {
		// TODO Auto-generated method stub
		if(!db.getDatebase().isOpen()) {
			return false;
		}
		BasicsettingTable bs = new BasicsettingTable();
		if(db.findById(account, BasicsettingTable.class) != null) {
			bs = db.findById(account, BasicsettingTable.class);
			bs.setViberate(viberate);
		}
		else {
			return false;
		}
		db.update(bs, "account='"+account+"'");
		return true;
	}
	@Override
	public boolean updateBasicsettingVoiceOrViberate(String account,
			int voice0rviberate) {
		// TODO Auto-generated method stub
		if(!db.getDatebase().isOpen()) {
			return false;
		}
		BasicsettingTable bs = new BasicsettingTable();
		if(db.findById(account, BasicsettingTable.class) != null) {
			bs = db.findById(account, BasicsettingTable.class);
			bs.setVoiceOrviberate(voice0rviberate);
		}
		else {
			return false;
		}
		db.update(bs, "account='"+account+"'");
		return true;
	}

	@Override
	public boolean updateBasicsettingProtected(String account,
			Boolean isprotected) {
		// TODO Auto-generated method stub
		if(!db.getDatebase().isOpen()) {
			return false;
		}
		BasicsettingTable bs = new BasicsettingTable();
		if(db.findById(account, BasicsettingTable.class) != null) {
			bs = db.findById(account, BasicsettingTable.class);
			bs.setProtected(isprotected);
		}
		else {
			return false;
		}
		db.update(bs, "account='"+account+"'");
		return true;
	}

	@Override
	public boolean updateBasicsettingNum(String account,
			Boolean num) {
		// TODO Auto-generated method stub
		if(!db.getDatebase().isOpen()) {
			return false;
		}
		BasicsettingTable bs = new BasicsettingTable();
		if(db.findById(account, BasicsettingTable.class) != null) {
			bs = db.findById(account, BasicsettingTable.class);
			bs.setNum(num);
		}
		else {
			return false;
		}
		db.update(bs, "account='"+account+"'");
		return true;
	}
	@Override
	public boolean updateBasicsettingGraph(String account,
			Boolean graph) {
		// TODO Auto-generated method stub
		if(!db.getDatebase().isOpen()) {
			return false;
		}
		BasicsettingTable bs = new BasicsettingTable();
		if(db.findById(account, BasicsettingTable.class) != null) {
			bs = db.findById(account, BasicsettingTable.class);
			bs.setGraph(graph);
		}
		else {
			return false;
		}
		db.update(bs, "account='"+account+"'");
		return true;
	}
	
	@Override
	public boolean updateBasicsettingPass(String account, String numpass) {
		// TODO Auto-generated method stub
		if(!db.getDatebase().isOpen()) {
			return false;
		}
		BasicsettingTable bs = new BasicsettingTable();
		if(db.findById(account, BasicsettingTable.class) != null) {
			bs = db.findById(account, BasicsettingTable.class);
			bs.setNumPass(numpass);
		}
		else {
			return false;
		}
		db.update(bs, "account='"+account+"'");
		return true;
	}
	*/
	
	//diary
	/**
	 * 
	 * @param author
	 * @param initDate
	 * @param title
	 * @param content
	 * @param editDate
	 * @param serverState
	 * @param isNew 1 isNew
	 * @return
	 */
	public boolean addDiary(String author,String initdate,String title,String content,String editdate,String serverState, int isNew){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

			
			return false;
		}
		 List<DiaryTable > diarylist = 
				 db.findAllByWhere(DiaryTable.class,"author='"+author+"' AND initdate='"+initdate+"'");
		 if(diarylist != null && diarylist.size() > 0){
			 DiaryTable diary = diarylist.get(0);
			 diary.setAuthor(author);
			 diary.setContent(content);
			 diary.setEditdate(editdate);
			 diary.setInitdate(initdate);
			 diary.setIsnew(isNew);
			 diary.setServerState(serverState);
			 diary.setTitle(title);
			 db.update(diary);
		 }else{

		DiaryTable diary = new DiaryTable();
		diary.setAuthor(author);
		diary.setContent(content);
		diary.setEditdate(editdate);
		diary.setInitdate(initdate);
		diary.setIsnew(isNew);
		diary.setServerState(serverState);
		diary.setTitle(title);
		 db.save(diary);
		 }
		 return true;
	}

	/**
	 * no boolean but int
	 * @param author
	 * @param initDate
	 * @param title
	 * @param content
	 * @param editDate
	 * @return
	 */
	public boolean modifyDiary(String author,String initdate,String title,String content,String editdate,String serverState,int isNew){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

			
			return false;
		}
		 List<DiaryTable > diarylist = 
				 db.findAllByWhere(DiaryTable.class,"author='"+author+"' AND initdate='"+initdate+"'");
		if(diarylist!= null && diarylist.size() >0){
			 DiaryTable diary = diarylist.get(0);
			 diary.setAuthor(author);
				diary.setContent(content);
				diary.setEditdate(editdate);
				diary.setInitdate(initdate);
				diary.setIsnew(isNew);  //1
				diary.setTitle(title);
				diary.setServerState(serverState);
				db.update(diary);
				return true;

		}
		
		return false;
	}
	
	public boolean removeDiary(String author,String initdate){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		if(db.findAllByWhere(DiaryTable.class, "initDate='" + initdate +"' AND author='" + author + "'" ) == null) {
		
			return false;
		}
		db.deleteByWhere(DiaryTable.class, "initDate='" + initdate +"' AND author='" + author + "'" );
		return true;
	}
	
	public boolean changeNewDiary(String author,String initdate,int isnew){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		
		 List<DiaryTable > diarylist = 
				 db.findAllByWhere(DiaryTable.class,"author='"+author+"' AND initdate='"+initdate+"'");
		if(diarylist!= null && diarylist.size() >0){
	     DiaryTable diary = diarylist.get(0);
		

		diary.setAuthor(author);
		diary.setInitdate(initdate);
		diary.setIsnew(isnew);
		db.update(diary);

		return true;
		}
		return false;
	}
	
	

	//判断该记事本记录是否存在
	public boolean isExistTheDiaryItemWithAccount(String account,String initDate)
	{
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return false;
		}
		 List<DiaryTable > diarylist = 
				 db.findAllByWhere(DiaryTable.class,"author='"+account+"' AND initdate='"+initDate+"'");
		
		if(diarylist.size() >0){
			return true;
		}

		return false;
	  
	}
	//获取的日志不包括那些待删除状态的;
	public List<DiaryTable> getDiary(){
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return null;
		}
		List<DiaryTable> diarylist = db.findAll(DiaryTable.class, "editDate DESC");	
		List<DiaryTable> data =new ArrayList<DiaryTable>();
		for(DiaryTable mDT: diarylist) {
			 String  stateFromServer = mDT.getServerState();
//			 Log.v("result","serverstate" + stateFromServer );
			 if(stateFromServer == null)   continue;
			 if (stateFromServer.equals(Protocol.WaitForServerComfirmRemove+""))
	          {
	              continue;
	          }
		
			 data.add(mDT);
			 
		}
		 

		return data;
	}
	
	//获取待删除的所有日志;
	public List<DiaryTable> getWaitForRemoveDiary()
	{
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){

			return null;
		}
	
		List<DiaryTable> diarylist = db.findAllByWhere(DiaryTable.class, "serverState ='"+Protocol.WaitForServerComfirmRemove+"'", "editDate DESC");	
	
	    return diarylist;
	}
	//获取待添加的所有日志;
	public List<DiaryTable> getWaitForAddDiary()
	{
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){
			return null;
		}
		List<DiaryTable> diarylist = db.findAllByWhere(DiaryTable.class, "serverState ='"+Protocol.WaitForServerComfirmAdd+"'", "editdate DESC");	
	   
	    return diarylist;
	}

	//获取待修改的所有日志;
	public List<DiaryTable> getWaitForModifyDiary()
	{
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){
			return null;
		}
		List<DiaryTable> diarylist = db.findAllByWhere(DiaryTable.class, "serverState ='"+Protocol.WaitForServerComfirmModify+"'", "editdate DESC");	
	   
	    return diarylist;
	}


	//获取数据中的第一条记录，如果不是草稿的话，那么就代表没有草稿;
	public List<DiaryTable> getDiaryDraft() {
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return null;
		}
		List<DiaryTable> diarylist = db.findAllByWhere(DiaryTable.class, "serverState ='"+Protocol.DiaryDraft+"'", "editDate DESC");	
	
	    return diarylist;
	}

//-------------------------------------------------------自定义动作---------------------------------------------------
	//获取的action不包括那些待删除状态的;以及删除状态的 已经failed
	public  List<CustomActionTable> getAllAction() {
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){
			return null;
		}
		 List<CustomActionTable> actionlist = db.findAll(CustomActionTable.class, "typeID ASC");	
		
		 if(actionlist == null || actionlist.size() <5){
			 this.initActionTable();
			 return null;
		 }
		 List<CustomActionTable> data =new ArrayList<CustomActionTable>() ;
		for(CustomActionTable mDT: actionlist){
			 String  stateFromServer = mDT.getStatus();
		
			 if(stateFromServer == null)   continue;
			 if (!(stateFromServer.equals(Protocol.SuccessFromServer+"")))
	          {
	              continue;
	          }
		
			 data.add(mDT);
			 
		}
		return data;

	}
	
	//获取添加action需要的typeid
		public  String getAddActionId() {
			if(db == null) return null;
			if(!db.getDatebase().isOpen()){
				return null;
			}
			 List<CustomActionTable> actionlist = 
					 db.findAllByWhere(CustomActionTable.class, "status ='"+Protocol.ActionRemoved+"'", "typeID ASC");	
		     if(actionlist != null && actionlist.size() >0){
		    	 return actionlist.get(0).getTypeID();
		     }
		
			return null;

		}
	
	public boolean addCustomAction(String typeID,String content,String serverState) {
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
			return false;
		}
		if(db.findById(typeID, CustomActionTable.class) != null){
			db.deleteById(CustomActionTable.class, typeID);
		}
		CustomActionTable  mCA = new CustomActionTable();
		mCA.setContent(content);
		mCA.setTypeID(typeID);
		mCA.setStatus(serverState);
	
		return db.saveBindId(mCA);
	}
	
	public boolean updateActionStatus(String typeID ,String status) {
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
			return false;
		}
		CustomActionTable  tar = new CustomActionTable();
		if(db.findById(typeID, CustomActionTable.class) != null){
			tar = db.findById(typeID, CustomActionTable.class);
			tar.setStatus(status);
		}
		else {
			return false;
		}
		db.update(tar, "typeID='"+typeID+"'");
		return true;
	}
	
	public boolean updateActionContent(String typeID ,String content) {
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
			return false;
		}
		CustomActionTable  tar = new CustomActionTable();
		if(db.findById(typeID, CustomActionTable.class) != null){
			tar = db.findById(typeID, CustomActionTable.class);
			tar.setContent(content);
		}
		else {
			return false;
		}
		db.update(tar, "typeID='"+typeID+"'");
		return true;
	}
	
	public boolean updateWholeAction(String typeID ,String content,String status) {
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
			return false;
		}
		CustomActionTable  tar = new CustomActionTable();
		if(db.findById(typeID, CustomActionTable.class) != null){
			tar = db.findById(typeID, CustomActionTable.class);
			tar.setContent(content);
			tar.setStatus(status);
		}
		else {
			return false;
		}
		db.update(tar, "typeID='"+typeID+"'");
		return true;
	}
	/*
	 * 初始化对应的action表数据 自定义动作 1--5 
	 */
	public boolean initActionTable(){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
			return false;
		}
		db.deleteAll(CustomActionTable.class);
		CustomActionTable  mCA = new CustomActionTable();
		mCA.setContent("");
		mCA.setStatus(Protocol.ActionRemoved+"");
		for(int i = 1;i<6;i++){
		
		mCA.setTypeID(String.format("%d", i));	
		 db.saveBindId(mCA);
		}
	       
		return true;
	}
	
	//-------------------------------------------------------------------chat
	public boolean addChat(String account,String initdate,String status,String message,
			int msgtype, String recordTime){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		ChatTable old = db.findById(initdate, ChatTable.class);
		if (old != null) //消息initdate重复的情况下，返回失败
			return false;
		/*if(old != null) {
			if (old.getMessage().equals(message)) //消息重复的情况下，返回失败
				return false;
			else
				db.deleteById(ChatTable.class, initdate);
		}*/
		ChatTable chat = new ChatTable();
		chat.setAccount(account);
		chat.setInitdate(initdate);
		chat.setMessage(message);
		chat.setStatus(status);
		chat.setMsgtype(msgtype);
		chat.setRecordTime(recordTime);
		return db.saveBindId(chat);
	}

	//把fromDate到toDate时间段内的状态都改为state,除了状态为发送失败和发送中的。
	public boolean modifyChatStatus(String author ,String  fromDate ,String toDate,String status){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
//		@"UPDATE CHAT SET status = '%@' WHERE account= '%@' AND initDate>='%@' AND initDate<='%@' AND status != '%@'",status,author,fromDate,toDate,[NSString stringWithFormat:@"%c",SendFail]]
		String sql = "UPDATE CHAT SET status = '"+status+"' WHERE account='"+author+"' AND initDate>='"+fromDate+"' AND initDate<='"+toDate+"' AND status != '"+Protocol.SendFail+"'"+ " AND status != '"+Protocol.Sending+"'";
      try{
    	  db.getDatebase().execSQL(sql);
      }catch(Exception e) {
    	  return false;
      }
		
		
		return true;
	}
	
	public boolean modifyChatStatus(String author ,String  initdate,String status){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		String sql = "UPDATE CHAT SET status = '"+status+"' WHERE account='"+author+"' AND initDate='"+initdate+"'";
      try{
    	  db.getDatebase().execSQL(sql);
      }catch(Exception e) {
    	  return false;
      }
		
		
		return true;
	}
	public boolean removeChat(String account,String initdate){	
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

			return false;
		}
		db.deleteByWhere(ChatTable.class, "account='"+account+"' AND initDate='"+initdate+"'");
		return true;
	}
	

	//获取最后一条非动作的消息的时间;
public String getLastMsgDate(){
	if(db == null) return null;
	if(!db.getDatebase().isOpen()){

//		System.out.println("DataBase is not open");
		return "";
	}
	   String lastMsgDate = "";
		List<ChatTable> chatlist = db.findAll(ChatTable.class, "initDate DESC");
		for(ChatTable mcT : chatlist) {
			if((mcT.getMsgtype() != Protocol.VALUE_LEFT_ACTION) && (mcT.getMsgtype() != Protocol.VALUE_RIGHT_ACTION)) {
				  lastMsgDate  = mcT.getInitdate();
				  break;
			}
		}
	    return lastMsgDate;
	}
	
	public List<ChatTable> getChat(){
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return null;
		}
		List<ChatTable> chatlist = db.findAll(ChatTable.class, "initDate ASC");
		return chatlist;
	};
	
	//获取那些仍然是发送状态的聊天记录
	public List<ChatTable> getSendingChat()
	{
		if(db == null) return null;
		if(!db.getDatebase().isOpen()) {
//			System.out.println("DataBase is not open");
			return null;
		}
		
		List<ChatTable> chatlist = db.findAllByWhere(ChatTable.class, "status ='"+Protocol.Sending+"'", "initDate ASC");	
	
	    return chatlist;
	        }
/**
 *最新的在数组后面;
 */
	public List<ChatTable> getLastChat(int beginIndex,int endIndex){
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return null;
		}
		//降序排序
		List<ChatTable> tempChatlist = db.findAll(ChatTable.class, "initDate DESC LIMIT "+beginIndex+","+endIndex);
		List<ChatTable> chatList = new ArrayList<ChatTable>() ;
		/**
		 *最新的在数组后面;
		 */
		for(ChatTable mCt : tempChatlist) {
			chatList.add(0,mCt);
		}
		return chatList;
	}
	//根据帐号和时间获取特定的聊天的记录;
	public List<ChatTable> getChatWithAccount(String account, String iniDate){
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return null;
		}
		List<ChatTable> chatlist = db.findAllByWhere(ChatTable.class,"account='"+account+"' AND initDate='"+iniDate+"'");;
	
		return chatlist;
	}

	
	//calendar
	public boolean addCalendar(String initDate, String editDate, String memoDate, 
			String promptPolicy, String title){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return false;
		}
		if (SelfInfo.getInstance().getAccount() == null)
			return false;
		
		if(db.findById(initDate, CalendarTable.class) != null) {
			db.deleteById(CalendarTable.class, initDate);
		}

		if (promptPolicy.charAt(0) < 1 || promptPolicy.charAt(0) > 4)
			promptPolicy = "" + (char) 0x01;
		
		CalendarTable calendar = new CalendarTable();
		calendar.setInitDate(initDate);
		calendar.setEditDate(editDate);
		calendar.setMemoDate(memoDate);
		calendar.setTitle(title);
		calendar.setPromptPolicy(promptPolicy);
		calendar.setServerState("" + Protocol.WaitForServerComfirmAdd);
		
		boolean ret = db.saveBindId(calendar);
//		Log.d("calendar", "Database::addCalendar() success, initDate=" + initDate);
		return ret;
	}
	
	public boolean modifyCalendar(String initDate, String editDate, String memoDate, 
			String promptPolicy, String title){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return false;
		}

		CalendarTable calendar = db.findById(initDate, CalendarTable.class);
		if(calendar == null) {
//			Log.d("calendar", "database:modifyCalendar() fail, "
//					+ "can not find target, initDate=" + initDate);
			return false;
		}

		if (promptPolicy.charAt(0) < 1 || promptPolicy.charAt(0) > 4)
			promptPolicy = "" + (char) 0x01;
		
		calendar.setInitDate(initDate);
		calendar.setEditDate(editDate);
		calendar.setMemoDate(memoDate);
		calendar.setTitle(title);
		calendar.setPromptPolicy(promptPolicy);
		calendar.setServerState("" + Protocol.WaitForServerComfirmModify);
		db.update(calendar);

//		Log.d("calendar", "Database::modifyCalendar() success, initDate=" + initDate);
		return true;
	}

	//第一次调用，仅仅设置标志为WaitForServerComfirmRemove。第二次调用才删除。
	public boolean removeCalendar(String initDate){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

//			System.out.println("DataBase is not open");
			return false;
		}
		
		CalendarTable calendar = db.findById(initDate, CalendarTable.class);
		if(calendar == null) {
//			Log.d("calendar", "Database::removeCalendar() fail, "
//					+ "can not find target, initDate=" + initDate);
			return false;
		}
		
		if (calendar.getServerState().equals("" + Protocol.WaitForServerComfirmRemove)) {
			db.deleteByWhere(CalendarTable.class, "initDate='" + initDate +"'");
//			Log.d("calendar", "Database::removeCalendar() success, initDate=" + initDate);
		}
		else {
			calendar.setServerState("" + Protocol.WaitForServerComfirmRemove);
			db.update(calendar);
//			Log.d("calendar", "Database::removeCalendar() success setServerState, initDate=" + initDate);
		}

		return true;
	}

	public boolean modifyCalendarServerState(String initDate, String serverState) {
		if(!db.getDatebase().isOpen()) {
//			System.out.println("DataBase is not open");
			return false;
		}

		CalendarTable calendar = db.findById(initDate, CalendarTable.class);
		if(calendar == null) {
//			Log.d("calendar", "Database::modifyCalendarServerState() fail, "
//					+ "can not find target, initDate=" + initDate);
			return false;
		}
		
		calendar.setServerState(serverState);
		db.update(calendar);

//		Log.d("calendar", "Database::modifyCalendarServerState() success, initDate=" + initDate);
		return true;
	}
	
	//获取待handle的所有日历;
	public List<CalendarTable> getWaitForHandleCalendar() {
		if(!db.getDatebase().isOpen()) {
//			System.out.println("DataBase is not open");
			return null;
		}
		
		List<CalendarTable> calendarList = db.findAll(CalendarTable.class, "initDate ASC");
		List<CalendarTable> data = new ArrayList<CalendarTable>() ;
		for(CalendarTable mDT: calendarList) {
			 if (mDT.getServerState().equals("" + Protocol.SuccessFromServer))
				 data.add(mDT);
		}
		
	    return data;
	}
	
	public CalendarTable getCalendarTable(String initDate){
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return null;
		}
		
		CalendarTable calendar = db.findById(initDate, CalendarTable.class);
		
//		if (calendar != null)
////			Log.d("calendar", "Database::getCalendarTable() success, initDate=" + initDate);
//		else
//			Log.d("calendar", "Database::getCalendarTable() fail, initDate=" + initDate);
		return calendar;
	}
	
	public List<CalendarTable> getCalendarTables(){
		if(db == null) return null;
		if(!db.getDatebase().isOpen()){
//			System.out.println("DataBase is not open");
			return null;
		}

		List<CalendarTable> calendarList = db.findAll(CalendarTable.class, "initDate ASC");
		List<CalendarTable> data = new ArrayList<CalendarTable>();
		for(CalendarTable mDT: calendarList) {
			String stateFromServer = mDT.getServerState();
//			 Log.v("result", "serverstate" + stateFromServer);
			 if (!stateFromServer.equals("" + Protocol.WaitForServerComfirmRemove))
				 data.add(mDT);
		}
		
		return data;
		//return db.findAllByWhere(CalendarTable.class, 
			//	"author='" + SelfInfo.getInstance().getAccount() + "'", "initDate ASC");
	}
	
	public void clearAllCalendar() {
		if(db == null) return ;
		db.deleteAll(CalendarTable.class);
		//db.deleteByWhere(CalendarTable.class, "author='" + SelfInfo.getInstance().getAccount() + "'");
	}

	//Album=====================================================
	public boolean saveAlbumPicture(String lastModefyTime,String path,String oriPath){
		if(db == null) return false;
		if(db.findById(lastModefyTime, GalleryTable.class) != null){

			db.deleteById(GalleryTable.class, lastModefyTime);
		}
		GalleryTable galleryTable = new GalleryTable();
		galleryTable.setLastModefyTime(lastModefyTime);
		galleryTable.setPath(path);
		galleryTable.setOriPath(oriPath);
		galleryTable.setDeleteStatus(0);
		return db.saveBindId(galleryTable);
	}
	
	public boolean deleteAlbumPicture(String lastModefyTime){
		if(db == null) return false;
		if(db.findById(lastModefyTime, GalleryTable.class) != null){

			db.deleteById(GalleryTable.class, lastModefyTime);
		}
		else{
			return true;
		}
		return true;
	}
	//通过创建日期获取图片路径,主要是相册首页显示用;
	public String getImageFilePathWithInitDate(String initDate)
	{
		if(db == null) return "";
		if(!db.getDatebase().isOpen()) {
			return "";
		}
		GalleryTable gT = db.findById(initDate, GalleryTable.class);
	   if(gT != null) {
		   return gT.getPath();
	   }
		
	   return "";
	}
	//获取所有删除状态的图片;
	public List<GalleryTable> FinePicForDelete() {
		if(db == null) return null;
		List<GalleryTable> list = db.findAllByWhere(GalleryTable.class, "deleteStatus=1", "lastModefyTime DESC");

		return list;
	}
	
	public void deletePicAccordToKey(String lastModefyTime) {
		if(db == null) return ;
		db.deleteByWhere(GalleryTable.class, "lastModefyTime='"+lastModefyTime+"'");
	}
	
	public void deleteAllPicture() {
		if(db == null) return;
		db.deleteAll(GalleryTable.class);
	}
	
	public void SetStatusForPic(String lastModefyTime,int status){
		if(db == null) return ;
		if(db.findById(lastModefyTime, GalleryTable.class) == null){

//			System.out.println("No found Pic in Database");
			return ;
		}

		GalleryTable gT = db.findById(lastModefyTime, GalleryTable.class);
		gT.setDeleteStatus(1);
		db.update(gT);
	}
	//非待删除
	public List<GalleryTable> getAllPicture() {
		if(db == null) return null;
		List<GalleryTable> gallerylist =db.findAllByWhere(GalleryTable.class, "deleteStatus=0", "lastModefyTime DESC");
		return gallerylist;
	}
	
	public boolean isThePicInDatabase(String lastModefyTime) {
		if(db == null) return false;
		if(db.findById(lastModefyTime, GalleryTable.class) == null)
			return false;
		else return true;
	}
	
	//===================common=======================
	
	public String getMotion(String acc) {
		if(db == null) return "";
	    String motion = null;
	
	    if(!db.getDatebase().isOpen()) {
	    	   return motion;
		}
	  CommonTable  mcT =db.findById(acc, CommonTable.class);
	  motion= mcT.getMotion();

	    return motion;

	}
	public String getFriends(String acc) {
		if(db == null) return "";
	    String friends = null;
	    if(!db.getDatebase().isOpen()) {
	    	   return friends;
		}
	  CommonTable  mcT =db.findById(acc, CommonTable.class);
	  friends = mcT.getFriends();

	    return friends;
	}
	public String getWitness(String acc) {
		if(db == null) return "";
	    String witness = null;
	    if(!db.getDatebase().isOpen()) {
	    	   return witness;
		}
	  CommonTable  mcT =db.findById(acc, CommonTable.class);
	  witness = mcT.getWitness();

	    return witness;

	}
	public String getHouseStyle(String acc) {
		if(db == null) return "";
	    String houseStyle = null;
	    if(!db.getDatebase().isOpen()) {
	    	   return houseStyle;
		}
	  CommonTable  mcT =db.findById(acc, CommonTable.class);
	  houseStyle = mcT.getHouseStyle();

	    return houseStyle;

	}
	public String  getLightState(String acc) {
		if(db == null) return "";
	     String  lightState = null;
	     if(!db.getDatebase().isOpen()) {
	    	   return lightState;
		}
	  CommonTable  mcT =db.findById(acc, CommonTable.class);
	  lightState = mcT.getLightState();
		
	  
	    return lightState;
	  
	    
	}

	public String getFirstPicture(String acc)
	{
		if(db == null) return "";
	    String  firstPicture = null;
	    if(!db.getDatebase().isOpen()) {
	    	   return firstPicture;
		}
	  CommonTable  mcT =db.findById(acc, CommonTable.class);
	  firstPicture = mcT.getFirstPicture();
		
	  
	    return firstPicture;
	}
	public boolean addCommonInfo(String account,String motion,String friends ,
			String witness ,String houseStyle
			,String lightState,String firstPicture){
		if(db == null) return false;
		if(!db.getDatebase().isOpen()){

			return false;
		}
		if(db.findById(account, CommonTable.class) != null) {
			db.deleteById(CommonTable.class, account);
		}
		CommonTable mCT = new CommonTable();
		mCT.setAccount(account);
		mCT.setMotion(motion);
		mCT.setFriends(friends);
		mCT.setWitness(witness);
		mCT.setHouseStyle(houseStyle);
		mCT.setLightState(lightState);
		mCT.setFirstPicture(firstPicture);
		
		return db.saveBindId(mCT);
	
	}
	public boolean updateMotion(String account ,String motion)
	{
	    return true;
	}
	public boolean updateFriends(String account,String friends)
	{
	    return true;
	}
	public boolean updateWitness(String account ,String  witness)
	{
	    return true;
	}

	public boolean updateHouseStyle(String account,String houseStyle) {
		if(!db.getDatebase().isOpen()) {
			return false;
		}
		CommonTable  mCT= null;
		mCT = db.findById(account, CommonTable.class);
		if(mCT != null) {
		
		    mCT.setHouseStyle(houseStyle);
		}
		else {
			return false;
		}
		db.update(mCT, "account='"+account+"'");
		return true;
	}
	public boolean updateLightState(String account ,String lightState) {
		if(!db.getDatebase().isOpen()) {
			return false;
		}
		CommonTable  mCT= null;
		mCT = db.findById(account, CommonTable.class);
		if(mCT != null) {
		
		    mCT.setLightState(lightState);
		}
		else {
			return false;
		}
		db.update(mCT, "account='"+account+"'");
		return true;
	}
	public boolean updateFirstPicture(String account,String firstPicture)
	{
		if(!db.getDatebase().isOpen()) {
			return false;
		}
		CommonTable  mCT= null;
		mCT = db.findById(account, CommonTable.class);
		if(mCT != null) {
		
		    mCT.setFirstPicture(firstPicture);
		}
		else {
			return false;
		}
		db.update(mCT, "account='"+account+"'");
		return true;
	 
	}

	
	//==========================================
	public void clearTemp() {
		if(!db.getDatebase().isOpen()) {
//			System.out.println("DataBase is not open");
			return ;
		}
		db.deleteAll(Temp.class);
	}
	
	//
	public void saveHeadPhotoToTemp(String account,String headphotopath) {
		if(!db.getDatebase().isOpen()) {
//			System.out.println("DataBase is not open");
			return ;
		}
		Temp temp = new Temp();
		if(db.findById(account, Temp.class) != null) {
			db.deleteById(Temp.class, account);
		}
		temp.setAccount(account);
		temp.setHeadphoto(headphotopath);
		db.save(temp);
	}
	
	//
	public String readHeadPhotoFromTemp(String account) {
		if(!db.getDatebase().isOpen()) {
//			System.out.println("DataBase is not open");
			return "-1";
		}
		if(db.findById(account, Temp.class) == null) {
//			Log.d("temp_error", "database:get temp headphoto error");
//			System.out.println("database:get temp headphoto error");
			return "-2";
		}
		Temp temp = new Temp();
		temp.same(db.findById(account, Temp.class));
		return temp.getHeadphoto();
	}
/**
 * 单例模式,线程
 * @param ctx
 * @return
 */
	
	private static class DatabaseContainer{
		private static Database instance = new Database(context);
	}
	
	public static Database getInstance(Context ctx) {
		  context = ctx;	
		  Database mInstance = DatabaseContainer.instance;
//		  if(needInit){
//			  needInit = !(mInstance.initDatabase());
//			 
//		  }
		  return mInstance;
	}

	
	//read only
	public FinalDb getFinalDb() {
		return this.db;
	}
	
	/**
	 * 将图片文件保存到SD卡下
	 * 
	 *
	 */
	public void writeToSD(String path,Bitmap m,String bitName) {
		try {
		
			//创建文件对象，用来存储新的图像文件  
	        File f1 = new File(Environment.getExternalStorageDirectory()  
	                + "/LoverHouse" + path );  
	        if(!f1.exists() ) {
	              f1.mkdirs();     //如果文件夹不存在 则建立新文件夹

	        
        }
	        File f = new File(Environment.getExternalStorageDirectory()  
	                + "/LoverHouse" + path+"/"+bitName + ".png");  
	        try   
	        {  
	            //创建文件  
	            f.createNewFile();  
	        } catch (IOException e)   
	        {  
	            // TODO Auto-generated catch block  
//	            System.out.println("在保存图片时出错：" + e.toString());  
	        }  
	      //定义文件输出流  
	        FileOutputStream fOut = null;  
	        try {  
	            fOut = new FileOutputStream(f);  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        }  
	        //将bitmap存储为png格式的图片  
	        m.compress(Bitmap.CompressFormat.PNG, 100, fOut);  
	        try   
	        {  
	            //刷新文件流  
	            fOut.flush();  
	        } catch (IOException e)   
	        {  
	            e.printStackTrace();  
	        }  
	        try   
	        {  
	            //关闭文件流  
	            fOut.close();  
	        } catch (IOException e)   
	        {  
	            e.printStackTrace();  
	        }  
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 从SD卡中读取图片文件 这里的完整的路径base+ path + "/"+xx.png
	 * 
	 */
	
	public Bitmap getDiskBitmap(String pathString)
	{
		Bitmap bitmap = null;
		try
		{
			File file = new File(pathString);
			if(file.exists())
			{
				bitmap = BitmapFactory.decodeFile(pathString);
			}
		} catch (Exception e)
		{// TODO: handle exception
//			Log.v("result", "exception in get bitmap");
		}
          return bitmap;
	}

	@Override
	public boolean updateStateFromServer(String selfAcc, String inidate,
			String stateFromServer) {
		// TODO Auto-generated method stub
		return false;
	}

}
