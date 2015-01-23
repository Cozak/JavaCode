package com.minus.gallery;
import android.content.Context;  
import android.view.GestureDetector.SimpleOnGestureListener;  
import android.view.GestureDetector;  
import android.view.View;  
import android.view.View.OnTouchListener;  
import android.view.MotionEvent;  
  
/** 
 * ʵ�ּ������һ������¼����ĸ�view��Ҫ��ʱ��ֱ��setOnTouchListener�Ϳ������� 
 */  
public class GestureListener extends SimpleOnGestureListener implements OnTouchListener {  
    /** ���һ�������̾��� */  
    private int distance = 100;  
    /** ���һ���������ٶ� */  
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
     * ���󻬵�ʱ����õķ���������Ӧ����д 
     * @return 
     */  
    public boolean left() {  
        return true;  
    }  
      
    /** 
     * ���һ���ʱ����õķ���������Ӧ����д 
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
        // e1����1��ACTION_DOWN MotionEvent  
        // e2�����һ��ACTION_MOVE MotionEvent  
        // velocityX��X���ϵ��ƶ��ٶȣ�����/�룩  
        // velocityY��Y���ϵ��ƶ��ٶȣ�����/�룩  
    	
    	
        // ����  
        if (e1.getX() - e2.getX() > distance  
                && Math.abs(velocityX) > velocity) {  
            left();  
        }  
        // ���һ�  
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
			/*		��ָ����ʱ��x����*/
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
	 * * �ж��Ƿ��г����������� * @param lastX ����ʱX���� * @param lastY ����ʱY���� *
	 * 
	 * @param thisX
	 *            �ƶ�ʱX���� *
	 * @param thisY
	 *            �ƶ�ʱY���� *
	 * @param lastDownTime
	 *            ����ʱ�� *
	 * @param thisEventTime
	 *            �ƶ�ʱ�� *
	 * @param longPressTime
	 *            �жϳ���ʱ��ķ�ֵ
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