package com.minus.gallery;
import android.content.Context;  
import android.view.GestureDetector.SimpleOnGestureListener;  
import android.view.GestureDetector;  
import android.view.View;  
import android.view.View.OnTouchListener;  
import android.view.MotionEvent;  
  
/** 
 * 实现监听左右滑动的事件，哪个view需要的时候直接setOnTouchListener就可以用了 
 */  
public class GestureListener extends SimpleOnGestureListener implements OnTouchListener {  
    /** 左右滑动的最短距离 */  
    private int distance = 100;  
    /** 左右滑动的最大速度 */  
    private int velocity = 200;  
    
    private int lastMotionX = 0;
    private int lastMotionY = 0;
    private long  lastDowntime = 0;

      
    private GestureDetector gestureDetector;  
      
    public GestureListener(Context context) {  
        super();  
        gestureDetector = new GestureDetector(context, this);  
    }  
  
    /** 
     * 向左滑的时候调用的方法，子类应该重写 
     * @return 
     */  
    public boolean left() {  
        return true;  
    }  
      
    /** 
     * 向右滑的时候调用的方法，子类应该重写 
     * @return 
     */  
    public boolean right() {  
        return true;  
    }  
    
    public boolean clickView() {  
        return true;  
    }  
      
    @Override  
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
            float velocityY) {  
        // TODO Auto-generated method stub  
        // e1：第1个ACTION_DOWN MotionEvent  
        // e2：最后一个ACTION_MOVE MotionEvent  
        // velocityX：X轴上的移动速度（像素/秒）  
        // velocityY：Y轴上的移动速度（像素/秒）  
    	
    	
        // 向左滑  
        if (e1.getX() - e2.getX() > distance  
                && Math.abs(velocityX) > velocity) {  
            left();  
        }  
        // 向右滑  
        if (e2.getX() - e1.getX() > distance  
                && Math.abs(velocityX) > velocity) {  
            right();  
        }  
        return false;  
    }  
  
    @Override  
    public boolean onTouch(View v, MotionEvent ev) {  
        // TODO Auto-generated method stub  
        gestureDetector.onTouchEvent(ev);  
		int x = (int)ev.getRawX();
		int y = (int)ev.getRawY();
		
		
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			/*		手指按下时的x坐标*/
			lastMotionX = (int)ev.getRawX();
			lastMotionY = (int)ev.getRawY();
			lastDowntime = ev.getDownTime();
//			ev.get
		}
		if(ev.getAction() == MotionEvent.ACTION_MOVE){
		
		}

			if(ev.getAction() == MotionEvent.ACTION_UP){
			
			
			      if(Math.abs(this.lastMotionX-x) <= 80 && Math.abs(this.lastMotionY-y) <= 80){
			    	  clickView();
			 
			    	  }
				}                 
			     

		

        return false;  
    }  
  
    public int getDistance() {  
        return distance;  
    }  
  
    public void setDistance(int distance) {  
        this.distance = distance;  
    }  
  
    public int getVelocity() {  
        return velocity;  
    }  
  
    public void setVelocity(int velocity) {  
        this.velocity = velocity;  
    }  
  
    public GestureDetector getGestureDetector() {  
        return gestureDetector;  
    }  
  
    public void setGestureDetector(GestureDetector gestureDetector) {  
        this.gestureDetector = gestureDetector;  
    }  
    
    /**
	 * * 判断是否有长按动作发生 * @param lastX 按下时X坐标 * @param lastY 按下时Y坐标 *
	 * 
	 * @param thisX
	 *            移动时X坐标 *
	 * @param thisY
	 *            移动时Y坐标 *
	 * @param lastDownTime
	 *            按下时间 *
	 * @param thisEventTime
	 *            移动时间 *
	 * @param longPressTime
	 *            判断长按时间的阀值
	 */
    boolean isLongPressed(int lastX, int lastY, int thisX,
			int thisY, long lastDownTime, long thisEventTime,
			long longPressTime) {
		int offsetX = Math.abs(thisX - lastX);
		int offsetY = Math.abs(thisY - lastY);
		long intervalTime = thisEventTime - lastDownTime;
		if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
			return true;
		}
		return false;
	}
}  