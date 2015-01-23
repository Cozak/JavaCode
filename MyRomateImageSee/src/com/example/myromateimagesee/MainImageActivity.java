package com.example.myromateimagesee;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainImageActivity extends Activity {
	private ImageSource IS = new ImageSource();
	private ListView mylv = null;
	private SThread mST = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.initAct();
		this.setContentView(this.mylv);
		
		this.mST = new SThread(this.IS);
		this.mST.start();
	}
	
	private void initAct() {
		ImageSource.sourceLoad(this.IS);
		ImageSource.aM = this.getAssets();
		this.IS.IA = new ItemAdapter(MainImageActivity.this, this.IS);
		mylv = new ListView(this);
		mylv.setAdapter(this.IS.IA);
	}
	
	@Override
	protected void onDestroy() {
		
		if (this.mST.getSThread() != null) {
			this.mST.getSThread().getLooper().quit();
		}
		super.onDestroy();
	}
	
	public static class SThread extends Thread {
		private ImageSource iS = null;
		private Handler mhd = null;
		private Handler mainHd = new Handler(Looper.getMainLooper(), new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				SThread.this.iS.IA.notifyDataSetChanged();
				return true;
			}
		});
		public SThread(ImageSource is) {
			this.iS = is;
		}
		
		public Handler getSThread() {
			return this.mhd;
		}
		
		@Override
		public void run() {
			Looper.prepare();
			this.mhd = new Handler(Looper.myLooper());
			bitmapLoading(this.mainHd, this.mhd, this.iS);
			Looper.loop();
		}
		
		private static void bitmapLoading(Handler mainHd, Handler mhd, ImageSource iS) {
			if (mhd == null) {
				try {
					throw new Exception();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				for (int i = 0; i < iS.files.size(); ++i) {
					mhd.sendMessage(Message.obtain(mhd, new RunTask(mainHd, iS.files.get(i), iS.bms, i)));
				}
				//mhd.getLooper().quit();
			}
		}
		
		public static class RunTask implements Runnable {
			private Handler mHd = null;
			private String file = null;
			private Bitmap[] bmp = null;
			private int index;
			public RunTask(Handler hd, String fs, Bitmap[] bs, int index) {
				this.mHd = hd;
				this.file = fs;
				this.bmp = bs;
				this.index = index;
			}
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//this.bmp[index] = BitmapFactory.decodeFile(file);
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				try {
					this.bmp[index] = BitmapFactory.decodeStream(ImageSource.aM.open(this.file));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mHd.sendEmptyMessage(this.index);
			}
		}
	}

	
	public static class ItemAdapter extends BaseAdapter {
		private Context mCox = null;
		private LayoutInflater mLif = null;
		private ImageSource iS = null;
		public ItemAdapter (Context cox, ImageSource is) {
			this.mCox = cox;
			this.mLif = LayoutInflater.from(mCox);
			this.iS = is;
			if (this.iS.files.size() == 0) {
				try {
					throw new Exception();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				this.iS.bms = new Bitmap[this.iS.files.size()];
				Log.i("=====>", "some files");
			}
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.iS.files.size();
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
			// TODO Auto-generated method stub
			Log.i("Item-"+position, " "+convertView);
			ViewCache vc = null;
			if (convertView == null) {
				convertView = this.mLif.inflate(R.layout.activity_main, null);
				vc = new ViewCache();
				vc.iv = (ImageView)convertView.findViewById(R.id.item_img);
				vc.tv = (TextView)convertView.findViewById(R.id.item_tv);
				convertView.setTag(vc);
			} else {
				vc = (ViewCache)convertView.getTag();
			}
			
			vc.tv.setText("NO."+position);
			if (this.iS.bms[position] == null) {
				this.iS.bms[position] = BitmapFactory.decodeResource(mCox.getResources(), R.drawable.pict_default);
			}
			vc.iv.setImageBitmap(this.iS.bms[position]);
			
			return convertView;
		}
		
		private static class ViewCache {
			public TextView tv = null;
			public ImageView iv = null;
		}
		
	}
	
	public static class ImageSource {
		//private static final String repath = "\\res\\drawable";
		public static AssetManager aM = null;
		public ItemAdapter IA = null;
		public ArrayList<String> files = new ArrayList<String>();
		public Bitmap[] bms = null;
		public static void sourceLoad(ImageSource is) {
			// collect the files' name which can be showed out
			//........
			for (int i = 0; i < 9; ++i) {
				is.files.add("p_"+(i+1)+".jpg");
			}
		}
	}
	
}


