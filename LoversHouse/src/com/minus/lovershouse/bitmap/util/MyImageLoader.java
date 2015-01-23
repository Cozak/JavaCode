package com.minus.lovershouse.bitmap.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.minus.lovershouse.BuildConfig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyImageLoader {
	
	private Map<String, Bitmap> bitmaps= new HashMap<String, Bitmap>();
	private ExecutorService executorService =  Executors.newFixedThreadPool(5); // �̶�����߳���ִ������
	private final Object _LOCK= new Object();
	private boolean isBusy= false;
	private List<Thread> waitThread= new ArrayList<Thread>();
	
	public void loadDrawable(boolean flag) {
		
		synchronized (_LOCK) {
			
			isBusy= flag;
		}
	}
	
	public void loadDrawable(final String imageUrl, final int width, final int height, final ImageCallBack callBack, final Handler handler, final boolean isCome) {
		
		if (!isBusy) {
			if(BuildConfig.DEBUG){
				Log.v("pop", " loadDrawable  !isBusy)");
			}
			if (bitmaps.containsKey(imageUrl)) {

				Bitmap bit = bitmaps.get(imageUrl);
				if (bit != null) {

					Bundle bundle = new Bundle();
					bundle.putString(Keys.PATH, imageUrl);
					Message msg = handler.obtainMessage();
					msg.obj = bit;
					msg.setData(bundle);
					handler.sendMessage(msg);
				} else {

					bitmaps.remove(imageUrl);
					// loadBitmap(imageUrl, width, height, handler);
				}
			} else {
				if(BuildConfig.DEBUG){
					Log.v("pop", " loadDrawable  !isBusy 001 )");
				}
				loadBitmap(imageUrl, width, height, handler,isCome);
			}
		}
	}
	
	public void shutDownThread() {
		
		if(!executorService.isShutdown()) {
			
			executorService.shutdownNow();
		}
	}
	
	Object obj= null;
	public void notifyAllWorkThread() {
		
		synchronized (obj) {
			
			for(int i= 0; i< waitThread.size(); i++) {

				waitThread.get(i).notify();
			}
		}
	}
	
	public void loadBitmap(final String imageUrl, final int width, final int height, final Handler handler,final boolean isCome) {

		// ������û��ͼ�����������ȡ�����ݣ�����ȡ�������ݻ��浽�ڴ���
		executorService.submit(new Thread() {
			public void run() {

				try {

					obj= new Object();
					synchronized (obj) {
						
						if(isBusy) {
							
							obj.wait();
							waitThread.add(this);
						}else {
							
							obj.notifyAll();
						}
						if(BuildConfig.DEBUG){
							Log.v("pop", "001");
						}
						final Bitmap drawable = loadImageFromUrl(imageUrl, width, height);
					    bitmaps.put(imageUrl, drawable);
						Message msg = handler.obtainMessage();
						Bundle bundle = new Bundle();
						bundle.putString(Keys.PATH, imageUrl);
						msg.setData(bundle);
						msg.obj = drawable;
						if(isCome){
						msg.arg1= 1;
						}else{
							msg.arg1 = 0;
						}
						handler.sendMessage(msg);
					}
				} catch (Exception e) {

					throw new RuntimeException(e);
				}
			}
		});
	}

	// ��������ȡ���ݷ���
	protected Drawable loadImageFromUrl(String imageUrl, boolean flag) {
		try {

//			return Drawable.createFromStream(new URL(imageUrl).openStream(),
//					"image.png");
			
			return Drawable.createFromPath(imageUrl);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Bitmap loadImageFromUrl(String path, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull
		if(BuildConfig.DEBUG){
			Log.v("pop", "002");
		}
		bitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // ��Ϊ false
		// �������ű�
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false
		bitmap = BitmapFactory.decodeFile(path, options);
		if(BuildConfig.DEBUG){
			Log.v("pop", "003");
		}
		// ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		if(BuildConfig.DEBUG){
			Log.v("pop", "004 " + bitmap.getHeight());
		}
		return bitmap;
	}
	
	public interface ImageCallBack {
		
		public void imageLoaded(final Bitmap drawable);
	}

}