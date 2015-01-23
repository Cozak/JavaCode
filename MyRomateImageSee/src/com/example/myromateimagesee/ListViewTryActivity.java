package com.example.myromateimagesee;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewTryActivity extends ListActivity {
	private TestAdapter myadapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setListAdapter(new TestAdapter(this));
	}
	
	public static class TestAdapter extends BaseAdapter {
		private ArrayList<String> strs = null;
		private Context mCox = null;
		public TestAdapter(Context cox) {
			this.mCox = cox;
			this.strs = new ArrayList<String>();
			for (int i = 1; i <= 50; ++i) {
				this.strs.add("Item" + i);
			}
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.strs.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i("Item-"+position, " "+convertView);
			if (convertView == null) {
				convertView = LayoutInflater.from(mCox).inflate(R.layout.activity_list_view_try, null);
				ViewCache tmp = new ViewCache();
				tmp.tv = (TextView)convertView.findViewById(R.id.test_listview_item);
				convertView.setTag(tmp);
			}
			ViewCache vc = (ViewCache)convertView.getTag();
			vc.tv.setText(this.strs.get(position));
			return convertView;
		}
		
	}
	
	public static class ViewCache {
		public TextView tv = null;
	} 
}
