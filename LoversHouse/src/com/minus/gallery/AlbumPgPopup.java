package com.minus.gallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.minus.lovershouse.R;

public class AlbumPgPopup extends PopupWindow {
 
 
    private Button  btn_cancel;
    private NumberProgressBar bnp = null ;
    private TextView tipTv;
  
    private View mMenuView;
 
    public AlbumPgPopup(Activity context, String tip) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.albumpgbar, null);
        btn_cancel = (Button) mMenuView.findViewById(R.id.album_canceluploadbtn);
        bnp= (NumberProgressBar ) mMenuView.findViewById(R.id.numberbar);
        bnp.setMax(100);
        tipTv = (TextView) mMenuView.findViewById(R.id.uploadtipTv);
        tipTv.setText(tip);
        //取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(AlbumImageHandler.getInstance().isUploadingImage()){
				AlbumImageHandler.getInstance().onClickedDismissUploadImage();
				}
				dismiss();
				 bnp.setProgress(0);
			
				
			}
		});
   
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
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
        
        setOutsideTouchable(false);
 
    }
//更新提示信息
	public void setTipText(String tip){
		this.tipTv.setText(tip);
	}
	//更新进度条
	public void setPg(int pg){
	 if(bnp != null){
		 bnp.setProgress(pg);
	 }
	}
	

	
}