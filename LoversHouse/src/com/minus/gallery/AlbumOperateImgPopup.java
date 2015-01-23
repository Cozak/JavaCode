package com.minus.gallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.minus.lovershouse.R;

public class AlbumOperateImgPopup extends PopupWindow {
 
 
    private TextView btn_showInMain, btn_saveImage, btn_deleteImage,btn_galleryCancleOperation;
    private View mMenuView;
 
    public AlbumOperateImgPopup(Activity context,OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.operate_image, null);
        btn_showInMain = (TextView) mMenuView.findViewById(R.id.galleryShowInMain);
        btn_saveImage = (TextView) mMenuView.findViewById(R.id.gallerySaveImage);
        btn_deleteImage = (TextView) mMenuView.findViewById(R.id.galleryDeleteImage);
        btn_galleryCancleOperation = (TextView) mMenuView.findViewById(R.id.galleryCancleOperation);
   
        //ȡ����ť
        btn_galleryCancleOperation.setOnClickListener(new OnClickListener() {
 
            public void onClick(View v) {
                //���ٵ�����
                dismiss();
            }
        });
        //���ð�ť����
        btn_showInMain.setOnClickListener(itemsOnClick);
        btn_saveImage.setOnClickListener(itemsOnClick);
        btn_deleteImage.setOnClickListener(itemsOnClick);
       
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