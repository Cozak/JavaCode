package com.minus.lovershouse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.minius.chat.imagedetail.ImagePagerActivity;
import com.minius.common.CommonBitmap;
import com.minus.lovershouse.BuildConfig;
import com.minus.lovershouse.R;

import com.minus.lovershouse.face.FaceConversionUtil;
import com.minus.lovershouse.singleton.GlobalApplication;
import com.minus.lovershouse.singleton.SelfInfo;
import com.minus.lovershouse.util.AppManagerUtil;
import com.minus.lovershouse.util.FileUtil;
import com.minus.lovershouse.util.RTPullListView;
import com.minus.lovershouse.util.RoundedImageView;
import com.minus.sql_interface.Database;
import com.minus.table.ChatTable;
import com.minus.xsocket.asynsocket.protocol.Protocol;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

//all_phonetics_stop
public class ChatListFragment extends Fragment {

	private static final String TAG = "ChatActivity";
	private ArrayList<Integer> urls = new ArrayList<Integer>();
	DisplayImageOptions options;
	OnSetListViewListener mListener;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	private RTPullListView mListView;
	private static List<ChatTable> mDataArrays = new ArrayList<ChatTable>();
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private ChatMsgViewAdapter mAdapter;



	/**
	 * Empty constructor as per the Fragment documentation
	 */

	public ChatListFragment() {

	}

	// Container Activity must implement this interface
	public interface OnSetListViewListener {
		public void setListView(RTPullListView mListView,
				ChatMsgViewAdapter mAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onAttach ");
		}
		try {
			mListener = (OnSetListViewListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSetListViewListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, " onCreate in fragment  ");
		}
		
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisk(false)
		.considerExifParams(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, " onCreateview  in fragment  ");
		}
		final View v = inflater.inflate(R.layout.chat_list_fragment, container,
				false);
		mListView = (RTPullListView) v.findViewById(R.id.chatlistview);

		boolean pauseOnScroll = true; // or true
		boolean pauseOnFling = true; // or false
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);
		mListView.setOnScrollListener(listener);


		initData();
		return v;
	}

	public void initData() {

		mDataArrays.clear();
		mAdapter = new ChatMsgViewAdapter(getActivity(), mDataArrays);
		mListView.setAdapter(mAdapter);
		mAdapter.setListView(mListView);

		this.mListener.setListView(mListView, mAdapter);
		

//		// init list data from db
//
//		List<ChatTable> preChatList = Database.getInstance(
//				getActivity().getApplicationContext()).getLastChat(0, 30);
//		
//		for (int i =0; i <   preChatList.size(); i++) {
//		
//			ChatTable mPreChatItem = preChatList.get(i);
//		    
//			mDataArrays.add(mPreChatItem);
//		}
//		mAdapter.notifyDataSetChanged();
//		mListView.setSelection(mListView.getCount() - 1);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

//		if (BuildConfig.DEBUG) {
//			Log.d(TAG, " onActivityCreated  in fragment  ");
//		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// init list data from db
	if(	!(GlobalApplication.getInstance().isSelectPic())){
				List<ChatTable> preChatList = Database.getInstance(
						getActivity().getApplicationContext()).getLastChat(0, 30);
				mDataArrays.clear();
				for (int i =0; i <   preChatList.size(); i++) {
				
					ChatTable mPreChatItem = preChatList.get(i);
				    
					mDataArrays.add(mPreChatItem);
				}
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mListView.getBottom());
	}
	GlobalApplication.getInstance().setSelectPic(false);
		// mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		if (voicePlayT != null) {
			voicePlayT.cancel();
			voicePlayT = null;
		}
		super.onPause();

	}

	@Override
	public void onDestroy() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// switch (item.getItemId()) {
		// case R.id.clear_cache:
		// mImageFetcher.clearCache();
		// Toast.makeText(getActivity(), R.string.clear_cache_complete_toast,
		// Toast.LENGTH_SHORT).show();
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}


   // the text  on the right
   class ViewHolderRightText{
	   private ImageView headIcon;
	   private TextView sendTimeTv;
	   private TextView contentTv;
	   private ProgressBar mPb;
	   private ImageView sendFailedImg;
    }
   //picture on the right
   class ViewHolderRightPic{
	   private ImageView headIcon;
	   private TextView sendTimeTv;
	   private ImageView contentTv;
	   private ProgressBar mPb;
	   private ImageView sendFailedImg;
   }
   
   class ViewHolderRightAudio
   {
	   private ImageView headIcon;
	   private TextView sendTimeTv;
	   private LinearLayout audioPlayLL;
	   private ImageView playStatus;
	   private ProgressBar mLoadingPb;
	   private ProgressBar playPgPb;
	   private  TextView audioLen;
	   private int itemPos;
   }
   
   class ViewHolderRightAction{
	   private ImageView headIcon;
	   private ImageView roundImg;
	   private TextView sendTimeTv;
	   private TextView contentTv;
	   private ProgressBar mPb;
	   private ImageView sendFailedImg;
   }

   // the text  on the left
   class ViewHolderLeftText{
	   private  ImageView headIcon;
	   private TextView sendTimeTv;
	   private TextView contentTv;
	   private ProgressBar mPb;
	   private ImageView sendFailedImg;
    }
   //picture on the left
   class ViewHolderLeftPic{
	   private ImageView headIcon;
	   private TextView sendTimeTv;
	   private ImageView contentTv;
	   private ProgressBar mPb;
	   private ImageView sendFailedImg;
   }
   
   class ViewHolderLeftAudio
   {
	   private ImageView headIcon;
	   private TextView sendTimeTv;
	   private LinearLayout audioPlayLL;
	   private ImageView playStatus;
	   private ProgressBar mLoadingPb;
	   private ProgressBar playPgPb;
	   private  TextView audioLen;
	   private int itemPos;
   }
   
   class ViewHolderLeftAction{
	   private ImageView headIcon;
	   private ImageView roundImg;
	   private TextView sendTimeTv;
	   private TextView contentTv;
	   private ProgressBar mPb;
	   private ImageView sendFailedImg;
   }






	public Timer voicePlayT = null;

	public class ChatMsgViewAdapter extends BaseAdapter {


		private int[] DefHeadimageIds = { R.drawable._0002_girl_photo_kuang,
				R.drawable._0003_boy_photo, R.drawable._0003_boy_photo,
				R.drawable._0003_boy_photo };
		private LayerDrawable tarHeadLayerDrawable;
		private LayerDrawable myHeadLayerDrawable;
		
		private Handler updateListItemHandler = new Handler();
		private boolean isPause = false; // for voice play
		private boolean isChange = false;
		/*
		 * //当前播放进度
		 */
		private int currVoicePgPos = 0;
		
        
//		private List<Object> voiceVHList = null; 
		private int currVoicePlayPos = -1;
		private int lastVoicePlayPos= -1;
		//用来存储当前播放和上一次播放viewholder
		private ViewHolderLeftAudio lastVoiceLVH = null;
		private ViewHolderLeftAudio currVoiceLVH = null;
		private ViewHolderRightAudio lastVoiceRVH = null;
		private ViewHolderRightAudio currVoiceRVH = null;

		private List<ChatTable> coll;
		private ListView mListView;

		private Context ctx;

		private LayoutInflater mInflater;

		public ChatMsgViewAdapter(Context context, List<ChatTable> coll) {
			ctx = context;
			this.coll = coll;
			mInflater = LayoutInflater.from(context);
			this.init(context);
		}

		public int getCount() {
			return coll.size();
		}

		public Object getItem(int position) {
			return coll.get(position);
		}

		public long getItemId(int position) {
			return position;
		}
      

		@Override
		public int getItemViewType(int position) {

//			return super.getItemViewType(position);
				ChatTable entity =(ChatTable) getItem(position);
           return entity.getMsgtype();
		}
		
		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
//			return super.getViewTypeCount();
			  return 8;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			ChatTable entity = (ChatTable)getItem(position);
//			int type = getItemViewType(position);
			int type = entity.getMsgtype();
			ViewHolderRightText holderRightText = null;
		    ViewHolderRightPic  holderRightPic = null;
			ViewHolderRightAudio holderRightAudio = null;
			ViewHolderRightAction holderRightAction = null;
			ViewHolderLeftText holderLeftText = null;
			ViewHolderLeftPic holderLeftPic = null;
			ViewHolderLeftAudio holderLeftAudio = null;
			ViewHolderLeftAction holderLeftAction = null;
			
			if (BuildConfig.DEBUG) {
				Log.v("pop", "@@" + position + "  " + coll.size());
			}
              if(convertView == null){
            	  switch(type){
            
            	  case Protocol.VALUE_LEFT_TEXT:
            	  holderLeftText = new ViewHolderLeftText();
            	  convertView = mInflater.inflate(
							R.layout.chatting_item_msg_text_left, null);
            	  holderLeftText.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
            	  holderLeftText.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            	  holderLeftText.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
            	  holderLeftText.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
            	  holderLeftText.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
            	  if(SelfInfo.getInstance().getSex().equals("b")){
            		  holderLeftText.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_left);
            	  }else{
            		  holderLeftText.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_left);
            	  }
            	  convertView.setTag(holderLeftText);
            	  break;
            	  case Protocol.VALUE_LEFT_ACTION:
            		  holderLeftAction = new ViewHolderLeftAction();
            		  convertView = mInflater.inflate(
  							R.layout.chatting_item_msg_action_left, null);
            		  holderLeftAction.headIcon = (ImageView)convertView.findViewById(R.id.iv_userhead);
            		  holderLeftAction.roundImg =(ImageView) convertView.findViewById(R.id.chat_action_round);
            		  holderLeftAction.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            		  holderLeftAction.mPb = (ProgressBar) convertView.findViewById( R.id.sending_progressBar);
            		  holderLeftAction.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
            		  holderLeftAction.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
            		  
            		  if(SelfInfo.getInstance().getSex().equals("b")){
            			  holderLeftAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_woman);
                	  }else{
                		  holderLeftAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_man);
                	  }
            		  convertView.setTag(holderLeftAction);
            		  
            		  break;
            	  case Protocol.VALUE_LEFT_IMAGE:
            		  holderLeftPic = new ViewHolderLeftPic();
            		  convertView = mInflater.inflate(
  							R.layout.chatting_item_msg_pic_left, null);
              	  holderLeftPic.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
              	  holderLeftPic.contentTv = (ImageView) convertView.findViewById(R.id.tv_chatcontent);
              	  holderLeftPic.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
              	  holderLeftPic.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
              	  holderLeftPic.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
              	  
              	  if(SelfInfo.getInstance().getSex().equals("b")){
              		holderLeftPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_left);
            	  }else{
            		  holderLeftPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_left);
            	  }
              	  convertView.setTag(holderLeftPic);
            	  break;
            	  
            	  case Protocol.VALUE_LEFT_AUDIO:
            		  holderLeftAudio = new ViewHolderLeftAudio();
            		  convertView = mInflater.inflate(
    							R.layout.chatting_item_msg_voice_left, null);
            		  holderLeftAudio.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
            		  holderLeftAudio.audioPlayLL = (LinearLayout) convertView.findViewById(R.id.audio_play_layout);
                  	  holderLeftAudio.playStatus = (ImageView) convertView.findViewById(R.id.play_status);
                  	  holderLeftAudio.mLoadingPb = (ProgressBar) convertView.findViewById(R.id.download_progress);
                  	  holderLeftAudio.playPgPb = (ProgressBar) convertView.findViewById(R.id.play_progress);
                  	
                  	  holderLeftAudio.audioLen = (TextView) convertView.findViewById(R.id.audio_len);
                  	  holderLeftAudio.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                  	  
                  	 convertView.setTag(holderLeftAudio);
            		  break;
            		  
            	  case Protocol.VALUE_RIGHT_TEXT:
                	  holderRightText = new ViewHolderRightText();
                	  convertView = mInflater.inflate(
    							R.layout.chatting_item_msg_text_right, null);
                	  holderRightText.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
                	  holderRightText.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                	  holderRightText.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
                	  holderRightText.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
                	  holderRightText.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                	  
                	  if(SelfInfo.getInstance().getSex().equals("b")){
                		  holderRightText.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_right);
                	  }else{
                		  holderRightText.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_right);
                	  }
                	  convertView.setTag(holderRightText);
                	  break;
                	  case Protocol.VALUE_RIGHT_ACTION:
                		  holderRightAction = new ViewHolderRightAction();
                		  convertView = mInflater.inflate(
      							R.layout.chatting_item_msg_action_right, null);
                		   holderRightAction.headIcon = (ImageView)convertView.findViewById(R.id.iv_userhead);
                		   holderRightAction.roundImg =(ImageView) convertView.findViewById(R.id.chat_action_round);
                		   holderRightAction.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                		   holderRightAction.mPb = (ProgressBar) convertView.findViewById( R.id.sending_progressBar);
                		   holderRightAction.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
                		   holderRightAction.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                		  
                		   if(SelfInfo.getInstance().getSex().equals("b")){
                			   holderRightAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_man);
                     	  }else{
                     		 holderRightAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_woman);
                     	  }
                		   convertView.setTag(holderRightAction);
                		  break;
                	  case Protocol.VALUE_RIGHT_IMAGE:
                		  holderRightPic = new ViewHolderRightPic();
                		  convertView = mInflater.inflate(
      							R.layout.chatting_item_msg_pic_right, null);
                  	   holderRightPic.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
                  	   holderRightPic.contentTv = (ImageView) convertView.findViewById(R.id.tv_chatcontent);
                  	   holderRightPic.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
                  	 holderRightPic.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
                  	holderRightPic.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                  	
                 	  if(SelfInfo.getInstance().getSex().equals("b")){
                 		 holderRightPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_right);
                	  }else{
                		  holderRightPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_right);
                	  }
                  	 convertView.setTag(holderRightPic);
                	  break;
                	  
                	  case Protocol.VALUE_RIGHT_AUDIO:
                		  holderRightAudio = new ViewHolderRightAudio();
                		  convertView = mInflater.inflate(
        							R.layout.chatting_item_msg_voice_right, null);
                		  holderRightAudio.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
                      	  holderRightAudio.audioPlayLL = (LinearLayout) convertView.findViewById(R.id.audio_play_layout);
                		  holderRightAudio.playStatus = (ImageView) convertView.findViewById(R.id.play_status);
                      	  holderRightAudio.mLoadingPb = (ProgressBar) convertView.findViewById(R.id.download_progress);
                      	  holderRightAudio.playPgPb = (ProgressBar) convertView.findViewById(R.id.play_progress);
                      	
                      	  holderRightAudio.audioLen = (TextView) convertView.findViewById(R.id.audio_len);
                      	  holderRightAudio.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                      	
                      	 convertView.setTag( holderRightAudio);
                		  break;
            	  default:
            		  break;
            	  }
              }else{
            	  switch(type){
            	
            	  case Protocol.VALUE_LEFT_TEXT:
            	  if(convertView.getTag().getClass() == ViewHolderLeftText.class){ 
            	  holderLeftText =(ViewHolderLeftText)convertView.getTag();
            	  }else{
            		  holderLeftText = new ViewHolderLeftText();
	            	  convertView = mInflater.inflate(
								R.layout.chatting_item_msg_text_left, null);
	            	  holderLeftText.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	            	  holderLeftText.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
	            	  holderLeftText.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
	            	  holderLeftText.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	            	  holderLeftText.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	            	  if(SelfInfo.getInstance().getSex().equals("b")){
	            		  holderLeftText.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_left);
	            	  }else{
	            		  holderLeftText.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_left);
	            	  }
	            	  convertView.setTag(holderLeftText);
            	  }
            	  break;
            	  case Protocol.VALUE_LEFT_ACTION:
            		  if(convertView.getTag().getClass() == ViewHolderLeftAction.class){ 
            		  holderLeftAction = (ViewHolderLeftAction)convertView.getTag();
            		  }else{
            			  holderLeftAction = new ViewHolderLeftAction();
                		  convertView = mInflater.inflate(
      							R.layout.chatting_item_msg_action_left, null);
                		  holderLeftAction.headIcon = (ImageView)convertView.findViewById(R.id.iv_userhead);
                		  holderLeftAction.roundImg =(ImageView) convertView.findViewById(R.id.chat_action_round);
                		  holderLeftAction.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                		  holderLeftAction.mPb = (ProgressBar) convertView.findViewById( R.id.sending_progressBar);
                		  holderLeftAction.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
                		  holderLeftAction.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                		  
                		  if(SelfInfo.getInstance().getSex().equals("b")){
                			  holderLeftAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_woman);
                    	  }else{
                    		  holderLeftAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_man);
                    	  }
                		  convertView.setTag(holderLeftAction);
                		  
            		  }
            		  break;
            	  case Protocol.VALUE_LEFT_IMAGE:
            		  if(convertView.getTag().getClass() == ViewHolderLeftPic.class){
            		  holderLeftPic =(ViewHolderLeftPic)convertView.getTag();
            		  }else{
            			  holderLeftPic = new ViewHolderLeftPic();
                		  convertView = mInflater.inflate(
      							R.layout.chatting_item_msg_pic_left, null);
                  	  holderLeftPic.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
                  	  holderLeftPic.contentTv = (ImageView) convertView.findViewById(R.id.tv_chatcontent);
                  	  holderLeftPic.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
                  	  holderLeftPic.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
                  	  holderLeftPic.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                  	  
                  	  if(SelfInfo.getInstance().getSex().equals("b")){
                  		holderLeftPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_left);
                	  }else{
                		  holderLeftPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_left);
                	  }
                  	  convertView.setTag(holderLeftPic);
            		  }
            		
            	  break;
            	  
            	  case Protocol.VALUE_LEFT_AUDIO:
            		  if(convertView.getTag().getClass() == ViewHolderLeftAudio.class){
            		  holderLeftAudio = (ViewHolderLeftAudio)convertView.getTag();
            		  }else{
            			  holderLeftAudio = new ViewHolderLeftAudio();
                		  convertView = mInflater.inflate(
        							R.layout.chatting_item_msg_voice_left, null);
                		  holderLeftAudio.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
                		  holderLeftAudio.audioPlayLL = (LinearLayout) convertView.findViewById(R.id.audio_play_layout);
                      	  holderLeftAudio.playStatus = (ImageView) convertView.findViewById(R.id.play_status);
                      	  holderLeftAudio.mLoadingPb = (ProgressBar) convertView.findViewById(R.id.download_progress);
                      	  holderLeftAudio.playPgPb = (ProgressBar) convertView.findViewById(R.id.play_progress);
                      	
                      	  holderLeftAudio.audioLen = (TextView) convertView.findViewById(R.id.audio_len);
                      	  holderLeftAudio.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                      	  
                      	 convertView.setTag(holderLeftAudio);
            		  }
            		
            		  break;
            		  
            	  case Protocol.VALUE_RIGHT_TEXT:
            		  if(convertView.getTag().getClass() == ViewHolderRightText.class){
                	  holderRightText = (ViewHolderRightText) convertView.getTag();
            		  }else{
            			  holderRightText = new ViewHolderRightText();
                    	  convertView = mInflater.inflate(
        							R.layout.chatting_item_msg_text_right, null);
                    	  holderRightText.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    	  holderRightText.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                    	  holderRightText.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
                    	  holderRightText.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
                    	  holderRightText.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                    	  
                    	  if(SelfInfo.getInstance().getSex().equals("b")){
                    		  holderRightText.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_right);
                    	  }else{
                    		  holderRightText.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_right);
                    	  }
                    	  convertView.setTag(holderRightText);
            		  }
                	  break;
                	  case Protocol.VALUE_RIGHT_ACTION:
                		  if(convertView.getTag().getClass() == ViewHolderRightAction.class){
                		  holderRightAction = (ViewHolderRightAction) convertView.getTag();
                		  }else{
                			  holderRightAction = new ViewHolderRightAction();
                    		  convertView = mInflater.inflate(
          							R.layout.chatting_item_msg_action_right, null);
                    		   holderRightAction.headIcon = (ImageView)convertView.findViewById(R.id.iv_userhead);
                    		   holderRightAction.roundImg =(ImageView) convertView.findViewById(R.id.chat_action_round);
                    		   holderRightAction.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                    		   holderRightAction.mPb = (ProgressBar) convertView.findViewById( R.id.sending_progressBar);
                    		   holderRightAction.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
                    		   holderRightAction.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                    		  
                    		   if(SelfInfo.getInstance().getSex().equals("b")){
                    			   holderRightAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_man);
                         	  }else{
                         		 holderRightAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_woman);
                         	  }
                    		   convertView.setTag(holderRightAction);
                		  }
                		
                		  break;
                	  case Protocol.VALUE_RIGHT_IMAGE:
                		  if(convertView.getTag().getClass() ==ViewHolderRightPic.class){
                		  holderRightPic = (ViewHolderRightPic) convertView.getTag();
                		  }else{
                			  holderRightPic = new ViewHolderRightPic();
                    		  convertView = mInflater.inflate(
          							R.layout.chatting_item_msg_pic_right, null);
                      	   holderRightPic.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
                      	   holderRightPic.contentTv = (ImageView) convertView.findViewById(R.id.tv_chatcontent);
                      	   holderRightPic.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
                      	 holderRightPic.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
                      	holderRightPic.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                      	
                     	  if(SelfInfo.getInstance().getSex().equals("b")){
                     		 holderRightPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_right);
                    	  }else{
                    		  holderRightPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_right);
                    	  }
                      	 convertView.setTag(holderRightPic);  
                		  }
                	
                	  break;
                	  
                	  case Protocol.VALUE_RIGHT_AUDIO:
                		  if(convertView.getTag().getClass() ==ViewHolderRightAudio.class){
                		  holderRightAudio = (ViewHolderRightAudio) convertView.getTag();
                		  }else{
                			  holderRightAudio = new ViewHolderRightAudio();
                    		  convertView = mInflater.inflate(
            							R.layout.chatting_item_msg_voice_right, null);
                    		  holderRightAudio.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
                          	  holderRightAudio.audioPlayLL = (LinearLayout) convertView.findViewById(R.id.audio_play_layout);
                    		  holderRightAudio.playStatus = (ImageView) convertView.findViewById(R.id.play_status);
                          	  holderRightAudio.mLoadingPb = (ProgressBar) convertView.findViewById(R.id.download_progress);
                          	  holderRightAudio.playPgPb = (ProgressBar) convertView.findViewById(R.id.play_progress);
                          	
                          	  holderRightAudio.audioLen = (TextView) convertView.findViewById(R.id.audio_len);
                          	  holderRightAudio.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
                          	
                          	 convertView.setTag( holderRightAudio);
                		  }
                		
                		  break;
            	  default:
            		  break;
            	  }
              }
              
              
              switch(type){
        	 
        	  case Protocol.VALUE_LEFT_TEXT:
        	  holderLeftText.headIcon.setImageDrawable(tarHeadLayerDrawable);
        		SpannableString spannableString = FaceConversionUtil
						.getInstace().getExpressionString(ctx,
								entity.getMessage());
        	  holderLeftText.contentTv.setText(spannableString);
              
//        	  Date timedate =AppManagerUtil.StrToDate(entity.getInitdate());
        	  String serverTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());
        	  
              holderLeftText.sendTimeTv.setText(serverTime);
        	
        	  break;
        	  case Protocol.VALUE_LEFT_ACTION:
        		  holderLeftAction.headIcon.setImageDrawable(tarHeadLayerDrawable);
        		  holderLeftAction.contentTv.setText(entity.getMessage());  
//        		  Date LAdate =AppManagerUtil.StrToDate(entity.getInitdate());
        		  String leftActionServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());
            	  
        		  holderLeftAction.sendTimeTv.setText(leftActionServerTime);	
        		  break;
        	  case Protocol.VALUE_LEFT_IMAGE:
        		  holderLeftPic.headIcon.setImageDrawable(tarHeadLayerDrawable);
        		  String leftImgServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());
            
        	         
        		  holderLeftPic.sendTimeTv.setText(leftImgServerTime);	
        		  holderLeftPic.contentTv.setTag(position + "");
			
        		  holderLeftPic.contentTv
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									int pos = Integer.valueOf((String) v
											.getTag());
									imageBrower(pos);
//									ChatTable entity = ChatListFragment
//											.getmDataArrays().get(pos);
//								
//								
//										Intent intent = new Intent();
//										intent.setClass(getActivity(),
//												ChatPicDetailActivity.class);
//										intent.putExtra(Keys.PATH,
//												entity.getMessage());
//										startActivity(intent);

									
								}
							});
        		  
        		// Load image, decode it to Bitmap and return Bitmap to callback
        		  ImageSize targetSize = new ImageSize(120, 80); // result Bitmap will be fit to this size
        		  final ImageView temp = holderLeftPic.contentTv;
//        		  imageLoader.displayImage( "file://"+entity.getMessage(), holderLeftPic.contentTv, options);
        		  imageLoader.loadImage( "file://"+entity.getMessage(), targetSize, options, new SimpleImageLoadingListener() {
        		      @Override
        		      public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        		          // Do whatever you want with Bitmap
        		    	  temp.setImageBitmap(loadedImage);
        		      }
        		  });
					
        	     break;
        	  
        	  case Protocol.VALUE_LEFT_AUDIO:
//        		  holderLeftAudio = (ViewHolderLeftAudio)
        	      holderLeftAudio.headIcon.setImageDrawable(tarHeadLayerDrawable);
        	      String leftAudioServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());
                  holderLeftAudio.sendTimeTv.setText(leftAudioServerTime);	       	
                  holderLeftAudio.audioLen.setText(entity.getRecordTime()+"\"");
                  holderLeftAudio.audioPlayLL.setTag(R.id.a1, position+"");
                  holderLeftAudio.audioPlayLL.setTag(R.id.a2, holderLeftAudio);

                  holderLeftAudio.audioPlayLL.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							
							 if(FileUtil.isSDCardExist()){

							ViewHolderLeftAudio holderLeftAudio= (ViewHolderLeftAudio) v.getTag(R.id.a2);
                            int itemPos = Integer.parseInt((String)v.getTag(R.id.a1));
							stopMusic();
							
							if (itemPos != currVoicePlayPos) {
								isPause = false;
                            }

							if (!(isPause)) {
								isChange = true;
								if (itemPos != currVoicePlayPos) {
									lastVoicePlayPos = currVoicePlayPos;
									if(currVoiceLVH != null){
										lastVoiceLVH = currVoiceLVH;
										lastVoiceRVH = null;
										currVoiceRVH= null;
										currVoiceLVH = holderLeftAudio;
									}else{
										lastVoiceRVH =  currVoiceRVH;
										lastVoiceLVH = null;
										currVoiceRVH= null;
										currVoiceLVH = holderLeftAudio;
									}
					
								}
								holderLeftAudio.playStatus.setBackgroundResource(R.drawable.chat_voice_pause);

								initPlayMusic(coll.get(itemPos).getMessage());
								holderLeftAudio.playPgPb
										.setVisibility(View.VISIBLE);
                                lastVoicePlayPos = currVoicePlayPos;
								currVoicePlayPos = itemPos;

								holderLeftAudio.playPgPb.setMax(mMediaPlayer
										.getDuration());
								mMediaPlayer.start();
								startSetVoicePg();
								mMediaPlayer
										.setOnCompletionListener(new OnCompletionListener() {
											public void onCompletion(
													MediaPlayer mp) {
												if(currVoiceLVH != null){
													currVoiceLVH.playPgPb
													.setVisibility(View.GONE);
													currVoiceLVH.playStatus
													.setBackgroundResource(R.drawable.chat_voice_start);
												}else if(currVoiceRVH != null){
													currVoiceRVH.playPgPb
													.setVisibility(View.GONE);
													currVoiceRVH.playStatus
													.setBackgroundResource(R.drawable.chat_voice_start);
												}
												
												 isPause = false; // for voice play
  											   isChange = false;
  												/*
  												 * //当前播放进度
  												 */
  											    currVoicePgPos = 0;
  							
  											    currVoicePlayPos = -1;
  												lastVoicePlayPos= -1;
  												//用来存储当前播放和上一次播放viewholder
  											    lastVoiceLVH = null;
  												currVoiceLVH = null;
  												lastVoiceRVH = null;
  												currVoiceRVH = null;
											}
										});
								isPause = true;

							} else {
								holderLeftAudio.playPgPb
										.setVisibility(View.GONE);
								holderLeftAudio.playStatus
										.setBackgroundResource(R.drawable.chat_voice_start);
								 isPause = false; // for voice play
								   isChange = false;
									/*
									 * //当前播放进度
									 */
								    currVoicePgPos = 0;
				
								    currVoicePlayPos = -1;
									lastVoicePlayPos= -1;
									//用来存储当前播放和上一次播放viewholder
								    lastVoiceLVH = null;
									currVoiceLVH = null;
									lastVoiceRVH = null;
									currVoiceRVH = null;
							}
						}else{
							Toast.makeText(ctx.getApplicationContext(), "SD卡已拔出，语音功能暂时不能使用", Toast.LENGTH_SHORT).show();
							
						}
						}
					});
        		  
        		  break;	  
        	  case Protocol.VALUE_RIGHT_TEXT:
            	  holderRightText.headIcon.setImageDrawable(myHeadLayerDrawable);
          	 	SpannableString spannableStringR = FaceConversionUtil
  						.getInstace().getExpressionString(ctx,
  								entity.getMessage());
          	 	holderRightText.contentTv.setText(spannableStringR);
          	  String righttextServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());
        	  
           	   
          	 	String timeStr = righttextServerTime;
          	 	if (entity.getStatus().equals(Protocol.Sending + "")) {
          	 		holderRightText.mPb.setVisibility(View.VISIBLE);
          	 		holderRightText.sendFailedImg.setVisibility(View.GONE);
          	 		holderRightText.sendTimeTv.setText(timeStr+" 发送...");
				} else if (entity.getStatus().equals(Protocol.SendFail + "")) {
					holderRightText.mPb.setVisibility(View.GONE);
					holderRightText.sendFailedImg.setVisibility(View.VISIBLE);
					holderRightText.sendTimeTv.setText(timeStr+" 发送失败");
				} else if (entity.getStatus().equals(Protocol.Sended + "")) {
					holderRightText.mPb.setVisibility(View.GONE);
					holderRightText.sendFailedImg.setVisibility(View.GONE);
					holderRightText.sendTimeTv.setText(timeStr+" 已发送");
				}else if (entity.getStatus().equals(Protocol.Read + "")) {
					holderRightText.mPb.setVisibility(View.GONE);
					holderRightText.sendFailedImg.setVisibility(View.GONE);
					holderRightText.sendTimeTv.setText(timeStr+" 已读");
				}
            	  break;
            	  case Protocol.VALUE_RIGHT_ACTION:
            		  holderRightAction.headIcon.setImageDrawable(myHeadLayerDrawable);
            		  holderRightAction.contentTv.setText(entity.getMessage()); 
            		  String rightActionServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());

            		  holderRightAction.sendTimeTv.setText(rightActionServerTime);	
            		
            		  break;
            	  case Protocol.VALUE_RIGHT_IMAGE:
            		  holderRightPic.headIcon.setImageDrawable(myHeadLayerDrawable);
            		  String rightImgServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());

                  	   
                	 	String timeStr1 =  rightImgServerTime;
            		
                  	 	if (entity.getStatus().equals(Protocol.Sending + "")) {
                  	 		holderRightPic.mPb.setVisibility(View.VISIBLE);
                  	 		holderRightPic.sendFailedImg.setVisibility(View.GONE);
                  	 		holderRightPic.sendTimeTv.setText(timeStr1+" 发送...");
        				} else if (entity.getStatus().equals(Protocol.SendFail + "")) {
        					holderRightPic.mPb.setVisibility(View.GONE);
        					holderRightPic.sendFailedImg.setVisibility(View.VISIBLE);
        					holderRightPic.sendTimeTv.setText(timeStr1+" 发送失败");
        				} else if (entity.getStatus().equals(Protocol.Sended + "")) {
        					holderRightPic.mPb.setVisibility(View.GONE);
        					holderRightPic.sendFailedImg.setVisibility(View.GONE);
        					holderRightPic.sendTimeTv.setText(timeStr1+" 已发送");
        				}else if (entity.getStatus().equals(Protocol.Read + "")) {
        					holderRightPic.mPb.setVisibility(View.GONE);
        					holderRightPic.sendFailedImg.setVisibility(View.GONE);
        					holderRightPic.sendTimeTv.setText(timeStr1+" 已读");
        				}
            		  holderRightPic.contentTv.setTag(position + "");
    			
            		  holderRightPic.contentTv
    							.setOnClickListener(new View.OnClickListener() {

    								@Override
    								public void onClick(View v) {
    									int pos = Integer.valueOf((String) v
    											.getTag());
//    									ChatTable entity = coll.get(pos);
//    								
//    								
//    										Intent intent = new Intent();
//    										intent.setClass(getActivity(),
//    												ChatPicDetailActivity.class);
//    										intent.putExtra(Keys.PATH,
//    												entity.getMessage());
//    										startActivity(intent);
    									imageBrower(pos);
    									
    								}
    							});
            		  
//            		// Load image, decode it to Bitmap and return Bitmap to callback
//            		  ImageSize targetSize1 = new ImageSize(120, 80); // result Bitmap will be fit to this size
//            		  imageLoader.loadImage( "file://"+entity.getMessage(), targetSize1, options, new SimpleImageLoadingListener() {
//            		      @Override
//            		      public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//            		          // Do whatever you want with Bitmap
//            		    	  holderRightPic.contentTv.setImageBitmap(loadedImage);
//            		      }
//            		  });
            		// Load image, decode it to Bitmap and return Bitmap to callback
            		  ImageSize rightTargetSize = new ImageSize(120, 80); // result Bitmap will be fit to this size
            		  final ImageView rightTemp = holderRightPic.contentTv;
//            		  imageLoader.displayImage( "file://"+entity.getMessage(), holderLeftPic.contentTv, options);
            		  imageLoader.loadImage( "file://"+entity.getMessage(), rightTargetSize, options, new SimpleImageLoadingListener() {
            		      @Override
            		      public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            		          // Do whatever you want with Bitmap
            		    	  rightTemp.setImageBitmap(loadedImage);
            		      }
            		  });
//            		  imageLoader.displayImage( "file://"+entity.getMessage(), holderRightPic.contentTv, options);
            	  break;
            	  
            	  case Protocol.VALUE_RIGHT_AUDIO:
            		  holderRightAudio.headIcon.setImageDrawable(myHeadLayerDrawable);;
            		  String rightAudioServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());

                	 	String timeStr2 =  rightAudioServerTime;
            			
                  	 	if (entity.getStatus().equals(Protocol.Sending + "")) {
                  	 		holderRightAudio.mLoadingPb.setVisibility(View.VISIBLE);
                  	 	
                  	 		holderRightAudio.sendTimeTv.setText(timeStr2+" 发送...");
        				} else if (entity.getStatus().equals(Protocol.SendFail + "")) {
        					holderRightAudio.mLoadingPb.setVisibility(View.GONE);
        				
        					holderRightAudio.sendTimeTv.setText(timeStr2+" 发送失败");
        				} else if (entity.getStatus().equals(Protocol.Sended + "")) {
        					holderRightAudio.mLoadingPb.setVisibility(View.GONE);
        				
        					holderRightAudio.sendTimeTv.setText(timeStr2+" 已发送");
        				}else if (entity.getStatus().equals(Protocol.Read + "")) {
        					holderRightAudio.mLoadingPb.setVisibility(View.GONE);
        					
        					holderRightAudio.sendTimeTv.setText(timeStr2+" 已读");
        				}
                  	  holderRightAudio.audioLen.setText(entity.getRecordTime()+"\"");
                  	holderRightAudio.audioPlayLL.setTag(R.id.a1, position+"");
                  	holderRightAudio.audioPlayLL.setTag(R.id.a2, holderRightAudio);

                  	holderRightAudio.audioPlayLL.setOnClickListener(new View.OnClickListener() {

    						@Override
    						public void onClick(View v) {
    							 if(FileUtil.isSDCardExist()){

    							ViewHolderRightAudio holderRightAudio= (ViewHolderRightAudio) v.getTag(R.id.a2);
                                int itemPos = Integer.parseInt((String)v.getTag(R.id.a1));
    							stopMusic();
    							
    							if (itemPos != currVoicePlayPos) {
    								isPause = false;
                                }

    							if (!(isPause)) {
    								isChange = true;
    								if (itemPos != currVoicePlayPos) {
    									lastVoicePlayPos = currVoicePlayPos;
    									if(currVoiceRVH != null){
    										lastVoiceRVH = currVoiceRVH;
    										lastVoiceLVH = null;
    										currVoiceLVH= null;
    										currVoiceRVH = holderRightAudio;
    									}else {
    										lastVoiceLVH =  currVoiceLVH;
    										lastVoiceRVH = null;
    										currVoiceLVH= null;
    										currVoiceRVH = holderRightAudio;
    									}
    					
    								}
    								holderRightAudio.playStatus.setBackgroundResource(R.drawable.chat_voice_pause);

    								initPlayMusic(coll.get(itemPos).getMessage());
    								holderRightAudio.playPgPb
    										.setVisibility(View.VISIBLE);
                                    lastVoicePlayPos = currVoicePlayPos;
    								currVoicePlayPos = itemPos;

    								holderRightAudio.playPgPb.setMax(mMediaPlayer
    										.getDuration());
    								mMediaPlayer.start();
    								startSetVoicePg();
    								mMediaPlayer
    										.setOnCompletionListener(new OnCompletionListener() {
    											public void onCompletion(
    													MediaPlayer mp) {
    												
    												if(currVoiceLVH != null){
    													currVoiceLVH.playPgPb
    													.setVisibility(View.GONE);
    													currVoiceLVH.playStatus
    													.setBackgroundResource(R.drawable.chat_voice_start);
    												}else if(currVoiceRVH != null){
    													currVoiceRVH.playPgPb
    													.setVisibility(View.GONE);
    													currVoiceRVH.playStatus
    													.setBackgroundResource(R.drawable.chat_voice_start);
    												}
    												
    											   isPause = false; // for voice play
    											   isChange = false;
    												/*
    												 * //当前播放进度
    												 */
    											    currVoicePgPos = 0;
    							
    											    currVoicePlayPos = -1;
    												lastVoicePlayPos= -1;
    												//用来存储当前播放和上一次播放viewholder
    											    lastVoiceLVH = null;
    												currVoiceLVH = null;
    												lastVoiceRVH = null;
    												currVoiceRVH = null;
    												
    												

    											}
    										});
    								isPause = true;

    							} else {
    								holderRightAudio.playPgPb
    										.setVisibility(View.GONE);
    								holderRightAudio.playStatus
    										.setBackgroundResource(R.drawable.chat_voice_start);
    								 isPause = false; // for voice play
									   isChange = false;
										/*
										 * //当前播放进度
										 */
									    currVoicePgPos = 0;
					
									    currVoicePlayPos = -1;
										lastVoicePlayPos= -1;
										//用来存储当前播放和上一次播放viewholder
									    lastVoiceLVH = null;
										currVoiceLVH = null;
										lastVoiceRVH = null;
										currVoiceRVH = null;
    							}
    						}else{
    							Toast.makeText(ctx.getApplicationContext(), "SD卡已拔出，语音功能暂时不能使用", Toast.LENGTH_SHORT).show();
    						}
    						}
    					});
            		  break;
        	  default:
        		  break;
        	  }
			return convertView;
		}



		
		private  void imageBrower(int position){
//		getImagePaths
			List<String> tempList = new ArrayList<String>();
			int newPos = 0;
			for(int pos = 0; pos < coll.size(); pos++){
				ChatTable mCT = coll.get(pos);
				if(mCT.getMsgtype() == Protocol.VALUE_LEFT_IMAGE || mCT.getMsgtype() == Protocol.VALUE_RIGHT_IMAGE){
					tempList.add(mCT.getMessage());
					tempList.add(mCT.getInitdate());
				}
				if(pos == position){
					newPos = tempList.size()/2-1;
				}
			}
			String[] urls = tempList.toArray(new String[tempList.size()]);
			Intent intent = new Intent(ctx, ImagePagerActivity.class);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, newPos);
			ctx.startActivity(intent);
//			String[] urls = new String[tempList.size()];
//			for(int i = 0; i<urls.length;i++){
//				urls[i] = tempList.get(i);
//			}
		}


		private void init(Context con) {
			Bitmap myHeadBm = CommonBitmap.getInstance().getMyHeadBm();
			Bitmap tarHeadBm = GlobalApplication.getInstance()
					.getTarHeadPicBm();
			Resources r = con.getResources();
			Drawable[] mylayers = new Drawable[2];
			Drawable[] tarlayers = new Drawable[2];
			if (myHeadBm != null) {

				if (SelfInfo.getInstance().getSex().equals("b")) {
					mylayers[0] = r.getDrawable(DefHeadimageIds[2]);
				} else {
					mylayers[0] = r.getDrawable(DefHeadimageIds[0]);
				}
				mylayers[1] = new BitmapDrawable(r, myHeadBm);
			} else {
				// no myheadpoto use default
				if (SelfInfo.getInstance().getSex().equals("b")) {
					mylayers[0] = r.getDrawable(DefHeadimageIds[2]);
					mylayers[1] = r.getDrawable(DefHeadimageIds[3]);
				} else {
					mylayers[0] = r.getDrawable(DefHeadimageIds[0]);
					mylayers[1] = r.getDrawable(DefHeadimageIds[1]);
				}
			}

			if (tarHeadBm != null) {

				if (SelfInfo.getInstance().getSex().equals("b")) {
					tarlayers[0] = r.getDrawable(DefHeadimageIds[2]);
				} else {
					tarlayers[0] = r.getDrawable(DefHeadimageIds[0]);
				}
				tarlayers[1] = new BitmapDrawable(r, tarHeadBm);
			} else {
				// no tarheadpoto use default
				if (SelfInfo.getInstance().getSex().equals("b")) {
					tarlayers[0] = r.getDrawable(DefHeadimageIds[0]);
					tarlayers[1] = r.getDrawable(DefHeadimageIds[1]);
				} else {
					tarlayers[0] = r.getDrawable(DefHeadimageIds[2]);
					tarlayers[1] = r.getDrawable(DefHeadimageIds[3]);
				}
			}

			myHeadLayerDrawable = new LayerDrawable(mylayers);
			tarHeadLayerDrawable = new LayerDrawable(tarlayers);
		}

		// private static class UpdateListItemHandler extends Handler
		// {
		// WeakReference<ChatListFragment> mCF;
		// UpdateListItemHandler(ChatListFragment mCM) {
		// mCF = new WeakReference<ChatListFragment>(mCM);
		// }
		// @Override
		// public void handleMessage(Message msg)
		// {
		//
		// switch (msg.what) {
		//
		// case 0: // update a nomal item
		// ChatMsgEnity entity = (ChatMsgEnity) msg.obj;
		// int location = msg.arg1;
		// mCF.coll.set(location, entity);
		// mCF.updateView(location);
		// break;
		// case 1 : //update curren voice item
		// break;
		// case 2 : // update last voice item
		// updateLastVoice();
		// break;
		//
		// default:
		// break;
		// }
		// }
		// }
		// private static Handler updateListItemHandler = new Handler() {
		//
		// public void handleMessage(Message msg) {
		// switch (msg.what) {
		//
		// case 0: // update a nomal item
		// ChatMsgEnity entity = (ChatMsgEnity) msg.obj;
		// int location = msg.arg1;
		// coll.set(location, entity);
		// updateView(location);
		// break;
		// case 1 : //update curren voice item
		// break;
		// case 2 : // update last voice item
		// updateLastVoice();
		// break;
		//
		// default:
		// break;
		// }
		//
		// // DownloadFile downloadFile = (DownloadFile)msg.obj;
		// // AppFile appFile = dataList.get(downloadFile.downloadID);
		// // appFile.downloadSize = downloadFile.downloadSize;
		// // appFile.downloadState = downloadFile.downloadState;
		//
		// // notifyDataSetChanged会执行getView函数，更新所有可视item的数据
		//
		// // notifyDataSetChanged();
		//
		// // updateView(appFile.id);
		// }
		// };

		public void startSetVoicePg() {
			if (voicePlayT == null) {
				voicePlayT = new Timer();
				TimerTask mTimerTask = new TimerTask() {

					@Override
					public void run() {

						if (currVoicePlayPos != -1) {

							if (isChange) {
								isChange = false;

								updateListItemHandler.post(new Runnable() {
									@Override
									public void run() {
										// update last voice view
										updateLastVoice();
									}
								});

							}

							currVoicePgPos = mMediaPlayer.getCurrentPosition();
							
							updateListItemHandler.post(new Runnable() {
								@Override
								public void run() {
									// update curr voice item progresss
									updateCurrVoicePg();
								}
							});

						}

					}

				};
				voicePlayT.schedule(mTimerTask, 500L, 100L);

			}
		}

		// 获得上一个voice item ，改变其播放状态ui
		private void updateLastVoice() {
			if (lastVoiceLVH != null
					&& ( lastVoicePlayPos!= currVoicePlayPos)) {
				lastVoiceLVH.playPgPb.setVisibility(View.GONE);
				lastVoiceLVH.playStatus
						.setBackgroundResource(R.drawable.chat_voice_start);
			}else if(lastVoiceRVH != null
					&& ( lastVoicePlayPos!= currVoicePlayPos)){
				lastVoiceRVH.playPgPb.setVisibility(View.GONE);
				lastVoiceRVH.playStatus
						.setBackgroundResource(R.drawable.chat_voice_start);
			}
		}

		// update curr voice item
		private void updateCurrVoicePg() {
			if (currVoicePlayPos >=0 && currVoiceLVH != null){
				currVoiceLVH.playPgPb.setProgress(currVoicePgPos);
			}else if (currVoicePlayPos >=0 && currVoiceRVH != null){
				currVoiceRVH.playPgPb.setProgress(currVoicePgPos);
			}
				
		}

		/**
		 * @Description playMusic
		 * @param name
		 */

		private void initPlayMusic(String name) {
			try {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
				if(name.equals("")) return;
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(name);
				mMediaPlayer.prepare();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void stopMusic() {

			try {
				if (mMediaPlayer.isPlaying()) {

					mMediaPlayer.stop();
				}
				mMediaPlayer.reset();

			} catch (Exception e) {

			}
		}

		/**
		 * 
		 * @param itemIndex
		 */
		public void updateView(int position) {
			// 得到第一个可显示控件的位置，
			int visiblePosition = mListView.getFirstVisiblePosition();
			// 只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
			if (position - visiblePosition >= 0) {
				// 得到要更新的item的view , it has an invisible view
				View convertView = mListView.getChildAt(position
						- visiblePosition + 1);

				ChatTable entity = coll.get(position);

				int type = entity.getMsgtype();
			
				ViewHolderRightText holderRightText = null;
			    ViewHolderRightPic  holderRightPic = null;
				ViewHolderRightAudio holderRightAudio = null;
				ViewHolderRightAction holderRightAction = null;
				ViewHolderLeftText holderLeftText = null;
				ViewHolderLeftPic holderLeftPic = null;
				ViewHolderLeftAudio holderLeftAudio = null;
				ViewHolderLeftAction holderLeftAction = null;
				
				if (BuildConfig.DEBUG) {
					Log.v("pop", "@@" + position + "  " + coll.size());
				}
			    if(convertView == null){
	            	  switch(type){ 
	            	  case Protocol.VALUE_LEFT_TEXT:
	            	  holderLeftText = new ViewHolderLeftText();
	            	  convertView = mInflater.inflate(
								R.layout.chatting_item_msg_text_left, null);
	            	  holderLeftText.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	            	  holderLeftText.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
	            	  holderLeftText.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
	            	  holderLeftText.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	            	  holderLeftText.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	            	  if(SelfInfo.getInstance().getSex().equals("b")){
	            		  holderLeftText.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_left);
	            	  }else{
	            		  holderLeftText.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_left);
	            	  }
	            	  convertView.setTag( holderLeftText);
	            	  break;
	            	  case Protocol.VALUE_LEFT_ACTION:
	            		  holderLeftAction = new ViewHolderLeftAction();
	            		  convertView = mInflater.inflate(
	  							R.layout.chatting_item_msg_action_left, null);
	            		  holderLeftAction.headIcon = (ImageView)convertView.findViewById(R.id.iv_userhead);
	            		  holderLeftAction.roundImg =(ImageView) convertView.findViewById(R.id.chat_action_round);
	            		  holderLeftAction.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
	            		  holderLeftAction.mPb = (ProgressBar) convertView.findViewById( R.id.sending_progressBar);
	            		  holderLeftAction.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	            		  holderLeftAction.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	            		  
	            		  if(SelfInfo.getInstance().getSex().equals("b")){
	            			  holderLeftAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_woman);
	                	  }else{
	                		  holderLeftAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_man);
	                	  }
	            		  convertView.setTag(holderLeftAction);
	            		  
	            		  break;
	            	  case Protocol.VALUE_LEFT_IMAGE:
	            		  holderLeftPic = new ViewHolderLeftPic();
	            		  convertView = mInflater.inflate(
	  							R.layout.chatting_item_msg_pic_left, null);
	              	  holderLeftPic.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	              	  holderLeftPic.contentTv = (ImageView) convertView.findViewById(R.id.tv_chatcontent);
	              	  holderLeftPic.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
	              	  holderLeftPic.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	              	  holderLeftPic.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	              	  
	              	  if(SelfInfo.getInstance().getSex().equals("b")){
	              		holderLeftPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_left);
	            	  }else{
	            		  holderLeftPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_left);
	            	  }
	              	  convertView.setTag(holderLeftPic);
	            	  break;
	            	  
	            	  case Protocol.VALUE_LEFT_AUDIO:
	            		  holderLeftAudio = new ViewHolderLeftAudio();
	            		  convertView = mInflater.inflate(
	    							R.layout.chatting_item_msg_voice_left, null);
	            		  holderLeftAudio.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	            		  holderLeftAudio.audioPlayLL = (LinearLayout) convertView.findViewById(R.id.audio_play_layout);
	                  	  holderLeftAudio.playStatus = (ImageView) convertView.findViewById(R.id.play_status);
	                  	  holderLeftAudio.mLoadingPb = (ProgressBar) convertView.findViewById(R.id.download_progress);
	                  	  holderLeftAudio.playPgPb = (ProgressBar) convertView.findViewById(R.id.play_progress);
	                  	
	                  	  holderLeftAudio.audioLen = (TextView) convertView.findViewById(R.id.audio_len);
	                  	  holderLeftAudio.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                  	  
	                  	 convertView.setTag(holderLeftAudio);
	            		  break;
	            		  
	            	  case Protocol.VALUE_RIGHT_TEXT:
	                	  holderRightText = new ViewHolderRightText();
	                	  convertView = mInflater.inflate(
	    							R.layout.chatting_item_msg_text_right, null);
	                	  holderRightText.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	                	  holderRightText.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
	                	  holderRightText.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
	                	  holderRightText.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	                	  holderRightText.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                	  
	                	  if(SelfInfo.getInstance().getSex().equals("b")){
	                		  holderRightText.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_right);
	                	  }else{
	                		  holderRightText.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_right);
	                	  }
	                	  convertView.setTag(holderRightText);
	                	  break;
	                	  case Protocol.VALUE_RIGHT_ACTION:
	                		  holderRightAction = new ViewHolderRightAction();
	                		  convertView = mInflater.inflate(
	      							R.layout.chatting_item_msg_action_right, null);
	                		   holderRightAction.headIcon = (ImageView)convertView.findViewById(R.id.iv_userhead);
	                		   holderRightAction.roundImg =(ImageView) convertView.findViewById(R.id.chat_action_round);
	                		   holderRightAction.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
	                		   holderRightAction.mPb = (ProgressBar) convertView.findViewById( R.id.sending_progressBar);
	                		   holderRightAction.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	                		   holderRightAction.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                		  
	                		   if(SelfInfo.getInstance().getSex().equals("b")){
	                			   holderRightAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_man);
	                     	  }else{
	                     		 holderRightAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_woman);
	                     	  }
	                		   convertView.setTag(holderRightAction);
	                		  break;
	                	  case Protocol.VALUE_RIGHT_IMAGE:
	                		  holderRightPic = new ViewHolderRightPic();
	                		  convertView = mInflater.inflate(
	      							R.layout.chatting_item_msg_pic_right, null);
	                  	   holderRightPic.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	                  	   holderRightPic.contentTv = (ImageView) convertView.findViewById(R.id.tv_chatcontent);
	                  	   holderRightPic.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
	                  	 holderRightPic.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	                  	holderRightPic.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                  	
	                 	  if(SelfInfo.getInstance().getSex().equals("b")){
	                 		 holderRightPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_right);
	                	  }else{
	                		  holderRightPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_right);
	                	  }
	                  	 convertView.setTag(holderRightPic);
	                	  break;
	                	  
	                	  case Protocol.VALUE_RIGHT_AUDIO:
	                		  holderRightAudio = new ViewHolderRightAudio();
	                		  convertView = mInflater.inflate(
	        							R.layout.chatting_item_msg_voice_right, null);
	                		  holderRightAudio.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	                      	  holderRightAudio.audioPlayLL = (LinearLayout) convertView.findViewById(R.id.audio_play_layout);
	                		  holderRightAudio.playStatus = (ImageView) convertView.findViewById(R.id.play_status);
	                      	  holderRightAudio.mLoadingPb = (ProgressBar) convertView.findViewById(R.id.download_progress);
	                      	  holderRightAudio.playPgPb = (ProgressBar) convertView.findViewById(R.id.play_progress);
	                      	
	                      	  holderRightAudio.audioLen = (TextView) convertView.findViewById(R.id.audio_len);
	                      	  holderRightAudio.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                      	
	                      	 convertView.setTag( holderRightAudio);
	                		  break;
	            	  default:
	            		  break;
	            	  }
	              }else{
	            	  switch(type){
	              	
	            	  case Protocol.VALUE_LEFT_TEXT:
	            	  if(convertView.getTag().getClass() == ViewHolderLeftText.class){ 
	            	  holderLeftText =(ViewHolderLeftText)convertView.getTag();
	            	  }else{
	            		  holderLeftText = new ViewHolderLeftText();
		            	  convertView = mInflater.inflate(
									R.layout.chatting_item_msg_text_left, null);
		            	  holderLeftText.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
		            	  holderLeftText.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
		            	  holderLeftText.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
		            	  holderLeftText.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
		            	  holderLeftText.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
		            	  if(SelfInfo.getInstance().getSex().equals("b")){
		            		  holderLeftText.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_left);
		            	  }else{
		            		  holderLeftText.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_left);
		            	  }
		            	  convertView.setTag(holderLeftText);
	            	  }
	            	  break;
	            	  case Protocol.VALUE_LEFT_ACTION:
	            		  if(convertView.getTag().getClass() == ViewHolderLeftAction.class){ 
	            		  holderLeftAction = (ViewHolderLeftAction)convertView.getTag();
	            		  }else{
	            			  holderLeftAction = new ViewHolderLeftAction();
	                		  convertView = mInflater.inflate(
	      							R.layout.chatting_item_msg_action_left, null);
	                		  holderLeftAction.headIcon = (ImageView)convertView.findViewById(R.id.iv_userhead);
	                		  holderLeftAction.roundImg =(ImageView) convertView.findViewById(R.id.chat_action_round);
	                		  holderLeftAction.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
	                		  holderLeftAction.mPb = (ProgressBar) convertView.findViewById( R.id.sending_progressBar);
	                		  holderLeftAction.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	                		  holderLeftAction.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                		  
	                		  if(SelfInfo.getInstance().getSex().equals("b")){
	                			  holderLeftAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_woman);
	                    	  }else{
	                    		  holderLeftAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_man);
	                    	  }
	                		  convertView.setTag(holderLeftAction);
	                		  
	            		  }
	            		  break;
	            	  case Protocol.VALUE_LEFT_IMAGE:
	            		  if(convertView.getTag().getClass() == ViewHolderLeftPic.class){
	            		  holderLeftPic =(ViewHolderLeftPic)convertView.getTag();
	            		  }else{
	            			  holderLeftPic = new ViewHolderLeftPic();
	                		  convertView = mInflater.inflate(
	      							R.layout.chatting_item_msg_pic_left, null);
	                  	  holderLeftPic.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	                  	  holderLeftPic.contentTv = (ImageView) convertView.findViewById(R.id.tv_chatcontent);
	                  	  holderLeftPic.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
	                  	  holderLeftPic.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	                  	  holderLeftPic.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                  	  
	                  	  if(SelfInfo.getInstance().getSex().equals("b")){
	                  		holderLeftPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_left);
	                	  }else{
	                		  holderLeftPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_left);
	                	  }
	                  	  convertView.setTag(holderLeftPic);
	            		  }
	            		
	            	  break;
	            	  
	            	  case Protocol.VALUE_LEFT_AUDIO:
	            		  if(convertView.getTag().getClass() == ViewHolderLeftAudio.class){
	            		  holderLeftAudio = (ViewHolderLeftAudio)convertView.getTag();
	            		  }else{
	            			  holderLeftAudio = new ViewHolderLeftAudio();
	                		  convertView = mInflater.inflate(
	        							R.layout.chatting_item_msg_voice_left, null);
	                		  holderLeftAudio.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	                		  holderLeftAudio.audioPlayLL = (LinearLayout) convertView.findViewById(R.id.audio_play_layout);
	                      	  holderLeftAudio.playStatus = (ImageView) convertView.findViewById(R.id.play_status);
	                      	  holderLeftAudio.mLoadingPb = (ProgressBar) convertView.findViewById(R.id.download_progress);
	                      	  holderLeftAudio.playPgPb = (ProgressBar) convertView.findViewById(R.id.play_progress);
	                      	
	                      	  holderLeftAudio.audioLen = (TextView) convertView.findViewById(R.id.audio_len);
	                      	  holderLeftAudio.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                      	  
	                      	 convertView.setTag(holderLeftAudio);
	            		  }
	            		
	            		  break;
	            		  
	            	  case Protocol.VALUE_RIGHT_TEXT:
	            		  if(convertView.getTag().getClass() == ViewHolderRightText.class){
	                	  holderRightText = (ViewHolderRightText) convertView.getTag();
	            		  }else{
	            			  holderRightText = new ViewHolderRightText();
	                    	  convertView = mInflater.inflate(
	        							R.layout.chatting_item_msg_text_right, null);
	                    	  holderRightText.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	                    	  holderRightText.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
	                    	  holderRightText.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
	                    	  holderRightText.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	                    	  holderRightText.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                    	  
	                    	  if(SelfInfo.getInstance().getSex().equals("b")){
	                    		  holderRightText.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_right);
	                    	  }else{
	                    		  holderRightText.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_right);
	                    	  }
	                    	  convertView.setTag(holderRightText);
	            		  }
	                	  break;
	                	  case Protocol.VALUE_RIGHT_ACTION:
	                		  if(convertView.getTag().getClass() == ViewHolderRightAction.class){
	                		  holderRightAction = (ViewHolderRightAction) convertView.getTag();
	                		  }else{
	                			  holderRightAction = new ViewHolderRightAction();
	                    		  convertView = mInflater.inflate(
	          							R.layout.chatting_item_msg_action_right, null);
	                    		   holderRightAction.headIcon = (ImageView)convertView.findViewById(R.id.iv_userhead);
	                    		   holderRightAction.roundImg =(ImageView) convertView.findViewById(R.id.chat_action_round);
	                    		   holderRightAction.contentTv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
	                    		   holderRightAction.mPb = (ProgressBar) convertView.findViewById( R.id.sending_progressBar);
	                    		   holderRightAction.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	                    		   holderRightAction.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                    		  
	                    		   if(SelfInfo.getInstance().getSex().equals("b")){
	                    			   holderRightAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_man);
	                         	  }else{
	                         		 holderRightAction.roundImg.setBackgroundResource(R.drawable.chat_action_round_woman);
	                         	  }
	                    		   convertView.setTag(holderRightAction);
	                		  }
	                		
	                		  break;
	                	  case Protocol.VALUE_RIGHT_IMAGE:
	                		  if(convertView.getTag().getClass() ==ViewHolderRightPic.class){
	                		  holderRightPic = (ViewHolderRightPic) convertView.getTag();
	                		  }else{
	                			  holderRightPic = new ViewHolderRightPic();
	                    		  convertView = mInflater.inflate(
	          							R.layout.chatting_item_msg_pic_right, null);
	                      	   holderRightPic.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	                      	   holderRightPic.contentTv = (ImageView) convertView.findViewById(R.id.tv_chatcontent);
	                      	   holderRightPic.mPb = (ProgressBar) convertView.findViewById(R.id.sending_progressBar);
	                      	 holderRightPic.sendFailedImg = (ImageView) convertView.findViewById(R.id.sendfail);
	                      	holderRightPic.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                      	
	                     	  if(SelfInfo.getInstance().getSex().equals("b")){
	                     		 holderRightPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_man_right);
	                    	  }else{
	                    		  holderRightPic.contentTv.setBackgroundResource(R.drawable.chat_bubble_woman_right);
	                    	  }
	                      	 convertView.setTag(holderRightPic);  
	                		  }
	                	
	                	  break;
	                	  
	                	  case Protocol.VALUE_RIGHT_AUDIO:
	                		  if(convertView.getTag().getClass() ==ViewHolderRightAudio.class){
	                		  holderRightAudio = (ViewHolderRightAudio) convertView.getTag();
	                		  }else{
	                			  holderRightAudio = new ViewHolderRightAudio();
	                    		  convertView = mInflater.inflate(
	            							R.layout.chatting_item_msg_voice_right, null);
	                    		  holderRightAudio.headIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
	                          	  holderRightAudio.audioPlayLL = (LinearLayout) convertView.findViewById(R.id.audio_play_layout);
	                    		  holderRightAudio.playStatus = (ImageView) convertView.findViewById(R.id.play_status);
	                          	  holderRightAudio.mLoadingPb = (ProgressBar) convertView.findViewById(R.id.download_progress);
	                          	  holderRightAudio.playPgPb = (ProgressBar) convertView.findViewById(R.id.play_progress);
	                          	
	                          	  holderRightAudio.audioLen = (TextView) convertView.findViewById(R.id.audio_len);
	                          	  holderRightAudio.sendTimeTv = (TextView) convertView.findViewById(R.id.tv_sendtime);
	                          	
	                          	 convertView.setTag( holderRightAudio);
	                		  }
	                		
	                		  break;
	            	  default:
	            		  break;
	            	  }
	              }
	              
	              
	              switch(type){
	        	 
	        	  case Protocol.VALUE_LEFT_TEXT:
	        	  holderLeftText.headIcon.setImageDrawable(tarHeadLayerDrawable);
	        		SpannableString spannableString = FaceConversionUtil
							.getInstace().getExpressionString(ctx,
									entity.getMessage());
	        	  holderLeftText.contentTv.setText(spannableString);
	              
//	        	  Date timedate =AppManagerUtil.StrToDate(entity.getInitdate());
	        	  String serverTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());
	        	  
	              holderLeftText.sendTimeTv.setText(serverTime);
	        	
	        	  break;
	        	  case Protocol.VALUE_LEFT_ACTION:
	        		  holderLeftAction.headIcon.setImageDrawable(tarHeadLayerDrawable);
	        		  holderLeftAction.contentTv.setText(entity.getMessage());  
//	        		  Date LAdate =AppManagerUtil.StrToDate(entity.getInitdate());
	        		  String leftActionServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());
	            	  
	        		  holderLeftAction.sendTimeTv.setText(leftActionServerTime);	
	        		  break;
	        	  case Protocol.VALUE_LEFT_IMAGE:
	        		  holderLeftPic.headIcon.setImageDrawable(tarHeadLayerDrawable);
	        		  String leftImgServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());
	            
	        	         
	        		  holderLeftPic.sendTimeTv.setText(leftImgServerTime);	
	        		  holderLeftPic.contentTv.setTag(position + "");
				
	        		  holderLeftPic.contentTv
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										int pos = Integer.valueOf((String) v
												.getTag());
										imageBrower(pos);
//										ChatTable entity = ChatListFragment
//												.getmDataArrays().get(pos);
//									
//									
//											Intent intent = new Intent();
//											intent.setClass(getActivity(),
//													ChatPicDetailActivity.class);
//											intent.putExtra(Keys.PATH,
//													entity.getMessage());
//											startActivity(intent);

										
									}
								});
	        		  
	        		// Load image, decode it to Bitmap and return Bitmap to callback
	        		  ImageSize targetSize = new ImageSize(120, 80); // result Bitmap will be fit to this size
	        		  final ImageView temp = holderLeftPic.contentTv;
//	        		  imageLoader.displayImage( "file://"+entity.getMessage(), holderLeftPic.contentTv, options);
	        		  imageLoader.loadImage( "file://"+entity.getMessage(), targetSize, options, new SimpleImageLoadingListener() {
	        		      @Override
	        		      public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	        		          // Do whatever you want with Bitmap
	        		    	  temp.setImageBitmap(loadedImage);
	        		      }
	        		  });
						
	        	     break;
	        	  
	        	  case Protocol.VALUE_LEFT_AUDIO:
//	        		  holderLeftAudio = (ViewHolderLeftAudio)
	        	      holderLeftAudio.headIcon.setImageDrawable(tarHeadLayerDrawable);
	        	      String leftAudioServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());
	                  holderLeftAudio.sendTimeTv.setText(leftAudioServerTime);	       	
	                  holderLeftAudio.audioLen.setText(entity.getRecordTime()+"\"");
	                  holderLeftAudio.audioPlayLL.setTag(R.id.a1, position+"");
	                  holderLeftAudio.audioPlayLL.setTag(R.id.a2, holderLeftAudio);

	                  holderLeftAudio.audioPlayLL.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								 if(FileUtil.isSDCardExist()){
								ViewHolderLeftAudio holderLeftAudio= (ViewHolderLeftAudio) v.getTag(R.id.a2);
	                            int itemPos = Integer.parseInt((String)v.getTag(R.id.a1));
								stopMusic();
								
								if (itemPos != currVoicePlayPos) {
									isPause = false;
	                            }

								if (!(isPause)) {
									isChange = true;
									if (itemPos != currVoicePlayPos) {
										lastVoicePlayPos = currVoicePlayPos;
										if(currVoiceLVH != null){
											lastVoiceLVH = currVoiceLVH;
											lastVoiceRVH = null;
											currVoiceRVH= null;
											currVoiceLVH = holderLeftAudio;
										}else{
											lastVoiceRVH =  currVoiceRVH;
											lastVoiceLVH = null;
											currVoiceRVH= null;
											currVoiceLVH = holderLeftAudio;
										}
						
									}
									holderLeftAudio.playStatus.setBackgroundResource(R.drawable.chat_voice_pause);

									initPlayMusic(coll.get(itemPos).getMessage());
									holderLeftAudio.playPgPb
											.setVisibility(View.VISIBLE);
	                                lastVoicePlayPos = currVoicePlayPos;
									currVoicePlayPos = itemPos;

									holderLeftAudio.playPgPb.setMax(mMediaPlayer
											.getDuration());
									mMediaPlayer.start();
									startSetVoicePg();
									mMediaPlayer
											.setOnCompletionListener(new OnCompletionListener() {
												public void onCompletion(
														MediaPlayer mp) {
													if(currVoiceLVH != null){
														currVoiceLVH.playPgPb
														.setVisibility(View.GONE);
														currVoiceLVH.playStatus
														.setBackgroundResource(R.drawable.chat_voice_start);
													}else if(currVoiceRVH != null){
														currVoiceRVH.playPgPb
														.setVisibility(View.GONE);
														currVoiceRVH.playStatus
														.setBackgroundResource(R.drawable.chat_voice_start);
													}
													
													 isPause = false; // for voice play
	    											   isChange = false;
	    												/*
	    												 * //当前播放进度
	    												 */
	    											    currVoicePgPos = 0;
	    							
	    											    currVoicePlayPos = -1;
	    												lastVoicePlayPos= -1;
	    												//用来存储当前播放和上一次播放viewholder
	    											    lastVoiceLVH = null;
	    												currVoiceLVH = null;
	    												lastVoiceRVH = null;
	    												currVoiceRVH = null;

												}
											});
									isPause = true;

								} else {
									holderLeftAudio.playPgPb
											.setVisibility(View.GONE);
									holderLeftAudio.playStatus
											.setBackgroundResource(R.drawable.chat_voice_start);
									 isPause = false; // for voice play
									   isChange = false;
										/*
										 * //当前播放进度
										 */
									    currVoicePgPos = 0;
					
									    currVoicePlayPos = -1;
										lastVoicePlayPos= -1;
										//用来存储当前播放和上一次播放viewholder
									    lastVoiceLVH = null;
										currVoiceLVH = null;
										lastVoiceRVH = null;
										currVoiceRVH = null;
								}
								 }else{
									 Toast.makeText(ctx.getApplicationContext(), "SD卡已拔出，语音功能暂时不能使用", Toast.LENGTH_SHORT).show();
										
								 }
							}
						});
	        		  
	        		  break;	  
	        	  case Protocol.VALUE_RIGHT_TEXT:
	            	  holderRightText.headIcon.setImageDrawable(myHeadLayerDrawable);
	          	 	SpannableString spannableStringR = FaceConversionUtil
	  						.getInstace().getExpressionString(ctx,
	  								entity.getMessage());
	          	 	holderRightText.contentTv.setText(spannableStringR);
	          	  String righttextServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());
	        	  
	           	   
	          	 	String timeStr = righttextServerTime;
	          	 	if (entity.getStatus().equals(Protocol.Sending + "")) {
	          	 		holderRightText.mPb.setVisibility(View.VISIBLE);
	          	 		holderRightText.sendFailedImg.setVisibility(View.GONE);
	          	 		holderRightText.sendTimeTv.setText(timeStr+" 发送...");
					} else if (entity.getStatus().equals(Protocol.SendFail + "")) {
						holderRightText.mPb.setVisibility(View.GONE);
						holderRightText.sendFailedImg.setVisibility(View.VISIBLE);
						holderRightText.sendTimeTv.setText(timeStr+" 发送失败");
					} else if (entity.getStatus().equals(Protocol.Sended + "")) {
						holderRightText.mPb.setVisibility(View.GONE);
						holderRightText.sendFailedImg.setVisibility(View.GONE);
						holderRightText.sendTimeTv.setText(timeStr+" 已发送");
					}else if (entity.getStatus().equals(Protocol.Read + "")) {
						holderRightText.mPb.setVisibility(View.GONE);
						holderRightText.sendFailedImg.setVisibility(View.GONE);
						holderRightText.sendTimeTv.setText(timeStr+" 已读");
					}
	            	  break;
	            	  case Protocol.VALUE_RIGHT_ACTION:
	            		  holderRightAction.headIcon.setImageDrawable(myHeadLayerDrawable);
	            		  holderRightAction.contentTv.setText(entity.getMessage()); 
	            		  String rightActionServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());

	            		  holderRightAction.sendTimeTv.setText(rightActionServerTime);	
	            		
	            		  break;
	            	  case Protocol.VALUE_RIGHT_IMAGE:
	            		  holderRightPic.headIcon.setImageDrawable(myHeadLayerDrawable);
	            		  String rightImgServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());

	                  	   
	                	 	String timeStr1 =  rightImgServerTime;
	            		
	                  	 	if (entity.getStatus().equals(Protocol.Sending + "")) {
	                  	 		holderRightPic.mPb.setVisibility(View.VISIBLE);
	                  	 		holderRightPic.sendFailedImg.setVisibility(View.GONE);
	                  	 		holderRightPic.sendTimeTv.setText(timeStr1+" 发送...");
	        				} else if (entity.getStatus().equals(Protocol.SendFail + "")) {
	        					holderRightPic.mPb.setVisibility(View.GONE);
	        					holderRightPic.sendFailedImg.setVisibility(View.VISIBLE);
	        					holderRightPic.sendTimeTv.setText(timeStr1+" 发送失败");
	        				} else if (entity.getStatus().equals(Protocol.Sended + "")) {
	        					holderRightPic.mPb.setVisibility(View.GONE);
	        					holderRightPic.sendFailedImg.setVisibility(View.GONE);
	        					holderRightPic.sendTimeTv.setText(timeStr1+" 已发送");
	        				}else if (entity.getStatus().equals(Protocol.Read + "")) {
	        					holderRightPic.mPb.setVisibility(View.GONE);
	        					holderRightPic.sendFailedImg.setVisibility(View.GONE);
	        					holderRightPic.sendTimeTv.setText(timeStr1+" 已读");
	        				}
	            		  holderRightPic.contentTv.setTag(position + "");
	    			
	            		  holderRightPic.contentTv
	    							.setOnClickListener(new View.OnClickListener() {

	    								@Override
	    								public void onClick(View v) {
	    									int pos = Integer.valueOf((String) v
	    											.getTag());
//	    									ChatTable entity = coll.get(pos);
//	    								
//	    								
//	    										Intent intent = new Intent();
//	    										intent.setClass(getActivity(),
//	    												ChatPicDetailActivity.class);
//	    										intent.putExtra(Keys.PATH,
//	    												entity.getMessage());
//	    										startActivity(intent);
	    									imageBrower(pos);
	    									
	    								}
	    							});
	            		  
//	            		// Load image, decode it to Bitmap and return Bitmap to callback
//	            		  ImageSize targetSize1 = new ImageSize(120, 80); // result Bitmap will be fit to this size
//	            		  imageLoader.loadImage( "file://"+entity.getMessage(), targetSize1, options, new SimpleImageLoadingListener() {
//	            		      @Override
//	            		      public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//	            		          // Do whatever you want with Bitmap
//	            		    	  holderRightPic.contentTv.setImageBitmap(loadedImage);
//	            		      }
//	            		  });
	            		// Load image, decode it to Bitmap and return Bitmap to callback
	            		  ImageSize rightTargetSize = new ImageSize(120, 80); // result Bitmap will be fit to this size
	            		  final ImageView rightTemp = holderRightPic.contentTv;
//	            		  imageLoader.displayImage( "file://"+entity.getMessage(), holderLeftPic.contentTv, options);
	            		  imageLoader.loadImage( "file://"+entity.getMessage(), rightTargetSize, options, new SimpleImageLoadingListener() {
	            		      @Override
	            		      public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	            		          // Do whatever you want with Bitmap
	            		    	  rightTemp.setImageBitmap(loadedImage);
	            		      }
	            		  });
//	            		  imageLoader.displayImage( "file://"+entity.getMessage(), holderRightPic.contentTv, options);
	            	  break;
	            	  
	            	  case Protocol.VALUE_RIGHT_AUDIO:
	            		  holderRightAudio.headIcon.setImageDrawable(myHeadLayerDrawable);;
	            		  String rightAudioServerTime = AppManagerUtil.transformDisplayChat(entity.getInitdate());

	                	 	String timeStr2 =  rightAudioServerTime;
	            			
	                  	 	if (entity.getStatus().equals(Protocol.Sending + "")) {
	                  	 		holderRightAudio.mLoadingPb.setVisibility(View.VISIBLE);
	                  	 	
	                  	 		holderRightAudio.sendTimeTv.setText(timeStr2+" 发送...");
	        				} else if (entity.getStatus().equals(Protocol.SendFail + "")) {
	        					holderRightAudio.mLoadingPb.setVisibility(View.GONE);
	        				
	        					holderRightAudio.sendTimeTv.setText(timeStr2+" 发送失败");
	        				} else if (entity.getStatus().equals(Protocol.Sended + "")) {
	        					holderRightAudio.mLoadingPb.setVisibility(View.GONE);
	        				
	        					holderRightAudio.sendTimeTv.setText(timeStr2+" 已发送");
	        				}else if (entity.getStatus().equals(Protocol.Read + "")) {
	        					holderRightAudio.mLoadingPb.setVisibility(View.GONE);
	        					
	        					holderRightAudio.sendTimeTv.setText(timeStr2+" 已读");
	        				}
	                  	  holderRightAudio.audioLen.setText(entity.getRecordTime()+"\"");
	                  	holderRightAudio.audioPlayLL.setTag(R.id.a1, position+"");
	                  	holderRightAudio.audioPlayLL.setTag(R.id.a2, holderRightAudio);

	                  	holderRightAudio.audioPlayLL.setOnClickListener(new View.OnClickListener() {

	    						@Override
	    						public void onClick(View v) {

	    							if(FileUtil.isSDCardExist()){
	    							ViewHolderRightAudio holderRightAudio= (ViewHolderRightAudio) v.getTag(R.id.a2);
	                                int itemPos = Integer.parseInt((String)v.getTag(R.id.a1));
	    							stopMusic();
	    							
	    							if (itemPos != currVoicePlayPos) {
	    								isPause = false;
	                                }

	    							if (!(isPause)) {
	    								isChange = true;
	    								if (itemPos != currVoicePlayPos) {
	    									lastVoicePlayPos = currVoicePlayPos;
	    									if(currVoiceRVH != null){
	    										lastVoiceRVH = currVoiceRVH;
	    										lastVoiceLVH = null;
	    										currVoiceLVH= null;
	    										currVoiceRVH = holderRightAudio;
	    									}else{
	    										lastVoiceLVH =  currVoiceLVH;
	    										lastVoiceRVH = null;
	    										currVoiceLVH= null;
	    										currVoiceRVH = holderRightAudio;
	    									}
	    					
	    								}
	    								holderRightAudio.playStatus.setBackgroundResource(R.drawable.chat_voice_pause);
                                     
	    								initPlayMusic(coll.get(itemPos).getMessage());
	    								holderRightAudio.playPgPb
	    										.setVisibility(View.VISIBLE);
	                                    lastVoicePlayPos = currVoicePlayPos;
	    								currVoicePlayPos = itemPos;

	    								holderRightAudio.playPgPb.setMax(mMediaPlayer
	    										.getDuration());
	    								mMediaPlayer.start();
	    								startSetVoicePg();
	    								mMediaPlayer
	    										.setOnCompletionListener(new OnCompletionListener() {
	    											public void onCompletion(
	    													MediaPlayer mp) {
	    												if(currVoiceLVH != null){
	    													currVoiceLVH.playPgPb
	    													.setVisibility(View.GONE);
	    													currVoiceLVH.playStatus
	    													.setBackgroundResource(R.drawable.chat_voice_start);
	    												}else if(currVoiceRVH != null){
	    													currVoiceRVH.playPgPb
	    													.setVisibility(View.GONE);
	    													currVoiceRVH.playStatus
	    													.setBackgroundResource(R.drawable.chat_voice_start);
	    												}
	    												
	    												 isPause = false; // for voice play
	      											   isChange = false;
	      												/*
	      												 * //当前播放进度
	      												 */
	      											    currVoicePgPos = 0;
	      							
	      											    currVoicePlayPos = -1;
	      												lastVoicePlayPos= -1;
	      												//用来存储当前播放和上一次播放viewholder
	      											    lastVoiceLVH = null;
	      												currVoiceLVH = null;
	      												lastVoiceRVH = null;
	      												currVoiceRVH = null;

	    											}
	    										});
	    								isPause = true;

	    							} else {
	    								holderRightAudio.playPgPb
	    										.setVisibility(View.GONE);
	    								holderRightAudio.playStatus
	    										.setBackgroundResource(R.drawable.chat_voice_start);
	    								 isPause = false; // for voice play
										   isChange = false;
											/*
											 * //当前播放进度
											 */
										    currVoicePgPos = 0;
						
										    currVoicePlayPos = -1;
											lastVoicePlayPos= -1;
											//用来存储当前播放和上一次播放viewholder
										    lastVoiceLVH = null;
											currVoiceLVH = null;
											lastVoiceRVH = null;
											currVoiceRVH = null;
	    							}
	    							}else{
	    								Toast.makeText(ctx.getApplicationContext(), "SD卡已拔出，语音功能暂时不能使用", Toast.LENGTH_SHORT).show();
		    						}
	    						}
	    						
	    					});
	            		  break;
	        	  default:
	        		  break;
	        	  }
			}
		}

		public void setListView(ListView listView) {
			this.mListView = listView;
		}

	
	}

	public static List<ChatTable> getmDataArrays() {
		return mDataArrays;
	}

	public static void setmDataArrays(List<ChatTable> mDataArrays) {
		ChatListFragment.mDataArrays = mDataArrays;
	}

}
