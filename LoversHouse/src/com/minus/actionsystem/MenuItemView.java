package com.minus.actionsystem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
public class MenuItemView extends ViewGroup{
	
	public  final static int STATUS_CLOSE = 5;
	public  final static int STATUS_OPEN = 6;
	
	
	private int flagX = 1;
	private int flagY =1;
	private float radius =100;
	private int status ;
//	private int customSize = -1;  //自定义动作个数
	private boolean myChange = false;
	
	private int positon =1;
	private int viewHeight = 50;
	
	private Context context;
	public MenuItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MenuItemView(Context context) {
		super(context);
	}
	
	private void init(Context context) {
		this.context = context;
		this.status = STATUS_CLOSE;
//		this.customSize = -1;
		
	}
	
	public void setPosition( int pos,int height){
		this.positon = pos;
		this.viewHeight = height;
		//水平轴，负数为左边两个，否则调整右边，正负互换会导致Item从远处靠拢而不是散开
		flagX = 1;
		//垂直轴，负数为调整上面两个，否则调整下面两个
		flagY = 1;
		
	}
	public void setPosition( ){
		this.positon = 1;
		viewHeight = 50;
		//水平轴，负数为左边两个，否则调整右边，正负互换会导致Item从远处靠拢而不是散开
		flagX = 1;
		//垂直轴，负数为调整上面两个，否则调整下面两个
		flagY = 1;
		
	}
	
	@Override
	     protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	         for (int index = 0; index < getChildCount(); index++) {
	             final View child = getChildAt(index);
	             // measure
	            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
	        }
	
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    }

	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		if(positon == 0)
//			throw new RuntimeException("PositonUnknow!Use method setPosition to set the position first!");
		if(radius == 0)
			radius = 100;
		
		if(changed  || myChange){
		
			int count = getChildCount();
			int dx = -flagX * (int) MyAnimations.dip2px(context, 10);
			int dy = -flagY * (int) MyAnimations.dip2px(context, 10);
			for (int i = 0; i < count; i++) {
				View childView = getChildAt(i);
				if(!(myChange)){
				childView.setVisibility(View.GONE);
				}
				
				int width = childView.getMeasuredWidth();
				int height = childView.getMeasuredHeight();
				
				if(count == 1){
					
					float temp = MyAnimations.PI/ (float)(5) * i-MyAnimations.PI/2;
//					int x = (int) (radius *  Math.sin(temp));
//					int y = (int) (radius* 1.5 * Math.cos(temp));
					int x = (int) (MyAnimations.dip2px(context, 50) *  Math.sin(temp));
					int y = (int) (MyAnimations.dip2px(context, 50) *  Math.cos(temp));
					x = -x*3/4 -(int)(width/2.5) + this.positon;
//					x =  this.positon - x*3/4 -(int)(width/2.5);
					y = (int)(getMeasuredHeight()/1.1) - y*3/4 -2*height ;
					childView.layout(x + dx , y + dy, x + width+ dx, y + height + dy );
				}else{
				//the position of childview leftTop
				float temp = MyAnimations.PI/ (float)(count - 1) * i-MyAnimations.PI/2;
//				int x = (int) (radius *  Math.sin(temp));
//				int y = (int) (radius* 1.5 * Math.cos(temp));
//				int x = (int) (MyAnimations.dip2px(context, radius) *  Math.sin(temp));
//				int y = (int) (MyAnimations.dip2px(context, radius) * 2 * Math.cos(temp));
				int x = (int) (MyAnimations.dip2px(context, radius) *  Math.sin(temp));
				int y = (int) (MyAnimations.dip2px(context, radius) *  Math.cos(temp));
				x = -x*3/4 -(int)(width/2.5) + this.positon;
//				x =  this.positon - x*3/4 -(int)(width/2.5);
//				getMeasuredHeight() :   菜单栏的高  height button的高this.viewHeight
//				y = (int)(getMeasuredHeight()/1.1) - y*3/4 -2*height ;
				y = (int)(this.viewHeight/1.1) - y*3/4 -height ;
				childView.layout(x + dx , y + dy, x + width+ dx, y + height + dy );
				}
			}
			myChange = false;
		}
	}
	
	public int getFlagX() {
		return flagX;
	}

	public void setFlagX(int flagX) {
		this.flagX = flagX;
	}

	public int getFlagY() {
		return flagY;
	}

	public void setFlagY(int flagY) {
		this.flagY = flagY;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		if(radius >100){
			this.radius = 100;
		}else{
		this.radius = radius;
		}
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	
//	public int getCustomSize() {
//		return customSize;
//	}
//
//	public void setCustomSize(int customSize) {
//		this.customSize = customSize;
//	}

	public boolean isMyChange() {
		return myChange;
	}

	public void setMyChange(boolean myChange) {
		this.myChange = myChange;
	}

}
