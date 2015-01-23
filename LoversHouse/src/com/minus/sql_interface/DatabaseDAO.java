package com.minus.sql_interface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import net.tsz.afinal.FinalDb;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.minus.table.BasicsettingTable;
import com.minus.table.CalendarTable;
import com.minus.table.ChatTable;
import com.minus.table.CustomActionTable;
import com.minus.table.DiaryTable;
import com.minus.table.GalleryTable;
import com.minus.table.TargetTable;
import com.minus.table.UserTable;

/**
 * ���ݿ���� Ϊ��ͬ�û��������������ݿ�
 * 
 * @author Administrator
 * 
 */
public interface DatabaseDAO {

	/**
	 * TODO
	 */
	public void closeDatabase();

	public boolean deleteUserTable();

	public boolean deleteTargetTable();

	public boolean deleteDiaryTable();

	public boolean deleteAlarmClockTable();

	public boolean deleteChatTable();

	public boolean deleteCalendarTable();

	public boolean deleteAll(String id);

	public Boolean userTableEmpty();

	public Boolean emptyUserTable(String id);

	// ��ȡ�Լ�����Ϣ ��������
	public UserTable getSelfInfo();

	/**
	 * return path where store the img when return -1 means null database ,
	 * while -2 means null table
	 * 
	 * @param account
	 * @return
	 */
	public String getHeadPhoto(String account);

	// insert������Ϣ ���ûͷ����� ��ɾ������� �������䣬���룬 Ĭ��ͷ��0x01
	public boolean addSelfInfo(String account, String password,
			String defaultPath);

	public boolean updateSelfInfo(String account, String sex, String bigname,
			String appearance, String status, String smallname, String target);

	public boolean updateSelfPassword(String account, String password);

	public boolean updateSelfSex(String account, String sex);

	public boolean updateSelfBigname(String account, String bigname);

	public boolean updateSelfAppearance(String account, String appearance);

	public boolean updateSelfStatus(String account, String status);

	public boolean updateSelfSmallname(String account, String smallname);

	/**
	 * 
	 * @param account
	 * @param headphotopath
	 *            ������·����/mnt/sd/loverhouse/myhead/my.png(�����������ݿ��·��)
	 * @return
	 */
	public boolean updateSelfHeadPhoto(String account, String headphotopath);

	/**
	 * 
	 * @param account
	 * @param headphoto
	 * @return
	 */
	public boolean appendSelfHeadPhoto(String account, String headphotopath);

	// target, ------------ accountΪ�Լ���account -----------
	public TargetTable getTargetInfo();

	public String getTargetHeadPhoto(String account);

	public boolean addTargetInfo(String account, String sex, String bigname,
			String appearance, String status, String smallname, String target);

	public boolean updateTargetBigname(String account, String bigname);

	public boolean updateTargetAppearance(String account, String appearance);

	public boolean updateTargetStatus(String account, String status);

	public boolean updateTargetSmallname(String account, String smallname);

	/**
	 * 
	 * @param account
	 * @param headphotopath
	 * @return
	 */
	public boolean updateTargetHeadPhoto(String account, String headphotopath);

	// basicsetting
	// public BasicsettingTable getBasicsettingInfo();
	// public boolean addBasicsettingInfo(String account,Boolean
	// defaultState,String numpass);
	// public boolean updateBasicsettingVoice(String account,Boolean voice);
	// public boolean updateBasicsettingViberate(String account,Boolean
	// viberate);
	// public boolean updateBasicsettingVoiceOrViberate(String account,int
	// voice0rviberate);
	// public boolean updateBasicsettingProtected(String account ,Boolean
	// isprotected);
	// public boolean updateBasicsettingNum(String account ,Boolean num);
	// public boolean updateBasicsettingGraph(String account ,Boolean graph);
	// public boolean updateBasicsettingPass(String account ,String numpass);

	/**
	 * @param account
	 * @param headphoto
	 * @return
	 */
	// public boolean appendTargetHeadPhoto(String account,String
	// headphotopath);

	// diary
	public boolean addDiary(String author, String initdate, String title,
			String content, String editdate, String serverState, int isNew);

	public boolean updateStateFromServer(String selfAcc, String inidate,
			String stateFromServer);

	public boolean modifyDiary(String author, String initdate, String title,
			String content, String editdate, String serverState, int isNew);

	public boolean removeDiary(String author, String initdate);

	public boolean changeNewDiary(String author, String initdate, int isnew);

	public boolean isExistTheDiaryItemWithAccount(String account,
			String initDate);

	public List<DiaryTable> getDiary();

	// ��ȡ��ɾ����������־;
	public List<DiaryTable> getWaitForRemoveDiary();

	// ��ȡ����ӵ�������־;
	public List<DiaryTable> getWaitForAddDiary();

	// ��ȡ���޸ĵ�������־;
	public List<DiaryTable> getWaitForModifyDiary();

	// ��ȡ�����еĵ�һ����¼��������ǲݸ�Ļ�����ô�ʹ���û�вݸ�;
	public List<DiaryTable> getDiaryDraft();

	// action
	public List<CustomActionTable> getAllAction();

	public String getAddActionId();

	public boolean addCustomAction(String typeID, String content,
			String serverState);

	public boolean updateActionStatus(String typeID, String status);

	public boolean updateActionContent(String typeID, String content);

	public boolean updateWholeAction(String typeID, String content,
			String status);

	public boolean initActionTable();

	// chat
	public boolean addChat(String account, String initdate, String status,
			String message, int msgtype, String recordTime);

	public boolean modifyChatStatus(String author, String fromDate,
			String toDate, String status);

	public boolean modifyChatStatus(String author, String initdate,
			String status);

	public boolean removeChat(String account, String initdate);

	public String getLastMsgDate();

	public List<ChatTable> getChat();

	public List<ChatTable> getSendingChat();
	public List<ChatTable> getLastChat(int beginIndex, int endIndex);
	public List<ChatTable> getChatWithAccount(String account, String iniDate);
	
	//calendar
	public boolean addCalendar(String initDate, String editDate, String memoDate, String promptPolicy, String title);
	public boolean modifyCalendar(String initDate, String editDate, String memoDate, String promptPolicy, String title);
	public boolean removeCalendar(String initDate);
	public boolean modifyCalendarServerState(String initDate, String serverState);
	public CalendarTable getCalendarTable(String initDate);
	public List<CalendarTable> getCalendarTables();
	
	// �������
	public boolean saveAlbumPicture(String lastModefyTime, String path,
			String oriPath);

	public boolean deleteAlbumPicture(String lastModefyTime);

	public List<GalleryTable> FinePicForDelete();

	public void deletePicAccordToKey(String lastModefyTime);

	public void deleteAllPicture();

	public void SetStatusForPic(String lastModefyTime, int status);

	public List<GalleryTable> getAllPicture();

	public boolean isThePicInDatabase(String lastModefyTime);

	// ============================= common ============================
	public String getMotion(String acc);

	public String getFriends(String acc);

	public String getWitness(String acc);

	public String getHouseStyle(String acc);

	public String getLightState(String acc);

	public String getFirstPicture(String acc);

	public boolean addCommonInfo(String account, String motion, String friends,
			String witness, String houseStyle, String lightState,
			String firstPicture);

	public boolean updateMotion(String account, String motion);

	public boolean updateFriends(String account, String friends);

	public boolean updateWitness(String account, String witness);

	public boolean updateHouseStyle(String account, String houseStyle);

	public boolean updateLightState(String account, String lightState);

	public boolean updateFirstPicture(String account, String firstPicture);

	// =========================================================

	public void clearTemp();

	//
	public void saveHeadPhotoToTemp(String account, String headphotopath);

	//
	public String readHeadPhotoFromTemp(String account);

	// ===== LASTMODIFYTIME

	public boolean  initDatabase(String dbtitle);

	// read only ����
	public FinalDb getFinalDb();

	/**
	 * ��ͼƬ�ļ����浽SD����
	 * 
	 * 
	 */
	/**
	 * 
	 * @param path
	 *            ���ļ���
	 * @param m
	 *            ͼƬ��bitmap
	 * @param bitName
	 *            ͼƬ������
	 */
	public void writeToSD(String path, Bitmap m, String bitName);

	/**
	 * ��SD���ж�ȡͼƬ�ļ� �����������·��base+ path + "/"+xx.png
	 * 
	 */
	/**
	 * 
	 * @param pathString
	 *            ������·����/mnt/sd/loverhouse/myhead/my.png(�����������ݿ��·��)
	 * @return
	 */

	public Bitmap getDiskBitmap(String pathString);

}
