package com.minus.actionsystem;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class MyAnimations {
	public final static float PI = (float) 3.1415926536;
	private static boolean clockwise = false;
  
	/**
	 * @param context
	 * @param view
	 * @param radiusByDP
	 * @param durationMillis
	 */
		
		public static void startAnimations(Context context, final MenuItemView view, int durationMillis) {
			final MainActivityItemClickListener listener = (MainActivityItemClickListener) context;
			final ActionBtnOnItemLongClickListener LongClicklistener =
					(ActionBtnOnItemLongClickListener) context;
			float radiusByDP = dip2px(context, view.getRadius());
			for (int i = 0; i < view.getChildCount(); i++) {
				final View childView = view.getChildAt(i);
				int count = view.getChildCount();
				childView.setVisibility(View.VISIBLE);
				float x ;
				float y;
				if(count ==1 ){
					x= 0;
					y=  view.getFlagY() * (float) (radiusByDP);
				}else{
					 x = view.getFlagX() * (float) (radiusByDP *  Math.sin(PI / 2 / (float)(count - 1) * i));
					 y = view.getFlagY() * (float) (radiusByDP *  Math.cos(PI / 2 / (float)(count - 1) * i));
				}
				
				AnimationSet as=new AnimationSet(true);
				Animation animation = null;
				if(view.getStatus() == MenuItemView.STATUS_CLOSE){
					//to open
					if(count ==1 ){
						as.setInterpolator(new OvershootInterpolator(2F));
						animation = new TranslateAnimation(x, 0, y, 0);
				
						childView.setClickable(true);
						childView.setFocusable(true);
						childView.setLongClickable(true);
					}else{
					as.setInterpolator(new OvershootInterpolator(2F));
					animation = new TranslateAnimation(x, 0, y, 0);
					childView.setClickable(true);
					childView.setFocusable(true);
					childView.setLongClickable(true);
					}
				}else if(view.getStatus() == MenuItemView.STATUS_OPEN){//to close
					if(count ==1 ){
						animation = new TranslateAnimation(0f, x, 0f, y);
						childView.setClickable(false);
						childView.setFocusable(false);
						childView.setLongClickable(false);
					}else{
					animation = new TranslateAnimation(0f, x, 0f, y);
					childView.setClickable(false);
					childView.setFocusable(false);
					childView.setLongClickable(false);
					}
				}
				animation.setAnimationListener(new AnimationListener() {
					public void onAnimationStart(Animation animation) {
					}
					public void onAnimationRepeat(Animation animation) {}
					public void onAnimationEnd(Animation animation) {
						if(view.getStatus() == MenuItemView.STATUS_CLOSE)
							childView.setVisibility(View.GONE);
						
					}
				});

				animation.setFillAfter(true);
				animation.setDuration(durationMillis);
				if(view.getChildCount() == 1){
					animation.setStartOffset(0);
				}else{
				animation.setStartOffset((i * 100) / (-1 + view.getChildCount()));
				}
				
				RotateAnimation rotate = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				rotate.setDuration(durationMillis);
				rotate.setFillAfter(true);
				
				as.addAnimation(rotate);
				as.addAnimation(animation);
				
				childView.startAnimation(as);
				final int item = i;
				childView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						toShowAnimations(view, item);
						view.setStatus(MenuItemView.STATUS_CLOSE);
						listener.onclick(item);
						
					}
				});
				
                childView.setOnLongClickListener(new View.OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						toShowAnimations(view, item);
						view.setStatus(MenuItemView.STATUS_CLOSE);
				
						LongClicklistener.onActionBtnLongclick(item);
						return true;
					}
				});
               

			}
			int status = view.getStatus() == MenuItemView.STATUS_CLOSE ? MenuItemView.STATUS_OPEN : MenuItemView.STATUS_CLOSE;
			view.setStatus(status);
		}
		
		private static void toShowAnimations(ViewGroup viewgroup, int item) {
			for (int i = 0; i < viewgroup.getChildCount(); i++) {
				View childView = viewgroup.getChildAt(i);
				if(i == item){
					childView.startAnimation(getMaxAnimation(300));
				}else{
					childView.startAnimation(getMiniAnimation(300));
				}
				childView.setClickable(false);
				childView.setFocusable(false);
				childView.setLongClickable(false);
				
			}
			
		}

		private static Animation getMiniAnimation(int durationMillis) {
			Animation miniAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			miniAnimation.setDuration(durationMillis);
			miniAnimation.setFillAfter(true);
			return miniAnimation;
		}
	
		private static Animation getMaxAnimation(int durationMillis) {
			AnimationSet animationset = new AnimationSet(true);

			Animation maxAnimation = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			Animation alphaAnimation = new AlphaAnimation(1, 0);

			animationset.addAnimation(maxAnimation);
			animationset.addAnimation(alphaAnimation);

			animationset.setDuration(durationMillis);
			animationset.setFillAfter(true);
			return animationset;
		}
		public static void getRotateAnimation(View view, float fromDegrees, float toDegrees, int durationMillis) {
			RotateAnimation rotate = null;
			if(clockwise){
				rotate = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			}else{
				rotate = new RotateAnimation(toDegrees, fromDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			}
			rotate.setDuration(durationMillis);
			rotate.setFillAfter(true);
			view.startAnimation(rotate);
		}

	    public static float dip2px(Context context, float dpValue) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return  (dpValue * scale + 0.5f);  
	    }  
}
