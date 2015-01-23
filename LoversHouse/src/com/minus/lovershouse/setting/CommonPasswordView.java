package com.minus.lovershouse.setting;

import com.minus.lovershouse.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommonPasswordView extends LinearLayout {

	Context context;
	// 展示位数的4个点
	RelativeLayout showNumberLayout;
	// 显示的4个点的图片
	private ImageView[] imgs = new ImageView[4];
	// 展示数字的
	GridView dcMyGameMyGridView;
	// 界面的声明
	LayoutInflater mInflater;
	//更改的密码
	private String password = "";
	//键盘的左下角按钮
	OnClickPhoneNumberListener onClickPhoneNumberListener;
	//左下角文字的显示
//	String showButtonText;
	private Handler lockHandler = null;
	PhoneNumberAdapter phoneNumberAdapter = new PhoneNumberAdapter();
	//声明数字0-9
	private int[] phoneNumberImgs = { R.drawable.selector_password_button1,
			R.drawable.selector_password_button2,
			R.drawable.selector_password_button3,
			R.drawable.selector_password_button4,
			R.drawable.selector_password_button5,
			R.drawable.selector_password_button6,
			R.drawable.selector_password_button7,
			R.drawable.selector_password_button8,
			R.drawable.selector_password_button9,
			R.drawable.selector_password_button0, 
			R.drawable.password_cancel};
	private TextView unlockhint;

	public CommonPasswordView(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	public CommonPasswordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}
	
	public void setOnClickPhoneNumberListener (OnClickPhoneNumberListener onClickPhoneNumberListener){
		this.onClickPhoneNumberListener = onClickPhoneNumberListener;
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		this.mInflater = LayoutInflater.from(context);
		LayoutInflater.from(context).inflate(R.layout.common_password_view,
				this);
		showNumberLayout = (RelativeLayout) findViewById(R.id.llayout);
		for (int i = 0; i < 4; i++) {
			imgs[i] = (ImageView) showNumberLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		setHideImageView();
		unlockhint = (TextView) findViewById(R.id.unlockhint);
		dcMyGameMyGridView = (GridView) findViewById(R.id.phone_number);
		dcMyGameMyGridView.setAdapter(phoneNumberAdapter);
		dcMyGameMyGridView.setSelector(new ColorDrawable(
				Color.TRANSPARENT));
		dcMyGameMyGridView.setOnItemClickListener(new PhoneNumberItemClickListener());
	}

	/**
	 * 展示的点的有几个是被选中的
	 * 
	 * @param count
	 *            数量
	 */
	public void setShowImageViewCount(int count) {
		for (int i = 0; i < count; i++) {
			imgs[i].setEnabled(false);
		}
	}
	
	/**
	 * 当前显示的有几位，然后剩下的隐藏
	 */
	public void setHideImageView(){
		int length = getPassword().length();
		for (int i = 0; i < length; i++) {
			imgs[i].setEnabled(false);
		}
		for (int i = 0;i<4-length;i++){
			imgs[3-i].setEnabled(true); 
		}
	}

	/**
	 * 展示数字键盘
	 * 
	 * @author imlilu
	 * 
	 */
	private class PhoneNumberAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 12;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {

			if (position < 9) {
				contentView = mInflater.inflate(R.layout.phone_number_layout,
						null);
				ImageView imageView = (ImageView) contentView
						.findViewById(R.id.phone_number_view);
				imageView.setImageResource(phoneNumberImgs[position]);

			} else if (position == 9) {
				contentView = mInflater.inflate(R.layout.phone_number_text_layout,
						null);
//				ImageView imageView = (ImageView) contentView
//						.findViewById(R.id.delete);
			} else if (position == 10) {
				contentView = mInflater.inflate(R.layout.phone_number_layout,
						null);
				ImageView imageView = (ImageView) contentView
						.findViewById(R.id.phone_number_view);
				imageView.setImageResource(phoneNumberImgs[9]);
			} else if (position == 11) {
				contentView = mInflater.inflate(R.layout.phone_number_text_layout,
						null);
				ImageView imageView = (ImageView) contentView
						.findViewById(R.id.delete);
				imageView.setImageResource(phoneNumberImgs[10]);
			}
			return contentView;
		}

	}
	
	private class PhoneNumberItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			if (position < 9) {
				//添加数字
				changePasswrod((position+1)+"");
			} else if (position == 9) {
				//根据界面执行结果
//				if (getPassword().length()<4) {
////					Toast.makeText(getContext(), "密码不足4位", Toast.LENGTH_SHORT).show();
//					unlockhint.setText("密码不足4位");
//					return;
//				}
				
			} else if (position == 10) {
				//添加数字
				changePasswrod(0+"");
			} else if (position == 11) {
				//删除一位
				deletePassword();
			}
		}
		
	}
	
	/**
	 * 拼接传入进来的字符串输出密码
	 * @param number
	 */
	private void changePasswrod(String number){
		String passwordString = getPassword();
		unlockhint.setVisibility(View.INVISIBLE);
		if (TextUtils.isEmpty(passwordString)||passwordString.length()<4) {
			setPassword(passwordString+number);
			setHideImageView();
			if(getPassword().length()==4){
				if(this.lockHandler == null){
					this.lockHandler = new MyHandler();
					
				}
				Message msg =lockHandler.obtainMessage();//延迟100ms，使第四个圆点显示
				msg.what = 1;
				lockHandler.sendMessageDelayed(msg, 100);
				
			}
		}
	}
	private  class MyHandler extends Handler {
		

		@Override
		public void handleMessage(Message msg) {
	
			unlockhint.setVisibility(View.VISIBLE);
				if (onClickPhoneNumberListener != null) {
					onClickPhoneNumberListener.OnClick();
				}
		}
	}
	/**
	 * 删除一个密码
	 */
	private void deletePassword(){
		String passwordString = getPassword();
		if (!TextUtils.isEmpty(passwordString)||passwordString.length()>0) {
			setPassword(passwordString.substring(0, passwordString.length()-1));
			setHideImageView();
		}else {
//			Toast.makeText(getContext(), "密码已清空", Toast.LENGTH_SHORT).show();
			unlockhint.setText("密码已清空");
		}
	}
	
	public String getHint() {
		return unlockhint.getText().toString();
	}

	public void setHint(String hint,Animation shake) {
		this.unlockhint.setText(hint);
		if(shake!=null)
			unlockhint.startAnimation(shake);
	}
	
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public static interface OnClickPhoneNumberListener{
		public void OnClick();
	}

//	public String getShowButtonText() {
//		return showButtonText;
//	}
//
//	public void setShowButtonText(String showButtonText) {
//		this.showButtonText = showButtonText;
//		phoneNumberAdapter.notifyDataSetChanged();
//	}
	
	public void cleanAllPassword(){
		setPassword("");
		setHideImageView();
	}
	
}
