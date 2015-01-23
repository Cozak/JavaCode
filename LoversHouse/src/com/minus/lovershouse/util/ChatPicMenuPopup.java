package com.minus.lovershouse.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.minus.lovershouse.R;

public class ChatPicMenuPopup extends PopupWindow {
 
 
    private TextView btn_savToPhone, btn_savToAlbum, btn_cancel;
    private View mMenuView;
 
    public ChatPicMenuPopup(Activity context,OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.chat_pic_menu, null);
        btn_savToPhone = (TextView) mMenuView.findViewById(R.id.btn_save_phone);
        btn_savToAlbum = (TextView) mMenuView.findViewById(R.id.btn_save_album);
        btn_cancel =  (TextView)mMenuView.findViewById(R.id.cancel);
        //ȡ����ť
        btn_cancel.setOnClickListener(new OnClickListener() {
 
            public void onClick(View v) {
                //���ٵ�����
                dismiss();
            }
        });
        //���ð�ť����
     
        btn_savToPhone.setOnClickListener(itemsOnClick);
        btn_savToAlbum.setOnClickListener(itemsOnClick);
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
        ColorDrawable dw = new ColorDrawable(0xb0000000);
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
