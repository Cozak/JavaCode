package com.minus.calendar;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.minus.lovershouse.R;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.table.CalendarTable;

public class CalendarSysBirthday extends CalendarItem {

	public CalendarSysBirthday(Activity activity, CalendarTable mCalendarTable) {
		super(activity, mCalendarTable);
	}
	
	@Override
	protected void initPreview() {
		super.initPreview();

		super.mPreview.setOnLongClickListener(new RelativeLayout.OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				return true;
			}
		});
		
		super.mPreview.setBackgroundResource(R.drawable.memoday_preview_background2);
		
		refreshPreview();
	}

	@Override
	protected void refreshPreview() {
		TextView title = (TextView) mPreview.findViewById(R.id.calendar_unit_title);
		TextView date = (TextView) mPreview.findViewById(R.id.calendar_unit_date);
		ImageButton star = (ImageButton) mPreview.findViewById(R.id.calendar_unit_star);
		
		String peopleName;
		if ((super.mCalendarTable.getInitDate().equals(CalendarMainActivity.gWifeBirthdayId) && 
				SelfInfo.getInstance().getSex().equals(SelfInfo.SEX_GIRL)) 
				|| 
				(super.mCalendarTable.getInitDate().equals(CalendarMainActivity.gHusbandBirthdayId) && 
				SelfInfo.getInstance().getSex().equals(SelfInfo.SEX_BOY))) {
			peopleName = SelfInfo.getInstance().getNickName();
		}
		else {
			peopleName = GlobalApplication.getInstance().getTiBigName();
			if (peopleName == null || peopleName.equals(""))
				peopleName = "TA";
		}

		title.setText(peopleName + "的生日是");
		String memoDate = super.mCalendarTable.getMemoDate();
		if (CalendarMainActivity.isValidMemoDate(memoDate) == false) {
			date.setText("");
			return;
		}
		
		String[] str = memoDate.split("-");
		int year = Integer.valueOf(str[0]);
		int month = Integer.valueOf(str[1]);
		int day = Integer.valueOf(str[2]);
		
		String[] str2 = AppManagerUtil.getCurDate().split("-");
		int curYear = Integer.valueOf(str2[0]);
		//int curMonth = Integer.valueOf(str2[1]);
		//int curDay = Integer.valueOf(str2[2]);
		
		String curDate = AppManagerUtil.getSimpleCurDate();
		String nextMemoday = curYear + "-" + month + "-" + day;
		int countdown = CommonFunction.calculateDay(curDate, nextMemoday);
		if (countdown < 0) {
			nextMemoday = (curYear + 1) + "-" + month + "-" + day;
			countdown = CommonFunction.calculateDay(curDate, nextMemoday);
		}

		if (countdown == 0) {
			star.setVisibility(View.VISIBLE);
			title.setText("祝 " + peopleName + " " + (curYear - year) + " 岁生日快乐！");
			date.setText("");
		} else if (countdown <= 30) {
			star.setVisibility(View.VISIBLE);
			title.setText("距离 " + peopleName + " 的 " + (curYear - year) + " 岁生日还有  " + countdown + " 天");
			date.setText("");
		} else {
			date.setText(CommonFunction.standardizeDate(memoDate));
		}
	}

	@Override
	protected void setPrompt(int promptPolicy) {
		TextView date = (TextView) mDetailView.findViewById(R.id.calendar_detail_date);
		TextView prompt_count = (TextView) mDetailView.findViewById(R.id.calendar_detail_prompt_count);

		String peopleName;
		if ((super.mCalendarTable.getInitDate().equals(CalendarMainActivity.gWifeBirthdayId) && 
				SelfInfo.getInstance().getSex().equals(SelfInfo.SEX_GIRL)) 
				|| 
				(super.mCalendarTable.getInitDate().equals(CalendarMainActivity.gHusbandBirthdayId) && 
				SelfInfo.getInstance().getSex().equals(SelfInfo.SEX_BOY))) {
			peopleName = SelfInfo.getInstance().getNickName();
		}
		else {
			peopleName = GlobalApplication.getInstance().getTiBigName();
			if (peopleName == null || peopleName.equals(""))
				peopleName = "TA";
		}
		
		String memoDate = date.getText().toString();
		if (CalendarMainActivity.isValidMemoDate(memoDate) == false) {
			prompt_count.setText("");
			return;
		}
		
		String[] str = memoDate.split("-");
		//int year = Integer.valueOf(str[0]);
		int month = Integer.valueOf(str[1]);
		int day = Integer.valueOf(str[2]);
		
		String[] str2 = AppManagerUtil.getCurDate().split("-");
		int curYear = Integer.valueOf(str2[0]);
		//int curMonth = Integer.valueOf(str2[1]);
		//int curDay = Integer.valueOf(str2[2]);
		
		String curDate = AppManagerUtil.getSimpleCurDate();
		String nextMemoday = curYear + "-" + month + "-" + day;
		int countdown = CommonFunction.calculateDay(curDate, nextMemoday);
		if (countdown < 0) {
			nextMemoday = (curYear + 1) + "-" + month + "-" + day;
			countdown = CommonFunction.calculateDay(curDate, nextMemoday);
		}
		
		if (countdown == 0)
			prompt_count.setText("祝 " + peopleName + " 生日快乐");
		else
			prompt_count.setText("距离下次过生日还有 " + countdown + " 天");
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

		String peopleName;
		if ((super.mCalendarTable.getInitDate().equals(CalendarMainActivity.gWifeBirthdayId) && 
				SelfInfo.getInstance().getSex().equals(SelfInfo.SEX_GIRL)) 
				|| 
				(super.mCalendarTable.getInitDate().equals(CalendarMainActivity.gHusbandBirthdayId) && 
				SelfInfo.getInstance().getSex().equals(SelfInfo.SEX_BOY))) {
			peopleName = SelfInfo.getInstance().getNickName();
		}
		else {
			peopleName = GlobalApplication.getInstance().getTiBigName();
			if (peopleName == null || peopleName.equals(""))
				peopleName = "TA";
		}
		title.setText(peopleName + " 的生日");
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
