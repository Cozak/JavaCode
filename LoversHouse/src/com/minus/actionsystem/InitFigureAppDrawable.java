package com.minus.actionsystem;

import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minus.lovershouse.R;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.xsocket.asynsocket.protocol.Protocol;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class InitFigureAppDrawable {
	/*
	 * 策略 第一次登陆成功后，根据status生成对应的drawable
	 * 当且仅当自己主动改变appear或者对方改变对应appearance时重新生成bitmap， 更新对应图片
	 */
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	public Map<String, LayerDrawableSoftRef> mImageCacheMap = 
			null;
	
	public Map<String, ViewSoftRef> mVGCacheMap = 
			null;
	
    /** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
    private ReferenceQueue<LayerDrawable> q = null;
    
    /** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
    private ReferenceQueue<View> VGq = null;
    
    public static final String mainGirlCacheName = "mainGirl";
    public static final String mainBoyCacheName = "mainBoy";
    
    public static final  String mainMissGirlCacheName = "mainMissGirl";
    public static final String mainMissBoyCacheName = "mainMissBoy";
    
    public static final String mainAngryGirlCacheName = "mainAngryGirl";
    public static final String mainAngryBoyCacheName = "mainAngryBoy";
    
    public static final String mainEatGirlCacheName = "mainEatGirl";
    public static final  String mainEatBoyCacheName = "mainEatBoy";
    
    public static final  String mainLearnGirlCacheName = "mainLearnGirl";
    public static final String mainLearnBoyCacheName = "mainLearnBoy";
    
    public static final String abuseCacheName = "abuse";
    public static final String girlPFaceCacheName = "girlpface";
    public static final String boyPFaceCacheName = "boypface";
    public static final String girlPHeadCacheName = "girlpHead";
    public static final String boyPHeadCacheName = "boypHead";
    public static final String hugCacheName = "hug";
    public static final String kissCacheName = "kiss";
    
 
    
	//主界面人物状态
//	public static final int boyStaticStaus = 0;
//	public static final int boyStandStaus = 1;
//	public static final int boySitStaus = 2;
//	public static final int boySleepStaus = 3;
//	
//	public static final int girlStaticStaus = 10;
//	public static final int girlStandStaus = 11;
//	public static final int girlSitStaus = 12;
//	public static final int girlSleepStaus = 13;
	
	DisplayImageOptions options;
	private int screenWith;
	private int screenHeight;
	private String path = null;
	private String girlAppearance = null;
	private boolean isGirlInit = false;
	private boolean isGirlEatingInit = false;
	private boolean isGirlStudyInit = false;
	private boolean isGirlAngryInit = false;
	private boolean isGirlMissInit = false;

	private String boyAppearance = null;
	private boolean isBoyInit = false;
	private boolean isBoyEatingInit = false;
	private boolean isBoyStudyInit = false;
	private boolean isBoyAngryInit = false;
	private boolean isBoyMissInit = false;

	// ------------couple

	private boolean isAbuseInit = false;
	private boolean isPFHBoyInit = false;
	private boolean isPFHGirlInit = false;
	private boolean isKissInit = false;
	private boolean isHugInit = false;

	private InitFigureAppDrawable() {
		super();
		setDefault();
		options = new DisplayImageOptions.Builder().cacheInMemory(false)
				.cacheOnDisk(false).considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		mImageCacheMap = new HashMap<String,LayerDrawableSoftRef>();
        q = new ReferenceQueue<LayerDrawable>();
        
        mVGCacheMap = new HashMap<String,ViewSoftRef>();
         VGq = new ReferenceQueue<View>();
		screenWith = GlobalApplication.getInstance().getScreenWidth();
		screenHeight = GlobalApplication.getInstance().getScreenHeigh();
		path = "/actiontemp";
	}

	/**
	 * 单例模式,线程
	 * 
	 * @return
	 */

	private static class InitFigureAppDrawableContainer {
		private static InitFigureAppDrawable instance = new InitFigureAppDrawable();
	}

	public static InitFigureAppDrawable getInstance() {

		return InitFigureAppDrawableContainer.instance;
	}

	// 设置女生的形象，表明形象有更新。
	public void resetGirlAppearance(String girlAppearance) {
		this.girlAppearance = girlAppearance;
		isGirlInit = false;
		isGirlEatingInit = false;
		isGirlStudyInit = false;
		isGirlAngryInit = false;
		isGirlMissInit = false;

		isAbuseInit = false;
		isPFHBoyInit = false;
		isPFHGirlInit = false;
		isKissInit = false;
		isHugInit = false;

	}

	// 设置男生的形象，表明形象有更新。
	public void resetBoyAppearance(String boyAppearance) {
		this.boyAppearance = boyAppearance;
		isBoyInit = false;
		isBoyEatingInit = false;
		isBoyStudyInit = false;
		isBoyAngryInit = false;
		isBoyMissInit = false;

		isAbuseInit = false;
		isPFHBoyInit = false;
		isPFHGirlInit = false;
		isKissInit = false;
		isHugInit = false;

	}

	private void setDefault() {
		this.girlAppearance = String.format("%c%c%c%c", Protocol.DEFAULT,
				Protocol.DEFAULT, Protocol.DEFAULT, Protocol.DEFAULT);
		isGirlInit = false;
		isGirlEatingInit = false;
		isGirlStudyInit = false;
		isGirlAngryInit = false;
		isGirlMissInit = false;
		this.boyAppearance = String.format("%c%c%c%c", Protocol.DEFAULT,
				Protocol.DEFAULT, Protocol.DEFAULT, Protocol.DEFAULT);
		;
		isBoyInit = false;
		isBoyEatingInit = false;
		isBoyStudyInit = false;
		isBoyAngryInit = false;
		isBoyMissInit = false;

		isAbuseInit = false;
		isPFHBoyInit = false;
		isPFHGirlInit = false;
		isKissInit = false;
		isHugInit = false;
	}

	public int getImageid(String imageName, Context ctx) {
		int mImageid = ctx.getResources().getIdentifier(imageName, "drawable",
				"com.minus.lovershouse");
		ctx = null;
		// name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
		return mImageid;
	}
	
	//--------------------------view group  start--------------
    /**
     * 继承SoftReference，使得每一个实例都具有可识别的标识。key的名字与layerdrawable一致
      */
    private class ViewSoftRef extends SoftReference<View> {
        private String _key = "";

        public ViewSoftRef(View mVG, ReferenceQueue<View> q, String key) {
            super(mVG, q);
            _key = key;
        }
    }

    /**
     * 先判断缓存是否存在。如果返回null证明不存在
     */
    public View getViewGroupFormCache(String key) {
    	View mVG = null;
        // 缓存中是否有该实例的软引用，如果有，从软引用中取得。
         if (mVGCacheMap.containsKey(key)) {
        	 ViewSoftRef ref = (ViewSoftRef) mVGCacheMap.get(key);
              mVG = (View) ref.get();
        }
        
        return mVG;
    }
    /**
     * 以软引用的方式对一个viewgroup对象的实例进行引用并保存该引用
      */
    private void addCacheVG(View mVG, String key) {
        cleanVGCache();// 清除垃圾引用
        ViewSoftRef ref = new ViewSoftRef(mVG,VGq,key);
        mVGCacheMap.put(key, ref);
        mVG = null;
      
    }
    

    @SuppressWarnings("unchecked")
	private void cleanVGCache() {
    	ViewSoftRef ref = null;
        while ((ref = (ViewSoftRef) VGq.poll()) != null) {
        	mVGCacheMap.remove(ref._key);
        }
    }

    /**
     * 清除Cache内的全部内容
     */
    public void clearVGCache() {
        cleanVGCache();
        mVGCacheMap.clear();
        System.gc();
        System.runFinalization();
    }
    //------------------------------  ViewGroup    end --------------
    
    
    //----------------------------- layerDrawable  start-------------
	
    /**
     * 继承SoftReference，使得每一个实例都具有可识别的标识。
      */
    private class LayerDrawableSoftRef extends SoftReference<LayerDrawable> {
        private String _key = "";

        public LayerDrawableSoftRef(LayerDrawable bmp, ReferenceQueue<LayerDrawable> q, String key) {
            super(bmp, q);
            _key = key;
        }
    }
    
    /**
     * 先判断缓存是否存在。如果返回null证明不存在
     */
    public LayerDrawable getLayerDrawableFormCache(String key, Context context) {
    	LayerDrawable mLD = null;
        // 缓存中是否有该实例的软引用，如果有，从软引用中取得。
         if (mImageCacheMap.containsKey(key)) {
        	 LayerDrawableSoftRef ref = (LayerDrawableSoftRef) mImageCacheMap.get(key);
              mLD = (LayerDrawable) ref.get();
        }
        
        return mLD;
    }
    /**
     * 以软引用的方式对一个layerDrawable对象的实例进行引用并保存该引用
      */
    private LayerDrawableSoftRef addCacheLD(LayerDrawable mLD, String key) {
        cleanCache();// 清除垃圾引用
        LayerDrawableSoftRef ref = new LayerDrawableSoftRef(mLD,q,key);
        mImageCacheMap.put(key, ref);
        mLD = null;
        return ref;
    }
    

    @SuppressWarnings("unchecked")
	private void cleanCache() {
    	LayerDrawableSoftRef ref = null;
        while ((ref = (LayerDrawableSoftRef) q.poll()) != null) {
        	mImageCacheMap.remove(ref._key);
        }
    }

    /**
     * 清除Cache内的全部内容
     */
    public void clearCache() {
        cleanCache();
        mImageCacheMap.clear();
        System.gc();
        System.runFinalization();
    }

	// --------------------------------------------------girl-----------------------------------------------------
	private int[] girlAppID = { 0, 0, 0, 0 };

	public LayerDrawable initGirl(Context ctx) {

		
		boolean isGirlInitTemp = isGirlInit;
		LayerDrawable myHeadLayerDrawable = null;
		Resources r = ctx.getResources();
	

		if (!(isGirlInitTemp) || girlAppID.length != 4) {
			String hairImageName = "girl_hair_to_left"
					+ (byte) girlAppearance.charAt(1);
			String clothesImageName = "girl_clothes_to_left"
					+ (byte) girlAppearance.charAt(2);
			String decorationImageName = "girl_decoration_to_left"
					+ (byte) girlAppearance.charAt(3);

			int bodyid = R.drawable.girl_portrait_body;

			int hairImageid = getImageid(hairImageName, ctx);

			int clothesImageid = getImageid(clothesImageName, ctx);

			int decorationImageid = getImageid(decorationImageName, ctx);

			if (hairImageid == 0) {
				hairImageid = R.drawable.girl_hair_to_left1;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.girl_clothes_to_left1;
			}
			if (decorationImageid == 0) {
				decorationImageid = R.drawable.girl_decoration_to_left1;
			}
			girlAppID[0] = bodyid;
			girlAppID[1] = hairImageid;
			girlAppID[2] = clothesImageid;
			girlAppID[3] = decorationImageid;

			isGirlInit = true;
		}
		//如果有初始化过 就先访问软缓存
        if(isGirlInitTemp){
        	myHeadLayerDrawable = getLayerDrawableFormCache(mainGirlCacheName,ctx);
        }
        //如果缓存存在直接返回
        if(myHeadLayerDrawable != null){
        	return myHeadLayerDrawable;
        }
    	Drawable[] mylayers = new Drawable[4];
		mylayers[0] = r.getDrawable(girlAppID[0]);
		mylayers[1] = r.getDrawable(girlAppID[1]);
		mylayers[2] = r.getDrawable(girlAppID[2]);
		mylayers[3] = r.getDrawable(girlAppID[3]);

		myHeadLayerDrawable = new LayerDrawable(mylayers);
		
		//缓存对象
		LayerDrawableSoftRef ref = addCacheLD(myHeadLayerDrawable, mainGirlCacheName);
		myHeadLayerDrawable = null;
		

		return ref.get();
	}

	// -----------------------------------------boy------------------------------------------------
	private int[] boyAppID = { 0, 0, 0, 0 };

	public LayerDrawable initBoy(Context ctx) {

		boolean isBoylInitTemp = isBoyInit;
		LayerDrawable myLayerDrawable = null;
		Resources r = ctx.getResources();
		

		if (!(isBoylInitTemp) || boyAppID.length != 4) {
			String hairImageName = "boy_hair_to_right"
					+ (byte) boyAppearance.charAt(1);
			String clothesImageName = "boy_clothes_to_right"
					+ (byte) boyAppearance.charAt(2);
			String decorationImageName = "boy_decoration_to_right"
					+ (byte) boyAppearance.charAt(3);

			int bodyid = R.drawable.boy_portrait_body;
			int hairImageid = getImageid(hairImageName, ctx);
			int clothesImageid = getImageid(clothesImageName, ctx);
			int decorationImageid = getImageid(decorationImageName, ctx);

			if (hairImageid == 0) {
				hairImageid = R.drawable.girl_hair_to_left1;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.girl_clothes_to_left1;
			}
			if (decorationImageid == 0) {
				decorationImageid = R.drawable.girl_decoration_to_left1;
			}
			boyAppID[0] = bodyid;
			boyAppID[1] = hairImageid;
			boyAppID[2] = clothesImageid;
			boyAppID[3] = decorationImageid;

			isBoyInit = true;
		}
		//如果有初始化过 就先访问软缓存
        if(isBoylInitTemp){
        	myLayerDrawable= getLayerDrawableFormCache(mainBoyCacheName,ctx);
        }
        //如果缓存存在直接返回
        if(myLayerDrawable != null){
        	return myLayerDrawable;
        }
        Drawable[] mylayers = new Drawable[4];
		mylayers[0] = r.getDrawable(boyAppID[0]);
		mylayers[1] = r.getDrawable(boyAppID[1]);
		mylayers[2] = r.getDrawable(boyAppID[2]);
		mylayers[3] = r.getDrawable(boyAppID[3]);

		myLayerDrawable = new LayerDrawable(mylayers);
		//缓存对象
		LayerDrawableSoftRef ref = addCacheLD(myLayerDrawable, mainBoyCacheName);
		myLayerDrawable = null;
		return ref.get();	
	}

	// ---------------------------------------girl eat
	// ---------------------------------------------
	private int[] girlEatAppID = { 0, 0, 0, 0, 0, 0, 0 };

	public LayerDrawable initGirlEatting(Context ctx) {
		boolean isGirlInitTemp = this.isGirlEatingInit;
		LayerDrawable myLayerDrawable = null;
		Resources r = ctx.getResources();

		if (!(isGirlInitTemp) || girlEatAppID.length != 7) {
			String hairImageName = "girl_eat_hair0"
					+ (byte) girlAppearance.charAt(1);
			String clothesImageName = "girl_eat_clothes"
					+ (byte) girlAppearance.charAt(2);
			String decorationImageName = "girl_eat_decoraion"
					+ (byte) girlAppearance.charAt(3);

			// 固定id
			int eatChairId = R.drawable.girl_eat_chair;
			int eatBodyId = R.drawable.girl_eat_body;
			int eatFaceId = R.drawable.girl_eat_face;
			int eatRightHandId = R.drawable.girl_eat_righthand;

			int hairImageid = getImageid(hairImageName, ctx);
			int clothesImageid = getImageid(clothesImageName, ctx);
			int decorationImageid = getImageid(decorationImageName, ctx);
			if (hairImageid == 0) {
				hairImageid = R.drawable.girl_eat_hair01;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.girl_eat_clothes1;
			}
			if (decorationImageid == 0) {
				decorationImageid = R.drawable.girl_eat_decoraion1;
			}

			girlEatAppID[0] = eatChairId;
			
			girlEatAppID[1] = eatFaceId;
			girlEatAppID[2] = hairImageid;
			girlEatAppID[3] = eatBodyId;

			girlEatAppID[4] = clothesImageid;
			girlEatAppID[5] = decorationImageid;
			girlEatAppID[6] = eatRightHandId;

			isGirlEatingInit = true;
		}
		//如果有初始化过 就先访问软缓存
        if(isGirlInitTemp){
        	myLayerDrawable= getLayerDrawableFormCache(mainEatGirlCacheName,ctx);
        }
        //如果缓存存在直接返回
        if(myLayerDrawable != null){
        	return myLayerDrawable;
        }
    	Drawable[] mylayers = new Drawable[7];
		mylayers[0] = r.getDrawable(girlEatAppID[0]);
		mylayers[1] = r.getDrawable(girlEatAppID[1]);
		mylayers[2] = r.getDrawable(girlEatAppID[2]);
		mylayers[3] = r.getDrawable(girlEatAppID[3]);
		mylayers[4] = r.getDrawable(girlEatAppID[4]);
		mylayers[5] = r.getDrawable(girlEatAppID[5]);
		mylayers[6] = r.getDrawable(girlEatAppID[6]);

		myLayerDrawable = new LayerDrawable(mylayers);
		//缓存对象
				LayerDrawableSoftRef ref = addCacheLD(myLayerDrawable, mainEatGirlCacheName);
				myLayerDrawable = null;
				return ref.get();	
	}

	// ---------------------------------------girl
	// study---------------------------------------------
	private int[] girlStudyAppID = { 0, 0, 0, 0, 0, 0 };

	public LayerDrawable initGirlStudy(Context ctx) {
		boolean isGirlInitTemp = this.isGirlStudyInit;
		LayerDrawable myLayerDrawable = null;
		Resources r = ctx.getResources();
		
		if (!(isGirlInitTemp)) {
			String hairImageName = "girl_learn_hair0"
					+ (byte) girlAppearance.charAt(1);
			String clothesImageName = "girl_learn_clothes"
					+ (byte) girlAppearance.charAt(2);
			String decorationImageName = "girl_learn_decoration"
					+ (byte) girlAppearance.charAt(3);

			// 固定id
			int studyChairId = R.drawable.girl_learn_chair;
			int studyBodyId = R.drawable.girl_learn_body;
			int studyFaceId = R.drawable.girl_learn_face;

			int hairImageid = getImageid(hairImageName, ctx);
			int clothesImageid = getImageid(clothesImageName, ctx);
			int decorationImageid = getImageid(decorationImageName, ctx);
			if (hairImageid == 0) {
				hairImageid = R.drawable.girl_eat_hair01;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.girl_learn_clothes1;
			}
			if (decorationImageid == 0) {
				decorationImageid = R.drawable.girl_learn_decoration1;
			}
			girlStudyAppID[0] = studyChairId;
			girlStudyAppID[1] = studyFaceId;
			girlStudyAppID[2] = studyBodyId;
			girlStudyAppID[3] = hairImageid;

			girlStudyAppID[4] = clothesImageid;
			girlStudyAppID[5] = decorationImageid;

			isGirlStudyInit = true;
		}
		
		//如果有初始化过 就先访问软缓存
        if(isGirlInitTemp){
        	myLayerDrawable= getLayerDrawableFormCache(mainLearnGirlCacheName,ctx);
        }
        //如果缓存存在直接返回
        if(myLayerDrawable != null){
        	return myLayerDrawable;
        }
        Drawable[] mylayers = new Drawable[6];
		mylayers[0] = r.getDrawable(girlStudyAppID[0]);
		mylayers[1] = r.getDrawable(girlStudyAppID[1]);
		mylayers[2] = r.getDrawable(girlStudyAppID[2]);
		mylayers[3] = r.getDrawable(girlStudyAppID[3]);
		mylayers[4] = r.getDrawable(girlStudyAppID[4]);
		mylayers[5] = r.getDrawable(girlStudyAppID[5]);

		myLayerDrawable = new LayerDrawable(mylayers);

		//缓存对象
		LayerDrawableSoftRef ref = addCacheLD(myLayerDrawable,mainLearnGirlCacheName);
		myLayerDrawable = null;
		return ref.get();	
		

	}

	// ---------------------------------------girl
	// angry------------------------------不需要头饰---------------
	private int[] girlAngryAppID = { 0, 0, 0};

	public LayerDrawable initGirlAngry(Context ctx) {

		boolean isGirlInitTemp = this.isGirlAngryInit;
		LayerDrawable myLayerDrawable = null;
		Resources r = ctx.getResources();
		
		if (!(isGirlInitTemp)) {

			String hairImageName = "girl_back_hair0"
					+ (byte) girlAppearance.charAt(1);
			String clothesImageName = "girl_angry_cloth"
					+ (byte) girlAppearance.charAt(2);
//			String decorationImageName = "girl_angry_deco"
//					+ (byte) girlAppearance.charAt(3);

			// 固定id
			int angryBodyId = R.drawable.girl_body_angry;
			int hairImageid = getImageid(hairImageName, ctx);
			int clothesImageid = getImageid(clothesImageName, ctx);
//			int decorationImageid = getImageid(decorationImageName, ctx);
			if (hairImageid == 0) {
				hairImageid = R.drawable.girl_back_hair01;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.girl_angry_cloth1;
			}
//			if (decorationImageid == 0) {
//				decorationImageid = R.drawable.girl_angry_deco9;
//			}

			girlAngryAppID[0] = angryBodyId;
			girlAngryAppID[1] = hairImageid;
			girlAngryAppID[2] = clothesImageid;
//			girlAngryAppID[3] = decorationImageid;

			this.isGirlAngryInit = true;
		}
		//如果有初始化过 就先访问软缓存
        if(isGirlInitTemp){
        	myLayerDrawable= getLayerDrawableFormCache(mainAngryGirlCacheName,ctx);
        }
        //如果缓存存在直接返回
        if(myLayerDrawable != null){
        	return myLayerDrawable;
        }
        Drawable[] mylayers = new Drawable[3];
		mylayers[0] = r.getDrawable(girlAngryAppID[0]);
		mylayers[1] = r.getDrawable(girlAngryAppID[1]);
		mylayers[2] = r.getDrawable(girlAngryAppID[2]);
//		mylayers[3] = r.getDrawable(girlAngryAppID[3]);

		myLayerDrawable = new LayerDrawable(mylayers);

		//缓存对象
		LayerDrawableSoftRef ref = addCacheLD(myLayerDrawable,mainAngryGirlCacheName);
		myLayerDrawable = null;
		return ref.get();	

	}

	// ---------------------------------------girl
	// missing---------------------------------------------
	private int[] girlMissAppID = { 0, 0, 0, 0, 0, 0 };

	public LayerDrawable initGirlMiss(Context ctx) {

		boolean isGirlInitTemp = this.isGirlMissInit;
		LayerDrawable myLayerDrawable = null;
		Resources r = ctx.getResources();
	
		if (!(isGirlInitTemp)) {
			String hairImageName = "girl_miss_hair"
					+ (byte) girlAppearance.charAt(1);
			String clothesImageName = "girl_miss_clothes"
					+ (byte) girlAppearance.charAt(2);
			String decorationImageName = "girl_miss_decoation"
					+ (byte) girlAppearance.charAt(3);

			// 固定id
			int missBodyId = R.drawable.girl_body_missing;
			int clothesImageid = getImageid(clothesImageName, ctx);
			int missHandId = R.drawable.girl_miss_hand;
			int missHeadId = R.drawable.girl_miss_head;
			int hairImageid = getImageid(hairImageName, ctx);
			int decorationImageid = getImageid(decorationImageName, ctx);
			if (hairImageid == 0) {
				hairImageid = R.drawable.girl_miss_hair1;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.girl_miss_clothes1;
			}
			if (decorationImageid == 0) {
				decorationImageid = R.drawable.girl_miss_decoation1;
			}

			girlMissAppID[0] = missBodyId;
			girlMissAppID[1] =missHeadId;
			girlMissAppID[2] =  hairImageid;
			girlMissAppID[3] =clothesImageid;
			girlMissAppID[4] = missHandId;
			girlMissAppID[5] = decorationImageid;

			this.isGirlMissInit = true;
		}
		//如果有初始化过 就先访问软缓存
        if(isGirlInitTemp){
        	myLayerDrawable= getLayerDrawableFormCache(mainMissGirlCacheName,ctx);
        }
        //如果缓存存在直接返回
        if(myLayerDrawable != null){
        	return myLayerDrawable;
        }
    	Drawable[] mylayers = new Drawable[6];
		mylayers[0] = r.getDrawable(girlMissAppID[0]);
		mylayers[1] = r.getDrawable(girlMissAppID[1]);
		mylayers[2] = r.getDrawable(girlMissAppID[2]);
		mylayers[3] = r.getDrawable(girlMissAppID[3]);
		mylayers[4] = r.getDrawable(girlMissAppID[4]);
		mylayers[5] = r.getDrawable(girlMissAppID[5]);

		myLayerDrawable = new LayerDrawable(mylayers);

		//缓存对象
				LayerDrawableSoftRef ref = addCacheLD(myLayerDrawable,mainMissGirlCacheName);
				myLayerDrawable = null;
				return ref.get();

	}

	// ---------------------------------------boy eat
	// ---------------------------------------------
	private int[] boyEatAppID = { 0, 0, 0, 0, 0, 0, 0 };

	public LayerDrawable initBoyEatting(Context ctx) {
		boolean isBoyInitTemp = this.isBoyEatingInit;
		LayerDrawable myLayerDrawable = null;
		Resources r = ctx.getResources();
	
		if (!(isBoyInitTemp) || boyEatAppID.length != 7) {
			String hairImageName = "boy_eat_hair0"
					+ (byte) boyAppearance.charAt(1);
			String clothesImageName = "boy_eatcloth0"
					+ (byte) boyAppearance.charAt(2);
			String decorationImageName = "boy_eat_decoration0"
					+ (byte) boyAppearance.charAt(3);

			// 固定id
			int eatChairId = R.drawable.boy_eat_chair;
			int eatBodyId = R.drawable.boy_eat_body;
			int eatFaceId = R.drawable.boy_eat_face;
			int eatRightHandId = R.drawable.boy_eat_right_hand;

			int hairImageid = getImageid(hairImageName, ctx);
			int clothesImageid = getImageid(clothesImageName, ctx);
			int decorationImageid = getImageid(decorationImageName, ctx);
			if (hairImageid == 0) {
				hairImageid = R.drawable.boy_eat_hair01;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.boy_eatcloth01;
			}
			if (decorationImageid == 0) {
				decorationImageid = R.drawable.boy_eat_decoration01;
			}

			boyEatAppID[0] = eatChairId;
			boyEatAppID[1] = eatBodyId;
			boyEatAppID[2] = eatFaceId;
			boyEatAppID[3] = hairImageid;

			boyEatAppID[4] = clothesImageid;
			boyEatAppID[5] = decorationImageid;
			boyEatAppID[6] = eatRightHandId;

			isBoyEatingInit = true;
		}
		//如果有初始化过 就先访问软缓存
        if(isBoyInitTemp){
        	myLayerDrawable= getLayerDrawableFormCache(mainEatBoyCacheName,ctx);
        }
        //如果缓存存在直接返回
        if(myLayerDrawable != null){
        	return myLayerDrawable;
        }
    	Drawable[] mylayers = new Drawable[7];

		mylayers[0] = r.getDrawable(boyEatAppID[0]);
		mylayers[1] = r.getDrawable(boyEatAppID[1]);
		mylayers[2] = r.getDrawable(boyEatAppID[2]);
		mylayers[3] = r.getDrawable(boyEatAppID[3]);
		mylayers[4] = r.getDrawable(boyEatAppID[4]);
		mylayers[5] = r.getDrawable(boyEatAppID[5]);
		mylayers[6] = r.getDrawable(boyEatAppID[6]);

		myLayerDrawable = new LayerDrawable(mylayers);
		//缓存对象
		LayerDrawableSoftRef ref = addCacheLD(myLayerDrawable,mainEatBoyCacheName);
		myLayerDrawable = null;
		return ref.get();
	}

	// ---------------------------------------boy
	// study---------------------------------------------
	private int[] boyStudyAppID = { 0, 0, 0, 0, 0, 0 };

	public LayerDrawable initBoyStudy(Context ctx) {
		boolean isGirlInitTemp = this.isBoyStudyInit;
		LayerDrawable myLayerDrawable = null;
		Resources r = ctx.getResources();
		
		if (!(isGirlInitTemp)) {
			String hairImageName = "boy_learn_hair0"
					+ (byte) boyAppearance.charAt(1);
			String clothesImageName = "boy_learn_clothes0"
					+ (byte) boyAppearance.charAt(2);
			String decorationImageName = "boy_learn_decoration0"
					+ (byte) boyAppearance.charAt(3);

			// 固定id
			int studyChairId = R.drawable.boy_learn_chair;
			int studyBodyId = R.drawable.boy_learn_body;
			int studyFaceId = R.drawable.boy_learn_face;

			int hairImageid = getImageid(hairImageName, ctx);
			int clothesImageid = getImageid(clothesImageName, ctx);
			int decorationImageid = getImageid(decorationImageName, ctx);
			if (hairImageid == 0) {
				hairImageid = R.drawable.boy_learn_hair01;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.boy_learn_clothes01;
			}
			if (decorationImageid == 0) {
				decorationImageid = R.drawable.boy_learn_decoration01;
			}
			boyStudyAppID[0] = studyChairId;
			boyStudyAppID[1] = studyBodyId;
			boyStudyAppID[2] = studyFaceId;
			boyStudyAppID[3] = hairImageid;
			boyStudyAppID[4] = clothesImageid;
			boyStudyAppID[5] = decorationImageid;
			this.isBoyStudyInit = true;
		}
		
		//如果有初始化过 就先访问软缓存
        if(isGirlInitTemp){
        	myLayerDrawable= getLayerDrawableFormCache(mainLearnBoyCacheName,ctx);
        }
        //如果缓存存在直接返回
        if(myLayerDrawable != null){
        	return myLayerDrawable;
        }
        Drawable[] mylayers = new Drawable[6];
		mylayers[0] = r.getDrawable(boyStudyAppID[0]);
		mylayers[1] = r.getDrawable(boyStudyAppID[1]);
		mylayers[2] = r.getDrawable(boyStudyAppID[2]);
		mylayers[3] = r.getDrawable(boyStudyAppID[3]);
		mylayers[4] = r.getDrawable(boyStudyAppID[4]);
		mylayers[5] = r.getDrawable(boyStudyAppID[5]);

		myLayerDrawable = new LayerDrawable(mylayers);

		//缓存对象
				LayerDrawableSoftRef ref = addCacheLD(myLayerDrawable,mainLearnBoyCacheName);
				myLayerDrawable = null;
				return ref.get();
	}

	// ---------------------------------------boy
	// angry---------------------------------------------
	private int[] boyAngryAppID = { 0, 0, 0 };

	public LayerDrawable initBoyAngry(Context ctx) {

		boolean isGirlInitTemp = this.isBoyAngryInit;
		LayerDrawable myLayerDrawable = null;
		Resources r = ctx.getResources();
	
		if (!(isGirlInitTemp)) {
			String hairImageName = "boy_back_hair0"
					+ (byte) boyAppearance.charAt(1);
			String clothesImageName = "boy_angrycloth0"
					+ (byte) boyAppearance.charAt(2);
			// 固定id
			int angryBodyId = R.drawable.boy_body_angry;
			int hairImageid = getImageid(hairImageName, ctx);
			int clothesImageid = getImageid(clothesImageName, ctx);
			if (hairImageid == 0) {
				hairImageid = R.drawable.boy_back_hair01;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.boy_angrycloth01;
			}
			boyAngryAppID[0] = angryBodyId;
			boyAngryAppID[1] = hairImageid;
			boyAngryAppID[2] = clothesImageid;

			this.isBoyAngryInit = true;
		}
		
		//如果有初始化过 就先访问软缓存
        if(isGirlInitTemp){
        	myLayerDrawable= getLayerDrawableFormCache(mainAngryBoyCacheName,ctx);
        }
        //如果缓存存在直接返回
        if(myLayerDrawable != null){
        	return myLayerDrawable;
        }
    	Drawable[] mylayers = new Drawable[3];
		mylayers[0] = r.getDrawable(boyAngryAppID[0]);
		mylayers[1] = r.getDrawable(boyAngryAppID[1]);
		mylayers[2] = r.getDrawable(boyAngryAppID[2]);

		myLayerDrawable = new LayerDrawable(mylayers);
		//缓存对象
		LayerDrawableSoftRef ref = addCacheLD(myLayerDrawable,mainAngryBoyCacheName);
		myLayerDrawable = null;
		return ref.get();
	}

	// ---------------------------------------boy
	// missing---------------------------------------------
	private int[] boyMissAppID = { 0, 0, 0, 0, 0, 0 };

	public LayerDrawable initBoyMiss(Context ctx) {
		LayerDrawable myLayerDrawable = null;
		Resources r = ctx.getResources();
	
		boolean isGirlInitTemp = this.isBoyMissInit;

		if (!(isGirlInitTemp)) {
			String hairImageName = "boy_miss_hair0"
					+ (byte) boyAppearance.charAt(1);
			String clothesImageName = "boy_miss_clothes0"
					+ (byte) boyAppearance.charAt(2);
			String decorationImageName = "boy_stand_decoration0"
					+ (byte) boyAppearance.charAt(3);

			// 固定id
			int missBodyId = R.drawable.boy_miss_body;
			int clothesImageid = getImageid(clothesImageName, ctx);
			int missHandId = R.drawable.boy_miss_hand;
			int missHeadId = R.drawable.boy_miss_head;
			int hairImageid = getImageid(hairImageName, ctx);
			int decorationImageid = getImageid(decorationImageName, ctx);
			if (hairImageid == 0) {
				hairImageid = R.drawable.boy_miss_hair01;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.boy_miss_clothes01;
			}
			if (decorationImageid == 0) {
				decorationImageid = R.drawable.boy_stand_decoration01;
			}

			boyMissAppID[0] = missBodyId;
			boyMissAppID[1] = clothesImageid;
			boyMissAppID[2] = missHandId;
			boyMissAppID[3] = missHeadId;
			boyMissAppID[4] = hairImageid;
			boyMissAppID[5] = decorationImageid;
			this.isBoyMissInit = true;
		}
		//如果有初始化过 就先访问软缓存
        if(isGirlInitTemp){
        	myLayerDrawable= getLayerDrawableFormCache(mainMissBoyCacheName,ctx);
        }
        //如果缓存存在直接返回
        if(myLayerDrawable != null){
        	return myLayerDrawable;
        }
    	Drawable[] mylayers = new Drawable[6];
		mylayers[0] = r.getDrawable(boyMissAppID[0]);
		mylayers[1] = r.getDrawable(boyMissAppID[1]);
		mylayers[2] = r.getDrawable(boyMissAppID[2]);
		mylayers[3] = r.getDrawable(boyMissAppID[3]);
		mylayers[4] = r.getDrawable(boyMissAppID[4]);
		mylayers[5] = r.getDrawable(boyMissAppID[5]);

		myLayerDrawable = new LayerDrawable(mylayers);
		//缓存对象
				LayerDrawableSoftRef ref = addCacheLD(myLayerDrawable,mainMissBoyCacheName);
				myLayerDrawable = null;
				return ref.get();
	}

	// -------------------------------couple action start
	// --------------------------------

	// ------------------------------couple action
	// abuse-------------------------------
	// girl 专用
	private int[] abuseAppID = { 0, 0, 0, 0, 0, 0 };

	public Bitmap getCoupleAbuseID(Context ctx) {
		String bitName = "coupleAbuse";
		
		SoftReference<Bitmap> newBitmap = null;
		boolean isGirlInitTemp = this.isAbuseInit;

		if (isGirlInitTemp) {
			String pathString = Environment.getExternalStorageDirectory()
					+ "/LoverHouse" + path + "/" + bitName + ".png";
			String imageUri = "file://" + pathString;
													// size
			Bitmap iNewBitmap = imageLoader.loadImageSync(imageUri, options);
					
			// newBitmap = AppManagerUtil.getDiskBitmap(pathString);
			if (iNewBitmap == null) {
				isGirlInitTemp = false;
			} else {
				return iNewBitmap;
			}

		}
		if (!(isGirlInitTemp)) {
			String hairImageName = "girl_abuse_hair"
					+ (byte) girlAppearance.charAt(1);
			String clothesImageName = "girl_abuse_clothes"
					+ (byte) girlAppearance.charAt(2);

			// 固定id
			int boyBodyId = R.drawable.boy_abuse_body;
			int girlHeadiId = R.drawable.girl_abuse_head;
			int girlBodyId = R.drawable.girl_abuse_body;

			int clothesImageid = getImageid(clothesImageName, ctx);

			int hairImageid = getImageid(hairImageName, ctx);

			int girlFaceId = R.drawable.girl_abuse_face;
			if (hairImageid == 0) {
				hairImageid = R.drawable.girl_abuse_hair1;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.girl_abuse_clothes1;
			}

			abuseAppID[0] = boyBodyId;
			abuseAppID[1] = girlHeadiId;
			abuseAppID[2] = girlBodyId;
			abuseAppID[3] = clothesImageid;
			abuseAppID[4] = hairImageid;
			abuseAppID[5] = girlFaceId;
			
			  // 防止出现Immutable bitmap passed to Canvas constructor错误
//          BitmapFactory.decodeResource(res, id, opts)
//	        Bitmap bitmap1 = BitmapFactory.decodeResource(ctx.getResources(),
//	        		abuseAppID[0]).copy(Bitmap.Config.ARGB_8888, true);
	
	    	
	       
//	        		((BitmapDrawable)ctx.getResources().getDrawable(
//	        		abuseAppID[1])).getBitmap();
	      
	        
	         newBitmap = null;

	        Bitmap newBitmaptemp = readBitMap(ctx, abuseAppID[0])
	        		.copy(Bitmap.Config.ARGB_8888, true);
	        
	        Canvas canvas = new Canvas(newBitmaptemp);
	        Paint paint = new Paint();

	        int w = newBitmaptemp.getWidth();
	        int h = newBitmaptemp.getHeight();

	        Bitmap bitmap3temp = readBitMap(ctx, abuseAppID[1]);
	        int w_2 = bitmap3temp.getWidth();
	        int h_2 = bitmap3temp.getHeight();

	        paint.setColor(Color.TRANSPARENT);
	        paint.setAlpha(0);
	        canvas.drawRect(0, 0, w, h, paint);
	    

	        paint = new Paint();
	  
	     
	        canvas.drawBitmap(bitmap3temp, Math.abs(w - w_2) / 2,
	                Math.abs(h - h_2) / 2, paint);
	        
            SoftReference<Bitmap> bitmap3 = new SoftReference<Bitmap>(bitmap3temp);	
	        bitmap3temp = null;
	        bitmap3.get().recycle();
//            bitmap3 = null;
     	
	    
            Bitmap bitmap4temp = readBitMap(ctx, abuseAppID[2]);
            
	        canvas.drawBitmap(bitmap4temp, Math.abs(w - w_2) / 2,
	                Math.abs(h - h_2) / 2, paint);
	        
	        SoftReference<Bitmap> bitmap4 = new SoftReference<Bitmap>(bitmap4temp);
	        
	        bitmap4.get().recycle();
//            bitmap4 = null;
            
            Bitmap bitmap5temp = readBitMap(ctx, abuseAppID[3]);
            
	        canvas.drawBitmap(bitmap5temp, Math.abs(w - w_2) / 2,
	                Math.abs(h - h_2) / 2, paint);
	        SoftReference<Bitmap> bitmap5 = new SoftReference<Bitmap>(bitmap5temp);
	        bitmap5.get().recycle();
            bitmap5 = null;
            
            Bitmap bitmap6temp = readBitMap(ctx, abuseAppID[4]);
           
	        canvas.drawBitmap(bitmap6temp, Math.abs(w - w_2) / 2,
	                Math.abs(h - h_2) / 2, paint);
	        SoftReference<Bitmap> bitmap6 = new SoftReference<Bitmap>(bitmap6temp);

	        bitmap6.get().recycle();
//            bitmap6 = null;
            
            Bitmap bitmap7temp = readBitMap(ctx, abuseAppID[5]);
            
	        canvas.drawBitmap(bitmap7temp, Math.abs(w - w_2) / 2,
	                Math.abs(h - h_2) / 2, paint);
	        SoftReference<Bitmap> bitmap7 = new SoftReference<Bitmap>(bitmap7temp);
	        bitmap7.get().recycle();
//            bitmap7 = null;
            
	        canvas.save(Canvas.ALL_SAVE_FLAG);
	        // 存储新合成的图片
	        canvas.restore();
       
	       
			this.isAbuseInit = true;
			AppManagerUtil.writeToSD(path, newBitmaptemp, bitName);
	          ctx = null;
	          newBitmap = new SoftReference<Bitmap>(newBitmaptemp);
	          newBitmaptemp= null;
		      return newBitmap.get();
			}
			return null;
	}
	
//	public void initCoupleAbuse(Context ctx) {
//		String bitName = "coupleAbuse";
//		
//		SoftReference<Bitmap> newBitmap = null;
//		boolean isGirlInitTemp = this.isAbuseInit;
//
//		if (!(isGirlInitTemp)) {
//			String hairImageName = "girl_abuse_hair"
//					+ (byte) girlAppearance.charAt(1);
//			String clothesImageName = "girl_abuse_clothes"
//					+ (byte) girlAppearance.charAt(2);
//
//			// 固定id
//			int boyBodyId = R.drawable.boy_abuse_body;
//			int girlHeadiId = R.drawable.girl_abuse_head;
//			int girlBodyId = R.drawable.girl_abuse_body;
//
//			int clothesImageid = getImageid(clothesImageName, ctx);
//
//			int hairImageid = getImageid(hairImageName, ctx);
//
//			int girlFaceId = R.drawable.girl_abuse_face;
//			if (hairImageid == 0) {
//				hairImageid = R.drawable.girl_abuse_hair1;
//			}
//			if (clothesImageid == 0) {
//				clothesImageid = R.drawable.girl_abuse_clothes1;
//			}
//
//			abuseAppID[0] = boyBodyId;
//			abuseAppID[1] = girlHeadiId;
//			abuseAppID[2] = girlBodyId;
//			abuseAppID[3] = clothesImageid;
//			abuseAppID[4] = hairImageid;
//			abuseAppID[5] = girlFaceId;
//			
//			  // 防止出现Immutable bitmap passed to Canvas constructor错误
////          BitmapFactory.decodeResource(res, id, opts)
////	        Bitmap bitmap1 = BitmapFactory.decodeResource(ctx.getResources(),
////	        		abuseAppID[0]).copy(Bitmap.Config.ARGB_8888, true);
//	        
//			
//			 Bitmap bitmap1temp = readBitMap(ctx, abuseAppID[5]);
//	         SoftReference<Bitmap> bitmap1 = new SoftReference<Bitmap>(bitmap1temp);
//	       
//	         Bitmap bitmap3temp = readBitMap(ctx, abuseAppID[1]);
//	         SoftReference<Bitmap> bitmap3 = new SoftReference<Bitmap>(bitmap3temp);
//	       
////	        Bitmap bitmap3 = readBitMap(ctx, abuseAppID[1]);
////	        		((BitmapDrawable)ctx.getResources().getDrawable(
////	        		abuseAppID[1])).getBitmap();
//	         bitmap1temp = null;
//	         bitmap3temp = null;
//	      
//
//	         newBitmap = null;
//
//	        Bitmap newBitmaptemp = Bitmap.createBitmap(bitmap1.get());
//	        newBitmap = new SoftReference<Bitmap>(newBitmaptemp);
//	        Canvas canvas = new Canvas(newBitmap.get());
//	        Paint paint = new Paint();
//
//	        int w = bitmap1.get().getWidth();
//	        int h = bitmap1.get().getHeight();
//
//	        int w_2 = bitmap3.get().getWidth();
//	        int h_2 = bitmap3.get().getHeight();
//
//	        paint.setColor(Color.TRANSPARENT);
//	        paint.setAlpha(0);
//	        canvas.drawRect(0, 0, w, h, paint);
//	    
//
//	        paint = new Paint();
//	  
//	     
//	        canvas.drawBitmap(bitmap3.get(), Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        
//	        bitmap1.get().recycle();
//            bitmap1 = null; 
//            bitmap3.get().recycle();
//            bitmap3 = null;
//            
//            Bitmap bitmap4temp = readBitMap(ctx, abuseAppID[2]);
//            SoftReference<Bitmap> bitmap4 = new SoftReference<Bitmap>(bitmap4temp);
// 	       
//            		
////            		((BitmapDrawable) ctx.getResources().getDrawable(
////	        		abuseAppID[2])).getBitmap();
//	       
//	        canvas.drawBitmap(bitmap4.get(), Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        
//	        bitmap4.get().recycle();
//            bitmap4 = null;
//            
//            Bitmap bitmap5temp =readBitMap(ctx, abuseAppID[3]);
////            ((BitmapDrawable) ctx.getResources().getDrawable(
////	        		abuseAppID[3])).getBitmap();
//            SoftReference<Bitmap> bitmap5 = new SoftReference<Bitmap>(bitmap5temp);
//  	       
////	      
//	        canvas.drawBitmap(bitmap5.get(), Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        
//	        bitmap5.get().recycle();
//            bitmap5 = null;
//            
//            Bitmap bitmap6temp = readBitMap(ctx, abuseAppID[4]);
////            		((BitmapDrawable) ctx.getResources().getDrawable(
////	        		abuseAppID[4])).getBitmap();
//            SoftReference<Bitmap> bitmap6 = new SoftReference<Bitmap>(bitmap6temp);
//   	       
//	        canvas.drawBitmap(bitmap6.get(), Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        bitmap6.get().recycle();
//            bitmap6 = null;
//            
//            Bitmap bitmap7temp = readBitMap(ctx, abuseAppID[5]);
////            		((BitmapDrawable) ctx.getResources().getDrawable(
////	        		abuseAppID[5])).getBitmap();
//            SoftReference<Bitmap> bitmap7 = new SoftReference<Bitmap>(bitmap7temp);
//    	       
//	        canvas.drawBitmap(bitmap7.get(), Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        bitmap7.get().recycle();
//            bitmap7 = null;
//            
//	        canvas.save(Canvas.ALL_SAVE_FLAG);
//	        // 存储新合成的图片
//	        canvas.restore();
//       
// 
//			this.isAbuseInit = true;
//			AppManagerUtil.writeToSD(path, newBitmap, bitName);
//	          ctx = null;
//	          newBitmap.recycle();
//	          newBitmap= null;
//			}
//			
//	}

	// ------------------------------couple action 捏脸
	// 摸头-------------------------------
	private int[] pGirlHeadOrFaceAppID = { 0, 0 ,0};
	private int[] pBoyHeadOrFaceAppID = { 0, 0 ,0};

	/**
	 * 
	 * @param ctx
	 * @param isBoy 0 girl , 1 boy
	 *            发起动作的那一方
	 * @return
	 */
	public Bitmap getPFaceOrPHeadID(Context ctx, int isBoy) {

		  SoftReference<Bitmap>  newBitmap = null;
		if (isBoy ==1) {
			String bitName = "PGirlFaceOrPHead";
			
			boolean isGirlInitTemp = this.isPFHGirlInit;
			if (isGirlInitTemp) {
				String pathString = Environment.getExternalStorageDirectory()
						+ "/LoverHouse" + path + "/" + bitName + ".png";
				String imageUri = "file://" + pathString;
																					// size
				Bitmap mBitmap =imageLoader.loadImageSync(imageUri, options);
				// newBitmap = AppManagerUtil.getDiskBitmap(pathString);
				if (mBitmap == null) {
					isGirlInitTemp = false;
				} else {
					return mBitmap;
				}

			}

			if (!(isGirlInitTemp)) {
				String hairImageName = "girl_pinchedface_hair"
						+ (byte) girlAppearance.charAt(1);
				String clothesImageName = "girl_pinchedface_clothes"
						+ (byte) girlAppearance.charAt(2);
				String decImageName = "girl_pinchedface_pettingdeco"
						+ (byte) girlAppearance.charAt(3);

				int clothesImageid = getImageid(clothesImageName, ctx);

				int hairImageid = getImageid(hairImageName, ctx);
				
				int decImageid = getImageid(decImageName, ctx);

				if (hairImageid == 0) {
					hairImageid = R.drawable.girl_pinchedface_hair1;
				}
				if (clothesImageid == 0) {
					clothesImageid = R.drawable.girl_pinchedface_clothes1;
				}
				if (decImageid == 0) {
					decImageid = R.drawable.girl_pinchedface_pettingdeco1;
				}

				pGirlHeadOrFaceAppID[0] = clothesImageid;
				pGirlHeadOrFaceAppID[1] = hairImageid;
				pGirlHeadOrFaceAppID[2] = decImageid;

				
				  // 防止出现Immutable bitmap passed to Canvas constructor错误
//		        Bitmap bitmap1temp =readBitMap(ctx, pGirlHeadOrFaceAppID[0]).copy(Bitmap.Config.ARGB_8888, true);
		   
		        newBitmap = null;
		       
		        Bitmap newBitmaptemp = readBitMap(ctx, pGirlHeadOrFaceAppID[0]).copy(Bitmap.Config.ARGB_8888, true);;
		        
		        Canvas canvas = new Canvas(newBitmaptemp);
		        Paint paint = new Paint();

		        int w = newBitmaptemp.getWidth();
		        int h = newBitmaptemp.getHeight();
		        Bitmap bitmap2temp =readBitMap(ctx, 
		        		pGirlHeadOrFaceAppID[1]);
		        int w_2 =bitmap2temp.getWidth();
		        int h_2 =bitmap2temp.getHeight();

		        paint.setColor(Color.TRANSPARENT);
		        paint.setAlpha(0);
		        canvas.drawRect(0, 0, w, h, paint);
		    

		        paint = new Paint();
		        canvas.drawBitmap(bitmap2temp, Math.abs(w - w_2) / 2,
		                Math.abs(h - h_2) / 2, paint);
		        
		        SoftReference<Bitmap> bitmap2 = new SoftReference<Bitmap>(bitmap2temp);	 
		        bitmap2temp = null; 
                bitmap2.get().recycle();
//                bitmap2 = null;
                
                Bitmap bitmap3temp =readBitMap(ctx, 
		        		pGirlHeadOrFaceAppID[2]);
                canvas.drawBitmap(bitmap3temp, Math.abs(w - w_2) / 2,
		                Math.abs(h - h_2) / 2, paint);
		        
		        SoftReference<Bitmap> bitmap3 = new SoftReference<Bitmap>(bitmap3temp);	 
		        bitmap3temp = null; 
                bitmap3.get().recycle();
		    
		        canvas.save(Canvas.ALL_SAVE_FLAG);
		        // 存储新合成的图片
		        canvas.restore();
		        
	    		this.isPFHGirlInit = true;
				
				AppManagerUtil.writeToSD(path, newBitmaptemp, bitName);
		
		          ctx = null;
		          newBitmap = new SoftReference<Bitmap>(newBitmaptemp);	
				    newBitmaptemp= null;
			      return newBitmap.get();
				}
				return null;

		} else {
			// 男生形象
			String bitName = "PBoyFaceOrPHead";
			boolean isGirlInitTemp = this.isPFHBoyInit;
			
			if (isGirlInitTemp) {
				String pathString = Environment.getExternalStorageDirectory()
						+ "/LoverHouse" + path + "/" + bitName + ".png";
				String imageUri = "file://" + pathString;
																// size
				Bitmap mBitmap  = imageLoader.loadImageSync(imageUri, options);
				// newBitmap = AppManagerUtil.getDiskBitmap(pathString);
				if (mBitmap == null) {
					isGirlInitTemp = false;
				} else {
					return mBitmap;
				}

			}

			if (!(isGirlInitTemp)) {
				String hairImageName = "boy_pinchedface_hair"
						+ (byte) boyAppearance.charAt(1);
				String clothesImageName = "boy_pinchedface_clothes"
						+ (byte) boyAppearance.charAt(2);
				String decImageName = "boy_pinchedface_pettingdeco"
						+ (byte) boyAppearance.charAt(3);

				int clothesImageid = getImageid(clothesImageName, ctx);

				int hairImageid = getImageid(hairImageName, ctx);
				
				int decImageid = getImageid(decImageName,ctx);

				if (hairImageid == 0) {
					hairImageid = R.drawable.boy_pinchedface_hair1;
				}
				if (clothesImageid == 0) {
					clothesImageid = R.drawable.boy_pinchedface_clothes1;
				}
				if(decImageid == 0){
					decImageid = R.drawable.boy_pinchedface_pettingdeco1;
				}

				pBoyHeadOrFaceAppID[0] = clothesImageid;
				pBoyHeadOrFaceAppID[1] = hairImageid;
				pBoyHeadOrFaceAppID[2] = decImageid;
			   
                 newBitmap = null;

			    Bitmap newBitmaptemp = readBitMap(ctx,
						 pBoyHeadOrFaceAppID[0]).copy(Bitmap.Config.ARGB_8888,true);
			    
			        Canvas canvas = new Canvas(newBitmaptemp);
			        Paint paint = new Paint();

			        int w = newBitmaptemp.getWidth();
			        int h =newBitmaptemp.getHeight();

			        Bitmap bitmap2temp =  readBitMap(ctx,
			        		pBoyHeadOrFaceAppID[1]);
			        int w_2 = bitmap2temp.getWidth();
			        int h_2 = bitmap2temp.getHeight();

			        paint.setColor(Color.TRANSPARENT);
			        paint.setAlpha(0);
			        canvas.drawRect(0, 0, w, h, paint);
			    

			        paint = new Paint(); 
			        canvas.drawBitmap(bitmap2temp, Math.abs(w - w_2) / 2,
			                Math.abs(h - h_2) / 2, paint);
			        SoftReference<Bitmap> bitmap2 = new SoftReference<Bitmap>(bitmap2temp);	
				    bitmap2temp =null;
			          bitmap2.get().recycle();
			         
			          Bitmap bitmap3temp =  readBitMap(ctx,
				        		pBoyHeadOrFaceAppID[2]);
			          canvas.drawBitmap(bitmap3temp, Math.abs(w - w_2) / 2,
				                Math.abs(h - h_2) / 2, paint);
				        SoftReference<Bitmap> bitmap3 = new SoftReference<Bitmap>(bitmap3temp);	
					    bitmap3temp =null;
				          bitmap3.get().recycle();
//		              bitmap2 = null;
			        canvas.save(Canvas.ALL_SAVE_FLAG);
			        // 存储新合成的图片
			        canvas.restore();
		       
			       
				this.isPFHBoyInit = true;
				AppManagerUtil.writeToSD(path, newBitmaptemp, bitName);
				
		          ctx = null;
		          newBitmap = new SoftReference<Bitmap>(newBitmaptemp);	
				  newBitmaptemp= null;
			      return newBitmap.get();
				}
				return null;
		}

	}

	// ------------------------------couple action
	// kiss------------------------------
	private int[] kissAppID = { 0, 0, 0, 0 };

	public Bitmap getKissID(Context ctx) {

		String bitName = "coupleKiss";
		boolean isGirlInitTemp = this.isKissInit;
		 SoftReference<Bitmap> newBitmap  = null;
		if (kissAppID == null || kissAppID[0] == 0)
			isGirlInitTemp = false;
		
		if (isGirlInitTemp) {
			String pathString = Environment.getExternalStorageDirectory()
					+ "/LoverHouse" + path + "/" + bitName + ".png";
			String imageUri = "file://" + pathString;
																			// size
			Bitmap mBitmap =imageLoader.loadImageSync(imageUri, options);
			// newBitmap = AppManagerUtil.getDiskBitmap(pathString);
			if (mBitmap == null) {
				isGirlInitTemp = false;
			} else {
				return mBitmap;
			}

		}

		if (!(isGirlInitTemp)) {
			String hairImageName = "girl_kiss_head"
					+ (byte) girlAppearance.charAt(1);
			String clothesImageName = "girl_kiss_body_"
					+ (byte) girlAppearance.charAt(2);

			String boyhairImageName = "boy_kiss_head"
					+ (byte) boyAppearance.charAt(1);
			String boyclothesImageName = "boy_kiss_body"
					+ (byte) boyAppearance.charAt(2);

			int clothesImageid = getImageid(clothesImageName, ctx);
			int hairImageid = getImageid(hairImageName, ctx);
			int boyclothesImageid = getImageid(boyclothesImageName, ctx);
			int boyhairImageid = getImageid(boyhairImageName, ctx);

			if (hairImageid == 0) {
				hairImageid = R.drawable.girl_kiss_head1;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.girl_kiss_body_17;
			}
			if (boyhairImageid == 0) {
				boyhairImageid = R.drawable.boy_kiss_head1;
			}
			if (boyclothesImageid == 0) {
				boyclothesImageid = R.drawable.boy_kiss_body1;
			}

			kissAppID[0] = clothesImageid;
			kissAppID[1] = hairImageid;
			kissAppID[2] = boyhairImageid;
			kissAppID[3] = boyclothesImageid;
			

//			 // 防止出现Immutable bitmap passed to Canvas constructor错误
//	        Bitmap  girlBodyBmtemp =  readBitMap(ctx,
//	        		kissAppID[0]).copy(Bitmap.Config.ARGB_8888, true);
//	    
//	        Bitmap newBitmaptemp = Bitmap.createBitmap(girlBodyBmtemp);
	        Bitmap newBitmaptemp = readBitMap(ctx,
	        		kissAppID[0]).copy(Bitmap.Config.ARGB_8888, true);
	        
//            SoftReference<Bitmap> girlBodyBm = new SoftReference<Bitmap>(girlBodyBmtemp);		
//	        girlBodyBmtemp = null;
//	        girlBodyBm.get().recycle();
//	        girlBodyBm = null;
	        
	        Canvas canvas = new Canvas(newBitmaptemp);
	        Paint paint = new Paint();

	        int w = newBitmaptemp.getWidth();
	        int h = newBitmaptemp.getHeight();

	        paint.setColor(Color.TRANSPARENT);
	        paint.setAlpha(0);
	        canvas.drawRect(0, 0, w,h, paint);
	        paint = new Paint();
	        
	        Bitmap girlHeadBmtemp =  readBitMap(ctx,
	        		kissAppID[1]);
	        int w_2 = girlHeadBmtemp.getWidth();
	        int h_2 = girlHeadBmtemp.getHeight();
	        
	        canvas.drawBitmap(girlHeadBmtemp, Math.abs(w - w_2) / 2,
	                Math.abs(h - h_2) / 2, paint);
	        
	      
	    
	        SoftReference<Bitmap> girlHeadBm = new SoftReference<Bitmap>(girlHeadBmtemp);		
	        girlHeadBmtemp = null;
	       
	        girlHeadBm.get().recycle();
//	        girlHeadBm = null;
	       
	        
	        
	        Bitmap boyBodyBmtemp =  readBitMap(ctx,
	        		kissAppID[2]);
	      
	     
	        
	        canvas.drawBitmap(boyBodyBmtemp, Math.abs(w - w_2) / 2,
	                Math.abs(h - h_2) / 2, paint);
	        
            SoftReference<Bitmap> boyBodyBm = new SoftReference<Bitmap>(boyBodyBmtemp);		
	        boyBodyBmtemp = null;
	        boyBodyBm.get().recycle();
//	        boyBodyBm = null;
	        
	        Bitmap boyHeadBmtemp =  readBitMap(ctx,
	        		kissAppID[3]);
	       
	        canvas.drawBitmap(boyHeadBmtemp, Math.abs(w - w_2) / 2,
	                Math.abs(h - h_2) / 2, paint);
	     
            SoftReference<Bitmap> boyHeadBm = new SoftReference<Bitmap>(boyHeadBmtemp);	
			
	        boyHeadBmtemp = null;
	        
	        canvas.save(Canvas.ALL_SAVE_FLAG);
	        // 存储新合成的图片
	        canvas.restore();

        
			AppManagerUtil.writeToSD(path, newBitmaptemp, bitName);
		

			  this.isKissInit = true;
			  boyHeadBm.get().recycle();
//	          boyHeadBm = null;
	          ctx = null;
	          newBitmap = new SoftReference<Bitmap>(newBitmaptemp);	
		      newBitmaptemp =null;
		      return newBitmap.get();
			}
			return null;

	

	}

	// ------------------------------couple action
	// hug------------------------------
	private int[] hugAppID = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public int getHugGirlHair(){
		if(hugAppID[5] == 0){
			return R.drawable.girl_hug_hair1;
		}
		return hugAppID[5];
	}
	public Bitmap getHugID(Context ctx) {
		System.gc();
		String bitName = "coupleHug";
		boolean isGirlInitTemp = this.isHugInit;
		
		SoftReference<Bitmap>  newBitmap = null;

		if (hugAppID == null || hugAppID[0] == 0)
			isGirlInitTemp = false;
		if (isGirlInitTemp) {
			String pathString = Environment.getExternalStorageDirectory()
					+ "/LoverHouse" + path + "/" + bitName + ".png";
			String imageUri = "file://" + pathString;
							// Bitmap
																			// size
			Bitmap mBitmap=imageLoader.loadImageSync(imageUri, options);
			// newBitmap = AppManagerUtil.getDiskBitmap(pathString);
			if (mBitmap == null) {
				isGirlInitTemp = false;
			} else {
				return mBitmap;
			}

		}
		if (!(isGirlInitTemp)) {
			String hairImageName = "girl_hug_hair"
					+ (byte) girlAppearance.charAt(1);
			String clothesImageName = "girl_hug_body"
					+ (byte) girlAppearance.charAt(2);

			String boyhairImageName = "boy_hug_hair"
					+ (byte) boyAppearance.charAt(1);
			String boyclothesImageName = "boy_hug_clothes"
					+ (byte) boyAppearance.charAt(2);

			String boyHugLeftHandName = "boy_hug_lefthand"
					+ (byte) boyAppearance.charAt(2);

			// 固定id
			int boyHeadId = R.drawable.boy_hug_head;
			int boyhairImageid = getImageid(boyhairImageName, ctx);
			int boyBodyId = R.drawable.boy_hug_body;
			int boyclothesImageid = getImageid(boyclothesImageName, ctx);
			int boyRightHandid = R.drawable.boy_hug_righthand;
			int boyLeftHandId = getImageid(boyHugLeftHandName, ctx);

			int girlHeadiId = R.drawable.girl_hug_head;
			int hairImageid = getImageid(hairImageName, ctx);
			int clothesImageid = getImageid(clothesImageName, ctx);

			if (hairImageid == 0) {
				hairImageid = R.drawable.girl_hug_hair1;
			}
			if (clothesImageid == 0) {
				clothesImageid = R.drawable.girl_hug_body13;
			}
			if (boyhairImageid == 0) {
				boyhairImageid = R.drawable.boy_hug_hair1;
			}
			if (boyclothesImageid == 0) {
				boyclothesImageid = R.drawable.boy_hug_clothes1;
			}
			if (boyLeftHandId == 0) {
				boyLeftHandId = R.drawable.boy_hug_lefthand1;
			}

			hugAppID[0] = boyHeadId;
			hugAppID[1] = boyhairImageid;
			hugAppID[2] = boyBodyId;
			hugAppID[3] = boyclothesImageid;
			hugAppID[4] = girlHeadiId;
			hugAppID[5] = hairImageid;
			hugAppID[6] = clothesImageid;
			hugAppID[7] = boyRightHandid;
			hugAppID[8] = boyLeftHandId;
			
		

			// 防止出现Immutable bitmap passed to Canvas constructor错误
//			readBitMap(Context context, int resId)
//			Bitmap boyHeadBmtemp =readBitMap(ctx,
//					hugAppID[0]).copy(Bitmap.Config.ARGB_4444, true);
//			Bitmap boyHeadBm = readBitMap(ctx, int resId);BitmapFactory.decodeResource(ctx.getResources(),
//					hugAppID[0]).copy(Bitmap.Config.ARGB_4444, true);	
			
			newBitmap = null;

			Bitmap newBitmaptemp = readBitMap(ctx,
					hugAppID[0]).copy(Bitmap.Config.ARGB_4444, true);
					
//					Bitmap.createBitmap(boyHeadBmtemp);
			
//			SoftReference<Bitmap> boyHeadBm = new SoftReference<Bitmap>(boyHeadBmtemp);	
//			boyHeadBmtemp = null;
//			boyHeadBm.get().recycle();
//			boyHeadBm = null;

			Canvas canvas = new Canvas(newBitmaptemp);
			Paint paint = new Paint();

			int w = newBitmaptemp.getWidth();
			int h = newBitmaptemp.getHeight();

			

			paint.setColor(Color.TRANSPARENT);
			paint.setAlpha(0);
			canvas.drawRect(0, 0, w, h, paint);

			paint = new Paint();
			Bitmap boyHairBmtemp = readBitMap(ctx,hugAppID[1]);
			int w_2 = boyHairBmtemp.getWidth();
			int h_2 =boyHairBmtemp.getHeight();
			
			canvas.drawBitmap(boyHairBmtemp, Math.abs(w - w_2) / 2,
					Math.abs(h - h_2) / 2, paint);
			
			SoftReference<Bitmap> boyHairBm = new SoftReference<Bitmap>(boyHairBmtemp);	
			boyHairBmtemp = null;	
			boyHairBm.get().recycle();
//			boyHairBm = null;

			Bitmap boyBodyBmtemp = readBitMap(ctx,hugAppID[2]);
			
			canvas.drawBitmap( boyBodyBmtemp, Math.abs(w - w_2) / 2,
					Math.abs(h - h_2) / 2, paint);
			SoftReference<Bitmap> boyBodyBm = new SoftReference<Bitmap>(boyBodyBmtemp);	
			boyBodyBmtemp = null;
			boyBodyBm.get().recycle();
//			boyBodyBm = null;

			Bitmap boyClothesBmtemp = readBitMap(ctx,hugAppID[3]);
			
			canvas.drawBitmap(boyClothesBmtemp, Math.abs(w - w_2) / 2,
					Math.abs(h - h_2) / 2, paint);
			
			SoftReference<Bitmap> boyClothesBm = new SoftReference<Bitmap>(boyClothesBmtemp);	
			boyClothesBmtemp = null;
			boyClothesBm.get().recycle();
//			boyClothesBm = null;
		
			Bitmap boyRightHandBmtemp= readBitMap(ctx,hugAppID[4]);
			
			canvas.drawBitmap(boyRightHandBmtemp, Math.abs(w - w_2) / 2,
					Math.abs(h - h_2) / 2, paint);
			SoftReference<Bitmap> boyRightHandBm = new SoftReference<Bitmap>(boyRightHandBmtemp);	
			boyRightHandBmtemp = null;
			boyRightHandBm.get().recycle();
//			boyRightHandBm = null;

			Bitmap boyLeftHandBmtemp = readBitMap(ctx,hugAppID[6]);
			
			canvas.drawBitmap(boyLeftHandBmtemp, Math.abs(w - w_2) / 2,
					Math.abs(h - h_2) / 2, paint);
			SoftReference<Bitmap> boyLeftHandBm = new SoftReference<Bitmap>(boyLeftHandBmtemp);	
			boyLeftHandBmtemp = null;
			boyLeftHandBm.get().recycle();
//			boyLeftHandBm = null;

			Bitmap girlHeadBmtemp = readBitMap(ctx,hugAppID[7]);
			
			canvas.drawBitmap(girlHeadBmtemp, Math.abs(w - w_2) / 2,
					Math.abs(h - h_2) / 2, paint);
			SoftReference<Bitmap> girlHeadBm = new SoftReference<Bitmap>(girlHeadBmtemp);	
			girlHeadBmtemp = null;
			girlHeadBm.get().recycle();
//			girlHeadBm = null;

			// Bitmap girlHairBm = ((BitmapDrawable) getResources().getDrawable(
			// mID[7])).getBitmap();
			Bitmap girlBodyBmtemp = readBitMap(ctx,hugAppID[8]);
			
			canvas.drawBitmap(girlBodyBmtemp, Math.abs(w - w_2) / 2,
					Math.abs(h - h_2) / 2, paint);
			SoftReference<Bitmap> girlBodyBm = new SoftReference<Bitmap>(girlBodyBmtemp);	
			girlBodyBmtemp = null;
			girlBodyBm.get().recycle();
//			girlBodyBm = null;
			
			canvas.save(Canvas.ALL_SAVE_FLAG);
			// 存储新合成的图片
			canvas.restore();

			AppManagerUtil.writeToSD(path, newBitmaptemp, bitName);
			

			ctx = null;
			this.isHugInit = true;
			newBitmap = new SoftReference<Bitmap>(newBitmaptemp);
			newBitmaptemp = null;
			return newBitmap.get();
		}
		return null;

	}

	/**
	 * 以最省内存的方式读取本地资源的图片，原大小
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public Bitmap readBitMap(Context context, int resId) {
		String imageUri = "drawable://" + resId;
		return (imageLoader
		.loadImageSync(imageUri, options));
		
//		BitmapFactory.Options opt = new BitmapFactory.Options();
//		opt.inPreferredConfig = Bitmap.Config.RGB_565;
//		opt.inPurgeable = true;
//		opt.inInputShareable = true;
//		// 获取资源图片
//		InputStream is = context.getResources().openRawResource(resId);
//		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	//------------------------view group -------------------
	public View   getAbuseVG(LayoutInflater inflater){
		View abuseView = null;
		//先访问软缓存
        abuseView = getViewGroupFormCache(abuseCacheName);
      
        if(abuseView != null){
        	return abuseView;
        }
		abuseView = inflater.inflate(R.layout.couple_person_abuse,null);  
		addCacheVG(abuseView,abuseCacheName);
		return abuseView;
	}
	public View   getGirlpFaceVG(LayoutInflater inflater){
		View mView = null;
		//先访问软缓存
        mView = getViewGroupFormCache(girlPFaceCacheName);
      
        if(mView != null){
        	return mView;
        }
		mView = inflater.inflate(R.layout.girlpface,null);  
		addCacheVG(mView,girlPFaceCacheName);
		return mView;
	}
	
	public View   getBoypFaceVG(LayoutInflater inflater){
		View mView = null;
		//先访问软缓存
        mView = getViewGroupFormCache(boyPFaceCacheName);
      
        if(mView != null){
        	return mView;
        }
		mView = inflater.inflate(R.layout.boypface,null);  
		addCacheVG(mView,boyPFaceCacheName);
		return mView;
	}
	
	public View   getGirlpHeadVG(LayoutInflater inflater){
		View mView = null;
		//先访问软缓存
        mView = getViewGroupFormCache(girlPHeadCacheName);
      
        if(mView != null){
        	return mView;
        }
		mView = inflater.inflate(R.layout.girlpetthead,null);  
		addCacheVG(mView,girlPHeadCacheName);
		return mView;
	}
	
	public View   getBoypHeadVG(LayoutInflater inflater){
		View mView = null;
		//先访问软缓存
        mView = getViewGroupFormCache(boyPHeadCacheName);
      
        if(mView != null){
        	return mView;
        }
		mView = inflater.inflate(R.layout.boypethead,null);  
		addCacheVG(mView,boyPHeadCacheName);
		return mView;
	}
	
	public View   getKissVG(LayoutInflater inflater){
		View mView = null;
		//先访问软缓存
        mView = getViewGroupFormCache(kissCacheName);
      
        if(mView != null){
        	return mView;
        }
		mView = inflater.inflate(R.layout.couple_person_kiss,null);  
		addCacheVG(mView,kissCacheName);
		return mView;
	}
	
	public View getHugVG(LayoutInflater inflater){
		View mView = null;
		//先访问软缓存
        mView = getViewGroupFormCache(hugCacheName);
      
        if(mView != null){
        	return mView;
        }
		mView = inflater.inflate(R.layout.couple_person_hug,null);  
		addCacheVG(mView,hugCacheName);
		return mView;
	}
	
	public void destoryAll(){
		clearVGCache();
		clearCache();
	}
	
	

}
