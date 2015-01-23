package com.minus.calendar;

import com.minus.lovershouse.R;
import com.minus.lovershouse.baseActivity.BroadCast;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.sql_interface.Database;
import com.minus.xsocket.handler.CalendarHandler;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarAddActivity extends BroadCast {
	
	private ImageView cancleButton;
	private ImageView completeButton;
	
	private EditText title;
	private TextView date;
	private Spinner prompt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_add);
		
		initTopView();
		initBodyView();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initTopView() {
		cancleButton = (ImageView) findViewById(R.id.calendar_add_canclebutton);
		completeButton = (ImageView) findViewById(R.id.calendar_add_completebutton);
		
		cancleButton.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		completeButton.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View arg0) {
	            String memoTitle = title.getText().toString();
				if (memoTitle.length() == 0) {
					title.setHint("请输入标题");
					return;
				}
	            if (memoTitle.contains(" ")) {
	            	Toast toast = Toast.makeText(CalendarAddActivity.this, "标题不能包含空格", Toast.LENGTH_LONG);
	            	toast.setGravity(Gravity.BOTTOM, 0, 100);
	            	toast.show();
	            	return;
	            }
				
	            String curdate = AppManagerUtil.getCurDate();
	            //TODO String memoDate = date.getText().toString();
	            String memoDate = date.getText().toString() + "-00:01:00";
	            String promptPolicy = "" + (char) (prompt.getSelectedItemPosition() + 1);
	            
	            Database.getInstance(CalendarAddActivity.this).addCalendar(
	            		curdate, curdate, memoDate, promptPolicy, memoTitle);
	    		CalendarHandler.getInstance().addCalendar(
	    				curdate, curdate, memoDate, promptPolicy, memoTitle);
	    		
	            finish();
			}
		});
	}
	
	private void initBodyView() {
		title = (EditText) findViewById(R.id.calendar_add_title);
		date = (TextView) findViewById(R.id.calendar_add_date);
		prompt = (Spinner) findViewById(R.id.calendar_add_prompt);
		final TextView prompt_title = (TextView) this.findViewById(R.id.calendar_add_prompt_title);
		
		title.addTextChangedListener(new MaxLengthWatcher(
				MaxLengthWatcher.MAX_CHINESE_CHARACTER_LENGTH, title));
		
		prompt_title.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				prompt.performClick();
			}
		});
		//prompt.setPrompt("提醒方式");
		prompt.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String text = "提醒方式：" + getResources().getStringArray(R.array.calendar_prompt_policy)[arg2];
				prompt_title.setText(text);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {	
			}
		});
		
		date.setText(AppManagerUtil.getSimpleCurDate());
		date.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] str = date.getText().toString().split("-");
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
					}
				};
				DatePickerDialog datePickerDialog = new DatePickerDialog(
						CalendarAddActivity.this, onDateSetListener, year, month-1, day);
				datePickerDialog.show();
			}
		});
	}
	
}
