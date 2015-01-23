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
        	title.setText("退出“MiniUs”?");
        //取消按钮
        cancelexit.setOnClickListener(new OnClickListener() {
 
            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });
        //设置按钮监听
        
        exitapp.setOnClickListener(itemsOnClick);
        
     
       
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
        ColorDrawable dw = new ColorDrawable(0x00000000);
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
