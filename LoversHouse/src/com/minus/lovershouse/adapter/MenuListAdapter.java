package com.minus.lovershouse.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.minius.common.CommonBitmap;
import com.minus.lovershouse.R;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.RoundedImageView;
import com.minus.xsocket.asynsocket.protocol.Protocol;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuListAdapter extends BaseAdapter {
	
	private Activity context;
	private List<Map<String, Object>> position;
	private int itemCount;
	private LayoutInflater listInflater;
	private int[] HeadimageIds= {R.drawable.girl_photoframe,
			R.drawable.default_girl_photo, R.drawable.boy_photoframe,
			R.drawable.default_boy_photo};
	private int[] popIds = {R.string.me,R.string.ta,R.string.housestyle,
			R.string.software};//R.drawable._0007_goodfriends,
	private final int COUNT = 5; 
//	private LayerDrawable layerDrawable;
	
//	private View taView= null;

	
	/*一个menu item中包含一个imageView*/
	public final class ListItemsView{
		
		public ImageView menuIcon;
		public TextView popupText;
		public ImageView dividerView;
	}
	
	
	
//	public void setTaView(View taView) {
//		this.taView = taView;
//	}
	public MenuListAdapter(Activity context) {
		this.context = context;

		this.itemCount = this.COUNT;
//		this.init(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.itemCount;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		System.out.println(position);
		ListItemsView listItemsView;
		this.listInflater = LayoutInflater.from(context); 
		listItemsView = new ListItemsView();
		convertView = this.listInflater.inflate(R.layout.menu_list_item, null);
		
//		listItemsView.headframe=(ImageView)convertView.findViewById(R.id.headphotoframe);
		listItemsView.menuIcon = (ImageView)convertView.findViewById(R.id.roundimage);
		listItemsView.popupText = (TextView)convertView.findViewById(R.id.popuptext);
		listItemsView.dividerView = (ImageView)convertView.findViewById(R.id.dividerview);
		
		if(position == 0){
//			listItemsView.headframe.setVisibility(View.VISIBLE);
			
			Resources r = context.getResources();
			
//			if(SelfInfo.getInstance().getSex().equals("b")){
//				
//				listItemsView.headframe.setBackgroundResource(HeadimageIds[2]);
//				      
//			}else{
//				listItemsView.headframe.setBackgroundResource(HeadimageIds[0]);
//			}	

			listItemsView.menuIcon.setVisibility(View.VISIBLE);
			listItemsView.popupText.setVisibility(View.GONE);
			if(CommonBitmap.getInstance().getMyHeadBm()!=null){
	
//				listItemsView.menuIcon.setVisibility(View.GONE);
//				listItemsView.menuIcon2.setVisibility(View.VISIBLE);
				
//				listItemsView.menuIcon.setImageBitmap(AppManagerUtil.createBitmapBySize(
//						GlobalApplication.getInstance().getHeadPicBm(),50,50));
				listItemsView.menuIcon.setImageBitmap(
						CommonBitmap.getInstance().getMyHeadBm());
			}
			else{
//				listItemsView.menuIcon.setVisibility(View.VISIBLE);
//				listItemsView.menuIcon2.setVisibility(View.GONE);
				
				Drawable layer;
				if(SelfInfo.getInstance().getSex().equals("b")){
					
					layer = r.getDrawable(HeadimageIds[3]);
					      
				}else{
					layer = r.getDrawable(HeadimageIds[1]);
				}
//				layerDrawable = new LayerDrawable(layers);
		
				listItemsView.menuIcon.setImageDrawable(layer);
			}
				
			
			convertView.setTag(listItemsView);
		}else{
//			listItemsView.headframe.setVisibility(View.GONE);
			listItemsView.menuIcon.setVisibility(View.GONE);
			listItemsView.popupText.setVisibility(View.VISIBLE);

			listItemsView.popupText.setText(popIds[position-1]);
//			if(position == 2){
//			if(taView == null){
//				taView = convertView;
//			}
//			}
			convertView.setTag(listItemsView);

		}
		
		
		return convertView;
	} 

}