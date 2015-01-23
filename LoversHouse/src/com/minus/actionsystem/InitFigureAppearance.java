package com.minus.actionsystem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Environment;

import com.minus.lovershouse.R;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.xsocket.asynsocket.protocol.Protocol;

public class InitFigureAppearance {

//	 /*策略
//	第一次登陆成功后，根据status生成对应的bitmap，保存对应的图片到sd卡
//	当且仅当自己主动改变appear或者对方改变对应appearance时重新生成bitmap，更新对应图片
//	*/
//	
//	String path = null;
//	private String girlAppearance = null;
//	private boolean isGirlInit = false;
//	private boolean isGirlEatingInit = false;
//	private boolean isGirlStudyInit = false;
//	private boolean isGirlAngryInit = false;
//	private boolean isGirlMissInit = false;
//	
//	private String boyAppearance = null;
//	private boolean isBoyInit = false;
//	private boolean isBoyEatingInit = false;
//	private boolean isBoyStudyInit = false;
//	private boolean isBoyAngryInit = false;
//	private boolean isBoyMissInit = false;
//	
//	// ------------couple
//
//		private boolean isAbuseInit = false;
//		private boolean isPFHBoyInit = false;
//		private boolean isPFHGirlInit = false;
//		private boolean isKissInit = false;
//		private boolean isHugInit = false;
//	
//	private  InitFigureAppearance(){
//		  super();
//		  path = "/actiontemp";
//		  setDefault();
//	}
//	/**
//	 * 单例模式,线程
//	 * @return
//	 */
//		
//		private static class  InitFigureAppearanceContainer{
//			private static InitFigureAppearance instance = new InitFigureAppearance();
//		}
//		
//		public static InitFigureAppearance getInstance(){
//		    
//			  return  InitFigureAppearanceContainer.instance;
//		}
//	
//	
//
////设置女生的形象，表明形象有更新。
//		public void resetGirlAppearance(String girlAppearance) {
//			this.girlAppearance = girlAppearance;
//			 isGirlInit = false;
//			 isGirlEatingInit = false;
//	         isGirlStudyInit = false;
//		     isGirlAngryInit = false;
//		     isGirlMissInit = false;
//			
//		}
//		//设置男生的形象，表明形象有更新。
//				public void resetBoyAppearance(String boyAppearance) {
//					this.boyAppearance = boyAppearance;
//				     isBoyInit = false;
//				     isBoyEatingInit = false;
//				     isBoyStudyInit = false;
//				     isBoyAngryInit = false;
//				     isBoyMissInit = false;
//					
//				}
//				
//				private  void setDefault(){
//					this.girlAppearance = String.format("%c%c%c%c", Protocol.DEFAULT, Protocol.DEFAULT, Protocol.DEFAULT, Protocol.DEFAULT);
//					 isGirlInit = false;
//					 isGirlEatingInit = false;
//			         isGirlStudyInit = false;
//				     isGirlAngryInit = false;
//				     isGirlMissInit = false;
//				 	this.boyAppearance = String.format("%c%c%c%c", Protocol.DEFAULT, Protocol.DEFAULT, Protocol.DEFAULT, Protocol.DEFAULT);;
//				     isBoyInit = false;
//				     isBoyEatingInit = false;
//				     isBoyStudyInit = false;
//				     isBoyAngryInit = false;
//				     isBoyMissInit = false;
//				}
//		
//
//	public int getImageid(String imageName,Context ctx){
//		int mImageid = ctx.getResources().getIdentifier(
//				imageName, "drawable", "com.minus.lovershouse");
//		ctx= null;
//		// name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
//		return mImageid;
//	}
//	//--------------------------------------------------girl-----------------------------------------------------
//	 public  Bitmap  initGirl(Context ctx) {
//
//		 String bitName = "girl";
//		 boolean isGirlInitTemp = isGirlInit;
//		 Bitmap newBitmap = null;
//		 Bitmap hairImageBm = null;
//		  Bitmap clothesImageBm  = null;
//		  Bitmap decorationImageBm = null;
//		 if(isGirlInitTemp){
//			 String pathString = Environment.getExternalStorageDirectory()  
//		                + "/LoverHouse"+path+"/"+bitName+".png";
//			 newBitmap = AppManagerUtil.getDiskBitmap(pathString);
//			 if(newBitmap == null){
//				 isGirlInitTemp = false;
//			 }else{
//				 return newBitmap;
//			 }
//			 
//		 }
//		 if(!(isGirlInitTemp)){
//		 String hairImageName = "girl_hair_to_left"+(byte)girlAppearance.charAt(1);
// 		String clothesImageName="girl_clothes_to_left"+(byte)girlAppearance.charAt(2);
// 		String decorationImageName = "girl_decoration_to_left"+(byte)girlAppearance.charAt(3);
//      	int hairImageid =getImageid(hairImageName,ctx);
//     	int clothesImageid = getImageid(clothesImageName,ctx);
//     	int decorationImageid  = getImageid(decorationImageName,ctx);
//     	if(hairImageid == 0){
//     		hairImageid = R.drawable.girl_hair_to_left1;
//     	}
//    	if(clothesImageid == 0){
//    		clothesImageid = R.drawable.girl_clothes_to_left1;
//     	}
//    	if(decorationImageid == 0){
//    		decorationImageid = R.drawable.girl_decoration_to_left1;
//     	}
//	        // 防止出现Immutable bitmap passed to Canvas constructor错误
//    	 hairImageBm = BitmapFactory.decodeResource(ctx.getResources(),
//    			   hairImageid).copy(Bitmap.Config.ARGB_8888, true);
//    	   clothesImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		clothesImageid)).getBitmap();
//	       
//
//	        newBitmap = Bitmap.createBitmap(hairImageBm);
//	        Canvas canvas = new Canvas(newBitmap);
//	        Paint paint = new Paint();
//
//	        int w = hairImageBm.getWidth();
//	        int h = hairImageBm.getHeight();
//
//	        int w_2 = clothesImageBm.getWidth();
//	        int h_2 = clothesImageBm.getHeight();
//
//	        paint.setColor(Color.TRANSPARENT);
//	        paint.setAlpha(0);
//	        canvas.drawRect(0, 0, w, h, paint);
//	        paint = new Paint();
//	     
//	        canvas.drawBitmap(clothesImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//
//	        decorationImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		decorationImageid)).getBitmap();
//	      
//	        canvas.drawBitmap(decorationImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	     
//	        canvas.save(Canvas.ALL_SAVE_FLAG);
//	        // 存储新合成的图片
//	        canvas.restore();
//
//	        AppManagerUtil.writeToSD(path,newBitmap,bitName);      
//        
//          
//	    }
//
////		   decorationImageBm.recycle();
//           decorationImageBm = null;
////           clothesImageBm.recycle();
//	        clothesImageBm = null;
//	          
//	         hairImageBm.recycle();
//	         hairImageBm = null;
//           ctx = null;
//           this.isGirlInit = true;
//		 return newBitmap;
//	 }
//	 //---------------------------------------girl eat ---------------------------------------------
//	 public  Bitmap  initGirlEatting(Context ctx) {
//
//		 String bitName = "girleatting";
//		 boolean isGirlInitTemp =this.isGirlEatingInit;
//		 if(isGirlInitTemp){
//			 String pathString = Environment.getExternalStorageDirectory()  
//		                + "/LoverHouse"+path+"/"+bitName+".png";
//			 Bitmap girlEatBm = AppManagerUtil.getDiskBitmap(pathString);
//			 if(girlEatBm == null){
//				 isGirlInitTemp = false;
//			 }else{
//				 return girlEatBm;
//			 }
//			 
//		 }
//		 if(!(isGirlInitTemp)){
//		 String hairImageName = "girl_eat_hair0"+(byte)girlAppearance.charAt(1);
// 		String clothesImageName= "girl_eat_clothes"+(byte)girlAppearance.charAt(2);
// 		String decorationImageName =  "girl_eat_decoraion"+(byte)girlAppearance.charAt(3);
// 		
// 		//固定id
// 		int eatChairId = R.drawable.girl_eat_chair;
// 		int eatBodyId = R.drawable.girl_eat_body;
// 		int eatFaceId= R.drawable.girl_eat_face;
// 		int eatRightHandId = R.drawable.girl_eat_righthand;
// 		
//      	int hairImageid =getImageid(hairImageName,ctx);
//     	int clothesImageid = getImageid(clothesImageName,ctx);
//     	int decorationImageid  = getImageid(decorationImageName,ctx);
//     	if(hairImageid == 0){
//     		hairImageid = R.drawable.girl_eat_hair01;
//     	}
//    	if(clothesImageid == 0){
//    		clothesImageid = R.drawable.girl_eat_clothes1;
//     	}
//    	if(decorationImageid == 0){
//    		decorationImageid = R.drawable.girl_eat_decoraion1;
//     	}
//	        // 防止出现Immutable bitmap passed to Canvas constructor错误
//    	  Bitmap eatChairBm = BitmapFactory.decodeResource(ctx.getResources(),
//    			  eatChairId).copy(Bitmap.Config.ARGB_8888, true);
//    	   Bitmap eatBodyBm =((BitmapDrawable)ctx.getResources().getDrawable(
//    			   eatBodyId)).getBitmap(); 
//    	
//    	
//    	
//    	
//	        Bitmap newBitmap = null;
//
//	        newBitmap = Bitmap.createBitmap(eatChairBm);
//	        Canvas canvas = new Canvas(newBitmap);
//	        Paint paint = new Paint();
//
//	        int w = eatChairBm.getWidth();
//	        int h = eatChairBm.getHeight();
//
//	        int w_2 = eatBodyBm.getWidth();
//	        int h_2 = eatBodyBm.getHeight();
//
//	        paint.setColor(Color.TRANSPARENT);
//	        paint.setAlpha(0);
//	        canvas.drawRect(0, 0, w, h, paint);
//	        paint = new Paint();
//	     
//	        canvas.drawBitmap(eatBodyBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        
////	        clothesImageBm.recycle();
//	        eatBodyBm = null;
//	          
//	        eatChairBm.recycle();
//	        eatChairBm = null;
//	        
//	        Bitmap eatFaceBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	    			   eatFaceId)).getBitmap(); 
//	        canvas.drawBitmap(eatFaceBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        eatFaceBm =null;
//	        
//	    
//	        
//	        Bitmap hairImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	    			   hairImageid)).getBitmap(); 
//	        canvas.drawBitmap(hairImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        hairImageBm = null;
//	        
//	        Bitmap clothesImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		clothesImageid)).getBitmap();
//	        canvas.drawBitmap(clothesImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        clothesImageBm = null;
//	        
//	        Bitmap decorationImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		decorationImageid)).getBitmap();
//	        canvas.drawBitmap(decorationImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        decorationImageBm= null;
//	        Bitmap eatRightHandBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	    			   eatRightHandId)).getBitmap(); 
//	        canvas.drawBitmap(eatRightHandBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        eatRightHandBm = null;
//	     
//	        canvas.save(Canvas.ALL_SAVE_FLAG);
//	        // 存储新合成的图片
//	        canvas.restore();
//
//	        AppManagerUtil.writeToSD(path,newBitmap,bitName);      
//           ctx = null;
//           this.isGirlEatingInit = true;
//           return newBitmap;
//	    }
//		 return null;
//	 }
//	 
//	 //---------------------------------------girl study---------------------------------------------
//	 public  Bitmap  initGirlStudy(Context ctx) {
//
//		 String bitName = "girlstudy";
//		 boolean isGirlInitTemp =this.isGirlStudyInit;
//		 if(isGirlInitTemp){
//			 String pathString = Environment.getExternalStorageDirectory()  
//		                + "/LoverHouse"+path+"/"+bitName+".png";
//			 Bitmap girlStudyBm = AppManagerUtil.getDiskBitmap(pathString);
//			 if(girlStudyBm == null){
//				 isGirlInitTemp = false;
//			 }else{
//				 return girlStudyBm;
//			 }
//			 
//		 }
//		 if(!(isGirlInitTemp)){
//		 String hairImageName = "girl_learn_hair0"+(byte)girlAppearance.charAt(1);
// 		String clothesImageName= "girl_learn_clothes"+(byte)girlAppearance.charAt(2);
// 		String decorationImageName =  "girl_learn_decoration"+(byte)girlAppearance.charAt(3);
// 		
// 		//固定id
// 		int studyChairId = R.drawable.girl_learn_chair;
// 		int studyBodyId = R.drawable.girl_learn_body;
// 		int studyFaceId= R.drawable.girl_learn_face;
// 		
//      	int hairImageid =getImageid(hairImageName,ctx);
//     	int clothesImageid = getImageid(clothesImageName,ctx);
//     	int decorationImageid  = getImageid(decorationImageName,ctx);
//     	if(hairImageid == 0){
//     		hairImageid = R.drawable.girl_eat_hair01;
//     	}
//    	if(clothesImageid == 0){
//    		clothesImageid = R.drawable.girl_learn_clothes1;
//     	}
//    	if(decorationImageid == 0){
//    		decorationImageid = R.drawable.girl_learn_decoration1;
//     	}
//	        // 防止出现Immutable bitmap passed to Canvas constructor错误
//    	  Bitmap studyChairBm = BitmapFactory.decodeResource(ctx.getResources(),
//    			  studyChairId).copy(Bitmap.Config.ARGB_8888, true);
//    	  
//    	  
//    	   Bitmap studyBodyBm =((BitmapDrawable)ctx.getResources().getDrawable(
//    			   studyBodyId)).getBitmap(); 
//    	
//    	
//    	     Bitmap studyFaceBm =((BitmapDrawable)ctx.getResources().getDrawable(
// 	        		studyFaceId)).getBitmap(); 
//    	
//	        Bitmap newBitmap = null;
//
//	        newBitmap = Bitmap.createBitmap(studyChairBm);
//	        Canvas canvas = new Canvas(newBitmap);
//	        Paint paint = new Paint();
//
//	        int w = studyChairBm.getWidth();
//	        int h = studyChairBm.getHeight();
//
//	        int w_2 = studyFaceBm.getWidth();
//	        int h_2 =studyFaceBm.getHeight();
//
//	        paint.setColor(Color.TRANSPARENT);
//	        paint.setAlpha(0);
//	        canvas.drawRect(0, 0, w, h, paint);
//	        paint = new Paint();
//	        
//	   
//	        canvas.drawBitmap(studyFaceBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        int w_3 = studyBodyBm.getWidth();
//	        int h_3 =studyBodyBm.getHeight();
//	        canvas.drawBitmap(studyBodyBm, Math.abs(w - w_3) / 2,
//	                Math.abs(h - h_3) / 2, paint);
//	        
////	        clothesImageBm.recycle();
//	        studyBodyBm = null;
//	          
//	        studyChairBm.recycle();
//	        studyChairBm = null;
//	        
//	      
//	        studyFaceBm =null;
//	        
//	     
//	        
//	        Bitmap hairImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	    			   hairImageid)).getBitmap(); 
//	        canvas.drawBitmap(hairImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        hairImageBm = null;
//	        
//	        Bitmap clothesImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		clothesImageid)).getBitmap();
//	        canvas.drawBitmap(clothesImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        clothesImageBm = null;
//	        
//	        Bitmap decorationImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		decorationImageid)).getBitmap();
//	        canvas.drawBitmap(decorationImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	     
//	        canvas.save(Canvas.ALL_SAVE_FLAG);
//	        // 存储新合成的图片
//	        canvas.restore();
//
//	        AppManagerUtil.writeToSD(path,newBitmap,bitName);      
//           ctx = null;
//           this.isGirlStudyInit = true;
//           return newBitmap;
//	    }
//		 return null;
//	 }
//	 
//	 //---------------------------------------girl angry---------------------------------------------
//	 public  Bitmap  initGirlAngry(Context ctx) {
//
//		 String bitName = "girlangry";
//		 boolean isGirlInitTemp =this.isGirlAngryInit;
//		 if(isGirlInitTemp){
//			 String pathString = Environment.getExternalStorageDirectory()  
//		                + "/LoverHouse"+path+"/"+bitName+".png";
//			 Bitmap girlStudyBm = AppManagerUtil.getDiskBitmap(pathString);
//			 if(girlStudyBm == null){
//				 isGirlInitTemp = false;
//			 }else{
//				 return girlStudyBm;
//			 }
//			 
//		 }
//		 if(!(isGirlInitTemp)){
//		 String hairImageName = "girl_back_hair0"+(byte)girlAppearance.charAt(1);
// 		String clothesImageName= "girl_angry_cloth"+(byte)girlAppearance.charAt(2);
// 		String decorationImageName =  "girl_angry_deco"+(byte)girlAppearance.charAt(3);
// 		
// 		//固定id
// 		int angryBodyId = R.drawable.girl_body_angry;
// 
//      	int hairImageid =getImageid(hairImageName,ctx);
//     	int clothesImageid = getImageid(clothesImageName,ctx);
//     	int decorationImageid  = getImageid(decorationImageName,ctx);
//     	if(hairImageid == 0){
//     		hairImageid = R.drawable.girl_back_hair01;
//     	}
//    	if(clothesImageid == 0){
//    		clothesImageid = R.drawable.girl_angry_cloth1;
//     	}
//    	if(decorationImageid == 0){
//    		decorationImageid = R.drawable.girl_angry_deco3;
//     	}
//	        // 防止出现Immutable bitmap passed to Canvas constructor错误
//    	  Bitmap angryBodyBm = BitmapFactory.decodeResource(ctx.getResources(),
//    			  angryBodyId).copy(Bitmap.Config.ARGB_8888, true);
//    	   Bitmap clothesImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//    			   clothesImageid)).getBitmap(); 
//    	
//    	
//    	
//    	
//	        Bitmap newBitmap = null;
//
//	        newBitmap = Bitmap.createBitmap(angryBodyBm);
//	        Canvas canvas = new Canvas(newBitmap);
//	        Paint paint = new Paint();
//
//	        int w = angryBodyBm.getWidth();
//	        int h = angryBodyBm.getHeight();
//
//	        int w_2 = clothesImageBm.getWidth();
//	        int h_2 =clothesImageBm.getHeight();
//
//	        paint.setColor(Color.TRANSPARENT);
//	        paint.setAlpha(0);
//	        canvas.drawRect(0, 0, w, h, paint);
//	        paint = new Paint();
//	     
//	        canvas.drawBitmap(clothesImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        
////	        clothesImageBm.recycle();
//	        clothesImageBm = null;
//	          
//	        angryBodyBm.recycle();
//	        angryBodyBm = null;
//	        
//	        Bitmap hairImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	        		hairImageid)).getBitmap(); 
//	        canvas.drawBitmap(hairImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        hairImageBm =null;
//	        
//	     
//	        
//	        Bitmap decorationImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		decorationImageid)).getBitmap();
//	        canvas.drawBitmap(decorationImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	     
//	        canvas.save(Canvas.ALL_SAVE_FLAG);
//	        // 存储新合成的图片
//	        canvas.restore();
//
//	        AppManagerUtil.writeToSD(path,newBitmap,bitName);      
//           ctx = null;
//           this.isGirlAngryInit = true;
//           return newBitmap;
//	    }
//		 return null;
//	 }
//	 
//	 //---------------------------------------girl missing---------------------------------------------
//	 public  Bitmap  initGirlMiss(Context ctx) {
//
//		 String bitName = "girlmiss";
//		 boolean isGirlInitTemp =this.isGirlMissInit;
//		 if(isGirlInitTemp){
//			 String pathString = Environment.getExternalStorageDirectory()  
//		                + "/LoverHouse"+path+"/"+bitName+".png";
//			 Bitmap girlMissBm = AppManagerUtil.getDiskBitmap(pathString);
//			 if(girlMissBm == null){
//				 isGirlInitTemp = false;
//			 }else{
//				 return girlMissBm;
//			 }
//			 
//		 }
//		 if(!(isGirlInitTemp)){
//		 String hairImageName = "girl_miss_hair"+(byte)girlAppearance.charAt(1);
// 		String clothesImageName= "girl_miss_clothes"+(byte)girlAppearance.charAt(2);
// 		String decorationImageName =  "girl_miss_decoation"+(byte)girlAppearance.charAt(3);
// 		
// 		//固定id
// 		int missBodyId = R.drawable.girl_body_missing;
// 		int clothesImageid = getImageid(clothesImageName,ctx);
// 		int missHandId = R.drawable.girl_miss_hand;
// 		int missHeadId = R.drawable.girl_miss_head;
//      	int hairImageid =getImageid(hairImageName,ctx);
//     	int decorationImageid  = getImageid(decorationImageName,ctx);
//     	if(hairImageid == 0){
//     		hairImageid = R.drawable.girl_miss_hair1;
//     	}
//    	if(clothesImageid == 0){
//    		clothesImageid = R.drawable.girl_miss_clothes1;
//     	}
//    	if(decorationImageid == 0){
//    		decorationImageid = R.drawable.girl_miss_decoation1;
//     	}
//	        // 防止出现Immutable bitmap passed to Canvas constructor错误
//    	  Bitmap missBodyBm = BitmapFactory.decodeResource(ctx.getResources(),
//    			  missBodyId).copy(Bitmap.Config.ARGB_8888, true);
//    	   Bitmap clothesImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//    			   clothesImageid)).getBitmap(); 
//
//	        Bitmap newBitmap = null;
//
//	        newBitmap = Bitmap.createBitmap(missBodyBm);
//	        Canvas canvas = new Canvas(newBitmap);
//	        Paint paint = new Paint();
//
//	        int w = missBodyBm.getWidth();
//	        int h = missBodyBm.getHeight();
//
//	        int w_2 = clothesImageBm.getWidth();
//	        int h_2 =clothesImageBm.getHeight();
//
//	        paint.setColor(Color.TRANSPARENT);
//	        paint.setAlpha(0);
//	        canvas.drawRect(0, 0, w, h, paint);
//	        paint = new Paint();
//	     
//	        canvas.drawBitmap(clothesImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        
////	        clothesImageBm.recycle();
//	        clothesImageBm = null;
//	          
//	        missBodyBm.recycle();
//	        missBodyBm = null;
//	        
//	        Bitmap missHandBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	        		missHandId)).getBitmap(); 
//	        canvas.drawBitmap(missHandBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        missHandBm =null;
//	        
//	        Bitmap  missHeadBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	        		 missHeadId)).getBitmap(); 
//	        canvas.drawBitmap( missHeadBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        missHeadBm =null;
//	        
//	       
//	        
//	        Bitmap hairImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	        		hairImageid)).getBitmap(); 
//	        canvas.drawBitmap(hairImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        hairImageBm =null;
//	        
//	     
//	        
//	        Bitmap decorationImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		decorationImageid)).getBitmap();
//	        canvas.drawBitmap(decorationImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	     
//	        canvas.save(Canvas.ALL_SAVE_FLAG);
//	        // 存储新合成的图片
//	        canvas.restore();
//
//	        AppManagerUtil.writeToSD(path,newBitmap,bitName);      
//           ctx = null;
//           this.isGirlMissInit = true;
//           return newBitmap;
//	    }
//		 return null;
//	 }
//	 //-----------------------------------------boy------------------------------------------------
//	 public  Bitmap  initBoy(Context ctx) {
//
//		 String bitName = "boy";
//		 boolean isBoyInitTemp = this.isBoyInit;
//		 if(isBoyInitTemp){
//			 String pathString = Environment.getExternalStorageDirectory()  
//		                + "/LoverHouse"+path+"/"+bitName+".png";
//			 Bitmap boyBm = AppManagerUtil.getDiskBitmap(pathString);
//			 if(boyBm == null){
//				 isBoyInitTemp = false;
//			 }else{
//				 return boyBm;
//			 }
//			 
//		 }
//		 if(!(isBoyInitTemp)){
//		 String hairImageName = "boy_hair_to_right"+(byte)boyAppearance.charAt(1);
// 		String clothesImageName= "boy_clothes_to_right"+(byte)boyAppearance.charAt(2);
// 		String decorationImageName =  "boy_decoration_to_right"+(byte)boyAppearance.charAt(3);
//      	int hairImageid =getImageid(hairImageName,ctx);
//     	int clothesImageid = getImageid(clothesImageName,ctx);
//     	int decorationImageid  = getImageid(decorationImageName,ctx);
//     	if(hairImageid == 0){
//     		hairImageid = R.drawable.boy_hair_to_right1;
//     	}
//    	if(clothesImageid == 0){
//    		clothesImageid = R.drawable.boy_clothes_to_right1;
//     	}
//    	if(decorationImageid == 0){
//    		decorationImageid = R.drawable.boy_decoration_to_right1;
//     	}
//	        // 防止出现Immutable bitmap passed to Canvas constructor错误
//    	   Bitmap hairImageBm = BitmapFactory.decodeResource(ctx.getResources(),
//    			   hairImageid).copy(Bitmap.Config.ARGB_8888, true);
//    	    Bitmap clothesImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		clothesImageid)).getBitmap();
//	        Bitmap newBitmap = null;
//
//	        newBitmap = Bitmap.createBitmap(hairImageBm);
//	        Canvas canvas = new Canvas(newBitmap);
//	        Paint paint = new Paint();
//
//	        int w = hairImageBm.getWidth();
//	        int h = hairImageBm.getHeight();
//
//	        int w_2 = clothesImageBm.getWidth();
//	        int h_2 = clothesImageBm.getHeight();
//
//	        paint.setColor(Color.TRANSPARENT);
//	        paint.setAlpha(0);
//	        canvas.drawRect(0, 0, w, h, paint);
//	        paint = new Paint();
//	     
//	        canvas.drawBitmap(clothesImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        
////	        clothesImageBm.recycle();
//	        clothesImageBm = null;
//	          
//	         hairImageBm.recycle();
//	         hairImageBm = null;
//	        
//	    
//	        Bitmap decorationImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		decorationImageid)).getBitmap();
//	      
//	        canvas.drawBitmap(decorationImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	     
//	        canvas.save(Canvas.ALL_SAVE_FLAG);
//	        // 存储新合成的图片
//	        canvas.restore();
//
//	        AppManagerUtil.writeToSD(path,newBitmap,bitName);      
////           decorationImageBm.recycle();
//           decorationImageBm = null;
//           
//           ctx = null;
//           this.isBoyInit = true;
//           return newBitmap;
//	    }
//		 return null;
//	 }
//	 //-----------------------------------------boy eat------------------------------------------------
//	 public  Bitmap  initBoyEatting(Context ctx) {
//
//		 String bitName = "boyeatting";
//		 boolean isGirlInitTemp =this.isBoyEatingInit;
//		 if(isGirlInitTemp){
//			 String pathString = Environment.getExternalStorageDirectory()  
//		                + "/LoverHouse"+path+"/"+bitName+".png";
//			 Bitmap boyEatBm = AppManagerUtil.getDiskBitmap(pathString);
//			 if(boyEatBm == null){
//				 isGirlInitTemp = false;
//			 }else{
//				 return boyEatBm;
//			 }
//			 
//		 }
//		 if(!(isGirlInitTemp)){
//		 String hairImageName = "boy_eat_hair0"+(byte)boyAppearance.charAt(1);
// 		String clothesImageName= "boy_eatcloth0"+(byte)boyAppearance.charAt(2);
// 		String decorationImageName =  "boy_eat_decoration0"+(byte)boyAppearance.charAt(3);
// 		
// 		//固定id
// 		int eatChairId = R.drawable.boy_eat_chair;
// 		int eatBodyId = R.drawable.boy_eat_body;
// 		int eatFaceId= R.drawable.boy_eat_face;
// 		int eatRightHandId = R.drawable.boy_eat_right_hand;
// 		
//      	int hairImageid =getImageid(hairImageName,ctx);
//     	int clothesImageid = getImageid(clothesImageName,ctx);
//     	int decorationImageid  = getImageid(decorationImageName,ctx);
//     	if(hairImageid == 0){
//     		hairImageid = R.drawable.boy_eat_hair01;
//     	}
//    	if(clothesImageid == 0){
//    		clothesImageid = R.drawable.boy_eatcloth01;
//     	}
//    	if(decorationImageid == 0){
//    		decorationImageid = R.drawable.boy_eat_decoration01;
//     	}
//	        // 防止出现Immutable bitmap passed to Canvas constructor错误
//    	  Bitmap eatChairBm = BitmapFactory.decodeResource(ctx.getResources(),
//    			  eatChairId).copy(Bitmap.Config.ARGB_8888, true);
//    	   Bitmap eatBodyBm =((BitmapDrawable)ctx.getResources().getDrawable(
//    			   eatBodyId)).getBitmap(); 
//    	
//    	
//    	
//    	
//	        Bitmap newBitmap = null;
//
//	        newBitmap = Bitmap.createBitmap(eatChairBm);
//	        Canvas canvas = new Canvas(newBitmap);
//	        Paint paint = new Paint();
//
//	        int w = eatChairBm.getWidth();
//	        int h = eatChairBm.getHeight();
//
//	        int w_2 = eatBodyBm.getWidth();
//	        int h_2 = eatBodyBm.getHeight();
//
//	        paint.setColor(Color.TRANSPARENT);
//	        paint.setAlpha(0);
//	        canvas.drawRect(0, 0, w, h, paint);
//	        paint = new Paint();
//	     
//	        canvas.drawBitmap(eatBodyBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        
////	        clothesImageBm.recycle();
//	        eatBodyBm = null;
//	          
//	        eatChairBm.recycle();
//	        eatChairBm = null;
//	        
//	        Bitmap eatFaceBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	    			   eatFaceId)).getBitmap(); 
//	        canvas.drawBitmap(eatFaceBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        eatFaceBm =null;
//	        
//	       
//	        
//	        Bitmap hairImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	    			   hairImageid)).getBitmap(); 
//	        canvas.drawBitmap(hairImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        hairImageBm = null;
//	        
//	        Bitmap clothesImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		clothesImageid)).getBitmap();
//	        canvas.drawBitmap(clothesImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        clothesImageBm = null;
//	        
//	        Bitmap decorationImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		decorationImageid)).getBitmap();
//	        canvas.drawBitmap(decorationImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        decorationImageBm = null;
//	        Bitmap eatRightHandBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	    			   eatRightHandId)).getBitmap(); 
//	        canvas.drawBitmap(eatRightHandBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        eatRightHandBm = null;
//	        canvas.save(Canvas.ALL_SAVE_FLAG);
//	        // 存储新合成的图片
//	        canvas.restore();
//
//	        AppManagerUtil.writeToSD(path,newBitmap,bitName);      
//           ctx = null;
//           this.isBoyEatingInit = true;
//           return newBitmap;
//	    }
//		 return null;
//	 }
//	 
//	 //---------------------------------------boy study---------------------------------------------
//	 public  Bitmap  initBoyStudy(Context ctx) {
//
//		 String bitName = "boystudy";
//		 boolean isGirlInitTemp =this.isBoyStudyInit;
//		 if(isGirlInitTemp){
//			 String pathString = Environment.getExternalStorageDirectory()  
//		                + "/LoverHouse"+path+"/"+bitName+".png";
//			 Bitmap boyStudyBm = AppManagerUtil.getDiskBitmap(pathString);
//			 if(boyStudyBm == null){
//				 isGirlInitTemp = false;
//			 }else{
//				 return boyStudyBm;
//			 }
//			 
//		 }
//		 if(!(isGirlInitTemp)){
//		 String hairImageName = "boy_learn_hair0"+(byte)boyAppearance.charAt(1);
// 		String clothesImageName= "boy_learn_clothes0"+(byte)boyAppearance.charAt(2);
// 		String decorationImageName =  "boy_learn_decoration0"+(byte)boyAppearance.charAt(3);
// 		
// 		//固定id
// 		int studyChairId = R.drawable.boy_learn_chair;
// 		int studyBodyId = R.drawable.boy_learn_body;
// 		int studyFaceId= R.drawable.boy_learn_face;
// 		
//      	int hairImageid =getImageid(hairImageName,ctx);
//     	int clothesImageid = getImageid(clothesImageName,ctx);
//     	int decorationImageid  = getImageid(decorationImageName,ctx);
//     	if(hairImageid == 0){
//     		hairImageid = R.drawable.boy_learn_hair01;
//     	}
//    	if(clothesImageid == 0){
//    		clothesImageid = R.drawable.boy_learn_clothes01;
//     	}
//    	if(decorationImageid == 0){
//    		decorationImageid = R.drawable.boy_learn_decoration01;
//     	}
//	        // 防止出现Immutable bitmap passed to Canvas constructor错误
//    	  Bitmap studyChairBm = BitmapFactory.decodeResource(ctx.getResources(),
//    			  studyChairId).copy(Bitmap.Config.ARGB_8888, true);
//    	   Bitmap studyBodyBm =((BitmapDrawable)ctx.getResources().getDrawable(
//    			   studyBodyId)).getBitmap(); 
//    	
//    	
//    	
//    	
//	        Bitmap newBitmap = null;
//
//	        newBitmap = Bitmap.createBitmap(studyChairBm);
//	        Canvas canvas = new Canvas(newBitmap);
//	        Paint paint = new Paint();
//
//	        int w = studyChairBm.getWidth();
//	        int h = studyChairBm.getHeight();
//
//	        int w_2 = studyBodyBm.getWidth();
//	        int h_2 = studyBodyBm.getHeight();
//
//	        paint.setColor(Color.TRANSPARENT);
//	        paint.setAlpha(0);
//	        canvas.drawRect(0, 0, w, h, paint);
//	        paint = new Paint();
//	     
//	        canvas.drawBitmap(studyBodyBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        
////	        clothesImageBm.recycle();
//	        studyBodyBm = null;
//	          
//	        studyChairBm.recycle();
//	        studyChairBm = null;
//	        
//	        Bitmap studyFaceBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	        		studyFaceId)).getBitmap(); 
//	        canvas.drawBitmap(studyFaceBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        studyFaceBm =null;
//	        
//	     
//	        
//	        Bitmap hairImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	    			   hairImageid)).getBitmap(); 
//	        canvas.drawBitmap(hairImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        hairImageBm = null;
//	        
//	        Bitmap clothesImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		clothesImageid)).getBitmap();
//	        canvas.drawBitmap(clothesImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        clothesImageBm = null;
//	        
//	        Bitmap decorationImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		decorationImageid)).getBitmap();
//	        canvas.drawBitmap(decorationImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	     
//	        canvas.save(Canvas.ALL_SAVE_FLAG);
//	        // 存储新合成的图片
//	        canvas.restore();
//	        decorationImageBm = null;
//	        AppManagerUtil.writeToSD(path,newBitmap,bitName);      
//           ctx = null;
//           this.isBoyStudyInit = true;
//           return newBitmap;
//	    }
//		 return null;
//	 }
//	 
//	 //---------------------------------------boy angry---------------------------------------------
//	 public  Bitmap  initBoyAngry(Context ctx) {
//
//		 String bitName = "boyangry";
//		 boolean isGirlInitTemp =this.isBoyAngryInit;
//		 if(isGirlInitTemp){
//			 String pathString = Environment.getExternalStorageDirectory()  
//		                + "/LoverHouse"+path+"/"+bitName+".png";
//			 Bitmap boyAngryBm = AppManagerUtil.getDiskBitmap(pathString);
//			 if(boyAngryBm == null){
//				 isGirlInitTemp = false;
//			 }else{
//				 return boyAngryBm;
//			 }
//			 
//		 }
//		 if(!(isGirlInitTemp)){
//		 String hairImageName = "boy_back_hair0"+(byte)boyAppearance.charAt(1);
// 		String clothesImageName= "boy_angrycloth0"+(byte)boyAppearance.charAt(2);
//// 		String decorationImageName =  "girl_angry_deco"+(byte)boyAppearance.charAt(3);
// 		
// 		//固定id
// 		int angryBodyId = R.drawable.boy_body_angry;
// 
//      	int hairImageid =getImageid(hairImageName,ctx);
//     	int clothesImageid = getImageid(clothesImageName,ctx);
////     	int decorationImageid  = getImageid(decorationImageName,ctx);
//     	if(hairImageid == 0){
//     		hairImageid = R.drawable.boy_back_hair01;
//     	}
//    	if(clothesImageid == 0){
//    		clothesImageid = R.drawable.boy_angrycloth01;
//     	}
////    	if(decorationImageid == 0){
////    		decorationImageid = R.drawable.girl_angry_deco3;
////     	}
//	        // 防止出现Immutable bitmap passed to Canvas constructor错误
//    	  Bitmap angryBodyBm = BitmapFactory.decodeResource(ctx.getResources(),
//    			  angryBodyId).copy(Bitmap.Config.ARGB_8888, true);
//    	   Bitmap clothesImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//    			   clothesImageid)).getBitmap(); 
//    	
//    	
//    	
//    	
//	        Bitmap newBitmap = null;
//
//	        newBitmap = Bitmap.createBitmap(angryBodyBm);
//	        Canvas canvas = new Canvas(newBitmap);
//	        Paint paint = new Paint();
//
//	        int w = angryBodyBm.getWidth();
//	        int h = angryBodyBm.getHeight();
//
//	        int w_2 = clothesImageBm.getWidth();
//	        int h_2 =clothesImageBm.getHeight();
//
//	        paint.setColor(Color.TRANSPARENT);
//	        paint.setAlpha(0);
//	        canvas.drawRect(0, 0, w, h, paint);
//	        paint = new Paint();
//	     
//	        canvas.drawBitmap(clothesImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        
////	        clothesImageBm.recycle();
//	        clothesImageBm = null;
//	          
////	        angryBodyBm.recycle();
//	        angryBodyBm = null;
//	        
//	        Bitmap hairImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	        		hairImageid)).getBitmap(); 
//	        canvas.drawBitmap(hairImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        hairImageBm =null;
//	        
//	     
//	        
////	        Bitmap decorationImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
////	        		decorationImageid)).getBitmap();
////	        canvas.drawBitmap(decorationImageBm, Math.abs(w - w_2) / 2,
////	                Math.abs(h - h_2) / 2, paint);
////	     
//	        canvas.save(Canvas.ALL_SAVE_FLAG);
//	        // 存储新合成的图片
//	        canvas.restore();
//	        AppManagerUtil.writeToSD(path,newBitmap,bitName);      
//           ctx = null;
//           this.isBoyAngryInit = true;
//           return newBitmap;
//	    }
//		 return null;
//	 }
//	 
//	 //---------------------------------------boy missing---------------------------------------------
//	 public  Bitmap  initBoyMiss(Context ctx) {
//
//		 String bitName = "boymiss";
//		 boolean isGirlInitTemp =this.isBoyMissInit;
//		 if(isGirlInitTemp){
//			 String pathString = Environment.getExternalStorageDirectory()  
//		                + "/LoverHouse"+path+"/"+bitName+".png";
//			 Bitmap boyMissBm = AppManagerUtil.getDiskBitmap(pathString);
//			 if(boyMissBm == null){
//				 isGirlInitTemp = false;
//			 }else{
//				 return boyMissBm;
//			 }
//			 
//		 }
//		 if(!(isGirlInitTemp)){
//		 String hairImageName = "boy_miss_hair0"+(byte)girlAppearance.charAt(1);
// 		String clothesImageName= "boy_miss_clothes0"+(byte)girlAppearance.charAt(2);
// 		String decorationImageName =  "boy_stand_decoration0"+(byte)girlAppearance.charAt(3);
// 		
// 		//固定id
// 		int missBodyId = R.drawable.boy_miss_body;
// 		int clothesImageid = getImageid(clothesImageName,ctx);
// 		int missHandId = R.drawable.boy_miss_hand;
// 		int missHeadId = R.drawable.boy_miss_head;
//      	int hairImageid =getImageid(hairImageName,ctx);
//     	int decorationImageid  = getImageid(decorationImageName,ctx);
//     	if(hairImageid == 0){
//     		hairImageid = R.drawable.boy_miss_hair01;
//     	}
//    	if(clothesImageid == 0){
//    		clothesImageid = R.drawable.boy_miss_clothes01;
//     	}
//    	if(decorationImageid == 0){
//    		decorationImageid = R.drawable.boy_stand_decoration01;
//     	}
//	        // 防止出现Immutable bitmap passed to Canvas constructor错误
//    	  Bitmap missBodyBm = BitmapFactory.decodeResource(ctx.getResources(),
//    			  missBodyId).copy(Bitmap.Config.ARGB_8888, true);
//    	   Bitmap clothesImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//    			   clothesImageid)).getBitmap(); 
//
//	        Bitmap newBitmap = null;
//
//	        newBitmap = Bitmap.createBitmap(missBodyBm);
//	        Canvas canvas = new Canvas(newBitmap);
//	        Paint paint = new Paint();
//
//	        int w = missBodyBm.getWidth();
//	        int h = missBodyBm.getHeight();
//
//	        int w_2 = clothesImageBm.getWidth();
//	        int h_2 =clothesImageBm.getHeight();
//
//	        paint.setColor(Color.TRANSPARENT);
//	        paint.setAlpha(0);
//	        canvas.drawRect(0, 0, w, h, paint);
//	        paint = new Paint();
//	     
//	        canvas.drawBitmap(clothesImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        
////	        clothesImageBm.recycle();
//	        clothesImageBm = null;
//	          
////	        missBodyBm.recycle();
//	        missBodyBm = null;
//	        
//	        Bitmap missHandBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	        		missHandId)).getBitmap(); 
//	        canvas.drawBitmap(missHandBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        missHandBm =null;
//	        
//	        Bitmap  missHeadBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	        		 missHeadId)).getBitmap(); 
//	        canvas.drawBitmap( missHeadBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        missHeadBm =null;
//	        
//	       
//	        
//	        Bitmap hairImageBm =((BitmapDrawable)ctx.getResources().getDrawable(
//	        		hairImageid)).getBitmap(); 
//	        canvas.drawBitmap(hairImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	        hairImageBm =null;
//	        
//	     
//	        
//	        Bitmap decorationImageBm = ((BitmapDrawable) ctx.getResources().getDrawable(
//	        		decorationImageid)).getBitmap();
//	        canvas.drawBitmap(decorationImageBm, Math.abs(w - w_2) / 2,
//	                Math.abs(h - h_2) / 2, paint);
//	     
//	        canvas.save(Canvas.ALL_SAVE_FLAG);
//	        // 存储新合成的图片
//	        canvas.restore();
//	        decorationImageBm = null;
//	        AppManagerUtil.writeToSD(path,newBitmap,bitName);      
//           ctx = null;
//           this.isBoyMissInit = true;
//           return newBitmap;
//	    }
//		 return null;
//	 }
//	 
//	// -------------------------------couple action start
//		// --------------------------------
//
//		// ------------------------------couple action
//		// abuse-------------------------------
//		// girl 专用
//		private int[] abuseAppID = { 0, 0, 0, 0, 0, 0 };
//
//		public LayerDrawable initCoupleAbuse(Context ctx) {
//			LayerDrawable myLayerDrawable;
//			Resources r = ctx.getResources();
//			Drawable[] mylayers = new Drawable[6];
//			boolean isGirlInitTemp = this.isAbuseInit;
//
//			if (!(isGirlInitTemp)) {
//				String hairImageName = "girl_abuse_hair"
//						+ (byte) girlAppearance.charAt(1);
//				String clothesImageName = "girl_abuse_clothes"
//						+ (byte) girlAppearance.charAt(2);
//
//				// 固定id
//				int boyBodyId = R.drawable.boy_abuse_body;
//				int girlHeadiId = R.drawable.girl_abuse_head;
//				int girlBodyId = R.drawable.girl_abuse_body;
//
//				int clothesImageid = getImageid(clothesImageName, ctx);
//
//				int hairImageid = getImageid(hairImageName, ctx);
//
//				int girlFaceId = R.drawable.girl_abuse_face;
//				if (hairImageid == 0) {
//					hairImageid = R.drawable.girl_abuse_hair1;
//				}
//				if (clothesImageid == 0) {
//					clothesImageid = R.drawable.girl_abuse_clothes1;
//				}
//
//				abuseAppID[0] = boyBodyId;
//				abuseAppID[1] = girlHeadiId;
//				abuseAppID[2] = girlBodyId;
//				abuseAppID[3] = clothesImageid;
//				abuseAppID[4] = hairImageid;
//				abuseAppID[5] = girlFaceId;
//				this.isAbuseInit = true;
//			}
//
//			mylayers[0] = r.getDrawable(abuseAppID[0]);
//			mylayers[1] = r.getDrawable(abuseAppID[1]);
//			mylayers[2] = r.getDrawable(abuseAppID[2]);
//			mylayers[3] = r.getDrawable(abuseAppID[3]);
//			mylayers[4] = r.getDrawable(abuseAppID[4]);
//			mylayers[5] = r.getDrawable(abuseAppID[5]);
//
//			myLayerDrawable = new LayerDrawable(mylayers);
//
//			return myLayerDrawable;
//		}
//
//		// ------------------------------couple action 捏脸
//		// 摸头-------------------------------
//		private int[] pGirlHeadOrFaceAppID = { 0, 0 };
//		private int[] pBoyHeadOrFaceAppID = { 0, 0 };
//
//		/**
//		 * 
//		 * @param ctx
//		 * @param isBoy
//		 *            发起动作的那一方
//		 * @return
//		 */
//		public LayerDrawable initPFaceOrPHead(Context ctx, boolean isBoy) {
//			LayerDrawable myLayerDrawable;
//			Resources r = ctx.getResources();
//			Drawable[] mylayers = new Drawable[2];
//			if (isBoy) {
//				boolean isGirlInitTemp = this.isPFHGirlInit;
//
//				if (!(isGirlInitTemp)) {
//					String hairImageName = "girl_pinchedface_hair"
//							+ (byte) girlAppearance.charAt(1);
//					String clothesImageName = "girl_pinchedface_clothes"
//							+ (byte) girlAppearance.charAt(2);
//
//					int clothesImageid = getImageid(clothesImageName, ctx);
//
//					int hairImageid = getImageid(hairImageName, ctx);
//
//					if (hairImageid == 0) {
//						hairImageid = R.drawable.girl_pinchedface_hair1;
//					}
//					if (clothesImageid == 0) {
//						clothesImageid = R.drawable.girl_pinchedface_clothes1;
//					}
//
//					pGirlHeadOrFaceAppID[0] = clothesImageid;
//					pGirlHeadOrFaceAppID[1] = hairImageid;
//
//					this.isPFHGirlInit = true;
//				}
//
//				mylayers[0] = r.getDrawable(pGirlHeadOrFaceAppID[0]);
//				mylayers[1] = r.getDrawable(pGirlHeadOrFaceAppID[1]);
//				myLayerDrawable = new LayerDrawable(mylayers);
//
//			} else {
//				// 男生形象
//				boolean isGirlInitTemp = this.isPFHBoyInit;
//
//				if (!(isGirlInitTemp)) {
//					String hairImageName = "boy_pinchedface_hair"
//							+ (byte) boyAppearance.charAt(1);
//					String clothesImageName = "boy_pinchedface_clothes"
//							+ (byte) boyAppearance.charAt(2);
//
//					int clothesImageid = getImageid(clothesImageName, ctx);
//
//					int hairImageid = getImageid(hairImageName, ctx);
//
//					if (hairImageid == 0) {
//						hairImageid = R.drawable.boy_pinchedface_hair1;
//					}
//					if (clothesImageid == 0) {
//						clothesImageid = R.drawable.boy_pinchedface_clothes1;
//					}
//
//					pBoyHeadOrFaceAppID[0] = clothesImageid;
//					pBoyHeadOrFaceAppID[1] = hairImageid;
//
//					this.isPFHBoyInit = true;
//				}
//
//				mylayers[0] = r.getDrawable(pBoyHeadOrFaceAppID[0]);
//				mylayers[1] = r.getDrawable(pBoyHeadOrFaceAppID[1]);
//				myLayerDrawable = new LayerDrawable(mylayers);
//			}
//
//			return myLayerDrawable;
//		}
//
//		// ------------------------------couple action
//		// kiss------------------------------
//		private int[] kissAppID = { 0, 0, 0, 0 };
//
//		public LayerDrawable initKiss(Context ctx) {
//
//			LayerDrawable myLayerDrawable;
//			Resources r = ctx.getResources();
//			Drawable[] mylayers = new Drawable[6];
//			boolean isGirlInitTemp = this.isKissInit;
//
//			if (!(isGirlInitTemp)) {
//				String hairImageName = "girl_kiss_head0"
//						+ (byte) girlAppearance.charAt(1);
//				String clothesImageName = "girl_kiss_body_"
//						+ (byte) girlAppearance.charAt(2);
//
//				String boyhairImageName = "boy_kiss_head0"
//						+ (byte) boyAppearance.charAt(1);
//				String boyclothesImageName = "boy_kiss_body0"
//						+ (byte) boyAppearance.charAt(2);
//
//				// 固定id
//				int boyBodyId = R.drawable.boy_abuse_body;
//				int girlHeadiId = R.drawable.girl_abuse_head;
//				int girlBodyId = R.drawable.girl_abuse_body;
//
//				int clothesImageid = getImageid(clothesImageName, ctx);
//				int hairImageid = getImageid(hairImageName, ctx);
//				int boyclothesImageid = getImageid(boyclothesImageName, ctx);
//				int boyhairImageid = getImageid(boyhairImageName, ctx);
//
//				if (hairImageid == 0) {
//					hairImageid = R.drawable.girl_kiss_head1;
//				}
//				if (clothesImageid == 0) {
//					clothesImageid = R.drawable.girl_kiss_body_17;
//				}
//				if (boyhairImageid == 0) {
//					boyhairImageid = R.drawable.boy_kiss_head1;
//				}
//				if (boyclothesImageid == 0) {
//					boyclothesImageid = R.drawable.boy_kiss_body1;
//				}
//
//				kissAppID[0] = boyBodyId;
//				kissAppID[1] = girlHeadiId;
//				kissAppID[2] = girlBodyId;
//				kissAppID[3] = clothesImageid;
//
//				this.isKissInit = true;
//			}
//
//			mylayers[0] = r.getDrawable(kissAppID[0]);
//			mylayers[1] = r.getDrawable(kissAppID[1]);
//			mylayers[2] = r.getDrawable(kissAppID[2]);
//			mylayers[3] = r.getDrawable(kissAppID[3]);
//
//			myLayerDrawable = new LayerDrawable(mylayers);
//
//			return myLayerDrawable;
//
//		}
//
//		// ------------------------------couple action
//		// hug------------------------------
//		private int[] hugAppID = { 0, 0, 0, 0,0,0,0 ,0,0};
//
//		public LayerDrawable initHug(Context ctx) {
//			LayerDrawable myLayerDrawable;
//			Resources r = ctx.getResources();
//			Drawable[] mylayers = new Drawable[9];
//			boolean isGirlInitTemp = this.isHugInit;
//
//			if (!(isGirlInitTemp)) {
//				String hairImageName = "girl_hug_hair"
//						+ (byte) girlAppearance.charAt(1);
//				String clothesImageName = "girl_hug_body"
//						+ (byte) girlAppearance.charAt(2);
//
//				String boyhairImageName = "boy_hug_hair"
//						+ (byte) boyAppearance.charAt(1);
//				String boyclothesImageName = "boy_hug_clothes"
//						+ (byte) boyAppearance.charAt(2);
//
//				String boyHugLeftHandName = "boy_hug_lefthand"
//						+ (byte) boyAppearance.charAt(2);
//
//				// 固定id
//				int boyHeadId = R.drawable.boy_hug_head;
//				int boyhairImageid = getImageid(boyhairImageName, ctx);
//				int boyBodyId = R.drawable.boy_hug_body;
//				int boyclothesImageid = getImageid(boyclothesImageName, ctx);
//				int boyRightHandid = R.drawable.boy_hug_righthand;
//				int boyLeftHandId =  getImageid(boyHugLeftHandName, ctx);
//				
//				int girlHeadiId =R.drawable.girl_hug_head;
//				int hairImageid = getImageid(hairImageName, ctx);
//				int clothesImageid = getImageid(clothesImageName, ctx);
//				
//				
//			
//
//				if (hairImageid == 0) {
//					hairImageid = R.drawable.girl_hug_hair1;
//				}
//				if (clothesImageid == 0) {
//					clothesImageid = R.drawable.girl_hug_body13;
//				}
//				if (boyhairImageid == 0) {
//					boyhairImageid = R.drawable.boy_hug_hair1;
//				}
//				if (boyclothesImageid == 0) {
//					boyclothesImageid = R.drawable.boy_hug_clothes1;
//				}
//				if(boyLeftHandId == 0){
//					boyLeftHandId = R.drawable.boy_hug_lefthand1;
//				}
//
//				hugAppID[0] = boyHeadId;
//				hugAppID[1] = boyhairImageid;
//				hugAppID[2] = boyBodyId;
//				hugAppID[3] = boyclothesImageid;
//				hugAppID[8] = boyRightHandid;
//				hugAppID[7] =boyLeftHandId;
//				hugAppID[4] =girlHeadiId;
//				hugAppID[5] = hairImageid;
//				hugAppID[6] = clothesImageid;
//			
//
//				this.isHugInit = true;
//			}
//
//			mylayers[0] = r.getDrawable(hugAppID[0]);
//			mylayers[1] = r.getDrawable(hugAppID[1]);
//			mylayers[2] = r.getDrawable(hugAppID[2]);
//			mylayers[3] = r.getDrawable(hugAppID[3]);
//
//			mylayers[4] = r.getDrawable(hugAppID[4]);
//			mylayers[5] = r.getDrawable(hugAppID[5]);
//			mylayers[6] = r.getDrawable(hugAppID[6]);
//			mylayers[7] = r.getDrawable(hugAppID[7]);
//			mylayers[8] = r.getDrawable(hugAppID[8]);
//
//			myLayerDrawable = new LayerDrawable(mylayers);
//
//			return myLayerDrawable;
//
//		}
}
