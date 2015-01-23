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

	//--------------------字符常量-----------------------------------
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
	 * 单例模式,线程
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
		
		
		
		 /** 用于Chche内容的存储 */

	    private Hashtable<Integer, MySoftRef> hashRefs;

	    /** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */

	    private ReferenceQueue<Bitmap> q;

	 

	    /**

	     * 继承SoftReference，使得每一个实例都具有可识别的标识。

	      */

	    private class MySoftRef extends SoftReference<Bitmap> {

	        private Integer _key = 0;

	 

	        public MySoftRef(Bitmap bmp, ReferenceQueue<Bitmap> q, int key) {

	            super(bmp, q);

	            _key = key;

	        }

	    }
		
	    /**

	     * 以软引用的方式对一个Bitmap对象的实例进行引用并保存该引用

	      */

	    public  void addCacheBitmap(Bitmap bmp, Integer key) {

	        cleanCache();// 清除垃圾引用

	         MySoftRef ref = new MySoftRef(bmp, q, key);

	        hashRefs.put(key, ref);

	    }

	 

	    /**
         * 
	     * 获取首页相册 Bitmap对象的实例
	     * 如果首页相册的照片不存在返回null

	     */

	    public Bitmap getAlbumBitmap(Context ctx, String houseStyle) {
	        Bitmap bmp = null;
	        Bitmap content = null;
	        
	        boolean tempIsInit = this.isAlubmPhotoInit;

	      if(tempIsInit){
            if(hasPic){
        	// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
	           if (hashRefs.containsKey(ALBUMFIRSTPHO)) {
	              MySoftRef ref = (MySoftRef) hashRefs.get(ALBUMFIRSTPHO);
	              bmp = (Bitmap) ref.get();
	           }
            }else{
        	   
        	   return null;
            }
	      }

	        // 如果没有软引用，或者从软引用中得到的实例是null，或者用户改变了首页图片，需重新构建一个实例，
	         // 并保存对这个新建实例的软引用

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
	    			opt.inPreferredConfig = Bitmap.Config.RGB_565;// 表示16位位图
	    															// 565代表对应三原色占的位数
	    			opt.inInputShareable = true;
	    			opt.inSampleSize = 1;
	    			opt.inPurgeable = true;// 设置图片可以被回收
	    			
	    			String imageName = "main_album_" + houseStyle;
	    			int frameId =ctx.getResources().getIdentifier(imageName,
	    					"drawable", "com.minus.lovershouse"); // name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
	    
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
	    			// 倾斜程度
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
	     * 获取自己的 Bitmap对象的实例
	     * 如果首页相册的照片不存在返回null

	     */

	    public Bitmap getMyHeadBm() {
	    	
	        Bitmap bmp = null;
	    
	        boolean tempIsInit = this.isMyHeadPhotoInit;

	      if(tempIsInit){
         
        	// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
	           if (hashRefs.containsKey(MYHEADPHO)) {
	              MySoftRef ref = (MySoftRef) hashRefs.get(MYHEADPHO);
	              bmp = (Bitmap) ref.get();
	           }
           
	      }

	        // 如果没有软引用，或者从软引用中得到的实例是null，或者用户更新了头像，需重新构建一个实例，
	         // 并保存对这个新建实例的软引用

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

	     * 清除Cache内的全部内容

	     */

	    public void clearCache() {

	        cleanCache();

	        hashRefs.clear();

	        System.gc();

	        System.runFinalization();

	    }
	
}
