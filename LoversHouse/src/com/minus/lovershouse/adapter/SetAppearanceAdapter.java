package com.minus.lovershouse.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.minus.lovershouse.R;
import com.minus.lovershouse.util.AppManagerUtil;


public class SetAppearanceAdapter  extends BaseAdapter {
	
	private Activity context;
	private List<Map<String, Object>> listItems;
	private int itemCount;
	private LayoutInflater listInflater;
	private int[] imageIds = {R.drawable.signup_2_hairx,R.drawable.signup_2_clothes,R.drawable.signup_2_decoration};
	private final int COUNT = 3; 
	private LayerDrawable layerDrawable;

	
	/*一个menu item中包含一个imageView*/
	public final class ListItemsView{
		public ImageView menuIcon;
	}
	
	
	public SetAppearanceAdapter(Activity context) {
		this.context = context;

		this.init(context); 
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
		if(convertView == null){
			listItemsView = new ListItemsView();
			convertView = this.listInflater.inflate(R.layout.setappearancelistviewitem, null);
			listItemsView.menuIcon = (ImageView)convertView.findViewById(R.id.appearanceIcon);
		
			//设置ViewItem的高度为ListView宽度的1.618倍
			//将图片重做，否则会出现滚动条
			Drawable drawable= context.getResources().getDrawable((Integer)listItems.get(position).get("menuIcon"));
			Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
			bitmap = AppManagerUtil.createBitmapBySize(bitmap,parent.getLayoutParams().width,(int)(parent.getLayoutParams().width*1.618)); 
			Log.e("test", String.valueOf(parent.getLayoutParams().width)+" "+String.valueOf(parent.getLayoutParams().height));
//			listItemsView.menuIcon.setBackground(new BitmapDrawable(bitmap));
			listItemsView.menuIcon.setImageBitmap(bitmap);
			LayoutParams layoutParams = listItemsView.menuIcon.getLayoutParams();
			layoutParams.height=(int)(1.618*parent.getLayoutParams().width);
			Log.d("test", layoutParams.toString());
			listItemsView.menuIcon.setLayoutParams(layoutParams);
			convertView.setTag(listItemsView);
		}
		else{
			listItemsView = (ListItemsView)convertView.getTag();
		}
		
		return convertView;
	} 
	
	
	
	private void init(Context con){
		
		this.itemCount = this.COUNT;
		this.listItems =  new ArrayList<Map<String, Object>>();
	
		for(int i = 0; i < this.itemCount; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			
					map.put("menuIcon", imageIds[i]);
					this.listItems.add(map);
		}
			
			
			
		
		this.listInflater = LayoutInflater.from(context); }
	}
