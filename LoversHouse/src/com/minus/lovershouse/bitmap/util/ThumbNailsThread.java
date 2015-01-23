package com.minus.lovershouse.bitmap.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;

public class ThumbNailsThread extends Thread {

	
	private String path;
	private int width;
	private int height;
	private Bitmap bitmap;
	private Handler handler;
	public ThumbNailsThread(Handler handler, String path, int width, int height) {
		
		this.path= path;
		this.height= height;
		this.width= width;
		this.handler= handler;
	}
	@Override
	public synchronized void start() {
		super.start();

		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull
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
		// ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		Message msg= new Message();
		msg.obj= bitmap;
		handler.sendMessage(msg);
	}
	
	/**
	 * ����ָ����ͼ��·���ʹ�С����ȡ����ͼ
	 * �˷���������ô���
	 *     1. ʹ�ý�С���ڴ�ռ䣬��һ�λ�ȡ��bitmapʵ����Ϊnull��ֻ��Ϊ�˶�ȡ��Ⱥ͸߶ȣ�
	 *        �ڶ��ζ�ȡ��bitmap�Ǹ��ݱ���ѹ������ͼ�񣬵����ζ�ȡ��bitmap����Ҫ������ͼ��
	 *     2. ����ͼ����ԭͼ������û�����죬����ʹ����2.2�汾���¹���ThumbnailUtils��ʹ
	 *        ������������ɵ�ͼ�񲻻ᱻ���졣
	 * @param imagePath ͼ���·��
	 * @param width ָ�����ͼ��Ŀ��
	 * @param height ָ�����ͼ��ĸ߶�
	 * @return ���ɵ�����ͼ
	 */
	public Bitmap getImageThumbnail(String imagePath, int width, int height) {
		
		return bitmap;
	}

	/**
	 * ��ȡ��Ƶ������ͼ
	 * ��ͨ��ThumbnailUtils������һ����Ƶ������ͼ��Ȼ��������ThumbnailUtils������ָ����С������ͼ��
	 * �����Ҫ������ͼ�Ŀ�͸߶�С��MICRO_KIND��������Ҫʹ��MICRO_KIND��Ϊkind��ֵ���������ʡ�ڴ档
	 * @param videoPath ��Ƶ��·��
	 * @param width ָ�������Ƶ����ͼ�Ŀ��
	 * @param height ָ�������Ƶ����ͼ�ĸ߶ȶ�
	 * @param kind ����MediaStore.Images.Thumbnails���еĳ���MINI_KIND��MICRO_KIND��
	 *            ���У�MINI_KIND: 512 x 384��MICRO_KIND: 96 x 96
	 * @return ָ����С����Ƶ����ͼ
	 */
	private Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		// ��ȡ��Ƶ������ͼ
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		System.out.println("w"+bitmap.getWidth());
		System.out.println("h"+bitmap.getHeight());
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
	
	
	
	
}