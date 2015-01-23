package com.minus.calendar;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import com.minus.lovershouse.R;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.sql_interface.Database;
import com.minus.table.CalendarTable;
import com.minus.xsocket.handler.CalendarHandler;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarItem {
	protected Activity mActivity;
	protected CalendarTable mCalendarTable;

	//如果不使用R.layout.calendar_unit来初始化mPreview，则必须重写void initPreview()
	protected View mPreview;

	//如果不使用R.layout.calendar_detail来初始化mDetailView，则必须重写void initDetailView()
	protected View mDetailView;
	
	public CalendarItem(Activity activity, CalendarTable mCalendarTable) {
		this.mActivity = activity;
		this.mCalendarTable = mCalendarTable;

		LayoutInflater inflater = LayoutInflater.from(mActivity);
		mPreview = (RelativeLayout) inflater.inflate(R.layout.calendar_unit, null); 

		mDetailView = (RelativeLayout) inflater.inflate(R.layout.calendar_detail, null);
		initPreview();
		//initDetailView();
	}

	public View getPreview() {
		return mPreview;
	}
	
	public CalendarTable getCalendarTable() {
		return this.mCalendarTable;
	}
	
	//该函数要求mPreview的布局为R.layout.calendar_unit
	protected void initPreview() {
		final RelativeLayout delete = (RelativeLayout) mPreview.findViewById(R.id.calendar_unit_delete);
		
		mPreview.setOnLongClickListener(new RelativeLayout.OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				delete.setVisibility(View.VISIBLE);
				return true;
			}
		});
		mPreview.setOnClickListener(new RelativeLayout.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addDetailViewToFather();
			}
		});
		
		int rand = CommonFunction.calculateDay(this.mCalendarTable.getMemoDate()) % 3;
		if (rand == 0)
			mPreview.setBackgroundResource(R.drawable.memoday_preview_background1);
		else if (rand == 1)
			mPreview.setBackgroundResource(R.drawable.memoday_preview_background2);
		else if (rand == 2)
			mPreview.setBackgroundResource(R.drawable.memoday_preview_background3);
		
		delete.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Database.getInstance(mActivity).removeCalendar(mCalendarTable.getInitDate());
				CalendarHandler.getInstance().removeCalendar(mCalendarTable.getInitDate());
				((CalendarMainActivity) mActivity).refreshBodyView();
			}
		});
		
		refreshPreview();
	}
	
	private static final int gPreviewTextByteLength_normal = 16;
	private static final int gPreviewTextByteLength_prompt = 8;
	protected void refreshPreview() {
		TextView title = (TextView) mPreview.findViewById(R.id.calendar_unit_title);
		TextView date = (TextView) mPreview.findViewById(R.id.calendar_unit_date);
		ImageButton star = (ImageButton) mPreview.findViewById(R.id.calendar_unit_star);
		
		String title_normal = mCalendarTable.getTitle();
		if (CommonFunction.calculateLengthWithByte(title_normal) > gPreviewTextByteLength_normal)
			title_normal = CommonFunction.subStringWithByte(title_normal, gPreviewTextByteLength_normal) + "...";
		title.setText(title_normal);
		date.setText(CommonFunction.standardizeDate(mCalendarTable.getMemoDate()));
		star.setVisibility(View.INVISIBLE);

		String memodate = mCalendarTable.getMemoDate();
		String[] str = memodate.split("-");
		//int year = Integer.valueOf(str[0]);
		int month = Integer.valueOf(str[1]);
		int day = Integer.valueOf(str[2]);
		
		String[] str2 = AppManagerUtil.getCurDate().split("-");
		int curYear = Integer.valueOf(str2[0]);
		int curMonth = Integer.valueOf(str2[1]);
		int curDay = Integer.valueOf(str2[2]);
		
		String title_prompt = mCalendarTable.getTitle();
		if (CommonFunction.calculateLengthWithByte(title_prompt) > gPreviewTextByteLength_prompt)
			title_prompt = CommonFunction.subStringWithByte(
					title_prompt, gPreviewTextByteLength_prompt) + "...";
		int promptPolicy = mCalendarTable.getPromptPolicy().charAt(0) - 1;
		if (promptPolicy == 0) { //no prompt
			//
		}
		else if (promptPolicy == 1) { //every month
			int countdown = day - curDay;
			if (countdown < 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, curYear);
				calendar.set(Calendar.MONTH, curMonth - 1);
				countdown += calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			}
			if (countdown == 0) {
				star.setVisibility(View.VISIBLE);
				title.setText("今天是纪念" + title_prompt + "的日子");
				date.setText("");
			} else if (countdown <= 5) {
				star.setVisibility(View.VISIBLE);
				title.setText("距离纪念" + title_prompt + "的日子还有" + countdown + "天");
				date.setText("");
			}
		}
		else if (promptPolicy == 2) { //every 100 days
			int dayPassed = CommonFunction.calculateDay(date.getText().toString());
			int countdown = 100 - (dayPassed % 100);
			if (countdown == 0) {
				star.setVisibility(View.VISIBLE);
				title.setText("今天是纪念" + title_prompt + "的日子");
				date.setText("");
			} else if (countdown <= 20) {
				star.setVisibility(View.VISIBLE);
				title.setText("距离纪念" + title_prompt + "的日子还有" + countdown + "天");
				date.setText("");
			}
		}
		else { //every year
			String curDate = AppManagerUtil.getSimpleCurDate();
			String nextMemoday = curYear + "-" + month + "-" + day;
			int countdown = CommonFunction.calculateDay(curDate, nextMemoday);
			if (countdown < 0) {
				nextMemoday = (curYear + 1) + "-" + month + "-" + day;
				countdown = CommonFunction.calculateDay(curDate, nextMemoday);
			}
			if (countdown == 0) {
				star.setVisibility(View.VISIBLE);
				title.setText("今天是纪念" + title_prompt + "的日子");
				date.setText("");
			} else if (countdown <= 30) {
				star.setVisibility(View.VISIBLE);
				title.setText("距离纪念" + title_prompt + "的日子还有" + countdown + "天");
				date.setText("");
			}
		}
	}
	
	//该函数要求mDetailView的布局为R.layout.calendar_detail
	protected void initDetailView() {
		initDetailViewTop();
		initDetailViewBody();
	}

	protected void initDetailViewTop() {
		ImageView back = (ImageView) mDetailView.findViewById(R.id.calendar_detail_backbutton);
		ImageView confirm = (ImageView) mDetailView.findViewById(R.id.calendar_detail_confirmbutton);

		final EditText title = (EditText) mDetailView.findViewById(R.id.calendar_detail_title);
		final TextView date = (TextView) mDetailView.findViewById(R.id.calendar_detail_date);
		final Spinner prompt = (Spinner) mDetailView.findViewById(R.id.calendar_detail_prompt_spinner);

		title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
		
		back.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				removeDetailViewFromFather();
			}
		});
		
		confirm.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View view) {
				String titleStr = title.getText().toString();
				if (titleStr.length() == 0) {
					title.setHint("请输入标题");
					return;
				}
				
				String editDate = AppManagerUtil.getCurDate();
				//TODO String memoDate = date.getText().toString();
				String memoDate = date.getText().toString() + "-00:01:00";
				String promptPolicy = "" + (char) (prompt.getSelectedItemPosition() + 1);
				
				mCalendarTable.setEditDate(editDate);
				mCalendarTable.setMemoDate(memoDate);
				mCalendarTable.setPromptPolicy(promptPolicy);
				mCalendarTable.setTitle(titleStr);
				
				if (Database.getInstance(mActivity).getCalendarTable(mCalendarTable.getInitDate()) != null) {
					Database.getInstance(mActivity).modifyCalendar(
							mCalendarTable.getInitDate(), editDate, memoDate, promptPolicy, titleStr);
					CalendarHandler.getInstance().modifyCalendar(
							mCalendarTable.getInitDate(), editDate, memoDate, promptPolicy, titleStr);
				}
				else {
					Database.getInstance(mActivity).addCalendar(
							mCalendarTable.getInitDate(), editDate, memoDate, promptPolicy, titleStr);
					CalendarHandler.getInstance().addCalendar(
							mCalendarTable.getInitDate(), editDate, memoDate, promptPolicy, titleStr);	
				}
				removeDetailViewFromFather();
			}
		});
	}
	
	protected void addDetailViewToFather() {
		FrameLayout container = (FrameLayout) mActivity.findViewById(R.id.calendar_main_frameLayout);
		mDetailView.setClickable(true);
		initDetailView();
		container.addView(mDetailView);
	}
	
	protected void removeDetailViewFromFather() {
		FrameLayout container = (FrameLayout) mActivity.findViewById(R.id.calendar_main_frameLayout);
		container.removeView(mDetailView);
		((CalendarMainActivity) mActivity).refreshBodyView();

		EditText title = (EditText) mDetailView.findViewById(R.id.calendar_detail_title);
		InputMethodManager imm = (InputMethodManager) 
				mActivity.getSystemService(mActivity.INPUT_METHOD_SERVICE);    
        imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
	}
	
	protected void initDetailViewBody() {
		EditText title = (EditText) mDetailView.findViewById(R.id.calendar_detail_title);
		final TextView date = (TextView) mDetailView.findViewById(R.id.calendar_detail_date);
		final TextView dateCount = (TextView) mDetailView.findViewById(R.id.calendar_detail_date_count);
		TextView promptTitle = (TextView) mDetailView.findViewById(R.id.calendar_detail_prompt_title);
		final Spinner prompt = (Spinner) mDetailView.findViewById(R.id.calendar_detail_prompt_spinner);
		
		title.setText(mCalendarTable.getTitle());
		title.addTextChangedListener(new MaxLengthWatcher(
				MaxLengthWatcher.MAX_CHINESE_CHARACTER_LENGTH, title));
		
		date.setText(CommonFunction.standardizeDate(mCalendarTable.getMemoDate()));
		int dayPassed = CommonFunction.calculateDay(mCalendarTable.getMemoDate());
		dateCount.setText("已经过去" + dayPassed + "天");
		
		date.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] str = date.getText().toString().split("-");
				if (CalendarMainActivity.isValidMemoDate(date.getText().toString()) == false)
					str = AppManagerUtil.getSimpleCurDate().split("-");
				int year = Integer.valueOf(str[0]);
				int month = Integer.valueOf(str[1]);
				int day = Integer.valueOf(str[2]);
				OnDateSetListener onDateSetListener = new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						String monthStr = String.valueOf(++month);
						if (month < 10)
							monthStr = "0" + monthStr;
						String dayStr = String.valueOf(day);
						if (day < 10)
							dayStr = "0" + dayStr;
						String newDate = year + "-" + monthStr + "-" + dayStr;
						int dayPassed = CommonFunction.calculateDay(newDate);
						if (dayPassed < 0)
							return;
						date.setText(newDate);
						dateCount.setText("已经过去 " + dayPassed + "天");
						
						setPrompt(prompt.getSelectedItemPosition());
					}
				};
				DatePickerDialog datePickerDialog = new DatePickerDialog(
						mActivity, onDateSetListener, year, month-1, day);
				datePickerDialog.show();
			}
		});

		promptTitle.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				prompt.requestFocus();
				prompt.performClick();
			}
		});
		
		prompt.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				setPrompt(arg2);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		prompt.setSelection(mCalendarTable.getPromptPolicy().charAt(0) - 1);
	}
	
	protected void setPrompt(int promptPolicy) {
		TextView date = (TextView) mDetailView.findViewById(R.id.calendar_detail_date);
		TextView promptCount = (TextView) mDetailView.findViewById(R.id.calendar_detail_prompt_count);
		TextView promptTitle = (TextView) mDetailView.findViewById(R.id.calendar_detail_prompt_title);
		
		promptTitle.setText("提醒方式:\t" + 
		mActivity.getResources().getStringArray(R.array.calendar_prompt_policy)[promptPolicy]);
		
		String[] str = date.getText().toString().split("-");
		//int year = Integer.valueOf(str[0]);
		int month = Integer.valueOf(str[1]);
		int day = Integer.valueOf(str[2]);
		
		String[] str2 = AppManagerUtil.getCurDate().split("-");
		int curYear = Integer.valueOf(str2[0]);
		int curMonth = Integer.valueOf(str2[1]);
		int curDay = Integer.valueOf(str2[2]);
		
		if (promptPolicy == 0) {
			promptCount.setText("");
		} else if (promptPolicy == 1) { //every month
			int countdown = day - curDay;
			if (countdown < 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, curYear);
				calendar.set(Calendar.MONTH, curMonth - 1);
				countdown += calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			}
			promptCount.setText("距离下次过纪念日还有" + countdown + "天");
		} else if (promptPolicy == 2) { //every 100 days
			int dayPassed = CommonFunction.calculateDay(date.getText().toString());
			int countdown = 100 - (dayPassed % 100);
			promptCount.setText("距离下次过纪念日还有" + countdown + "天");
		} else if (promptPolicy == 3) { //every year
			String curDate = AppManagerUtil.getSimpleCurDate();
			String nextMemoday = curYear + "-" + month + "-" + day;
			int countdown = CommonFunction.calculateDay(curDate, nextMemoday);
			if (countdown < 0) {
				nextMemoday = (curYear + 1) + "-" + month + "-" + day;
				countdown = CommonFunction.calculateDay(curDate, nextMemoday);
			}
			promptCount.setText("距离下次过纪念日还有" + countdown + "天");
		} else {
			Toast.makeText(mActivity.getApplicationContext(), "Error in CalendarDetail.setPrompt()",
				     Toast.LENGTH_SHORT).show();
		}
	}
	
}
