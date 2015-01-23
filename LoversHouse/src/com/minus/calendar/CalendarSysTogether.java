package com.minus.calendar;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minus.lovershouse.R;
import com.minus.table.CalendarTable;

public class CalendarSysTogether extends CalendarItem {
	
	public CalendarSysTogether(Activity activity, CalendarTable mCalendarTable) {
		super(activity, mCalendarTable);
	}
	
	@Override
	protected void initPreview() {
		LayoutInflater inflater = LayoutInflater.from(super.mActivity);
		super.mPreview = (RelativeLayout) inflater.inflate(R.layout.calendar_first_item, null);

		super.mPreview.setOnLongClickListener(new RelativeLayout.OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				//清除本地数据库和服务器的所有纪念日数据，仅用于测试
				Toast.makeText(mActivity, "removeAllCalendar", Toast.LENGTH_SHORT).show();
				((CalendarMainActivity) mActivity).removeAllCalendar();
				return true;
			}
		});
		super.mPreview.setOnClickListener(new RelativeLayout.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addDetailViewToFather();
			}
		});
		
		refreshPreview();
	}

	@Override
	protected void refreshPreview() {
		TextView dayPassed = (TextView) super.mPreview.findViewById(R.id.calendar_first_item_dayPassed);
		TextView memoDate = (TextView) super.mPreview.findViewById(R.id.calendar_first_item_memodate);
		
		if (CalendarMainActivity.isValidMemoDate(super.mCalendarTable.getMemoDate())) {
			dayPassed.setText("" + CommonFunction.calculateDay(super.mCalendarTable.getMemoDate()));
			memoDate.setText(CommonFunction.standardizeDate(super.mCalendarTable.getMemoDate()));
		}
		else {
			dayPassed.setText("");
			memoDate.setText("");
		}
	}
	
	@Override
	protected void setPrompt(int promptPolicy) {
		TextView date = (TextView) mDetailView.findViewById(R.id.calendar_detail_date);
		//TextView dateCount = (TextView) mDetailView.findViewById(R.id.calendar_detail_date_count);
		TextView prompt_count = (TextView) mDetailView.findViewById(R.id.calendar_detail_prompt_count);

		String memoDate = date.getText().toString();
		if (CalendarMainActivity.isValidMemoDate(memoDate) == false) {
			prompt_count.setText("");
			return;
		}
		
		int dayPassed = CommonFunction.calculateDay(memoDate);
		prompt_count.setText("已经在一起" + dayPassed + "天");
		/*
		String[] str = super.mCalendarTable.getMemoDate().split("-");
		//int year = Integer.valueOf(str[0]);
		int month = Integer.valueOf(str[1]);
		int day = Integer.valueOf(str[2]);

		String[] str2 = AppManagerUtil.getCurDate().split("-");
		//int curYear = Integer.valueOf(str2[0]);
		int curMonth = Integer.valueOf(str2[1]);
		int curDay = Integer.valueOf(str2[2]);

		int dayPassed = CommonFunction.calculateDay(date.getText().toString());
		//dateCount.setText("已经过去" + dayPassed + "天");
		if (dayPassed == 100 || dayPassed == 520 || dayPassed == 1314 
				|| (month == curMonth && day == curDay))
			prompt_count.setText("已经在一起" + dayPassed + "天");
		else if (dayPassed < 100)
			prompt_count.setText("距离下次纪念日还有 " + (100 - dayPassed) + " 天");
		else if (dayPassed < 520)
			prompt_count.setText("距离下次纪念日还有 " + (520 - 100 - dayPassed) + " 天");
		else if (dayPassed < 1314)
			prompt_count.setText("距离下次纪念日还有 " + (1314 - 520 - dayPassed) + " 天");
		*/
	}

	@Override
	protected void initDetailView() {
		super.initDetailView();

		RelativeLayout body = (RelativeLayout) mDetailView.findViewById(R.id.calendar_detail_body);
		EditText title = (EditText) mDetailView.findViewById(R.id.calendar_detail_title);
		TextView date = (TextView) mDetailView.findViewById(R.id.calendar_detail_date);
		TextView dateCount = (TextView) mDetailView.findViewById(R.id.calendar_detail_date_count);
		TextView prompt_title = (TextView) mDetailView.findViewById(R.id.calendar_detail_prompt_title);
		TextView prompt_count = (TextView) mDetailView.findViewById(R.id.calendar_detail_prompt_count);
		
		body.setBackgroundResource(R.drawable.memoday_detail_background_2line);
		
		title.setFocusable(false); //设置EditText不可编辑
		//title.setKeyListener(null); //设置EditText不可编辑
		
		dateCount.setVisibility(View.INVISIBLE);
		
		prompt_title.setVisibility(View.INVISIBLE);
		
		RelativeLayout.LayoutParams prompt_count_params = (RelativeLayout.LayoutParams) prompt_count.getLayoutParams();
		prompt_count_params.addRule(RelativeLayout.BELOW, date.getId());
		prompt_count.setLayoutParams(prompt_count_params);
		//prompt_count.setVisibility(View.INVISIBLE);
	}
}
