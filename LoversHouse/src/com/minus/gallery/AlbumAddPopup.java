package com.minus.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.minus.lovershouse.R;

public class AlbumAddPopup  extends PopupWindow {
 
 
    private TextView btn_take_photo, btn_pick_photo, btn_cancel;
    private TextView tipTv;
    private View mMenuView;
 
    public AlbumAddPopup(Activity context,OnClickListener itemsOnClick,String tip) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.update_image, null);
        btn_take_photo = (TextView) mMenuView.findViewById(R.id.gallery_makePhoto);
        btn_pick_photo = (TextView) mMenuView.findViewById(R.id.gallery_comeFromPhone);
        btn_cancel = (TextView) mMenuView.findViewById(R.id.gallery_cancleUpdate);
        tipTv = (TextView) mMenuView.findViewById(R.id.gallery_updateText);
        tipTv.setText(tip);
        //取消按钮
        btn_cancel.setOnClickListener(new OnClickListener() {
 
            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });
        //设置按钮监听
        if(checkCameraHardware(context)){
        	 btn_take_photo.setOnClickListener(itemsOnClick);
        	 btn_take_photo.setVisibility(View.VISIBLE);
        }else{
        	btn_take_photo.setOnClickListener(null);
        	 btn_take_photo.setVisibility(View.GONE);
        }
        btn_pick_photo.setOnClickListener(itemsOnClick);
       
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
	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	public void setTipText(String tip){
		this.tipTv.setText(tip);
	}
	
}