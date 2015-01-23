package com.minus.cropimage;


import com.minus.lovershouse.setting.ConfigActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.RegisterActivity;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

public class CropActivity extends Activity {
//	private ImageView ivImage;

	private CaptureView mCaptureView;
	private Button btnCrop;
    private Button btnCancel;
	private Bitmap mBitmap;
	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Intent backIntent=null;
//			GlobalApplication.getInstance().setHeadPicBm((Bitmap)msg.obj);
			if(flag=="0")
				backIntent= new Intent(CropActivity.this,RegisterActivity.class);
			else
				backIntent= new Intent(CropActivity.this,ConfigActivity.class);
			setResult(1,backIntent);
			CropActivity.this.finish();
			
		}

	};
	private String flag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cropheadpic);
//		ivImage = (ImageView) this.findViewById(R.id.iv_image);
     
		 Intent mIntent = this.getIntent();
		 String picPath = mIntent.getStringExtra("picPath");
		mBitmap = BitmapFactory
				.decodeFile(picPath);
		flag=mIntent.getStringExtra("flag");
		
//		ivImage.setImageBitmap(mBitmap);
		mCaptureView = (CaptureView) this.findViewById(R.id.capture);
		mCaptureView.setImageBitmap(mBitmap);
		btnCrop = (Button) this.findViewById(R.id.btn_crop);
		btnCrop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Runnable crop = new Runnable() {
					public void run() {
						Message msg = Message.obtain(mHandler);
						msg.obj = cropImage();
						
//						AppManagerUtil.writeToSD("/crop",(Bitmap)msg.obj,"l");
						msg.sendToTarget();
					}
				};
				startBackgroundJob("截图", "处理中", crop, mHandler);
			}
		});
		
		btnCancel = (Button) this.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent backIntent=null;
				if(flag=="0")
					backIntent= new Intent(CropActivity.this,RegisterActivity.class);
				else
					backIntent= new Intent(CropActivity.this,ConfigActivity.class);
				setResult(0,backIntent);
				CropActivity.this.finish();
			}
		});
		PushAgent.getInstance(this).onAppStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		MobclickAgent.onResume(this);
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	private Bitmap cropImage(){
		Rect cropRect = mCaptureView.getCaptureRect();
		int width = cropRect.width();
		int height = cropRect.height();

		Bitmap croppedImage = Bitmap.createBitmap(width,
				height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(croppedImage);
		Rect dstRect = new Rect(0, 0, width, height);

		// 调整图片显示比例
		mBitmap = regulationBitmap(mBitmap);

		canvas.drawBitmap(mBitmap, cropRect, dstRect, null);
		return croppedImage;
	}
	// ImageView中的图像是跟实际的图片有比例缩放，因此需要调整图片比例
	private Bitmap regulationBitmap(Bitmap bitmap) {
		int ivWidth = mCaptureView.getWidth();
		int ivHeight = mCaptureView.getHeight();

		int bmpWidth = bitmap.getWidth();
		int bmpHeight = bitmap.getHeight();

		// 宽和高的比例
		float scaleWidth = (float) ivWidth / bmpWidth;
		float scaleHeight = (float) ivHeight / bmpHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth,
				bmpHeight, matrix, true);

		return resizeBmp;
	}

	public void startBackgroundJob(String title, String message, Runnable job,
			Handler handler) {
		ProgressDialog dialog = ProgressDialog.show(this, title, message, true,
				false);
		new Thread(new CropJob(job, dialog)).start();
	}

	private class CropJob implements Runnable {

		private final ProgressDialog mDialog;
		private final Runnable mJob;

		public CropJob(Runnable job, ProgressDialog dialog) {
			mDialog = dialog;
			mJob = job;
		}

		public void run() {
			try {
				mJob.run();
			} finally {
				if (mDialog.getWindow() != null)
					mDialog.dismiss();
			}
		}
	}
	
	
}