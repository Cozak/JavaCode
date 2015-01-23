package com.minius.leadpage;

import java.util.List;

import com.minus.lovershouse.R;
import com.minus.lovershouse.RegisterActivity;
import com.minus.xsocket.asynsocket.protocol.Protocol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ViewPagerAdapter extends PagerAdapter {

	// 界面列表
	private List<View> views;
	private Activity activity;


	public ViewPagerAdapter(List<View> views, Activity activity) {
		this.views = views;
		this.activity = activity;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(views.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	// 获得当前界面
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	//添加Button事件响应
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(views.get(arg1), 0);
		if (arg1 == views.size() - 1) {
			ImageView mStartImageButton = (ImageView) arg0
					.findViewById(R.id.start_button);
			mStartImageButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					goHome();
				}

			});
		}
		return views.get(arg1);
	}

	private void goHome() {
		SharedPreferences mSP = activity.getApplicationContext().getSharedPreferences(
				Protocol.PREFERENCE_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSP.edit();
		mEditor.putBoolean("IsFirstRun", false);
		mEditor.commit();
		Intent intent = new Intent(activity, RegisterActivity.class);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.in_from_left_slow,
				R.anim.out_to_right_slow);
		activity.finish();
	}

	/*设置已经引导过了，下次启动不用再次引导
	 */

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
