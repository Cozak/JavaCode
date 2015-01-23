package com.minius.common;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

import com.minius.ui.HeadPhotoHanddler;
import com.minus.lovershouse.MainActivity;
import com.minus.lovershouse.R;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.sql_interface.Database;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class CommonBitmap {

	//--------------------�ַ�����-----------------------------------
	public  static final int MYHEADPHO= 1;
	public  static final int TARHEADPHO = 2;
	public static final int ALBUMFIRSTPHO = 3;
	
	//-----------------------end--------------------------------
	
	private boolean isMyHeadPhotoInit = false;

	private boolean isTarHeadPhotoInit = false;
	
	private boolean isAlubmPhotoInit = false;
	private boolean hasPic = false;
	
	private  CommonBitmap(){
		  super();
		 
		  setDefault();
	}
	private void setDefault() {
		hashRefs = new Hashtable<Integer, MySoftRef>();

        q = new ReferenceQueue<Bitmap>();
        
        isMyHeadPhotoInit = false;

    	isTarHeadPhotoInit = false;
    	
    	isAlubmPhotoInit = false;
    	hasPic = false;
		
	}
	/**
	 * ����ģʽ,�߳�
	 * @return
	 */
		
		private static class  CommonBitmapContainer{
			private static CommonBitmap instance = new CommonBitmap();
		}
		
		public static CommonBitmap getInstance(){
		    
			  return  CommonBitmapContainer.instance;
		}
		
		
		//getter setter 
	
		public boolean isAlubmPhotoInit() {
			return isAlubmPhotoInit;
		}
		public boolean isMyHeadPhotoInit() {
			return isMyHeadPhotoInit;
		}
		public void setMyHeadPhotoInit(boolean isMyHeadPhotoInit) {
			this.isMyHeadPhotoInit = isMyHeadPhotoInit;
		}
		public boolean isTarHeadPhotoInit() {
			return isTarHeadPhotoInit;
		}
		public void setTarHeadPhotoInit(boolean isTarHeadPhotoInit) {
			this.isTarHeadPhotoInit = isTarHeadPhotoInit;
		}
		public void setAlubmPhotoInit(boolean isAlubmPhotoInit) {
			this.isAlubmPhotoInit = isAlubmPhotoInit;
		}
		
		
		
		 /** ����Chche���ݵĴ洢 */

	    private Hashtable<Integer, MySoftRef> hashRefs;

	    /** ����Reference�Ķ��У������õĶ����Ѿ������գ��򽫸����ô�������У� */

	    private ReferenceQueue<Bitmap> q;

	 

	    /**

	     * �̳�SoftReference��ʹ��ÿһ��ʵ�������п�ʶ��ı�ʶ��

	      */

	    private class MySoftRef extends SoftReference<Bitmap> {

	        private Integer _key = 0;

	 

	        public MySoftRef(Bitmap bmp, ReferenceQueue<Bitmap> q, int key) {

	            super(bmp, q);

	            _key = key;

	        }

	    }
		
	    /**

	     * �������õķ�ʽ��һ��Bitmap�����ʵ���������ò����������

	      */

	    public  void addCacheBitmap(Bitmap bmp, Integer key) {

	        cleanCache();// �����������

	         MySoftRef ref = new MySoftRef(bmp, q, key);

	        hashRefs.put(key, ref);

	    }

	 

	    /**
         * 
	     * ��ȡ��ҳ��� Bitmap�����ʵ��
	     * �����ҳ������Ƭ�����ڷ���null

	     */

	    public Bitmap getAlbumBitmap(Context ctx, String houseStyle) {
	        Bitmap bmp = null;
	        Bitmap content = null;
	        
	        boolean tempIsInit = this.isAlubmPhotoInit;

	      if(tempIsInit){
            if(hasPic){
        	// �������Ƿ��и�Bitmapʵ���������ã�����У�����������ȡ�á�
	           if (hashRefs.containsKey(ALBUMFIRSTPHO)) {
	              MySoftRef ref = (MySoftRef) hashRefs.get(ALBUMFIRSTPHO);
	              bmp = (Bitmap) ref.get();
	           }
            }else{
        	   
        	   return null;
            }
	      }

	        // ���û�������ã����ߴ��������еõ���ʵ����null�������û��ı�����ҳͼƬ�������¹���һ��ʵ����
	         // �����������½�ʵ����������

	         if (bmp == null ) {
	        	 boolean tempHavePic = false;
	     		GlobalApplication mIns = GlobalApplication.getInstance();
	     		String firstPicInitDate = mIns.getFirstPicture();
	     		String filePath = Database.getInstance(mIns.getApplicationContext())
	     				.getImageFilePathWithInitDate(firstPicInitDate);
	     		if (!(filePath.equals(""))) {
	     		 content = ImageLoader.getInstance().loadImageSync("file://"+filePath); 

				if (content != null) {
					tempHavePic = true;

				}
	     		}
	     		
	     		this.hasPic = tempHavePic;
	     		
	     		if (tempHavePic) {
	    			// album frame
	    			BitmapFactory.Options opt = new BitmapFactory.Options();
	    			opt.inPreferredConfig = Bitmap.Config.RGB_565;// ��ʾ16λλͼ
	    															// 565�����Ӧ��ԭɫռ��λ��
	    			opt.inInputShareable = true;
	    			opt.inSampleSize = 1;
	    			opt.inPurgeable = true;// ����ͼƬ���Ա�����
	    			
	    			String imageName = "main_album_" + houseStyle;
	    			int frameId =ctx.getResources().getIdentifier(imageName,
	    					"drawable", "com.minus.lovershouse"); // name:ͼƬ������defType����Դ���ͣ�drawable��string����������defPackage:���̵İ���
	    
	    			Bitmap albumFrame =BitmapFactory.decodeResource(ctx.getResources(),
	    					frameId, opt);

	    			int albumHeight = albumFrame.getHeight();
	    			int albumWidth = albumFrame.getWidth();
	    			opt = null;
	    			
	    			Bitmap  resizedPictureBitmap = Bitmap.createScaledBitmap(content,
	    					albumWidth - 50, albumHeight - 50, true);

	    			Bitmap m1 = (AppManagerUtil.mergeBitmap(albumFrame,
	    					resizedPictureBitmap, 25, 25));
	    			Matrix skewMatrix = new Matrix();
	    			// ��б�̶�
	    			float skew1 = 0f;
	    			float skew2 = 0.4f;
	    			skewMatrix.setSkew(skew1, skew2);

	    			bmp =Bitmap.createBitmap(m1, 0, 0, albumWidth,
	    					albumHeight, skewMatrix, true);

	    			resizedPictureBitmap = null;
	    			albumFrame = null;
	    			content = null;
	    		} 
	            this.addCacheBitmap(bmp, ALBUMFIRSTPHO);
	            this.isAlubmPhotoInit = true;
	        }
	        	return bmp;
	    }

	 

	    /**
         * 
	     * ��ȡ�Լ��� Bitmap�����ʵ��
	     * �����ҳ������Ƭ�����ڷ���null

	     */

	    public Bitmap getMyHeadBm() {
	    	
	        Bitmap bmp = null;
	    
	        boolean tempIsInit = this.isMyHeadPhotoInit;

	      if(tempIsInit){
         
        	// �������Ƿ��и�Bitmapʵ���������ã�����У�����������ȡ�á�
	           if (hashRefs.containsKey(MYHEADPHO)) {
	              MySoftRef ref = (MySoftRef) hashRefs.get(MYHEADPHO);
	              bmp = (Bitmap) ref.get();
	           }
           
	      }

	        // ���û�������ã����ߴ��������еõ���ʵ����null�������û�������ͷ�������¹���һ��ʵ����
	         // �����������½�ʵ����������

	         if (bmp == null) {
	        	 HeadPhotoHanddler mHeadPhotoHandler = new HeadPhotoHanddler();
	 			GlobalApplication mIns = GlobalApplication.getInstance();
	 			SelfInfo mSelfInfo = SelfInfo.getInstance();
	 			if (mSelfInfo.getSex().equals("b")) {
	 				Bitmap frameBm = BitmapFactory.decodeResource(mIns.getApplicationContext().getResources(),
	 						R.drawable.boy_photoframe);
	 				bmp = (mHeadPhotoHandler.handleHeadPhoto(mSelfInfo.getHeadpotoPath(), frameBm,
	 						mIns.getApplicationContext()));
	 				frameBm = null;
	 			} else {
	 				Bitmap frameBm = BitmapFactory.decodeResource(mIns.getApplicationContext().getResources(),
	 						R.drawable.girl_photoframe);
	 				bmp = (mHeadPhotoHandler.handleHeadPhoto(mSelfInfo.getHeadpotoPath(), frameBm,
	 						mIns.getApplicationContext()));
	 			
	 				frameBm = null;
	 			}
	 			
	            this.addCacheBitmap(bmp,MYHEADPHO);
	            this.isMyHeadPhotoInit = true;
	        }
	        	return bmp;
	    }
	    
	    


	    private void cleanCache() {

	        MySoftRef ref = null;

	        while ((ref = (MySoftRef) q.poll()) != null) {

	            hashRefs.remove(ref._key);

	        }

	    }

	 

	    /**

	     * ���Cache�ڵ�ȫ������

	     */

	    public void clearCache() {

	        cleanCache();

	        hashRefs.clear();

	        System.gc();

	        System.runFinalization();

	    }
	
}
