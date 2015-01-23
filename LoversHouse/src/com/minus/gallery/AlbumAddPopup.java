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
        //ȡ����ť
        btn_cancel.setOnClickListener(new OnClickListener() {
 
            public void onClick(View v) {
                //���ٵ�����
                dismiss();
            }
        });
        //���ð�ť����
        if(checkCameraHardware(context)){
        	 btn_take_photo.setOnClickListener(itemsOnClick);
        	 btn_take_photo.setVisibility(View.VISIBLE);
        }else{
        	btn_take_photo.setOnClickListener(null);
        	 btn_take_photo.setVisibility(View.GONE);
        }
        btn_pick_photo.setOnClickListener(itemsOnClick);
       
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