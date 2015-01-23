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
        //ȡ����ť
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
   
        //����SelectPicPopupWindow��View
        this.setContentView(mMenuView);
        //����SelectPicPopupWindow��������Ŀ�
        this.setWidth(LayoutParams.WRAP_CONTENT);
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
        
        setOutsideTouchable(false);
 
    }
//������ʾ��Ϣ
	public void setTipText(String tip){
		this.tipTv.setText(tip);
	}
	//���½�����
	public void setPg(int pg){
	 if(bnp != null){
		 bnp.setProgress(pg);
	 }
	}
	

	
}