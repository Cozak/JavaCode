package com.minus.diary;

import com.minius.common.CommonBitmap;
import com.minius.ui.CustomDialog.Builder;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.util.ExitPopup;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.sql_interface.Database;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.minus.xsocket.handler.ActionPacketHandler;
import com.minus.xsocket.handler.DiaryPacketHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditDiaryActivity extends BroadCast implements View.OnClickListener{

	private ImageView Back_Button = null;
	private ImageView Commit_Button = null;
	private ImageView headphoto_View = null;
//	private TextView Author_View = null;
	
	private EditText Title_View = null;
	private EditText Content_View = null;
	
	private Builder mBuilder = null;
  
	
	public Database db = null;
	private GlobalApplication mIns = null;
	private boolean IsModifer = false;

	
	private SharedPreferences preferences = null;
	private SharedPreferences.Editor editor = null;
	
	   //edit  diary 
    private String diaryAcc = "";
    private String diaryAuthor ="";
    private String diaryIniDate = "";
    private String diaryTitle ="";
    private String diaryArticle = "";
    private Bitmap diaryHeadLayerDrawable;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_diary);
		mIns = GlobalApplication.getInstance();
		db = Database.getInstance(this);
		
//		Author_View = (TextView)findViewById(R.id.newdiary_namelable);
		Title_View = (EditText)findViewById(R.id.newdiary_title);
		headphoto_View = (ImageView)findViewById(R.id.newdiary_headphoto);
		Back_Button = (ImageView)findViewById(R.id.newdiary_back);
		Commit_Button = (ImageView)findViewById(R.id.newdiary_write);
		Content_View = (EditText)findViewById(R.id.newdiary_content);
		
		
		initSefInfo();
		initData();
	
		Back_Button.setOnClickListener(this);
		Commit_Button.setOnClickListener(this);
		PushAgent.getInstance(this).onAppStart();
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		GlobalApplication.getInstance().setDiaryVisible(false);
	}
	
	public void initSefInfo(){
		 diaryAcc = SelfInfo.getInstance().getAccount();
	     diaryAuthor = SelfInfo.getInstance().getNickName();
	     preferences =getSharedPreferences(diaryAcc, Activity.MODE_PRIVATE);
		 editor = preferences.edit();
//	     diaryHeadLayerDrawable = mIns.getHeadPicBm();
	     diaryHeadLayerDrawable = CommonBitmap.getInstance().getMyHeadBm();
//	 	 Author_View.setText(diaryAuthor);
		 headphoto_View.setImageBitmap(diaryHeadLayerDrawable);	
//			headphoto_View.setImageBitmap(GlobalApplication.getInstance().getTarHeadPicBm());	
	}
	
	public void  initData(){
		 int who = EditDiaryActivity.this.getIntent().getIntExtra("who", -1);
		 ImageView titleImageView = (ImageView)findViewById(R.id.iv_diary_title);

		//0 new ; 1 edit
		if(who == 0){
			//new 
			IsModifer = false;
			   diaryTitle =preferences.getString(
						"DiaryDraftTitle", "");
			   diaryArticle = preferences.getString(
						"DiaryDraftContent", "");
				Title_View.setText(diaryTitle);
				Content_View.setText(diaryArticle);
				//将标题栏设为“新建日志”
				titleImageView.setImageResource(R.drawable.diary_new_navigation_bar);
		}else if(who == 1){
			//modify
			IsModifer = true;
		    diaryIniDate =getIntent().getStringExtra("DiaryIniDate");
			diaryTitle  =getIntent().getStringExtra("DiaryTitle");
		    diaryArticle =getIntent().getStringExtra("DiaryArticle");
			Title_View.setText(diaryTitle);
			Content_View.setText(diaryArticle);
			//将标题栏设为“修改日志”
			titleImageView.setImageResource(R.drawable.diary_modify_navigation_bar);
		}
	}
	

	
	@SuppressLint("SimpleDateFormat")
	private class MyOnClickListener implements View.OnClickListener{

		int index = 0;
		
		public MyOnClickListener(int i) {
			// TODO Auto-generated constructor stub
			this.index = i;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		GlobalApplication.getInstance().setDiaryVisible(true);
		super.onResume();
		MobclickAgent.onResume(this);
		
	}
	//当日志标题或者内容为空的时候提醒用户输入;
	public void warnNull(String str)
	{
		Toast.makeText(EditDiaryActivity.this, str, Toast.LENGTH_LONG).show();

	}

	
	public void onClickPublishButton()
	{
		DiaryPacketHandler req = new DiaryPacketHandler();
		String title = Title_View.getText().toString().trim();
		String content = Content_View.getText().toString().trim();
		
		 if (title.equals(""))
		    {
		    this.warnNull("请输入标题");
		        return ;
		    }
		    if (content.equals(""))
		    {
			    this.warnNull("请输入内容");
		        return ;
		    }
		    //处理尾部换行符的情况;
		    int lastIndex =content.length()-1;
		    while (content.charAt(lastIndex) == '\n'){
		        lastIndex--;
		    }
		    if (lastIndex != content.length() - 1) {
		    	
		    	content = content.substring(0,lastIndex);
		      
		    }
	//获得当前时间的东八区形式
		String editdate = AppManagerUtil.getCurDateInServer();

		if(!IsModifer){
			//清空草稿.
			editor.putString("DiaryDraftTitle", "");
			editor.putString("DiaryDraftContent", "");
			editor.commit();
			String state =String.format("%c",Protocol.WaitForServerComfirmAdd);
			req.AddDiary(editdate, title, content, editdate);
			  
			db.addDiary(diaryAcc, editdate, title, content, editdate,state,0);
	
		}
		else{
			   String state =String.format("%c",Protocol.WaitForServerComfirmModify);
			   req.ModifyDiary(diaryIniDate, title, content, editdate);
         	   db.modifyDiary(diaryAcc, diaryIniDate, title, content, editdate,state,0);
         }

		
		Intent intent = new Intent();
		intent.setClass(EditDiaryActivity.this, DiaryActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	    EditDiaryActivity.this.finish();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.newdiary_back:
			if (IsModifer) {
				showCancelModifyAlert();
			
		    }
		    else{
		    
		        diaryTitle = Title_View.getText().toString().trim();
		        diaryArticle =  Content_View.getText().toString().trim();
		       if(!(diaryTitle.length() <= 0 && diaryArticle.length() <= 0)){ //如果写有东西就存入草稿箱;
		    	    editor.putString("DiaryDraftTitle",diaryTitle);
					editor.putString("DiaryDraftContent",diaryArticle);
					editor.commit();
		       }
		       Intent intent = new Intent();
				intent.setClass(EditDiaryActivity.this, DiaryActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				  EditDiaryActivity.this.finish();
		    }
			break;
		case R.id.newdiary_write:
			onClickPublishButton();
			break;
		default:
			break;
		}

		
		
	}
	public void showCancelModifyAlert() {
		String title = "";
		String content = "是否放弃本次编辑?";
		
		mBuilder = AppManagerUtil.openAlertDialog(EditDiaryActivity.this,
				title, content, "放弃", "取消",
				new View.OnClickListener() {

			@Override
			public void onClick(View v){
						//qiut
						Intent intent = new Intent();
						intent.setClass(EditDiaryActivity.this, DiaryActivity.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
						EditDiaryActivity.this.finish();
						
						if(mBuilder != null && mBuilder.getDialog()!= null)
							mBuilder.getDialog().dismiss();

					}
				}, new View.OnClickListener() {
					@Override
					public void onClick(View v){
					//cancel
//						dialog.dismiss();
						if(mBuilder != null && mBuilder.getDialog()!= null)
							mBuilder.getDialog().dismiss();

					}
				}, true);
		
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		if (IsModifer) {
    			showCancelModifyAlert();
			
		    }
		    else{
		    
		        diaryTitle = Title_View.getText().toString().trim();
		        diaryArticle =  Content_View.getText().toString().trim();
		       if(!(diaryTitle.length() <= 0 && diaryArticle.length() <= 0)){ //如果写有东西就存入草稿箱;
		    	    editor.putString("DiaryDraftTitle",diaryTitle);
					editor.putString("DiaryDraftContent",diaryArticle);
					editor.commit();
		       }
		       Intent intent = new Intent();
				intent.setClass(EditDiaryActivity.this, DiaryActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				EditDiaryActivity.this.finish();
		    }
    	
    	}
    	return true;
	}
	
}
