package com.minus.lovershouse.util;

import java.util.Date;

import com.minus.lovershouse.BuildConfig;
import com.minus.lovershouse.R;

import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RTPullListView extends ListView implements OnScrollListener {  
	
	private static final int MAX_SCROLL = 200;    
    private static final float SCROLL_RATIO = 0.5f;// 阻尼系数  

	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	private LayoutInflater inflater;

	private LinearLayout headView;

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	// 用于保证startY的在一个完整的touch事件中只被记录一次
	private boolean isRecored;

//	private int headContentWidth;
	private int headContentHeight;

	private int startY;
	private int firstItemIndex;
	private int state;
	private boolean isBack;
	private OnRefreshListener refreshListener;

	private boolean isRefreshable;
	private boolean isPush;

	private int visibleLastIndex;
	private int visibleItemCount;

	public RTPullListView(Context context) {
		super(context);
		init(context);
	}

	public RTPullListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		inflater = LayoutInflater.from(context);
		headView = (LinearLayout) inflater.inflate(R.layout.pulllist_head, null);
		arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
//		arrowImageView.setMinimumWidth(70);
//		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
//		headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		addHeaderView(headView, null, false);
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
		isPush = true;
	}
	
	@Override
	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		if (BuildConfig.DEBUG)
			Log.d("chat-RTPullListView", "onScroll(): begin: firstItemIndex = " + firstItemIndex 
					+ ", visibleLastIndex = " + visibleLastIndex + ", visibleItemCount = " + visibleItemCount);
		firstItemIndex = firstVisiableItem;
		/*visibleLastIndex = firstVisiableItem + arg2; 
		visibleItemCount = arg2;
		if(firstItemIndex == 1 && !isPush){
			setSelection(0);
		}*/
		if (BuildConfig.DEBUG)
			Log.d("chat-RTPullListView", "onScroll(): end: firstVisiableItem = " + firstVisiableItem 
					+ ", arg2 = " + arg2 + ", arg3 = " + arg3);
	}
	
	public void setSelectionfoot(){
//		this.setSelection(visibleLastIndex - visibleItemCount + 1);
	}
	
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (BuildConfig.DEBUG)
			Log.d("chat-RTPullListView", "onTouchEvent(): begin: event.getAction() = " + event.getAction() + ", state = " + state
					+ ", isRecored = " + isRecored + ", firstItemIndex = " + firstItemIndex);
		
		if (isRefreshable && (state != REFRESHING)) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					isPush = true;
					startY = (int) event.getY();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
					
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
					}
				}
				isRecored = false;
				isBack = false;
				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!isRecored && firstItemIndex == 0) {
		
					isRecored = true;
					startY = tempY;
				}
				if (state != REFRESHING && isRecored && state != LOADING) {
					// 保证在设置padding的过程中，当前的位置�?��是在head，否则如果当列表超出屏幕的话，当在上推的时�?，列表会同时进行滚动
					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {
						setSelection(0);
						// 推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
						//推到顶
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
						}
						// 还没有上推到屏幕顶部掩盖head的地
						else {
							// 不用进行特别的操作，只用更新paddingTop就行
						}
					}
					// 还没有到达显示松�?��新的时�?,DONE或�?是PULL_To_REFRESH状�?
					if (state == PULL_To_REFRESH) {
//						setSelection(0);
						// 下拉到可以进入RELEASE_TO_REFRESH
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
						}
						// 上推到顶
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							isPush = false;
//							Log.v(TAG, "由DOne或�?下拉刷新状�?转变到done状�?");
						}
					}

					// done
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}
					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
					}
					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}
				}
				break;
			}
		}

		if (BuildConfig.DEBUG)
			Log.d("chat-RTPullListView", "onTouchEvent(): end: state = " + state 
					+ ", isRecored = " + isRecored + ", firstItemIndex = " + firstItemIndex);
		return super.onTouchEvent(event);
	}


	// 当状态改变时候，调用该方法，以更新界
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText(getResources().getString(R.string.release_to_refresh));

			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText(getResources().getString(R.string.pull_to_refresh));
			} else {
				tipsTextview.setText(getResources().getString(R.string.pull_to_refresh));
			}
		
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText(getResources().getString(R.string.refreshing));
			lastUpdatedTextView.setVisibility(View.VISIBLE);

		
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.pulltorefresh);
			tipsTextview.setText(getResources().getString(R.string.pull_to_refresh));
			lastUpdatedTextView.setVisibility(View.VISIBLE);

		
			break;
		}
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		lastUpdatedTextView.setText(getResources().getString(R.string.updating) + new Date().toLocaleString());
		changeHeaderViewByState();
		invalidateViews();
		setSelection(0);
		
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}
	
	public void clickToRefresh(){
		state = REFRESHING;
		changeHeaderViewByState();
	}
	

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		lastUpdatedTextView.setText(getResources().getString(R.string.updating) + new Date().toLocaleString());
		super.setAdapter(adapter);
	}

	  @TargetApi(9)
	@Override    
	    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent){     
	        int newDeltaY = deltaY;  
	        int delta = (int) (deltaY * SCROLL_RATIO);  
	        if (delta != 0) newDeltaY = delta;  
	        return super.overScrollBy(deltaX, newDeltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, MAX_SCROLL, isTouchEvent);      
	    }  
	
	
	
}  