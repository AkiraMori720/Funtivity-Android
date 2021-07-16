package com.brainyapps.funtivity.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.brainyapps.funtivity.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DragListView extends ListView implements OnScrollListener, OnClickListener {
	private enum DListViewState {
		LV_HEADER_NORMAL, LV_HEADER_PULL_REFRESH, LV_HEADER_RELEASE_REFRESH, LV_HEADER_LOADING;
	}

	private enum DListViewLoadingMore {
		LV_FOOTER_NORMAL, LV_FOOTER_LOADING, LV_FOOTER_OVER;
	}

	private View mHeadView;
	private TextView mRefreshTextview;
	private TextView mLastUpdateTextView;
	private ImageView mArrowImageView;
	private ProgressBar mHeadProgressBar;
	private int mHeadViewHeight;
	private int mHeadViewWidth;
	private View mFootView;
	private View mLoadMoreView;
	private View mLoadingView;
	private Animation animation, reverseAnimation;
	public int mFirstItemIndex = -1;
	public boolean mIsLoading = false;
	public boolean mIsEnd = true;
	public boolean mIsRecord = false;
	private int mStartY, mMoveY;
	private DListViewState mlistViewState = DListViewState.LV_HEADER_NORMAL;
	private DListViewLoadingMore loadingMoreState = DListViewLoadingMore.LV_FOOTER_NORMAL;
	private final static int RATIO = 2;
	private boolean mBack = false;
	private OnRefreshLoadingMoreListener onRefreshLoadingMoreListener;
	public boolean mIsScroller = true;

	public DragListView(Context context) {
		super(context, null);
		initDragListView(context);
	}

	public DragListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDragListView(context);
	}

	public void setOnRefreshListener(
			OnRefreshLoadingMoreListener onRefreshLoadingMoreListener) {
		this.onRefreshLoadingMoreListener = onRefreshLoadingMoreListener;
	}

	@SuppressLint("SimpleDateFormat")
	public void initDragListView(Context context) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
		String date = sdf.format(new Date());

		initHeadView(context, date);
		initLoadMoreView(context);

		setOnScrollListener(this);
	}

	public void initHeadView(Context context, String time) {
		mHeadView = LayoutInflater.from(context).inflate(R.layout.refresh_head,null);
		mArrowImageView = mHeadView.findViewById(R.id.head_arrowImageView);
		mArrowImageView.setAdjustViewBounds(true);
		mArrowImageView.setMaxWidth(30);
		mArrowImageView.setMaxHeight(30);
		mHeadProgressBar = mHeadView.findViewById(R.id.head_progressBar);
		mRefreshTextview = mHeadView.findViewById(R.id.head_tipsTextView);
		mLastUpdateTextView = mHeadView.findViewById(R.id.head_lastUpdatedTextView);
		mLastUpdateTextView.setText("Last Update: " + time);
		measureView(mHeadView);
		mHeadViewWidth = mHeadView.getMeasuredWidth();
		mHeadViewHeight = mHeadView.getMeasuredHeight();
		addHeaderView(mHeadView, null, false);
		mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
		initAnimation();
	}

	private void initLoadMoreView(Context context) {
		mFootView = LayoutInflater.from(context).inflate(R.layout.refresh_footer, null);
		mLoadMoreView = mFootView.findViewById(R.id.load_more_view);
		mLoadingView = mFootView.findViewById(R.id.loading_layout);
		mLoadMoreView.setOnClickListener(this);
		addFooterView(mFootView);
	}

	private void initAnimation() {
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
		reverseAnimation.setDuration(250);
		reverseAnimation.setFillAfter(true);
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null)
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0)
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		else
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			doActionDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			doActionMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			doActionUp(ev);
			break;
		default:
			break;
		}

		if (mIsScroller) {
			return super.onTouchEvent(ev);
		} else {
			return true;
		}
	}

	void doActionDown(MotionEvent event) {
		if (mIsRecord == false && mFirstItemIndex == 0) {
			mStartY = (int) event.getY();
			mIsRecord = true;
		}
	}

	void doActionMove(MotionEvent event) {
		mMoveY = (int) event.getY();
		if (mIsRecord == false && mFirstItemIndex == 0) {
			mStartY = (int) event.getY();
			mIsRecord = true;
		}

		if (mIsRecord == false
				|| mlistViewState == DListViewState.LV_HEADER_LOADING) {
			return;
		}

		int offset = (mMoveY - mStartY) / RATIO;

		switch (mlistViewState) {
		case LV_HEADER_NORMAL: {
			if (offset > 0) {
				mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);
				switchViewState(DListViewState.LV_HEADER_PULL_REFRESH);
			}
		} break;
		
		case LV_HEADER_PULL_REFRESH: {
			setSelection(0);
			mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);
			if (offset < 0) {
				mIsScroller = false;
				switchViewState(DListViewState.LV_HEADER_NORMAL);
			} else if (offset > mHeadViewHeight) {
				switchViewState(DListViewState.LV_HEADER_RELEASE_REFRESH);
			}
		}
			break;
		case LV_HEADER_RELEASE_REFRESH: {
			setSelection(0);
			mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);
			if (offset >= 0 && offset <= mHeadViewHeight) {
				mBack = true;
				switchViewState(DListViewState.LV_HEADER_PULL_REFRESH);
			} else if (offset < 0) {
				switchViewState(DListViewState.LV_HEADER_NORMAL);
			}
		}
			break;
		default:
			return;
		}
		;
	}

	public void doActionUp(MotionEvent event) {
		mIsRecord = false;
		mIsScroller = true;
		mBack = false;
		if (mlistViewState == DListViewState.LV_HEADER_LOADING) {
			return;
		}
		switch (mlistViewState) {
		case LV_HEADER_NORMAL:

			break;
		case LV_HEADER_PULL_REFRESH:
			mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
			switchViewState(mlistViewState.LV_HEADER_NORMAL);
			break;
		case LV_HEADER_RELEASE_REFRESH:
			mHeadView.setPadding(0, 0, 0, 0);
			switchViewState(mlistViewState.LV_HEADER_LOADING);
			onRefresh();
			break;
		}
	}

	public void refresh() {
		mHeadView.setPadding(0, 0, 0, 0);
		switchViewState(mlistViewState.LV_HEADER_LOADING);
		onRefresh();
	}

	private void switchViewState(DListViewState state) {

		switch (state) {
		case LV_HEADER_NORMAL: {
			mArrowImageView.clearAnimation();
			mArrowImageView.setImageResource(R.drawable.com_loading_arrow);
			mIsLoading = false;
		}
			break;
		case LV_HEADER_PULL_REFRESH: {
			mHeadProgressBar.setVisibility(View.GONE);
			mArrowImageView.setVisibility(View.VISIBLE);
			mRefreshTextview.setText("Drag down to refresh");
			mArrowImageView.clearAnimation();

			if (mBack) {
				mBack = false;
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(reverseAnimation);
			}
		}
			break;
			
		case LV_HEADER_RELEASE_REFRESH: {
			mHeadProgressBar.setVisibility(View.GONE);
			mArrowImageView.setVisibility(View.VISIBLE);
			mRefreshTextview.setText("Release to update");
			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(animation);
		}
			break;
		case LV_HEADER_LOADING: {
			mHeadProgressBar.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.GONE);
			mRefreshTextview.setText("Loading Data...");
		}
			break;
		default:
			return;
		}

		mlistViewState = state;
	}

	private void onRefresh() {
		if (onRefreshLoadingMoreListener != null) {
			onRefreshLoadingMoreListener.onDragRefresh();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
			String date = sdf.format(new Date());
			mLastUpdateTextView.setText("Last Update: " + date);
		}
	}

	public void onRefreshComplete() {
		mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
		switchViewState(mlistViewState.LV_HEADER_NORMAL);
	}

	public void onLoadMoreComplete(boolean flag) {
		if (flag) {
			updateLoadMoreViewState(DListViewLoadingMore.LV_FOOTER_OVER);
		} else {
			updateLoadMoreViewState(DListViewLoadingMore.LV_FOOTER_NORMAL);
		}
	}

	private void updateLoadMoreViewState(DListViewLoadingMore state) {
		switch (state) {
		case LV_FOOTER_NORMAL:
			mLoadingView.setVisibility(View.GONE);
			break;
		case LV_FOOTER_LOADING:
			mLoadingView.setVisibility(View.VISIBLE);
			break;
		case LV_FOOTER_OVER:
			mLoadingView.setVisibility(View.GONE);
			mIsEnd = true;
			break;
		default:
			break;
		}
		loadingMoreState = state;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
		mFirstItemIndex = firstVisibleItem;
		if (visibleItemCount > 0 && totalItemCount > 0 && firstVisibleItem > 0 && firstVisibleItem + visibleItemCount == totalItemCount && !mIsLoading && !mIsEnd) {
			updateLoadMoreViewState(DListViewLoadingMore.LV_FOOTER_LOADING);
			if (onRefreshLoadingMoreListener != null)
				onRefreshLoadingMoreListener.onDragLoadMore();
			mIsLoading = true;
		}
	}

	@Override
	public void onClick(View v) {
		if (onRefreshLoadingMoreListener != null && loadingMoreState == DListViewLoadingMore.LV_FOOTER_NORMAL) {
			updateLoadMoreViewState(DListViewLoadingMore.LV_FOOTER_LOADING);
			onRefreshLoadingMoreListener.onDragLoadMore();
		}
	}

	public interface OnRefreshLoadingMoreListener {
		void onDragRefresh();
		void onDragLoadMore();
	}

	private float xDistance, yDistance, xLast, yLast;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}
}