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
   
        //取消按钮
        btn_galleryCancleOperation.setOnClickListener(new OnClickListener() {
 
            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });
        //设置按钮监听
        btn_showInMain.setOnClickListener(itemsOnClick);
        btn_saveImage.setOnClickListener(itemsOnClick);
        btn_deleteImage.setOnClickListener(itemsOnClick);
       
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
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