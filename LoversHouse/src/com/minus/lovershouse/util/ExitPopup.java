package com.minus.lovershouse.util;

import com.minus.lovershouse.R;

import android.app.Activity;
import android.content.Context;
//import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
//import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;


public class ExitPopup  extends PopupWindow {
 
 
    private TextView exitapp, cancelexit;
    private View mMenuView;
	private TextView title;
 
    public ExitPopup(Activity context,String titlePop,OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.exitbottompop, null);
        exitapp = (TextView) mMenuView.findViewById(R.id.exitapp);
        cancelexit = (TextView) mMenuView.findViewById(R.id.cancelexit);
        title = (TextView) mMenuView.findViewById(R.id.title);
        if(titlePop.equals("mainexit"))
        	title.setText("�˳���MiniUs��?");
        //ȡ����ť
        cancelexit.setOnClickListener(new OnClickListener() {
 
            public void onClick(View v) {
                //���ٵ�����
                dismiss();
            }
        });
        //���ð�ť����
        
        exitapp.setOnClickListener(itemsOnClick);
        
     
       
        //����SelectPicPopupWindow��View
        this.setContentView(mMenuView);
        //����SelectPicPopupWindow��������Ŀ�
        this.setWidth(LayoutParams.MATCH_PARENT);
        //����SelectPicPopupWindow��������ĸ�
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //����SelectPicPopupWindow��������ɵ��
        this.setFocusable(true);
        //����SelectPicPopupWindow�������嶯��Ч��
        this.setAnimationStyle(R.style.AnimBottom);
        //ʵ����һ��ColorDrawable��ɫΪ��͸��
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //����SelectPicPopupWindow��������ı���
        this.setBackgroundDrawable(dw);
        //mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ������������ٵ�����
        mMenuView.setOnTouchListener(new OnTouchListener() {
             
            public boolean onTouch(View v, MotionEvent event) {
                 
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }               
                return true;
            }
        });
 
    }
	
}
