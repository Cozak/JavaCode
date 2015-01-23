package com.minus.lovershouse.face;

import java.util.ArrayList;
import java.util.List;

import com.minus.lovershouse.R;
import com.minus.lovershouse.singleton.GlobalApplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 ******************************************
 * @鏂囦欢鎻忚堪	: 甯﹁〃鎯呯殑鑷畾涔夎緭鍏ユ
 ******************************************
 */
public class FaceRelativeLayout extends RelativeLayout implements
		OnItemClickListener, OnClickListener {

	private Context context;

	/** 琛ㄦ儏椤电殑鐩戝惉浜嬩欢 */
	private OnCorpusSelectedListener mListener;

	/** 鏄剧ず琛ㄦ儏椤电殑viewpager */
	private ViewPager vp_face;

	/** 琛ㄦ儏椤电晫闈㈤泦锟�*/
	private ArrayList<View> pageViews_emoji;
	private ArrayList<View> pageViews_emoticon;

	/** 娓告爣鏄剧ず甯冨眬 */
	private LinearLayout layout_point;

	/** 娓告爣鐐归泦锟�*/
	private ArrayList<ImageView> pointViews;

	/** 琛ㄦ儏闆嗗悎 */
	private List<List<ChatEmoji>> emojis;
	private List<List<String>> emoticons;

	/** 琛ㄦ儏鍖哄煙 */
	private View view;

	/** 杈撳叆锟�*/
	private EditText et_sendmessage;

	/** 琛ㄦ儏鏁版嵁濉厖锟�*/
	private List<FaceAdapter> faceAdapters;

	/** 褰撳墠琛ㄦ儏锟�*/
	private int current = 0;

	public FaceRelativeLayout(Context context) {
		super(context);
		this.context = context;
	}

	public FaceRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public FaceRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public void setOnCorpusSelectedListener(OnCorpusSelectedListener listener) {
		mListener = listener;
	}

	/**
	 * 琛ㄦ儏閫夋嫨鐩戝惉
	 */
	public interface OnCorpusSelectedListener {

		void onCorpusSelected(ChatEmoji emoji);

		void onCorpusDeleted();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		FaceConversionUtil.getInstace().getFileText(GlobalApplication.getInstance().getApplicationContext());
		emojis = FaceConversionUtil.getInstace().emojiLists;
		emoticons = EmoticonConversionUtil.getInstace().getEmoticonsList();
//		System.out.println("onFinishInflate " + emojis.size());
		onCreate();
	}

	private void onCreate() {
		Init_View();
		Init_viewPager_emoji();
		Init_viewPager_emoticon();
		Init_emoji_emoticon();
		setPoint_emoji_emoticon(pageViews_emoji);
		choose_emoji_emoticon(pageViews_emoji);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_face:
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0);
	
			// 闅愯棌琛ㄦ儏閫夋嫨锟�
			if (view.getVisibility() == View.VISIBLE) {
				view.setVisibility(View.GONE);
			} else {
				view.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.et_sendmessage:
			// 闅愯棌琛ㄦ儏閫夋嫨锟�
			if (view.getVisibility() == View.VISIBLE) {
				view.setVisibility(View.GONE);
			}
			break;

		}
	}

	/**
	 * 闅愯棌琛ㄦ儏閫夋嫨锟�
	 */
	public  boolean hideFaceView() {
		// 闅愯棌琛ㄦ儏閫夋嫨锟�
		if (view.getVisibility() == View.VISIBLE) {
			view.setVisibility(View.GONE);
			return true;
		}
		return false;
	}

	/**
	 * 鍒濆鍖栨帶锟�
	 */
	private void Init_View() {
//		System.out.println("Init_View");
		vp_face = (ViewPager) findViewById(R.id.vp_contains);
		et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
		layout_point = (LinearLayout) findViewById(R.id.iv_image);
		et_sendmessage.setOnClickListener(this);
		findViewById(R.id.iv_face).setOnClickListener(this);
		view = findViewById(R.id.ll_facechoose);

	}

	private void Init_emoji_emoticon() {
		LinearLayout select = (LinearLayout) this.findViewById(R.id.custom_faceRelativeLayout_emoji_emoticon);
		final ImageButton deleteBtn = (ImageButton) this.findViewById(R.id.custom_faceRelativeLayout_deleteBtn);
		final ImageView emoji = (ImageView) this.findViewById(R.id.custom_faceRelativeLayout_selectEmoji);
		final ImageView emoticon = (ImageView) this.findViewById(R.id.custom_faceRelativeLayout_selectEmoticon);
		
		emoji.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				emoji.setImageResource(R.drawable.face_emoji_selected);
				emoji.setScaleType(ScaleType.FIT_END);
				emoticon.setImageResource(R.drawable.face_emoticon_unselected);
				emoticon.setScaleType(ScaleType.FIT_END);
				deleteBtn.setVisibility(View.INVISIBLE);
				setPoint_emoji_emoticon(pageViews_emoji);
				choose_emoji_emoticon(pageViews_emoji);
			}
		});

		emoticon.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				emoji.setImageResource(R.drawable.face_emoji_unselected);
				emoji.setScaleType(ScaleType.FIT_END);
				emoticon.setImageResource(R.drawable.face_emoticon_selected);
				emoticon.setScaleType(ScaleType.FIT_END);
				deleteBtn.setVisibility(View.VISIBLE);
				setPoint_emoji_emoticon(pageViews_emoticon);
				choose_emoji_emoticon(pageViews_emoticon);
			}
		});
		
		deleteBtn.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				int selection = et_sendmessage.getSelectionStart();
				String text = et_sendmessage.getText().toString();
				if (selection > 0) {
					char deleteChar = text.charAt(selection - 1);
					if (deleteChar == (char) 0x03) {
						int start = text.substring(0, selection - 1).lastIndexOf("" + (char) 0x02);
						int end = selection;
						et_sendmessage.getText().delete(start, end);
						return;
					}
					et_sendmessage.getText().delete(selection - 1, selection);
				}
				/*
				int selection = et_sendmessage.getSelectionStart();aa
				if (selection > 0)
					et_sendmessage.getText().delete(selection - 1, selection);
				*/
			}
		});
	}
	
	/**
	 * 鍒濆鍖栨樉绀鸿〃鎯呯殑viewpager_emoji
	 */
	private void Init_viewPager_emoji() {
//		System.out.println("init_viewPager_emoji");
		pageViews_emoji = new ArrayList<View>();
		// 宸︿晶娣诲姞绌洪〉
		View nullView1 = new View(context);
		// 璁剧疆閫忔槑鑳屾櫙
		nullView1.setBackgroundColor(Color.TRANSPARENT);
		pageViews_emoji.add(nullView1);

		// 涓棿娣诲姞琛ㄦ儏锟�

		faceAdapters = new ArrayList<FaceAdapter>();
		for (int i = 0; i < emojis.size(); i++) {
			GridView gridView = new GridView(context);
			FaceAdapter adapter = new FaceAdapter(context, emojis.get(i));
			gridView.setAdapter(adapter);
			faceAdapters.add(adapter);
			gridView.setOnItemClickListener(this);
			gridView.setNumColumns(7);
			gridView.setBackgroundColor(Color.TRANSPARENT);
			gridView.setHorizontalSpacing(1);
			gridView.setVerticalSpacing(1);
			gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			gridView.setCacheColorHint(0);
			gridView.setPadding(5, 0, 5, 0);
			gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			gridView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			gridView.setGravity(Gravity.CENTER);
			pageViews_emoji.add(gridView);
		}

		// 鍙充晶娣诲姞绌洪〉锟�
		View nullView2 = new View(context);
		// 璁剧疆閫忔槑鑳屾櫙
		nullView2.setBackgroundColor(Color.TRANSPARENT);
		pageViews_emoji.add(nullView2);
	}
	
	private void Init_viewPager_emoticon() {
		pageViews_emoticon = new ArrayList<View>();
		// 宸︿晶娣诲姞绌洪〉
		View nullView1 = new View(context);
		// 璁剧疆閫忔槑鑳屾櫙
		nullView1.setBackgroundColor(Color.TRANSPARENT);
		pageViews_emoticon.add(nullView1);

		// 涓棿娣诲姞琛ㄦ儏锟�

		for (int i = 0; i < emoticons.size(); i++) {
			GridView gridView = new GridView(context);
			EmoticonAdapter adapter = new EmoticonAdapter(context, emoticons.get(i));
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View v,
						int arg2, long arg3) {
					EmoticonAdapter.ViewHolder holder = (EmoticonAdapter.ViewHolder) v.getTag();
					
					TextView textView = holder.tv_emoicon;
					et_sendmessage.append(textView.getText().toString());
					
				}
			});
	
			gridView.setNumColumns(3);
			gridView.setBackgroundColor(Color.parseColor("#FF9999"));
			gridView.setHorizontalSpacing(0);
			gridView.setVerticalSpacing(0);
			gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			gridView.setCacheColorHint(Color.TRANSPARENT);
			gridView.setPadding(0, 0, 0, 0);
			gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			gridView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
		
//			gridView.set
			gridView.setGravity(Gravity.CENTER);
			pageViews_emoticon.add(gridView);
		}

		// 鍙充晶娣诲姞绌洪〉锟�
		View nullView2 = new View(context);
		// 璁剧疆閫忔槑鑳屾櫙
		nullView2.setBackgroundColor(Color.TRANSPARENT);
		pageViews_emoticon.add(nullView2);
	}

	/**
	 * 鍒濆鍖栨父锟�
	 */
	private void setPoint_emoji_emoticon(List<View> pageViews) {

		pointViews = new ArrayList<ImageView>();
		layout_point.removeAllViews();
		for (int i = 0; i < pageViews.size(); i++) {
			ImageView imageView = new ImageView(context);
			imageView.setBackgroundResource(R.drawable.d1);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			layoutParams.width = 8;
			layoutParams.height = 8;
			layout_point.addView(imageView, layoutParams);
			if (i == 0 || i == pageViews.size() - 1) {
				imageView.setVisibility(View.GONE);
			}
			if (i == 1) {
				imageView.setBackgroundResource(R.drawable.d2);
			}
			pointViews.add(imageView);

		}
	}

	/**
	 * 濉厖鏁版嵁
	 */
	private void choose_emoji_emoticon(List<View> pageViews) {
		vp_face.setAdapter(new ViewPagerAdapter(pageViews));

		vp_face.setCurrentItem(1);
		vp_face.setOffscreenPageLimit(3);
		current = 0;
		vp_face.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				current = arg0 - 1;
				// 鎻忕粯鍒嗛〉锟�
				draw_Point(arg0);
				// 濡傛灉鏄锟�锟斤拷鎴栵拷?鏄渶鍚庝竴灞忕姝㈡粦鍔紝鍏跺疄杩欓噷瀹炵幇鐨勬槸濡傛灉婊戝姩鐨勬槸绗竴灞忓垯璺宠浆鑷崇浜屽睆锛屽鏋滄槸锟�锟斤拷锟�锟斤拷鍒欒烦杞埌鍊掓暟绗簩锟�
				if (arg0 == pointViews.size() - 1 || arg0 == 0) {
					if (arg0 == 0) {
						vp_face.setCurrentItem(arg0 + 1);// 绗簩锟�浼氬啀娆″疄鐜拌鍥炶皟鏂规硶瀹炵幇璺宠浆.
						pointViews.get(1).setBackgroundResource(R.drawable.d2);
					} else {
						vp_face.setCurrentItem(arg0 - 1);// 鍊掓暟绗簩锟�
						pointViews.get(arg0 - 1).setBackgroundResource(
								R.drawable.d2);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	/**
	 * 缁樺埗娓告爣鑳屾櫙
	 */
	public void draw_Point(int index) {
		for (int i = 1; i < pointViews.size(); i++) {
			if (index == i) {
				pointViews.get(i).setBackgroundResource(R.drawable.d2);
			} else {
				pointViews.get(i).setBackgroundResource(R.drawable.d1);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ChatEmoji emoji = (ChatEmoji) faceAdapters.get(current).getItem(arg2);
		if (emoji.getId() == R.drawable.face_del_icon) {
			int selection = et_sendmessage.getSelectionStart();
			String text = et_sendmessage.getText().toString();
			if (selection > 0) {
				char deleteChar = text.charAt(selection - 1);
				if (deleteChar == (char) 0x03) {
					int start = text.substring(0, selection - 1).lastIndexOf("" + (char) 0x02);
					int end = selection;
					et_sendmessage.getText().delete(start, end);
					return;
				}
				et_sendmessage.getText().delete(selection - 1, selection);
			}
		}
		if (!TextUtils.isEmpty(emoji.getCharacter())) {
			if (mListener != null)
				mListener.onCorpusSelected(emoji);
			SpannableString spannableString = FaceConversionUtil.getInstace()
					.addFace(getContext(), emoji.getId(), emoji.getCharacter());
			et_sendmessage.append(spannableString);
		}

	}
}
